package com.eeepay.boss.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.eeepay.boss.service.VersionService;


@Controller
@RequestMapping(value = "/ver")
public class VersionController extends BaseController {
	@Resource
	private VersionService versionService;
	
	//查询版本控制信息
	@RequestMapping(value = "/verQuery")
	public String userAuthQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = versionService.getAgentVersionList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/version/verQuery";
	}
	
	
	@RequestMapping(value = "/verInput")
	public String userAuthInput(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		if(id!=null&&id.length()!=0){
			Map<String,Object> blackMap = versionService.getAgentVersionById(Long.parseLong(id));
			model.put("params", blackMap);
		}
		return "/version/verInput";
	}
	
	
	//添加或是更新操作
	@RequestMapping(value = "/verSave")
	public String blackSave(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException{
		String id=params.get("id");
		if(id==null||id.length()==0){
			try {
				versionService.agentVersionSave(params);
				model.put("flag", "1");
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
		}else{
			try {
				versionService.agentVersionModify(params);   //修改版本信息
				model.put("flag", "2");
				model.put("params", params);
			}catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
		}
		return "/version/verInput";
	}
	
	//查询版本详情
	@RequestMapping(value = "/verDetail")
	public String blackDetail(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		Map<String,Object> blackMap = versionService.getAgentVersionById(Long.parseLong(id));
		model.put("params", blackMap);
		return "/version/verDetail";
	}
	
	
	//删除版本信息
	@RequestMapping(value = "/verDel")
	public void blackDel(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception{
		
		Map<String, String> okMap = new HashMap<String, String>();
		String id=params.get("id");
		try{
			versionService.deleteAgentVersion(Long.parseLong(id));
			okMap.put("msg", "OK");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "/verCheck")
	public void agentNameCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		Map<String, String> okMap = new HashMap<String, String>();
		String id = params.get("id");
		String version = params.get("version");
		String platform = params.get("platform");
		
		Long count = null;
		if(id == null || id.trim().length() == 0){  //添加时的验证
			Map<String, Object> map = versionService.checkAddVersion(version, platform);
			count = (Long) map.get("count");
		}else{   //修改时的验证
			Map<String, Object> map = versionService.checkModVersion(version, platform, id);
		    count = (Long) map.get("count");
		}
		if(count.intValue()> 0){
			okMap.put("msg", "exist");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			return;
		}
		okMap.put("msg", "noexist");
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
		return;
	}	
}
