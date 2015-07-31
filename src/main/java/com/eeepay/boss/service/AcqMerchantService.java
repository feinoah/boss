package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.axis.impl.AxisCleanCacheServiceImpl;
import com.eeepay.boss.domain.AcqOrg;
import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.JCEHandler;

/**
 * 收单商户基本操作
 * 
 * @author zhanghw
 */
@Service
public class AcqMerchantService {
	@Resource
	private Dao dao;

	private static final Logger log = LoggerFactory
			.getLogger(AcqMerchantService.class);

	@Resource
	private AxisCleanCacheServiceImpl cleanCacheService;

	// 根据收单机构英文名称查询收单机构
	public AcqOrg queryAcqOrgByEnname(String acqEnname) {
		String sql = "select * from acq_org where acq_enname ='" + acqEnname
				+ "'";
		return dao.findFirst(AcqOrg.class, sql);
	}
	
	/**
	 * 根据收单机构以及收单机构编号查询实名商户是否存在集群信息
	 * @param acqMerchantNo 收单机构商户编号
	 * @param acqEnName 收单机构英文名称
	 * @return Map集合String, Object
	 */
	public List<Map<String, Object>> queryMerchantGroupByAcqAndAcqNo(String acqMerchantNo, String acqEnName) {
		String sql = "select * from trans_route_group_merchant g where g.merchant_no="
				+ " (select merchant_no from acq_merchant where acq_enname=? and acq_merchant_no=?)";
		List<String> list = new ArrayList<String>();
		list.add(acqEnName);
		list.add(acqMerchantNo);
		return dao.find(sql, list.toArray());
	} 

	// 根据收单机构商户号查询基本信息
	public Map<String, Object> queryAcqMerchantInfo(String acqMerchantNo) {

		String sql = "select o.acq_cnname, a.acq_merchant_name,a.agent_no, a.fee_type acq_fee_type, a.fee_rate acq_fee_rate,"
				+ " a.fee_cap_amount acq_fee_cap_amount, a.fee_max_amount acq_fee_max_amount, a.fee_single_amount acq_fee_single_amount  "
				+ "from acq_merchant a, acq_org o where a.acq_enname = o.acq_enname and acq_merchant_no ='"
				+ acqMerchantNo + "'";

		return dao.findFirst(sql);
	}

	// 根据ID查询基本信息
	public Map<String, Object> queryAcqMerchantInfo(Long id) {
		String sql = "select a.*,p.merchant_no,p.merchant_name ,CONCAT(IFNULL(i.agent_name,'---'),' ', IFNULL(i.brand_type,'') ) as agent_name from acq_merchant a left join pos_merchant p on a.merchant_no=p.merchant_no left join agent_info i on a.agent_no=i.agent_no  where a.id="
				+ id;
		return dao.findFirst(sql);
	}

	public Map<String, Object> queryAcqMerchant(Long id) {
		String sql = "select *  from acq_merchant a  where a.id=" + id;
		return dao.findFirst(sql);
	}

