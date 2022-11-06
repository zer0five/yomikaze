package org.yomikaze.web.dto.comic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Nullable;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.snowflake.Snowflake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class EditComicForm extends CreateComicForm {

    private Snowflake id;
    @Nullable
    private String thumbnail;
    private List<String> chapters = new ArrayList<>();

    public URI getThumbnailUri() {
        return Optional.ofNullable(thumbnail).map(URI::create).orElse(null);
    }

    public List<Chapter> getChapterList() {
        List<Chapter> result = new ArrayList<>();
        for (String chapter : chapters) {
            Snowflake id = Snowflake.of(chapter);
            Chapter c = new Chapter();
            c.setId(id);
            result.add(c);
        }
        return result;
    }


}
