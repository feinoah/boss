package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.boss.service.QrCodePayNotifyService;
import com.eeepay.boss.service.SZFSDbService;
import com.eeepay.boss.service.SZFSService;
import com.eeepay.boss.utils.DictCache;
import com.eeepay.boss.utils.HttpComm;
import com.eeepay.boss.utils.SecureUtils;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.hxb.pub.util.MvelCompilers;

/**
 *  描述：api接口
 *  
 *  @author:ym
 *  创建时间：2014-11-19
 */
@Controller
@RequestMapping(value = "/api")
public class APIController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(APIController.class);
//  //TODO 生产
//  public  final String SZFS_QRCODE_URL="http://192.168.86.11/ydzf-access/mech/access";
//  public  final String CENTER_NO="999999999999";
//  public  final String MERCH_NO="000000000003";
//  
//测试
//  public  final String SZFS_QRCODE_URL="http://183.62.226.165:8000/ydzf-access/mech/access";
//  public  final String CENTER_NO="999999999999";
//  public  final String MERCH_NO="000000000005";
//  
  
	
	@Resource
	private SZFSService szfsService;
//  @Resource
//  private QrCodePayNotifyService qrCodePayNotifyService;
  @Resource
  private SZFSDbService szfsDbService;

	
	/**
	 * 
	 * 功能：结算中心账户验证
	 *
	 * @param model
	 * @param params
	 * @return
	 */
  @RequestMapping(value = "/szfsAccountVerifyApi")
  public String szfsAccountVerify(final ModelMap model,
      @RequestParam Map<String, String> params){
    ErrorVO error=new ErrorVO();
    String accName=params.get("accName");
    if (StringUtil.isBlank(accName)) {
      model.put("errCode", "error");
      model.put("errMsg", "账户户名不能为空");
      log.info("结算中心校验账户："+error.getErrCode()+"|"+error.getErrMsg());
      return "api/szfsAccountVerify";
    }
    try {
      accName=URLDecoder.decode(accName, "UTF8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    params.put("accName", accName);
    error=szfsService.accountVerify(params);
    
    model.put("errCode", error.getErrCode());
    model.put("errMsg", error.getErrMsg());
    log.info("结算中心校验账户："+error.getErrCode()+"|"+error.getErrMsg());
    return "api/szfsAccountVerify";
  }

  
  
  //, method = RequestMethod.POST
//  @RequestMapping(value = "/szfsQrCodeOrder")
//  public String  szfsQrCodeOrder(final ModelMap model, @RequestParam Map<String, String> params) {
//    String channel=params.get("channel");
//    String channelSeqNo=params.get("channelSeqNo");
//    String merCode=params.get("merCode");
//    String posId=params.get("posId");
//    String amount=params.get("amount");
//    String resv=params.get("resv");
//    
//    String errCode="";
//    String errMsg="";
//    String status="2";
//    
//    if (StringUtils.isBlank(channel)) {
//      errCode="dataErr";
//      errMsg="渠道不能为空";
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "/szfs/qrCodeOrder";
//    }
//    if (StringUtils.isBlank(channelSeqNo)) {
//      errCode="dataErr";
//      errMsg="渠道流水不能为空";
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "/szfs/qrCodeOrder";
//    }
//    if (StringUtils.isBlank(merCode)) {
//      errCode="dataErr";
//      errMsg="商户号不能为空";
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "/szfs/qrCodeOrder";
//    }
//    if (StringUtils.isBlank(posId)) {
//      errCode="dataErr";
//      errMsg="终端号不能为空";
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "/szfs/qrCodeOrder";
//    }
//    
//    Date now=new Date();
//    String creDtTm=new SimpleDateFormat("yyyy-MM-dd").format(now)+"T"+new SimpleDateFormat("HH:mm:ss").format(now);
//    String busiType="00";
//    
//    String prefType="0";
//    String mechDate=new SimpleDateFormat("yyyyMMdd").format(now);
//    String mechTime=new SimpleDateFormat("HHmmss").format(now);
//    
//    params.put("mechNo", MERCH_NO);
//    params.put("creDtTm", creDtTm);
//    params.put("busiType", busiType);
//    
//    params.put("prefType", prefType);
//    params.put("mechDate", mechDate);
//    params.put("merCode", merCode);
//    params.put("posId", posId);
//    //金额以分为单位
//    BigDecimal amountBigDecimal=new BigDecimal(amount);
//    amountBigDecimal=amountBigDecimal.multiply(new BigDecimal("100"));
//    params.put("orderAmount", ""+amountBigDecimal.intValue());
//    params.put("initOrderAmount", ""+amountBigDecimal.intValue());
//    params.put("resv", resv);
//    
//    String id=szfsDbService.insertQrCodeOrder(params);
//    String mechSeqId=id;
//    params.put("mechSeqId", mechSeqId);
//    
//    CloseableHttpClient http = HttpClients.createDefault();
//    Document document=null;
//    Document doc=null;
//    SAXReader reader = new SAXReader();
//    String xml="";
//    String returnStr="";
//    HttpComm httpComm=new HttpComm();
//    try {
//      document = reader.read(APIController.class.getClassLoader().getResourceAsStream("xml/SZFS_QRCODE_ORDER.xml"));
//      xml=document.asXML().trim();
//      xml = MvelCompilers.eval(xml, params);
//      System.out.println("请求xml："+xml);
//      
//      byte[] digestByte=this.sign("xml", "GBK");
//      
//      StringBuffer baseBuffer=new StringBuffer(new String(Base64.encodeBase64(digestByte)));
//      StringBuffer bf=new StringBuffer(SecureUtils.signMsg(xml, "xml/szfs000000000001.pfx", "1aokp809", "GBK"));
//      System.out.println(baseBuffer.toString().equals(bf.toString()));
//      
//      int length=bf.toString().length();
//      if (length<512) {
//        for (int i = 0; i <512-length; i++) {
//          bf.append(" ");
//        }
//      }
//      
//      StringBuffer sign=new StringBuffer();
//      sign.append("{S:");
//      sign.append(bf.toString());
//      sign.append("}\r\n");
//      
//      int mesgLong=512+xml.length();
//      String mesgLength=""+mesgLong;
//      mesgLength=StringUtil.stringFillLeftZero(mesgLength, 8);
//      
//      StringBuffer headerBuffer=new StringBuffer();
//      headerBuffer.append("{H:");
//      headerBuffer.append("01");
//      headerBuffer.append(MERCH_NO);
//      headerBuffer.append(CENTER_NO);
//      headerBuffer.append(mechDate);
//      headerBuffer.append(mechTime);
//      headerBuffer.append(mesgLength);
//      headerBuffer.append("11001");
//      headerBuffer.append("}\r\n");
//      log.info("报文头："+headerBuffer.toString());
//      
//      Map<String, String> httpParams=new HashMap<String, String>();
//      httpParams.put("Sender", headerBuffer.toString());
//      httpParams.put("CheckValue", sign.toString());
//      httpParams.put("Packet", xml);
//      
//      returnStr=httpComm.sendComm(http, SZFS_QRCODE_URL, httpParams, "GBK");
//      log.info("通讯返回："+returnStr);
//      String[] returnStrs=returnStr.split("}");
//      if (returnStrs.length<3) {
//        log.error("平台返回报文异常");
//        errCode="centerErr";
//        errMsg="平台返回报文异常";
//        model.put("errCode", errCode);
//        model.put("errMsg", errMsg);
//        return "api/szfsQrCodeOrder";
//      }
//      String returnSign=returnStrs[1];
//      String returnXml=returnStrs[2];
//      
//      returnSign=returnSign.substring(3, returnSign.length()-1);
////      boolean signFlag=SecureUtils.verifyMsgSign("xml/szfs999999999999.crt", returnSign, returnXml, "GBK");
////      if (!signFlag) {
////        log.error("平台返回数据异常，可能已被篡改");
////        errCode="centerSignErr";
////        errMsg="平台返回数据异常，可能已被篡改";
////        model.put("errCode", errCode);
////        model.put("errMsg", errMsg);
////        return "api/szfsQrCodeOrder";
////      }
//      
//      doc = DocumentHelper.parseText(returnXml); 
//    } catch (Exception e) {
//      e.printStackTrace();
//      if(e instanceof SocketTimeoutException) {
//        status="3";
//        errCode="time_out";
//        errMsg="提交平台超时";
//      }else {
//        status="2";
//        errCode="error";
//        errMsg=e.getMessage();
//        errMsg=errMsg.length()>200?errMsg.substring(0, 200):errMsg;
//      }
//      
//      Map<String, String> dbParamsMap=new HashMap<String, String>();
//      dbParamsMap.put("id",id);
//      dbParamsMap.put("status", status);
//      dbParamsMap.put("errCode", errCode);
//      dbParamsMap.put("errMsg", errMsg);
//      dbParamsMap.put("channelBak", resv);
//      szfsDbService.updateQrCodeOrder(dbParamsMap);
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "api/szfsQrCodeOrder";
//    
//    }finally{
//      httpComm.closeHttpClient(http);
//    }
//    
//    
//    Element returnRoot = doc.getRootElement(); 
//    Element bodyElement=returnRoot.element("RespInfo").element("GrpBody");
//    String respCode=bodyElement.element("StsRsn").element("RespCode").getTextTrim();
//    String respDesc=bodyElement.element("StsRsn").element("RespDesc").getTextTrim();
//    String centerSeq="";
//    String centerStatus="";
//    String picCode="";
//    
//    
//    Element stsElement=bodyElement.element("StsRsnInf");
//    if ("90000".equals(respCode)) {
//      centerStatus=stsElement.element("Status").getTextTrim();
//      if ("00".equals(centerStatus)) {
//        status="1";
//        errCode="success";
//        errMsg="交易成功";
//        
//        picCode=stsElement.element("Pic").getTextTrim();
//        centerSeq=stsElement.element("CenterSeqId").getTextTrim();
//      }
//      
//    }else {
//      status="2";
//      errCode="centerErr";
//      errMsg=respCode+"|"+respDesc;
//    }
//    
//    Map<String, String> dbParamsMap=new HashMap<String, String>();
//    dbParamsMap.put("id",id);
//    dbParamsMap.put("status", status);
//    dbParamsMap.put("errCode", errCode);
//    dbParamsMap.put("errMsg", errMsg);
//    dbParamsMap.put("channelBak", resv);
//    dbParamsMap.put("centerRespCode", respCode);
//    dbParamsMap.put("centerRespDesc", respDesc);
//    dbParamsMap.put("centerSeqId", centerSeq);
//    dbParamsMap.put("centerStatus", centerStatus);
//    dbParamsMap.put("picCode", picCode);
//    szfsDbService.updateQrCodeOrder(dbParamsMap);
//   
//    model.put("id", id);
//    model.put("errCode", errCode);
//    model.put("errMsg", errMsg);
//    
//    model.put("picCode", picCode);
//    model.put("resv", resv);
//    
//    return "api/szfsQrCodeOrder";
//  
//  }
//  
//  /**
//   * 
//   * 功能：二维码支付
//   *
//   * @param model
//   * @param params
//   * @return
//   */
////  @RequestMapping(value = "/szfsQrCodePay")
//  public String  szfsQrCodePay(final ModelMap model, @RequestParam Map<String, String> params) {
//    String picCode=params.get("picCode");
//    String seqNo=params.get("seqNo");
//    seqNo=seqNo.substring(1);
//    
//    params.put("seqNo", seqNo);
//    
//    String errCode="";
//    String errMsg="";
//    
//    Map<String, String> dbParamsMap=new HashMap<String, String>();
//    dbParamsMap.put("id",seqNo);
//    dbParamsMap.put("status", "4");
//    dbParamsMap.put("errCode", "pay_success");
//    dbParamsMap.put("errMsg", "支付成功");
//    szfsDbService.updateQrCodeOrder(dbParamsMap);
//    
//    log.info("支付成功");
//    
//    //通知
////    szfsQrCodePayNotify(model,params);
//    model.put("errCode", "success");
//    model.put("errMsg", "交易成功");
//    model.put("payStatus", "pay_success");
//    model.put("payStatusDesc", "支付成功");
//    return "api/szfsQrCodePay";
//  }
//  
//  
//  /**
//   * 
//   * 功能：支付通知
//   *
//   * @param model
//   * @param params
//   * @return
//   */
//  @RequestMapping(value = "/szfsQrCodePayNotify")
//  public void  szfsQrCodePayNotify(final ModelMap model, @RequestParam Map<String, String> params,HttpServletResponse response)  throws IOException {
//    log.info("----结算中心二维码支付结果通知开始----");
//    String xmlPacket=params.get("packet");
//    
////    //TODO
////    xmlPacket="<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n" + 
////        "<Document xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
////        "  xsi:noNamespaceSchemaLocation=\"11201.xsd\">\r\n" + 
////        "  <RespInfo>\r\n" + 
////        "    <GrpHdr>\r\n" + 
////        "      <MechNo>999999999999</MechNo>\r\n" + 
////        "      <CreDtTm>2014-11-27T11:27:37</CreDtTm>\r\n" + 
////        "    </GrpHdr>\r\n" + 
////        "    <GrpBody>\r\n" + 
////        "      <StsRsn>\r\n" + 
////        "        <MechSeqId>33</MechSeqId>\r\n" + 
////        "        <MechDate>20141127</MechDate>\r\n" + 
////        "        <CenterSeqId>2014112710070125</CenterSeqId>\r\n" + 
////        "        <CenterDate>20141127</CenterDate>\r\n" + 
////        "        <CenterSuccessDate>20141127</CenterSuccessDate>\r\n" + 
////        "        <CenterSuccessTime>112737</CenterSuccessTime>\r\n" + 
////        "        <CenterCheckDate>20141127</CenterCheckDate>\r\n" + 
////        "        <BankCode>100000000003</BankCode>\r\n" + 
////        "        <BankNo>6222123456789</BankNo>\r\n" + 
////        "      </StsRsn>\r\n" + 
////        "    </GrpBody>\r\n" + 
////        "  </RespInfo>\r\n" + 
////        "</Document>";
//    
//    String errCode="";
//    String errMsg="";
//    
//    Document doc=null;
//    
//    try {
//      doc = DocumentHelper.parseText(xmlPacket);
//    } catch (DocumentException e) {
//      e.printStackTrace();
//      errCode="error";
//      errMsg="解析平台通知报文失败";
//      response.getWriter().write(errCode+"|"+errMsg);
//      return;
//    } 
//    log.info("----解析通知报文成功");
//    Element returnRoot = doc.getRootElement(); 
//    Element bodyElement=returnRoot.element("RespInfo").element("GrpBody");
//    String seqNo=bodyElement.element("StsRsn").element("MechSeqId").getTextTrim();
//    String payDate=bodyElement.element("StsRsn").element("CenterSuccessDate").getTextTrim();
//    String payTime=bodyElement.element("StsRsn").element("CenterSuccessTime").getTextTrim();
//    String payBankCode=bodyElement.element("StsRsn").element("BankCode").getTextTrim();
//    String payAccNo=bodyElement.element("StsRsn").element("BankNo").getTextTrim();
//    log.info("----流水"+seqNo+"于"+payDate+" "+payTime+"由"+payAccNo+"在"+payBankCode+"支付成功");
//    
//    Map<String, Object> map=szfsDbService.queryQrCodeOrder(seqNo);
//    if (map==null) {
//      errCode="error";
//      errMsg="订单流水不存在";
//      response.getWriter().write(errCode+"|"+errMsg);
//      return;
//    }
//    log.info("----检索本地流水成功");
//    map.put("centerPayDate", payDate);
//    map.put("centerPayTime", payTime);
//    map.put("centerPayBankCode", payBankCode);
//    map.put("centerPayAccNo", payAccNo);
//    
//    log.info("----开始通知商户支付结果");
//    qrCodePayNotifyService.setParams(map);
//    new Thread(qrCodePayNotifyService).start();
//    log.info("----通知商户支付结果结束");
//    
//    errCode="success";
//    errMsg="通知完成";
//    response.setCharacterEncoding("GBK");
//    response.getWriter().write(errCode+"|"+errMsg);
//    return;
//  }
//  
//  
//  @RequestMapping(value = "/szfsQrCodeOrderQuery")
//  public String  szfsQrCodeOrderQuery(final ModelMap model, @RequestParam Map<String, String> params) {
//    String errCode="";
//    String errMsg="";
//    String seqNo=params.get("seqNo");
//    Map<String, Object> map=szfsDbService.queryQrCodeOrder(seqNo);
//    if (map==null) {
//      errCode="dataErr";
//      errMsg="交易数据不存在";
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "api/szfsQrCodeOrderQuery";
//    }
//    
//    
//    Date now=new Date();
//    String creDtTm=new SimpleDateFormat("yyyy-MM-dd").format(now)+"T"+new SimpleDateFormat("HH:mm:ss").format(now);
//    String mechDate=new SimpleDateFormat("yyyyMMdd").format(now);
//    String mechTime=new SimpleDateFormat("HHmmss").format(now);
//    
//    java.sql.Timestamp createDateTamp=((java.sql.Timestamp)map.get("create_time"));
//    String orderDate=new SimpleDateFormat("yyyyMMdd").format(createDateTamp);
//    
//    params.put("mechNo",MERCH_NO);
//    params.put("creDtTm",creDtTm);
//    params.put("mechSeqId",seqNo);
//    params.put("orderDate",orderDate);
//    
//    
//    CloseableHttpClient http = HttpClients.createDefault();
//    Document document=null;
//    Document doc=null;
//    SAXReader reader = new SAXReader();
//    String xml="";
//    String returnStr="";
//    HttpComm httpComm=new HttpComm();
//    try {
//      document = reader.read(APIController.class.getClassLoader().getResourceAsStream("xml/SZFS_QRCODE_ORDER_QUERY.xml"));
//      xml=document.asXML().trim();
//      xml = MvelCompilers.eval(xml, params);
//      System.out.println("请求xml："+xml);
//    
//      StringBuffer bf=new StringBuffer(SecureUtils.signMsg(xml, "xml/szfs000000000001.pfx", "1aokp809", "GBK"));
//      
//      int length=bf.toString().length();
//      if (length<512) {
//        for (int i = 0; i <512-length; i++) {
//          bf.append(" ");
//        }
//      }
//      
//      StringBuffer sign=new StringBuffer();
//      sign.append("{S:");
//      sign.append(bf.toString());
//      sign.append("}\r\n");
//      
//      int mesgLong=512+xml.length();
//      String mesgLength=""+mesgLong;
//      mesgLength=StringUtil.stringFillLeftZero(mesgLength, 8);
//      
//      StringBuffer headerBuffer=new StringBuffer();
//      headerBuffer.append("{H:");
//      headerBuffer.append("01");
//      headerBuffer.append(MERCH_NO);
//      headerBuffer.append(CENTER_NO);
//      headerBuffer.append(mechDate);
//      headerBuffer.append(mechTime);
//      headerBuffer.append(mesgLength);
//      headerBuffer.append("31001");
//      headerBuffer.append("}\r\n");
//      log.info("报文头："+headerBuffer.toString());
//      
//      Map<String, String> httpParams=new HashMap<String, String>();
//      httpParams.put("Sender", headerBuffer.toString());
//      httpParams.put("CheckValue", sign.toString());
//      httpParams.put("Packet", xml);
//      
//      returnStr=httpComm.sendComm(http, SZFS_QRCODE_URL, httpParams, "GBK");
//      log.info("通讯返回："+returnStr);
//      String[] returnStrs=returnStr.split("}");
//      if (returnStrs.length<3) {
//        log.error("平台返回报文异常");
//        errCode="centerErr";
//        errMsg="平台返回报文异常";
//        model.put("errCode", errCode);
//        model.put("errMsg", errMsg);
//        return "api/szfsQrCodeOrderQuery";
//      }
//      String returnSign=returnStrs[1];
//      String returnXml=returnStrs[2];
//      
//      returnSign=returnSign.substring(3, returnSign.length()-1);
//      System.out.println("sign:"+returnSign.trim());
//      doc = DocumentHelper.parseText(returnXml); 
//    
//    } catch (Exception e) {
//      errCode="error";
//      errMsg=e.getMessage();
//      errMsg=errMsg.length()>200?errMsg.substring(0, 200):errMsg;
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg);
//      return "api/szfsQrCodeOrderQuery";
//    }finally{
//      httpComm.closeHttpClient(http);
//    }
//    
//    Element returnRoot = doc.getRootElement(); 
//    Element bodyElement=returnRoot.element("RespInfo").element("GrpBody");
//    String respCode=bodyElement.element("StsRsn").element("RespCode").getTextTrim();
//    String respDesc=bodyElement.element("StsRsn").element("RespDesc").getTextTrim();
//    
//    Element stsElement=bodyElement.element("StsRsnInf");
//    
//    if (!"90000".equals(respCode)||stsElement==null) {
//      errCode="centerQueryErr";
//      errMsg="中心查询失败";
//      model.put("errCode", errCode);
//      model.put("errMsg", errMsg+"["+respCode+"|"+respDesc+"]");
//      return "api/szfsQrCodeOrderQuery";
//    }
//    
//    String payStatus=stsElement.element("PayStatus").getTextTrim();  
//    String payStatusDesc=DictCache.getDictName("szfs_qrcode_pay_status", payStatus);
//    
//    errCode="success";
//    errMsg="交易成功";
//    model.put("errCode", errCode);
//    model.put("errMsg", errMsg);
//    model.put("seqNo", seqNo);
//    model.put("payStatus", payStatus);
//    model.put("payStatusDesc", payStatusDesc);
//    
//    //test
//    model.put("payStatus", "00000");
//    model.put("payStatusDesc", "已支付");
//    
//    return "api/szfsQrCodeOrderQuery";
//  }
//  
//  
//  
//    
//  
//  
//  private byte[] sign(String src,String charset) throws NoSuchAlgorithmException, 
//                    InvalidKeySpecException,InvalidKeyException, SignatureException, UnsupportedEncodingException{
//    
//    PrivateKey prikey=getPvkformPfx();
//    Signature signature = Signature.getInstance("SHA1WithRSA");
//    PKCS8EncodedKeySpec peks = new PKCS8EncodedKeySpec(prikey.getEncoded());
//    KeyFactory kf = KeyFactory.getInstance("rsa");
//    PrivateKey privateKey = kf.generatePrivate(peks);
//    signature.initSign(privateKey);
//    signature.update(src.getBytes(charset));
//    return signature.sign();
//  }
//  
//  
//  private  PrivateKey getPvkformPfx(){  
//    try {  
//        String pwd="1aokp809";
//        KeyStore ks = KeyStore.getInstance("PKCS12"); 
//        InputStream in=APIController.class.getClassLoader().getResourceAsStream("xml/szfs000000000001.pfx");
//        
//        ks.load(in, pwd.toCharArray());  
//        in.close();  
//        System.out.println("keystore type=" + ks.getType());  
//        Enumeration<String> enumas = ks.aliases();  
//        String keyAlias = null;  
//        if (enumas.hasMoreElements())
//        {  
//            keyAlias = (String)enumas.nextElement();   
//            System.out.println("alias=[" + keyAlias + "]");  
//        }  
//        System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));  
//        PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, pwd.toCharArray());  
//        
////        Certificate cert = ks.getCertificate(keyAlias);  
////        PublicKey pubkey = cert.getPublicKey();  
////        System.out.println("cert class = " + cert.getClass().getName());  
////        System.out.println("cert = " + cert);  
////        System.out.println("public key = " + pubkey);  
////        System.out.println("private key = " + prikey); 
//        return prikey; 
//    }  
//    catch (Exception e)  
//    {  
//        e.printStackTrace();  
//        return  null;
//    }  
//     
//}
//  
//  /**
//   * bytes转换成十六进制字符串
//   */
//  public static String byte2HexStr(byte[] b) {
//    String hs="";
//    String stmp="";
//    for (int n=0;n<b.length;n++) {
//      stmp=(Integer.toHexString(b[n] & 0XFF));
//      if (stmp.length()==1) hs=hs+"0"+stmp;
//      else hs=hs+stmp;
//      //if (n<b.length-1)  hs=hs+":";
//    }
//    return hs.toUpperCase();
//  }
//  
//  
//  public static void main(String[] args) {
//   
//  
//  }
  
  
}

