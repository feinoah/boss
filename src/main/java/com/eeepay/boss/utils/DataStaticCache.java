/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 描述：
 * 
 * @author ym 创建时间：2014年10月27日
 */

public class DataStaticCache {
  private static Object lock = new Object();
  private static Queue<String> hxbOutQueue = new LinkedList<String>();

  
  
  static {

    // 初始化 hxb outAcc队列
    hxbOutQueue.add("hxbaccountnumberf");
    hxbOutQueue.add("hxbaccountnumbers");
    hxbOutQueue.add("hxbaccountnumbert");

  }

  /**
   * 
   * 功能：按轮流顺序获取hxb的outAcc
   * 
   * @return
   */
  public  static String getHxbOutAccAtIndex() {
    synchronized (lock) {
      String outAcc = hxbOutQueue.poll();
      hxbOutQueue.add(outAcc);
      return outAcc;
    }

  }


}
