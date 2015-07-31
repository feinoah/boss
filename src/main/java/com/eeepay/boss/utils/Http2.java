package com.eeepay.boss.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.utils.pub.PostUTF8Method;
import com.sun.net.ssl.internal.ssl.Provider;


/**
 * 代码实现http通讯
 * @author 林新格
 *
 * 创建时间：2009-03-03
 */
public class Http2{
  private static final String HTTP_METHOD_POST = "GET";
  private static final String DEFAULT_CHARSET = "UTF-8";

  private static final Logger Log = LoggerFactory
  .getLogger(Http2.class);
  
  
  /**
   * 
   * 功能：发送xml格式的http通讯，post发送
   *
   * @param url 地址
   * @param encode 字符集
   * @return String 返回内容
   */
  public static String sendXML(String url, String encode){
	 
	  String body = "";
	  String m = HTTP_METHOD_POST;
	  
	  Security.addProvider(new Provider());
	  System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
	  
	  HttpURLConnection httpConn=null;
	  try {
		  //设置网络超时时间
		  String timeOut = "300";
		  int iTimeOut = 300;
		  try{
			  Integer.parseInt(timeOut);
		  }catch (Exception e) {
			  iTimeOut = 300;
		  }
		  System.setProperty("sun.net.client.defaultConnectTimeout", iTimeOut*1000+"");
		  System.setProperty("sun.net.client.defaultReadTimeout", iTimeOut*1000+"");
		  
		  String data = "";
		  String uri = url;
		  //如果是post，把uri和参数分开
//      if (HTTP_METHOD_POST.equalsIgnoreCase(m)){
//		  int pos = url.indexOf("?");
//		  if (pos > 0){
//			  uri = url.substring(0, pos);
//			  if (url.length() > (pos+1)){
//				  data = url.substring(pos+1);
//			  }
//		  }
//      }
	  
		  
		  URL someUrl = new URL(uri);
		  URLConnection conn = someUrl.openConnection();
		  httpConn = (HttpURLConnection) conn;
		  httpConn.setRequestMethod(m);
 		  httpConn.setDoOutput(true);
 	    httpConn.setDoInput(true);
 		  httpConn.setUseCaches(false);
		  httpConn.setRequestProperty("Content-type", "text/xml"
		  );
		  
		  OutputStream o = httpConn.getOutputStream();
//		  if (data.length() > 0){
//			  o.write(data.getBytes(encode));
//			  
//		  }
		  o.flush();
      o.close();
		  int status = httpConn.getResponseCode();
		  if (status != HttpURLConnection.HTTP_OK) {
			      Log.info("发送请求失败["+m+"]:status != HttpURLConnection.HTTP_OK"+url);
		        Log.info(httpConn.getResponseMessage());
		        body="msg=HTTP发送请求失败["+m+"]:"+"status != HttpURLConnection.HTTP_OK";
		  } else {
			  
        //处理utf8的bom
        InputStreamReader in = new InputStreamReader( httpConn.getInputStream(),encode) ;
        //读取数据
        BufferedReader rd = new BufferedReader(in);
        StringBuffer buff = new StringBuffer();
        int c;
        while ((c = rd.read()) != -1){
          buff.append((char) c);
        }
        body = buff.toString();
        rd.close();
		  }
	  } catch (Exception e) {
//		  System.out.println("发送请求失败["+m+"]:"+url);
//		  e.printStackTrace();
//      throw new RuntimeException("eSys0009"+e.getMessage()+"");
		  Log.info("发送请求失败["+m+"]:HTTP类catch到通讯异常"+url);
		  body="msg=HTTP发送请求失败["+m+"]:"+"HTTP类catch到通讯异常";
 	  }finally{
		  if (httpConn != null){
			  httpConn.disconnect();
			  httpConn = null;
		  }
	  }
	  
	  return body;
  }
  
  
  public static String send(String url,Map<String,String> params, String encode){
		
		PostUTF8Method postmethod=new PostUTF8Method(url);  
      try {
      	HttpClient httpclient=new HttpClient();  
      	//post请求  
      	NameValuePair[] postData=new NameValuePair[params.size()];  
      	int index=0;
      	for(String key:params.keySet()){
      		postData[index]=new NameValuePair(key,params.get(key)+"");  
      		index++;
      	}
			
			Header header=new Header("Content-type", "application/x-www-form-urlencoded; charset=" + encode);
			postmethod.setRequestHeader(header);
			postmethod.addParameters(postData);  
			
			httpclient.executeMethod(postmethod);  
		    
		    byte[] body=postmethod.getResponseBody();
		    
		    return new String(body,0,body.length);
		    
		} catch (Exception e) {  
      	 e.printStackTrace(); 
		} finally{  
          //释放  
          postmethod.releaseConnection();  
      }  
	return null;
}
  //"application/x-www-form-urlencoded; charset="+encode);//"text/xml"
  
}
