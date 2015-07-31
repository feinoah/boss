package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

public class SettleDetail {
	
	private Long id;
	
	private String settleBatchNo;//结算批次号
	
	private String agentNo;//代理商编号
	
	private String merchantName;//商户名称
	
	private String merchantNo;//商户号
	
	private String acqMerchantNo;//收单机构商户号
	
	private String acqTerminalNo;//收单机构终端号
	
	private String merchantRate;//商户扣率
	
	private String acqMerchantRate;//收单商户扣率
	
	private BigDecimal transAmount;//交易金额
	
	private Long transItems;//交易笔数
	
	private BigDecimal merchantFee;//商户手续费
	
	private BigDecimal acqMerchantFee;//收单商户手续费
	
	private Date merchantSettleDate;//商户结算日期
	
	private BigDecimal settleAmount;//结算金额
	
	private BigDecimal acqSettleAmount;//收单机构商户结算金额
	
	private String accountNo;//结算账号
	
	private String accountName;//开户人
	
	private String accountType;//账户类型
	
	private String bankName;//开户行名称
	
	private String cnapsNo;//联行号
	
	private Date createTime;//创建时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSettleBatchNo() {
		return settleBatchNo;
	}

	public void setSettleBatchNo(String settleBatchNo) {
		this.settleBatchNo = settleBatchNo;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public BigDecimal getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(BigDecimal transAmount) {
		this.transAmount = transAmount;
	}

	public BigDecimal getMerchantFee() {
		return merchantFee;
	}

	public void setMerchantFee(BigDecimal merchantFee) {
		this.merchantFee = merchantFee;
	}

	public String getMerchantRate() {
		return merchantRate;
	}

	public void setMerchantRate(String merchantRate) {
		this.merchantRate = merchantRate;
	}

	public BigDecimal getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
	}

	public Long getTransItems() {
		return transItems;
	}

	public void setTransItems(Long transItems) {
		this.transItems = transItems;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCnapsNo() {
		return cnapsNo;
	}

	public void setCnapsNo(String cnapsNo) {
		this.cnapsNo = cnapsNo;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Date getMerchantSettleDate() {
		return merchantSettleDate;
	}

	public void setMerchantSettleDate(Date merchantSettleDate) {
		this.merchantSettleDate = merchantSettleDate;
	}

	public BigDecimal getAcqMerchantFee() {
		return acqMerchantFee;
	}

	public void setAcqMerchantFee(BigDecimal acqMerchantFee) {
		this.acqMerchantFee = acqMerchantFee;
	}

	public String getAcqMerchantNo() {
		return acqMerchantNo;
	}

	public void setAcqMerchantNo(String acqMerchantNo) {
		this.acqMerchantNo = acqMerchantNo;
	}

	public String getAcqTerminalNo() {
		return acqTerminalNo;
	}

	public void setAcqTerminalNo(String acqTerminalNo) {
		this.acqTerminalNo = acqTerminalNo;
	}

	public String getAcqMerchantRate() {
		return acqMerchantRate;
	}

	public void setAcqMerchantRate(String acqMerchantRate) {
		this.acqMerchantRate = acqMerchantRate;
	}

	public BigDecimal getAcqSettleAmount() {
		return acqSettleAmount;
	}

	public void setAcqSettleAmount(BigDecimal acqSettleAmount) {
		this.acqSettleAmount = acqSettleAmount;
	}
	
}
