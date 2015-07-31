package com.eeepay.boss.utils.tag;

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
 * 传入代理商编号  返回代理商名称
 * @author 王帅
 * @date 2014年10月20日10:46:38
 * @see 本类将根据传入的代理商编号，返回其名称，如代理商不存在，则返回传入的代理商编号
 *
 */
public class AgentNameForNo extends BodyTagSupport implements	ApplicationContextAware {

	//获取Spring上下文
	private static ApplicationContext applicationContext;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4001396827450321071L;
	
	private String agentNo;
	
	
	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	
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
		if(agentNo != null && !"".equals(agentNo)){
			String sql = "select CONCAT(IFNULL(a.agent_name,''), ' ',IFNULL(a.brand_type,'') ) as agent_name from agent_info a where a.agent_no='"
					+ agentNo + "'";
			try {
				Map<String, Object> result = dao.findFirst(sql);
				if(result != null && result.size() > 0)
				{
					String text = result.get("agent_name").toString();
					if (StringUtils.isEmpty(text)) {
						text = agentNo;
					}
					out.write(text);
				}else{
					out.write( agentNo);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try {
				out.write("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return super.doEndTag();
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;
	}

}
