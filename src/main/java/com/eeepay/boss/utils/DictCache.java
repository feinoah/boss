/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;



/**
 * 描述：数据字典缓存
 *
 * @author ym
 * 创建时间：2014-5-29
 */

public class DictCache implements ApplicationContextAware{
  private static ApplicationContext applicationContext;
  private static List<Map<String,Object>> dicts = null;
  
  public static void load(){
    Dao dao = getDao(); 
    String sql="select * from sys_dict order by code_type,seq";
    List<Map<String,Object>> temp=dao.find(sql);
    if(temp!=null){
      dicts=temp;
    }
  }
  
  
/**
* 
* 功能：得到某个具体字典的名字，用于展示字典用
*
* @param codeType 类型
* @param dictId 字典标识
* @return 字典名称
*/
  public static String getDictName(String codeId){
	  if (dicts==null) {
	    load();
	  }
	   
	  String xpath = ".[code_id='"+codeId+"']/remark";
	  JXPathContext context =JXPathContext.newContext(dicts);
	  context.setLenient(true);
	  Object result = context.getValue(xpath);
	  if (result != null){
	    return (String) result;
	  }
	  return "";
	 }
  
  /**
   * 
   * 功能：得到某个具体字典的名字，用于展示字典用
   *
   * @param codeType 类型
   * @param dictId 字典标识
   * @return 字典名称
   */
   public static String getDictName(String codeType,String codeId){
    if (dicts==null) {
      load();
    }
     
    String xpath = ".[code_type='"+codeType+"' and code_id='"+codeId+"']/code_name";
    JXPathContext context =JXPathContext.newContext(dicts);
    context.setLenient(true);
    Object result = context.getValue(xpath);
    if (result != null){
      return (String) result;
    }
    return "";
   }
  

/**
 * 
 * 功能：得到某个银行某个类型的字典列表
 *
 * @param codeType 类型
 * @return 符合字典列表
 */
@SuppressWarnings("unchecked")
public static List<Map<String,Object>> getList(String codeType){
  if (dicts==null) {
    load();
  }
  String xpath = ".[code_type='"+codeType+"']";
  JXPathContext context =JXPathContext.newContext(dicts);
  List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
  for(Iterator<Map<String,Object>> it = context.iterate(xpath);it.hasNext();){
    Map<String,Object> temp = (Map<String,Object>) it.next();
    result.add(temp);
  }
  return result;
}

@Override
public void setApplicationContext(ApplicationContext arg0)
    throws BeansException {
  applicationContext = arg0;
}

public static Dao getDao() {
  return applicationContext.getBean(Dao.class);
}

}
