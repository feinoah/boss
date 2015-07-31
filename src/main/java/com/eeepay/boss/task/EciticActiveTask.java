package com.eeepay.boss.task;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.service.EciticPayService;

/**
 *  描述：中信银企直连心跳激活
 *  
 *  @author:ym
 *  创建时间：2015-03-13
 */
@Component("EciticActiveTask")
public class EciticActiveTask {
	private static final Logger log = LoggerFactory.getLogger(EciticActiveTask.class);
  
  @Resource
  private EciticPayService eciticPayService;
	 
  @Scheduled(cron="0 0/15 * * * ?")
	public void execute(){
	  String xml="<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n" + 
	      "<stream>\r\n" + 
	      "<action>DLCIDSTT</action>\r\n" + 
	      "<userName>"+eciticPayService.ECITIC_BANK_USER_NAME+"</userName>\r\n" + 
	      "<clientID>0123456789</clientID>\r\n" + 
	      "<type/>\r\n" + 
	      "</stream>";
	  String returnXML="";
	  try {
	    returnXML=this.httpPostXML(xml);
      log.info("中信银行心跳正常："+returnXML);
    } catch (Exception e) {
      log.info("中信银行心跳异常："+e.getMessage());
    }
	 
	}
  
  private String httpPostXML(String xml)throws SocketTimeoutException,Exception {
    HttpURLConnection httpConn  =  null;
    try{
      URL postUrl = new URL(eciticPayService.ECITIC_URL);
      // 打开连接   
      httpConn = (HttpURLConnection) postUrl.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setRequestMethod("POST");
      httpConn.setUseCaches(false);
      httpConn.setInstanceFollowRedirects(true);
      httpConn.setRequestProperty("Content-Type", "application/octet-stream; charset=utf-8");
      httpConn.setConnectTimeout(eciticPayService.CONN_TIME_OUT);
      httpConn.setReadTimeout(eciticPayService.TIME_OUT);
      
      DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
      out.write(xml.getBytes("GB2312"));
      out.flush();
      out.close();
      int status = httpConn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        return "";
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(   
          httpConn.getInputStream(),"GB2312"));
      StringBuffer responseSb =new StringBuffer();
      String line = null;
      while ((line = reader.readLine()) != null) {   
        responseSb.append(line.trim());
      }
      reader.close();
      return responseSb.toString().trim();
    } catch (SocketTimeoutException e) {
      throw e;
    }catch(Exception ex){
      throw ex;
    }finally{
      httpConn.disconnect();   
    }
    
  }
	
	
}
 