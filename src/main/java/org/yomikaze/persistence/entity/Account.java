package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.yomikaze.persistence.listener.AccountListener;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.json.SnowflakeJsonSerializer;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Entity(name = "account")
@Table(name = "account")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EntityListeners(AccountListener.class)
public class Account implements UserDetails {

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
            name = "account",
            referencedColumnName = "id"
        ),
        inverseJoinColumns = @JoinColumn(
            name = "role",
            referencedColumnName = "id"
        )
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles = new HashSet<>();

    @PrimaryKeyJoinColumn
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Profile profile = new Profile(this);

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "locked", nullable = false)
    private boolean locked = false;

    public void setId(long id) {
        this.id = Snowflake.of(id);
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

    public void setRoles(Collection<Role> roles) {
        this.roles = new HashSet<>(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
            .map(Role::getPermissions)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }

}
