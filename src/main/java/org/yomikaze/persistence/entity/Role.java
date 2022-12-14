package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.json.SnowflakeJsonSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "role")
@Table(name = "role")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Role implements Serializable {
    @Id
    @GeneratedValue(generator = "role-snowflake")
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    @JsonSerialize(using = SnowflakeJsonSerializer.class)
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
    @JsonIgnore
    private boolean defaultRole = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
        joinColumns = @JoinColumn(
            name = "role",
            referencedColumnName = "id"
        ),
        inverseJoinColumns = @JoinColumn(
            name = "permission",
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
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
