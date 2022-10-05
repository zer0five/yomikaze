package io.gitlab.zeromatter.yomikaze.service;

import io.gitlab.zeromatter.yomikaze.persistence.entity.DBFile;
import io.gitlab.zeromatter.yomikaze.persistence.repository.DBFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DatabaseFileStorageService implements FileStorageService<UUID> {

    private final DBFileRepository dbFileRepository;

    @Override
    public UUID storeFile(MultipartFile file) {
        try {
            DBFile dbFile = new DBFile();
            dbFile.setType(file.getContentType());
            dbFile.setName(file.getOriginalFilename());
            dbFile.setData(file.getBytes());
            dbFileRepository.save(dbFile);
            return dbFile.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] loadFile(UUID id) {
        return dbFileRepository.findById(id).map(DBFile::getData).orElse(null);
    }

    @Override
    public boolean deleteFile(UUID id) {
        if (dbFileRepository.existsById(id)) {
            dbFileRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
