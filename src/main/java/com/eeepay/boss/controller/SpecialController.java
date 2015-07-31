package com.eeepay.boss.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.GroupService;
import com.eeepay.boss.service.SpecialService;
import com.eeepay.boss.utils.SysConfig;

/**
 * 
 * @author 王帅
 * @date 2015年3月25日18:19:57
 * @see 批量操作商户特殊需求将写在这里
 *
 */
@Controller
@RequestMapping(value = "/sc")
public class SpecialController  extends BaseController{
	
	@Resource
	private SpecialService specialService;
	
	@Resource
	private GroupService groupService;
	
	private static final Logger log = LoggerFactory.getLogger(SpecialController.class);
	
	
	/**
	 * 批量转移集群
	 * @param request
	 * @param response 返回批量处理状态码，以及操作失败的商户编号
	 * @param params
	 */
	@RequestMapping(value = "/batchMerchantGroup")
	public void batchMerchantGroup(HttpServletRequest request,	HttpServletResponse response,
			@RequestParam Map<String, String> params) { 
		log.info("SpecialController batchMerchantGroup start...");
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		String code = "1001";
		Map<String, String> okMap = new HashMap<String, String>();
		if(params != null){
			if(params.get("groupFileName") != null
					&& !"".equals(params.get("groupFileName").toString())
					&& params.get("groupName") != null && !"-1".equals(params.get("groupName").toString())
					&& params.get("operType") != null && !"-1".equals(params.get("operType").toString())){
				try {
					List<String> list = getExcelContent(params.get("groupFileName").toString(), tmpPath);
					if(null != list && list.size() > 0){
						if(list.size() <= 5000){
							String gid = params.get("groupName").toString();
							Map<String, Object> getGroupInfo = groupService.getGroupsById(gid);
							if(getGroupInfo != null && getGroupInfo.get("group_code") != null && getGroupInfo.get("my_settle") != null && getGroupInfo.get("bag_settle") != null && 
									!"".equals(getGroupInfo.get("group_code").toString()) && !"".equals(getGroupInfo.get("my_settle").toString()) && !"".equals(getGroupInfo.get("bag_settle").toString())){
								List<String> failList = specialService.batchMerchantGroup(list,
										getUser().getRealName(),
										getGroupInfo.get("group_code").toString(),
										getGroupInfo.get("my_settle").toString(),
										getGroupInfo.get("bag_settle").toString(),
										params.get("operType").toString());
								if(failList != null && failList.size() > 0){
									code = "1002";
									okMap.put("merchant_no", failList.toString());
								}else{
									code = "2000";
								}
							}else{
								code = "1006";
							}
						}else{
							code = "1004";
						}
					}else{
						code = "1005";
					}
				} catch (Exception e) {
					log.error("商户批量转移集群异常=" + e.getMessage());
					okMap.put("code", "1000");
					String json = JSONObject.fromObject(okMap).toString();
					outJson(json, response);
				}
			}
		}
		log.info("SpecialController batchMerchantGroup End");
		okMap.put("code", code);
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		
	}
	
	/**
	 * 进入批量操作界面并载入当前系统中所有状态为正常的集群信息
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/batchMerchant")
	public String batchMerchant(final ModelMap model) { 
		//获取当前所有状态为正常的集群信息
		List<Map<String, Object>> groupList = groupService.getGroupInfo();
		model.put("groupList", groupList);
		return "/batchMerchant/batchMerchant";
	}
	
	/**
	 * 根据集群ID获取集群详细信息
	 * @param request
	 * @param response 返回集群详细信息
	 * @param gid 集群编号
	 */
	@RequestMapping(value = "/getGroupInfo")
	public void getGroupInfo(HttpServletRequest request,	HttpServletResponse response,@RequestParam String gid) { 
		//获取当前所有状态为正常的集群信息
		Map<String, Object> getGroupInfo = groupService.getGroupsById(gid);
		String json = JSONObject.fromObject(getGroupInfo).toString();
		outJson(json, response);
	}
	
	
	
	/**
	 * @author 王帅
	 * @date 2015年3月31日15:35:28
	 * @see 批量修改商户类型、是否实名、商户状态，并记录操作记录，部分修改失败的商户编号返回到客户端
	 * @param request
	 * @param response
	 * @param params
	 */
	@RequestMapping(value = "/batchMerchantUpdate")
	public void batchMerchantUpdate(HttpServletRequest request,	HttpServletResponse response,
			@RequestParam Map<String, String> params) { 
		log.info("SpecialController batchMerchantUpdate start...");
		Map<String, String> okMap = new HashMap<String, String>();
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		String code = "1001";
		try {
			if(params != null && null != params.get("merchant_type") && null != params.get("real_flag") && null != params.get("open_status")){
				if("-1".equals(params.get("merchant_type").toString()) && "-1".equals(params.get("real_flag").toString()) && "-1".equals(params.get("open_status").toString())){
					code = "1003";
				}else{
					List<String> list = getExcelContent(params.get("excelFileName").toString(), tmpPath);
					if(null != list && list.size() > 0){
						if(list.size() <= 1000){
							List<String> failList = specialService.specialModifyMerchant(params, list, getUser().getRealName());
							if(failList != null && failList.size() > 0){
								code = "1002";
								okMap.put("merchant_no", failList.toString());
							}else{
								code = "2000";
							}
						}else{
							code = "1004";
						}
					}else{
						code = "1005";
					}
				}
			}
		} catch (Exception e) {
			log.error("SpecialController batchMerchantUpdate Exception = " + e);
			okMap.put("code", "1000");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}
		okMap.put("code", code);
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
	}
	
