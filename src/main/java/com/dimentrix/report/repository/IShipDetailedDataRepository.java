package com.dimentrix.report.repository;

import com.dimentrix.report.model.ShipDetailedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IShipDetailedDataRepository extends JpaRepository<ShipDetailedData, Long> {

    List<ShipDetailedData> findAllByShipMetaId(Long id);
    List<ShipDetailedData> findAllByShipMetaIdAndStatusIn(Long id,List<String> status);

    @Query(value="SELECT ship_detailed_data.id,ship_detailed_data.ship_meta_id,checklist_reference,description,status,target_date " +
            "from ship_meta_data,ship_detailed_data where ship_meta_data.id = ship_detailed_data.ship_meta_id " +
            "and DATE(received_date) = :startDate and vessel_name=:vesselName",nativeQuery = true)
    List<ShipDetailedData> getRecordsByDateAndVesselName(
            @Param("startDate") String receivedDate,
            @Param("vesselName") String vesselName
    );
}
