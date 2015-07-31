/**
 * 版权 (c) 2015 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.StringUtil;

/**
 * 描述：提现
 *
 * @author ym 
 * 创建时间：2015年7月2日
 */
@Service
public class PurseCashService {
  
  private static final Logger log = LoggerFactory.getLogger(PurseCashService.class);
  private String nowCashBank="szfs";//提现默认出账通道
  
  @Resource
  private PurseService purseSerivce;
  @Resource
  private PurseRecService purseRecService;
  @Resource
  private HxbPayService hxbPayService;
  @Resource
  private EciticPayService eciticPayService;
  @Resource
  private SZFSPayService szfsPayService;
  @Resource
  private CmbcXMPayService cmbcXMPayService;
  
  /**
   * 
   * 功能：
   *
   * @param model
   * @param params
   */
  public void purseCash(List<Map<String, Object>> cashList) {
    Map<String, Object> channelMap=purseSerivce.getNowExtractionChannel();
    if (channelMap!=null) {
      String dbCashBank=(String)channelMap.get("channel_code");
      if (!StringUtil.isBlank(dbCashBank)) {
        nowCashBank=dbCashBank;
      }
    }
    log.info("----当前钱包提现通道为："+nowCashBank);
    //不符合转账条件记录
    List<Map<String, Object>> errorList    =new ArrayList<Map<String,Object>>();
    List<Map<String, Object>> hxbInList    =new ArrayList<Map<String,Object>>();
    List<Map<String, Object>> hxbOutList   =new ArrayList<Map<String,Object>>();
    List<Map<String, Object>> eciticList   =new ArrayList<Map<String,Object>>();
    List<Map<String, Object>> szfsList     =new ArrayList<Map<String,Object>>();
    List<Map<String, Object>> cmbcXmAPiList=new ArrayList<Map<String,Object>>();
    
    //路由待处理记录到结算通道
    for (int i = 0; i < cashList.size(); i++) {
      Map<String, Object> map=cashList.get(i);
      
      String bankNo = (String)map.get("bank_no");
      if (StringUtil.isBlank(bankNo)) {
        map.put("cashRemark", "清算行号不能为空");
        errorList.add(map);
        log.info("---->提现ID:"+map.get("id").toString()+"  bankNo为空，不做处理");
        continue;
      }

      //根据当前开通的钱包提现通道选择执行的通道
      if ("ecitic".equalsIgnoreCase(nowCashBank)) {
        eciticList.add(map);//中信银行
      }else if ("szfs".equalsIgnoreCase(nowCashBank)) {
        szfsList.add(map);//深圳金融结算中心
      }else if ("cmbc_xm_api".equalsIgnoreCase(nowCashBank)) {
        cmbcXmAPiList.add(map);//厦门民生直连代付接口
      }else if ("hxb".equalsIgnoreCase(nowCashBank)) {
        //华夏银行
        if ("304100040000".equals(bankNo)) { 
          hxbInList.add(map);
        } else {
          hxbOutList.add(map);
        }
      }
      
    }
    
    //向结算通道发送结算请求
    Map<String, Object> payParams=new HashMap<String, Object>();
    if (eciticList.size() > 0) {
      log.info("---->中信通道提现"+eciticList.size()+"条");
      payParams.clear();
      payParams.put("payBankChannel", "ECITIC");
//      payParams.put("lockStamp", lockStamp);
      payParams.put("transList", eciticList);
      this.payList(payParams);
    }
    if (szfsList.size() > 0) {
      log.info("---->结算中心通道提现"+szfsList.size()+"条");
      payParams.clear();
      payParams.put("payBankChannel", "SZFS");
//      payParams.put("lockStamp", lockStamp);
      payParams.put("transList", szfsList);
      this.payList(payParams);
    }
    if (cmbcXmAPiList.size() > 0) {
      log.info("---->民生厦门直连通道提现"+cmbcXmAPiList.size()+"条");
      payParams.clear();
      payParams.put("payBankChannel", "CMBC_XM_API");
//      payParams.put("lockStamp", lockStamp);
      payParams.put("transList", cmbcXmAPiList);
      this.payList(payParams);
    }
    if (hxbInList.size() > 0) {
      log.info("---->华夏通道行内转账"+hxbInList.size()+"条");
      payParams.clear();
      payParams.put("payBankChannel", "HXB");
//      payParams.put("lockStamp", lockStamp);
      payParams.put("transType", "in");
      payParams.put("transList", hxbInList);
      this.payList(payParams);
    }
    if (hxbOutList.size() > 0) {
      log.info("---->华夏通道跨行转账"+hxbOutList.size()+"条");
      payParams.clear();
      payParams.put("payBankChannel", "HXB");
//      payParams.put("lockStamp", lockStamp);
      payParams.put("transType", "out");
      payParams.put("transList", hxbOutList);
      this.payList(payParams);
    }
    
    //处理不符合要求的记录
    int totalErrRet=0;
    int errSize=errorList.size();
    log.info("---->不符合条件提现记录"+errSize+"条");
    
    if (errSize>0) {
      for (Map<String, Object> map : errorList) {
        Map<String, String> temMap=new HashMap<String, String>();
        temMap.put("id", map.get("id").toString());
        temMap.put("openStatus", "5");
        temMap.put("cashStatus", "3");
        temMap.put("isBack", "1");
        temMap.put("cashRemark", map.get("cashRemark").toString());
        
        int ret=purseSerivce.updatePurseCashById(temMap);
        totalErrRet+=ret;
      }
      log.info("---->"+totalErrRet+"条不符合条件提现状态修改为:openStatus[5] cashStatus[3]");
      this.asyPurseBalance(errorList);
    }
    
    
  }
  
  
  /**
   * 
   * 功能：银行通道列表转账
   *
   * @param payParams
   */
  private void payList(Map<String, Object> payParams) {
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> transList=(List<Map<String, Object>>)payParams.get("transList");
    String payBankChannel=payParams.get("payBankChannel").toString();
    
    Map<String, Object> payMap=new HashMap<String, Object>();
    Map<String, Object> returnMap=new HashMap<String, Object>();
    
    /**
     *  中信银行银企直连通道
     */
    if ("ECITIC".equalsIgnoreCase(payBankChannel)) {
      log.info("---->中信通道，开始执行");
      BigDecimal totalAmountBigDecimal = new BigDecimal("0");
      for (Map<String, Object> tempMap : transList) {
        totalAmountBigDecimal = totalAmountBigDecimal.add((BigDecimal)tempMap.get("settle_amount"));
      }
      payMap.clear();
      payMap.put("userID", "1");
      payMap.put("userName", "task");
      payMap.put("summary", "钱包提现");
      payMap.put("totalAmount", totalAmountBigDecimal.toPlainString());
      payMap.put("transList", transList);
      returnMap = eciticPayService.eciticPay(payMap);
    }
    
    /**
     *  结算中心直连通道
     */
    if ("SZFS".equalsIgnoreCase(payBankChannel)) {
      log.info("---->深圳结算中心通道，开始执行");
      BigDecimal totalAmountBigDecimal = new BigDecimal("0");
      for (Map<String, Object> tempMap : transList) {
        totalAmountBigDecimal = totalAmountBigDecimal.add((BigDecimal)tempMap.get("settle_amount"));
      }
      payMap.clear();
      payMap.put("userID", "1");
      payMap.put("userName", "task");
      payMap.put("summary", "钱包提现");
      payMap.put("totalAmount", totalAmountBigDecimal.toPlainString());
      payMap.put("transList", transList);
      payMap.put("payBankChannel", payBankChannel);
      returnMap = szfsPayService.szfsPay(payMap);
    }
    /**
     *  厦门民生直连通道
     */
    if ("CMBC_XM_API".equalsIgnoreCase(payBankChannel)) {
      log.info("---->厦门民生直连通道，开始执行");
      BigDecimal totalAmountBigDecimal = new BigDecimal("0");
      for (Map<String, Object> tempMap : transList) {
        totalAmountBigDecimal = totalAmountBigDecimal.add((BigDecimal)tempMap.get("settle_amount"));
      }
      payMap.clear();
      payMap.put("userID", "1");
      payMap.put("userName", "task");
      payMap.put("summary", "钱包提现");
      payMap.put("totalAmount", totalAmountBigDecimal.toPlainString());
      payMap.put("transList", transList);
      payMap.put("payBankChannel", payBankChannel);
      returnMap = cmbcXMPayService.cmbcXMPay(payMap);
    }
    
    /**
     *  华夏银行银企直连通道
     */
    if ("HXB".equalsIgnoreCase(payBankChannel)) {
      log.info("---->华夏通道，开始执行");
      
      String transType=payParams.get("transType").toString();
      BigDecimal totalAmountBigDecimal = new BigDecimal("0");
      for (Map<String, Object> tempMap : transList) {
        totalAmountBigDecimal = totalAmountBigDecimal.add((BigDecimal)tempMap.get("settle_amount"));
      }
      payMap.clear();
      payMap.put("userID", "1");
      payMap.put("userName", "task");
      payMap.put("summary", "钱包提现");
      payMap.put("transType", transType);
      payMap.put("submitChannel", "BOSS");
      payMap.put("totalAmount", totalAmountBigDecimal.toPlainString());
      payMap.put("transList", transList);
      returnMap = hxbPayService.hxbPay(payMap);
    }
    
    boolean isRevert=false;//是否需要余额冲正
    String errCode=returnMap.get("errCode").toString();
    Map<String, String> params=new HashMap<String, String>();
    if ("saveErr".equals(errCode)) {
      // 保存数据失败,将提现状态改回"审核通过",下次任务重新执行
      params.put("openStatus", "1");
      log.info("---->保存数据失败,将提现状态改回[审核通过],下次任务重新执行");
    }else {
      String fileId=returnMap.get("fileId").toString();
      String errMsg=returnMap.get("errMsg").toString();
      params.put("cashRemark", errMsg);
      params.put("cashFileId", fileId);
      params.put("cashChannel", payBankChannel);
      
      if ("success".equals(errCode)) {
        params.put("cashStatus", "2");
        params.put("isBack", "0");
        log.info("---->提现提交成功");
      }else if("timeOut".equals(errCode)){
        params.put("cashStatus", "4");
        params.put("isBack", "0");
        log.info("---->提现提交超时");
      }else{
        params.put("openStatus", "5");
        params.put("cashStatus", "3");
        params.put("isBack", "1");
        isRevert=true;
        log.info("---->提现提交失败");
      }
      
    }
    
    //修改提现记录状态
    int totalRetS=0;
    for (Map<String, Object> map : transList) {
      String id=map.get("id").toString();
      params.put("id", id);
      int ret=purseSerivce.updatePurseCashById(params);
      totalRetS+=ret;
    }
    log.info("---->修改提现申请状态"+totalRetS+"条");
    
    //修改提现记录后，将提现金额充回钱包余额
    if (isRevert) {
      log.info("---->提现失败,并开始余额冲正");
      this.asyPurseBalance(transList);
    }
    
  }
  
  
  /**
   * 
   * 功能：同步钱包提现记录
   *
   */
  public void synPurseCash() {
    Map<String , String> params=new HashMap<String, String>();
    
    //update by ym 20150708:查询条件添加【2天以内切5分钟以外】
    List<Map<String, Object>> cashList = purseSerivce.querySynCash(params);
    List<Map<String, Object>> cashFileIdList = purseSerivce.querySynCashFileId();
    
    log.info("本次任务共扫描到" + cashList.size() + "条待同步记录");

    List<Map<String, Object>> transferListAll = new ArrayList<Map<String, Object>>();
    for (Map<String, Object> tempMap : cashFileIdList) {
      String cashChannel=(String) tempMap.get("cash_channel");
      String cashFileId=(String) tempMap.get("cash_file_id");
      if (StringUtil.isBlank(cashChannel,cashFileId)) {
        continue;
      }
      List<Map<String, Object>> transferBankList = this.getBankStatusFileId(cashFileId,cashChannel);
      transferListAll.addAll(transferBankList);
    }
    int bankNum=transferListAll.size();

    for (Map<String, Object> map : cashList) {
      String cashFileId=(String)map.get("cash_file_id");
      String seqNo=map.get("id").toString();
      
      //本地数据比返回结果多
      if (transferListAll.size()==0) {
        map.put("bankStatus", "2");
        map.put("bankCode","return_not_found" );
        map.put("bankMsg", "查询返回结果不存在");
        continue;
      }
      
      boolean found=false;
      for (Map<String, Object> bankMap : transferListAll) {
        String bankFileId=bankMap.get("file_id").toString();
        String bankSeqNo=bankMap.get("seq_no").toString();
        if (cashFileId.equals(bankFileId)&&seqNo.equals(bankSeqNo)) {
          if (bankMap.get("bankStatus")!=null) {
            map.put("bankStatus", bankMap.get("bankStatus"));
            map.put("bankCode", bankMap.get("bankCode"));
            map.put("bankMsg", bankMap.get("bankMsg"));
          }else{
            map.put("bankStatus", "2");
            map.put("bankCode","return_not_found" );
            map.put("bankMsg", "查询返回结果不存在");
          }
            
          transferListAll.remove(bankMap);
          found=true;
          break;
        }
      }
      //查询结果中未匹配到
      if (!found) {
        map.put("bankStatus", "2");
        map.put("bankCode","return_not_found" );
        map.put("bankMsg", "查询返回结果不存在");
      }
      
    }
    
    //根据匹配的结果，更新提现记录
    int bankNumS=0;
    int bankNumF=0;
    int bankNumW=0;
    int updateNumS=0;
    int updateNumF=0;
    List<Map<String, Object>> faildList=new ArrayList<Map<String,Object>>();
    for (Map<String, Object> map : cashList) {
      String bankStatus=(String)map.get("bankStatus");
      String openStatus="";
      String cashStatus="";
      if ("1".equals(bankStatus)) {
        openStatus="4";
        cashStatus="5";
        map.put("openStatus", openStatus);
        map.put("cashStatus", cashStatus);
        map.put("isBack", "0");
        int ret=purseSerivce.updateCashStatus(map);
        updateNumS+=ret;
        bankNumS++;
      }else if ("0".equals(bankStatus)) {
        openStatus="5";
        cashStatus="6";
        map.put("openStatus", openStatus);
        map.put("cashStatus", cashStatus);
        map.put("isBack", "1");
        faildList.add(map);
        int ret=purseSerivce.updateCashStatus(map);
        updateNumF+=ret;
        bankNumF++;
      }else if ("2".equals(bankStatus)) {
        bankNumW++;
      }
      
    }
    log.info("本次任务共扫描到" + cashList.size() + "条待同步记录,同步到通道"+bankNum+"条结果");
    log.info("转账成功"+bankNumS+"条，更新[转账成功]"+updateNumS+"条");
    log.info("转账失败"+bankNumF+"条，更新[转账失败]"+updateNumF+"条");
    log.info("银行处理中"+bankNumW+"条，未做更新操作");
    
    if (faildList.size()>0) {
      this.asyPurseBalance(faildList);
    }
    
    
  }
  
