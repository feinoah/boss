package com.eeepay.boss.enums;

public enum TransType {
	
	/**
	 * 消费
	 */
	PURCHASE,
	
	/**
	 * 冲正
	 */
	REVERSED,
	
	/**
	 * 消费撤销
	 */
	PURCHASE_VOID,
	
	
	/**
	 * 预授权
	 */
	PRE_AUTHORIZATION,
	
	
	/**
	 * 预授权撤销
	 */
	PRE_AUTHORIZATION_VOID,

	
	/**
	 * 预授权完成
	 */
	PRE_AUTHORIZATION_COMPLETION,
	
	
	/**
	 * 预授权完成撤销
	 */
	PRE_AUTHORIZATION_COMPLETION_VOID,
	
	
	/**
	 * 退货
	 */
	PURCHASE_REFUND,
	
	/**
	 * 查余额
	 */
	BALANCE_QUERY,
	
	/**
	 * 转账
	 */
	TRANSFER_ACCOUNTS;
	

}
