package com.eeepay.boss.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.service.PurseCashService;

/**
 *  描述：钱包提现结果同步任务
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */
@Component("purseCashSynTaskNew")
public class PurseCashSynTaskNew {
	private static final Logger log = LoggerFactory.getLogger(PurseCashSynTaskNew.class);
  
  
  @Resource
  private PurseCashService purseCashService;
	 
//  @Scheduled(cron="0 5,35 * * * ?")
//  @Scheduled(cron="0 5,15,25,35,45,55 * * * ?")
//	@Scheduled(cron="0 12 * * * ?")
	public void execute(){
	  
	  // open_status [0:未审核   1：审核通过  2：审核不通过  3:体现中       4：提现成功   5：提现失败 ]
    // cash_status [1：未发送  2：发送成功  3：发送失败      4：发送未知  5：转账成功   6：转账失败 ]
			
    String taskStamp=new SimpleDateFormat("yyyy-MM-dd:HH").format(new Date());
    String threadId=Long.toString(Thread.currentThread().getId());
    log.info("----"+taskStamp+"钱包提现同步任务开始"+threadId+"开始执行----");
	  purseCashService.synPurseCash();
	  log.info(taskStamp+"----钱包提现同步任务处理完毕----");
	  
	}
	
}
 