/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.domain.HxbPayErr;

/**
 * 描述：华夏银企直连
 *
 * @author ym 
 * 创建时间：2014年8月23日
 */

public class HxbCashComm {
  private static final Logger log = LoggerFactory.getLogger(HxbCashComm.class);

  
  /**
   * 
   * 功能：华夏银企直连通讯
   *
   * @param http : HttpClient,传入可以保持session
   * @param url
   * @param params 参数map
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public HxbPayErr hxbPayComm(CloseableHttpClient http,String url,Map<String, String> params,HxbPayErr err) {
    HttpPost httpPost = new HttpPost(url);
    List paramsList = new ArrayList();
    Set<String> keySet=params.keySet();
    for (String key : keySet) {
        paramsList.add(new BasicNameValuePair(key, params.get(key)));
      }
       
    CloseableHttpResponse response=null; 
    StringBuilder sb = new StringBuilder();
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(paramsList));
      RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Constants.PURSE_TIME_OUT).setConnectTimeout(Constants.PURSE_CONN_TIME_OUT).build();
      httpPost.setConfig(requestConfig);
      response = http.execute(httpPost);
      
//      log.info("接口通讯："+response.getStatusLine());
//      log.info("接口返回报文："+EntityUtils.toString(response.getEntity()));
      
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
    } catch (SocketTimeoutException e2) {
      e2.printStackTrace();
      err.setErrCode("timeOut");
      err.setErrMsg("请求超时，状态未知");
      log.error("请求超时，状态未知");
      return err;
    } catch (Exception e) {
      e.printStackTrace();
      err.setErrCode("exception");
      err.setErrMsg(e.getMessage());
      log.error(e.getMessage());
      return err;
    }finally{
      try {
        response.close();
      } catch (Exception e) {
      }
    }
    
    err.setErrCode("success");
    err.setErrMsg("提交成功");
    err.setData(sb.toString());
    return err;
  }
  
  /**
   * 
   * 功能：关闭httpclient
   *
   * @param httpClient
   */
  public void closeHttpClient(CloseableHttpClient httpClient) {
    try {
      httpClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 
   * 功能：对象转换成xml 
   *
   * @param object 对象已使用jaxb标签标注
   * @return
   */
  public String object2XML(Object object) {
    String xml="";
    try {
      JAXBContext context = JAXBContext.newInstance(object.getClass());
      Marshaller marshaller = context.createMarshaller();
      StringWriter writer = new StringWriter();
      marshaller.marshal(object, writer);
      xml=writer.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return xml;
    } 
    return xml;
    
  }
  
  
}
