package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;

/**
 * 权限信息
 * @author hdb
 */
@Service
public class AuthManagerService {

	@Resource
	private Dao dao;

	public Page<Map<String, Object>> getAuthList(Map<String, String> params,
			PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 用户名
			if(StringUtils.isNotEmpty(params.get("auth_name"))){
				/*	String auth_name = "%"+params.get("auth_name")+"%";
					sb.append(" and a.auth_name like ? ");*/
					String auth_name =params.get("auth_name");
					sb.append(" and a.auth_name = ? ");
					list.add(auth_name);
			}
			if(StringUtils.isNotEmpty(params.get("parent_id"))&&!"-1".equals(params.get("parent_id"))){
				String parent_id = params.get("parent_id");
				sb.append(" and a.parent_id = ? ");
				list.add(parent_id);
			}
		}
		String sql = "SELECT a.*,b1.auth_name as parent_name from boss_auth a left join boss_auth b1 on a.parent_id=b1.id where 1=1 "
				+ sb.toString() + " order by a.id desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}

	public boolean checkAuthCanDel(Map<String, String> params) {
		String sql="select * from boss_auth a where a.parent_id=?";
		Long parent_id = Long.parseLong(params.get("id"));
		List<Map<String,Object>> list=dao.find(sql, parent_id);
		return (list!=null&&list.size()>0)?false:true;
	}

	public int bossAuthDel(Map<String, String> params) {
		String id = params.get("id");
		String delete_group_auth = "delete from group_auth  where auth_id =? ";
		String delete_boss_auth = "delete from boss_auth  where id =? ";
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		try {
			dao.update(delete_group_auth,list.toArray());
			dao.update(delete_boss_auth,list.toArray());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<Map<String, Object>> getParentAuthList() {
		String sql="SELECT * FROM boss_auth b where category_id=? order by  b.id desc";
		long category_id=1;
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(category_id);
		List<Map<String,Object>> list=dao.find(sql,paramList.toArray());
		List<Map<String,Object>> valList=new ArrayList<Map<String,Object>>();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id", "0");
		map.put("auth_name", "无");
		valList.add(map);
		valList.addAll(list);
		return valList;
	}
	
	public boolean chechedParent(long parentID){
		if(parentID==0){
			return true;
		}
		String sql="select b.* from boss_auth b where b.id=?";
		List<Map<String,Object>> list= dao.find(sql, parentID);
		return (list!=null&&list.size()>0)?true:false;
	}

	public int userAuthSave(Map<String, String> params) throws SQLException {
		String auth_name=params.get("auth_name");
		String auth_code=params.get("auth_code");
		String category_id=params.get("category_id");
		String parent_id=params.get("parent_id");
		String sql="insert into boss_auth(auth_name,auth_code,parent_id,create_time,category_id) values(?,?,?,now(),?)";
		int num=dao.update(sql,new Object[]{auth_name,auth_code,parent_id,category_id});
		return num;
	}

	public boolean chechedAuthCode(String authCode) {
		String sql="select b.* from boss_auth b where b.auth_code=?";
		List<Map<String,Object>> list= dao.find(sql, authCode);
		return (list!=null&&list.size()>0)?true:false;
	}

	public Map<String, Object> getAuthMap(long id) {
		String sql="select b.* from boss_auth b where b.id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}

	public int userAuthUpdate(Map<String, String> params) throws SQLException {
		String id=params.get("id");
		String auth_name=params.get("auth_name");
		String auth_code=params.get("auth_code");
		String category_id=params.get("category_id");
		String parent_id=params.get("parent_id");
		String sql="update boss_auth set auth_name=?,auth_code=?,parent_id=?,category_id=? where id=?";
		int num=dao.update(sql,new Object[]{auth_name,auth_code,parent_id,category_id,Long.parseLong(id)});
		return num;
	}

	public List<Map<String, Object>> getParentAuthListAll() {
		String sql="SELECT * FROM boss_auth b where category_id=? order by  b.id desc";
		long category_id=1;
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(category_id);
		List<Map<String,Object>> list=dao.find(sql,paramList.toArray());
		List<Map<String,Object>> valList=new ArrayList<Map<String,Object>>();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id", "0");
		map.put("auth_name", "无");
		Map<String,Object> map2=new HashMap<String,Object>();
		map2.put("id", "-1");
		map2.put("auth_name", "全部");
		valList.add(map);
		valList.add(map2);
		valList.addAll(list);
		return valList;
	}
	
}