  /**
   * 
   * 功能：根据查询编号选择结算通道同步转账结果
   *
   * @param cashFileId
   * @param cashChannel
   * @return
   */
  private List<Map<String, Object>> getBankStatusFileId(String cashFileId,String cashChannel) {
    List<Map<String, Object>> transferBankList=new ArrayList<Map<String,Object>>();
    if ("ECITIC".equalsIgnoreCase(cashChannel)) {
      transferBankList=eciticPayService.getTransferByFileEcitic(cashFileId);
    }else if ("HXB".equalsIgnoreCase(cashChannel)) {
      transferBankList=hxbPayService.getTransferFileHXB(cashFileId);
    }else if ("SZFS".equalsIgnoreCase(cashChannel)) {
      transferBankList=szfsPayService.getTransferByFileSzfs(cashFileId);
    }else if ("CMBC_XM_API".equalsIgnoreCase(cashChannel)) {
      transferBankList=cmbcXMPayService.getTransferByFileCmbcXM(cashFileId);
    }
    
    return transferBankList;
  }
  
  
  /**
   * 
   * 功能：异步调用钱包余额冲正接口
   *
   * @param transList
   */
  public void asyPurseBalance(List<Map<String, Object>> transList) {
    
    Map<String, Object> params= new HashMap<String, Object>();
    params.put("remark", "提现失败,余额冲正");
    params.put("channel", "CASH");
    params.put("list", transList);
    
    log.info("异步发送余额冲正请求，共"+transList.size()+"条冲正明细" );
    new PurseRecService(params,purseSerivce).start();
    
  }
  
  

}
