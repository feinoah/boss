package com.eeepay.boss.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 收单机构列表
 * @author Administrator
 *
 */
@Service
public class AcqOrgService {
	
	 @Resource
	  private Dao dao;
	 
	 private static final Logger log = LoggerFactory.getLogger(AcqOrgService.class);
	 
	 /**
	  * 根据收单机构英文名称得到中文名称
	  * @param enname 英文名称
	  * @return
	  */
	 public Map<String, Object> getAcqNameByEnName(String enname){
		 log.info("AcqOrgService getAcqNameByEnName START");
		 String sql = "select * from acq_org where ACQ_ENNAME=?";
		 log.info("AcqOrgService getAcqNameByEnName END");
		 return dao.findFirst(sql, new Object[]{enname});
	 }
	
	  // 查询收单机构(供标签使用)
	 public Page<Map<String, Object>> getOrgListForTag(String value,
	     final PageRequest pageRequest) throws Exception {
	     List<Object> list = new ArrayList<Object>();
	      String sql = "";	
		  sql = "select  acq_cnname,acq_enname from acq_org a   order by a.id asc"; 
	   return dao.find(sql, list.toArray(), pageRequest);
	 }
}
