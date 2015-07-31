package com.eeepay.boss.utils.tag;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eeepay.boss.service.PosTypeService;

public class PosTypeTag extends BodyTagSupport implements
		ApplicationContextAware {
	private static final long serialVersionUID = 1L;
	private String svalue = "";

	private static ApplicationContext applicationContext; // Spring应用上下文环境

	public String getSvalue() {
		return svalue;
	}

	public void setSvalue(String svalue) {
		this.svalue = svalue;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			PosTypeService posTypeService = null;

			if (applicationContext == null) {
				posTypeService = new PosTypeService();
			} else {
				posTypeService = applicationContext.getBean(PosTypeService.class);
			}

			Map<String, Object> map = posTypeService.getPosType(svalue);

			JspWriter out = pageContext.getOut();
			if(map!=null){
				if(map.get("pos_type_name") != null){
					out.print(map.get("pos_type_name").toString());
				}
			}else{
				out.print(svalue);
			}
				
		} catch (Exception e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;

	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}


}