package com.dimentrix.report.service;

import com.dimentrix.report.model.FileInfo;
import com.dimentrix.report.model.ShipDetailedData;
import com.dimentrix.report.model.ShipMetaData;
import com.dimentrix.report.repository.IShipDetailedDataRepository;
import com.dimentrix.report.repository.IShipMetaDataRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelParserService {
    @Autowired
    IShipDetailedDataRepository shipDetailedDataRepository;
    @Autowired
    IShipMetaDataRepository shipMetaDataRepository;




    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelParserService.class);


    public List<Long> getFiles(List<FileInfo> files) {
        List<Long> shipMetaDataIdList = new ArrayList<Long>();
        File file;
        FileInputStream fs;
        String filePath;
        String fileExt;
        try {

            for (int i = 0; i < files.size(); i++) {
                FileInfo fileInfo = files.get(i);
                filePath = generateSavePath() +fileInfo.getFileName() ;
                file = new File(filePath);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Current File => "+files.get(i));
                }
                String[] ext = filePath.split("\\.");
                fileExt = ext[ext.length - 1];

                if (fileExt.equalsIgnoreCase("xlsx")) {
                    shipMetaDataIdList.add(parseXlsx(file,fileInfo.getReceivedDate()));
                } else if (fileExt.equalsIgnoreCase("xls")) {
                    shipMetaDataIdList.add(parseXls(file,fileInfo.getReceivedDate()));
                }
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Exception Raised => " + e);
            }
            e.printStackTrace();
        }
        return shipMetaDataIdList;
    }

    public Long parseXlsx(File file,Date receivedDate) {
        ShipMetaData smd = new ShipMetaData();
        ShipDetailedData sdd;

        try {
            Workbook wb = new XSSFWorkbook(new FileInputStream(file));
            Sheet sh = wb.getSheetAt(0);

            Row r;
            Row.MissingCellPolicy mcp = Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
            Row dateRow =  sh.getRow(2);
            Date tt = null;
            switch (dateRow.getCell(3).getCellType()){
                case STRING:
                    String date = sh.getRow(2).getCell(3).getStringCellValue();
                    //atul code added for date bug fix
                    SimpleDateFormat sdf=null;

                    if(date.contains(".") && date.contains("-")){
                        char secondChar=date.charAt(2);
                        if(secondChar=='.')
                        {
                            sdf = new SimpleDateFormat("dd.MM-yyyy");
                        }
                        else
                        {
                            sdf = new SimpleDateFormat("dd-MM.yyyy");
                        }
                    }
                    else if(date.contains(".")){
                        sdf = new SimpleDateFormat("dd.MM.yyyy");
                    }
                    else if(date.contains("-")){
                        sdf = new SimpleDateFormat("dd-MM-yyyy");
                    }
                    else
                    {
                        sdf = new SimpleDateFormat("dd/MM/yyyy");
                    }

                    //old code commented
                    // String format = sh.getRow(2).getCell(4).getStringCellValue();
                    //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    tt = sdf.parse(date);
                    tt = sdf.parse(date);
                    break;
                case NUMERIC:
                    tt =sh.getRow(2).getCell(3).getDateCellValue();
                    break;
                default:
            }

            smd = new ShipMetaData(
                    String.valueOf(sh.getRow(1).getCell(3).getRichStringCellValue()).trim(),
                    tt,
                    String.valueOf(sh.getRow(3).getCell(3).getRichStringCellValue()),
                    String.valueOf(sh.getRow(4).getCell(3).getRichStringCellValue()),
                    String.valueOf(sh.getRow(5).getCell(3).getRichStringCellValue()),
                    receivedDate

            );

            shipMetaDataRepository.save(smd);

            List sedList = new ArrayList();


            int count =0;
            for(int k=8;;k++){
                if(count ==5)
                    break;
                sdd = new ShipDetailedData();
                r = sh.getRow(k);
                if(r == null)
                    break;

                if((r.getCell(0) == null || r.getCell(0).getCellType().toString().equals("BLANK"))){
                    if(r.getCell(1) == null || r.getCell(1).getCellType().toString().equals("BLANK")){
                        count++;
                        continue;
                    }
                }

                //fixed 2nd bug produced because od date fix- checklist reference contains few numeric values
                if(r.getCell(1,mcp).getCellType()== CellType.NUMERIC) {
                    sdd.setChecklistReference(String.valueOf(r.getCell(1, mcp).getNumericCellValue()));
                }
                else {
                    sdd.setChecklistReference(String.valueOf(r.getCell(1, mcp).getRichStringCellValue()));
                }
                sdd.setDescription(String.valueOf(r.getCell(2,mcp).getRichStringCellValue()));

                switch (r.getCell(3).getCellType()){
                    case STRING:
                        sdd.setTargetDate(String.valueOf(r.getCell(3,mcp).getRichStringCellValue()));
                        break;
                    case NUMERIC:
                        sdd.setTargetDate(String.valueOf(r.getCell(3,mcp)));
                        break;
                    default:
                }

                sdd.setStatus(String.valueOf(r.getCell(4,mcp).getRichStringCellValue()));
                sdd.setShipMetaId(smd.getId());

                sedList.add(sdd);
            }

            shipDetailedDataRepository.saveAll(sedList);

        }
        catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error Occurred While Parsing XlSX File=>  " + file.getAbsolutePath(), e);
            }
            e.printStackTrace();
        }
        return smd.getId();
    }

    public Long parseXls(File file,Date receivedDate) {
        ShipMetaData sd = new ShipMetaData();
        try {

            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet sh = wb.getSheetAt(0);
            Row row;
            int i = 0;

            row = sh.getRow(1);
            sd.setVesselName(String.valueOf(row.getCell(3).getRichStringCellValue()));
            row = sh.getRow(2);
            sd.setReportDate(row.getCell(3).getDateCellValue());
            row = sh.getRow(3);
            sd.setAtSeaFrom(String.valueOf(row.getCell(3).getRichStringCellValue()));
            row = sh.getRow(4);
            sd.setAtSeaTo(String.valueOf(row.getCell(3).getRichStringCellValue()));
            row = sh.getRow(5);
            sd.setAtPort(String.valueOf(row.getCell(3).getRichStringCellValue()));

        } catch (Exception e) {
            e.printStackTrace();
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Error Occurred While Parsing XlS File" + e);
            }
        }
        return sd.getId();
    }

    public String generateSavePath() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String path = System.getProperty("user.home") + "/.smartShipReports/" + sdf.format(new Date()) + "/";
        File directory = new File(path);
        if (!directory.exists())
            directory.mkdir();
        return path;
    }
}