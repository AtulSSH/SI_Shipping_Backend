package com.dimentrix.report.controller;

import com.dimentrix.report.model.ShipMetaData;
import com.dimentrix.report.service.IShipMetaDataService;
import com.dimentrix.report.service.IexcelToPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/ship")
public class ShipMetaDataController {

    @Autowired
    IShipMetaDataService shipMetaDataService;

    @Autowired
    IexcelToPdfService excelToPdfService;

    @PostMapping("/search")
    public List<ShipMetaData> searchRecords(@RequestBody Map data){
        return shipMetaDataService.listAll(data);
    }

    @GetMapping("/getCurrent")
    public List<ShipMetaData> getCurrentDayRecords(){
        return shipMetaDataService.getCurrentDayRecords();
    }


    @GetMapping("/getHistorical")
    public List<ShipMetaData> getHistoricalRecords(){ return shipMetaDataService.getHistoricalRecords(); }

    @PostMapping("/getPreviousData")
    public List<ShipMetaData> getPreviousRecord(@RequestBody Map data){
        String prev = data.get("prevDate").toString();
        return shipMetaDataService.getPreviousRecord(prev);
    }

    @PostMapping("/downloadFile/{option}")
    public ResponseEntity<Object> downloadExcel(
            @RequestBody Map<String,Integer> req,
            @PathVariable("option") int option
    )
    {
        ResponseEntity<Object> responseEntity =null;
        List<String> lInt = new ArrayList<String>();
        for(int id : req.values().toArray(new Integer[0]) ) {
            lInt.add(String.valueOf(id));
        }
        List<String>  paths =  shipMetaDataService.getFilePathByIds(lInt);
        responseEntity = excelToPdfService.downloadExcelFile(paths,option);

        return responseEntity;
    }
    @PostMapping("/downloadFile/{fileName}/{option}")
    public ResponseEntity<Object> downloadPdf(
            @RequestBody Map<String,Integer> req,
            @PathVariable("fileName") String fileName,
            @PathVariable("option") int option
        )
    {

        ResponseEntity<Object> responseEntity =null;
        List<String> lInt = new ArrayList<String>();
        for(int id : req.values().toArray(new Integer[0]) ) {
            lInt.add(String.valueOf(id));
        }


            List<String>  paths =  shipMetaDataService.getFilePathByIds(lInt);
            responseEntity = excelToPdfService.createPDF(paths,fileName,option);

        return responseEntity;
    }
    @GetMapping("/vesselCount")
    public List getVesselCount(){
        return shipMetaDataService.getVesselCount();
    }
}