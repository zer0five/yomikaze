package io.gitlab.zeromatter.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.gitlab.zeromatter.yomikaze.persistence.listener.AccountListener;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "account")
@Table(name = "account")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EntityListeners(AccountListener.class)
public class Account {

    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @GeneratedValue(generator = "account-snowflake")
    @Setter(AccessLevel.NONE)
    private Snowflake id;

    public void setId(long id) {
        this.id = Snowflake.of(id);
    }

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            updatable = false
    )
    private String username;

    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            name = "password",
            nullable = false
    )
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_has_role",
            joinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "account_id",
                    referencedColumnName = "id"
            )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Collection<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
