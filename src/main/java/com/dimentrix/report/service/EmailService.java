package com.dimentrix.report.service;

import com.dimentrix.report.model.FileInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class EmailService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private static String tokenUrl;
    private static String scopesUrl;
    private static String clientId;
    private static String clientSecrete;
    private static String username;
    private static String password;
    private static String host;
    private static String port;
    private static String sslEnable;
    private static String saslEnable;
    private static String saslMechanisms;
    private static String authLoginDisable;
    private static String authPlainDisable;
    private static String sslFactroy;
    private static String startTlsEnable;
    private static String socketFactory;



    @Autowired
    private Environment env;

    SearchTerm searchCondition =new SearchTerm() {
        @Override
        public boolean match(Message message) {
            try {
                Address[] from = message.getFrom();
                String str = env.getProperty("app.settings.senderDomainList");
                String[] arrOfStr = str.split(",");
                String email = ((InternetAddress) from[0]).getAddress();
                System.out.println("Checking email   "+email);
                String domain = email.split("@")[1];
                for (String a : arrOfStr) {
                    if (a.equalsIgnoreCase(domain)) {
                        System.out.println("Matched email   " + email);
                        return true;
                    }
                }
            }catch (MessagingException ex){
                ex.printStackTrace();
            }
            return false;
        }
    };

    SimpleDateFormat sdf = null;
    private Date currDate = null;
    private Date prevDate = null;
    String logsPath= System.getProperty("user.home")+"/.smartShipReports/emailServiceLogs.properties";

    private void getLogs(){
        Properties props = new Properties();
        File file = new File(logsPath);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            currDate = sdf.parse(sdf.format(new Date()));
//            currDate = sdf.parse("2023-02-27 15:49:15");

            if(file.exists()){
                props.load(new FileInputStream(file));
                prevDate = sdf.parse(props.getProperty("lastRun"));
//                prevDate = sdf.parse("2023-02-15 15:49:15");
            }
            else{
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(System.currentTimeMillis()));
                calendar.add(Calendar.DAY_OF_YEAR, -1);

                props.put("lastRun", sdf.format(calendar.getTime()));
                props.store(new FileOutputStream(logsPath),"" );
                prevDate = sdf.parse(props.getProperty("lastRun"));
//                prevDate = sdf.parse("2024-01-25 15:49:15");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public List<FileInfo> fetchEmails() {
        SearchTerm olderThan;
        SearchTerm newerThan;
        SearchTerm dateCondition = null;
        SearchTerm combinedCondition = null;
        List<FileInfo> files = new ArrayList<FileInfo>(10);
        this.getLogs();
        try{
            olderThan = new ReceivedDateTerm(ComparisonTerm.LT, currDate);
            newerThan = new ReceivedDateTerm(ComparisonTerm.GT, prevDate);
            dateCondition = new AndTerm(olderThan,newerThan);
            combinedCondition = new AndTerm(dateCondition,searchCondition);
            //combinedCondition = searchCondition;
        }catch (Exception e){
            e.printStackTrace();
        }

        Properties logs = new Properties();

        //read properties
        tokenUrl=env.getProperty("mail.imap.token_url");
        scopesUrl=env.getProperty("mail.imap.scopes_url");
        clientId=env.getProperty("mail.imap.clientid");
        clientSecrete=env.getProperty("mail.imap.client_secrete");
        username=env.getProperty("mail.user");
        password=env.getProperty("mail.password");
        host=env.getProperty("mail.imap.host");
        port=env.getProperty("mail.imap.port");

        sslFactroy=env.getProperty("mail.imap.ssl_factory");
        sslEnable=env.getProperty("mail.imap.ssl_enable");
        saslEnable=env.getProperty("mail.imap.sasl_enable");
        saslMechanisms=env.getProperty("mail.imap.sasl.mechanisms");
        authLoginDisable=env.getProperty("mail.imap.auth.login.disable");
        authPlainDisable=env.getProperty("mail.imap.auth.plain.disable");
        socketFactory=env.getProperty("mail.imap.socket_factory.fallback");
        startTlsEnable=env.getProperty("mail.imap.starttls.enable");

        try{
//            old code
//            sessionProps.put("mail.smtp.auth",env.getProperty("mail.smtp.auth"));
//            sessionProps.put("mail.smtp.starttls.enable",env.getProperty("mail.smtp.starttls.enable"));
//            sessionProps.put("mail.smtp.host",env.getProperty("mail.smtp.host"));
//            sessionProps.put("mail.smtp.port",env.getProperty("mail.smtp.port"));
//            sessionProps.put("mail.user",env.getProperty("mail.user"));
//            sessionProps.put("mail.password",env.getProperty("mail.password"));

            //atul code - outlook access
            String accessToken = getAccessTokenByClientCredentialGrant();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("access token value is:"+accessToken);
            }

            Properties props= new Properties();

            props.put("mail.imaps.ssl.enable", sslEnable);
            props.put("mail.imaps.sasl.enable", saslEnable);
            props.put("mail.imaps.port", port);
            props.put("mail.imaps.sasl.mechanisms", saslMechanisms);
            props.put("mail.imaps.auth.login.disable", authLoginDisable);
            props.put("mail.imaps.auth.plain.disable", authPlainDisable);
            props.setProperty("mail.imaps.socketFactory.class", sslFactroy);
            props.setProperty("mail.imaps.socketFactory.fallback", socketFactory);
            props.setProperty("mail.imaps.socketFactory.port", port);
            props.setProperty("mail.imaps.starttls.enable", startTlsEnable);
//	        props.put("mail.debug", "true");
//          props.put("mail.imaps.auth.mechanisms", "XOAUTH2");
//	        props.put("mail.debug.auth", "true");

            Session session = Session.getInstance(props);

            Store store = session.getStore("imaps");

            logs.load(new FileInputStream(logsPath));
            store.connect(host,username,accessToken);

            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Searching For Attachments In "+
                        inbox.getMessageCount()+
                        " Emails Where Date is Between '"
                        +prevDate+"' And '"+currDate);
            }

            Message[] foundMessages = inbox.search(dateCondition);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(foundMessages.length + " Emails Matched the search criteria ");
            }
            int count =0;
            String fileName;
            if(foundMessages.length>0){
                for (Message message : foundMessages) {
                    Object content = message.getContent();
                    if (!(content instanceof Multipart)) {
                        if (LOGGER.isInfoEnabled()) {
                            Address[] from = message.getFrom();
                            String email = ((InternetAddress) from[0]).getAddress();

                            LOGGER.info("Email Skipped :: Start");
                            LOGGER.info(
                                    "\nFrom : " + email + "\n" +
                                    "Received Date : " + message.getReceivedDate() + "\n" +
                                    "Subject : " + message.getSubject() + "\n" +
                                    "Content Type : " + message.getContentType()
                            );
                            LOGGER.info("Email Skipped :: End");
                        }

                        continue;
                    }

                    Multipart multipart = (Multipart) content;
                        //Multipart multipart = (Multipart) message.getContent();
                    Date receivedDate = message.getReceivedDate();
                    for (int j = 0; j < multipart.getCount(); j++) {
                             MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(j);
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            //atul changes
                            String fileNameToSave = bodyPart.getFileName();
                            if (fileNameToSave != null) {
                                String[] ext = fileNameToSave.split("[.]");

                                if (ext[ext.length - 1].equalsIgnoreCase("xlsx") ||
                                        ext[ext.length - 1].equalsIgnoreCase("xls")) {
                                    fileName = getEpoch() + "_" + bodyPart.getFileName();
                                    bodyPart.saveFile(generateSavePath() + fileName);
                                    if (LOGGER.isInfoEnabled()) {
                                        LOGGER.info(bodyPart.getFileName() + " File Saved :: Subject-> " + message.getSubject());
                                    }

                                    FileInfo fileInfo = new FileInfo(fileName, count, receivedDate);
                                    files.add(fileInfo);
                                    count++;
                                } else {
                                    LOGGER.info("File format is other than xlsx/xls : " + fileNameToSave);
                                }
                            } else {
                                LOGGER.info("File name receieved is null  : " + fileNameToSave);
                            }
                        }
                         }
                     }
                }
            if(count>0){
                logs.put("lastRun",sdf.format(currDate));
                logs.store(new FileOutputStream(logsPath),"");
            }

            inbox.close(true);
            store.close();

        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Error Occurred : " + e.getMessage());
            }
        }
        return  files;
    }

    public String generateSavePath(){
        SimpleDateFormat sdf=new SimpleDateFormat("MM-dd-yyyy");
        String path = System.getProperty("user.home")+"/.smartShipReports/"+sdf.format(new Date())+"/";
        File directory = new File(path);
        if(!directory.exists())
            directory.mkdir();
        return path;
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

    //atul changes to access outlook 365
    private static String getAccessTokenByClientCredentialGrant() {

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost loginPost =  new HttpPost(tokenUrl);
            String encodedBody = "client_id=" + clientId
                    + "&scope=" + scopesUrl
                    + "&client_secret=" + clientSecrete
                    + "&username=" + username
                    + "&password=" + password
                    + "&grant_type=password";

            loginPost.setEntity(new StringEntity(encodedBody, ContentType.APPLICATION_FORM_URLENCODED));

            loginPost.addHeader(new BasicHeader("cache-control", "no-cache"));
            CloseableHttpResponse loginResponse = client.execute(loginPost);
            byte[] response = loginResponse.getEntity().getContent().readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType type = objectMapper.constructType(objectMapper.getTypeFactory()
                    .constructParametricType(Map.class, String.class, String.class));
            Map<String, String> parsed = new ObjectMapper().readValue(response, type);
            return parsed.get("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}