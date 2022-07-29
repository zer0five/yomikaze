package io.gitlab.zeromatter.yomikaze.entity.session;

import io.gitlab.zeromatter.yomikaze.entity.account.Account;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "session")
public class Session {
    @Id
    @Column(name = "token", nullable = false, unique = true, updatable = false)
    private String token;

    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Account account;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "created_at", nullable = false, insertable = false)
    private Timestamp createdAt = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Session session = (Session) o;
        return token != null && Objects.equals(token, session.token);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
