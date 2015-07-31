package com.eeepay.boss.service;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.enterprise.inject.New;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.BagDao;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.DictCache;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.utils.StringUtil;

/**
 * 手机钱包用户
 * @author Administrator
 *
 */
@Service
public class PurseService {
  private static final Logger log=LoggerFactory.getLogger(PurseService.class);

	@Resource
	private Dao dao;
	
	@Resource
	private BagDao bagDao;
	
	public int modifySave(Map<String, String> params) throws Exception{
		int count = 0;
		if(params != null){
			List<Object> list = new ArrayList<Object>();
			list.add(params.get("single_recharge_max_amount"));
			list.add(params.get("day_recharge_max_amount"));
			list.add(params.get("day_increment_max_amount"));
			list.add(params.get("day_extraction_max_amount"));
			list.add(params.get("day_increment_max_amount_count"));
			list.add(params.get("day_extraction_max_amount_count"));
			list.add(params.get("day_transfer_max_amount_count"));
			list.add(params.get("day_transfer_max_amount"));
			list.add(params.get("day_recharge_max_amount_count"));
			//list.add(params.get("retention_money"));
			list.add(params.get("tone_free_count"));
			//list.add(params.get("withdraws_time_star_short"));
			//list.add(params.get("withdraws_time_end_short"));
			list.add(params.get("is_weekend_withdraws"));
			list.add(params.get("tzero_withdraws_max_amount"));
			list.add(params.get("tone_withdraws_max_amount"));
			list.add(params.get("tzero_fee"));
			//list.add(params.get("tzero_fee_rate"));
			//list.add(params.get("tone_count_fee"));
			list.add(params.get("is_tzero"));
			list.add(params.get("id"));
			
			for(int i=0;i<list.size();i++){
				System.out.println("---"+i+"---"+list.get(i).toString());
			}
			
			StringBuffer sql = new StringBuffer("update bag_login b set b.single_recharge_max_amount=?,b.day_recharge_max_amount=?,");
			sql.append("b.day_increment_max_amount=?,b.day_extraction_max_amount=?,b.day_increment_max_amount_count=?,b.day_extraction_max_amount_count=?,b.day_transfer_max_amount_count=?,");
			sql.append("b.day_transfer_max_amount=?,b.day_recharge_max_amount_count=?,b.tone_free_count=?,");
			sql.append("b.is_weekend_withdraws=?,b.tzero_withdraws_max_amount=?,b.tone_withdraws_max_amount=?,b.tzero_fee=?,");
			sql.append("b.is_tzero=? where b.id=? ");
			System.out.println("----sql.toString()-----"+sql.toString()+"-----list.size()-----"+list.size());
			count = bagDao.update(sql.toString(), list.toArray());
		}
		return count;
	}
	
	public  List<Map<String, Object>> getSettleAccount(String moblie,String app_type) {
		String sql = "select sa.*,a.app_name from settle_account sa,app_type a where sa.app_type=a.app_no and sa.mobile_no=? and sa.app_type=?";
		return bagDao.find(sql, new Object[]{moblie,app_type});
	}
	
	public List<Map<String, Object>> getExtractionChannel(Map<String, String> params){
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select * from bag_extraction_channel where 1=1 ");
		if (null != params) {
			//id
			if(StringUtils.isNotEmpty(params.get("id"))){
				String id=params.get("id").trim();
				sb.append(" and id = ?");
				list.add(id);
			}
		}
		return bagDao.find(sb.toString(), list.toArray());
	}
	
	public Map<String, Object> getExtractionChannelById(String channelId){
		String sql = "select * from bag_extraction_channel where id=?";
		return bagDao.findFirst(sql, channelId);
	}
	
	public List<Map<String, Object>> getExtractionChannelLog(Integer channelId){
		String sql = "select * from bag_extraction_channel_log where channel_id=? order by id desc limit 5";
		return bagDao.find(sql, channelId);
	}
	
	public List<Map<String, Object>> getOtherExtractionChannelLog(){
		String sql = "select * from bag_extraction_channel_log where channel_id is null order by id desc limit 10";
		return bagDao.find(sql);
	}
	
