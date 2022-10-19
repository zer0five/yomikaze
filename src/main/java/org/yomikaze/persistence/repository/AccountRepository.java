package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Snowflake> {

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Account> findByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

}
