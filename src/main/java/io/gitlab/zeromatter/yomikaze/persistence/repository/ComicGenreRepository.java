package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Comic;
import io.gitlab.zeromatter.yomikaze.persistence.entity.ComicGenre;
import org.springframework.data.repository.CrudRepository;

public interface ComicGenreRepository extends CrudRepository<ComicGenre, Comic> {
}