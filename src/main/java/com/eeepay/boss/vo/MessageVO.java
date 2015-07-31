/**
 * 
 */
package com.eeepay.boss.vo;

import com.eeepay.boss.domain.ToString;


/**
 * hdb
 * 2013-8-9 上午9:58:34 
 */
public class MessageVO extends ToString{
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
//		if("中国联通".equals(catName)){
//			return "联通";
//		}else if("中国移动".equals(catName)){
//			return "移动";
//		}else if("中国电信".equals(catName)){
//			return "电信";
//		}
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