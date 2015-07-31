package com.eeepay.boss.domain;

import java.math.BigDecimal;

public class MobileProduct {

	private Long id;
	private String prodId;
	private int prodContent;
	private BigDecimal prodPrice;
	private String prodIsptype;
	private String prodDelaytimes;
	private String prodProvinceid;
	private String prodType;
	
	public MobileProduct(String prodId, int prodContent, BigDecimal prodPrice,
			String prodIsptype, String prodDelaytimes, String prodProvinceid,
			String prodType) {
		super();
		this.prodId = prodId;
		this.prodContent = prodContent;
		this.prodPrice = prodPrice;
		this.prodIsptype = prodIsptype;
		this.prodDelaytimes = prodDelaytimes;
		this.prodProvinceid = prodProvinceid;
		this.prodType = prodType;
	}
	
	public MobileProduct(Long id, String prodId, int prodContent,
			BigDecimal prodPrice, String prodIsptype, String prodDelaytimes,
			String prodProvinceid, String prodType) {
		super();
		this.id = id;
		this.prodId = prodId;
		this.prodContent = prodContent;
		this.prodPrice = prodPrice;
		this.prodIsptype = prodIsptype;
		this.prodDelaytimes = prodDelaytimes;
		this.prodProvinceid = prodProvinceid;
		this.prodType = prodType;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public int getProdContent() {
		return prodContent;
	}
	public void setProdContent(int prodContent) {
		this.prodContent = prodContent;
	}
	public BigDecimal getProdPrice() {
		return prodPrice;
	}
	public void setProdPrice(BigDecimal prodPrice) {
		this.prodPrice = prodPrice;
	}
	public String getProdIsptype() {
		return prodIsptype;
	}
	public void setProdIsptype(String prodIsptype) {
		this.prodIsptype = prodIsptype;
	}
	public String getProdDelaytimes() {
		return prodDelaytimes;
	}
	public void setProdDelaytimes(String prodDelaytimes) {
		this.prodDelaytimes = prodDelaytimes;
	}
	public String getProdProvinceid() {
		return prodProvinceid;
	}
	public void setProdProvinceid(String prodProvinceid) {
		this.prodProvinceid = prodProvinceid;
	}
	public String getProdType() {
		return prodType;
	}
	public void setProdType(String prodType) {
		this.prodType = prodType;
	}
}
