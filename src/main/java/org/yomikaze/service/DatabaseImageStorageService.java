package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.hibernate.engine.jdbc.BlobProxy;
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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DatabaseImageStorageService implements IFileStorageService<Snowflake, Resource, Account> {

    private final ImageRepository imageRepository;

    private final WebpImageProcessingService webpImageProcessingService;

    private String getName(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
            .map(name -> {
                int index = name.lastIndexOf('.');
                return index == -1 ? name : name.substring(0, index);
            }).orElse("untitled") + ".webp";
    }

    @Override
    public Snowflake store(MultipartFile file, Account owner) throws IOException {
        Validate.notNull(owner, "Owner cannot be null");
        Validate.notNull(file, "File cannot be null");
        Validate.isTrue(!file.isEmpty(), "File cannot be empty");
        String contentType = file.getContentType();
        Validate.notNull(contentType, "File content type cannot be null");
        MediaType mediaType = MediaType.valueOf(contentType);
        if (!mediaType.getType().equalsIgnoreCase("image")) {
            throw new IllegalArgumentException("File is not an image");
        }
        Blob data;
        String name = getName(file);
        String type = "image/webp";
        if (mediaType.getSubtype().equalsIgnoreCase("webp")) {
            data = BlobProxy.generateProxy(file.getInputStream(), file.getSize());
        } else {
            try {
                InputStream converted = webpImageProcessingService.convertToWebp(file.getInputStream());
                data = BlobProxy.generateProxy(converted, converted.available());
            } catch (Exception e) {
                log.warn("Failed to convert image {} to webp", name);
                data = BlobProxy.generateProxy(file.getInputStream(), file.getSize());
                type = contentType;
                name = Optional.ofNullable(file.getOriginalFilename()).orElse(MessageFormat.format("untitled.{0}", mediaType.getSubtype()));
            }
        }
        Image image = new Image();
        image.setName(name);
        image.setType(type);
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
        try {
            return new InputStreamResource(image.getData().getBinaryStream());
        } catch (SQLException e) {
            throw new IOException(e);
        }
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
