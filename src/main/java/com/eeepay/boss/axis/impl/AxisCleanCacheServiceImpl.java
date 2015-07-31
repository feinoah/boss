/**
 * 
 */
package com.eeepay.boss.axis.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.axis.AxisCleanCacheService;
import com.eeepay.boss.utils.SysConfig;

/**
 * hdb
 * 2013-6-4 下午2:34:26 
 */
@Service
public class AxisCleanCacheServiceImpl implements AxisCleanCacheService {

	private static final Logger log = LoggerFactory.getLogger(AxisCleanCacheService.class);
	
	private Lock lock=new ReentrantLock();
	
	public void cleanAllCache(final String merchantNo) throws AxisFault {
		/*final ServiceClient sc = new ServiceClient();;
		new Thread(new Runnable(){
			@Override
			public void run() {
				lock.lock();
				try{
					Options opts = sc.getOptions();
					String url = SysConfig.value("axisCleanCacheUrl");
					opts.setTimeOutInMilliSeconds(40000l);
					opts.setProperty(HTTPConstants.CHUNKED, "false");//设置不受限制.
					opts.setTo(new EndpointReference(url));
					log.info("AxisCleanCacheServiceImpl|cleanAllCache|"+merchantNo);
					OMFactory fac = OMAbstractFactory.getOMFactory();
					OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2","nsl");
					OMElement method = fac.createOMElement("echo", omNs);
					sc.sendRobust(method);
				}catch (Exception e) {
					log.error("AxisCleanCacheServiceImpl|cleanAllCache|Exception|"+e.getMessage());
				}finally{
					try {
						sc.cleanupTransport();
					} catch (Exception e) {
						log.error("AxisCleanCacheServiceImpl|cleanAllCache|finally|Exception|"+e.getMessage());
					}finally{
						lock.unlock();
					}
				}
			}
		}).start();*/
	}
}
