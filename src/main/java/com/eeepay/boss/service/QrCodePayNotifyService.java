/**
 * 版权 (c) 2014 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eeepay.boss.utils.HttpComm;
import com.eeepay.boss.utils.md5.Md5;

/**
 * 描述：腾付通
 *
 * @author ym
 * 创建时间：2014-9-29
 */

@Service
public class QrCodePayNotifyService implements Runnable {
  private static final Logger log=LoggerFactory.getLogger(QrCodePayNotifyService.class);
  
  
  @Resource
  private SZFSDbService szfsDbService;
  
  private Map<String, Object> params;
  
  @Override
  public void run() {
    String channel=params.get("channel").toString();
    
    if ("ygs".equals(channel)) {
      log.info("----通知移公社");
      ygsNofity();
    }else if ("YFB_Mobile".equals(channel)) { 
      log.info("----通知手机充值");
      yfbMobileNofity();
    }
    
    Map<String, String> dbParams=new HashMap<String, String>();
    dbParams.put("id", params.get("id").toString());
    dbParams.put("status", params.get("status").toString());
    dbParams.put("centerPayDate", params.get("centerPayDate").toString());
    dbParams.put("centerPayTime", params.get("centerPayTime").toString());
    dbParams.put("centerPayBankCode", params.get("centerPayBankCode").toString());
    dbParams.put("centerPayAccNo", params.get("centerPayAccNo").toString());
    dbParams.put("notifyFlag", params.get("notifyFlag").toString());
    dbParams.put("notifyMsg", params.get("notifyMsg").toString());
    
    szfsDbService.updateQrCodeOrder(dbParams);
    log.info("----修改通知结果为：" + params.get("notifyFlag").toString()+"|"+params.get("notifyMsg").toString());
    return;
  }
  
  public Map<String, Object> getParams() {
    return params;
  }
  public void setParams(Map<String, Object> params) {
    this.params = params;
  }


  /**
   * 
   * 功能：移公社二维码支付成功通知
   *
   * @return
   */
private void ygsNofity() {
    String notifyFlag = "1";
    String channelSeqNo = params.get("channel_seq_no").toString();
    // TODO 
     String url="http://120.24.80.92/shop_core/payResult?responseBody=";
//    String url = "http://192.168.1.118:6780/shop_core/payResult?responseBody=";
    String getParams = "success=true&msg=支付成功&orderNo=" + channelSeqNo+ "&channel=4";
    String returnStr = "";
    try {
      getParams = Base64.encodeBase64String(getParams.getBytes("UTF-8"));
      getParams = URLEncoder.encode(getParams, "UTF-8");

      returnStr = new HttpComm().sendGet(url + getParams);
      log.info("----移公社通知返回：" + returnStr);
      if (!"success".equalsIgnoreCase(returnStr)) {
        notifyFlag = "0";
      }

    } catch (Exception e) {
      e.printStackTrace();
      notifyFlag = "0";
      returnStr=e.getMessage();
    }
    
    returnStr=returnStr.length()>200?returnStr.substring(0, 200):returnStr;
    params.put("notifyFlag", notifyFlag);
    params.put("notifyMsg", returnStr);

    return;
}
 
/**
 * 
 * 功能：手机充值二维码支付成功通知
 *
 * @return
 */
  public void yfbMobileNofity() {
    String notifyFlag = "1";
    String payKey = "39AB021AC72A7A8B";
    String channelSeqNo = params.get("channel_seq_no").toString();

    // TODO
     String url = "http://172.16.2.108:5781/kamobile/chargeMobile?";
//    String url = "http://192.168.1.49:8088/kamobile/chargeMobile?";
    String result = "";
    String payMethod = "QRCODE";
    String merchantNo = "110011011201958";

    String md5Str = Md5.md5Str("result" + result + "payMethod" + payMethod
        + "merchantNo" + merchantNo + channelSeqNo + payKey);
    String getParams = "result=" + result + "&payMethod=" + payMethod
        + "&merchantNo=" + merchantNo + "&orderId=" + channelSeqNo
        + "&cardType=" + "&hmac=" + md5Str;
    String returnStr = "";
    try {
      returnStr = new HttpComm().sendGet(url + getParams);
      log.info("----手机充值通知返回：" + returnStr);
      if (!"ok".equalsIgnoreCase(returnStr)) {
        notifyFlag = "0";
      }

    } catch (Exception e) {
      e.printStackTrace();
      notifyFlag = "0";
      returnStr=e.getMessage();
    }
    returnStr=returnStr.length()>200?returnStr.substring(0, 200):returnStr;
    params.put("notifyFlag", notifyFlag);
    params.put("notifyMsg", returnStr);
    return;
  }


  
}
