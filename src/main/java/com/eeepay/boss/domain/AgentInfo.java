package com.eeepay.boss.domain;

import java.util.Date;

/**
 * 代理商信息
 * 
 * @author dj
 * 
 */
public class AgentInfo extends ToString {

	private Long id;
	// 上级代理商
	private Long parentId;
	// 代理商编号
	private String agentNo;
	// 代理商名称
	private String agentName;
	// 代理商联系人
	private String agentLinkName;
	// 代理商联系电话
	private String agentLinkTel;
	// 代理商联系邮箱
	private String agentLinkMail;
	// 代理商区域
	private String agentArea;
	// 代理商地址
	private String agentAddress;
	// 销售名称
	private String saleName;
	// 管理系统LOGO
	private String managerLogo;
	// 客户端LOGO
	private String clientLogo;
	//代理商状态
	private String agentStatus;
	// 创建时间
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentLinkName() {
		return agentLinkName;
	}

	public void setAgentLinkName(String agentLinkName) {
		this.agentLinkName = agentLinkName;
	}

	public String getAgentLinkTel() {
		return agentLinkTel;
	}

	public void setAgentLinkTel(String agentLinkTel) {
		this.agentLinkTel = agentLinkTel;
	}

	public String getAgentLinkMail() {
		return agentLinkMail;
	}

	public void setAgentLinkMail(String agentLinkMail) {
		this.agentLinkMail = agentLinkMail;
	}

	public String getAgentArea() {
		return agentArea;
	}

	public void setAgentArea(String agentArea) {
		this.agentArea = agentArea;
	}

	public String getAgentAddress() {
		return agentAddress;
	}

	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public String getManagerLogo() {
		return managerLogo;
	}

	public void setManagerLogo(String managerLogo) {
		this.managerLogo = managerLogo;
	}

	public String getClientLogo() {
		return clientLogo;
	}

	public void setClientLogo(String clientLogo) {
		this.clientLogo = clientLogo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAgentStatus() {
		return agentStatus;
	}

	public void setAgentStatus(String agentStatus) {
		this.agentStatus = agentStatus;
	}
	
}
