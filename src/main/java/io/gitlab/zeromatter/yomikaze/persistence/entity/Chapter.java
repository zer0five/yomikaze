package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "chapters")
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(generator = "chapter-snowflake")
    @Column(name = "id", nullable = false, updatable = false)
    private Snowflake id;

    @Column(name = "title", nullable = false)
    private String title = "";

    @Column(name = "index", nullable = false)
    private Long index;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter", nullable = false)
    @ToString.Exclude
    @OrderColumn(name = "index")
    private Set<Page> pages = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Chapter chapter = (Chapter) o;
        return id != null && Objects.equals(id, chapter.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
