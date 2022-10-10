package io.gitlab.zeromatter.yomikaze.service;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.DBFile;
import io.gitlab.zeromatter.yomikaze.persistence.repository.DBFileRepository;
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
public class DatabaseFileStorageService implements FileStorageService<Snowflake, DBFile, Account> {

    private final DBFileRepository dbFileRepository;

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
        DBFile dbFile = new DBFile();
        dbFile.setName(name);
        dbFile.setOwner(owner);
        dbFile.setType("image/webp");
        dbFile.setData(data);
        dbFileRepository.save(dbFile);
        return dbFile.getId();
    }

    @Override
    public DBFile loadFile(Snowflake id, Account owner) {
        return dbFileRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteFile(Snowflake id) {
        if (dbFileRepository.existsById(id)) {
            dbFileRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
