package io.gitlab.zeromatter.yomikaze.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService<ID, F, O> {

    ID storeFile(MultipartFile file, O owner);

    F loadFile(ID id, O owner);

    boolean deleteFile(ID id);

}
