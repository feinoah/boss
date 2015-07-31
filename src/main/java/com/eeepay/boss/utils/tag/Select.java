package com.eeepay.boss.utils.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.eeepay.boss.service.AgentService;

/**
 * Select列表框
 * 
 * @author donjek
 * 
 */
public class Select extends BodyTagSupport implements ApplicationContextAware {
	private static ApplicationContext applicationContext; // Spring应用上下文环境
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//select的name
	private String sname;
	//select数据来源，agent
	private String stype;
	//默认选中值
	private String value;

  private String style;
  //需要作为select标签的value的字段。默认agent_no
  private String fieldAsValue;
  
  //给了parentAgentno，就只选择出它的子级。
  private String getChildByParentAgentno;
  
  
  
  //随便输入什么，以便html页面使用，比如 change事件等。
  private String otherOptions;
	
	private String id;
	
  private String disabled;
  //是否只显示那些可作为父代理商的代理商。
  private String onlyThowParentAgent;
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
		try {
			
			if(StringUtils.isEmpty(style)){
				style= "padding:2px;width:140px";
			}
			
			if(StringUtils.isEmpty(id)){
				id= "agent-select";
			}
			
			AgentService agentService = applicationContext.getBean(AgentService.class);
		  Map<String, String> params=new HashMap<String,String>();
      params.put("onlyThowParentAgent", onlyThowParentAgent);
      params.put("getChildByParentAgentno", getChildByParentAgentno);
		  
//			Page<Map<String, Object>>  list = agentService.getAgentListForTag(params, new PageRequest(0,9999));
      		List<Map<String, Object>>  maps = agentService.getAgentListForTag(params, new PageRequest(0,9999));
//			List<Map<String,Object>> maps = list.getContent();
			StringBuffer sb = new StringBuffer();
			if("".equals(fieldAsValue)||null==fieldAsValue){
			  fieldAsValue="agent_no";
			}
			for(Map<String,Object> m:maps){
				if(String.valueOf(m.get(fieldAsValue)).equals(getValue())){
					sb.append("<option  value=\""+m.get(fieldAsValue)+"\"" + "selected>"+m.get("agent_name")+"</option>");
				}else{
					sb.append("<option  value=\""+m.get(fieldAsValue)+"\" >"+m.get("agent_name")+"</option>");
				}
			}
			String   allOption= "<option value=\"-1\" >全部</option>";
			 if("haveWU".equals(otherOptions)){
			   allOption= "<option value=\"\" >无</option>";
	     }else if("onlyInDB".equals(otherOptions)){
	       allOption= "";
	     }else{
	       //什么也不需要做。
	     }
			 
			if(StringUtils.isEmpty(disabled)){
				out.append("<select id='"+id+"'   style=\""+style+"\" "+value+" name=\""+sname+"\">"+ allOption +sb.toString()+"</select>");

			}else{
				out.append("<select id='"+id+"' disabled  style=\""+style+"\" "+value+" name=\""+sname+"\"> "+allOption+sb.toString()+"</select>");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.doEndTag();
	}

	public String getStype() {
		return stype;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		applicationContext = arg0;

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisabled() {
		return disabled;
	}

	public String getOtherOptions() {
    return otherOptions;
  }

  public void setOtherOptions(String otherOptions) {
    this.otherOptions = otherOptions;
  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getFieldAsValue() {
    return fieldAsValue;
  }

	public void setFieldAsValue(String fieldAsValue) {
    this.fieldAsValue = fieldAsValue;
  }

  public String getOnlyThowParentAgent() {
    return onlyThowParentAgent;
  }

  public void setOnlyThowParentAgent(String onlyThowParentAgent) {
    this.onlyThowParentAgent = onlyThowParentAgent;
  }

  public String getGetChildByParentAgentno() {
    return getChildByParentAgentno;
  }

  public void setGetChildByParentAgentno(String getChildByParentAgentno) {
    this.getChildByParentAgentno = getChildByParentAgentno;
  }

  

}
