package com.eeepay.boss.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.MerchantCheckPersonService;
import com.eeepay.boss.service.UserGroupService;
import com.eeepay.boss.utils.SysConfig;


/**
 * 商户审核人管理控制类* 用于管理商户审核人信息
 * @author 王帅
 * @see *注明：本类中每一个方法必须要有注释以及日志信息，禁止出现吃异常现象，以便于后期问题定位以及维护工作提供良好的基础。
 */
@Controller
@RequestMapping(value = "/merCP")
public class MerchantCheckPersonController extends BaseController {
	
	@Resource
	private UserGroupService userGroupService;
	
	@Resource
	private MerchantCheckPersonService merchantCheckPersonService;
	
	//日志
	private static final Logger log = LoggerFactory.getLogger(MerchantCheckPersonController.class);
	
	//主目录路径
	private  final String path = "/merchantCheckManage/";
	
	/**
	 * 删除商户审核人信息
	 * @author 王帅
	 * @param params 所属ID编号
	 * @return
	 */
	@RequestMapping(value = "/removeMerchantCheckStatusById")
	public void removeMerchantCheckStatusById(HttpServletResponse response,@RequestParam Map<String, String> params){
		log.info("MerchantCheckPersonController removeMerchantCheckStatusById start ...");
		JSONObject json = new JSONObject();
		int removeCount = 0;
		try {
			removeCount = merchantCheckPersonService.removeMerchantCheckStatusById(Integer.parseInt(params.get("id")));
			if(removeCount > 0){
				log.info("MerchantCheckPersonController merchantCheckAddSave SUCCESS = "+removeCount);
			}
		} catch (Exception e) {
			log.error("MerchantCheckPersonController removeMerchantCheckStatusById Exception = "+e);
			e.printStackTrace();
		}
		log.info("MerchantCheckPersonController removeMerchantCheckStatusById End");
		json.put("removeCount", removeCount);
		outJson(json.toString(), response);
	}
	
	/**
	 * 开启商户审核人信息
	 * @author 王帅
	 * @param params 所属ID编号
	 */
	@RequestMapping(value = "/openMerchantCheckStatusById")
	public void openMerchantCheckStatusById(HttpServletResponse response, @RequestParam Map<String, String> params){
		log.info("MerchantCheckPersonController openMerchantCheckStatusById start ...");
		JSONObject json = new JSONObject();
		int openCount = 0;
		try {
			openCount = merchantCheckPersonService.openMerchantCheckStatusById(Integer.parseInt(params.get("id")));
			if(openCount > 0){
				log.info("MerchantCheckPersonController merchantCheckAddSave SUCCESS = "+openCount);
			}
		} catch (Exception e) {
			log.error("MerchantCheckPersonController openMerchantCheckStatusById Exception = "+e);
			e.printStackTrace();
			json.put("removeCount", openCount);
			outJson(json.toString(), response);
		}
		log.info("MerchantCheckPersonController openMerchantCheckStatusById End");
		json.put("removeCount", openCount);
		outJson(json.toString(), response);
	}

	/**
	 * 关闭商户审核人信息
	 * @author 王帅
	 * @param params 所属ID编号
	 * @return
	 */
	@RequestMapping(value = "/closeMerchantCheckStatusById")
	public void closeMerchantCheckStatusById(HttpServletResponse response,@RequestParam Map<String, String> params){
		log.info("MerchantCheckPersonController closeMerchantCheckStatusById start ...");
		JSONObject json = new JSONObject();
		int closeCount = 0;
		try {
			closeCount = merchantCheckPersonService.closeMerchantCheckStatusById(Integer.parseInt(params.get("id")));
			if(closeCount > 0){
				log.info("MerchantCheckPersonController closeMerchantCheckStatusById SUCCESS = "+closeCount);
			}
		} catch (Exception e) {
			log.error("MerchantCheckPersonController closeMerchantCheckStatusById Exception = "+e);
			e.printStackTrace();
			json.put("removeCount", closeCount);
			outJson(json.toString(), response);
		}
		log.info("MerchantCheckPersonController closeMerchantCheckStatusById End");
		json.put("removeCount", closeCount);
		outJson(json.toString(), response);
	}
	
