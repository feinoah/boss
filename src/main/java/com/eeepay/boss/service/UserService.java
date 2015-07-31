package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.BossAuth;
import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.IPParser;
import com.eeepay.boss.utils.IPParser.IPLocation;
import com.eeepay.boss.utils.MD5;

/**
 * 系统用户及权限管理
 * 
 * @author dj
 * 
 */
@Service
public class UserService {
	@Resource
	private Dao dao;

	/**
	 * 数据库中
	 * 
	 * @param userName
	 * @return
	 */
	public BossUser getByUserName(String userName) {
		BossUser bossUser = null;
		String sql = "select * from boss_user where user_name=?";
		bossUser = dao.findFirst(BossUser.class, sql, userName);
		return bossUser;
	}

	public BossUser getUserByPwd(String userName, String pwd) {
		BossUser bossUser = null;
		List<Object> userInfo = new ArrayList<Object>();
		userInfo.add(userName);
		userInfo.add(pwd);
		String sql = "select * from boss_user where user_name=? and password=?";
		bossUser = dao.findFirst(BossUser.class, sql, userInfo.toArray());
		return bossUser;
	}

	public Integer getFailTimes(String userName) {
		String sql = "select fail_times from   boss_user   where user_name=?";
			Map<String, Object> map=dao.findFirst(sql, userName);
			return Integer.valueOf(map.get("fail_times").toString());
	}
	
