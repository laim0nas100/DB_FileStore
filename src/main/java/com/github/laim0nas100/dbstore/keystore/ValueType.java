package com.github.laim0nas100.dbstore.keystore;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author laim0nas100
 */
public abstract class ValueType<T> {

    public static final StringType STRING = new StringType();
    public static final LongType LONG = new LongType();
    public static final DoubleType DOUBLE = new DoubleType();
    public static final BooleanType BOOLEAN = new BooleanType();
    public static final TimestampType TIMESTAMP = new TimestampType();
    public static final BigIntegerType BIG_INTEGER = new BigIntegerType();
    public static final BigDecimalType BIG_DECIMAL = new BigDecimalType();
    public static final UUIDType UUID = new UUIDType();
    public static final JsonType JSON = new JsonType();

    public static final List<ValueType> ALL_TYPES
            = Collections.unmodifiableList(Stream.<ValueType>builder()
                    .add(STRING)
                    .add(LONG)
                    .add(DOUBLE)
                    .add(BOOLEAN)
                    .add(TIMESTAMP)
                    .add(BIG_INTEGER)
                    .add(BIG_DECIMAL)
                    .add(UUID)
                    .add(JSON)
                    .build()
                    .collect(Collectors.toList())
            );

    public static final List<String> NAMES
            = Collections.unmodifiableList(ALL_TYPES
                    .stream()
                    .map(m -> m.getName())
                    .collect(Collectors.toList())
            );

    private final Class<T> type;

    protected ValueType(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public abstract String getName();

    public abstract String serialize(T value);

    public abstract T deserialize(String value);

    public static final class StringType extends ValueType<String> {

        public StringType() {
            super(String.class);
        }

        @Override
        public String getName() {
            return "STRING";
        }

        @Override
        public String serialize(String value) {
            return value;
        }

        @Override
        public String deserialize(String value) {
            return value;
        }
    }

    public static final class LongType extends ValueType<Long> {

        public LongType() {
            super(Long.class);
        }

        @Override
        public String getName() {
            return "LONG";
        }

        @Override
        public String serialize(Long value) {
            return value.toString();
        }

        @Override
        public Long deserialize(String value) {
            return Long.valueOf(value);
        }
    }

    public static final class DoubleType extends ValueType<Double> {

        public DoubleType() {
            super(Double.class);
        }

        @Override
        public String getName() {
            return "DOUBLE";
        }

        @Override
        public String serialize(Double value) {
            return value.toString();
        }

        @Override
        public Double deserialize(String value) {
            return Double.valueOf(value);
        }
    }

    public static final class BooleanType extends ValueType<Boolean> {

        public BooleanType() {
            super(Boolean.class);
        }

        @Override
        public String getName() {
            return "BOOLEAN";
        }

        @Override
        public String serialize(Boolean value) {
            return value.toString();
        }

        @Override
        public Boolean deserialize(String value) {
            return Boolean.valueOf(value);
        }
    }

    public static final class TimestampType extends ValueType<Instant> {

        public TimestampType() {
            super(Instant.class);
        }

        @Override
        public String getName() {
            return "TIMESTAMP";
        }

        @Override
        public String serialize(Instant value) {
            return value.toString();
        }

        @Override
        public Instant deserialize(String value) {
            return Instant.parse(value);
        }
    }

    public static final class BigIntegerType extends ValueType<BigInteger> {

        public BigIntegerType() {
            super(BigInteger.class);
        }

        @Override
        public String getName() {
            return "BIG_INTEGER";
        }

        @Override
        public String serialize(BigInteger value) {
            return value.toString();
        }

        @Override
        public BigInteger deserialize(String value) {
            return new BigInteger(value);
        }
    }

    public static final class BigDecimalType extends ValueType<BigDecimal> {

        public BigDecimalType() {
            super(BigDecimal.class);
        }

        @Override
        public String getName() {
            return "BIG_DECIMAL";
        }

        @Override
        public String serialize(BigDecimal value) {
            return value.toString();
        }

        @Override
        public BigDecimal deserialize(String value) {
            return new BigDecimal(value);
        }
    }

    public static final class UUIDType extends ValueType<UUID> {

        public UUIDType() {
            super(UUID.class);
        }

        @Override
        public String getName() {
            return "UUID";
        }

        @Override
        public String serialize(UUID value) {
            return value.toString();
        }

        @Override
        public UUID deserialize(String value) {
            return java.util.UUID.fromString(value);
        }
    }

    public static final class JsonType extends ValueType<String> {

        public JsonType() {
            super(String.class);
        }

        @Override
        public String getName() {
            return "JSON";
        }

        @Override
        public String serialize(String value) {
            return value;
        }

        @Override
        public String deserialize(String value) {
            return value;
        }
    }
}
