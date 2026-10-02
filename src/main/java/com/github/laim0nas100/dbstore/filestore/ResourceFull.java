package com.github.laim0nas100.dbstore.filestore;


/**
 *
 * @author laim0nas100
 */
public class ResourceFull extends ResourceMetadata {

    static final byte[] EMPTY = new byte[]{};

    protected byte[] content = EMPTY;

    public ResourceFull() {
    }

    public ResourceFull(Object id, String uri, String name, String description, String mimeType, String additional_info, long size, byte[] content) {
        super(id, uri, name, description, mimeType, additional_info, size);
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    public void setContentAndSize(byte[] content){
        this.content = content;
        this.size = content.length;
    }

}
