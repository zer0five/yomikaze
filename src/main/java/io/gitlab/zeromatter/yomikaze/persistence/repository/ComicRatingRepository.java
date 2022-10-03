package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.ComicRating;
import org.springframework.data.repository.CrudRepository;

public interface ComicRatingRepository extends CrudRepository<ComicRating, Integer> {
}