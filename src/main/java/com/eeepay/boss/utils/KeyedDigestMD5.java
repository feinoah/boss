package com.eeepay.boss.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyedDigestMD5 {

	public static byte[] getKeyedDigest(byte[] buffer, byte[] key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(buffer);
            return md5.digest(key);
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        }
        return null;
    }
	
		
	public static String getKeyedDigest(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));
            String result="";
            byte[] temp;
            temp=md5.digest(key.getBytes("UTF8"));
    		for (int i=0; i<temp.length; i++){
    			result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
    		}
    		return result;
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String mi;
        String s = "hf1000";
        
        //第二个参数请填空字符串
		mi=KeyedDigestMD5.getKeyedDigest(s,"");
		
		System.out.println("mi:"+mi);
		
			
	}

}
