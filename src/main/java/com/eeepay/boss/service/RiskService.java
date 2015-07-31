package com.eeepay.boss.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.SysConfig;

/**
 * 系统用户及权限管理
 * 
 * @author dj
 * 
 */
@Service
public class RiskService {
	@Resource
	private Dao dao;

	// 查询代理商
	public Page<Map<String, Object>> getRuleParameterList(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 参数编号
			if (StringUtils.isNotEmpty(params.get("parameter"))) {
			/*	String merchant_name = "%" + params.get("parameter") + "%";
				sb.append(" and (p.parameter_name like ?  or p.parameter_no like ?) ");*/
				String merchant_name =params.get("parameter");
				sb.append(" and (p.parameter_name = ?  or p.parameter_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
			/*	list.add(merchant_name);*/
			}

		}

		String sql = "select p.* from  risk_rule_parameter p where 1=1 "
				+ sb.toString() + " order by p.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	public int paramSave(Map<String, String> params) throws SQLException {
		String parameter_no = params.get("parameter_no");
		String parameter_name = params.get("parameter_name");
		String parameter_method = "ruleMethod" + parameter_no;
		String sql = "insert into risk_rule_parameter(parameter_no,parameter_name,parameter_method,create_time) values(?,?,?,now())";
		int num = dao.update(sql, new Object[] { parameter_no, parameter_name,
				parameter_method });
		return num;
	}

	public int paramModify(Map<String, String> params) throws SQLException {
		String parameter_name = params.get("parameter_name");
		String id = params.get("id");
		String sql = "update risk_rule_parameter set parameter_name=? where id=?";
		int num = dao.update(sql,
				new Object[] { parameter_name, Long.parseLong(id) });
		return num;
	}

	// 风险商户查询
	public Page<Map<String, Object>> getRuleResultList(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 参数编号
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				/*String merchant_name = "%" + params.get("merchant") + "%";
				sb.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or r.merchant_no like ?) ");*/
				String merchant_name =params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or r.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}

			// 案例编号
			if (StringUtils.isNotEmpty(params.get("case_no"))) {
				String case_no = params.get("case_no");
				sb.append(" and r.case_no =? ");
				list.add(case_no);
			}

