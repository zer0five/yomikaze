package org.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "comment")
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(generator = "comment-snowflake")
    @Column(name = "id", nullable = false, updatable = false)
    private Snowflake id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account", nullable = false)
    @ToString.Exclude
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter", nullable = false)
    @ToString.Exclude
    private Chapter chapter;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    @ToString.Exclude
    private Set<Comment> replies = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "comment_likes",
        joinColumns = @JoinColumn(name = "comment"),
        inverseJoinColumns = @JoinColumn(name = "account"))
    @ToString.Exclude
    private Set<Account> likedBy = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
