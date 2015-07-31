/**
 * 版权所有(C) 2014 深圳市雁联计算系统有限公司
 * 创建:ZhangDM(Mingly) 2014-6-10
 */

package com.eeepay.boss.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.eeepay.boss.controller.APIController;

/** 
 * @author ZhangDM(Mingly)
 * @date 2014-6-10
 * @description：安全工具类
 */

public class SecureUtils {
  
  /**
   * @description 验签
   * @param cerPath 证书路径
   * @param checkValue 加签内容
   * @param xmlMsg 明文
   * @param encoding 字符集
   * @return 验签结果    true：验签成功 
   *          false：验签失败
   * @throws Exception  
   * @author ZhangDM(Mingly)
   * @date 2014-6-10
   */
  public static boolean verifyMsgSign(String cerPath, String checkValue, String xmlMsg, 
      String encoding) throws Exception {
    
    //将加签内容进行Base64解码
    byte[] binaryData  = Base64.decodeBase64(checkValue);
    
    //获取公钥
    PublicKey publicKey = getPublicKey(cerPath);
    if(publicKey == null){
      throw new IllegalArgumentException("获取证书失败");
    }
    //验签
    Signature signetcheck = Signature.getInstance("SHA1withRSA");
    signetcheck.initVerify(publicKey);
    signetcheck.update(xmlMsg.getBytes(encoding));
    return signetcheck.verify(binaryData);
  }
  
  /**
   * @description 得到公钥
   * @param cerPath
   * @return
   * @throws Exception  
   * @author ZhangDM(Mingly)
   * @date 2014-6-10
   */
  private static PublicKey getPublicKey(String cerPath) throws Exception {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
//    FileInputStream certInputStream = new FileInputStream(cerPath);
    
    InputStream certInputStream=SecureUtils.class.getClassLoader().getResourceAsStream(cerPath);
    
    Certificate certificate = cf.generateCertificate(certInputStream);
    certInputStream.close();
    return certificate.getPublicKey();
  }
  
  /**
   * @description 加签
   * @param msg 明文信息
   * @param keyPath 证书路径
   * @param keyPwd 证书密码
   * @param encoding 字符集
   * @return 加签内容
   * @throws Exception  
   * @author ZhangDM(Mingly)
   * @date 2014-6-10
   */
  public static String signMsg(String msg, String keyPath, String keyPwd, 
      String encoding) throws Exception {
    
    PrivateKey priKey = getPrivateKey(keyPath, keyPwd);
    if(priKey == null){
      throw new IllegalArgumentException("获取证书失败");
    }
    Signature signet = Signature.getInstance("SHA1withRSA");
    signet.initSign(priKey);
    signet.update(msg.getBytes(encoding));
    byte[] signed = signet.sign();
    return new String(Base64.encodeBase64(signed), encoding);
  }
  
  /**
   * @description 得到私钥
   * @param keyPath 证书路径
   * @param keyPwd 证书密码
   * @return
   * @throws Exception  
   * @author ZhangDM(Mingly)
   * @date 2014-6-10
   */
  private static PrivateKey getPrivateKey(String keyPath, String keyPwd) throws Exception{
    
    InputStream fis = null;
    try{
//      File file = new File(keyPath);
//      fis = new FileInputStream(file);

      fis=SecureUtils.class.getClassLoader().getResourceAsStream(keyPath);
      
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(fis, keyPwd.toCharArray());
  
      Enumeration aliases = keyStore.aliases();
      String keyAlias = null;
      if (aliases != null){
        while (aliases.hasMoreElements()){
          keyAlias = (String) aliases.nextElement();
          return (PrivateKey)(keyStore.getKey(keyAlias, keyPwd.toCharArray()));
        }
      }
    }catch (Exception e){
      throw e;
    }finally{
      if (fis != null)
        fis.close();
    }
    return null;
  }
  
  /**
   * @description 解析二维码信息
   * @param imgStr 二维码图片字符串
   * @param encoding 字符集
   * @return 二维码信息
   * @throws Exception  
   * @author ZhangDM(Mingly)
   * @date 2014-6-10
   */
//  public static String returnParseQRCode(String imgStr, String encoding) 
//      throws Exception{
//    byte[] imgByte = Base64.decodeBase64(imgStr.getBytes(encoding));
//    return QRCodeUtil.decoderQRCode(imgByte);
//  }
  
  /**
   * @description 公钥解密
   * @param data 密文
   * @param cerPath 证书路径
   * @param encoding 字符集
   * @return 明文信息
   * @throws Exception  
   * @author ZhangDM(Mingly)
   * @date 2014-6-10
   */
  public static String decryptByPublicKey(String data, String cerPath, String encoding) 
      throws Exception {
    //获取公钥
    PublicKey publicKey = getPublicKey(cerPath);
    if(publicKey == null){
      throw new IllegalArgumentException("获取证书失败");
    }
    //解密
    byte[] binaryData  = Base64.decodeBase64(data.getBytes(encoding));
    Cipher cipher = Cipher.getInstance("RSA",new BouncyCastleProvider());
    cipher.init(Cipher.DECRYPT_MODE, publicKey);
    byte[] doFinal = cipher.doFinal(binaryData);
    
    return new String(doFinal, encoding);
  }
}
