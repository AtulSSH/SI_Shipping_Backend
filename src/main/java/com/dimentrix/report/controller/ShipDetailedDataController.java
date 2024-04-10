package com.dimentrix.report.controller;

import com.dimentrix.report.model.ShipDetailedData;
import com.dimentrix.report.service.IShipDetailedDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/ship")
public class ShipDetailedDataController {

    @Autowired
    IShipDetailedDataService shipDetailedDataService;

    @GetMapping("/getDetailedRecords/{id}")
    public List getRecordsForId(
            @PathVariable("id") Long id,
            @RequestParam("status") List<String> status
    ) {
        return shipDetailedDataService.getRecordsByShipMetaIdAndStatus(id,status);
    }

    @GetMapping("/getDetailedRecordsE/{id}")
    public Map getCombinedRecordsForId(
            @PathVariable("id") Long id
    ) {
        return shipDetailedDataService.getCombinedRecordsByMetaId(id);
    }

    @PostMapping("/updateRecord")
    public boolean updateDetailedRecords(
            @RequestBody ShipDetailedData data
            ){
        return shipDetailedDataService.updateRecord(data);
    }

    @PostMapping("/addNewRecord")
    public boolean addNewDetailedRecords(
            @RequestBody ShipDetailedData data
    ){
        return shipDetailedDataService.addRecord(data);
    }

    @GetMapping("/deleteRecord/{id}")
    public boolean deleteRecord(
            @PathVariable("id") Long id
    ) {
        return shipDetailedDataService.deleteRecord(id);
    }

    @PostMapping("/getDetailedRecordsByFilter")
    public List getRecordsByFilter(@RequestBody Map data) {
        String date = data.get("receivedDate").toString();
        String vesselName = data.get("vesselName").toString();
        return shipDetailedDataService.getRecordsByDateAndVesselName(date, vesselName);
    }

    @PostMapping("/updateRecords")
    public Map addupdateDetailedRecords(
            @RequestBody List<ShipDetailedData> data
    ){
        return shipDetailedDataService.updateRecords(data);
    }


}