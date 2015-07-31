package com.eeepay.boss.domain;

public class AgentShareRule {
	
	private Long id;
	
	private String agentNo;
	
	private String sharingRule;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getSharingRule() {
		return sharingRule;
	}

	public void setSharingRule(String sharingRule) {
		this.sharingRule = sharingRule;
	}

}
