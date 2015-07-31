/**
 * 
 */
package com.eeepay.boss.utils.pub;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hdb
 * 发送短信接口
 * 2013-5-13 上午9:26:23 
 */
public class Sms implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Sms.class);
	
	private final static String sn="SDK-BBX-010-13629";
	private final static String password="556756";
	private final static String ENCODE="UTF-8";
	
	private String methodType;
	private String mobile;
	private String context;
	private String endStr;
	private String[] phones;
	
	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getEndStr() {
		return endStr;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

	public String[] getPhones() {
		return phones;
	}

	public void setPhones(String[] phones) {
		this.phones = phones;
	}

	/**
	 * 发送
	 * @param mobile 电话号码
	 * @param context 内容
	 * @return
	 */
	public static int sendMsg(String mobile, String context) {

		PostUTF8Method postmethod = new PostUTF8Method(
				"http://sdk2.entinfo.cn/z_send.aspx");
		int sendStatus = 0;
		try {
			HttpClient httpclient = new HttpClient();
			// post请求
			NameValuePair[] postData = new NameValuePair[4];
			postData[0] = new NameValuePair("sn", sn);
			postData[1] = new NameValuePair("mobile", mobile);
			//postData[2] = new NameValuePair("content", context + "[移联支付]");
			postData[2] = new NameValuePair("content", context + "[支付随心]");
			postData[3] = new NameValuePair("pwd", password);

			Header header = new Header("Content-type",
					"application/x-www-form-urlencoded; charset=" + ENCODE);
			postmethod.setRequestHeader(header);

			postmethod.addParameters(postData);
			sendStatus = httpclient.executeMethod(postmethod);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放
			postmethod.releaseConnection();
		}

		return sendStatus;
	}

	public static int sendMsgOem(String mobile, String context,String endStr) {
		endStr = "[支付随心]";
		context += endStr;
		PostUTF8Method postmethod=new PostUTF8Method("http://sdk2.entinfo.cn/z_send.aspx");  
    	int sendStatus=0;
        try {
        	HttpClient httpclient=new HttpClient();  
        	//post请求  
        	NameValuePair[] postData=new NameValuePair[4];  
        	postData[0]=new NameValuePair("sn",sn);  
        	postData[1]=new NameValuePair("mobile",mobile);  
			postData[2]=new NameValuePair("content", context);//,
			postData[3]=new NameValuePair("pwd",password);  
			
			Header header=new Header("Content-type", "application/x-www-form-urlencoded; charset=" + ENCODE);
			postmethod.setRequestHeader(header);
			
			postmethod.addParameters(postData);  
		    sendStatus=httpclient.executeMethod(postmethod);  
		} catch (Exception e) {  
        	 e.printStackTrace(); 
		} finally{  
            //释放  
            postmethod.releaseConnection();  
        }  
	
	return sendStatus;
}
	
	/**
	 * @param phones
	 * @param context
	 * @return
	 */
	public static void sendMsg(String[] phones, String content) {
		
		for(String phone:phones){
			int status=sendMsg(phone, content);
			if(status!=200){
				log.warn("定时短信发送任务,出错");
			}
		}
	}
	
	public static void main(String[] args) {
//		int status=Sms.sendMsg("18666826370", "这是一个测试短信带尾巴");
//		int status=Sms.sendMsgOem("18666826370", "这是一个测试短信[众联支付]");
//		System.out.println("========"+status);
	}

	@Override
	public void run() {
		if ("mobile".equals(this.methodType)) {
			Sms.sendMsg(this.mobile, this.context);
		} else if ("endStr".equals(this.methodType)) {
			Sms.sendMsgOem(this.mobile, this.context,this.endStr);
		} else if ("phones".equals(this.methodType)) {
			Sms.sendMsg(this.phones, this.context);
		}
	}

	/**
	 * 带参构造函数是为了实例多线程而使用的
	 * @param methodType ，指定调用的方法：
	 * 	<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; mobile，调用sendMsg(String mobile, String context)
	 * 	<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; endStr，调用sendMsgOem(String mobile, String context,String endStr)
	 * 	<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; phones，调用sendMsg(String[] phones, String context)
	 * @param mobile 手机号
	 * @param context 内容
	 * @param endStr 短信尾巴
	 * @param phones 手机号列表
	 */
	public Sms(String methodType, String mobile, String context, String endStr,
			String[] phones) {
		this.methodType = methodType;
		this.mobile = mobile;
		this.context = context;
		this.endStr = endStr;
		this.phones = phones;
	}

	public Sms() {
	}
	
	
	
}
