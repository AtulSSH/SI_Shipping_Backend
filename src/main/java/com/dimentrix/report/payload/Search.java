package com.dimentrix.report.payload;

public class Search {

    private String sText;
    private String sc;

    public String getsText() {
        return sText;
    }

    public void setsText(String sText) {
        this.sText = sText;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    @Override
    public String toString() {
        return "Search{" +
                "sText='" + sText + '\'' +
                ", sc='" + sc + '\'' +
                '}';
    }
}
