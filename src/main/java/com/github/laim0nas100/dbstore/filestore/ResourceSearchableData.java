package com.github.laim0nas100.dbstore.filestore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class ResourceSearchableData {

    protected String name;
    protected String description;
    protected String mimeType;
    protected String additional_info;

    public ResourceSearchableData() {
    }

    public ResourceSearchableData(ResultSet rs) throws SQLException {
        this(rs.getString("name"),
                rs.getString("description"),
                rs.getString("mime_type"),
                rs.getString("additional_info")
        );
    }

    public ResourceSearchableData(String name, String description, String mimeType, String additional_info) {
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
        this.additional_info = additional_info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getAdditional_info() {
        return additional_info;
    }

    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Objects.hashCode(this.description);
        hash = 43 * hash + Objects.hashCode(this.mimeType);
        hash = 43 * hash + Objects.hashCode(this.additional_info);
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
        if (obj instanceof ResourceSearchableData) {
            final ResourceSearchableData other = (ResourceSearchableData) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.description, other.description)) {
                return false;
            }
            if (!Objects.equals(this.mimeType, other.mimeType)) {
                return false;
            }
            return Objects.equals(this.additional_info, other.additional_info);
        } else {
            return false;
        }

    }

    public Map<String, Object> asValueMap() {
        Map<String, Object> valueMap = new LinkedHashMap<>();
        putIfPresent(valueMap, "name", getName());
        putIfPresent(valueMap, "description", getDescription());
        putIfPresent(valueMap, "mime_type", getMimeType());
        putIfPresent(valueMap, "additional_info", getAdditional_info());

        return valueMap;
    }

    public Map<String, String> searchableData() {
        Map<String, String> map = new LinkedHashMap<>();
        putIfPresent(map, "name", getName());
        putIfPresent(map, "description", getDescription());
        putIfPresent(map, "mime_type", getMimeType());
        putIfPresent(map, "additional_info", getAdditional_info());

        return map;
    }

    protected <V> void putIfPresent(Map<String, V> valueMap, String key, V value) {
        if (value != null) {
            valueMap.put(key, value);
        }
    }

}
