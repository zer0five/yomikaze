package org.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.util.*;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "comment")
@Table(name = "comment")
public class Comment implements Comparable<Comment> {
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
    @JoinColumn(name = "chapter")
    @ToString.Exclude
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic", nullable = false)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comic comic;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    @ToString.Exclude
    @OrderBy("id ASC")
    private Collection<Comment> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    @ToString.Exclude
    private Comment parent;

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

    @Override
    public int compareTo(@NotNull Comment o) {
        return this.id.compareTo(o.id);
    }
}
