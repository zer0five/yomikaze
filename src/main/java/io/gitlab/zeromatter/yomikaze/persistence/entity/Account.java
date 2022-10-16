package io.gitlab.zeromatter.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.gitlab.zeromatter.yomikaze.persistence.listener.AccountListener;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import io.gitlab.zeromatter.yomikaze.snowflake.json.SnowflakeJsonSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@ToString
@Entity(name = "account")
@Table(name = "account")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EntityListeners(AccountListener.class)
public class Account {

    @Id
    @JoinColumn(
            name = "id",
            nullable = false,
            updatable = false
    )
    @GeneratedValue(generator = "account-snowflake")
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = SnowflakeJsonSerializer.class)
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
    private Set<Role> roles = new HashSet<>();

    @PrimaryKeyJoinColumn
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile = new Profile(this);

    public void setRoles(Collection<Role> roles) {
        this.roles = new HashSet<>(roles);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addRoles(Role... roles) {
        this.roles.addAll(Arrays.asList(roles));
    }

    public void addRoles(Collection<Role> roles) {
        this.roles.addAll(roles);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void removeRoles(Role... roles) {
        Arrays.asList(roles).forEach(this.roles::remove);
    }

    public void removeRoles(Collection<Role> roles) {
        this.roles.removeAll(roles);
    }

    public Collection<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id.getId());
    }

}
