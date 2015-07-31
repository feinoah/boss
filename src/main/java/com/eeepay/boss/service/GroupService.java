package com.eeepay.boss.service;

import java.sql.Connection;
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

/**
 * 商户分组管理
 * 
 * @author zxm
 * 
 */
@Service
public class GroupService {
	@Resource
	private Dao dao;
	
	/**
	 * 获取状态为正常的集群信息
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getGroupInfo(){
		String sql = "select g.* from trans_route_group g where g.`status`=0";
		return  dao.find(sql);
	}
	
	/**
	 * 根据集群ID以及集群编号删除集群信息
	 * @param id 集群ID
	 * @param groupCode 集群编号
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public int removeGroupByIdAndCode(int id, int groupCode)  throws SQLException{
		int count = 0;
		if(id > 0 && groupCode >0){
			List<Object> list = new ArrayList<Object>();
			String sql = "delete from trans_route_group where id=?  and group_code=?";
			list.add(id);
			list.add(groupCode);
			count = dao.update(sql, list.toArray());
		}
		return count;
	}
	
	/**
	 * 数据库中根据id查询
	 * 
	 * @param id
	 * @author swang
	 * @return
	 */
	public Map<String, Object> getGroupsById(String id) {
		List<Object> list = new ArrayList<Object>();
		String sql = "SELECT * from trans_route_group a where 1=1  and  id=? ";
		list.add(id);
		return dao. findFirst(sql,list.toArray());
	}
	
