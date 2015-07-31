package com.eeepay.boss.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 龙宝查询
 * @author LJ
 *
 */
@Service
public class LongBaoService {
   
	@Resource
	private Dao dao;
	
	/**
	 * 龙宝查询
	 * @param params
	 * @param pageRequest
	 * @return
	 */
	public Page<Map<String, Object>> getLongBaoList(Map<String, String> params,
				final PageRequest pageRequest) {
			List<Object> list = new ArrayList<Object>();
			StringBuffer sb = new StringBuffer();
			
			if (null != params) {
				//order_no
				if (StringUtils.isNotEmpty(params.get("order_no"))) {
					String order_no = params.get("order_no");
					sb.append(" and order_no= ? ");
					list.add(order_no);
				}
				
				//card_no
				if (StringUtils.isNotEmpty(params.get("card_no"))) {
					String card_no = params.get("card_no");
					sb.append(" and card_no = ? ");
					list.add(card_no);
				}
				
				//创建时间
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

			String sql = "select * from longbao_notify where 1=1" + sb.toString()+" order by id desc";
			return dao.find(sql, list.toArray(), pageRequest);
		}
}
