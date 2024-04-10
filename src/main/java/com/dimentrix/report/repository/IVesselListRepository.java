package com.dimentrix.report.repository;

import com.dimentrix.report.model.vesselList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IVesselListRepository extends JpaRepository<vesselList, Long> {
     @Query(value="SELECT * from vessel_list where status='Active' order by vessel_name asc",nativeQuery = true)
     List<vesselList> getvesselListByAlphabetical();

}
