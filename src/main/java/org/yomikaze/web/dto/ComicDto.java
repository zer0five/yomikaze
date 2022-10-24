package org.yomikaze.web.dto;


import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
public class ComicDto {
    private String name;
    private String aliases;
    private String description;
    private String thumbnailUrl;
    private String authors;
    private Instant published;
    private Instant finished;
    private List<Long> genres;
}
