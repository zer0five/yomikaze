package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.History;
import org.yomikaze.snowflake.Snowflake;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface HistoryRepository extends CrudRepository<History, Snowflake> {
    Page<History> findAllByAccountId(Snowflake accountId, Pageable pageable);

    Page<History> findAllByAccount(Account account, Pageable pageable);

    long countAllByChapterComicId(Snowflake comicId);

    long countAllByChapterId(Snowflake chapterId);

    long countAllByReadAtAfter(Instant instant);

    long countAllByReadAtBefore(Instant instant);

    long countAllByReadAtBetween(Instant start, Instant end);

    Optional<History> findFirstByAccountAndChapterComicOrderByReadAtDesc(Account account, Comic comic);

    @Query("select distinct h.chapter.comic from history h where h.account = ?1")
    Collection<Comic> findDistinctChapterComicByAccount(Account account);

}
