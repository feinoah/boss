package com.eeepay.boss.utils.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eeepay.boss.utils.Dao;

/**
 * bank response code
 * 
 * @author donjek
 * 
 */
public class BankCode extends BodyTagSupport implements ApplicationContextAware {
	private static ApplicationContext applicationContext; // Spring应用上下文环境

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		JspWriter out = this.pageContext.getOut();
		Dao dao = applicationContext.getBean(Dao.class);

		String sql = "select response_text from acq_response_code where response_code='"
				+ code + "'";
		try {
			Map<String, Object> result = dao.findFirst(sql);
			if(result != null && result.size() > 0)
			{
				String text = result.get("response_text").toString();
				if (StringUtils.isEmpty(text)) {
					text = code;
				}
				out.write(text + "(" + code + ")");
			}else{
				out.write("(" + code + ")");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.doEndTag();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;

	}

}
