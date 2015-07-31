package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

public class CheckAccountDetail {
	
	private Long id;
	
	private String acqTransSerialNo;//收单机构交易流水号
	
	private String acqMerchantNo;//收单机构商户号
	
	private String acqMerchantName;//收单机构商户名称
	
	private String acqTerminalNo;//收单机构终端号
	
	private String accessOrgNo;//接入机构编号
	
	private String accessOrgName;//接入机构名称
	
	private String acqBatchNo;//收单机构批次号
	
	private String acqSerialNo;//收单机构流水号
	
	private String acqAccountNo;//收单机构卡号
	
	private String acqCardSequenceNo;//收单机构卡序列号
	
	private Date acqTransTime;//收单机构交易时间
	
	private String acqReferenceNo;//收单机构系统参考号
	
	private Date acqSettleDate;//收单机构入账日期
	
	private String acqTransCode;//收单机构交易吗
	
	private String acqTransStatus;//收单机构交易状态
	
	private BigDecimal acqTransAmount;//收单机构交易金额
	
	private BigDecimal acqRefundAmount;//收单机构退货金额
	
	private Date acqCheckDate;//收单机构对账日期
	
	private String acqOriTransSerialNo;//收单机构原交易流水号
	
	private String acqEnname;//收单机构英文名称
	
	private String plateAcqMerchantNo;//平台收单机构商户号
	
	private String plateAcqTerminalNo;//平台收单机构终端号
	
	private String plateMerchantNo;//平台商户号
	
	private String plateTerminalNo;//平台终端号
	
	private String plateAcqBatchNo;//平台收单机构批次号
	
	private String plateAcqSerialNo;//平台收单机构流水号
	
	private String plateBatchNo;//平台批次号
	
	private String plateSerialNo;//平台流水号
	
	private String plateAccountNo;//平台交易账号
	
	private BigDecimal plateTransAmount;//平台交易金额
	
	private BigDecimal plateAcqMerchantFee;//平台收单机构商户手续费
	
	private BigDecimal plateMerchantFee;//平台商户手续费
	
	private String plateAcqMerchantRate;//收单机构商户扣率
	
	private String plateMerchantRate;//商户扣率
	
	private String plateAcqReferenceNo;//平台收单机构系统参考号
	
	private Date plateMerchantSettleDate;//平台商户结算日期
	
	private Date plateAcqTransTime;//平台收单机构交易时间
	
	private String plateTransType;//平台交易类型
	
	private String plateTransStatus;//平台交易状态
	
	private String checkAccountStatus;//对账状态
	
	private String checkBatchNo;//对账批次号
	
	private String description;//描述
	
	private Date createTime;//创建时间
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcqTransSerialNo() {
		return acqTransSerialNo;
	}

	public void setAcqTransSerialNo(String acqTransSerialNo) {
		this.acqTransSerialNo = acqTransSerialNo;
	}

	public String getAcqMerchantNo() {
		return acqMerchantNo;
	}

	public void setAcqMerchantNo(String acqMerchantNo) {
		this.acqMerchantNo = acqMerchantNo;
	}

	public String getAcqMerchantName() {
		return acqMerchantName;
	}

	public void setAcqMerchantName(String acqMerchantName) {
		this.acqMerchantName = acqMerchantName;
	}

	public String getAcqTerminalNo() {
		return acqTerminalNo;
	}

	public void setAcqTerminalNo(String acqTerminalNo) {
		this.acqTerminalNo = acqTerminalNo;
	}

	public String getAccessOrgNo() {
		return accessOrgNo;
	}

	public void setAccessOrgNo(String accessOrgNo) {
		this.accessOrgNo = accessOrgNo;
	}

	public String getAccessOrgName() {
		return accessOrgName;
	}