	/**
	 * @author 王帅
	 * @date 2015年3月31日15:46:55
	 * @see 重置同步好乐付状态，并记录操作记录
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/batchCancelHLF")
	public void batchCancelHLF(HttpServletRequest request,	HttpServletResponse response,@RequestParam Map<String, String> params) { 
		log.info("SpecialController batchCancelHLF START...");
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		Map<String, String> returnMap = new HashMap<String, String>();
		String returnCode = "1001";
		if(!"".equals(tmpPath) && null != params.get("CancelHLF") && !"".equals(params.get("CancelHLF").toString())){
			try {
				List<String> list = getExcelContent(params.get("CancelHLF").toString(), tmpPath);
				if(null != list && list.size() > 0){
					if(list.size() <= 1000){
						List<String> failList = specialService.batchCancelHLF(list, getUser().getRealName());
						if(failList != null && failList.size() > 0){
							returnCode = "1002";
							returnMap.put("merchant_no", failList.toString());
						}else{
							returnCode = "2000";
						}
					}else{
						returnCode = "1004";
					}
				}else{
					returnCode = "1005";
				}
			} catch (Exception e) {
				log.error("SpecialController batchCancelHLF Exception = " + e.getMessage());
				returnMap.put("code", "1000");
				String json = JSONObject.fromObject(returnMap).toString();
				outJson(json, response);
			}
		}
		log.info("SpecialController batchCancelHLF END");
		returnMap.put("code", returnCode);
		String json = JSONObject.fromObject(returnMap).toString();
		outJson(json, response);
	}
	
	/**
	 * @author 王帅
	 * @date 2015年3月31日15:43:41
	 * @see  解析Excel文件，获取第一列数据并以LIST格式返回
	 * @param fileName 文件名称
	 * @param urlTemp 文件地址
	 * @return List
	 * @throws Exception
	 */
	public List<String> getExcelContent(String fileName, String urlTemp)	throws Exception {
		log.info("SpecialController getExcelContent start...");
				String url = urlTemp + fileName;
				List<String> list = new ArrayList<String>();
				InputStream is = new FileInputStream(url);
				Workbook rwb = Workbook.getWorkbook(is);
				try {
					// 获取第一张Sheet表
					Sheet rs = (Sheet) rwb.getSheet(0);
					Cell c;
					for (int i = 0; i < rs.getRows(); i++) {
						c = ((Sheet) rs).getCell(0, i);
						String snStr = c.getContents().trim();
						if(null != snStr && !"".equals(snStr)){
							list.add(snStr);
						}
					}
				} catch (Exception e) {
					log.error("SpecialController getExcelContent Exception = " + e.getMessage());
					throw e;
				}finally{
					log.info("SpecialController getExcelContent 关闭工作簿");
					rwb.close();
				}
				log.info("SpecialController getExcelContent End");
				return list;
			}
	
	@RequestMapping(value="/batchTransferAgent")
	public void batchTransferAgent(HttpServletRequest request,	HttpServletResponse response,@RequestParam Map<String, String> params){
		
		log.info("SpecialController batchTransferAgent START...");
		Map<String, String> returnMap = new HashMap<String, String>();
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		String returnCode = "1001";
		
		if(!"".equals(tmpPath) && null != params.get("TransferAgent") && !"".equals(params.get("TransferAgent").toString())){
			try {
				String agent_no = params.get("agentNo");
				String belong_to_agent = params.get("belong_to_agent");
				
				// 检测代理商编号是否存在，并且代理商编号为从属关系
				if(!"".equals(agent_no) && !"".equals(belong_to_agent) && specialService.checkAgentExistsAndSatisfied(agent_no, belong_to_agent)){
					
					List<String> list = getExcelContent(params.get("TransferAgent").toString(), tmpPath);
					if(null != list && list.size() > 0){
						if(list.size() <= 1000){
							// 执行转移操作返回失败的商户编号列表
							List<String> failList = specialService.batchTransferAgent(params, list, getUser());
							if(failList != null && failList.size() > 0){
								returnCode = "1002";
								returnMap.put("merchant_no", failList.toString());
							}else{
								returnCode = "2000";
							}
						}else{
							returnCode = "1004";
						}
					}else{
						returnCode = "1005";
					}
				}else{
					returnCode = "1001";
				}
			} catch (Exception e) {
				log.error("SpecialController batchTransferAgent Exception = " + e.getMessage());
				returnMap.put("code", "1000");
				String json = JSONObject.fromObject(returnMap).toString();
				outJson(json, response);
			}
		}
		
		returnMap.put("code", returnCode);
		String json = JSONObject.fromObject(returnMap).toString();
		outJson(json, response);
		log.info("SpecialController batchCancelHLF END");
	}

}
