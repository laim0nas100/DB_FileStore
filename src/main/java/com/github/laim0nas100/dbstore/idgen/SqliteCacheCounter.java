package com.github.laim0nas100.dbstore.idgen;

import com.github.laim0nas100.dbstore.TableColumnDefinition;
import com.github.laim0nas100.dbstore.TableDefinition;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import java.util.Arrays;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class SqliteCacheCounter implements IdGenerator {

    protected SqliteCounterTable counter;
    public static final long DEFAULT_CACHED = 100L;
    public static final String DEFAULT_TABLE_NAME = "Counters";
    public static final String DEFAULT_TABLE_COL_1 = "CounterID";
    public static final String DEFAULT_TABLE_COL_2 = "CounterValue";

    public SqliteCacheCounter(Jdbi jdbi, String counterId) {
        this(jdbi, counterId, DEFAULT_CACHED);
    }

    public SqliteCacheCounter(Jdbi jdbi, String counterId, long cache) {
        this(jdbi, DEFAULT_TABLE_NAME, DEFAULT_TABLE_COL_1, DEFAULT_TABLE_COL_2, counterId, cache);
    }

    public SqliteCacheCounter(Jdbi jdbi, String tableName, String counterIdColName, String counterValColName, String counterId, long increment) {
        counter = new SqliteCounterTable(jdbi, counterId, 1L, increment,
                new TableDefinition(tableName,
                        Arrays.asList(
                                TableColumnDefinition.ofString(counterIdColName),
                                TableColumnDefinition.ofLong(counterValColName)
                        )
                )
        );
    }

    @Override
    public SafeOpt getAndIncrement() {
        return counter.getAndIncrement();
    }

    @Override
    public SafeOpt getCurrent() {
        return counter.getCurrent();
    }

    @Override
    public Jdbi getJdbi() {
        return counter.getJdbi();
    }

}
