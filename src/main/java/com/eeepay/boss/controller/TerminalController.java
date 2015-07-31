package com.eeepay.boss.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.domain.AgentInfo;
import com.eeepay.boss.service.AgentService;
import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.TerminalService;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.DictCache;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.JCEHandler;
import com.eeepay.boss.utils.MessageUtil;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;
/**
 * 机具管理
 * 
 * 
 */
@Controller
@RequestMapping(value = "/ter")
public class TerminalController extends BaseController{

	private static Logger log = LoggerFactory.getLogger(TerminalController.class);
	
	@Resource
	private TerminalService terminalService;
	@Resource
	private AgentService agentService;
	@Resource
	private MerchantService merchantService;
	
	/**
	 * 新增设备型号
	 * @param response
	 * @param params
	 */
	@RequestMapping(value = "/savePos")
	public void savePos(HttpServletResponse response,@RequestParam Map<String, String> params) {
		log.info("TerminalController savePos start...");
		JSONObject  json = new JSONObject();
		String returnCode = "1001";
		if(params != null){
			returnCode = "1002";
			if(!StringUtils.isEmpty(params.get("pos_model_name"))){
				returnCode = "1003";
				List<Map<String, Object>> modelNameList = terminalService.searchPos(params.get("pos_model_name"), "","");
				if(modelNameList.size() == 0){
					returnCode = "1004";
					if(!StringUtils.isEmpty(params.get("pos_model"))){
						returnCode = "1005";
						List<Map<String, Object>> modelList = terminalService.searchPos( "", params.get("pos_model"),"");
						if(modelList.size() == 0){
							returnCode = "1006";
							try {
								if(!StringUtils.isEmpty(params.get("pos_type_name"))){
									List<Map<String, Object>> pos = terminalService.searchPos("", "",params.get("pos_type_name"));
									if(pos.size() == 0){
										params.put("pos_type", GenSyncNo.getInstance().getPosTypeSeq())	;
										params.put("create_person", getUser().getRealName());
										int count = terminalService.savePos(params);
										if(count > 0){
											returnCode = "1007";
										}
									}else{
										returnCode = "1010";
									}
								}else{
									returnCode = "1009";
								}
							} catch (Exception e) {
								log.error("TerminalController savePos Exception = " + e.getMessage());
								returnCode = "1008";
								json.put("code", returnCode);
								outJson(json.toString(), response);
								return;
							}
						}
					}
				}
			}
		}
		json.put("code", returnCode);
		log.info("TerminalController savePos End");
		outJson(json.toString(), response);
	}
	
	/**
	 * 新增设备型号
	 * @param response
	 * @param params
	 */
	@RequestMapping(value = "/savePosModel")
	public void savePosModel(HttpServletResponse response,@RequestParam Map<String, String> params) {
		log.info("TerminalController savePosModel start...");
		JSONObject  json = new JSONObject();
		String returnCode = "1001";
		if(params != null){
			returnCode = "1002";
			if(!StringUtils.isEmpty(params.get("pos_model_name"))){
				returnCode = "1003";
				List<Map<String, Object>> modelNameList = terminalService.searchPos(params.get("pos_model_name"), "","");
				if(modelNameList.size() == 0){
					returnCode = "1004";
					if(!StringUtils.isEmpty(params.get("pos_model"))){
						returnCode = "1005";
						List<Map<String, Object>> modelList = terminalService.searchPos( "", params.get("pos_model"),"");
						if(modelList.size() == 0){
							returnCode = "1006";
							try {
								params.put("create_person", getUser().getRealName());
								int count = terminalService.savePos(params);
								if(count > 0){
									returnCode = "1007";
								}
							} catch (Exception e) {
								log.error("TerminalController savePosModel Exception = " + e.getMessage());
								returnCode = "1008";
								json.put("code", returnCode);
								outJson(json.toString(), response);
							}
						}
					}
				}
			}
		}
		json.put("code", returnCode);
		log.info("TerminalController savePosModel End");
		outJson(json.toString(), response);
	}
	
