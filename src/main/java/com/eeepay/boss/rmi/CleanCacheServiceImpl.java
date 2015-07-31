package com.eeepay.boss.rmi;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.SysConfig;

@Service
public class CleanCacheServiceImpl extends RmiProxyFactoryBean implements CleanCacheService{
	
	public CleanCacheServiceImpl(){
		super();
		setServiceUrl("");
		setServiceInterface(RmiCleanCacheService.class);
	}

	public void toCleancache() {
		
		String url = SysConfig.value("rmiCleanCacheUrl");
		setServiceUrl(url);
		setServiceInterface(RmiCleanCacheService.class);
		super.afterPropertiesSet();
		
		if(super.getObject() instanceof RmiCleanCacheService){
			RmiCleanCacheService rmiCleanCacheService = (RmiCleanCacheService) super.getObject();
			rmiCleanCacheService.cleanCache();
		}
	}
	
}
