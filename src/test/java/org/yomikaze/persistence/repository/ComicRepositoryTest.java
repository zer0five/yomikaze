package org.yomikaze.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Genre;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
        comic.setName("Test 01");
        comic.setGenres(Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure"), genresMap.get("Comedy")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setName("Test 02");
        comic.setGenres(Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure"), genresMap.get("Drama")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setName("Test 03");
        comic.setGenres(Arrays.asList(genresMap.get("Action"), genresMap.get("Fantasy")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setName("Test 04");
        comic.setGenres(Arrays.asList(genresMap.get("Adventure"), genresMap.get("Comedy")));
        comicRepository.save(comic);
        comic = new Comic();
        comic.setName("One Piece");
        comicRepository.save(comic);
        comic = new Comic();
        comic.setName("ONE PUNCH MAN");
        comicRepository.save(comic);
        comic = new Comic();
        comic.setName("one one");
        comicRepository.save(comic);
        pageable = Pageable.ofSize(10);
        initialized = true;
    }

    @Test
    void testComicContainsAllGenres() {
        Page<Comic> comics = comicRepository.findByGenresContainingAll(Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure")), pageable);
        assertEquals(2, comics.getTotalElements());
        List<String> comicList = comics.getContent().stream().map(Comic::getName).collect(Collectors.toList());
        assertTrue(comicList.contains("Test 01"));
        assertTrue(comicList.contains("Test 02"));
    }

    @Test
    void testComicNotHasGenres() {
        Genre genre = genresMap.get("Fantasy");
        Page<Comic> comics = comicRepository.findByGenresContainingNone(singletonList(genre), pageable);
        assertEquals(6, comics.getTotalElements());
        Collection<String> comicList = comics.stream().map(Comic::getName).collect(Collectors.toList());
        assertTrue(comicList.contains("Test 01"));
        assertTrue(comicList.contains("Test 02"));
        assertFalse(comicList.contains("Test 03"));
        assertTrue(comicList.contains("Test 04"));
        assertTrue(comicList.contains("One Piece"));
        assertTrue(comicList.contains("ONE PUNCH MAN"));
        assertTrue(comicList.contains("one one"));
    }

    @Test
    void testComicAdvancedSearch() {
        Page<Comic> comics = comicRepository.findByGenresContainingAllAndGenresContainingNone(
            Arrays.asList(genresMap.get("Action"), genresMap.get("Adventure")),
            singletonList(genresMap.get("Fantasy")),
            pageable
        );
        assertEquals(2, comics.getTotalElements());
        List<String> comicList = comics.getContent().stream().map(Comic::getName).collect(Collectors.toList());
        assertTrue(comicList.contains("Test 01"));
        assertTrue(comicList.contains("Test 02"));
    }
    @Test
    void testfindByNameContainingIgnoreCase(){
        Page<Comic> comics = comicRepository.findByNameContainingIgnoreCase("One", pageable );
        assertEquals(3,comics.getTotalElements());
        Collection<String> comicList = comics.stream().map(Comic::getName).collect(Collectors.toList());
        assertTrue(comicList.contains("One Piece"));
        assertTrue(comicList.contains("ONE PUNCH MAN"));
        assertTrue(comicList.contains("one one"));
    }
    @Test
    void testFindByNameContainingIgnoreCase1(){
        Page<Comic> comics = comicRepository.findByNameContainingIgnoreCase("PUNCH", pageable);
        assertEquals(1,comics.getTotalElements());
        Collection<String> comicList = comics.stream().map(Comic::getName).collect(Collectors.toList());
        assertTrue(comicList.contains("ONE PUNCH MAN"));

    }
    @Test
    void testfindByNameStartingWithIgnoreCase(){
        Page<Comic> comics = comicRepository.findByNameStartingWithIgnoreCase("p",pageable);
        assertEquals(0,comics.getTotalElements());
    }



}