	public Map<String, Object> getNowExtractionChannel(){
	  String sql = "select * from bag_extraction_channel "
        + "where  channel_state=1  ";
    Map<String, Object> map = bagDao.findFirst(sql);
    return map;
	}
	
		
	public Boolean updateChannelAutoCheck(Integer auto_check_state,String check_rate,String operater,String content){
		Connection conn = null;
		try {
			conn = bagDao.getConnection();
			conn.setAutoCommit(false);
			//String sql = "update bag_extraction_channel set auto_check_state=?,check_rate=?";
			String sql = "update sys_config set PARAM_VALUE=? where PARAM_KEY='autoCheckStatus'";
			String sql2 = "insert into bag_extraction_channel_log(operater,content,create_time) values(?,?,?)";
			bagDao.updateByTranscation(sql, new Object[]{auto_check_state},conn);
			bagDao.updateByTranscation(sql2, new Object[]{operater,content,new Date()},conn);
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Boolean updateRealTimeExtraction(Integer real_time_state,String operater,String content){
		Connection conn = null;
		try {
			conn = bagDao.getConnection();
			conn.setAutoCommit(false);
			String sql = "update sys_config set PARAM_VALUE=? where PARAM_KEY='realTimeCheckStatus'";
			String sql2 = "insert into bag_extraction_channel_log(operater,content,create_time) values(?,?,?)";
			bagDao.updateByTranscation(sql, new Object[]{real_time_state},conn);
			bagDao.updateByTranscation(sql2, new Object[]{operater,content,new Date()},conn);
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getParamValue(String paramKey){
		String sql = "select * from sys_config where PARAM_KEY=?";
		return (String)(bagDao.findBy(sql, "PARAM_VALUE", paramKey));
	}
	
	public Boolean updateWarnPhone(String mobileNo,String operater,String content){
		Connection conn = null;
		try {
			conn = bagDao.getConnection();
			conn.setAutoCommit(false);
			//String sql = "update bag_extraction_channel set warn_mobile_no=?";
			String sql = "update sys_config set PARAM_VALUE=? where PARAM_KEY='extractionChannelWarnPerson'";
			String sql2 = "insert into bag_extraction_channel_log(operater,content,create_time) values(?,?,?)";
			bagDao.updateByTranscation(sql, new Object[]{mobileNo},conn);
			bagDao.updateByTranscation(sql2, new Object[]{operater,content,new Date()},conn);
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Boolean updateChannel(Integer id,BigDecimal add_amount,BigDecimal warn_amount,String operater,String content){
		Connection conn = null;
		try {
			conn = bagDao.getConnection();
			conn.setAutoCommit(false);
			String sql = "update bag_extraction_channel set remain_amount=remain_amount+?,warn_amount=? where id=? ";
			String sql2 = "insert into bag_extraction_channel_log(channel_id,operater,content,create_time) values(?,?,?,?)";
			bagDao.updateByTranscation(sql, new Object[]{add_amount,warn_amount,id},conn);
			bagDao.updateByTranscation(sql2, new Object[]{id,operater,content,new Date()},conn);
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Boolean updateChannelState(Integer id,Integer channelState,String operater,String content){
		Connection conn = null;
		try {
			conn = bagDao.getConnection();
			conn.setAutoCommit(false);
			String sql = "update bag_extraction_channel set channel_state=? where id=? ";
			String sql2 = "insert into bag_extraction_channel_log(channel_id,operater,content,create_time) values(?,?,?,?)";
			/*if(channel!=null && !"".equals(channel)){
				//ecitic 表示中信银行   szfs 表示结算中心
				String sql3 = "update  sys_config set PARAM_VALUE=? where PARAM_KEY='purse_cash_bank'";
				dao.update(sql3, channel);
			}*/
			bagDao.updateByTranscation(sql, new Object[]{channelState,id},conn);
			bagDao.updateByTranscation(sql2, new Object[]{id,operater,content,new Date()},conn);
			conn.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//钱包提现手工开关设置查询
	public Map<String, Object> bagExtractionManualSwitchQuery(){
		String sql = "select * from bag_extraction_time";
		return bagDao.findFirst(sql);
	}
	//钱包提现手工开关设置更新
	public void bagExtractionManualSwitchUpdate(Integer id,String hoursBegin,
			String minutesBegin,String hoursEnd,String minutesEnd,Integer manualSwitch,
			String opinionTextArea,String notice) throws SQLException{
		String sql = "update bag_extraction_time set extraction_hours_begin=?,extraction_minutes_begin=?,"
				+ "extraction_hours_end=?,extraction_minutes_end=?,manual_switch=?,manual_opinion=?,"
				+ "notice=? where id=?";
		bagDao.update(sql, new Object[]{hoursBegin,minutesBegin,hoursEnd,minutesEnd,manualSwitch,opinionTextArea,notice,id});
	}
	
	
	public Page<Map<String, Object>> getUserList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			
			//手机号
			if(StringUtils.isNotEmpty(params.get("mobile_no"))){
				String mobile_no=params.get("mobile_no").trim();
				/*sb.append(" and b.mobile_no like ?");
				list.add("%"+mobile_no+"%");*/
				sb.append(" and b.mobile_no = ?");
				list.add(mobile_no);
			}
			
			// 用户名
			if (StringUtils.isNotEmpty(params.get("real_name"))) {
				String real_name = params.get("real_name").trim();
			/*	sb.append(" and b.real_name like ? ");
				list.add("%"+real_name+"%");*/
				sb.append(" and b.real_name = ? ");
				list.add(real_name);
			}
		}

		String sql="select  b.id,b.mobile_no,b.real_name,b.login_key,b.balance,b.last_use_time,b.last_login_time,b.create_time from bag_login  b where 1=1 "
			+sb.toString();
		return dao.find(sql, list.toArray(), pageRequest);
	}

	
	/**
	 * 数据库中 查询手机钱包用户详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getPurseDetailById(long id) throws SQLException{
		String sql="select * from bag_login where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}
	
	
	
	/**
	 * 密码重置
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int resetUserPassword(long id) throws SQLException {
		String password = "888888";
		password = MD5.toMD5(password);
		String sql = "update bag_login set password=? where id=?";
		int num = dao.update(sql, new Object[] { password, id });
		return num;
	}
	
	/**
	 * 手机充值查询
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getRechargeList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			
			//手机号
			if(StringUtils.isNotEmpty(params.get("mobile_no"))){
				String mobile_no=params.get("mobile_no").trim();
				/*sb.append(" and b.mobile_no like ?");
				list.add("%"+mobile_no+"%");*/
				sb.append(" and b.mobile_no = ?");
				list.add(mobile_no);
			}
		}
		String sql="select  b.id,b.mobile_no,b.amount,b.create_time from bag_recharge_his  b where 1=1 "
			+sb.toString();
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	
	/**
	 * 数据库中 查询手机充值详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getRechargeDetailById(long id) throws SQLException{
		String sql="select * from bag_recharge_his where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}
	
/*	*//**
	 * 手机提现查询
	 * @param params
	 * @param pageRequest
	 * @return
	 *//*
	public Page<Map<String, Object>> getExtractionList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			//手机号
			if(StringUtils.isNotEmpty(params.get("mobile_no"))){
				String mobile_no=params.get("mobile_no").trim();
				sb.append(" and b.mobile_no like ?");
				list.add("%"+mobile_no+"%");
				sb.append(" and b.mobile_no = ?");
				list.add(mobile_no);
			}
		}
		String sql="select  b.id,b.mobile_no,b.amount,b.create_time,b.account_no,b.account_name,b.settle_days,b.cnaps,b.bank_name  from bag_extraction_his  b where 1=1 "
			+sb.toString();
		return dao.find(sql, list.toArray(), pageRequest);
	}*/
	
	
	/**
	 * 数据库中 查询手机提现详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getExtractionDetailById(long id) throws SQLException{
		String sql="select * from bag_extraction_his where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}
	
	

	/**
	 * 钱包用户管理
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getBagLoginList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		try {
		if (null != params) {
			
			//手机号
			if(StringUtils.isNotEmpty(params.get("mobile_no"))){
				String mobile_no=params.get("mobile_no").trim();
				/*sb.append(" and b.mobile_no like ?");
				list.add("%"+mobile_no+"%");*/
				sb.append(" and b.mobile_no = ?");
				list.add(mobile_no);
			}
			
			// 用户名
			if (StringUtils.isNotEmpty(params.get("real_name"))) {
				String real_name = params.get("real_name").trim();
				/*sb.append(" and b.real_name like ? ");
				list.add("%"+real_name+"%");*/
				sb.append(" and b.real_name = ? ");
				list.add(real_name);
			}
			if(StringUtils.isNotEmpty(params.get("balanceType"))&&!params.get("balanceType").equals("-1")){
				if(params.get("balanceType").equals("0")){
					sb.append(" and b.balance > 0");
				}else{
					sb.append(" and b.balance1 > 0");
				}
			}
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				sb.append(" and b.create_time > ? ");
				list.add(params.get("createTimeBegin"));
			}
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				sb.append(" and b.create_time < ? ");
				list.add(params.get("createTimeEnd"));
			}
			
		}

		/*String sql="select  b.id,b.status,b.mobile_no,a.app_name,b.real_name,b.login_key,b.balance,b.balance1,b.last_use_time,b.last_login_time,b.create_time,b.app_type from bag_login  b,app_type a where 1=1 and b.app_type=a.app_no "*/
		String sql="select  b.app_type,b.mobile_no,b.real_name,b.balance,b.balance1,b.status,b.retention_money,b.manual_retention_money,b.id,t.app_name from bag_login  b  ,app_type t where t.app_no=b.app_type "
			+sb.toString()+"order by b.create_time desc";
		return bagDao.find(sql, list.toArray(), pageRequest);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAppName(String appType) throws SQLException {
		String sql = "select app_name from app_type where app_no=?";
		return (String)bagDao.findBy(sql, "app_name", appType);
	}
	
	
	
	/**
	 * 数据库中 查询手机钱包用户详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getBagDetailById(long id) throws SQLException{
		String sql="select b.*,a.app_name from bag_login b,app_type a where b.id=? and b.app_type=a.app_no";
		Map<String,Object> map= bagDao.findFirst(sql, id);
		return map;
	}
	
	/**
	 * 钱包密码重置
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int resetbagUserPassword(long id) throws SQLException {
		String password = "888888";
		password = MD5.toMD5(password);
		String sql = "update bag_login set password=? where id=?";
		int num = bagDao.update(sql, new Object[] { password, id });
		return num;
	}
	
	
	
	  
	  /**
	   * 统计提现数据
	   * @param id
	   * @return
	   */
	  public Map<String, Object> queryCashStatistic(Map<String, String> params) {
		  try {
		  String mobileNo=params.get("mobileNo");
		    String accountName=params.get("accountName");
		    String openStatus=params.get("openStatus");
		    String purseBalanceType=params.get("purseBalanceType");
		    String checkPerson=params.get("checkPerson");
		    String accountNo=params.get("accountNo");
		    String startDate=params.get("startDate");
		    String endDate=params.get("endDate");
		    String checkStartDate=params.get("checkStartDate");
		    String checkEndDate=params.get("checkEndDate");
		    String merchantNo= params.get("merchantNo");
		    List<Object> paramsList=new ArrayList<Object>();
		    String sql = "select count(1) totalCount,sum(amount) totalAmount,sum(fee) totalFee from bag_extraction_his b " +
		    		" left join app_type t on t.app_no=b.app_type  " +
		    		" left join bag_merchant bm on b.app_type = bm.app_type and b.mobile_no = bm.mobile_no where 1 =1 ";
		    
		    if (!StringUtil.isBlank(mobileNo)) {
		      sql+="and b.mobile_no=? ";
		      paramsList.add(mobileNo);
		    }
		    if (!StringUtil.isBlank(merchantNo)) {
		        sql+="and bm.merchant_no=? ";
		        paramsList.add(merchantNo);
		      }
		    if (!StringUtil.isBlank(accountName)) {
		      sql+="and b.account_name like ? ";
		      paramsList.add("%"+accountName+"%");
		    } 
		    if (!StringUtil.isBlank(openStatus)) {
		      sql+="and b.open_status = ? ";
		      paramsList.add(openStatus);
		    } 
		    if (!StringUtil.isBlank(purseBalanceType)) {
		      sql+="and b.settle_days = ? ";
		      paramsList.add(purseBalanceType);
		    } 
		    if (!StringUtil.isBlank(checkPerson)) {
		      sql+="and b.check_person like ? ";
		      paramsList.add("%"+checkPerson+"%");
		    } 
		    if (!StringUtil.isBlank(accountNo)) {
		      sql+="and b.account_no = ? ";
		      paramsList.add(accountNo);
		    } 
		    if (!StringUtil.isBlank(startDate)) {
		      sql += "and b.create_time >= ? ";
		      paramsList.add(startDate);
		    }
		    if (!StringUtil.isBlank(endDate)) {
		      sql += "and b.create_time <=? ";
		      paramsList.add(endDate);
		    }
		    if (!StringUtil.isBlank(checkStartDate)) {
		      sql += "and b.check_time >=? ";
		      paramsList.add(checkStartDate);
		    }
		    if (!StringUtil.isBlank(checkEndDate)) {
		      sql += "and b.check_time <=? ";
		      paramsList.add(checkEndDate);
		    }
		    
		    
		    Map<String, Object> list= bagDao.findFirst(sql, paramsList.toArray());
		    return list;
		  } catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			  return null;  
	  }
	
	/**
	 * 钱包提现查询
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getBagExtractionList(Map<String, String> params,
			PageRequest pageRequest) {
		try {
    String mobileNo=params.get("mobileNo");
    String accountName=params.get("accountName");
    String openStatus=params.get("openStatus");
    /*String purseBalanceType=params.get("purseBalanceType");*/
    String checkPerson=params.get("checkPerson");
    String channel = params.get("channel");
    String accountNo=params.get("accountNo");
    String startDate=params.get("startDate");
    String endDate=params.get("endDate");
    String checkStartDate=params.get("checkStartDate");
    String checkEndDate=params.get("checkEndDate");
    String merchantNo= params.get("merchantNo");
    List<Object> paramsList=new ArrayList<Object>();
    String sql = "select b.id ,b.account_name,b.create_time,b.mobile_no,b.amount,b.open_status,b.settle_days,b.fee,b.settle_amount,b.check_person,b.cash_channel,b.cnaps,b.bank_name,b.account_no,bm.merchant_no,t.app_name from bag_extraction_his b " +
    		" left join app_type t on t.app_no=b.app_type  " +
    		" left join bag_merchant bm on b.app_type = bm.app_type and b.mobile_no = bm.mobile_no where 1=1 ";
    
    if (!StringUtil.isBlank(mobileNo)) {
      sql+="and b.mobile_no=? ";
      paramsList.add(mobileNo);
    }
    if (!StringUtil.isBlank(merchantNo)) {
        sql+="and bm.merchant_no=? ";
        paramsList.add(merchantNo);
      }
    if (!StringUtil.isBlank(accountName)) {
      sql+="and b.account_name like ? ";
      paramsList.add("%"+accountName+"%");
    } 
    if (!StringUtil.isBlank(openStatus)) {
      sql+="and b.open_status = ? ";
      paramsList.add(openStatus);
    } 
    /*if (!StringUtil.isBlank(purseBalanceType)) {
      sql+="and b.settle_days = ? ";
      paramsList.add(purseBalanceType);
    } */
    if (!StringUtil.isBlank(checkPerson)) {
      sql+="and b.check_person like ? ";
      paramsList.add("%"+checkPerson+"%");
    } 
    if (!StringUtil.isBlank(channel)) {
	    sql+="and b.cash_channel = ? ";
	    paramsList.add(channel);
	  } 
    if (!StringUtil.isBlank(accountNo)) {
      sql+="and b.account_no = ? ";
      paramsList.add(accountNo);
    } 
    if (!StringUtil.isBlank(startDate)) {
      sql += "and b.create_time >= ? ";
      paramsList.add(startDate);
    }
    if (!StringUtil.isBlank(endDate)) {
      sql += "and b.create_time <=? ";
      paramsList.add(endDate);
    }
    if (!StringUtil.isBlank(checkStartDate)) {
      sql += "and b.check_time >=? ";
      paramsList.add(checkStartDate);
    }
    if (!StringUtil.isBlank(checkEndDate)) {
      sql += "and b.check_time <=? ";
      paramsList.add(checkEndDate);
    }
    
    sql+=" order by b.create_time desc";
    System.out.println("--------------"+sql);
    if (paramsList.size()>0) {
      return bagDao.find(sql, paramsList.toArray(), pageRequest);
   }else {
     return bagDao.find(sql, null, pageRequest);
   }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	  
	  
	}
	
	//查询所有提现审核人
	public List<Map<String, Object>> getChecker(){
		String sql = "select check_person from bag_extraction_his group by check_person";
		return bagDao.find(sql);
	}
	
	/**
	 * 钱包提现导出查询
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public List<Map<String, Object>> getBagExpordList(Map<String, String> params) {
		try {
	String mobileNo=params.get("mobileNo");
    String accountName=params.get("accountName");
    String openStatus=params.get("openStatus");
    String purseBalanceType=params.get("purseBalanceType");
    String checkPerson=params.get("checkPerson");
    String accountNo=params.get("accountNo");
    String startDate=params.get("startDate");
    String endDate=params.get("endDate");
    String checkStartDate=params.get("checkStartDate");
    String checkEndDate=params.get("checkEndDate");
    String merchantNo= params.get("merchantNo");
    List<Object> paramsList=new ArrayList<Object>();
    String sql = "select b.account_name,b.create_time,b.mobile_no,b.amount,b.open_status,b.settle_days,b.fee,b.settle_amount,b.check_person,b.cnaps,b.bank_name,b.account_no,bm.merchant_no,t.app_name from bag_extraction_his b " +
    		" left join app_type t on t.app_no=b.app_type  " +
    		" left join bag_merchant bm on b.app_type = bm.app_type and b.mobile_no = bm.mobile_no where 1 =1 ";
    
    if (!StringUtil.isBlank(mobileNo)) {
      sql+="and b.mobile_no=? ";
      paramsList.add(mobileNo);
    }
    if (!StringUtil.isBlank(merchantNo)) {
        sql+="and bm.merchant_no=? ";
        paramsList.add(merchantNo);
      }
    if (!StringUtil.isBlank(accountName)) {
      sql+="and b.account_name like ? ";
      paramsList.add("%"+accountName+"%");
    } 
    if (!StringUtil.isBlank(openStatus)) {
      sql+="and b.open_status = ? ";
      paramsList.add(openStatus);
    } 
    if (!StringUtil.isBlank(purseBalanceType)) {
      sql+="and b.settle_days = ? ";
      paramsList.add(purseBalanceType);
    } 
    if (!StringUtil.isBlank(checkPerson)) {
      sql+="and b.check_person like ? ";
      paramsList.add("%"+checkPerson+"%");
    } 
    if (!StringUtil.isBlank(accountNo)) {
      sql+="and b.account_no = ? ";
      paramsList.add(accountNo);
    } 
    if (!StringUtil.isBlank(startDate)) {
      sql += "and b.create_time >= ? ";
      paramsList.add(startDate);
    }
    if (!StringUtil.isBlank(endDate)) {
      sql += "and b.create_time <=? ";
      paramsList.add(endDate);
    }
    if (!StringUtil.isBlank(checkStartDate)) {
      sql += "and b.check_time >=? ";
      paramsList.add(checkStartDate);
    }
    if (!StringUtil.isBlank(checkEndDate)) {
      sql += "and b.check_time <=? ";
      paramsList.add(checkEndDate);
    }
    
    sql+=" order by b.create_time desc";
    if (paramsList.size()>0) {
      return bagDao.find(sql, paramsList.toArray());
   }else {
     return bagDao.find(sql, null);
   }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	  
	  
	}
	
	
	
	/**
	 * 钱包充值查询
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getBagRechargeList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {	
			//手机号
			if(StringUtils.isNotEmpty(params.get("mobile_no"))){
				String mobile_no=params.get("mobile_no").trim();
			/*	sb.append(" and b.mobile_no like ?");
				list.add("%"+mobile_no+"%");*/
				sb.append(" and b.mobile_no = ?");
				list.add(mobile_no);
			}
		}
		String sql="select  b.id,b.mobile_no,b.amount,b.create_time from bag_recharge_his  b where 1=1 "
			+sb.toString();
		return bagDao.find(sql, list.toArray(), pageRequest);
	}
	
	
	/**
	 * 数据库中 查询钱包提现详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getBagExtractionDetailById(long id) throws SQLException{
		String sql="select id,mobile_no,is_back,amount,back_status,settle_amount,account_no,account_name,fee,cnaps,bank_name,open_status,settle_days,cash_remark,check_remark,check_person,cash_channel,check_time,bank_no,settle_time,create_time from bag_extraction_his where id=?";
		Map<String,Object> map= bagDao.findFirst(sql, id);
		//Map<String,Object> map= testDao.findFirst(sql, id);
		try {
			if(map.get("check_time")!=null && !"".equals(map.get("check_time"))){
				SimpleDateFormat  dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String check_time=dft.format(map.get("check_time"));
				map.put("check_time", check_time);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 数据库中 查询钱包充值详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getBagRechargeDetailById(long id) throws SQLException{
		String sql="select * from bag_recharge_his where id=?";
		Map<String,Object> map= bagDao.findFirst(sql, id);
		return map;
	}
	
	
   
     /**
      * 更新审核状态
      * @param id
      * @author LJ
      * @return
      * @throws SQLException
      */
	public int checkRecharges(String id,Long check_id,String check_person) throws SQLException {
		 Date date=new Date();
        SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sys_date= dft.format(date);
		String sql = "update bag_extraction_his set open_status=?,check_id=?,check_person=?,check_time=? where id=?";
		Object[] ps = {1,check_id,check_person,sys_date,id};
		int n = bagDao.update(sql,ps);
		//int n = testDao.update(sql,ps);
		return n;
	}
	
	
	/**
     * 更新审核状态
     * @param id
     * @author LJ
     * @return
     * @throws SQLException
     */
	public void extractionModify(Map<String, String> params,Long check_id,String check_person) throws SQLException {
	   try {
			String check_remark=(String)params.get("check_remark").trim();
			String id=(String)params.get("id");
            Date date=new Date();
            SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sys_date= dft.format(date);
			
			String sql = "update bag_extraction_his set check_remark=?,check_id=?,check_person=?,check_time=?,open_status=?,is_back=? where id=?";
			Object[] ps = {check_remark,check_id,check_person,sys_date,2,1,id};
		    bagDao.update(sql,ps);
			//testDao.update(sql,ps);
	   } catch (Exception e) {
		// TODO: handle exception
	   }
		
	}
	
	
	
	
	
	/**
	 * 查询提现申请
	 */
	public List<Map<String, Object>> queryPurseCash(Map<String, String> paramsMap) {
		String sql = "select * from bag_extraction_his "
		    + "where 1=1 ";
		List<String> paramsList=new ArrayList<String>();
		String openStatus=paramsMap.get("openStatus");
		String cashStatus=paramsMap.get("cashStatus");
		String lockStamp=paramsMap.get("lockStamp");
		if(!StringUtil.isBlank(openStatus)){
			sql+=" and open_status=? ";
			paramsList.add(openStatus);
		}
		if(!StringUtil.isBlank(cashStatus)){
			sql+=" and cash_status=? ";
			paramsList.add(cashStatus);
		}
		if(!StringUtil.isBlank(lockStamp)){
			sql+=" and lock_stamp=? ";
			paramsList.add(lockStamp);
		}
		sql+=" order by id asc ";
		List<Map<String, Object>> send_list = bagDao.find(sql, paramsList.toArray());
		log.info("执行sql:"+sql);
		return send_list;
	}
	
	
	 /**
     * 提现初始化
     * 2天以内，2分钟以外的记录
     */
	public String initPurseCash(Map<String, String> params)  {
		String lockStamp=UUID.randomUUID().toString().replace("-", "");
		lockStamp=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"-"+lockStamp;
		String sql = "update bag_extraction_his set open_status=?,cash_status=?,"
				+ " lock_stamp=? where open_status=1 "
				+ " and datediff(NOW(),create_time)<2 and timestampdiff(MINUTE,create_time,NOW())>2 ";
		Object[] ps = {"3","1",lockStamp};
		int ret=0;
		try {
			ret=bagDao.update(sql,ps);
			log.info("执行sql:"+sql+"   更新数据"+ret+"条");
		} catch (SQLException e) {
			e.printStackTrace();
			return lockStamp;
		}
		return lockStamp;
	}
	
	public int initPurseCashDetail(String cashId){
	  String sql = "update bag_extraction_his set open_status='3',cash_status='1' "
        + "  where id=? and open_status='1' ";
    Object[] ps = {cashId};
    int ret=0;
    try {
      ret=bagDao.update(sql,ps);
      log.info("执行sql:"+sql+"   更新数据"+ret+"条");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
	}
	
	
	
	
  /**
   * 修改提现申请
   */
public int updatePurseCash(Map<String, String> params)  {
  String sql = "update bag_extraction_his set cash_time=now() ";
  List<String> paramsList=new ArrayList<String>();
  String openStatus=params.get("openStatus");
  String cashStatus=params.get("cashStatus");
  String cashFileId=params.get("cashFileId");
  String cashRemark=params.get("cashRemark");
  String lockStamp=params.get("lockStamp");
  String cashChannel=params.get("cashChannel");
  String isBack=params.get("isBack");
  
  if(!StringUtil.isBlank(openStatus)){
    sql+=" , open_status=? ";
    paramsList.add(openStatus);
  }
  if(!StringUtil.isBlank(cashStatus)){
    sql+=" , cash_status=? ";
    paramsList.add(cashStatus);
  }
  if(!StringUtil.isBlank(cashFileId)){
    sql+=" , cash_file_id=? ";
    paramsList.add(cashFileId);
  }
  if(!StringUtil.isBlank(cashRemark)){
    sql+=" , cash_remark=? ";
    paramsList.add(cashRemark);
  }
  if(!StringUtil.isBlank(cashChannel)){
    sql+=" , cash_channel=? ";
    paramsList.add(cashChannel);
  }
  if(!StringUtil.isBlank(isBack)){
    sql+=" , is_back=? ";
    paramsList.add(isBack);
  }
  
  paramsList.add(lockStamp);
  sql+=" where lock_stamp=? ";
  
  int ret=0;
  try {
    ret=bagDao.update(sql,paramsList.toArray());
    log.info("执行sql:"+sql+"  更新数据"+ret+"条");
  } catch (SQLException e) {
    e.printStackTrace();
    return ret;
  }
  return ret;
}
 
/**
 * 修改提现申请状态by  id
 */
public int updatePurseCashById(Map<String, String> params)  {
String sql = "update bag_extraction_his set cash_time=now() ";
List<String> paramsList=new ArrayList<String>();
String id=params.get("id");
String openStatus=params.get("openStatus");
String cashStatus=params.get("cashStatus");
String cashFileId=params.get("cashFileId");
String cashRemark=params.get("cashRemark");
String cashChannel=params.get("cashChannel");
String isBack=params.get("isBack");

if(!StringUtil.isBlank(openStatus)){
  sql+=" , open_status=? ";
  paramsList.add(openStatus);
}
if(!StringUtil.isBlank(cashStatus)){
  sql+=" , cash_status=? ";
  paramsList.add(cashStatus);
}
if(!StringUtil.isBlank(cashFileId)){
  sql+=" , cash_file_id=? ";
  paramsList.add(cashFileId);
}
if(!StringUtil.isBlank(cashRemark)){
  sql+=" , cash_remark=? ";
  paramsList.add(cashRemark);
}
if(!StringUtil.isBlank(cashChannel)){
  sql+=" , cash_channel=? ";
  paramsList.add(cashChannel);
}
if(!StringUtil.isBlank(isBack)){
  sql+=" , is_back=? ";
  paramsList.add(isBack);
}

paramsList.add(id);
sql+=" where id=? ";
log.info(sql);
int ret=0;
try {
  ret=bagDao.update(sql,paramsList.toArray());
} catch (SQLException e) {
  e.printStackTrace();
  return ret;
}
return ret;
}




public int updatePurseRec(Map<String, Object> params) {
  String sql="update bag_extraction_his set back_status=?,back_remark=? where id=?";
  try {
    int ret=bagDao.update(sql, new Object[]{(String)params.get("backStatus"),
        (String)params.get("backRemark"),params.get("id").toString()});
    return ret;
  } catch (SQLException e) {
    e.printStackTrace();
    return 0;
  }
  
}


		/**
		 * 
		 * 功能：查询需要同步的数据
		 *     状态为【提现中】，提现状态为【发送成功】或者【发送未知】
		 *     查询两天以内，5分钟意外的记录
		 *
		 * @param paramsMap
		 * @return
		 */
		public List<Map<String, Object>> querySynCash(Map<String, String> paramsMap) {
		  String sql = "select * from bag_extraction_his h where open_status=3 and (cash_status=2 or cash_status=4) "
		      + " and datediff(NOW(),create_time)<2 and timestampdiff(MINUTE,create_time,NOW())>5 ";
		  List<Map<String, Object>> send_list = bagDao.find(sql);
		  log.info("执行sql:"+sql);
		  return send_list;
		}
		
		/**
     * 
     * 功能：查询需要同步的cashFileId
     *     状态为【提现中】，提现状态为【发送成功】或者【发送未知】
     *     查询两天以内，5分钟意外的记录
     *
     * @param paramsMap
     * @return
     */
    public List<Map<String, Object>> querySynCashFileId() {
      String sql = "select cash_file_id,cash_channel from bag_extraction_his h where open_status=3 and (cash_status=2 or cash_status=4) "
          + "  and datediff(NOW(),create_time)<2 and timestampdiff(MINUTE,create_time,NOW())>5  "
          + " group by cash_file_id,cash_channel order by cash_file_id asc ";
      List<Map<String, Object>> send_list = bagDao.find(sql);
      log.info("执行sql:"+sql);
      return send_list;
    }


/**
 * 
 * 功能：更新提现记录状态
 *
 * @param params
 * @return
 */
public int updateCashStatus(Map<String, Object> params)  {
String sql = "update bag_extraction_his set open_status=?,cash_status=?,cash_remark=?,is_back=? "
    + " where cash_file_id=? and id=? ";
Object[] ps = {(String)params.get("openStatus"),(String)params.get("cashStatus"),
    (String)params.get("bankMsg"),(String)params.get("isBack"),
    (String)params.get("cash_file_id"),params.get("id")};
int ret=0;
try {
  ret=bagDao.update(sql,ps);
  log.info("执行sql:"+sql+"   更新数据"+ret+"条");
} catch (SQLException e) {
  e.printStackTrace();
  return ret;
}
return ret;
}

	/**
	 * 查询是否为钱包
	 * @param mobile_no
	 * @return
	 */
	public List<Map<String, Object>> findBagUser(String mobile_no) {
	  String sql = "select * from bag_login b  where  b.mobile_no=?";
	  List<String> paramsList=new ArrayList<String>();
	  paramsList.add(mobile_no);
	  List<Map<String, Object>> send_list = bagDao.find(sql, paramsList.toArray());
	  
	  //List<Map<String, Object>> send_list = testDao.find(sql, paramsList.toArray());
	  log.info("执行sql:"+sql);
	  return send_list;
	}
	
	
	/**
	 * 
	 * 功能：钱包入账记录查询
	 *
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> queryPurseTrans(Map<String, String> params,
      PageRequest pageRequest) {
		try {
	   String startDate=params.get("startDate");
	   String endDate=params.get("endDate");
	   String mobileNo=params.get("mobileNo");
	   String merchantName=params.get("merchantName");
	   String status=params.get("status");
	   String operStatus = params.get("operStatus");
	   System.out.println("--operStatus--"+operStatus);
	   List<Object> paramsList=new ArrayList<Object>();
	   String sql = "select distinct t.*,a.app_name from purse_trans_info t,pos_merchant p,app_pos_type a where t.merchant_no=p.merchant_no and p.app_no=a.app_no ";
	   if (!StringUtil.isBlank(startDate)) {
	    sql+="and str_to_date(t.create_time,'%Y-%m-%d')>=? ";
	    paramsList.add(startDate);
	  }
	   if (!StringUtil.isBlank(endDate)) {
	    sql+="and str_to_date(t.create_time,'%Y-%m-%d')<=? ";
	    paramsList.add(endDate);
	  }
	   if (!StringUtil.isBlank(mobileNo)) {
	     sql+="and t.mobile_no=? ";
	     paramsList.add(mobileNo);
	   }
	   if (!StringUtil.isBlank(status)) {
	     sql+="and t.status=? ";
	     paramsList.add(status);
	   } 
	   if (!StringUtil.isBlank(operStatus)) {
		   if("1".equals(operStatus)){
			   sql+="and t.hand_num<>0 ";
		   }else if("2".equals(operStatus)){
			   sql+="and t.hand_num=0 ";
		   }
	   } 
	   if (!StringUtil.isBlank(merchantName)) {
	     sql+="and t.merchant_name like ? ";
	     paramsList.add("%"+merchantName+"%");
	   } 
	   
	   sql+=" order by t.create_time desc";
	   System.out.println("*********-----"+sql);
	   if (paramsList.size()>0) {
	     return dao.find(sql, paramsList.toArray(), pageRequest);
	  }else {
	    return dao.find(sql, null, pageRequest);
	  }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
  }
	
	 /**
   * 查询钱包入账详情
   * @param id
   * @return
   */
  public Map<String, Object> queryPureseTransDetail(String id) {
    String sql="select * from purse_trans_info where id=?";
    Map<String,Object> map= dao.findFirst(sql, id);
    return map;
  }
  
  
  /**
   * 
   * 功能：修改钱包入账记录
   *
   * @param params
   * @return
   */
  public int updatePurseTrans(Map<String, String> params) {
     List<Object> paramsList=new ArrayList<Object>();
     String sql = "update purse_trans_info set err_code=err_code ";
    
     String status=params.get("status");
     String errCode=params.get("errCode");
     String errMsg=params.get("errMsg");
     String handStatus=params.get("handStatus");
     String hander=params.get("hander");
     String handErrCode=params.get("handErrCode");
     String handErrMsg=params.get("handErrMsg");
     String id=params.get("id");
     
     if (!StringUtil.isBlank(status)) {
      sql+=" ,status=? ";
      paramsList.add(status);
     }
     if (!StringUtil.isBlank(errCode)) {
       sql+=" ,err_code=? ";
       paramsList.add(errCode);
     }
     if (!StringUtil.isBlank(errMsg)) {
       sql+=" ,err_msg=? ";
       paramsList.add(errMsg);
     }
     if (!StringUtil.isBlank(handStatus)) {
       sql+=" ,hand_status=?,hand_num=hand_num+1,hand_time=NOW() ";
       paramsList.add(handStatus);
     }
     if (!StringUtil.isBlank(hander)) {
       sql+=" ,hander=? ";
       paramsList.add(hander);
     }
     if (!StringUtil.isBlank(handErrCode)) {
       sql+=" ,hand_err_code=? ";
       paramsList.add(handErrCode);
     }
     if (!StringUtil.isBlank(handErrMsg)) {
       sql+=" ,hand_err_msg=? ";
       paramsList.add(handErrMsg);
     }
     
     sql+=" where id=? ";
     paramsList.add(id);
     
     int ret=0;
     try {
      ret= dao.update(sql, paramsList.toArray());
    } catch (SQLException e) {
      e.printStackTrace();
    }
     return ret;
  }
  
	/**
	 * 冻结解冻
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int updateBagUserStatus(String id,String status) throws SQLException {
		
		String sql = "update bag_login set status=? where id=?";
		int num = bagDao.update(sql, new Object[] { status, id });
		return num;
	}
  
  /**
   * 功能：需手工冲正的钱包提现记录查询
   * @param params
   * @param pageRequest
   * @return
   */
  public Page<Map<String, Object>> queryReversalTrans(Map<String, String> params,
      PageRequest pageRequest) {
	  try {
     String startDate=params.get("startDate");
     String endDate=params.get("endDate");
     String mobileNo=params.get("mobileNo");
     String accountName=params.get("accountName");
     String bankRemark = params.get("bankRemark");
     
     List<Object> paramsList=new ArrayList<Object>();
     String sql = "select b.mobile_no,b.account_name,b.account_no,b.amount,b.create_time,b.open_status,b.back_status,b.back_remark,b.id,t.app_name from bag_extraction_his b,app_type t  where t.app_no=b.app_type  and b.is_back = '1'  ";
     if (!StringUtil.isBlank(startDate)) {
      sql+="and create_time>=? ";
      paramsList.add(startDate);
    }
     if (!StringUtil.isBlank(endDate)) {
      sql+="and create_time<=? ";
      paramsList.add(endDate);
    }
     if (!StringUtil.isBlank(mobileNo)) {
       sql+="and b.mobile_no=? ";
       paramsList.add(mobileNo);
     }
     if (!StringUtil.isBlank(accountName)) {
       sql+="and b.account_name like ? ";
       paramsList.add("%"+accountName+"%");
     } 
   
     if(!StringUtil.isBlank(bankRemark)&&!bankRemark.equals("-1")){
    	 if(bankRemark.equals("0")){
    		 sql +=" and (back_status in ('0','2') or back_status is null) ";
    	 }else{
	    	 sql +=" and back_status = ? ";
	    	 paramsList.add(bankRemark);
    	 }
     }
     
     sql+=" order by create_time desc";
     
     if (paramsList.size()>0) {
       return bagDao.find(sql, paramsList.toArray(), pageRequest);
    }else {
      return bagDao.find(sql, null, pageRequest);
    }
	  } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	     return null;
  }
  
  
  
  /**
   * 
   * 功能：需手工冲正的钱包提现统计
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public Map<String, Object> countBagReverSal(Map<String, String> params) {
	  try {
	  String startDate=params.get("startDate");
     String endDate=params.get("endDate");
     String mobileNo=params.get("mobileNo");
     String accountName=params.get("accountName");
     String bankRemark = params.get("bankRemark");
     
     List<Object> paramsList=new ArrayList<Object>();
     String sql = "select count(1) totalCount  , sum(amount) totalAmount from bag_extraction_his b,app_type t  where t.app_no=b.app_type and  is_back = '1'  ";
     String resSuccess = "select count(1) count from bag_extraction_his b,app_type t  where t.app_no=b.app_type and  is_back = '1' and  back_status = '1'  ";
     String resFail = "select count(1) count from bag_extraction_his b,app_type t  where t.app_no=b.app_type and   is_back = '1' and (back_status in ('0','2') or back_status is null)  ";
     StringBuilder sb = new StringBuilder();
     if (!StringUtil.isBlank(startDate)) {
    	 sb.append("and create_time>=? ");
    	 paramsList.add(startDate);
    }
     if (!StringUtil.isBlank(endDate)) {
    	 sb.append("and create_time <=? ");
    	 paramsList.add(endDate);
    }
     if (!StringUtil.isBlank(mobileNo)) {
    	 sb.append("and b.mobile_no=? ");
    	 paramsList.add(mobileNo);
     }
     if (!StringUtil.isBlank(accountName)) {
    	 sb.append("and b.account_name like ? ");
    	 paramsList.add("%"+accountName+"%");
     } 

     
    
     if(!StringUtil.isBlank(bankRemark)&&!bankRemark.equals("-1")){
    	 if(bankRemark.equals("0")){
    		 sb.append(" and (back_status in ('0','2') or back_status is null )");
    	 }else{
    		 sb.append(" and back_status = ? ");
	    	 paramsList.add(bankRemark);
    	 }
     }
     resSuccess = resSuccess + sb.toString();
     resFail = resFail + sb.toString();
     sql = sql + sb.toString();
     
     Map<String, Object> totalSuccess = bagDao.findFirst(resSuccess ,paramsList.toArray());
     Map<String, Object> totalFail = bagDao.findFirst(resFail,paramsList.toArray());
     Map<String, Object> total = bagDao.findFirst(sql,paramsList.toArray());
    
     Map<String, Object> param = new HashMap<String, Object>();
     param.put("totalCount", total.get("totalCount"));
     param.put("totalAmount", total.get("totalAmount")==null?"0":total.get("totalAmount"));
     param.put("totalSuccess", totalSuccess.get("count"));
     param.put("totalFail", totalFail.get("count"));
     return param;
	  } catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		  return null;
  }
  
  
  /**
   * 
   * 功能：钱包交易查询
   *
   * @param params
   * @param pageRequest
   * @return
   */
  public Page<Map<String, Object>> queryTransaction(Map<String, String> params,
      PageRequest pageRequest) {
     
     String mobileNo=params.get("mobileNo");
     String transType=params.get("transType");
     String status=params.get("status");
     String startDate=params.get("startDate");
     String endDate=params.get("endDate");
     
     List<Object> paramsList=new ArrayList<Object>();
     String sql = "select b.*,t.app_name from bag_trans_order b,app_type t where t.app_no=b.app_type  ";
     
     if (!StringUtil.isBlank(mobileNo)) {
       sql+="and user_name=? ";
       paramsList.add(mobileNo);
     }
     if (!StringUtil.isBlank(transType)) {
       sql+="and trans_type=? ";
       paramsList.add(transType);
     }
     if (!StringUtil.isBlank(status)) {
       sql+="and result_status=? ";
       paramsList.add(status);
     }
     if (!StringUtil.isBlank(startDate)) {
       sql+="and create_time>=? ";
       paramsList.add(startDate);
     }
      if (!StringUtil.isBlank(endDate)) {
       sql+="and create_time<=? ";
       paramsList.add(endDate);
     }
     
     sql+=" order by create_time desc";
     if (paramsList.size()>0) {
       return bagDao.find(sql, paramsList.toArray(), pageRequest);
    }else {
      return bagDao.find(sql, null, pageRequest);
    }
  }
  
  /**
   * 统计钱包余额
   * @param id
   * @return
   */
  public Map<String, Object> queryTotalPurseBalance() {
    String sql="SELECT count(1) totalNum,sum(balance) todayTotalBalance,"
        + " sum(balance1) hisTotalBalance from bag_login ";
    Map<String, Object> map= bagDao.findFirst(sql);
    return map;
  }
  
  /**
   * 统计出入账总金额
   * @param id
   * @return
   */
  public List<Map<String, Object>> queryTotalPurseTransaction(Map<String, String> params) {
    String startDate=params.get("startDate");
    String endDate=params.get("endDate");
    
    List<Object> paramsList=new ArrayList<Object>();
    String sql = "select trans_type,count(1) totalNum,sum(amount) totalAmount "
        + " from bag_trans_order where result_status='1'  ";
    
    if (!StringUtil.isBlank(startDate)) {
      sql+="and str_to_date(create_time,'%Y-%m-%d')>=? ";
      paramsList.add(startDate);
    }
     if (!StringUtil.isBlank(endDate)) {
      sql+="and str_to_date(create_time,'%Y-%m-%d')<=? ";
      paramsList.add(endDate);
    }
     
     sql+=" group by trans_type";
     
     if (paramsList.size()>0) {
       return bagDao.find(sql, paramsList.toArray());
    }else {
       return bagDao.find(sql);
    }
    
  }
  
  
  public Page<Map<String, Object>> queryPurseMerchantRule(Map<String, String> params,
      PageRequest pageRequest) {
     
     String merchantType=params.get("merchantType");
     String ruleType=params.get("ruleType");
     String merchantNo=params.get("merchantNo");
     String merchantName=params.get("merchantName");
     
     List<Object> paramsList=new ArrayList<Object>();
     String sql = "select * from purse_merchant_rule where 1=1  ";
     
     if (!StringUtil.isBlank(merchantType)) {
       sql+="and merchant_type=? ";
       paramsList.add(merchantType);
     }
     if (!StringUtil.isBlank(ruleType)) {
       sql+="and rule_type=? ";
       paramsList.add(ruleType);
     }
     if (!StringUtil.isBlank(merchantNo)) {
       sql+="and merchant_no=? ";
       paramsList.add(merchantNo);
     }
     if (!StringUtil.isBlank(merchantName)) {
       sql+="and merchant_name like ? ";
       paramsList.add("%"+merchantName+"%");
     } 
     
     sql+=" order by create_time desc";
     return dao.find(sql, paramsList.toArray(), pageRequest);
  }
  
  
  /**
   * 
   * 功能：根据商户编号查询商户
   *
   * @param merchantNo
   * @return
   */
  public Map<String, Object> queryMerchant(String merchantNo) {
    String sql="select * from pos_merchant where merchant_no ='"+merchantNo+"'";
    return dao.findFirst(sql);
  }
  
  /**
   * 
   * 功能：根据代理商编号查询代理商
   *
   * @param agentNo
   * @return
   */
  public Map<String, Object> queryAgent(String agentNo) {
    String sql="select * from agent_info where agent_no ="+agentNo;
    return dao.findFirst(sql);
  }
  
  /**
   * 
   * 功能：新增钱包商户规则
   *
   * @param params
   * @return
   */
  public int addPurseMerchantRule(Map<String, String> params) {
    String sql="insert into purse_merchant_rule(id,merchant_no,merchant_name,merchant_type,"
        + " rule_type,create_time,create_person_id,create_person_name) "
        + " values(NULL,?,?,?,?,NOW(),?,?)";
    List<Object> list=new ArrayList<Object>();
    list.add(params.get("merchantNo"));
    list.add(params.get("merchantName"));
    list.add(params.get("merchantType"));
    list.add(params.get("ruleType"));
    list.add(params.get("createPersonId"));
    list.add(params.get("createPersonname"));
    int ret=0;
    try {
      ret= dao.update(sql, list.toArray());
    } catch (SQLException e) {
      e.printStackTrace();
      log.warn("新增钱包商户规则 失败:"+e.getMessage());
    }
    return ret;
  }
  

  
  /**
   * 
   * 功能：根据ID查询钱包商户规则
   *
   * @param id
   * @return
   */
  public Map<String, Object> queryPurseMerchantRuleById(String id) {
    String sql="select * from purse_merchant_rule where id ="+id;
    return dao.findFirst(sql);
  }
  
  
  public Map<String, Object> queryPurseMerchantRule(String merchantNo,String merchantType) {
    String sql="select * from purse_merchant_rule where merchant_no = "
               +merchantNo+" and merchant_type="+merchantType;
    return dao.findFirst(sql);
  }
  
  public Map<String, Object> queryMerchantByNo(String merchantNo) {
    String sql="select * from pos_merchant where merchant_no ='"
               +merchantNo+"'";
    return dao.findFirst(sql);
  }
  
  /**
   * 
   * 功能：删除钱包商户规则
   *
   * @param id
   * @return
   */
  public int delPurseMerchantRule(String id) {
    String sql="delete from purse_merchant_rule where id ="+id;
    int ret=0;
    try {
      ret= dao.update(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }
  
  
  /**
   * 
   * 功能：增加钱包商户开关参数
   *
   * @return
   */
  public int insertPurseMerchantOffOn(String offOn) {
    String sql="insert into sys_config(ID,PARAM_KEY,PARAM_VALUE,comments) "
        + " values(NULL,'purse_check_boolean',?,'钱包入账商户规则开关，fasle：关闭规则；true:打开规则')";
    int ret=0;
    try {
      ret= dao.update(sql,offOn);
    } catch (SQLException e) {
      e.printStackTrace();
      log.warn("新增钱包商户规则开关 失败:"+e.getMessage());
    }
    return ret;
  }
  
  /**
   * 
   * 功能：修改钱包商户规则开关
   *
   * @param offOn
   * @return
   */
  public int updatePurseMerchantOffOn(String offOn) {
    String sql="update sys_config set PARAM_VALUE=? where PARAM_KEY='purse_check_boolean' ";
    int ret=0;
    try {
      ret= dao.update(sql,offOn);
    } catch (SQLException e) {
      e.printStackTrace();
      log.warn("修改钱包商户规则开关 失败:"+e.getMessage());
    }
    return ret;
  }
  
  /**
   * 
   * 功能：新增钱包调账记录
   *
   * @param params
   * @return
   */
  public int addTransferAcc(Map<String, String> params) {
    String sql="insert into bag_check_account" +
    		"(mobile_no,amount,check_status,trans_status,adjust_type,create_time,check_msg,app_type,real_name)" +
    		" values(?,?,?,?,?,NOW(),?,?,?)";
    List<Object> list=new ArrayList<Object>();
    list.add(params.get("mobileNo"));
    list.add(params.get("amount"));
    list.add(params.get("checkStatus"));
    list.add(params.get("transStatus"));
    list.add(params.get("adjustType"));
    list.add(params.get("checkMsg"));
    list.add(params.get("appType"));
    list.add(params.get("realName"));
    int ret=0;
    try {
      ret= dao.update(sql, list.toArray());
    } catch (SQLException e) {
      e.printStackTrace();
      log.warn("新增调账记录 失败:"+e.getMessage());
    }
    return ret;
  }
  
  /**
   * 查询用户某审核状态的调账记录
   * @param mobileNo
   * @return
   */
  public int  queryCheckTrans(String mobileNo,String checkStatus) {
	    int ret=0;
	    String sql="select count(*) as selcount from bag_check_account where mobile_no =? and check_status=?" ;
	    Map<String, Object> m1 = dao.findFirst(sql, new Object[]{mobileNo,checkStatus}) ;
	    ret=Integer.valueOf(String.valueOf(m1.get("selcount")));
	    return ret;
  }
  
  
  
  public Page<Map<String, Object>> getBagCheckList(Map<String, String> params,
			PageRequest pageRequest) {
		  String mobileNo=params.get("mobileNo");
		  String checkStatus=params.get("checkStatus");
		  String transStatus=params.get("transStatus");
		  String checkPerson=params.get("checkPerson");
		  String startDate=params.get("startDate");
		  String endDate=params.get("endDate");
		  String checkStartDate=params.get("checkStartDate");
		  String checkEndDate=params.get("checkEndDate");
		  
		  List<Object> paramsList=new ArrayList<Object>();
		  String sql = "select b.* from bag_check_account b  where 1=1  ";
		  
		  if (!StringUtil.isBlank(mobileNo)) {
		    sql+="and b.mobile_no=? ";
		    paramsList.add(mobileNo);
		  }
		  
		  if (!StringUtil.isBlank(checkStatus)) {
		    sql+="and b.check_status = ? ";
		    paramsList.add(checkStatus);
		  } 
		  if (!StringUtil.isBlank(transStatus)) {
			    sql+="and b.trans_status = ? ";
			    paramsList.add(transStatus);
		  }
	
		  if (!StringUtil.isBlank(checkPerson)) {
		    sql+="and b.check_user like ? ";
		    paramsList.add("%"+checkPerson+"%");
		  } 

		  if (!StringUtil.isBlank(startDate)) {
		    sql += "and b.create_time(create_time,'%Y-%m-%d')>=? ";
		    paramsList.add(startDate);
		  }
		  if (!StringUtil.isBlank(endDate)) {
		    sql += "and b.create_time(create_time,'%Y-%m-%d')<=? ";
		    paramsList.add(endDate);
		  }
		  if (!StringUtil.isBlank(checkStartDate)) {
		    sql += "and b.check_time(check_time,'%Y-%m-%d')>=? ";
		    paramsList.add(checkStartDate);
		  }
		  if (!StringUtil.isBlank(checkEndDate)) {
		    sql += "and b.check_time(check_time,'%Y-%m-%d')<=? ";
		    paramsList.add(checkEndDate);
		  }
		  
		  sql+=" order by create_time desc";
		  
		  if (paramsList.size()>0) {
		    return dao.find(sql, paramsList.toArray(), pageRequest);
		 }else {
		   return dao.find(sql, null, pageRequest);
		 }
	  
	}
  
	/**
	 * 数据库中 查询钱包提现详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getCheckAccountDetailById(long id) throws SQLException{
		String sql="select * from bag_check_account where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}
	
    /**
     * 更新调账审核状态
     * @param id
     * @return
     * @throws SQLException
     */
	public int bagCheckRecharges(String id,String check_person) throws SQLException {
		Date date=new Date();
       SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String sys_date= dft.format(date);
		String sql = "update bag_check_account set check_status=?,check_user=?,check_time=? where id=?";
		Object[] ps = {1,check_person,sys_date,id};
		int n = dao.update(sql,ps);
		return n;
	}
  
	
	public void bagCheckModify(Map<String, String> params,String check_person) throws SQLException {
		   try {
				String check_remark=(String)params.get("check_remark").trim();
				String id=(String)params.get("id");
	            Date date=new Date();
	            SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            String sys_date= dft.format(date);
				
				String sql = "update bag_check_account set check_fail_msg=?,check_user=?,check_time=?," +
						"check_status=? where id=?";
				Object[] ps = {check_remark,check_person,sys_date,3,id};
				dao.update(sql,ps);
		   } catch (Exception e) {
			// TODO: handle exception
		   }
			
		}
	
	/**
	 * 更新调账状态
	 * @param id
	 * @param transStatus
	 * @return
	 * @throws SQLException
	 */
	public int checkAccountSub(String id,String transStatus) throws SQLException {
		Date date=new Date();
       SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String sys_date= dft.format(date);
		String sql = "update bag_check_account set trans_status=?,trans_time=? where id=?";
		Object[] ps = {transStatus,sys_date,id};
		int n = dao.update(sql,ps);
		return n;
	}
	
	/**
	 * 
	 * 功能：钱包提升额度审核查询
	 *
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> bagTzeroAmountLimitQuery(Map<String, String> params,
      PageRequest pageRequest) {
	  String startDate=params.get("startDate");
	  String endDate=params.get("endDate");
	  String mobileNo=params.get("mobileNo");
	  String merchantName=params.get("merchantName");
	  String merchantNo=params.get("merchantNo");
	  String phase=params.get("phase");
	  String chechStatus=params.get("chechStatus");
	   List<Object> paramsList=new ArrayList<Object>();
	   String sql = "select * from basic_data where 1=1 ";
	   if (!StringUtil.isBlank(startDate)) {
	    sql+="and create_time>=? ";
	    paramsList.add(startDate);
	  }
	   if (!StringUtil.isBlank(endDate)) {
	    sql+="and create_time<=? ";
	    paramsList.add(endDate);
	  }
	   if (!StringUtil.isBlank(mobileNo)) {
	     sql+="and mobile_no=? ";
	     paramsList.add(mobileNo);
	   }
	   if (!StringUtil.isBlank(chechStatus)) {
	     sql+="and check_status=? ";
	     paramsList.add(chechStatus);
	   } 
	   if (!StringUtil.isBlank(merchantName)) {
	     sql+="and merchant_name like ? ";
	     paramsList.add("%"+merchantName+"%");
	   }
	   if (!StringUtil.isBlank(merchantNo)) {
	     sql+="and merchant_no=? ";
	     paramsList.add(merchantNo);
	   }
	   if (!StringUtil.isBlank(phase)) {
		   //基础资料
		   if("0".equals(phase)){
			   sql+="and check_status in (0,2) ";
		   }
		   //丰富资料
		   if("1".equals(phase)){
			   sql+="and check_status=1 and is_rich_check=1 ";
		   }
		   //审核完成
		   if("2".equals(phase)){
			   sql+="and check_status=1 and is_rich_check=0 ";
		   }
	   }
	   
	   sql+=" order by modify_time";
	   log.info("----sql---"+sql);
	   if (paramsList.size()>0) {
	     return bagDao.find(sql, paramsList.toArray(), pageRequest);
	  }else {
	    return bagDao.find(sql, null, pageRequest);
	  }
	   
    }
	
	/**
	 * 
	 * 功能：T+0额度修改查询
	 *
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> bagTzeroAmountModifyQuery(Map<String, String> params,
      PageRequest pageRequest) {
	  String chooseObj=params.get("chooseObj");
	  String agentNo=params.get("agentNo");
	  String agentName=params.get("agentName");
	  String merchantNo=params.get("merchantNo");
	  String merchantName=params.get("merchantName");
	  String merchantSaleName=params.get("merchantSaleName");
	  List<Object> paramsList=new ArrayList<Object>();
	  if (!StringUtil.isBlank(chooseObj)) {
		  String sql = null;
		  if("agent".equals(chooseObj)){
			  sql = "select * from (select m.* from bag_merchant m where 1=1 ";
			  if (!StringUtil.isBlank(agentNo)) {
				  sql+="and m.agent_no=? ";
				  paramsList.add(agentNo);
			  }
			  if (!StringUtil.isBlank(agentName)) {
				  sql+="and m.one_level_agent_name like ?";
				  paramsList.add("%"+agentName+"%");
			  }
			  sql+=" group by m.agent_no) tmp";
		  }else if("merchant".equals(chooseObj)){
			  sql ="select m.* from bag_merchant m,bag_login b where b.is_tzero=1 and m.mobile_no=b.mobile_no and m.app_type=b.app_type ";
			  if (!StringUtil.isBlank(merchantNo)) {
				  sql+="and m.merchant_no=? ";
				  paramsList.add(merchantNo);
			  }
			  if (!StringUtil.isBlank(merchantName)) {
				  sql+="and m.merchant_name like ?";
				  paramsList.add("%"+merchantName+"%");
			  }
			  if (!StringUtil.isBlank(merchantSaleName)) {
				  sql+="and m.sale_name like ?";
				  paramsList.add("%"+merchantSaleName+"%");
			  }
		  }
		  if(sql!=null){
			  if (paramsList.size()>0) {
				  return bagDao.find(sql, paramsList.toArray(), pageRequest);
			  }else {
				  return bagDao.find(sql, null, pageRequest);
			  }
		  }
	  }
	   return null;
    }
	
	public List<Map<String, Object>> selectAgentOrSale(String type){
		if("agent".equals(type)){
			String sql ="select one_level_agent_name from bag_merchant group by one_level_agent_name";
			return bagDao.find(sql);
		}else if("sale".equals(type)){
			String sql ="select sale_name from bag_merchant group by sale_name";
			return bagDao.find(sql);
		}else{
			return null;
		}
	}
	
	public Map<String, Object> selectBagMerchantById(String id){
		String sql = "select * from bag_merchant where id=?";
		return bagDao.findFirst(sql, id);
	}
	
	public Map<String,Object> selectAgentDataAmount(String agentNo){
		Map<String,Object> agentAmountMap = null;
		String sql = "select * from agent_data_amount where agent_no=?";
		agentAmountMap = bagDao.findFirst(sql, agentNo);
		if(agentAmountMap==null){
			sql = "select * from agent_data_amount where agent_no=-1";
			agentAmountMap = bagDao.findFirst(sql);
		}else if(agentAmountMap.isEmpty()){
			sql = "select * from agent_data_amount where agent_no=-1";
			agentAmountMap = bagDao.findFirst(sql);
		}
		return agentAmountMap;
	}
	
	public Map<String,Object> selectAgentDataAmountByAgentNo(String agentNo){
		String sql = "select * from agent_data_amount where agent_no=?";
		return bagDao.findFirst(sql, agentNo);
	}
	
	public Boolean agentDataAmountModify(Map<String, String> params,String operater){
		List<Object> paramsList = new ArrayList<Object>();
		Connection conn = null;
		try {
			String agentNo = params.get("agentNo").toString();
			String type = params.get("type");
			String merchantChange = params.get("merchantChange");//0现存商户   1未来商户
			log.info("---agentNo="+agentNo+"--type="+type+"--merchantChange="+merchantChange);
			conn = bagDao.getConnection();
			conn.setAutoCommit(false);
			BigDecimal day_extraction_max_amount = new BigDecimal("0.00");
			if("agent".equals(type) && "1".equals(merchantChange)){
				log.info("------代理商未来商户修改------");
				BigDecimal basicData = new BigDecimal(params.get("basicData"));
				BigDecimal richData0 = new BigDecimal(params.get("richData0"));
				BigDecimal richData1 = new BigDecimal(params.get("richData1"));
				BigDecimal richData2 = new BigDecimal(params.get("richData2"));
				BigDecimal richData3 = new BigDecimal(params.get("richData3"));
				BigDecimal richData4 = new BigDecimal(params.get("richData4"));
				BigDecimal richData5 = new BigDecimal(params.get("richData5"));
				BigDecimal richData6 = new BigDecimal(params.get("richData6"));
				BigDecimal richData7 = new BigDecimal(params.get("richData7"));
				BigDecimal richData8 = new BigDecimal(params.get("richData8"));
				paramsList.add(basicData);
				paramsList.add(richData0);
				paramsList.add(richData1);
				paramsList.add(richData2);
				paramsList.add(richData3);
				paramsList.add(richData4);
				paramsList.add(richData5);
				paramsList.add(richData6);
				paramsList.add(richData7);
				paramsList.add(richData8);
				paramsList.add(new Date());
				paramsList.add(operater);
				paramsList.add(agentNo);
				Map<String, Object> agentAmountMap = selectAgentDataAmountByAgentNo(agentNo);
				if(agentAmountMap!=null && !agentAmountMap.isEmpty()){
					log.info("------代理商未来商户修改update------");
					String sql = "update agent_data_amount set basic_data_amount=?,rich_data0_amount=?,"
							+ "rich_data1_amount=?,rich_data2_amount=?,rich_data3_amount=?,rich_data4_amount=?,"
							+ "rich_data5_amount=?,rich_data6_amount=?,rich_data7_amount=?,rich_data8_amount=?,"
							+ "create_time=?,operater=? where agent_no=?";
					bagDao.updateByTranscation(sql, paramsList.toArray(),conn);
				}else{
					log.info("------代理商未来商户修改insert------");
					String sql = "insert into agent_data_amount(basic_data_amount,rich_data0_amount,"
							+ "rich_data1_amount,rich_data2_amount,rich_data3_amount,rich_data4_amount,"
							+ "rich_data5_amount,rich_data6_amount,rich_data7_amount,rich_data8_amount,"
							+ "create_time,operater,agent_no) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
					bagDao.updateByTranscation(sql, paramsList.toArray(),conn);
				}
				//day_extraction_max_amount = basicData.add(richData0).add(richData1).add(richData2).add(richData3).add(richData4).add(richData5).add(richData6).add(richData7).add(richData8);
			}else if("agent".equals(type) && "0".equals(merchantChange)){
				log.info("------代理商现存商户修改------");
				day_extraction_max_amount = new BigDecimal(params.get("day_extraction_max_amount"));
				log.info("-----额度为----"+day_extraction_max_amount);
				String sql2 = "update bag_login b,bag_merchant m set b.day_extraction_max_amount=? where m.agent_no=? and b.mobile_no=m.mobile_no and b.app_type=m.app_type and b.is_tzero=1";
				dao.updateByTranscation(sql2, new Object[]{day_extraction_max_amount,agentNo}, conn);
			}else{
				log.info("------商户修改------");
				day_extraction_max_amount = new BigDecimal(params.get("day_extraction_max_amount"));
				String mobileNo = params.get("mobileNo");
				String appType = params.get("appType");
				log.info("-----商户额度为----"+day_extraction_max_amount);
				String sql2 = "update bag_login set day_extraction_max_amount=? where mobile_no=? and app_type=?";
				dao.updateByTranscation(sql2, new Object[]{day_extraction_max_amount,mobileNo,appType}, conn);
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(conn!=null){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return false;
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public Page<Map<String, Object>> redBagQuery(Map<String, String> params,
      PageRequest pageRequest) {
	  String startDate=params.get("createTimeBegin");
	  String endDate=params.get("createTimeEnd");
	  String mobileNo=params.get("mobileNo");
	  String realName=params.get("realName");
	  String amountBegin=params.get("amountBegin");
	  String amountEnd=params.get("amountEnd");
	   List<Object> paramsList=new ArrayList<Object>();
	   String sql = "select distinct r.*,a.app_name from red_bag r,app_type a where r.app_type=a.app_no ";
	   if (!StringUtil.isBlank(startDate)) {
	    sql+="and r.create_time>=? ";
	    paramsList.add(startDate);
	  }
	   if (!StringUtil.isBlank(endDate)) {
	    sql+="and r.create_time<=? ";
	    paramsList.add(endDate);
	  }
	   if (!StringUtil.isBlank(amountBegin)) {
		    sql+="and r.red_balance>=? ";
		    paramsList.add(amountBegin);
		  }
		   if (!StringUtil.isBlank(amountEnd)) {
		    sql+="and r.red_balance<=? ";
		    paramsList.add(amountEnd);
		  }
	   if (!StringUtil.isBlank(mobileNo)) {
	     sql+="and r.mobile_no=? ";
	     paramsList.add(mobileNo);
	   }
	   if (!StringUtil.isBlank(realName)) {
	     sql+="and r.real_name=? ";
	     paramsList.add(realName);
	   } 
	   
	   sql+=" order by r.create_time desc";
	   log.info("----sql---"+sql);
	   if (paramsList.size()>0) {
	     return bagDao.find(sql, paramsList.toArray(), pageRequest);
	  }else {
	    return bagDao.find(sql, null, pageRequest);
	  }
	   
    }
	
	//钱包用户统计
   public Map<String, Object> countRedBagInfo(Map<String, String> params){
	   String startDate=params.get("createTimeBegin");
		  String endDate=params.get("createTimeEnd");
		  String mobileNo=params.get("mobileNo");
		  String realName=params.get("realName");
		  String amountBegin=params.get("amountBegin");
		  String amountEnd=params.get("amountEnd");
		   List<Object> paramsList=new ArrayList<Object>();
		   String sql = "select sum(red_balance) total_amount,count(1) total_user from red_bag where red_balance<>0  ";
		   if (!StringUtil.isBlank(startDate)) {
		    sql+="and create_time>=? ";
		    paramsList.add(startDate);
		  }
		   if (!StringUtil.isBlank(endDate)) {
		    sql+="and create_time<=? ";
		    paramsList.add(endDate);
		  }
		   if (!StringUtil.isBlank(amountBegin)) {
			    sql+="and red_balance>=? ";
			    paramsList.add(amountBegin);
			  }
			   if (!StringUtil.isBlank(amountEnd)) {
			    sql+="and red_balance<=? ";
			    paramsList.add(amountEnd);
			  }
		   if (!StringUtil.isBlank(mobileNo)) {
		     sql+="and mobile_no=? ";
		     paramsList.add(mobileNo);
		   }
		   if (!StringUtil.isBlank(realName)) {
		     sql+="and real_name=? ";
		     paramsList.add(realName);
		   } 
	   
		   sql+=" order by create_time desc";
		   log.info("----sql---"+sql);
		   if (paramsList.size()>0) {
		     return bagDao.findFirst(sql, paramsList.toArray());
		  }else {
		    return bagDao.findFirst(sql, null);
		  }
   }
   
   //红包换购管理
   public Page<Map<String, Object>> redBuyMachines(Map<String, String> params,
      PageRequest pageRequest) {
	  String agentSureTimeBegin=params.get("agentSureTimeBegin");
	  String agentSureTimeEnd=params.get("agentSureTimeEnd");
	  String mobileNo=params.get("mobileNo");
	  String realName=params.get("realName");
	  String machines = params.get("machines");
	  String agentNo = params.get("agentNo");
	  String status = params.get("status");
	  String province = params.get("province");
	  String city = params.get("city");
	   List<Object> paramsList=new ArrayList<Object>();
	   String sql = "select distinct r.*,a.app_name from red_bag r,app_type a where r.app_type=a.app_no and status is not null ";
	   if (!StringUtil.isBlank(agentSureTimeBegin)) {
	    sql+="and r.agent_sure_time>=? ";
	    paramsList.add(agentSureTimeBegin);
	  }
	   if (!StringUtil.isBlank(agentSureTimeEnd)) {
	    sql+="and r.agent_sure_time<=? ";
	    paramsList.add(agentSureTimeEnd);
	  }
	   if (!StringUtil.isBlank(mobileNo)) {
	     sql+="and r.mobile_no=? ";
	     paramsList.add(mobileNo);
	   }
	   if (!StringUtil.isBlank(realName)) {
	     sql+="and r.real_name=? ";
	     paramsList.add(realName);
	   } 
	   if(!StringUtil.isBlank(machines)){
		   sql+="and (r.machines1_name=? or r.machines2_name=?) ";
		   paramsList.add(machines);
		   paramsList.add(machines);
	   }
	   if (!StringUtil.isBlank(agentNo)) {
	     sql+="and r.agent_no=? ";
	     paramsList.add(agentNo);
	   }
	   if (!StringUtil.isBlank(status)) {
	     sql+="and r.status=? ";
	     paramsList.add(status);
	   }
	   if (!StringUtil.isBlank(province)) {
	     sql+="and (r.address1 like ? or r.address2 like ? ) ";
	     paramsList.add("%"+province+city+"%");
	     paramsList.add("%"+province+city+"%");
	   }
	   
	   sql+=" order by r.id desc";
	   log.info("----sql---"+sql);
	   if (paramsList.size()>0) {
	     return bagDao.find(sql, paramsList.toArray(), pageRequest);
	  }else {
	    return bagDao.find(sql, null, pageRequest);
	  }
	   
    }
   
 //红包换购管理导出查询
   public List<Map<String,Object>> redBuyMachinesExport(Map<String, String> params) {
	  String agentSureTimeBegin=params.get("agentSureTimeBegin");
	  String agentSureTimeEnd=params.get("agentSureTimeEnd");
	  String mobileNo=params.get("mobileNo");
	  String realName=params.get("realName");
	  String machines = params.get("machines");
	  String agentNo = params.get("agentNo");
	  String status = params.get("status");
	  String province = params.get("province");
	  String city = params.get("city");
	   List<Object> paramsList=new ArrayList<Object>();
	   String sql = "select distinct r.*,a.app_name from red_bag r,app_type a where r.app_type=a.app_no and status is not null ";
	   if (!StringUtil.isBlank(agentSureTimeBegin)) {
	    sql+="and r.agent_sure_time>=? ";
	    paramsList.add(agentSureTimeBegin);
	  }
	   if (!StringUtil.isBlank(agentSureTimeEnd)) {
	    sql+="and r.agent_sure_time<=? ";
	    paramsList.add(agentSureTimeEnd);
	  }
	   if (!StringUtil.isBlank(mobileNo)) {
	     sql+="and r.mobile_no=? ";
	     paramsList.add(mobileNo);
	   }
	   if (!StringUtil.isBlank(realName)) {
	     sql+="and r.real_name=? ";
	     paramsList.add(realName);
	   } 
	   if(!StringUtil.isBlank(machines)){
		   sql+="and (r.machines1_name=? or r.machines2_name=?) ";
		   paramsList.add(machines);
		   paramsList.add(machines);
	   }
	   if (!StringUtil.isBlank(agentNo)) {
	     sql+="and r.agent_no=? ";
	     paramsList.add(agentNo);
	   }
	   if (!StringUtil.isBlank(status)) {
	     sql+="and r.status=? ";
	     paramsList.add(status);
	   }
	   if (!StringUtil.isBlank(province)) {
	     sql+="and (r.address1 like ? or r.address2 like ? ) ";
	     paramsList.add("%"+province+city+"%");
	     paramsList.add("%"+province+city+"%");
	   }
	   
	   sql+=" order by r.id desc";
	   log.info("----sql---"+sql);
	   if (paramsList.size()>0) {
	     return bagDao.find(sql, paramsList.toArray());
	  }else {
	    return bagDao.find(sql, null);
	  }
	   
    }
	
	/**
	    * 钱包提升额度审核基本资料查询详情
	    * @param id
	    * @return
	    */
	   public Map<String, Object> bagTzeroAmountLimitDetail(String id) {
	     String sql="select * from basic_data where id=?";
	     Map<String,Object> map= bagDao.findFirst(sql, id);
	     return map;
	   }
	   
	   /**
	    * 钱包提升额度审核基础资料审核失败记录
	    * @param mobileNo
	    * @param appType
	    * @return
	    */
	   public List<Map<String, Object>> bagTzeroCheckFailHis(String mobileNo,String appType) {
	     String sql="select * from basic_data_fail_his where mobile_no=? and app_type=?";
	     return bagDao.find(sql, new Object[]{mobileNo,appType});
	   }
	   
	   /**
	    * 钱包提升额度审核丰富资料审核失败记录
	    * @param mobileNo
	    * @param appType
	    * @return
	    */
	   public List<Map<String, Object>> bagTzeroRichCheckFailHis(String mobileNo,String appType) {
	     String sql="select * from rich_data_fail_his where mobile_no=? and app_type=?";
	     return bagDao.find(sql, new Object[]{mobileNo,appType});
	   }
	   
	   /**
	    * 钱包提升额度丰富资料审核查询详情
	    * @param mobileNo
	    * @param appType
	    * @return
	    */
	   public List<Map<String, Object>> bagTzeroAmountLimitRichDetail(String mobileNo,String appType) {
	     String sql="select * from rich_data where mobile_no=? and app_type=?";
	     List<Map<String, Object>> list = bagDao.find(sql, new Object[]{mobileNo,appType});
	     return list;
	   }
	   
	   /**
	    * 审核后更改审核结果
	    * @param id
	    * @param checker
	    * @param checkStatus
	    * @param checkOpinion
	    * @return
	 * @throws SQLException 
	    */
	   public void bagTzeroCheckResult(String id,String richDataType,String checker,String checkStatus,String checkOpinion,String richOpinion,String basicOrRichData,String mobileNo,String appType,BigDecimal amount,String batch){
		   Connection conn = null;
		   try {
			   conn = bagDao.getConnection();
			   conn.setAutoCommit(false);
			   if("0".equals(basicOrRichData)){
				   String sql="update basic_data set checker=?,check_status=?,check_opinion=?,check_time=? where id=?";
			       bagDao.updateByTranscation(sql, new Object[]{checker,checkStatus,checkOpinion,new Date(),id},conn);
			       if("1".equals(checkStatus)){
			    	   String sql2 = "update bag_login set is_tzero='1' where mobile_no=? and app_type=?";
			    	   bagDao.updateByTranscation(sql2, new Object[]{mobileNo ,appType}, conn);
			       }else if("2".equals(checkStatus)){
			    	   String sql2 = "delete from rich_data where mobile_no=? and app_type=?";
			    	   bagDao.updateByTranscation(sql2, new Object[]{mobileNo ,appType}, conn);
			    	   String sql3 = "update bag_login set is_tzero='0' where mobile_no=? and app_type=?";
			    	   bagDao.updateByTranscation(sql3, new Object[]{mobileNo ,appType}, conn);
			    	   String sql4 = "insert into basic_data_fail_his(mobile_no,app_type,checker,check_opinion,check_time) values(?,?,?,?,?)";
			    	   bagDao.updateByTranscation(sql4, new Object[]{mobileNo ,appType,checker,checkOpinion,new Date()}, conn);
			       }
			   }else if("1".equals(basicOrRichData)){
				   String sql="update rich_data set checker=?,check_status=?,check_opinion=?,check_time=?,last_check_status=? where id=?";
			       bagDao.updateByTranscation(sql, new Object[]{checker,checkStatus,richOpinion,new Date(),checkStatus,id},conn);
			       if("2".equals(checkStatus)){
			    	   //丰富资料审核不通过，记录
			    	   String sql4 = "insert into rich_data_fail_his(mobile_no,app_type,data_type,checker,check_opinion,check_time,batch) values(?,?,?,?,?,?,?)";
			    	   bagDao.updateByTranscation(sql4, new Object[]{mobileNo,appType,richDataType,checker,richOpinion,new Date(),batch}, conn);
			       }
			       List<Map<String, Object>> list = bagTzeroAmountLimitRichDetail(mobileNo,appType);
			       //如果审核状态有一个是0（未审核），则notice=1，则需要丰富资料审核（ 0不需要   1需要）
			       int notice=0;
			       for(Map<String, Object> map:list){
			    	   if(!id.equals(map.get("id").toString())){
			    		   if("0".equals(map.get("check_status").toString())){
				    		   notice=1;
				    	   }
			    	   }
			       }
			       System.out.println("notice="+notice);
			       if(notice==0){
			    	   String sql2="update basic_data set is_rich_check='0' where mobile_no=? and app_type=?";
				       bagDao.updateByTranscation(sql2, new Object[]{mobileNo,appType},conn);
			       }else if(notice==1){	
			    	   String sql2="update basic_data set is_rich_check='1' where mobile_no=? and app_type=?";
				       bagDao.updateByTranscation(sql2, new Object[]{mobileNo,appType},conn);
			       }
			   }
			   String sql3 = "update bag_login set day_extraction_max_amount=?,tzero_withdraws_max_amount=? where mobile_no=? and app_type=?";
		       bagDao.updateByTranscation(sql3, new Object[]{amount,amount, mobileNo,appType},conn);
		       conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	   }
	   
	   
	   public Map<String,Object> selectBagLogin(String mobileNo,String appType){
		   String sql = "select * from bag_login where mobile_no=? and app_type=?";
		   return bagDao.findFirst(sql, new Object[]{mobileNo,appType});
	   }
	   
	   public Map<String,Object> selectBagMerchant(String mobileNo,String appType){
		   String sql = "select * from bag_merchant where mobile_no=? and app_type=?";
		   return bagDao.findFirst(sql, new Object[]{mobileNo,appType});
	   }
	   
	   public void addFreezeRecord(Map<String, String> params) throws SQLException{
		   String sql = "insert into bag_freeze (mobile_no,app_type,front_retention_money,amount,behind_retention_money,channel,operation,operater,msg,create_time,freeze_day) values (?,?,?,?,?,?,?,?,?,?,?)";
		   List<Object> paramList = new ArrayList<Object>();
		   paramList.add(params.get("mobileNo"));
		   paramList.add(params.get("appType"));
		   paramList.add(params.get("userAmount"));
		   paramList.add(params.get("amount"));
		   paramList.add(params.get("amount"));
		   paramList.add(2);
		   paramList.add(params.get("operation"));
		   paramList.add(params.get("operater"));
		   paramList.add(params.get("freezeReason"));
		   paramList.add(params.get("createTime"));
		   paramList.add(params.get("freezeDay"));
		   bagDao.update(sql,paramList.toArray());
		
	   }
	   
	   
	   public int freezeUserAmount(int operation,String mobileNo,String freezeTime,String appType,String amount){
		   String sql = ""; 
		  try {
		   if(operation == 0){
			   sql = "update bag_login set manual_retention_money =?,manual_arrive_time =  ? where mobile_no = ? and app_type=?";
			   return bagDao.update(sql,new Object[]{amount,freezeTime,mobileNo,appType});
		   }else{
			   sql = "update bag_login set manual_retention_money =(manual_retention_money - ?) where mobile_no = ? and app_type=?";
			   return bagDao.update(sql,new Object[]{amount,mobileNo,appType});
		   }
		
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	   }
	   
	   public Map<String, Object> selectFreezeInfo(String mobileNo ,String appType){
		   String sql = "select create_time ,operater,freeze_day from bag_freeze where mobile_no = ? and app_type = ? and channel = 2 order by id desc";
		   return bagDao.findFirst(sql,new Object[]{mobileNo,appType});
	   }
	   
	   public Map<String, Object> selectFreezeAmount(String mobileNo ,String appType){
		   String sql = "select retention_money , manual_retention_money from bag_login where mobile_no = ? and app_type = ? ";
		   return bagDao.findFirst(sql,new Object[]{mobileNo,appType}); 
	   }
	   
	   
	   
	   //钱包用户统计
	   public Map<String, Object> countBagUserInfo(){
		   
		   String sql = "select sum(balance) + sum(balance1) total_amount,sum(balance) total_today,sum(balance1) total_his from bag_login";
		   
		   return bagDao.findFirst(sql);
	   }

	public List<Map<String, Object>> getBagFreezes(String mobileNo, String appType) {
		String sql = "select create_time,operation,channel,operater,amount,freeze_day,msg from bag_freeze where mobile_no = ? and app_type = ? order by create_time desc limit 0, 3";
		return bagDao.find(sql, new Object[]{mobileNo, appType});
	}
}
