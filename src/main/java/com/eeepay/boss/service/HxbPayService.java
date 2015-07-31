package com.eeepay.boss.service;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.boss.domain.HxbTransComparator;
import com.eeepay.boss.utils.DataStaticCache;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.hxb.b2e.HxbComm;
import com.eeepay.hxb.pub.map.DataMap;
import com.eeepay.hxb.pub.util.HxbConfig;

/**
 *  描述：华夏银企直连通道
 *  
 *  @author:ym
 *  创建时间：2014-10-27
 */

@Service
public class HxbPayService  {
  private static final Logger log = LoggerFactory.getLogger(HxbPayService.class);
  
  @Resource
  private HxbDBService hxbDBService;
  
  public Map<String, Object> hxbPay(Map<String, Object> params) {
    Map<String, Object> returnMap=new HashMap<String, Object>();
    
    ErrorVO error=this.hxbPaySave(params);
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
    
    error=this.hxbPayTransfer(map);
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
  private ErrorVO hxbPaySave(Map<String, Object> params) {
    ErrorVO error=new ErrorVO();
    
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> transList=(List<Map<String, Object>>)params.get("transList");
    String userID=params.get("userID").toString();
    String submitChannel=params.get("submitChannel").toString();
    String userName=params.get("userName").toString();
    String transferType=params.get("transType").toString(); 
    String summary=params.get("summary").toString();
    String totalAmount=params.get("totalAmount").toString();
    
    //TODO 暂时使用固定的出款账号
    String outAccId="hxbaccountnumbert"; 
//    String outAccId=DataStaticCache.getHxbOutAccAtIndex(); 
    String outBankNo  =HxbConfig.getProperties("hxb_b2e_eeepay_outbank_no"+"_"+outAccId);//出款行号
    String outBankName=HxbConfig.getProperties("hxb_b2e_eeepay_outbank_name"+"_"+outAccId);//出款行名
    String outAccNo   =HxbConfig.getProperties("hxb_b2e_eeepay_outacc_no"+"_"+outAccId);//出款账号
    String outAccName =HxbConfig.getProperties("hxb_b2e_eeepay_outacc_name"+"_"+outAccId);//出款账户名
  
    int cycDataCount=transList.size();
    DataMap hxbParams=new DataMap();
    hxbParams.add("fileName", userName+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    hxbParams.add("operatorId", userID);
    hxbParams.add("operatorName", userName);
    hxbParams.add("totalNumber", ""+cycDataCount);
    hxbParams.add("totalRowCount", ""+cycDataCount);
    hxbParams.add("illegalCount", "0");
    hxbParams.add("totalAmount", totalAmount);
    hxbParams.add("summary", summary);
    hxbParams.add("outBankNo", outBankNo);
    hxbParams.add("outBankName", outBankName);
    hxbParams.add("outAccNo", outAccNo);
    hxbParams.add("outAccName", outAccName);
    hxbParams.add("transferType", transferType);
    hxbParams.add("submitChannel", submitChannel);
    
    String fileId="";
    try {
      fileId = hxbDBService.insertTransferUploadFile(hxbParams);
    } catch (SQLException sqlException) {
      log.error("保存数据异常,请重试:"+sqlException.getMessage());
      error.setErrCode("saveErr");
      error.setErrMsg("保存数据失败，获取fileId失败");
      return error;
    }
    hxbParams.add("fileId", fileId);
    int totalSaveSuccDetailCount=0;
    
    int batchCount=Integer.parseInt(HxbConfig.getProperties("hxb_b2e_transfer_batch_count"));
    int cycNum=cycDataCount/batchCount;
    int endCount=cycDataCount%batchCount;
    int endWidth=batchCount;
    if (endCount>0)  {
      cycNum++;
      endWidth=endCount;
    }
    
    //分批处理
    List<DataMap> tempList=new ArrayList<DataMap>();
    for (int i = 0; i < cycNum; i++) {
      tempList.clear();
      BigDecimal batchAmountBigDecimal=new BigDecimal("0");
      int cycWidth=batchCount;
      if (i==(cycNum-1)) {
        cycWidth=endWidth;
      }
      
      String batchNo = hxbDBService.getHxbTransferBatchNo();
      if (StringUtil.isBlank(batchNo)) {
          log.error("获取批次号异常，保存数据失败");
          error.setErrCode("saveErr");
          error.setErrMsg("获取批次号异常，保存数据失败");
          return error;
      }
      
      for (int j = 0; j < cycWidth; j++) {
        String flowNo = hxbDBService.getHxbFlowNo();
        if (StringUtil.isBlank(flowNo)) {
          log.error("获取交易流水号异常，保存数据失败");
          error.setErrCode("saveErr");
          error.setErrMsg("获取交易流水号异常，保存数据失败");
          return error;
        }
        
        DataMap dataMap=new DataMap();
        Map<String, Object> tempMap = transList.get(i * batchCount+ j);
        BigDecimal amountBigDecimal = new BigDecimal(tempMap.get("settle_amount").toString());
        batchAmountBigDecimal = batchAmountBigDecimal.add(amountBigDecimal);

        dataMap.add("seqNo", tempMap.get("id"));
        dataMap.add("flowNo", flowNo);
        dataMap.add("amount", amountBigDecimal.toPlainString());
        dataMap.add("inBankNo", tempMap.get("bank_no"));
        dataMap.add("inAccNo", tempMap.get("account_no"));
        dataMap.add("inAccName", tempMap.get("account_name"));

        tempList.add(dataMap);
      }
      
      hxbParams.set("batchNo", batchNo);
      hxbParams.set("batchTotalNumber", tempList.size());
      hxbParams.set("batchTotalAmount", batchAmountBigDecimal.toPlainString());
      int saveCount=hxbDBService.saveTranfer(hxbParams, tempList);
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
//    String duplicateTimeStamp= DuplicateSubmitCheck.setDuplicateTime();
//    error.setData(fileId+"#"+duplicateTimeStamp);
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
  private ErrorVO hxbPayTransfer(Map<String, String> params) {
    
    ErrorVO error=new ErrorVO();
    String fileId=params.get("fileId");
    
    int ret=0;
    List<Map<String, Object>> batchList=hxbDBService.queryTransferBatchByFileId(fileId);
    log.info("---->fileId:"+fileId+"查询到："+batchList.size()+"个批次");
    for (Map<String, Object> map : batchList) {
      String batchNo=(String)map.get("batch_no");
      log.info("---->批次号："+batchNo+"开始转账");
      int tempRet=this.transferBatch(batchNo);
      ret+=tempRet;
    }
    String errCode="";
    String errMsg="";
    DataMap param=new DataMap();
    param.add("fileId", fileId);
    if (ret>0) {
      log.info("---->成功提交"+ret+"条转账明细");
      errCode="success";
      errMsg="批量转账提交成功";
      param.add("status", "1");
      param.add("errCode", errCode);
      param.add("errMsg", errMsg);
      hxbDBService.updateTranserFileUpload(param);
    }else{
      log.info("---->没有转账明细数据成功提交");
      errCode="transferErr";
      errMsg="批量转账提交失败";
      param.add("status", "2");
      param.add("errCode", errCode);
      param.add("errMsg", errMsg);
      hxbDBService.updateTranserFileUpload(param);
    }
    error.setErrCode(errCode);
    error.setErrMsg(errMsg);
    return error;
  }
  
  
  private int transferBatch(String batchNo) {
    Map<String, Object> map=hxbDBService.queryTransferBatchByBatchNo(batchNo);
    String transferType=(String)map.get("transfer_type");
    String status=(String)map.get("status");
    if (!"0".equals(status)) {
      log.error("---->批次"+batchNo+"已经提交过，请勿再次提交");
      return 0;
    }
    log.error("---->批次"+batchNo+"符合条件，继续转账操作");
    DataMap hxbParams=new DataMap();
    hxbParams.add("batchNo", batchNo);
    hxbParams.add("fileId", map.get("file_id"));
    hxbParams.add("operatorName", map.get("operator_name"));
    hxbParams.add("totalAmount", map.get("total_amt"));
    hxbParams.add("totalNumber", map.get("total_num"));
    hxbParams.add("summary", map.get("summary"));
    hxbParams.add("outAccNoType", HxbConfig.getProperties("hxb_b2e_eeepay_outacc_type"));//出款账户类型
    
    log.info("---->组织转账明细数据");
    List<Map<String, Object>> list=hxbDBService.queryTransferDetailByBatchNo(batchNo);
    List<DataMap> cycData=new ArrayList<DataMap>();
    for (Map<String, Object> tempMap : list) {
      DataMap dataMap=new DataMap();
      dataMap.add("seqNo", (String)tempMap.get("seq_no"));
      dataMap.add("flowNo", (String)tempMap.get("flow_no"));
      dataMap.add("outBankNo", (String)tempMap.get("out_bank_no"));
      dataMap.add("outBankName", (String)tempMap.get("out_bank_name"));
      dataMap.add("outAccNo", (String)tempMap.get("out_acc_no"));
      dataMap.add("outAccName", (String)tempMap.get("out_acc_name"));
      dataMap.add("inBankNo", (String)tempMap.get("in_bank_no"));
      dataMap.add("inBankName", (String)tempMap.get("in_bank_name"));
      String inAccNo=(String)tempMap.get("in_acc_no");
      dataMap.add("inAccNo", inAccNo);
      dataMap.add("inAccName", (String)tempMap.get("in_acc_name"));
      dataMap.add("amount", ((BigDecimal)tempMap.get("amount")).toString());
      
      if ("in".equals(transferType)) {
        dataMap.add("inBankAddress", "");
        dataMap.add("mobileNo", "");
        String transType="4";  // 行内异地
        String inAccNoPre=inAccNo.substring(0, 3);
        if ("108".equals(inAccNoPre)) { // 深圳同城
          transType="3";
        }
        dataMap.add("transType", transType);
      }
      cycData.add(dataMap);
    }
    
    String transCode="";
    if ("in".equals(transferType)) {
      transCode="xhj3011";//华夏本行
      log.info("---->华夏本行转账");
    }else if("out".equals(transferType)){
      transCode="xhj2012";//华夏跨行
      log.info("---->华夏跨行转账");
    }
    hxbParams.set("cycData", cycData);
    HxbComm hxbComm=new HxbComm();
    DataMap returnMap=null;
    try {
      log.info("---->向华夏银行发送批次号："+batchNo+" 转账请求");
      returnMap=hxbComm.clientDoComm(transCode, hxbParams, 0);
      log.info("---->向华夏银行发送转账完毕");
    } catch (SocketTimeoutException el) {
      //通讯超时，将批量转账数据状态置为“已提交”，稍后查询结果状态，避免用户重复操作
      hxbParams.set("status", "1");
      hxbDBService.updateTranserBatch(hxbParams);
      hxbDBService.updateTranserDetail(hxbParams);
      String errMsg=batchNo+"批量转账超时,请稍后查询转账结果:"+el.getMessage();
      log.error("---->"+errMsg);
      return list.size();
    } catch (Exception e) {
      e.printStackTrace();
      String errMsg=batchNo+"批量转账异常:"+e.getMessage();
      hxbParams.set("status", "2");
      hxbParams.set("errCode", "exception");
      hxbParams.set("errMsg", e.getMessage());
      hxbDBService.updateTranserBatch(hxbParams);
      hxbDBService.updateTranserDetail(hxbParams);
      log.error("---->"+errMsg);
      return 0;
    }
    
    //修改批量转账记录状态+新增批量转账明细记录
    log.info("---->处理华夏银行转账返回结果");
    String returnCode=returnMap.getString("response_code");
    if ("000000".equals(returnCode)) {
      hxbParams.set("status", "1");
      hxbParams.set("errCode", "success");
      hxbParams.set("errMsg", returnCode);
      hxbDBService.updateTranserBatch(hxbParams);
      hxbDBService.updateTranserDetail(hxbParams);
      log.info("---->"+batchNo+"批量转账提交成功,请稍后查询转账结果");
    }else {
      hxbParams.set("status", "2");
      hxbParams.set("errCode", "bank_error");
      hxbParams.set("errMsg", returnCode);
      hxbDBService.updateTranserBatch(hxbParams);
      hxbDBService.updateTranserDetail(hxbParams);
      log.info("---->"+batchNo+"批量转账提交失败,银行返回："+returnCode);
    }
    return list.size();
    
  }
  
  
  
  /**
   * 
   * 功能：根据fileId查询银行转账结果
   *
   * @param params
   * @param model
   * @return
   */
  public List<Map<String, Object>> getTransferFileHXB(String cashFileId) {
    List<Map<String, Object>> transferList=new ArrayList<Map<String, Object>>();
    
    Map<String, String> params=new HashMap<String, String>();
    params.put("fileId", cashFileId);
    List<Map<String,Object>> batchList=hxbDBService.queryTransferBatch(params);
    for (Map<String, Object> map : batchList) {
      String status=(String)map.get("status");
      if (!"1".equals(status)) {
        continue;
      }
      String batchNo=(String)map.get("batch_no");
      java.sql.Timestamp transferDateTamp=((java.sql.Timestamp)map.get("transfer_time"));
      String transferDate=new SimpleDateFormat("yyyyMMdd").format(transferDateTamp);
      
      List<Map<String, Object>> tempList=this.getTransferBatchHXB(batchNo,transferDate);
      transferList.addAll(tempList);
      
    }
    return transferList;
  }
  
  
  
  /**
   * 
   * 功能：从银企直连前置查询批量转账记录
   *     银行状态：  01成功、09失败、06已受理  05待发送 中间状态
   *     返回状态；  0 ：失败  1：成功  2：未知
   * @return
   */
  private List<Map<String, Object>> getTransferBatchHXB(String batchNo,String entryDate) {
    List<Map<String, Object>> list=hxbDBService.queryTransferDetailByBatchNo(batchNo);
    String bankStatus="";
    String bankCode="";
    String bankMsg="";
    DataMap hxbParams=new DataMap();
    hxbParams.add("batchNo", batchNo);
    hxbParams.add("entryDate", entryDate);
    
    HxbComm hxbComm=new HxbComm();
    DataMap returnMap=new DataMap();
    try {
      returnMap=hxbComm.clientDoComm("xhj5002", hxbParams, 0);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("批次["+batchNo+"]查询批量转账结果异常:"+e.getMessage());
      for (Map<String, Object> map: list) {
        map.put("bankStatus", "2");
        map.put("bankCode", "syc_exception");
        map.put("bankMsg", "同步银行结果异常");
      }
      return list;
    }
    
    String returnCode=returnMap.getString("response_code");
    if (!"000000".equals(returnCode)) {
      log.error("批次["+batchNo+"]查询批量转账结果异常,银行返回错误码:"+returnCode);
      //EL4418:满足条件的记录不存在
      if ("EL4418".equals(returnCode)) {
        bankStatus="0";
        bankCode="EL4418";
        bankMsg="提交失败";
      }
//      else if ("EL1007".equals(returnCode)) {
//        bankStatus="0";
//        bankCode="EL1007";
//        bankMsg="银行系统异常";
//      }
      for (Map<String, Object> map: list) {
        map.put("bankStatus", bankStatus);
        map.put("bankCode", bankCode);
        map.put("bankMsg", bankMsg);
      }
      return list;
    }
    
    @SuppressWarnings("unchecked")
    List<DataMap> bankCycData=(List<DataMap>)returnMap.getObject("cycData");
    if (bankCycData==null) {
      for (Map<String, Object> map: list) {
        map.put("bankStatus", "2");
        map.put("bankCode", "syc_null");
        map.put("bankMsg", "同步银行结果数据异常");
      }
      return list;
    }
    
    //列表按seqNo排升序
    Collections.sort(bankCycData,new HxbTransComparator());
    //匹配转账明细与银行转账结果
    //如果上送的清算行号错误，返回的清算行号就可能为空，所以做结果匹配时不用清算行号
    for (int i = 0; i < list.size(); i++) {
      Map<String, Object> tempMap=list.get(i);
      String inAccNo=tempMap.get("in_acc_no").toString();
      String inAccName=tempMap.get("in_acc_name").toString();
      BigDecimal amount=(BigDecimal)tempMap.get("amount");
      for (int j = 0; j < bankCycData.size(); j++) {
        DataMap bankMap=bankCycData.get(j);
        String bankInAccNo=bankMap.getString("inAccNo");
        String bankInAccName=bankMap.getString("inAccName");
        String bankAmount=bankMap.getString("amount");
        
        if (inAccNo.trim().equals(bankInAccNo.trim())&&inAccName.trim().equals(bankInAccName.trim())
              &&amount.compareTo(new BigDecimal(bankAmount))==0) {
            bankStatus=bankMap.getString("status");
            bankCode  =bankMap.getString("errCode");
            bankMsg   =bankMap.getString("errMsg");
            
            if ("01".equals(bankStatus)) {
              bankStatus="1";
            }else if ("09".equals(bankStatus)) {
              bankStatus="0";
            }else {
              bankStatus="2";
            }
            tempMap.put("bankStatus", bankStatus);
            tempMap.put("bankCode", bankCode);
            tempMap.put("bankMsg", bankMsg);
            
            bankCycData.remove(j);
            break;
        }
        
      }
      
    }
    
    return list;
  }
  
	
}
