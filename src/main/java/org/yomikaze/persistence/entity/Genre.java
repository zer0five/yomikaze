package org.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "genres")
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue(generator = "genre-snowflake")
    @Column(name = "id", nullable = false, updatable = false)
    private Snowflake id;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private String name;

    @Column(name = "description", length = 1023)
    private String description = "";

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Comic> comics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    private Account creator;

    public Genre(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
