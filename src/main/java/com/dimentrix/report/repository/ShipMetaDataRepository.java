package com.dimentrix.report.repository;

import com.dimentrix.report.model.ShipMetaData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.*;

@Repository
public class ShipMetaDataRepository implements IShipMetaDataRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void saveShipMetaData(ShipMetaData dt) {
        this.entityManager.persist(dt);
    }

    @Override
    public List<ShipMetaData> currentDayRecords() {
        Query nativeQuery = entityManager.createNativeQuery("Select * From ship_meta_data where CAST(date as date) = CURRENT_DATE;",ShipMetaData.class);
        List<ShipMetaData> resultList = nativeQuery.getResultList();
        Iterator<ShipMetaData> itr = resultList.iterator();
        List<String> vNames= new ArrayList();
        while(itr.hasNext()) {
            vNames.add(itr.next().getVesselName());
        }
        Query nativeQuery1 = entityManager.createNativeQuery("Select vessel_name From vessel_list where vessel_name NOT IN :list1");
        nativeQuery1.setParameter("list1", vNames);
        List <String> names = nativeQuery1.getResultList();
        Iterator<String> itr1 = names.iterator();
        ShipMetaData s ;
        while(itr1.hasNext()) {
            s = new ShipMetaData(itr1.next(),null,null,null,null,null);
            resultList.add(s);

        }

        return resultList;

    }

    @Override
    public List<ShipMetaData> historicalRecords() {
       Query nativeQuery = entityManager.createNativeQuery("Select * From ship_meta_data where DATE(received_date) = CURRENT_DATE()", ShipMetaData.class);
       List<ShipMetaData> resultList = nativeQuery.getResultList();
       return resultList;
    }

    @Override
    public List<ShipMetaData> getPreviousRecord(String prev) {
        Query nativeQuery = entityManager.createNativeQuery("Select * From ship_meta_data where DATE(date) = '"+prev+"'", ShipMetaData.class);
        List<ShipMetaData> resultList = nativeQuery.getResultList();
        return resultList;
    }

    @Override
    public String getFileLocation(int id) {
        Query nativeQuery = entityManager.createNativeQuery("SELECT  file_location FROM ship_meta_data where id ='"+id+"'");
        String result = String.valueOf(nativeQuery.getResultList().get(0));
        return result;
    }

    @Override
    public List<String> getFileLocations(List<String> id) {
        String ids = String.join(",",id);
        Query nativeQuery = entityManager.createNativeQuery("SELECT  file_location FROM ship_meta_data where id IN ("+ids+")");
        List<String> result = nativeQuery.getResultList();
        return result;
    }

    @Transactional
    public ShipMetaData save(ShipMetaData sd) {
        this.entityManager.persist(sd);
        return sd;

    }


    public List<ShipMetaData> searchShipMetaData(Map dt){
        String startDate = dt.get("startDate").toString();
        String endDate = dt.get("endDate").toString();
        List<String> vesselNames = (List<String>) dt.get("vesselNames");

        if (vesselNames.size() > 0){
            Query nativeQuery = entityManager.createNativeQuery("Select * From ship_meta_data where DATE(received_date) between '"+startDate+"' and '"+endDate+"' and vessel_name in :list order by report_date desc , vessel_name asc ", ShipMetaData.class);
            nativeQuery.setParameter("list", vesselNames);
            return nativeQuery.getResultList();
        }
        return new ArrayList<>();
    }

    @Override
    public ShipMetaData findAllById(Long id) {
        Query nativeQuery = entityManager.createNativeQuery("Select * from ship_meta_data where id = :id",ShipMetaData.class);
        nativeQuery.setParameter("id", id);
        ShipMetaData sd = null;
        try{
            sd = (ShipMetaData) nativeQuery.getSingleResult();
        }
        catch (NoResultException noResultException){}

        return sd;
    }

    @Override
    public List<ShipMetaData> findAllByIdsAndDateRange(List<Integer> ids, String startDate, String endDate) {
        Query nativeQuery = entityManager.createNativeQuery("Select * from ship_meta_data " +
                "where id In :ids and DATE(received_date) between :startDate and :endDate ORDER BY vessel_name asc,report_date desc ",ShipMetaData.class);
        nativeQuery.setParameter("ids",ids);
        nativeQuery.setParameter("startDate",startDate);
        nativeQuery.setParameter("endDate",endDate);

       return (List<ShipMetaData>) nativeQuery.getResultList();

    }

    public List getVesselCount() {
        List m = new ArrayList();
        HashMap <String,Long> count = new HashMap<>();
        Query nativeQuery = entityManager.createNativeQuery("Select COUNT(DISTINCT vessel_name)" +
                " From ship_meta_data where DATE(received_date) = CURRENT_DATE");
        Long daily =((Number)nativeQuery.getSingleResult()).longValue();
        count.put("Daily",daily);
        nativeQuery = entityManager.createNativeQuery("Select COUNT(DISTINCT vessel_name) " +
                "From vessel_list where status = 'Active'");
        Long total =((Number)nativeQuery.getSingleResult()).longValue();
        count.put("Total",total);
        m.add(count);
        return m;
    }
}
