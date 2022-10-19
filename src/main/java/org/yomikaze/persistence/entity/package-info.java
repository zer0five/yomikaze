@TypeDef(name = "snowflake", typeClass = SnowflakeType.class, defaultForType = Snowflake.class)
@GenericGenerator(
    name = "snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "0"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "0")
    }
)
@GenericGenerator(
    name = "account-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "1")
    }
)
@GenericGenerator(
    name = "role-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "2")
    }
)
@GenericGenerator(
    name = "permission-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "3")
    }
)
@GenericGenerator(
    name = "history-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "4")
    }
)
@GenericGenerator(
    name = "comic-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "5")
    }
)
@GenericGenerator(
    name = "genre-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "6")
    }
)
@GenericGenerator(
    name = "image-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "7")
    }
)
@GenericGenerator(
    name = "library-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "8")
    }
)
@GenericGenerator(
    name = "chapter-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "9")
    }
)
@GenericGenerator(
    name = "page-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "10")
    }
)
@GenericGenerator(
    name = "comment-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "11")
    }
)
@GenericGenerator(
    name = "report-comic-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "9")
    }
)
@GenericGenerator(
    name = "report-comment-snowflake",
    strategy = "org.yomikaze.snowflake.hibernate.SnowflakeGenerator",
    parameters = {
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_DATA_CENTER_ID, value = "1"),
        @Parameter(name = SnowflakeGenerator.SNOWFLAKE_WORKER_ID, value = "10")
    }
)
package org.yomikaze.persistence.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.snowflake.hibernate.SnowflakeGenerator;
import org.yomikaze.snowflake.hibernate.SnowflakeType;
