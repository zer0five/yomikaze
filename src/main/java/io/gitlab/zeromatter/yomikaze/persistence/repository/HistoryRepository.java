package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.History;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<History, Snowflake> {
    Page<History> findAllByAccountId(Snowflake accountId, Pageable pageable);

    Page<History> findAllByAccount(Account account, Pageable pageable);
}