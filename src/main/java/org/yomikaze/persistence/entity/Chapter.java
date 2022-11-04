package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.json.SnowflakeJsonSerializer;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "chapter")
@Table(name = "chapter")
public class Chapter implements Comparable<Chapter> {

    @Id
    @GeneratedValue(generator = "chapter-snowflake")
    @Column(name = "id", nullable = false, updatable = false)
    @JsonSerialize(using = SnowflakeJsonSerializer.class)
    private Snowflake id;

    @Column(name = "title", nullable = false)
    private String title = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Comic comic;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @OrderColumn(name = "index")
    @JsonIgnore
    private List<Page> pages = new ArrayList<>();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant updatedAt;

    public Chapter(Comic comic) {
        this();
        this.comic = comic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(id, chapter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull Chapter o) {
        return id.compareTo(o.id);
    }
}
