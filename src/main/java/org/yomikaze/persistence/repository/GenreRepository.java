package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Genre;
import org.yomikaze.snowflake.Snowflake;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Snowflake> {
    Optional<Genre> findByName(String genre);

    List<Genre> findAllByIdIn(Collection<Snowflake> ids);

    Page<Genre> findAllByIdIn(Collection<Snowflake> ids, Pageable pageable);

    List<Genre> findAllByNameIn(Collection<String> names);

    Page<Genre> findAllByNameIn(Collection<String> names, Pageable pageable);

    Page<Genre> findAll(Pageable pageable);

}
