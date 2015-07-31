package com.eeepay.boss.service;
import java.io.BufferedReader;
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
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.boss.encryptor.md5.Md5;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;

/**
 *  描述：民生厦门直连
 *  
 *  @author:ym
 *  创建时间：2015-06-30
 */

@Service
public class CmbcXMPayService  {
  private static final Logger log = LoggerFactory.getLogger(CmbcXMPayService.class);
  
  @Resource
  private SettleTransferDBService settleTransferDBService;
  public static final int SETTLE_TRANSFER_BATCH_NUM=200;
  
  private static final String SECRETKEY="71552E3475470396E0531D05ACC68660";
  
  public  final String CMBC_XM_OUT_ACC_NO="693568719";
  public  final String CMBC_XM_OUT_ACC_NAME="深圳市移付宝科技有限公司";
  public  final String CMBC_XM_OUT_BANK_NO="";
  public  final String CMBC_XM_OUT_BANK_NAME="";
  public  final String CMBC_XM_OUT_SETTLE_BANK_NO="305100000013";
  
  
  /**
   * 
   * 功能：
   *
   * @param model
   * @param params
   */
  public Map<String, Object> cmbcXMPay(Map<String, Object> params) {
    Map<String, Object> returnMap=new HashMap<String, Object>();
    ErrorVO error=this.cmbcXMPaySave(params);
    String errCode=error.getErrCode();
    String errMsg=error.getErrMsg();
    if (!"success".equals(errCode)) {
      log.error("---->保存数据失败:"+errMsg);
      returnMap.put("errCode", errCode);
      returnMap.put("errMsg", errMsg);
      return returnMap;
    }
    log.info("---->保存数据成功:"+errMsg+",继续执行转账"); 
    
    String returnDate=error.getData().toString();
    String[] returnDatas=returnDate.split("#");
    String fileId=returnDatas[0];
    Map<String, String> map=new HashMap<String, String>();
    map.put("fileId", fileId);
    
    error=this.cmbcXMPayTransfer(map);
    log.info("---->转账执行完毕:"+error.getErrCode()+"|"+error.getErrMsg());
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
  private ErrorVO cmbcXMPaySave(Map<String, Object> params) {
    ErrorVO error=new ErrorVO();
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> transList=(List<Map<String, Object>>)params.get("transList");
    String userID=params.get("userID").toString();
    String userName=params.get("userName").toString();
    String summary=params.get("summary").toString();
    String totalAmount=params.get("totalAmount").toString();
    String payBankChannel=params.get("payBankChannel").toString();
    
    int cycDataCount=transList.size();
    Map<String,String> cmbcXMParams=new HashMap<String, String>();
    cmbcXMParams.put("fileName", userName+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    cmbcXMParams.put("operatorId", userID);
    cmbcXMParams.put("operatorName", userName);
    cmbcXMParams.put("transferOperatorId", userID);
    cmbcXMParams.put("transferOperatorName", userName);
    cmbcXMParams.put("totalNum", ""+cycDataCount);
    cmbcXMParams.put("totalAmount", totalAmount);
    cmbcXMParams.put("summary", summary);
    cmbcXMParams.put("settleBank", payBankChannel);
    cmbcXMParams.put("outAccNo", CMBC_XM_OUT_ACC_NO);
    cmbcXMParams.put("outAccName", CMBC_XM_OUT_ACC_NAME);
    cmbcXMParams.put("outBankNo", CMBC_XM_OUT_BANK_NO);
    cmbcXMParams.put("outBankName", CMBC_XM_OUT_BANK_NAME);
    cmbcXMParams.put("outSettleBankNo", CMBC_XM_OUT_SETTLE_BANK_NO);
   
    String fileId="";
    try {
      fileId = settleTransferDBService.insertTransferUploadFile(cmbcXMParams);
      
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
      log.error("保存数据异常,请重试:"+sqlException.getMessage());
      error.setErrCode("saveErr");
      error.setErrMsg("保存数据失败，获取fileId失败");
      return error;
    }
    cmbcXMParams.put("fileId", fileId);
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
      
      cmbcXMParams.put("batchId", batchNo);
      cmbcXMParams.put("fileId", fileId);
      cmbcXMParams.put("settleBank", "cmbc_xm_api");
      int saveCount=settleTransferDBService.saveTranfer(cmbcXMParams, tempList);
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
  private ErrorVO cmbcXMPayTransfer(Map<String, String> params) {
    
    ErrorVO error=new ErrorVO();
    String fileId=params.get("fileId");
    
    
    Map<String, Object> fileMap=settleTransferDBService.queryFileById(fileId);
    String fileStatus=(String)fileMap.get("status");
    if (!"0".equals(fileStatus)) {
      log.error("---->fileId:"+fileId+"已经提交过，请勿再次提交");
      error.setErrCode("file_id_error");
      error.setErrMsg("已提交过，请勿重复提交");
      return error;
    }
    
    int ret=this.transferByFile(fileId);
    
    String errCode="";
    String errMsg="";
    Map<String,String> param=new HashMap<String, String>();
    param.put("fileId", fileId);
    if (ret>0) {
      log.info("---->成功提交"+ret+"条转账明细");
      errCode="success";
      errMsg="批量转账提交成功";
//      param.put("status", "1");
//      param.put("errCode", errCode);
//      param.put("errMsg", errMsg);
//      settleTransferDBService.updateTranserFile(param);
    }else{
      log.info("---->没有转账明细数据成功提交");
      errCode="transferErr";
      errMsg="批量转账提交失败";
//      param.put("status", "2");
//      param.put("errCode", errCode);
//      param.put("errMsg", errMsg);
//      settleTransferDBService.updateTranserFile(param);
    }
    
    error.setErrCode(errCode);
    error.setErrMsg(errMsg);
    return error;
  }
  
  
  /**
   * 
   * 功能：向代付直连接口发送请求
   *
   * @param fileId
   * @return
   */
  private int transferByFile(String fileId) {
    log.info("---->组织转账明细数据");
    
    String sign=Md5.md5Str(fileId+SECRETKEY);
    Map<String, String> cmbcXMParams=new HashMap<String, String>();
    cmbcXMParams.put("cashFileId", fileId);
    cmbcXMParams.put("sign", sign);
    
    String resultStr="";
    JSONObject returnJson=null;
    String status="";
    String errCode="";
    String errMsg="";
    int successRet=0;
    try {
      String cmbc_xm_api_url=SysConfig.value("cmbc_xm_api_url");
      resultStr=this.httpGet(cmbc_xm_api_url+"/cmbcxm/cmbcXMPay",cmbcXMParams);
      returnJson=JSONObject.fromObject(resultStr);
      
      status=returnJson.getString("status");
      errCode=returnJson.getString("errCode");
      errMsg=returnJson.getString("errMsg");
      
      status="0".equals(status)?"2":status;
      if ("1".equals(status)) {
        successRet=1;
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (e instanceof SocketTimeoutException) {
        log.error("---->fileId:"+fileId+"提交转账超时,请稍后查询转账结果:"+e.getMessage());
        status="1";
        errCode="time_out";
        errMsg="超时,做提交成功处理，稍后查询获得具体结果";
      }else if(e instanceof JSONException){
        log.error("---->fileId:"+fileId+"处理返回结果异常，当做成功处理,请稍后查询转账结果:"+e.getMessage());
        status="1";
        errCode="json_error";
        errMsg="处理返回结果异常，当做成功处理";
      }else {
        log.error("---->fileId:"+fileId+"提交转账失败,请稍后查询转账结果:"+e.getMessage());
        status="2";
        errCode="exception";
        errMsg=e.getMessage();
        errMsg=errMsg.length()>240?errMsg.substring(0, 240):errMsg;
      }
      
    }
    
    Map<String, String> newMap=new HashMap<String, String>();
    newMap.put("fileId", fileId);
    newMap.put("status", status);
    newMap.put("errCode", errCode);
    newMap.put("errMsg", errMsg);
    
    settleTransferDBService.updateTranserFile(newMap);
    settleTransferDBService.updateTransBatchByFileId(newMap);
    //此处可能存在[厦门民生单笔直连代付接口线程]接收到银行返回后已经修改明细流水的状态，与此处出现多线程数据不安全的问题
    //为了避免上述问题，此处修改明细流水的状态时添加明细状态status='0'[未提交] 的条件
    int retDetail=settleTransferDBService.updateTransDetailByFileIdCmbcXMApi(newMap);
    log.info("---->fileId"+fileId+"状态修改为["+status+"],"+retDetail+"条明细状态修改为["+status+"]"); 
    return successRet;
    
  }
  
  
  
  /**
   * 
   * 功能：根据fileId查询银行转账结果
   *
   * @param params
   * @param model
   * @return
   */
  public List<Map<String, Object>> getTransferByFileCmbcXM(String cashFileId) {
    Map<String, String> params=new HashMap<String, String>();
    params.put("fileId", cashFileId);
    List<Map<String, Object>> transUnKonwList=settleTransferDBService.queryTransUNByFileId(cashFileId);
    if (transUnKonwList.size()>0) {
      log.info("同步交易结果未知的明细，共"+transUnKonwList.size()+"条");
      String sign=Md5.md5Str(cashFileId+SECRETKEY);
      Map<String, String> cmbcXMParams=new HashMap<String, String>();
      cmbcXMParams.put("cashFileId", cashFileId);
      cmbcXMParams.put("sign", sign);
      try {
        String cmbc_xm_api_url=SysConfig.value("cmbc_xm_api_url");
        this.httpGet(cmbc_xm_api_url+"/cmbcxm/sycCmbcXMStatus",cmbcXMParams);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    List<Map<String, Object>> transList=settleTransferDBService.queryTransByFileId(params);
    
    //将本地明细状态翻译成：  bankStatus,bankErrCode,bankErrMsg
    String bankStatus="";
    for (int i = 0; i < transList.size(); i++) {
      Map<String, Object> map=transList.get(i);
      
      String status=(String)map.get("status");
      if ("4".equals(status)) {
        bankStatus="1"; //成功
      }else if ("2".equals(status)||"5".equals(status)) {
        bankStatus="0"; //失败
      }else {
        bankStatus="2"; //未知
      }
      map.put("bankStatus", bankStatus);
      map.put("bankCode", map.get("err_code"));
      map.put("bankMsg", map.get("err_msg"));
    }
    
    return transList;
  }
  
  
  
  
  /**
   * 功能：
   *
   * @param params
   * @return
   * @throws SocketTimeoutException
   * @throws Exception
   */
  private String httpGet(String url,Map<String, String> params)throws SocketTimeoutException,Exception {
    HttpURLConnection httpConn = null;
    StringBuffer sb = new StringBuffer();
    Set<String> paramsSet = params.keySet();
    for (String key : paramsSet) {
      sb.append("&" + key + "=" + params.get(key));
    }

    url = url + "?" + sb.toString().substring(1);
    try {
      URL postUrl = new URL(url);
      // 打开连接
      httpConn = (HttpURLConnection) postUrl.openConnection();
      httpConn.setConnectTimeout(40000);
      httpConn.setReadTimeout(200000);
      log.info("发送：" + url);
      int status = httpConn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        return "";
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));

      StringBuffer responseSb = new StringBuffer();
      String line = null;
      while ((line = reader.readLine()) != null) {
        responseSb.append(line.trim());
      }
      reader.close();
      return responseSb.toString().trim();
    } catch (SocketTimeoutException e) {
      throw e;
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      httpConn.disconnect();
    }
    
  }
  
	
}
