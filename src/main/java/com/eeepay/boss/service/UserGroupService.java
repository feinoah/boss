package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.AuthList;
import com.eeepay.boss.domain.UserGroup;
import com.eeepay.boss.utils.Dao;

/**
 * 用户组信息
 * @author hdb
 */
@Service
public class UserGroupService {
	
	/**
	 * @param params
	 * @param page
	 * @return
	 */
	public List<Map<String,Object>> getUserToGroupId(int groupId) {
		String sql="select bu.id,bu.user_name,bu.real_name from user_auth ua,boss_user bu where ua.user_id=bu.id and ua.auth_id="+groupId+"  and bu.status=1 order by bu.id desc";
		return dao.find(sql);
	}
	
	/**
	 * @param params
	 * @param page
	 * @return
	 */
	public List<Map<String,Object>> getUserToGroupId(int groupId, int groupId2) {
		String sql="select bu.id,bu.user_name,bu.real_name from user_auth ua,boss_user bu where ua.user_id=bu.id and (ua.auth_id="+groupId+" or ua.auth_id="+groupId2+")  and bu.status=1 order by bu.id desc";
		return dao.find(sql);
	}

	@Resource
	private Dao dao;
	
	public Page<Map<String, Object>> getUserGroupList(Map<String, String> params,
			PageRequest pageRequest) {
		String sql = "select a.* from user_group a order by a.id desc ";
		return dao.find(sql, new Object[]{}, pageRequest);
	}
	
