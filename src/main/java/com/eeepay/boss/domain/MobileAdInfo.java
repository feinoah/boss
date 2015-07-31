package com.eeepay.boss.domain;

/**
 * 手机客户端显示的广告信息
 * 
 * @author dj
 * 
 */
public class MobileAdInfo extends ToString {

	private Long id;
	private String picUrl;
	private String adTitle;
	private String adContent;
	private String linkUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getAdTitle() {
		return adTitle;
	}

	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public String getAdContent() {
		return adContent;
	}

	public void setAdContent(String adContent) {
		this.adContent = adContent;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

}
