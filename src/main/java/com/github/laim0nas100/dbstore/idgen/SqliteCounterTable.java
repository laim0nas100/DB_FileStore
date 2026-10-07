package com.github.laim0nas100.dbstore.idgen;

import com.github.laim0nas100.dbstore.TableDefinition;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class SqliteCounterTable extends CounterTable {

    public SqliteCounterTable(Jdbi jdbi, String counterID, long startingCounter, long increment, TableDefinition definition) {
        super(jdbi, counterID, startingCounter, increment, definition);
    }

    @Override
    public SafeOpt<Long> getAndIncrement() {
        return safeTransaction(handle -> {

            return handle.createQuery(
                    "INSERT INTO " + def.tableName
                    + def.formattedColumns()
                    + " VALUES (:counterID, :counter)"
                    + " ON CONFLICT (" + def.colName(0) + ")"
                    + " DO UPDATE SET "
                    + def.colName(1) + " = "
                    + def.colName(1) + " + :increment"
                    + " RETURNING "
                    + def.colName(1) + " - :increment")
                    .bind("counterID", counterID)
                    .bind("counter", startingCounter + increment)
                    .bind("increment", increment)
                    .mapTo(Long.class)
                    .one();
        });
    }

}
