package org.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

@Entity(name = "image")
@Table(name = "image")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Image {

    @Id
    @GeneratedValue(generator = "image-snowflake")
    @Column(name = "id", nullable = false, updatable = false)
    private Snowflake id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "data", nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", referencedColumnName = "id")
    @ToString.Exclude
    private Account owner;

    @Transient
    private MediaType mediaType = null;

    public MediaType getMediaType() {
        if (mediaType == null) {
            try {
                mediaType = MediaType.parseMediaType(type);
            } catch (InvalidMediaTypeException e) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }
        return mediaType;
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(data);
    }

    public Resource toResource() {
        return new InputStreamResource(toInputStream());
    }

    public int size() {
        return data.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Image image = (Image) o;
        return id != null && Objects.equals(id, image.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean isEmpty() {
        return data == null || data.length == 0;
    }
}
