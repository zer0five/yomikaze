package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.History;
import org.yomikaze.snowflake.Snowflake;

public interface HistoryRepository extends CrudRepository<History, Snowflake> {
    @Query("select count(h) from history h where h.chapter.comic.id = ?1")
    long countAllByComicId(Snowflake comicId);
    Page<History> findAllByAccountId(Snowflake accountId, Pageable pageable);

    Page<History> findAllByAccount(Account account, Pageable pageable);
}
