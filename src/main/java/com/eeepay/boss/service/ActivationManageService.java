package com.eeepay.boss.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.Dao;

@Service
public class ActivationManageService {


	@Resource
	private Dao dao;
	
	
	//查询激活码信息
	public Page<Map<String, Object>> actQuery(Map<String, String> params,final PageRequest pageRequest) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		
		if (null != params) {
			
			//状态
			String state = params.get("state");
			if (StringUtils.isNotEmpty(state)
					&& !"-1".equals(state)) {
				sb.append(" and state=?");
				list.add(state);
			}
			
			
			// 购买者
			if (StringUtils.isNotEmpty(params.get("batch"))) {
				String batch = params.get("batch");
				sb.append(" and batch=?");
				list.add(batch);
			}
			

			// 使用者
			if (StringUtils.isNotEmpty(params.get("code_user"))) {
				String code_user = params.get("code_user");
				sb.append(" and code_user=?");
				list.add(code_user);
			}
			
			// 激活码
			if (StringUtils.isNotEmpty(params.get("keycode"))) {
				String keycode = params.get("keycode");
				sb.append(" and keycode=?");
				list.add(keycode);
			}
		}
	
		String sql="SELECT * from activation where 1=1 "+sb.toString();
		
		System.out.println(sql+"\n"+list);
	
		return dao.find(sql, list.toArray(), pageRequest);
	}
	
	

	//生成激活码
	public long produceAct(String uuid,String batch) throws SQLException {
		List<Object> list = new ArrayList<Object>();
		
		//获取操作用户名
		BossUser bossUser = (BossUser) SecurityUtils.getSubject().getPrincipal();
	    String operator= bossUser.getRealName();
	    
		String sql="insert into activation (keycode,state,batch,operator,usertime) values (?,1,?,?,now()) ";
		list.add(uuid);
		list.add(batch);
		list.add(operator);
		return dao.update(sql, list.toArray());
	}
	
	
	
	// 根据主键查询激活码详情
	public Map<String, Object> activationQueryById(String keycode) throws SQLException {
		String sql = "select  *  from activation  where keycode=?";
		return dao.findFirst(sql, keycode);
	}
	
	// 更新激活码状态
	public int updateActivationLocked(String state,String operator,String keycode) throws SQLException {
		//获得操作人加上当前系统时间用“;"号隔开
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time1=df.format(date);
		String lastOperator=operator+";"+time1;
		
		String sql = "update activation set state=?,operator=? where keycode=?";
		Object[] params={state,lastOperator,keycode};
		int n=dao.update(sql,params);
		return n;
	}
	
	
	    /**
	     * 判断激活码是否存在
	     * @param state
	     * @param keycode
	     * @return
	     * @throws SQLException
	     */
		public Map<String, Object> findActivation(String keycode) throws SQLException {
			String sql = "select  keycode  from activation  where keycode=?";
			return dao.findFirst(sql, keycode);
		}
}
