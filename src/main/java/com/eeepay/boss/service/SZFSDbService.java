package com.eeepay.boss.service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.search.StringTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.StringUtil;

/**
 *  描述：深圳金融结算中心
 *  
 *  @author:ym
 *  创建时间：2014-10-08
 */

@Service
public class SZFSDbService  {
  
  @Resource
  private Dao dao;

  private static final Logger log = LoggerFactory.getLogger(SZFSDbService.class);


	
  /**
   * 
   * 功能：新增流水
   *
   * @param params
   */
  public String insertAccountVerify(Map<String, String> params) {
    String sql="insert into szfs_acc_verify(id,merchant_id,channel,verify_type,bank_no,currency,acc_no," 
        + " acc_name,identity_type,identity_id,mobile_no,momo,verify_time,verifyer_id,verifyer_name,status,err_code,err_msg,"
        + " szfs_status,szfs_procode,szfs_remark) "
        + " values(NULL,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?,?,?,?,?)";
    List<Object> list=new ArrayList<Object>();
    list.add(params.get("merchantId"));
    list.add(params.get("channel"));
    list.add(params.get("verifyType"));
    list.add(params.get("bankNo"));
    list.add(params.get("currency"));
    list.add(params.get("accNo"));
    list.add(params.get("accName"));
    list.add(params.get("identityType"));
    list.add(params.get("identityId"));
    list.add(params.get("mobileNo"));
    list.add(params.get("momo"));
    list.add(params.get("verifyerId"));
    list.add(params.get("verifyerName"));
    list.add("0");
    list.add("");
    list.add("");
    list.add("");
    list.add("");
    list.add("");
    
    long ret=0;
    try {
      ret = dao.updateGetID(sql, list);
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("插入账户验证流水失败:"+e.getMessage());
    }
    return Long.toString(ret);
  }

  
  /**
   * 
   * 功能：修改流水
   *
   * @param params
   * @return
   */
    
  public int updateTranserFileUpload(Map<String, String> params) {
    String sql = " update szfs_acc_verify set status=? ";
    List<String> paramsList = new ArrayList<String>();
    String status =  params.get("status");
    String errCode =  params.get("errCode");
    String errMsg =  params.get("errMsg");
    String szfsStatus =  params.get("szfsStatus");
    String szfsProcode =  params.get("szfsProcode");
    String szfsRemark =  params.get("szfsRemark");
    String id =  params.get("id");

    paramsList.add(status);
    if (!StringUtil.isBlank(errCode)) {
      sql += " ,err_code=? ";
      paramsList.add(errCode);
    }
    if (!StringUtil.isBlank(errMsg)) {
      sql += " ,err_msg=? ";
      paramsList.add(errMsg);
    }
    if (!StringUtil.isBlank(szfsStatus)) {
      sql += " ,szfs_status=? ";
      paramsList.add(szfsStatus);
    }
    if (!StringUtil.isBlank(szfsProcode)) {
      sql += " ,szfs_procode=? ";
      paramsList.add(szfsProcode);
    }
    if (!StringUtil.isBlank(szfsRemark)) {
      sql += " ,szfs_remark=? ";
      paramsList.add(szfsRemark);
    }

    sql += "  where id=?";
    paramsList.add(id);

    int ret = 0;
    try {
      ret = dao.update(sql, paramsList.toArray());
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("修改账户验证流水失败:" + e.getMessage());
    }
    return ret;
  }
  
  /**
   * 
   * 功能：查询流水
   *
   * @param 
   * @return
   */
  public List<Map<String, Object>> queryAccVerify(String accNo,String accName,String identityType,String identityId) {
    String sql="select * from szfs_acc_verify where 1=1  ";
    List<String> paramsList = new ArrayList<String>();
    
    if (!StringUtil.isBlank(accNo)) {
      sql += " and acc_no=? ";
      paramsList.add(accNo);
    }
    if (!StringUtil.isBlank(accName)) {
      sql += " and acc_name=? ";
      paramsList.add(accName);
    }
    if (!StringUtil.isBlank(identityType)) {
      sql += " and identity_type=? ";
      paramsList.add(identityType);
    }
    if (!StringUtil.isBlank(identityId)) {
      sql += " and identity_id=? ";
      paramsList.add(identityId);
    }
    
    return dao.find(sql, paramsList.toArray() );
  }
  
  
  /**
   * 
   * 功能：新增流水
   *
   * @param params
   */
  public String insertQrCodeOrder(Map<String, String> params) {
    String sql="insert into szfs_qccode_order(id,channel,channel_seq_no,busi_type,mer_code,pos_id,perf_type," 
        + " init_order_amount,order_amount,channel_bak,center_resp_code,center_resp_desc,center_seq_id,center_status,pic_code,"
        + " create_time,status,err_code,err_msg,bak1,bak2) "
        + " values(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?,?)";
    List<Object> list=new ArrayList<Object>();
    list.add(params.get("channel"));
    list.add(params.get("channelSeqNo"));
    list.add(params.get("busiType"));
    list.add(params.get("merCode"));
    list.add(params.get("posId"));
    list.add(params.get("prefType"));
    list.add(params.get("initOrderAmount"));
    list.add(params.get("orderAmount"));
    list.add(params.get("channel_bak"));
    list.add(params.get(""));
    list.add(params.get(""));
    list.add(params.get(""));
    list.add(params.get(""));
    list.add(params.get(""));
    list.add("0");
    list.add("");
    list.add("");
    list.add("");
    list.add("");
    
    long ret=0;
    try {
      ret = dao.updateGetID(sql, list);
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("插入二维码订单失败:"+e.getMessage());
    }
    return Long.toString(ret);
  }
  
