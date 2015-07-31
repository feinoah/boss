/**
 * 版权 (c) 2015 深圳移付宝科技有限公司
 * 保留所有权利。
 */

package com.eeepay.boss.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.encryptor.md5.Md5;
import com.eeepay.boss.service.PurseCashService;
import com.eeepay.boss.service.PurseService;
import com.eeepay.boss.service.SettleTransferDBService;

/**
 * 描述：实时提现
 *
 * @author ym 
 * 创建时间：2015年6月25日
 */
@Controller
@RequestMapping("pursePay")
public class PursePayController extends BaseController{
  private static final Logger log=LoggerFactory.getLogger(PursePayController.class);
  private static final String SECRETKEY="1552E31378470396E0531D055CC68660";
  
  //TODO test
  private static final String ALLOW_ACCESS_IP="192.168.4.14,192.168.4.10,192.168.1.138";
//  private static final String ALLOW_ACCESS_IP="115.28.36.50"; //online
  
  private static final String STATUS_FAILURE="0";
  private static final String STATUS_SUCCESS="1";
  
  
  @Resource
  private PurseCashService purseCashService;
  @Resource
  private SettleTransferDBService settleTransferDBService;
  @Resource
  private PurseService purseSerivce;
  
  
  /**
   * 
   * 功能：
   *
   * @param params
   * @param response
   * @return status 0:失败 1:成功  仅作展示，不做修改流水的依据
   *         返回：cashId,status,errCode,errMsg
   * @throws IOException 
   */
  @RequestMapping("cashPursePay")
  public void cashPursePay(@RequestParam Map<String, String> params,HttpServletRequest request,HttpServletResponse response) throws IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/plain; charset=utf-8");

    String cashId = params.get("cashId");
    String sign = params.get("sign");

    JSONObject result = new JSONObject();
    result.put("cashId", cashId);
    String digest = Md5.md5Str(cashId + SECRETKEY);
    if (!digest.equalsIgnoreCase(sign)) {
      log.error("交易验证失败");
      result.put("status", STATUS_FAILURE);
      result.put("errCode", "sign_error");
      result.put("errMsg", "交易验证失败");
      response.getWriter().write(result.toString());
      return;
    }

    String clientIP = this.getIpAddr(request);
    log.info("客户端ip："+clientIP);
    if (ALLOW_ACCESS_IP.indexOf(clientIP) == -1) {
      log.error("访问受限,非法请求");
      result.put("status", STATUS_FAILURE);
      result.put("errCode", "access_error");
      result.put("errMsg", "非法请求");
      response.getWriter().write(result.toString());
      return;
    }

    Map<String, Object> cashMap = new HashMap<String, Object>();
    int ret = 0;
    try {
      cashMap = purseSerivce.getBagExtractionDetailById(Long.parseLong(cashId));
      if (cashMap == null) {
        throw new Exception("提现记录不存在");
      }
      ret = purseSerivce.initPurseCashDetail(cashId);
      if (ret != 1) {
        throw new Exception("提现记录状态异常");
      }
    } catch (Exception e) {
      log.info(e.getMessage());
      result.put("status", STATUS_FAILURE);
      result.put("errCode", "error");
      result.put("errMsg", e.getMessage());
      response.getWriter().write(result.toString());
      return;
    }

    List<Map<String, Object>> cashList = new ArrayList<Map<String, Object>>();
    cashList.add(cashMap);
    purseCashService.purseCash(cashList);
    result.put("status", STATUS_SUCCESS);
    result.put("errCode", "success");
    result.put("errMsg", "提现请求已发送");
    response.getWriter().write(result.toString());
    return;
    
  } 
 
 /**
  * 获取客户端IP地址，使用反向代理服务器也可以正常获取到
  * 
  * @param request
  * @return
  */
 public  String getIpAddr(HttpServletRequest request) {
   String ipAddress = null;
   ipAddress = request.getHeader("x-forwarded-for");
   if (ipAddress == null || ipAddress.length() == 0
       || "unknown".equalsIgnoreCase(ipAddress)) {
     ipAddress = request.getHeader("Proxy-Client-IP");
   }
   if (ipAddress == null || ipAddress.length() == 0
       || "unknown".equalsIgnoreCase(ipAddress)) {
     ipAddress = request.getHeader("WL-Proxy-Client-IP");
   }
   if (ipAddress == null || ipAddress.length() == 0
       || "unknown".equalsIgnoreCase(ipAddress)) {
     ipAddress = request.getRemoteAddr();
     if (ipAddress.equals("127.0.0.1")) {
       // 根据网卡取本机配置的IP
       InetAddress inet = null;
       try {
         inet = InetAddress.getLocalHost();
       } catch (UnknownHostException e) {
         e.printStackTrace();
       }
       ipAddress = inet.getHostAddress();
     }

   }
   // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
   if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                             // = 15
     if (ipAddress.indexOf(",") > 0) {
       ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
     }
   }
   if (ipAddress == null) {
     ipAddress = "";
   }
   return ipAddress;
 }
 
  
  

}
