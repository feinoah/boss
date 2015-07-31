package com.eeepay.boss.utils.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * 卡号截取
 * 
 * @author donjek
 * 
 */
public class CardCut extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		if (StringUtils.isNotEmpty(content) && content.length() >= 10) {
			content = content.substring(0, 6)+"*****"+content.substring(content.length()-4, content.length());
		}
		
		try {
			out.write(content);
		} catch (IOException e) {
			e.printStackTrace();
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
