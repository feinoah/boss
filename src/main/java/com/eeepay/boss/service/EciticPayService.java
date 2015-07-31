package com.eeepay.boss.service;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.hxb.pub.map.DataMap;
import com.eeepay.hxb.pub.util.MvelCompilers;
import com.eeepay.hxb.pub.util.Tools;

/**
 *  描述：中信银企直连通道
 *  
 *  @author:ym
 *  创建时间：2015-03-11
 */

@Service
public class EciticPayService  {
  private static final Logger log = LoggerFactory.getLogger(EciticPayService.class);
  
//  public static final String ECITIC_URL="http://127.0.0.1:6789";
  public static final String ECITIC_URL="http://172.16.1.118:6789";
  public static final int CONN_TIME_OUT=40000;
  public static final int TIME_OUT=200000;
  public static final int SETTLE_TRANSFER_BATCH_NUM=200;
  
  //TODO   测试数据
//  public  final String ECITIC_BANK_USER_NAME="jt21";
//  public  final String ECITIC_OUT_ACC_NO="7328310182600000238";
//  public  final String ECITIC_OUT_ACC_NAME="深圳市移付宝科技有限公司";
//  public  final String ECITIC_OUT_BANK_NO="";
//  public  final String ECITIC_OUT_BANK_NAME="中信银行沙河支行";
//  public  final String ECITIC_OUT_SETTLE_BANK_NO="302100011000";
  
public  final String ECITIC_BANK_USER_NAME="yfbzl";
public  final String ECITIC_OUT_ACC_NO="7440610182600009236";
public  final String ECITIC_OUT_ACC_NAME="深圳市移付宝科技有限公司";
public  final String ECITIC_OUT_BANK_NO="";
public  final String ECITIC_OUT_BANK_NAME="中信银行深圳沙河支行";
public  final String ECITIC_OUT_SETTLE_BANK_NO="302100011000";
  
  @Resource
  private SettleTransferDBService settleTransferDBService;
  
  public Map<String, Object> eciticPay(Map<String, Object> params) {
    Map<String, Object> returnMap=new HashMap<String, Object>();
    
    ErrorVO error=this.eciticPaySave(params);
    String errCode=error.getErrCode();
    String errMsg=error.getErrMsg();
    if (!"success".equals(errCode)) {
      log.error("---->保存数据失败:"+errMsg);
      returnMap.put("errCode", errCode);
      returnMap.put("errMsg", errMsg);
      return returnMap;
    }
    log.error("---->保存数据成功:"+errMsg+",继续执行转账"); 
    
    String returnDate=error.getData().toString();
    String[] returnDatas=returnDate.split("#");
    String fileId=returnDatas[0];
    Map<String, String> map=new HashMap<String, String>();
    map.put("fileId", fileId);
    
    error=this.eciticPayTransfer(map);
    log.error("---->转账执行完毕:"+error.getErrCode()+"|"+error.getErrMsg());
    returnMap.put("fileId", fileId);
    returnMap.put("errCode", error.getErrCode());
    returnMap.put("errMsg", error.getErrMsg());
    return returnMap;
  }
  
  
	
