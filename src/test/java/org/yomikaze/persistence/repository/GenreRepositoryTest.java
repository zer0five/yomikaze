package org.yomikaze.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yomikaze.persistence.entity.Genre;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class GenreRepositoryTest {

    
    @Autowired
    GenreRepository genreRepository;

   @Test
    void testfindByName() {
       Iterable<Genre> genres = genreRepository.saveAll(Arrays.asList(
           new Genre("Action"),
           new Genre("Adventure"),
           new Genre("Comedy"),
           new Genre("Drama"),
           new Genre("Fantasy")
       ));

       Optional<Genre> genreOptional = genreRepository.findByName("Action");
       assertTrue(genreOptional.isPresent());
       Genre genre = genreOptional.get();

       assertEquals("Action",genre.getName());

   }
}
