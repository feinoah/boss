/**
 * 
 */
package com.eeepay.boss.axis;

import org.apache.axis2.AxisFault;


/**
 * hdb
 * 2013-6-4 下午2:24:27 
 */
public interface AxisCleanCacheService {

	public void cleanAllCache(String merchantNo) throws AxisFault;
}
