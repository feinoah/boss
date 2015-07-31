package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.rmi.CleanCacheService;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.StringUtil;

@Service
public class TransRouteInfoService
{
	private static Logger log = LoggerFactory.getLogger(TransRouteInfoService.class);
	@Resource
	private Dao dao;
	
//	@Resource
//	private CleanCacheService cleanCacheService;
	
	public Map<String,Object> getTransRouteInfo(Map<String,String> params)
	{
		String merchant_no = params.get("merchant_no");
	    String terminal_no = params.get("terminal_no");
	    
	    String sql ="select * from trans_route_info where 1 = 1 ";
	    
	    List<Object> listTransRouteInfo = new ArrayList<Object>();
	    
	    if(!StringUtil.isEmpty(merchant_no))
	    {
	    	sql += " and merchant_no = ? ";
	    	listTransRouteInfo.add(merchant_no);
	    }
	    
	    if(!StringUtil.isEmpty(terminal_no))
	    {
	    	sql += " and terminal_no = ? ";
	    	listTransRouteInfo.add(terminal_no);
	    }
	    else
	    {
	    	sql += " and terminal_no is null ";
	    }
	    
	    
	    return dao.findFirst(sql, listTransRouteInfo.toArray());
		
	    
	}
	
	public void  saveTransRouteInfo(Map<String,String> params) throws SQLException
	{
//		id
//		merchant_no
//		terminal_no
//		acq_merchant_no
//		acq_terminal_no
//		last_update_time
//		create_time
		String acq_enname = params.get("acq_enname");
		String acq_merchant_no = params.get("acq_merchant_no");
	    String acq_terminal_no = params.get("acq_terminal_no");
	    String merchant_no = params.get("merchant_no");
	    String terminal_no = params.get("terminal_no");
	    int updateCount = 0;
	    List<Object> listTransRouteInfo = new ArrayList<Object>();
	    //执行update操作，如果返回更新数为0，则执行插入操作
	    String updateSql = " update trans_route_info set "
	    	+" acq_enname = ?, "
	    	+" acq_merchant_no = ?, "
	    	+" acq_terminal_no = ?,"
	    	+" last_update_time = now() "
	    	+" where merchant_no = ? ";
	    listTransRouteInfo.add(acq_enname);
	    listTransRouteInfo.add(acq_merchant_no);
	    listTransRouteInfo.add(acq_terminal_no);
	    listTransRouteInfo.add((merchant_no==null || merchant_no.trim().length() == 0 ) ?null : merchant_no.trim() );
	    
	    if(!StringUtil.isEmpty(terminal_no))
	    {
	    	if(!"".equals(terminal_no.trim())){
	    		updateSql += " and terminal_no = ? ";
	    		listTransRouteInfo.add(terminal_no.trim());
	    	}else{
	    		updateSql += " and terminal_no is null ";
	    	}
	    }
	    else
	    {
	    	updateSql += " and terminal_no is null ";
	    }
	    
	    updateCount = dao.update(updateSql,listTransRouteInfo.toArray());
	    
	    if(updateCount == 0)
	    {
	    	//执行插入操作
	    	String insertSql = " insert into trans_route_info "
	    		+" (acq_enname,merchant_no,terminal_no,acq_merchant_no,acq_terminal_no,create_time) "
	    		+" values "
	    		+"(?,?,?,?,?,now())";
	    	listTransRouteInfo.clear();
	    	listTransRouteInfo.add(acq_enname);
	    	listTransRouteInfo.add((merchant_no==null || merchant_no.trim().length() == 0 ) ?null : merchant_no.trim());
	    	listTransRouteInfo.add((terminal_no==null || terminal_no.trim().length() == 0 ) ?null : terminal_no.trim());
	    	listTransRouteInfo.add(acq_merchant_no);
	    	listTransRouteInfo.add(acq_terminal_no);
	    	
	    	 dao.update(insertSql,listTransRouteInfo.toArray());
	    	
	    }
	    
	    
//	    try {
//			cleanCacheService.toCleancache();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
	    
	}
	
	// 组合条件查询数据
	public Page<Map<String, Object>> getTransRouteList(String acqTerminalNo,
			final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		list.add(acqTerminalNo);
		String sql = " select t.* , m.merchant_short_name from trans_route_info t LEFT JOIN pos_merchant m " +
				"ON t.merchant_no=m.merchant_no where acq_terminal_no=? order by t.id desc";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	// 大套小解除
	public int agentInfoDel(Map<String, String> params) {
		
		StringBuffer sb = new StringBuffer();
		String merchant_no = params.get("merchant_no");
		String terminal_no = params.get("terminal_no");
		List<Object> list = new ArrayList<Object>();
		list.add(merchant_no);
		if(StringUtil.isEmpty(terminal_no)){
			sb.append(" and ( terminal_no = '' || terminal_no is null )");
		}else{
			sb.append(" and terminal_no = ?");
			list.add(terminal_no);
		}
		String delete_trans_route_info = "delete from trans_route_info  where merchant_no =? "+sb.toString();
		try {
			int m = dao.update(delete_trans_route_info,list.toArray());
			
//			cleanCacheService.toCleancache();
			
			return m;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
