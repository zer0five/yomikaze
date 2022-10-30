package org.yomikaze.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ComicTest {

    @Autowired
    ComicRepository comicRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    PageRepository pageRepository;

    @Test
    void test() {
        Comic comic = new Comic();
        comic.setName("Test 01");

        Chapter chapter = new Chapter(comic);
        chapter.setTitle("Chapter 01");

        Page page1 = new Page(chapter);
        page1.setUri("/image/1");
        Page page2 = new Page(chapter);
        page2.setUri("/image/2");

        chapter.getPages().add(page1);
        chapter.getPages().add(page2);

        comic.getChapters().add(chapter);
        comicRepository.save(comic);

        chapter = chapterRepository.findById(chapter.getId()).orElse(null);
        assertNotNull(chapter);
        assertFalse(chapter.getPages().isEmpty());
        assertEquals(2, chapter.getPages().size());
        long i = 0;
        for (Page page : chapter.getPages()) {
            assertEquals(++i, page.getUri().charAt(page.getUri().length() - 1) - '0');
        }
    }

}
