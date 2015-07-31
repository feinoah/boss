package com.eeepay.boss.service;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import szfs.tws.adapter.RespType;
import szfs.tws.adapter.TwsRequest;
import szfs.tws.adapter.TwsRestClient;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.boss.domain.SZFSBatSinglePay;
import com.eeepay.boss.domain.SZFSBatSinglePayItem;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.hxb.pub.util.MvelCompilers;

/**
 *  描述：深圳金融结算中心----转账类
 *  
 *  @author:ym
 *  创建时间：2015-05-04
 */

@Service
public class SZFSPayService  {
  private static final Logger log = LoggerFactory.getLogger(SZFSPayService.class);
  
  @Resource
  private SettleTransferDBService settleTransferDBService;
  public static final int SETTLE_TRANSFER_BATCH_NUM=200;
  
  
  public  final String SZFS_OUT_ID="A01700000001";
  public  final String SZFS_OUT_ACC_NO="44201018600052501870";
  public  final String SZFS_OUT_ACC_NAME="深圳市移付宝科技有限公司";
  public  final String SZFS_OUT_BANK_NO="";
  public  final String SZFS_OUT_BANK_NAME="中国建设银行后海公馆支行";
  public  final String SZFS_OUT_SETTLE_BANK_NO="105584000005";
  public  final String SZFS_TRANS_CODE="31702";
  public  final String SZFS_PRODUCT_CODE="G0001";
  public  final String SZFS_FEE_ITEM="00800";
  
//  public  final String SZFS_OUT_ID="A02600000001";
//  public  final String SZFS_OUT_ACC_NO="201407291115";
//  public  final String SZFS_OUT_ACC_NAME="深圳市移付宝科技有限公司";
//  public  final String SZFS_OUT_BANK_NO="";
//  public  final String SZFS_OUT_BANK_NAME="中国建设银行后海公馆支行";
//  public  final String SZFS_OUT_SETTLE_BANK_NO="710584000001";
//  public  final String SZFS_TRANS_CODE="31702";
//  public  final String SZFS_PRODUCT_CODE="G0001";
//  public  final String SZFS_FEE_ITEM="00504";
//  
  
