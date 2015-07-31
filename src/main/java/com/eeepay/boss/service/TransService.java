package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.enums.TransStatus;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.OrderUtil;
import com.eeepay.boss.utils.StringUtil;

/**
 * 交易查询，可以查询代理商及子代理商交易数据
 * 
 * @author dj
 */
@Service
public class TransService {
	@Resource
	private Dao dao;

	/**
	 * 添加打印小票记录
	 * 
	 * @param params
	 * @return
	 */
	public int addTransReceipt(Map<String, Object> params) {
		int addCount = 0;
		if (null != params) {
			boolean check = true;
			String sql = "insert into trans_receipt(trans_id,acq_merchant_name,acq_merchant_no,terminal_no,account_no,bank_name,trans_type,"
					+ "refer_no,auth_no,trans_date_time,amount,order_no,merchant_no,create_person,acq_batch_no) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try {
				if (StringUtil.isEmpty(params.get("trans_id").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("acq_merchant_name")
						.toString())) {
					check = false;
				}

				if (StringUtil
						.isEmpty(params.get("acq_merchant_no").toString())) {
					check = false;
				}

				if (StringUtil
						.isEmpty(params.get("acq_terminal_no").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("account_no").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("bank_nameT").toString())) {
					check = false;
				}
				String trans_type = "";
				if (StringUtil.isEmpty(params.get("trans_type").toString())) {
					check = false;
				} else {
					trans_type = params.get("trans_type").toString();
				}

				String acq_reference_no = "";

				if (null != params.get("acq_reference_no")
						&& !"".equals(params.get("acq_reference_no").toString())) {
					acq_reference_no = params.get("acq_reference_no")
							.toString();
				}

				String acq_auth_no = "";

				if (null != params.get("acq_auth_no")
						&& !"".equals(params.get("acq_auth_no").toString())) {
					acq_auth_no = params.get("acq_auth_no").toString();
				}

				if (StringUtil.isEmpty(params.get("trans_time").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("trans_amount").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("trans_id").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("merchant_no").toString())) {
					check = false;
				}

				if (StringUtil.isEmpty(params.get("create_person").toString())) {
					check = false;
				}

				if (check) {
					if ("PURCHASE".equals(trans_type)) {
						trans_type = "消费";
					} else if ("PURCHASE_VOID".equals(trans_type)) {
						trans_type = "消费撤销";
					} else if ("PURCHASE_REFUND".equals(trans_type)) {
						trans_type = "退货";
					} else if ("REVERSED".equals(trans_type)) {
						trans_type = "冲正";
					} else if ("BALANCE_QUERY".equals(trans_type)) {
						trans_type = "余额查询";
					} else {
						trans_type = "其他:" + trans_type;
					}
					List<String> list = new ArrayList<String>();
					list.add(params.get("trans_id").toString());
					list.add(params.get("acq_merchant_name").toString());
					list.add(params.get("acq_merchant_no").toString());
					list.add(params.get("acq_terminal_no").toString());
					list.add(params.get("account_no").toString());
					list.add(params.get("bank_nameT").toString());
					list.add(trans_type);

					list.add(acq_reference_no);
					list.add(acq_auth_no);
					// list.add(params.get("trans_type").toString());

//					list.add(acq_reference_no); //收单机构参考号
//					list.add(acq_auth_no); //收单机构授权码

					list.add(params.get("trans_time").toString());
					list.add(params.get("trans_amount").toString());
					list.add(params.get("trans_id").toString());
					list.add(params.get("merchant_no").toString());
					list.add(params.get("create_person").toString());
					list.add(params.get("acq_batch_no").toString());
					addCount = dao.update(sql, list.toArray());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
		return addCount;
	}
	
	public int clearTransReceipt(String time, String username)  throws SQLException{
		String sql  = "delete from trans_receipt where trans_date_time<=? and create_person = ?";
		List<String> param = new ArrayList<String>();
		param.add(time);
		param.add(username);
		return  dao.update(sql, param.toArray());
	}
	
	public List<Map<String, Object>> getTransReceipt(String username){
		String sql = "select acq_merchant_name,acq_merchant_no,terminal_no,account_no,bank_name,trans_type,acq_batch_no,refer_no,auth_no," +
				"date_format(trans_date_time,'%Y-%m-%d %H:%i:%s') as trans_date_time,concat('',amount) amount,order_no from trans_receipt where create_person = ? order by create_time desc  ";
		return dao.find(sql, new Object[]{username});
	}

	public Map<String, Object> saleCountTransFast(Map<String, String> params) {
		Map<String, Object> totalMsg = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

//			// 商户名称查询
//			if (StringUtils.isNotEmpty(params.get("merchant_name"))) {
//				String merchant_name = params.get("merchant_name");
//				sb.append(" and m.merchant_name = ? ");
//				list.add(merchant_name);
//			}
//			// 商户编号
//			if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
//				String merchant_no = params.get("merchant_no");
//				sb.append(" and  m.merchant_no = ? ");
//				list.add(merchant_no);
//			}
//
//			if (StringUtils.isNotEmpty(params.get("status"))) {
//				sb.append(" and t.status=? ");
//				list.add(params.get("status"));
//			}
//
//			// 代理商编号
//			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
//				if (!params.get("agentNo").equals("-1")) {
//					String agentNo = params.get("agentNo");
//					sb.append(" and a.agent_no = ? ");
//					list.add(agentNo);
//				}
//			}
//
//			if (StringUtils.isNotEmpty(params.get("orderNo"))) {
//				String orderNo = params.get("orderNo");
//				sb.append(" and t.order_no = ? ");
//				list.add(orderNo);
//			}
//			// 收单机构
//			if (StringUtils.isNotEmpty(params.get("acqEnname"))) {
//				sb.append(" and t.acq_enname=? ");
//				list.add(params.get("acqEnname"));
//			}
//
//			if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
//				String acqMerchantNo = params.get("acqMerchantNo");
//				sb.append(" and t.acq_merchant_no =? ");
//				list.add(acqMerchantNo);
//			}
//
//			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
//				String createTimeBegin = params.get("createTimeBegin") + ":00";
//				sb.append(" and t.create_time >=? ");
//				list.add(createTimeBegin);
//
//			}
//
//			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
//				String createTimeEnd = params.get("createTimeEnd") + ":59";
//				sb.append(" and t.create_time <=? ");
//				list.add(createTimeEnd);
//			}
//			// 卡类型
//			if (StringUtils.isNotEmpty(params.get("cardType"))) {
//				String cardType = params.get("cardType");
//				if (!"-1".equals(cardType)) {
//					sb.append(" and t.card_type =? ");
//					list.add(cardType);
//				}
//			}
//
//			// 手机号
//			if (StringUtils.isNotEmpty(params.get("mobile_username"))) {
//				String mobile_username = params.get("mobile_username");
//				sb.append(" and m.mobile_username =? ");
//				list.add(mobile_username);
//			}

			// 交易卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {

				String cardNo = params.get("cardNo");
				sb.append(" and t.card_no = ? ");
				list.add(cardNo);
			}
			// 订单编号
			if (StringUtils.isNotEmpty(params.get("orderNo"))) {
				String orderNo = params.get("orderNo");
				sb.append(" and t.order_no = ? ");
				list.add(orderNo);
			}

			// 收单机构
			if (StringUtils.isNotEmpty(params.get("acqEnname"))
					&& !"-1".equals(params.get("acqEnname"))) {
				sb.append(" and t.acq_enname=? ");
				list.add(params.get("acqEnname"));
			} 

			// 交易时间
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and t.create_time <=? ");
				list.add(createTimeEnd);
			}
			// 商户
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				/* String merchant_name = "%" + params.get("merchant") + "%"; */
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			// 状态
			if (StringUtils.isNotEmpty(params.get("status"))) {
				if(params.get("status").equals("已冻结")){
					sb.append(" and f.freeze_status=? ");
					list.add("1");
				}else{
					sb.append(" and f.status=? ");
					list.add(params.get("status"));
				}
			}
			// sale_name
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");
				sb.append(" and (a.sale_name =? ");
				list.add(sale_name);
				sb.append(" or m.sale_name =?) ");
				list.add(sale_name);
			}
		}

		String totalSql = "select count(*) total_count from fastpay_trans_info t,pos_merchant m, agent_info a  where "
				+ "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  "
				+ sb.toString() + " order by t.id desc";

		String totalPurSql = "select count(*) total_pur_count, sum(t.amount) total_amount from fastpay_trans_info t,pos_merchant m, agent_info a where "
				+ "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no "
				+ sb.toString() + " order by t.id desc";

		String totalPurAmountSql = "select sum(t.amount) total_pur_amount , sum(t.merchant_fee) total_mer_fee from fastpay_trans_info t,pos_merchant m, agent_info a where"
				+ " t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  "
				+ sb.toString() + " order by t.id desc";

		Map<String, Object> totalCount = dao
				.findFirst(totalSql, list.toArray());
		Map<String, Object> totalPurCount = dao.findFirst(totalPurSql,
				list.toArray());
		Map<String, Object> totalPurAmount = dao.findFirst(totalPurAmountSql,
				list.toArray());

		if (totalCount != null) {
			if (totalCount.get("total_count") != null) {
				totalMsg.put("total_count", totalCount.get("total_count"));
			} else {
				totalMsg.put("total_count", 0);
			}
		}
		if (totalPurCount != null) {
			if (totalPurCount.get("total_pur_count") != null) {
				totalMsg.put("total_pur_count",
						totalPurCount.get("total_pur_count"));
			} else {
				totalMsg.put("total_pur_count", 0);
			}
		}

		if (totalPurAmount != null) {
			if (totalPurAmount.get("total_pur_amount") != null) {
				totalMsg.put("total_pur_amount",
						totalPurAmount.get("total_pur_amount"));
			} else {
				totalMsg.put("total_pur_amount", "0.00");
			}

			if (totalPurAmount.get("total_mer_fee") != null) {
				totalMsg.put("total_mer_fee",
						totalPurAmount.get("total_mer_fee"));
			} else {
				totalMsg.put("total_mer_fee", "0.00");
			}
		}

		return totalMsg;
	}

	/**
	 * 销售管理 快捷交易查询
	 * @author 王帅
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getSaleTransFastPat(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {
				String cardNo = params.get("cardNo");
				sb.append(" and f.card_no = ? ");
				list.add(cardNo);
			}
			if (StringUtils.isNotEmpty(params.get("orderNo"))) {
				String orderNo = params.get("orderNo");
				sb.append(" and f.order_no = ? ");
				list.add(orderNo);
			}

			if (StringUtils.isNotEmpty(params.get("acqEnname"))
					&& !"-1".equals(params.get("acqEnname"))) {
				sb.append(" and f.acq_enname=? ");
				list.add(params.get("acqEnname"));
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and f.create_time >=? ");
				list.add(createTimeBegin);
			}
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and f.create_time <=? ");
				list.add(createTimeEnd);
			}
			if (StringUtils.isNotEmpty(params.get("merchant_name"))) {
				String merchant_name = params.get("merchant_name");
				sb.append(" and m.merchant_name = ? ");
				list.add(merchant_name);
			}
			// 商户编号
			if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
				String merchant_no = params.get("merchant_no");
				sb.append(" and  m.merchant_no = ? ");
				list.add(merchant_no);
			}
			if (StringUtils.isNotEmpty(params.get("status"))) {
				if(params.get("status").equals("已冻结")){
					sb.append(" and f.freeze_status=? ");
					list.add("1");
				}else{
					sb.append(" and f.status=? ");
					list.add(params.get("status"));
				}
			}

			// sale_name
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");
				sb.append(" and (a.sale_name =? ");
				list.add(sale_name);
				sb.append(" or m.sale_name =?) ");
				list.add(sale_name);
			}
		}
		String sql = "select f.*,m.merchant_short_name,m.merchant_no from fastpay_trans_info f,pos_merchant m,agent_info a  where m.merchant_no = f.merchant_no and  m.agent_no = a.agent_no "
				+ sb.toString() + " order by f.id desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	/**
	 * 
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getTransFast(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 订单id查询
			// 因为jsp页面提交的id不是数据库中的订单id，而是数据库中createTime的yyyyMMdd+OrderUtil.buildOrderId(id,createTime))组合未来。所以这里要拆分jsp提交的参数。
			if (StringUtils.isNotEmpty(params.get("id"))) {
				String idFromJsp = params.get("id");
				String[] createTimeAndId = OrderUtil.chaiFeiOrderId(idFromJsp);
				// String createTime = createTimeAndId[0];
				String id = createTimeAndId[1];
				String queryid = id;
				sb.append(" and t.id = ? ");
				list.add(queryid);
			}
			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant_name"))) {
				String merchant_name = params.get("merchant_name");
				sb.append(" and m.merchant_name = ? ");
				list.add(merchant_name);
			}
			// 商户编号
			if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
				String merchant_no = params.get("merchant_no");
				sb.append(" and  m.merchant_no = ? ");
				list.add(merchant_no);
			}
			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentNo);
				}
			}
			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
				String acqMerchantNo = params.get("acqMerchantNo");
				sb.append(" and t.acq_merchant_no =? ");
				list.add(acqMerchantNo);
			}

			if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
				String acqTerminalNo = params.get("acqTerminalNo");
				sb.append(" and t.acq_terminal_no =? ");
				list.add(acqTerminalNo);
			}
			// 终端号
			if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
				String terminalNo = params.get("terminalNo");
				sb.append(" and t.terminal_no =? ");
				list.add(terminalNo);
			}
			// 卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {
				String cardNo = params.get("cardNo");
				sb.append(" and t.account_no = ? ");
				list.add(cardNo);
			}
			// 参考号
			if (StringUtils.isNotEmpty(params.get("referenceNo"))) {
				String referenceNo = params.get("referenceNo");
				sb.append(" and t.acq_reference_no =? ");
				list.add(referenceNo);
			}
			// 手机号
			if (StringUtils.isNotEmpty(params.get("mobile_username"))) {
				String mobile_username = params.get("mobile_username");
				sb.append(" and m.mobile_username =? ");
				list.add(mobile_username);
			}
			// 交易类型
			if (StringUtils.isNotEmpty(params.get("transType"))) {
				String transType = params.get("transType");
				if (!"-1".equals(transType)) {
					sb.append(" and t.trans_type =? ");
					list.add(transType);
				}
			}
			// 交易状态
			if (StringUtils.isNotEmpty(params.get("transStatus"))) {
				String transStatus = params.get("transStatus");
				if (!"-1".equals(transStatus)) {
					sb.append(" and t.trans_status =? ");
					list.add(transStatus);
				}
			}
			// 卡类型
			if (StringUtils.isNotEmpty(params.get("cardType"))) {
				String cardType = params.get("cardType");
				if (!"-1".equals(cardType)) {
					sb.append(" and t.card_type =? ");
					list.add(cardType);
				}
			}
			// 交易来源
			if (StringUtils.isNotEmpty(params.get("transSource"))) {
				String transSource = params.get("transSource");
				if (!"-1".equals(transSource)) {
					sb.append(" and t.trans_source =? ");
					list.add(transSource);
				}
			}
			// 收单机构
			if (StringUtils.isNotEmpty(params.get("ACQ_ENNAME"))) {
				String acq_enname = params.get("ACQ_ENNAME");
				if (!"-1".equals(acq_enname)) {
					sb.append(" and o.ACQ_ENNAME =? ");
					list.add(acq_enname);
				}
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") ;
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);
			}
			// sale_name
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");
				sb.append(" and (a.sale_name =? ");
				list.add(sale_name);
				sb.append(" or m.sale_name =?) ");
				list.add(sale_name);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and t.create_time <=? ");
				list.add(createTimeEnd);
			}
			String littleTreasure = params.get("review_status");
			if (StringUtils.isNotEmpty(littleTreasure)
					&& !"-1".equals(littleTreasure)) {
				sb.append(" and t.review_status=?");
				list.add(littleTreasure);
			}
		}
		StringBuffer sql = new StringBuffer(
				"select m.merchant_short_name,m.mcc mermcc , m.mobile_username ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,o.ACQ_CNNAME,o.ACQ_ENNAME,t.* ");
		sql.append(" from acq_org o,trans_info t,pos_merchant m,agent_info a  where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and t.acq_enname=o.ACQ_ENNAME");
		sql.append(sb.toString());
		sql.append("order by t.id desc ");
		return dao.find(sql.toString(), list.toArray(), pageRequest);
	}

	/**
	 * 按 日期，获取 当天交易总金额，以及总交易笔数
	 * 
	 * @param date
	 * @return
	 */
	public Map<String, Object> countTrade(String date) {
		String sql = "select count(*) as trans_num ,sum(t.trans_amount) trans_amount "
				+ "from trans_info t,pos_merchant m,agent_info a "
				+ "where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no "
				+ "and t.trans_type ='PURCHASE' and t.trans_status ='SUCCESS' and DATE_FORMAT(t.create_time,'%Y-%m-%d')=?";
		// String
		// sql="select sum(trans_amount) trans_amount,count(*) trans_num from trans_info where DATE_FORMAT(create_time,'%Y-%m-%d')=?"
		// +
		// "and trans_type = 'PURCHASE' and trans_status = 'SUCCESS'";
		return dao.findFirst(sql, date);
	}

	// 组合条件查询交易数据
	public Page<Map<String, Object>> getTransFastPat(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 交易卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {

				String cardNo = params.get("cardNo");
				sb.append(" and f.card_no = ? ");
				list.add(cardNo);
			}
			// 订单编号
			if (StringUtils.isNotEmpty(params.get("orderNo"))) {
				String orderNo = params.get("orderNo");
				sb.append(" and f.order_no = ? ");
				list.add(orderNo);
			}

			// 收单机构
			if (StringUtils.isNotEmpty(params.get("acqEnname"))
					&& !"-1".equals(params.get("acqEnname"))) {
				sb.append(" and f.acq_enname=? ");
				list.add(params.get("acqEnname"));
			} 

			// 交易创建时间
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and f.create_time >=? ");
				list.add(createTimeBegin);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and f.create_time <=? ");
				list.add(createTimeEnd);
			}
			// 商户
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				/* String merchant_name = "%" + params.get("merchant") + "%"; */
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			// 交易 状态
			if (StringUtils.isNotEmpty(params.get("status"))) {
				sb.append(" and f.status=? ");
				list.add(params.get("status"));
			}
			
			String amountBegin = params.get("start_trans_amount");
			if (StringUtils.isNotEmpty(amountBegin)) {
				sb.append(" and f.amount>=? ");
				list.add(amountBegin);
			}
			
			String amountEnd = params.get("end_trans_amount");
			if (StringUtils.isNotEmpty(amountEnd)) {
				sb.append(" and f.amount<=? ");
				list.add(amountEnd);
			}
			//冻结 状态
			if (StringUtils.isNotEmpty(params.get("freezeStatus"))) {
				String freezeStatus = params.get("freezeStatus");
				if(!freezeStatus.equals("-1")){
					sb.append(" and f.freeze_status=? ");
					list.add(freezeStatus);
				}
				
			}

			
		}
		String sql = "select f.*,m.merchant_short_name,m.merchant_no from fastpay_trans_info f,pos_merchant m  where m.merchant_no = f.merchant_no "
				+ sb.toString() + " order by f.id desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}


	// 组合条件查询交易数据
	public Page<Map<String, Object>> getTrans(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 订单id查询
			// 因为jsp页面提交的id不是数据库中的订单id，而是数据库中createTime的yyyyMMdd+OrderUtil.buildOrderId(id,createTime))组合未来。所以这里要拆分jsp提交的参数。
			if (StringUtils.isNotEmpty(params.get("id"))) {
				String idFromJsp = params.get("id");
				String[] createTimeAndId = OrderUtil.chaiFeiOrderId(idFromJsp);
				//String createTime = createTimeAndId[0];
				String id = createTimeAndId[1];

				String queryid = id;
				sb.append(" and t.id = ? ");
				list.add(queryid);
			}

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}

			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentNo);
				}
			}

			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			// 收单商户
			if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
				String acqMerchantNo = params.get("acqMerchantNo");
				sb.append(" and t.acq_merchant_no =? ");
				list.add(acqMerchantNo);
			}

			// //
			// if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
			// String acqTerminalNo = params.get("acqTerminalNo");
			// sb.append(" and t.acq_terminal_no =? ");
			// list.add(acqTerminalNo);
			// }
			// // 终端号
			// if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
			// String terminalNo = params.get("terminalNo");
			// sb.append(" and t.terminal_no =? ");
			// list.add(terminalNo);
			// }

			// 卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {
				String cardNo = params.get("cardNo");
				sb.append(" and t.account_no = ? ");
				list.add(cardNo);
			}
			// 参考号
			if (StringUtils.isNotEmpty(params.get("referenceNo"))) {
				String referenceNo = params.get("referenceNo");
				sb.append(" and t.acq_reference_no =? ");
				list.add(referenceNo);
			}

			// 手机号
			if (StringUtils.isNotEmpty(params.get("mobile_username"))) {
				String mobile_username = params.get("mobile_username");
				sb.append(" and m.mobile_username =? ");
				list.add(mobile_username);
			}

			// 交易类型
			if (StringUtils.isNotEmpty(params.get("transType"))
					&& !"-1".equals(params.get("transType"))) {
				String transType = params.get("transType");
				sb.append(" and t.trans_type =? ");
				list.add(transType);
			} 

			// 交易状态
			if (StringUtils.isNotEmpty(params.get("transStatus"))) {
				String transStatus = params.get("transStatus");
				if(transStatus.equals("FREEZED")){
					sb.append(" and t.freeze_status =? ");
					list.add("1");
				}else if (!"-1".equals(transStatus)) {
					sb.append(" and t.trans_status =? ");
					list.add(transStatus);
				} 
			}
			
			
			// 冻结状态
			if (StringUtils.isNotEmpty(params.get("freezeStatus"))) {
				String transStatus = params.get("freezeStatus");
				if(!transStatus.equals("-1")){
					sb.append(" and t.freeze_status =? ");
					list.add(transStatus);
				}
			}

			// 卡类型
			if (StringUtils.isNotEmpty(params.get("cardType"))) {
				String cardType = params.get("cardType");
				if (!"-1".equals(cardType)) {
					sb.append(" and t.card_type =? ");
					list.add(cardType);
				}
			}

			// 交易来源
			if (StringUtils.isNotEmpty(params.get("transSource"))
					&& !"-1".equals(params.get("transSource"))) {
				String transSource = params.get("transSource");
				sb.append(" and t.trans_source =? ");
				list.add(transSource);
			} 

			// 收单机构
			if (StringUtils.isNotEmpty(params.get("ACQ_ENNAME"))
					&& !"-1".equals(params.get("ACQ_ENNAME"))) {
				String acq_enname = params.get("ACQ_ENNAME");
				sb.append(" and o.ACQ_ENNAME =? ");
				list.add(acq_enname);
			}

			// address

			if (StringUtils.isNotEmpty(params.get("address"))) {
				String address = "%" + params.get("address") + "%";
				sb.append(" and (ptil.address like ? or mrd.province like ? or mrd.city like ? or mrd.stree like ?)");
				list.add(address);
				list.add(address);
				list.add(address);
				list.add(address);
			}

			// params.put("createTimeBegin","2012-06-04 00:00:00");
			if (StringUtils.isNotEmpty(params.get("transTimeBegin"))) {
				String createTimeBegin = params.get("transTimeBegin");
				sb.append(" and t.trans_time >=? ");
				list.add(createTimeBegin);
			}
			
			

			 // sale_name
			 if (StringUtils.isNotEmpty(params.get("sale_name"))) {
			 String sale_name = params.get("sale_name");
			 sb.append(" and (a.sale_name =? ");
			 list.add(sale_name);
			 sb.append(" or m.sale_name =?) ");
			 list.add(sale_name);
			 }

			if (StringUtils.isNotEmpty(params.get("transTimeEnd"))) {
				String createTimeEnd = params.get("transTimeEnd");
				sb.append(" and t.trans_time <=? ");
				list.add(createTimeEnd);
			}

			
			// params.put("createTimeBegin","2012-06-04 00:00:00");
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);
			}
			
			
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeBegin = params.get("createTimeEnd");
				sb.append(" and t.create_time <=? ");
				list.add(createTimeBegin);
			}
			// 电子小票
			if (StringUtils.isNotEmpty(params.get("review_status"))
					&& !"-1".equals(params.get("review_status"))) {
				String littleTreasure = params.get("review_status");
				if (littleTreasure.equals("2")) {
					sb.append(" and (t.review_status=? or t.review_status is null) ");
				} else {
					sb.append(" and t.review_status=? ");
				}

				list.add(littleTreasure);
			} 

			/*String trans_amount = params.get("trans_amount");
			if (StringUtils.isNotEmpty(trans_amount)	&& trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount = ? ");
				list.add(new BigDecimal(trans_amount));
			}*/
			String checkTimeBegin = params.get("checkTimeBegin");
			if(StringUtils.isNotEmpty(checkTimeBegin)){
				sb.append(" and sign_check_time>=?");
				list.add(checkTimeBegin);
			}
			
			String checkTimeEnd = params.get("checkTimeEnd");
			if(StringUtils.isNotEmpty(checkTimeEnd)){
				sb.append(" and sign_check_time<=?");
				list.add(checkTimeEnd);
			}
			
			String checkPerson= params.get("checkPerson");
			if(StringUtils.isNotEmpty(checkPerson) && !"-1".equals(checkPerson)){
				sb.append(" and sign_check_person=?");
				list.add(checkPerson);
			}
			
			String start_trans_amount = params.get("start_trans_amount");
			if (StringUtils.isNotEmpty(start_trans_amount)	&& start_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount >= ? ");
				list.add(new BigDecimal(start_trans_amount));
			}
			
			
			String end_trans_amount = params.get("end_trans_amount");
			if (StringUtils.isNotEmpty(end_trans_amount)	&& end_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount <= ? ");
				list.add(new BigDecimal(end_trans_amount));
			}
			
			
		}
		/*
		 * String sql =
		 * "select m.merchant_short_name,m.mcc mermcc , m.mobile_username , " +
		 * "a.agent_name, " + " t.*" +
		 * " from trans_info t  ,pos_merchant m,agent_info a " +
		 * " where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no " +
		 * sb.toString() + " order by t.id desc";
		 */
