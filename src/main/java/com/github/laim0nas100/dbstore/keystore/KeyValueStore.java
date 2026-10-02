package com.github.laim0nas100.dbstore.keystore;

import com.github.laim0nas100.uncheckedutils.SafeOpt;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 * @author Lemmin
 */
public interface KeyValueStore {

    SafeOpt<String> getValue(String key);

    SafeOpt<ValueType> getType(String key);

    SafeOpt<TypedKeyValue> get(String key);

    <T> SafeOpt<TypedKeyValue<T>> get(String key, ValueType<T> type);

    <T> SafeOpt<TypedKeyValue<T>> put(TypedKeyValue<T> value);

    <T> SafeOpt<TypedKeyValue<T>> put(String key, ValueType<T> type, T value);

    <T> SafeOpt<TypedKeyValue<T>> computeIfAbsent(
            String key,
            ValueType<T> type,
            Function<String, T> functor);

    default SafeOpt<TypedKeyValue<String>> computeIfAbsent(
            String key,
            Function<String, String> functor) {
        return computeIfAbsent(key, ValueType.STRING, functor);
    }

    default SafeOpt<TypedKeyValue<String>> computeIfAbsentJson(
            String key,
            Function<String, String> functor) {
        return computeIfAbsent(key, ValueType.JSON, functor);
    }

    default SafeOpt<TypedKeyValue<Long>> computeIfAbsentLong(
            String key,
            Function<String, Long> functor) {
        return computeIfAbsent(key, ValueType.LONG, functor);
    }

    default SafeOpt<TypedKeyValue<Double>> computeIfAbsentDouble(
            String key,
            Function<String, Double> functor) {
        return computeIfAbsent(key, ValueType.DOUBLE, functor);
    }

    default SafeOpt<TypedKeyValue<Boolean>> computeIfAbsentBoolean(
            String key,
            Function<String, Boolean> functor) {
        return computeIfAbsent(key, ValueType.BOOLEAN, functor);
    }

    default SafeOpt<TypedKeyValue<Instant>> computeIfAbsentTimestamp(
            String key,
            Function<String, Instant> functor) {
        return computeIfAbsent(key, ValueType.TIMESTAMP, functor);
    }

    default SafeOpt<TypedKeyValue<BigInteger>> computeIfAbsentBigInteger(
            String key,
            Function<String, BigInteger> functor) {
        return computeIfAbsent(key, ValueType.BIG_INTEGER, functor);
    }

    default SafeOpt<TypedKeyValue<BigDecimal>> computeIfAbsentBigDecimal(
            String key,
            Function<String, BigDecimal> functor) {
        return computeIfAbsent(key, ValueType.BIG_DECIMAL, functor);
    }

    default SafeOpt<TypedKeyValue<UUID>> computeIfAbsentUUID(
            String key,
            Function<String, UUID> functor) {
        return computeIfAbsent(key, ValueType.UUID, functor);
    }

    default SafeOpt<TypedKeyValue<String>> put(
            String key,
            String value) {
        return put(key, ValueType.STRING, value);
    }

    default SafeOpt<TypedKeyValue<String>> putJson(
            String key,
            String value) {
        return put(key, ValueType.JSON, value);
    }

    default SafeOpt<TypedKeyValue<Long>> put(
            String key,
            Long value) {
        return put(key, ValueType.LONG, value);
    }

    default SafeOpt<TypedKeyValue<Double>> put(
            String key,
            Double value) {
        return put(key, ValueType.DOUBLE, value);
    }

    default SafeOpt<TypedKeyValue<Boolean>> put(
            String key,
            Boolean value) {
        return put(key, ValueType.BOOLEAN, value);
    }

    default SafeOpt<TypedKeyValue<Instant>> put(
            String key,
            Instant value) {
        return put(key, ValueType.TIMESTAMP, value);
    }

    default SafeOpt<TypedKeyValue<BigInteger>> put(
            String key,
            BigInteger value) {
        return put(key, ValueType.BIG_INTEGER, value);
    }

    default SafeOpt<TypedKeyValue<BigDecimal>> put(
            String key,
            BigDecimal value) {
        return put(key, ValueType.BIG_DECIMAL, value);
    }

    default SafeOpt<TypedKeyValue<UUID>> put(
            String key,
            UUID value) {
        return put(key, ValueType.UUID, value);
    }

    SafeOpt<TypedKeyValue> remove(String key);

    SafeOpt<Boolean> containsKey(String key);

    SafeOpt<Set<String>> getKeys();

    SafeOpt<List<TypedKeyValue>> getEntries();

    SafeOpt<Integer> size();
}
