package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {
    Optional<Genre> findByName(String genre);
}