//		String sql = "select m.merchant_short_name,m.mcc mermcc , m.mobile_username ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,o.ACQ_CNNAME,o.ACQ_ENNAME,t.* "
//				+ "from acq_org o,trans_info t,pos_merchant m,agent_info a  "
//				+ "where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and t.acq_enname=o.ACQ_ENNAME "
//				+ sb.toString() + " order by t.id desc";
		String sql = "select m.merchant_short_name,m.mcc mermcc , m.mobile_username ,CONCAT(IFNULL(a.agent_name,''),' ', IFNULL(a.brand_type,'') ) as agent_name,o.ACQ_CNNAME,o.ACQ_ENNAME, "
				    + " t.trans_type, t.card_type, t.account_no, t.trans_amount, t.trans_status, t.freeze_status, t.create_time, t.id, t.review_status, t.merchant_no, t.merchant_fee, t.bag_settle, t.merchant_settle_date, t.trans_source  "
					+ " from trans_info t" 
					+ " left join pos_merchant m on t.merchant_no = m.merchant_no "
					+ " left join agent_info a on m.agent_no = a.agent_no "
					+ " left join acq_org o on t.acq_enname=o.acq_enname "
					+ " where 1 = 1 "
					+ sb.toString()
					+ " order by t.id desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 组合条件查询交易数据ForExport
	public Page<Map<String, Object>> getTransForExport(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 订单id查询
			// 因为jsp页面提交的id不是数据库中的订单id，而是数据库中createTime的yyyyMMdd+OrderUtil.buildOrderId(id,createTime))组合未来。所以这里要拆分jsp提交的参数。
			if (StringUtils.isNotEmpty(params.get("id"))) {
				String idFromJsp = params.get("id");
				String[] createTimeAndId = OrderUtil.chaiFeiOrderId(idFromJsp);
				String createTime = createTimeAndId[0];
				String id = createTimeAndId[1];

				String queryid = id;
				sb.append(" and (t.id = ?) ");
				list.add(queryid);
			}

			// 如果有勾选的ids，则还要添加上ids
			if (StringUtils.isNotEmpty(params.get("ids"))) {
				String ids = params.get("ids");
				String array[] = ids.split(",");
				String idsDBUtilsNeed = "";
				for (int i = 0; i < array.length; i++) {
					idsDBUtilsNeed = idsDBUtilsNeed + "'" + array[i] + "',";
				}
				idsDBUtilsNeed = idsDBUtilsNeed.endsWith(",") ? idsDBUtilsNeed
						.substring(0, idsDBUtilsNeed.length() - 1)
						: idsDBUtilsNeed;
				sb.append(" and  t.id in (" + idsDBUtilsNeed + ")");

			}

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}

			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentNo);
				}
			}

			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			// 收单机构
			if (StringUtils.isNotEmpty(params.get("ACQ_ENNAME"))) {
				String acq_enname = params.get("ACQ_ENNAME");
				if (!"-1".equals(acq_enname)) {
					sb.append(" and t.ACQ_ENNAME =? ");
					list.add(acq_enname);
				}
			}

			if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
				String acqMerchantNo = params.get("acqMerchantNo");
				sb.append(" and t.acq_merchant_no =? ");
				list.add(acqMerchantNo);
			}

			if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
				String acqTerminalNo = params.get("acqTerminalNo");
				sb.append(" and t.acq_terminal_no =? ");
				list.add(acqTerminalNo);
			}
			// 终端号
			if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
				String terminalNo = params.get("terminalNo");
				sb.append(" and t.terminal_no =? ");
				list.add(terminalNo);
			}
			// 卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {
				String cardNo = params.get("cardNo");
				/* sb.append(" and t.account_no like ? "); */
				sb.append(" and t.account_no = ? ");
				list.add(cardNo);
			}
			// 参考号
			if (StringUtils.isNotEmpty(params.get("referenceNo"))) {
				String referenceNo = params.get("referenceNo");
				sb.append(" and t.acq_reference_no =? ");
				list.add(referenceNo);
			}
			// 交易类型
			if (StringUtils.isNotEmpty(params.get("transType"))) {
				String transType = params.get("transType");
				if (!"-1".equals(transType)) {
					sb.append(" and t.trans_type =? ");
					list.add(transType);
				}
			}

			// 交易状态
			if (StringUtils.isNotEmpty(params.get("transStatus"))) {
				String transStatus = params.get("transStatus");
				if (!"-1".equals(transStatus)) {
					sb.append(" and t.trans_status =? ");
					list.add(transStatus);
				}
			}
			// 卡类型
			if (StringUtils.isNotEmpty(params.get("cardType"))) {
				String cardType = params.get("cardType");
				if (!"-1".equals(cardType)) {
					sb.append(" and t.card_type =? ");
					list.add(cardType);
				}
			}
			// 交易来源
			if (StringUtils.isNotEmpty(params.get("transSource"))) {
				String transSource = params.get("transSource");
				if (!"-1".equals(transSource)) {
					sb.append(" and t.trans_source =? ");
					list.add(transSource);
				}
			}
			// params.put("createTimeBegin","2012-06-04 00:00:00");
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);
			}
			
			
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeBegin = params.get("createTimeEnd");
				sb.append(" and t.create_time <=? ");
				list.add(createTimeBegin);
			}
			
			if (StringUtils.isNotEmpty(params.get("transTimeBegin"))) {
				String createTimeBegin = params.get("transTimeBegin");
				sb.append(" and t.trans_time >=? ");
				list.add(createTimeBegin);
			}
			
			if (StringUtils.isNotEmpty(params.get("transTimeEnd"))) {
				String createTimeEnd = params.get("transTimeEnd");
				sb.append(" and t.trans_time <=? ");
				list.add(createTimeEnd);
			}
			// sale_name
			if (StringUtils.isNotEmpty(params.get("sale_name"))) {
				String sale_name = params.get("sale_name");
				sb.append(" and (a.sale_name =? ");
				list.add(sale_name);

				sb.append(" or m.sale_name =?) ");
				list.add(sale_name);
			}

			
			String littleTreasure = params.get("review_status");
			if (StringUtils.isNotEmpty(littleTreasure)
					&& !"-1".equals(littleTreasure)) {
				sb.append(" and t.review_status=?");
				list.add(littleTreasure);
			}

			String start_trans_amount = params.get("start_trans_amount");
			if (StringUtils.isNotEmpty(start_trans_amount)	&& start_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount >= ? ");
				list.add(new BigDecimal(start_trans_amount));
			}
			
			
			String end_trans_amount = params.get("end_trans_amount");
			if (StringUtils.isNotEmpty(end_trans_amount)	&& end_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount <= ? ");
				list.add(new BigDecimal(end_trans_amount));
			}

			String checkTimeBegin = params.get("checkTimeBegin");
			if(StringUtils.isNotEmpty(checkTimeBegin)){
				sb.append(" and t.sign_check_time>=?");
				list.add(checkTimeBegin);
			}

			String checkTimeEnd = params.get("checkTimeEnd");
			if(StringUtils.isNotEmpty(checkTimeEnd)){
				sb.append(" and t.sign_check_time<=?");
				list.add(checkTimeEnd);
			}

			String checkPerson= params.get("checkPerson");
			if(StringUtils.isNotEmpty(checkPerson) && !"-1".equals(checkPerson)){
				sb.append(" and t.sign_check_person=?");
				list.add(checkPerson);
			}

		}
