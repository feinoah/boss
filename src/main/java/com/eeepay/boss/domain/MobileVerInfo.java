package com.eeepay.boss.domain;

import java.util.Date;

/**
 * 手机客户端版本信息
 * 
 * @author dj
 * 
 */
public class MobileVerInfo extends ToString {

	private Long id;
	private String version;
	// 0:android 1:iOS
	private Integer platform;
	private String appUrl;
	// 0：不需要,1:需要更新,2：需要强制下载
	private Integer downFlag;
	private String verDesc;
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public Integer getDownFlag() {
		return downFlag;
	}

	public void setDownFlag(Integer downFlag) {
		this.downFlag = downFlag;
	}

	public String getVerDesc() {
		return verDesc;
	}

	public void setVerDesc(String verDesc) {
		this.verDesc = verDesc;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
