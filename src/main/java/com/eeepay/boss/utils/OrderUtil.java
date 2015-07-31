package com.eeepay.boss.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class OrderUtil {

  /**
   * 根据"数据库的订单号"，生成"订单jsp页面订单号"（由数据库的createTime+orderid组成）。
   */

  public static synchronized String buildOrderId(long id, Date createTime) {
    String orderId = id + "";
    orderId = StringUtils.leftPad(orderId, 6, "0");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    return sdf.format(createTime) + orderId;
  }

  
  /**
   * 拆分成日期和订单号,或者 ""和订单号.
   */
  public static synchronized String[] chaiFeiOrderId(String id) {
    String[] createTimeAndId = new String[2];
    if (id.length() > 8) {
      createTimeAndId[0] = id.substring(0, 8);
      createTimeAndId[1] = id.substring(8, id .length());
    }else{
      createTimeAndId[0] = "" ;
      createTimeAndId[1] = id;
    }
    createTimeAndId[1]=delLeftZero(createTimeAndId[1]);
    return createTimeAndId;
  }
  
  /**
   * 去掉左边的"0"。
   */
  public static   String  delLeftZero(String id) {
     
    if("".equals(id)){
      return "";
    }
    while(id.startsWith("0")){
      id=id.substring(1,id.length()); 
    }
    return id;
  }
  
}
