package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.eeepay.boss.enums.CardType;
import com.eeepay.boss.enums.CurrencyType;
import com.eeepay.boss.enums.TransSource;
import com.eeepay.boss.enums.TransStatus;
import com.eeepay.boss.enums.TransType;



public class TransInfo extends ToString{
	
	private Long id;
	
	private String acqEnname;//收单机构英文名称
	
	private String acqCode;//收单机构代码
	
	private String acqMerchantNo;//收单机构商户号
	
	private String acqTerminalNo;//收单机构终端号
	
	private String acqAuthNo;//收单机构授权码
	
	private String acqReferenceNo;//收单机构系统参考号
	
	private String acqBatchNo;//收单机构批次号
	
	private String acqSerialNo;//收单机构流水号
	
	private String acqResponseCode;//收单机构返回的处理码
	
	private String agentNo;//代理商编号
	
	private String merchantNo;//商户号
	
	private String terminalNo;//终端号
	
	private String batchNo;//批次号
	
	private String serialNo;//流水号
	
	private String accountNo;//账号
	
	private CardType cardType;//卡类型
	
	private CurrencyType currencyType;//币种
	
	private BigDecimal transAmount;//交易金额
	
	private BigDecimal merchantFee;//商户手续费
	
	private String merchantRate;//商户扣率
	
	private BigDecimal acqMerchantFee;//收单机构商户手续费
	
	private String acqMerchantRate;//收单商户扣率
	
	private TransType transType;//交易类型
	
	private TransStatus transStatus;//交易状态
	
	private TransSource transSource;//交易来源
	
	private String oriAcqBatchNo;//收单机构原交易批次号
	
	private String oriAcqSerialNo;//收单机构原交易流水号
	
	private String oriBatchNo;//终端原交易批次号
	
	private String oriSerialNo;//终端原交易流水号
	
	private Date acqSettleDate;//收单机构清算日期
	
	private Date merchantSettleDate;//商户清算日期
	
	private Date transTime;//交易时间
	
	private Date lastUpdateTime;//最后更新时间
	
	private Date createTime;//创建时间

	private String reviewStatus; //审核状态用于小宝的交易审核   1正常，空字符串、null、2待审核或者 ，3审核失败 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcqEnname() {
		return acqEnname;
	}

	public void setAcqEnname(String acqEnname) {
		this.acqEnname = acqEnname;
	}

	public String getAcqCode() {
		return acqCode;
	}

	public void setAcqCode(String acqCode) {
		this.acqCode = acqCode;
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

	public String getAcqAuthNo() {
		return acqAuthNo;
	}

	public void setAcqAuthNo(String acqAuthNo) {
		this.acqAuthNo = acqAuthNo;
	}

	public String getAcqBatchNo() {
		return acqBatchNo;
	}

	public void setAcqBatchNo(String acqBatchNo) {
		this.acqBatchNo = acqBatchNo;
	}

	public String getAcqSerialNo() {
		return acqSerialNo;
	}

	public void setAcqSerialNo(String acqSerialNo) {
		this.acqSerialNo = acqSerialNo;
	}
	
	
	public String getAcqResponseCode() {
		return acqResponseCode;
	}

	public void setAcqResponseCode(String acqResponseCode) {
		this.acqResponseCode = acqResponseCode;
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

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
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

	public BigDecimal getAcqMerchantFee() {
		return acqMerchantFee;
	}

	public void setAcqMerchantFee(BigDecimal acqMerchantFee) {
		this.acqMerchantFee = acqMerchantFee;
	}

	public TransType getTransType() {
		return transType;
	}

	public void setTransType(TransType transType) {
		this.transType = transType;
	}

	public TransStatus getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(TransStatus transStatus) {
		this.transStatus = transStatus;
	}

	public TransSource getTransSource() {
		return transSource;
	}

	public void setTransSource(TransSource transSource) {
		this.transSource = transSource;
	}

	public String getOriAcqBatchNo() {
		return oriAcqBatchNo;
	}

	public void setOriAcqBatchNo(String oriAcqBatchNo) {
		this.oriAcqBatchNo = oriAcqBatchNo;
	}

	public String getOriAcqSerialNo() {
		return oriAcqSerialNo;
	}

	public void setOriAcqSerialNo(String oriAcqSerialNo) {
		this.oriAcqSerialNo = oriAcqSerialNo;
	}

	public String getOriBatchNo() {
		return oriBatchNo;
	}

	public void setOriBatchNo(String oriBatchNo) {
		this.oriBatchNo = oriBatchNo;
	}

	public String getOriSerialNo() {
		return oriSerialNo;
	}

	public void setOriSerialNo(String oriSerialNo) {
		this.oriSerialNo = oriSerialNo;
	}

	public Date getTransTime() {
		return transTime;
	}

	public void setTransTime(Date transTime) {
		this.transTime = transTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getAcqReferenceNo() {
		return acqReferenceNo;
	}

	public void setAcqReferenceNo(String acqReferenceNo) {
		this.acqReferenceNo = acqReferenceNo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getAcqSettleDate() {
		return acqSettleDate;
	}

	public void setAcqSettleDate(Date acqSettleDate) {
		this.acqSettleDate = acqSettleDate;
	}

	public Date getMerchantSettleDate() {
		return merchantSettleDate;
	}

	public void setMerchantSettleDate(Date merchantSettleDate) {
		this.merchantSettleDate = merchantSettleDate;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getMerchantRate() {
		return merchantRate;
	}

	public void setMerchantRate(String merchantRate) {
		this.merchantRate = merchantRate;
	}

	public String getAcqMerchantRate() {
		return acqMerchantRate;
	}

	public void setAcqMerchantRate(String acqMerchantRate) {
		this.acqMerchantRate = acqMerchantRate;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
}
