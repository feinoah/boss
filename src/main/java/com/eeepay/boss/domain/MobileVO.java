/**
 * 
 */
package com.eeepay.boss.domain;



public class MobileVO{
	  private String mts;
	  private String province;//省份
	  private String catName;//类型：联通，移动，电信
	  private String telString;
	public String getMts() {
		return mts;
	}
	public void setMts(String mts) {
		this.mts = mts;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCatName() {
		return catName;
	}
	public void setCatName(String catName) {
		this.catName = catName;
	}
	public String getTelString() {
		return telString;
	}
	public void setTelString(String telString) {
		this.telString = telString;
	}
}