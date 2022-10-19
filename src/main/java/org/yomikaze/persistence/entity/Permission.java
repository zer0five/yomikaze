package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.json.SnowflakeJsonSerializer;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "permission")
@Table(name = "permission")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Permission implements GrantedAuthority {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(generator = "permission-snowflake")
    @JsonSerialize(using = SnowflakeJsonSerializer.class)
    private Snowflake id;

    @Column(name = "authority", nullable = false, unique = true, updatable = false)
    private String authority;

    @Column(name = "description", length = 1023)
    private String description = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
