package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.ComicChapter;
import org.springframework.data.repository.CrudRepository;

public interface ComicChapterRepository extends CrudRepository<ComicChapter, Integer> {
}