	public List<UserGroup> getUserGroupByID(Long id){
		String sql = "select * from user_group where id=?";
		try {
			List<Object> userInfo = new ArrayList<Object>();
			userInfo.add(id);
			return dao.find(UserGroup.class, sql, userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int userGroupDel(Long id) {
		String delete_user_group = "delete from user_group  where id =? ";
		String delete_group_auth = "delete  from group_auth where user_group_id=? ";
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		try {
			dao.update(delete_group_auth,list.toArray());
			dao.update(delete_user_group,list.toArray());
			return 1;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public Map<String, Object> queryUserGroupInfo(long id) {
		String sql ="select a.*  from user_group a  where a.id="+id;
		return dao.findFirst(sql);
	}

	public long userGroupSave(Map<String, String> params,String[] authVals) throws SQLException {
		String group_name=params.get("group_name");
		String group_desc=params.get("group_desc");
		String sql = "insert into user_group(group_name,group_desc,create_time) values(?,?,now())";
		List<Object> paramList=new ArrayList<Object>();
		paramList.add(group_name);
		paramList.add(group_desc);
		Long id=dao.updateGetID(sql,paramList);//TODO 插入id
		String inSql="insert into group_auth(user_group_id,auth_id) values(?,?)";//
		for(String authID:authVals){
			List<Object> pams=new ArrayList<Object>();
			pams.add(id);
			pams.add(Long.parseLong(authID));
			dao.update(inSql, pams.toArray());
		}
		return id;
	}

	public int userGroupUpdate(Map<String, String> params, String[] authVals) throws SQLException {
		String id=params.get("id");
		String group_name=params.get("group_name");
		String group_desc=params.get("group_desc");
		String sql = "update user_group set group_name=?,group_desc=? where id=?";
		List<Object> paramList=new ArrayList<Object>();
		paramList.add(group_name);
		paramList.add(group_desc);
		paramList.add(Long.parseLong(id));
		dao.updateGetID(sql,paramList);//TODO 
		String delSql="delete from group_auth where user_group_id=?";
		dao.update(delSql,Long.parseLong(id));//删除
		
		String inSql="insert into group_auth(user_group_id,auth_id) values(?,?)";//
		for(String authID:authVals){
			List<Object> pams=new ArrayList<Object>();
			pams.add(id);
			pams.add(Long.parseLong(authID));
			dao.update(inSql, pams.toArray());
		}
		return 1;		
	}

	
	public List<Map<String, Object>> getUserGroupList(long userID) {
		String sql="SELECT * FROM user_group u";
		String uGroupSql="select ug.* from user_auth ua,user_group ug where ua.user_id=? and ua.auth_id=ug.id";
		List<Map<String,Object>> list=dao.find(sql);
		List<Map<String,Object>> list2=dao.find(uGroupSql, userID);
		for(Map<String,Object> mp:list2){
			Object id=mp.get("id");
			for(Map<String,Object> mmp:list){
				Object mID=mmp.get("id");
				if(id.equals(mID)){
					mmp.put("checked", "show");
				}else{
					if(!mmp.containsKey("checked")){
						mmp.put("checked", "hiden");
					}
				}
			}
		}
		return list;
	}

	public List<Map<String, Object>> getUserGroupList() {
		String sql="SELECT * FROM user_group u";
		return dao.find(sql);
	}

	public List<AuthList> getAuthList() throws SQLException {
		String sql="select * from boss_auth";
		List<AuthList> authList=new ArrayList<AuthList>();
		List<Map<String,Object>> list=dao.find(sql);
		//分组
		for(Map<String,Object> mp:list){
			int parent_id=(Integer)mp.get("parent_id");
			AuthList auth=new AuthList();
			Object id=mp.get("id");
			if(parent_id==0){
				auth.setId((Integer)mp.get("id"));	
				auth.setParent_code((String)mp.get("auth_code"));
				auth.setParent_name((String)mp.get("auth_name"));
				List<AuthList> nodeList=new ArrayList<AuthList>();
//				List<Map<String,Object>> nodeList=new ArrayList<Map<String,Object>>();
				for(Map<String,Object> mmp:list){
					Object node_parent_id=mmp.get("parent_id");
					if(id.equals(node_parent_id)){
						nodeList.add(new AuthList((Integer)mmp.get("id"), (String)mmp.get("auth_name") , (String)mmp.get("checked") , (Integer)mmp.get("parent_id")));
					}
				}
				Collections.sort(nodeList);
				auth.setNodeList(nodeList);
			}
			if(auth!=null&&auth.getId()!=null&&auth.getId()>0){
				authList.add(auth);
			}
		}
		getAuthList(authList,list);
		return authList;
	}
	
	public List<AuthList> getAuthList(long groupID) {
		String sql="select * from boss_auth";
		List<Map<String, Object>> list=dao.find(sql);
		String authSql="SELECT ba.* FROM group_auth ga , boss_auth ba where ga.auth_id=ba.id and ga.user_group_id=?";
		List<Map<String,Object>> list2=dao.find(authSql, groupID);
		//checked
		for(Map<String,Object> mp:list2){
			Object id=mp.get("id");
			for(Map<String,Object> mmp:list){
				Object mID=mmp.get("id");
				if(id.equals(mID)){
					mmp.put("checked", "show");
				}else{
					if(!mmp.containsKey("checked")){
						mmp.put("checked", "hiden");
					}
				}
			}
		}
		//分组
		List<AuthList> authList=new ArrayList<AuthList>();
		for(Map<String,Object> mp:list){
			int parent_id=(Integer)mp.get("parent_id");
			AuthList auth=new AuthList();
			Object id=mp.get("id");
			if(parent_id==0){
				auth.setId((Integer)mp.get("id"));	
				auth.setParent_code((String)mp.get("auth_code"));
				auth.setParent_name((String)mp.get("auth_name"));
				auth.setChecked((String)mp.get("checked"));
				List<AuthList> nodeList=new ArrayList<AuthList>();
//				List<Map<String,Object>> nodeList=new ArrayList<Map<String,Object>>();
				for(Map<String,Object> mmp:list){
					Object node_parent_id=mmp.get("parent_id");
					if(id.equals(node_parent_id)){
						nodeList.add(new AuthList((Integer)mmp.get("id"), (String)mmp.get("auth_name") , (String)mmp.get("checked") , (Integer)mmp.get("parent_id")));
					}
				}
				Collections.sort(nodeList);
				auth.setNodeList(nodeList);
			}
			if(auth!=null&&auth.getId()!=null&&auth.getId()>0){
				authList.add(auth);
			}
		}
		authList=getAuthList(authList,list);
		return authList;
	}
	
	private List<AuthList> getAuthList(List<AuthList> authList,List<Map<String, Object>> list){
		for(AuthList auth:authList){
			for(AuthList nodeAuth:auth.getNodeList()){
				List<AuthList> nodeList=new ArrayList<AuthList>();
				for(Map<String, Object> mmp :list){
					Integer parent_id=(Integer)mmp.get("parent_id");
					if(nodeAuth.getId().equals(parent_id)){
						nodeList.add(new AuthList((Integer)mmp.get("id"), (String)mmp.get("auth_name"), (String)mmp.get("checked"), (Integer)mmp.get("parent_id")));
					}
				}
				if(nodeList.size()>0){
					Collections.sort(nodeList);
				}
				nodeAuth.setNodeList(nodeList);
			}
		}
		return authList;
	}

	public boolean checkGroupName(Map<String, String> params) {
		Object group_name=params.get("group_name");
		String sql="SELECT u.* FROM user_group u where u.group_name=?";
		List<Map<String,Object>> listMap=dao.find(sql, group_name);
		return (listMap!=null&&listMap.size()>0)?true:false;
	}

	public boolean checkGroupCanDel(long id) {
		String sql="SELECT * FROM user_auth ua,boss_user bu where ua.auth_id=? and ua.user_id=bu.id and bu.status=1";
		List<Map<String,Object>> listMap=dao.find(sql, id);
		return (listMap!=null&&listMap.size()>0)?true:false;
	}

	/**
	 * @param params
	 * @param page
	 * @return
	 */
	public Page<Map<String, Object>> getUserList(Map<String, String> params,
			PageRequest pageRequest) {
		String sql="select bu.* from user_auth ua,boss_user bu where ua.user_id=bu.id and ua.auth_id=? order by bu.id desc";
		return dao.find(sql, new Object[]{params.get("id")}, pageRequest);
	}
	
	/**
	 * @param params
	 * @param page
	 * @return
	 */
	public List<Map<String,Object>> getUserToGroupList(int groupId) {
		String sql="select bu.* from user_auth ua,boss_user bu where ua.user_id=bu.id and ua.auth_id="+groupId+"  and bu.status=1 order by bu.id desc";
		
		return dao.find(sql);
	}
	
	/**
	 * @param params
	 * @param page
	 * @return
	 */
	public List<Map<String,Object>> getUserToGroupList(int groupId, int groupId2) {
		String sql="select bu.* from user_auth ua,boss_user bu where ua.user_id=bu.id  and bu.status=1 and ua.auth_id="+groupId+" or  ua.auth_id= "+groupId2+" order by bu.id desc";
		return dao.find(sql);
	}
	
	/**
	 * @param params
	 * @param page
	 * @return
	 */
	public List<Map<String,Object>> getUserToGroupList() {
		String sql="select bu.* from user_auth ua,boss_user bu where ua.user_id=bu.id  and bu.status=1 and ua.auth_id in(2,48,34,35,58,57,56,55) order by bu.id desc";
		return dao.find(sql);
	}
}
