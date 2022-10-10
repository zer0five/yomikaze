package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "permission")
@Table(name = "permission")
public class Permission implements GrantedAuthority {
    @Id
    @Column(nullable = false, updatable = false, insertable = false)
    @GeneratedValue(generator = "permission-snowflake")
    private Snowflake id;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private String name;

    @Column(name = "description", unique = true)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Permission that = (Permission) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
