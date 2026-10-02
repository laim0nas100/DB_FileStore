package com.github.laim0nas100.dbstore.filestore;

import com.github.laim0nas100.dbstore.JdbiMixin;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public abstract class JdbiFileStoreDB implements FileStoreDB, JdbiMixin {

    protected final Jdbi jdbi;
    protected final String metaName;
    protected final String blobName;

    public JdbiFileStoreDB(Jdbi jdbi) {
        this(jdbi, "FileStore", "FileBlobStore");
    }

    public JdbiFileStoreDB(Jdbi jdbi, String metaName, String blobName) {
        this.jdbi = jdbi;
        this.metaName = Objects.requireNonNull(metaName);
        this.blobName = Objects.requireNonNull(blobName);
    }

    @Override
    public SafeOpt<ResourceMetadata> find(String uri) {

        return safeHandle(handle -> {
            return handle.createQuery(formatted("SELECT id,uri,name,description,mime_type,additional_info,size FROM %s WHERE uri = :uri", metaName))
                    .bind("uri", uri)
                    .map((rs, ctx) -> new ResourceMetadata(
                            rs.getObject("id"),
                            rs.getString("uri"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("mime_type"),
                            rs.getString("additional_info"),
                            rs.getLong("size")
                    ))
                    .findOne();
        }, uri).flatMapOpt(f -> f);

    }

    @Override
    public SafeOpt<InputStream> open(String uri) {

        return SafeOpt.ofNullable(uri).map(m -> {
            Handle handle = jdbi.open();

            try {
                PreparedStatement statement = handle.getConnection().prepareStatement(
                        formatted("SELECT b.content FROM %s m JOIN %s b ON b.id = m.id WHERE m.uri = ?", metaName, blobName)
                );

                statement.setString(1, uri);

                ResultSet rs = statement.executeQuery();

                if (!rs.next()) {
                    rs.close();
                    statement.close();
                    handle.close();
                    throw new IOException("Resource not found:" + uri);
                }

                InputStream stream = rs.getBinaryStream("content");

                return new JdbcInputStream(
                        stream,
                        rs,
                        statement,
                        handle
                );

            } catch (Throwable e) {
                handle.close();
                throw e;
            }
        });

    }

    @Override
    public SafeOpt put(ResourceMetadata meta, InputStream content) {
        return safeHandle(handle -> {
            try (PreparedStatement ps = handle.getConnection()
                    .prepareStatement(
                            formatted("INSERT INTO %s  (id, uri, name, description, mime_type, additional_info, size)", metaName)
                            + "\n VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                int i = 1;
                ps.setObject(i++, meta.getId());
                ps.setString(i++, meta.getUri());
                ps.setString(i++, meta.getName());
                ps.setString(i++, meta.getDescription());
                ps.setString(i++, meta.getMimeType());
                ps.setString(i++, meta.getAdditional_info());
                ps.setLong(i++, meta.getSize());

                ps.executeUpdate();
            }

            try (PreparedStatement blobPs = handle.getConnection()
                    .prepareStatement(formatted("INSERT INTO %s (id, content)  VALUES (?, ?)", blobName))) {

                blobPs.setObject(1, meta.getId());
                blobPs.setBinaryStream(2, content, meta.getSize());

                blobPs.executeUpdate();
            }
            return meta.getId();
        }, meta, content);
    }

    @Override
    public SafeOpt delete(String uri) {
        return safeHandle(handle -> {
            Object id = handle.createQuery(formatted("SELECT id FROM %s WHERE uri = :uri", metaName))
                    .bind("uri", uri)
                    .mapTo(Object.class)
                    .findOne()
                    .orElse(null);

            if (id == null) {
                return null;
            }

            handle.createUpdate(formatted("DELETE FROM %s WHERE id = :id", metaName))
                    .bind("uri", uri)
                    .execute();

            return id;
        }, uri);
    }

    @Override
    public Jdbi getJdbi() {
        return jdbi;
    }

    static class JdbcInputStream extends FilterInputStream {

        private final ResultSet resultSet;
        private final java.sql.Statement statement;
        private final Handle handle;

        JdbcInputStream(
                InputStream delegate,
                ResultSet resultSet,
                java.sql.Statement statement,
                Handle handle) {

            super(delegate);
            this.resultSet = resultSet;
            this.statement = statement;
            this.handle = handle;
        }

        @Override
        public void close() throws IOException {
            IOException failure = null;

            try {
                super.close();
            } catch (IOException e) {
                failure = e;
            }

            try {
                resultSet.close();
            } catch (Exception e) {
                failure = addFailure(failure, e);
            }

            try {
                statement.close();
            } catch (Exception e) {
                failure = addFailure(failure, e);
            }

            try {
                handle.close();
            } catch (Exception e) {
                failure = addFailure(failure, e);
            }

            if (failure != null) {
                throw failure;
            }
        }

        private static IOException addFailure(
                IOException existing,
                Exception e) {

            if (existing == null) {
                return new IOException(e);
            }

            existing.addSuppressed(e);
            return existing;
        }
    }

}