	public void delTerminalQuery(String terminalNo) {
		try {
			dao.update("delete from acq_terminal where acq_terminal_no=?",
					terminalNo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void delTerminalById(String terminalId) {
		try {
			dao.update("delete from acq_terminal where id=?",terminalId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateTerminalStatus(String terminalNo, int status) {
		try {
			dao.update(
					"update acq_terminal set status=? where acq_terminal_no=?",
					new Object[] { status, terminalNo });
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			cleanCacheService.cleanAllCache(terminalNo);
		} catch (Exception e) {
			log.error(e.getMessage() + "|" + terminalNo);
			// e.printStackTrace();
		}
	}

	/**
	 * 开通或关闭收单机构商户
	 * 
	 * @param acq_merchant_no
	 *            收单机构商户编号
	 * @param locked
	 *            锁定状态
	 */
	public void updateMerchantStatus(String acq_merchant_no, int locked) throws Exception {
		String selectAcqMerchant = "select acq_merchant_no, merchant_no,acq_enname from acq_merchant where acq_merchant_no = ?";
		String insertAcqMerchantLog = "insert into acq_merchant_log(acq_merchant_no,merchant_no,locked,oper_time,oper_id,oper,oper_type) values(?,?,?,now(),?,?,?)";

		Map<String, Object> acqMerchant = dao.findFirst(selectAcqMerchant, new Object[]{acq_merchant_no});

		if(acqMerchant != null){
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);

				dao.updateByTranscation("update acq_merchant  set locked=? where acq_merchant_no=?", new Object[]{locked, acq_merchant_no}, conn);

				// 记录操作日志
				BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				dao.updateByTranscation(insertAcqMerchantLog, new Object[]{acqMerchant.get("acq_merchant_no").toString(), acqMerchant.get("merchant_no").toString(), locked, bu.getId(), bu.getRealName(), 0}, conn);

				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				conn.rollback();
				throw new RuntimeException(e);
			}finally{
				conn.close();
			}
		}
	}

	public List<Map<String, Object>> getMerchantList() {
		return dao.find("select * from acq_org");
	}

	/**
	 * 根据收单商户名称查询对应大商户扣率以及费率
	 * 
	 * @param acq_merchant_name
	 *            收单商户名称
	 * @return
	 */
	public Map<String, Object> findMerchantRate(String acq_merchant_name) {
		List<Object> list = new ArrayList<Object>();
		list.add(acq_merchant_name);
		list.add(acq_merchant_name);
		String sql = "select  f.id,f.merchant_no,f.fee_type,f.fee_rate,f.fee_cap_amount,f.fee_max_amount,f.fee_single_amount,f.ladder_fee from pos_merchant_fee f,pos_merchant t "
				+ "where f.merchant_no=t.merchant_no and (t.merchant_name=? or t.merchant_short_name=?)";
		Map<String, Object> merchantRate = dao.findFirst(sql, list.toArray());
		return merchantRate;
	}

	// 查询银盛商户信息
	public Page<Map<String, Object>> getMerchantList(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agent_no"))) {
				if (!params.get("agent_no").equals("-1")) {
					String agent_no = params.get("agent_no");
					sb.append(" and a.agent_no = ? ");
					list.add(agent_no);
				}
			}

			if (StringUtils.isNotEmpty(params.get("acq_merchant_no"))) {
				sb.append(" and acq.acq_merchant_no = ? ");
				list.add(params.get("acq_merchant_no"));

			}

			/*
			 * if (StringUtils.isNotEmpty(params.get("real"))) { String real =
			 * params.get("real");
			 * 
			 * if(!"-1".equals(real)){ if(real.equals("1")){ sb.append(
			 * " and ( acq.merchant_no is not null && acq.merchant_no!='') ");
			 * }else{
			 * sb.append(" and ( acq.merchant_no is null or acq.merchant_no ='' ) "
			 * ); } }
			 * 
			 * }
			 */

			// 大套小标志
			if (StringUtils.isNotEmpty(params.get("large_small_flag"))) {
				if (!params.get("large_small_flag").equals("-1")) {
					sb.append(" and acq.large_small_flag = ? ");
					list.add(params.get("large_small_flag"));
				}
			}

			// 收单机构商户名称查询
			if (StringUtils.isNotEmpty(params.get("acq_merchant"))) {
				/*
				 * String merchant_name = "%" + params.get("acq_merchant") +
				 * "%"; sb.append(
				 * " and (acq.acq_merchant_name like ?  or acq.acq_merchant_no like ?) "
				 * );
				 */
				String merchant_name = params.get("acq_merchant");
				sb.append(" and (acq.acq_merchant_name = ?  or acq.acq_merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
			}
			// 普通商户名称/编号查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				/*
				 * String merchant = "%" + params.get("merchant") + "%";
				 * sb.append
				 * (" and (p.merchant_name like ?  or p.merchant_no like ?) ");
				 */
				String merchant = params.get("merchant");
				sb.append(" and (p.merchant_name = ?  or p.merchant_no = ?) ");
				list.add(merchant);
				list.add(merchant);
			}

			// 收单机构英文名称
			if (StringUtils.isNotEmpty(params.get("acq_enname"))) {
				String acq_enname = params.get("acq_enname");
				sb.append(" and acq_enname=? ");
				list.add(acq_enname);
			}

			// 锁定状态
			if (StringUtils.isNotEmpty(params.get("locked"))) {
				if (!params.get("locked").equals("-1")) {
					String locked = params.get("locked");
					sb.append(" and acq.locked=?");
					list.add(locked);
				}
			}

			// 所属销售查询
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");
				sb.append(" and a.sale_name=?  ");
				list.add(sale_name);
			}

		}

		String sql = "select acq.*, a.agent_name  from agent_info a  right join acq_merchant acq  on acq.agent_no=a.agent_no"
				+ " left join pos_merchant p on acq.merchant_no=p.merchant_no where 1=1  "
				+ sb.toString() + " order by acq.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 查询银盛终端信息
	public Page<Map<String, Object>> getTerminalList(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();

		if (null != params) {

			// 代理名称
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentCode = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentCode);
				}
			}

			if (StringUtils.isNotEmpty(params.get("acqMerchant"))) {
				/*
				 * sb.append(
				 * " and (acq.acq_merchant_no = ? or acq.acq_merchant_name like ?)"
				 * ); list.add(params.get("acqMerchant")); list.add("%" +
				 * params.get("acqMerchant") + "%");
				 */
				sb.append(" and (acq.acq_merchant_no = ? or acq.acq_merchant_name = ?)");
				list.add(params.get("acqMerchant"));
				list.add(params.get("acqMerchant"));

			}

			// agent_no

			// if (StringUtils.isNotEmpty(params.get("agent_no")) &&
			// (!"-1".equals(params.get("agent_no")))) {
			// sb.append(" and agent_no = ? ");
			// list.add( params.get("agent_no") );
			//
			// }

			// 大套小标志
			if (StringUtils.isNotEmpty(params.get("large_small_flag"))) {
				if (!params.get("large_small_flag").equals("-1")) {
					sb.append(" and acq.large_small_flag = ? ");
					list.add(params.get("large_small_flag"));
				}
			}

			if (StringUtils.isNotEmpty(params.get("acq_terminal_no"))) {
				/*
				 * sb.append(" and acq_terminal_no like ? "); list.add("%" +
				 * params.get("acq_terminal_no") + "%");
				 */
				sb.append(" and acq_terminal_no = ? ");
				list.add(params.get("acq_terminal_no"));

			}
			// 收单机构英文名称
			if (StringUtils.isNotEmpty(params.get("acq_enname"))) {
				String acq_enname = params.get("acq_enname");
				sb.append(" and t.acq_enname=? ");
				list.add(acq_enname);
			}
		}

