package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.json.SnowflakeJsonSerializer;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
    @JsonSerialize(using = SnowflakeJsonSerializer.class)
    private Snowflake id;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private String name;

    @Column(name = "description", length = 1023)
    private String description = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    @JsonIgnore
    private Account creator;

    @ManyToMany(mappedBy = "genres")
    @ToString.Exclude
    @JsonIgnore
    private Collection<Comic> comics = new HashSet<>();

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
