package com.eeepay.boss.service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.shiro.codec.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.md5.Md5;

/**
 *  描述：钱包提现余额冲正
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */

@Service
public class PurseRecService extends Thread {
  private static final Logger log = LoggerFactory.getLogger(PurseRecService.class);
  
  //通讯超时
  private static final int CONN_TIMEOUT=30000;
  //请求超时
  private static final int TIMEOUT=80000;
  
  @Resource
  private PurseService  purseService;
  
  private Map<String, Object> params;
  public Map<String, Object> getParams() {
    return params;
  }
  public void setParams(Map<String, Object> params) {
    this.params = params;
  }


  public PurseRecService(Map<String, Object> params,PurseService purseService) {
    super();
    this.params = params;
    this.purseService=purseService;
  }
  public PurseRecService() {
  }
	
  /**
   * 
   * 功能：钱包余额冲正
   *
   * @param params
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void balanceRec(CloseableHttpClient http,Map<String, Object> params) {
    List<Map<String, Object>> list=(List<Map<String, Object>>)params.get("list");
    String remark=(String)params.get("remark");
    String channel=(String)params.get("channel");
    
    String url=SysConfig.value("purse_rec_url");
    HttpPost httpPost = new HttpPost(url);
    List paramsList = new ArrayList();
    
    StringBuffer xmlBuffer=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlBuffer.append("<returnExtractionList>");
    
    for (Map<String, Object> map : list) {
      String mobileNo=(String)map.get("mobile_no");
      String id=map.get("id").toString();
      String accountNo=(String)map.get("account_no");
      String accountName=(String)map.get("account_name");
      String amount=map.get("amount").toString();
      String settleDays=map.get("settle_days").toString();
      String hmac=Md5.md5Str(mobileNo+accountName+accountNo+amount+settleDays+remark+Constants.BAG_HMAC);
      
      xmlBuffer.append("<returnExtraction>");
      xmlBuffer.append("<channel>").append(channel).append("</channel>");
      xmlBuffer.append("<channelId>").append(id).append("</channelId>");
      xmlBuffer.append("<mobileNo>").append(mobileNo).append("</mobileNo>");
      xmlBuffer.append("<amount>").append(amount).append("</amount>");
      xmlBuffer.append("<accountNo>").append(accountNo).append("</accountNo>");
      xmlBuffer.append("<accountName>").append(accountName).append("</accountName>");
      xmlBuffer.append("<settleDays>").append(settleDays).append("</settleDays>");
      xmlBuffer.append("<remark>").append(remark).append("</remark>");
      xmlBuffer.append("<hmac>").append(hmac).append("</hmac>");
      xmlBuffer.append("</returnExtraction>");
    }
    xmlBuffer.append("</returnExtractionList>");
    
    log.info("xml："+xmlBuffer.toString());
    
    String xmlStr="";
    try {
      xmlStr = URLEncoder.encode(Base64.encodeToString(xmlBuffer.toString().getBytes("UTF-8")), "UTF-8");
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
      log.warn("编码解码失败，余额冲正未发送");
      this.updateBalanceRecSendFaild(list,"2","发送冲正失败,编码解码失败："+e1.getMessage());
    }
    
    
    paramsList.add(new BasicNameValuePair("xmlStr", xmlStr));
    CloseableHttpResponse response=null; 
    StringBuilder sb = new StringBuilder();
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(paramsList));
      RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(CONN_TIMEOUT).build();
      httpPost.setConfig(requestConfig);
      response = http.execute(httpPost);
      
      HttpEntity httpEntity = response.getEntity();  
      if(httpEntity != null){
          httpEntity = new BufferedHttpEntity(httpEntity);  
          InputStream is = httpEntity.getContent();  
          BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
          String str;
          while((str=br.readLine())!=null){
            sb.append(str);
          }
          is.close();
      }
      log.info("接口返回报文："+sb.toString());
      
      //根据返回报文处理提现记录的余额冲正状态
      Document doc = DocumentHelper.parseText(sb.toString());
      Map<String, String> xmlMap = new HashMap<String, String>();  
      xmlMap.put("default", doc.getRootElement().getNamespaceURI());  
      XPath x = doc.createXPath("//default:package/default:details/default:detail");
      x.setNamespaceURIs(xmlMap);
      List<Node> cashList=x.selectNodes(doc);
      this.updateBalanceRecSendSucc(cashList);
    } catch (SocketTimeoutException e2) {
      e2.printStackTrace();
      log.warn("调用钱包余额冲正超时");
      this.updateBalanceRecSendFaild(list,"3","发送冲正超时");
    } catch (Exception e) {
      e.printStackTrace();
      log.warn("调用钱包余额冲正异常");
      this.updateBalanceRecSendFaild(list,"2","发送冲正失败："+e.getMessage());
    }finally{
      try {
        response.close();
      } catch (Exception e) {
      }
    }
    
    
    
  }

  @Override
  public void run() {
    
    CloseableHttpClient http = HttpClients.createDefault();
    balanceRec(http,this.params);
  }
  
  private void updateBalanceRecSendFaild(List<Map<String, Object>> list,String status,String backRemark) {
    int totalRet=0;
    for (Map<String, Object> map : list) {
      map.put("backStatus", status);
      map.put("backRemark", backRemark);
//      int ret=this.updateRec(map);
      int ret=purseService.updatePurseRec(map);
      totalRet+=ret;
    }
    log.info("修改提现记录的余额冲正状态"+totalRet+"条");
    
  }
  
  private void updateBalanceRecSendSucc(List<Node> cashList) {
    int totalRet=0;
    for (Node node : cashList) {
      Map<String, Object> params=new HashMap<String, Object>();
      Element element=(Element)node;
      params.put("id", element.elementTextTrim("id"));
      params.put("backStatus", element.elementTextTrim("status"));
      params.put("backRemark", element.elementTextTrim("statusDesc"));
      
//      int ret=this.updateRec(params);
      int ret=purseService.updatePurseRec(params);
      totalRet+=ret;
    }
    log.info("修改提现记录的余额冲正状态"+totalRet+"条");
    
  }
  
  
//  private int updateRec(Map<String, Object> params) {
//    String sql="update bag_extraction_his set back_status=?,back_remark=? where id=?";
//    try {
//      int ret=bagDao.update(sql, new Object[]{(String)params.get("backStatus"),
//          (String)params.get("backRemark"),params.get("id").toString()});
//      return ret;
//    } catch (SQLException e) {
//      e.printStackTrace();
//      return 0;
//    }
//    
//  }

	
}
