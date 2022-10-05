package io.gitlab.zeromatter.yomikaze.snowflake;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class SnowflakeJsonSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("raw", Long.toUnsignedString(value));
        gen.writeObjectField("snowflake", Snowflake.of(value));
        gen.writeEndObject();
    }
}
