package org.yomikaze.web.dto.comic;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.yomikaze.snowflake.Snowflake;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
public class AdvancedSearchForm {

    private String nameOrAlias = "";
    private String authors = "";
    private List<Snowflake> includeGenre = new ArrayList<>();
    private List<Snowflake> excludeGenre = new ArrayList<>();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date published;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date finished;
    private Integer chapterCount = -1;
    private Integer chapterCountMax = Integer.MAX_VALUE;
    private String uploader;

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
