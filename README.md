[![](https://jitpack.io/v/laim0nas100/DB_FileStore.svg)](https://jitpack.io/#laim0nas100/DB_FileStore)

# DB_FileStore

A small Java persistence library for MCP-like resources and simple key-value data.

## Features

* Persistent key-value storage with extendable typed values
* Resource metadata and binary content storage
* Simple JDBI-based API
* Java 8 compatible

## KeyValueStore

```java
KeyValueStore store = ...;

store.put("counter", 42L);
store.put("enabled", true);
store.put("name", "example");
store.putJson("config", "{\"enabled\":true}");

long counter = store.get("counter", ValueType.LONG)
        .map(TypedKeyValue::getValue)
        .orElse(0L);

store.computeIfAbsentLong(
        "requests",
        key -> 0L
);
```

Typed values include `String`, `Long`, `Double`, `Boolean`, `Instant`, `BigInteger`, `BigDecimal`, `UUID`, and JSON strings.

## FileStore

Store resource metadata together with binary content:

```java
FileStore store = ...;

store.put(
    metadata,
    inputStream
);

InputStream content = store.get(uri);

store.delete(uri);
```

Resources have metadata such as URI, name, description, MIME type, additional information, and size, with their binary content stored separately.

