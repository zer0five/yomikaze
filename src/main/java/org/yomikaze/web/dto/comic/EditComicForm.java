package org.yomikaze.web.dto.comic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.snowflake.Snowflake;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EditComicForm extends CreateComicForm {

    private Snowflake id;

    @Nullable
    private String thumbnailUri;
    private String chapters;

    public EditComicForm(Comic comic) {
        super(comic);
        this.id = comic.getId();
        this.thumbnailUri = comic.getThumbnail().toString();
        this.chapters = comic.getChapters().stream().map(Chapter::getId)
            .map(Snowflake::toString).collect(Collectors.joining("\n"));
    }

    public List<Chapter> getChapterList() {
        List<Chapter> result = new ArrayList<>();
        List<String> rawChapters = readLines(chapters);
        for (String chapter : rawChapters) {
            Snowflake id = Snowflake.of(chapter);
            Chapter c = new Chapter();
            c.setId(id);
            result.add(c);
        }
        return result;
    }


}
