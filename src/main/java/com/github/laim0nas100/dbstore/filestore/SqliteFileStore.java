package com.github.laim0nas100.dbstore.filestore;

import com.github.laim0nas100.uncheckedutils.SafeOpt;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author Lemmin
 */
public class SqliteFileStore extends JdbiFileStoreDB {

    public SqliteFileStore(Jdbi jdbi) throws Exception {
        super(jdbi);
        initializeDB();
    }

    public SqliteFileStore(Jdbi jdbi, String metaName, String blobName) throws Exception {
        super(jdbi, metaName, blobName);
        initializeDB();
    }

    @Override
    public void initializeDB() throws Exception {

        String createMetaTable
                = formatted("CREATE TABLE %s(\n", metaName)
                + "    id                  TEXT NOT NULL,\n"
                + "    uri                 TEXT NOT NULL,\n"
                + "    name                TEXT,\n"
                + "    description         TEXT,\n"
                + "    mime_type           TEXT,\n"
                + "    additional_info     TEXT,\n"
                + "    size                INTEGER NOT NULL,\n"
                + "\n"
                + "    PRIMARY KEY (id),\n"
                + "    UNIQUE (uri)\n"
                + ");\n";

        String createBlobTable
                = formatted("CREATE TABLE %s(\n", blobName)
                + "    id                  TEXT NOT NULL,\n"
                + "    content             BLOB NOT NULL,\n"
                + "\n"
                + "    PRIMARY KEY (id),\n"
                + formatted("    FOREIGN KEY (id) REFERENCES %s(id) ON DELETE CASCADE\n", metaName)
                + ");\n";

        jdbi.withHandle(h -> {
            if (!tableExists(h.getConnection(), metaName)) {
                h.execute(createMetaTable);
            }

            if (!tableExists(h.getConnection(), blobName)) {
                h.execute(createBlobTable);
            }
            return 0;
        });

    }

    @Override
    public SafeOpt delete(String uri) {
        return SafeOpt.ofNullable(uri).map(u -> jdbi
                .withHandle(handle -> handle.createQuery(formatted("DELETE FROM %s WHERE uri = :uri RETURNING id", metaName))
                        .bind("uri", u)
                        .mapTo(Object.class)
                        .findOne()
                        .orElse(null)
                )
        );
    }

}
