package io.gitlab.zeromatter.yomikaze.service;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Image;
import io.gitlab.zeromatter.yomikaze.persistence.repository.ImageRepository;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DatabaseFileStorageService implements FileStorageService<Snowflake, Image, Account> {

    private final ImageRepository imageRepository;

    @Override
    public Snowflake storeFile(MultipartFile file, Account owner) {
        Validate.notNull(file, "file cannot be null");
        Validate.notNull(file.getContentType(), "File content type cannot be null");
        Validate.notNull(owner, "owner cannot be null");
        MediaType mediaType = MediaType.valueOf(file.getContentType());
        if (!mediaType.getType().equals("image")) {
            throw new IllegalArgumentException("File is not an image");
        }
        byte[] data;
        String name;
        try {
            if (mediaType.getSubtype().equalsIgnoreCase("webp")) {
                name = file.getOriginalFilename();
                data = file.getBytes();
            } else {
                BufferedImage image = ImageIO.read(file.getInputStream());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "webp", outputStream);
                data = outputStream.toByteArray();
                name = Optional.ofNullable(file.getOriginalFilename())
                        .map(s -> s.substring(0, s.lastIndexOf('.')))
                        .orElse("untitled")
                        + ".webp";
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        Image image = new Image();
        image.setName(name);
        image.setOwner(owner);
        image.setType("image/webp");
        image.setData(data);
        imageRepository.save(image);
        return image.getId();
    }

    @Override
    public Image loadFile(Snowflake id, Account owner) {
        return imageRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteFile(Snowflake id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
