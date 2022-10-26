package org.yomikaze.web.dto.comic;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.yomikaze.snowflake.Snowflake;

@Data
public class GenreModel implements Comparable<GenreModel> {
    private Snowflake id;
    private String name;
    private String description;

    @Override
    public int compareTo(@NotNull GenreModel o) {
        int nameCompare = name.compareTo(o.name);
        if (nameCompare != 0) {
            return nameCompare;
        }
        return id.compareTo(o.id);
    }
}
