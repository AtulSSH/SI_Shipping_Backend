package com.dimentrix.report.model;

import javax.persistence.*;
import java.lang.ref.Reference;
import java.util.Date;

@Entity
@Table(name = "shipDetailedData", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "id"
        })
}, schema = "app")
public class ShipDetailedData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String checklistReference;
    @Column(length = 4000)
    private String description;
    private String targetDate;
    private  String status;
    private Long shipMetaId;

    public ShipDetailedData() {
    }

    public ShipDetailedData(Long id, String checklistReference, String description, String targetDate, String status, Long shipMetaId) {
        this.id = id;
        this.checklistReference = checklistReference;
        this.description = description;
        this.targetDate = targetDate;
        this.status = status;
        this.shipMetaId = shipMetaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChecklistReference() {
        return checklistReference;
    }

    public void setChecklistReference(String checklistReference) {
        this.checklistReference = checklistReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getShipMetaId() {
        return shipMetaId;
    }

    public void setShipMetaId(Long shipMetaId) {
        this.shipMetaId = shipMetaId;
    }
}
