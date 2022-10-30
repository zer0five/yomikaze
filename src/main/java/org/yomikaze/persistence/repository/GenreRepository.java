package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Genre;
import org.yomikaze.snowflake.Snowflake;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Snowflake> {
    Optional<Genre> findByName(String genre);

    List<Genre> findAllByIdIn(Iterable<Snowflake> ids);
}
