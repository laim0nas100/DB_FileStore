package com.github.laim0nas100.dbstore;

import com.github.laim0nas100.uncheckedutils.SafeOpt;
import com.github.laim0nas100.uncheckedutils.func.UncheckedFunction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public interface JdbiMixin {

    public Jdbi getJdbi();

    public default String formatted(String query, Object... args) {
        return String.format(query, args);
    }

    public default boolean tableExists(Connection connection, String tableName) throws SQLException {

        try (ResultSet rs = connection.getMetaData()
                .getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }
    
    public default <R, X extends Exception> SafeOpt<R> safeHandle(UncheckedFunction<Handle, ? extends R> functor, Object... nonNulls){
        Objects.requireNonNull(functor);
        for(Object o:nonNulls){
            if(o == null){
                return SafeOpt.empty();
            }
        }
        return SafeOpt.of(getJdbi()).map( jd -> jd.withHandle(functor::apply));
    }
    
    public default <R, X extends Exception> SafeOpt<R> safeTransaction(UncheckedFunction<Handle, ? extends R> functor, Object... nonNulls){
        Objects.requireNonNull(functor);
        for(Object o:nonNulls){
            if(o == null){
                return SafeOpt.empty();
            }
        }
        return SafeOpt.of(getJdbi()).map( jd -> jd.inTransaction(functor::apply));
    }
}
