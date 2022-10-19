package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Genre;

import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {
    Optional<Genre> findByName(String genre);
}
