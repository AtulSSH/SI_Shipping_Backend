package com.dimentrix.report.repository;

import com.dimentrix.report.model.ShipMetaData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IShipMetaDataRepository {

     List<ShipMetaData> searchShipMetaData(Map dt);
     void saveShipMetaData(ShipMetaData dt);
     List<ShipMetaData> currentDayRecords();
     List<ShipMetaData> historicalRecords();
     List<ShipMetaData> getPreviousRecord(String prev);
     String getFileLocation(int id);
     List<String> getFileLocations(List<String> id);
     ShipMetaData save(ShipMetaData sd);
     ShipMetaData findAllById(Long id);

     List<ShipMetaData> findAllByIdsAndDateRange(List<Integer> ids, String startDate, String endDate);

     List getVesselCount();


}
