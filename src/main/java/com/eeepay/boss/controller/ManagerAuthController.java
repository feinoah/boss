package com.eeepay.boss.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.AuthManagerService;
import com.eeepay.boss.service.LogService;
/**
 * 权限信息
 * @author hdb
 */
@Controller
@RequestMapping(value = "/auth")
public class ManagerAuthController extends BaseController {

	@Resource
	private AuthManagerService authManagerService;
	@Resource
	private LogService logService;
	//查询权限
	@RequestMapping(value = "/userAuthQuery")
	public String userAuthQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = authManagerService.getAuthList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		List<Map<String,Object>> authList = authManagerService.getParentAuthListAll();
		model.put("authList", authList);
		return "/auth/userAuthQuery";
	}
	
	//删除权限
	@RequestMapping(value = "/userAuthDel")
	public void userAuthDel(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException{
		boolean flag = authManagerService.checkAuthCanDel(params);
		if(flag){
			int rowsuc = authManagerService.bossAuthDel(params);
			logService.saveOperateLog("删除权限");
			if (rowsuc > 0) {
				outText("1",  response);
			}else{
				outText("2",  response);
			}
		}else{
			outText("0",  response);
		}
	}
	 
	//新增
	@RequestMapping(value = "/userAuthInput")
	public String userAuthInput(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params){
		String id=params.get("id");
		if(id==null||id.length()==0){
			List<Map<String,Object>> authList = authManagerService.getParentAuthList();
			model.put("authList", authList);
		}else{
			
			Map<String,Object> authMap = authManagerService.getAuthMap(Long.parseLong(id));
			model.put("params", authMap);
			
			List<Map<String,Object>> authList = authManagerService.getParentAuthList();
			model.put("authList", authList);
		}
		return "/auth/userAuthInput";
	}
	
	//新增
	@RequestMapping(value = "/userAuthSave")
	public String userAuthSave(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException{
		String id=params.get("id");
		if(id==null||id.length()==0){
			String parentID=params.get("parent_id");
			boolean fg=authManagerService.chechedAuthCode(params.get("auth_code"));
			boolean flag=authManagerService.chechedParent(Long.parseLong(parentID));
			if(flag&&!fg){
				authManagerService.userAuthSave(params);
				logService.saveOperateLog("增加权限");
				model.put("flag", "1");
			}else if(fg){
				model.put("flag", "4");//auth_code已经存在
			}else{
				model.put("flag", "3");
			}
			List<Map<String,Object>> authList = authManagerService.getParentAuthList();
			model.put("authList", authList);
		}else{
			authManagerService.userAuthUpdate(params);
			logService.saveOperateLog("修改权限");
			model.put("flag", "2");
			List<Map<String,Object>> authList = authManagerService.getParentAuthList();
			model.put("authList", authList);
		}
		
		return "/auth/userAuthInput";
	}
	
}
