package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Comic;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=dev")
class ComicRepositoryTest {

    static Map<String, Genre> genresMap;
    static Pageable pageable;
    static boolean initialized = false;
    @Autowired
    ComicRepository comicRepository;
    @Autowired
    GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        if (initialized) return;
        Iterable<Genre> genres = genreRepository.saveAll(Arrays.asList(
                new Genre("Action"),
                new Genre("Adventure"),
                new Genre("Comedy"),
                new Genre("Drama"),
                new Genre("Fantasy")
        ));
        genresMap = StreamSupport.stream(genres.spliterator(), false)
                .collect(Collectors.toMap(Genre::getName, Function.identity()));
        Comic comic = new Comic();
        comic.setTitle("Test 01");
        comic.setGenres(Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure"), genresMap.get("Comedy")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setTitle("Test 02");
        comic.setGenres(Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure"), genresMap.get("Drama")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setTitle("Test 03");
        comic.setGenres(Arrays.asList(genresMap.get("Action"), genresMap.get("Fantasy")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setTitle("Test 04");
        comic.setGenres(Arrays.asList(genresMap.get("Adventure"), genresMap.get("Comedy")));
        comicRepository.save(comic);
        pageable = Pageable.ofSize(10);
        initialized = true;
    }

    @Test
    void testComicContainsAllGenres() {
        Page<Comic> comics = comicRepository.findByGenresContainingAll(Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure")), pageable);
        assertEquals(2, comics.getTotalElements());
        List<String> comicList = comics.getContent().stream().map(Comic::getTitle).collect(Collectors.toList());
        assertTrue(comicList.contains("Test 01"));
        assertTrue(comicList.contains("Test 02"));
    }

    @Test
    void testComicNotHasGenres() {
        Genre genre = genresMap.get("Fantasy");
        Page<Comic> comics = comicRepository.findByGenresContainingNone(singletonList(genre), pageable);
        assertEquals(3, comics.getTotalElements());
        Collection<String> comicList = comics.stream().map(Comic::getTitle).collect(Collectors.toList());
        assertTrue(comicList.contains("Test 01"));
        assertTrue(comicList.contains("Test 02"));
        assertFalse(comicList.contains("Test 03"));
        assertTrue(comicList.contains("Test 04"));
    }

    @Test
    void testComicAdvancedSearch() {
        Page<Comic> comics = comicRepository.findByGenresContainingAllAndGenresContainingNone(
                Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure")),
                singletonList(genresMap.get("Fantasy")),
                pageable
        );
        assertEquals(2, comics.getTotalElements());
        List<String> comicList = comics.getContent().stream().map(Comic::getTitle).collect(Collectors.toList());
        assertTrue(comicList.contains("Test 01"));
        assertTrue(comicList.contains("Test 02"));
    }
}