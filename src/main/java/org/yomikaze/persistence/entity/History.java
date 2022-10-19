package org.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@Entity(name = "history")
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(generator = "history-snowflake")
    @Column(name = "id", nullable = false)
    private Snowflake id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account", nullable = false)
    @ToString.Exclude
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "chapter", nullable = false)
    @ToString.Exclude
    private Chapter chapter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        History history = (History) o;
        return Objects.equals(id, history.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