//		String sql = "select  m.merchant_short_name,m.mcc mermcc , a.agent_name, "
//				+ " t.*,CONCAT(arc.response_text,'(',arc.response_code,')') as response_code,"
//				+ " pcb.bank_name, pcb.card_name, o.acq_cnname, am.acq_merchant_name"
//				+ " from trans_info t,pos_merchant m,agent_info a,acq_org o, acq_response_code arc, pos_card_bin pcb, acq_merchant am"
//				+ " where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  and t.acq_enname=o.ACQ_ENNAME and arc.response_code = t.acq_response_code"
//				+ " and pcb.card_length = length(t.account_no) and pcb.verify_code = left(t.account_no,  pcb.verify_length) "
//				+ " and am.acq_enname = o.acq_enname and am.acq_merchant_no = t.acq_merchant_no "
//				+ sb.toString() + " order by t.id desc";
		String sql = "select distinct m.merchant_short_name, m.mcc mermcc, a.agent_name, "
			+ " t.*,CONCAT(arc.response_text,'(',arc.response_code,')') as response_code, "
			+ " pcb.bank_name, pcb.card_name, o.acq_cnname, am.acq_merchant_name "
			+ " from trans_info t " 
			+ " left join pos_merchant m on t.merchant_no = m.merchant_no "
			+ " left join agent_info a on m.agent_no = a.agent_no "
			+ " left join acq_org o on t.acq_enname=o.acq_enname " 
			+ " left join acq_response_code arc on arc.response_code = t.acq_response_code "
			+ " left join (select * from pos_card_bin group by card_length,verify_code,verify_length) pcb on pcb.card_length = length(t.account_no) and pcb.verify_code = left(t.account_no,  pcb.verify_length) "
			+ " left join acq_merchant am on am.acq_merchant_no = t.acq_merchant_no "
			+ " where 1 = 1 "
			+ sb.toString()
			+ " order by t.id desc";

		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 统计交易信息
	public Map<String, Object> countTransInfo(Map<String, String> params) {

		Map<String, Object> totalMsg = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 订单id查询
			// 因为jsp页面提交的id不是数据库中的订单id，而是数据库中createTime的yyyyMMdd+OrderUtil.buildOrderId(id,createTime))组合未来。所以这里要拆分jsp提交的参数。
			if (StringUtils.isNotEmpty(params.get("id"))) {
				String idFromJsp = params.get("id");
				String[] createTimeAndId = OrderUtil.chaiFeiOrderId(idFromJsp);
				//String createTime = createTimeAndId[0];
				String id = createTimeAndId[1];

				String queryid = id;
				sb.append(" and t.id = ? ");
				list.add(queryid);
			}

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}

			// 代理商编号
			if (StringUtils.isNotEmpty(params.get("agentNo"))) {
				if (!params.get("agentNo").equals("-1")) {
					String agentNo = params.get("agentNo");
					sb.append(" and a.agent_no = ? ");
					list.add(agentNo);
				}
			}

			// 代理商名称查询
			if (StringUtils.isNotEmpty(params.get("agentName"))) {
				String merchantName = params.get("agentName");
				sb.append(" and a.agent_name=?  ");
				list.add(merchantName);
			}

			// 收单商户
			if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
				String acqMerchantNo = params.get("acqMerchantNo");
				sb.append(" and t.acq_merchant_no =? ");
				list.add(acqMerchantNo);
			}

			// //
			// if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
			// String acqTerminalNo = params.get("acqTerminalNo");
			// sb.append(" and t.acq_terminal_no =? ");
			// list.add(acqTerminalNo);
			// }
			// // 终端号
			// if (StringUtils.isNotEmpty(params.get("terminalNo"))) {
			// String terminalNo = params.get("terminalNo");
			// sb.append(" and t.terminal_no =? ");
			// list.add(terminalNo);
			// }

			// 卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {
				String cardNo = params.get("cardNo");
				sb.append(" and t.account_no = ? ");
				list.add(cardNo);
			}
			// 参考号
			if (StringUtils.isNotEmpty(params.get("referenceNo"))) {
				String referenceNo = params.get("referenceNo");
				sb.append(" and t.acq_reference_no =? ");
				list.add(referenceNo);
			}

			// 手机号
			if (StringUtils.isNotEmpty(params.get("mobile_username"))) {
				String mobile_username = params.get("mobile_username");
				sb.append(" and m.mobile_username =? ");
				list.add(mobile_username);
			}

			// 交易类型
			if (StringUtils.isNotEmpty(params.get("transType"))
					&& !"-1".equals(params.get("transType"))) {
				String transType = params.get("transType");
				sb.append(" and t.trans_type =? ");
				list.add(transType);
			} 

			// 交易状态
			if (StringUtils.isNotEmpty(params.get("transStatus"))) {
				String transStatus = params.get("transStatus");
				if (!"-1".equals(transStatus)) {
					sb.append(" and t.trans_status =? ");
					list.add(transStatus);
				} 
			}
			
			// 冻结状态
			if (StringUtils.isNotEmpty(params.get("freezeStatus"))) {
				String transStatus = params.get("freezeStatus");
				if(!transStatus.equals("-1")){
					sb.append(" and t.freeze_status =? ");
					list.add(transStatus);
				}
			}

			// 卡类型
			if (StringUtils.isNotEmpty(params.get("cardType"))) {
				String cardType = params.get("cardType");
				if (!"-1".equals(cardType)) {
					sb.append(" and t.card_type =? ");
					list.add(cardType);
				}
			}

			// 交易来源
			if (StringUtils.isNotEmpty(params.get("transSource"))
					&& !"-1".equals(params.get("transSource"))) {
				String transSource = params.get("transSource");
				sb.append(" and t.trans_source =? ");
				list.add(transSource);
			} 

			// 收单机构
			if (StringUtils.isNotEmpty(params.get("ACQ_ENNAME"))
					&& !"-1".equals(params.get("ACQ_ENNAME"))) {
				String acq_enname = params.get("ACQ_ENNAME");
				sb.append(" and o.ACQ_ENNAME =? ");
				list.add(acq_enname);
			}

			// address

			if (StringUtils.isNotEmpty(params.get("address"))) {
				String address = "%" + params.get("address") + "%";
				sb.append(" and (ptil.address like ? or mrd.province like ? or mrd.city like ? or mrd.stree like ?)");
				list.add(address);
				list.add(address);
				list.add(address);
				list.add(address);
			}

			// params.put("createTimeBegin","2012-06-04 00:00:00");
			if (StringUtils.isNotEmpty(params.get("transTimeBegin"))) {
				String createTimeBegin = params.get("transTimeBegin");
				sb.append(" and t.trans_time >=? ");
				list.add(createTimeBegin);
			}

			 // sale_name
			 if (StringUtils.isNotEmpty(params.get("sale_name"))) {
			 String sale_name = params.get("sale_name");
			 sb.append(" and (a.sale_name =? ");
			 list.add(sale_name);
			 sb.append(" or m.sale_name =?) ");
			 list.add(sale_name);
			 }

			if (StringUtils.isNotEmpty(params.get("transTimeEnd"))) {
				String createTimeEnd = params.get("transTimeEnd");
				sb.append(" and t.trans_time <=? ");
				list.add(createTimeEnd);
			}
			
			
			// params.put("createTimeBegin","2012-06-04 00:00:00");
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);
			}
			
			
			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeBegin = params.get("createTimeEnd");
				sb.append(" and t.create_time <=? ");
				list.add(createTimeBegin);
			}
			String checkTimeBegin = params.get("checkTimeBegin");
			if(StringUtils.isNotEmpty(checkTimeBegin)){
				sb.append(" and sign_check_time>=?");
				list.add(checkTimeBegin);
			}
			
			String checkTimeEnd = params.get("checkTimeEnd");
			if(StringUtils.isNotEmpty(checkTimeEnd)){
				sb.append(" and sign_check_time<=?");
				list.add(checkTimeEnd);
			}
			
			String checkPerson= params.get("checkPerson");
			if(StringUtils.isNotEmpty(checkPerson) && !"-1".equals(checkPerson)){
				sb.append(" and sign_check_person=?");
				list.add(checkPerson);
			}
			
			String start_trans_amount = params.get("start_trans_amount");
			if (StringUtils.isNotEmpty(start_trans_amount)	&& start_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount >= ? ");
				list.add(new BigDecimal(start_trans_amount));
			}
			
			
			String end_trans_amount = params.get("end_trans_amount");
			if (StringUtils.isNotEmpty(end_trans_amount)	&& end_trans_amount	.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount <= ? ");
				list.add(new BigDecimal(end_trans_amount));
			}

			// 电子小票
			if (StringUtils.isNotEmpty(params.get("review_status"))
					&& !"-1".equals(params.get("review_status"))) {
				String littleTreasure = params.get("review_status");
				if (littleTreasure.equals("2")) {
					sb.append(" and (t.review_status=? or t.review_status is null) ");
				} else {
					sb.append(" and t.review_status=? ");
				}

				list.add(littleTreasure);
			} 

			String trans_amount = params.get("trans_amount");
			if (StringUtils.isNotEmpty(trans_amount)
					&& trans_amount
							.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")) {
				sb.append(" and trans_amount = ? ");
				list.add(new BigDecimal(trans_amount));
			}
		}
		/*
		 * String totalSql =
		 * "select count(*) total_count from trans_info t,pos_merchant m, agent_info a,acq_org o  where "
		 * +
		 * "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  and  t.acq_enname=o.ACQ_ENNAME  "
		 * + sb.toString() + " order by t.id desc";
		 * 
		 * String totalPurSql =
		 * "select count(*) total_pur_count, sum(t.trans_amount) total_amount from trans_info t,pos_merchant m, agent_info a,acq_org o  where "
		 * +
		 * "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and t.trans_type = 'PURCHASE' and t.trans_status = 'SUCCESS'  and  t.acq_enname=o.ACQ_ENNAME "
		 * + sb.toString() + " order by t.id desc";
		 * 
		 * String totalPrvSql =
		 * "select count(*) total_prv_count, sum(t.trans_amount) total_amount from trans_info t,pos_merchant m, agent_info a,acq_org o  where "
		 * +
		 * "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and t.trans_type = 'PURCHASE_VOID' and t.trans_status = 'SUCCESS'  and  t.acq_enname=o.ACQ_ENNAME "
		 * + sb.toString() + " order by t.id desc";
		 * 
		 * String totalRefSql =
		 * "select count(*) total_ref_count, sum(t.trans_amount) total_amount from trans_info t,pos_merchant m, agent_info a,acq_org o  where "
		 * +
		 * "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  and t.trans_type = 'PURCHASE_REFUND' and t.trans_status = 'SUCCESS'  and  t.acq_enname=o.ACQ_ENNAME "
		 * + sb.toString() + " order by t.id desc";
		 * 
		 * String totalPurAmountSql =
		 * "select sum(t.trans_amount) total_pur_amount , sum(t.merchant_fee) total_mer_fee from trans_info t,pos_merchant m, agent_info a where"
		 * +
		 * " t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  and t.trans_type = 'PURCHASE' and t.trans_status = 'SUCCESS' "
		 * + sb.toString() + " order by t.id desc";
		 */

		String totalSql = "select count(t.id) total_count from trans_info t "+
						" left join pos_merchant m on t.merchant_no = m.merchant_no"+
						" left join agent_info a on m.agent_no = a.agent_no "+
						" left join acq_org o on t.acq_enname=o.ACQ_ENNAME  where 1=1"
				+ sb.toString() + " order by t.id desc";

		String totalPurSql = "select count(t.id) total_pur_count, sum(t.trans_amount) total_amount from trans_info t "+
				" left join pos_merchant m on t.merchant_no = m.merchant_no"+
				" left join agent_info a on m.agent_no = a.agent_no "+
				" left join acq_org o on t.acq_enname=o.ACQ_ENNAME  where 1=1"
				+ sb.toString() + " order by t.id desc";
		//
		// String totalPrvSql =
		// "select count(*) total_prv_count, sum(t.trans_amount) total_amount from trans_info t,pos_merchant m, agent_info a,acq_org o  where "
		// +
		// "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and  t.acq_enname=o.ACQ_ENNAME "
		// + sb.toString() + " order by t.id desc";
		//
		// String totalRefSql =
		// "select count(*) total_ref_count, sum(t.trans_amount) total_amount from trans_info t,pos_merchant m, agent_info a,acq_org o  where "
		// +
		// "t.merchant_no = m.merchant_no and m.agent_no = a.agent_no  and  t.acq_enname=o.ACQ_ENNAME "
		// + sb.toString() + " order by t.id desc";

		String totalPurAmountSql = "select sum(t.trans_amount) total_pur_amount , sum(t.merchant_fee) total_mer_fee from trans_info t "+
				" left join pos_merchant m on t.merchant_no = m.merchant_no"+
				" left join agent_info a on m.agent_no = a.agent_no "+
				" left join acq_org o on t.acq_enname=o.ACQ_ENNAME  where 1=1 "
				+ sb.toString() + " order by t.id desc";

		Map<String, Object> totalCount = dao
				.findFirst(totalSql, list.toArray());
		Map<String, Object> totalPurCount = dao.findFirst(totalPurSql,
				list.toArray());
		// Map<String, Object> totalPrvCount = dao.findFirst(totalPrvSql,
		// list.toArray());
		// Map<String, Object> totalRefCount = dao.findFirst(totalRefSql,
		// list.toArray());
		Map<String, Object> totalPurAmount = dao.findFirst(totalPurAmountSql,
				list.toArray());

		if (totalCount != null) {
			if (totalCount.get("total_count") != null) {
				totalMsg.put("total_count", totalCount.get("total_count"));
			} else {
				totalMsg.put("total_count", 0);
			}
		}
		if (totalPurCount != null) {
			if (totalPurCount.get("total_pur_count") != null) {
				totalMsg.put("total_pur_count",
						totalPurCount.get("total_pur_count"));
			} else {
				totalMsg.put("total_pur_count", 0);
			}
		}
		// if (totalPrvCount != null) {
		// if (totalPrvCount.get("total_prv_count") != null) {
		// totalMsg.put("total_prv_count",
		// totalPrvCount.get("total_prv_count"));
		// } else {
		// totalMsg.put("total_prv_count", 0);
		// }
		// }
		// if (totalRefCount != null) {
		// if (totalRefCount.get("total_ref_count") != null) {
		// totalMsg.put("total_ref_count",
		// totalRefCount.get("total_ref_count"));
		// } else {
		// totalMsg.put("total_ref_count", 0);
		// }
		// }
		if (totalPurAmount != null) {
			if (totalPurAmount.get("total_pur_amount") != null) {
				totalMsg.put("total_pur_amount",
						totalPurAmount.get("total_pur_amount"));
			} else {
				totalMsg.put("total_pur_amount", "0.00");
			}

			if (totalPurAmount.get("total_mer_fee") != null) {
				totalMsg.put("total_mer_fee",
						totalPurAmount.get("total_mer_fee"));
			} else {
				totalMsg.put("total_mer_fee", "0.00");
			}
		}

		return totalMsg;
	}

	// 统计快捷交易信息
	public Map<String, Object> countTransFast(Map<String, String> params) {

		Map<String, Object> totalMsg = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {

			// 交易卡号
			if (StringUtils.isNotEmpty(params.get("cardNo"))) {

				String cardNo = params.get("cardNo");
				sb.append(" and f.card_no = ? ");
				list.add(cardNo);
			}
			// 订单编号
			if (StringUtils.isNotEmpty(params.get("orderNo"))) {
				String orderNo = params.get("orderNo");
				sb.append(" and f.order_no = ? ");
				list.add(orderNo);
			}

			// 收单机构
			if (StringUtils.isNotEmpty(params.get("acqEnname"))
					&& !"-1".equals(params.get("acqEnname"))) {
				sb.append(" and f.acq_enname=? ");
				list.add(params.get("acqEnname"));
			}

			// 交易时间
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and f.create_time >=? ");
				list.add(createTimeBegin);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and f.create_time <=? ");
				list.add(createTimeEnd);
			}
			// 商户
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				/* String merchant_name = "%" + params.get("merchant") + "%"; */
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			// 状态
			if (StringUtils.isNotEmpty(params.get("status"))) {
				
					sb.append(" and f.status=? ");
					list.add(params.get("status"));
				
			}
			//冻结 状态
			if (StringUtils.isNotEmpty(params.get("freezeStatus"))) {
				String freezeStatus = params.get("freezeStatus");
				if(!freezeStatus.equals("-1")){
					sb.append(" and f.freeze_status=? ");
					list.add(freezeStatus);
				}
				
			}
		}

		String totalSql = "select count(f.id) total_count from fastpay_trans_info f,pos_merchant m where "
				+ "f.merchant_no = m.merchant_no "
				+ sb.toString() + " order by f.id desc";

		String totalPurSql = "select count(f.id) total_pur_count, sum(f.amount) total_amount from fastpay_trans_info f,pos_merchant m where "
				+ "f.merchant_no = m.merchant_no "
				+ sb.toString() + " order by f.id desc";

		String totalPurAmountSql = "select sum(f.amount) total_pur_amount , sum(f.merchant_fee) total_mer_fee from fastpay_trans_info f,pos_merchant m, agent_info a where"
				+ " f.merchant_no = m.merchant_no and m.agent_no = a.agent_no    "
				+ sb.toString() + " order by f.id desc";

		Map<String, Object> totalCount = dao
				.findFirst(totalSql, list.toArray());
		Map<String, Object> totalPurCount = dao.findFirst(totalPurSql,
				list.toArray());
		Map<String, Object> totalPurAmount = dao.findFirst(totalPurAmountSql,
				list.toArray());

		if (totalCount != null) {
			if (totalCount.get("total_count") != null) {
				totalMsg.put("total_count", totalCount.get("total_count"));
			} else {
				totalMsg.put("total_count", 0);
			}
		}
		if (totalPurCount != null) {
			if (totalPurCount.get("total_pur_count") != null) {
				totalMsg.put("total_pur_count",
						totalPurCount.get("total_pur_count"));
			} else {
				totalMsg.put("total_pur_count", 0);
			}
		}

		if (totalPurAmount != null) {
			if (totalPurAmount.get("total_pur_amount") != null) {
				totalMsg.put("total_pur_amount",
						totalPurAmount.get("total_pur_amount"));
			} else {
				totalMsg.put("total_pur_amount", "0.00");
			}

			if (totalPurAmount.get("total_mer_fee") != null) {
				totalMsg.put("total_mer_fee",
						totalPurAmount.get("total_mer_fee"));
			} else {
				totalMsg.put("total_mer_fee", "0.00");
			}
		}

		return totalMsg;
	}

	// 交易详情查询
	public Map<String, Object> queryTransInfoById(Long id) {
		String sql = "select merchant_no,acq_reference_no, terminal_no, batch_no, serial_no, acq_merchant_no, acq_terminal_no, sign_check_person,sign_check_time,device_sn,sign_img,"
				+ "acq_batch_no, acq_serial_no, account_no, ti.trans_type, trans_status,acq_auth_no, acq_response_code, currency_type, "
				+ "trans_amount, merchant_rate, acq_merchant_rate, merchant_fee, acq_merchant_fee, trans_source, merchant_settle_date, "
				+ "acq_settle_date, ti.create_time ,trans_time,review_status,ptil.address,CONCAT(mrd.province,mrd.city,mrd.stree)  mobile_address,is_iccard  from trans_info ti left join pos_trans_info_location ptil on ti.id"
				+ "=ptil.trans_info_id left join mobile_rquest_dev mrd on ti.id=trim(both '0' from SUBSTRING(mrd.order_id from 9)) where ti.id =?";
		return dao.findFirst(sql, id);
	}

	// 交易详情查询小票
	public Map<String, Object> queryTransInfoByIdAndmNo(Long id, String tNo) {
		/*
		 * String sql =
		 * "select merchant_no,acq_reference_no, terminal_no, batch_no, serial_no, acq_merchant_no, acq_terminal_no, "
		 * +
		 * "acq_batch_no, acq_serial_no, account_no, trans_type, trans_status, acq_response_code, currency_type, "
		 * +
		 * "trans_amount, merchant_rate, acq_merchant_rate, merchant_fee, acq_merchant_fee, trans_source, merchant_settle_date, "
		 * +
		 * "acq_settle_date, create_time ,trans_time,review_status from trans_info where id = ? and terminal_no =? "
		 * ;
		 */

		String sql = "select t.* , a.acq_merchant_name from trans_info t , acq_merchant a where t.acq_merchant_no = a.acq_merchant_no and t.id = ? ";

		return dao.findFirst(sql, new Object[] { id });
	}

	/**
	 * 统计结算信息
	 * 
	 * */
	public Page<Map<String, Object>> querySettleDetail(
			Map<String, String> params, final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			if (StringUtils.isNotEmpty(params.get("acqMerchantNo"))) {
				sb.append(" and t.acq_merchant_no = ? ");
				list.add(params.get("acqMerchantNo"));
			}
			if (StringUtils.isNotEmpty(params.get("acqTerminalNo"))) {
				sb.append(" and t.acq_terminal_no = ? ");
				list.add(params.get("acqTerminalNo"));
			}
			if (StringUtils.isNotEmpty(params.get("merchantName"))) {
				/*
				 * String merchantName = "%" + params.get("merchantName") + "%";
				 * sb.append(
				 * " and (m.merchant_name like ? or m.merchant_short_name like ?) "
				 * );
				 */
				String merchantName = params.get("merchantName");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?) ");
				list.add(merchantName);
				list.add(merchantName);
			}
			if (StringUtils.isNotEmpty(params.get("transTimeBegin"))) {
				sb.append(" and t.trans_time >= ? ");
				list.add(params.get("transTimeBegin"));
			}
			if (StringUtils.isNotEmpty(params.get("transTimeEnd"))) {
				sb.append(" and t.trans_time <= ? ");
				list.add(params.get("transTimeEnd"));
			}
		}

		String sql = "select am.acq_merchant_no, am.acq_merchant_name, t.acq_terminal_no, t.merchant_no, m.merchant_name, count(*) total_count, sum(t.trans_amount) total_amount, sum(t.merchant_fee) merchant_fee,"
				+ "(sum(t.trans_amount)-sum(t.merchant_fee)) merchant_settle_amount, sum(t.acq_merchant_fee) acq_merchant_fee,(sum(t.trans_amount)-sum(t.acq_merchant_fee)) acq_merchant_settle_amount, "
				+ "m.bank_name,m.cnaps_no,m.account_no,m.account_name,m.account_type "
				+ "from trans_info t left join acq_terminal at on t.acq_merchant_no = at.acq_merchant_no and t.acq_terminal_no = at.acq_terminal_no "
				+ "left join acq_merchant am on t.acq_merchant_no = am.acq_merchant_no "
				+ "left join pos_merchant m on t.merchant_no = m.merchant_no "
				+ "where t.trans_type = 'PURCHASE' and t.trans_status = 'SUCCESS' "
				+ sb.toString()
				+ " group by am.acq_merchant_no,am.acq_merchant_name, t.acq_terminal_no, t.merchant_no,m.merchant_name,m.bank_name,m.cnaps_no,m.account_no,m.account_name,m.account_type";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	// 超额交易数据
	public Page<Map<String, Object>> getExcessTrans(Map<String, String> params,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
				/*
				 * String merchant_name = "%" + params.get("merchant") + "%";
				 * sb.append(
				 * " and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) "
				 * );
				 */
				String merchant_name = params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin") + ":00";
				sb.append(" and t.create_time >=? ");
				list.add(createTimeBegin);
			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd") + ":59";
				sb.append(" and t.create_time <=? ");
				list.add(createTimeEnd);
			}

			if (StringUtils.isNotEmpty(params.get("s_trans_amount"))) {
				String s_trans_amount = params.get("s_trans_amount");
				sb.append(" and t.trans_amount >=? ");
				list.add(s_trans_amount);
			}

			if (StringUtils.isNotEmpty(params.get("m_trans_amount"))) {
				String m_trans_amount = params.get("m_trans_amount");
				sb.append(" and t.trans_amount <=? ");
				list.add(m_trans_amount);
			}

		}
		String sql = "select m.merchant_short_name,m.mcc mermcc , a.agent_name, "
				+ " t.*"
				+ " from trans_info t,pos_merchant m,agent_info a "
				+ " where t.merchant_no = m.merchant_no and m.agent_no = a.agent_no and t.trans_type='PURCHASE' and t.trans_amount >= 20000.00 "
				+ sb.toString() + " order by t.id desc";

		return dao.find(sql, list.toArray(), pageRequest);
	}

	/**
	 * @param parseLong
	 * @throws SQLException
	 */
	public void updateStatus(long id) throws SQLException {
		String sql = "update trans_info set trans_status=? where id=?";
		dao.update(sql, new Object[] { TransStatus.REFUND.toString(), id });
	}

	/**
	 * 修改移小宝签名审核的状态
	 * 
	 * @param id
	 *            trans_info的id
	 * @param status
	 *            需要更改的状态
	 * @throws SQLException
	 */
	public int updateReviewStatus(long id, String status, String realName) throws SQLException {
		List<Object> objects = new ArrayList<Object>();
		objects.add(status);
		String sql = "update trans_info set review_status=?,sign_check_person=?,sign_check_time=? where id=?";
		Date date = new Date();
		objects.add(realName);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objects.add(sdf2.format(date));
		objects.add(id);
		return dao.update(sql, objects.toArray());
	}

	// 交易详情查询
	public Map<String, Object> queryFastTransInfoById(Long id) {
		String sql = "select f.*,m.merchant_name,m.merchant_no from fastpay_trans_info f,pos_merchant m "
				+ " where m.merchant_no = f.merchant_no and  f.id=?";
		return dao.findFirst(sql, id);
	}

	
	// 订单交易详情查询