  public int updateQrCodeOrder(Map<String, String> params) {
    String sql = " update szfs_qccode_order set status=? ";
    List<String> paramsList = new ArrayList<String>();
    String status =  params.get("status");
    String errCode =  params.get("errCode");
    String errMsg =  params.get("errMsg");
    String channelBak=params.get("channelBak");
    String bak1 =  params.get("bak1");
    String bak2 =  params.get("bak2");
    String centerRespCode =  params.get("centerRespCode");
    String centerRespDesc =  params.get("centerRespDesc");
    String centerSeqId =  params.get("centerSeqId");
    String centerStatus =  params.get("centerStatus");
    String picCode =  params.get("picCode");
    String centerPayDate =  params.get("centerPayDate");
    String centerPayTime =  params.get("centerPayTime");
    String centerPayBankCode =  params.get("centerPayBankCode");
    String centerPayAccNo =  params.get("centerPayAccNo");
    String notifyFlag =  params.get("notifyFlag");
    String notifyMsg =  params.get("notifyMsg");
    String id =  params.get("id");

    paramsList.add(status);
    if (!StringUtil.isBlank(errCode)) {
      sql += " ,err_code=? ";
      paramsList.add(errCode);
    }
    if (!StringUtil.isBlank(errMsg)) {
      sql += " ,err_msg=? ";
      paramsList.add(errMsg);
    }
    if (!StringUtil.isBlank(channelBak)) {
      sql += " ,channel_bak=? ";
      paramsList.add(channelBak);
    }
    if (!StringUtil.isBlank(bak1)) {
      sql += " ,bak1=? ";
      paramsList.add(bak1);
    }
    if (!StringUtil.isBlank(bak2)) {
      sql += " ,bak2=? ";
      paramsList.add(bak2);
    }
    if (!StringUtil.isBlank(centerRespCode)) {
      sql += " ,center_resp_code=? ";
      paramsList.add(centerRespCode);
    }
    if (!StringUtil.isBlank(centerRespDesc)) {
      sql += " ,center_resp_desc=? ";
      paramsList.add(centerRespDesc);
    }
    if (!StringUtil.isBlank(centerSeqId)) {
      sql += " ,center_seq_id=? ";
      paramsList.add(centerSeqId);
    }
    if (!StringUtil.isBlank(centerStatus)) {
      sql += " ,center_status=? ";
      paramsList.add(centerStatus);
    }
    if (!StringUtil.isBlank(picCode)) {
      sql += " ,pic_code=? ";
      paramsList.add(picCode);
    }
    
    
    if (!StringUtil.isBlank(centerPayDate)) {
      sql += " ,center_pay_date=? ";
      paramsList.add(centerPayDate);
    }
    if (!StringUtil.isBlank(centerPayTime)) {
      sql += " ,center_pay_time=? ";
      paramsList.add(centerPayTime);
    }
    if (!StringUtil.isBlank(centerPayBankCode)) {
      sql += " ,center_pay_bank_code=? ";
      paramsList.add(centerPayBankCode);
    }
    if (!StringUtil.isBlank(centerPayAccNo)) {
      sql += " ,center_pay_acc_no=? ";
      paramsList.add(centerPayAccNo);
    }
    if (!StringUtil.isBlank(notifyFlag)) {
      sql += " ,merchant_notify=? ";
      paramsList.add(notifyFlag);
    }
    if (!StringUtil.isBlank(notifyMsg)) {
      sql += " ,notify_msg=? ";
      paramsList.add(notifyMsg);
    }

    sql += "  where id=?";
    paramsList.add(id);

    int ret = 0;
    try {
      ret = dao.update(sql, paramsList.toArray());
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("修改二维码订单失败:" + e.getMessage());
    }
    return ret;
  }
  
  
  public Map<String, Object> queryQrCodeOrder(String seqNo) {
    String sql="select * from szfs_qccode_order where id= "+seqNo;
    log.info("执行sql:"+sql);
    return dao.findFirst(sql);
  }
  

}
