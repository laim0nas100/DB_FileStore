package com.github.laim0nas100.dbstore.keystore;

import com.github.laim0nas100.dbstore.JdbiMixin;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import org.jdbi.v3.core.Jdbi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public abstract class JdbiKeyValueStore implements KeyValueStore, JdbiMixin {

    protected final Jdbi jdbi;
    protected final String tableName;
    protected final Map<String, ValueType> types;

    public JdbiKeyValueStore(Jdbi jdbi) {
        this(jdbi, "KeyValueStore");
    }

    public JdbiKeyValueStore(Jdbi jdbi, String tableName) {
        this(jdbi, tableName, ValueType.ALL_TYPES);
    }

    public JdbiKeyValueStore(Jdbi jdbi, String tableName, List<ValueType> types) {
        this.jdbi = Objects.requireNonNull(jdbi);
        this.tableName = Objects.requireNonNull(tableName);

        Map<String, ValueType> map = new HashMap<>();

        for (ValueType type : types) {
            ValueType previous = map.put(
                    type.getName(),
                    type
            );

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate ValueType name: "
                        + type.getName()
                );
            }
        }

        this.types = Collections.unmodifiableMap(map);
    }

    @Override
    public SafeOpt<String> getValue(String key) {
        return SafeOpt.ofNullable(key).map(k -> {
            return jdbi.withHandle(handle
                    -> handle.createQuery(
                            formatted(
                                    "SELECT value FROM %s "
                                    + "WHERE key = :key",
                                    tableName))
                            .bind("key", k)
                            .mapTo(String.class)
                            .findOne()
                            .orElse(null)
            );
        });
    }

    @Override
    public SafeOpt<ValueType> getType(String key) {
        return SafeOpt.ofNullable(key).map(k -> {
            return jdbi.withHandle(handle
                    -> handle.createQuery(
                            formatted(
                                    "SELECT type FROM %s "
                                    + "WHERE key = :key",
                                    tableName))
                            .bind("key", key)
                            .mapTo(String.class)
                            .findOne()
                            .orElse(null)
            );
        }).map(this::getValueType);
    }

    @Override
    public SafeOpt<TypedKeyValue> get(String key) {
        return SafeOpt.ofNullable(key).map(k -> {
            return jdbi.withHandle(handle
                    -> handle.createQuery(
                            formatted(
                                    "SELECT key, type, value "
                                    + "FROM %s "
                                    + "WHERE key = :key",
                                    tableName))
                            .bind("key", k)
                            .map((rs, ctx) -> {
                                String typeName = rs.getString("type");

                                return createTypedValue(
                                        rs.getString("key"),
                                        typeName,
                                        rs.getString("value")
                                );
                            })
                            .findOne()
                            .orElse(null)
            );
        });
    }

    @Override
    public <T> SafeOpt<TypedKeyValue<T>> get(
            String key,
            ValueType<T> type) {

        return safeHandle(handle -> {
            return handle.createQuery(
                    "SELECT key, type, value "
                    + formatted("FROM %s ", tableName)
                    + "WHERE key = :key")
                    .bind("key", key)
                    .map((rs, ctx) -> {
                        String storedType = rs.getString("type");

                        if (!type.getName().equals(storedType)) {
                            throw new IllegalStateException(
                                    "Value for key '" + key
                                    + "' has type " + storedType
                                    + ", requested "
                                    + type.getName()
                            );
                        }

                        return new TypedKeyValue<>(
                                rs.getString("key"),
                                type,
                                type.deserialize(
                                        rs.getString("value"))
                        );
                    })
                    .findOne()
                    .orElse(null);
        }, key, type);

    }

    @Override
    public <T> SafeOpt<TypedKeyValue<T>> put(
            TypedKeyValue<T> value) {

        return put(
                value.getKey(),
                value.getType(),
                value.getValue()
        );
    }

    @Override
    public <T> SafeOpt<TypedKeyValue<T>> put(
            String key,
            ValueType<T> type,
            T value) {
        return safeHandle(handle -> {
            String serialized = type.serialize(value);
            handle.createUpdate(
                    formatted("INSERT INTO %s ", tableName)
                    + "(key, type, value) "
                    + "VALUES (:key, :type, :value) "
                    + "ON CONFLICT(key) DO UPDATE SET "
                    + "type = excluded.type, "
                    + "value = excluded.value")
                    .bind("key", key)
                    .bind("type", type.getName())
                    .bind("value", serialized)
                    .execute();

            return new TypedKeyValue<>(
                    key,
                    type,
                    value
            );
        }, key, type, value);

    }

    @Override
    public <T> SafeOpt<TypedKeyValue<T>> computeIfAbsent(
            String key,
            ValueType<T> type,
            Function<String, T> functor) {

        return safeTransaction(handle -> {
            TypedKeyValue<T> existing = handle.createQuery(
                    "SELECT key, type, value "
                    + formatted("FROM %s ", tableName)
                    + "WHERE key = :key")
                    .bind("key", key)
                    .map((rs, ctx) -> {
                        String storedType = rs.getString("type");

                        if (!type.getName().equals(storedType)) {
                            throw new IllegalStateException(
                                    "Value for key '" + key
                                    + "' has type " + storedType
                                    + ", requested " + type.getName()
                            );
                        }

                        return new TypedKeyValue<>(
                                rs.getString("key"),
                                type,
                                type.deserialize(
                                        rs.getString("value"))
                        );
                    })
                    .findOne()
                    .orElse(null);

            if (existing != null) {
                return existing;
            }

            T value = functor.apply(key);

            if (value == null) {
                return null;
            }

            handle.createUpdate(
                    "INSERT INTO %s "
                    + formatted("(key, type, value) ", tableName)
                    + "VALUES (:key, :type, :value)")
                    .bind("key", key)
                    .bind("type", type.getName())
                    .bind("value", type.serialize(value))
                    .execute();

            return new TypedKeyValue<>(
                    key,
                    type,
                    value
            );
        }, key, type, functor);

    }

    @Override
    public SafeOpt<TypedKeyValue> remove(String key) {
        return safeTransaction(handle -> {
            TypedKeyValue<?> existing = handle.createQuery(
                    "SELECT key, type, value "
                    + formatted("FROM %s ", tableName)
                    + "WHERE key = :key")
                    .bind("key", key)
                    .map((rs, ctx)
                            -> createTypedValue(
                                    rs.getString("key"),
                                    rs.getString("type"),
                                    rs.getString("value")
                            ))
                    .findOne()
                    .orElse(null);

            if (existing == null) {
                return null;
            }

            handle.createUpdate(
                    formatted("DELETE FROM %s ", tableName)
                    + "WHERE key = :key")
                    .bind("key", key)
                    .execute();

            return existing;
        }, key);

    }

    @Override
    public SafeOpt<Boolean> containsKey(String key) {
        return safeHandle(handle -> {
            return handle.createQuery(
                    formatted("SELECT 1 FROM %s ", tableName)
                    + "WHERE key = :key")
                    .bind("key", key)
                    .mapTo(Integer.class)
                    .findOne()
                    .isPresent();
        }, key);

    }

    @Override
    public SafeOpt<Set<String>> getKeys() {
        return safeHandle(handle -> {
            return new LinkedHashSet<>(
                    handle.createQuery(
                            formatted(
                                    "SELECT key FROM %s "
                                    + "ORDER BY key",
                                    tableName))
                            .mapTo(String.class)
                            .list());
        });
    }

    @Override
    public SafeOpt<List<TypedKeyValue>> getEntries() {
        return safeHandle(handle -> {
            return handle.createQuery(
                    "SELECT key, type, value "
                    + formatted("FROM %s ", tableName)
                    + "ORDER BY key")
                    .map((rs, ctx)
                            -> createTypedValue(
                                    rs.getString("key"),
                                    rs.getString("type"),
                                    rs.getString("value")
                            ))
                    .list();
        });
    }

    @Override
    public SafeOpt<Integer> size() {
        return safeHandle(handle -> {
            return handle.createQuery(
                    formatted(
                            "SELECT COUNT(*) FROM %s",
                            tableName))
                    .mapTo(Integer.class)
                    .one();
        });
    }

    protected TypedKeyValue createTypedValue(
            String key,
            String typeName,
            String value) {

        ValueType type = getValueType(typeName);

        return createTypedValue(key, type, value);
    }

    protected <T> TypedKeyValue<T> createTypedValue(
            String key,
            ValueType<T> type,
            String value) {

        return new TypedKeyValue<>(
                key,
                type,
                type.deserialize(value)
        );
    }

    protected ValueType getValueType(String name) {
        ValueType type = types.get(name);

        if (type == null) {
            throw new IllegalStateException(
                    "Unknown ValueType: " + name
            );
        }

        return type;
    }

    @Override
    public Jdbi getJdbi() {
        return jdbi;
    }
}
