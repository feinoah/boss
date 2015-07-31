package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.domain.AuthList;
import com.eeepay.boss.domain.CardBin;
import com.eeepay.boss.service.AcqMerchantService;
import com.eeepay.boss.service.LogService;
import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.TerminalService;
import com.eeepay.boss.service.TransRouteInfoService;
import com.eeepay.boss.service.UserGroupService;
import com.eeepay.boss.service.UserService;
import com.eeepay.boss.utils.MessageUtil;
import com.eeepay.boss.utils.StringUtil;

/**
 * 收单机构管理
 * 
 */
@Controller
@RequestMapping(value = "/acq")
public class AcqController extends BaseController {

	@Resource
	private AcqMerchantService acqMerchantService;
	@Resource
	private MerchantService merchantService;
	@Resource
	private TerminalService terminalService;
	@Resource
	private TransRouteInfoService transRouteInfoService;
	@Resource
	private UserService userService;
	@Resource
	private UserGroupService userGroupService;
	
	@Resource
	private LogService logService;

	private static final Logger log = LoggerFactory.getLogger(AcqController.class);

	// 系统用户查询
	@RequestMapping(value = "/sysUserQuery")
	public String userQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		paramsTrim(params);

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = userService.getUserList(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/acq/systemUserQuery";
	}

	// 删除收单机构终端
	@RequestMapping(value = "/delTerminalQuery")
	public String delTerminalQuery(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		paramsTrim(params);
		//String acq_terminal_no = params.get("acq_terminal_no");
		//acqMerchantService.delTerminalQuery(acq_terminal_no);
		String terminalId = params.get("terminalId");
		acqMerchantService.delTerminalById(terminalId);
		return "redirect:/acq/terminalQuery";
	}

	// 停用用户
	@RequestMapping(value = "/systemUserDel")
	public void systemUserDel(final ModelMap model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		paramsTrim(params);
		boolean flag = userService.checkUseCanDel(params);
		if (flag) {
			int rowsuc = userService.bossUserDel(params);
			logService.saveOperateLog("停用用户");
			if (rowsuc > 0) {
				outText("1", response);
			} else {
				outText("0", response);
			}
		} else {
			outText("0", response);
		}
	}

	//重新启用用户
	@RequestMapping(value = "/systemUserRestart")
	public void systemUserRestart(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		paramsTrim(params);
		boolean flag = userService.checkUseStatus(params);
		if (flag) {
			int rowsuc = userService.updateUserStatus(params);
			logService.saveOperateLog("重新启用用户");
			if (rowsuc > 0) {
				outText("1", response);
			} else {
				outText("0", response);
			}
		} else {
			outText("0", response);
		}
	}

	// 重置用户密码
	@RequestMapping(value = "/systemUserReset")
	public void systemUserReset(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage)
			throws SQLException {
		paramsTrim(params);
		String id = params.get("id");
		if (id != null && id.trim().length() > 0) {
			int rowNum = userService.resetUserPassword(Long.parseLong(id));
			logService.saveOperateLog("重置用户密码");
			if (rowNum > 0) {
				outText("1", response);
			} else {
				outText("0", response);
			}
		} else {
			outText("0", response);
		}
	}

	// 新增用户信息
	@RequestMapping(value = "/systemUserInput")
	public String systemUserInput(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage)
			throws SQLException {
		
		paramsTrim(params);
		String id = params.get("id");
		if (id != null && id.trim().length() > 0) {
			Map<String, Object> userInfo = userService.queryUserInfo(Long
					.parseLong(id));
			model.put("params", userInfo);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = userGroupService.getUserGroupList(Long.parseLong(id));
			model.put("list", list);
		} else {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = userGroupService.getUserGroupList();
			model.put("list", list);
		}
		return "/acq/systemUserInput";
	}

