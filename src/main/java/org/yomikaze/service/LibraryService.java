package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Library;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.LibraryRepository;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final ComicRepository comicRepository;

    public Page<Library> getLibrary(Account account, Pageable pageable) {
        return libraryRepository.findAllByAccount(account, pageable);
    }

    public void addComic(Account account, Comic comic) {
        libraryRepository
            .findByAccountAndComic(account, comic.getId())
            .ifPresent(library -> {
                throw new EntityExistsException("Comic already in library");
            });
        Library library = new Library();
        library.setAccount(account);
        library.setComic(comic);
        libraryRepository.save(library);
    }

    public void addComic(Account account, Snowflake comicId) {
        Comic comic = comicRepository.findById(comicId).orElseThrow(EntityNotFoundException::new);
        addComic(account, comic);
    }

    public void removeComic(Account account, Comic comic) {
        Library library = libraryRepository.findByAccountAndComic(account, comic.getId())
            .orElseThrow(EntityNotFoundException::new);
        libraryRepository.delete(library);
    }

    public void removeComic(Account account, Snowflake comicId) {
        Comic comic = comicRepository.findById(comicId).orElseThrow(EntityNotFoundException::new);
        removeComic(account, comic);
    }

    public void toggleComic(Account account, Snowflake id) {
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Optional<Library> library = libraryRepository.findByAccountAndComic(account, comic.getId());
        if (library.isPresent()) {
            libraryRepository.delete(library.get());
        } else {
            addComic(account, comic);
        }
    }

    public boolean isInLibrary(Account account, Snowflake id) {
        return libraryRepository.findByAccountAndComic(account, id).isPresent();
    }
}
