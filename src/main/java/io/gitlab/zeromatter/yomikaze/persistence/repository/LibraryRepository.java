package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Library;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryRepository extends CrudRepository<Library, Snowflake> {
    Page<Library> findAllByAccount(Account account, Pageable pageable);

    Page<Library> findAllByAccountId(Snowflake accountId, Pageable pageable);
}
