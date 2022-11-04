package org.yomikaze.web.dto.comic.chapter;

import lombok.Data;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Data
public class ChapterForm {
    private String title;
    private long index;
    private String pages;

    public Collection<URI> getPagesUri() {
        if (pages == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(pages.split("\n"))
            .map(String::trim)
            .map(URI::create)
            .collect(Collectors.toList());
    }


}