  /**
   * 
   * 功能：账户校验（结算中心）
   *
   * @param model
   * @param params
   */
  public Map<String, Object> szfsPay(Map<String, Object> params) {
    Map<String, Object> returnMap=new HashMap<String, Object>();
    
    ErrorVO error=this.szfsPaySave(params);
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
    
    error=this.szfsPayTransfer(map);
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
  private ErrorVO szfsPaySave(Map<String, Object> params) {
    ErrorVO error=new ErrorVO();
    
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> transList=(List<Map<String, Object>>)params.get("transList");
    String userID=params.get("userID").toString();
    String userName=params.get("userName").toString();
    String summary=params.get("summary").toString();
    String totalAmount=params.get("totalAmount").toString();
    String payBankChannel=params.get("payBankChannel").toString();
    
    int cycDataCount=transList.size();
    Map<String,String> szfsParams=new HashMap<String, String>();
    szfsParams.put("fileName", userName+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    szfsParams.put("operatorId", userID);
    szfsParams.put("operatorName", userName);
    szfsParams.put("transferOperatorId", userID);
    szfsParams.put("transferOperatorName", userName);
    szfsParams.put("totalNum", ""+cycDataCount);
    szfsParams.put("totalAmount", totalAmount);
    szfsParams.put("summary", summary);
    szfsParams.put("settleBank", payBankChannel);
    szfsParams.put("outAccNo", SZFS_OUT_ACC_NO);
    szfsParams.put("outAccName", SZFS_OUT_ACC_NAME);
    szfsParams.put("outBankNo", SZFS_OUT_BANK_NO);
    szfsParams.put("outBankName", SZFS_OUT_BANK_NAME);
    szfsParams.put("outSettleBankNo", SZFS_OUT_SETTLE_BANK_NO);
    
    String fileId="";
    try {
      fileId = settleTransferDBService.insertTransferUploadFile(szfsParams);
    } catch (SQLException sqlException) {
      log.error("保存数据异常,请重试:"+sqlException.getMessage());
      error.setErrCode("saveErr");
      error.setErrMsg("保存数据失败，获取fileId失败");
      return error;
    }
    szfsParams.put("fileId", fileId);
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
      
      szfsParams.put("batchId", batchNo);
      szfsParams.put("fileId", fileId);
      szfsParams.put("settleBank", "szfs");
      int saveCount=settleTransferDBService.saveTranfer(szfsParams, tempList);
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
  private ErrorVO szfsPayTransfer(Map<String, String> params) {
    
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
    Map<String, Object> szfsParams=new HashMap<String, Object>();
    szfsParams.put("batchNo", batchNo);
    szfsParams.put("fileId", fileId);
    
    log.info("---->组织转账明细数据");
    Map<String, Object> fileDetailMap=settleTransferDBService.queryFileById(fileId);
    List<Map<String, Object>> transList=settleTransferDBService.queryTransByBatch(batchNo);
   
    SZFSBatSinglePay szfsBatSinglePay=new SZFSBatSinglePay();
    List<SZFSBatSinglePayItem> details=new ArrayList<SZFSBatSinglePayItem>();
    
    szfsBatSinglePay.setSubdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    szfsBatSinglePay.setBatno(SZFS_OUT_ID+"317"+batchNo);
    szfsBatSinglePay.setCorpno(SZFS_OUT_ID);
    szfsBatSinglePay.setTranscode(SZFS_TRANS_CODE);//实时入账
    szfsBatSinglePay.setProductcode(SZFS_PRODUCT_CODE);
    szfsBatSinglePay.setFeeitem(SZFS_FEE_ITEM);
    szfsBatSinglePay.setBankno((String)fileDetailMap.get("out_settle_bank_no"));
    szfsBatSinglePay.setAccbankno((String)fileDetailMap.get("out_bank_no"));
    szfsBatSinglePay.setAccno((String)fileDetailMap.get("out_acc_no"));
    szfsBatSinglePay.setAccname((String)fileDetailMap.get("out_acc_name"));
    szfsBatSinglePay.setCurrency("CNY");
    szfsBatSinglePay.setSummary((String)fileDetailMap.get("summary"));;
    
    BigDecimal totalAmountBigDecimal=new BigDecimal("0");
    for (Map<String, Object> transMap : transList) {
      BigDecimal amountBigDecimal=(BigDecimal)transMap.get("amount");
      totalAmountBigDecimal=totalAmountBigDecimal.add(amountBigDecimal);
      
      SZFSBatSinglePayItem detail=new SZFSBatSinglePayItem();
      String id=""+transMap.get("id");
      String seqNo=""+transMap.get("seq_no");//TODO当天内不重复 ，不超过8位
      seqNo=seqNo.length()>8?seqNo.substring(seqNo.length()-8,seqNo.length()):seqNo;//接口限制该字段不超过8位
      
      detail.setLstno(seqNo);
      detail.setOutid(id);
      
      String inSettleBankNo=(String)transMap.get("in_settle_bank_no");
      String inBankNo=(String)transMap.get("in_bank_no");
      String inAccNo=(String)transMap.get("in_acc_no");
      String inAccName=(String)transMap.get("in_acc_name");
      if (!StringUtil.isBlank(inSettleBankNo)) {
        inSettleBankNo=inSettleBankNo.trim();
      }
      if (!StringUtil.isBlank(inBankNo)) {
        inBankNo=inBankNo.trim();
      }
      if (!StringUtil.isBlank(inAccNo)) {
        inAccNo=inAccNo.trim();
      }
      if (!StringUtil.isBlank(inAccName)) {
        inAccName=inAccName.trim();
      }
      
      detail.setCustbankno(inSettleBankNo);
      detail.setCustaccbankno(inBankNo);
      detail.setCustaccno(inAccNo);
      detail.setCustaccname(inAccName);
      detail.setMoney(""+transMap.get("amount"));
      
      details.add(detail);
    }
    szfsBatSinglePay.setDetail(details);
    szfsBatSinglePay.setTotalnum(""+transList.size());
    szfsBatSinglePay.setTotalmoney(totalAmountBigDecimal.toString());
    
    Document doc=null;
    String status="";
    String errCode="";
    String errMsg="";
    int ret=0;
    try {
      SAXReader reader = new SAXReader();
      Document document = reader.read(SZFSPayService.class.getClassLoader().getResourceAsStream("xml/SZFS_BATSINGLEPAY.xml"));
              
      String xml=document.asXML();
      fileDetailMap.put("detail", transList);
      xml=MvelCompilers.eval(xml, szfsBatSinglePay);
      
      TwsRequest req = new TwsRequest();
      req.setMsgtype("BATSINGLEPAY"); // 报文类型
      req.setVersion("1.0"); // 版本号
      req.setResptype(RespType.ACTQRY); // 响应模式:主动查询
      req.setSubnode(SZFS_OUT_ID); // 委托方
      req.setSendtime(new Date()); // 发送时间
      req.setMsgno(System.currentTimeMillis() + ""); // 报文序号
      req.setZip(true); // 是否压缩
      req.setXml(xml); 
      TwsRestClient request = new TwsRestClient();
      log.info("请求结算中心XML："+xml);
      String returnXML = request.post(req);
      log.info("结算中心返回XML："+returnXML);
      
      doc = DocumentHelper.parseText(returnXML);  
      //解析返回xml
      Element returnRoot = doc.getRootElement(); 
      Element statusElement=returnRoot.element("status");
      Element statusDescElement=returnRoot.element("remark");
      String returnCode=statusElement.getTextTrim();
      String returnsDescription=statusDescElement.getTextTrim();
      
      if (!"99".equals(returnCode)) {
      // 修改批量及明细状态为"提交成功"
        status="1";
        errCode="success";
        errMsg="提交成功";
        ret=transList.size();
      }else{
      // 修改批量及明细状态为"提交失败",更新错误码及错误信息
        status="2";
        errCode=returnCode;
        errMsg=returnsDescription.length()>60?returnsDescription.substring(0, 60):returnsDescription;
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (e instanceof SocketTimeoutException) {
        log.error("---->batchNo:"+batchNo+"提交转账超时,当提交成功处理,请稍后查询转账结果:"+e.getMessage());
        
        status="1";
        errCode="time_out";
        errMsg="超时,做提交成功处理，稍后查询获得具体结果";
        ret=transList.size();
      } else {
        log.error("---->batchNo:"+batchNo+"提交转账失败,请稍后查询转账结果:"+e.getMessage());
        
        status="2";
        errCode="exception";
        errMsg=e.getMessage();
        errMsg=errMsg.length()>240?errMsg.substring(0, 240):errMsg;
      }
      
    }
    

    
    Map<String, String> newMap=new HashMap<String, String>();
    newMap.put("batchId", batchNo);
    newMap.put("status", status);
    newMap.put("errCode", errCode);
    newMap.put("errMsg", errMsg);
    settleTransferDBService.updateTransBatch(newMap);
    int retDetail=settleTransferDBService.updateTransDetail(newMap);
    log.info("---->"+batchNo+"状态修改为["+status+"],"+retDetail+"条明细状态修改为["+status+"]"); 
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
  public List<Map<String, Object>> getTransferByFileSzfs(String cashFileId) {
    List<Map<String, Object>> transferList=new ArrayList<Map<String, Object>>();
    
//    Map<String, String> params=new HashMap<String, String>();
//    params.put("fileId", cashFileId);
//    Map<String, Object> fileMap= settleTransferDBService.queryFileById(cashFileId);
//    java.sql.Timestamp transferDateTamp=((java.sql.Timestamp)fileMap.get("transfer_time"));
//    String transferDate=new SimpleDateFormat("yyyyMMdd").format(transferDateTamp);
    
    List<Map<String,Object>> batchList=settleTransferDBService.queryTransBatchByFileId(cashFileId,"");
    for (Map<String, Object> map : batchList) {
      List<Map<String, Object>> bankList=this.getTransferBatchSzfs(map);
      transferList.addAll(bankList);
      
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
  private List<Map<String, Object>> getTransferBatchSzfs(Map<String, Object> batchMap) {
    List<Map<String, Object>> bankList = new ArrayList<Map<String, Object>>();
    String batchNo = batchMap.get("batch_id").toString();
    List<Map<String, Object>> transList=settleTransferDBService.queryTransByBatch(batchNo);
    
    Map<String, String> params=new HashMap<String, String>();
    params.put("corpno", SZFS_OUT_ID);
    params.put("batchIdAll", SZFS_OUT_ID + "317" + batchNo);
    SAXReader reader = new SAXReader();
    Document document = null;
    Document doc = null;
    try {
      document = reader.read(SZFSPayService.class.getClassLoader().getResourceAsStream("xml/SZFS_BIZTRANSQUERY.xml"));
      String xml = document.asXML();
      xml = MvelCompilers.eval(xml, params);
      
      TwsRequest req = new TwsRequest();
      req.setMsgtype("BIZTRANSQUERY"); // 报文类型
      req.setVersion("1.0"); // 版本号
      req.setResptype(RespType.SYNC); // 响应模式:主动查询
      req.setSubnode(SZFS_OUT_ID); // 委托方
      req.setSendtime(new Date()); // 发送时间
      req.setMsgno(System.currentTimeMillis() + ""); // 报文序号
      req.setZip(false); // 是否压缩
      req.setXml(xml);
      TwsRestClient request = new TwsRestClient();
      log.info("请求结算中心XML：" + xml);
      String returnXML = request.post(req);
      log.info("结算中心返回XML：" + returnXML);

      doc = DocumentHelper.parseText(returnXML);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("批次["+batchNo+"]查询批量转账结果异常:"+e.getMessage());
      for (Map<String, Object> map: transList) {
        map.put("bankStatus", "2");
        map.put("bankCode", "exception");
        map.put("bankMsg", "查询失败");
      }
      return transList;
    }

    // 解析返回xml
    Element returnRoot = doc.getRootElement();
    String returnRootName = returnRoot.getName();
    String returnStatus = returnRoot.element("status").getTextTrim();
    String returnRemark = returnRoot.element("remark").getTextTrim();
    
    if ("01".equals(returnStatus)||"31".equals(returnStatus)||"99".equals(returnStatus)) {//失败
      String procode = doc.getRootElement().elementTextTrim("procode");
      if (!StringUtil.isBlank(procode)) {
        returnStatus=procode+"|"+returnStatus;
      }
      
      log.error("批次["+batchNo+"]中心处理异常,银行返回:"+returnStatus+"|"+returnRemark);
      for (Map<String, Object> map: transList) {
        map.put("bankStatus", "0");
        map.put("bankCode", returnStatus);
        map.put("bankMsg", returnRemark);
      }
      return transList;
    }else if("00".equals(returnStatus)) {//处理成功,所有银行均已返回入账回执,有状态明细返回
      Map<String, String> xmlMap = new HashMap<String, String>();
      xmlMap.put("default", doc.getRootElement().getNamespaceURI());
      XPath x = doc.createXPath("//default:BizTransNotice/default:details/default:detail");
      x.setNamespaceURIs(xmlMap);
      @SuppressWarnings("unchecked")
      List<Node> nodeList = x.selectNodes(doc);

      for (Node node : nodeList) {
        Map<String, Object> map = new HashMap<String, Object>();
        Element element = (Element) node;
        
        String bankStatus="";
        String status = element.elementTextTrim("dtstatus");
        String bankCode=element.elementTextTrim("dtprocode");
        String bankMsg=element.elementTextTrim("dtremark");
        
        // 转换结算中心的处理状态    
        if ("00".equals(status)) {
          bankStatus = "1"; // 成功
        } else if ("01".equals(status) || "32".equals(status) || "99".equals(status)) {
          bankStatus = "0";// 失败
        } else { // 未知
          bankStatus = "2";
        }
        
        /**
         *   特殊错误码处理：
         *   33和35在银行清算日期内，当中间状态处理，超过清算日期则按交易成功处理
         */
        if ("33".equals(status)||"35".equals(status)) {
          String settle = returnRoot.element("setwrkdate").getTextTrim();//获取交易的清算日期
         
          SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
          String now=sd.format(new Date());
          Date settleDate=null;
          Date nowDate=null;
          try {
            settleDate = sd.parse(settle);
            nowDate    = sd.parse(now);
            boolean isAfter=nowDate.after(settleDate);
            if (isAfter) {
              bankStatus = "1"; // 成功
            }
          } catch (ParseException e) {
            e.printStackTrace();
            log.error("解析清算日期异常："+e.getMessage());
          }
          
        }
        
        map.put("seq_no", element.elementTextTrim("outid"));
        map.put("in_acc_no", element.elementTextTrim("custaccno"));
        map.put("in_acc_name", element.elementTextTrim("custaccname"));
        map.put("amount", element.elementTextTrim("dttrnmoney"));
        map.put("bankStatus", bankStatus);
        map.put("bankCode", bankCode);
        map.put("bankMsg","["+status+"|"+bankCode+"]"+bankMsg);

        bankList.add(map);
      }
      
      //匹配中心返回结果
      for (int i = 0; i < transList.size(); i++) {
        Map<String, Object> transMap=transList.get(i);
        String id=transMap.get("id").toString();
        
        //如果本地数据比返回结果多
        if (bankList.size()==0) {
          transMap.put("bankStatus", "2");
          transMap.put("bankCode","return_not_found" );
          transMap.put("bankMsg", "查询返回结果不存在");
          continue;
        }
        for (int j = 0; j < bankList.size(); j++) {
          Map<String, Object> bankMap=bankList.get(j);
          String outId=bankMap.get("seq_no").toString();
          if (id.equals(outId)) {
            transMap.put("bankStatus", bankMap.get("bankStatus"));
            transMap.put("bankCode", bankMap.get("bankCode"));
            transMap.put("bankMsg", bankMap.get("bankMsg"));
            bankList.remove(j);
            break;
          }
        }
      }
      
      return transList;
    }else {//处理中,不返回数据
      return new ArrayList<Map<String,Object>>();
    }

//    if ("result".equals(returnRootName)) {
//      
//      //TODO 根据状态分别处理      
//      log.error("批次["+batchNo+"]查询异常,银行返回:"+returnsDescription);
//      for (Map<String, Object> map: transList) {
//        map.put("bankStatus", "2");
//        map.put("bankCode", "exception");
//        map.put("bankMsg", returnsDescription);
//      }
//      return transList;
//    } else {
//      Map<String, String> xmlMap = new HashMap<String, String>();
//      xmlMap.put("default", doc.getRootElement().getNamespaceURI());
//      XPath x = doc.createXPath("//default:BizTransNotice/default:details/default:detail");
//      x.setNamespaceURIs(xmlMap);
//      List<Node> nodeList = x.selectNodes(doc);
//
////      int transNum = Integer.parseInt(doc.getRootElement().elementTextTrim("totalnum"));
//      
//      String procode = doc.getRootElement().elementTextTrim("procode");
//      String statusDesc = doc.getRootElement().elementTextTrim("remark");
////      if (transNum != transList.size()) {
////        model.put("errorInfo", "批次号:" + batchId + "状态为:" + statusDesc + "("
////            + returnStatus + "|" + procode + "),有" + (transNum - list.size())
////            + "条数据未能显示");
////      }
//
//      for (Node node : nodeList) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        Element element = (Element) node;
//        String status = element.elementTextTrim("dtstatus");
//        String bankStatus="";
//        String bankErrCode="";
//        String bankErrMsg="";
//        // 转换结算中心的处理状态
//        if ("00".equals(status) || "33".equals(status) || "35".equals(status)) {
//          bankStatus = "1"; // 成功
//        } else if ("01".equals(status) || "32".equals(status) || "99".equals(status)) {
//          bankStatus = "0";// 失败
//        } else { // 未知
//          bankStatus = "2";
//        }
//
//        map.put("seq_no", element.elementTextTrim("outid"));
//        map.put("in_acc_no", element.elementTextTrim("custaccno"));
//        map.put("in_acc_name", element.elementTextTrim("custaccname"));
//        map.put("amount", element.elementTextTrim("dttrnmoney"));
//        map.put("bankStatus", bankStatus);
//        map.put("bankErrCode", element.elementTextTrim("dtprocode"));
//        map.put("bankErrMsg", element.elementTextTrim("dtremark"));
//
//        bankList.add(map);
//      }
//      
//      //匹配中心返回结果
//      for (int i = 0; i < transList.size(); i++) {
//        Map<String, Object> transMap=transList.get(i);
//        String id=transMap.get("id").toString();
//        for (int j = 0; j < bankList.size(); j++) {
//          Map<String, Object> bankMap=bankList.get(j);
//          String outId=bankMap.get("seq_no").toString();
//          if (id.equals(outId)) {
//            transMap.put("bankStatus", bankMap.get("bankStatus"));
//            transMap.put("bankErrCode", bankMap.get("bankErrCode"));
//            transMap.put("bankErrMsg", bankMap.get("bankErrMsg"));
//            bankList.remove(j);
//            break;
//          }
//        }
//      }
//      
//      return transList;

//    }

    
  }
  
	
}
