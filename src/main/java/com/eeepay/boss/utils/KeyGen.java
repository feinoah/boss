package com.eeepay.boss.utils;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;

import com.eeepay.boss.common.bean.KeyPair;

public class KeyGen {
	private static final String ALGORITHM="RSA";
	private static KeyPairGenerator kpg; 
	public static KeyPairGenerator getKeyPairGenerator(){
		if(kpg==null){
			synchronized(KeyGen.class){
				if(kpg==null){
					try {
						kpg=KeyPairGenerator.getInstance(ALGORITHM);
						kpg.initialize(2048);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return kpg;
	}
	/**
	 * 生成一对密钥
	 * @return
	 */
	public static KeyPair genKeyPair(){
		KeyPairGenerator kpg = KeyGen.getKeyPairGenerator();
		java.security.KeyPair kp = kpg.genKeyPair();
		PublicKey publicKey = kp.getPublic();
		PrivateKey privateKey = kp.getPrivate();
		KeyPair keyPair=new KeyPair();
		keyPair.setPublicKey(Base64.encodeBase64String(publicKey.getEncoded()));
		keyPair.setPrivateKey(Base64.encodeBase64String(privateKey.getEncoded()));
		return keyPair;
	}
}
