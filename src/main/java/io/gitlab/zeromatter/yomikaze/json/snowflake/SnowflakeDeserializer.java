package io.gitlab.zeromatter.yomikaze.json.snowflake;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class SnowflakeDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return Long.parseUnsignedLong(p.getValueAsString());
    }
}
