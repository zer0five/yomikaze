package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "comic")
public class Comic {
    @Id
    @GeneratedValue(generator = "comic-snowflake")
    @Column(name = "id", nullable = false)
    private Snowflake id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1023)
    private String description = "";

    @Column(name = "cover_picture")
    private String coverPicture;

    @Column(name = "published")
    private Instant published;

    @Column(name = "finished")
    private Instant finished;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comic_genres",
            joinColumns = @JoinColumn(name = "comic_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private Set<Genre> genres = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "uploader", nullable = false)


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