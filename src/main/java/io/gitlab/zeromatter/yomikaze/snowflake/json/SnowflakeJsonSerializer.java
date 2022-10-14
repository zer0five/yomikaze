package io.gitlab.zeromatter.yomikaze.snowflake.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;

import java.io.IOException;

public class SnowflakeJsonSerializer extends JsonSerializer<Snowflake> {
    @Override
    public void serialize(Snowflake snowflake, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(snowflake.getId());
    }
}
