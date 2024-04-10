package com.dimentrix.report.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "shipMetaData", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "id"
        })
}, schema = "app")
public class ShipMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String vesselName;
    private Date reportDate;
    private String atSeaFrom;
    private String atSeaTo;
    private String atPort;
    private Date receivedDate;


    public ShipMetaData(String vesselName, Date reportDate, String atSeaFrom, String atSeaTo, String atPort, Date receivedDate) {
        this.vesselName = vesselName;
        this.reportDate = reportDate;
        this.atSeaFrom = atSeaFrom;
        this.atSeaTo = atSeaTo;
        this.atPort = atPort;
        this.receivedDate = receivedDate;
    }

    public ShipMetaData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getAtSeaFrom() {
        return atSeaFrom;
    }

    public void setAtSeaFrom(String atSeaFrom) {
        this.atSeaFrom = atSeaFrom;
    }

    public String getAtSeaTo() {
        return atSeaTo;
    }

    public void setAtSeaTo(String atSeaTo) {
        this.atSeaTo = atSeaTo;
    }

    public String getAtPort() {
        return atPort;
    }

    public void setAtPort(String atPort) {
        this.atPort = atPort;
    }

    public Date getReceivedDate() { return receivedDate; }

    public void setReceivedDate(Date reportDate) { this.receivedDate = receivedDate; }

}
