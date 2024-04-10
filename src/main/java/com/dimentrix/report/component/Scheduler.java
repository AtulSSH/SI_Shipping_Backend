package com.dimentrix.report.component;

import com.dimentrix.report.model.ShipMetaData;
import com.dimentrix.report.service.EmailService;
import com.dimentrix.report.service.ExcelParserService;
import com.dimentrix.report.service.IShipMetaDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class Scheduler {
    @Autowired
    EmailService emailService;

    @Autowired
    ExcelParserService excelParserService;

    @Autowired
    IShipMetaDataService shipMetaDataService;

    @Autowired
    private Environment env;



    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private String getTime(){
        return sdf.format(new Date().getTime());
    }

    @Scheduled(fixedRateString = "${app.settings.emailFetchFixedTime}")
    public void emailFetchTask(){

        List parsedData;
        LOGGER.info("Scheduled Job - Save Email Attachments Started :: " + getTime());
        if(!env.getProperty("mail.user").isEmpty() && !env.getProperty("mail.password").isEmpty()){
            parsedData = excelParserService.getFiles(emailService.fetchEmails());
            if(parsedData.size()>0){
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Parsed and Stored " + parsedData.size() + " Ship MetaData Records In DB...");
                }
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("No New Attachments Found!");
                }
            }
        }else{
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Email or Password is not configured. Can't Connect to SMTP");
            }
        }


        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scheduled Job - Save Email Attachments Finished :: " + getTime());
        }
    }

    @Scheduled(fixedRateString = "${app.settings.tempDirCleanupFixedTime}")
    public void cleanTempDir(){
        long diff;
        long time=Integer.parseInt(env.getProperty("app.settings.tempDirFilesExpiry"));
        int count  = 0;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scheduled Job - Temp Directory Cleanup Started :: " + getTime());
        }
        File file = new File(System.getProperty("user.home")+"/.smartShipReports/.temp/");
        if(file.isDirectory()){
            for(File f : file.listFiles()){
                diff = new Date().getTime() - f.lastModified();
                if(diff > time){
                    f.delete();
                    count++;
                }
            }
            if(count>0) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(count + " Expired Files Deleted from Temp Directory");
                }
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scheduled Job - Temp Directory Cleanup Finished :: " + getTime());
        }
    }
}