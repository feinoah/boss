package com.eeepay.boss.utils.tag;

import java.io.IOException;
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

import com.eeepay.boss.service.CheckerService;

/**
 * 审核人下来列表框
 * @author Administrator
 *
 */
public class SelectChecker  extends BodyTagSupport implements ApplicationContextAware {

	private static ApplicationContext applicationContext; // Spring应用上下文环境
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//select的name
	private String sname;
	//select数据来源，checker
	private String stype;
	//默认选中值
	private String value;

    private String style;
    
    //需要作为select标签的value的字段。
    private String fieldAsValue;
  
    private String needAll;
	
	private String id;
	
	private String disabled;
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
				style= "padding:2px;width:130px";
			}
			
			if(StringUtils.isEmpty(id)){
				id= "checker-select";
			}
			
			CheckerService checkerService = applicationContext.getBean(CheckerService.class);
			Page<Map<String, Object>> list = null;
			try {
				list = checkerService.getCheckerListForTag(value, new PageRequest(0,1000));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<Map<String,Object>> maps = list.getContent();
			StringBuffer sb = new StringBuffer();
			if("".equals(fieldAsValue)||null==fieldAsValue){
			  fieldAsValue="checker";
			}
			for(Map<String,Object> m:maps){
				if(m.get(fieldAsValue).equals(getValue())){
					sb.append("<option  value=\""+m.get(fieldAsValue)+"\"" + "selected>"+m.get("checker")+"</option>");
				}else{
					sb.append("<option  value=\""+m.get(fieldAsValue)+"\" >"+m.get("checker")+"</option>");
				}
			}
			String allOption="";
			 if(!"false".equals(needAll)){
			   allOption= "<option value=\"-1\" >全部</option>";
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

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getFieldAsValue() {
    return fieldAsValue;
  }

	public void setFieldAsValue(String fieldAsValue) {
    this.fieldAsValue = fieldAsValue;
  }

  public String getNeedAll() {
    return needAll;
  }

  public void setNeedAll(String needAll) {
    this.needAll = needAll;
  }

	
	
	
}
