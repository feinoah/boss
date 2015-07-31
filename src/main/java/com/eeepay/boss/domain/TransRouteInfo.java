package com.eeepay.boss.domain;

import java.util.Date;

public class TransRouteInfo {
	
	private Long id;
	
	private String merchantNo;//商户号
	
	private String terminalNo;//终端号
	
	private String acqMerchantNo;//收单机构商户号
	
	private String acqTerminalNo;//收单机构终端号
	
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

	public String getAcqMerchantNo() {
		return acqMerchantNo;
	}

	public void setAcqMerchantNo(String acqMerchantNo) {
		this.acqMerchantNo = acqMerchantNo;
	}

	public void setAcqTerminalNo(String acqTerminalNo) {
		this.acqTerminalNo = acqTerminalNo;
	}

	public String getAcqTerminalNo() {
		return acqTerminalNo;
	}

	public void setAcqterminalNo(String acqTerminalNo) {
		this.acqTerminalNo = acqTerminalNo;
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
