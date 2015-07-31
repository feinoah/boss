package com.eeepay.boss.domain;

import java.sql.Timestamp;

/**
 * 手机请求日志
 * 
 * @author dj
 * 
 */
public class MobileRequestLog {

	private Long id;
	// psam卡号
	private String psamNo;
	// 交易码
	private String tradeId;
	// 0:android 1:iOS
	private Integer platform;
	// 手机操作系统版本号
	private String clientVersion;
	// 手机号
	private String loginMobile;
	// 报文内容
	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPsamNo() {
		return psamNo;
	}

	public void setPsamNo(String psamNo) {
		this.psamNo = psamNo;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getLoginMobile() {
		return loginMobile;
	}

	public void setLoginMobile(String loginMobile) {
		this.loginMobile = loginMobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	private Timestamp createTime;

}
