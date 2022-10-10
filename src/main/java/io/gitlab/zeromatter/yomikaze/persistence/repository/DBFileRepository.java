package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.DBFile;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBFileRepository extends CrudRepository<DBFile, Snowflake> {

}
