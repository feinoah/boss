package com.eeepay.boss.enums;
/**
 * merchantApi开通状态
 * @author wg
 * @date   2013-11-28
 */
public enum MerchantApiStatus {
	USED(1),NOT_USED(0);
	private	int stauts;
	private MerchantApiStatus(int status){
		this.stauts=status;
	}
	public int getStauts() {
		return stauts;
	}
	
}
