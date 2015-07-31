package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

@Service
public class BlackService {
	@Resource
	  private Dao dao;
	
	public Page<Map<String, Object>> getBlackList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			//黑名单类型
			if (StringUtils.isNotEmpty(params.get("black_type"))) {
				if (!params.get("black_type").equals("-1")) {
					String black_type = params.get("black_type");
					sb.append(" and a.black_type = ? ");
					list.add(black_type);
				}
			}
			
			String merchant_no = params.get("merchant_no");
			String id_card_no = params.get("id_card_no");
			String account_no = params.get("account_no");
			boolean merchant_no_empty = StringUtils.isNotEmpty(merchant_no);
			boolean id_card_no_empty = StringUtils.isNotEmpty(id_card_no);
			boolean account_no_empty = StringUtils.isNotEmpty(account_no);
			
			if(merchant_no_empty || id_card_no_empty || account_no_empty){
				sb.append(" and a.black_value in (?, ?, ?) ");
				list.add(merchant_no);
				list.add(id_card_no);
				list.add(account_no);
			}
			
			
			//黑名单状态
			if (StringUtils.isNotEmpty(params.get("status"))) {
				if (!params.get("status").equals("-1")) {
					String status = params.get("status");
					sb.append(" and a.status = ? ");
					list.add(status);
				}
			}
		}
		String sql = "SELECT * from pos_black_list a where 1=1 "
				+ sb.toString() + " order by a.create_time desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	public int blackSave(Map<String, String> params) throws SQLException{
		String blackType=params.get("black_type");
		String black_value=params.get("black_value");
		String cause=params.get("cause");
		String status=params.get("status");
		String sql="insert into pos_black_list(black_type,black_value,cause,status,create_time) values(?,?,?,?,now())";
		int num=dao.update(sql,new Object[]{blackType,black_value,cause,status});
		return num;
	}
	
	public int blackModify(Map<String, String> params) throws SQLException{
		String blackType=params.get("black_type");
		String black_value=params.get("black_value");
		String cause=params.get("cause");
		String status=params.get("status");
		String id=params.get("id");
		String sql="update pos_black_list set black_type=?,black_value=?,cause=?,status=? where id=?";
		int num=dao.update(sql,new Object[]{blackType,black_value,cause,status,Long.parseLong(id)});
		return num;
	}
	
	public Map<String, Object> getBlackById(long id) throws SQLException{
		String sql="select * from pos_black_list where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}
	//删除黑名单
	public int deleteBlack(long id) throws SQLException{
		String sql="delete from pos_black_list where id=?";
		return dao.update(sql, id);
	}
	
	//黑名单过滤查询
	public Page<Map<String, Object>> getBlackFilterList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			//交易类型
			if (StringUtils.isNotEmpty(params.get("transType"))) {
				if (!params.get("transType").equals("-1")) {
					String transType = params.get("transType");
					sb.append(" and a.trans_type = ? ");
					list.add(transType);
				}
			}
		}
		
		String merchant_no = params.get("merchant_no");
		String id_card_no = params.get("id_card_no");
		String account_no = params.get("account_no");
		boolean merchant_no_empty = StringUtils.isNotEmpty(merchant_no);
		boolean id_card_no_empty = StringUtils.isNotEmpty(id_card_no);
		boolean account_no_empty = StringUtils.isNotEmpty(account_no);
		
		if(merchant_no_empty || id_card_no_empty || account_no_empty){
			sb.append(" and a.filter_condition in (?, ?, ?) ");
			list.add(merchant_no);
			list.add(id_card_no);
			list.add(account_no);
		}
		
		String sql = "SELECT * from pos_black_filter a where 1=1 "
				+ sb.toString() + " order by a.create_time desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}
}