	/**
	 * 改版后修改集群信息
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int modifyGroupInfo(Map<String, String> params) throws SQLException {
	    String group_code = params.get("group_code");
	    String id = params.get("id");
	    String group_name = params.get("group_name");
	    String acq_no = params.get("acq_enname");
	    String agent_no = params.get("agent_no");
	    String accounts_period = params.get("accounts_period");
	    String sales_no = params.get("sales");
	    String merchant_type = params.get("merchant_type");
	    String route_last = params.get("route_last_name");
	    String route_describe = params.get("route_describe");
	    String route_type = params.get("route_type");
	    String status = params.get("status");
	    String province = params.get("province");
	    String sql = "update trans_route_group t set t.group_name=?,t.acq_no=?,t.agent_no=?,t.accounts_period=?,t.sales_no=?,t.merchant_type=?,t.route_last=?,t.route_describe=?,t.route_type=?,t.status=?,t.group_province=? where t.group_code =? and t.id=?";

	    List<Object> listPosTerminal = new ArrayList<Object>();
	    listPosTerminal.add(group_name);
	    listPosTerminal.add(acq_no);
	    if(null != agent_no && !"".equals(agent_no) && !"全部".equals(agent_no)){
	    	listPosTerminal.add(agent_no);
	    }else{
	    	listPosTerminal.add(null);
	    }
	    
	    listPosTerminal.add(accounts_period);
	    if(null !=sales_no && !"".equals(sales_no) && !"全部".equals(sales_no) && !"-1".equals(sales_no)){
	    	 listPosTerminal.add(sales_no);
	    }else{
	    	 listPosTerminal.add(null);
	    }
	   
	    listPosTerminal.add(merchant_type);
	    listPosTerminal.add(route_last);
	    if(null != route_describe && !"".equals(route_describe)){
	    	listPosTerminal.add(route_describe);
	    }else{
	    	listPosTerminal.add(null);
	    }
	    
	    
	    
	    listPosTerminal.add(route_type);
	    listPosTerminal.add(status);
	    if(null !=province && !"".equals(province)){
	    	listPosTerminal.add(province);
	    }else{
	    	listPosTerminal.add(null);
	    }
	    listPosTerminal.add(group_code);
	    listPosTerminal.add(id);
	    int rowsuc = dao.update(sql, listPosTerminal.toArray());
	    return rowsuc;
	  }
	
	/**
	 * 改版后新增集群信息
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int addGroupInfo(Map<String, String> params) throws SQLException {
	    String group_code = params.get("group_code");
	    String group_name = params.get("group_name");
	    String acq_no = params.get("acq_enname");
	    String agent_no = params.get("agent_no");
	    String accounts_period = params.get("accounts_period");
	    String sales_no = params.get("sales");
	    String merchant_type = params.get("merchant_type");
	    String route_last = params.get("route_last_name");
	    String route_describe = params.get("route_describe");
	    String route_type = params.get("route_type");
	    String province = params.get("province");
	    String my_settle = params.get("my_settle");
	    String bag_settle = params.get("bag_settle");
	    
	    String insert_pos_terminal = "insert into  trans_route_group( group_code,group_name,acq_no,agent_no,accounts_period,sales_no,merchant_type,route_last,route_describe,route_type,group_province,my_settle,bag_settle) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

	    List<Object> listPosTerminal = new ArrayList<Object>();
	    listPosTerminal.add(group_code);
	    listPosTerminal.add(group_name);
	    listPosTerminal.add(acq_no);
	    if(!"全部".equals(agent_no) && !"-1".equals(agent_no)){
	    	listPosTerminal.add(agent_no);
	    }else{
	    	listPosTerminal.add(null);
	    }
	    
	    listPosTerminal.add(accounts_period);
	   
	    if(!"".equals(sales_no) && !"-1".equals(sales_no)){
	    	 listPosTerminal.add(sales_no);
	    }else{
	    	listPosTerminal.add(null);
	    }
	    listPosTerminal.add(merchant_type);
	    listPosTerminal.add(route_last);
	    listPosTerminal.add(route_describe);
	    /*if(null != route_describe && !"".equals(route_describe)){
	    	listPosTerminal.add(route_describe);
	    }else{
	    	listPosTerminal.add("");
	    }*/
	    listPosTerminal.add(route_type);
	    if(null != province && !"".equals(province)){
	    	 listPosTerminal.add(province);
	    }else{
	    	listPosTerminal.add(null);
	    }
	    listPosTerminal.add(my_settle);
	    listPosTerminal.add(bag_settle);
	    int rowsuc = dao.update(insert_pos_terminal, listPosTerminal.toArray());
	    return rowsuc;
	  }
	
	/**
	 * 查询所有集群信息
	 *  @param params 查询条件
	 *  @author zengja
	 *  @date 2014年5月12日 上午11:11:53
	 */
	public List<Map<String, Object>> getGroups(Map<String, String> params){
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
				String group_code = params.get("group_code");
				sb.append(" and a.group_code = ? ");
				list.add(group_code);
			}
			if (StringUtils.isNotEmpty(params.get("group_name"))) {
				String group_name = params.get("group_name");
			/*	sb.append(" and a.group_name  like  ? ");
				list.add("%" + group_name + "%");*/
				sb.append(" and a.group_name  =  ? ");
				list.add(group_name);
			}
			if (StringUtils.isNotEmpty(params.get("id"))) {
				String id = params.get("id");
				sb.append(" and a.id = ? ");
				list.add(id);
			}

		}
		String sql = "SELECT * from trans_route_group a where 1=1 " + sb.toString() + " order by a.id  ";
		List<Map<String, Object>> list_rs = dao.find(sql, list.toArray());
		return list_rs;
	}

	/**
	 * 数据库中
	 * 
	 * @param userName
	 * @return
	 */
	public Page<Map<String, Object>> getGroups(Map<String, String> params,PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			 
			if (StringUtils.isNotEmpty(params.get("group_code"))) {
 					String group_code = params.get("group_code").trim();
					sb.append(" and a.group_code = ? ");
					list.add(group_code);
			}
			
		    if (StringUtils.isNotEmpty(params.get("group_name"))) {
			        String group_name = params.get("group_name").trim();
			      /*  sb.append(" and a.group_name  like  ? ");
			        list.add("%"+group_name+"%");*/
			        sb.append(" and a.group_name  =  ? ");
			        list.add(group_name);
	         }  
		    
		    if (StringUtils.isNotEmpty(params.get("status"))) {
		    	int  status=Integer.parseInt(params.get("status"));
		    	if(!"".equals(status)){
			        sb.append(" and a.status =  ? ");
			        list.add(status);
		    	}
            }
		    
		    if (StringUtils.isNotEmpty(params.get("merchant_type"))) { //验证所属商户类型
		    	int  merchant_type =Integer.parseInt(params.get("merchant_type"));
			        sb.append(" and a.merchant_type =  ? ");
			        list.add(merchant_type);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("sales"))) { //验证所属销售
		    	int  sales =Integer.parseInt(params.get("sales"));
			        sb.append(" and a.sales_no =  ? ");
			        list.add(sales);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("agentNo"))) { //验证所属代理商
		    	int  agent_no =Integer.parseInt(params.get("agentNo"));
		    	if(agent_no > 0){
		    		sb.append(" and a.agent_no =  ? ");
			        list.add(agent_no);
		    	}
            }
		    
		    if (StringUtils.isNotEmpty(params.get("acq_enname"))) { //验证所属收单机构
		    	String  acq_enname =params.get("acq_enname");
			        sb.append(" and a.acq_no =  ? ");
			        list.add(acq_enname);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("route_type"))) { //验证集群类型
		    	int  route_type =Integer.parseInt(params.get("route_type"));
			        sb.append(" and a.route_type =  ? ");
			        list.add(route_type);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("accounts_period"))) { //验证结算周期
		    	String  accounts_period =params.get("accounts_period");
			        sb.append(" and a.accounts_period =  ? ");
			        list.add(accounts_period);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("province"))) { //验证结算周期
		    	String  province =params.get("province");
			        sb.append(" and a.group_province =  ? ");
			        list.add(province);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("my_settle")) && !"-1".equals(params.get("my_settle"))) { //是否优质
		    	String  my_settle =params.get("my_settle");
			        sb.append(" and a.my_settle =  ? ");
			        list.add(my_settle);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("bag_settle")) && !"-1".equals(params.get("bag_settle"))) { //是否优质
		    	String  bag_settle =params.get("bag_settle");
			        sb.append(" and a.bag_settle =  ? ");
			        list.add(bag_settle);
            }
		    
		    if (StringUtils.isNotEmpty(params.get("id"))) {
			      String id = params.get("id");
			       sb.append(" and a.id = ? ");
			       list.add(id);
	         }
		    
		    String specialName = params.get("specialName");
		    String specialCode = params.get("specialCode");
		    if(!"true".equals(specialName) && specialCode != null){
		    	sb.append(" and a.group_code <> ? ");
		    	list.add(specialCode);
		    }
		   
		}
		String sql = "SELECT * from trans_route_group a where 1=1 "
				+ sb.toString() + " order by a.id  ";
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	
	/**
	 * 数据库中根据id查询
	 * 
	 * @param id
	 * @author LJ
	 * @return
	 */
	public Page<Map<String, Object>> getGroupsById(String id,PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		String sql = "SELECT * from trans_route_group a where 1=1  and  id=? ";
		list.add(id);
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	

 
	public int addSubmit(Map<String, String> params) throws SQLException {
    String group_code = params.get("group_code");
    String group_name = params.get("group_name");
   
    String insert_pos_terminal = "insert into    trans_route_group( group_code,group_name) values(?,?)   ";

    List<Object> listPosTerminal = new ArrayList<Object>();
    listPosTerminal.add(group_code);
    listPosTerminal.add(group_name);
    int rowsuc = dao.update(insert_pos_terminal, listPosTerminal.toArray());
    return rowsuc;
  }
	
	 public int updateSubmit(Map<String, String> params) throws SQLException {
        String id = params.get("id");
 	    String group_name = params.get("group_name");
 	    //String status=params.get("status");
 	    int status = Integer.parseInt(params.get("status"));
	   
	    String insert_pos_terminal = "update   trans_route_group set  group_name=?,status=?  where id=? ";

	    List<Object> listPosTerminal = new ArrayList<Object>();
	    listPosTerminal.add(group_name);
	    listPosTerminal.add(status);
	    listPosTerminal.add(id);
	    int rowsuc = dao.update(insert_pos_terminal, listPosTerminal.toArray());
	    return rowsuc;
	  }
	 

	public void agentVersionSave(Map<String, String> params) throws SQLException{
		Connection conn = null;
		String version=params.get("version");
		String phone_os=params.get("phone_os");
		String ver_desc=params.get("ver_desc");
		String is_top=params.get("is_top");
		String download_url=params.get("download_url");
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			
			int verCount = getVersionCount(phone_os).intValue();
			if(verCount > 0){
				String sql="update agent_app_version set is_top = 0 where phone_os = ? and is_top=1";
				dao.updateByTranscation(sql, new Object[]{phone_os}, conn);
			}
			
			String sql="insert into agent_app_version (version, phone_os, ver_desc, is_top, download_url, create_time)values(?, ?, ?, ?, ?, now())";
			dao.updateByTranscation(sql,new Object[]{version,phone_os,ver_desc,is_top,download_url},conn);
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();//回滚事务
			throw new RuntimeException();
		}finally{
			conn.close();//关闭连接
		}
		
	}
	
	/**
	 * 判断是否有存在相同的集群名称
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> existGroupInfo(int id, String  group_name) throws SQLException
	{
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" select * from trans_route_group where 1=1 ");
		
		if (StringUtils.isNotEmpty(group_name)) {
			sql.append(" and group_name =? ");
			list.add(group_name);
		}
		
		if (id > 0) {
			sql.append(" and id !=? ");
			list.add(id);
		}
		
		return dao.find(sql.toString(), list.toArray());
	}
	
	
	/**
	 * 判断是否有存在相同的集群名称
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> groupQuery(String  group_name) throws SQLException
	{
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" select * from trans_route_group where 1=1 ");
		
		if (StringUtils.isNotEmpty(group_name)) {
			sql.append(" and group_name =? ");
			list.add(group_name);
		}
		return dao.find(sql.toString(), list.toArray());
	}
	
	/**
	 * 判断是否有存在相同的集群编号
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> groupCodeQuery(String  group_code) throws SQLException
	{
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" select * from trans_route_group where 1=1 ");
		if (StringUtils.isNotEmpty(group_code)) {
			sql.append(" and group_code =? ");
			list.add(group_code);
		}
		return dao.find(sql.toString(), list.toArray());
	}
	
	

	/**
	 * 修改判断根据id和group_name查询是否存在
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> groupNameQuery(String  group_name,String id) throws SQLException
	{
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" select * from trans_route_group where 1=1 ");
		
		if (StringUtils.isNotEmpty(group_name)) {
			sql.append(" and group_name =? ");
			list.add(group_name);
		}
		if (StringUtils.isNotEmpty(id)) {
			sql.append(" and id =? ");
			list.add(id);
		}
		return dao.find(sql.toString(), list.toArray());
	}
	
	
	
	public int agentVersionModify(Map<String, String> params) throws SQLException{
		String version=params.get("version");
		String phone_os=params.get("phone_os");
		String ver_desc=params.get("ver_desc");
		String is_top=params.get("is_top");
		String download_url=params.get("download_url");
		String id=params.get("id");
		String sql="update agent_app_version set version=?,phone_os=?,ver_desc=?,is_top=?,download_url=? where id=?";
		int num=dao.update(sql,new Object[]{version,phone_os,ver_desc,is_top,download_url,Long.parseLong(id)});
		return num;
	}
	//删除版本信息
	public int deleteAgentVersion(long id) throws SQLException{
		String sql="delete from agent_app_version where id=?";
		return dao.update(sql, id);
	}
	
	public Map<String, Object> checkModVersion(String version,String phone_os,String id) {
		String sql = " select count(*) count FROM agent_app_version where phone_os =? and version =? and id != ?";
		Object[] ter = {phone_os,version, id };
		return dao.findFirst(sql, ter);
	}
	public Map<String, Object> checkAddVersion(String version,String phone_os) {
		String sql = " select count(*) count FROM agent_app_version where phone_os =? and version =?";
		Object[] ter = {phone_os,version };
		return dao.findFirst(sql, ter);
	}
	public Long getVersionCount(String phone_os) {
		String sql = "select count(*) count from agent_app_version where is_top =1 and phone_os ="+phone_os;
		return (Long) dao.findFirst(sql).get("count");
	}
}
