package com.dimentrix.report.service;

import com.dimentrix.report.model.ShipDetailedData;
import com.dimentrix.report.model.ShipMetaData;
import com.dimentrix.report.repository.IShipDetailedDataRepository;
import com.dimentrix.report.repository.IShipMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShipDetailedDataService implements IShipDetailedDataService {

    @Autowired
    IShipDetailedDataRepository shipDetailedDataRepository;

    @Autowired
    IShipMetaDataRepository shipMetaDataRepository;

    @Override
    public List<ShipDetailedData> getRecordsByShipMetaId(Long id) {
        return shipDetailedDataRepository.findAllByShipMetaId(id);
    }

    @Override
    public Map getCombinedRecordsByMetaId(Long id) {
        ShipMetaData sd = shipMetaDataRepository.findAllById(id);

        if (sd != null) {
            List de = shipDetailedDataRepository.findAllByShipMetaId(id);

            Map m1 = new HashMap();

            m1.put("metaData", sd);
            m1.put("detailedData", de);

            return m1;
        }
        return new HashMap();
    }

    public List<ShipDetailedData> getRecordsByShipMetaIdAndStatus(Long id, List<String> status) {
        return shipDetailedDataRepository.findAllByShipMetaIdAndStatusIn(id, status);
    }

    @Override
    public boolean deleteRecord(Long id) {
        shipDetailedDataRepository.deleteById(id);
        return !shipDetailedDataRepository.existsById(id);
    }

    @Override
    public boolean updateRecord(ShipDetailedData sd) {
        if(sd.getId()!=null && shipDetailedDataRepository.existsById(sd.getId())){
            shipDetailedDataRepository.save(sd);
            return true;
        }
        return false;
    }

    @Override
    public boolean addRecord(ShipDetailedData sd) {
        if (sd.getId() == null) {
            shipDetailedDataRepository.save(sd);
            return true;
        }
        return false;
    }

    public List<ShipDetailedData> getRecordsByDateAndVesselName(String receivedDate,String vesselName){
        return shipDetailedDataRepository.getRecordsByDateAndVesselName(receivedDate,vesselName);
    }


    @Override
    public Map updateRecords(List<ShipDetailedData> data) {
        int updateCount=0,addCount=0;
        if(data.size()>0){
            List<ShipDetailedData> updateList = new ArrayList<>();
            List<ShipDetailedData> addList = new ArrayList<>();
            for(ShipDetailedData sd : data){
                if(sd.getId()!=null ){
                    if(shipDetailedDataRepository.existsById(sd.getId())){
                        updateList.add(sd);
                        updateCount++;
                    }
                }
                else{
                    addList.add(sd);
                    addCount++;
                }
            }

            if(updateList.size()>0){
                shipDetailedDataRepository.saveAll(updateList);
            }
            if(addList.size()>0){
                shipDetailedDataRepository.saveAll(addList);
            }
        }
        Map response = new HashMap();
        response.put("updatedRecords",updateCount);
        response.put("addedRecords",addCount);
        return response;
    }
}
