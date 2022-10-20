package org.yomikaze.snowflake.hibernate;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.yomikaze.snowflake.Snowflake;

public class SnowflakeTypeDescriptor extends AbstractTypeDescriptor<Snowflake> {

    public static final SnowflakeTypeDescriptor INSTANCE = new SnowflakeTypeDescriptor();

    private SnowflakeTypeDescriptor() {
        super(Snowflake.class);
    }

    @Override
    public String toString(Snowflake value) {
        return value.toString();
    }

    @Override
    public Snowflake fromString(String string) {
        return Snowflake.of(string);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(Snowflake value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Snowflake.class.isAssignableFrom(type)) {
            return (X) value;
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return (X) Long.valueOf(value.getId());
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) value.toString();
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> Snowflake wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Long.class.isAssignableFrom(value.getClass())) {
            return Snowflake.of((Long) value);
        }
        if (String.class.isAssignableFrom(value.getClass())) {
            return Snowflake.of((String) value);
        }
        if (Snowflake.class.isAssignableFrom(value.getClass())) {
            return (Snowflake) value;
        }
        throw unknownWrap(value.getClass());
    }
}
