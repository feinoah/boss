package com.eeepay.boss.domain;

import java.math.BigDecimal;

import com.eeepay.boss.enums.FeeType;


public class AgentFee {
	
	private Long id;
	
	private String agentNo;//代理商编号
	
	private FeeType feeType;//手续费类型
	
	private BigDecimal feeRate;//手续费扣率
	
	private BigDecimal feeCapAmount;//手续费封顶金额
	
	private BigDecimal feeMaxAmount;//最高手续费金额
	
	private BigDecimal feeSingleAmount;//单笔手续费金额
	
	private String ladderFee;//阶梯费率

	
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

	public FeeType getFeeType() {
		return feeType;
	}

	public void setFeeType(FeeType feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public BigDecimal getFeeCapAmount() {
		return feeCapAmount;
	}

	public void setFeeCapAmount(BigDecimal feeCapAmount) {
		this.feeCapAmount = feeCapAmount;
	}

	public BigDecimal getFeeMaxAmount() {
		return feeMaxAmount;
	}

	public void setFeeMaxAmount(BigDecimal feeMaxAmount) {
		this.feeMaxAmount = feeMaxAmount;
	}

	public BigDecimal getFeeSingleAmount() {
		return feeSingleAmount;
	}

	public void setFeeSingleAmount(BigDecimal feeSingleAmount) {
		this.feeSingleAmount = feeSingleAmount;
	}

	public String getLadderFee() {
		return ladderFee;
	}

	public void setLadderFee(String ladderFee) {
		this.ladderFee = ladderFee;
	}
	
}
