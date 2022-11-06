package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Page;
import org.yomikaze.persistence.repository.*;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.comic.ComicDetailModel;
import org.yomikaze.web.dto.comic.CreateComicForm;
import org.yomikaze.web.dto.comic.EditComicForm;
import org.yomikaze.web.dto.comic.GenreModel;
import org.yomikaze.web.dto.comic.chapter.ChapterForm;
import org.yomikaze.web.dto.comic.chapter.ChapterModel;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.Normalizer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ComicService {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private final GenreRepository genreRepository;
    private final ComicRepository comicRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final LibraryRepository libraryRepository;
    private final HistoryRepository historyRepository;
    private final DatabaseImageStorageService imageStorageService;

    public String getSlug(Comic comic) {
        String name = comic.getName();
        String noWhitespace = WHITESPACE.matcher(name).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public Comic createComic(CreateComicForm comic, MultipartFile thumbnail, Account uploader) {
        Comic comicEntity = new Comic();
        comicEntity.setName(comic.getName());
        comicEntity.setAliases(comic.getAliasList());
        comicEntity.setDescription(comic.getDescription());
        comicEntity.setAuthors(comic.getAuthorList());
        comicEntity.setPublished(comic.getPublished());
        comicEntity.setFinished(comic.getFinished());
        comicEntity.setGenres(genreRepository.findAllByIdIn(comic.getGenres()));
        comicEntity.setUploader(uploader);
        if (!thumbnail.isEmpty()) {
            try {
                Snowflake id = imageStorageService.store(thumbnail);
                URI thumbnailUrl = URI.create("/image/" + id);
                comicEntity.setThumbnail(thumbnailUrl);
            } catch (IOException ignore) {
                // IGNORED
            }
        }
        return comicRepository.save(comicEntity);
    }

    public boolean isNew(Comic comic) {
        return comic.getUpdatedAt().isAfter(Instant.now().minus(12, ChronoUnit.HOURS));
    }

    public long countViews(Comic comic) {
        return historyRepository.countAllByChapterComicId(comic.getId());
    }

    public long countInLibraries(Comic comic) {
        return libraryRepository.countAllByComic(comic);
    }

    public ComicDetailModel getComicDetail(Comic comic) {
        ComicDetailModel model = new ComicDetailModel();
        model.setId(comic.getId());
        model.setName(comic.getName());
        model.setAliases(comic.getAliases());
        model.setAuthors(comic.getAuthors());
        model.setDescription(comic.getDescription());
        model.setThumbnail(comic.getThumbnail());
        model.setPublished(comic.getPublished());
        model.setFinished(comic.getFinished());
        model.setUpdatedAt(comic.getUpdatedAt());
        model.setUploader(comic.getUploader());
        Collection<ChapterModel> chapters = new TreeSet<>();
        comic.getChapters().forEach(chapter -> {
            ChapterModel chapterModel = new ChapterModel();
            chapterModel.setId(chapter.getId());
            chapterModel.setTitle(chapter.getTitle());
            chapters.add(chapterModel);
        });
        model.setChapters(chapters);
        Collection<GenreModel> genres = new TreeSet<>();
        comic.getGenres().forEach(genre -> {
            GenreModel genreModel = new GenreModel();
            genreModel.setId(genre.getId());
            genreModel.setName(genre.getName());
            genres.add(genreModel);
        });
        model.setGenres(genres);
        model.setNew(isNew(comic));
        model.setViews(countViews(comic));
        model.setInLibraries(countInLibraries(comic));
        return model;
    }

    public void addChapter(ComicDetailModel comic, ChapterForm chapter) {
        Comic comicEntity = comicRepository.findById(comic.getId()).orElseThrow(EntityNotFoundException::new);
        Chapter chapterEntity = new Chapter(comicEntity);
        chapterEntity.setTitle(chapter.getTitle());
        for (URI pageUri : chapter.getPagesUri()) {
            Page page = new Page(chapterEntity);
            page.setUri(pageUri.toString());
            chapterEntity.getPages().add(page);
        }
        comicEntity.getChapters().add(chapterEntity);
        comicRepository.save(comicEntity);
    }

    public Comic updateComic(Snowflake id, EditComicForm comic, MultipartFile thumbnail) {
        Comic comicEntity = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        comicEntity.setName(comic.getName());
        comicEntity.setAliases(comic.getAliasList());
        comicEntity.setDescription(comic.getDescription());
        comicEntity.setAuthors(comic.getAuthorList());
        comicEntity.setPublished(comic.getPublished());
        comicEntity.setFinished(comic.getFinished());
        comicEntity.setGenres(genreRepository.findAllByIdIn(comic.getGenres()));
        if (!thumbnail.isEmpty()) {
            try {
                Snowflake imageId = imageStorageService.store(thumbnail);
                URI thumbnailUrl = URI.create("/image/" + imageId);
                comicEntity.setThumbnail(thumbnailUrl);
            } catch (IOException ignore) {
                // IGNORED
            }
        }
        return comicRepository.save(comicEntity);
    }
}
