package com.github.laim0nas100.dbstore.filestore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class ResourceMetadata extends ResourceSearchableData {

    protected Object id;
    protected String uri;
    protected boolean text = true;
    protected long size;

    public ResourceMetadata() {
    }

    public ResourceMetadata(ResultSet rs) throws SQLException {
        super(rs);
        this.id = rs.getObject("id");
        this.uri = rs.getString("uri");
        this.size = rs.getLong("size");
        this.text = rs.getBoolean("text");
    }

    public ResourceMetadata(Object id, String uri, String name, String description, String mimeType, boolean text, String additional_info, long size) {
        super(name, description, mimeType, additional_info);
        this.id = id;
        this.uri = uri;
        this.text = text;
        this.size = size;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.uri);
        hash = 47 * hash + Objects.hashCode(this.text);
        hash = 47 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 47 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ResourceMetadata) {
            final ResourceMetadata other = (ResourceMetadata) obj;
            if (this.size != other.size) {
                return false;
            }
            if (!Objects.equals(this.uri, other.uri)) {
                return false;
            }

            if (!Objects.equals(this.text, other.text)) {
                return false;
            }
            if (!Objects.equals(this.id, other.id)) {
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
        putIfPresent(valueMap, "id", getId());
        putIfPresent(valueMap, "uri", getUri());
        putIfPresent(valueMap, "size", getSize());
        putIfPresent(valueMap, "text", isText());
        return valueMap;
    }

}
