package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "files")
@Table(name = "files")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class DBFile {

    @Id
    @GeneratedValue(generator = "file-snowflake")
    @Column(name = "id", nullable = false, updatable = false)
    private Snowflake id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "data", nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "org.hibernate.type.ImageType")
    @ToString.Exclude
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private Account owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DBFile dbFile = (DBFile) o;
        return id != null && Objects.equals(id, dbFile.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id.getId());
    }
}
