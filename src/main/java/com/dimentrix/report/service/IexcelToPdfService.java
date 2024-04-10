package com.dimentrix.report.service;

import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public interface IexcelToPdfService {
    //public ByteArrayOutputStream createPDF(String path);

    public ResponseEntity<Object> createPDF(List<String> path,String fileName,int option);
    public ResponseEntity<Object> downloadExcelFile(List<String> path,int option);
}