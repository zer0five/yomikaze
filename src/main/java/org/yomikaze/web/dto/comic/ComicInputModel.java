package org.yomikaze.web.dto.comic;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.yomikaze.snowflake.Snowflake;

import javax.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class ComicInputModel {

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

    private List<Snowflake> genres = Collections.emptyList();

    public List<String> getListAliases() {
        return readLines(aliases);
    }

    public List<String> getListAuthors() {
        return readLines(authors);
    }

    private List<String> readLines(String text) {
        List<String> result = new ArrayList<>();
        String textSafe = text == null ? "" : text;
        BufferedReader reader = new BufferedReader(new StringReader(textSafe));
        reader.lines().forEach(result::add);
        return result;
    }
}