	// 新增用户信息
	@RequestMapping(value = "/systemUserSave")
	public String systemUserSave(final ModelMap model,
			HttpServletRequest request, @RequestParam Map<String, String> params)
			throws Exception {
		paramsTrim(params);
		String id = params.get("id");
		String[] values = request.getParameterValues("userGroup");
		try {
			// TODO
			if (id == null || id.length() == 0) {
				boolean flag = false;
				flag = userService.checkExitUserName(params);
				if (flag) {

					model.put("params", null);
					model.put("flag", "3");
					model.put("errorMessage", "");
				} else {
					// TODO 新增
					try {
						userService.addSystemUser(params, values);
						logService.saveOperateLog("新增系统用户信息");
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new Exception("新增用户信息出错");
					}
					model.put("params", null);
					model.put("flag", "1");
					model.put("errorMessage", "");
				}
			} else {
				userService.modifySystemUser(params, values);
				logService.saveOperateLog("修改系统用户信息");
				Long.parseLong(id);
				model.put("params", null);
				model.put("flag", "2");
				model.put("errorMessage", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = userGroupService.getUserGroupList();
		model.put("list", list);
		return "/acq/systemUserInput";
	}

	// 用户组查询
	@RequestMapping(value = "/userGroupQuery")
	public String userGroupQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		paramsTrim(params);
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = userGroupService.getUserGroupList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/acq/userGroupQuery";
	}

	// 删除用户组
	@RequestMapping(value = "/userGroupDel")
	public void userGroupDel(final ModelMap model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		String id = params.get("id");
		try {
			Map<String, Object> userGroupInfo = userGroupService
					.queryUserGroupInfo(Long.parseLong(id));
			if (userGroupInfo == null || userGroupInfo.size() == 0) {
				throw new Exception("删除用户组信息出错");
			}
			boolean flag = false;
			flag = userGroupService.checkGroupCanDel(Long.parseLong(id));
			if (flag) {
				outText("2", response);
			} else {
				int reslut = userGroupService.userGroupDel(Long.parseLong(id));
				logService.saveOperateLog("删除用户组信息");
				if (reslut > 0) {
					outText("1", response);
				} else {
					outText("0", response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用户组详情
	 * 
	 * @param model
	 * @param params
	 * @param cpage
	 * @return
	 */
	@RequestMapping(value = "/userGroupDetail")
	public String userGroupDetail(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		paramsTrim(params);
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = userGroupService.getUserList(params,
				page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/acq/userGroupDetail";
	}

	// 新增
	@RequestMapping(value = "/userGroupInput")
	public String userGroupInput(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage)
			throws SQLException {
		paramsTrim(params);
		String id = params.get("id");
		if (id != null && id.trim().length() > 0) {
			Map<String, Object> userGroupInfo = userGroupService
					.queryUserGroupInfo(Long.parseLong(id));
			model.put("params", userGroupInfo);
			List<AuthList> list = new ArrayList<AuthList>();
			list = userGroupService.getAuthList(Long.parseLong(id));
			model.put("list", list);
		} else {
			List<AuthList> list = new ArrayList<AuthList>();
			list = userGroupService.getAuthList();
			model.put("list", list);
		}

		return "/acq/userGroupInput";
	}

	// 新增用户组
	@RequestMapping(value = "/userGroupSave")
	public String userGroupSave(final ModelMap model,
			HttpServletRequest request, @RequestParam Map<String, String> params)
			throws SQLException {
		paramsTrim(params);
		String[] values = request.getParameterValues("authGroup");
		String id = params.get("id");
		if (id == null || id.trim().length() == 0) {
			boolean flag = false;
			flag = userGroupService.checkGroupName(params);
			if (flag) {
				model.put("flag", "3");
			} else {
				userGroupService.userGroupSave(params, values);
				model.put("flag", "1");
				logService.saveOperateLog("新增用户组");
			}
		} else {
			userGroupService.userGroupUpdate(params, values);
			Long.parseLong(id);
			model.put("flag", "2");
			logService.saveOperateLog("修改用户组信息");
		}
		List<AuthList> list = new ArrayList<AuthList>();
		list = userGroupService.getAuthList();
		model.put("list", list);
		return "/acq/userGroupInput";
	}

	// 商户查询
	@RequestMapping(value = "/merchantQuery")
	public String merQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		paramsTrim(params);

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = acqMerchantService.getMerchantList(
				params, page);

		List<Map<String, Object>> acqOrgList = acqMerchantService
				.getMerchantList();

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		model.put("acqOrgList", acqOrgList);
		return "/acq/merchantQuery";
	}
	
	// 终端查询
	@RequestMapping(value = "/terminalQuery")
	public String terQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		paramsTrim(params);

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = acqMerchantService.getTerminalList(params, page);
		
		List<Map<String, Object>> acqOrgList = acqMerchantService.getMerchantList();
		
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		model.put("acqOrgList", acqOrgList);
		return "/acq/terminalQuery";
	}
	
	@RequestMapping(value = "/updateTerminalStatus")
	public String updateTerminalStatus(final ModelMap model,
			@RequestParam Map<String, String> params,HttpServletResponse response){
		paramsTrim(params);
		String type=params.get("type");
		String acq_terminal_no=params.get("acq_terminal_no");
		if("on".equals(type)){
			acqMerchantService.updateTerminalStatus(acq_terminal_no,1);
		}else{
			acqMerchantService.updateTerminalStatus(acq_terminal_no,0);
		}
//		outText("1", response);
		return "redirect:/acq/terminalQuery";
	}
	
	
	//开通或关闭收单机构商户
	@RequestMapping(value = "/updateMerchantStatus")
	public void updateMerchantStatus(final ModelMap model,
			@RequestParam String type,@RequestParam String acq_merchant_no,HttpServletResponse response, HttpServletRequest request){
		try{

			acqMerchantService.updateMerchantStatus(acq_merchant_no,"on".equals(type) ? 0 : 2);
			outText("1", response);
		}catch (Exception e) {
			e.printStackTrace();
			outText("0", response);
		}

	}

	@RequestMapping(value= "/openMerchant")
	public void openMerchant(@RequestParam String acq_merchant_no, HttpServletResponse response){
		try{
			List<Map<String, Object>> gmList = acqMerchantService.queryMerchantGroupByAcqAndAcqNo(acq_merchant_no,"halpay");
			if(gmList != null && gmList.size() > 0){
				outText("2", response);
			}else{
				acqMerchantService.openMerchant(acq_merchant_no);
				outText("1", response);
			}
		}catch (Exception e) {
			e.printStackTrace();
			outText("0", response);
		}
	}
	
	

	
	// 商户详情
	@RequestMapping(value = "/merDetail")
	public String merDetail(final ModelMap model,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		String id = params.get("id");
		Map<String, Object> map = acqMerchantService.queryAcqMerchantInfo(Long
				.valueOf(id));

		Object ladder_fee = map.get("ladder_fee");
		if (ladder_fee != null && ladder_fee.toString().length() > 0) {
			if (ladder_fee.toString().indexOf("<") > 0) {
				String[] ladder = ladder_fee.toString().split("<");
				String ladder_min = ladder[0];
				String ladder_value = ladder[1];
				String ladder_max = ladder[2];

				map.put("ladder_min",
						new BigDecimal(ladder_min).movePointRight(2));
				map.put("ladder_value", ladder_value);
				map.put("ladder_max",
						new BigDecimal(ladder_max).movePointRight(2));
			}
		}
		
		Object merchant_rates = map.get("merchant_rate");
		
		String fee_type = map.get("fee_type").toString();
		if(null != merchant_rates && !"".equals(merchant_rates)){
			if("RATIO".equals(fee_type)){
				String merchant_min = merchant_rates.toString();
				map.put("merchant_Drate",
						new BigDecimal(merchant_min).movePointRight(2));
			}else if("CAPPING".equals(fee_type)){
				String[] mrate = merchant_rates.toString().split("~");
				String merchant_min = mrate[0];
				String merchant_value = mrate[1];
				map.put("merchant_Drate",	new BigDecimal(merchant_min).movePointRight(2));
				map.put("merchant_max_amount", merchant_value);
				
			}else if("LADDER".equals(fee_type)){
				String[] mrate = merchant_rates.toString().split("<");
				String merchant_min = mrate[0];
				String merchant_value = mrate[1];
				String merchant_max = mrate[2];
				
				map.put("merchant_min",	new BigDecimal(merchant_min).movePointRight(2));
				map.put("merchant_value", merchant_value);
				map.put("merchant_max",
						new BigDecimal(merchant_max).movePointRight(2));
			}
		}

		model.put("params", map);
		return "/acq/merchantDetail";
	}

	// 商户新增
	@RequestMapping(value = "/merchantInput")
	public String merInput(final ModelMap model,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		String id = params.get("id");
		if (id != null && id.trim().length() > 0) {
			Map<String, Object> acqMerchantInfo = acqMerchantService.queryAcqMerchant(Long.valueOf(id));

			Object fee_rate = acqMerchantInfo.get("fee_rate");
			if (fee_rate != null && fee_rate.toString().length() > 0) {
				String feeRate = ((BigDecimal) acqMerchantInfo.get("fee_rate"))
						.multiply(new BigDecimal("100")).setScale(4,
								RoundingMode.HALF_UP)
						+ "";
				acqMerchantInfo.put("fee_rate", feeRate);
			}

			Object ladder_fee = acqMerchantInfo.get("ladder_fee");
			
			Object merchant_rates = acqMerchantInfo.get("merchant_rate");
			
			String fee_type = acqMerchantInfo.get("fee_type").toString();
			if(null != merchant_rates && !"".equals(merchant_rates)){
				if("RATIO".equals(fee_type)){
					String merchant_min = merchant_rates.toString();
					acqMerchantInfo.put("merchant_Drate",
							new BigDecimal(merchant_min).movePointRight(2));
				}else if("CAPPING".equals(fee_type)){
					String[] mrate = merchant_rates.toString().split("~");
					String merchant_min = mrate[0];
					String merchant_value = mrate[1];
					acqMerchantInfo.put("merchant_Drate",	new BigDecimal(merchant_min).movePointRight(2));
					acqMerchantInfo.put("merchant_max_amount", merchant_value);
					
				}else if("LADDER".equals(fee_type)){
					String[] mrate = merchant_rates.toString().split("<");
					String merchant_min = mrate[0];
					String merchant_value = mrate[1];
					String merchant_max = mrate[2];
					
					acqMerchantInfo.put("merchant_min",	new BigDecimal(merchant_min).movePointRight(2));
					acqMerchantInfo.put("merchant_value", merchant_value);
					acqMerchantInfo.put("merchant_max",
							new BigDecimal(merchant_max).movePointRight(2));
				}
			}
			
			
			if (ladder_fee != null && ladder_fee.toString().length() > 0) {
				if (ladder_fee.toString().indexOf("<") > 0) {
					String[] ladder = ladder_fee.toString().split("<");
					
					String ladder_min = ladder[0];
					String ladder_value = ladder[1];
					String ladder_max = ladder[2];
					
					acqMerchantInfo.put("ladder_min",
							new BigDecimal(ladder_min).movePointRight(2));
					acqMerchantInfo.put("ladder_value", ladder_value);
					acqMerchantInfo.put("ladder_max",
							new BigDecimal(ladder_max).movePointRight(2));
					
				}
			}
			model.put("params", acqMerchantInfo);
		}
		List<Map<String, Object>> acqOrgList = acqMerchantService
		.getMerchantList();
		model.put("acqOrgList", acqOrgList);

		return "/acq/merchantInput";
	}
	
	// 商户新增
	@RequestMapping(value = "/merchantAdd")
	public String merchantAdd(final ModelMap model,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		String id = params.get("id");
		if (id != null && id.trim().length() > 0) {
			Map<String, Object> acqMerchantInfo = acqMerchantService
					.queryAcqMerchant(Long.valueOf(id));
			
			Object fee_rate = acqMerchantInfo.get("fee_rate");
			if (fee_rate != null && fee_rate.toString().length() > 0) {
				String feeRate = ((BigDecimal) acqMerchantInfo.get("fee_rate"))
						.multiply(new BigDecimal("100")).setScale(4,
								RoundingMode.HALF_UP)
								+ "";
				acqMerchantInfo.put("fee_rate", feeRate);
			}
			
			Object ladder_fee = acqMerchantInfo.get("ladder_fee");
			
			Object merchant_rates = acqMerchantInfo.get("merchant_rate");
			
			String fee_type = acqMerchantInfo.get("fee_type").toString();
			if(null != merchant_rates && !"".equals(merchant_rates)){
				if("RATIO".equals(fee_type)){
					String merchant_min = merchant_rates.toString();
					acqMerchantInfo.put("merchant_Drate",
							new BigDecimal(merchant_min).movePointRight(2));
				}else if("CAPPING".equals(fee_type)){
					String[] mrate = merchant_rates.toString().split("~");
					String merchant_min = mrate[0];
					String merchant_value = mrate[1];
					acqMerchantInfo.put("merchant_Drate",	new BigDecimal(merchant_min).movePointRight(2));
					acqMerchantInfo.put("merchant_max_amount", merchant_value);
					
				}else if("LADDER".equals(fee_type)){
					String[] mrate = merchant_rates.toString().split("<");
					String merchant_min = mrate[0];
					String merchant_value = mrate[1];
					String merchant_max = mrate[2];
					
					acqMerchantInfo.put("merchant_min",	new BigDecimal(merchant_min).movePointRight(2));
					acqMerchantInfo.put("merchant_value", merchant_value);
					acqMerchantInfo.put("merchant_max",
							new BigDecimal(merchant_max).movePointRight(2));
				}
			}
			
			
			if (ladder_fee != null && ladder_fee.toString().length() > 0) {
				if (ladder_fee.toString().indexOf("<") > 0) {
					String[] ladder = ladder_fee.toString().split("<");
					
					String ladder_min = ladder[0];
					String ladder_value = ladder[1];
					String ladder_max = ladder[2];
					
					acqMerchantInfo.put("ladder_min",
							new BigDecimal(ladder_min).movePointRight(2));
					acqMerchantInfo.put("ladder_value", ladder_value);
					acqMerchantInfo.put("ladder_max",
							new BigDecimal(ladder_max).movePointRight(2));
					
				}
			}
			model.put("params", acqMerchantInfo);
		}
		List<Map<String, Object>> acqOrgList = acqMerchantService
				.getMerchantList();
		model.put("acqOrgList", acqOrgList);
		
		return "/acq/merchantAdd";
	}

	// 终端新增
	@RequestMapping(value = "/terminalInput")
	public String terInput(final ModelMap model,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		model.put("params", params);

		return "/acq/terminalInput";
	}

	// 终端新增
	@RequestMapping(value = "/terminalAdd")
	public void terAdd(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		paramsTrim(params);
		// acqMerchantService.addTerminal(params);
		Map<String, String> okMap = new HashMap<String, String>();

		if (acqMerchantService.getAcqTerByTerNo(params.get("acq_terminal_no"))) {
			try {
				acqMerchantService.addTerminalWithAcq(params);
				okMap.put("msg", "OK");
			} catch (Exception e) {
				okMap.put("msg", e.getMessage());
			}
		} else {
			okMap.put("msg", "ERROR");
		}
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		// return "/acq/terminalInput";
	}

	// 终端新增验证
	@RequestMapping(value = "/checkTerAdd")
	public void checkTerAdd(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		paramsTrim(params);
		Map<String, String> okMap = new HashMap<String, String>();
		String acq_merchant_no = params.get("acq_merchant_no");
		Map<String, Object> acqMerInfo = acqMerchantService
				.getAcqMerByNo(acq_merchant_no);

		if (acqMerInfo != null && acqMerInfo.size() > 0) {
			okMap.put("msg", "exist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		} else {
			okMap.put("msg", "noexist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}
	}

	
			// 增加/修改收单商户信息
			 @RequestMapping(value = "/acqMerchantSave")
			 public String acqMerchantSave(final ModelMap model,
			   @RequestParam Map<String, String> params) throws Exception {
				 paramsTrim(params);
			  String id = params.get("id");
			  try {
			   // 判断普通商户编号是否存在
			   String merchant_no = params.get("merchant_no");
			   if (merchant_no != null && !"-1".equals(merchant_no.trim())) {
			    Map<String, Object> map = merchantService
			      .queryMerchantInfoByNo(merchant_no);
			    if (map == null || map.size() == 0) {
			     throw new Exception("普通商户编号不存在");
			    } else {
			     String agent_no = params.get("agent_no");
			     String merchant_agent_no = map.get("agent_no").toString();
			     if (!agent_no.equals(merchant_agent_no)) {
			      throw new Exception("普通商户编号不属于该代理商编号下商户");
			     }
			    }
			   }
			   String large_small_flag = params.get("large_small_flag"); // 0 不可套,1可套
			   
			   //恢复
			   if ("1".equals(large_small_flag)) {
			    params.put("merchant_no", "");
			   }
		
			   if (id == null || id.trim().length() == 0) {
			    // TODO 增加操作
			    try {
			     acqMerchantService.addAcqMerchant(params);
			    } catch (Exception ex) {
			     ex.printStackTrace();
			     throw new Exception("新增收单商户信息出错");
			    }
		
			    model.put("params", null);
			    model.put("flag", "1");
			    model.put("errorMessage", "");
		
			   } else {
			    // TODO 更新操作
			    acqMerchantService.modifyAcqMerchant(params);
			    model.put("params", params);
			    model.put("flag", "2");
			    model.put("errorMessage", "");
		
			   }
			  } catch (Exception e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
			   model.put("flag", "0");
			   model.put("errorMessage", e.getMessage());
			   model.put("params", params);
			  }
			  List<Map<String, Object>> acqOrgList = acqMerchantService
			    .getMerchantList();
			    model.put("acqOrgList", acqOrgList);
			  return "/acq/merchantInput";
			 }

	//保存修改收单机构商户信息
	@RequestMapping(value = "/acqMerchantUpdateSave")
	public String acqMerchantUpdateSave(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception {
		paramsTrim(params);
		String id = params.get("id");
		try {
			// 判断普通商户编号是否存在
			String merchant_no = params.get("merchant_no");
			if (merchant_no != null && !"".equals(merchant_no.trim()) && !"-1".equals(merchant_no.trim())) {
				
				Map<String, Object> map = merchantService.queryMerchantInfoByNo(merchant_no);
				
				if (map == null || map.size() == 0) {
					throw new Exception("普通商户编号不存在");
				} else {
					String agent_no = params.get("agent_no");
					String merchant_agent_no = map.get("agent_no").toString();
					if (!agent_no.equals(merchant_agent_no)) {
						throw new Exception("普通商户编号不属于该代理商编号下商户");
					}
				}
			}
			String large_small_flag = params.get("large_small_flag"); // 0 不可套,1可套
			
			//恢复
			if ("1".equals(large_small_flag)) {
				params.put("merchant_no", "");
			}

		// TODO 更新操作
		try {
			 acqMerchantService.modifyAcqMerchant(params);
		    } catch (Exception ex) {
			   ex.printStackTrace();
			   throw new Exception("修改收单商户信息出错");
		    }
			model.put("params", params);
			model.put("flag", "2");
			model.put("errorMessage", "");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.put("flag", "0");
			model.put("errorMessage", e.getMessage());
			model.put("params", params);
		}
		List<Map<String, Object>> acqOrgList = acqMerchantService
				.getMerchantList();
				model.put("acqOrgList", acqOrgList);
		return "/acq/merchantInput";
	}
	
	
	// 保存新增收单机构商户信息
	@RequestMapping(value = "/acqMerchantAddSave")
	public String acqMerchantAddSave(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception {
		paramsTrim(params);
		//String id = params.get("id");
		try {
			// 判断普通商户编号是否存在
			String merchant_no = params.get("merchant_no");
			String[] terNo =  params.get("terNo").split(";");
			
			if (merchant_no != null && !"".equals(merchant_no.trim()) && !"-1".equals(merchant_no.trim())) {
				
				Map<String, Object> map = merchantService.queryMerchantInfoByNo(merchant_no);
				
				if (map == null || map.size() == 0) {
					throw new Exception("普通商户编号不存在");
				} else {
					String agent_no = params.get("agent_no");
					String merchant_agent_no = map.get("agent_no").toString();
					if (!agent_no.equals(merchant_agent_no)) {
						throw new Exception("普通商户编号不属于该代理商编号下商户");
					}
				}
			}
			String large_small_flag = params.get("large_small_flag"); // 0 不可套,1可套
			
			
			//恢复
			if ("1".equals(large_small_flag)) {
				params.put("merchant_no", "");
			}

			
			try {
				acqMerchantService.addAcqMerchant(params);
				// TODO 增加为收单商户增加机具
				/*acqMerchantService.getAcqTerByTerNo(params.get("acq_merchant_no"));*/
				if(terNo.length < 21){
					for(int h=0;h<terNo.length;h++){
						if(!"".equals(terNo[h])){
							params.put("acq_terminal_no", terNo[h].trim());
							acqMerchantService.addTerminalWithAcq(params);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new Exception("新增收单商户信息出错");
			}
			model.put("params", null);
			model.put("flag", "1");
			model.put("errorMessage", "");
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.put("flag", "0");
			model.put("errorMessage", e.getMessage());
			model.put("params", params);
		}
		List<Map<String, Object>> acqOrgList = acqMerchantService.getMerchantList();
		
		model.put("acqOrgList", acqOrgList);
		return "/acq/merchantAdd";
	}

	// 大套小绑定操作
	@RequestMapping(value = "/merchantBind")
	public String merchantBind(final ModelMap model,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		model.put("params", params);
		return "/acq/merchantBind";
	}
	
	
	
	@RequestMapping(value = "/findMerchantRate")
	public void findMerchantRate(final ModelMap model,
			@RequestParam String  acq_merchant_name,HttpServletResponse response)throws Exception{
		Map<String, Object> okMap=new HashMap<String, Object>();
		Map<String, Object> temp = acqMerchantService.findMerchantRate(acq_merchant_name);
		if (temp != null && temp.size() > 0) {
			
			if(temp.get("fee_rate")==null){
				okMap.put("fee_rate", temp.get("fee_rate")+"");
			}else{
				okMap.put("fee_rate", new BigDecimal(temp.get("fee_rate").toString()).movePointRight(2));
			}
			
			if(temp.get("fee_type")==null){
				okMap.put("fee_type", temp.get("fee_type")+"");
			}else{
				okMap.put("fee_type", temp.get("fee_type").toString());
			}
			
		    if(temp.get("fee_cap_amount") == null){
				okMap.put("fee_cap_amount", temp.get("fee_cap_amount")+"");
			}else{
				okMap.put("fee_cap_amount", temp.get("fee_cap_amount"));
			}
		   
		   if(temp.get("fee_max_amount")== null){
				okMap.put("fee_max_amount", temp.get("fee_max_amount")+"");
			}else{
				okMap.put("fee_max_amount", temp.get("fee_max_amount"));
			} 
		   
		   if(temp.get("fee_single_amount")==null){
				okMap.put("fee_single_amount", temp.get("fee_single_amount")+"");
		   }else{
				okMap.put("fee_single_amount", temp.get("fee_single_amount"));
		   }
		   if(temp.get("ladder_fee")==null){
			   okMap.put("ladder_fee", (String)temp.get("ladder_fee")+"");
		   }else{
			   Object ladder_fee = temp.get("ladder_fee");
				if (ladder_fee != null && ladder_fee.toString().length() > 0) {
					if (ladder_fee.toString().indexOf("<") > 0) {
						String[] ladder = ladder_fee.toString().split("<");
						String ladder_min = ladder[0];
						String ladder_value = ladder[1];
						String ladder_max = ladder[2];

						okMap.put("ladder_min", new BigDecimal(ladder_min).movePointRight(2));
						okMap.put("ladder_value", ladder_value);
						okMap.put("ladder_max", new BigDecimal(ladder_max).movePointRight(2));
					}
				}
		   }
		   if(temp.get("merchant_no")==null){
			   okMap.put("merchant_no", (String)temp.get("merchant_no")+"");
		   }else{
			   okMap.put("merchant_no", (String)temp.get("merchant_no"));
		   }

			okMap.put("msg", "exist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		} else {
			okMap.put("msg", "noexist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}
	}

	
	
	
	// 大套小绑定操作
	@RequestMapping(value = "/merchantBindSaveCheck")
	public void merchantBindSaveCheck(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);

		String acq_merchant_no = params.get("acq_merchant_no");
		String acq_terminal_no = params.get("acq_terminal_no");
		String merchant_no = params.get("merchant_no");
		String terminal_no = params.get("terminal_no");

		// 判断商户号，是否存在
		Map<String, Object> merchantInfo = merchantService
				.queryMerchantInfoByNo(merchant_no);
		if (merchantInfo == null || merchantInfo.size() == 0) {
			outText("-1", response); // 商户号不存在
			return;
		} else {
			Object real_flag = merchantInfo.get("real_flag");
			if (real_flag != null && Integer.valueOf(real_flag.toString()) == 1) {
				outText("-5", response); // 商户号不存在
				return;
			}

			// 普通商户与银盛商户，是否属于同一代理商
			Object agent_no = merchantInfo.get("agent_no");

			Map<String, Object> acqMerchantInfo = acqMerchantService
					.queryAcqMerchantInfo(acq_merchant_no);

			Object agent_no_acq = acqMerchantInfo.get("agent_no");

			if (agent_no_acq == null || agent_no == null
					|| (!agent_no.toString().equals(agent_no_acq.toString()))) {
				outText("-6", response); // 普通商户与银盛商户，不属于同一代理商
				return;
			}

		}
		// 如果输入终端号，判断终端号是否属于这个商户，且存在
		if (terminal_no.trim().length() > 0) {
			Map<String, Object> posTerminalInfo = terminalService
					.getPosTerminalByTerminalNo(terminal_no);
			if (posTerminalInfo == null || posTerminalInfo.size() == 0) {
				outText("-2", response);// 终端号不存在
				return;
			} else {
				String pos_terminal_merchant_no = posTerminalInfo.get(
						"merchant_no").toString();
				if (!merchant_no.equals(pos_terminal_merchant_no)) {
					outText("-3", response);// 终端号不属于此商户号商户
					return;
				}
			}
		}

		// 如果该记录已经存在，提示，是否更新
		Map<String, Object> transRouteInfo = transRouteInfoService
				.getTransRouteInfo(params);
		if (transRouteInfo == null || transRouteInfo.size() == 0) {
			outText("1", response);
			return;
		} else {
			String m = transRouteInfo.get("acq_merchant_no").toString();
			String t = transRouteInfo.get("acq_terminal_no").toString();

			if (acq_merchant_no.equals(m) && acq_terminal_no.equals(t)) {
				outText("-4", response);// 该关系已经绑定，无需重复绑定
				return;
			}

			outText("0", response);
			return;
		}

		// 如果方案不存在，执行插入操作

	}

	// 大套小绑定操作
	@RequestMapping(value = "/merchantBindSave")
	public void merchantBindSave(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		paramsTrim(params);
		transRouteInfoService.saveTransRouteInfo(params);
		outText("1", response);
	}

	// 详情查看操作
	@RequestMapping(value = "/merchantBindDetail")
	public String merchantBindDetail(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		paramsTrim(params);
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		String acq_merchant_no = params.get("acq_merchant_no");
		String acqTerminalNo = params.get("acq_terminal_no");
		Map<String, Object> acqMerchantDetail = acqMerchantService
				.getAcqMerchantDetail(acq_merchant_no, acqTerminalNo);
		Page<Map<String, Object>> list = transRouteInfoService
				.getTransRouteList(acqTerminalNo, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", acqMerchantDetail);
		return "/acq/bindDetail";
	}

	// 大套小解除
	@RequestMapping(value = "/bindRemove")
	public void bindRemove(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		Map<String, String> okMap = new HashMap<String, String>();
		int rowsuc = transRouteInfoService.agentInfoDel(params);
		if (rowsuc > 0) {
			okMap.put("msg", "OK");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}

	}

	@RequestMapping(value = "/acqCheck")
	public void acqCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		Map<String, String> okMap = new HashMap<String, String>();
		String id = params.get("id");
		String agent_no = params.get("agent_no");
		String acq_merchant_no = params.get("acq_merchant_no");
		String acq_merchant_name = params.get("acq_merchant_name");

		boolean acq_merchant_no_flag = false;
		boolean acq_merchant_name_flag = false;
		if (StringUtil.isBlank(id)) {
			acq_merchant_no_flag = acqMerchantService
					.getAcqMerByAcqMerchantNo(acq_merchant_no);
			acq_merchant_name_flag = acqMerchantService
					.getAcqMerByAcqMerchantName(acq_merchant_name);
		} else {
			acq_merchant_no_flag = acqMerchantService
					.getAcqMerByAcqMerchantNoModify(acq_merchant_no, id);
			acq_merchant_name_flag = acqMerchantService
					.getAcqMerByAcqMerchantNameModify(acq_merchant_name, id);
		}

		if (acq_merchant_no_flag) {
			okMap.put("msg", "existAcqMerchantNo");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			return;
		} else if (acq_merchant_name_flag) {
			okMap.put("msg", "existAcqMerchantName");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			return;
		}
		okMap.put("msg", "noexist");
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		return;
	}
	/**
	 * 检查商户是否已有所属机构
	 * @param request
	 * @param response
	 * @param params
	 */
	@RequestMapping(value = "/merCheck")
	public void merCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		paramsTrim(params);
		String id = params.get("id");
		Map<String, String> okMap = new HashMap<String, String>();
		String merchant_no = params.get("merchant_no");
		boolean mer_flag = false;
		if (StringUtil.isBlank(id)) {
			mer_flag = acqMerchantService
					.isMerExist(merchant_no);
		} else {
			mer_flag = acqMerchantService
					.isMerExistByModify(merchant_no, id);
		}
		if(mer_flag){//已存在
			okMap.put("msg", "exist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			System.out.println("exist");
			return;
		}
		System.out.println("noexits");
		okMap.put("msg", "noexist");
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
	}
	
	
	
	//修改锁定状态
	@RequestMapping(value = "/updateTerminalLocked")
	public String updateTerminalLocked(final ModelMap model,
		@RequestParam String id) throws SQLException {
		Map<String, Object> acqMerchant = acqMerchantService.getAcqMerchantLocked(id);
		model.put("params", acqMerchant);
		return "/acq/merchantLocked";
	}
	
	
	//修改收单商户编号
	@RequestMapping(value = "/acqLockedSave")
	public void acqLockedSave(final ModelMap model,
			@RequestParam Map<String, String> params,HttpServletRequest request,HttpServletResponse response) throws SQLException{
		paramsTrim(params);
		Map<String, String> okMap = new HashMap<String, String>();
		String locked=params.get("locked");
		String id=params.get("id");
		int num=acqMerchantService.updateTerminalLocked(locked, id);
		if(num>0){
			okMap.put("flag", "1");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}else{
			okMap.put("flag", "0");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}
		
	}
	
	// 收单商户导出
	@RequestMapping(value = "/merExport")
	public void merExport(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request) {
		log.info("MerchantController merExport strat ...");
		int row = 2; // 从第二行开始写
		int col = 0; // 从第一列开始写
		PageRequest page = new PageRequest(0, 79999);
		Page<Map<String, Object>>list = acqMerchantService.getAcqMerchant4Export(params, page);

		// 收单机构
		List<Map<String, Object>> acqOrgs = acqMerchantService.getAcqOrg();
		Map<String, String> acqOrg = new HashMap<String, String>();
		for(Map<String, Object> map : acqOrgs){
			acqOrg.put(map.get("acq_enname").toString(), map.get("acq_cnname").toString());
		}
		
		// 审核人
		List<Map<String, Object>> checkerList = acqMerchantService.getCheckerList();
		Map<String, String> checker = new HashMap<String, String>();
		for(Map<String, Object> map : checkerList){
			checker.put(map.get("user_name").toString(), map.get("real_name").toString());
		}
		
		OutputStream os = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);
			String fileName = "收单商户查询" + sdf.format(new Date()) + "_" + random + ".xls";
			
			request.setCharacterEncoding("UTF-8");
			os = response.getOutputStream(); // 取得输出流
			response.reset(); // 清空输出流
			response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
			
			Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/acqMerchant.xls"));
			WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
			WritableSheet ws = wwb.getSheet(0);
			
			for(Map<String, Object> map : list){

				ws.addCell(new Label(col++, row, row + ""));									//序号
				
				String acq = map.get("acq_enname").toString();
				String acqName = acqOrg.get(acq);
				ws.addCell(new Label(col++, row, acqName + "/" + acq));							//收单机构
				
				String acqMerchantNo = map.get("acq_merchant_no") != null ? map.get("acq_merchant_no").toString() : "";
				ws.addCell(new Label(col++, row, acqMerchantNo)); 								//收单机构商户编号
				
				String acqMerchantName = map.get("acq_merchant_name") != null ? map.get("acq_merchant_name").toString() : "";
				ws.addCell(new Label(col++, row, acqMerchantName)); 							//收单机构商户名称
				
				String agentNo = map.get("agent_no") != null ? map.get("agent_no").toString() : "";
				ws.addCell(new Label(col++, row, agentNo)); 									//代理商编号
				
				String agentName = map.get("agent_name") != null ? map.get("agent_name").toString() : "";
				ws.addCell(new Label(col++, row, agentName)); 									//代理商名称
				
				String province = map.get("province") != null ? map.get("province").toString() + "省" : "";
				String city = map.get("city") != null ? map.get("city").toString() + "市" : "";
				String address = map.get("address") != null ? map.get("address").toString() : "";
				ws.addCell(new Label(col++, row, province + city + address)); 					//经营地址
				
				String fee_type = "";
				String fee_rate = "";
				String fee_max_amount = "";
				String ladder_fee = "";
				String merchant_fee_type = "";
				String merchant_fee_rate = "";
				String merchant_fee_max_amount = "";
				String merchant_ladder_fee = "";
				if(map.get("fee_type") != null){
					if("RATIO".equals(map.get("fee_type").toString())){
						BigDecimal d = new BigDecimal(map.get("fee_rate").toString()).movePointRight(2);
						fee_rate = d + "%";
						fee_type="扣率";
					}else if("CAPPING".equals(map.get("fee_type").toString())){
						BigDecimal d = new BigDecimal(map.get("fee_rate").toString()).movePointRight(2);
						fee_rate = d + "%";
						if(null != map.get("fee_max_amount") && !"".equals(map.get("fee_max_amount").toString())){
							fee_max_amount = map.get("fee_max_amount").toString();
						}
						fee_type="封顶";
					}else if("LADDER".equals(map.get("fee_type").toString())){
						ladder_fee = map.get("ladder_fee") != null ? map.get("ladder_fee").toString() : "";
						
						String[] ladder = ladder_fee.split("<");
						if(ladder.length == 3){
							ladder_fee = new BigDecimal(ladder[0]).movePointRight(2) + "%<" + ladder[1] + "元<" + new BigDecimal(ladder[2]).movePointRight(2) + "%";
						}
						fee_type="阶梯";
					}
				}
				ws.addCell(new Label(col++, row, fee_type));
				ws.addCell(new Label(col++, row, fee_rate));
				ws.addCell(new Label(col++, row, fee_max_amount));
				ws.addCell(new Label(col++, row, ladder_fee));
				
				if(null != map.get("merchant_fee_type")){
					if("RATIO".equals(map.get("merchant_fee_type").toString())){
						BigDecimal d = new BigDecimal(map.get("merchant_fee_rate").toString()).movePointRight(2);
						merchant_fee_rate = d + "%";
						merchant_fee_type="扣率";
					}else if("CAPPING".equals(map.get("merchant_fee_type").toString())){
						BigDecimal d = new BigDecimal(map.get("merchant_fee_rate").toString()).movePointRight(2);
						merchant_fee_rate = d + "%";
						if(null != map.get("merchant_fee_max_amount") && !"".equals(map.get("merchant_fee_max_amount").toString())){
							merchant_fee_max_amount = map.get("merchant_fee_max_amount").toString();
						}
						merchant_fee_type="封顶";
					}else if("LADDER".equals(map.get("merchant_fee_type").toString())){

						merchant_ladder_fee = map.get("merchant_ladder_fee") != null ? map.get("merchant_ladder_fee").toString() : "";
						
						String[] merchant_ladder = merchant_ladder_fee.split("<");
						if(merchant_ladder.length == 3){
							merchant_ladder_fee = new BigDecimal(merchant_ladder[0]).movePointRight(2) + "%<" + merchant_ladder[1] + "元<" + new BigDecimal(merchant_ladder[2]).movePointRight(2) + "%";
						}
						
						merchant_fee_type="阶梯";
					}
				}
				
				ws.addCell(new Label(col++, row, merchant_fee_type));
				ws.addCell(new Label(col++, row, merchant_fee_rate));
				ws.addCell(new Label(col++, row, merchant_fee_max_amount));
				ws.addCell(new Label(col++, row, merchant_ladder_fee));
				
				String realFlag = map.get("real_flag") != null ? ("1".equals(map.get("real_flag").toString()) ? "是" : "否") : "";
				ws.addCell(new Label(col++, row, realFlag)); 									//是否实名
				
				String merchantNo = map.get("real_flag") != null && "1".equals(map.get("real_flag").toString()) && map.get("merchant_no") != null ? map.get("merchant_no").toString() : "";
				ws.addCell(new Label(col++, row, merchantNo)); 									//实名商户编号
				
				String merchantName = map.get("real_flag") != null && "1".equals(map.get("real_flag").toString()) && map.get("merchant_name") != null ? map.get("merchant_name").toString() : "";
				ws.addCell(new Label(col++, row, merchantName)); 								//实名商户名称
				
				String mySettle = map.get("my_settle") != null ? ("1".equals(map.get("my_settle").toString()) ? "是" : "否") : "";
				ws.addCell(new Label(col++, row, mySettle)); 									//是否优质商户
				
				String bagSettle = map.get("bag_settle") != null ? ("1".equals(map.get("bag_settle").toString()) ? "是" : "否") : "";
				ws.addCell(new Label(col++, row, bagSettle)); 									//是否钱包结算
				
				ws.addCell(new Label(col++, row, realFlag)); 									//资料是否齐全
				
				String locked = map.get("locked") != null && "0".equals(map.get("locked").toString()) ? "正常" : (map.get("locked") != null && "1".equals(map.get("locked").toString()) ? "锁定" : "废弃");
				ws.addCell(new Label(col++, row, locked)); 										//锁定状态
				
				String creatTime = map.get("create_time") != null ? map.get("create_time").toString() : "";
				ws.addCell(new Label(col++, row, creatTime)); 									//创建时间
				
				row++;
				col = 0;
			}
			wwb.write();
			wwb.close();
			wb.close();
			os.close();
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
		log.info("MerchantController merExport End");
	}

	@RequestMapping(value = "/checkPosMerchant")
	public void checkPosMerchantInAcqMerchant(@RequestParam Long id,HttpServletRequest request,HttpServletResponse response){

		try{
			Map<String, Object> map = acqMerchantService.getPosMerchantWithAcqMerchant(id);

			String json = JSONObject.fromObject(map).toString();
			outJson(json, response);

		}catch (Exception e){
			outText("error",response);
		}
	}
	
	
}