	public void clearFailTimes(String userName) {
		String sql = "update  boss_user set fail_times=0 where user_name=?";
		try {
			dao.update(sql, userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void failTimes(String userName) {
		String sql = "update  boss_user set fail_times=fail_times+1 where user_name=?";
		try {
			dao.update(sql, userName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void failTimesReset(String username){
		String sql="update boss_user set fail_times=0 where user_name=?";
		try {
			dao.update(sql, username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void status(String userName,int status) {
		String sql = "update  boss_user set status=? where user_name=?";
		try {
			dao.update(sql, new Object[]{status,userName});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int changUserPwd(String userName, String oldPwd, String newPwd)
			throws SQLException {
		String sql = "update boss_user set password =?, update_passw_time = now() where user_name=? and password=? ";
		List<Object> listUser = new ArrayList<Object>();
		listUser.add(newPwd);
		listUser.add(userName);
		listUser.add(oldPwd);
		int affectedRows = dao.update(sql, listUser.toArray());
		return affectedRows;
	}

	public List<BossAuth> authList(Long userId) {
		String sql = "select ba.* from user_auth ua,boss_auth ba where ua.user_id=? and ua.auth_id=ba.id";
		Object[] params = { userId };
		try {
			return dao.find(BossAuth.class, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveLog(String ip, BossUser bu) throws SQLException {
		String sql = "insert into sys_login_log(sys_name,user_id,user_name,login_ip,location,last_login_time) values(?,?,?,?,?,now())";
		String location = "";
		try {
			IPLocation ipp = IPParser.parse(ip);
			location = ipp.country + ipp.country;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object[] params = { Constants.SYS_NAME, bu.getId(), bu.getRealName(),
				ip, location };
		dao.update(sql, params);
	}

	public Page<Map<String, Object>> getUserList(Map<String, String> params,
			PageRequest pageRequest) {

		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		if (null != params) {
			// 用户名
			if (StringUtils.isNotEmpty(params.get("user_name"))) {
				String user_name = params.get("user_name");
				sb.append(" and bu.user_name = ? ");
				list.add(user_name);
			}
		}

		String sql = "select tempzz.*,ug.group_name from (select tm.*,ua.auth_id from (select * from boss_user bu left join (select max(last_login_time) last_login_time ,user_id from sys_login_log where sys_name='BOSS'or sys_name='boss' group by user_id ) temp on bu.id=temp.user_id where 1=1 "
				+ sb.toString()
				+ ") tm left join user_auth ua on tm.id=ua.user_id) tempzz left join user_group ug on tempzz.auth_id=ug.id";
		// select tu.*,ug.group_name from (select bu.*,min(llg.last_login_time)
		// last_login_time from boss_user bu left join sys_login_log llg on
		// bu.id=llg.user_id group by id having 1=1 "+ sb.toString() + ")
		// tu,user_auth ua,user_group ug where tu.id=ua.user_id and
		// ua.auth_id=ug.id
		// String sql =
		// "select tu.*,ug.group_name from (select bu.*,min(llg.last_login_time) last_login_time from boss_user bu left join sys_login_log llg on bu.id=llg.user_id group by id having 1=1 "
		// + sb.toString() +
		// ") tu,user_auth ua,user_group ug where tu.id=ua.user_id and ua.auth_id=ug.id  order by tu.id desc ";
		// String sql =
		// "select bu.*,min(llg.last_login_time) last_login_time from boss_user bu left join sys_login_log llg on bu.id=llg.user_id group by id having 1=1 "
		// + sb.toString() + " order by bu.id desc ";
		return find(sql, list.toArray(), pageRequest);
	}

	private Page<Map<String, Object>> find(String sql, Object[] params,
			PageRequest pageRequest) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Long count = count(sql, params);
		String limit = " limit " + pageRequest.getOffset() + " ,"
				+ pageRequest.getPageSize();
		sql += limit;

		try {
			if (params == null) {
				list = (List<Map<String, Object>>) dao.find(sql);
			} else {
				list = (List<Map<String, Object>>) dao.find(sql, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new PageImpl<Map<String, Object>>(list, pageRequest, count);
	}

	private Long count(String sql, Object[] params) {
		Long retVal = 0L;
		try {
			sql = "select count(aa.id) as count from (" + sql + ") aa";
			Map<String, Object> first = dao.findFirst(sql, params);
			retVal = Long.valueOf(first.get("count").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public boolean checkUseCanDel(Map<String, String> params) {
		Long id = Long.parseLong(params.get("id"));
		List<BossAuth> bossAuthList = authList(id);
		if (bossAuthList != null && bossAuthList.size() > 0) {
			return true;
		}
		String sql = "select * from boss_user where id=?";
		List<Map<String, Object>> list = dao.find(sql, id);
		return (list != null && list.size() > 0) ? true : false;
	}

	public int bossUserDel(Map<String, String> params) {
		String id = params.get("id");
		// String delete_agent_info =
		// "delete from user_auth  where user_id =? ";
		// String delete_agent_user = "delete from boss_user  where id =? ";
		String update_boss_user = "update boss_user set status=0 where id=?";
		List<Object> list = new ArrayList<Object>();
		list.add(id);
		try {
			// dao.update(delete_agent_info,list.toArray());
			dao.update(update_boss_user, list.toArray());
			// dao.update(delete_agent_user,list.toArray());
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public Map<String, Object> queryUserInfo(long id) {
		String sql = "select *  from boss_user a  where a.id=" + id;
		return dao.findFirst(sql);
	}

	public void modifySystemUser(Map<String, String> params, String[] values) {
		String id = params.get("id");
		// String user_name = params.get("user_name");
		String email = params.get("email");
		String real_name = params.get("real_name");
		String status = params.get("status");
		// String password = params.get("password");

		String sql = "update boss_user set email=?,real_name=?,status=? where id=?";

		List<Object> list = new ArrayList<Object>();
		// list.add(user_name);
		list.add(email);
		list.add(real_name);
		list.add(status);
		// list.add(password);
		list.add(id);
		try {
			dao.update(sql, list.toArray());
			String delSql = "delete from user_auth where user_id=?";
			dao.update(delSql, id);
			String insSql = "insert into user_auth(user_id,auth_id) values(?,?)";
			for (String val : values) {
				List<Object> objList = new ArrayList<Object>();
				objList.add(id);
				objList.add(Long.parseLong(val));
				dao.update(insSql, objList.toArray());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public long addSystemUser(Map<String, String> params, String[] values) {
		long id = 0;
		String user_name = params.get("user_name");
		String email = params.get("email");
		String real_name = params.get("real_name");
		String status = params.get("status");
    String password = params.get("password");
    String passwordmd5 = params.get("passwordmd5");
		String sql = "insert into boss_user (user_name,email,real_name,status,password,create_time,update_passw_time) "
				+ " values " + " (?,?,?,?,?,now(), now()) ";
		List<Object> list = new ArrayList<Object>();
		list.add(user_name);
		list.add(email);
		list.add(real_name);
		list.add(status);
		//list.add(MD5.toMD5(password));
    list.add(passwordmd5);

		try {
			id = dao.updateGetID(sql, list);
			if (values != null && values.length > 0) {
				String insSql = "insert into user_auth(user_id,auth_id) values(?,?)";
				for (String val : values) {
					List<Object> objList = new ArrayList<Object>();
					objList.add(id);
					objList.add(Long.parseLong(val));
					dao.update(insSql, objList.toArray());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	public int resetUserPassword(long id) throws SQLException {
		String password = "eeepay888";
		password = MD5.toMD5(password);
		String sql = "update boss_user set password=? where id=?";
		int num = dao.update(sql, new Object[] { password, id });
		return num;
	}

	public List<BossAuth> getAuthList(Long id) throws SQLException {
		String sql = "select ba.* from (select k.* from (select t.auth_id as group_id  from user_auth t where t.user_id=?) z,group_auth k where z.group_id=k.user_group_id) h,boss_auth ba where h.auth_id=ba.id";
		List<BossAuth> list = dao
				.find(BossAuth.class, sql, new Object[] { id });
		List<BossAuth> lst = new ArrayList<BossAuth>();
		for (BossAuth au : list) {
			if (!lst.contains(au)) {
				lst.add(au);
			}
		}
		return lst;
	}

	public boolean checkExitUserName(Map<String, String> params) {
		Object user_name = params.get("user_name");
		String sql = "select bu.* from boss_user bu where bu.user_name=?";
		List<Map<String, Object>> listMap = dao.find(sql, user_name);
		return (listMap != null && listMap.size() > 0) ? true : false;
	}

	public boolean checkUseStatus(Map<String, String> params) {
		Long id = Long.parseLong(params.get("id"));
		List<BossAuth> bossAuthList = authList(id);
		if (bossAuthList != null && bossAuthList.size() > 0) {
			return true;
		}
		Integer status = 0;
		String sql = "select * from boss_user where id=? and status=?";
		List<Map<String, Object>> list = dao.find(sql, new Object[] { id,
				status });
		return (list != null && list.size() > 0) ? true : false;
	}

	public int updateUserStatus(Map<String, String> params) throws SQLException {
		Long id = Long.parseLong(params.get("id"));
		String sql = "update boss_user set status=? ,fail_times=0 where id=?";
		Integer status = 1;
		int num = dao.update(sql, new Object[] { status, id });
		return num;
	}

	public Long checkPower(long id) {
		String sql = "select a.auth_id from user_auth a join boss_user b on a.user_id=b.id where b.id=?";
		Map<String, Object> m = dao.findFirst(sql, id);
		if (m != null)
			return new Long((Integer) m.get("auth_id"));
		return null;

	}
}
