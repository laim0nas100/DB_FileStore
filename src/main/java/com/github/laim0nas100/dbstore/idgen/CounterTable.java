package com.github.laim0nas100.dbstore.idgen;

import com.github.laim0nas100.dbstore.TableDefinition;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import java.util.Objects;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class CounterTable implements IdGenerator {

    protected final Jdbi jdbi;
    protected final TableDefinition def;
    protected final String counterID;

    protected final long startingCounter;
    protected final long increment;

    public CounterTable(Jdbi jdbi, String counterID, long startingCounter, long increment, TableDefinition definition) {
        this.jdbi = Objects.requireNonNull(jdbi);
        this.counterID = Objects.requireNonNull(counterID);
        this.startingCounter = startingCounter;
        this.increment = increment;
        this.def = definition.assertHasColumns(2).assertColumnTypes(String.class, Long.class);
    }

    @Override
    public SafeOpt<Long> getAndIncrement() {
        return safeTransaction(handle -> {
            Long current = handle.createQuery("SELECT " + def.colName(1) + " FROM " + def.tableName + " WHERE " + def.colName(0) + "= :counterID")
                    .bind("counterID", counterID)
                    .mapTo(Long.class)
                    .findOne()
                    .orElse(null);

            if (current == null) { // update
                handle.createUpdate("INSERT INTO " + def.tableName + def.formattedColumns() + " VALUES (:counterID, :counter)")
                        .bind("counterID", counterID)
                        .bind("counter", startingCounter + increment)
                        .execute();
                return startingCounter;
            } else {
                long next = current + increment;
                handle.createUpdate("UPDATE " + def.tableName + " SET " + def.colName(1) + " = :counter" + "WHERE " + def.colName(0) + " = :counterID")
                        .bind("counterID", counterID)
                        .bind("counter", next)
                        .execute();

                return current;
            }

        });
    }

    @Override
    public Jdbi getJdbi() {
        return jdbi;
    }

    @Override
    public SafeOpt getCurrent() {
        return safeHandle(handle -> {
            return handle.createQuery("SELECT " + def.colName(1) + " FROM " + def.tableName + " WHERE " + def.colName(0) + "= :counterID")
                    .bind("counterID", counterID)
                    .mapTo(Long.class)
                    .findOne()
                    .orElse(null);
        });
    }

}
