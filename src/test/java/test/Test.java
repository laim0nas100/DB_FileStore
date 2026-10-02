
package test;

import com.github.laim0nas100.commonslb.Java;
import com.github.laim0nas100.commonslb.io.autopath.AutoPath;
import com.github.laim0nas100.dbstore.filestore.ResourceFull;
import com.github.laim0nas100.dbstore.filestore.SqliteFileStore;
import com.github.laim0nas100.dbstore.keystore.SqliteKeyValueStore;
import com.github.laim0nas100.dbstore.keystore.TypedKeyValue;
import com.github.laim0nas100.uncheckedutils.SafeOpt;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class Test {

    public static final AutoPath WORK_DIR = AutoPath.fs(Java.getUserHome(), "lb-soft", "FileDBTest");
    public static final AutoPath DB_FILE = WORK_DIR.concat("FileDatabase.db");
    public static Jdbi jdbi;
    
    public static String readerDump(Reader reader){
        char[] buffer = new char[1024];
        StringBuilder sb = new StringBuilder();
        while(true){
            try {
                int read = reader.read(buffer);
                if(read<=0){
                    reader.close();
                    return sb.toString();
                }
                sb.append(buffer, 0, read);
            } catch (IOException ex) {
                try{
                    reader.close();
                }catch(IOException another){
                    
                }
                return sb.toString();
            }
        }
    }

    public static void main(String[] args) throws Exception {

        /*
        # ──────────────────────────────────────────────────────────────
        # HikariCP + SQLite configuration
        # ──────────────────────────────────────────────────────────────
        # Basic connection settings
        jdbc.driverClassName     = org.sqlite.JDBC
        # %s will be replaced with full path
        jdbc.url.template        = jdbc:sqlite:%s
        # HikariCP pool settings 
        hikaricp.poolName           = Pool
        # SQLite can handle ~10-20 concurrent well
        hikaricp.maximumPoolSize    = 4
        hikaricp.minimumIdle        = 1
        # 5 minutes
        hikaricp.idleTimeout        = 300000
        # 30 minutes
        hikaricp.maxLifetime        = 1800000
        # 30 seconds
        hikaricp.connectionTimeout  = 30000
        # SQLite performance / safety tunings (applied via init SQL)
        hikaricp.connectionInitSql  = PRAGMA journal_mode=WAL; PRAGMA synchronous=NORMAL; PRAGMA busy_timeout=5000;
        # Optional - very useful for debugging log if connection is held > 60s
        hikaricp.leakDetectionThreshold = 60000
         */
        Files.createDirectories(DB_FILE.toPath().getParent());
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(org.sqlite.JDBC.class.getName());
        config.setJdbcUrl(String.format("jdbc:sqlite:%s", DB_FILE.getStringPath()));
        config.setMaximumPoolSize(4);
        config.setConnectionInitSql("PRAGMA foreign_keys = ON");

        HikariDataSource ds = new HikariDataSource(config);
        jdbi = Jdbi.create(ds);

        SqliteFileStore fs
                = new SqliteFileStore(jdbi);
        
        SqliteKeyValueStore kvStore = new SqliteKeyValueStore(jdbi);

        ResourceFull r = new ResourceFull();
        r.setId(1);
        r.setUri("text/1.txt");
        r.setName("1.txt");
        r.setDescription("test file");
        r.setMimeType("text");
        r.setContentAndSize("Hello".getBytes());

        SafeOpt put = fs.put(r);

        fs.find(r.getUri()).ifPresent(file -> {
            System.out.println("Found file:" + file.getName());
            fs.openString(file.getUri()).map(Test::readerDump).ifPresent(string -> {
                System.out.println(string);
            });
        });
        
        SafeOpt<TypedKeyValue<Double>> doubleVal = kvStore.put("my-double", 1337.37);
        
        doubleVal.ifPresent(dv ->{
            SafeOpt<TypedKeyValue> got = kvStore.get(dv.getKey());
            
            if(dv.equals(got.get())){
                System.out.println("Got same value!");
            }
        });
        
        fs.search(r).ifPresent(list->{
            System.out.println("Found list of size:"+list.size());
        });
        
    }
}
