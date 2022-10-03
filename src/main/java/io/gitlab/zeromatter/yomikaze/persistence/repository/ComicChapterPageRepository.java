package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.ComicChapterPage;
import org.springframework.data.repository.CrudRepository;

public interface ComicChapterPageRepository extends CrudRepository<ComicChapterPage, Integer> {
}