package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 商户审核人管理业务逻辑类
 * @author 王帅
 *
 */
@Service
public class MerchantCheckPersonService {
	@Resource
	private Dao dao;
	
	private static final Logger log = LoggerFactory.getLogger(MerchantCheckPersonService.class);
	
	/**
	 * 删除审核人信息
	 * @author 王帅
	 * @param id id编号
	 * @return 受影响的行数
	 * @throws SQLException 数据库异常
	 * @throws Exception
	 */
	public int removeMerchantCheckStatusById(int id)   throws SQLException,Exception {
		log.info("MerchantCheckPersonService removeMerchantCheckStatusById start ...");
		int removeCount = 0;
		if(id > 0){
			String sql = "delete from merchant_check_person  where id="+id;
			removeCount = dao.update(sql);
			if(removeCount>0){
				log.info("MerchantCheckPersonService  removeMerchantCheckStatusById SUCCESS");
			}
		}
		log.info("MerchantCheckPersonService removeMerchantCheckStatusById End");
		return removeCount;
	}
	
	/**
	 * 开启审核人信息
	 * @author 王帅
	 * @param id id编号
	 * @return 受影响的行数
	 * @throws SQLException 数据库异常
	 * @throws Exception
	 */
	public int openMerchantCheckStatusById(int id)  throws SQLException,Exception {
		log.info("MerchantCheckPersonService openMerchantCheckStatusById start ...");
		int openCount = 0;
		if(id > 0){
			String sql = "update merchant_check_person mp set mp.check_status=1 where mp.id="+id;
			openCount = dao.update(sql);
			if(openCount>0){
				log.info("MerchantCheckPersonService  openMerchantCheckStatusById SUCCESS");
			}
		}
		log.info("MerchantCheckPersonService openMerchantCheckStatusById End");
		return openCount;
	}
	
	/**
	 * 关闭审核人信息
	 * @author 王帅
	 * @param id id编号
	 * @return 受影响的行数
	 * @throws SQLException 数据库异常
	 * @throws Exception
	 */
	public int closeMerchantCheckStatusById(int id)  throws SQLException,Exception {
		log.info("MerchantCheckPersonService closeMerchantCheckStatusById start ...");
		int  closeCount = 0;
		if(id > 0){
			String sql = "update merchant_check_person mp set mp.check_status=2 where mp.id="+id;	
			closeCount = dao.update(sql);
			if(closeCount>0){
				log.info("MerchantCheckPersonService  closeMerchantCheckStatusById SUCCESS");
			}
		}
		
		log.info("MerchantCheckPersonService closeMerchantCheckStatusById End");
		return closeCount;
	}
	
