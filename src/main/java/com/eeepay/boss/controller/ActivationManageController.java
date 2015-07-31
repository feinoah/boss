package com.eeepay.boss.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.ActivationManageService;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.MD5;


/**
 * 激活码管理和验证
 * @author LJ
 */
@Controller
@RequestMapping(value = "/activation")
public class ActivationManageController  extends BaseController{


	@Resource
	private  ActivationManageService activationService;
	
	
	// 激活码查询
	@RequestMapping(value = "/activationQuery")
	public String activationQuery(final ModelMap model, @RequestParam Map<String, String> params, 
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			if ((params.get("createTimeBegin") == null || "".equals(params.get("createTimeBegin"))) && (params.get("createTimeEnd") == null || "".equals(params.get("createTimeEnd")))) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);

				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime + " 23:59:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}
			Page<Map<String, Object>> list = activationService.actQuery(params,page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			
			return "/activation/activationManageQuery";
		}
	
	
	//跳到生成激活码的页面
	@RequestMapping(value = "/viewActivationProduce")
	public String viewActivationProduce(final ModelMap model)
			throws Exception {
		return "activation/activationProduce";
	}
	
	

	//生成激活码
	@RequestMapping(value = "/activationProduce")
	public void activationProduce(final ModelMap model,
			@RequestParam int amount, HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		boolean falg = false;
		System.out.println(amount);
		try {
				//生成批次号
				String batch =GenSyncNo.getInstance().getNextPosAllotNo();   
				for (int i = 0; i<amount; i++) {
					String uuid = UUID.randomUUID().toString();
					uuid = MD5.toMD5(uuid).toUpperCase();
					uuid = uuid.substring(0, 20);
					System.out.println(uuid);
					activationService.produceAct(uuid,batch);
					falg = true;
				}     
				if (falg){
				   response.getWriter().write("1");
				} else{
					response.getWriter().write("0");
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	//跳到生成激活码的页面
	@RequestMapping(value = "/stateLoad")
	public String stateLoad(final ModelMap model,@RequestParam  String  keycode)
			throws Exception {
		Map<String, Object> activationMap = activationService.activationQueryById(keycode);
		
		model.put("params", activationMap);
		return "activation/stateInput";
	}
	
	//修改激活码状态
	@RequestMapping(value = "/activationStateSave")
	public void activationStateSave(final ModelMap model,
			@RequestParam Map<String, String> params,HttpServletRequest request,HttpServletResponse response) throws SQLException{
		Map<String, String> okMap = new HashMap<String, String>();
		String state=params.get("state");
		String keycode=params.get("keycode");
		int num=activationService.updateActivationLocked(state,getUser().getUserName(),keycode);
		

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
	
	    //判断激活码是否存在
		@RequestMapping(value = "/findActivation")
		public void findActivation(final ModelMap model,
				@RequestParam String keycode,HttpServletRequest request,HttpServletResponse response) throws SQLException{
			Map<String, String> okMap = new HashMap<String, String>();
			Map<String, Object>list=activationService.findActivation(keycode);
			if(list.size()>0 && list!=null){
				okMap.put("flag", "1");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}else{
				okMap.put("flag", "0");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
			
		}
	
	
}
