package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.GenreRepository;
import org.yomikaze.persistence.repository.HistoryRepository;
import org.yomikaze.persistence.repository.LibraryRepository;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.comic.ComicDetailModel;
import org.yomikaze.web.dto.comic.ComicInputModel;
import org.yomikaze.web.dto.comic.GenreModel;
import org.yomikaze.web.dto.comic.chapter.ChapterModel;

import java.io.IOException;
import java.net.URI;
import java.text.Normalizer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ComicService {

    private final GenreRepository genreRepository;
    private final ComicRepository comicRepository;
    private final DatabaseImageStorageService imageStorageService;

    private final LibraryRepository libraryRepository;
    private final HistoryRepository historyRepository;

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    public String getSlug(Comic comic) {
        String name = comic.getName();
        String noWhitespace = WHITESPACE.matcher(name).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public Comic createComic(ComicInputModel comic, MultipartFile thumbnail, Account uploader) {
        Comic comicEntity = new Comic();
        comicEntity.setName(comic.getName());
        comicEntity.setAliases(comic.getListAliases());
        comicEntity.setDescription(comic.getDescription());
        comicEntity.setAuthors(comic.getListAuthors());
        comicEntity.setPublished(comic.getPublished());
        comicEntity.setFinished(comic.getFinished());
        comicEntity.setGenres(genreRepository.findAllByIdIn(comic.getGenres()));
        comicEntity.setUploader(uploader);
        URI thumbnailUrl;
        try {
            Snowflake id = imageStorageService.store(thumbnail);
            thumbnailUrl = URI.create("/image/" + id);
        } catch (IOException e) {
            thumbnailUrl = URI.create("https://via.placeholder.com/200x280/000000/FFFFFF/?text=No+Thumbnail");
        }
        comicEntity.setThumbnail(thumbnailUrl);
        return comicRepository.save(comicEntity);
    }

    public boolean isNew(Comic comic) {
        return comic.getUpdatedAt().isAfter(Instant.now().minus(12, ChronoUnit.HOURS));
    }

    public long countViews(Comic comic) {
        return historyRepository.countAllByComicId(comic.getId());
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
        Collection<ChapterModel> chapters = new TreeSet<>();
        comic.getChapters().forEach(chapter -> {
            ChapterModel chapterModel = new ChapterModel();
            chapterModel.setId(chapter.getId());
            chapterModel.setTitle(chapter.getTitle());
            chapterModel.setIndex(chapter.getIndex());
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
}
