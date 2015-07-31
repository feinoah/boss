package com.eeepay.boss.enums;

public enum TransStatus {
	
	/**
	 * 成功
	 * 
	 * */
	SUCCESS,
	
	/**
	 * 失败
	 * 
	 * */
	FAILED,
	
	/**
	 * 初始化
	 * 
	 * */
	INIT,	
	
	/**
	 * 已冲正
	 * 
	 * */
	REVERSED,
	
	/**
	 * 已撤销
	 * 
	 * */
	REVOKED,
	
	/**
	 * 已结算
	 * 
	 * */
	SETTLE,
	
	/**
	 * 已退款
	 * 
	 * */
	REFUND,
	
	/**
	 * 已下单
	 * 
	 * */
	SENDORDER,
	
	
	/**
	 * 已冻结
	 */
	FREEZED,
	
	/**
	 * 部分成功
	 * 
	 * */
	PARTSUCCESS;
	
	
	

}
