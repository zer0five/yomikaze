package org.yomikaze.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "report_comment")
@Table(name = "report_comment")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ReportComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Snowflake id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    @ToString.Exclude
    private Account reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "approved_by",
        nullable = false,
        updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Account approvedBy;

    @Column(name = "approved")
    private Boolean approved;

    @JoinColumn(name = "comment")
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReportComment that = (ReportComment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
