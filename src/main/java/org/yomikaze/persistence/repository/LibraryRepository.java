package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Library;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

@Repository
public interface LibraryRepository extends CrudRepository<Library, Snowflake> {
    Page<Library> findAllByAccount(Account account, Pageable pageable);

    Page<Library> findAllByAccountId(Snowflake accountId, Pageable pageable);

    long countAllByComic(Comic comic);

    Optional<Library> findByAccountAndComic(Account account, Comic comic);

    @Query("select l from library l where l.account = ?1 and l.comic.id = ?2")
    Optional<Library> findByAccountAndComic(Account account, Snowflake comicId);
}
