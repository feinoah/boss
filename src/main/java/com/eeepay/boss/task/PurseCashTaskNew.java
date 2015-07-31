package com.eeepay.boss.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.service.PurseCashService;
import com.eeepay.boss.service.PurseService;

/**
 *  描述：钱包提现任务
 *  
 *  @author:ym
 *  创建时间：2014-08-19
 */
@Component("purseCashTaskNew")
public class PurseCashTaskNew {
	private static final Logger log = LoggerFactory.getLogger(PurseCashTaskNew.class);
	
  @Resource
  private PurseCashService purseCashService;
  
	@Resource
	private PurseService purseSerivce;
	
	
//	@Scheduled(cron="0 20,50 * * * ?")
//	@Scheduled(cron="0 11 * * * ?")
	public void execute(){
	  
		// open_status [0:未审核   1：审核通过  2：审核不通过  3:体现中       4：提现成功   5：提现失败 ]
		// cash_status [1：未发送  2：发送成功  3：发送失败      4：发送未知  5：转账成功   6：转账失败 ]
	  
	  String taskStamp=new SimpleDateFormat("yyyy-MM-dd:HH").format(new Date());
	  String threadId=Long.toString(Thread.currentThread().getId());
	  log.info("----"+taskStamp+"钱包提现任务"+threadId+"开始执行----");
	  
		//锁定本次任务处理的记录
	  //update by ym 20150708:查询条件添加【2天以内切2分钟以外】
    Map<String, String> params=new HashMap<String, String>();
		String lockStamp=purseSerivce.initPurseCash(params);
		
		//获取本次处理的记录
		params.clear();
    params.put("openStatus", "3");
    params.put("cashStatus", "1");
    params.put("lockStamp", lockStamp);
		List<Map<String, Object>> list = purseSerivce.queryPurseCash(params);
		log.info("---->共扫描到"+list.size()+"条已审核的提现申请");
		
		purseCashService.purseCash(list);
		
		log.info("----"+taskStamp+"钱包提现任务"+threadId+"处理完毕----");
	}
	
	
	
}
 