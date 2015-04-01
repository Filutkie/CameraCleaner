package com.filutkie.cameracleaner.model;

public class HistoryRecord {

    private long size;
    private long date;

    public HistoryRecord() {
    }

    public HistoryRecord(long size, long date) {
        this.size = size;
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" + "size=" + size + ", date=" + date + '}';
    }
}
