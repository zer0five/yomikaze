package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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