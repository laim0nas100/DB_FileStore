package com.github.laim0nas100.dbstore.keystore;

import java.sql.SQLException;
import java.util.List;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class SqliteKeyValueStore extends JdbiKeyValueStore {

    public SqliteKeyValueStore(Jdbi jdbi) throws SQLException {
        super(jdbi);
        initialize();
    }

    public SqliteKeyValueStore(Jdbi jdbi, String tableName) throws SQLException {
        super(jdbi, tableName);
        initialize();
    }

    public SqliteKeyValueStore(Jdbi jdbi, String tableName, List<ValueType> types) throws SQLException {
        super(jdbi, tableName, types);
        initialize();
    }

    public void initialize() throws SQLException {
        String createTable
                = formatted("CREATE TABLE %s(\n", tableName)
                + "    key     TEXT NOT NULL,\n"
                + "    type    TEXT NOT NULL,\n"
                + "    value   TEXT,\n"
                + "\n"
                + "    PRIMARY KEY (key)\n"
                + ");";

        jdbi.withHandle(h -> {
            if (!tableExists(h.getConnection(), tableName)) {
                h.execute(createTable);
            }

            return 0;
        });
    }

}
