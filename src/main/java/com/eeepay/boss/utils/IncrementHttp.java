package com.eeepay.boss.utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 代码实现http通讯
 *
 * 创建时间：2009-03-03
 */
public class IncrementHttp{
  private static final Logger Log = LoggerFactory.getLogger(IncrementHttp.class);
  
  /**
   * 
   * @param url
   * @param encode
   * @return
   */
  public static String send(String url,Map<String,Object> params, String encode){

			PostMethod postmethod = new PostMethod(url);
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

  
  public static void main(String[] args) throws UnsupportedEncodingException{

  }
}
