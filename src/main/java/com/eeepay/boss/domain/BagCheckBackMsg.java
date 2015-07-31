package com.eeepay.boss.domain;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 建行上传文件返回状态xml
 * 
 * 
 */
@XmlRootElement(name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
public class BagCheckBackMsg  {
	
	String msg;
	String success;
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}

	
}