		// 锁定状态
		if (StringUtils.isNotEmpty(params.get("locked"))) {
			if (!params.get("locked").equals("-1")) {
				String status = params.get("locked");
				sb.append(" and t.locked = ? ");
				list.add(status);
			}
		}
		String sql = "select t.*, acq.acq_merchant_no,acq.acq_merchant_name ,acq.agent_no , acq.merchant_no ,acq.large_small_flag from  acq_terminal t "
				+ "left join acq_merchant acq  on acq.acq_merchant_no=t.acq_merchant_no  left join  agent_info a on acq.agent_no=a.agent_no where 1=1 "
				+ sb.toString() + " order by t.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	public int addTerminal(Map<String, String> params) throws SQLException {
		String sql = "insert into acq_terminal(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no) values(?,?,?,?,?)";
		Object[] ps = { "eptok", params.get("acq_merchant_no"),
				params.get("acq_terminal_no"), "000000", "000000" };
		int n = dao.update(sql, ps);

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return n;
	}

	/**
	 * 添加终端
	 * 
	 * @param params
	 *            传入的参数
	 * @return 结果集
	 * @throws SQLException
	 */
	public int addTerminalWithAcq(Map<String, String> params)
			throws Exception {
		String acq_terminal_no = params.get("acq_terminal_no");
		if (acq_terminal_no == null || acq_terminal_no.trim().length() == 0) {
			acq_terminal_no = null;
		} else {
			acq_terminal_no = acq_terminal_no.trim();
		}
		String acqMerchantNo = params.get("acq_merchant_no");
		params.put("acq_merchant_no", acqMerchantNo.trim());
		String acq_enname = params.get("acq_enname");
		
		String sql = "insert into acq_terminal(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no,last_update_time,status) values(?,?,?,?,?,now(),?)";
		List<Object> list = new ArrayList<Object>();
		list.add(params.get("acq_enname"));
		list.add(params.get("acq_merchant_no"));
		list.add(acq_terminal_no);
		list.add("000000");
		list.add("000000");
		
		
		if ("bill".equals(acq_enname)) {
			String lmk_zmk = "AAC5E35F691A73938F11AD544DF23818";
			String lmk_zpk = "C1E4F2A201419E6F05BF0C2FB0BF9EF6";
			String lmk_zpk_cv = "DABAC6BD92424474DABAC6BD92424474";
			String lmk_zak = "C82F62ECA67B3CC64CF544BD7817F1CB";
			String lmk_zak_cv = "8834B6D7AE75F0548834B6D7AE75F054";
			String lmk_zdk = "E9A986959B14C44012090F1D0F92274F";
			String lmk_zdk_cv = "9A3AEA751A854AD29A3AEA751A854AD2";
			String work_key = "975117BBC20557EA26E0DA6BA6F210DD05DC6039A3AEA751A854AD298511A30EEE9B8C703A64FF3EB85BC881155402DABAC6BD92424474995111A9271B787CA8F55CCEC0F96881DF493018834B6D7AE75F054";
			
			sql = "insert into acq_terminal(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no,lmk_zmk,lmk_zpk,"
					+ "lmk_zpk_cv,lmk_zak,lmk_zak_cv,lmk_zdk,lmk_zdk_cv,work_key,last_update_time,status) values(?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?)";
			list.add(lmk_zmk);
			list.add(lmk_zpk);
			list.add(lmk_zpk_cv);
			list.add(lmk_zak);
			list.add(lmk_zak_cv);
			list.add(lmk_zdk);
			list.add(lmk_zdk_cv);
			list.add(work_key);
			
		} else if ("tftpay".equals(acq_enname)) {
			String lmk_zmk = "E4713CA1BC4159380319CA6FA8AA2F9F";
			
			sql = "insert into acq_terminal(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no,lmk_zmk,last_update_time,status) values(?,?,?,?,?,?,now(),?)";
			
			list.add(lmk_zmk);
			
		} else if(("ubs").equals(acq_enname) || "hypay".equals(acq_enname) || "qlhdpay".equals(acq_enname)){
			/**
			 * 瑞银信/翰亿添加终端密钥
			 */
			String terKeys = params.get("secretKey");;
			if(!StringUtils.isBlank(terKeys) && terKeys.trim().length()==32){
				String key = Constants.UBS_KEK;
				if("hypay".equals(acq_enname))key = Constants.HYPAY_KEK;
				if("qlhdpay".equals(acq_enname)) key = Constants.QLHDPAY_KEY;
				String ubsKey = JCEHandler.decryptData(terKeys, key);//明文
				String lmk_zmk = JCEHandler.encryptData(ubsKey, Constants.UBS_LMK_KEY);
				String checkValue = JCEHandler.encryptData("00000000000000000000000000000000",ubsKey);
				
				sql = "insert into acq_terminal(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no,lmk_zmk,lmk_zmk_cv,last_update_time,status) values(?,?,?,?,?,?,?,now(),?)";
				
				list.add(lmk_zmk);
				list.add(checkValue);
				
			}
		}
		
		list.add(1);

		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			int n = dao.updateByTranscation(sql, list.toArray(), conn);
			
			addAcqTerminalLog(conn, "2", params, acq_terminal_no);
		
			conn.commit();
			
			return n;
		} catch (Exception e) {
			conn.rollback();
			log.error("新增收单机构终端未知异常=" + e.getMessage());
			e.printStackTrace();
			return 0;
		} finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("新增收单机构终端关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void addAcqTerminalLog(Connection conn, String oper_type, Map<String, String> params, String acq_terminal_no) throws SQLException{
		String sql = "insert into acq_terminal_log(acq_enname,acq_merchant_no,acq_terminal_no,batch_no,serial_no,status,oper_time,oper_id,oper,oper_type) values(?,?,?,?,?,?,now(),?,?,?)";
		
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		
		dao.updateByTranscation(sql, new Object[]{params.get("acq_enname"),params.get("acq_merchant_no"), acq_terminal_no, "000000","000000", 1,bu.getId(),bu.getRealName(),oper_type}, conn);
	}

	/*
	 * // 新增收单商户信息 public void addAcqMerchant(Map<String, String> params) throws
	 * SQLException { // String acq_enname = "eptok"; String acq_enname =
	 * params.get("acq_enname");; String acq_merchant_no =
	 * params.get("acq_merchant_no"); String acq_merchant_name =
	 * params.get("acq_merchant_name"); String merchant_no =
	 * params.get("merchant_no"); String agent_no = params.get("agent_no");
	 * String mcc = params.get("mcc"); String fee_rate = params.get("fee_rate");
	 * // String fee_cap_amount = params.get("fee_cap_amount"); String
	 * fee_max_amount = params.get("fee_max_amount"); // String
	 * fee_single_amount = params.get("fee_single_amount"); // String
	 * create_time = params.get("create_time");
	 * 
	 * String fee_type = params.get("fee_type"); String ladder_min =
	 * params.get("ladder_min"); String ladder_value =
	 * params.get("ladder_value"); String ladder_max = params.get("ladder_max");
	 * 
	 * String merchant_rate = params.get("merchant_Drate"); String
	 * merchant_max_amount = params.get("merchant_max_amount"); String
	 * merchant_min = params.get("merchant_min"); String merchant_value =
	 * params.get("merchant_value"); String merchant_max =
	 * params.get("merchant_max");
	 * 
	 * String large_small_flag = params.get("large_small_flag");
	 * 
	 * String sql = " insert into acq_merchant" +
	 * " (acq_enname,acq_merchant_no,acq_merchant_name,merchant_no,agent_no,mcc,"
	 * +
	 * "fee_type,fee_rate,fee_cap_amount,fee_max_amount,ladder_fee,large_small_flag,   create_time,merchant_rate) "
	 * + " values " + " (?,?,?,?,?,?,?,?,?,?,?,?,now(),?) ";
	 * 
	 * List<Object> list = new ArrayList<Object>();
	 * 
	 * list.add(acq_enname); list.add((acq_merchant_no == null ||
	 * acq_merchant_no.trim().length() == 0) ? null : acq_merchant_no.trim());
	 * list.add((acq_merchant_name == null || acq_merchant_name.trim() .length()
	 * == 0) ? null : acq_merchant_name.trim()); list.add(merchant_no);
	 * list.add((agent_no == null || agent_no.trim().length() == 0) ? null :
	 * agent_no); list.add((mcc == null || mcc.trim().length() == 0) ? null :
	 * mcc.trim()); // list.add((fee_max_amount ==null
	 * ||fee_max_amount.trim().length() ==0 // || new
	 * BigDecimal(fee_max_amount).doubleValue() == 0) // ?"RATIO":"CAPPING"); //
	 * list.add(fee_rate ==null ?null:new //
	 * BigDecimal(fee_rate).movePointLeft(2)); // list.add((fee_max_amount
	 * ==null||fee_max_amount.trim().length() ==0) // ?null:new
	 * BigDecimal(fee_max_amount));
	 * 
	 * list.add(fee_type); if ("RATIO".equals(fee_type)) { list.add(fee_rate ==
	 * null ? null : new BigDecimal(fee_rate) .movePointLeft(2));
	 * list.add(null); list.add(null); list.add(null);
	 * 
	 * } else if ("CAPPING".equals(fee_type)) { list.add(fee_rate == null ? null
	 * : new BigDecimal(fee_rate) .movePointLeft(2)); list.add((fee_rate == null
	 * || fee_max_amount == null) ? null : new
	 * BigDecimal(fee_max_amount).divide(new BigDecimal(
	 * fee_rate).movePointLeft(2), 2, BigDecimal.ROUND_HALF_UP));
	 * list.add((fee_max_amount == null || fee_max_amount.trim().length() == 0)
	 * ? null : new BigDecimal(fee_max_amount)); list.add(null);
	 * 
	 * } else if ("LADDER".equals(fee_type)) {
	 * 
	 * ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
	 * ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();
	 * 
	 * String ladder_fee = ladder_min + "<" + ladder_value + "<" + ladder_max;
	 * 
	 * list.add(null); list.add(null); list.add(null); list.add(ladder_fee);
	 * 
	 * }
	 * 
	 * list.add(large_small_flag);
	 * 
	 * if ("RATIO".equals(fee_type)) { list.add(merchant_rate == null ? null :
	 * new BigDecimal(merchant_rate) .movePointLeft(2));
	 * 
	 * } else if ("CAPPING".equals(fee_type)) { DecimalFormat df = new
	 * DecimalFormat("#.00"); list.add((merchant_max_amount == null ||
	 * merchant_max_amount.trim().length() == 0) ? null :new
	 * BigDecimal(merchant_rate) .movePointLeft(2) +"~"+ df.format(new
	 * BigDecimal(merchant_max_amount)));
	 * 
	 * } else if ("LADDER".equals(fee_type)) {
	 * 
	 * merchant_min = new BigDecimal(merchant_min).movePointLeft(2).toString();
	 * merchant_max = new BigDecimal(merchant_max).movePointLeft(2).toString();
	 * 
	 * String merchant_fee = merchant_min + "<" + merchant_value + "<" +
	 * merchant_max; list.add(merchant_fee);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * dao.update(sql, list.toArray());
	 * 
	 * // try { // cleanCacheService.toCleancache(); // } catch (Exception e) {
	 * // e.printStackTrace(); // }
	 * 
	 * }
	 */

	// -----------------------------------------------------------------------------修改过后的新增方法
	// start-----------------------------------------

	// 新增收单商户
	public void addAcqMerchant(Map<String, String> params) throws SQLException {
		// String acq_enname = "eptok";
		String acq_enname = params.get("acq_enname");
		;
		String acq_merchant_no = params.get("acq_merchant_no");
		String acq_merchant_name = params.get("acq_merchant_name");
		String merchant_no = params.get("merchant_no");
		String agent_no = params.get("agent_no");
		String mcc = params.get("mcc");
		String fee_rate = params.get("fee_rate");
		// String fee_cap_amount = params.get("fee_cap_amount");
		String fee_max_amount = params.get("fee_max_amount");
		// String fee_single_amount = params.get("fee_single_amount");
		// String create_time = params.get("create_time");

		String fee_type = params.get("fee_type");
		String ladder_min = params.get("ladder_min");
		String ladder_value = params.get("ladder_value");
		String ladder_max = params.get("ladder_max");

		String merchant_rate = params.get("merchant_Drate");
		String merchant_max_amount = params.get("merchant_max_amount");
		String merchant_min = params.get("merchant_min");
		String merchant_value = params.get("merchant_value");
		String merchant_max = params.get("merchant_max");

		String large_small_flag = params.get("large_small_flag");

		String rate_type = params.get("rate_type");

		String special_fee_rate = params.get("fee_rate_ForCa");
		
		String rep_pay = params.get("rep_pay");

		if (!"".equals(special_fee_rate) && special_fee_rate != null) {
			fee_rate = special_fee_rate;
		}

		String sql = " insert into acq_merchant"
				+ " (acq_enname,acq_merchant_no,acq_merchant_name,merchant_no,agent_no,mcc,"
				+ "fee_type,fee_rate,fee_cap_amount,fee_max_amount,ladder_fee,rate_type,large_small_flag,create_time,merchant_rate,rep_pay) "
				+ " values " + " (?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?) ";
		
		List<Object> list = new ArrayList<Object>();

		list.add(acq_enname);
		list.add((acq_merchant_no == null || acq_merchant_no.trim().length() == 0) ? null
				: acq_merchant_no.trim());
		list.add((acq_merchant_name == null || acq_merchant_name.trim()
				.length() == 0) ? null : acq_merchant_name.trim());
		list.add(merchant_no);
		list.add((agent_no == null || agent_no.trim().length() == 0) ? null
				: agent_no);
		list.add((mcc == null || mcc.trim().length() == 0) ? null : mcc.trim());
		// list.add((fee_max_amount ==null ||fee_max_amount.trim().length() ==0
		// || new BigDecimal(fee_max_amount).doubleValue() == 0)
		// ?"RATIO":"CAPPING");
		// list.add(fee_rate ==null ?null:new
		// BigDecimal(fee_rate).movePointLeft(2));
		// list.add((fee_max_amount ==null||fee_max_amount.trim().length() ==0)
		// ?null:new BigDecimal(fee_max_amount));

		list.add(fee_type);
		if ("RATIO".equals(fee_type)) {
			list.add(fee_rate == null ? null : new BigDecimal(fee_rate)
					.movePointLeft(2));
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);

		} else if ("CAPPING".equals(fee_type)) {
			list.add(fee_rate == null ? null : new BigDecimal(fee_rate)
					.movePointLeft(2));

			list.add((fee_rate == null || fee_max_amount == null) ? null
					: new BigDecimal(fee_max_amount).divide(new BigDecimal(
							fee_rate).movePointLeft(2), 2,
							BigDecimal.ROUND_HALF_UP));

			list.add((fee_max_amount == null || fee_max_amount.trim().length() == 0) ? null
					: new BigDecimal(fee_max_amount));

			list.add(null);

			list.add(rate_type);

		} else if ("LADDER".equals(fee_type)) {

			ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
			ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

			String ladder_fee = ladder_min + "<" + ladder_value + "<"
					+ ladder_max;

			list.add(null);
			list.add(null);
			list.add(null);
			list.add(ladder_fee);
			list.add(rate_type);

		}

		list.add(large_small_flag);

		if ("RATIO".equals(fee_type)) {
			list.add(merchant_rate == null ? null : new BigDecimal(
					merchant_rate).movePointLeft(2));

		} else if ("CAPPING".equals(fee_type)) {
			DecimalFormat df = new DecimalFormat("#.00");
			list.add((merchant_max_amount == null || merchant_max_amount.trim()
					.length() == 0) ? null : new BigDecimal(merchant_rate)
					.movePointLeft(2)
					+ "~"
					+ df.format(new BigDecimal(merchant_max_amount)));

		} else if ("LADDER".equals(fee_type)) {

			merchant_min = new BigDecimal(merchant_min).movePointLeft(2)
					.toString();
			merchant_max = new BigDecimal(merchant_max).movePointLeft(2)
					.toString();

			String merchant_fee = merchant_min + "<" + merchant_value + "<"
					+ merchant_max;
			list.add(merchant_fee);

		}
		list.add(rep_pay);
		
		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
		
			dao.updateByTranscation(sql, list.toArray(),conn);
			
			addAcqMerchantCreateLog(conn, "2", list);
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("新增收单商户未知异常=" + e.getMessage());
			e.printStackTrace();
		} finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("新增收单商户关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}

		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}
	
	private void addAcqMerchantModifyLog(Connection conn, String oper_type, List<Object> list) throws SQLException{
		
		String sql = " insert into acq_merchant_log"
			+ " (acq_enname,acq_merchant_no,acq_merchant_name,merchant_no,agent_no,mcc,"
			+ "fee_type,fee_rate,fee_cap_amount,fee_max_amount,ladder_fee,large_small_flag,merchant_rate,rep_pay,"
			+ "oper_time,oper_id,oper,oper_type) "
			+ " values " + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ";
		
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.remove(list.size() - 1);
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(oper_type);
		
		dao.updateByTranscation(sql, list.toArray(), conn);
	}
	
	// 收单商户操作日志
	private void addAcqMerchantCreateLog(Connection conn, String oper_type, List<Object> list) throws SQLException{
		String sql = " insert into acq_merchant_log"
			+ " (acq_enname,acq_merchant_no,acq_merchant_name,merchant_no,agent_no,mcc,"
			+ "fee_type,fee_rate,fee_cap_amount,fee_max_amount,ladder_fee,rate_type,large_small_flag,merchant_rate,rep_pay,"
			+ "oper_time,oper_id,oper,oper_type) "
			+ " values " + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?) ";
		
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add(oper_type);
		
		dao.updateByTranscation(sql, list.toArray(), conn);
	}

	// ---------------------------------------------------------------------------------------------------------end

	// 修改收单商户信息
	public void modifyAcqMerchant(Map<String, String> params)
			throws SQLException {
		String id = params.get("id");
		String acq_enname = params.get("acq_enname");
		;
		String acq_merchant_no = params.get("acq_merchant_no");
		String acq_merchant_name = params.get("acq_merchant_name");
		String merchant_no = params.get("merchant_no");
		String agent_no = params.get("agent_no");
		String mcc = params.get("mcc");

		String fee_type = params.get("fee_type");
		String fee_rate = params.get("fee_rate");
		String fee_max_amount = params.get("fee_max_amount");

		String ladder_min = params.get("ladder_min");
		String ladder_value = params.get("ladder_value");
		String ladder_max = params.get("ladder_max");

		String merchant_rate = params.get("merchant_Drate");
		String merchant_max_amount = params.get("merchant_max_amount");

		String merchant_min = params.get("merchant_min");
		String merchant_value = params.get("merchant_value");
		String merchant_max = params.get("merchant_max");

		// String fee_single_amount = params.get("fee_single_amount");
		// String create_time = params.get("create_time");
		String large_small_flag = params.get("large_small_flag");
		String rep_pay = params.get("rep_pay");
		/*
		 * String sql = " update  acq_merchant set " +
		 * " acq_enname= ?,acq_merchant_no= ?,acq_merchant_name= ?,merchant_no= ?,agent_no= ?,"
		 * +
		 * " mcc= ?,fee_type= ?,fee_rate= ?,fee_max_amount= ? ,large_small_flag=? "
		 * + " where id = ?";
		 */

		String sql = " update  acq_merchant set "
				+ " acq_enname= ?,acq_merchant_no= ?,acq_merchant_name= ?,merchant_no= ?,agent_no= ?, mcc= ?,"
				+ "fee_type= ?,fee_rate= ?,fee_cap_amount=?,fee_max_amount= ?,ladder_fee=?,large_small_flag=?,merchant_rate=?,rep_pay=? "
				+ " where id = ?";
		List<Object> list = new ArrayList<Object>();

		// list.add(id);
		list.add(acq_enname);
		list.add((acq_merchant_no == null || acq_merchant_no.trim().length() == 0) ? null
				: acq_merchant_no.trim());
		list.add((acq_merchant_name == null || acq_merchant_name.trim()
				.length() == 0) ? null : acq_merchant_name.trim());
		list.add(merchant_no);
		list.add((agent_no == null || agent_no.trim().length() == 0) ? null
				: agent_no);
		list.add((mcc == null || mcc.trim().length() == 0) ? null : mcc.trim());
		list.add(fee_type);
		if ("RATIO".equals(fee_type)) {
			list.add(fee_rate == null ? null : new BigDecimal(fee_rate)
					.movePointLeft(2));
			list.add(null);
			list.add(null);
			list.add(null);

		} else if ("CAPPING".equals(fee_type)) {
			list.add(fee_rate == null ? null : new BigDecimal(fee_rate)
					.movePointLeft(2));
			list.add((fee_rate == null || fee_max_amount == null) ? null
					: new BigDecimal(fee_max_amount).divide(new BigDecimal(
							fee_rate).movePointLeft(2), 2,
							BigDecimal.ROUND_HALF_UP));
			list.add((fee_max_amount == null || fee_max_amount.trim().length() == 0) ? null
					: new BigDecimal(fee_max_amount));
			list.add(null);

		} else if ("LADDER".equals(fee_type)) {

			ladder_min = new BigDecimal(ladder_min).movePointLeft(2).toString();
			ladder_max = new BigDecimal(ladder_max).movePointLeft(2).toString();

			String ladder_fee = ladder_min + "<" + ladder_value + "<"
					+ ladder_max;

			list.add(null);
			list.add(null);
			list.add(null);
			list.add(ladder_fee);

		}

		list.add(large_small_flag);

		if ("RATIO".equals(fee_type)) {
			list.add(merchant_rate == null ? null : new BigDecimal(
					merchant_rate).movePointLeft(2));

		} else if ("CAPPING".equals(fee_type)) {
			DecimalFormat df = new DecimalFormat("#.00");
			list.add((merchant_rate == null || merchant_max_amount == null) ? null
					: new BigDecimal(merchant_rate).movePointLeft(2) + "~"
							+ df.format(new BigDecimal(merchant_max_amount)));
		} else if ("LADDER".equals(fee_type)) {

			merchant_min = new BigDecimal(merchant_min).movePointLeft(2)
					.toString();
			merchant_max = new BigDecimal(merchant_max).movePointLeft(2)
					.toString();

			String merchant_fee = merchant_min + "<" + merchant_value + "<"
					+ merchant_max;
			list.add(merchant_fee);

		}
		list.add(rep_pay);
		list.add(id);

		Connection conn = null;
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			dao.updateByTranscation(sql, list.toArray(), conn);
			
			addAcqMerchantModifyLog(conn, "0", list);
		
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			log.error("修改收单商户未知异常=" + e.getMessage());
			e.printStackTrace();
		} finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("修改收单商户关闭连接异常，原因="+e.getMessage());
				e.printStackTrace();
			}
		}
		
		//
		// try {
		// cleanCacheService.toCleancache();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	// 收单商户信息
	public Map<String, Object> getAcqMerchantDetail(String acq_merchant_no,
			String acq_terminal_no) {
		List<Object> list = new ArrayList<Object>();
		list.add(acq_merchant_no);
		list.add(acq_terminal_no);
		String sql = "select t.acq_terminal_no, acq.acq_merchant_no,acq.acq_merchant_name , acq.merchant_no,p.merchant_name,t.acq_enname ,a.agent_name ,acq.agent_no from acq_merchant acq left  "
				+ "join agent_info a on acq.agent_no=a.agent_no LEFT JOIN pos_merchant p ON acq.merchant_no = p.merchant_no , acq_terminal t  "
				+ "where t.acq_merchant_no=acq.acq_merchant_no and acq.acq_merchant_no=? and t.acq_terminal_no=? ";
		Map<String, Object> terInfo = dao.findFirst(sql, list.toArray());
		return terInfo;
	}

	// 通过商户号查询银盛商户信息
	public Map<String, Object> getAcqMerByNo(String acqMerNo) {
		List<Object> list = new ArrayList<Object>();
		list.add(acqMerNo);
		String sql = "select u.* from acq_merchant u where  u.acq_merchant_no=?";
		Map<String, Object> acqMerInfo = dao.findFirst(sql, list.toArray());
		return acqMerInfo;
	}

	// 通过终端号查询此条记录是否存在
	public boolean getAcqTerByTerNo(String terno) {
		List<Object> list = new ArrayList<Object>();
		list.add(terno);
		String sql = "select * from acq_terminal where  acq_terminal_no=?";
		Map<String, Object> acqMerInfo = dao.findFirst(sql, list.toArray());
		return (acqMerInfo != null && acqMerInfo.size() > 0) ? false : true;
	}

	// 通过收单机构商户号查询此条记录是否存在
	public boolean getAcqMerByAcqMerchantNo(String acq_merchant_no) {
		List<Object> list = new ArrayList<Object>();
		list.add(acq_merchant_no);
		String sql = "select * from acq_merchant where acq_merchant_no=?";
		Map<String, Object> acqMerInfo = dao.findFirst(sql, list.toArray());
		return (acqMerInfo != null && acqMerInfo.size() > 0) ? true : false;
	}

	// 通过收单机构商户名称查询此条记录是否存在
	public boolean getAcqMerByAcqMerchantName(String acq_merchant_name) {
		List<Object> list = new ArrayList<Object>();
		list.add(acq_merchant_name);
		String sql = "select * from acq_merchant where acq_merchant_name=?";
		Map<String, Object> acqMerInfo = dao.findFirst(sql, list.toArray());
		return (acqMerInfo != null && acqMerInfo.size() > 0) ? true : false;
	}

	// 通过收单机构商户号和ID查询此条记录是否存在
	public boolean getAcqMerByAcqMerchantNoModify(String acq_merchant_no,
			String id) {
		List<Object> list = new ArrayList<Object>();
		list.add(acq_merchant_no);
		list.add(id);
		String sql = "select * from acq_merchant where acq_merchant_no=? and id!=?";
		Map<String, Object> acqMerInfo = dao.findFirst(sql, list.toArray());
		return (acqMerInfo != null && acqMerInfo.size() > 0) ? true : false;
	}

	// 通过收单机构商户名称和ID查询此条记录是否存在
	public boolean getAcqMerByAcqMerchantNameModify(String acq_merchant_name,
			String id) {
		List<Object> list = new ArrayList<Object>();
		list.add(acq_merchant_name);
		list.add(id);
		String sql = "select * from acq_merchant where acq_merchant_name=? and id!=?";
		Map<String, Object> acqMerInfo = dao.findFirst(sql, list.toArray());
		return (acqMerInfo != null && acqMerInfo.size() > 0) ? true : false;
	}

	// 查看该收单机构下是否已有此普通商户
	public boolean isMerExist(String merchant_no) {
		List<Object> list = new ArrayList<Object>();
		list.add(merchant_no);
		String sql = "SELECT count(*) count FROM acq_merchant WHERE merchant_no = ?";
		Long count = (Long) dao.findFirst(sql, list.toArray()).get("count");
		return (count > 0) ? true : false;
	}

	// 查看该收单机构下是否已有此普通商户
	public boolean isMerExistByModify(String merchant_no, String id) {
		List<Object> list = new ArrayList<Object>();
		list.add(merchant_no);
		list.add(id);
		String sql = "SELECT count(*) count FROM acq_merchant WHERE merchant_no = ? and id!=?";
		Long count = (Long) dao.findFirst(sql, list.toArray()).get("count");
		return (count > 0) ? true : false;
	}

	// 更新收单机构商户终端锁定状态为正常
	public int updateAcqTerminalLock() throws SQLException {
		String sql = "update acq_terminal set locked=? where locked=? and acq_enname='eptok'";
		Object[] ps = { 0, 1 };
		int n = dao.update(sql, ps);
		return n;
	}

	// 更新收单机构商户锁定状态为正常
	public int updateAcqMerchantLock() throws SQLException {
		String sql = "update acq_merchant set locked=? where locked=? and acq_enname='eptok'";
		Object[] ps = { 0, 1 };
		int n = dao.update(sql, ps);
		return n;
	}

	// ------------------------------------------新增两个修改状态方法--------------------------------------
	/**
	 * 更新收单机构终端锁定状态
	 * 
	 * @param locked
	 * @param acq_merchant_no
	 * @author LJ
	 * @return
	 * @throws SQLException
	 */
	public int updateTerminalLocked(String locked, String id)
			throws SQLException {
		String sql = "update acq_terminal set locked=? where id=?";
		Object[] ps = { locked, id };
		int n = dao.update(sql, ps);
		return n;
	}

	/**
	 * 更新收单机构终端锁定状态
	 * 
	 * @param locked
	 * @param acq_merchant_no
	 * @author LJ
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getAcqMerchantLocked(String id)
			throws SQLException {
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		String sql = "select t.*,m.acq_merchant_name from acq_terminal t,acq_merchant m where t.acq_merchant_no=m.acq_merchant_no and  t.id=?";
		Map<String, Object> results = dao.findFirst(sql, list.toArray());
		return results;
	}

	public Page<Map<String, Object>> getAcqMerchant4Export( Map<String, String> params, PageRequest pageRequest) {
		
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agent_no"))) {
				if (!params.get("agent_no").equals("-1")) {
					String agent_no = params.get("agent_no");
					sb.append(" and a.agent_no = ? ");
					list.add(agent_no);
				}
			}

			if (StringUtils.isNotEmpty(params.get("acq_merchant_no"))) {
				sb.append(" and acq.acq_merchant_no = ? ");
				list.add(params.get("acq_merchant_no"));

			}

			// 大套小标志
			if (StringUtils.isNotEmpty(params.get("large_small_flag"))) {
				if (!params.get("large_small_flag").equals("-1")) {
					sb.append(" and acq.large_small_flag = ? ");
					list.add(params.get("large_small_flag"));
				}
			}

			// 收单机构商户名称查询
			if (StringUtils.isNotEmpty(params.get("acq_merchant"))) {

				String merchant_name = params.get("acq_merchant");
				sb.append(" and (acq.acq_merchant_name = ?  or acq.acq_merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
			}
			// 普通商户名称/编号查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {

				String merchant = params.get("merchant");
				sb.append(" and (p.merchant_name = ?  or p.merchant_no = ?) ");
				list.add(merchant);
				list.add(merchant);
			}

			// 收单机构英文名称
			if (StringUtils.isNotEmpty(params.get("acq_enname"))) {
				String acq_enname = params.get("acq_enname");
				sb.append(" and acq_enname=? ");
				list.add(acq_enname);
			}

			// 锁定状态
			if (StringUtils.isNotEmpty(params.get("locked"))) {
				if (!params.get("locked").equals("-1")) {
					String locked = params.get("locked");
					sb.append(" and acq.locked=?");
					list.add(locked);
				}
			}

		}

		String sql = "select acq.acq_enname,acq.acq_merchant_no, acq.acq_merchant_name,acq.locked,acq.create_time,acq.fee_type,acq.fee_rate,acq.fee_max_amount,acq.merchant_rate,acq.ladder_fee, "
				+ " a.agent_no, a.agent_name, p.merchant_no, p.merchant_name, p.address, p.city, p.province, p.checker, p.real_flag, p.my_settle, p.bag_settle, " 
				+ " f.fee_type as merchant_fee_type,f.fee_rate as merchant_fee_rate,f.fee_max_amount as merchant_fee_max_amount,f.ladder_fee as merchant_ladder_fee"
				+ " from acq_merchant acq left join agent_info a on acq.agent_no = a.agent_no "
				+ " left join pos_merchant p on acq.merchant_no = p.merchant_no "
				+ " left join pos_merchant_fee f on acq.merchant_no = f.merchant_no"
				+ " where 1 = 1 "
				+ sb.toString() + " order by acq.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	public List<Map<String, Object>> getAcqOrg() {
		return dao.find("select acq_cnname,acq_enname from acq_org");
	}
	
	public List<Map<String,Object>> getCheckerList()  {
		return dao.find("select b.real_name,b.user_name from merchant_check_person m,boss_user b where m.user_id=b.id group by m.user_id");
	}

	public Map<String, Object> getPosMerchantWithAcqMerchant(Long id) {

		return dao.findFirst("select pm.bag_settle, pm.my_settle from pos_merchant pm where pm.merchant_no = (select merchant_no from acq_merchant am where am.id = ? )", new Object[]{id});
	}


	public void openMerchant(String acq_merchant_no) throws Exception{
		String selectAcqMerchant = "select acq_merchant_no, merchant_no,acq_enname from acq_merchant where acq_merchant_no = ?";
		String openAcqMerchant = "update acq_merchant set locked = 0 where acq_merchant_no = ? ";
		String insertAcqMerchantLog = "insert into acq_merchant_log(acq_merchant_no,merchant_no,locked,oper_time,oper_id,oper,oper_type) values(?,?,?,now(),?,?,?)";
		String updPosMerchant = "update pos_merchant set my_settle = 0, bag_settle = '0' where merchant_no = ? ";

		Map<String, Object> acqMerchant = dao.findFirst(selectAcqMerchant, new Object[]{acq_merchant_no});

		if(acqMerchant != null){
			Connection conn = null;
			try {
				conn = dao.getConnection();
				conn.setAutoCommit(false);

				// 判断是否好乐付的收单商户
				if(acqMerchant.get("merchant_no") != null && acqMerchant.get("acq_enname") != null && "halpay".equals(acqMerchant.get("acq_enname").toString())){
					dao.updateByTranscation(updPosMerchant, new Object[]{acqMerchant.get("merchant_no")}, conn);
					addPosMerchantLogNew(acqMerchant.get("merchant_no").toString(), conn);
				}

				// 打开收单商户
				dao.updateByTranscation(openAcqMerchant, new Object[]{acq_merchant_no}, conn);

				// 记录操作日志
				BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
				dao.updateByTranscation(insertAcqMerchantLog, new Object[]{acqMerchant.get("acq_merchant_no").toString(), acqMerchant.get("merchant_no").toString(), 0, bu.getId(), bu.getRealName(), 0}, conn);

				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				conn.rollback();
				throw new RuntimeException(e);
			}finally{
				conn.close();
			}
		}
	}

	// 根据Id查询商户信息
	public void addPosMerchantLogNew(String merchant_no, Connection conn) throws SQLException {
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

		} else {
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);
			list.add(null);
		}
			/*
			 * list.add(params.get("fee_type")); list.add(params.get("fee_rate"));
			 * list.add(params.get("fee_cap_amount"));
			 * list.add(params.get("fee_max_amount"));
			 */
		// list.add(params.get("fee_single_amount"));
		// list.add(params.get("ladder_fee"));
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

		// list.add(params.get("ed_max_amount"));
		// list.add(params.get("ed_card_max_items"));
		// list.add(params.get("ed_card_max_amount"));
		// list.add(params.get("ed_total_amount"));
		// list.add(params.get("operate_time"));
		// 操作员信息
		BossUser bu = (BossUser) SecurityUtils.getSubject().getPrincipal();
		list.add("开启好乐付收单商户关闭优质商户和钱包结算");
		list.add(bu.getId());
		list.add(bu.getRealName());
		list.add("1");
		list.add(0);

		list.add(params.get("clear_card_no"));
		list.add(params.get("pos_type"));
		list.add(params.get("add_type"));
		list.add(params.get("trans_cancel"));
		list.add(params.get("pay_method"));
		list.add("0");
		list.add(params.get("bus_license_no"));
		list.add(params.get("app_no"));

		String insert = "INSERT INTO pos_merchant_log (" + "merchant_no, " + "merchant_name, " + "merchant_short_name, " + "lawyer, " + "merchant_type, " + "mobile_username, " + "open_status, " + "agent_no, " + "address, " + "sale_address, " + "province, " + "city, " + "link_name, " + "phone, " + "email, " + "mcc, " + "sale_name, " + "settle_cycle, " + "bank_name, " + "account_no, "
				+ "account_name, " + "cnaps_no, " + "account_type, " + "attachment, " + "real_flag, " + "id_card_no, " + "terminal_count, " + "main_business, " + "remark, " + "code_word, " + "belong_to_agent, " + "trans_time_start, " + "trans_time_end, " + "fee_type, " + "fee_rate, " + "fee_cap_amount, " + "fee_max_amount, " + "fee_single_amount, " + "ladder_fee, " + "terminal_no, "
				+ "single_max_amount, " + "ed_max_amount, " + "ed_card_max_items, " + "ed_card_max_amount, " + "ed_total_amount, " + "operate_time, " + "oper_desc, " + "oper_user_id, " + "oper_user_name, " + "oper_type, " + "my_settle,"
				+ "clear_card_no, pos_type, add_type, trans_cancel, pay_method, bag_settle, bus_license_no, app_no "
				+")" + "VALUES" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?)";

		dao.updateByTranscation(insert, list.toArray(), conn);
	}
}
