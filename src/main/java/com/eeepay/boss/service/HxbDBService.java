/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.MasterDao;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.hxb.pub.map.DataMap;
import com.eeepay.hxb.pub.util.HxbConfig;
import com.eeepay.hxb.pub.util.Tools;

/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年10月27日
 */
@Service
public class HxbDBService {

private static final Logger log=LoggerFactory.getLogger(HxbDBService.class);
  
  @Resource
  private MasterDao masterDao;
  
  /**
   * 
   * 功能：新增上传文件记录
   *
   * @param params
   * @throws SQLException 
   */
  public String insertTransferUploadFile(DataMap params) throws SQLException {
    String sql="insert into hxb_upload_file(id,file_name,operator_id,operator_name,legal_count,illegal_count,legal_amount," +
        "summary,transfer_type,submit_channel,create_time,status,err_code,err_msg) values(NULL,?,?,?,?,?,?,?,?,?,NOW(),?,?,?) /*use_master*/";
    List<Object> list=new ArrayList<Object>();
    list.add(params.getString("fileName"));
    list.add(params.getString("operatorId"));
    list.add(params.getString("operatorName"));
    list.add(params.getString("totalNumber"));
    list.add(params.getString("illegalCount"));
    list.add(params.getString("totalAmount"));
    list.add(params.getString("summary"));
    list.add(params.getString("transferType"));
    list.add(params.getString("submitChannel"));
    list.add("0");
    list.add("");
    list.add("");
    return Long.toString(masterDao.updateGetID(sql, list));
  }
  
  
  /**
   * 
   * 功能：保存转账数据，包括新增批量数据+新增明细数据，保证事务完整
   *
   * @param params
   * @throws Exception 
   * @return 新增明细条数
   */
  public int saveTranfer(DataMap params,List<DataMap> tempList)   {
    Connection connection=masterDao.getConnection();
    int rowCount=0;
    
    try {
      connection.setAutoCommit(false);
      insertTransferBatch(params, connection);
      rowCount=insertTranserBatchDetail(params, tempList, connection);
      connection.commit();
    } catch (SQLException e) {
      log.error("新增转账数据异常:"+e.getMessage());
      e.printStackTrace();
      try {
        connection.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return rowCount;
    } finally{
      try {
        if (!connection.isClosed()) {
          DbUtils.closeQuietly(connection);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return rowCount;

  }
  
  

  
  
  
  /**
   * 
   * 功能：新增批量转账记录
   *
   * @param parsms
   * @throws SQLException 
   */
  private int insertTransferBatch(DataMap params,Connection connection) throws SQLException {
    String sql="insert into hxb_transfer_batch(batch_no,file_id,file_name,operator_id,operator_name,total_num," +
    "total_amt,transfer_type,submit_channel,create_time,summary,status,err_code,err_msg) values(?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?)";
     Object[] sqlParams=new Object[] {params.getString("batchNo"),params.getString("fileId"),params.getString("fileName"),
         params.getString("operatorId"),params.getString("operatorName"),params.getString("batchTotalNumber"),
         params.getString("batchTotalAmount"),params.getString("transferType"),params.getString("submitChannel"),params.getString("summary"),"0","",""};
  return masterDao.updateByTranscation(sql, sqlParams,connection);
  }
  
  /**
   * 
   * 功能：新增转账明细
   * @throws SQLException 
   *
   */
private int insertTranserBatchDetail(DataMap params,List<DataMap> list,Connection connection) throws SQLException {
   String sql="insert into hxb_transfer(id,flow_no,file_id,batch_no,seq_no,out_acc_no,out_acc_name," +
      "out_bank_no,out_bank_name,in_acc_no,in_acc_name,in_bank_no,in_bank_name," +
      "amount,create_time,status,err_code,err_msg) values(NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?)";
  
   int resultCount=0;
   for (int i = 0; i < list.size(); i++) {
     DataMap dataMap=list.get(i);
     Object[] sqlParams=new Object[] {dataMap.getString("flowNo"),params.getString("fileId"),params.getString("batchNo"),dataMap.getString("seqNo"),
         params.getString("outAccNo"),params.getString("outAccName"),params.getString("outBankNo"),params.getString("outBankName"),
         dataMap.getString("inAccNo"),dataMap.getString("inAccName"),dataMap.getString("inBankNo"),dataMap.getString("inBankName"),
         dataMap.getString("amount"),"0",dataMap.getString("errCode"),dataMap.getString("errMsg")};
     int temp=masterDao.updateByTranscation(sql, sqlParams,connection);
     resultCount+=temp;
   }
   return resultCount;
  }
  
/**
 * 
 * 功能：删除上传文件记录，删除关联的批次转账记录和转账明细记录,保证事务一致
 *
 * @param params
 * @throws Exception 
 * @return 新增明细条数
 * @throws SQLException 
 */
public int deleteUploadFile(String fileId) throws SQLException   {
  Connection connection=masterDao.getConnection();
  int rowCount=0;
  try {
    connection.setAutoCommit(false);
    deleteFileUpload(fileId, connection);
    deleteTransferBatch(fileId, connection);
    deleteTransferDetail(fileId, connection);
    connection.commit();
  } catch (SQLException e) {
    log.error("删除上传文件数据异常:"+e.getMessage());
    e.printStackTrace();
    try {
      connection.rollback();
    } catch (SQLException e1) {
      e1.printStackTrace();
    }
    throw e;
  } finally{
    try {
      if (!connection.isClosed()) {
        DbUtils.closeQuietly(connection);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  return rowCount;

}  


/**
 * 
 * 功能：删除上传文件记录
 *
 * @param fileId
 * @param connection
 * @return
 * @throws SQLException
 */
public int deleteFileUpload(String fileId,Connection connection) throws SQLException {
  String sql="delete from hxb_upload_file where id=? "; 
return masterDao.updateByTranscation(sql,new Object[] {fileId},connection);
}

/**
 * 
 * 功能：删除批次转账记录
 *
 * @param fileId
 * @param connection
 * @return
 * @throws SQLException
 */
public int deleteTransferBatch(String fileId,Connection connection) throws SQLException {
  String sql="delete from  hxb_transfer_batch where file_id=? "; 
return masterDao.updateByTranscation(sql,new Object[] {fileId},connection);
}

/**
 * 
 * 功能：删除转账明细记录
 *
 * @param fileId
 * @param connection
 * @return
 * @throws SQLException
 */
public int deleteTransferDetail(String fileId,Connection connection) throws SQLException {
  String sql="delete from  hxb_transfer where file_id=? "; 
return masterDao.updateByTranscation(sql,new Object[] {fileId},connection);
}




/**
 * 
 * 功能：修改上传文件记录状态
 *
 * @param params
 * @return
 */
  
  public int updateTranserFileUpload(DataMap params) {
    String sql = "update hxb_upload_file set status=?,err_code=?,err_msg=?  where id=?";
    Object[] sqlParams = new Object[] { params.getString("status"),
        params.getString("errCode"), params.getString("errMsg"),
        params.getString("fileId") };
    int ret = 0;
    try {
      ret = masterDao.update(sql, sqlParams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }
  
  
  /**
   *  
   * 功能：修改批量上传记录状态
   *
   * @param params
   * @return
   * @throws SQLException 
   */
  public int updateTranserBatch(DataMap params) {
    String sql = "update hxb_transfer_batch set status=?,summary=?,transfer_time=NOW(),err_code=?,err_msg=?  where batch_no=?";
    Object[] sqlParams = new Object[] { params.getString("status"),
        params.getString("summary"), params.getString("errCode"),
        params.getString("errMsg"), params.getString("batchNo") };
    int ret = 0;

    try {
      ret = masterDao.update(sql, sqlParams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }
  
  
  
  /**
   *  
   * 功能：修改明细状态
   *
   * @param params
   * @return
   * @throws SQLException 
   */
  public int updateTranserDetail(DataMap params) {
    String sql = "update hxb_transfer set status=?,err_code=?,err_msg=?  where batch_no=?";
    Object[] sqlParams = new Object[] { params.getString("status"),
        params.getString("errCode"), params.getString("errMsg"),
        params.getString("batchNo") };
    int ret = 0;

    try {
      ret = masterDao.update(sql, sqlParams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }

 
 /**
  * 
  * 功能：根据日期查询上传文件列表
  *
  * @param params
  * @param pageRequest
  * @return
  */
 public Page<Map<String, Object>> queryFileUploadPage(Map<String, String> params,
     PageRequest pageRequest) {
   String startDate=params.get("startDate");
   String endDate=params.get("endDate");
   String transferType=params.get("transferType");
   String submitChannel=params.get("submitChannel");
   
   List<Object> paramsList=new ArrayList<Object>();
   String sql = "select * from hxb_upload_file where 1=1 ";
   if (!Tools.isStrEmpty(startDate)) {
    sql+="and str_to_date(create_time,'%Y-%m-%d')>=? ";
    paramsList.add(startDate);
  }
   if (!Tools.isStrEmpty(endDate)) {
    sql+="and str_to_date(create_time,'%Y-%m-%d')<=? ";
    paramsList.add(endDate);
  }
   if (!Tools.isStrEmpty(transferType)) {
     sql+="and transfer_type=? ";
     paramsList.add(transferType);
   }
   if (!Tools.isStrEmpty(submitChannel)) {
     sql+="and submit_channel=? ";
     paramsList.add(submitChannel);
   } 
   
   sql+=" order by create_time desc";
   
   if (paramsList.size()>0) {
     return masterDao.find(sql, paramsList.toArray(), pageRequest);
  }else {
    return masterDao.find(sql, null, pageRequest);
  }
   
 }
 
 /**
  * 
  * 功能：根据fileId查询上传文件记录
  *
  * @param params
  * @param 
  * @return
  */
 public Map<String, Object> queryFileUploadByFileId(String fileId) {
   String sql = "select * from hxb_upload_file where id=? ";
   return masterDao.findFirst(sql, fileId);
 }
 
 
 /**
  * 
  * 功能：根据fileId查询关联批量记录
  *
  * @param params
  * @return
  */
 public List<Map<String,Object>> queryTransferBatch(Map<String, String> params){
   String sql="select * from hxb_transfer_batch where file_id=? order by batch_no asc ";
   Object[] sqlParams=new Object[] {params.get("fileId")};
   return masterDao.find(sql, sqlParams);
 }
 
 /**
  * 
  * 功能：根据batchNo查询批量记录
  *
  * @param batchNo
  * @return
  */
 public Map<String,Object> queryTransferBatchByBatchNo(String batchNo){
   String sql="select * from hxb_transfer_batch where batch_no=? ";
   return masterDao.findFirst(sql, new Object[]{batchNo});
 }
 
 /**
  * 
  * 功能：根据batchNo查询批量记录
  *
  * @param batchNo
  * @return
  */
 public List<Map<String,Object>> queryTransferBatchByFileId(String fileId){
   String sql="select * from hxb_transfer_batch where file_id=? order by batch_no asc  ";
   log.info("执行sql："+sql+"[fileId="+fileId+"]");
   return masterDao.find(sql,  new Object[]{fileId});
 }
 
 
 /**
  * 
  * 功能：根据batchNo查询转账明细
  *
  * @param batchNo
  * @return
  */
 public List<Map<String,Object>> queryTransferDetailByBatchNo(String batchNo){
   String sql="select * from hxb_transfer where batch_no=? order by id";
   return masterDao.find(sql, new Object[]{batchNo});
 }
 
 
 /**
  * 
  * 功能：根据fileId查询转账明细
  *
  * @param fileId
  * @return
  */
 public List<Map<String,Object>> queryTransferDetailByFileId(String fileId){
   String sql="select * from hxb_transfer where file_id=? order by id asc ";
   return masterDao.find(sql, new Object[]{fileId});
 }
 
  
  /*
   *  获取批量转账批次号,27位,9位客户号+8位日期+10位序列号
   * */
  public String getHxbTransferBatchNo() {
    List<Object> list=new ArrayList<Object>();
    String sql = "insert into hxb_batch_no_seq(id,time) values(NULL,NOW()) ";
    String batchNo="";
    try {
      batchNo = Long.toString(masterDao.updateGetID(sql,list));
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("获取批次号异常:"+e.getMessage());
      return batchNo;
    }
    batchNo=StringUtil.stringFillLeftZero(batchNo, 10);
    return HxbConfig.getProperties("hxb_b2e_eeepay_cid")+Tools.getSysDate()+batchNo;
  }
  
  /*
   *  获取企业流水号,20位
   * */
  public String getHxbFlowNo()  {
    List<Object> list=new ArrayList<Object>();
    String sql = "insert into hxb_flow_no_seq(id,time) values(NULL,NOW()) ";
    String flowNo="";
    try {
      flowNo = Long.toString(masterDao.updateGetID(sql,list));
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("获取企业流水号异常异常:"+e.getMessage());
      return flowNo;
    }
    return StringUtil.stringFillLeftZero(flowNo,20);
  }
  
}
