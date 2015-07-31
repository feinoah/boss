package com.eeepay.boss.utils.el.functions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.utils.ALiYunOssUtil;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.SysConfig;

public class FileUrlGen {
	private static Logger logger=LoggerFactory.getLogger(FileUrlGen.class);
	private static final String BUCKET_NAME=Constants.ALIYUN_OSS_ATTCH_TUCKET;
	public static String fileUrlGen(String fileName){
		String agentaddress="http://120.132.177.194";
		//agentaddress="http://localhost:8081";
		String uploadmerchant=SysConfig.value("uploadmerchant");
		String urlStr=agentaddress+uploadmerchant+fileName;
		/*Calendar calendar=Calendar.getInstance();
		//设置时间2014-03-31
		calendar.set(2014, 2,31);
		//推迟20天   去掉时间限制的查询， 每次都去其他服务器查找附件是否存在
		calendar.add(Calendar.DAY_OF_YEAR, 20);*/
		Calendar currentCalendar=Calendar.getInstance();
		Date expiresDate=new Date(currentCalendar.getTime().getTime()*3600*1000);
		boolean hasError=false;
		//判断是否超过检查的时间 
		/*if(currentCalendar.compareTo(calendar)<=0){*/
			if(true){
				HttpURLConnection huc=null;
			try {
				String urlStr1="http://localhost";
				URLConnection uc=new URL(urlStr1).openConnection();
				huc=(HttpURLConnection) uc;
				huc.setConnectTimeout(1000);
				//不允许自动转发 302
				huc.setInstanceFollowRedirects(false);
				huc.connect();
				
				if(huc.getResponseCode()!=200){
					urlStr=ALiYunOssUtil.genUrl(BUCKET_NAME, fileName, expiresDate);
				}
				return urlStr;
			} catch (MalformedURLException e) {
				//e.printStackTrace();
				logger.error("文件url标签类-----无效的url："+urlStr,e);
				hasError=true;
			} catch (IOException e) {
				//e.printStackTrace();
				logger.error("文件url标签类-----出错：",e);
				hasError=true;
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("文件url标签类-----出错：",e);
				hasError=true;
			}finally{
				if(huc!=null){
					huc.disconnect();
				}
			}
		}/*else{
			urlStr=ALiYunOssUtil.genUrl(BUCKET_NAME, fileName, expiresDate);
		}*/
		if(hasError){
			urlStr=ALiYunOssUtil.genUrl(BUCKET_NAME, fileName, expiresDate);
		}
		return urlStr;
	}
	
	/**
	 * 钱包的云存储的附件
	 */
	private static final String BAG_BUCKET_NAME=Constants.ALIYUN_OSS_BAG_TUCKET;
	public static String bagFileUrlGen(String mobileNo,String fileName){
		String fileUrl = mobileNo+"/"+fileName;
		String urlStr=null;
		/*Calendar calendar=Calendar.getInstance();
		//设置时间2014-03-31
		calendar.set(2014, 2,31);
		//推迟20天   去掉时间限制的查询， 每次都去其他服务器查找附件是否存在
		calendar.add(Calendar.DAY_OF_YEAR, 20);*/
		Calendar currentCalendar=Calendar.getInstance();
		Date expiresDate=new Date(currentCalendar.getTime().getTime()*3600*1000);
		boolean hasError=false;
		//判断是否超过检查的时间 
		/*if(currentCalendar.compareTo(calendar)<=0){
			if(true){
				HttpURLConnection huc=null;
			try {
				String urlStr1="http://localhost";
				URLConnection uc=new URL(urlStr1).openConnection();
				huc=(HttpURLConnection) uc;
				huc.setConnectTimeout(1000);
				//不允许自动转发 302
				huc.setInstanceFollowRedirects(false);
				huc.connect();
				
				if(huc.getResponseCode()!=200){
					urlStr=ALiYunOssUtil.genUrl(BAG_BUCKET_NAME, fileUrl, expiresDate);
				}
				return urlStr;
			} catch (MalformedURLException e) {
				//e.printStackTrace();
				logger.error("文件url标签类-----无效的url："+urlStr,e);
				hasError=true;
			} catch (IOException e) {
				//e.printStackTrace();
				logger.error("文件url标签类1-----出错：",e);
				hasError=true;
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("文件url标签类2-----出错：",e);
				hasError=true;
			}finally{
				if(huc!=null){
					huc.disconnect();
				}
			}
		}else{
			urlStr=ALiYunOssUtil.genUrl(BAG_BUCKET_NAME, fileName, expiresDate);
		}*/
		try {
			urlStr=ALiYunOssUtil.genUrl(BAG_BUCKET_NAME, fileUrl, expiresDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hasError=true;
		}
		if(hasError){
			urlStr=ALiYunOssUtil.genUrl(BAG_BUCKET_NAME, fileUrl, expiresDate);
		}
		return urlStr;
	}
}
