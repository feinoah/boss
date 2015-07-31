package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

public class PosMerchantTransRule {
	
	private Long id;
	
	private String merchantNo;//商户号
	
	private String terminalNo;//终端号
	
	private BigDecimal singleMaxAmount;//单笔最大金额
	
	private BigDecimal edMaxAmount;//每天最高金额
	
	private BigDecimal edTotalAmount;//每天累计交易金额
	
	private Date lastUpdateTime;//最后一次更新时间
	
	private Date createTime;//创建时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public BigDecimal getSingleMaxAmount() {
		return singleMaxAmount;
	}

	public void setSingleMaxAmount(BigDecimal singleMaxAmount) {
		this.singleMaxAmount = singleMaxAmount;
	}

	public BigDecimal getEdMaxAmount() {
		return edMaxAmount;
	}

	public void setEdMaxAmount(BigDecimal edMaxAmount) {
		this.edMaxAmount = edMaxAmount;
	}

	public BigDecimal getEdTotalAmount() {
		return edTotalAmount;
	}

	public void setEdTotalAmount(BigDecimal edTotalAmount) {
		this.edTotalAmount = edTotalAmount;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
