package com.github.laim0nas100.dbstore.keystore;

import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public final class TypedKeyValue<T> {

    private final String key;
    private final ValueType<T> type;
    private final T value;

    public TypedKeyValue(String key, ValueType<T> type, T value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public ValueType<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.key);
        hash = 37 * hash + Objects.hashCode(this.type);
        hash = 37 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TypedKeyValue<?> other = (TypedKeyValue<?>) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }
    
    
}
