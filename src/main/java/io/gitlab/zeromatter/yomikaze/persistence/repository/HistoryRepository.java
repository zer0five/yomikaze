package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.History;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<History, Snowflake> {
}