//	public Map<String, Object> queryFastAirOrderInfoById(Long id) {
//		String sql = "select f.*,m.merchant_name ,ao.airport,ao.amount_end,ao.amount_str," +
//				" ao.city,ao.flytime,ao.plane,ao.seat from fastpay_trans_info f " +
//				"LEFT JOIN pos_merchant m ON f.merchant_no=m.merchant_no LEFT JOIN  " +
//				"fastpay_air_order  ao ON  f.air_id=ao.id  where m.merchant_no = f.merchant_no and f.id=?";
//		return dao.findFirst(sql, id);
//	}
	
	//查询机票订单
	public Map<String, Object> findFastAirOrderByAmount(BigDecimal amount){
		String sql = "SELECT * from fastpay_air_order where amount_end>=? and amount_str<? ";
		return dao.findFirst(sql, new Object[] { amount ,amount});
	}
	
	//查询机票订单BYID
	public Map<String, Object> findFastAirOrderById(String id){
		String sql = "SELECT * from fastpay_air_order where id=? ";
		return dao.findFirst(sql, new Object[] { id});
	}
	
	/**
	 * 冻结
	 * 
	 * @param params
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void updateTransInfoFreezed(Map<String, String> params) {
		System.out.println("-----冻结交易---service开始--");
		Connection conn = dao.getConnection();
		try {
			conn.setAutoCommit(false);
			// 冻结的sql
			StringBuffer freezeSql = new StringBuffer();
			// 参数集合
			List<Object> paras = new ArrayList<Object>();

			Date freezeTime = null;
			if (!StringUtil.isEmpty(params.get("freezeDay"))) {
				Date d = DateUtils.parseDateTime(params.get("settleTime").length() >10?params.get("settleTime"):params.get("settleTime")+" 00:00:00" );
				freezeTime = DateUtils.addDate(d,
						Integer.parseInt(params.get("freezeDay")));
			} else {
				freezeTime = DateUtils.parseDateTime(("2037-12-31 23:59:59"));
			}
			// 冻结（交易查询（pos||移小宝））
			if (!StringUtil.isEmpty(params.get("id"))) {
				freezeSql
						.append(" update trans_info set freeze_status = '1' where id = ?");
				paras.add(params.get("id"));
			}
			// 冻结（交易查询（快捷））
			if (!StringUtil.isEmpty(params.get("fastId"))) {
				freezeSql
						.append(" update fastpay_trans_info set freeze_status ='1'  where id = ?");
				paras.add(params.get("fastId"));
			}
			System.out
					.println("-----冻结交易---更新交易状态为FREEZED和更新清算时间时间操作--dao.updateByTranscation开始--");
			int res = dao.updateByTranscation(freezeSql.toString(),
					paras.toArray(), conn);
			System.out
					.println("====冻结交易==更新交易状态为FREEZED和更新清算时间时间操作-====-dao.updateByTranscation结束--");
			/*
			 * long id = new Long(params.get("id")); int res = 0; String
			 * settleTime = params.get("settleTime"); if(freezeDay == 999999){
			 * freezeSql =
			 * "update trans_info set merchant_settle_date = '2999-12-29',trans_status = ? where id = ?"
			 * ; res = dao.update(freezeSql, new Object[]
			 * {TransStatus.FREEZED.toString(), id }); }else{
			 * 
			 * freezeSql =
			 * "update trans_info set merchant_settle_date = ?,trans_status = ? where id = ?"
			 * ; res = dao.update(freezeSql, new Object[]
			 * {DateUtils.addDate(DateUtils.parseDate(settleTime),
			 * freezeDay),TransStatus.FREEZED.toString(), id }); }
			 */

			if (res > 0) {
				System.out.println("=========冻结交易====插入冻结记录开始======");
				paras.clear();
				String user = params.get("user");
				String fastId = params.get("fastId");
				String id = params.get("id");
				String marchartNo = params.get("merNo");
				String createTime = params.get("createTime");

				String freezeReson = params.get("freezeReson");
				String insertSql = "";

				paras.add(marchartNo);

				if (fastId == null || fastId.length() == 0) {
					insertSql = "insert into trans_info_freeze_log (merchant_no,trans_id,create_time,trans_create_time,freeze_settle_time,freeze_people,free_reason) values (?,?,?,?,?,?,?)";
					paras.add(id);
				} else {
					insertSql = "insert into trans_info_freeze_log (merchant_no,fast_id,create_time,trans_create_time,freeze_settle_time,freeze_people,free_reason) values (?,?,?,?,?,?,?)";
					paras.add(fastId);
				}
				paras.add(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				paras.add(createTime);
				paras.add(freezeTime);
				paras.add(user);
				paras.add(freezeReson);

				int inserCount = 0;
				System.out
						.println("=======冻结交易--执行插入冻结记录操作-====-dao.updateByTranscation开始--");
				inserCount = dao.updateByTranscation(insertSql,
						paras.toArray(), conn);
				System.out
						.println("=======冻结交易--执行插入冻结记录操作-====-dao.updateByTranscation结束--");
				System.out.println(inserCount);
			}
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
				;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	
	/**
	 * 冻结
	 * 
	 * @param params
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void updateTransInfoFreezedNew(Map<String, String> params) {
		System.out.println("-----冻结交易---service开始--");
		Connection conn = dao.getConnection();
		try {
			conn.setAutoCommit(false);
			// 冻结的sql
			StringBuffer freezeSql = new StringBuffer();
			// 参数集合
			List<Object> paras = new ArrayList<Object>();

			Date freezeTime = null;
			int freezeWay = 1;
			int freezeDay = 0;
			if (!StringUtil.isEmpty(params.get("freezeDay"))) {
				Date d = DateUtils.parseDateTime(params.get("settleTime").length() >10?params.get("settleTime"):params.get("settleTime")+" 00:00:00" );
				freezeDay = Integer.parseInt(params.get("freezeDay"));
				freezeTime = DateUtils.addDate(d,freezeDay);
				freezeWay = 1;
			} else {
				freezeTime = DateUtils.parseDateTime(("2037-12-31 23:59:59"));
				freezeWay = 0;
			}
			// 冻结（交易查询（pos||移小宝））
			int transType = 0;
			String transId = "";
			if (!StringUtil.isEmpty(params.get("id"))) {
				transId = params.get("id");
				freezeSql
						.append(" update trans_info set freeze_status = '1' where id = ?");
				paras.add(transId);
				transType = 0;
				
			}
			// 冻结（交易查询（快捷））
			if (!StringUtil.isEmpty(params.get("fastId"))) {
				transId = params.get("fastId");
				freezeSql
						.append(" update fastpay_trans_info set freeze_status ='1'  where id = ?");
				paras.add(transId);
				transType = 1;
			}
			
			System.out
					.println("-----冻结交易---更新交易状态为FREEZED和更新清算时间时间操作--dao.updateByTranscation开始--");
			int res = dao.updateByTranscation(freezeSql.toString(),
					paras.toArray(), conn);
			System.out
					.println("====冻结交易==更新交易状态为FREEZED和更新清算时间时间操作-====-dao.updateByTranscation结束--");
			paras.add(transType);
			if (res > 0) {
				System.out.println("=========冻结交易====插入冻结记录开始======");
				paras.clear();
				String user = params.get("user");
				String userId = params.get("userId");
				String freezeReson = params.get("freezeReson");
				String insertSql = "insert into trans_info_freeze_new_log (trans_id,trans_type,oper_type,oper_reason,freeze_way,freeze_day,oper_time,oper_id,oper_name,settle_time) values (?,?,?,?,?,?,?,?,?,?)";
				paras.add(transId);
				paras.add(transType);
				paras.add(0);
				paras.add(freezeReson);
				paras.add(freezeWay);
				paras.add(freezeDay);
				
				paras.add(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				
				paras.add(userId);
				paras.add(user);
				paras.add(freezeTime);
				

				int inserCount = 0;
				System.out
						.println("=======冻结交易--执行插入冻结记录操作-====-dao.updateByTranscation开始--");
				inserCount = dao.updateByTranscation(insertSql,
						paras.toArray(), conn);
				System.out
						.println("=======冻结交易--执行插入冻结记录操作-====-dao.updateByTranscation结束--");
				System.out.println(inserCount);
			}
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
				;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	
	public Map<String, Object> getPosMerchant(String merchantNo){
		String sql = "select mobile_username,app_no from pos_merchant where merchant_no = ?";
		return dao.findFirst(sql, merchantNo);
	}
	
	
	/**
	 * 解冻
	 * 
	 * @param params
	 * @throws SQLException
	 */
	public void updateTransInfoUnFreezedNew(Map<String, String> params) {
		System.out.println("-----解冻交易开始-----");
		Connection conn = dao.getConnection();
		try {
			conn.setAutoCommit(false);
			String id = params.get("id");
			Map<String, Object> map = null;
			String transId = "";
			String transType ="";
			List<Object> paras = new ArrayList<Object>();
			int res = 0;
			if (!StringUtil.isEmpty(id)) {
				
				transId = id;
				transType="0";
				String selectSql = "select merchant_no,create_time from trans_info where id=?";
				System.out.println("----交易解冻--查询当前id的trans_info记录----开始---");
				map = dao.findFirst(selectSql, id);
				System.out.println("----交易解冻--查询当前id的trans_info记录----结束---");

				String unFreezeSql = "update trans_info set merchant_settle_date = ? ,freeze_status = '2' where id = ?";
				System.out
						.println("-----交易解冻---trans_info----更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation开始--");
				res = dao.updateByTranscation(unFreezeSql, new Object[] {
						DateUtils.addDate(new Date(), 1), id }, conn);
				System.out
						.println("-----交易解冻--更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation开始--");
			} else {
				String fastId = params.get("fastId");
				paras.add(fastId);
				transId = fastId;
				transType="1";
				String selectSql = "select merchant_no,create_time from fastpay_trans_info where id=?";
				System.out
						.println("----交易解冻--查询当前id的fastpay_trans_info记录----开始---");
				map = dao.findFirst(selectSql, fastId);
				System.out
						.println("----交易解冻--查询当前id的fastpay_trans_info记录----结束---");
				String unFreezeSql = "update fastpay_trans_info set settle_date = ? ,freeze_status = '2' where id = ?";
				System.out
						.println("-----交易解冻---fastpay_trans_info---更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation开始--");
				res = dao.updateByTranscation(
						unFreezeSql,
						new Object[] { DateUtils.addDate(new Date(), 1),
								fastId }, conn);
				System.out
						.println("-----交易解冻---fastpay_trans_info---更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation结束--");
			}

			if (res > 0) {
				System.out.println("=========交易解冻====插入解冻记录开始======");
				paras.clear();
				String user = params.get("user");
				String userId = params.get("userId");
				

				String insertSql = "insert into trans_info_freeze_new_log (trans_id,trans_type,oper_type,oper_reason,freeze_way,freeze_day,oper_time,oper_id,oper_name,settle_time) values (?,?,?,?,?,?,?,?,?,?)";
				paras.add(transId);
				paras.add(transType);
				paras.add(1);
				paras.add(null);
				paras.add(null);
				paras.add(0);
				Date d = new Date();
				paras.add(DateUtils.format(d, "yyyy-MM-dd HH:mm:ss"));
				paras.add(userId);
				paras.add(user);
				paras.add(DateUtils.addDate(d, 1));
				
				System.out
						.println("=========交易解冻====插入解冻记录===dao.updateByTranscation开始======");
				int inserCount = dao.updateByTranscation(insertSql,
						paras.toArray(), conn);
				System.out
						.println("=========交易解冻====插入解冻记录===dao.updateByTranscation结束======");
				System.out.println(inserCount);
			}
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 解冻
	 * 
	 * @param params
	 * @throws SQLException
	 */
	public void updateTransInfoUnFreezed(Map<String, String> params) {
		System.out.println("-----解冻交易开始-----");
		Connection conn = dao.getConnection();
		try {
			conn.setAutoCommit(false);
			String id = params.get("id");
			Map<String, Object> map = null;
			List<Object> paras = new ArrayList<Object>();
			int res = 0;
			if (!StringUtil.isEmpty(id)) {
				String selectSql = "select merchant_no,create_time from trans_info where id=?";
				System.out.println("----交易解冻--查询当前id的trans_info记录----开始---");
				map = dao.findFirst(selectSql, params.get("id"));
				System.out.println("----交易解冻--查询当前id的trans_info记录----结束---");

				String unFreezeSql = "update trans_info set merchant_settle_date = ? ,freeze_status = '2' where id = ?";
				System.out
						.println("-----交易解冻---trans_info----更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation开始--");
				res = dao.updateByTranscation(unFreezeSql, new Object[] {
						DateUtils.addDate(new Date(), 1), id }, conn);
				System.out
						.println("-----交易解冻--更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation开始--");
			} else {
				String selectSql = "select merchant_no,create_time from fastpay_trans_info where id=?";
				System.out
						.println("----交易解冻--查询当前id的fastpay_trans_info记录----开始---");
				map = dao.findFirst(selectSql, params.get("fastId"));
				System.out
						.println("----交易解冻--查询当前id的fastpay_trans_info记录----结束---");
				String unFreezeSql = "update fastpay_trans_info set settle_date = ? ,freeze_status = '2' where id = ?";
				System.out
						.println("-----交易解冻---fastpay_trans_info---更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation开始--");
				res = dao.updateByTranscation(
						unFreezeSql,
						new Object[] { DateUtils.addDate(new Date(), 1),
								params.get("fastId") }, conn);
				System.out
						.println("-----交易解冻---fastpay_trans_info---更新交易状态为SUCCESS和更新清算时间时间操作--dao.updateByTranscation结束--");
			}

			if (res > 0) {
				System.out.println("=========交易解冻====插入解冻记录开始======");
				paras.clear();
				String user = params.get("user");
				String fastId = params.get("fastId");

				String insertSql = "";

				paras.add(map.get("merchant_no"));

				if (fastId == null || fastId.length() == 0) {
					insertSql = "insert into trans_info_freeze_log (merchant_no,trans_id,free_time,trans_create_time,free_settle_time,free_people) values (?,?,?,?,?,?)";
					paras.add(id);
				} else {
					insertSql = "insert into trans_info_freeze_log (merchant_no,fast_id,free_time,trans_create_time,free_settle_time,free_people) values (?,?,?,?,?,?)";
					paras.add(fastId);
				}
				Date d = new Date();
				paras.add(DateUtils.format(d, "yyyy-MM-dd HH:mm:ss"));
				paras.add(map.get("create_time"));
				paras.add(DateUtils.addDate(d, 1));
				paras.add(user);
				System.out
						.println("=========交易解冻====插入解冻记录===dao.updateByTranscation开始======");
				int inserCount = dao.updateByTranscation(insertSql,
						paras.toArray(), conn);
				System.out
						.println("=========交易解冻====插入解冻记录===dao.updateByTranscation结束======");
				System.out.println(inserCount);
			}
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * 冻结交易 查询冻结操作人和最后一次操作时间
	 * @param params
	 * @return
	 */
	public Map<String, Object> freezedInfo(Map<String, String> params) {
		String sql = "";
		List<Object> paras = new ArrayList<Object>();
		System.out.println("--------拼接查询sql 开始 -------");
		if (!StringUtil.isEmpty(params.get("trans_id"))) {
			sql = "select merchant_no ,freeze_people,Max(create_time) create_time from trans_info_freeze_log  where trans_id = ? and freeze_people is not null group by merchant_no,freeze_people,trans_id order by create_time desc";
			paras.add(params.get("trans_id"));
		} else if (!StringUtil.isEmpty(params.get("fast_id"))) {
			sql = "select merchant_no,freeze_people,Max(create_time) create_time from trans_info_freeze_log  where fast_id = ? and freeze_people is not null group by merchant_no,freeze_people,fast_id order by create_time desc";
			paras.add(params.get("fast_id"));
		}
		System.out.println("--------拼接查询sql 结束-------");
		return dao.findFirst(sql, paras.toArray());
	}

	/**
	 * 冻结交易 查询冻结操作人和最后一次操作时间
	 * @param params
	 * @return
	 */
	public Map<String, Object> freezedInfoNew(Map<String, String> params) {
		String sql = "";
		List<Object> paras = new ArrayList<Object>();
		System.out.println("--------拼接查询sql 开始 -------");
		if (!StringUtil.isEmpty(params.get("trans_id"))) {
			sql = "select oper_name,oper_time from trans_info_freeze_new_log where trans_id = ? and oper_type = '0' and trans_type='0' order by oper_time desc limit 1 ";
			paras.add(params.get("trans_id"));
		} else if (!StringUtil.isEmpty(params.get("fast_id"))) {
			sql = "select oper_name,oper_time from trans_info_freeze_new_log where trans_id = ? and oper_type = '0' and trans_type='1' order by oper_time desc limit 1 ";
			paras.add(params.get("fast_id"));
		}
		System.out.println("--------拼接查询sql 结束-------");
		return dao.findFirst(sql, paras.toArray());
	}
	
	/**
	 * 定时任务，获取前一天的未审核小票的交易
	 * @param createTimeBegin
	 * @param createTimeEnd
	 * @return
	 * @throws SQLException 
	 */
	public int updateUnreviewedTransToReviewed(String createTimeBegin, String createTimeEnd, String checkTime, String checkPerson) throws SQLException {
		String sql = "update trans_info set review_status = '1',sign_check_time=?,sign_check_person=? where trans_time > ? and trans_time < ? and review_status=2 and trans_type <> 'BALANCE_QUERY' ";
		return dao.update(sql, new Object[]{checkTime, checkPerson, createTimeBegin, createTimeEnd});
	}

	public List<Map<String, Object>> getTransFreezeLogs(Long id, String oper_type) {
		String sql = "select oper_type,oper_reason,freeze_way,freeze_day,oper_time,oper_name from trans_info_freeze_new_log where trans_id = ? and trans_type = ? order by oper_time desc limit 0, 3 ";
		return dao.find(sql, new Object[]{id, oper_type});
	}

	public List<Map<String, Object>> getAcqMerchantTransCount(String acqOrg, String createTimeBegin, String createTimeEnd) {

		List<String> list = new ArrayList<String>();
		list.add(createTimeBegin);
		list.add(createTimeEnd);

		StringBuffer sb = new StringBuffer();

		if(StringUtils.isNotBlank(acqOrg)){
			String[] acqOrgs = acqOrg.split(",");
			sb.append("HAVING t.acq_enname in ( ");

			for(String acq : acqOrgs){
				sb.append(" ?,");
				list.add(acq);
			}

			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(" ) ");
		}

		String sql = "SELECT t.acq_enname, t.acq_merchant_no as merchant_no, p.acq_merchant_name as merchant_name,\n" +
				"SUM(CASE WHEN (t.trans_status = 'SUCCESS') THEN t.trans_amount ELSE 0 end) AS 'success' , \n" +
				"SUM(CASE WHEN (t.trans_status = 'FAILED') THEN t.trans_amount ELSE 0 end) AS 'failed' , \n" +
				"SUM(CASE WHEN (t.trans_status = 'INIT') THEN t.trans_amount ELSE 0 end) AS 'init' , \n" +
				"SUM(CASE WHEN (t.trans_status = 'REVERSED') THEN t.trans_amount ELSE 0 end) AS 'reversed', \n" +
				"SUM(CASE WHEN (t.trans_status = 'SUCCESS') THEN t.merchant_fee ELSE 0 end) AS 'fee' \n" +
				"FROM trans_info t inner join acq_merchant p on t.acq_merchant_no = p.acq_merchant_no \n" +
				"where t.trans_type <> 'BALANCE_QUERY' and t.create_time >= ? and t.create_time <= ? \n" +
				"GROUP BY t.acq_enname,t.acq_merchant_no \n" + sb.toString();

		return dao.find(sql, list.toArray());
	}
}
