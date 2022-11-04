package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Profile;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<Profile, Snowflake> {


    Optional<Profile> findProfileByAccount(Account account);

}