			// 开通状态
			if (StringUtils.isNotEmpty(params.get("risk_class"))) {
				String risk_class = params.get("risk_class");
				if (!"-1".equals(risk_class)) {
					sb.append(" and r.risk_class =? ");
					list.add(risk_class);
				}
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and r.create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and r.create_time <=? ");
				list.add(createTimeEnd);
			}
		}

		String sql = "select r.* , m.merchant_short_name,m.merchant_name from  risk_result r ,pos_merchant m  where r.merchant_no=m.merchant_no "
				+ sb.toString() + " order by r.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	public int riskMerchantSave(Map<String, String> params) throws SQLException {
		String case_no = params.get("case_no");
		String terminal_no = params.get("terminal_no");
		String merchant_no = params.get("merchant_no");
		String risk_class = params.get("risk_class");
		String case_type = "1";

		String sql = "insert into risk_result(case_no,terminal_no,merchant_no,risk_class,case_type,create_time)"
				+ " values(?,?,?,?,?,?,now())";
		int num = dao.update(sql, new Object[] { case_no, terminal_no,
				merchant_no, risk_class, case_type });
		return num;
	}

	public int riskMerchantModify(Map<String, String> params)
			throws SQLException {
		String case_no = params.get("case_no");
		String terminal_no = params.get("terminal_no");
		String merchant_no = params.get("merchant_no");
		String risk_class = params.get("risk_class");
		String id = params.get("id");
		String sql = "update risk_result set case_no=?,terminal_no=?,merchant_no=?,risk_class=? where id=?";
		int num = dao.update(sql, new Object[] { case_no, terminal_no,
				merchant_no, risk_class, Long.parseLong(id) });
		return num;
	}

	// 删除风险商户
	public int deleteRiskMerchant(long id) throws SQLException {
		String sql = "delete from risk_result where id=?";
		return dao.update(sql, id);
	}

	// 信用卡大额交易笔数
	public Map<String, Object> ruleMethod001(Map<String, String> params)
			throws SQLException {

		List<Object> list = new ArrayList<Object>();
		String risk_amount = SysConfig.value("risk_amount");
		String timeMark = params.get("timeMark");
		StringBuffer sb = new StringBuffer();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String createTime = sdf.format(date);
		String createTimeBegin = createTime + " 00:00";
		String createTimeEnd = createTime + " 23:59";
		list.add(risk_amount);
		if (timeMark != null && "day".equals(timeMark)) {
			sb.append(" and t.trans_time >= ? and t.trans_time <=? ");
			list.add(createTimeBegin);
			list.add(createTimeEnd);
		}
		if (timeMark != null && "month".equals(timeMark)) {
			sb.append(" and date_format(t.trans_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')");
		}
		String sql = "SELECT count(*) methodresult FROM trans_info t WHERE t.card_type='CREDIT_CARD' "
				+ "and t.trans_type='PURCHASE'and t.trans_amount>=? and t.trans_status='SUCCESS'"
				+ sb.toString();
		return dao.findFirst(sql, list);
	}

	// 信用卡交易总笔数
	public Map<String, Object> ruleMethod002(Map<String, String> params)
			throws SQLException {

		List<Object> list = new ArrayList<Object>();
		String timeMark = params.get("timeMark");
		StringBuffer sb = new StringBuffer();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String createTime = sdf.format(date);
		String createTimeBegin = createTime + " 00:00";
		String createTimeEnd = createTime + " 23:59";
		if (timeMark != null && "day".equals(timeMark)) {
			sb.append(" and t.trans_time >= ? and t.trans_time <=? ");
			list.add(createTimeBegin);
			list.add(createTimeEnd);
		}
		if (timeMark != null && "month".equals(timeMark)) {
			sb.append(" and date_format(t.trans_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')");
		}
		String sql = "SELECT count(*) methodresult FROM trans_info t WHERE t.card_type='CREDIT_CARD' "
				+ "and t.trans_type='PURCHASE' and t.trans_status='SUCCESS'"
				+ sb.toString();
		return dao.findFirst(sql, list);
	}

	// 风险预警筛选
	public List<Map<String, Object>> riskWarnQuery(Map<String, String> params)
			throws SQLException {
		String trans_amount = params.get("trans_amount");
		String warnCount = params.get("warnCount");
		List<Object> list = new ArrayList<Object>();
		list.add(trans_amount);
		list.add(warnCount);
		String sql = "SELECT  big_amount_count,sum_amount_count,tb.merchant_no,tb.terminal_no,tb.agent_no,(big_amount_count/sum_amount_count) AS ratio "
				+ "FROM (SELECT COUNT(*) AS big_amount_count,t.terminal_no,t.merchant_no,t.agent_no FROM trans_info t WHERE t.trans_amount>? AND "
				+ " t.card_type='CREDIT_CARD' AND t.trans_type = 'PURCHASE' AND t.trans_status = 'SUCCESS' AND t.trans_time >ADDDATE(CURDATE(), INTERVAL -1 DAY) AND t.trans_time < CURDATE() GROUP BY t.terminal_no) AS tb "
				+ "INNER JOIN "
				+ "(SELECT COUNT(*) AS sum_amount_count,terminal_no FROM trans_info WHERE  card_type='CREDIT_CARD' AND trans_type = 'PURCHASE' AND trans_status = 'SUCCESS'  and trans_time >ADDDATE(CURDATE(), INTERVAL -1 DAY) AND trans_time < CURDATE() GROUP BY terminal_no) AS ts "
				+ "ON tb.terminal_no = ts.terminal_no WHERE (big_amount_count/sum_amount_count)>=?";
		return dao.find(sql, list.toArray());
	}

	// 风险结果新增
	public int riskResultAdd(Map<String, Object> params) throws SQLException {
		String case_no = (String) params.get("case_no");
		String group_no = (String) params.get("group_no");
		String terminal_no = (String) params.get("terminal_no");
		String merchant_no = (String) params.get("merchant_no");
		String risk_class = (String) params.get("risk_class");
		String case_type = (String) params.get("case_type");
		String agent_no = (String) params.get("agent_no");
		String sql = "INSERT INTO risk_result(case_no,group_no,terminal_no,merchant_no,risk_class,case_type,create_time,agent_no)VALUES(?,?,?,?,?,?,now(),?)";
		List<Object> list = new ArrayList<Object>();
		list.add(case_no);
		list.add(group_no);
		list.add(terminal_no);
		list.add(merchant_no);
		list.add(risk_class);
		list.add(case_type);
		list.add(agent_no);
		int num = dao.update(sql, list.toArray());
		return num;
	}

	public List<String> getTimeOutRecord() {
		List<String> list = new ArrayList<String>();
		String sql = "select acq_code,acq_cnname,acq_enname from acq_org";
		if (null == this.dao) {
			this.dao = new Dao();
		}
		List<Map<String, Object>> acq_codeList = this.dao.find(sql);
		for (Map<String, Object> map : acq_codeList) {
			String acq_code = (String) map.get("acq_code");
			String acq_enname = (String) map.get("acq_enname");

			String sql2 = "select count(trans_status) as countnum from ( select id,acq_code,acq_enname,trans_status from trans_info ti where ti.acq_code = ?  order  by id  desc limit 0,5 ) te where te.trans_status = 'INIT'";

			Map<String, Object> data = this.dao.findFirst(sql2, acq_code);
			int countnum = Integer.parseInt(data.get("countnum").toString());
			if (countnum == 5) {
				map.put(acq_enname, Integer.valueOf(countnum));
				list.add(acq_enname);
			}
		}
		return list;
	}

}
