package com.github.laim0nas100.dbstore;

/**
 *
 * @author laim0nas100
 */
public class TableColumnDefinition {

    public String columnName;
    public String sqlType;
    public Class javaType;

    public TableColumnDefinition(String columnName, Class javaType, String sqlType) {
        this.columnName = columnName;
        this.javaType = javaType;
        this.sqlType = sqlType;
    }

    public static TableColumnDefinition ofString(String columnName) {
        return new TableColumnDefinition(columnName, String.class, "TEXT");
    }

    public static TableColumnDefinition ofLong(String columnName) {
        return new TableColumnDefinition(columnName, Long.class, "LONG");
    }

}
