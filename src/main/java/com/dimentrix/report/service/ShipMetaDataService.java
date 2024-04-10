package com.dimentrix.report.service;

import com.dimentrix.report.model.ShipMetaData;
import com.dimentrix.report.repository.IShipMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShipMetaDataService implements IShipMetaDataService {

    @Autowired
    IShipMetaDataRepository shipMetaDataRepository;


    @Override
    public List<ShipMetaData> listAll(Map dt) {

        return shipMetaDataRepository.searchShipMetaData(dt);
    }

    @Override
    public void saveShip(ShipMetaData dt) {
        shipMetaDataRepository.saveShipMetaData(dt);
    }

    @Override
    public List<ShipMetaData> getCurrentDayRecords() {
       return shipMetaDataRepository.currentDayRecords();
    }

    @Override
    public List<ShipMetaData> getHistoricalRecords() {
        return shipMetaDataRepository.historicalRecords();
    }

    @Override
    public List<ShipMetaData> getPreviousRecord(String prev) {
        return shipMetaDataRepository.getPreviousRecord(prev);
    }

    @Override
    public String getFilePathById(int id) {
        return shipMetaDataRepository.getFileLocation(id);
    }

    @Override
    public List<String> getFilePathByIds(List<String> id) {
        return shipMetaDataRepository.getFileLocations(id);
    }

    @Override
    public List getVesselCount() {
        return shipMetaDataRepository.getVesselCount();
    }
}
