package org.yomikaze.persistence.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "report_comic")
@Table(name = "report_comic")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class ReportComic {
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @GeneratedValue(generator = "report-comic-snowflake")
    @Setter(AccessLevel.NONE)
    private Snowflake id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "comic_id",
            nullable = false,
            updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Comic comic ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reporter_id",
            nullable = false,
            updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Account reporter;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "approved_by",
            nullable = false,
            updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Account approvedBy;

    @Column(name="approved")
    private Boolean approved;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReportComic reportComic = (ReportComic) o;
        return id != null && Objects.equals(id, reportComic.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
