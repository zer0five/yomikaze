package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "profile")
@Table(name = "profile")
public class Profile {

    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @Setter(AccessLevel.NONE)
    private Snowflake id;

    @MapsId
    @OneToOne(mappedBy = "profile")
    @JoinColumn(
            name = "id",
            nullable = false,
            updatable = false
    )
    @ToString.Exclude
    private Account account;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "picture")
    private String picture;

    @Column(name = "bio")
    private String bio;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "show_birthday")
    private boolean showBirthday = false;

    @Column(name = "show_email")
    private boolean showEmail = false;

    @Column(name = "show_library")
    private boolean showLibrary = false;

    public Profile() {
    }

    public Profile(Account account) {
        this.account = account;
        this.id = account.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Profile profile = (Profile) o;
        return Objects.equals(account, profile.account);
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }
}
