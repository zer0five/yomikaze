package io.gitlab.zeromatter.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "role")
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    @Column(
            name = "id",
            nullable = false,
            updatable = false,
            insertable = false
    )
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            unique = true,
            updatable = false
    )
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(targetEntity = Account.class)
    @JoinTable(name = "account_role",
            joinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "account_id",
                    referencedColumnName = "id"
            )
    )
    @ToString.Exclude
    private Set<Account> accounts;

    @ManyToMany(targetEntity = Permission.class)
    @JoinTable(name = "role_permission",
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
    private Set<Permission> permissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role role = (Role) o;
        return id != null && Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
