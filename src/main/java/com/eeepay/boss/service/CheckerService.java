package com.eeepay.boss.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 查询审核人
 * @author Administrator
 *
 */
@Service
public class CheckerService {

	
	 @Resource
	  private Dao dao;
	
	  // 查询审核人(供标签使用)
	 public Page<Map<String, Object>> getCheckerListForTag(String value,
	     final PageRequest pageRequest) throws Exception {
	     List<Object> list = new ArrayList<Object>();
	     StringBuffer sql_sb = new StringBuffer(""); 
	     sql_sb.append(" select group_concat(DISTINCT m.checker) as checker from pos_merchant m");
	     sql_sb.append(" where m.checker !='' GROUP BY  m.checker");	     
	   return dao.find(sql_sb.toString(), list.toArray(), pageRequest);
	 }
}