	public void setAccessOrgName(String accessOrgName) {
		this.accessOrgName = accessOrgName;
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

	public String getAcqAccountNo() {
		return acqAccountNo;
	}

	public void setAcqAccountNo(String acqAccountNo) {
		this.acqAccountNo = acqAccountNo;
	}

	public String getAcqCardSequenceNo() {
		return acqCardSequenceNo;
	}

	public void setAcqCardSequenceNo(String acqCardSequenceNo) {
		this.acqCardSequenceNo = acqCardSequenceNo;
	}

	public String getAcqReferenceNo() {
		return acqReferenceNo;
	}

	public void setAcqReferenceNo(String acqReferenceNo) {
		this.acqReferenceNo = acqReferenceNo;
	}

	public String getAcqTransCode() {
		return acqTransCode;
	}

	public void setAcqTransCode(String acqTransCode) {
		this.acqTransCode = acqTransCode;
	}

	public String getAcqTransStatus() {
		return acqTransStatus;
	}

	public void setAcqTransStatus(String acqTransStatus) {
		this.acqTransStatus = acqTransStatus;
	}


	public String getAcqOriTransSerialNo() {
		return acqOriTransSerialNo;
	}

	public void setAcqOriTransSerialNo(String acqOriTransSerialNo) {
		this.acqOriTransSerialNo = acqOriTransSerialNo;
	}

	public Date getAcqTransTime() {
		return acqTransTime;
	}

	public void setAcqTransTime(Date acqTransTime) {
		this.acqTransTime = acqTransTime;
	}

	public Date getAcqSettleDate() {
		return acqSettleDate;
	}

	public void setAcqSettleDate(Date acqSettleDate) {
		this.acqSettleDate = acqSettleDate;
	}

	public BigDecimal getAcqTransAmount() {
		return acqTransAmount;
	}

	public void setAcqTransAmount(BigDecimal acqTransAmount) {
		this.acqTransAmount = acqTransAmount;
	}

	public BigDecimal getAcqRefundAmount() {
		return acqRefundAmount;
	}

	public void setAcqRefundAmount(BigDecimal acqRefundAmount) {
		this.acqRefundAmount = acqRefundAmount;
	}

	public Date getAcqCheckDate() {
		return acqCheckDate;
	}

	public void setAcqCheckDate(Date acqCheckDate) {
		this.acqCheckDate = acqCheckDate;
	}

	public String getAcqEnname() {
		return acqEnname;
	}

	public void setAcqEnname(String acqEnname) {
		this.acqEnname = acqEnname;
	}

	public String getPlateAcqMerchantNo() {
		return plateAcqMerchantNo;
	}

	public void setPlateAcqMerchantNo(String plateAcqMerchantNo) {
		this.plateAcqMerchantNo = plateAcqMerchantNo;
	}

	public String getPlateAcqTerminalNo() {
		return plateAcqTerminalNo;
	}

	public void setPlateAcqTerminalNo(String plateAcqTerminalNo) {
		this.plateAcqTerminalNo = plateAcqTerminalNo;
	}

	public String getPlateMerchantNo() {
		return plateMerchantNo;
	}

	public void setPlateMerchantNo(String plateMerchantNo) {
		this.plateMerchantNo = plateMerchantNo;
	}

	public String getPlateTerminalNo() {
		return plateTerminalNo;
	}

	public void setPlateTerminalNo(String plateTerminalNo) {
		this.plateTerminalNo = plateTerminalNo;
	}

	public String getPlateAcqBatchNo() {
		return plateAcqBatchNo;
	}

	public void setPlateAcqBatchNo(String plateAcqBatchNo) {
		this.plateAcqBatchNo = plateAcqBatchNo;
	}

	public String getPlateAcqSerialNo() {
		return plateAcqSerialNo;
	}

	public void setPlateAcqSerialNo(String plateAcqSerialNo) {
		this.plateAcqSerialNo = plateAcqSerialNo;
	}

	public String getPlateBatchNo() {
		return plateBatchNo;
	}

	public void setPlateBatchNo(String plateBatchNo) {
		this.plateBatchNo = plateBatchNo;
	}

	public String getPlateSerialNo() {
		return plateSerialNo;
	}

	public void setPlateSerialNo(String plateSerialNo) {
		this.plateSerialNo = plateSerialNo;
	}

	public String getPlateAccountNo() {
		return plateAccountNo;
	}

	public void setPlateAccountNo(String plateAccountNo) {
		this.plateAccountNo = plateAccountNo;
	}

	public BigDecimal getPlateTransAmount() {
		return plateTransAmount;
	}

	public void setPlateTransAmount(BigDecimal plateTransAmount) {
		this.plateTransAmount = plateTransAmount;
	}

	public BigDecimal getPlateAcqMerchantFee() {
		return plateAcqMerchantFee;
	}

	public void setPlateAcqMerchantFee(BigDecimal plateAcqMerchantFee) {
		this.plateAcqMerchantFee = plateAcqMerchantFee;
	}

	public BigDecimal getPlateMerchantFee() {
		return plateMerchantFee;
	}

	public void setPlateMerchantFee(BigDecimal plateMerchantFee) {
		this.plateMerchantFee = plateMerchantFee;
	}

	public String getPlateAcqMerchantRate() {
		return plateAcqMerchantRate;
	}

	public void setPlateAcqMerchantRate(String plateAcqMerchantRate) {
		this.plateAcqMerchantRate = plateAcqMerchantRate;
	}

	public String getPlateMerchantRate() {
		return plateMerchantRate;
	}

	public void setPlateMerchantRate(String plateMerchantRate) {
		this.plateMerchantRate = plateMerchantRate;
	}

	public String getPlateAcqReferenceNo() {
		return plateAcqReferenceNo;
	}

	public void setPlateAcqReferenceNo(String plateAcqReferenceNo) {
		this.plateAcqReferenceNo = plateAcqReferenceNo;
	}

	public Date getPlateAcqTransTime() {
		return plateAcqTransTime;
	}

	public void setPlateAcqTransTime(Date plateAcqTransTime) {
		this.plateAcqTransTime = plateAcqTransTime;
	}

	public String getPlateTransType() {
		return plateTransType;
	}

	public void setPlateTransType(String plateTransType) {
		this.plateTransType = plateTransType;
	}

	public String getPlateTransStatus() {
		return plateTransStatus;
	}

	public void setPlateTransStatus(String plateTransStatus) {
		this.plateTransStatus = plateTransStatus;
	}

	public String getCheckAccountStatus() {
		return checkAccountStatus;
	}

	public void setCheckAccountStatus(String checkAccountStatus) {
		this.checkAccountStatus = checkAccountStatus;
	}

	public String getCheckBatchNo() {
		return checkBatchNo;
	}

	public void setCheckBatchNo(String checkBatchNo) {
		this.checkBatchNo = checkBatchNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPlateMerchantSettleDate() {
		return plateMerchantSettleDate;
	}

	public void setPlateMerchantSettleDate(Date plateMerchantSettleDate) {
		this.plateMerchantSettleDate = plateMerchantSettleDate;
	}
	
}
