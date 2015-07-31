package com.eeepay.boss.utils;

import com.eeepay.boss.domain.SmsBean;
import com.eeepay.boss.utils.pub.Sms;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmsUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext; // Spring应用上下文环境


	public static void sendSms(final SmsBean smsBean){
		new Thread(new Runnable(){
			@Override
			public void run() {
				String sql = "select content, suffix from sms_config where agent_no = ? and pos_type = ? and send_for = ?";
				Map<String, Object> smsConfig  = getDao().findFirst(sql, new Object[]{smsBean.getAgent_no(), smsBean.getPos_type(), smsBean.getSend_for()});

				if(smsConfig == null){
					sql = "select content, suffix from sms_config where agent_no is null and pos_type = ? and send_for = ?";
					smsConfig = getDao().findFirst(sql, new Object[]{smsBean.getPos_type(), smsBean.getSend_for()});
				}

				if(smsConfig != null){
					String content = smsConfig.get("content").toString();
					String suffix = smsConfig.get("suffix").toString();

					FormattingTuple ft = MessageFormatter.arrayFormat(content, smsBean.getPlaceHolder());
					Sms.sendMsgOem(smsBean.getMobile(), ft.getMessage(), suffix);
				}
			}
		}).start();
	}




	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;
	}

	public static Dao getDao() {
		return applicationContext.getBean(Dao.class);
	}
}
