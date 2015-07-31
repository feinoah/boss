package com.eeepay.boss.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统告警通知
 * @author DJ
 *
 */
public class MessageUtil implements Runnable {
	static Logger log = LoggerFactory.getLogger(MessageUtil.class.getName());
	
	private String title;
	private  String content;
	private String email;
	
	public MessageUtil(){
	}
	
	public MessageUtil(String title, String content, String email){
		this.title = title;
		this.content = content;
		this.email = email;
	}

	@Override
	public void run() {
		//调用发送邮件的方法
		MessageUtil.sendMail(this.title, this.content,this.email);
	}

	/**
	 * 发送邮件
	 *
	 * @param title
	 * @param content
	 * @param to
	 */
	public static void sendMail(String title, String content, String to) {
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", "mailhost");
		props.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtp.auth", "true");
		Session s = Session.getInstance(props);
		// s.setDebug(true);
		MimeMessage message = new MimeMessage(s);
		try {
			InternetAddress from = new InternetAddress(Constants.EMAIL_ADDRESS,
					Constants.EMAIL_FROMNAME);
			message.setFrom(from);
			InternetAddress[] iaToList = new InternetAddress().parse(to);
			message.setRecipients(Message.RecipientType.TO, iaToList);
			message.setSubject(title, "utf8");
			message.setContent(content, "text/html;charset=utf-8");
			message.saveChanges();
			Transport transport = s.getTransport("smtp");
			// smtp验证，就是你用来发邮件的邮箱用户名密码
			transport.connect(Constants.EMAIL_SMTP, Constants.EMAIL_USERNSME,
					Constants.EMAIL_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			log.info("发送邮件成功：" + to + " Date: "+ new Date());
		} catch (Exception e) {
			log.error("发送邮件异常：" + title + "|" + content + "|" + to);
			log.info("发送邮件异常：" + e);
			e.printStackTrace();
		}
	}
}
