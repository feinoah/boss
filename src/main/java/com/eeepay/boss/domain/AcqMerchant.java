package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.eeepay.boss.enums.FeeType;


public class AcqMerchant {
	
	private Long id;
	
	private String acqEnname;//收单机构英文名称
	
	private String acqMerchantNo;//收单机构商户号
	
	private String acqMerchantName;//收单机构商户号名称
	
	private String merchantNo;//收单机构商户号对应的普通商户号
	
	private String mcc;//行业码
	
	private FeeType feeType;//手续费类型
	
	private BigDecimal feeRate;//手续费扣率
	
	private BigDecimal feeCapAmount;//手续费封顶金额
	
	private BigDecimal feeMaxAmount;//最高手续费金额
	
	private BigDecimal feeSingleAmount;//单笔手续费金额
	
	private String ladderFee;//阶梯费率
	
	private String largeSmallFlag;//大套小标志
	
	private Date createTime;//创建时间

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

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLargeSmallFlag() {
		return largeSmallFlag;
	}

	public void setLargeSmallFlag(String largeSmallFlag) {
		this.largeSmallFlag = largeSmallFlag;
	}
	
	
}
