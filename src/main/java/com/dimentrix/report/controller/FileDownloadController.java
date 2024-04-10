package com.dimentrix.report.controller;

import com.dimentrix.report.service.IExportService;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/download/")
public class FileDownloadController {

    @Autowired
    IExportService exportService;


    @PostMapping("/downloadPDF")
    public ResponseEntity<Object> generatePDF(
            @RequestBody Map body
    ) {
        return exportService.GeneratePDF(body);
    }

    @PostMapping("/downloadDocx")
    public ResponseEntity<Object> generateDoc(@RequestBody Map body) {
        return exportService.generateDocx(body);
    }


}
