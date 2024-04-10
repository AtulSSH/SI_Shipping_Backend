package com.dimentrix.report.model;

import javax.persistence.*;

@Entity
@Table(name = "vesselList", schema = "app")
public class vesselList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vesselName;

    private String vesselCode;

    private String vesselType;

    private String sizeCategory;

    private String sizeShortName;

    private String dtw;

    private String status = "Active";

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

    public String getVesselCode() { return vesselCode; }

    public void setVesselCode(String vesselCode) { this.vesselCode = vesselCode; }

    public String getVesselType() { return vesselType; }

    public void setVesselType(String vesselType) { this.vesselType = vesselType; }

    public String getSizeCategory() { return sizeCategory; }

    public void setSizeCategory(String sizeCategory) { this.sizeCategory = sizeCategory; }

    public String getSizeShortName() { return sizeShortName; }

    public void setSizeShortName(String sizeShortName) { this.sizeShortName = sizeShortName; }

    public String getDtw() { return dtw; }

    public void setDtw(String dtw) { this.dtw = dtw; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
