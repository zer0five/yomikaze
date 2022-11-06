package org.yomikaze.web.dto.comic;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Genre;
import org.yomikaze.snowflake.Snowflake;

import javax.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CreateComicForm {

    @NotBlank
    private String name;

    private String aliases;

    private String description;

    private String authors;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date published;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date finished;

    private List<Snowflake> genres = new ArrayList<>();

    public CreateComicForm(Comic comic) {
        this.name = comic.getName();
        this.aliases = String.join("\n", comic.getAliases());
        this.authors = String.join("\n", comic.getAuthors());
        this.description = comic.getDescription();
        this.published = comic.getPublished();
        this.finished = comic.getFinished();
        this.genres = comic.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
    }

    public List<String> getAliasList() {
        return readLines(aliases);
    }

    public List<String> getAuthorList() {
        return readLines(authors);
    }

    protected List<String> readLines(String text) {
        List<String> result = new ArrayList<>();
        String textSafe = Optional.ofNullable(text).orElse("");
        BufferedReader reader = new BufferedReader(new StringReader(textSafe));
        reader.lines().forEach(result::add);
        return result;
    }
}
