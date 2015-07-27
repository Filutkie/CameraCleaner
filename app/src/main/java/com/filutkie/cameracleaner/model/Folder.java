package com.filutkie.cameracleaner.model;

/**
 * A Folder that represents name and size of temp / panorama sessions cache folders.
 */
public class Folder {

    private String name;
    private long size;

    public Folder() {
    }

    public Folder(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
