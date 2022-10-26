package org.yomikaze.web.dto.comic;

import lombok.Data;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.comic.chapter.ChapterModel;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class ComicDetailModel {

    // basic info
    private Snowflake id;
    private String name;
    private Collection<String> aliases;
    private Collection<String> authors;
    private String description;
    private URI thumbnail;
    private Date published;
    private Date finished;
    private Instant updatedAt;

    // extra info
    private Collection<ChapterModel> chapters;
    private Collection<GenreModel> genres;

    private boolean isNew;
    private long views;
    private long inLibraries;

}
