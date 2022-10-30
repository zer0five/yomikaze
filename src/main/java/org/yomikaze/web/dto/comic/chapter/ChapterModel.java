package org.yomikaze.web.dto.comic.chapter;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.yomikaze.snowflake.Snowflake;

@Data
public class ChapterModel implements Comparable<ChapterModel> {
    private Snowflake id;
    private long index;
    private String title;

    @Override
    public int compareTo(@NotNull ChapterModel o) {
        return -Long.compare(index, o.index);
    }
}
