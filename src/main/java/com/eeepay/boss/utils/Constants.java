package com.eeepay.boss.utils;

public class Constants {
	public static String EMAIL_FROMNAME = "eeepay";
	public static String EMAIL_SMTP = "smtp.126.com";
	public static String EMAIL_USERNSME = "eeepaycn";
	public static String EMAIL_PASSWORD = "172839";
	public static String EMAIL_ADDRESS = "eeepaycn@126.com";
	public static String SYS_NAME = "BOSS";
	
	/*阿里云存储boss附件临时bucket*/
	public static final String ALIYUN_OSS_TEMP_TUCKET="boss-temp";
	/*阿里云存储boss附件bucket*/
	public static final String ALIYUN_OSS_ATTCH_TUCKET="agent-attch";
	/*阿里云存储移小宝电子小票签名bucket*/
	public static final String ALIYUN_OSS_SIGN_TUCKET="sign-img";
	/*阿里云存储bag附件bucket*/
	public static final String ALIYUN_OSS_BAG_TUCKET="eeepaybag";
	
	//钱包提现 接口地址
	public static final int PURSE_CONN_TIME_OUT=30000;
	public static final int PURSE_TIME_OUT=80000;
	
	public static final String PURSE_CASH_URL="http://****";
	
	/*移小宝电子签名小票保存的服务器地址*/
	public static final String SIGN_URL1="http://183.238.157.6:5780"; 
	public static final String SIGN_URL2="http://115.28.36.50:5780";
	
	public static String BAG_HMAC = "2AAB2CE21BDA125093DA45BCDEAE011B";
	
	
	/**
	 * 艾创小盒子KEK
	 * */
	public static final String ITRON_KEK = "B21B113A5C673DF441AAC9DBDBD7D9AA";

	/**
	 * 新大陆 me30 kek
	 */
	public static final String NEWLAND_KEK="FF1B113A5C673DF441BBC9DBDBD7D9CC";
	
	/**
	 * BBPOS  key
	 */
	public static final String BBPOS_KEK="CB2B113A5C673DF441BBC9DBDBD7D9BB";
	
	/**
	 * 天喻刷卡器KEK
	 */
	public static final String TY_KEK="6B99E4316F5FC4033D75C3103D56BCD3";
	
	/**
	 * UBS  key 通道
	 */
	public static final String UBS_KEK="BF3E2C801FA798A2AEDBE7262B2A4C75";
	
	/**
	 * HYPAY  key 通道
	 */
	public static final String HYPAY_KEK="48B4483FA322D1A03FEB062BB1A0F5B1";
	
	//加密密钥
	public static final String UBS_LMK_KEY = "73DBB4BCBFB2A6F173DBB4BCBFB2A6F1";
	
	public static final String QLHDPAY_KEY = "ADDEBEA6DA8DDCCAE8F8A6BADB982D36";
}
