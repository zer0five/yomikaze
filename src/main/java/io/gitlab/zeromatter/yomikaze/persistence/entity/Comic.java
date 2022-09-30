package io.gitlab.zeromatter.yomikaze.persistence.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1023)
    private String description;

    @Column(name = "cover_picture")
    private String coverPicture;

    @Column(name = "published")
    private Instant published;

    @Column(name = "finished")
    private Instant finished;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "comic")
    @ToString.Exclude
    private Set<ComicChapter> comicChapters = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "comic_genre",
            joinColumns = @JoinColumn(name = "comic_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @ToString.Exclude
    private Set<Genre> genres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "comic")
    @ToString.Exclude
    private Set<Comment> comments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "comic")
    @ToString.Exclude
    private Set<ComicRating> comicRatings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "comic")
    @ToString.Exclude
    private Set<ListItem> listItems = new LinkedHashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comic comic = (Comic) o;
        return id != null && Objects.equals(id, comic.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}