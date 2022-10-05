package io.gitlab.zeromatter.yomikaze.snowflake.hibernate;

import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import io.gitlab.zeromatter.yomikaze.snowflake.SnowflakeFactory;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeGenerator implements IdentifierGenerator {
    public static final String SNOWFLAKE_DATA_CENTER_ID = "snowflake.datacenterId";
    public static final String SNOWFLAKE_WORKER_ID = "snowflake.workerId";
    private static final AtomicLong DATA_CENTER_ID = new AtomicLong(0);
    private static final AtomicLong WORKER_ID = new AtomicLong(0);

    private SnowflakeFactory snowflakeFactory;

    private boolean raw = false;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        if (Long.class.isAssignableFrom(type.getReturnedClass())) {
            raw = true;
        } else if (!Snowflake.class.isAssignableFrom(type.getReturnedClass())) {
            throw new MappingException("SnowflakeGenerator only supports Snowflake or Long");
        }
        if (params.containsKey(SNOWFLAKE_DATA_CENTER_ID)) {
            DATA_CENTER_ID.set(Long.parseLong(params.getProperty(SNOWFLAKE_DATA_CENTER_ID)));
        }
        String dataCenterIdStr = params.getProperty(SNOWFLAKE_DATA_CENTER_ID);
        String workerIdStr = params.getProperty(SNOWFLAKE_WORKER_ID);
        long dataCenterId;
        if (dataCenterIdStr == null || dataCenterIdStr.isEmpty()) {
            dataCenterId = DATA_CENTER_ID.getAndUpdate(operand -> (operand + 1) % SnowflakeFactory.MAX_DATACENTER_ID);
        } else {
            dataCenterId = Long.parseLong(dataCenterIdStr);
        }
        long workerId;
        if (workerIdStr == null || workerIdStr.isEmpty()) {
            workerId = WORKER_ID.getAndUpdate(operand -> (operand + 1) % SnowflakeFactory.MAX_WORKER_ID);
        } else {
            workerId = Long.parseLong(workerIdStr);
        }
        snowflakeFactory = new SnowflakeFactory(dataCenterId, workerId);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return raw ? snowflakeFactory.nextId() : snowflakeFactory.next();
    }
}
