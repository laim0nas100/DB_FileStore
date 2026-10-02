package com.github.laim0nas100.dbstore.filestore;

import com.github.laim0nas100.uncheckedutils.SafeOpt;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *
 * @author laim0nas100
 */
public interface FileStoreDB {

    public void initializeDB() throws Exception;

    SafeOpt<ResourceMetadata> find(String uri);

    SafeOpt<InputStream> open(String uri);

    default SafeOpt<InputStreamReader> openString(String uri) {
        return open(uri).map(stream -> new InputStreamReader(stream, Charset.defaultCharset()));
    }
    
    default SafeOpt<InputStreamReader> openString(String uri, Charset charset) {
        return open(uri).map(stream -> new InputStreamReader(stream, charset));
    }

    /**
     * Returns ID
     * @param metadata
     * @param content
     * @return 
     */
    SafeOpt put(ResourceMetadata metadata, InputStream content);

    default SafeOpt put(ResourceFull resource) {
        return SafeOpt.ofNullable(resource)
                .flatMap(res -> put(res, new ByteArrayInputStream(res.getContent())));
    }

    SafeOpt delete(String uri);

}
