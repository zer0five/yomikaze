package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Image;
import org.yomikaze.persistence.repository.ImageRepository;
import org.yomikaze.snowflake.Snowflake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DatabaseImageStorageService implements IFileStorageService<Snowflake, Resource, Account> {

    private final ImageRepository imageRepository;

    @Override
    public Snowflake store(MultipartFile file, Account owner) throws IOException {
        Validate.notNull(file, "File cannot be null");
        Validate.isTrue(!file.isEmpty(), "File cannot be empty");
        String contentType = file.getContentType();
        Validate.notNull(contentType, "File content type cannot be null");
        MediaType mediaType = MediaType.valueOf(contentType);
        if (!mediaType.getType().equalsIgnoreCase("image")) {
            throw new IllegalArgumentException("File is not an image");
        }
        byte[] data = file.getBytes();
        String name = Optional.ofNullable(file.getOriginalFilename())
            .orElse(MessageFormat.format("untitled.{0}", mediaType.getSubtype()));
        Image image = new Image();
        image.setName(name);
        image.setType(contentType);
        image.setData(data);
        image.setOwner(owner);
        return imageRepository.save(image).getId();
    }

    @Override
    public Resource retrieve(Snowflake id, @Nullable Account owner) throws IOException {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return null;
        }
        if (owner != null && !image.getOwner().equals(owner)) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image.getData());
        return new InputStreamResource(inputStream);
    }

    @Override
    public void delete(Snowflake id, @Nullable Account owner) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return;
        }
        if (owner != null && !image.getOwner().equals(owner)) {
            return;
        }
        imageRepository.delete(image);
    }

    @Override
    public void delete(Snowflake id) {
        imageRepository.deleteById(id);
    }

    @Override
    public boolean exists(Snowflake id, @Nullable Account owner) {
        if (owner == null) {
            return imageRepository.existsById(id);
        }
        return imageRepository.existsByIdAndOwner(id, owner);
    }

}
