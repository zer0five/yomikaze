package io.gitlab.zeromatter.yomikaze.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService<ID> {

    ID storeFile(MultipartFile file);

    byte[] loadFile(ID id);

    boolean deleteFile(ID id);

}
