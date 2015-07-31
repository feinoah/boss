package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

public class SettleBatch {
	
	private Long id;
	
	private String settleBatchNo;//结算批次号
	
	private String checkBatchNo;//对账批次
	
	private BigDecimal totalTransAmount;//交易总金额
	
	private BigDecimal totalMerchantFee;// 手续费总金额
	
	private BigDecimal totalAcqMerchantFee;// 收单商户手续费总金额
	
	private BigDecimal totalSettleAmount;//结算总金额
	
	private BigDecimal totalAcqSettleAmount;//收单结算总金额
	
	private Long totalItems;//结算总笔数
	
	private Date settleTime;//结算文件生成时间
	
	private String settleFileName;//结算文件名称
	
	private String operator;//操作员
	
	private Date fileCreateTime;//文件生成时间
	
	private String fileCreateOperator;//文件创建操作员
	
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

	public BigDecimal getTotalTransAmount() {
		return totalTransAmount;
	}

	public void setTotalTransAmount(BigDecimal totalTransAmount) {
		this.totalTransAmount = totalTransAmount;
	}

	public BigDecimal getTotalMerchantFee() {
		return totalMerchantFee;
	}

	public void setTotalMerchantFee(BigDecimal totalMerchantFee) {
		this.totalMerchantFee = totalMerchantFee;
	}

	public BigDecimal getTotalSettleAmount() {
		return totalSettleAmount;
	}

	public void setTotalSettleAmount(BigDecimal totalSettleAmount) {
		this.totalSettleAmount = totalSettleAmount;
	}

	public Long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}

	public Date getSettleTime() {
		return settleTime;
	}

	public void setSettleTime(Date settleTime) {
		this.settleTime = settleTime;
	}

	public String getSettleFileName() {
		return settleFileName;
	}

	public void setSettleFileName(String settleFileName) {
		this.settleFileName = settleFileName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCheckBatchNo() {
		return checkBatchNo;
	}

	public void setCheckBatchNo(String checkBatchNo) {
		this.checkBatchNo = checkBatchNo;
	}

	public BigDecimal getTotalAcqMerchantFee() {
		return totalAcqMerchantFee;
	}

	public void setTotalAcqMerchantFee(BigDecimal totalAcqMerchantFee) {
		this.totalAcqMerchantFee = totalAcqMerchantFee;
	}

	public BigDecimal getTotalAcqSettleAmount() {
		return totalAcqSettleAmount;
	}

	public void setTotalAcqSettleAmount(BigDecimal totalAcqSettleAmount) {
		this.totalAcqSettleAmount = totalAcqSettleAmount;
	}

	public Date getFileCreateTime() {
		return fileCreateTime;
	}

	public void setFileCreateTime(Date fileCreateTime) {
		this.fileCreateTime = fileCreateTime;
	}

	public String getFileCreateOperator() {
		return fileCreateOperator;
	}

	public void setFileCreateOperator(String fileCreateOperator) {
		this.fileCreateOperator = fileCreateOperator;
	}
	
}
