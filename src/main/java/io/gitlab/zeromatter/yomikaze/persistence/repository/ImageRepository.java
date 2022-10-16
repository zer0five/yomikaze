package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Image;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image, Snowflake> {

    Optional<Image> findByIdAndOwner_IdAndNameLikeIgnoreCase(Snowflake id, Snowflake owner, String name);

}
