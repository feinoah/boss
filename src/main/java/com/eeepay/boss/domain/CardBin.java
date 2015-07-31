package com.eeepay.boss.domain;

import java.sql.Timestamp;

public class CardBin extends ToString {
	private String cardNo;
	private Long id;
	private String bankName;
	private String cardName;
	private Integer cardLength;
	private String cardType;
	private Integer verifyLength;
	private Integer verifyCode;
	private Timestamp createTime;
	private String bankNo;
	private String bankDesc;
	

  public String getCardNo() {
		return cardNo;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Long getId() {

		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Integer getCardLength() {
		return cardLength;
	}

	public void setCardLength(Integer cardLength) {
		this.cardLength = cardLength;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Integer getVerifyLength() {
		return verifyLength;
	}

	public void setVerifyLength(Integer verifyLength) {
		this.verifyLength = verifyLength;
	}

	public Integer getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(Integer verifyCode) {
		this.verifyCode = verifyCode;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
  public String getBankNo() {
    return bankNo;
  }
  public void setBankNo(String bankNo) {
    this.bankNo = bankNo;
  }
  public String getBankDesc() {
    return bankDesc;
  }
  public void setBankDesc(String bankDesc) {
    this.bankDesc = bankDesc;
  }

}
