package com.dimentrix.report.service;


import com.dimentrix.report.model.vesselList;
import com.dimentrix.report.repository.IVesselListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VesselListService implements IVesselListService {
    @Autowired
    IVesselListRepository iVesselListRepository;

    @Override
    public List<vesselList> getAllVessels() {
        return iVesselListRepository.getvesselListByAlphabetical();
    }
}
