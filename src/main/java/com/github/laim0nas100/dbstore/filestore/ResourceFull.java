package com.github.laim0nas100.dbstore.filestore;

import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author laim0nas100
 */
public class ResourceFull extends ResourceMetadata {

    static final byte[] EMPTY = new byte[]{};

    protected byte[] content = EMPTY;

    public ResourceFull() {
    }

    public ResourceFull(Object id, String uri, String name, String description, String mimeType, boolean text, String additional_info, long size, byte[] content) {
        super(id, uri, name, description, mimeType, text, additional_info, size);
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setContentAndSize(byte[] content) {
        this.content = content;
        this.size = content.length;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.content);
        return hash + super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ResourceFull) {
            final ResourceFull other = (ResourceFull) obj;
            if (!Arrays.equals(this.content, other.content)) {
                return false;
            }
            return super.equals(obj);
        } else {
            return false;
        }

    }

    @Override
    public Map<String, Object> asValueMap() {
        Map<String, Object> valueMap = super.asValueMap();
        putIfPresent(valueMap, "content", content);
        return valueMap;
    }

}
