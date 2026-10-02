/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.laim0nas100.dbstore.keystore;

/**
 *
 * @author Lemmin
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
}
