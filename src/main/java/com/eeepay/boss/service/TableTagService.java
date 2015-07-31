package com.eeepay.boss.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eeepay.boss.domain.AgentInfo;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.utils.SysConfig;

/**
 * 供标签使用，其它地方勿用。
 * 
 * @author zxm
 * 
 */
@Service
public class TableTagService {
  @Resource
  private Dao dao;

 

  // 做列子使用。
//  public Map<String, Object> getAgentFeeAndRuleDetail(String agentNo) {
//    List<Object> list = new ArrayList<Object>();
//    list.add(agentNo);
//    String sql = "select  r.sharing_rule  from agent_share_rule r   where r.agent_no = ? ";
//    Map<String, Object> agentInfo = dao.findFirst(sql, list.toArray());
//    return agentInfo;
//  }

  

  // 做列子使用。
  // public List<Map<String, Object>> agentListSelect() {
  // List<Object> list = new ArrayList<Object>();
  // String sql = "select a.* from agent_info a where a.parent_id is null or
  // a.parent_id='' order by a.id desc ";
  // return dao.find(sql, list.toArray());
  //  }
  
  public   List<Map<String, Object>> getPosType() {
	    String sql = "select  p.pos_type_name,p.pos_type from pos_type p group by p.pos_type";
	    if(dao==null){
	      dao=new Dao();
	    }
	     return   dao.find(sql);
	} 

 
  // 查询代理商
  public   List<Map<String, Object>> getList( String tablename,List<Object> list) {
   
    String sql = "select  * from "+tablename+" where ?= ?";
    if(dao==null){
      dao=new Dao();
    }
     return   dao.find(sql, list.toArray());
  }
  
  public   List<Map<String, Object>> getPosModel() {
	    String sql = "select  p.pos_model,p.pos_model_name from pos_type p where p.pos_type!='00'";
	    if(dao==null){
	      dao=new Dao();
	    }
	     return   dao.find(sql);
	} 
  
  public   List<Map<String, Object>> getPosModel(String type) {
	    String sql = "select  p.pos_model,p.pos_model_name from pos_type p where p.pos_status in(1,?)";
	    if(dao==null){
	      dao=new Dao();
	    }
	     return   dao.find(sql, new Object[]{type});
	} 


}
