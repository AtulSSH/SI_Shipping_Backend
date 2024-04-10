package com.dimentrix.report.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IExportService {
    ResponseEntity<Object> GeneratePDF(Map body);
    ResponseEntity<Object> generateDocx(Map body);



}
