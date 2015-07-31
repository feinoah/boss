/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.math.IntRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.MasterDao;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.hxb.pub.util.Tools;

/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年7月24日
 */

@Service
public class SettleTransferDBService {
  private static final Logger log=LoggerFactory.getLogger(SettleTransferDBService.class);

  @Resource
  private MasterDao masterDao;
  
  
  /*
   *  获取批量转账凭证号
   * */
  public String getSettleTransferBatchId() {
    List<Object> list=new ArrayList<Object>();
    String sql = "insert into szfs_seq(id,time) values(NULL,NOW()) ";
    String batchId="";
    try {
      batchId = Long.toString(masterDao.updateGetID(sql,list));
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
    batchId=StringUtil.stringFillLeftZero(batchId, 10);
    return new SimpleDateFormat("yyyyMMddHH").format(new Date())+batchId;
  }
  
  
  /**
   * 
   * 功能：新增上传文件记录
   *
   * @param params
   * @throws SQLException 
   */
  public String insertTransferUploadFile(Map<String, String> params) throws SQLException {
    String sql="insert into settle_transfer_file(id,file_name,file_md5,operator_id,operator_name,"
        + " total_num,total_amount,summary,create_time,transfer_time,settle_bank,out_acc_no,"
        + " out_acc_name,out_bank_no,out_bank_name,out_settle_bank_no,status,err_code,err_msg,bak1,bak2) "
        + " values(NULL,?,?,?,?,?,?,?,NOW(),NULL,?,?,?,?,?,?,?,?,?,?,?)";
    List<Object> list=new ArrayList<Object>();
    list.add(params.get("fileName"));
    list.add(params.get("fileMd5"));
    list.add(params.get("operatorId"));
    list.add(params.get("operatorName"));
    list.add(params.get("totalNum"));
    list.add(params.get("totalAmount"));
    list.add(params.get("summary"));
    list.add(params.get("settleBank"));
    list.add(params.get("outAccNo"));
    list.add(params.get("outAccName"));
    list.add(params.get("outBankNo"));
    list.add(params.get("outBankName"));
    list.add(params.get("outSettleBankNo"));
    list.add("0");
    list.add("");
    list.add("");
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
  public int saveTranfer(Map<String, String> params,List<Map<String, String>> tempList)   {
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
  private int insertTransferBatch(Map<String, String> params,Connection connection) throws SQLException {
    String sql="insert into settle_transfer_batch(batch_id,file_id,create_time,transfer_time,settle_bank,"
        + " status,err_code,err_msg,bak1,bak2) values(?,?,NOW(),NULL,?,?,?,?,?,?)";
     Object[] sqlParams=new Object[] {params.get("batchId"),params.get("fileId"),params.get("settleBank"),"0","","","",""};
  return masterDao.updateByTranscation(sql, sqlParams,connection);
  }
  
  /**
   * 
   * 功能：新增转账明细
   * @throws SQLException 
   *
   */
private int insertTranserBatchDetail(Map<String, String> params,List<Map<String, String>> list,Connection connection) throws SQLException {
   String sql="insert into settle_transfer(id,batch_id,file_id,seq_no,file_name,settle_bank,in_acc_no,"
       + " in_acc_name,in_settle_bank_no,in_bank_no,in_bank_name,"
       + " amount,create_time,status,err_code,err_msg,bak1,bak2) "
       + " values(NULL,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?,?,?)";
  
    int resultCount = 0;
    for (Map<String, String> dataMap : list) {
      Object[] sqlParams = new Object[] { params.get("batchId"),
          params.get("fileId"), dataMap.get("seqNo"), params.get("fileName"), params.get("settleBank"),
          dataMap.get("inAccNo"), dataMap.get("inAccName"), dataMap.get("inSettleBankNo"),
          dataMap.get("inBankNo"), dataMap.get("inBankName"), dataMap.get("amount"), "0", "","",dataMap.get("bak1"),dataMap.get("bak2")};
      int temp = masterDao.updateByTranscation(sql, sqlParams, connection);
      resultCount += temp;
    }

    return resultCount;
  }
  
  
  /**
   * 
   * 功能：查询上传文件列表
   * 
   * @param params
   * @param pageRequest
   * @return
   */
  public Page<Map<String, Object>> queryFileUpload(Map<String, String> params,
      PageRequest pageRequest) {
    String startDate = params.get("startDate");
    String endDate = params.get("endDate");
    String settleBank=params.get("settleBank");
    List<Object> paramsList = new ArrayList<Object>();
    String sql = "select * from settle_transfer_file where 1=1 ";
    if (!Tools.isStrEmpty(startDate)) {
      sql += "and str_to_date(create_time,'%Y-%m-%d')>=? ";
      paramsList.add(startDate);
    }
    if (!Tools.isStrEmpty(endDate)) {
      sql += "and str_to_date(create_time,'%Y-%m-%d')<=? ";
      paramsList.add(endDate);
    }
    if (!Tools.isStrEmpty(settleBank)) {
      sql+=" and settle_bank=? ";
      paramsList.add(settleBank);
    }
    
    sql += " order by create_time desc";

    if (paramsList.size() > 0) {
      return masterDao.find(sql, paramsList.toArray(), pageRequest);
    } else {
      return masterDao.find(sql, null, pageRequest);
    }

  }
  
  
  /**
   * 
   * 功能：查询转账文件详情
   *
   * @param fileId
   * @return
   */
  public Map<String,Object> queryFileById(String fileId){
    String sql="select * from settle_transfer_file where id=? ";
    return masterDao.findFirst(sql, new Object[]{fileId});
  }
  
  
  /**
   * 
   * 功能：查询转账明细by fileId
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public Page<Map<String, Object>> queryTransByFileId(Map<String, String> params,PageRequest pageRequest) {
    String sql="select * from settle_transfer where file_id=? ";
    String status=params.get("status");
    List<String> paramsList=new ArrayList<String>();
    paramsList.add(params.get("fileId"));
    if (!Tools.isStrEmpty(status)) {
      sql+=" and status=? ";
      paramsList.add(status);
    }
    return masterDao.find(sql,paramsList.toArray(), pageRequest);
    
  }
  
  /**
   * 
   * 功能：查询转账明细by fileId
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public List<Map<String, Object>> queryTransByFileId(Map<String, String> params) {
    List<String> paramsList=new ArrayList<String>();
    String sql="select * from settle_transfer where 1=1 ";
    String fileId=params.get("fileId");
    String inAccNo=params.get("inAccNo");
    String inAccName=params.get("inAccName");
    
    if (!Tools.isStrEmpty(fileId)) {
      sql+=" and file_id=? ";
      paramsList.add(fileId);
    }
    if (!Tools.isStrEmpty(inAccNo)) {
      sql+=" and in_acc_no=? ";
      paramsList.add(inAccNo);
    }
    if (!Tools.isStrEmpty(inAccName)) {
      sql+=" and in_acc_name=? ";
      paramsList.add(inAccName);
    }
    return masterDao.find(sql, paramsList.toArray());
    
  }
  
  public List<Map<String, Object>> queryTransByFileIdGroup(Map<String, String> params) {
    String sql=" select file_id,in_acc_no,in_acc_name,in_bank_no,in_bank_name,bak1,bak2,sum(amount) amount from settle_transfer where file_id=? "
        + " group by file_id,in_acc_no,in_acc_name,in_bank_no,in_bank_name,bak1,bak2 ";
    return masterDao.find(sql, new Object[]{params.get("fileId")});
    
  }
  
  
  /**
   * 
   * 功能：查询交易状态未知的交易
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public List<Map<String, Object>> queryTransUNByFileId(String fileId) {
    List<String> paramsList=new ArrayList<String>();
    paramsList.add(fileId);
    String sql=" select * from settle_transfer where file_id=? "
              + " and (status='1' or status='3' or status='6')";
    sql+="  order by id asc ";
    return masterDao.find(sql, paramsList.toArray());
    
  }
  
  /**
   * 
   * 功能：查询转账明细by fileId  (提交失败+超时)
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public List<Map<String, Object>> queryTransErrByFileId(Map<String, String> params) {
    String sql="select * from settle_transfer where file_id=? and (status='2' or status='3') ";
    return masterDao.find(sql, new Object[]{params.get("fileId")});
    
  }
  
  
  /**
   * 
   * 功能：查询转账批次 by fileId
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public List<Map<String, Object>> queryTransBatchByFileId(String fileId,String status) {
    List<String> paramsList=new ArrayList<String>();
    paramsList.add(fileId);
    String sql="select * from settle_transfer_batch where file_id=?";
    if (!Tools.isStrEmpty(status)) {
      sql+="  and status=? ";
      paramsList.add(status);
    }
    sql+="  order by batch_id asc ";
    return masterDao.find(sql, paramsList.toArray());
    
  }
  
  public List<Map<String, Object>> queryBatchByFileId(Map<String, String> params) {
    String fileId=params.get("fileId");
    String inAccNo=params.get("inAccNo");
    String inAccName=params.get("inAccName");

    List<String> paramsList=new ArrayList<String>();
    paramsList.add(fileId);
    String sql="select distinct(batch_id) from settle_transfer where file_id=?";
    
    if (!Tools.isStrEmpty(inAccNo)) {
      sql+="  and in_acc_no=? ";
      paramsList.add(inAccNo);
    }
    if (!Tools.isStrEmpty(inAccName)) {
      sql+="  and in_acc_name=? ";
      paramsList.add(inAccName);
    }
    sql+="  order by batch_id asc ";
    return masterDao.find(sql, paramsList.toArray());
    
  }
  
  /**
   * 
   * 功能：根据batchId查询批量记录
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public Map<String,Object> queryTransferBatchByBatchNo(String batchId){
    String sql="select * from settle_transfer_batch where batch_id=? ";
    return masterDao.findFirst(sql, new Object[]{batchId});
    
  }
  
  /**
   * 
   * 功能：查询转账明细by batchaId
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public List<Map<String, Object>> queryTransByBatch(String batchId) {
    String sql="select * from settle_transfer where batch_id=? ";
    return masterDao.find(sql, new Object[]{batchId});
    
  }
  
  
  /**
   * 
   * 功能：查询转账明细by id  
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public Map<String, Object> queryTransById(String id) {
    String sql="select * from settle_transfer where id=? ";
    return masterDao.findFirst(sql, new Object[]{id});
    
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
   * 功能：删除上传文件记录,事务内
   *
   * @param fileId
   * @param connection
   * @return
   * @throws SQLException
   */
  public int deleteFileUpload(String fileId,Connection connection) throws SQLException {
    String sql="delete from settle_transfer_file where id=? "; 
  return masterDao.updateByTranscation(sql,new Object[] {fileId},connection);
  }
  
  /**
   * 
   * 功能：删除批次转账记录,事务内
   *
   * @param fileId
   * @param connection
   * @return
   * @throws SQLException
   */
  public int deleteTransferBatch(String fileId,Connection connection) throws SQLException {
    String sql="delete from  settle_transfer_batch where file_id=? "; 
  return masterDao.updateByTranscation(sql,new Object[] {fileId},connection);
  }
  
  /**
   * 
   * 功能：删除转账明细记录,事务内
   *
   * @param fileId
   * @param connection
   * @return
   * @throws SQLException
   */
  public int deleteTransferDetail(String fileId,Connection connection) throws SQLException {
    String sql="delete from  settle_transfer where file_id=? "; 
  return masterDao.updateByTranscation(sql,new Object[] {fileId},connection);
  }
  
  
  /**
   * 
   * 功能：修改上传文件
   *
   * @param params
   * @return
   */
    
  public int updateTranserFile(Map<String, String> params) {
    String sql = "update settle_transfer_file set status=?,transfer_time=NOW(),err_code=?,err_msg=?  where id=?";
    Object[] sqlParams = new Object[] {params.get("status"),
        params.get("errCode"), params.get("errMsg"), params.get("fileId") };
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
     * 功能：修改批量转账
     *
     * @param params
     * @return
     * @throws SQLException 
     */
  public int updateTransBatch(Map<String, String> params) {
    String sql = "update settle_transfer_batch set status=?,transfer_time=NOW(),err_code=?,err_msg=?  where batch_id=?";
    Object[] sqlParams = new Object[] { params.get("status"),
        params.get("errCode"), params.get("errMsg"), params.get("batchId") };
    int ret = 0;
    try {
      ret = masterDao.update(sql, sqlParams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }
  
  public int updateTransBatchByFileId(Map<String, String> params) {
    String sql = "update settle_transfer_batch set status=?,transfer_time=NOW(),err_code=?,err_msg=?  where file_id=?";
    Object[] sqlParams = new Object[] { params.get("status"),
        params.get("errCode"), params.get("errMsg"), params.get("fileId") };
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
     * 功能：修改转账明细
     *
     * @param params
     * @return
     * @throws SQLException 
     */
  public int updateTransDetail(Map<String, String> params) {
    String sql = "update settle_transfer set status=?,err_code=?,err_msg=?  where batch_id=?";
    Object[] sqlParams = new Object[] { params.get("status"),
        params.get("errCode"), params.get("errMsg"), params.get("batchId") };
    int ret = 0;
    try {
      ret = masterDao.update(sql, sqlParams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }
  public int updateTransDetailByFileId(Map<String, String> params) {
    String sql = "update settle_transfer set status=?,err_code=?,err_msg=?  where file_id=?";
    Object[] sqlParams = new Object[] { params.get("status"),
        params.get("errCode"), params.get("errMsg"), params.get("fileId") };
    int ret = 0;
    try {
      ret = masterDao.update(sql, sqlParams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }
  public int updateTransDetailByFileIdCmbcXMApi(Map<String, String> params) {
    String sql = "update settle_transfer set status=?,err_code=?,err_msg=?  where file_id=? and status='0' ";
    Object[] sqlParams = new Object[] { params.get("status"),
        params.get("errCode"), params.get("errMsg"), params.get("fileId") };
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
     * 功能：修改转账明细  byid
     *
     * @param params
     * @return
     * @throws SQLException 
     */
    public int updateTransDetailById(String id,String status,String errCode,String errMsg )   {
      String sql="update settle_transfer set status=?,err_code=?,err_msg=?  where id=?"; 
      Object[] sqlParams=new Object[] {status,errCode,errMsg,id};
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
     * 功能：根据fileName查询上传文件记录
     *
     * @param fileId
     * @return
     */
    public Map<String,Object> queryFileByFileName(String fileName){
      String sql="select * from settle_transfer_file where file_name=?  ";
      return masterDao.findFirst(sql, new Object[]{fileName});
    }
    
    /**
     * 
     * 功能：根据文件md5摘要查询上传文件记录
     *
     * @param fileId
     * @return
     */
    public Map<String,Object> queryFileByFileMD5(String fileMd5){
      String sql="select * from settle_transfer_file where file_md5=?  ";
      return masterDao.findFirst(sql, new Object[]{fileMd5});
    }
    
    
    public Map<String,Object> queryIdentityIdByAccNo(String accNo){
      String sql="select * from pos_merchant where account_no="+accNo;
      return masterDao.findFirst(sql);
    }
    
    
    public Map<String,Object> queryChinaumsBatchId(String fileId,String settleBank,String date){
      String sql="select COUNT(1) batchId from settle_transfer_file  where str_to_date(create_time,'%Y-%m-%d')=? " + 
          " and id<=? and settle_bank=? ";
      return masterDao.findFirst(sql,new Object[]{date,fileId,settleBank} );
    }
    
}
