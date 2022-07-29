package io.gitlab.zeromatter.yomikaze.entity.account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Account> findByUsernameOrEmail(String username, String email);

}