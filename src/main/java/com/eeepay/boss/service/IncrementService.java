package com.eeepay.boss.service;

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
 * 商户业务操作
 * 
 * @author zhanghw
 */
@Service
public class IncrementService {
	@Resource
	private Dao dao;

	private static final Logger log = LoggerFactory.getLogger(IncrementService.class);

	public Page<Map<String, Object>> getPubPayList(Map<String, String> params, PageRequest page) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (params != null) {

			// 商户名称查询
			if (StringUtils.isNotEmpty(params.get("merchant"))) {
			/*	String merchant_name = "%" + params.get("merchant") + "%";
				sb.append(" and (m.merchant_name like ? or m.merchant_short_name like ?  or m.merchant_no like ?) ");*/
				String merchant_name =params.get("merchant");
				sb.append(" and (m.merchant_name = ? or m.merchant_short_name = ?  or m.merchant_no = ?) ");
				list.add(merchant_name);
				list.add(merchant_name);
				list.add(merchant_name);
			}
			
			if (StringUtils.isNotEmpty(params.get("merchant_no"))) {
				String merchant_no = params.get("merchant_no");
				list.add(merchant_no);
				sb.append(" and merchant_no=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("order_no"))) {
				String order_no = params.get("order_no");
				list.add(order_no);
				sb.append(" and order_no=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("order_type"))) {
				String order_type = params.get("order_type");
				list.add(order_type);
				sb.append(" and order_type=?");
			}
			

			if (StringUtils.isNotEmpty(params.get("status"))) {
				String status = params.get("status");
				list.add(status);
				sb.append(" and status=?");
			}
			
			if (StringUtils.isNotEmpty(params.get("createTimeBegin"))) {
				String createTimeBegin = params.get("createTimeBegin");
				sb.append(" and create_time >=? ");
				list.add(createTimeBegin);

			}

			if (StringUtils.isNotEmpty(params.get("createTimeEnd"))) {
				String createTimeEnd = params.get("createTimeEnd");
				sb.append(" and create_time <=? ");
				list.add(createTimeEnd);
			}
		}
		String sql = "select * from increment_pubpay  where 1=1" + sb.toString() + " order by create_time desc";
		return dao.find(sql, list.toArray(), page);

	}
	
	public Map<String, Object> getPubPayDetail(Long id) {
		String sql = "select * from increment_pubpay where id= ? ";
		return dao.findFirst(sql,new Object[]{id});
	}

		
}
