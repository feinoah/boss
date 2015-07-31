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
import com.eeepay.boss.utils.StringUtil;

/**
 * 版本管理
 * 
 * @author yt
 * 
 */
@Service
public class VersionService {
	@Resource
	private Dao dao;

	
	/**
	 * 数据库中 查询
	 * 
	 * @param userName
	 * @return
	 */
	public Page<Map<String, Object>> getAgentVersionList(Map<String, String> params,PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 系统平台
			if (StringUtils.isNotEmpty(params.get("platform"))) {
				if (!params.get("platform").equals("-1")) {
					String platform = params.get("platform");
					sb.append(" and a.platform = ? ");
					list.add(platform);
				}
			}
			if(StringUtils.isNotEmpty(params.get("app_type"))){
				if(!params.get("app_type").equals("-1")){
					String app_type=params.get("app_type");
					sb.append(" and a.app_type = ?");
					list.add(app_type);
				}
			}
		}
		String sql = "SELECT * from mobile_ver_info a where 1=1 "
				+ sb.toString() + " order by a.create_time desc ";
		return dao.find(sql, list.toArray(), pageRequest);
	}
    
	/**
	 * 数据库中 查询详情
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> getAgentVersionById(long id) throws SQLException{
		String sql="select * from mobile_ver_info where id=?";
		Map<String,Object> map= dao.findFirst(sql, id);
		return map;
	}

	
	/**
	 * 代理商版本保存
	 * @param params
	 * @throws SQLException
	 */
	public void agentVersionSave(Map<String, String> params) throws SQLException{
		Connection conn = null;
		String version=params.get("version");
		String platform=params.get("platform");
		String ver_desc=params.get("ver_desc");
		String app_url=params.get("app_url");
		String down_flag=params.get("down_flag");
		String app_type=params.get("app_type");
		String app_logo=params.get("app_logo");
		try {
			conn = dao.getConnection();
			conn.setAutoCommit(false);
			String sql="insert into mobile_ver_info (platform,version,app_url,down_flag,ver_desc,app_type,app_logo,create_time)" +
					"values(?, ?, ?, ?, ?, ?, ?, now())";
			dao.updateByTranscation(sql,new Object[]{platform,version,app_url,down_flag,ver_desc,app_type,app_logo},conn);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();//回滚事务
			throw new RuntimeException();
		}finally{
			conn.close();//关闭连接
		}		
	}
	
	
	//修改版本信息
	public int agentVersionModify(Map<String, String> params) throws SQLException{
		String version=params.get("version");
		String platform=params.get("platform");
		String ver_desc=params.get("ver_desc");
		String app_url=params.get("app_url");
		String down_flag=params.get("down_flag");
		String app_type=params.get("app_type");
		String app_logo=params.get("app_logo");
		String id=params.get("id");
		String sql="update mobile_ver_info set version=?,platform=?,ver_desc=?,app_url=?,down_flag=?,app_type=?,app_logo=? where id=?";
		int num=dao.update(sql,new Object[]{version,platform,ver_desc,app_url,down_flag,app_type,app_logo,Long.parseLong(id)});
		return num;
	}
	
	
	
	//删除版本信息
	public int deleteAgentVersion(long id) throws SQLException{
		String sql="delete from mobile_ver_info where id=?";
		return dao.update(sql, id);
	}
	
	
	//修改时验证
	public Map<String, Object> checkModVersion(String version,String platform,String id) {
		String sql = " select count(*) count FROM mobile_ver_info where platform =? and version =? and id != ?";
		Object[] ter = {platform,version, id };
		return dao.findFirst(sql, ter);
	}
	
	
	//添加时验证
	public Map<String, Object> checkAddVersion(String version,String platform) {
		String sql = " select count(*) count FROM mobile_ver_info where platform =? and version =?";
		Object[] ter = {platform,version };
		return dao.findFirst(sql, ter);
	}
	
}
