package org.yomikaze.service;

import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface IFileStorageService<I, F, O> {

    I store(MultipartFile file, @Nullable O owner) throws IOException;

    default I store(MultipartFile file) throws IOException {
        return store(file, null);
    }

    default Optional<I> storeSafe(MultipartFile file, @Nullable O owner) {
        try {
            return Optional.of(store(file, owner));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    default Optional<I> storeSafe(MultipartFile file) {
        try {
            return Optional.of(store(file));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    F retrieve(I id, @Nullable O owner) throws IOException;

    default F retrieve(I id) throws IOException {
        return retrieve(id, null);
    }

    default Optional<F> retrieveSafe(I id, @Nullable O owner) {
        try {
            return Optional.of(retrieve(id, owner));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    default Optional<F> retrieveSafe(I id) {
        try {
            return Optional.of(retrieve(id));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    void delete(I id, @Nullable O owner) throws IOException;

    default void delete(I id) throws IOException {
        delete(id, null);
    }

    default boolean deleteSafe(I id, @Nullable O owner) {
        try {
            delete(id, owner);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    default boolean deleteSafe(I id) {
        try {
            delete(id);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    boolean exists(I id, @Nullable O owner) throws IOException;

    default boolean exists(I id) throws IOException {
        return exists(id, null);
    }

    default Optional<Boolean> existsSafe(I id, @Nullable O owner) {
        try {
            return Optional.of(exists(id, owner));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    default Optional<Boolean> existsSafe(I id) {
        try {
            return Optional.of(exists(id));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
