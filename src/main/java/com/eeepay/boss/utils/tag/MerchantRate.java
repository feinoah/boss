package com.eeepay.boss.utils.tag;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * 页面常用标签，如截取长度
 * 
 * 
 */
@SuppressWarnings("serial")
public class MerchantRate extends BodyTagSupport {

	
	private String content;


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
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(content)) {
			if(content.indexOf("<") > 0 )
			{
				String[] ladder =  content.split("<");
				String ladder_min = ladder[0];
				String ladder_value = ladder[1];
				String ladder_max = ladder[2];
				
				sb.append(new BigDecimal(ladder_min).movePointRight(2)+"%");
				sb.append("<");
				sb.append(ladder_value);
				sb.append("<");
				sb.append(new BigDecimal(ladder_max).movePointRight(2)+"%");
				
				content = sb.toString();
			}
				

			try {
				out.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return super.doEndTag();
	}

	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
