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

    protected final Jdbi jdbi;

    public MultiSqliteCounter(Jdbi jdbi) throws SQLException {
        this(jdbi, SqliteCacheCounter.DEFAULT_TABLE_NAME);
    }

    public MultiSqliteCounter(Jdbi jdbi, String counterTableName) throws SQLException {
        this.jdbi = Objects.requireNonNull(jdbi);
        this.counterTableName = Objects.requireNonNull(counterTableName);
        initialize();
    }

    public void initialize() throws SQLException {

        safeHandle(h -> {
            if (!tableExists(h.getConnection(), counterTableName)) {
                h.execute(getCreateTableString());
            }
            return 0;
        }).throwIfError(SQLException.class).throwAny();

    }

    public String getCreateTableString() {
        return formatted("CREATE TABLE %s(\n", counterTableName)
                + "   tableID TEXT NOT NULL,\n"
                + "   counter INTEGER NOT NULL,\n \n"
                + "   PRIMARY KEY (tableID)"
                + ");";
    }

    public IdGenerator getFor(String id) {
        return generators.computeIfAbsent(id, this::createGenerator);
    }

    protected IdGenerator createGenerator(String id) {
        return new SqliteCacheCounter(getJdbi(), counterTableName, SqliteCacheCounter.DEFAULT_TABLE_COL_1, SqliteCacheCounter.DEFAULT_TABLE_COL_2, id, 100L);
    }

    @Override
    public Jdbi getJdbi() {
        return jdbi;
    }

}
