package org.yomikaze.web.dto.comic;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.yomikaze.snowflake.Snowflake;

import javax.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
public class ComicForm {

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

    public List<String> getAliasList() {
        return readLines(aliases);
    }

    public List<String> getAuthorList() {
        return readLines(authors);
    }

    private List<String> readLines(String text) {
        List<String> result = new ArrayList<>();
        String textSafe = Optional.ofNullable(text).orElse("");
        BufferedReader reader = new BufferedReader(new StringReader(textSafe));
        reader.lines().forEach(result::add);
        return result;
    }
}
