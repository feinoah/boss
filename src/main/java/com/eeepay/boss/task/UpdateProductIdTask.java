package com.eeepay.boss.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eeepay.boss.domain.MobileProduct;
import com.eeepay.boss.service.MobileProductService;
import com.eeepay.boss.utils.Http;
import com.eeepay.boss.utils.KeyedDigestMD5;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.XMLAnalysis;

/**
 * 
 * @author hdb
 */
@Component
public class UpdateProductIdTask {

	private static final Logger log = LoggerFactory.getLogger(UpdateProductIdTask.class);
	
	@Resource
	private MobileProductService mobileProductService; 
	
//	@Scheduled(cron="0 0 2 ? * MON ")
	@Scheduled(cron="0 0 12 ? * THU")  //每星期4
	public void execute(){
		log.info("UpdateProductIdTask execute start...");
		try {
			  //获取产品id
			System.out.println("===========UpdateProductIdTask start=================");
			String agentId = SysConfig.value("agentid");
			try {
				agentId=URLDecoder.decode(agentId, "GB2312") ;
			} catch (UnsupportedEncodingException e2) {
				log.error("UpdateProductIdTask execute UnsupportedEncodingException = " + e2);
				e2.printStackTrace();
			}
			
			 Map<String,Object> paramss=new HashMap<String,Object>();
			  paramss.put("agentid", agentId);
			  paramss.put("source", "esales");
			  String sd="agentid="+agentId+"&source=esales&merchantKey="+SysConfig.value("merchantKey");
			  paramss.put("verifystring", KeyedDigestMD5.getKeyedDigest(sd, ""));
			  String body = Http.sendOtherCharset(SysConfig.value("productUrl"), paramss, "GB2312");
			  String responseXml=URLDecoder.decode(body,"UTF-8");
//			  String responseXml=body;
			  List<MobileProduct> list=new ArrayList<MobileProduct>();
			  XMLAnalysis.parsersXml(responseXml, list);
			  mobileProductService.deleteAllData();
			  for(MobileProduct mobile:list){
				  mobileProductService.saveMobileProduct(mobile);
			  }
			  log.info("UpdateProductIdTask execute Success");
		}catch (Exception e) {
			e.printStackTrace();
			log.error("UpdateProductIdTask execute Exception = ",e);
		}
		log.info("UpdateProductIdTask execute end");
	}
	
}
 