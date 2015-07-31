package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.encryptor.md5.Md5;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.Http;
import com.eeepay.boss.utils.SysConfig;

/**
 * @author 王帅
 * @date 2015年3月26日12:03:48
 * @see 本类用于特殊需求批量操作类，所有特殊需求设计批量操作，请写在此处
 */
@Service
public class SpecialService {

	@Resource
	private Dao dao;
	
	private static final Logger log = LoggerFactory.getLogger(SpecialService.class);
	
	/**
	 * @author 王帅
	 * @date 2015年4月14日20:02:16
	 * @see 本方法用于批量转移商户所属集群，并将商户的“是否优质商户”以及“是否钱包结算”统一修改为与需要转入的目标集群"是否优质"、
	 * "是否钱包结算"一致,并保存操作记录，且已做事务控制，任何环节操作失败都将回滚，最终将返回操作失败的字符串商户编号类型的集合！
	 * @param merchantNoList 字符集合商户编号
	 * @param realName 操作人
	 * @param group_code 目标集群编号
	 * @param my_settle 目标集群优质商户信息 0：否 1：是
	 * @param bag_settle 目标集群钱包结算信息  0：否 1：是
	 * @return 返回操作失败的字符串集合List
	 * @throws SQLException
	 */
	public List<String> batchMerchantGroup(List<String> merchantNoList, String realName, String group_code, String my_settle, String bag_settle, String operType) throws SQLException{
		log.info("MerchantService batchMerchantGroup START...");
		List<String> failList = new ArrayList<String>();
//		for (int i = 0; i < merchantNoList.size(); i++) {
		for(String merchant_no : merchantNoList){
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);
				String mgsql = "update trans_route_group_merchant t set t.group_code=? where t.merchant_no=?";
				String insSql = "insert into trans_route_group_merchant(group_code, merchant_no) values(?,?)";
				List<String> list = new ArrayList<String>();
				list.add(group_code);
//				list.add(merchantNoList.get(i));
				list.add(merchant_no);
				int mgsqlCount = dao.updateByTranscation("0".equals(operType) ? mgsql : insSql, list.toArray(), conn);
				if(mgsqlCount>0){
					list.clear();
					String updateSettleSQL = "update pos_merchant p set p.my_settle=?,p.bag_settle=? where p.merchant_no=?";
					list.add(my_settle);
					list.add(bag_settle);
//					list.add(merchantNoList.get(i));
					list.add(merchant_no);
					int updateSettleCount = dao.updateByTranscation(updateSettleSQL, list.toArray(), conn);
					if(updateSettleCount > 0){
						list.clear();
						String addMonitorSQL = "insert into trans_route_group_merchant_log(merchant_no,group_code,create_person,my_settle,bag_settle,create_type) values(?,?,?,?,?,?)";
//						list.add(merchantNoList.get(i));
						list.add(merchant_no);
						list.add(group_code);
						list.add(realName);
						list.add(my_settle);
						list.add(bag_settle);
						list.add("0".equals(operType) ? "批量转移-转入" : "批量添加-转入");
						int addMonitorCount = dao.updateByTranscation(addMonitorSQL, list.toArray(), conn);
						if(addMonitorCount > 0){
							conn.commit();
						}else{
							conn.rollback();
//							failList.add(merchantNoList.get(i));
							failList.add(merchant_no);
						}
					}else{
						conn.rollback();
//						failList.add(merchantNoList.get(i));
						failList.add(merchant_no);
					}
				}else{
//					failList.add(merchantNoList.get(i));
					failList.add(merchant_no);
				}
			} catch (Exception e) {
//				log.error("批量转移集群异常，商户编号=" + merchantNoList.get(i) + ",操作人=" + realName + "，集群编号=" + group_code + ",错误信息=" + e.getMessage());
				log.error("批量转移集群异常，商户编号=" + merchant_no + ",操作人=" + realName + "，集群编号=" + group_code + ",错误信息=" + e.getMessage());
				conn.rollback();
//				failList.add(merchantNoList.get(i));
				failList.add(merchant_no);
			}finally{
				conn.close();
			}
		}
		log.info("MerchantService batchMerchantGroup End");
		return failList;
	}
	
	/**
	 * @author 王帅
	 * @date 2015年3月31日17:30:18
	 * @see 根据商户编号重置同步好乐付状态，并记录操作人以及操作的商户编号，方法返回未操作成功的商户编号
	 * @param merchantNoList 商户编号集合
	 * @param realName 操作人姓名
	 * @return 操作失败的List
	 * @throws SQLException
	 */
	public List<String> batchCancelHLF(List<String> merchantNoList, String realName) throws SQLException{
		log.info("MerchantService specialMerchant START...");
		List<String> failList = new ArrayList<String>();
		if(null != merchantNoList && merchantNoList.size() > 0 && null != realName && !"".equals(realName)){
			for (String merchant_no : merchantNoList) {
				Connection conn = null;
				try {
					conn = dao.getConnection();
					conn.setAutoCommit(false);
					String cancelHLFSQL = "update pos_merchant p set p.send_hlf=0 where p.merchant_no=?";
					int cancelHLFCount = 0;
					List<String> cancelHLFList = new ArrayList<String>();
					cancelHLFList.add(merchant_no);
					cancelHLFCount = dao.updateByTranscation(cancelHLFSQL, cancelHLFList.toArray(), conn);

					// 判断是否重置成功，并且收单商户和收单机构终端关闭成功
					if(cancelHLFCount > 0 && closeAcqMerchantAndTerminal(merchant_no, conn)){

						String addMonitorSQL = "insert into send_hlf_monitor(merchant_no,create_person,operation_type) values(?,?,?)";
						List<String> addMonitorList = new ArrayList<String>();
						addMonitorList.add(merchant_no);
						addMonitorList.add(realName);
						addMonitorList.add("2");//重置同步好乐付状态
						int addMonitorCount = 0;
						addMonitorCount = dao.updateByTranscation(addMonitorSQL, addMonitorList.toArray(), conn);
						if(addMonitorCount > 0){
							conn.commit();
						}else{
							conn.rollback();
							failList.add(merchant_no);
						}
					}else{
						conn.rollback();
						failList.add(merchant_no);
					}
				} catch (Exception e) {
					log.error("MerchantService specialMerchant Exception = " + e.getMessage()+",操作人="+realName);
					conn.rollback();
					failList.add(merchant_no);
				}finally{
					conn.close();
				}
			}
		}
		log.info("MerchantService specialMerchant START...");
		return failList;
	}

	/*
	* 关闭收单商户和收单机构终端并记录日志
	* */
	private boolean closeAcqMerchantAndTerminal(String merchant_no, Connection conn) throws Exception{

		String selectAcqMerchant = "select acq_merchant_no, merchant_no from acq_merchant where merchant_no = ?";
		String closeAcqMerchant = "update acq_merchant set locked = 2 where merchant_no = ? ";
		String closeAcqTerminal = "update acq_terminal set status = 0 where acq_merchant_no = ?";
		String insertAcqMerchantLog = "insert into acq_merchant_log(acq_merchant_no,merchant_no,locked,oper_time,oper_id,oper,oper_type) values(?,?,?,now(),?,?,?)";

		Map<String, Object> acqMerchant = dao.findFirst(selectAcqMerchant, new Object[]{merchant_no});
		if(acqMerchant != null && acqMerchant.get("acq_merchant_no") != null){
			int closedAcqMerchant = dao.updateByTranscation(closeAcqMerchant, new Object[]{merchant_no}, conn);
			int closedAcqTerminal = dao.updateByTranscation(closeAcqTerminal, new Object[]{acqMerchant.get("acq_merchant_no").toString()}, conn);

			BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
			int insertLog = dao.updateByTranscation(insertAcqMerchantLog, new Object[]{acqMerchant.get("acq_merchant_no").toString(), merchant_no, 2, bu.getId(), bu.getRealName(), 0}, conn);

			return closedAcqMerchant > 0 && closedAcqTerminal > 0 && insertLog > 0;
		}

		return false;
	}

	/**
	 * @author 王帅
	 * @date 2015年3月26日15:36:36
	 * @see 根据导入的Excel文件批量修改商户信息，并记录操作人
	 * @param params 需要修改的内容
	 * @param list 需要操作的商户编号
	 * @param realName 操作人
	 * @return List<String> 操作失败的商户编号
	 * @throws SQLException
	 */
	public List<String> specialModifyMerchant(Map<String, String> params, List<String> list, String realName) throws SQLException{
		log.info("MerchantService specialMerchant START...");
		List<String> failList = new ArrayList<String>();
		if(params != null && realName != null && !"".equals(realName) && list != null && list.size() > 0){
				for (int i = 0; i < list.size(); i++) {
					Connection conn = null;
					StringBuffer specialModifySQL = new StringBuffer("update pos_merchant p set ");
					List<String> specialModifyList = new ArrayList<String>();
					boolean specialModify = false;
					if(null != params.get("merchant_type") && !"-1".equals(params.get("merchant_type").toString())){
						specialModifySQL.append("p.merchant_type=?");
						specialModifyList.add(params.get("merchant_type").toString());
						specialModify = true;
					}
					
					if(null != params.get("real_flag") && !"-1".equals(params.get("real_flag").toString())){
						if(specialModify){
							specialModifySQL.append(",p.real_flag=?");
							specialModifyList.add(params.get("real_flag").toString());
							specialModify = true;
						}else{
							specialModifySQL.append("p.real_flag=?");
							specialModifyList.add(params.get("real_flag").toString());
							specialModify = true;
						}
					}
					
					if(null != params.get("open_status") && !"-1".equals(params.get("open_status").toString())){
						if(specialModify){
							specialModifySQL.append(",p.open_status=?");
							specialModifyList.add(params.get("open_status").toString());
						}else{
							specialModifySQL.append("p.open_status=?");
							specialModifyList.add(params.get("open_status").toString());
						}
					}
					try {
						conn = dao.getConnection();
						conn.setAutoCommit(false);
						int specialModifyCount= 0;
						specialModifySQL.append(" where p.merchant_no=?");
						specialModifyList.add(list.get(i));
						specialModifyCount = dao.updateByTranscation(specialModifySQL.toString(), specialModifyList.toArray(), conn);
						if(specialModifyCount > 0){
							String addMonnitorSQL = "insert into special_modify(merchant_no,merchant_type,real_flag,open_status,real_name) values(?,?,?,?,?);";
							int addMonnitorCount = 0;
							List<String> addMonnitorList = new ArrayList<String>();
							addMonnitorList.add(list.get(i));
							addMonnitorList.add(params.get("merchant_type"));
							addMonnitorList.add(params.get("real_flag"));
							addMonnitorList.add(params.get("open_status"));
							addMonnitorList.add(realName);
							addMonnitorCount = dao.updateByTranscation(addMonnitorSQL, addMonnitorList.toArray(), conn);
							if(addMonnitorCount > 0){
								conn.commit();
							}else{
								failList.add(list.get(i));
								conn.rollback();
							}
						}else{
							failList.add(list.get(i));
						}
					} catch (Exception e) {
						log.error("MerchantService specialMerchant Exception = " + e.getMessage());
						conn.rollback();
						failList.add(list.get(i));
					}finally{
						conn.close();
					}
					specialModifyList.clear();
				}
		}
		log.info("MerchantService specialMerchant END");
		return failList;
	}

	
	/**
	 * 批量转移代理商，如只转所属代理商，则马上生效，如转移一级代理商，则次月生效
	 * @param params
	 * @param list
	 * @param user
	 * @return
	 */
	public List<String> batchTransferAgent(Map<String, String> params, List<String> list, BossUser user) {
		log.info("MerchantService batchTransferAgent START...");
		
		List<String> failList = new ArrayList<String>();
		String agent_no = params.get("agentNo");
		String belong_to_agent = params.get("belong_to_agent");
		String sale_name = params.get("sale_name");
		
		log.info("=================加载商户、定时任务列表开始===========================");
		StringBuffer sb = new StringBuffer();
		List<String> merList = new ArrayList<String>();
		for(String merchant_no : list){
			sb.append("?,");
			merList.add(merchant_no);
		}
		
		String select_agent4exists = "select merchant_no,agent_no from pos_merchant where merchant_no in (" + sb.substring(0, sb.lastIndexOf(",")) + ")";
		List<Map<String, Object>> merchantList = dao.find(select_agent4exists, merList.toArray());
		
		String select_agent4quartzExists = "select merchant_no,agent_no from pos_merchant_agent4quartz where merchant_no in (" + sb.substring(0, sb.lastIndexOf(",")) + ")";
		List<Map<String, Object>> quartzList = dao.find(select_agent4quartzExists, merList.toArray());
		
		log.info("=================加载商户、定时任务列表结束===========================");
		
		log.info("=================转移商户代理商开始===========================");
	
		for(Map<String, Object> merchant : merchantList){
			String merchantNo = merchant.get("merchant_no").toString();
			String agentNo = merchant.get("agent_no").toString();
				
			if(agent_no.equals(agentNo)){
				
				// 需要实时修改所属代理商(如果需要，要删除定时任务中可能存在的任务)
				log.info("=========================商户编号：" + merchantNo + "实时修改所属代理商开始=========================");
				try {
					transferAgentNow(merchantNo, agentNo, belong_to_agent, sale_name, user, quartzList);
				} catch (Exception e) {
					failList.add(merchantNo);
					e.printStackTrace();
				}
				
				log.info("=========================商户编号：" + merchantNo + "实时修改所属代理商结束=========================");
			}else{
				// 需要次月生效一级代理商及所属代理商
				log.info("=========================商户编号：" + merchantNo + "注册次月生效代理商定时任务开始=========================");
				try {
					transferAgentNextMonth(merchantNo, agent_no, belong_to_agent, sale_name, user, quartzList);
				} catch (Exception e) {
					failList.add(merchantNo);
					e.printStackTrace();
				}
				log.info("=========================商户编号：" + merchantNo + "注册次月生效代理商定时任务结束=========================");
			}
			
		}
		
		log.info("=================转移商户代理商结束===========================");
		
		return failList;
	}
	
	private void transferAgentNextMonth(String merchantNo, String agentNo, String belongToAgent, String saleName, BossUser user, List<Map<String, Object>> quartzList) throws Exception {
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			// 定时任务中存在该商户的定时任务，则修改该定时任务
			log.info("=================判断是否存在定时任务==============================");
			boolean existsFlag = false;
			for(Map<String, Object> merchantNos : quartzList){
				if(merchantNo != null && merchantNo.equals(merchantNos.get("merchant_no").toString())){
					log.info("=================存在定时任务==============================");
					String update_agent4quartz = "update pos_merchant_agent4quartz set agent_no = ?, belong_to_agent = ?, sale_name =?, update_time = now() where merchant_no = ?";
					dao.updateByTranscation(update_agent4quartz, new Object[]{agentNo, belongToAgent, saleName, merchantNo}, conn);
					existsFlag = true;
					break;
				}
			}
			// 定时任务中不存在该商户的定时任务，则增加定时任务
			if(!existsFlag){
				log.info("=================不存在定时任务==============================");
				String insert_agent4quartz = "insert into pos_merchant_agent4quartz(merchant_no, agent_no, belong_to_agent, sale_name, create_time) values(?, ?, ?, ?, now())";
				dao.updateByTranscation(insert_agent4quartz, new Object[]{merchantNo, agentNo, belongToAgent, saleName}, conn);
			}
			// 记录操作日志
			log.info("=================记录注册定时任务操作日志==============================");
			String insert_agent4log = "insert into pos_merchant_agent4log(merchant_no, oper_id, oper_name, content, oper_time) values (?, ?, ?, ?, now())";
			String content = "操作员ID:" + user.getId() + " 操作人" + user.getRealName() + ",修改商户：" + merchantNo + "代理商为：" + agentNo + "二级代理商为：" + belongToAgent + "所属销售为：" + saleName;
			dao.updateByTranscation(insert_agent4log, new Object[]{merchantNo, user.getId(), user.getRealName(), content}, conn);
			
			conn.commit();// 提交事务
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("商户号：" + merchantNo + "注册定时任务失败！");
			conn.rollback();// 回滚事务
		} finally {
			conn.close();// 关闭连接
		}
	}

	private void transferAgentNow(String merchantNo, String agentNo, String belongToAgent, String saleName, BossUser user, List<Map<String, Object>> quartzList) throws Exception {
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			log.info("=================修改所属代理商同步钱包==============================");
			// 同步到钱包
			Map<String, Object> map = getMerchantByNo(merchantNo);
			Map<String, Object> nodeMap = dao.findFirst("select * from agent_info where agent_no=?", belongToAgent);
			map.put("parent_node",nodeMap.get("self_node").toString());
			map.put("oldMobileNo", map.get("mobile_username").toString());

			map.put("sale_name", saleName); // 同步钱包增加所属销售字段
			String appNo = map.get("app_no").toString().trim();
			String hmac = Md5.md5Str(map.get("mobile_username").toString() + appNo + Constants.BAG_HMAC);
			map.put("hmac", hmac);
			Http.send(SysConfig.value("merModMobile2Bag"), map, "UTF-8");

			// 实时修改所属代理商
			log.info("=================实时修改所属代理商==============================");
			String sql = "update pos_merchant set parent_node = ?, belong_to_agent = ?, sale_name = ? where merchant_no = ? and agent_no = ?";


			List<Object> listPosMerchant= new ArrayList<Object>();
			String self_node=(String)nodeMap.get("self_node");
			listPosMerchant.add(self_node);
			listPosMerchant.add(belongToAgent);
			listPosMerchant.add(saleName);
			listPosMerchant.add(merchantNo);
			listPosMerchant.add(agentNo);
			dao.updateByTranscation(sql, listPosMerchant.toArray(), conn);

			// 添加商户修改操作日志
			log.info("=================添加商户修改操作日志==============================");
			addPosMerchantLogNew(merchantNo, conn, "1", user);
			
			// 如果定时任务中有该商户的任务，则删除
			log.info("=================如果定时任务中有该商户的任务，则删除==============================");
			for(Map<String, Object> merchantNos : quartzList){
				if(merchantNo != null && merchantNo.equals(merchantNos.get("merchant_no").toString())){
					log.info("=================定时任务中有该商户的任务，删除==============================");
					String delete_agent4quartz = "delete from pos_merchant_agent4quartz where merchant_no = ?";
					dao.updateByTranscation(delete_agent4quartz, new Object[]{merchantNo}, conn);
					
					// 记录操作日志
					String insert_agent4log = "insert into pos_merchant_agent4log(merchant_no, oper_id, oper_name, content, oper_time) values (?, ?, ?, ?, now())";
					String content = "操作员ID:" + user.getId() + " 操作人" + user.getRealName() + ",删除商户：" + merchantNo + "转移代理商定时任务";
					dao.updateByTranscation(insert_agent4log, new Object[]{merchantNo, user.getId(), user.getRealName(), content}, conn);

					break;
				}
			}
			
			conn.commit();// 提交事务
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("商户号：" + merchantNo + "修改所属代理商失败！");
			conn.rollback();// 回滚事务
		} finally {
			conn.close();// 关闭连接
		}
	}

	/**
	 * 根据代理商编号查询
	 * @param agentNo
	 * @return
	 * @throws SQLException
	 */
	public boolean checkAgentExistsAndSatisfied(String agent_no, String belong_to_agent) throws SQLException {
		String sql = "select id from agent_info where self_node like (select concat(self_node,'%') from agent_info where agent_no = ?) and agent_no = ?";
		List<Map<String, Object>> list = dao.find(sql, new Object[]{agent_no, belong_to_agent});
		return list != null && list.size() > 0;
	}
	
	public Map<String, Object> getMerchantByNo(String merchantNo){
		String sql = "select * from pos_merchant where merchant_no = ?";
		return dao.findFirst(sql, new Object[]{merchantNo});
	}
	
	/**
	 * 增加商户修改操作日志
	 * @param merchant_no
	 * @param conn
	 * @param oper_type
	 * @throws SQLException
	 */
	public void addPosMerchantLogNew(String merchant_no, Connection conn, String oper_type, BossUser bu) throws SQLException {
		String sql = "select m.*,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee,r.single_max_amount,r.ed_max_amount, r.ed_card_max_amount, r.ed_card_max_items, CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name from pos_merchant m left join pos_merchant_fee f on m.merchant_no = f.merchant_no   left join pos_merchant_trans_rule r on m.merchant_no = r.merchant_no "
				+ " left join agent_info a on m.agent_no = a.agent_no where m.merchant_no = ? ";
		Map<String, Object> params = dao.findFirst(sql, new Object[]{merchant_no});
		
		List<Object> list = new ArrayList<Object>();
		list.add(params.get("merchant_no"));
		list.add(params.get("merchant_name"));
		list.add(params.get("merchant_short_name"));
		list.add(params.get("lawyer"));
		list.add(params.get("merchant_type"));
		list.add(params.get("mobile_username"));
		list.add(params.get("open_status"));
		list.add(params.get("agent_no"));
		list.add(params.get("address"));
		list.add(params.get("sale_address"));
		list.add(params.get("province"));
		list.add(params.get("city"));
		list.add(params.get("link_name"));
		list.add(params.get("phone"));
		list.add(params.get("email"));
		list.add(params.get("mcc"));
		list.add(params.get("sale_name"));
		list.add(params.get("settle_cycle"));
		list.add(params.get("bank_name"));
		list.add(params.get("account_no"));
		list.add(params.get("account_name"));
		list.add(params.get("cnaps_no"));
		list.add(params.get("account_type"));
		list.add(params.get("attachment"));
		list.add(params.get("real_flag"));
		list.add(params.get("id_card_no"));
		list.add(params.get("terminal_count"));
		list.add(params.get("main_business"));
		list.add(params.get("remark"));
		list.add(params.get("code_word"));
		list.add(params.get("belong_to_agent"));
		String trans_time_start = params.get("trans_time_start") != null ? params.get("trans_time_start").toString() : null;
		String trans_time_end = params.get("trans_time_end") != null ? params.get("trans_time_end").toString() : null;
		list.add(trans_time_start);
		list.add(trans_time_end);
		// 费率
		String fee_type = params.get("fee_type") != null ? params.get("fee_type").toString() : "";
		String rate1 = params.get("rate1") != null ? params.get("rate1").toString() : null;
		String rate2 = params.get("rate2") != null ? params.get("rate2").toString() : null;
		String ladder_min = params.get("ladder_min") != null ? params.get("ladder_min").toString() : "";
		String ladder_value = params.get("ladder_value") != null ? params.get("ladder_value").toString() : "";
		String ladder_max = params.get("ladder_max") != null ? params.get("ladder_max").toString() : "";

		list.add(fee_type);
		if ("RATIO".equals(fee_type)) {
			list.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
			list.add(null);
			list.add(null);
			list.add(params.get("fee_single_amount"));
			list.add(null);

		} else if ("CAPPING".equals(fee_type)) {
			list.add(rate1 == null ? null : new BigDecimal(rate1).movePointLeft(2));
			list.add((rate1 == null || rate2 == null) ? null : new BigDecimal(rate2).divide(new BigDecimal(rate1).movePointLeft(2), 2, BigDecimal.ROUND_HALF_UP));
			list.add((rate2 == null || rate2.trim().length() == 0) ? null : new BigDecimal(rate2));
			list.add(params.get("fee_single_amount"));
			list.add(null);

		} else if ("LADDER".equals(fee_type)) {

			ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
			ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

			String ladder_fee = ladder_min + "<" + ladder_value + "<" + ladder_max;

			list.add(null);
			list.add(null);
			list.add(null);
			list.add(params.get("fee_single_amount"));
			list.add(ladder_fee);

		}else {
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);
		}
		list.add(params.get("terminal_no"));
		// 交易规则
		list.add(params.get("single_max_amount"));
		String ed_max_amount = params.get("ed_max_amount") != null ? params.get("ed_max_amount").toString() : "";
		String single_max_amount = params.get("single_max_amount") != null ? params.get("single_max_amount").toString() : "";
		String ed_card_max_amount = params.get("ed_card_max_amount") != null ? params.get("ed_card_max_amount").toString() : "";
		String ed_card_max_items = params.get("ed_card_max_items") != null ? params.get("ed_card_max_items").toString() : "";
		list.add((single_max_amount == null || single_max_amount.trim().length() == 0) ? null : new BigDecimal(single_max_amount));
		list.add((ed_max_amount == null || ed_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_max_amount));
		list.add((ed_card_max_items == null || ed_card_max_items.trim().length() == 0) ? null : new BigDecimal(ed_card_max_items));
		list.add((ed_card_max_amount == null || ed_card_max_amount.trim().length() == 0) ? null : new BigDecimal(ed_card_max_amount));


		list.add(params.get("oper_desc"));
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(oper_type);
		list.add(params.get("my_settle"));
		
		list.add(params.get("clear_card_no"));
		list.add(params.get("pos_type"));
		list.add(params.get("add_type"));
		list.add(params.get("trans_cancel"));
		list.add(params.get("pay_method"));
		list.add(params.get("bag_settle"));
		list.add(params.get("bus_license_no"));
		list.add(params.get("my_settle"));
		
		String insert = "INSERT INTO pos_merchant_log (" + "merchant_no, " + "merchant_name, " + "merchant_short_name, " + "lawyer, " + "merchant_type, " + "mobile_username, " + "open_status, " + "agent_no, " + "address, " + "sale_address, " + "province, " + "city, " + "link_name, " + "phone, " + "email, " + "mcc, " + "sale_name, " + "settle_cycle, " + "bank_name, " + "account_no, "
				+ "account_name, " + "cnaps_no, " + "account_type, " + "attachment, " + "real_flag, " + "id_card_no, " + "terminal_count, " + "main_business, " + "remark, " + "code_word, " + "belong_to_agent, " + "trans_time_start, " + "trans_time_end, " + "fee_type, " + "fee_rate, " + "fee_cap_amount, " + "fee_max_amount, " + "fee_single_amount, " + "ladder_fee, " + "terminal_no, "
				+ "single_max_amount, " + "ed_max_amount, " + "ed_card_max_items, " + "ed_card_max_amount, " + "ed_total_amount, " + "operate_time, " + "oper_desc, " + "oper_user_id, " + "oper_user_name, " + "oper_type, " + "my_settle,"
				+ "clear_card_no, pos_type, add_type, trans_cancel, pay_method, bag_settle, bus_license_no, app_no "
				+")" + "VALUES" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";

		dao.updateByTranscation(insert, list.toArray(), conn);
	}
}
