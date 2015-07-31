package com.eeepay.boss.task;

import com.eeepay.boss.service.AcqMerchantService;
import com.eeepay.boss.service.TransService;
import com.eeepay.boss.utils.StringUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by yuanpeng on 2015/5/26.
 */
@Component
public class AcqMerchantTransCountTask {

    private static final Logger log = LoggerFactory.getLogger(AcqMerchantTransCountTask.class);

    @Resource
    private TransService transService;

    @Resource
    private AcqMerchantService acqMerchantService;

    private String sendFrom;
    private String sendTo;
    private String host;
    private String username;
    private String password;
    private String subject;
    private String content;
    private String acqOrg;
    private Vector<File> attachment = new Vector<File>();

    public void execute(){

        log.info("==================统计收单机构交易开始==================");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        String createTimeBegin = yesterday + " 00:00:00";
        String createTimeEnd = yesterday + " 23:59:59";

        log.info("==================查询交易量开始，时间为:" + yesterday + "==================");
        List<Map<String,Object>> list = transService.getAcqMerchantTransCount(acqOrg, createTimeBegin, createTimeEnd);
        log.info("==================查询交易量结束，总交易商户数：" + (list != null ? list.size() + "" : "") + "==================");

        if(list != null && list.size() > 0){
            generateFile(list, yesterday);

//            subject += yesterday;
//            content += yesterday;

            if(sendMail(yesterday)){
                removeFile();
            }
        }

        log.info("==================统计收单机构交易结束==================");
    }

    private Map<String, String> getAcqOrgMap(){
        Map<String, String> acqOrg = new HashMap<String, String>();
        List<Map<String, Object>> acqOrgs = acqMerchantService.getAcqOrg();

        for(Map<String, Object> map : acqOrgs){
            acqOrg.put(map.get("acq_enname").toString(), map.get("acq_cnname").toString());
        }

        return acqOrg;
    }

    private void removeFile(){
        log.info("==================删除临时文件开始==================");
        for(File file : attachment){
            if(file != null && file.exists())
                log.info("==================删除临时文件" + file.getName() + "==================");
                file.delete();
        }

        attachment.removeAllElements();
        log.info("==================删除临时文件开始==================");
    }

    private void generateFile(List<Map<String,Object>> list, String date){

        log.info("==================生成临时文件开始==================");
        WritableWorkbook wwb = null;
        Workbook wb = null;
        FileOutputStream fos = null;

        Map<String, String> acqOrgMap = getAcqOrgMap();
        try {
            wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/acqMerchantTransCount.xls"));

            fos = new FileOutputStream(checkDirectoryAndCreateFile(date));
            wwb = Workbook.createWorkbook(fos, wb);
            WritableSheet ws = wwb.getSheet(0);

            int index = 1, row = 2, col = 0;
            for(Map<String, Object> map : list){
                ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(index++, "无")));
                ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(acqOrgMap.get(map.get("acq_enname").toString()), "无")));
                ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_no"), "无")));
                ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("merchant_name"), "无")));
                ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("success"), "无")));

                row++;
                col = 0;
            }

            wwb.write();
            log.info("==================生成临时文件结束==================");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("==================生成临时文件异常==================");
        } finally {
            if(wwb != null){
                try {
                    wwb.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(wb != null){
                try {
                    wb.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(fos != null){
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private File checkDirectoryAndCreateFile(String date) throws Exception{
        String fileSavePath = System.getProperty("user.dir") + "/downloads/temp/";

        File file = new File(fileSavePath);

        if(!file.exists() && !file.isDirectory()){
            file.mkdirs();
        }

        int random = (int) (Math.random() * 1000);
        String randomStr = StringUtil.stringFillLeftZero("" + random, 4);

        String fileName = fileSavePath + "交易统计" + date + "_" + randomStr+ ".xls";

        File excel = new File(fileName);

        if(!excel.exists()){
            excel.createNewFile();
        }

        attachment.add(excel);

        return excel;
    }

    public boolean sendMail(String date) {
        log.info("==================发送邮件开始==================");
        // 构造mail session
        Properties props = new Properties() ;
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // 构造MimeMessage 并设定基本的值
            MimeMessage msg = new MimeMessage(session);
            //MimeMessage msg = new MimeMessage();
            msg.setFrom(new InternetAddress(sendFrom));


            //msg.addRecipients(Message.RecipientType.TO, address); //这个只能是给一个人发送email
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(sendTo)) ;
            //subject = transferChinese(subject);
            msg.setSubject(subject + date);

            // 构造Multipart
            Multipart mp = new MimeMultipart();

            // 向Multipart添加正文
            MimeBodyPart mbpContent = new MimeBodyPart();
            mbpContent.setContent(content + date, "text/html;charset=UTF-8");

            // 向MimeMessage添加（Multipart代表正文）
            mp.addBodyPart(mbpContent);
            // 向Multipart添加附件
            Enumeration efile = attachment.elements();
            while (efile.hasMoreElements()) {
                MimeBodyPart mbpFile = new MimeBodyPart();
                String filename = efile.nextElement().toString();
                FileDataSource fds = new FileDataSource(filename);
                mbpFile.setDataHandler(new DataHandler(fds));
                //这个方法可以解决附件乱码问题。</span>
                mbpFile.setFileName(MimeUtility.encodeText(fds.getName()));
                // 向MimeMessage添加（Multipart代表附件）
                mp.addBodyPart(mbpFile);
            }
            //attachment.removeAllElements();
            // 向Multipart添加MimeMessage
            msg.setContent(mp);
            msg.setSentDate(new Date());
            msg.saveChanges() ;
            // 发送邮件
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            log.info("==================发送邮件结束==================");
        } catch (Exception mex) {
            mex.printStackTrace();
            log.info("==================发送邮件异常==================");
            return false;
        }
        return true;
    }

    public String transferChinese(String strText) {
        try {
            strText = MimeUtility.encodeText(new String(strText.getBytes(), "ISO-8859-1"), "UTF-8", strText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strText;
    }

    public String getSendFrom() { return sendFrom; }

    public void setSendFrom(String sendFrom) { this.sendFrom = sendFrom; }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAcqOrg() {
        return acqOrg;
    }

    public void setAcqOrg(String acqOrg) {
        this.acqOrg = acqOrg;
    }

}
