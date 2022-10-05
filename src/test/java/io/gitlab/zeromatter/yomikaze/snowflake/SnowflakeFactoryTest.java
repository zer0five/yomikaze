package io.gitlab.zeromatter.yomikaze.snowflake;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SnowflakeFactoryTest {

    @Test
    void next() {
        for (long i = 0; i < 32; i++) {
            SnowflakeFactory factory = new SnowflakeFactory(1, i);
            Snowflake previous = factory.next();
            for (long j = 0; j < 100; j++) {
                Snowflake snowflake = factory.next();
                assertSame(i, snowflake.getWorkerId());
                assertSame(1L, snowflake.getDataCenterId());
                assertNotEquals(previous, snowflake);
            }
        }
    }
}