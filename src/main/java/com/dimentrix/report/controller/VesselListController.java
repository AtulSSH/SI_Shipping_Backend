package com.dimentrix.report.controller;


import com.dimentrix.report.model.vesselList;
import com.dimentrix.report.service.IVesselListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@CrossOrigin
@RequestMapping("/api/vessel")
public class VesselListController {

    @Autowired
    IVesselListService VesselListService;

    @GetMapping("/getAllVessels")
    public List<vesselList> getAllVessels(){
        return VesselListService.getAllVessels();
    }
}
