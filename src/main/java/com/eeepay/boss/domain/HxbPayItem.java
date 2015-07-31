package com.eeepay.boss.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *  描述：
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */
@XmlAccessorType(value = XmlAccessType.PROPERTY)
public class HxbPayItem {
	  private String seqNo;
	  private String amount;
	  private String inBankNo;
	  private String inAccNo;
	  private String inAccName;
	  private String mobileNo;
	  private String settleDays;
	  
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getInBankNo() {
		return inBankNo;
	}
	public void setInBankNo(String inBankNo) {
		this.inBankNo = inBankNo;
	}
	public String getInAccNo() {
		return inAccNo;
	}
	public void setInAccNo(String inAccNo) {
		this.inAccNo = inAccNo;
	}
	public String getInAccName() {
		return inAccName;
	}
	public void setInAccName(String inAccName) {
		this.inAccName = inAccName;
	}
  /**
   * @return the mobileNo
   */
  public String getMobileNo() {
    return mobileNo;
  }
  /**
   * @param mobileNo the mobileNo to set
   */
  public void setMobileNo(String mobileNo) {
    this.mobileNo = mobileNo;
  }
  /**
   * @return the settleDays
   */
  public String getSettleDays() {
    return settleDays;
  }
  /**
   * @param settleDays the settleDays to set
   */
  public void setSettleDays(String settleDays) {
    this.settleDays = settleDays;
  }
	  
	  
	
	
}
