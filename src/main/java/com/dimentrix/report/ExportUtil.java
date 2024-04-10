package com.dimentrix.report;

import com.dimentrix.report.model.ShipDetailedData;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.List;

public class ExportUtil {

    private static final String COLOR_STRING = "d3d3d3";

    public static void createHeader(XWPFDocument docx, XWPFHeaderFooterPolicy policy) {
        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "Seven Islands Shipping Limited";
        ctHeader.setStringValue(headerText);
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, docx);
        XWPFParagraph[] parsHeader = new XWPFParagraph[1];
        parsHeader[0] = headerParagraph;
        headerParagraph.setAlignment(ParagraphAlignment.LEFT);
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
    }


    public static void createFooter(XWPFDocument document, XWPFHeaderFooterPolicy policy) {
        //write footer content
        CTP ctpFooter = CTP.Factory.newInstance();
        CTR ctrFooter = ctpFooter.addNewR();
        CTText ctFooter = ctrFooter.addNewT();
        String footerText = "PSC Report";
        ctFooter.setStringValue(footerText);
        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
        XWPFParagraph[] parsFooter = new XWPFParagraph[1];
        parsFooter[0] = footerParagraph;
        footerParagraph.setAlignment(ParagraphAlignment.RIGHT);
        policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);
    }

    public static void createTable(XWPFDocument document, XWPFHeaderFooterPolicy policy, List<ShipDetailedData> detailedData) {
        //create table
        XWPFTable table = document.createTable();


        CTTbl ctable        = table.getCTTbl();
        CTTblPr pr         = ctable.getTblPr();
        CTTblWidth  tblW = pr.getTblW();
        tblW.setW(BigInteger.valueOf(5000));
        tblW.setType(STTblWidth.PCT);
        pr.setTblW(tblW);
        ctable.setTblPr(pr);

        CTJc jc = pr.addNewJc();
        jc.setVal(STJc.RIGHT);
        pr.setJc(jc);

        int sr = 1;
        int twipsPerInch =  1440;
        XWPFTableRow tableRowOne = table.getRow(0);
//        tableRowOne.setHeight(500);
//        tableRowOne.getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT); //set w:hRule="exact"

        XWPFTableCell cell = tableRowOne.getCell(0);
        cell.setText("Sr");
        cell.getCTTc().addNewTcPr().addNewShd().setFill(COLOR_STRING);
        cell.setWidth("30");
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);


        XWPFTableCell desCell = tableRowOne.addNewTableCell();
        desCell.setText("Description of Defect/Concern");
        desCell.setWidth("200");
        desCell.setColor(COLOR_STRING);
        desCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        XWPFTableCell dateCell =  tableRowOne.addNewTableCell();
        dateCell.setText("Tar. Date");
        dateCell.setWidth("80");
        dateCell.setColor(COLOR_STRING);
        dateCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);


        XWPFTableCell statusCell = tableRowOne.addNewTableCell();
        statusCell.setText("Status");
        statusCell.setWidth("40");
        statusCell.setColor(COLOR_STRING);
        statusCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        for (ShipDetailedData sdd : detailedData) {
            String status;
            if (sdd.getStatus().equalsIgnoreCase("open"))
                status = "O";
            else if (sdd.getStatus().equalsIgnoreCase("closed"))
                status = "C";
            else if (sdd.getStatus().equalsIgnoreCase("in progress"))
                status = "INP";
            else {
                status = "NA";
                continue;
            }
            XWPFTableRow tableRowThree = table.createRow();
            XWPFTableCell srCell = tableRowThree.getCell(0);
            srCell.setWidth("30");
            srCell.setText(String.valueOf(sr++));
            table.setTableAlignment(TableRowAlign.CENTER);
            srCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            XWPFTableCell dCell =  tableRowThree.getCell(1);
            dCell.setText(sdd.getDescription() == null ? "" : sdd.getDescription());
            dCell.setWidth("200");
            dCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            XWPFTableCell tCell =  tableRowThree.getCell(2);
            tCell.setText(sdd.getTargetDate() == null ? "" : sdd.getTargetDate());
            tCell.setWidth("80");
            tCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);


            XWPFTableCell statusC = tableRowThree.getCell(3);
            statusC.setWidthType(TableWidthType.DXA);
            statusC.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            statusC.setText(status);
            statusC.setWidth("40");
        }

        document.createParagraph().createRun().addBreak();

    }
}
