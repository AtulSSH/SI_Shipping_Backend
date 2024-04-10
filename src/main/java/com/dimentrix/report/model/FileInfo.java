package com.dimentrix.report.model;


import java.util.Date;

public class FileInfo {

    private String fileName;

    private int count;

    public String getFileName() {
        return fileName;
    }

    public int getCount() {
        return count;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    private Date receivedDate;

    public FileInfo(String fileName, int count, Date receivedDate) {
        this.fileName = fileName;
        this.count = count;
        this.receivedDate = receivedDate;
    }
}