	/**
	 * 进入设备管理新增设备型号界面
	 * @return "addPosModel"
	 */
	@RequestMapping(value = "/addPosModel")
	public String addPosModel() {
		return "/terminal/addPosModel";
	}
	
	/**
	 * 进入设备管理新增设备类型界面
	 * @return "addPos"
	 */
	@RequestMapping(value = "/addPos")
	public String addPos() {
		return "/terminal/addPos";
	}
	
	/**
	 * 依据设备类型 加载设备型号
	 * @param response
	 * @param params
	 */
	@RequestMapping(value = "/searchPosModel")
	public void searchPosModel(HttpServletResponse response,@RequestParam Map<String, String> params) {
		log.info("TerminalController searchPosModel start....");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(params != null){
			if(params.get("pos_type") != null && !"".equals(params.get("pos_type").toString()) && !"-1".equals(params.get("pos_type").toString()) ){
				list = terminalService.searchPosModel(params.get("pos_type").toString(),"3");
			}
		}
		JSONArray json =JSONArray.fromObject(list);
		log.info("TerminalController searchPosModel End");
		outJson(json.toString(), response);
	}
	
	/**
	 * 查询设备信息
	 * @return "posManager"
	 */
	@RequestMapping(value = "/searchPos")
	public String searchPos(final ModelMap model, @RequestParam Map<String, String> params,	@RequestParam(value = "p", defaultValue = "1") int cpage) {
		log.info("TerminalController searchPos start....");
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>>list =  terminalService.searchPos(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		log.info("TerminalController searchPos End");
		return "/terminal/posManager";
	}
	
	/**
	 * 进入设备管理界面
	 * @return "posManager"
	 */
	@RequestMapping(value = "/posManager")
	public String posManager() {
		return "/terminal/posManager";
	}

	// 机具查询
	@RequestMapping(value = "/terQuery")
	public String terQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		if (params.size() == 110) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00";
			String createTimeEnd = createTime + " 23:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}
		
		Page<Map<String, Object>> list = terminalService.getTerList(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/terminal/terminalQuery";
	}
	
	
	
	
	
	
	    //解除分配代理商
		@RequestMapping(value = "/unDistributeTerminal")
		public void unDistributeTerminal(final ModelMap model,
				
				@RequestParam String id,
				HttpServletResponse response, HttpServletRequest request) throws Exception
		{
			terminalService.unDistributeTerminal(Integer.valueOf(id));
			response.getWriter().write("1");
		}
		//解除绑定商户
		@RequestMapping(value = "/unBindTerminal")
		public void unBindTerminal(final ModelMap model,
				@RequestParam String id,@RequestParam Map<String, String> params,
				HttpServletResponse response, HttpServletRequest request)  throws Exception
		{
			String agentNo = params.get("agentNo");
			terminalService.unBindTerminal(Integer.valueOf(id),agentNo);
			response.getWriter().write("1");
		}
		

	// 转入机具录入页面
	@RequestMapping(value = "/terAddLoad")
	public String terAddLoad(final ModelMap model,
			@RequestParam Map<String, String> params) {
		params.put("model", "Ypos-02");
		model.put("params", params);
		return "/terminal/terminalAdd";
	}

	// 机具录入
	@RequestMapping(value = "/terAdd")
	public void terAdd(HttpServletRequest request,	HttpServletResponse response,	@RequestParam Map<String, String> params) {
		log.info("TerminalController terAdd start...");
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = terminalService.terminalAdd(params);
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
			log.info("TerminalController terAdd End");
		} catch (Exception e) {
			log.error("TerminalController terAdd Exception = " + e.getMessage());
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}

	}
	// 机具修改
	@RequestMapping(value = "/terUpdate")
	public void terUpdate(HttpServletRequest request,
			HttpServletResponse response,
			 @RequestParam Map<String, String> params) {
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			String id =  params.get("id");
			terminalService.terminalUpdate(params, id);
			okMap.put("msg", "OK");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}

	}
	
	// 根据ID查询出要绑定的终端信息
	@RequestMapping(value = "/toTerUpdate")
	public String toTerUpdate(final ModelMap model, @RequestParam String id)
			throws Exception {
		Map<String, Object> params = terminalService.getPosTerminalById(id);
		model.put("params",params);
		return "terminal/terminalUpdate";
	}
	@RequestMapping(value = "/terCheck")
	public void agentNameCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		Map<String, String> okMap = new HashMap<String, String>();
		String id = params.get("id");
		String psam_no = params.get("psam_no");
		String sn = params.get("sn");
		Long countPsamNo = null;
		Long countSN = null;
		if(id!=null){//修改时的验证
			Map<String, Object> map = terminalService.checkPosTerminalPasmNo(psam_no,id);
		    countPsamNo = (Long) map.get("count");
			map = terminalService.checkPosTerminalSn(sn, id);
		    countSN = (Long) map.get("count");
		}else{//添加时的验证
			Map<String, Object> map = terminalService.checkPasmNo(psam_no);
		    countPsamNo = (Long) map.get("count");
			map = terminalService.checkSn(sn);
		    countSN = (Long) map.get("count");
		}
		
		if(countPsamNo.intValue()> 0){
			okMap.put("msg", "existPsamNo");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			return;
		}else if(countSN.intValue()>0){
			okMap.put("msg", "existSN");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			return;
		}
		okMap.put("msg", "noexist");
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		return;
	}
	// 绑定代理商检查
	@RequestMapping(value = "/isExistAgent")
	public void isExistAgent(final ModelMap model,
			@RequestParam String agentNo, HttpServletResponse response,
			HttpServletRequest request) throws Exception {

		try {
			AgentInfo ai = agentService.getAgentNo(agentNo);
			boolean falg = false;
			if (null != ai) {
				falg = true;
			}
			if (falg) {
				response.getWriter().write("1");
			} else {
				response.getWriter().write("0");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 绑定商户检查
	@RequestMapping(value = "/validateMerchant")
	public void validateMerchant(final ModelMap model,
			@RequestParam String merchantNo, @RequestParam String agentNo,HttpServletResponse response,
			HttpServletRequest request) throws Exception {

		try {
			Map<String,Object> merchantInfo = merchantService.queryMerchantInfoByNo(merchantNo);
			
			if(merchantInfo !=null && merchantInfo.size() >0)
			{
				String merchantAgentNo = merchantInfo.get("agent_no").toString();
				if(agentNo.equals(merchantAgentNo))
				{
					response.getWriter().write("1");
				}
				else
				{
					response.getWriter().write("-1");
				}
			}
			else
			{
				response.getWriter().write("0");
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 绑定代理商
	@RequestMapping(value = "/bindTerminal")
	public void bindTerminal(final ModelMap model,
			@RequestParam String merchantNo, @RequestParam String id,
			HttpServletResponse response, HttpServletRequest request)
			throws Exception {
		String flag = "0";
		try {
			boolean falg = false;
			StringBuffer content_sb = new StringBuffer("");
			int agentNo = 0;
			
			if (StringUtils.isNotEmpty(id)) {
				String[] ids = id.split(",");
				for (String i : ids) {
					
					Map<String, Object> map = terminalService.getTerDetail(Long.parseLong(i));
					
					agentNo = Integer.valueOf(map.get("agent_no").toString());
					terminalService.setMerchantNo(merchantNo, Integer.valueOf(i),agentNo);
					map = terminalService.getTerDetail(Long.parseLong(i));
					content_sb.append("<li>");
					content_sb.append("&nbsp;&nbsp;商户名称：<span class='value'>").append(map.get("merchant_short_name")).append("</span>");
					content_sb.append("&nbsp;&nbsp;商户号：<span class='value'>").append(map.get("merchant_no")).append("</span>");
					content_sb.append("&nbsp;&nbsp;终端号：<span class='value'>").append(map.get("terminal_no")).append("</span>");
					content_sb.append("</li>");
					falg = true;
				}
				content_sb.append("<br>");
			}
			flag = "1";
			//仅给代理商编号为3124的代理商发送邮件
			if (falg && agentNo == 3124) {
				//flag = "0";
				/**
				 * 绑定成功，则下发邮件，通知商户
				 */
				String title = "移联支付 --- 终端绑定通知";
				Map<String, Object> map = merchantService.queryMerchantMsgByNo(merchantNo);
				//拼接查询条件
				String key = "agent_email_"+agentNo;
				//从配置信息表里取邮件地址
				String email = SysConfig.value(key);
				//如果地址为空，则从机具对应的代理商信息里取邮件地址
				email = (email==null) ? ""+map.get("email") : email;
				String agent_name = ""+map.get("agent_name");
				
				StringBuffer sbf = new StringBuffer();
				sbf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" >");
				sbf.append("<head><meta charset=\"UTF-8\">");
				sbf.append("<title移联支付 --- 系统邮件</title>");
				sbf.append("<style type=\"text/css\">body{font-size:15px;} .value{color:blue;}</style>");
				sbf.append("</head><body>");
				sbf.append("<br>尊敬的&nbsp;<b>").append(agent_name).append("</b>&nbsp;您好：<br>");
				sbf.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为您绑定的机具信息如下：");
				sbf.append("<ol style =\"margin-left:50px;\">");
				//拼接邮件正文内容
				sbf.append(content_sb);
				sbf.append("</ol>");
				sbf.append("<br><span style=\"margin-left:60px;\">该邮件为系统邮件，请勿回复！谢谢！</span>");
				sbf.append("<div style=\"width:600px; margin:30px auto;color:blue;font-size:14px;\">");
				sbf.append("移联支付版权所有&nbsp;2010粤ICP备09161251号 客户服务热线： 400-600-2999(5*8小时)</div>");
				sbf.append(" </body></html>");
				
				String content = sbf.toString();
				
				MessageUtil mu = new MessageUtil(title, content, email);
				new Thread(mu).start();
				//flag="1";
			} 
			response.getWriter().write(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 分配代理商
	@RequestMapping(value = "/distributeTerminal")
	public void distributeTerminal(final ModelMap model,
			@RequestParam String agentNo, @RequestParam String id, @RequestParam String type,
			HttpServletResponse response, HttpServletRequest request)
			throws Exception {
		try {
			boolean falg = false;
			if (StringUtils.isNotEmpty(id)) {
				String[] ids = id.split(",");
				String allotBatch = GenSyncNo.getInstance().getNextPosAllotNo();
				for (String i : ids) {
					terminalService.setAgentNo(agentNo, Integer.valueOf(i),type,allotBatch);
					falg = true;
				}
				//存记录
				terminalService.saveAllotHistory(agentNo, allotBatch, ids.length, type, getUser().getUserName());
			}

			if (falg) {
				response.getWriter().write("1");
			} else {
				response.getWriter().write("0");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	// 机具详情
	@RequestMapping(value = "/terDetail")
	public String terDetail(final ModelMap model, @RequestParam Long id) {

		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> terInfo = terminalService.getTerDetail(id);
		params.putAll(terInfo);
		model.put("params", params);
		return "/terminal/terminalDetail";
	}

	// 根据ID查询出要绑定的终端信息
	@RequestMapping(value = "/viewTerminal")
	public String viewTerminal(final ModelMap model, @RequestParam String ids,@RequestParam String agentNo) {
		log.info("TerminalController viewTerminal START");
		String[] is = null;
		if (StringUtils.isNotEmpty(ids)) {
			is = ids.split(",");
		}
		model.put("ids", ids);
		model.put("agentNo", agentNo);
		//显示代理商下面商户
		/*List<Map<String,Object>> list = merchantService.queryMerchantByAgentNo(agentNo);
		model.put("list",list);*/
		//model.put("c", list.size());
		model.put("c", is.length);
		log.info("TerminalController viewTerminal END");
		return "terminal/bindTerminal";
	}
	
	// 根据ID查询出要绑定的终端信息
	@RequestMapping(value = "/viewDistributeTerminal")
	public String viewDistributeTerminal(final ModelMap model, @RequestParam String ids)
			throws Exception {
		String[] is = null;
		if (StringUtils.isNotEmpty(ids)) {
			is = ids.split(",");
		}
		model.put("ids", ids);
		model.put("c", is.length);
		return "terminal/distributeTerminal";
	}
	
	// 转入机具导入页面
	@RequestMapping(value = "/terImpExcel")
	public String terImpExcel(final ModelMap model,
			@RequestParam Map<String, String> params) {

		
		model.put("params", params);
		return "/terminal/terminalsImp";
	}
	
	/**
	 * 获取需要导入的机具信息
	 * @param fileName
	 * @param urlTemp
	 * @return
	 */
	public List<String> getExcelContent(String fileName, String urlTemp)	 {
		log.info("TerminalController getExcelContent start...");
		List<String> terminalList = new ArrayList<String>(); 
		String url = urlTemp + fileName;
		try {
			InputStream is = new FileInputStream(url);
			Workbook rwb = Workbook.getWorkbook(is);
			try {
				Sheet rs = (Sheet) rwb.getSheet(0);
				Cell c;
				System.out.println(rs.getRows());
				for (int i = 1; i < rs.getRows(); i++) {
					c = ((Sheet) rs).getCell(0, i);
					String snStr = c.getContents().trim();
					c = ((Sheet) rs).getCell(1, i);
					String psamStr = c.getContents().trim();
					if (!StringUtil.isEmpty(snStr) && !StringUtil.isEmpty(psamStr)) {
						terminalList.add( snStr+":"+psamStr);
					}
				}
			} catch (Exception e2) {
				log.error("TerminalController getExcelContent Exception2 = " + e2.getMessage());
				return terminalList;
			}finally{
				rwb.close();
				is.close();
			}
		} catch (Exception e) {
			log.error("TerminalController getExcelContent Exception = " + e.getMessage());
			return terminalList;
		}
		
		log.info("TerminalController getExcelContent End");
		return terminalList;
	}
	
	
	@RequestMapping(value = "/terImp")
	public void terImp(HttpServletRequest request,	 HttpServletResponse response, @RequestParam Map<String, String> params) {
		log.info("TerminalController terImp 导入机具开始");
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		Map<String, String> okMap = new HashMap<String, String>();
		String code = "1001";
		List<String> terminalList =  getExcelContent(params.get("excelFileName").toString(),tmpPath);
		if(terminalList != null && terminalList.size() > 0){
			if(terminalList.size() < 3001){
				try {
					List<String> failList =  terminalService.addTerminal(terminalList, params.get("model"), getUser().getRealName(),params.get("agentNo"),getUser().getId().toString(),params.get("pos_type"));
					if(failList != null && failList.size() > 0){
						code = "1003";
						okMap.put("failList", failList.toString());
					}else{
						code = "1004";
					}
				} catch (Exception e) {
					log.error("TerminalController terImp 导入异常 = " + e.getMessage());
					okMap.put("code", "2001");
					String json = JSONObject.fromObject(okMap).toString();
					outJson(json, response);
				}
				
			}else{
				code = "1002";
			}
		}
		log.info("TerminalController terImp 导入机具结束");
		okMap.put("code", code);
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
	}
	
	// 机具导入
	@RequestMapping(value = "/terImpLast")
	public void terImpLast(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		
		try {
			int line = terminalService.terminalImp(params,tmpPath);

			Object obj = params.get("agentNo");
			if(obj!=null && !"-1".equals(obj.toString())){
				Map map = terminalService.getTerminalImp(params,tmpPath);
				Set set = map.keySet();
				StringBuffer sb = new StringBuffer("");				
				String agentNo = (String) params.get("agentNo");
				String allotBatch =GenSyncNo.getInstance().getNextPosAllotNo();
				String model = (String) params.get("model");
				int updateTotal  =  0;
				for (Object key : set) {
					String value = (String) map.get(key);
					String sn = key.toString();
					System.out.println("key:"+key+";    value:"+value);
					updateTotal += terminalService.setAgentNo(agentNo, sn, model, allotBatch);
				}
				terminalService.saveAllotHistory(agentNo, allotBatch, updateTotal, model, getUser().getUserName());
			}
			
			okMap.put("msg", "OK");
			okMap.put("line", line+"");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}

	}
	
	
	// 转入机具导入页面
	@RequestMapping(value = "/viewSecKey")
	public String viewSecKey(final ModelMap model,
			@RequestParam Map<String, String> params) {

		model.put("params", params);
		return "/terminal/buildSecKey";
	}
	
	
	@RequestMapping(value = "/keyExport")
	public void keyExport(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) {
		
		String strDirPath = request.getSession().getServletContext().getRealPath("/");
		String temp = SysConfig.value("uploadtemp");
		String tmpPath = strDirPath + temp;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String fileName =  sdf.format(new Date())+random+".txt";
		
		int num = Integer.parseInt((String)params.get("count"));//生成数量
		List<File> files = new ArrayList<File>();
		String file1Path = tmpPath+File.separator+"SECRET"+"_"+num+"_"+fileName;
		String file2Path = tmpPath+File.separator+"DIVNO"+"_"+num+"_"+fileName;
		String file3Path = tmpPath+File.separator+""+"KEYS_"+num+"_"+fileName;
        try {
            /**这个集合就是你想要打包的所有文件，
             * 这里假设已经准备好了所要打包的文件*/
        	
			File filet1 = new File(file1Path);
			File parentFile1 = filet1.getParentFile();
			// 如果路径不存在，则创建
			System.out.print("\n文件1的上一级路径： "+file1Path);
			if (!parentFile1.exists()) {
				parentFile1.mkdirs();
				System.out.println("\t不存在，开始新建");
			}
			File filet2 = new File(file2Path);
			File parentFile2 = filet2.getParentFile();
			// 如果路径不存在，则创建
			System.out.print("\n文件2的上一级路径： "+file2Path);
			if (!parentFile2.exists()) {
				parentFile2.mkdirs();
				System.out.println("\t不存在，开始新建");
			}
			
			files.add(filet1);
			files.add(filet2);
			
			String marker = params.get("maker");
			if("NEWLAND".equals(marker)){
				File filet3 = new File(file3Path);
				File parentFile3 = filet3.getParentFile();
				// 如果路径不存在，则创建
				System.out.print("\n文件3的上一级路径： "+file3Path);
				if (!parentFile3.exists()) {
					parentFile3.mkdirs();
					System.out.println("\t不存在，开始新建");
				}
	
	            if (!filet1.exists()){   
	            	filet1.createNewFile();   
	            }
	            if (!filet2.exists()){   
	            	filet2.createNewFile();   
	            }
	            if (!filet3.exists()){   
	            	filet3.createNewFile();   
	            }
	            files.add(filet3);
			}
		
            
            //====================================================================================
            
        	params.get("count");
			
			List<String> list = new ArrayList<String>();
			List<String> listTow = new ArrayList<String>();
			List<String> listThree = new ArrayList<String>();
			List<Map<String, String>> secAndTerlist = new ArrayList<Map<String, String>>();  //数据
			
			
			
			/** 
			 * 设备号，生成规则：
			 * 1、取机具类型首字母 （1位）
			 * 2、取 UUID 的 hashCode值 （10位，位数不够，高位补0）
			 * 3、取序列，从1开始，至生成数量最大数（5位，位数不够，高位补0）
			 * 
			 */
			StringBuffer div_no_sb = new StringBuffer();
			DecimalFormat format2 = new DecimalFormat("00000");     
			
			String device = params.get("device");//机具名称
			String device_type = params.get("device_type");//机具款式
			String mid = System.currentTimeMillis()+"";
			String type = DictCache.getDictName(device);
			if(type.length()==1){
				mid = mid.substring(3,13);
			}else if(type.length()==2){
				mid = mid.substring(4,13);
			}
			
			//根据厂家获取传输密钥
			String maker_key = "";
			String key_type = "";
			if("ITRON".equals(marker)){
				maker_key = Constants.ITRON_KEK;
				key_type = "TDK";
			}else if("NEWLAND".equals(marker)){
				maker_key = Constants.NEWLAND_KEK;
				key_type = "TMK";
			}else if("BBPOS".equals(marker)){
				maker_key = Constants.BBPOS_KEK;
				key_type = "TDK";
				if("M368".equals(device_type) || "M188".equals(device_type)){
					key_type = "TMK";
				}
			}else if("TY".equals(marker)){
				maker_key = Constants.TY_KEK;
				key_type = "TMK";
			}
			
			
			
			String end = null;
			
			for (int i = 0; i < num; i++) {
				Map<String, String> secAndTerMap = new HashMap<String, String>();
				
				
				Map<String, String>  keyMap = getKeysCheckValue(maker_key);//获取密钥
				String secretKey = keyMap.get("secretKey");//密文
				String keys = keyMap.get("key");//明文
				String check_value = keyMap.get("cv");//检验值
				//第一个密钥值start
				/*String keysStr = terminalService.getKeys1(maker_key);//得到密钥值和检验值
				String[] key = keysStr.split("==");
				String keys = key[0];//密钥值
				String check_value = key[1].substring(0,8);//截取前8位,检验值
*/				//第一个密钥值end
				
				//生成设备号 start
				div_no_sb.setLength(0);
				//取机具类型首字母
				div_no_sb.append(type);
				//取 UUID 的 hashCode值 （10位，位数不够，高位补0）
				div_no_sb.append(mid);
				//取序列，从1开始，至生成数量最大数（5位，位数不够，高位补0）
				end = format2.format(1+i);
				div_no_sb.append(end);
				String div_no =  div_no_sb.toString(); //设备号
				//生成设备号 end
				
				
			    String divs = div_no+"="+keys+" "+check_value;//明文密钥
			    String divs_secret = div_no+"="+secretKey+" "+check_value;//密文密钥
				list.add(divs_secret);
				listTow.add(div_no);
				if("NEWLAND".equals(marker)){
					listThree.add(divs);
				}
				
				secAndTerMap.put("device_id", div_no);
				secAndTerMap.put("key_content", secretKey);
				secAndTerMap.put("device_type", device_type);
				secAndTerMap.put("check_value", check_value);
				secAndTerMap.put("key_type", key_type);
				secAndTerlist.add(secAndTerMap);
			}
			terminalService.writeToTxt2(file1Path,request, list);
			terminalService.writeToTxt2(file2Path,request, listTow);
		    if("NEWLAND".equals(marker)){
		     
			  terminalService.writeToTxt2(file3Path,request, listThree);
		    }
            
			//===========================================================================================
		    
     
            /**创建一个临时压缩文件，
             * 我们会把文件流全部注入到这个文件中
             * 这里的文件你可以自定义是.rar还是.zip*/

			File file = new File(tmpPath+File.separator+"magazinePub.zip");
			File parentFile=file.getParentFile();
			if(!parentFile.exists()){
				parentFile.mkdirs();
			}
			
            response.reset();
            //response.getWriter()
            //创建文件输出流
            FileOutputStream fous = new FileOutputStream(file);   
			/**
			 * 打包的方法我们会用到ZipOutputStream这样一个输出流, 所以这里我们把输出流转换一下
			 */
			ZipOutputStream zipOut = new ZipOutputStream(fous);
			/**
			 * 这个方法接受的就是一个所要打包文件的集合， 还有一个ZipOutputStream
			 */
			terminalService.zipFile(files, zipOut);
            zipOut.close();
            fous.close();
            terminalService.downloadZip(file,response);
            
			// 插入数据库secret_key and  terminals
			terminalService.addKeyAndTerminals(secAndTerlist);
        }catch (Exception e) {
                e.printStackTrace();
        } finally{
            try {
                   File f = new File(file1Path);
                   File f2 = new File(file2Path);
                   f.delete();
                   f2.delete();
               } catch (Exception e) {
                   e.printStackTrace();
               }
        }

	}
	
	 /**
     * 生成密钥
     * @return
     */
    public Map<String, String> getKeysCheckValue(String maker_key){
          Map<String, String> keysMap = new HashMap<String, String>();
          UUID uuid = UUID.randomUUID();
          String key = uuid.toString().replace("-", "").toUpperCase();
          String cv = JCEHandler.encryptData("00000000000000000000000000000000",key );
          
          String secretKey = JCEHandler.encryptData(key, maker_key);      
          
          keysMap.put("secretKey", secretKey);//密文密钥
          keysMap.put("key", key);//明文密钥
          keysMap.put("cv", cv.substring(0, 8));//截取前8位,检验值
          return keysMap;
    }
	// 刷新词典
	@RequestMapping(value = "/refreshDict")
	public String refresh(final ModelMap model,
			@RequestParam Map<String, String> params) {
		DictCache.load();
		return "/terminal/buildSecKey";
	}
	
    
         /* @RequestMapping(value = "/export")
          public void export1(HttpServletResponse response, HttpServletRequest request) {
                List<Map<String,String>> keysList = new ArrayList<Map<String,String>>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                OutputStream os = null;
                try {
                      int num=1980;
                      //获取参数值
                      List<String> list = new ArrayList<String>();
                      String type = "";
                      String typeNo = "D";
                      String fileName =  "密钥文件_"+type+"_"+sdf.format(new Date())+"_"+num+".txt";
                      
                      
                      request.setCharacterEncoding("UTF-8");
                      os = response.getOutputStream(); // 取得输出流
                      response.reset(); // 清空输出流
                      response.setHeader("Content-Disposition", 
                "attachment;filename="+ new String(fileName.getBytes("GBK"), "ISO8859-1"));// filename指定默认的名字
                      response.setContentType("text/plain;charset=UTF-8");
//                    Map
                      Map<String, String> params =  new HashMap<String, String>();
                      params.put("key_type", "TDK");
                      params.put("device_type", "ITRON_BOX");
                      for (int i = 0; i < num; i++) {
                            String div_no =  typeNo + System.currentTimeMillis();//设备号
                            
                            try {
                                  Thread.sleep(20);
                            } catch (InterruptedException e) {
                                  e.printStackTrace();
                            }
                            
                            //第一个密钥值start
                            Map<String, String> tdk = getKeysCheckValue();
                            tdk.put("key_type", "TDK");
                            tdk.put("device_id", div_no);
                            tdk.put("cv", "aaaaaa");
                            keysList.add(tdk);
                            //第一个密钥值end
                            
                            list.add(div_no+"="+tdk.get("key")+" "+tdk.get("cv"));
                      }
                      
                      int count = terminalService.scretKeyAdd(params, keysList);
                      if(count>0){
                            writeToTxt(os, request, list, fileName);
                      }
                } catch (IOException e) {
                      e.printStackTrace();
                } catch (Exception e) {
                      e.printStackTrace();
                } finally {
                      if (os != null) {
                            try {
                                  os.close();
                            } catch (IOException e) {
                                  e.printStackTrace();
                            }
                      }
                }
          }*/
     /**
           * 写入密钥
           * @param os
           * @param request
           * @param list
           */
           public static void writeToTxt(OutputStream os,HttpServletRequest request, List list,String fileName) {
                      String path = System.getProperty("user.dir");
                      String fileTemp="/upordown/";
                      System.out.println(File.separator);
                      File f = new File(path+fileTemp+File.separator+fileName);
                      File parentFile=f.getParentFile();
                      if(!parentFile.exists()){
                            parentFile.mkdirs();
                      }
                  String enter = "\r\n"; 
                  StringBuffer write ; 
                  try { 
                      for (int i = 0; i < list.size(); i++) { 
                          write = new StringBuffer(); 
                          write.append(list.get(i)+enter);
                          os.write(write.toString().getBytes("UTF-8")); 
                      }        
                      os.flush(); 
                      os.close(); 
                  } catch (Exception e) { 
                      e.printStackTrace(); 
                  } finally { 
                     
                      try { 
                            os.close(); 
                      } catch (Exception e) { 
                          e.printStackTrace(); 
                      } 
                  } 
              }

}

