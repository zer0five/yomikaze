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
@Entity
@Table(name = "request")
public class Request {
    @Id
    @Column(name = "id",
        nullable = false,
        updatable = false)
    @GeneratedValue(generator = "snowflake")
    private Snowflake id;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "is_approved")
    private Boolean approved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "requester",
        referencedColumnName = "id", nullable = false

    )
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account requester;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "approved_by",
        referencedColumnName = "id"

    )
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account approvedBy = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Request request = (Request) o;
        return Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