	/**
	 * 保存修改审核人信息
	 * @author 王帅
	 * @param params 要修改的参数
	 * @return 受影响的行数
	 * @throws SQLException 数据库异常
	 * @throws Exception 未知异常
	 */
	public int modifyMerchantCheckInfoById(Map<String, String> params)  throws SQLException,Exception {
		log.info("MerchantCheckPersonService  modifyMerchantCheckInfoById start ...");
		int modifyCount = 0;
		if(params != null){
			List<String> list = new ArrayList<String>();
			String sql = "update merchant_check_person mp set mp.user_id=?,mp.agent_no=?,mp.pos_type=?,mp.check_type=? where mp.id=?";
			if(StringUtils.isNotEmpty(params.get("user_id"))){
				 String user_id = params.get("user_id");
				 list.add(user_id);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("agentNo")) && !"-1".equals(params.get("agentNo"))){
				 String agentNo = params.get("agentNo");
				 list.add(agentNo);
			 }else{
				 list.add(null);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("pos_type"))  && !"-1".equals(params.get("pos_type"))){
				 String pos_type = params.get("pos_type");
				 list.add(pos_type);
			 }else{
				 list.add(null);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("check_type"))){
				 String check_type = params.get("check_type");
				 list.add(check_type);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("id"))){
				 String id = params.get("id");
				 list.add(id);
				 modifyCount =dao.update(sql, list.toArray());
			 }
			 log.info("MerchantCheckPersonService  modifyMerchantCheckInfoById SUCCESS");
		}
		log.info("MerchantCheckPersonService  modifyMerchantCheckInfoById End");
		return  modifyCount;
	}
	
	/**
	 * 根据ID 获取审核信息
	 * @author 王帅
	 * @param id 审核信息管理ID编号
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getMerchantCheckInfo(int id){
		log.info("MerchantCheckPersonService getMerchantCheckInfo start ...");
		String sql = "select * from merchant_check_person mp where mp.id="+id;
		log.info("MerchantCheckPersonService getMerchantCheckInfo end");
		return  dao.findFirst(sql);
	}
	
	
	/**
	 *添加商户审核人信息 
	 * @author 王帅
	 * @param params 商户审核人信息
	 * @return 受影响的行数
	 * @throws SQLException 抛出SQLException Exception
	 */
	public int addMerchantCheckPersonInfo(Map<String, String> params) throws SQLException,Exception {
		log.info("MerchantCheckPersonService addMerchantCheckPersonInfo start ...");
		int addCount = 0;
		String sql = "insert into merchant_check_person(user_id,agent_no,pos_type,check_type,check_status,create_person) values(?,?,?,?,1,?)";
		List<String> list = new ArrayList<String>();
		if(null != params){
			if(StringUtils.isNotEmpty(params.get("user_id"))){
				 String user_id = params.get("user_id");
				 list.add(user_id);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("agentNo")) && !"-1".equals(params.get("agentNo"))){
				 String agentNo = params.get("agentNo");
				 list.add(agentNo);
			 }else{
				 list.add(null);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("pos_type"))  && !"-1".equals(params.get("pos_type"))){
				 String pos_type = params.get("pos_type");
				 list.add(pos_type);
			 }else{
				 list.add(null);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("check_type"))){
				 String check_type = params.get("check_type");
				 list.add(check_type);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("create_person"))){
				 String create_person = params.get("create_person");
				 list.add(create_person);
			 }
			 addCount =dao.update(sql, list.toArray());
			 log.info("MerchantCheckPersonService addMerchantCheckPersonInfo SUCCESS");
		}
		
		log.info("MerchantCheckPersonService addMerchantCheckPersonInfo End");
		return addCount;
	}
	
	/**
	 * 商户审核人信息查询
	 * @author 王帅
	 * @param params 查询条件
	 * @param pageRequest 分页信息
	 * @return 返回带分页信息的结果集
	 */
	public Page<Map<String, Object>> getMerchantCheckPersonList(Map<String, String> params,PageRequest pageRequest){
		log.info("MerchantCheckPersonService getMerchantCheckPersonList start ...");
		String sql = "select mp.*,bu.real_name from merchant_check_person mp,boss_user bu where  mp.user_id=bu.id";
		List<String> list = new ArrayList<String>();
		 if(null != params){
			 if(StringUtils.isNotEmpty(params.get("user_id"))  && !"".equals(params.get("user_id"))){
				 String user_id = params.get("user_id");
				 sql += " and mp.user_id=?";
				 list.add(user_id);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("agent_no")) && !"-1".equals(params.get("agent_no"))){
				 String agent_no = params.get("agent_no");
				 sql += " and mp.agent_no=?";
				 list.add(agent_no);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("pos_type")) && !"-1".equals(params.get("pos_type"))){
				 String pos_type = params.get("pos_type");
				 sql += " and mp.pos_type=?";
				 list.add(pos_type);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("check_type")) && !"-1".equals(params.get("check_type"))){
				 String check_type = params.get("check_type");
				 sql += " and mp.check_type=?";
				 list.add(check_type);
			 }
			 
			 if(StringUtils.isNotEmpty(params.get("check_status")) && !"-1".equals(params.get("check_status"))){
				 String check_status = params.get("check_status");
				 sql += " and mp.check_status=?";
				 list.add(check_status);
			 }
			 
		 }
		log.info("MerchantCheckPersonService getMerchantCheckPersonList End");
		return  dao.find(sql, list.toArray(), pageRequest);
	}
	
}
