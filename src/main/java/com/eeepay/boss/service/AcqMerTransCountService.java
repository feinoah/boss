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
 * 收单商户交易统计
 * @author LJ
 *
 */
@Service
public class AcqMerTransCountService {

	@Resource
	private Dao dao;
	

	// 统计交易信息
	public Page<Map<String, Object>> transCountQuery(Map<String, String> params,final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		
		if (null != params) {
			// 收单机构商户名称查询
			if (StringUtils.isNotEmpty(params.get("acqMerchant"))) {
			/*	String acqMerchant = "%" + params.get("acqMerchant") + "%";
				sb.append(" and (r.acq_merchant_name like ?  or r.acq_merchant_no like ?) ");*/
				String acqMerchant = params.get("acqMerchant");
				sb.append(" and (r.acq_merchant_name = ?  or r.acq_merchant_no = ?) ");
				list.add(acqMerchant);
				list.add(acqMerchant);
			}
			
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
			
			// 交易来源
			if (StringUtils.isNotEmpty(params.get("transSource"))) {
				String transSource = params.get("transSource");
				
					String[] trans = transSource.split(",");
					if(trans.length == 1){
						for(String tran : trans){
						sb.append(" and t.trans_source =? ");
						list.add(tran);
						}
					}else if(trans.length > 1){
						sb.append(" and t.trans_source in( ");
						StringBuffer buffer  = new StringBuffer();
						for(String tran : trans){
							buffer.append("?").append(",");
							list.add(tran);
						}
						
						String tansStr = "";
						if(buffer.toString().endsWith(",")){
							tansStr = buffer.toString().substring(0, buffer.toString().length() -1);
						}
						sb.append(tansStr).append(")");
					}
			}
		}
		
		String timeBegin = params.get("createTimeBegin");
		String timeEnd = params.get("createTimeEnd");
		String timeBegins=StringUtils.isEmpty(timeBegin) ? timeBegin : timeBegin.substring(0, 10); 
		String timeEnds=StringUtils.isEmpty(timeEnd) ? timeEnd : timeEnd.substring(0, 10);
	
		
		/*String sql="select * from (select r.acq_merchant_name,r.acq_merchant_no,count(*) total_pur_count, sum(t.trans_amount) total_pur_amount,concat('"+timeBegins+"','~','"+timeEnds+"') as trans_cycle "
				+ " from acq_merchant r  INNER  JOIN trans_info t  on  r.acq_merchant_no = t.acq_merchant_no where "
				+ " t.trans_type = 'PURCHASE' and t.trans_status = 'SUCCESS'  "
				+ sb.toString()+" GROUP BY r.acq_merchant_no"
				+ "  ) r_temp";*/
		String sql="select * from (select r.acq_merchant_name,a.agent_name,m.sale_name,r.locked,r.acq_merchant_no,count(*) total_pur_count, sum(t.trans_amount) total_pur_amount,concat('"+timeBegins+"','~','"+timeEnds+"') as trans_cycle  "+
                         "from acq_merchant r  "+
                         "INNER JOIN trans_info t  on  r.acq_merchant_no = t.acq_merchant_no  "+ 
                         "LEFT  JOIN agent_info a on  r.agent_no=a.agent_no   "+
                         "LEFT  JOIN pos_merchant m  on  r.merchant_no = m.merchant_no  "+ 
                         "where t.trans_type = 'PURCHASE' and t.trans_status = 'SUCCESS'  "+  
                         sb.toString()+"GROUP BY r.acq_merchant_no  ) r_temp";
		
		
		//+ " and t.create_time >='2014-06-10 22:30:00'  and t.create_time <='2014-06-10 23:59:59'"
		
		System.out.println(sql+"\n"+list);
	
		return dao.find(sql, list.toArray(), pageRequest);
	}
}
