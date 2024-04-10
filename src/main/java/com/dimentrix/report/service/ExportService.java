package com.dimentrix.report.service;

import be.quodlibet.boxable.*;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.PDStreamUtils;
import com.dimentrix.report.ExportUtil;
import com.dimentrix.report.model.ShipDetailedData;
import com.dimentrix.report.model.ShipMetaData;
import com.dimentrix.report.repository.IShipDetailedDataRepository;
import com.dimentrix.report.repository.IShipMetaDataRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExportService implements IExportService {

    @Autowired
    IShipMetaDataRepository shipMetaDataRepository;

    @Autowired
    IShipDetailedDataRepository shipDetailedDataRepository;

    @Override
    public ResponseEntity<Object> GeneratePDF(Map body) {

        ResponseEntity re = null;
        String startDate, endDate = null;

        if(body.get("startDate")!=null && body.get("endDate") != null){
            startDate = body.get("startDate").toString();
            endDate = body.get("endDate").toString();
        } else {
            return re;
        }

        List<Integer> ids = (List<Integer>) body.get("id");
        List<String> status1 = (List<String>) body.get("status");

        if(ids != null && ids.size()>0){
            InputStreamResource isr = null;

            HttpHeaders headers = new HttpHeaders();

            MediaType mt =  MediaType.APPLICATION_PDF;

            isr = generatePDF("PSC Report", startDate, endDate, ids,status1);

            if(isr != null){

                headers.add("Access-Control-Expose-Headers","*");
                headers.add("Content-Disposition", "attachment; filename=Report.pdf");
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");

                re = ResponseEntity.ok()
                        .headers(headers)
                        .contentType(mt)
                        .body(isr);
            }
            else{
                re = ResponseEntity.ok()
                        .body("Error");
            }


        }

        return re;

    }

    private InputStreamResource generatePDF(String reportType,String startDate, String endDate,List<Integer> ids,List<String> selectedStatus){

       List<ShipMetaData> shipMetaData = shipMetaDataRepository.findAllByIdsAndDateRange(ids,startDate,endDate);
        InputStreamResource resource = null;

        if(shipMetaData.size()>0){

            FileOutputStream fos;
            String dlPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+getEpoch()+".pdf";


            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat stringFormatter = new SimpleDateFormat("dd-MMM-yyyy");
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;

                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);

                float margin = 20;
                float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
                float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                boolean drawContent = true;
                float yStart = yStartNewPage;
                float bottomMargin = 70;
                float yPosition = 820;
                Color grey = new Color(217,217,217);

                int[] widthArray = new int[]{5, 77, 10, 8};
                String[] headingsArray = new String[]{"Sr.","Description of Defect/Concern","Tar. Date","Status"};

                document.addPage(page);

                PDDocumentInformation pdd = document.getDocumentInformation();
                pdd.setAuthor("SmartShip");
                pdd.setTitle("SmartShip Report");
                pdd.setCreator("SmartShip");
                pdd.setSubject("SmartShip Report");
                Calendar date = new GregorianCalendar();
                pdd.setCreationDate(date);

                PDPageContentStream cos = new PDPageContentStream(document, page);

                BaseTable table = new BaseTable(yPosition, yStartNewPage,
                        bottomMargin, tableWidth, margin, document, page, true, drawContent);

                Row<PDPage> tableRow;
                Cell<PDPage> tCell;

//HEADER ->
                tableRow = table.createRow(25);

                tCell = tableRow.createCell(50, "Seven Islands Shipping Limited");

                tCell.setValign(VerticalAlignment.TOP);
                tCell.setAlign(HorizontalAlignment.LEFT);
                tCell.setBottomBorderStyle(new LineStyle(Color.BLACK,0.5f));
                tCell.setLeftBorderStyle(null);
                tCell.setTopBorderStyle(null);
                tCell.setRightBorderStyle(null);

                tCell = tableRow.createCell(50,"PSC-FSI Reporting System");

                tCell.setValign(VerticalAlignment.TOP);
                tCell.setAlign(HorizontalAlignment.RIGHT);
                tCell.setBottomBorderStyle(new LineStyle(Color.BLACK,0.5f));
                tCell.setLeftBorderStyle(null);
                tCell.setTopBorderStyle(null);
                tCell.setRightBorderStyle(null);

                table.addHeaderRow(tableRow);

//HEADING ->
                 tableRow = table.createRow(25);

                tCell = tableRow.createCell(100,reportType);

                tCell.setFontSize(11);
                tCell.setFontBold(fontBold);
                tCell.setValign(VerticalAlignment.TOP);
                tCell.setBorderStyle(null);
                tCell.setAlign(HorizontalAlignment.CENTER);

// TO FROM DATES ->

                tableRow = table.createRow(10);

                Date startdD= dateFormatter.parse(startDate);
                String startDateFormatted = stringFormatter.format(startdD);
                tCell = tableRow.createCell(50,"From date: "+startDateFormatted);


                tCell.setValign(VerticalAlignment.MIDDLE);
                tCell.setAlign(HorizontalAlignment.LEFT);
                tCell.setBorderStyle(null);
                tCell.setFontBold(PDType1Font.ZAPF_DINGBATS);
                tCell.setFontSize(8);
                tCell.setLeftPadding(20);

                tableRow = table.createRow(10);
                Date ed = dateFormatter.parse(endDate);
                String endDateFormatted = stringFormatter.format(ed);
                tCell = tableRow.createCell(50,"To date:     "+endDateFormatted);

                tCell.setValign(VerticalAlignment.MIDDLE);
                tCell.setAlign(HorizontalAlignment.LEFT);
                tCell.setBorderStyle(null);
                tCell.setFontBold(PDType1Font.ZAPF_DINGBATS);
                tCell.setFontSize(8);
                tCell.setLeftPadding(20);




//VESSEL ->

                tableRow = table.createRow(25);
                String name = shipMetaData.stream().map(ShipMetaData::getVesselName).distinct().collect(Collectors.joining(", "));
                tCell = tableRow.createCell(100,"Vessel: "+(ids.size() < 19 ? name:"All"));

                tCell.setValign(VerticalAlignment.MIDDLE);
                tCell.setAlign(HorizontalAlignment.LEFT);
                tCell.setFontBold(PDType1Font.ZAPF_DINGBATS);
                tCell.setFontSize(8);
                tCell.setLeftPadding(20);
                tCell.setBottomBorderStyle(new LineStyle(Color.BLACK,1));
                tCell.setLeftBorderStyle(null);
                tCell.setTopBorderStyle(null);
                tCell.setRightBorderStyle(null);

//DYNAMIC TABLE ->

               for(ShipMetaData sd: shipMetaData){
                    List<ShipDetailedData> detailedData;
                    detailedData = shipDetailedDataRepository.findAllByShipMetaIdAndStatusIn(sd.getId(),selectedStatus);
                    tableRow = table.createRow(20);

                    tCell = tableRow.createCell(100,sd.getVesselName());

                    tCell.setTopPadding(20);
                    tCell.setValign(VerticalAlignment.BOTTOM);
                    tCell.setAlign(HorizontalAlignment.CENTER);
                    tCell.setFontBold(PDType1Font.COURIER_BOLD);
                    tCell.setFontSize(12);
                    tCell.setBorderStyle(null);

                    tableRow = table.createRow(5);

                    tCell = tableRow.createCell(100,"Date: "+stringFormatter.format(sd.getReportDate()));

                    tCell.setValign(VerticalAlignment.TOP);
                    tCell.setAlign(HorizontalAlignment.LEFT);
                    tCell.setFontBold(PDType1Font.ZAPF_DINGBATS);
                    tCell.setFontSize(8);
                    tCell.setLeftBorderStyle(null);
                    tCell.setRightBorderStyle(null);
                    tCell.setTopBorderStyle(null);
                    tCell.setBottomBorderStyle(new LineStyle(Color.BLACK,1));

                    tableRow = table.createRow(20);

                    for(int i=0;i<4;i++){
                        tCell = tableRow.createCell(widthArray[i],headingsArray[i]);
                        tCell.setValign(VerticalAlignment.MIDDLE);
                        tCell.setAlign(HorizontalAlignment.LEFT);
                        tCell.setFillColor(grey);
                    }


                    int index = 1;

                    for(ShipDetailedData sdd : detailedData){
                        String status;
                        if(sdd.getStatus().equalsIgnoreCase("open"))
                            status= "O";
                        else if(sdd.getStatus().equalsIgnoreCase("closed"))
                            status = "C";
                        else if(sdd.getStatus().equalsIgnoreCase("in progress"))
                            status = "INP";
                        else {
                            status = "NA";
                            continue;
                        }

                        tableRow = table.createRow(20);

                        tCell = tableRow.createCell(5, String.valueOf(index));
                        tCell.setValign(VerticalAlignment.MIDDLE);
                        tCell.setAlign(HorizontalAlignment.CENTER);

                        tCell = tableRow.createCell(77,sdd.getDescription());
                        tCell.setValign(VerticalAlignment.MIDDLE);
                        tCell.setAlign(HorizontalAlignment.LEFT);

                        tCell = tableRow.createCell(10,sdd.getTargetDate());
                        tCell.setValign(VerticalAlignment.MIDDLE);
                        tCell.setAlign(HorizontalAlignment.CENTER);

                        tCell = tableRow.createCell(8,status);
                        tCell.setValign(VerticalAlignment.MIDDLE);
                        tCell.setAlign(HorizontalAlignment.CENTER);

                        index ++;
                    }
                }

                table.draw();
                addFooter(document, reportType);
                cos.close();

                try {
                    fos = new FileOutputStream(new File(dlPath));
                    document.save(fos);
                    resource = new InputStreamResource(new FileInputStream(new File(dlPath)));
                } catch (Exception e) { e.printStackTrace(); }
                document.close();
            }catch (Exception e){ e.printStackTrace(); }
        }

        return resource;
    }

    private void addFooter(PDDocument document, String reportType)throws IOException{
        int pageno =1;
        int numberOfPages = document.getNumberOfPages();
        for (int i = 0; i < numberOfPages; i++) {
            PDPage fpage = document.getPage(i);
            PDPageContentStream contentStream = new PDPageContentStream(document, fpage, PDPageContentStream.AppendMode.APPEND, true);

            PDStreamUtils.write(contentStream, reportType,
                    PDType1Font.HELVETICA, 8, 500, 25, Color.BLACK);
            PDStreamUtils.write(contentStream, String.valueOf(pageno),
                    PDType1Font.HELVETICA, 10, 280, 25, Color.BLACK);
            contentStream.close();
            pageno++;

        }
    }

    private String getEpoch() {
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
    public ResponseEntity<Object> generateDocx(Map body) {
        ResponseEntity re = null;
        String startDate, endDate = null;

        if(body.get("startDate")!=null && body.get("endDate") != null){
            startDate = body.get("startDate").toString();
            endDate = body.get("endDate").toString();
        } else {
            return re;
        }

        List<Integer> ids = (List<Integer>) body.get("id");
        List<String> status1 = (List<String>) body.get("status");

        if(ids != null && ids.size()>0){
            InputStreamResource isr = null;

            HttpHeaders headers = new HttpHeaders();

            MediaType mt =  MediaType.asMediaType(MimeType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));

            isr = generateDocx("PSC Report", startDate, endDate, ids,status1);

            if(isr != null){

                headers.add("Access-Control-Expose-Headers","*");
                headers.add("Content-Disposition", "attachment; filename=Report.docx");
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");

                re = ResponseEntity.ok()
                        .headers(headers)
                        .contentType(mt)
                        .body(isr);
            }
            else{
                re = ResponseEntity.ok()
                        .body("Error");
            }


        }

        return re;
    }

    private InputStreamResource generateDocx(String reportType,String startDate, String endDate,List<Integer> ids,List<String> selectedStatus){

        List<ShipMetaData> shipMetaData = shipMetaDataRepository.findAllByIdsAndDateRange(ids,startDate,endDate);
        InputStreamResource resource = null;

        if(shipMetaData.size()>0){

            FileOutputStream fos;
            String dlPath = System.getProperty("user.home")+"/.smartShipReports/.temp/"+getEpoch()+".docx";

            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat stringFormatter = new SimpleDateFormat("dd-MMM-yyyy");
                XWPFDocument document = new XWPFDocument();

                CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
                XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

                ExportUtil.createHeader(document,policy);
                ExportUtil.createFooter(document,policy);


                XWPFParagraph p1 = document.createParagraph();
                p1.setAlignment(ParagraphAlignment.CENTER);

                p1.setBorderTop(Borders.BASIC_BLACK_DASHES);


                XWPFRun r1 = p1.createRun();
                r1.setText("PSC Report");
                r1.setBold(true);
                r1.addCarriageReturn();

                XWPFParagraph paragraph = document.createParagraph();

                //Set bottom border to paragraph
                paragraph.setBorderTop(Borders.BASIC_BLACK_DASHES);
                paragraph.createRun().addBreak();

                Date startdD= dateFormatter.parse(startDate);
                String startDateFormatted = stringFormatter.format(startdD);
                Date ed = dateFormatter.parse(endDate);
                String endDateFormatted = stringFormatter.format(ed);


                XWPFParagraph p2 = document.createParagraph();
                //Set bottom border to paragraph
                p2.setBorderTop(Borders.BASIC_BLACK_DASHES);
                XWPFRun c2 = paragraph.createRun();
                c2.setText("From date: "+startDateFormatted);
                c2.addBreak();
                c2.setText("To date:      "+endDateFormatted);
                c2.addBreak();
                c2.addCarriageReturn();
                String name = shipMetaData.stream().map(ShipMetaData::getVesselName).distinct().collect(Collectors.joining(", "));
                c2.setText("Vessel: "+(ids.size() < 19 ? name:"All"));

                for (ShipMetaData sd : shipMetaData) {
                    List<ShipDetailedData> detailedData = shipDetailedDataRepository.findAllByShipMetaIdAndStatusIn(sd.getId(), selectedStatus);
                    XWPFParagraph bodyParagraph = document.createParagraph();
                    bodyParagraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun r = bodyParagraph.createRun();
                    r.setBold(true);
                    r.setText(sd.getVesselName());
                    r.addBreak();
                    XWPFParagraph datePara = document.createParagraph();
                    datePara.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun dateRun = datePara.createRun();
                    dateRun.setText("Date: "+stringFormatter.format(sd.getReportDate()));
                    ExportUtil.createTable(document, policy, detailedData);
                }

                try {
                    fos = new FileOutputStream(new File(dlPath));

                    document.write(fos);
                    fos.close();
                    document.close();
                    resource = new InputStreamResource(new FileInputStream(new File(dlPath)));
                } catch (Exception e) { e.printStackTrace(); }
                document.close();
            }catch (Exception e){ e.printStackTrace(); }
        }

        return resource;
    }



}
