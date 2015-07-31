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
public class RiskRulService {
  @Resource
  private Dao dao;

 
  
	public int subRuleAdd(Map<String, String> params) throws SQLException{
		String no=params.get("no");
    String name=params.get("name");
    String beforeop=params.get("beforeop");
    String op=params.get("op");
    String afterop=params.get("afterop");
    String relation=params.get("relation");
    String rulevalue=params.get("rulevalue");
    String rule_expression=beforeop+";"+op+";"+afterop+";"+relation+";"+rulevalue+";";
 		String sql="insert into risk_rule(rule_no, rule_name, rule_expression, create_time) values(?,?,?,now())";
		int num=dao.update(sql,new Object[]{no,name,rule_expression});
		return num;
	}
	
	 
}
