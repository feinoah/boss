package com.eeepay.boss.domain;

import java.util.Date;


public class AcqOrg {
	
	private Long id;
	
	private String acqCnname;//收单机构中文名称
	
	private String acqEnname;//收单机构英文名称
	
	private String acqCode;//收单机构识别码
	
	private String host;//主机名
	
	private String port;//端口号
	
	private String lmkOmk;//LMK下的机构主密钥
	
	private String lmkOmkCv;//LMK下的机构主密钥校验值
	
	private String lmkOpk;//LMK下的PIN密钥
	
	private String lmkOpkCv;//LMK下的PIN密钥校验值
	
	private String lmkOak;//LMK下的MAC密钥
	
	private String lmkOakCv;//LMK下的MAC密钥校验值
	
	private String workKey;//原始工作密钥
	
	private String testName;//测试联系人
	
	private String testMobile;//测试联系人电话
	
	private String testQq;//测试人QQ
	
	private Date lastUpdateTime;//最后一次更新时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcqCnname() {
		return acqCnname;
	}

	public void setAcqCnname(String acqCnname) {
		this.acqCnname = acqCnname;
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLmkOmk() {
		return lmkOmk;
	}

	public void setLmkOmk(String lmkOmk) {
		this.lmkOmk = lmkOmk;
	}

	public String getLmkOmkCv() {
		return lmkOmkCv;
	}

	public void setLmkOmkCv(String lmkOmkCv) {
		this.lmkOmkCv = lmkOmkCv;
	}

	public String getLmkOpk() {
		return lmkOpk;
	}

	public void setLmkOpk(String lmkOpk) {
		this.lmkOpk = lmkOpk;
	}

	public String getLmkOpkCv() {
		return lmkOpkCv;
	}

	public void setLmkOpkCv(String lmkOpkCv) {
		this.lmkOpkCv = lmkOpkCv;
	}

	public String getLmkOak() {
		return lmkOak;
	}

	public void setLmkOak(String lmkOak) {
		this.lmkOak = lmkOak;
	}

	public String getLmkOakCv() {
		return lmkOakCv;
	}

	public void setLmkOakCv(String lmkOakCv) {
		this.lmkOakCv = lmkOakCv;
	}

	public String getWorkKey() {
		return workKey;
	}

	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestMobile() {
		return testMobile;
	}

	public void setTestMobile(String testMobile) {
		this.testMobile = testMobile;
	}

	public String getTestQq() {
		return testQq;
	}

	public void setTestQq(String testQq) {
		this.testQq = testQq;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	

}
