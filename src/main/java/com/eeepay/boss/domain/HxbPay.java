package com.eeepay.boss.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *  描述：
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */
@XmlRootElement(name = "package")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class HxbPay {
	private String submitChannel;
	private String userID;
	private String userName;
	private String transferType;
	private String summary;
	private String totalAmount;
	
	@XmlElementWrapper(name = "details", required = true)
	private List<HxbPayItem> detail;

	public String getSubmitChannel() {
		return submitChannel;
	}

	public void setSubmitChannel(String submitChannel) {
		this.submitChannel = submitChannel;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTransferType() {
		return transferType;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<HxbPayItem> getDetail() {
		return detail;
	}

	public void setDetail(List<HxbPayItem> detail) {
		this.detail = detail;
	}

  /**
   * @return the totalAmount
   */
  public String getTotalAmount() {
    return totalAmount;
  }

  /**
   * @param totalAmount the totalAmount to set
   */
  public void setTotalAmount(String totalAmount) {
    this.totalAmount = totalAmount;
  }
	
	
	
}
