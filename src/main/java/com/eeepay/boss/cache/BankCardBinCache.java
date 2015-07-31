/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.cache;

import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.eeepay.boss.utils.Dao;


/**
 * 描述：银行卡bin缓存
 *
 * @author ym
 * 创建时间：2014-08-19
 */

public class BankCardBinCache {
  private static List<Map<String,Object>> cardBinList = null;
  
  public static void load(){
    AbstractApplicationContext context = new ClassPathXmlApplicationContext("spring/db.xml","applicationContext.xml");     
    Dao dao = (Dao)context.getBean("dao"); 
    String sql="select id,bank_name,card_name,card_length,card_type,verify_length,verify_code,create_time from pos_card_bin ";
    List<Map<String,Object>> temp=dao.find(sql);
    if(temp!=null){
      cardBinList=temp;
    }
  }
  
  
/**
* 
* 功能：根据卡bin得到银行名称
*
* @param cardBin 卡bin
* @return 银行名称
*/
public static String getBankName(String cardBin){
 if (cardBinList==null) {
   load();
 }
  
 String xpath = ".[verify_code='"+cardBin+"']/bank_name";
 JXPathContext context =JXPathContext.newContext(cardBinList);
 context.setLenient(true);
 Object result = context.getValue(xpath);
 if (result != null){
   return (String) result;
 }
 return "";
}




}
