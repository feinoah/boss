package com.eeepay.boss.service;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.shiro.SecurityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.md5.Md5;

/**
 *  描述：手工入账
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */

public class PurseTransService {
  private static final Logger log = LoggerFactory.getLogger(PurseTransService.class);
  private static final int CONN_TIMEOUT=10000;
  private static final int TIMEOUT=40000;
  
  public static final String BAG_HMAC = "2AAB2CE21BDA125093DA45BCDEAE011B";
  public static final String URL = "http://192.168.1.175:5880/bag/apiRecharge.do?";
//  public static final String URL = "http://115.28.36.50:9280/bag/apiRecharge.do?";
  
  private PurseService purseService;
  private Map<String, String> params;


  /**
   * @param purseService
   * @param map
   */
  public PurseTransService(PurseService purseService, Map<String, String> params) {
    this.purseService = purseService;
    this.params = params;
  }


  /**
   * 
   * 功能：钱包余额入账
   *
   * @param params
   * @return
   */
  public String purseTrans() {
    log.info("钱包入账--入账开始");
    String msg="";
    String settleDays=params.get("settleDays").trim();
    String id=params.get("id").trim();
    String transId = params.get("transId").trim();
    String mobileNo = params.get("mobileNo").trim();
    String amount = params.get("amount").trim();
    String orderNo = params.get("orderNo").trim();
    String cardNo = params.get("cardNo").trim();
    String merchantNo = params.get("merchantNo").trim();
    String merchantName = params.get("merchantName").trim();
    String appType = params.get("appType").toString().trim();
    String newHmac = Md5.md5Str(transId+mobileNo + amount + cardNo + orderNo+BAG_HMAC);
    
    Document doc=null;
    String handNum=params.get("handNum");
    Map<String, String> map=new HashMap<String, String>();
    map.put("id", id);
    String hander=((BossUser) SecurityUtils.getSubject().getSession().getAttribute("user")).getRealName();
    
    StringBuffer url = new StringBuffer(URL);
    try {
      url.append("transId=").append(transId);
      url.append("&mobileNo=").append(mobileNo);
      url.append("&settleDays=").append(settleDays);
      url.append("&amount=").append(amount);
      url.append("&orderNo=").append(orderNo);
      url.append("&cardNo=").append(cardNo);
      url.append("&merchantNo=").append(merchantNo);
      url.append("&merchantName=").append(URLEncoder.encode(merchantName, "UTF-8"));
      url.append("&appType=").append(appType);
      url.append("&hmac=").append(newHmac);

      String finalUrl = url.toString();
      HttpClient client = new HttpClient();
      HttpMethod method = new GetMethod(finalUrl);
      client.getHttpConnectionManager().getParams().setConnectionTimeout(CONN_TIMEOUT);     
      client.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT);  
      client.executeMethod(method);
      
      log.info("钱包入账--发送入账通讯,超时"+TIMEOUT+"毫秒，URL:"+finalUrl);
      String responseXML = method.getResponseBodyAsString();
      log.info("钱包入账--入账接口返回报文："+responseXML);
      doc=DocumentHelper.parseText(responseXML);
    }catch (DocumentException e1) {
        e1.printStackTrace();
        map.put("handStatus", "3");
        map.put("hander", hander);
        map.put("handErrCode", "XML_PARSE_ERROR");
        String errMsg=e1.getMessage();
        map.put("handErrMsg", errMsg.length()>120?errMsg.substring(0, 120):errMsg);
        purseService.updatePurseTrans(map);
        log.info("钱包手工入账失败："+errMsg);
        msg="钱包手工入账失败";
        return msg;
    } catch (SocketTimeoutException e2) {
      e2.printStackTrace();
      map.put("handStatus", "4");
      map.put("hander", hander);
      map.put("handErrCode", "TIME_OUT");
      String errMsg=e2.getMessage();
      map.put("handErrMsg", errMsg.length()>120?errMsg.substring(0, 120):errMsg);
      purseService.updatePurseTrans(map);
      log.info("钱包手工入账超时："+errMsg);
      msg="钱包手工入账超时";
      return msg;
    } catch (Exception e3) {
      e3.printStackTrace();
      map.put("handStatus", "3");
      map.put("hander", hander);
      map.put("handErrCode", "ERROR");
      String errMsg=e3.getMessage().length()>120?e3.getMessage().substring(0, 120):e3.getMessage();
      map.put("handErrMsg", errMsg);
      purseService.updatePurseTrans(map);
      log.info("钱包手工入账失败："+errMsg);
      msg="钱包手工入账失败";
      return msg;
    }
    Element xmlRoot = doc.getRootElement(); 
    String errCode=xmlRoot.elementTextTrim("success");
    String repeat=xmlRoot.elementTextTrim("repeat");
    String errMsg=xmlRoot.elementTextTrim("msg");
    
    String handStatus="";
    if ("true".equals(errCode)) {
      handStatus="2";
      map.put("status", "4");
      msg="钱包手工入账成功";
    }else {
      //此流水已经成功入账，不能重复入账
      if (!StringUtil.isBlank(repeat)&&"repeat".equals(repeat)) {
        //未进行过手工入账，此种情况为：自动入账超时
        if (Integer.parseInt(handNum)==0) {
          msg="原入账交易已成功";
          map.put("status", "1");
          map.put("errCode", "true");
          map.put("errMsg", msg);
        }else {//已进行过手工入账，此种情况为：手工入账超时
          msg="原入账交易已成功";
          map.put("status", "4");
          map.put("handStatus", "2");
          map.put("hander", hander);
          map.put("handErrCode", "true");
          map.put("handErrMsg", msg);
        }
        purseService.updatePurseTrans(map);
        log.info("钱包入账结束--"+msg);
        return msg;
      }
      handStatus="3";
      msg="入账失败:"+errMsg;
    }
   
    map.put("handStatus", handStatus);
    map.put("hander", hander);
    map.put("handErrCode", errCode);
    map.put("handErrMsg", msg);
    purseService.updatePurseTrans(map);
    
    log.info("钱包入账结束--"+msg);
    return msg;
  }



	
}
