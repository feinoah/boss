package com.eeepay.boss.domain;

public class PosMerchant {

	private Long id;

	private String merchantNo;// 商户号

	private String merchantName;// 商户全称

	private String merchantShortName;// 商户简称

	private String mobileUserName;// 手机登陆用户名

	private String mobilePassword;// 手机登陆密码

	private String openStatus;// 开通状态(0:未开通；1:开通)

	private String agentNo;// 代理商编号

	private String address;// 注册地址

	private String saleAddress;// 经营地址

	private String province;// 省份

	private String city;// 城市

	private String linkName;// 联系人
	
//	private String annexAuditFlag;//附件审核标志(0:未审核；1:已审核；2：审核失败)
	
	private String phone;// 联系电话

	private String mcc;// 行业代码

	private String saleName;// 销售名称

	private int settleCycle;// 结算周期
	
	private String examinationOopinions;//审核意见
	
	private int realflag; //是否实名商户0否，1是                                    
	
	private String idCardNo;//法人身份证号
	
	private Long terminalCount;//商户终端数量
	
	private String attachment;//附件

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantShortName() {
		return merchantShortName;
	}

	public void setMerchantShortName(String merchantShortName) {
		this.merchantShortName = merchantShortName;
	}

	public String getMobileUserName() {
		return mobileUserName;
	}

	public void setMobileUserName(String mobileUserName) {
		this.mobileUserName = mobileUserName;
	}

	public String getMobilePassword() {
		return mobilePassword;
	}

	public void setMobilePassword(String mobilePassword) {
		this.mobilePassword = mobilePassword;
	}

	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}
	 
	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSaleAddress() {
		return saleAddress;
	}

	public void setSaleAddress(String saleAddress) {
		this.saleAddress = saleAddress;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}


	public int getSettleCycle() {
		return settleCycle;
	}

	public void setSettleCycle(int settleCycle) {
		this.settleCycle = settleCycle;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getExaminationOopinions()
	{
		return examinationOopinions;
	}

	public void setExaminationOopinions(String examinationOopinions)
	{
		this.examinationOopinions = examinationOopinions;
	}

	public int getRealflag()
	{
		return realflag;
	}

	public void setRealflag(int realflag)
	{
		this.realflag = realflag;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public Long getTerminalCount() {
		return terminalCount;
	}

	public void setTerminalCount(Long terminalCount) {
		this.terminalCount = terminalCount;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	
}
