package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "role")
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(generator = "role-snowflake")
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Snowflake id;

    @Column(
            name = "name",
            nullable = false,
            unique = true,
            updatable = false
    )
    private String name;

    @Column(name = "description")
    private String description = "";

    @Column(name = "default_role", nullable = false)
    private boolean defaultRole = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id",
                    referencedColumnName = "id"
            )
    )
    @ToString.Exclude
    private Collection<Permission> permissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role role = (Role) o;
        return id != null && Objects.equals(id, role.id);
    }
}
