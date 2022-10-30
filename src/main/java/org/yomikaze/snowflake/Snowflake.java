package org.yomikaze.snowflake;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

public class Snowflake implements Serializable, Comparable<Snowflake> {
    @JsonIgnore
    private final long id;

    public Snowflake(long id) {
        this.id = id;
    }

    public Snowflake(Long id) {
        this.id = id == null ? 0 : id;
    }

    public Snowflake(String id) {
        if (id == null || id.isEmpty()) {
            this.id = 0;
            return;
        }
        if (id.startsWith("-")) {
            this.id = Long.parseLong(id);
        } else {
            this.id = Long.parseUnsignedLong(id);
        }
    }

    public static Snowflake of(long id) {
        return new Snowflake(id);
    }

    public static Snowflake of(Long id) {
        return new Snowflake(id);
    }

    public static Snowflake of(String id) {
        return new Snowflake(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnore
    public long getId() {
        return this.id;
    }

    @JsonGetter
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getTimestamp() {
        return new Date(((this.id & SnowflakeFactory.TIMESTAMP_MASK) >> SnowflakeFactory.TIMESTAMP_SHIFT) + SnowflakeFactory.EPOCH);
    }

    @JsonGetter
    public long getWorkerId() {
        return (this.id & SnowflakeFactory.WORKER_ID_MASK) >> SnowflakeFactory.WORKER_ID_SHIFT;
    }

    @JsonGetter
    public long getDataCenterId() {
        return (this.id & SnowflakeFactory.DATACENTER_ID_MASK) >> SnowflakeFactory.DATACENTER_ID_SHIFT;
    }

    @JsonGetter
    public long getSequence() {
        return this.id & SnowflakeFactory.SEQUENCE_MASK >> SnowflakeFactory.SEQUENCE_SHIFT;
    }

    @Override
    public String toString() {
        return Long.toUnsignedString(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Snowflake that = (Snowflake) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull Snowflake o) {
        return Long.compareUnsigned(this.id, o.id);
    }

    public static class Builder {
        private long timestamp;
        private long dataCenterId;
        private long workerId;
        private long sequence;

        private Builder() {
            timestamp = 0L;
            dataCenterId = 0L;
            workerId = 0L;
            sequence = 0L;
        }

        public Builder fromSnowflake(Snowflake snowflake) {
            this.timestamp = snowflake.getTimestamp().getTime();
            this.dataCenterId = snowflake.getDataCenterId();
            this.workerId = snowflake.getWorkerId();
            this.sequence = snowflake.getSequence();
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder timestamp(Date date) {
            this.timestamp = date.getTime();
            return this;
        }

        public Builder dataCenterId(long dataCenterId) {
            this.dataCenterId = dataCenterId;
            return this;
        }

        public Builder workerId(long workerId) {
            this.workerId = workerId;
            return this;
        }

        public Builder sequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Snowflake build() {
            timestamp -= SnowflakeFactory.EPOCH;
            long id = 0;
            id |= timestamp << SnowflakeFactory.TIMESTAMP_SHIFT;
            id |= dataCenterId << SnowflakeFactory.DATACENTER_ID_SHIFT;
            id |= workerId << SnowflakeFactory.WORKER_ID_SHIFT;
            id |= sequence << SnowflakeFactory.SEQUENCE_SHIFT;
            return new Snowflake(id);
        }
    }

}
