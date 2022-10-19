package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Image;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image, Snowflake> {

    Optional<Image> findByIdAndOwner_IdAndNameLikeIgnoreCase(Snowflake id, Snowflake owner, String name);

    boolean existsByIdAndOwner(Snowflake id, Account owner);
}