  /**
   * 
   * 功能：转账数据保存
   *
   * @param params
   * @param model
   * @return
   */
  private ErrorVO eciticPaySave(Map<String, Object> params) {
    ErrorVO error=new ErrorVO();
    
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> transList=(List<Map<String, Object>>)params.get("transList");
    String userID=params.get("userID").toString();
    String userName=params.get("userName").toString();
    String summary=params.get("summary").toString();
    String totalAmount=params.get("totalAmount").toString();
    
    int cycDataCount=transList.size();
    Map<String,String> eciticParams=new HashMap<String, String>();
    eciticParams.put("fileName", userName+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    eciticParams.put("operatorId", userID);
    eciticParams.put("operatorName", userName);
    eciticParams.put("transferOperatorId", userID);
    eciticParams.put("transferOperatorName", userName);
    eciticParams.put("totalNum", ""+cycDataCount);
    eciticParams.put("totalAmount", totalAmount);
    eciticParams.put("summary", summary);
    eciticParams.put("settleBank", "ecitic");
    eciticParams.put("outAccNo", ECITIC_OUT_ACC_NO);
    eciticParams.put("outAccName", ECITIC_OUT_ACC_NAME);
    eciticParams.put("outBankNo", ECITIC_OUT_BANK_NO);
    eciticParams.put("outBankName", ECITIC_OUT_BANK_NAME);
    eciticParams.put("outSettleBankNo", ECITIC_OUT_SETTLE_BANK_NO);
    
    String fileId="";
    try {
      fileId = settleTransferDBService.insertTransferUploadFile(eciticParams);
    } catch (SQLException sqlException) {
      log.error("保存数据异常,请重试:"+sqlException.getMessage());
      error.setErrCode("saveErr");
      error.setErrMsg("保存数据失败，获取fileId失败");
      return error;
    }
    eciticParams.put("fileId", fileId);
    int totalSaveSuccDetailCount=0;
    
    int batchCount=SETTLE_TRANSFER_BATCH_NUM;
    int cycNum=cycDataCount/batchCount;
    int endCount=cycDataCount%batchCount;
    int endWidth=batchCount;
    if (endCount>0)  {
      cycNum++;
      endWidth=endCount;
    }
    
    //分批处理
    List<Map<String, String>> tempList=new ArrayList<Map<String,String>>();
    for (int i = 0; i < cycNum; i++) {
      tempList.clear();
      BigDecimal batchAmountBigDecimal=new BigDecimal("0");
      int cycWidth=batchCount;
      if (i==(cycNum-1)) {
        cycWidth=endWidth;
      }
      
      String batchNo = settleTransferDBService.getSettleTransferBatchId();
      if (StringUtil.isBlank(batchNo)) {
          log.error("获取批次号异常，保存数据失败");
          error.setErrCode("saveErr");
          error.setErrMsg("获取批次号异常，保存数据失败");
          return error;
      }
      
      for (int j = 0; j < cycWidth; j++) {
        
        Map<String, String> dataMap=new HashMap<String, String>();
        Map<String, Object> tempMap = transList.get(i * batchCount+ j);
        BigDecimal amountBigDecimal = new BigDecimal(tempMap.get("settle_amount").toString());
        batchAmountBigDecimal = batchAmountBigDecimal.add(amountBigDecimal);

        dataMap.put("seqNo", tempMap.get("id").toString());
        dataMap.put("amount", amountBigDecimal.toPlainString());
        dataMap.put("inSettleBankNo", tempMap.get("bank_no").toString());
        dataMap.put("inAccNo", tempMap.get("account_no").toString());
        dataMap.put("inAccName", tempMap.get("account_name").toString());

        tempList.add(dataMap);
      }
      
      eciticParams.put("batchId", batchNo);
      eciticParams.put("fileId", fileId);
      eciticParams.put("settleBank", "ecitic");
      int saveCount=settleTransferDBService.saveTranfer(eciticParams, tempList);
      totalSaveSuccDetailCount+=saveCount;
    }
    
    String errCode="";
    String errMsg="";
    if (totalSaveSuccDetailCount>0) {
      errCode="success";
      errMsg="成功保存"+totalSaveSuccDetailCount+"条转账记录";
    }else{
      errCode="saveErr";
      errMsg="结算数据保存失败";
    }
    
    error.setErrCode(errCode);
    error.setErrMsg(errMsg);
    error.setData(fileId);
    return error;
  }
  
  
  /**
   * 
   * 功能：  转账
   *
   * @param params
   * @param model
   * @return
   */
  private ErrorVO eciticPayTransfer(Map<String, String> params) {
    
    ErrorVO error=new ErrorVO();
    String fileId=params.get("fileId");
    
    int ret=0;
    List<Map<String, Object>> batchList=settleTransferDBService.queryTransBatchByFileId(fileId, "");
    log.info("---->fileId:"+fileId+"查询到："+batchList.size()+"个批次");
    for (Map<String, Object> map : batchList) {
      String batchNo=(String)map.get("batch_id");
      String status=(String)map.get("status");
      log.info("---->批次："+batchNo+"开始执行");
      
      if (!"0".equals(status)) {
        log.error("---->批次"+batchNo+"已经提交过，请勿再次提交");
        continue;
      }
      log.error("---->批次"+batchNo+"符合条件，继续转账操作");
      
      int tempRet=this.transferBatch(fileId,batchNo);
      ret+=tempRet;
    }
    String errCode="";
    String errMsg="";
    Map<String,String> param=new HashMap<String, String>();
    param.put("fileId", fileId);
    if (ret>0) {
      log.info("---->成功提交"+ret+"条转账明细");
      errCode="success";
      errMsg="批量转账提交成功";
      param.put("status", "1");
      param.put("errCode", errCode);
      param.put("errMsg", errMsg);
      settleTransferDBService.updateTranserFile(param);
    }else{
      log.info("---->没有转账明细数据成功提交");
      errCode="transferErr";
      errMsg="批量转账提交失败";
      param.put("status", "2");
      param.put("errCode", errCode);
      param.put("errMsg", errMsg);
      settleTransferDBService.updateTranserFile(param);
    }
    error.setErrCode(errCode);
    error.setErrMsg(errMsg);
    return error;
  }
  
  
  private int transferBatch(String fileId,String batchNo) {
    Map<String, Object> eciticParams=new HashMap<String, Object>();
    eciticParams.put("batchNo", batchNo);
    eciticParams.put("fileId", fileId);
    
    log.info("---->组织转账明细数据");
    List<Map<String, Object>> list=settleTransferDBService.queryTransByBatch(batchNo);
    int ret=0;
    for (Map<String, Object> tempMap : list) {
      String id=tempMap.get("id").toString();
      String inAccNo=tempMap.get("in_acc_no").toString();
      String inSettleBankNo=tempMap.get("in_settle_bank_no").toString();
      String clientID=ECITIC_BANK_USER_NAME+new SimpleDateFormat("yyyyMMdd").format(new Date())+id;
      tempMap.put("bankUserName", ECITIC_BANK_USER_NAME);
      tempMap.put("out_acc_no", ECITIC_OUT_ACC_NO);
      tempMap.put("abstract", "");
      tempMap.put("clientID", clientID);
      
      String transCode="";
      if ("302100011000".equals(inSettleBankNo)) {
        if (inAccNo.length()==16) {
          transCode="DLOTHSUB";//行内对私
        }else {
          transCode="DLINETRN";//行内对公
        }
      }else { 
        //对外转账
        transCode="DLOUTTRN";
        tempMap.put("bankFlag", "1");//他行
      }
      
      Document doc=null;
      String status="";
      String errCode="";
      String errMsg="";
      try {
        doc = this.eciticTrade(transCode, tempMap);
        log.info("---->处理中信银行转账返回结果");
        Element returnRoot = doc.getRootElement(); 
        errCode=returnRoot.elementTextTrim("status"); 
        errMsg=returnRoot.elementTextTrim("statusText"); 
        errMsg=errMsg.length()>240?errMsg.substring(0, 240):errMsg;
        if ("AAAAAAA".equals(errCode)||"AAAAAAB".equals(errCode)||"AAAAAAC".equals(errCode)
            ||"AAAAAAD".equals(errCode)||"AAAAAAE".equals(errCode)||"AAAAAAF".equals(errCode)
            ||"BBBBBBB".equals(errCode)||"CCCCCCC".equals(errCode)||"EEEEEEE".equals(errCode)
            ||"UNKNOWN".equals(errCode)) {
          status="1";
          ret++;
        }else {
          status="2";
        }
        settleTransferDBService.updateTransDetailById(id, status, errCode, errMsg);
      } catch (SocketTimeoutException el) {
        //通讯超时，将批量转账数据状态置为“已提交”，稍后查询结果状态，避免用户重复操作
        status="1";
        errCode="time_out";
        errMsg="超时,做提交成功处理，稍后查询获得具体结果";
        log.error("---->clientID+\"转账超时,请稍后查询转账结果:"+el.getMessage());
        settleTransferDBService.updateTransDetailById(id, status, errCode, errMsg);
      } catch (Exception e) {
        e.printStackTrace();
        status="2";
        errCode="exception";
        errMsg=e.getMessage();
        errMsg=errMsg.length()>240?errMsg.substring(0, 240):errMsg;
        log.error("---->"+clientID+"提交转账异常:"+e.getMessage());
        settleTransferDBService.updateTransDetailById(id, status, errCode, errMsg);
      }
      
    }
    Map<String, String> newMap=new HashMap<String, String>();
    newMap.put("batchId", batchNo);
    newMap.put("status", "1");
    newMap.put("errCode", "");
    newMap.put("errMsg", "");
    settleTransferDBService.updateTransBatch(newMap);
    log.info("---->修改批次"+batchNo+"为已提交"); 
    return ret;
    
  }
  
  
  
  /**
   * 
   * 功能：根据fileId查询银行转账结果
   *
   * @param params
   * @param model
   * @return
   */
  public List<Map<String, Object>> getTransferByFileEcitic(String cashFileId) {
    List<Map<String, Object>> transferList=new ArrayList<Map<String, Object>>();
    
    Map<String, String> params=new HashMap<String, String>();
    params.put("fileId", cashFileId);
    Map<String, Object> fileMap= settleTransferDBService.queryFileById(cashFileId);
    java.sql.Timestamp transferDateTamp=((java.sql.Timestamp)fileMap.get("transfer_time"));
    String transferDate=new SimpleDateFormat("yyyyMMdd").format(transferDateTamp);
    
    List<Map<String,Object>> transList=settleTransferDBService.queryTransByFileId(params);
    for (Map<String, Object> map : transList) {
      map.put("transferDate", transferDate);
      Map<String, Object> statusMap=this.getTransferStatisEcitic(map);
      transferList.add(statusMap);
      
    }
    return transferList;
  }
  
  
  
  /**
   * 
   * 功能：从银企直连前置查询批量转账记录
   *     银行状态： 0 成功 1 失败 2未知 3审核拒绝 4 用户撤销
   *     返回状态；  0 ：失败  1：成功  2：未知
   * @return
   */
  private Map<String, Object> getTransferStatisEcitic(Map<String, Object> map) {
    Map<String, Object> returnMap=new HashMap<String, Object>();
    String id=map.get("id").toString();
    String inAccNo=map.get("in_acc_no").toString();
    String inSettleBankNo=map.get("in_settle_bank_no").toString();
    String transferDate=map.get("transferDate").toString();
    String fileId=map.get("file_id").toString();
    String seqNo=map.get("seq_no").toString();
    
    String transCode="DLCIDSTT";
    if ("302100011000".equals(inSettleBankNo)&&inAccNo.length()==16) {
      transCode="DLOTHDET";//行内对私
    }
    
    map.put("clientID", ECITIC_BANK_USER_NAME+transferDate+id);
    map.put("bankUserName", ECITIC_BANK_USER_NAME);
    Document doc=null;
    
    try {
      doc = this.eciticTrade(transCode, map);
      Element xmlRoot = doc.getRootElement();
      String status=xmlRoot.elementTextTrim("status");
      String statusText=xmlRoot.elementTextTrim("statusText");
      if (!"AAAAAAA".equals(status)) {
        //DLOTHDET接口 status=ED02109  表示无制单信息，须状态置为失败
        //DLCIDSTT接口 status=ED02083  表示输入的客户流水号无制单信息,请检查输入项
        if ("ED02109".equals(status)||"ED02083".equals(status)) {
          returnMap.put("file_id", fileId);
          returnMap.put("seq_no", seqNo);
          returnMap.put("bankStatus", "0");
          returnMap.put("bankCode", status);
          returnMap.put("bankMsg", statusText);
          return returnMap;
        }else {
          log.info("查询状态交易失败:"+statusText);
          throw new Exception(statusText+"["+status+"]");
        }
      }
      
      Map<String, String> xmlMap = new HashMap<String, String>();  
      xmlMap.put("default", doc.getRootElement().getNamespaceURI());  
      XPath x = doc.createXPath("//default:stream/default:list/default:row");
      x.setNamespaceURIs(xmlMap);
      @SuppressWarnings("unchecked")
      List<Node> transList=x.selectNodes(doc);
      
      //单笔交易，循环数据只取第一条
      Node node=transList.get(0);
      Element element=(Element)node;
      String stt=element.elementTextTrim("stt");
      String statusDetail=element.elementTextTrim("status");
      String statusMsg=element.elementTextTrim("statusText");
      if ("DLCIDSTT".equals(transCode)) {
        if ("0".equals(stt)) {//成功
          stt="1";
        }else if ("1".equals(stt)) {//失败
          stt="0";
        }else {//未知
          stt="2";
        }
        
      }else if ("DLOTHDET".equals(transCode)) {//对私，其他代付查询
        if ("AAAAAAA".equals(statusDetail)) {
          stt="1";//成功
        }else if ("AAAAAAB".equals(statusDetail)||"AAAAAAC".equals(statusDetail)
        ||"AAAAAAD".equals(statusDetail)||"AAAAAAE".equals(statusDetail)||"AAAAAAF".equals(statusDetail)
        ||"BBBBBBB".equals(statusDetail)||"CCCCCCC".equals(statusDetail)||"EEEEEEE".equals(statusDetail)
        ||"UNKNOWN".equals(statusDetail)) {
          stt="2";//未知
        }else {
          stt="0";//失败
        }
      }
      returnMap.put("file_id", fileId);
      returnMap.put("seq_no", seqNo);
      returnMap.put("bankStatus", stt);
      returnMap.put("bankCode", statusDetail);
      returnMap.put("bankMsg", statusMsg);
      
    } catch (Exception e) {
      log.error("流水：["+id+"]查询转账结果失败:"+e.getMessage());
      returnMap.put("file_id", fileId);
      returnMap.put("seq_no", seqNo);
      returnMap.put("bankStatus", "2");
      returnMap.put("bankCode", "syc_exception");
      returnMap.put("bankMsg", e.getMessage());
    }
    
    return returnMap;
    
  }
  
  
  
  private Document eciticTrade(String transCode,Map<String, Object> map)throws SocketTimeoutException,Exception {
    Document document=null;
    SAXReader reader = new SAXReader();
    String returnXML="";
    Document doc=null;
    try {
      document = reader.read(EciticPayService.class.getClassLoader().getResourceAsStream("xml/ECITIC_"+transCode+".xml"));
    } catch (DocumentException e) {
      log.info("解析XML模板失败[XML_PARSE_ERR]");
      throw new Exception("解析XML模板失败[XML_PARSE_ERR]");
    }  
    String xml=document.asXML();
    try {
      xml=MvelCompilers.eval(xml, map);
      returnXML=httpPostXML(xml);
      if (Tools.isStrEmpty(returnXML)) {
        throw new Exception("通讯失败");
      }
      doc = DocumentHelper.parseText(returnXML);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("交易异常:"+e.getMessage());
      throw e;
    } 
    
    return doc;
  }
  
  
  
  private String httpPostXML(String xml)throws SocketTimeoutException,Exception {
    HttpURLConnection httpConn  =  null;
    try{
      URL postUrl = new URL(ECITIC_URL);
      // 打开连接   
      httpConn = (HttpURLConnection) postUrl.openConnection();
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);
      httpConn.setRequestMethod("POST");
      httpConn.setUseCaches(false);
      httpConn.setInstanceFollowRedirects(true);
      httpConn.setRequestProperty("Content-Type", "application/octet-stream; charset=GBK");
      httpConn.setConnectTimeout(CONN_TIME_OUT);
      httpConn.setReadTimeout(TIME_OUT);
      // 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，   
      // 要注意的是connection.getOutputStream会隐含的进行 connect。   
      DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
      log.info("向中信银行"+ECITIC_URL+"发送报文："+xml);
      out.write(xml.getBytes("GBK"));
      out.flush();
      out.close();
      int status = httpConn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        return "";
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(   
          httpConn.getInputStream(),"GBK"));
      StringBuffer responseSb =new StringBuffer();
      String line = null;
      while ((line = reader.readLine()) != null) {   
        responseSb.append(line.trim());
      }
      reader.close();
      log.info("中信银行返回报文："+responseSb.toString().trim());
      return responseSb.toString().trim();
    } catch (SocketTimeoutException e) {
      throw e;
    }catch(Exception ex){
      throw ex;
    }finally{
      httpConn.disconnect();   
    }
    
  }
	
}
