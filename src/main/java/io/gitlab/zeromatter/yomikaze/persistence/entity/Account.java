package io.gitlab.zeromatter.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "account")
@Table(name = "account")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Account {

    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @GeneratedValue(generator = "account-snowflake")
    @Type(type = "io.gitlab.zeromatter.yomikaze.snowflake.hibernate.SnowflakeType")
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

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "accounts")
    private Set<Role> roles;

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
