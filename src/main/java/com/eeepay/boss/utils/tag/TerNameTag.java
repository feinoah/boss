package com.eeepay.boss.utils.tag;

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eeepay.boss.utils.DictCache;
import com.eeepay.boss.utils.StringUtil;

public class TerNameTag extends BodyTagSupport implements	ApplicationContextAware {

	private static ApplicationContext applicationContext;
	//name属性
	private String sname;
	//是否启用
	private String disabled;
	//默认选中
	private String value;
	//样式
	private String style;
	
	//id
	private String id;
	@Override
	public void setApplicationContext(ApplicationContext args)
			throws BeansException {
		// TODO Auto-generated method stub
		applicationContext =args;
	}
	
	
	@Override
	public int doStartTag() throws JspException {
		// TODO Auto-generated method stub
		return super.doStartTag();
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}


	@Override
	public int doEndTag() throws JspException {
		// TODO Auto-generated method stub
		try {
			JspWriter out = this.pageContext.getOut();
			List<Map<String, Object>> terList = DictCache.getList("ter_name");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < terList.size(); i++) {
				String svalue =terList.get(i).get("code_id").toString();
				String codeName = terList.get(i).get("code_name").toString();
				if(StringUtil.isEmpty(value)&&value.equals(svalue)){
					sb.append("<option value=\""+svalue+"\" selected>"+codeName+"</option>");
				}else{
					sb.append("<option value=\""+svalue+"\">"+codeName+"</option>");
				}
			}
			
			String allOp = "<option value=\"-1\">---请选择---</option>";
			
			if(StringUtils.isEmpty(disabled)){
				out.append("<select id='"+id+"'   style=\""+style+"\" "+value+" name=\""+sname+"\">"+ allOp +sb.toString()+"</select>");

			}else{
				out.append("<select id='"+id+"' disabled  style=\""+style+"\" "+value+" name=\""+sname+"\"> "+allOp+sb.toString()+"</select>");

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.doEndTag();
		
	}


	public String getSname() {
		return sname;
	}


	public void setSname(String sname) {
		this.sname = sname;
	}


	public String getDisabled() {
		return disabled;
	}


	public void setDisabled(String disabled) {
		this.disabled = disabled;
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
	
	

}
