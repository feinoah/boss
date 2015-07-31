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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 描述：http通讯
 *
 * @author ym 
 * 创建时间：2014年12月24日
 */

public class HttpComm {
  private static final Logger log = LoggerFactory.getLogger(HttpComm.class);
  
  public static final int CONN_TIME_OUT=40000;
  public static final int TIME_OUT=40000;
  

  
  /**
   * 
   * 功能：
   *
   * @param http : HttpClient,传入可以保持session
   * @param url
   * @param params 参数map
   * @return
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public String sendComm(CloseableHttpClient http,String url,Map<String, String> params,String charset) throws Exception{
    if (StringUtil.isBlank(charset)) {
      charset="UTF-8";
    }
    
    HttpPost httpPost = new HttpPost(url);
    List paramsList = new ArrayList();
    Set<String> keySet=params.keySet();
    for (String key : keySet) {
        paramsList.add(new BasicNameValuePair(key, params.get(key)));
      }
       
    CloseableHttpResponse response=null; 
    StringBuilder sb = new StringBuilder();
    
    httpPost.setEntity(new UrlEncodedFormEntity(paramsList));
    RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIME_OUT).setConnectTimeout(CONN_TIME_OUT).build();
    httpPost.setConfig(requestConfig);
    response = http.execute(httpPost);
    
    HttpEntity httpEntity = response.getEntity();  
    if(httpEntity != null){
        httpEntity = new BufferedHttpEntity(httpEntity);  
        InputStream is = httpEntity.getContent();  
        BufferedReader br = new BufferedReader(new InputStreamReader(is,charset));
        String str;
        while((str=br.readLine())!=null){
          sb.append(str);
        }
        is.close();
    }
//    log.info("通讯返回："+sb.toString());
    return sb.toString();
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
  
  
  
  public String sendPost(String url,List<NameValuePair> params) throws ClientProtocolException, IOException {
    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
          
    HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new UrlEncodedFormEntity(params));
          
        CloseableHttpResponse response = httpclient.execute(httppost);
        System.out.println("header:"+response.toString());
          
        HttpEntity entity = response.getEntity();
        String returnStr = EntityUtils.toString(entity, "utf-8");
        System.out.println("返回:"+returnStr);
          
        httppost.releaseConnection();
        return returnStr;
}
  
  public String sendGet(String urlWithParams) throws Exception {
    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
      
    //HttpGet httpget = new HttpGet("http://www.baidu.com/");
    HttpGet httpget = new HttpGet(urlWithParams);  
    //配置请求的超时设置
    RequestConfig requestConfig = RequestConfig.custom() 
            .setConnectionRequestTimeout(TIME_OUT)
            .setConnectTimeout(CONN_TIME_OUT) 
            .setSocketTimeout(TIME_OUT).build(); 
    httpget.setConfig(requestConfig);
      
    CloseableHttpResponse response = httpclient.execute(httpget);       
//    System.out.println("StatusCode -> " + response.getStatusLine().getStatusCode());
      
    HttpEntity entity = response.getEntity();       
    String returnStr = EntityUtils.toString(entity);//, "utf-8");
      
    httpget.releaseConnection();
    return returnStr;
}
  
  public static void main(String[] args) {
    
    try {
      String loginUrl = "http://183.62.226.165:8000/ydzf-access/";
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("name", "zhang"));
      params.add(new BasicNameValuePair("passwd", "123"));
            
      new HttpComm().sendPost(loginUrl,params);
  } catch (ClientProtocolException e) {
      e.printStackTrace();
  } catch (IOException e) {
      e.printStackTrace();
  }
    
  }
  
  
}
