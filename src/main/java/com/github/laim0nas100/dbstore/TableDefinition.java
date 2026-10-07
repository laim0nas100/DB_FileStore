package com.github.laim0nas100.dbstore;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author laim0nas100
 */
public class TableDefinition {

    public final String tableName;

    public final List<TableColumnDefinition> columns;

    public final Map<String, TableColumnDefinition> columnMap;

    public TableDefinition(String tableName, List<TableColumnDefinition> columns) {
        this.tableName = Objects.requireNonNull(tableName);
        Objects.requireNonNull(columns);
        LinkedHashMap<String, TableColumnDefinition> map = new LinkedHashMap();
        for (int i = 0; i < columns.size(); i++) {
            TableColumnDefinition col = columns.get(i);
            if (col == null) {
                throw new IllegalArgumentException("Null column at index:" + i);
            }
            if (map.containsKey(col.columnName)) {
                throw new IllegalArgumentException("Duplicate column name:" + col.columnName);
            }
            map.put(col.columnName, col);
        }
        this.columns = Collections.unmodifiableList(columns);
        this.columnMap = Collections.unmodifiableMap(map);
    }

    public TableColumnDefinition getCol(int index) {
        return columns.get(index);
    }
    
    public TableColumnDefinition getCol(String colName){
        return columnMap.get(colName);
    }

    public String colName(int index) {
        return columns.get(index).columnName;
    }

    public Class colType(int index) {
        return columns.get(index).javaType;
    }

    public String sqlType(int index) {
        return columns.get(index).sqlType;
    }
    
    public String colName(String name) {
        return columnMap.get(name).columnName;
    }

    public Class colType(String name) {
        return columnMap.get(name).javaType;
    }

    public String sqlType(String name) {
        return columnMap.get(name).sqlType;
    }

    public TableDefinition assertHasColumns(int count) {
        if (columns.size() != count) {
            throw new IllegalArgumentException(tableName + " should have " + count + " columns");
        }
        return this;
    }

    public TableDefinition assertColumn(int index, Consumer<TableColumnDefinition> consumer) {
        if (columns.size() <= index) {
            throw new IllegalArgumentException(tableName + " doesn't have the:" + index + "column");
        }

        TableColumnDefinition column = columns.get(index);
        Objects.requireNonNull(column);
        Objects.requireNonNull(consumer).accept(column);
        return this;

    }

    public TableDefinition assertColumnType(int index, Class type) {
        Objects.requireNonNull(type);
        return assertColumn(index, col -> {
            if (!col.javaType.equals(type)) {
                throw new IllegalArgumentException(col.columnName + " must be of type:" + type.getName());
            }
        });
    }

    public TableDefinition assertColumnTypes(Class... types) {
        for (int i = 0; i < types.length; i++) {
            assertColumnType(i, types[i]);
        }
        return this;
    }

    public String formattedSelect(int... columns) {
        return "SELECT " + formattedColumns(columns);
    }

    public String formattedSelectFrom(int... columns) {
        return "SELECT " + formattedColumns(columns) + " FROM " + tableName;
    }
    
     public String formattedSelectStr(String... columns) {
        return "SELECT " + formattedColumnsStr(columns);
    }

    public String formattedSelectStrFrom(String... columns) {
        return "SELECT " + formattedColumnsStr(columns) + " FROM " + tableName;
    }

    public String formattedColumns(int... columns) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        boolean first = true;
        for (int index : columns) {
            TableColumnDefinition col = getCol(index);
            if (first) {
                first = false;
                sb.append(col.columnName);
            } else {
                sb.append(", ").append(col.columnName);
            }
        }
        sb.append(") ");
        return sb.toString();
    }
    
    public String formattedColumnsStr(String... columns) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        boolean first = true;
        for (String name : columns) {
            TableColumnDefinition col = getCol(name);
            if (first) {
                first = false;
                sb.append(col.columnName);
            } else {
                sb.append(", ").append(col.columnName);
            }
        }
        sb.append(") ");
        return sb.toString();
    }

}
