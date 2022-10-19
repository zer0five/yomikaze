package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "library")
@Table(name = "library")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Library {

    @Id
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @GeneratedValue(generator = "library-snowflake")
    @Setter(AccessLevel.NONE)
    private Snowflake id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "comic_id",
        nullable = false,
        updatable = false
    )
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comic comic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "account_id",
        nullable = false,
        updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Account account;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Library library = (Library) o;
        return Objects.equals(id, library.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
