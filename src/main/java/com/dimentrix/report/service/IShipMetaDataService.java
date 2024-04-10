package com.dimentrix.report.service;

import com.dimentrix.report.model.ShipMetaData;

import java.util.List;
import java.util.Map;

public interface IShipMetaDataService {

     List<ShipMetaData> listAll(Map dt);
     void saveShip(ShipMetaData dt);
     List<ShipMetaData> getCurrentDayRecords();
     List<ShipMetaData> getHistoricalRecords();
     List<ShipMetaData> getPreviousRecord(String prev);
     String getFilePathById(int id);
     List<String> getFilePathByIds(List<String> id);

     List getVesselCount();
}
