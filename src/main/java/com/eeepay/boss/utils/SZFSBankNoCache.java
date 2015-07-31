/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;



/**
 * 描述：
 *
 * @author ym 
 * 创建时间：2014年10月21日
 */

public class SZFSBankNoCache implements ApplicationContextAware{
  private static ApplicationContext applicationContext;
  private static List<Map<String,Object>> bankList = null;


  
  private static void load(){
    Dao dao = getDao(); 
    String sql="select * from szfs_bank_map order by id";
    List<Map<String,Object>> temp=dao.find(sql);
    if(temp!=null){
      bankList=temp;
    }
  }
  

  public static List<Map<String,Object>> getList(){
    if (bankList==null) {
      load();
    }
    return bankList;
  }
  
  
   @SuppressWarnings("unchecked")
  public static Map<String, Object> getBankMap(String bankNo){
    if (bankList==null) {
      load();
    }
     
    String xpath = ".[bank_no='"+bankNo+"']";
    JXPathContext context =JXPathContext.newContext(bankList);
    context.setLenient(true);
    Object result = context.getValue(xpath);
    if (result == null){
      return null;
    }
    return (Map<String, Object>) result;
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
