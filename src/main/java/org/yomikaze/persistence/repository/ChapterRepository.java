package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.snowflake.Snowflake;

@Repository
public interface ChapterRepository extends CrudRepository<Chapter, Snowflake> {

}
