package io.gitlab.zeromatter.yomikaze.persistence.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "list")
public class List {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "public", nullable = false)
    private Boolean publicField = false;

    @OneToMany(mappedBy = "list")
    private Set<ListVote> listVotes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "list")
    private Set<ListItem> listItems = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getPublicField() {
        return publicField;
    }

    public void setPublicField(Boolean publicField) {
        this.publicField = publicField;
    }

    public Set<ListVote> getListVotes() {
        return listVotes;
    }

    public void setListVotes(Set<ListVote> listVotes) {
        this.listVotes = listVotes;
    }

    public Set<ListItem> getListItems() {
        return listItems;
    }

    public void setListItems(Set<ListItem> listItems) {
        this.listItems = listItems;
    }

}