package com.eeepay.boss.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.service.AcqMerchantService;

/**
 * 
 */
@Component
public class UpdateLockFlagTask {

	private static final Logger log = LoggerFactory.getLogger(UpdateLockFlagTask.class);
	
	@Resource
	private AcqMerchantService acqMerchantService  ; 
	
	@Scheduled(cron="0 0 2 * * * ")
	public void execute(){
		log.info("UpdateLockFlagTask execute start...");
		try {
			 //更新锁定状态
			acqMerchantService.updateAcqMerchantLock();
			acqMerchantService.updateAcqTerminalLock();
		}catch (Exception e) {
			e.printStackTrace();
			log.error("UpdateLockFlagTask execute Exception = ",e);
		}
		log.info("UpdateLockFlagTask execute End");
	}
	
}
 