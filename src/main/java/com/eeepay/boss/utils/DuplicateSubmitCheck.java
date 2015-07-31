/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.utils;

import java.util.Date;
import java.util.UUID;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eeepay.boss.domain.ErrorVO;
import com.eeepay.hxb.pub.util.Tools;

/**
 * 描述：重复提交控制
 *
 * @author ym 
 * 创建时间：2014年10月28日
 */

public class DuplicateSubmitCheck {

  private static final Logger log=LoggerFactory.getLogger(DuplicateSubmitCheck.class);
  private static Object lock = new Object();
    
  /**
   * 
   * 设置重复提交控制时间戳：0：交易未提交 1：交易已经提交
   *   
   * 
   */
  public static String setDuplicateTime() {
    String duplicateTimeStamp=""+new Date().getTime()+(int)(Math.random()*100000000)+"-"+UUID.randomUUID().toString().replace("-", "");
    SecurityUtils.getSubject().getSession().setAttribute(duplicateTimeStamp, "0");
    return duplicateTimeStamp;
  } 


  /**
   * 
   * 验证是否为重复提交交易
   * 
   */
  public static ErrorVO verifyDuplicate(String duplicateTimeStamp ) {
    ErrorVO error=new ErrorVO();
    Session session=(Session)SecurityUtils.getSubject().getSession();
    String duplicateFlag=(String)session.getAttribute(duplicateTimeStamp);
    if (Tools.isStrEmpty(duplicateFlag)) {
      log.error("重复提交控制时间戳为空或不存在");
      error.setErrCode("checkErr");
      error.setErrMsg("重复提交控制时间戳为空或不存在");
      return error;
    }
    synchronized (lock) {
      if ("0".equals(duplicateFlag)) {
        session.setAttribute(duplicateTimeStamp, "1");
        error.setErrCode("success");
        error.setErrMsg("重复提交控制检验通过");
        return error;
      }else {
        log.error("请勿重复提交");
        error.setErrCode("checkErr");
        error.setErrMsg("请勿重复提交交易");
        return error;
      }
    }
  }
  
}
