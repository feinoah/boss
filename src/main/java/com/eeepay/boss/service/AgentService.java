package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ctc.wstx.util.StringUtil;
import com.eeepay.boss.domain.AgentInfo;
import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.utils.SysConfig;

/**
 * 系统用户及权限管理
 * 
 * @author dj
 * 
 */
@Service
public class AgentService {
	@Resource
	private Dao dao;

	private static final Logger log = LoggerFactory
			.getLogger(AgentService.class);

	// @Resource
	// private CleanCacheService cleanCacheService;
	
	/**
	 * 根据代理商编号 获取代理商电话
	 * @author 王帅
	 * @param agent_no 代理商编号
	 * @return 代理商手机号码
	 */
	public Map<String, Object> getAgentPhoneByAgentNo(String agent_no){
		Map<String, Object> maps = new HashMap<String, Object>();
		if(null != agent_no && !"".equals(agent_no)){
			String sql = "select a.agent_link_tel from agent_info a where a.agent_no=? limit 1";
			List<String> list = new ArrayList<String>();
			list.add(agent_no);
			maps = dao.findFirst(sql, list.toArray());
		}
		return maps;
	}

	/**
	 * 查询超级刷代理商信息(要求与代理商分离出来)
	 * 
	 * @author 王帅
	 * @param params
	 *            查询条件
	 * @param pageRequest
	 *            分页信息
	 * @return 返回超级刷代理商集合
	 */
	public Page<Map<String, Object>> getAgentSPOSList(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))
					&& (!"-1".equals(params.get("agentNo")))) {
				String agentNo = params.get("agentNo");
				System.out.println(agentNo);
				sb.append(" and a.agent_no = ? ");
				list.add(agentNo);
			} else {
				sb.append(" and (a.parent_id is null or a.parent_id='' or a.parent_id='0') ");
			}
			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = "%" + params.get("agentName") + "%";
				sb.append(" and a.agent_name like ?  ");
				list.add(merchantName);
			}

			// agentType代理商类型查询
			if (StringUtils.isNotEmpty(params.get("agentType"))
					&& (!"-1".equals(params.get("agentType")))) {
				String agentType = params.get("agentType");
				sb.append(" and a.agent_no like ? ");
				list.add(agentType);
			}
			// 扣率锁定状态
			if (StringUtils.isNotEmpty(params.get("locked_status"))
					&& (!"-1".equals(params.get("locked_status")))) {
				String locked_status = params.get("locked_status");
				sb.append(" and (f.locked_status = ?) ");
				list.add(locked_status);
			}
			// 扣率审核状态
			if (StringUtils.isNotEmpty(params.get("checked_status"))
					&& (!"-1".equals(params.get("checked_status")))) {
				String checked_status = params.get("checked_status");
				sb.append(" and (f.checked_status = ?) ");
				list.add(checked_status);
			}

			// 分润比例锁定状态
			if (StringUtils.isNotEmpty(params.get("share_locked_status"))
					&& (!"-1".equals(params.get("share_locked_status")))) {
				String locked_status = params.get("share_locked_status");
				sb.append(" and (r.locked_status = ?) ");
				list.add(locked_status);
			}
			// 分润比例审核状态
			if (StringUtils.isNotEmpty(params.get("share_checked_status"))
					&& (!"-1".equals(params.get("share_checked_status")))) {
				String share_checked_status = params
						.get("share_checked_status");
				sb.append(" and (r.checked_status = ?) ");
				list.add(share_checked_status);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and a.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and a.create_time <=? ");
				list.add(createTimeEnd);
			}
			
			String isInvest = params.get("is_invest");
			if(StringUtils.isNotEmpty(isInvest) && !"-1".equals(isInvest)){
				if("0".equals(isInvest)){
					sb.append(" and (a.is_invest = ? or a.is_invest = '' or a.is_invest is null) ");
				}else{
					sb.append(" and a.is_invest = ? ");
				}
				list.add(isInvest);
			}
		}
		String sql = "select a.*, CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,p.agent_name as pagentname,IFNULL(h.terminal_count,0) as terminal_count,f.locked_status,f.checked_status,"
				+ "r.locked_status share_locked_status,r.checked_status share_checked_status from agent_info a "
				+ " LEFT JOIN agent_info p on a.parent_id=p.id  LEFT JOIN agent_settle_fee f ON a.agent_no=f.agent_no  left join agent_share_rule r on a.agent_no=r.agent_no "
				+ " LEFT JOIN ( SELECT agent_no,COUNT(*) terminal_count FROM pos_terminal GROUP BY  agent_no)  h "
				+ "ON a.agent_no = h.agent_no "
				+ "where 1=1 "
				+ sb.toString()
				+ " and r.rule_type='POS' and a.brand_type='SPOS' order by a.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	/**
	 * 根据代理商编号获取代理商信息
	 * 
	 * @param userName
	 * @return
	 */
	public AgentInfo getAgentNo(String agentNo) {
		AgentInfo agentInfo = null;
		String sql = "select * from agent_info where agent_no=?";
		agentInfo = dao.findFirst(AgentInfo.class, sql, agentNo);
		return agentInfo;
	}

	// 查询代理商
	public Page<Map<String, Object>> getAgentList(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))
					&& (!"-1".equals(params.get("agentNo")))) {
				String agentNo = params.get("agentNo");
				System.out.println(agentNo);
				sb.append(" and a.agent_no = ? ");
				list.add(agentNo);
			} else {
				sb.append(" and (a.parent_id is null or a.parent_id='' or a.parent_id='0') ");
			}
			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = "%" + params.get("agentName") + "%";
				sb.append(" and a.agent_name like ?  ");
				/*
				 * String merchantName =params.get("agentName");
				 * sb.append(" and a.agent_name = ?  ");
				 */
				list.add(merchantName);
				/* list.add(merchantName); */
			}

			// agentType代理商类型查询
			if (StringUtils.isNotEmpty(params.get("agentType"))
					&& (!"-1".equals(params.get("agentType")))) {
				String agentType = params.get("agentType");
				sb.append(" and a.agent_no like ? ");
				list.add(agentType);
			}
			// 扣率锁定状态
			if (StringUtils.isNotEmpty(params.get("locked_status"))
					&& (!"-1".equals(params.get("locked_status")))) {
				String locked_status = params.get("locked_status");
				sb.append(" and (f.locked_status = ?) ");
				list.add(locked_status);
			}
			// 扣率审核状态
			if (StringUtils.isNotEmpty(params.get("checked_status"))
					&& (!"-1".equals(params.get("checked_status")))) {
				String checked_status = params.get("checked_status");
				sb.append(" and (f.checked_status = ?) ");
				list.add(checked_status);
			}

			// 分润比例锁定状态
			if (StringUtils.isNotEmpty(params.get("share_locked_status"))
					&& (!"-1".equals(params.get("share_locked_status")))) {
				String locked_status = params.get("share_locked_status");
				sb.append(" and (r.locked_status = ?) ");
				list.add(locked_status);
			}
			// 分润比例审核状态
			if (StringUtils.isNotEmpty(params.get("share_checked_status"))
					&& (!"-1".equals(params.get("share_checked_status")))) {
				String share_checked_status = params
						.get("share_checked_status");
				sb.append(" and (r.checked_status = ?) ");
				list.add(share_checked_status);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and a.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and a.create_time <=? ");
				list.add(createTimeEnd);
			}
			
			String isInvest = params.get("is_invest");
			if(StringUtils.isNotEmpty(isInvest) && !"-1".equals(isInvest)){
				if("0".equals(isInvest)){
					sb.append(" and (a.is_invest = ? or a.is_invest = '' or a.is_invest is null) ");
				}else{
					sb.append(" and a.is_invest = ? ");
				}
				list.add(isInvest);
			}
		}
		// String sql = "select a.* from agent_info a where 1=1 "
		// + sb.toString() + " order by a.id desc";
		String sql = "select a.*, p.agent_name as pagentname,IFNULL(h.terminal_count,0) as terminal_count,f.locked_status,f.checked_status,"
				+ "r.locked_status share_locked_status,r.checked_status share_checked_status from agent_info a "
				+ " LEFT JOIN agent_info p on a.parent_id=p.id  LEFT JOIN agent_settle_fee f ON a.agent_no=f.agent_no  left join agent_share_rule r on a.agent_no=r.agent_no "
				+ " LEFT JOIN ( SELECT agent_no,COUNT(*) terminal_count FROM pos_terminal GROUP BY  agent_no)  h "
				+ "ON a.agent_no = h.agent_no "
				+ "where 1=1 and a.brand_type is NULL "
				+ sb.toString()
				+ " and r.rule_type='POS' and (a.brand_type='' or a.brand_type is null )  order by a.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 根据联系人查询代理商
	public Page<Map<String, Object>> getAgentListAccordLink(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			list.add(params.get("sale_name"));

			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))
					&& (!"-1".equals(params.get("agentNo")))) {
				String agentNo = params.get("agentNo");
				sb.append(" and a.agent_no like ? ");
				list.add(agentNo);
			}
			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			// 所属销售查询
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");
				sb.append(" and a.sale_name=?  ");
				list.add(sale_name);
			}

			// agentType代理商类型查询
			if (StringUtils.isNotEmpty(params.get("agentType"))
					&& (!"-1".equals(params.get("agentType")))) {
				String agentType = params.get("agentType");
				sb.append(" and a.agent_no like ? ");
				list.add(agentType);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and a.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and a.create_time <=? ");
				list.add(createTimeEnd);
			}
		}

		String sql = "select a.*, p.agent_name as pagentname,IFNULL(h.terminal_count,0) as terminal_count from agent_info a "
				+ " LEFT JOIN agent_info p on a.parent_id=p.id  "
				+ " LEFT JOIN ( SELECT agent_no,COUNT(*) terminal_count FROM pos_terminal GROUP BY  agent_no)  h "
				+ "ON a.agent_no = h.agent_no " + "where a.sale_name = ? " +
				// dao.find(sql1).get(0).get("agent_no")+")"+
				sb.toString() +

				" order by a.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 根据联系人查询代理商(供下拉列表使用)
	public Page<Map<String, Object>> getAgentListAccordLinkTag(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();

		list.add(params.get("sale_name"));

		String sql = "select CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name , a.agent_no from agent_info a "
				+ "where a.sale_name = ?   and  (a.parent_id is null or  a.parent_id='' or a.parent_id='0')"
				+
				// dao.find(sql1).get(0).get("agent_no")+")"+

				" order by a.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 代理商明细
	public Map<String, Object> getAgentDetail(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		// String sql = "select a.* from agent_info a where a.id = ?";
		String sql = "select a.*, CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,u.default_password, p.agent_name as pagentname from agent_user u, agent_info a LEFT JOIN agent_info p on a.parent_id=p.id  where a.id = ? and u.agent_no=a.agent_no";
		Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
		return agentInfo;
	}

	// 代理商分润明细
	public List<Map<String, Object>> getAgentFeeAndRuleDetail(String agentNo) {
		List<Object> list = new ArrayList<Object>();
		list.add(agentNo);
		String sql = "select  r.sharing_rule , r.rule_type from agent_share_rule r   where r.agent_no = ? ";
		List<Map<String, Object>> agentInfo = dao.find(sql, list.toArray());
		return agentInfo;
	}

	// 代理商扣费费率,来自agent_settle_fee表，（注意，不是之前的费率表）。
	public Map<String, Object> getAgent_agent_settle_fee(String agentNo) {
		List<Object> list = new ArrayList<Object>();
		list.add(agentNo);
		String sql = "select  s.* from agent_settle_fee s  where s.agent_no = ? ";
		Map<String, Object> agent_settle_fee_Info = dao.findFirst(sql,
				list.toArray());
		return agent_settle_fee_Info;
	}

	// 代理商明细
	public Map<String, Object> getAgentInfo(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = "select a.*  from  agent_info a  where a.id = ? ";
		Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
		return agentInfo;
	}

	// 代理商明细--含费率和分润。
	public Map<String, Object> getAgentInfoAndFeeAndFenrun(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = "select f.id fid,f.fee_type ffee_type, f.fee_rate ffee_rate  ,f.fee_cap_amount ffee_cap_amount,f.fee_max_amount ffee_max_amount ,f.fee_single_amount ffee_single_amount, f.ladder_fee fladder_fee,"
				+ "r.id rid,r.sharing_rule rsharing_rule,"
				+ "  a.*, "
				+ "s.id s_id,   s.live_a_type  , s.live_b_type , s.wholesale_pub_type ,"
				+ " s.wholesale_pri_cap_type ,   s.wholesale_pri_nocap_type , s.estate_car_type , s.general_type,s.general_b_type , s.catering_type ,s.smbox_type,s.ylst2_type,s.locked_status,s.checked_status,"
				+ "r.locked_status share_locked_status,r.checked_status share_checked_status "
				+ "from  agent_info a  left join   agent_fee f  on  a.agent_no=f.agent_no left join agent_share_rule r on    a.agent_no=r.agent_no  "
				+ " left join agent_settle_fee s on     a.agent_no=s.agent_no ";
		sql += " where 1=1 and  a.id =? and r.rule_type='POS' ";

		Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
		return agentInfo;
	}

	// 代理商明细--含费率和分润。
	public Map<String, Object> getSmboxShareRule(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = "select r.* from agent_share_rule r ,agent_info i where "
				+ "i.agent_no=r.agent_no and i.id=? and r.rule_type='SMBOX' ";

		Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
		return agentInfo;
	}
	
	// 代理商明细--含费率和分润。--移联商通1
		public Map<String, Object> getylst2ShareRule(Long id) {
			List<Object> list = new ArrayList<Object>();
			list.add(id);
			String sql = "select r.* from agent_share_rule r ,agent_info i where "
					+ "i.agent_no=r.agent_no and i.id=? and r.rule_type='ylst2' ";

			Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
			return agentInfo;
		}

	// 代理商是否是顶层代理商
	public Map<String, Object> isParent(Long id) {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = "select count(*) coutnAsParent from  agent_info a  where a.parent_id = ? ";
		Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
		return agentInfo;
	}

	// 代理商用户新增
	public int agentUserAdd(String userName, String defaultPassword,
			String realName, String agentNo, String createOperator)
			throws SQLException {
		String sql = "insert into agent_user(user_name,real_name,agent_no,status,password,default_password,create_operator,create_time,update_passw_time) values(?,?,?,?,?,?,?,now(),now())";

		Object[] params = { userName, realName, agentNo, "1",
				MD5.toMD5(defaultPassword), defaultPassword, createOperator };
		int n = dao.update(sql, params);

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return n;
	}

	/**
	 * 超级刷代理商新增方法
	 * 
	 * @author 王帅
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int agentInfoAddSPOS(Map<String, String> params) throws SQLException {
		log.info("AgentService agentInfoAddSPOS start ...");
		String parent_node = params.get("parent_node");
		String agentNo = params.get("agentNo");
		String agentName = params.get("agentName");
		String agentLinkName = params.get("agentLinkName");
		String agentLinkTel = params.get("agentLinkTel");
		String agentLinkMail = params.get("agentLinkMail");
		String agentArea = params.get("agentArea");
		String agentAddress = params.get("agentAddress");
		String saleName = params.get("saleName");
		String parentId = params.get("parentId");
		String managerLogo = params.get("managerLogo");
		String clientLogo = params.get("clientLogo");
		String agentPay = params.get("agentPay");
		String mixAmout = params.get("mixAmout");
		String agentRate = params.get("agentRate");

		String account_no = params.get("account_no");
		String bank_name = params.get("bank_name");
		String account_name = params.get("account_name");
		String cnaps_no = params.get("cnaps_no");
		String account_type = params.get("account_type");
		String agentAreaType = params.get("agentAreaType");
		String profitSharing = params.get("profitSharing");
		// 集群编号
		String group_code = params.get("group_code");

		// 如果集群编号为空，则使用默认集群
		group_code = (group_code == null) ? "1001" : group_code;

		// 是否钱包结算，默认为否（不做钱包结算）
		String bag_settle = params.get("bag_settle");

		// 是否投资相关字段处理
		String isInvest = params.get("is_invest");
		String investAmount = params.get("invest_amount");
		String commonDepositAmount = params.get("common_deposit_amount");
		String commonDepositRate = params.get("common_deposit_rate");
		String overDepositRate = params.get("over_deposit_rate");
		String depositRate = params.get("deposit_rate");
		
		commonDepositRate = StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointLeft(2).toString() : "";
		overDepositRate = StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointLeft(2).toString() : "";
		depositRate = StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointLeft(2).toString() : "";

		String insert_agent_info = "insert into agent_info"
				+ "(parent_id,parent_node,agent_no,agent_name,agent_link_name,agent_link_tel,agent_link_mail,agent_area,"
				+ "agent_address,sale_name,manager_logo,client_logo,create_time,agent_pay,mix_amout,agent_rate,"
				+ "agent_status,bank_name,account_no,account_name,cnaps_no,account_type,"
				+ "agent_area_type,profit_sharing,group_code,bag_settle,brand_type,is_invest,invest_amount,common_deposit_amount,common_deposit_rate,over_deposit_rate,deposit_rate) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?,'SPOS',?,?,?,?,?,?)";

		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(parentId == null || "".equals(parentId) ? "0"
				: parentId);

		listAgentInfo.add(parent_node);
		listAgentInfo.add(agentNo);
		listAgentInfo.add(agentName);
		listAgentInfo.add(agentLinkName);
		listAgentInfo.add(agentLinkTel);
		listAgentInfo.add(agentLinkMail);
		listAgentInfo.add(agentArea);
		listAgentInfo.add(agentAddress);
		listAgentInfo.add(saleName);
		if (StringUtils.isEmpty(managerLogo)) {
			managerLogo = SysConfig.value("agentaddress") + "/logo/1001.gif";
		} else {
			managerLogo = SysConfig.value("agentaddress")
					+ SysConfig.value("uploadsyslogo") + managerLogo;
		}
		listAgentInfo.add(managerLogo);
		if (StringUtils.isEmpty(clientLogo)) {
			clientLogo = SysConfig.value("agentaddress")
					+ "/logo/logo_eeepay.png";
		} else {
			clientLogo = SysConfig.value("agentaddress")
					+ SysConfig.value("uploadclientlogo") + clientLogo;
		}
		listAgentInfo.add(clientLogo);
		listAgentInfo.add(agentPay);
		listAgentInfo.add(mixAmout);
		listAgentInfo.add(agentRate);
		listAgentInfo.add(1); // 代理商状态 1 正常, 2 冻结

		listAgentInfo.add(bank_name);
		listAgentInfo.add(account_no);
		listAgentInfo.add(account_name);
		listAgentInfo.add(cnaps_no);
		listAgentInfo.add(account_type);
		listAgentInfo.add(agentAreaType);
		listAgentInfo.add(profitSharing);
		// 集群编号
		listAgentInfo.add(group_code);

		// 是否钱包结算
		listAgentInfo.add(bag_settle);
		
		// 是否投资相关字段处理
		listAgentInfo.add(isInvest);
		listAgentInfo.add(investAmount);
		listAgentInfo.add(commonDepositAmount);
		listAgentInfo.add(commonDepositRate);
		listAgentInfo.add(overDepositRate);
		listAgentInfo.add(depositRate);

		int affectedRows = dao.update(insert_agent_info,
				listAgentInfo.toArray());

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// 代理商新增成功
		if (affectedRows > 0) {
			String sql = "select id from agent_info where agent_no = ?";
			// 取代理商的ID
			Map map = dao.findFirstByWrite(sql, agentNo);

			try {
				if (map != null) {
					sql = "update agent_info set self_node = ? where agent_no = ?";
					String temp = parent_node + "[" + map.get("id") + "]";
					System.out.println("新增代理商成功，编号为：" + agentNo + "代理商上级节点："
							+ parent_node + "；自身节点：" + temp);
					// 更新代理商的自身节点信息
					dao.update(sql, new Object[] { temp, agentNo });
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 添加分润信息

		String fr_1_mix = params.get("fr_1_mix");
		String fr_1_fee = params.get("fr_1_fee");
		String fr_1_max = params.get("fr_1_max");

		String fr_2_mix = params.get("fr_2_mix");
		String fr_2_fee = params.get("fr_2_fee");
		String fr_2_max = params.get("fr_2_max");

		String fr_3_mix = params.get("fr_3_mix");
		String fr_3_fee = params.get("fr_3_fee");
		String fr_3_max = params.get("fr_3_max");

		String fr_4_mix = params.get("fr_4_mix");
		String fr_4_fee = params.get("fr_4_fee");
		String fr_4_max = params.get("fr_4_max");

		fr_1_fee = new BigDecimal(fr_1_fee).movePointLeft(2).toString();
		fr_2_fee = new BigDecimal(fr_2_fee).movePointLeft(2).toString();
		fr_3_fee = new BigDecimal(fr_3_fee).movePointLeft(2).toString();
		fr_4_fee = new BigDecimal(fr_4_fee).movePointLeft(2).toString();

		String fr = fr_1_mix + "~" + fr_1_fee + "~" + fr_1_max;
		fr = fr + "|" + fr_2_mix + "~" + fr_2_fee + "~" + fr_2_max;
		fr = fr + "|" + fr_3_mix + "~" + fr_3_fee + "~" + fr_3_max;
		fr = fr + "|" + fr_4_mix + "~" + fr_4_fee + "~" + fr_4_max;

		String insert_pos_merchant_trans_rule = "insert into agent_share_rule "
				+ "(agent_no, sharing_rule,rule_type,checked_status,locked_status)"
				+ " values" + "(?,?,'POS',?,?)";

		List<Object> listPosMerchantTransRule = new ArrayList<Object>();
		listPosMerchantTransRule.add(agentNo);
		listPosMerchantTransRule.add(fr);
		listPosMerchantTransRule.add("0");
		listPosMerchantTransRule.add("0");

		dao.update(insert_pos_merchant_trans_rule,
				listPosMerchantTransRule.toArray());

		// end---------------------------

		// 添加小宝分润信息

		String smfr_1_mix = params.get("smfr_1_mix");
		String smfr_1_fee = params.get("smfr_1_fee");
		String smfr_1_max = params.get("smfr_1_max");

		String smfr_2_mix = params.get("smfr_2_mix");
		String smfr_2_fee = params.get("smfr_2_fee");
		String smfr_2_max = params.get("smfr_2_max");

		String smfr_3_mix = params.get("smfr_3_mix");
		String smfr_3_fee = params.get("smfr_3_fee");
		String smfr_3_max = params.get("smfr_3_max");

		String smfr_4_mix = params.get("smfr_4_mix");
		String smfr_4_fee = params.get("smfr_4_fee");
		String smfr_4_max = params.get("smfr_4_max");

		smfr_1_fee = new BigDecimal(smfr_1_fee).movePointLeft(2).toString();
		smfr_2_fee = new BigDecimal(smfr_2_fee).movePointLeft(2).toString();
		smfr_3_fee = new BigDecimal(smfr_3_fee).movePointLeft(2).toString();
		smfr_4_fee = new BigDecimal(smfr_4_fee).movePointLeft(2).toString();

		String smfr = smfr_1_mix + "~" + smfr_1_fee + "~" + smfr_1_max;
		smfr = smfr + "|" + smfr_2_mix + "~" + smfr_2_fee + "~" + smfr_2_max;
		smfr = smfr + "|" + smfr_3_mix + "~" + smfr_3_fee + "~" + smfr_3_max;
		smfr = smfr + "|" + smfr_4_mix + "~" + smfr_4_fee + "~" + smfr_4_max;

		String smbox_trans_rule = "insert into agent_share_rule "
				+ "(agent_no, sharing_rule,rule_type,checked_status,locked_status)"
				+ " values" + "(?,?,'SMBOX',?,?)";

		List<Object> smboxTransRule = new ArrayList<Object>();
		smboxTransRule.add(agentNo);
		smboxTransRule.add(smfr);
		smboxTransRule.add("0");
		smboxTransRule.add("0");

		dao.update(smbox_trans_rule, smboxTransRule.toArray());
		
		// 添加移联商通润信息
		String ylst_1_mix = params.get("ylst_1_mix");
		String ylst_1_fee = params.get("ylst_1_fee");
		String ylst_1_max = params.get("ylst_1_max");

		String ylst_2_mix = params.get("ylst_2_mix");
		String ylst_2_fee = params.get("ylst_2_fee");
		String ylst_2_max = params.get("ylst_2_max");

		String ylst_3_mix = params.get("ylst_3_mix");
		String ylst_3_fee = params.get("ylst_3_fee");
		String ylst_3_max = params.get("ylst_3_max");

		String ylst_4_mix = params.get("ylst_4_mix");
		String ylst_4_fee = params.get("ylst_4_fee");
		String ylst_4_max = params.get("ylst_4_max");

		ylst_1_fee = new BigDecimal(ylst_1_fee).movePointLeft(2).toString();
		ylst_2_fee = new BigDecimal(ylst_2_fee).movePointLeft(2).toString();
		ylst_3_fee = new BigDecimal(ylst_3_fee).movePointLeft(2).toString();
		ylst_4_fee = new BigDecimal(ylst_4_fee).movePointLeft(2).toString();

		String ylst = ylst_1_mix + "~" + ylst_1_fee + "~" + ylst_1_max;
		ylst = ylst + "|" + ylst_2_mix + "~" + ylst_2_fee + "~" + ylst_2_max;
		ylst = ylst + "|" + ylst_3_mix + "~" + ylst_3_fee + "~" + ylst_3_max;
		ylst = ylst + "|" + ylst_4_mix + "~" + ylst_4_fee + "~" + ylst_4_max;

		String ylst_trans_rule = "insert into agent_share_rule "
				+ "(agent_no, sharing_rule,rule_type,checked_status,locked_status)"
				+ " values" + "(?,?,'ylst2',?,?)";

		List<Object> ylstTransRule = new ArrayList<Object>();
		ylstTransRule.add(agentNo);
		ylstTransRule.add(ylst);
		ylstTransRule.add("0");
		ylstTransRule.add("0");

		dao.update(ylst_trans_rule, ylstTransRule.toArray());

		// 代理商行业计算成本

		String live_a_type = params.get("live_a_type");
		String live_b_type = params.get("live_b_type");
		String wholesale_pub_type = params.get("wholesale_pub_type");
		String wholesale_pri_cap_type = params.get("wholesale_pri_cap_type");
		String wholesale_pri_nocap_type = params
				.get("wholesale_pri_nocap_type");
		String estate_car_type = params.get("estate_car_type");
		String general_type = params.get("general_type");
		String general_b_type = params.get("general_b_type");
		String catering_type = params.get("catering_type");
		String smbox_type = params.get("smbox_type");

		String insert_agent_settle_fee = "insert  agent_settle_fee   (  agent_no, live_a_type, live_b_type, wholesale_pub_type, "
				+ " wholesale_pri_cap_type, wholesale_pri_nocap_type, estate_car_type, general_type, general_b_type, catering_type,smbox_type,checked_status,locked_status)"
				+ " values  (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		List<Object> listParams_agent_settle_fee = new ArrayList<Object>();
		listParams_agent_settle_fee.add(agentNo);
		listParams_agent_settle_fee.add(live_a_type);
		listParams_agent_settle_fee.add(live_b_type);
		listParams_agent_settle_fee.add(wholesale_pub_type);
		listParams_agent_settle_fee.add(wholesale_pri_cap_type);
		listParams_agent_settle_fee.add(wholesale_pri_nocap_type);
		listParams_agent_settle_fee.add(estate_car_type);
		listParams_agent_settle_fee.add(general_type);
		listParams_agent_settle_fee.add(general_b_type);
		listParams_agent_settle_fee.add(catering_type);
		listParams_agent_settle_fee.add(smbox_type);
		listParams_agent_settle_fee.add("0");
		listParams_agent_settle_fee.add("0");

		dao.update(insert_agent_settle_fee,
				listParams_agent_settle_fee.toArray());

		// 代理商行业计算成本 -----------end---------------------------

		log.info("AgentService agentInfoAddSPOS end");
		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商添加 submit
	/**
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int agentInfoAdd(Map<String, String> params) throws SQLException {

		String parent_node = params.get("parent_node");

		String agentNo = params.get("agentNo");
		String agentName = params.get("agentName");
		String agentLinkName = params.get("agentLinkName");
		String agentLinkTel = params.get("agentLinkTel");
		String agentLinkMail = params.get("agentLinkMail");
		String agentArea = params.get("agentArea");
		String agentAddress = params.get("agentAddress");
		String saleName = params.get("saleName");
		String parentId = params.get("parentId");
		String managerLogo = params.get("managerLogo");
		String clientLogo = params.get("clientLogo");
		String agentPay = params.get("agentPay");
		String mixAmout = params.get("mixAmout");
		String agentRate = params.get("agentRate");

		String account_no = params.get("account_no");
		String bank_name = params.get("bank_name");
		String account_name = params.get("account_name");
		String cnaps_no = params.get("cnaps_no");
		String account_type = params.get("account_type");
		String agentAreaType = params.get("agentAreaType");
		String profitSharing = params.get("profitSharing");
		// 集群编号
		String group_code = params.get("group_code");

		// 如果集群编号为空，则使用默认集群
		group_code = (group_code == null) ? "1001" : group_code;

		// 是否钱包结算，默认为否（不做钱包结算）
		String bag_settle = params.get("bag_settle");

		// 是否投资相关字段处理
		String isInvest = params.get("is_invest");
		String investAmount = params.get("invest_amount");
		String commonDepositAmount = params.get("common_deposit_amount");
		String commonDepositRate = params.get("common_deposit_rate");
		String overDepositRate = params.get("over_deposit_rate");
		String depositRate = params.get("deposit_rate");
		
		commonDepositRate = StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointLeft(2).toString() : "";
		overDepositRate = StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointLeft(2).toString() : "";
		depositRate = StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointLeft(2).toString() : "";
		
		String insert_agent_info = "insert into agent_info"
				+ "(parent_id,parent_node,agent_no,agent_name,agent_link_name,agent_link_tel,agent_link_mail,agent_area,"
				+ "agent_address,sale_name,manager_logo,client_logo,create_time,agent_pay,mix_amout,agent_rate,"
				+ "agent_status,bank_name,account_no,account_name,cnaps_no,account_type,"
				+ "agent_area_type,profit_sharing,group_code,bag_settle,is_invest,invest_amount,common_deposit_amount,common_deposit_rate,over_deposit_rate,deposit_rate) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(parentId == null || "".equals(parentId) ? "0"
				: parentId);

		listAgentInfo.add(parent_node);
		listAgentInfo.add(agentNo);
		listAgentInfo.add(agentName);
		listAgentInfo.add(agentLinkName);
		listAgentInfo.add(agentLinkTel);
		listAgentInfo.add(agentLinkMail);
		listAgentInfo.add(agentArea);
		listAgentInfo.add(agentAddress);
		listAgentInfo.add(saleName);
		if (StringUtils.isEmpty(managerLogo)) {
			managerLogo = SysConfig.value("agentaddress") + "/logo/1001.gif";
		} else {
			managerLogo = SysConfig.value("agentaddress")
					+ SysConfig.value("uploadsyslogo") + managerLogo;
		}
		listAgentInfo.add(managerLogo);
		if (StringUtils.isEmpty(clientLogo)) {
			clientLogo = SysConfig.value("agentaddress")
					+ "/logo/logo_eeepay.png";
		} else {
			clientLogo = SysConfig.value("agentaddress")
					+ SysConfig.value("uploadclientlogo") + clientLogo;
		}
		listAgentInfo.add(clientLogo);
		listAgentInfo.add(agentPay);
		listAgentInfo.add(mixAmout);
		listAgentInfo.add(agentRate);
		listAgentInfo.add(1); // 代理商状态 1 正常, 2 冻结

		listAgentInfo.add(bank_name);
		listAgentInfo.add(account_no);
		listAgentInfo.add(account_name);
		listAgentInfo.add(cnaps_no);
		listAgentInfo.add(account_type);
		listAgentInfo.add(agentAreaType);
		listAgentInfo.add(profitSharing);
		// 集群编号
		listAgentInfo.add(group_code);

		// 是否钱包结算
		listAgentInfo.add(bag_settle);
		
		// 是否投资相关字段处理
		listAgentInfo.add(isInvest);
		listAgentInfo.add(investAmount);
		listAgentInfo.add(commonDepositAmount);
		listAgentInfo.add(commonDepositRate);
		listAgentInfo.add(overDepositRate);
		listAgentInfo.add(depositRate);

		int affectedRows = dao.update(insert_agent_info,
				listAgentInfo.toArray());

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// 代理商新增成功
		if (affectedRows > 0) {
			String sql = "select id from agent_info where agent_no = ?";
			// 取代理商的ID
			Map map = dao.findFirstByWrite(sql, agentNo);

			try {
				if (map != null) {
					sql = "update agent_info set self_node = ? where agent_no = ?";
					String temp = parent_node + "[" + map.get("id") + "]";
					System.out.println("新增代理商成功，编号为：" + agentNo + "代理商上级节点："
							+ parent_node + "；自身节点：" + temp);
					// 更新代理商的自身节点信息
					dao.update(sql, new Object[] { temp, agentNo });
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 添加分润信息

		String fr_1_mix = params.get("fr_1_mix");
		String fr_1_fee = params.get("fr_1_fee");
		String fr_1_max = params.get("fr_1_max");

		String fr_2_mix = params.get("fr_2_mix");
		String fr_2_fee = params.get("fr_2_fee");
		String fr_2_max = params.get("fr_2_max");

		String fr_3_mix = params.get("fr_3_mix");
		String fr_3_fee = params.get("fr_3_fee");
		String fr_3_max = params.get("fr_3_max");

		String fr_4_mix = params.get("fr_4_mix");
		String fr_4_fee = params.get("fr_4_fee");
		String fr_4_max = params.get("fr_4_max");

		fr_1_fee = new BigDecimal(fr_1_fee).movePointLeft(2).toString();
		fr_2_fee = new BigDecimal(fr_2_fee).movePointLeft(2).toString();
		fr_3_fee = new BigDecimal(fr_3_fee).movePointLeft(2).toString();
		fr_4_fee = new BigDecimal(fr_4_fee).movePointLeft(2).toString();

		String fr = fr_1_mix + "~" + fr_1_fee + "~" + fr_1_max;
		fr = fr + "|" + fr_2_mix + "~" + fr_2_fee + "~" + fr_2_max;
		fr = fr + "|" + fr_3_mix + "~" + fr_3_fee + "~" + fr_3_max;
		fr = fr + "|" + fr_4_mix + "~" + fr_4_fee + "~" + fr_4_max;

		String insert_pos_merchant_trans_rule = "insert into agent_share_rule "
				+ "(agent_no, sharing_rule,rule_type,checked_status,locked_status)"
				+ " values" + "(?,?,'POS',?,?)";

		List<Object> listPosMerchantTransRule = new ArrayList<Object>();
		listPosMerchantTransRule.add(agentNo);
		listPosMerchantTransRule.add(fr);
		listPosMerchantTransRule.add("0");
		listPosMerchantTransRule.add("0");

		dao.update(insert_pos_merchant_trans_rule,
				listPosMerchantTransRule.toArray());

		// end---------------------------

		// 添加小宝分润信息

		String smfr_1_mix = params.get("smfr_1_mix");
		String smfr_1_fee = params.get("smfr_1_fee");
		String smfr_1_max = params.get("smfr_1_max");

		String smfr_2_mix = params.get("smfr_2_mix");
		String smfr_2_fee = params.get("smfr_2_fee");
		String smfr_2_max = params.get("smfr_2_max");

		String smfr_3_mix = params.get("smfr_3_mix");
		String smfr_3_fee = params.get("smfr_3_fee");
		String smfr_3_max = params.get("smfr_3_max");

		String smfr_4_mix = params.get("smfr_4_mix");
		String smfr_4_fee = params.get("smfr_4_fee");
		String smfr_4_max = params.get("smfr_4_max");

		smfr_1_fee = new BigDecimal(smfr_1_fee).movePointLeft(2).toString();
		smfr_2_fee = new BigDecimal(smfr_2_fee).movePointLeft(2).toString();
		smfr_3_fee = new BigDecimal(smfr_3_fee).movePointLeft(2).toString();
		smfr_4_fee = new BigDecimal(smfr_4_fee).movePointLeft(2).toString();

		String smfr = smfr_1_mix + "~" + smfr_1_fee + "~" + smfr_1_max;
		smfr = smfr + "|" + smfr_2_mix + "~" + smfr_2_fee + "~" + smfr_2_max;
		smfr = smfr + "|" + smfr_3_mix + "~" + smfr_3_fee + "~" + smfr_3_max;
		smfr = smfr + "|" + smfr_4_mix + "~" + smfr_4_fee + "~" + smfr_4_max;

		String smbox_trans_rule = "insert into agent_share_rule "
				+ "(agent_no, sharing_rule,rule_type,checked_status,locked_status)"
				+ " values" + "(?,?,'SMBOX',?,?)";

		List<Object> smboxTransRule = new ArrayList<Object>();
		smboxTransRule.add(agentNo);
		smboxTransRule.add(smfr);
		smboxTransRule.add("0");
		smboxTransRule.add("0");

		dao.update(smbox_trans_rule, smboxTransRule.toArray());
		
		// 添加移联商通润信息
		String ylst_1_mix = params.get("ylst_1_mix");
		String ylst_1_fee = params.get("ylst_1_fee");
		String ylst_1_max = params.get("ylst_1_max");

		String ylst_2_mix = params.get("ylst_2_mix");
		String ylst_2_fee = params.get("ylst_2_fee");
		String ylst_2_max = params.get("ylst_2_max");

		String ylst_3_mix = params.get("ylst_3_mix");
		String ylst_3_fee = params.get("ylst_3_fee");
		String ylst_3_max = params.get("ylst_3_max");

		String ylst_4_mix = params.get("ylst_4_mix");
		String ylst_4_fee = params.get("ylst_4_fee");
		String ylst_4_max = params.get("ylst_4_max");

		ylst_1_fee = new BigDecimal(ylst_1_fee).movePointLeft(2).toString();
		ylst_2_fee = new BigDecimal(ylst_2_fee).movePointLeft(2).toString();
		ylst_3_fee = new BigDecimal(ylst_3_fee).movePointLeft(2).toString();
		ylst_4_fee = new BigDecimal(ylst_4_fee).movePointLeft(2).toString();

		String ylst = ylst_1_mix + "~" + ylst_1_fee + "~" + ylst_1_max;
		ylst = ylst + "|" + ylst_2_mix + "~" + ylst_2_fee + "~" + ylst_2_max;
		ylst = ylst + "|" + ylst_3_mix + "~" + ylst_3_fee + "~" + ylst_3_max;
		ylst = ylst + "|" + ylst_4_mix + "~" + ylst_4_fee + "~" + ylst_4_max;

		String ylst_trans_rule = "insert into agent_share_rule "
				+ "(agent_no, sharing_rule,rule_type,checked_status,locked_status)"
				+ " values" + "(?,?,'ylst2',?,?)";

		List<Object> ylstTransRule = new ArrayList<Object>();
		ylstTransRule.add(agentNo);
		ylstTransRule.add(ylst);
		ylstTransRule.add("0");
		ylstTransRule.add("0");

		dao.update(ylst_trans_rule, ylstTransRule.toArray());

		// 代理商行业计算成本

		String live_a_type = params.get("live_a_type");
		String live_b_type = params.get("live_b_type");
		String wholesale_pub_type = params.get("wholesale_pub_type");
		String wholesale_pri_cap_type = params.get("wholesale_pri_cap_type");
		String wholesale_pri_nocap_type = params
				.get("wholesale_pri_nocap_type");
		String estate_car_type = params.get("estate_car_type");
		String general_type = params.get("general_type");
		String general_b_type = params.get("general_b_type");
		String catering_type = params.get("catering_type");
		String smbox_type = params.get("smbox_type");
		String ylst2_type = params.get("ylst2_type");

		String insert_agent_settle_fee = "insert  agent_settle_fee   (  agent_no, live_a_type, live_b_type, wholesale_pub_type, "
				+ " wholesale_pri_cap_type, wholesale_pri_nocap_type, estate_car_type, general_type, general_b_type, catering_type,smbox_type,ylst2_type,checked_status,locked_status)"
				+ " values  (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		List<Object> listParams_agent_settle_fee = new ArrayList<Object>();
		listParams_agent_settle_fee.add(agentNo);
		listParams_agent_settle_fee.add(live_a_type);
		listParams_agent_settle_fee.add(live_b_type);
		listParams_agent_settle_fee.add(wholesale_pub_type);
		listParams_agent_settle_fee.add(wholesale_pri_cap_type);
		listParams_agent_settle_fee.add(wholesale_pri_nocap_type);
		listParams_agent_settle_fee.add(estate_car_type);
		listParams_agent_settle_fee.add(general_type);
		listParams_agent_settle_fee.add(general_b_type);
		listParams_agent_settle_fee.add(catering_type);
		listParams_agent_settle_fee.add(smbox_type);
		listParams_agent_settle_fee.add(ylst2_type);
		listParams_agent_settle_fee.add("0");
		listParams_agent_settle_fee.add("0");

		dao.update(insert_agent_settle_fee,
				listParams_agent_settle_fee.toArray());

		// 代理商行业计算成本 -----------end---------------------------

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商修改 submit
	public int agentInfoUpdate(Map<String, String> params) throws SQLException {

		String agentNo = params.get("agentNo");
		String agentName = params.get("agentName");
		String agentLinkName = params.get("agentLinkName");
		String agentLinkTel = params.get("agentLinkTel");
		String agentLinkMail = params.get("agentLinkMail");
		String agentArea = params.get("agentArea");
		String agentAddress = params.get("agentAddress");
		String saleName = params.get("saleName");
		String managerLogo = params.get("managerLogo");
		String clientLogo = params.get("clientLogo");
		String agentPay = params.get("agentPay");
		String mixAmout = params.get("mixAmout");
		String agentRate = params.get("agentRate");
		String agentStatus = params.get("agentStatus");
		String account_no = params.get("account_no");
		String bank_name = params.get("bank_name");
		String account_name = params.get("account_name");
		String cnaps_no = params.get("cnaps_no");
		String account_type = params.get("account_type");
		String agentAreaType = params.get("agentAreaType");
		String profitSharing = params.get("profitSharing");
		
		// 是否投资相关字段处理
		String isInvest = params.get("is_invest");
		String investAmount = params.get("invest_amount");
		String commonDepositAmount = params.get("common_deposit_amount");
		String commonDepositRate = params.get("common_deposit_rate");
		String overDepositRate = params.get("over_deposit_rate");
		String depositRate = params.get("deposit_rate");
		
		commonDepositRate = StringUtils.isNotBlank(commonDepositRate) ? new BigDecimal(commonDepositRate).movePointLeft(2).toString() : "";
		overDepositRate = StringUtils.isNotBlank(overDepositRate) ? new BigDecimal(overDepositRate).movePointLeft(2).toString() : "";
		depositRate = StringUtils.isNotBlank(depositRate) ? new BigDecimal(depositRate).movePointLeft(2).toString() : "";

		// 是否钱包结算
		String bag_settle = params.get("bag_settle");

		if (agentPay == null || "".equals(agentPay)) {
			agentPay = "0";
		}
		if (mixAmout == null || "".equals(mixAmout)) {
			mixAmout = "0";
		}
		if (agentRate == null || "".equals(agentRate)) {
			agentRate = "0";
		} else {
			agentRate = (new BigDecimal(agentRate).movePointLeft(2)).toString();
		}

		String update_agent_info = "update agent_info set  agent_name=? ,agent_link_name=? , agent_link_tel=? , agent_link_mail=? ,"
				+ "agent_area=? ,agent_address=?, sale_name=?, agent_pay=?,mix_amout=? ,"
				+ "agent_rate=? ,agent_status=?,bank_name=?,account_no=?,account_name=?,"
				+ "cnaps_no=?,account_type=?,agent_area_type=?,profit_sharing=?,manager_logo=?,"
				+ "client_logo=?,group_code=?,bag_settle=?,"
				+ "is_invest=?,invest_amount=?,common_deposit_amount=?,common_deposit_rate=?,over_deposit_rate=?,deposit_rate=? "
				+ " where agent_no =? ";

		List<Object> listAgentInfo = new ArrayList<Object>();

		listAgentInfo.add(agentName);
		listAgentInfo.add(agentLinkName);
		listAgentInfo.add(agentLinkTel);
		listAgentInfo.add(agentLinkMail);
		listAgentInfo.add(agentArea);
		listAgentInfo.add(agentAddress);
		listAgentInfo.add(saleName);
		listAgentInfo.add(agentPay);
		listAgentInfo.add(mixAmout);
		listAgentInfo.add(agentRate);
		listAgentInfo.add(agentStatus);

		listAgentInfo.add(bank_name);
		listAgentInfo.add(account_no);
		listAgentInfo.add(account_name);
		listAgentInfo.add(cnaps_no);
		listAgentInfo.add(account_type);
		listAgentInfo.add(agentAreaType);
		listAgentInfo.add(profitSharing);
		if (StringUtils.isEmpty(managerLogo)) {
			managerLogo = SysConfig.value("agentaddress") + "/logo/1001.gif";
		}
		listAgentInfo.add(managerLogo);
		if (StringUtils.isEmpty(clientLogo)) {
			clientLogo = SysConfig.value("agentaddress")
					+ "/logo/logo_eeepay.png";
		}
		listAgentInfo.add(clientLogo);
		// 集群编号
		String group_code = "" + params.get("group_code");
		listAgentInfo.add(group_code);
		// 是否钱包结算
		listAgentInfo.add(bag_settle);
		
		// 是否投资相关字段处理
		listAgentInfo.add(isInvest);
		listAgentInfo.add(investAmount);
		listAgentInfo.add(commonDepositAmount);
		listAgentInfo.add(commonDepositRate);
		listAgentInfo.add(overDepositRate);
		listAgentInfo.add(depositRate);

		listAgentInfo.add(agentNo);

		int affectedRows = 0;
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			affectedRows = dao.updateByTranscation(update_agent_info,
					listAgentInfo.toArray(), conn);
			
			addAgentLog(listAgentInfo, conn);
			
			// try {
			// cleanCacheService.toCleancache();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			// 代理商用户名称
			String update_agent_user = "update  agent_user set real_name=? where agent_no=?";
			List<Object> listAgentUser = new ArrayList<Object>();
			listAgentUser.add(agentName);
			listAgentUser.add(agentNo);
			dao.updateByTranscation(update_agent_user, listAgentUser.toArray(),
					conn);

			// 添加分润信息------ -------start
			String share_locked_status = params.get("share_locked_status");// 分润比例锁定状态
			if (!"1".equals(share_locked_status)) {
				String fr_1_mix = params.get("fr_1_mix");
				String fr_1_fee = params.get("fr_1_fee");
				String fr_1_max = params.get("fr_1_max");

				String fr_2_mix = params.get("fr_2_mix");
				String fr_2_fee = params.get("fr_2_fee");
				String fr_2_max = params.get("fr_2_max");

				String fr_3_mix = params.get("fr_3_mix");
				String fr_3_fee = params.get("fr_3_fee");
				String fr_3_max = params.get("fr_3_max");

				String fr_4_mix = params.get("fr_4_mix");
				String fr_4_fee = params.get("fr_4_fee");
				String fr_4_max = params.get("fr_4_max");

				fr_1_fee = new BigDecimal(fr_1_fee).movePointLeft(2).toString();
				fr_2_fee = new BigDecimal(fr_2_fee).movePointLeft(2).toString();
				fr_3_fee = new BigDecimal(fr_3_fee).movePointLeft(2).toString();
				fr_4_fee = new BigDecimal(fr_4_fee).movePointLeft(2).toString();

				String fr = fr_1_mix + "~" + fr_1_fee + "~" + fr_1_max;
				fr = fr + "|" + fr_2_mix + "~" + fr_2_fee + "~" + fr_2_max;
				fr = fr + "|" + fr_3_mix + "~" + fr_3_fee + "~" + fr_3_max;
				fr = fr + "|" + fr_4_mix + "~" + fr_4_fee + "~" + fr_4_max;

				String insert_pos_merchant_trans_rule = "update  agent_share_rule "
						+ "set   sharing_rule=?,checked_status='0' where agent_no=? and rule_type='POS' ";

				List<Object> listPosMerchantTransRule = new ArrayList<Object>();
				listPosMerchantTransRule.add(fr);
				listPosMerchantTransRule.add(agentNo);

				dao.updateByTranscation(insert_pos_merchant_trans_rule,
						listPosMerchantTransRule.toArray(), conn);
				addAgentShareLog(fr, agentNo, "POS", conn);

				// 添加分润信息----------- end---------------------------

				// 添加分润信息------ 移小宝 -------start

				String smfr_1_mix = params.get("smfr_1_mix");
				String smfr_1_fee = params.get("smfr_1_fee");
				String smfr_1_max = params.get("smfr_1_max");

				String smfr_2_mix = params.get("smfr_2_mix");
				String smfr_2_fee = params.get("smfr_2_fee");
				String smfr_2_max = params.get("smfr_2_max");

				String smfr_3_mix = params.get("smfr_3_mix");
				String smfr_3_fee = params.get("smfr_3_fee");
				String smfr_3_max = params.get("smfr_3_max");

				String smfr_4_mix = params.get("smfr_4_mix");
				String smfr_4_fee = params.get("smfr_4_fee");
				String smfr_4_max = params.get("smfr_4_max");

				smfr_1_fee = new BigDecimal(smfr_1_fee).movePointLeft(2)
						.toString();
				smfr_2_fee = new BigDecimal(smfr_2_fee).movePointLeft(2)
						.toString();
				smfr_3_fee = new BigDecimal(smfr_3_fee).movePointLeft(2)
						.toString();
				smfr_4_fee = new BigDecimal(smfr_4_fee).movePointLeft(2)
						.toString();

				String smfr = smfr_1_mix + "~" + smfr_1_fee + "~" + smfr_1_max;
				smfr = smfr + "|" + smfr_2_mix + "~" + smfr_2_fee + "~"
						+ smfr_2_max;
				smfr = smfr + "|" + smfr_3_mix + "~" + smfr_3_fee + "~"
						+ smfr_3_max;
				smfr = smfr + "|" + smfr_4_mix + "~" + smfr_4_fee + "~"
						+ smfr_4_max;

				String update_smbox_share_rule = "update  agent_share_rule "
						+ "set   sharing_rule=? where agent_no=? and rule_type='SMBOX' ";

				List<Object> list_smbox_share_rule = new ArrayList<Object>();
				list_smbox_share_rule.add(smfr);
				list_smbox_share_rule.add(agentNo);

				dao.updateByTranscation(update_smbox_share_rule,
						list_smbox_share_rule.toArray(), conn);
				addAgentShareLog(smfr, agentNo, "SMBOX", conn);
				
				
				// 添加分润信息------ 移联商通1 -------start

				String ylst_1_mix = params.get("ylst_1_mix");
				String ylst_1_fee = params.get("ylst_1_fee");
				String ylst_1_max = params.get("ylst_1_max");

				String ylst_2_mix = params.get("ylst_2_mix");
				String ylst_2_fee = params.get("ylst_2_fee");
				String ylst_2_max = params.get("ylst_2_max");

				String ylst_3_mix = params.get("ylst_3_mix");
				String ylst_3_fee = params.get("ylst_3_fee");
				String ylst_3_max = params.get("ylst_3_max");

				String ylst_4_mix = params.get("ylst_4_mix");
				String ylst_4_fee = params.get("ylst_4_fee");
				String ylst_4_max = params.get("ylst_4_max");

				ylst_1_fee = new BigDecimal(ylst_1_fee).movePointLeft(2)
						.toString();
				ylst_2_fee = new BigDecimal(ylst_2_fee).movePointLeft(2)
						.toString();
				ylst_3_fee = new BigDecimal(ylst_3_fee).movePointLeft(2)
						.toString();
				ylst_4_fee = new BigDecimal(ylst_4_fee).movePointLeft(2)
						.toString();

				String ylst = ylst_1_mix + "~" + ylst_1_fee + "~" + ylst_1_max;
				ylst = ylst + "|" + ylst_2_mix + "~" + ylst_2_fee + "~"
						+ ylst_2_max;
				ylst = ylst + "|" + ylst_3_mix + "~" + ylst_3_fee + "~"
						+ ylst_3_max;
				ylst = ylst + "|" + ylst_4_mix + "~" + ylst_4_fee + "~"
						+ ylst_4_max;

				String update_ylst_share_rule = "update  agent_share_rule "
						+ "set   sharing_rule=? where agent_no=? and rule_type='ylst2' ";

				List<Object> list_ylst_share_rule = new ArrayList<Object>();
				list_ylst_share_rule.add(ylst);
				list_ylst_share_rule.add(agentNo);

				dao.updateByTranscation(update_ylst_share_rule,
						list_ylst_share_rule.toArray(), conn);
				addAgentShareLog(ylst, agentNo, "ylst2", conn);
				
			}
			// 添加分润信息----------- end---------------------------

			// 代理商行业计算成本---------start新增功能，未锁定状态才可修改一下语句
			String locked_status = params.get("locked_status");// 锁定状态
			if (!"1".equals(locked_status)) {
				String live_a_type = params.get("live_a_type");
				String live_b_type = params.get("live_b_type");
				String wholesale_pub_type = params.get("wholesale_pub_type");
				String wholesale_pri_cap_type = params
						.get("wholesale_pri_cap_type");
				String wholesale_pri_nocap_type = params
						.get("wholesale_pri_nocap_type");
				String estate_car_type = params.get("estate_car_type");
				String general_type = params.get("general_type");
				String general_b_type = params.get("general_b_type");
				String catering_type = params.get("catering_type");
				String smbox_type = params.get("smbox_type");
				String ylst2_type = params.get("ylst2_type");
				String insert_agent_settle_fee = "update  agent_settle_fee "
						+ "set   live_a_type=?,   live_b_type=?,   wholesale_pub_type=?,   wholesale_pri_cap_type=?, "
						+ "  wholesale_pri_nocap_type=?,   estate_car_type=?,   general_type=?, general_b_type=?,  catering_type=? ,smbox_type=? ,ylst2_type=?,checked_status=? "
						+ " where agent_no=?  ";

				List<Object> listParams_agent_settle_fee = new ArrayList<Object>();
				listParams_agent_settle_fee.add(live_a_type);
				listParams_agent_settle_fee.add(live_b_type);
				listParams_agent_settle_fee.add(wholesale_pub_type);
				listParams_agent_settle_fee.add(wholesale_pri_cap_type);
				listParams_agent_settle_fee.add(wholesale_pri_nocap_type);
				listParams_agent_settle_fee.add(estate_car_type);
				listParams_agent_settle_fee.add(general_type);
				listParams_agent_settle_fee.add(general_b_type);
				listParams_agent_settle_fee.add(catering_type);
				listParams_agent_settle_fee.add(smbox_type);
				listParams_agent_settle_fee.add(ylst2_type);
				listParams_agent_settle_fee.add("0");
				listParams_agent_settle_fee.add(agentNo);

				dao.updateByTranscation(insert_agent_settle_fee,
						listParams_agent_settle_fee.toArray(), conn);
				addAgentFeeLog(params, conn);
			}
			// 代理商行业计算成本 -----------end---------------------------

			conn.commit();// 提交事务

		} catch (Exception e) {
			conn.rollback();// 回滚事务
			throw new RuntimeException();
		} finally {
			conn.close();// 关闭连接
		}

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 增加代理商扣率修改日志
	public void addAgentFeeLog(Map<String, String> params, Connection conn)
			throws SQLException {
		List<Object> list = new ArrayList<Object>();
		String agentNo = params.get("agentNo");
		String live_a_type = params.get("live_a_type");
		String live_b_type = params.get("live_b_type");
		String wholesale_pub_type = params.get("wholesale_pub_type");
		String wholesale_pri_cap_type = params.get("wholesale_pri_cap_type");
		String wholesale_pri_nocap_type = params
				.get("wholesale_pri_nocap_type");
		String estate_car_type = params.get("estate_car_type");
		String general_type = params.get("general_type");
		String general_b_type = params.get("general_b_type");
		String catering_type = params.get("catering_type");
		String smbox_type = params.get("smbox_type");
		String ylst2_type = params.get("ylst2_type");
		list.add(agentNo);
		list.add(live_a_type);
		list.add(live_b_type);
		list.add(wholesale_pub_type);
		list.add(wholesale_pri_cap_type);
		list.add(wholesale_pri_nocap_type);
		list.add(estate_car_type);
		list.add(general_type);
		list.add(general_b_type);
		list.add(catering_type);
		list.add(smbox_type);
		list.add(ylst2_type);
		// 操作员信息
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(bu.getId());
		list.add(bu.getRealName());

		String sq = "INSERT INTO agent_settle_fee_log (agent_no, live_a_type, live_b_type, wholesale_pub_type, wholesale_pri_cap_type, "
				+ "wholesale_pri_nocap_type, estate_car_type, general_type, catering_type, general_b_type, smbox_type, ylst2_type,oper_user_id, oper_user_name, create_time) VALUES"
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())";

		dao.updateByTranscation(sq, list.toArray(), conn);
	}
	
	// 增加代理商信息操作日志
	private void addAgentLog(List<Object> list, Connection conn) throws SQLException {
		String sql = "insert into agent_info_log(agent_name ,agent_link_name , agent_link_tel , agent_link_mail ,"
			+ "agent_area ,agent_address, sale_name, agent_pay,mix_amout ,"
			+ "agent_rate ,agent_status,bank_name,account_no,account_name,"
			+ "cnaps_no,account_type,agent_area_type,profit_sharing,manager_logo,"
			+ "client_logo,group_code,bag_settle,"
			+ "is_invest,invest_amount,common_deposit_amount,common_deposit_rate,over_deposit_rate,deposit_rate,agent_no,create_time,creat_person_id,creat_person,log_type ) values "
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?)";
		
		// 操作员信息
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(0);
		
		dao.updateByTranscation(sql, list.toArray(), conn);
	}

	// 增加代理商分润比例修改日志
	public void addAgentShareLog(String smfr, String agent_no,
			String rule_type, Connection conn) throws SQLException {
		List<Object> list = new ArrayList<Object>();
		list.add(agent_no);
		list.add(smfr);
		list.add(rule_type);
		// 操作员信息
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(bu.getId());
		list.add(bu.getRealName());

		String sq = "INSERT INTO agent_share_rule_log (agent_no, sharing_rule, rule_type, oper_user_id, oper_user_name,create_time) VALUES "
				+ "(?,?,?,?,?,now())";

		dao.updateByTranscation(sq, list.toArray(), conn);
	}
	
	// 超级刷代理商通过用户名查询
		public Map<String, Object> getAgentUserByNameSPOS(String userName) {
			List<Object> list = new ArrayList<Object>();
			list.add(userName);
			String sql = "select u.* from agent_user u,agent_info a where a.agent_no=u.agent_no and a.brand_type='SPOS' and u.user_name=?";
			Map<String, Object> agentUser = dao.findFirst(sql, list.toArray());
			return agentUser;
		}

	// 代理商通过用户名查询
	public Map<String, Object> getAgentUserByName(String userName) {
		List<Object> list = new ArrayList<Object>();
		list.add(userName);
		String sql = "select u.* from agent_user u,agent_info a where a.agent_no=u.agent_no and (a.brand_type='' or  a.brand_type is null)  and  u.user_name=?";
		Map<String, Object> agentUser = dao.findFirst(sql, list.toArray());
		return agentUser;
	}
	
	// 超级刷代理商通过代理商名称查询
		public Map<String, Object> getAgentUserByAgentNameSPOS(String agentName) {
			List<Object> list = new ArrayList<Object>();
			list.add(agentName);
			String sql = "select u.* from agent_info u where  u.brand_type='SPOS' and u.agent_name=?";
			Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
			return agentInfo;
		}

	// 代理商通过代理商名称查询
	public Map<String, Object> getAgentUserByAgentName(String agentName) {
		List<Object> list = new ArrayList<Object>();
		list.add(agentName);
		String sql = "select u.* from agent_info u where  u.agent_name=?  and (u.brand_type='' or  u.brand_type is null)";
		Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
		return agentInfo;
	}

	public Map<String, Object> getAgentMerAndTransCount(String agentNo) {
		List<Object> list = new ArrayList<Object>();
		list.add(agentNo);
		list.add(agentNo);
		String sql = "select t1.mercount,t2.transcount from "
				+ "(SELECT  count(*) as mercount from pos_merchant m where m.agent_no=?) t1, "
				+ "(SELECT  count(*) as transcount from  trans_info i where i.agent_no=?) t2";
		Map<String, Object> countInfo = dao.findFirst(sql, list.toArray());
		return countInfo;
	}

	public void checkIsAgentUsed(Page<Map<String, Object>> list) {
		List<Map<String, Object>> maps = list.getContent();
		for (Map<String, Object> m : maps) {
			String agentNo = (String) m.get("agent_no");
			// String agentStatus = (String)m.get("agent_status");
			Map<String, Object> countInfo = getAgentMerAndTransCount(agentNo);
			Long merCount = (Long) countInfo.get("mercount");
			Long transCount = (Long) countInfo.get("transcount");
			if (merCount > 0 || transCount > 0) {
				m.put("isDel", false);
			} else {
				m.put("isDel", true);
			}
		}
	}

	// 代理商冻结
	public int agentInfoFreeze(Map<String, String> params) throws SQLException {

		String agentNo = params.get("agentNo");
		String update_agent_info = "update  agent_info set agent_status=2 where agent_no =? ";
		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(agentNo);

		int affectedRows = dao.update(update_agent_info,
				listAgentInfo.toArray());

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商扣率锁定
	public int agentInfoLocked(Map<String, String> params) throws SQLException {
		String locked_status = params.get("locked_status");
		String agentNo = params.get("agentNo");
		String update_agent_info = "update  agent_settle_fee set locked_status=? where agent_no =? ";
		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(locked_status);
		listAgentInfo.add(agentNo);
		int affectedRows = dao.update(update_agent_info,
				listAgentInfo.toArray());

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商扣率审核
	public int agentInfoChecked(Map<String, String> params) throws SQLException {
		String checked_status = params.get("checked_status");
		String agentNo = params.get("agentNo");
		String update_agent_info = "update  agent_settle_fee set checked_status=? where agent_no =? ";
		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(checked_status);
		listAgentInfo.add(agentNo);
		int affectedRows = dao.update(update_agent_info,
				listAgentInfo.toArray());

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商分润锁定
	public int agentShareChecked(Map<String, String> params)
			throws SQLException {
		String checked_status = params.get("share_checked_status");
		String agentNo = params.get("agentNo");
		String update_agent_info = "update  agent_share_rule set checked_status=? where agent_no =?";
		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(checked_status);
		listAgentInfo.add(agentNo);
		int affectedRows = dao.update(update_agent_info,
				listAgentInfo.toArray());

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商分润锁定
	public int agentShareLocked(Map<String, String> params) throws SQLException {
		String checked_status = params.get("share_lockeded_status");
		String agentNo = params.get("agentNo");
		String update_agent_info = "update  agent_share_rule set locked_status=? where agent_no =?";
		List<Object> listAgentInfo = new ArrayList<Object>();
		listAgentInfo.add(checked_status);
		listAgentInfo.add(agentNo);
		int affectedRows = dao.update(update_agent_info,
				listAgentInfo.toArray());

		if (affectedRows > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	// 代理商删除
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
	public int agentInfoDel(Map<String, String> params) {

		String agentNo = params.get("agentNo");
		String delete_agent_info = "delete from agent_info  where agent_no =? ";
		String delete_agent_user = "delete from agent_user  where agent_no =? ";
		String delete_share_rule = "delete from agent_share_rule  where agent_no =? ";
		String delete_settle_fee = "delete from agent_settle_fee  where agent_no =? ";
		String update_terminal_type = "update pos_terminal set agent_no = null , open_status=0 , type=null ,allot_batch=null where agent_no =? ";
		List<Object> list = new ArrayList<Object>();
		list.add(agentNo);
		
		Connection conn = null;
		try {
			
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			dao.updateByTranscation(delete_agent_info, list.toArray(),conn);
			dao.updateByTranscation(delete_agent_user, list.toArray(),conn);
			dao.updateByTranscation(delete_share_rule, list.toArray(),conn);
			dao.updateByTranscation(delete_settle_fee, list.toArray(),conn);
			dao.updateByTranscation(update_terminal_type, list.toArray(),conn);
			
			addAgentDelLog(agentNo,conn);
			
			conn.commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	// 添加删除代理商日志
	private void addAgentDelLog(String agentNo, Connection conn) throws SQLException{
		String sql = "insert into agent_info_log(agent_no,create_time,creat_person_id,creat_person,log_type) values(?,now(),?,?,?)";
		List<Object> list = new ArrayList<Object>();
		list.add(agentNo);
		
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(1);
		
		dao.updateByTranscation(sql, list.toArray(), conn);
	}
	

	public boolean checkAgentCanDel(Map<String, String> params) {
		String agentNo = params.get("agentNo");
		Map<String, Object> countInfo = getAgentMerAndTransCount(agentNo);
		Long merCount = (Long) countInfo.get("mercount");
		Long transCount = (Long) countInfo.get("transcount");
		if (merCount > 0 || transCount > 0) {
			return false;
		} else {
			return true;
		}
	}

	// 查询代理商
	public List<Map<String, Object>> agentListSelect() {
		List<Object> list = new ArrayList<Object>();
		String sql = "select a.*,CONCAT(IFNULL(a.agent_name,''), ' ',IFNULL(a.brand_type,'') ) as agent_name from agent_info a where a.parent_id is null or a.parent_id='' or a.parent_id=0 order by convert(a.agent_name using utf8)";
		return dao.find(sql, list.toArray());
	}

	// 查询商户
	public List<Map<String, Object>> merchantSelect(String agentNo) {
		// List<Object> list = new ArrayList<Object>();
		String sql = "select m.merchant_no,m.merchant_short_name,m.agent_no,m.merchant_name from pos_merchant m where m.agent_no =?  order by convert(m.merchant_short_name using utf8)  ";
		Object[] params = { agentNo };
		return dao.find(sql, params);
	}

	// 查询代理商(供标签使用)
	public List<Map<String, Object>> getAgentListForTag(
			Map<String, String> params, final PageRequest pageRequest) {
		String onlyThowParentAgent = params.get("onlyThowParentAgent");
		String getChildByParentAgentno = params.get("getChildByParentAgentno");
		List<Object> list = new ArrayList<Object>();
		String sql_where = " where  1=1 ";
		String sql_orderbyString = "   order by   convert(agent_name using utf8)  ";
		String sql = "";
		if ("true".equals(onlyThowParentAgent)) {
			sql_where = sql_where
					+ " and a.parent_id is null or  a.parent_id='' or a.parent_id='0' ";

			sql = "select a.*  from agent_info a  " + sql_where
					+ sql_orderbyString;
			// 新版查询语句

			// sql =
			// "select * from agent_info a where a.parent_node ='[0]' order by a.id";
			StringBuffer sb = new StringBuffer();
			sb.append("select a.id, a.agent_status, a.parent_id, a.agent_no, a.agent_link_name");
			sb.append(" ,CONCAT(IFNULL(a.agent_name,''), ' ',IFNULL(a.brand_type,'') ) as agent_name");
			sb.append(" , a.agent_link_tel, a.agent_link_mail, a.agent_area");
			sb.append(" , a.agent_address, a.sale_name, a.manager_logo, a.client_logo, a.create_time");
			sb.append(" , a.agent_pay, a.mix_amout, a.agent_rate, a.bank_name, a.account_no, a.account_name");
			sb.append(" , a.cnaps_no, a.account_type, a.agent_area_type, a.profit_sharing, a.group_code");
			sb.append(" , a.sharing_rate, a.parent_node, a.self_node, a.bag_settle, a.brand_type");
			sb.append("  from agent_info a ");
			

			sql = sb.toString() + " where a.parent_node ='[0]' order by a.agent_name";
		}
		if (!"".equals(getChildByParentAgentno)
				&& (getChildByParentAgentno != null)) {
			// sql_where = sql_where
			// +
			// " and a.parent_id  in (select id ,parent_id from agent_info where agent_no=? ) or agent_no=? ";

			sql = "SELECT b.* from agent_info b where   b.parent_id  "
					+ "IN( select id  from agent_info a where  a.parent_id  "
					+ "IN (SELECT id from agent_info  WHERE agent_no=?)) "
					+ " union select *  from agent_info a where   a.parent_id  "
					+ "IN (SELECT id from agent_info  WHERE agent_no=?)  "
					+ "union  select * from agent_info c WHERE agent_no=? ORDER BY id";

			// 新版查询语句

			StringBuffer sb = new StringBuffer();
			sb.append("select a.id, a.agent_status, a.parent_id, a.agent_no, a.agent_link_name, a.agent_name");
			sb.append(" ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name");
			sb.append(" , a.agent_address, a.sale_name, a.manager_logo, a.client_logo, a.create_time");
			sb.append(" , a.agent_pay, a.mix_amout, a.agent_rate, a.bank_name, a.account_no, a.account_name");
			sb.append(" , a.cnaps_no, a.account_type, a.agent_area_type, a.profit_sharing, a.group_code");
			sb.append(" , a.sharing_rate, a.parent_node, a.self_node, a.bag_settle, a.brand_type");
			sb.append("  from agent_info a ");

			// sql =
			// "select * from agent_info a where a.agent_no = ? or a.parent_node like ( select CONCAT(a.self_node,'%') from agent_info a where a.agent_no = ? ) order by a.agent_area_type";
			sql = sb.toString()
					+ " where a.agent_no = ? or a.parent_node like ( select CONCAT(a.self_node,'%') from agent_info a where a.agent_no = ? ) order by a.agent_name";

			list.add(getChildByParentAgentno);
			list.add(getChildByParentAgentno);
		}

		return dao.find(sql, list.toArray());
	}

	/*
	 * 根据代理商编号获取代理商名称
	 */
	public String getAgentName(String agentNo) {
		String sql = "select CONCAT(IFNULL(agent_name,''),' ', IFNULL(brand_type,'') ) as agent_name from agent_info where agent_no=?";
		return (String) dao.findFirst(sql, agentNo).get("agent_name");
	}

	/*
	 * 根据代理商编号获取代理商锁定状态
	 */
	public String getAgentSettleFee(String agentNo) {
		String sql = "select * from agent_settle_fee where agent_no=?";
		return (String) dao.findFirst(sql, agentNo).get("locked_status");
	}

	/**
	 * 获取一级代理商的节点信息
	 * 
	 * @param agent_no
	 * @return
	 * @author LJ
	 */
	public Map<String, Object> getParentNode(String agent_no)
			throws SQLException {
		String sql = "select a.id,a.agent_no,a.parent_id,a.parent_node,a.self_node,a.agent_area_type from agent_info a where a.id =? and a.brand_type = '' or a.brand_type is null";
		Map<String, Object> map = null;
		map = dao.findFirst(sql, agent_no);
		return map;
	}

	public Page<Map<String, Object>> getAgentTransRuleList(Map<String, String> params, PageRequest page) {
		String sql = "select t.id,t.agent_no,t.pos_type, t.merchant_type,t.real_flag,t.account_type,t.fee_type,t.ed_max_amount,t.single_max_amount,t.ed_card_max_amount,t.ed_card_max_items from transaction_limit t where 1 = 1 ";
		List<String> list = new ArrayList<String>();
		
		String agentNo = params.get("agentNo");
		String agentNoText = params.get("agentNoText");
		if(StringUtils.isNotBlank(agentNo) && !"-1".equals(agentNo)){
			sql += " and t.agent_no = ? ";
			list.add(agentNo);
		}
		
		if(StringUtils.isNotBlank(agentNoText)){
			sql += " and t.agent_no = ? ";
			list.add(agentNoText);
		}
		
		String pos_type = params.get("pos_type");
		if(StringUtils.isNotBlank(pos_type) && !"-1".equals(pos_type)){
			sql += " and t.pos_type = ? ";
			list.add(pos_type);
		}
		
		String merchant_type = params.get("merchant_type");
		if(StringUtils.isNotBlank(merchant_type)){
			sql += " and t.merchant_type = ? ";
			list.add(merchant_type);
		}
		
		String real_flag = params.get("real_flag");
		if(StringUtils.isNotBlank(real_flag)){
			sql += " and t.real_flag = ? ";
			list.add(real_flag);
		}
		
		String fee_type = params.get("fee_type");
		if(StringUtils.isNotBlank(fee_type)){
			sql += " and t.fee_type = ? ";
			list.add(fee_type);
		}
		
		String account_type = params.get("account_type");
		if(StringUtils.isNotBlank(account_type)){
			sql += " and t.account_type = ? ";
			list.add(account_type);
		}
		
		sql += " order by t.create_time desc ";
		
		return dao.find(sql, list.toArray(), page);
		
	}
	
	public int addTransRule(Map<String, String> params){
		List<Object> list = new ArrayList<Object>();
		String agentNo = params.get("agentNo");
		String agentInput = params.get("agentInput");
		if(StringUtils.isEmpty(agentNo) && !"-1".equals(agentNo) && StringUtils.isEmpty(agentInput) && !"default".equals(agentInput)){
			return 0;
		}
		
		String agent_no = "-1".equals(agentNo) && "default".equals(agentInput) ? agentInput : agentNo;
		
		String pos_type = params.get("pos_type");
		pos_type = StringUtils.isEmpty(pos_type) || "-1".equals(pos_type)  ? null : pos_type;
		
		String merchant_type = params.get("merchant_type");
		merchant_type = StringUtils.isEmpty(merchant_type) ? null : merchant_type;
		
		String real_flag = params.get("real_flag");
		real_flag = StringUtils.isEmpty(real_flag) ? null : real_flag;
		
		String account_type = params.get("account_type");
		account_type = StringUtils.isEmpty(account_type) ? null : account_type;
		
		String fee_type = params.get("fee_type");
		fee_type = StringUtils.isEmpty(fee_type) ? null : fee_type;
		
		String ed_max_amount = params.get("ed_max_amount");
		ed_max_amount = StringUtils.isEmpty(ed_max_amount) ? null : ed_max_amount;
		
		String single_max_amount = params.get("single_max_amount");
		single_max_amount = StringUtils.isEmpty(single_max_amount) ? null : single_max_amount;
		
		String ed_card_max_amount = params.get("ed_card_max_amount");
		ed_card_max_amount = StringUtils.isEmpty(ed_card_max_amount) ? null : ed_card_max_amount;
		
		String ed_card_max_items = params.get("ed_card_max_items");
		ed_card_max_items = StringUtils.isEmpty(ed_card_max_items) ? null : ed_card_max_items;
		
		String start_time = params.get("start_time");
		start_time = StringUtils.isEmpty(start_time) ? null : start_time;
		
		String end_time = params.get("end_time");
		end_time = StringUtils.isEmpty(end_time) ? null : end_time;
		
		String limit_status = params.get("limit_status");
		limit_status = StringUtils.isEmpty(limit_status) ? null : limit_status;
		
		String create_user = params.get("create_user");
		create_user = StringUtils.isEmpty(create_user) ? null : create_user;
		
		StringBuffer sql = new StringBuffer("insert into transaction_limit ");
		
		StringBuffer param = new StringBuffer(" values (");
		sql.append(" (agent_no");
		param.append("?");
		list.add(agent_no);
		
		if (null != pos_type) {
			sql.append(", pos_type");
			list.add(pos_type);
			param.append(",?");
		}
		if (null != merchant_type) {
			sql.append(", merchant_type");
			list.add(merchant_type);
			param.append(",?");
		}
		if (null != real_flag) {
			sql.append(", real_flag");
			list.add(real_flag);
			param.append(",?");
		}
		if (null != account_type) {
			sql.append(", account_type");
			list.add(account_type);
			param.append(",?");
		}
		if (null != fee_type) {
			sql.append(", fee_type");
			list.add(fee_type);
			param.append(",?");
		}
		if (null != ed_max_amount) {
			sql.append(", ed_max_amount");
			list.add(ed_max_amount);
			param.append(",?");
		}
		if (null != single_max_amount) {
			sql.append(", single_max_amount");
			list.add(single_max_amount);
			param.append(",?");
		}
		if (null != ed_card_max_amount) {
			sql.append(", ed_card_max_amount");
			list.add(ed_card_max_amount);
			param.append(",?");
		}
		if (null != ed_card_max_items) {
			sql.append(", ed_card_max_items");
			list.add(ed_card_max_items);
			param.append(",?");
		}
		if (null != start_time) {
			sql.append(", start_time");
			list.add(start_time);
			param.append(",?");
		}
		if (null != end_time) {
			sql.append(", end_time");
			list.add(end_time);
			param.append(",?");
		}
		if (null != limit_status) {
			sql.append(", limit_status");
			list.add(limit_status);
			param.append(",?");
		}
		if (null != create_user) {
			sql.append(", create_user");
			list.add(create_user);
			param.append(",?");
		}
		
		sql.append(", create_time");
		param.append(",now() ");
		
		sql.append(")");
		param.append(")");
		sql.append(param);
		
		int row = 0;
		try {
			row = dao.update(sql.toString(), list.toArray());
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		
		return row;
	}
	
	public int delTransRule(String id){
		String sql = "delete from transaction_limit where id = ?";
		int row = 0;
		try {
			row = dao.update(sql,id);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return row;
	}
	
	public void transRuleModify(Map<String, String> params) {

		String ids = params.get("ids");
		String[] idArr = ids.split(",");
		String ed_max_amount = params.get("ed_max_amount");
		String single_max_amount = params.get("single_max_amount");
		String ed_card_max_amount = params.get("ed_card_max_amount");
		String ed_card_max_items = params.get("ed_card_max_items");
		
		String changeWay = params.get("changeWay");
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			// sql 赋值字符串
			String equalStr = "";
			// sql 相加字符串
			String plusStr = "";
			// sql 多个记录id字符串
			String idsStr = "";
			
			List<String> paramArray = new ArrayList<String>();
			List<String> idArray = new ArrayList<String>();
			List<String> updArray = new ArrayList<String>();
			
			if(StringUtils.isNotEmpty(ed_max_amount)){
				paramArray.add(ed_max_amount);
				updArray.add(ed_max_amount);
				equalStr += " ed_max_amount = ?, ";
				plusStr += " ed_max_amount = ed_max_amount + ?, ";
			}
			if(StringUtils.isNotEmpty(single_max_amount)){
				paramArray.add(single_max_amount);
				updArray.add(single_max_amount);
				equalStr += " single_max_amount = ?, ";
				plusStr += " single_max_amount = single_max_amount + ?, ";
			}
			if(StringUtils.isNotEmpty(ed_card_max_amount)){
				paramArray.add(ed_card_max_amount);
				updArray.add(ed_card_max_amount);
				equalStr += " ed_card_max_amount = ?, ";
				plusStr += " ed_card_max_amount = ed_card_max_amount + ?, ";
			}
			if(StringUtils.isNotEmpty(ed_card_max_items)){
				paramArray.add(ed_card_max_items);
				updArray.add(ed_card_max_items);
				equalStr += " ed_card_max_items = ?, ";
				plusStr += " ed_card_max_items = ed_card_max_items + ?, ";
			}
			equalStr = equalStr.substring(0, equalStr.lastIndexOf(","));
			plusStr = plusStr.substring(0, plusStr.lastIndexOf(","));
			
			for(String id : idArr){
				idsStr += "?,";
				paramArray.add(id);
				idArray.add(id);
			}
			idsStr = idsStr.substring(0, idsStr.lastIndexOf(","));
			
			if("0".equals(changeWay)){
				
				// 基础额度
				String baseSql = "update transaction_limit set " + equalStr + " where id in (" + idsStr + " )";
				dao.updateByTranscation(baseSql, paramArray.toArray(), conn);
				
			}else if("1".equals(changeWay)){
				
				// 特定额度(相加)
				String merchantNoStr = buildMerchantNoStr(updArray, idsStr, idArray);
				
				if(StringUtils.isNotBlank(merchantNoStr)){
					String SpecSql = "update pos_merchant_trans_rule set " + plusStr + " where merchant_no in (" + merchantNoStr + ")";
					dao.updateByTranscation(SpecSql, updArray.toArray(), conn);
				}
				
			}else if("2".equals(changeWay)){
				
				// 基础额度
				String baseSql = "update transaction_limit set " + equalStr + " where id in (" + idsStr + " )";
				dao.updateByTranscation(baseSql, paramArray.toArray(), conn);
				
				// 特定额度(赋值)
				String merchantNoStr = buildMerchantNoStr(updArray, idsStr, idArray);
				if(StringUtils.isNotBlank(merchantNoStr)){
					String SpecSql = "update pos_merchant_trans_rule set " + equalStr + " where merchant_no in (" + merchantNoStr + ")";
					dao.updateByTranscation(SpecSql, updArray.toArray(), conn);
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String buildMerchantNoStr(List<String> updArray, String idsStr, List<String> idArray){
		
		// 1.查询所有选中的额度记录
		String queryLimitSql = " select agent_no,pos_type,merchant_type,real_flag,account_type,fee_type "
							 + " from transaction_limit "
							 + " where id in(" + idsStr + ")";
		
		List<Map<String, Object>> limits = dao.find(queryLimitSql, idArray.toArray());
		
		// 2.根据限额记录 查询影响到对应的商户编号
		Set<String> merchantNos = new HashSet<String>();//用set过滤掉可能重复的merchant_no
		
		for(Map<String, Object> limit : limits){
			Object agent_no = limit.get("agent_no");
			Object pos_type = limit.get("pos_type");
			Object merchant_type = limit.get("merchant_type");
			Object real_flag = limit.get("real_flag");
			Object account_type = limit.get("account_type");
			Object fee_type = limit.get("fee_type");
			
			List<String> condArray = new ArrayList<String>();
			String queryMerchantNoSql = "select pm.merchant_no "
									  + " from pos_merchant pm "
									  + " inner join pos_merchant_fee pmf on pm.merchant_no = pmf.merchant_no "
									  + " where 1 = 1 ";
			
			if(agent_no != null && StringUtils.isNotBlank(agent_no.toString()) && !"default".equals(agent_no.toString())){
				// 代理商编号为default表示修改所有相应的记录
				queryMerchantNoSql += " and pm.agent_no = ? ";
				condArray.add(agent_no.toString());
			}
			
			if(pos_type != null && StringUtils.isNotBlank(pos_type.toString())){
				queryMerchantNoSql += " and pm.pos_type = ? ";
				condArray.add(pos_type.toString());
			}
			
			if(merchant_type != null && StringUtils.isNotBlank(merchant_type.toString())){
				queryMerchantNoSql += " and pm.merchant_type = ? ";
				condArray.add(merchant_type.toString());
			}
			
			if(real_flag != null && StringUtils.isNotBlank(real_flag.toString())){
				queryMerchantNoSql += " and pm.real_flag = ? ";
				condArray.add(real_flag.toString());
			}
			
			if(account_type != null && StringUtils.isNotBlank(account_type.toString())){
				queryMerchantNoSql += " and pm.account_type = ? ";
				condArray.add(account_type.toString());
			}
			
			if(fee_type != null && StringUtils.isNotBlank(fee_type.toString())){
				queryMerchantNoSql += " and pmf.fee_type = ? ";
				condArray.add(fee_type.toString());
			}
			
			List<Map<String, Object>> merchantNosList = dao.find(queryMerchantNoSql, condArray.toArray());
			for(Map<String, Object> map : merchantNosList){
				merchantNos.add(map.get("merchant_no").toString());//把查询到的所有商户编号放到Set中进行过滤
			}
		}

		// 3.拼装商户编号查询字符串
		String merchantNoStr = "";
		for(String merchantNo : merchantNos){
			merchantNoStr += " ?, ";
			updArray.add(merchantNo);
		}
		
		if(StringUtils.isNotBlank(merchantNoStr))
			merchantNoStr = merchantNoStr.substring(0, merchantNoStr.lastIndexOf(","));
		
		return merchantNoStr;
	}
}
