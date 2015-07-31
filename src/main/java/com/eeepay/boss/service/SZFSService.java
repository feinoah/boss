package com.eeepay.boss.service;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import szfs.tws.adapter.RespType;
import szfs.tws.adapter.TwsRequest;
import szfs.tws.adapter.TwsRestClient;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.boss.domain.SZFSError;
import com.eeepay.boss.utils.SZFSBankNoCache;
import com.eeepay.boss.utils.StringUtil;

/**
 *  描述：深圳金融结算中心
 *  
 *  @author:ym
 *  创建时间：2014-10-08
 */

@Service
public class SZFSService  {
  private static final Logger log = LoggerFactory.getLogger(SZFSService.class);
  public  final String SZFS_OUT_ID="A01700000001";
  
  @Resource
  private SZFSDbService szfsDbService;
  
  
  /**
   * 
   * 功能：账户校验（结算中心）
   *
   * @param model
   * @param params
   */
  public ErrorVO accountVerify(Map<String, String> paramsMap){
    String msg = "";
    ErrorVO error=new ErrorVO();
    SZFSError szfsError=new SZFSError();
    Map<String, String> params = new HashMap<String, String>();
    
    String channel=paramsMap.get("channel");
    String verifyType   =paramsMap.get("verifyType");
    String accNo        = paramsMap.get("accNo");
    String accName      = paramsMap.get("accName");
    String bankNo       = paramsMap.get("bankNo");
    String identityType = "";
    String identityId   = "";
    String merchantId   =params.get("merchantId");
    String userId       =paramsMap.get("userId");
    String userName     =paramsMap.get("userName");
    if (StringUtil.isBlank(merchantId)) {
      merchantId="0";
    }
    if (userId==null) {
      userId=channel;
    }
    if (userName==null) {
      userName=channel;
    }
    
    if (StringUtil.isBlank(verifyType)) {
      msg = "校验类型不能为空";
      log.warn(msg);
      error.setErrCode("faild");
      error.setErrMsg(msg);
      return error;
    }
    if (StringUtil.isBlank(channel)) {
      msg = "调用渠道不能为空";
      log.warn(msg);
      error.setErrCode("faild");
      error.setErrMsg(msg);
      return error;
    }
    if (!"BOSS".equals(channel)&&!"AGENT".equals(channel)) {
      msg = "不支持的调用渠道";
      log.warn(msg);
      error.setErrCode("faild");
      error.setErrMsg(msg);
      return error;
    }
    /**
     * 1：验证账号户名一致性    2：验证账号户名证件号码一致性
     */
    if ("1".equals(verifyType)) {
      if (StringUtil.isBlank(accNo,accName,bankNo)) {
        msg = "账号、户名或者清算行号不能为空";
        log.warn(msg);
        error.setErrCode("faild");
        error.setErrMsg(msg);
        return error;
      }
      
    }else if ("2".equals(verifyType)) {
      identityType="01";
      identityId=paramsMap.get("identityId");
      if (StringUtil.isBlank(accNo,accName,identityId,bankNo)) {
        msg = "账号、户名、证件号或者清算行号不能为空";
        log.warn(msg);
        error.setErrCode("faild");
        error.setErrMsg(msg);
        return error;
      }
      
    }else {
      msg = "不支持的验证类型";
      log.warn(msg);
      error.setErrCode("faild");
      error.setErrMsg(msg);
      return error;
    }
    
    //暂不支持  工商银行、交通银行、广州银行 的验证
    boolean bankFlag=true;
    String bankDesc="";
    if ("102584000002".equals(bankNo)) {
      bankDesc="中国工商银行";
      bankFlag=false;
    }
    if ("301584000016".equals(bankNo)) {
      bankDesc="交通银行";
      bankFlag=false;
    }
    if ("313584011011".equals(bankNo)) {
      bankDesc="广州银行";
      bankFlag=false;
    }
    if (!bankFlag) {
      msg = "暂不支持"+bankDesc+"卡的验证";
      log.warn(msg);
      error.setErrCode("faild");
      error.setErrMsg(msg);
      return error;
    }
    
    List<Map<String, Object>> list=szfsDbService.queryAccVerify(accNo,accName,identityType,identityId);
    if (list!=null&&list.size()>0) {
      String szfsStatus="";
      String szfsId="";
      for (Map<String, Object> szfsMap : list) {
        String tempStatus=(String)szfsMap.get("status");
        if ("1".equals(tempStatus)||"3".equals(tempStatus)) {
          szfsStatus=tempStatus;
          szfsId=szfsMap.get("id").toString();
          break;
        }
      }
      if ("1".equals(szfsStatus)) {       
        msg="校验成功";
        log.warn("账号:"+accNo+"已经做过账户校验，并且"+msg);
        error.setErrCode("success");
        error.setErrMsg(msg);
        return error;
      }else if ("3".equals(szfsStatus)) {//超时，主动查询
        szfsError=this.queryInfoTrans("ACCTVERIFY",SZFS_OUT_ID+"619"+szfsId);
        String returnStatus=szfsError.getStatus();
        if ("true".equals(szfsError.getSuccess())) {
          msg = "校验成功";
          params.put("id", szfsId);
          params.put("status", "1");
          params.put("errCode", szfsError.getSuccess());
          params.put("errMsg", msg.length() > 240 ? msg.substring(0, 240) : msg);
          params.put("szfsStatus", szfsError.getStatus());
          params.put("szfsProcode", szfsError.getProCode());
          params.put("szfsRemark", szfsError.getRemark().length() > 240 ? szfsError.getRemark().substring(0, 240) : szfsError.getRemark());
          szfsDbService.updateTranserFileUpload(params);
          error.setErrCode("success");
          error.setErrMsg(msg);
          return error;
        } else {
          if ("00".equals(returnStatus)||"04".equals(returnStatus)||"15".equals(returnStatus)||"98".equals(returnStatus)) {
            msg = "银行正在处理中，请稍后再试";
          }else if ("time_out".equals(returnStatus)&& "time_out".equals(returnStatus)) {
            msg = "校验超时";
          } else {
            msg = "校验失败";
            params.put("id", szfsId);
            params.put("status", "2");
            params.put("errCode", szfsError.getSuccess());
            params.put("errMsg", msg.length() > 240 ? msg.substring(0, 240) : msg);
            params.put("szfsStatus", szfsError.getStatus());
            params.put("szfsProcode", szfsError.getProCode());
            params.put("szfsRemark", szfsError.getRemark().length() > 240 ? szfsError.getRemark().substring(0, 240) : szfsError.getRemark());
            szfsDbService.updateTranserFileUpload(params);
          }
          msg += "[" + szfsError.getStatus() + "|" + szfsError.getProCode() + "|"+ szfsError.getRemark() + "]";
        }
        log.warn("账号:"+accNo+" 已经做过账户校验，主动查询："+msg);
        
        error.setErrCode("faild");
        error.setErrMsg(msg);
        return error;
      }
      
    }
    
    params.clear();
    params.put("merchantId", merchantId);
    params.put("channel", channel);
    params.put("verifyType", verifyType);
    params.put("bankNo", bankNo);
    params.put("currency", "CNY");
    params.put("accNo", accNo);
    params.put("accName", accName);
    params.put("identityType", identityType);
    params.put("identityId", identityId);
    params.put("mobileNo", "");
    params.put("momo", "");
    params.put("verifyerId", userId);
    params.put("verifyerName", userName);

    String referenceNo = szfsDbService.insertAccountVerify(params);
    if (StringUtil.isBlank(referenceNo)) {
      referenceNo = new Date().getTime() +UUID.randomUUID().toString().replaceAll("-", "");
    }

    String status = "";
    szfsError = this.acctVerify(verifyType,referenceNo, bankNo, accNo,accName, identityType, identityId);
    String returnStatus=szfsError.getStatus();
    if ("true".equals(szfsError.getSuccess())) {
      status = "1";
      msg = "校验成功";
      error.setErrCode("success");
      error.setErrMsg(msg);
    } else {
      if ("00".equals(returnStatus)||"04".equals(returnStatus)||"15".equals(returnStatus)||"98".equals(returnStatus)) {
        status = "3";
        msg = "银行正在处理中，请稍后再试";
      }else if ("time_out".equals(returnStatus)&& "time_out".equals(returnStatus)) {
        status = "3";
        msg = "校验超时";
      } else {
        status = "2";
        msg = "校验失败";
      }
      msg += "[" + szfsError.getStatus() + "|" + szfsError.getProCode() + "|"+ szfsError.getRemark() + "]";
      error.setErrCode("faild");
      error.setErrMsg(msg);
    }

    params.clear();
    params.put("id", referenceNo);
    params.put("status", status);
    params.put("errCode", szfsError.getSuccess());
    params.put("errMsg", msg.length() > 240 ? msg.substring(0, 240) : msg);
    params.put("szfsStatus", szfsError.getStatus());
    params.put("szfsProcode", szfsError.getProCode());
    params.put("szfsRemark", szfsError.getRemark().length() > 240 ? szfsError.getRemark().substring(0, 240) : szfsError.getRemark());
    szfsDbService.updateTranserFileUpload(params);

    log.warn(msg);
    return error;
  } 
  
  
	
