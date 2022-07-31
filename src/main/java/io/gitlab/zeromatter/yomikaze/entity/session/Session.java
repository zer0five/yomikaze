package io.gitlab.zeromatter.yomikaze.entity.session;

import com.vladmihalcea.hibernate.type.basic.Inet;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLInetType;
import io.gitlab.zeromatter.yomikaze.entity.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Entity
@Table(name = "session")
@NoArgsConstructor
@TypeDef(
        name = "ipv4",
        typeClass = PostgreSQLInetType.class,
        defaultForType = Inet.class
)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "gen_random_uuid()")
    @Column(
            name = "id",
            nullable = false,
            unique = true,
            updatable = false,
            insertable = false
    )
    private String id;

    @JoinColumn(
            name = "account_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Account account;

    @Column(name = "user_agent")
    private String userAgent = null;

    @Column(name = "remote_address", columnDefinition = "inet")
    private Inet remoteAddress = null;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "last_used", nullable = false, insertable = false)
    private Timestamp lastUsed;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Timestamp createdAt = null;

    public Session(Account account) {
        this.account = account;
    }

    public void setRemoteAddress(String address) {
        setRemoteAddress(new Inet(address));
    }

    public void setRemoteAddress(Inet remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
