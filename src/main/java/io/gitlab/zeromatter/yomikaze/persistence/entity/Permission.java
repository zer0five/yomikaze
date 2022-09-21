package io.gitlab.zeromatter.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity(name = "permission")
@Table(name = "permission")
public class Permission {
    @Id
    @Column(nullable = false, updatable = false, insertable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private String name;

    @Column(name = "description", unique = true)
    private String description;

    @ManyToMany(mappedBy = "permissions")
    @ToString.Exclude
    private Set<Role> roles;

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
}
