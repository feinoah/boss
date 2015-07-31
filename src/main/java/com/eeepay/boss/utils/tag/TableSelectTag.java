package com.eeepay.boss.utils.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eeepay.boss.service.TableTagService;

public class TableSelectTag extends BodyTagSupport  implements ApplicationContextAware { 
  private static final long serialVersionUID = 1L;
  private String tablename = ""; // 表名
  private String byField = ""; // 根据哪个字段
  private String byField_value = ""; // 根据那个字段的值。
  private String fleldAsSelectValue = ""; // 作为select标签的value值。
  private String fleldAsSelectText = ""; // 作为select标签的text值。
  private String otherOptions = ""; // "全选，请选择"等显示性质的option。
  private String disabled;
  private String value;// 默认选中值
  private String style;
  private String sid;	// select的id
  private String sname;   //select的name
  private String onEvent; //select中的事件
  
 
  private static ApplicationContext applicationContext; // Spring应用上下文环境
  
 
  public String getFleldAsSelectValue() {
    return fleldAsSelectValue;
  }

  public void setFleldAsSelectValue(String fleldAsSelectValue) {
    this.fleldAsSelectValue = fleldAsSelectValue;
  }

  public String getFleldAsSelectText() {
    return fleldAsSelectText;
  }

  public void setFleldAsSelectText(String fleldAsSelectText) {
    this.fleldAsSelectText = fleldAsSelectText;
  }

  public String getOtherOptions() {
    return otherOptions;
  }

  public void setOtherOptions(String otherOptions) {
    this.otherOptions = otherOptions;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  
  public String getOnEvent() {
	return onEvent;
  }
	
  public void setOnEvent(String onEvent) {
    this.onEvent = onEvent;
  }
  

	public String getSid() {
		return sid;
	}
	
	public void setSid(String sid) {
		this.sid = sid;
	}

@Override
  public int doEndTag() throws JspException {
    try {
      // 检查输入参数
      if (tablename == null)
        tablename = "";
      if (byField == null||"".equals(byField))
        byField = "1";
      if (byField_value == null||"".equals(byField_value))
         byField_value = "1"; 
      
    
      // 组织返回数据
      String returnString = "";

      List<Object> list = new ArrayList<Object>();
       
      list.add(byField);
      list.add(byField_value);

     
      TableTagService tableTagService = null;
      
      if(applicationContext==null){
        tableTagService=new TableTagService();
      }else {
        tableTagService= applicationContext.getBean(TableTagService.class);
      }
      
      
      List<Map<String, Object>> listResult = tableTagService.getPosType();
      StringBuffer sb = new StringBuffer();
      for (Map<String, Object> m : listResult) {

        if (String.valueOf(m.get(fleldAsSelectValue)).equals(value)) {
          sb.append("<option  value=\"" + m.get(fleldAsSelectValue) + "\""
              + "selected>" + m.get(fleldAsSelectText) + "</option>");
        } else {
          sb.append("<option  value=\"" + m.get(fleldAsSelectValue) + "\" >"
              + m.get(fleldAsSelectText) + "</option>");
        }
      }

     // String allOption = "<option value=\"-1\" >全部</option>";
      String allOption = "";
      if ("haveWU".equals(otherOptions)) {
        allOption = "<option value=\"\" >无</option>";
      } else if ("onlyInDB".equals(otherOptions)) {
        allOption = "";
      } else if ("needAll".equals(otherOptions)) {
    	allOption = "<option value=\"-1\" >全部</option>"; 
      }else {
        // 什么也不需要做。
      }

      JspWriter out = pageContext.getOut();
      if (StringUtils.isEmpty(disabled)) {
        out.append("<select id='" + sid + "'   style=\"" + style + "\" " + onEvent + " " + value
            + " name=\"" + sname + "\">" + allOption + sb.toString()
            + "</select>");

      } else {
        out.append("<select id='" + sid + "' disabled  style=\"" + style + "\" " + onEvent + " "
            + value + " name=\"" + sname + "\"> " + allOption + sb.toString()
            + "</select>");

      }
    } catch (Exception e) {
      throw new JspException(e);
    }
    return EVAL_PAGE;
  }

  public String getTablename() {
    return tablename;
  }

  @Override
  public void setApplicationContext(ApplicationContext arg0)
      throws BeansException {
    applicationContext = arg0;

  }

  public static ApplicationContext getApplicationContext() {
    return applicationContext;
  }
  
  
  public void setTablename(String tablename) {
    this.tablename = tablename;
  }

  public String getByField() {
    return byField;
  }

  public void setByField(String byField) {
    this.byField = byField;
  }

  public String getByField_value() {
    return byField_value;
  }

  public void setByField_value(String byField_value) {
    this.byField_value = byField_value;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public int doAfterBody() throws JspTagException {
    BodyContent bc = getBodyContent();
    this.byField_value = bc.getString().trim();
    bc.clearBody();
    return SKIP_BODY;
  }

  public String getDisabled() {
    return disabled;
  }

  public void setDisabled(String disabled) {
    this.disabled = disabled;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getSname() {
    return sname;
  }

  public void setSname(String sname) {
    this.sname = sname;
  }
}