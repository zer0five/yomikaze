package io.gitlab.zeromatter.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.gitlab.zeromatter.yomikaze.json.datetime.DateToEpochSerializer;
import io.gitlab.zeromatter.yomikaze.json.snowflake.SnowflakeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
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
    @GeneratedValue(generator = "generate_snowflake()")
    @Column(
            name = "id",
            nullable = false,
            updatable = false,
            insertable = false
    )
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = SnowflakeSerializer.class)
    private Long id;

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

    @Column(
            name = "two_factor_enabled",
            nullable = false
    )
    private boolean twoFactorEnabled = false;

    @Column(
            name = "two_factor_secret"
    )
    @JsonIgnore
    @ToString.Exclude
    private String twoFactorSecret;

    @GeneratedValue(generator = "now()")
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false
    )
    @JsonSerialize(using = DateToEpochSerializer.class)
    private Timestamp createdAt = null;

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
