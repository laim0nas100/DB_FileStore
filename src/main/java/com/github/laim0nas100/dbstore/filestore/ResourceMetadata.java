package com.github.laim0nas100.dbstore.filestore;


/**
 *
 * @author laim0nas100
 */
public class ResourceMetadata {

    protected Object id;
    protected String uri;
    protected String name;
    protected String description;
    protected String mimeType;
    protected String additional_info;
    protected long size;

    public ResourceMetadata() {
    }

    
    
    public ResourceMetadata(Object id, String uri, String name, String description, String mimeType, String additional_info, long size) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
        this.additional_info = additional_info;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    
    
    
}
