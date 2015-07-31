package com.eeepay.boss.controller;

import java.text.SimpleDateFormat;
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

import com.eeepay.boss.service.BagBizService;
import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.TerminalService;
import com.eeepay.boss.service.TransService;
import com.eeepay.boss.utils.md5.Md5;

/**
 * 
 * @author 王帅
 * @data 2014年12月18日10:34:11
 * @see 该类已配免登陆调用，用于提供接口给外部调用,所有提供给对外的接口请写在此处，以便于管理。
 *
 */
@Controller
@RequestMapping(value = "/eic")
public class ExternalInvokingController {
	
	@Resource
	private TransService transService;
	
	@Resource
	private TerminalService terminalService;
	
	@Resource
	private MerchantService merchantService;
	
	@Resource
	private BagBizService bagBizService;
	
	private static final Logger log = LoggerFactory.getLogger(ExternalInvokingController.class);
	
	//用以防止未授权调用的密文
	private static final String CHECK_KEY = "f228cd33dcef91f03598e3b731c8afa0";
	
	
	@RequestMapping(value = "/bindTerminal")
	public void bindTerminal(HttpServletResponse response, 	HttpServletRequest request,@RequestParam String key, @RequestParam String merchant_no, @RequestParam String ternial_no){
		log.info("ExternalInvokingController bindTerminal start ...");
		JSONObject  jsonObject = new JSONObject();
		String msg = "秘钥校验失败!";
		String res_code = "1001";
		try {
			if(key != null && !"".equals(key)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String vkey = sdf.format(new java.util.Date())+CHECK_KEY;
				String checkKey = Md5.md5Str(vkey).toLowerCase();
				if(checkKey.equals(key)){
					Map<String, Object> terMap = terminalService.getPosTerminalByTerminalNo(ternial_no);
					if(terMap != null && terMap.get("open_status") != null && "1".equals(terMap.get("open_status").toString())){
						Map<String, Object> merchantMap = merchantService.getMerchantInfoByMNo(merchant_no);
						if(merchantMap != null){
							if(merchantMap.get("open_status") != null && merchantMap.get("freeze_status") != null && !"0".equals(merchantMap.get("open_status").toString()) && !"1".equals(merchantMap.get("freeze_status").toString())){
								if(terMap.get("agent_no") != null && merchantMap.get("agent_no") != null && !terMap.get("agent_no").toString().equals(merchantMap.get("agent_no").toString())){
									terminalService.setMerchantNo(merchant_no,Integer.parseInt(terMap.get("id").toString()),Integer.parseInt(merchantMap.get("agent_no").toString()));
									msg = "机具绑定成功!";
									res_code = "1000";
								}else{
									msg = "设备所属代理商与商户所属代理商隶属关系异常!";
									res_code = "1005";
								}
							}else{
								msg = "已关闭或已冻结的商户无法绑定设备!";
								res_code = "1004";
							}
						}else{
							msg = "系统未找到商户信息,请检查商户编号是否有误!";
							res_code = "1003";
						}
					}else{
						msg = "系统未找到设备信息或设备未分配!";
						res_code = "1002";
					}
				}
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("ExternalInvokingController bindTerminal SUCCESS = " +  jsonObject.toString());
			response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			log.error("ExternalInvokingController bindTerminal Exception = " + e.getMessage());
		}
	}
	
	/**
	 * 商户钱包-获取微信最后一次生成的TOKEN
	 * @param key 传入的key
	 * @param app_no  客户端类型
	 * @param mobile_no 手机号
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/getLastAccessToken")
	public void getLastAccessToken(@RequestParam String key,	HttpServletResponse response, 	HttpServletRequest request) {
		log.info("ExternalInvokingController getLastAccessToken start ...");
		JSONObject  jsonObject = new JSONObject();
		String msg = "秘钥校验失败!";
		String res_code = "1001";
		try {
			if(key != null && !"".equals(key)){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String vkey = sdf.format(new java.util.Date())+CHECK_KEY;
						String checkKey = Md5.md5Str(vkey).toLowerCase();
						if(checkKey.equals(key)){
							Map<String, Object> list =  bagBizService.getLastAccessToken();
							if(list != null && list.size() > 0){
								jsonObject.put("access_token", list.get("access_token").toString());
								jsonObject.put("last_time", list.get("last_time").toString());
								msg = "";
								res_code = "1003";
							}else{
								msg = "未查询到信息!";
								res_code = "1002";
							}
						}
				
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("ExternalInvokingController getLastAccessToken SUCCESS = " +  jsonObject.toString());
			response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			log.error("ExternalInvokingController getLastAccessToken Exception=" + e.getMessage());
			e.printStackTrace();
		}
		log.info("ExternalInvokingController getLastAccessToken End");
	}
	
	
	/**
	 * 商户钱包就绪
	 * @param key 传入的key
	 * @param app_no  客户端类型
	 * @param mobile_no 手机号
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/merchantWalletReady")
	public void merchantWalletReady(@RequestParam String key,@RequestParam String app_no,@RequestParam String mobile_no,
			HttpServletResponse response, 	HttpServletRequest request) {
		log.info("ExternalInvokingController merchantWalletReady start ...");
		JSONObject  jsonObject = new JSONObject();
		String msg = "秘钥校验失败!";
		String res_code = "1001";
		try {
			if(key != null && !"".equals(key)){
					if(!"".equals(app_no) && !"".equals(mobile_no)){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String vkey = sdf.format(new java.util.Date())+CHECK_KEY;
						String checkKey = Md5.md5Str(vkey).toLowerCase();
						if(checkKey.equals(key)){
							List<Map<String, Object>> list =  merchantService.getMerchantInfoByAppNoAndMobile(app_no,mobile_no);
							if(list != null && list.size() > 0){
								if(list.size() == 1){
									String bag_prepare = list.get(0).get("bag_prepare").toString();
									if("1".equals(bag_prepare)){
										msg = "商户钱包准备就绪";
										res_code = "1003";
									}else{
										int updateCount = merchantService.merchantWalletReady(app_no, mobile_no);
										if(updateCount > 0){
											msg = "操作成功,商户钱包准备就绪";
											res_code = "1003";
										}else{
											msg = "操作失败,请重试";
											res_code = "1006";
										}
									}
								}else{
									msg = "操作失败,涉及多个商户信息";
									res_code = "1004";
								}
							}else{
								msg = "未查询到商户信息!";
								res_code = "1002";
							}
						}
					}else{
						msg = "参数不允许为空!";
						res_code = "1005";
					}
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("ExternalInvokingController merchantWalletReady SUCCESS = " +  jsonObject.toString());
			response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			log.error("ExternalInvokingController merchantWalletReady Exception=" + e.getMessage());
			e.printStackTrace();
		}
		log.info("ExternalInvokingController merchantWalletReady End");
	}
	
	/**
	 * 钱包查询商户信息
	 * @param key 传入的key
	 * @param app_no  客户端类型
	 * @param mobile_no 手机号
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/getMerchantInfo")
	public void getMerchantInfo(@RequestParam String key,@RequestParam String app_no,@RequestParam String mobile_no,
			HttpServletResponse response, 	HttpServletRequest request) {
		log.info("ExternalInvokingController getMerchantInfo start ...");
		JSONObject  jsonObject = new JSONObject();
		String msg = "秘钥校验失败!";
		String res_code = "1001";
		try {
			if(key != null && !"".equals(key)){
					if(!"".equals(app_no) && !"".equals(mobile_no)){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String vkey = sdf.format(new java.util.Date())+CHECK_KEY;
						String checkKey = Md5.md5Str(vkey).toLowerCase();
						if(checkKey.equals(key)){
							List<Map<String, Object>> list =  merchantService.getMerchantInfoByAppNoAndMobile(app_no,mobile_no);
							if(list != null && list.size() > 0){
								msg = "";
								res_code = "1003";
								jsonObject.put("list", list.toArray());
							}else{
								msg = "未查询到商户信息!";
								res_code = "1002";
							}
						}
					}else{
						msg = "参数不允许为空!";
						res_code = "1005";
					}
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("ExternalInvokingController getMerchantInfo SUCCESS");
			response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			log.error("ExternalInvokingController getMerchantInfo Exception=" + e);
			e.printStackTrace();
		}
		log.info("ExternalInvokingController getMerchantInfo End");
	}
	
	
	/**
	 * 客户端打印小票
	 * 获取需要打印小票记录
	 * @param key 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/getTransReceipt")
	public void getTransReceipt(@RequestParam String key,@RequestParam String username, HttpServletResponse response, 	HttpServletRequest request) {
		log.info("ExternalInvokingController getTransReceipt start ...");
		JSONObject  jsonObject = new JSONObject();
		String msg = "秘钥校验失败!";
		String res_code = "1001";
		try {
			if(key != null && !"".equals(key)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					String vkey = sdf.format(new java.util.Date())+CHECK_KEY;
					String checkKey = Md5.md5Str(vkey).toLowerCase();
					if(checkKey.equals(key)){
						List<Map<String, Object>> list =  transService.getTransReceipt(username);
						if(list != null && list.size() > 0){
							msg = "";
							res_code = "1003";
							jsonObject.put("list", list.toArray());
							jsonObject.put("time", list.get(0).get("trans_date_time"));
						}else{
							msg = "暂无打印小票信息!";
							res_code = "1002";
						}
					}
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("ExternalInvokingController getTransReceipt SUCCESS = ");
			response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			log.error("ExternalInvokingController getTransReceipt Exception=" + e);
			e.printStackTrace();
		}
		log.info("ExternalInvokingController getTransReceipt End");
	}
	
	/**
	 * 客户端调用
	 * 清空打印过的小票信息
	 * @param key 密文
	 * @param time 时间
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/clearTransReceipt")
	public void clearTransReceipt(@RequestParam String key, @RequestParam String time, @RequestParam String username, HttpServletResponse response, 	HttpServletRequest request) {
		log.info("ExternalInvokingController clearTransReceipt start ...");
		JSONObject  jsonObject = new JSONObject();
		String msg = "秘钥校验失败!";
		String res_code = "1001";
		try {
			if(key != null && !"".equals(key)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String vkey = sdf.format(new java.util.Date())+CHECK_KEY;
				String checkKey = Md5.md5Str(vkey).toLowerCase();
				if(checkKey.equals(key)){
					if(time != null && !"".equals(time)){
						int removeCount = transService.clearTransReceipt(time, username);
						if(removeCount > 0){
							msg = "";
							res_code = "1003";
						}else{
							msg = "未清空小票信息";
							res_code = "1004";
						}
						log.info("ExternalInvokingController clearTransReceipt SUCCESS = " +removeCount);
					}else{
						msg = "参数列表错误!";
						res_code = "1002";
					}
				}
			}
			jsonObject.put("res_code", res_code);
			jsonObject.put("msg", msg);  
			response.setContentType("text/json;charset=UTF-8");
			log.info("ExternalInvokingController clearTransReceipt SUCCESS = ");
			response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
			log.info("MerchantController clearTransReceipt  Exception = " + e);
			e.printStackTrace();
		}
		log.info("ExternalInvokingController clearTransReceipt End");
	}

}
