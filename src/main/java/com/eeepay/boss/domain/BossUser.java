package com.eeepay.boss.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户信息表
 * 
 * @author dj
 * 
 */
public class BossUser extends ToString implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	 * 登录名称
	 */
	private String userName;
	/**
	 * 真是名字
	 */
	private String realName;
	/**
	 * 电子邮件
	 */
	private String email;
	/**
	 * 1启用 0停用
	 */
	private String status;
	/**
	 * MD5加密密码
	 */
	private String password;
	/**
	 * 操作员
	 */
	private String createOperator;

	/**
	 * 代理商编号
	 */
	private String agentNo;
	
	private Integer failTimes;
	private Date updatePasswTime;//修改密码时间
	
	public Date getUpdatePasswTime() {
		return updatePasswTime;
	}

	public void setUpdatePasswTime(Date updatePasswTime) {
		this.updatePasswTime = updatePasswTime;
	}
	
//	/**
//	 * 上次登录时间
//	 */
//	private String lastTime;
//	/**
//	 * 创建时间
//	 */
//	private String createTime;
//	
//	public String getLastTime() {
//		return lastTime;
//	}
//
//	public void setLastTime(String lastTime) {
//		this.lastTime = lastTime;
//	}
//
//	public String getCreateTime() {
//		return createTime;
//	}
//
//	public void setCreateTime(String createTime) {
//		this.createTime = createTime;
//	}

	public Integer getFailTimes() {
		return failTimes;
	}

	public void setFailTimes(Integer failTimes) {
		this.failTimes = failTimes;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

}
