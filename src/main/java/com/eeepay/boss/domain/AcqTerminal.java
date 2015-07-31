package com.eeepay.boss.domain;

import java.util.Date;

public class AcqTerminal {

	private Long id;

	private String acqEnname;// 收单机构英文名称

	private String acqMerchantNo;// 收单机构商户号

	private String acqTerminalNo;// 收单机构终端号

	private String batchNo;// 收单机构终端批次号

	private String serialNo;// 收单机构终端流水号

	private String lmkZmk;// 终端LMK下的ZMK

	private String lmkZmkCv;// 终端LMK下ZMK的校验值

	private String lmkZpk;// 终端LMK下的ZPK

	private String lmkZpkCv;// 终端LMK下的ZPk的校验值

	private String lmkZak;// 终端LMK下的ZAK

	private String lmkZakCv;// 终端LMK下的ZAK的校验值

	private String workKey;// 工作密钥

	private Date lastUpdateTime;// 最后更新时间，更新密钥，更新批次号

	private Date lastUsedTime;// 最后使用时间
	
	private int status;//状态
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

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

	public String getAcqTerminalNo() {
		return acqTerminalNo;
	}

	public void setAcqTerminalNo(String acqTerminalNo) {
		this.acqTerminalNo = acqTerminalNo;
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

	public String getLmkZmk() {
		return lmkZmk;
	}

	public void setLmkZmk(String lmkZmk) {
		this.lmkZmk = lmkZmk;
	}

	public String getLmkZmkCv() {
		return lmkZmkCv;
	}

	public void setLmkZmkCv(String lmkZmkCv) {
		this.lmkZmkCv = lmkZmkCv;
	}

	public String getLmkZpk() {
		return lmkZpk;
	}

	public void setLmkZpk(String lmkZpk) {
		this.lmkZpk = lmkZpk;
	}

	public String getLmkZpkCv() {
		return lmkZpkCv;
	}

	public void setLmkZpkCv(String lmkZpkCv) {
		this.lmkZpkCv = lmkZpkCv;
	}

	public String getLmkZak() {
		return lmkZak;
	}

	public void setLmkZak(String lmkZak) {
		this.lmkZak = lmkZak;
	}

	public String getLmkZakCv() {
		return lmkZakCv;
	}

	public void setLmkZakCv(String lmkZakCv) {
		this.lmkZakCv = lmkZakCv;
	}

	public String getWorkKey() {
		return workKey;
	}

	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Date lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

}
