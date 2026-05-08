package org.PR2.model;

public class EncryptedFile {
    private final String path;
    private final long size;

    public EncryptedFile(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return new java.io.File(path).getName();
    }

    public long getSize() {
        return size;
    }
}