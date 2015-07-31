package com.eeepay.boss.task;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.service.TransService;

/**
 * 小票定时任务
 * @author 袁鹏
 * 创建时间  2015/1/27 16:43
 */
@Component
public class SignReviewTask {

	private static final Logger log = LoggerFactory.getLogger(SignReviewTask.class);
	
	@Resource
	private TransService transService;
	
	/**
	 * 每天凌晨1点执行定时任务
	 */
	@Scheduled(cron="0 0 1 * * ?")
	public void execute(){
		log.info("==================审核通过未审核的小票任务开始===================");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		
		String createTimeBegin = yesterday + " 00:00:00";
		String createTimeEnd = yesterday + " 23:59:59";
		log.info("==================影响到的记录区间开始时间为："+createTimeBegin + "，结束时间为：" + createTimeEnd + "===================");

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			String checkTime = dateFormat.format(date);
			int rows = transService.updateUnreviewedTransToReviewed(createTimeBegin, createTimeEnd, checkTime, "系统");
			log.info("==================影响到的记录数量" + rows + "===================");
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("==================审核通过未审核的小票任务失败" + e.getMessage() + "===================");
		}
		
		log.info("==================审核通过未审核的小票任务结束===================");
	}
	
}
