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
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.json.SnowflakeJsonSerializer;

import javax.persistence.*;
import java.net.URI;
import java.time.Instant;
import java.util.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "comic")
@Table(name = "comic")
public class Comic {
    public static final Sort SORT_BY_UPDATED_AT = Sort.sort(Comic.class).by(Comic::getUpdatedAt).descending();
    public static final Sort SORT_BY_ID = Sort.sort(Comic.class).by(Comic::getId).descending();
    public static final Sort SORT_BY_NAME = Sort.sort(Comic.class).by(Comic::getName).ascending();
    public static final Sort DEFAULT_SORT = SORT_BY_UPDATED_AT.and(SORT_BY_ID).and(SORT_BY_NAME);
    @Id
    @GeneratedValue(generator = "comic-snowflake")
    @Column(name = "id", nullable = false)
    @JsonSerialize(using = SnowflakeJsonSerializer.class)
    private Snowflake id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "aliases")
    @ElementCollection
    private Collection<String> aliases;

    @Column(name = "authors")
    @ElementCollection
    private Collection<String> authors;

    @Column(name = "description", length = 1023)
    private String description = "";

    @Column(name = "thumbnail")
    private URI thumbnail;

    @Column(name = "published")
    private Date published;

    @Column(name = "finished")
    private Date finished;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comic_genres",
        joinColumns = @JoinColumn(name = "comic_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private Collection<Genre> genres = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader")
    @ToString.Exclude
    @JsonIgnore
    private Account uploader = null;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comic", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @OrderColumn(name = "index")
    private List<Chapter> chapters = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comic comic = (Comic) o;
        return Objects.equals(id, comic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
