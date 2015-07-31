package com.eeepay.boss.task;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.TransService;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.pub.Sms;

/**
 * 定时统计
 * @author Administrator
 */
// @Component
public class TradeStatTask {

private static final Logger log = LoggerFactory.getLogger(UpdateProductIdTask.class);
	
	@Resource
	private TransService transService;
	@Resource
	private MerchantService merchantService;
	
//	@Scheduled(cron="0 0/5 * * * ? ")
	@Scheduled(cron="0 0 9 * * * ")
	public void execute(){
/*		try {
			String phoneNumber=SysConfig.value("phoneNumberTradeStat");//交易统计
			String[] phones=null;
			if(phoneNumber!=null&&phoneNumber.indexOf("|")>-1){
				phones=phoneNumber.split("\\|");
			}else{
				phones=new String[]{phoneNumber};
			}
//			 "9月2日数据统计  交易总额：322,323.00元，增长：+2.00%;总笔数：49笔，增长：+2.00% | 新增商户数：23户，增长：+2.00%"
			String date1=DateUtils.getDateFormatTime(new Date(), -1);
			String date2=DateUtils.getDateFormatTime(new Date(), -2);
			Map<String,Object> transMap1=transService.countTrade(date1);
			Map<String,Object> transMap2=transService.countTrade(date2);
			Map<String,Object> merchantMap1=merchantService.countMerchantNum(date1);
			Map<String,Object> merchantMap2=merchantService.countMerchantNum(date2);
			
			String trans_increase="";
			BigDecimal trans_a1=((BigDecimal)transMap1.get("trans_amount"))==null?new BigDecimal(0):((BigDecimal)transMap1.get("trans_amount"));
			BigDecimal trans_a2=((BigDecimal)transMap2.get("trans_amount"))==null?new BigDecimal(0):((BigDecimal)transMap2.get("trans_amount"));
			if(trans_a2.compareTo(new BigDecimal("0"))>0){
				if(trans_a1.divide(trans_a2,4, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(1))>0){
					trans_increase+="+"+((trans_a1.divide(trans_a2,4, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(1))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
				}else{
					trans_increase+="-"+((new BigDecimal(1).subtract((trans_a1.divide(trans_a2,4, BigDecimal.ROUND_HALF_UP)))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
				}
			}else{
				trans_increase+="+"+((trans_a1.subtract(trans_a2)).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
			}
			
			String transNum_increase="";
			BigDecimal transNum_a1=new BigDecimal((transMap1.get("trans_num")==null?0:transMap1.get("trans_num"))+"");
			BigDecimal transNum_a2=new BigDecimal((transMap2.get("trans_num")==null?0:transMap2.get("trans_num"))+"");
			if(transNum_a2.compareTo(new BigDecimal("0"))>0){
				if(transNum_a1.divide(transNum_a2,4, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(1))>0){
					transNum_increase+="+"+((transNum_a1.divide(transNum_a2,4, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(1))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
				}else{
					transNum_increase+="-"+((new BigDecimal(1).subtract(transNum_a1.divide(transNum_a2,4, BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
				}
			}else{
				transNum_increase+="+"+((transNum_a1.subtract(transNum_a2)).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
			}

			String merchantNum_increase="";
			
			BigDecimal merchantNum_a1=new BigDecimal((merchantMap1.get("num")==null?0:merchantMap1.get("num"))+"");//02
			BigDecimal merchantNum_a2=new BigDecimal((merchantMap2.get("num")==null?0:merchantMap2.get("num"))+"");//01
			if(merchantNum_a2.compareTo(new BigDecimal("0"))>0){
//				merchantNum_increase+=((merchantNum_a1.subtract(merchantNum_a2)).divide(merchantNum_a2,2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)))+"%";
				if(merchantNum_a1.divide(merchantNum_a2,4, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(1))>0){
					merchantNum_increase+="+"+((merchantNum_a1.divide(merchantNum_a2,4, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(1))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
				}else{
					merchantNum_increase+="-"+((new BigDecimal(1).subtract(merchantNum_a1.divide(merchantNum_a2,4, BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
				}
			}else{
				merchantNum_increase+="+"+((merchantNum_a1.subtract(merchantNum_a2)).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
			}
			
			NumberFormat number = NumberFormat.getNumberInstance(); 
			String amount_b=number.format(trans_a1.doubleValue());
			String content=date1+"数据统计  交易总额："+amount_b+"元，增长："+trans_increase+";" +
					"总笔数："+(transMap1.get("trans_num")==null?0:transMap1.get("trans_num"))+"笔，增长："+transNum_increase+" | 新增商户数："+(merchantMap1.get("num")==null?0:merchantMap1.get("num"))+"，增长："+merchantNum_increase;
			log.info(content);
			//Sms.sendMsg(phones, content);
		}catch (Exception e) {
			e.printStackTrace();
			log.error("",e);
		}*/
	}
	
	public static void main(String[] args) {
		Sms.sendMsg(new String[]{"13632945487"}, "测试");
	}
	
}
