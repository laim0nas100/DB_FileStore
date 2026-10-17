package com.github.laim0nas100.dbstore.idgen;

import com.github.laim0nas100.dbstore.JdbiMixin;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class MultiSqliteCounter implements JdbiMixin {

    protected final ConcurrentHashMap<String, IdGenerator> generators = new ConcurrentHashMap<>();

    protected final String counterTableName;

    protected final String counterIdCol;
    protected final String counterValCol;

    protected final Jdbi jdbi;
    
    protected int increment = 100;

    public MultiSqliteCounter(Jdbi jdbi) throws SQLException {
        this(jdbi, SqliteCacheCounter.DEFAULT_TABLE_NAME, SqliteCacheCounter.DEFAULT_TABLE_COL_1, SqliteCacheCounter.DEFAULT_TABLE_COL_2);
    }

    public MultiSqliteCounter(Jdbi jdbi, String counterTableName, String counterIdCol, String counterValCol) throws SQLException {
        this.jdbi = Objects.requireNonNull(jdbi);
        this.counterTableName = Objects.requireNonNull(counterTableName);
        this.counterIdCol = Objects.requireNonNull(counterIdCol);
        this.counterValCol = Objects.requireNonNull(counterValCol);
        initialize();
    }

    protected void initialize() throws SQLException {

        safeHandle(h -> {
            if (!tableExists(h.getConnection(), counterTableName)) {
                h.execute(getCreateTableString());
            }
            return 0;
        }).throwIfError(SQLException.class).throwAny();

    }

    protected String getCreateTableString() {
        return formatted("CREATE TABLE %s(\n", counterTableName)
                + formatted("   %s TEXT NOT NULL,\n", counterIdCol)
                + formatted("   %s INTEGER NOT NULL,\n \n", counterValCol)
                + formatted("   PRIMARY KEY (%s)", counterIdCol)
                + ");";
    }

    public IdGenerator getFor(String id) {
        return generators.computeIfAbsent(id, this::createGenerator);
    }

    protected IdGenerator createGenerator(String id) {
        return new SqliteCacheCounter(getJdbi(), counterTableName, counterIdCol, counterValCol, id, increment);
    }

    @Override
    public Jdbi getJdbi() {
        return jdbi;
    }

}
