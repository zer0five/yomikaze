package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Genre;
import org.yomikaze.persistence.repository.GenreRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public void createGenre(String name, String description) {
        genreRepository.findByName(name).ifPresent(genre -> {
            throw new EntityExistsException("Genre already exists");
        });
        Genre genre = new Genre();
        genre.setName(name);
        genre.setDescription(description);
        genreRepository.save(genre);
    }

    public void deleteGenre(String name) {
        Genre genre = genreRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        genreRepository.delete(genre);
    }

}
