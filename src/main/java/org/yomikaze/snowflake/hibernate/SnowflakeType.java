package org.yomikaze.snowflake.hibernate;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;
import org.yomikaze.snowflake.Snowflake;

public class SnowflakeType extends AbstractSingleColumnStandardBasicType<Snowflake> {

    public static final SnowflakeType INSTANCE = new SnowflakeType();

    public SnowflakeType() {
        super(BigIntTypeDescriptor.INSTANCE, SnowflakeTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "snowflake";
    }
}
