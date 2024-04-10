package com.dimentrix.report.service;

import be.quodlibet.boxable.*;
import be.quodlibet.boxable.line.LineStyle;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class excelToPdfService implements IexcelToPdfService {

    private String getEpoch() {
        String path = null;
        Date date = null;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
            String currentTime = sdf.format(Calendar.getInstance().getTime());
            date = sdf.parse(currentTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(date.getTime());
    }

    @Override
    public ResponseEntity<Object> downloadExcelFile(List<String> paths, int option) {
        List<String> fileList = new ArrayList<String>();
        File file = null;
        FileInputStream fis =null;
        InputStreamResource resource = null;
        HttpHeaders headers = new HttpHeaders();
        MediaType mt = null;
        ResponseEntity re = null;
        ZipOutputStream zos =null;
        byte[] buffer = new byte[1024];
        String zipPath= null;

        if (option == 0){
            try{
                file = new File(paths.get(0));
                fis = new FileInputStream(file);
                resource = new InputStreamResource(fis);
            }catch (Exception e){e.printStackTrace();}

        }else if(option == 2){

            try{
                zipPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+ getEpoch()+".zip";

                zos = new ZipOutputStream(new FileOutputStream(zipPath));
                for (String fileitem : paths) {
                    file = new File(fileitem);
                    fis = new FileInputStream(file);
                    zos.putNextEntry(new ZipEntry(getEpoch()+"_"+file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
                zos.close();
                file = new File(zipPath);
                resource = new InputStreamResource(new FileInputStream(file));
            }catch (Exception e){e.printStackTrace();}
        }

        if(option == 0){
            headers.add("Access-Control-Expose-Headers","*");
            headers.add("Content-Disposition", "attachment; filename=" + file.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            re = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else if(option == 2){
            headers.add("Access-Control-Expose-Headers","*");
            headers.add("Content-Disposition", "attachment; filename=reports.zip");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            re = ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }
        return re;
    }

    @Override
    public ResponseEntity<Object> createPDF(List<String> paths,String fileName,int option) {
        InputStreamResource resource = null;
        File file = null;
        FileInputStream fis =null;
        FileOutputStream fos = null;
        ZipOutputStream zos =null;
        HttpHeaders headers = new HttpHeaders();
        MediaType mt = null;
        List<String> fileList = new ArrayList<>();
        byte[] buffer = new byte[1024];
        String zipPath= null;


        if (option == 0){
            try{
                for (String path : paths){
                    fileList.add(this.createPDF(path, fileName));
                }

                file = new File(fileList.get(0));
                fis = new FileInputStream(file);
                resource = new InputStreamResource(fis);
            }catch (Exception e){e.printStackTrace();}

    }else if(option == 1){
            try{
                for(String path : paths){
                    fileList.add(this.createPDF(path, this.getEpoch()+".pdf"));
                }

                file = new File(this.multiPDF(fileList,fileName));
                fis = new FileInputStream(file);
                resource = new InputStreamResource(fis);

            }catch (Exception e){e.printStackTrace();}
        }
        else if(option == 2){
            try{
                for (String path : paths){
                    fileList.add(this.createPDF(path, this.getEpoch()));
                }
                zipPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+fileName+".zip";

                zos = new ZipOutputStream(new FileOutputStream(zipPath));
                for (String fileitem : fileList) {
                    file = new File(fileitem);
                    fis = new FileInputStream(file);
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
                zos.close();
                file = new File(zipPath);
                resource = new InputStreamResource(new FileInputStream(file));
            }catch (Exception e){e.printStackTrace();}
        }

        if (option == 0) {
            headers.add("Access-Control-Expose-Headers","*");
            headers.add("Content-Disposition", "attachment; filename=" + file.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
           mt =  MediaType.APPLICATION_PDF;
        } else if(option == 1){
            headers.add("Access-Control-Expose-Headers","*");
            headers.add("Content-Disposition", "attachment; filename=" + file.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            mt =  MediaType.APPLICATION_PDF;
        } else if(option == 2){
            headers.add("Access-Control-Expose-Headers","*");
            headers.add("Content-Disposition", "attachment; filename=" + file.getName());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            mt= MediaType.APPLICATION_OCTET_STREAM;
        }


      ResponseEntity re = ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mt)
                .body(resource);

        return re;
    }


    public String createPDF(String path, String fileName) {
        String dlPath = null;
        FileOutputStream fos = null;

        if(fileName.isEmpty())
            dlPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+this.getEpoch()+".pdf";
        else
            dlPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+fileName+".pdf";


        try {
            FileInputStream fs = new FileInputStream(new File(path));

            String[] ext = path.split("\\.");
            String fileExt = ext[ext.length - 1];


            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
            PDFont fontMono = PDType1Font.TIMES_ROMAN;

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);

            PDRectangle pageSize = page.getMediaBox();
            document.addPage(page);

            PDDocumentInformation pdd = document.getDocumentInformation();
            pdd.setAuthor("SmartShip");
            pdd.setTitle("SmartShip Report");
            pdd.setCreator("SmartShip");
            pdd.setSubject("SmartShip Report");
            Calendar date = new GregorianCalendar();
            pdd.setCreationDate(date);


            PDPageContentStream cos = new PDPageContentStream(document, page);

            float margin = 20;

            float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);

            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
            float yPosition = 820;
            Color blue1 = new Color(76, 129, 190);
            Color blue2 = new Color(186, 206, 230);
            Color blue3 = new Color(218, 230, 242);

            LineStyle lnsWhiteNoB = new LineStyle(Color.WHITE, 0);

            BaseTable table = new BaseTable(yPosition, yStartNewPage,
                    bottomMargin, tableWidth, margin, document, page, true, drawContent);

            be.quodlibet.boxable.Row<PDPage> headerRow = table.createRow(50);

            Cell<PDPage> tcell = headerRow.createCell(100, "Report");
            tcell.setFont(fontBold);
            tcell.setFontSize(20);
            tcell.setValign(VerticalAlignment.MIDDLE);
            tcell.setAlign(HorizontalAlignment.CENTER);
            tcell.setBorderStyle(null);
            table.addHeaderRow(headerRow);


            if (fileExt.equalsIgnoreCase("xlsx")) {

                XSSFWorkbook wb = new XSSFWorkbook(fs);
                XSSFSheet sh = wb.getSheetAt(0);

                org.apache.poi.ss.usermodel.Row row;
                Row<PDPage> tableRow;
                tableRow = table.createRow(20);
                tcell = tableRow.createCell(100, "");
                tcell.setBorderStyle(null);

                for (int r = 1; r <= 5; r++) {
                    tableRow = table.createRow(20);
                    row = sh.getRow(r);
                    tcell = tableRow.createCell(80, String.valueOf(row.getCell(2)));
                    tcell.setFontSize(13);
                    tcell.setAlign(HorizontalAlignment.RIGHT);
                    tcell.setBorderStyle(null);

                    tcell = tableRow.createCell(20, String.valueOf(row.getCell(3)));
                    tcell.setFontSize(13);
                    tcell.setAlign(HorizontalAlignment.LEFT);
                    tcell.setBorderStyle(null);
                }

                tableRow = table.createRow(20);
                tcell = tableRow.createCell(100, "");
                tcell.setBorderStyle(null);

                //Table Headers 0-4
                row = sh.getRow(7);
                int[] widthArray = new int[]{10, 20, 45, 15, 10};
                tableRow = table.createRow(20);
                for (int i = 0; i < 5; i++) {

                    tcell = tableRow.createCell(widthArray[i], String.valueOf(row.getCell(i)));
                    tcell.setFontSize(13);
                    tcell.setAlign(HorizontalAlignment.CENTER);
                    tcell.setValign(VerticalAlignment.MIDDLE);
                    tcell.setFillColor(blue1);
                    tcell.setFont(fontBold);
                    tcell.setTextColor(Color.WHITE);
                    tcell.setBorderStyle(lnsWhiteNoB);
                }

                //Table Data Dynamic
                int count = 0;
                int rowCount = 8;
                for (org.apache.poi.ss.usermodel.Row rw : sh) {
                    rw = sh.getRow(rowCount);
                    if (count > 3)
                        break;
                    if (String.valueOf(rw.getCell(0)) == "") {
                        count++;
                    } else {

                        tableRow = table.createRow(20);
                        for (int i = 0; i < 5; i++) {
                            tcell = tableRow.createCell(widthArray[i], String.valueOf(rw.getCell(i)));
                            tcell.setFontSize(13);
                            tcell.setAlign(HorizontalAlignment.CENTER);
                            tcell.setValign(VerticalAlignment.MIDDLE);
                            tcell.setFillColor((rowCount % 2) == 0 ? blue2 : blue3);
                            tcell.setFont(fontMono);
                            tcell.setBorderStyle(lnsWhiteNoB);
                        }
                    }
                    rowCount++;
                }
            } else if (fileExt.equalsIgnoreCase("xls")) {

                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sh = wb.getSheetAt(0);

                org.apache.poi.ss.usermodel.Row row;
                Row<PDPage> tableRow;
                tableRow = table.createRow(20);
                tcell = tableRow.createCell(100, "");
                tcell.setBorderStyle(null);

                for (int r = 1; r <= 5; r++) {
                    tableRow = table.createRow(20);
                    row = sh.getRow(r);
                    tcell = tableRow.createCell(80, String.valueOf(row.getCell(2)));
                    tcell.setFontSize(13);
                    tcell.setAlign(HorizontalAlignment.RIGHT);
                    tcell.setBorderStyle(null);

                    tcell = tableRow.createCell(20, String.valueOf(row.getCell(3)));
                    tcell.setFontSize(13);
                    tcell.setAlign(HorizontalAlignment.LEFT);
                    tcell.setBorderStyle(null);
                }

                tableRow = table.createRow(20);
                tcell = tableRow.createCell(100, "");
                tcell.setBorderStyle(null);

                //Table Headers 0-4
                row = sh.getRow(7);
                int[] widthArray = new int[]{10, 20, 45, 15, 10};
                tableRow = table.createRow(20);
                for (int i = 0; i < 5; i++) {

                    tcell = tableRow.createCell(widthArray[i], String.valueOf(row.getCell(i)));
                    tcell.setFontSize(13);
                    tcell.setAlign(HorizontalAlignment.CENTER);
                    tcell.setValign(VerticalAlignment.MIDDLE);
                    tcell.setFillColor(blue1);
                    tcell.setFont(fontBold);
                    tcell.setTextColor(Color.WHITE);
                    tcell.setBorderStyle(lnsWhiteNoB);
                }

                //Table Data Dynamic
                int count = 0;
                int rowCount = 8;
                for (org.apache.poi.ss.usermodel.Row rw : sh) {
                    rw = sh.getRow(rowCount);
                    if (count > 3)
                        break;
                    if (String.valueOf(rw.getCell(0)) == "") {
                        count++;
                    } else {

                        tableRow = table.createRow(20);
                        for (int i = 0; i < 5; i++) {
                            tcell = tableRow.createCell(widthArray[i], String.valueOf(rw.getCell(i)));
                            tcell.setFontSize(13);
                            tcell.setAlign(HorizontalAlignment.CENTER);
                            tcell.setValign(VerticalAlignment.MIDDLE);
                            tcell.setFillColor((rowCount % 2) == 0 ? blue2 : blue3);
                            tcell.setFont(fontMono);
                            tcell.setBorderStyle(lnsWhiteNoB);
                        }
                    }
                    rowCount++;
                }
            }


            table.draw();
            cos.close();

            try {
                fos = new FileOutputStream(new File(dlPath));
                document.save(fos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dlPath;
    }
    public String multiPDF(List<String> paths,String fileName){
        String dlPath = null;
        if(fileName.isEmpty())
            dlPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+this.getEpoch()+".pdf";
        else
            dlPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+fileName+".pdf";

        PDFMergerUtility PDFMerger = new PDFMergerUtility();

        PDFMerger.setDestinationFileName(dlPath);


        try{
            for(String fileItem : paths){
                PDFMerger.addSource(fileItem);
            }
            PDFMerger.mergeDocuments();

            }catch (Exception e){e.printStackTrace();}
        return dlPath;

    }
}