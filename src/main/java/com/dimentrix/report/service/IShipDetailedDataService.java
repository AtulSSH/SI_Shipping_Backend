package com.dimentrix.report.service;

import com.dimentrix.report.model.ShipDetailedData;

import java.util.List;
import java.util.Map;

public interface IShipDetailedDataService {
    List<ShipDetailedData> getRecordsByShipMetaId(Long id);
    Map getCombinedRecordsByMetaId(Long id);

    boolean updateRecord(ShipDetailedData sd);

    boolean addRecord(ShipDetailedData data);

    boolean deleteRecord(Long id);

    List<ShipDetailedData> getRecordsByShipMetaIdAndStatus(Long id, List<String> status);

    Map updateRecords(List<ShipDetailedData> data);

    List<ShipDetailedData> getRecordsByDateAndVesselName(String receivedDate,String vesselName);

}
