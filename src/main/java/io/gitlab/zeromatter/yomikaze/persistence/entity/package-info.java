@TypeDef(name = "snowflake", typeClass = SnowflakeType.class, defaultForType = Snowflake.class)
@GenericGenerator(
        name = "account-snowflake",
        strategy = "io.gitlab.zeromatter.yomikaze.snowflake.hibernate.SnowflakeGenerator",
        parameters = {
                @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
                @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "1")
        }
)
@GenericGenerator(
        name = "role-snowflake",
        strategy = "io.gitlab.zeromatter.yomikaze.snowflake.hibernate.SnowflakeGenerator",
        parameters = {
                @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
                @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "2")
        }
)
@GenericGenerator(
        name = "permission-snowflake",
        strategy = "io.gitlab.zeromatter.yomikaze.snowflake.hibernate.SnowflakeGenerator",
        parameters = {
                @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
                @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "3")
        }
)
package io.gitlab.zeromatter.yomikaze.persistence.entity;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import io.gitlab.zeromatter.yomikaze.snowflake.hibernate.SnowflakeGenerator;
import io.gitlab.zeromatter.yomikaze.snowflake.hibernate.SnowflakeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;