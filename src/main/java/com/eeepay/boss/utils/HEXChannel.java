package com.eeepay.boss.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;




public class HEXChannel{



	public static String dump(ISOMsg msg) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			msg.dump(new PrintStream(baos), "");
			baos.flush();
		} catch (IOException e) {
			// ignore
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return new String(baos.toByteArray());
	}

	/**
	 * 得到通讯报文内容
	 * @param s
	 * @return
	 */
	public static String getIsoMsg(String s) {
		
		URL url = DateUtils.class.getResource("eptok.xml");
		String path = url.getPath();
		ISOMsg msg = new ISOMsg();
		try {
			ISOPackager p = new GenericPackager(path);
			msg.setPackager(p);
			msg.unpack(ISOUtil.hex2byte(s));
			
		} catch (ISOException e) {
			e.printStackTrace();
		}
		return dump(msg);
	}
	
	
	public static void main(String[] args) {
		String s = "0200602004C220C098111962270029420201679570310000000018021000064F524730303030322020376227002942020167957D4101520423102000004645303030303032383030303735353530363530303032313536E1FBD0FDFEC63E70260000000000000000110100000200004437414642303633";
		ISOMsg msg = new ISOMsg();
		// System.out.println(DateUtils.class.getResource("eptok.xml"));
		URL url = DateUtils.class.getResource("eptok.xml");
		String path = url.getPath();
		try {
			ISOPackager p = new GenericPackager(path);
			msg.setPackager(p);
			msg.unpack(ISOUtil.hex2byte(s));
			
//			System.out.println(ISOUtil.hexString(msg.pack()));  
		} catch (ISOException e) {
			e.printStackTrace();
		}
		msg.dump(System.out, "");
		
		//System.out.println(dump(msg));
		
	}
	

}
