package com.eeepay.boss.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod; 
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.URIException;  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.domain.MobileProduct;
import com.eeepay.boss.utils.product.PostUTF8Method;



/**
 * 代码实现http通讯
 * @author 林新格
 *
 * 创建时间：2009-03-03
 */
public class Http{
  private static final Logger Log = LoggerFactory.getLogger(Http.class);
  
  /**
   * 
   * @param url
   * @param encode
   * @return
   */
  public static String send(String url,Map<String,Object> params, String encode){
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
				 Log.error("",e);
	        	 e.printStackTrace(); 
			} finally{  
	            //释放  
	            postmethod.releaseConnection();  
	        }  
		return null;
	}
  
  
  public static String sendOtherCharset(String url,Map<String,Object> params, String encode){
	  
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
			 Log.error("",e);
      	 e.printStackTrace(); 
		} finally{  
          //释放  
          postmethod.releaseConnection();  
      }  
	return null;
}
  
  
  public static String doGet(String url, String queryString, String charset, boolean pretty) { 
      StringBuffer response = new StringBuffer(); 
      HttpClient client = new HttpClient(); 
      HttpMethod method = new GetMethod(url); 
      try { 
              if ( queryString != null && !queryString.equals("")) 
                      //对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串 
                      method.setQueryString(URIUtil.encodeQuery(queryString)); 
              client.executeMethod(method); 
              if (method.getStatusCode() == HttpStatus.SC_OK) { 
                      BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset)); 
                      String line; 
                      while ((line = reader.readLine()) != null) { 
                              if (pretty) 
                                      response.append(line).append(System.getProperty("line.separator"));
                              else 
                                      response.append(line); 
                      } 
                      reader.close(); 
              } 
      } catch (URIException e) { 
    	  Log.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e); 
      } catch (IOException e) { 
    	  Log.error("执行HTTP Get请求" + url + "时，发生异常！", e); 
      } finally { 
              method.releaseConnection(); 
      } 
      return response.toString(); 
} 
  
  public static void main(String[] args) throws UnsupportedEncodingException{
	  //获取产品id
	 Map<String,Object> paramss=new HashMap<String,Object>();
	  paramss.put("agentid", "test_agent_id_1");
	  paramss.put("source", "esales");
	//paramss.put("merchantKey", "111111");
	  String sd="agentid=test_agent_id_1&source=esales&merchantKey=111111";
	 //Map<String,Object> params=new HashMap<String,Object>();
	  paramss.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
	  String body = send("http://219.143.36.227:101/dealer/prodquery/directProduct.do", paramss, "UTF-8");
	  System.out.println(URLDecoder.decode(body));
	  String xmlStr=URLDecoder.decode(body);
	  List<MobileProduct> list=new ArrayList<MobileProduct>();
	  XMLAnalysis.parsersXml(xmlStr,list);
	  System.out.println(list.size());
//	 String ss= URLDecoder.decode("<products><product name=\"prodId\" value=\"10003\"/><product name=\"prodContent\" value=\"30\"/><product name=\"prodPrice\" value=\"29.73\"/><product name=\"prodIsptype\" value=\"%E8%81%94%E9%80%9A\"/><product name=\"prodDelaytimes\" value=\"5%E5%88%86%E9%92%9F\"/><product name=\"prodProvinceid\" value=\"%E5%B9%BF%E4%B8%9C\"/><product name=\"prodType\" value=\"%E5%B0%8F%E7%81%B5%E9%80%9A\"/></products>");
//	 String ss = new String("<products><product name=\"prodId\" value=\"10003\"/><product name=\"prodContent\" value=\"30\"/><product name=\"prodPrice\" value=\"29.73\"/><product name=\"prodIsptype\" value=\"%E8%81%94%E9%80%9A\"/><product name=\"prodDelaytimes\" value=\"5%E5%88%86%E9%92%9F\"/><product name=\"prodProvinceid\" value=\"%E5%B9%BF%E4%B8%9C\"/><product name=\"prodType\" value=\"%E5%B0%8F%E7%81%B5%E9%80%9A\"/></products>".getBytes("UTF-8"),"GBK");
//	 System.out.println(ss);
	  //直充
//	  Map<String,Object> params=new HashMap<String,Object>();
//	  params.put("prodid", "8096");
//	  params.put("agentid", "test_agent_id_1");
//	  params.put("backurl", "");
//	  params.put("returntype", "2");
//	  params.put("orderid", "111111111");
//	  params.put("mobilenum", "13811528476");
//	  params.put("source", "esales");
//	  params.put("mark", "");
//	  String sd="prodid=8096&agentid=test_agent_id_1&backurl=&returntype=2&orderid=111111111&mobilenum=13811528476&source=esales&mark=&merchantKey=111111";
//	  params.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
//	  String body = send("http://219.143.36.227:101/dealer/directfill/directFill.do", params, "UTF-8");
//	  System.out.println(KeyedDigestMD5.getKeyedDigest(sd, ""));
//	  System.out.println(body);
  }
}
