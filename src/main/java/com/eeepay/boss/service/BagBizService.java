package com.eeepay.boss.service;

import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.utils.pub.Sms;

@Service
public class BagBizService {
	@Resource
	private Dao dao;
	
	public Map<String, Object> getLastAccessToken() {
		String sql = "select access_token,last_time from access_token order by id desc";
		return dao.findFirst(sql);
	}

	public boolean exists(String mobile) {
		String sql = "select * from pos_merchant where mobile_username=?";
		if (null == dao.findFirst(sql, mobile)) {
			return false;
		}
		return true; 
	}
	

	public boolean reg(String mobile,String merchantname) throws SQLException {
		String merchantno = System.currentTimeMillis()+"";
		String agentno = "1001";
		int i=(int)(Math.random()*1000000);
		String pwd = MD5.toMD5(i+"");
		Sms.sendMsg(mobile, "恭喜您注册成功，您的初始密码为："+i+",我们为您的账户钱包免费赠送1元钱，请及时体验二维码支付");
		String sql ="insert into pos_merchant(merchant_no,merchant_name,merchant_short_name,mobile_username,mobile_password,open_status,agent_no,bag,address) values(?,?,?,?,?,?,?,?,?)";
		dao.update(sql,new Object[]{merchantno,merchantname,merchantname,mobile,pwd,1,agentno,1,"广东省深圳市"});
		return true;
	}
}
