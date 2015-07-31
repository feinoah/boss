package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 商户业务操作
 * 
 * @author zhanghw
 */
@Service
public class StatisticsService {
	@Resource
	private Dao dao;

	// 根据Id查询商户信息
	public List<Map<String, Object>> query7DaysCounts() {
		String sql = "SELECT DATE_FORMAT(create_time,'%Y-%m-%d') time,SUM(trans_amount) money,DATE_FORMAT(create_time,'%e') day"
				+ " FROM trans_info WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND create_time <= CURDATE() GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d')";
		return dao.find(sql);
	}

	// 查询代理商日交易数据
	public List<Map<String, Object>> queryAgentDayCounts() {
		// create_time >CURDATE() AND create_time <CURDATE()+1 时间先不放
		String sql = "SELECT t.agent_no,SUM(t.trans_amount) trans_amount,a.agent_name FROM trans_info t,agent_info a "
				+ "WHERE      t.trans_type = 'PURCHASE' and  t.trans_status = 'SUCCESS' AND  t.agent_no=a.agent_no AND t.create_time >CURDATE() AND t.create_time <ADDDATE(now(), interval 1 day)  and t.agent_no IS NOT NULL GROUP BY t.agent_no LIMIT 10";
		return dao.find(sql);
	}

	// 根据最近三条成功的交易
	public List<Map<String, Object>> queryOrderForThree() {
		String sql = "SELECT m.merchant_short_name,a.agent_name, t.id,t.acq_response_code,t.trans_type,t.card_type,t.account_no,t.trans_amount,t.create_time,t.trans_status FROM trans_info t,pos_merchant m,agent_info a WHERE t.merchant_no = m.merchant_no AND "
				+ "t.trans_status ='SUCCESS' AND t.trans_type= 'PURCHASE' AND m.agent_no = a.agent_no ORDER BY t.id DESC LIMIT 3";
		return dao.find(sql);
	}

	// 根据当前日期查询最新14天日交易
	public List<Map<String, Object>> queryCountForDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 今天日期
		String end = sdf.format(new Date());
		// 14天前
		Calendar theCa = Calendar.getInstance();
		theCa.setTime(new Date());
		theCa.add(theCa.DATE, -14);
		Date date = theCa.getTime();
		String start = sdf.format(date);
		String sql = "SELECT DATE_FORMAT( create_time, '%Y-%m-%d' ) d , SUM(trans_amount) a,count(*) c FROM trans_info where   trans_type = 'PURCHASE' and  trans_status = 'SUCCESS'  and create_time BETWEEN '"+start+" 00:00:00' and '"
				+ end
				+ " 23:59:59' GROUP BY DATE_FORMAT( create_time, '%Y-%m-%d' ) ";
		return dao.find(sql);
	}

	// 活跃商户区
	public List<Map<String, Object>> queryActiveMer() {
		String sql = "SELECT p.city,SUM(t.trans_amount) trans_amount FROM pos_merchant p,trans_info t WHERE p.merchant_no=t.merchant_no GROUP BY  p.city  LIMIT 20";
		return dao.find(sql);
	}
	
	//查询总消费金额
	public Map<String, Object> queryTotalPurchase() {
		String sql = "SELECT COALESCE((SUM(t.trans_amount)),0)  total_amount FROM trans_info t  "
				+ "WHERE  t.trans_type = 'PURCHASE' and  t.trans_status = 'SUCCESS' " +
				"AND t.create_time >CURDATE() AND t.create_time <ADDDATE(now(), interval 1 day) ";
		return dao.findFirst(sql);
	}

}