	/**
	 * 查询审核人管理信息方法
	 * @author 王帅
	 * @param model 带分页信息的结果集以及查询条件
	 * @param params 查询参数信息
	 * @param cpage 分页信息
	 * @return 跳转到商户审核人管理查询界面
	 */
	@RequestMapping(value = "/queryMerchantCheckInfo")
	public String queryMerchantCheckInfo(final ModelMap model,	@RequestParam Map<String, String> params,	@RequestParam(value = "p", defaultValue = "1") int cpage) {
		log.info("MerchantCheckPersonController queryMerchantCheckInfo start ...");
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		//加载客服组所有状态为正常使用的成员信息
		List<Map<String, Object>> customServiceList = userGroupService.getUserToGroupList();//getUserToGroupList(2,48);
		Page<Map<String, Object>> list = merchantCheckPersonService.getMerchantCheckPersonList(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("customServiceList", customServiceList);
		model.put("params", params);
		log.info("MerchantCheckPersonController queryMerchantCheckInfo End");
		return path+"merchantCheckManage";
	}
	
	
	/**
	 * 进入审核人添加界面
	 * @author 王帅
	 * @param model 界面需要加载的数据信息
	 * @return 跳转到商户审核人添加界面
	 */
	@RequestMapping(value = "/merchantCheckAdd")
	public String merchantCheckAdd(final ModelMap model) {
		log.info("MerchantCheckPersonController merchantCheckAdd start ...");
		//加载客服组所有状态为正常使用的成员信息
		List<Map<String, Object>> customServiceList = userGroupService.getUserToGroupList();
		

		//model.put("p", cpage);
		model.put("customServiceList", customServiceList);
		log.info("MerchantCheckPersonController merchantCheckAdd End");
		return path+"merchantCheckAdd";
	}
	
	/**
	 * 保存添加审核人信息
	 * @author 王帅
	 * @param params 审核人参数信息
	 * @param model 界面需要加载的数据信息
	 * @return 跳转到商户审核人添加界面
	 */
	@RequestMapping(value = "/merchantCheckAddSave")
	public String merchantCheckAddSave(final ModelMap model, @RequestParam Map<String, String> params) {
		log.info("MerchantCheckPersonController merchantCheckAddSave start ...");
		//加载客服组所有状态为正常使用的成员信息  create_person
		List<Map<String, Object>> customServiceList = userGroupService.getUserToGroupList();
		try {
			//添加商户审核人信息  返回添加条数
			params.put("create_person", getUser().getId().toString());
			int addCount = merchantCheckPersonService.addMerchantCheckPersonInfo(params); 
			if(addCount > 0){
				log.info("MerchantCheckPersonController merchantCheckAddSave SUCCESS = "+addCount);
			}
		} catch (SQLException se) {
			log.error("MerchantCheckPersonController merchantCheckAddSave SQLException = " + se);
			se.printStackTrace();
		}catch (Exception e) {
			log.error("MerchantCheckPersonController merchantCheckAddSave Exception = " + e);
			e.printStackTrace();
		}
		//model.put("p", cpage);
		model.put("customServiceList", customServiceList);
		log.info("MerchantCheckPersonController merchantCheckAddSave End");
		return path+"merchantCheckAdd";
	}
	
	
	/**
	 * 进入审核人修改界面
	 * @author 王帅
	 * @param params 修改参数信息
	 * @param model 界面需要加载的数据信息
	 * @return 跳转到商户审核人修改界面
	 */
	@RequestMapping(value = "/merchantCheckModify")
	public String merchantCheckModify(final ModelMap model,	@RequestParam Map<String, String> params) {
		log.info("MerchantCheckPersonController merchantCheckModify start ...");
		//加载客服组所有状态为正常使用的成员信息
		List<Map<String, Object>> customServiceList = userGroupService.getUserToGroupList();
		Map<String, Object>  checkPersonInfo = merchantCheckPersonService.getMerchantCheckInfo(Integer.parseInt(params.get("id").toString()));

		model.put("checkPersonInfo", checkPersonInfo);
		model.put("customServiceList", customServiceList);
		log.info("MerchantCheckPersonController merchantCheckModify End");
		return path+"merchantCheckModify";
	}
	
	/**
	 *保存修改审核人信息
	 * @author 王帅
	 * @param params 修改参数信息
	 * @param model 界面需要加载的数据信息
	 */
	@RequestMapping(value = "/merchantCheckModifySave")
	public void merchantCheckModifySave(HttpServletResponse response,final ModelMap model,	@RequestParam Map<String, String> params) {
		log.info("MerchantCheckPersonController merchantCheckModifySave start ...");
		JSONObject json = new JSONObject();
		int modifyCount = 0;
		try {
			modifyCount = merchantCheckPersonService.modifyMerchantCheckInfoById(params);
			if(modifyCount > 0){
				log.info("MerchantCheckPersonController merchantCheckModifySave SUCCESS = "+modifyCount);
			}
		} catch (Exception e) {
			log.error("MerchantCheckPersonController merchantCheckModifySave Exception = " + e);
			e.printStackTrace();
			json.put("removeCount", modifyCount);
			outJson(json.toString(), response);
		}

		log.info("MerchantCheckPersonController merchantCheckModifySave End");
		json.put("removeCount", modifyCount);
		outJson(json.toString(), response);
	}
	
}