  /**
   * 
   * 功能：结算中心账户在验证接口
   *
   * @param verifyType
   * @param referenceNo
   * @param bankNo
   * @param accNo
   * @param accName
   * @param identityType
   * @param identityId
   * @return
   */
  public SZFSError acctVerify(String verifyType,String referenceNo,String bankNo,String accNo,String accName,String identityType,String identityId) {
    SZFSError szfsError=new SZFSError();
    Document doc=null;
    try {
      
      if (StringUtil.isBlank(referenceNo,bankNo,accNo,accName)) {
        szfsError.setStatus("error");
        szfsError.setProCode("error");
        szfsError.setRemark("用户数据不完整，无法校验");
        return szfsError;
      }
      
      StringBuffer xmlBuffer=new StringBuffer();
      xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
      xmlBuffer.append("<AcctVerify xmlns=\"szfs.tws.info.acctverify\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
      xmlBuffer.append("<subdate>").append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append("</subdate>");
      xmlBuffer.append("<outid>").append(SZFS_OUT_ID+"619"+referenceNo).append("</outid>");
      xmlBuffer.append("<type>").append(verifyType).append("</type>");
      xmlBuffer.append("<bankno>").append(bankNo).append("</bankno>");
      xmlBuffer.append("<currency>").append("CNY").append("</currency>");
      xmlBuffer.append("<acctno>").append(accNo).append("</acctno>");
      xmlBuffer.append("<acctname>").append(accName).append("</acctname>");
      if ("2".equals(verifyType)) {
        xmlBuffer.append("<idtype>").append(identityType).append("</idtype>");
        xmlBuffer.append("<id>").append(identityId).append("</id>");
      }
      xmlBuffer.append("</AcctVerify>");
      
      TwsRequest req = new TwsRequest();
      req.setMsgtype("ACCTVERIFY"); // 报文类型
      req.setVersion("1.0"); // 版本号
      req.setResptype(RespType.SYNC); // 响应模式:同步响应
      req.setSubnode(SZFS_OUT_ID); // 委托方
      req.setSendtime(new Date()); // 发送时间
      req.setMsgno(System.currentTimeMillis() + ""); // 报文序号
      req.setZip(true); // 是否压缩
      req.setXml(xmlBuffer.toString()); 
      TwsRestClient request = new TwsRestClient();
      log.info("请求结算中心XML："+xmlBuffer.toString());
      String returnXML = request.post(req);
      log.info("结算中心返回XML："+returnXML);
      doc = DocumentHelper.parseText(returnXML); 
    } catch (Exception e) {
      e.printStackTrace();
      log.error("账户校验失败，原因可能是："+e.getMessage());
      if (e instanceof SocketTimeoutException) {
        szfsError.setStatus("time_out");
        szfsError.setProCode("time_out");
      }else {
        szfsError.setStatus("error");
        szfsError.setProCode("error");
      }
      
      szfsError.setRemark(e.getMessage());
      return szfsError;
    }
    
    Element returnRoot = doc.getRootElement();
    String status = returnRoot.element("status").getTextTrim();
    if (!"result".equals(returnRoot.getName())) {
      if ("37".equals(status)) {
        szfsError.setSuccess("true");
      }
    }

    String proCode = "";
    Element proCodeElement = returnRoot.element("procode");
    if (proCodeElement != null) {
      proCode = proCodeElement.getTextTrim();
    }
    String remark = "";
    Element remarkElement = returnRoot.element("remark");
    if (remarkElement != null) {
      remark = remarkElement.getStringValue();
    }

    szfsError.setStatus(status);
    szfsError.setProCode(proCode);
    szfsError.setRemark(remark);
    return szfsError;
  }
  
  
  /**
   * 
   * 功能：信息类主动查询接口
   *
   * @param omsgtype     原报文类型
   * @param referenceNo  原外部参考号
   * @return
   */
  public SZFSError queryInfoTrans(String omsgtype, String referenceNo) {
    SZFSError szfsError = new SZFSError();
    Document doc = null;

    if (StringUtil.isBlank(omsgtype, referenceNo)) {
      szfsError.setStatus("error");
      szfsError.setProCode("error");
      szfsError.setRemark("数据异常，无法校验");
      return szfsError;
    }

    StringBuffer xmlBuffer = new StringBuffer();
    xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    xmlBuffer.append("<InfoTransQuery xmlns=\"szfs.tws.actqry.infotransquery\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
    xmlBuffer.append("<omsgtype>").append(omsgtype.trim()).append("</omsgtype>");
    xmlBuffer.append("<oversion>").append("1.0").append("</oversion>");
    xmlBuffer.append("<osubnode>").append(SZFS_OUT_ID).append("</osubnode>");
    xmlBuffer.append("<oseqid>").append(referenceNo.trim()).append("</oseqid>");
    xmlBuffer.append("</InfoTransQuery>");

    try {
      TwsRequest req = new TwsRequest();
      req.setMsgtype("INFOTRANSQUERY"); // 报文类型
      req.setVersion("1.0"); // 版本号
      req.setResptype(RespType.SYNC); // 响应模式:同步响应
      req.setSubnode(SZFS_OUT_ID); // 委托方
      req.setSendtime(new Date()); // 发送时间
      req.setMsgno(System.currentTimeMillis() + ""); // 报文序号
      req.setZip(true); // 是否压缩
      req.setXml(xmlBuffer.toString());
      TwsRestClient request = new TwsRestClient();
      log.info("请求结算中心XML：" + xmlBuffer.toString());
      String returnXML = request.post(req);
      log.info("结算中心返回XML：" + returnXML);
      doc = DocumentHelper.parseText(returnXML);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("主动查询失败，原因可能是：" + e.getMessage());
      if (e instanceof SocketTimeoutException) {
        szfsError.setStatus("time_out");
        szfsError.setProCode("time_out");
      } else {
        szfsError.setStatus("error");
        szfsError.setProCode("error");
      }
      szfsError.setRemark(e.getMessage());
      return szfsError;
    }

    Element returnRoot = doc.getRootElement();
    String status = returnRoot.element("status").getTextTrim();
    if (!"result".equals(returnRoot.getName())) {
      if ("37".equals(status)) {
        szfsError.setSuccess("true");
      }
    }

    String proCode = "";
    Element proCodeElement = returnRoot.element("procode");
    if (proCodeElement != null) {
      proCode = proCodeElement.getTextTrim();
    }
    String remark = "";
    Element remarkElement = returnRoot.element("remark");
    if (remarkElement != null) {
      remark = remarkElement.getTextTrim();
    }

    szfsError.setStatus(status);
    szfsError.setProCode(proCode);
    szfsError.setRemark(remark);

    return szfsError;
  }
  
  /**
   * 
   * 功能：对公账号，根据开户行名匹配结算中心对应的清算行号
   *
   * @param bankName
   * @return
   */
  public String getEntpriseBankNo(String bankName) {
    String bankNo="";
    String keyWord="银行";
    if (StringUtil.isBlank(bankName)) {
      return bankNo;
    }
    boolean bankNameFlag=bankName.contains(keyWord);
    if (!bankNameFlag) {
      return bankNo;
    }
    
    String bankKeyWord=bankName.substring(0, bankName.indexOf(keyWord)+keyWord.length());
    Map<String, Object> bankMap=null;
    List<Map<String, Object>> szfsBankNoList=SZFSBankNoCache.getList();
    for (Map<String, Object> map : szfsBankNoList) {
      String tempBankName=(String)map.get("bank_name");
      boolean bankNoFlag=tempBankName.contains(bankKeyWord);
      if (bankNoFlag) {
        bankMap=map;
      }
    }
    if (bankMap==null) {
      return bankNo;
    }
    bankNo=(String)bankMap.get("sz_bank_no");
    log.info("开户行["+bankName+"]匹配到清算行["+bankMap.get("sz_bank_name")+"],清算行号："+bankNo);
    return bankNo;
  }
  
  
	
}
