package com.eeepay.boss.domain;

import java.math.BigDecimal;
import java.util.Date;

public class CheckAccountBatch {
	
	private Long id;
	
	private String checkBatchNo;//对账批次号
	
	private String acqEnname;//收单机构英文名称
	
	private String acqCnname;//收单机构名称
	
	private BigDecimal acqTotalAmount;//收单机构交易总金额
	
	private Long acqTotalItems;//收单机构对账文件总笔数
	
	private Long acqTotalSuccessItems;//收单机构对账文件成功笔数
	
	private Long acqTotalFailedItems;//收单机构对账文件失败笔数
	
	private BigDecimal totalAmount;//平台交易总金额
	
	private Long totalItems;//平台交易总笔数
	
	private Long totalSuccessItems;//平台对账成功总笔数
	
	private Long totalFailedItems;//平台对账失败总笔数
	
	private String checkResult;//对账结果
	
	private Date checkFileDate;//对账 文件日期
	
	private Date checkTime;//对账时间
	
	private String checkFileName;//对账文件名称
	
	private String operator;//操作员
	
	private Date createTime;//创建时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCheckBatchNo() {
		return checkBatchNo;
	}

	public void setCheckBatchNo(String checkBatchNo) {
		this.checkBatchNo = checkBatchNo;
	}


	public BigDecimal getAcqTotalAmount() {
		return acqTotalAmount;
	}

	public void setAcqTotalAmount(BigDecimal acqTotalAmount) {
		this.acqTotalAmount = acqTotalAmount;
	}

	public Long getAcqTotalItems() {
		return acqTotalItems;
	}

	public void setAcqTotalItems(Long acqTotalItems) {
		this.acqTotalItems = acqTotalItems;
	}

	public Long getAcqTotalSuccessItems() {
		return acqTotalSuccessItems;
	}

	public void setAcqTotalSuccessItems(Long acqTotalSuccessItems) {
		this.acqTotalSuccessItems = acqTotalSuccessItems;
	}

	public Long getAcqTotalFailedItems() {
		return acqTotalFailedItems;
	}

	public void setAcqTotalFailedItems(Long acqTotalFailedItems) {
		this.acqTotalFailedItems = acqTotalFailedItems;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}

	public Long getTotalSuccessItems() {
		return totalSuccessItems;
	}

	public void setTotalSuccessItems(Long totalSuccessItems) {
		this.totalSuccessItems = totalSuccessItems;
	}

	public Long getTotalFailedItems() {
		return totalFailedItems;
	}

	public void setTotalFailedItems(Long totalFailedItems) {
		this.totalFailedItems = totalFailedItems;
	}

	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public Date getCheckFileDate() {
		return checkFileDate;
	}

	public void setCheckFileDate(Date checkFileDate) {
		this.checkFileDate = checkFileDate;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


	public String getAcqEnname() {
		return acqEnname;
	}

	public void setAcqEnname(String acqEnname) {
		this.acqEnname = acqEnname;
	}


	public String getAcqCnname() {
		return acqCnname;
	}

	public void setAcqCnname(String acqCnname) {
		this.acqCnname = acqCnname;
	}

	public String getCheckFileName() {
		return checkFileName;
	}

	public void setCheckFileName(String checkFileName) {
		this.checkFileName = checkFileName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
