/**
 * 
 */
package com.eeepay.boss.filter;

/**
 * hdb
 * 2013-7-12 下午3:06:36 
 */
public class Holder<T> {
	
	private T value;

	public Holder() {
	}
	
	public Holder(T value) {
		super();
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
