package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.yomikaze.persistence.entity.Page;
import org.yomikaze.snowflake.Snowflake;

public interface PageRepository extends CrudRepository<Page, Snowflake> {

}
