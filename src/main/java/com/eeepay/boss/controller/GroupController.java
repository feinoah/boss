package com.eeepay.boss.controller;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eeepay.boss.domain.SmsBean;
import com.eeepay.boss.utils.SmsUtil;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.service.AcqMerchantService;
import com.eeepay.boss.service.GroupAcqMerchantService;
import com.eeepay.boss.service.GroupMerchantService;
import com.eeepay.boss.service.GroupService;
import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.PosTypeService;
import com.eeepay.boss.service.TerminalService;
import com.eeepay.boss.service.UserGroupService;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.SysConfig;

/**
 * 商户管理
 * 
 * @author zxm
 * 
 */
@Controller
@RequestMapping(value = "/group")
public class GroupController extends BaseController {

	@Resource
	private GroupService groupService;

	@Resource
	private GroupMerchantService groupMerchantService;

	@Resource
	private GroupAcqMerchantService groupAcqMerchantService;
	
	@Resource
	private AcqMerchantService acqMerchantService;
	
	@Resource
	private UserGroupService userGroupService;
	
	@Resource
	private MerchantService  merchantService;
	
	@Resource
	private TerminalService terminalService;
	
	@Resource
	private PosTypeService posTypeService; 
	
	private static final Logger log = LoggerFactory.getLogger(GroupController.class);
	
	private String posTypeName(String pos_type){
		log.info("MerchantController posTypeName START");
		String posTypeName = pos_type;
		Map<String, Object> posTypeMap =  posTypeService.getPosType(pos_type);
		if(posTypeMap != null && posTypeMap.size() > 0){
			if(posTypeMap.get("pos_type_name") != null && !"".equals(posTypeMap.get("pos_type_name").toString())){
				posTypeName = posTypeMap.get("pos_type_name").toString();
			}
		}
		log.info("MerchantController posTypeName End");
		return posTypeName;
	}

	// 查询
	@RequestMapping(value = "/query")
	public String query(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		BossUser bu = getUser();
		String realName = bu.getRealName();
		String specialName = SysConfig.value("specialNameViewRoute");//特殊集群特殊人员查看的人员姓名
		String specialCode = SysConfig.value("specialViewRouteCode");//特殊集群特殊人员查看的集群编号
		if(specialName != null && specialName.equals(realName)){
			params.put("specialName", "true");
		}
		if(specialCode != null){
			params.put("specialCode", specialCode);
		}
		
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = groupService.getGroups(params, page); 
		List<Map<String, Object>> acqOrgList = acqMerchantService	.getMerchantList(); //得到收单机构信息
		List<Map<String,Object>> salesList = userGroupService.getUserToGroupList(16); //得到销售信息
		model.put("p", cpage);
		model.put("list", list);
		model.put("acqOrgList", acqOrgList);
		model.put("salesList", salesList);
		// model.put("totalMsg", totalMsg);
		model.put("params", params);
		return "/group/groupQuery";
	}

	// updateInput
	@RequestMapping(value = "/updateInput")
	public String updateInput(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		String id = (String) params.get("id");
		String group_code = (String) params.get("group_code");
		String group_name = (String) params.get("group_name");

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		params.put("group_code", group_code);
		params.put("group_name", group_name);
		params.put("id", id);

		Page<Map<String, Object>> list = groupService.getGroups(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/group/groupUpdateInput";
	}

			/**
			 * 根据ID 获取单个集群信息
			 * @param model
			 * @param params
			 * @param id 集群ID
			 * @return params：与ID对应的集群信息
			 */
			@RequestMapping(value = "/getGroupInfoById")
			public String getGroupInfoById(final ModelMap model,@RequestParam Map<String, String> params,
					@RequestParam String  id) {
				Map<String, Object> map = groupService.getGroupsById(id);
				List<Map<String, Object>> acqOrgList = acqMerchantService
						.getMerchantList();
				List<Map<String,Object>> salesList = userGroupService.getUserToGroupList(16);
				model.put("params", map);
				model.put("acqOrgList", acqOrgList);
				model.put("salesList", salesList);
				return "/group/groupUpdateInput";
			}
	
	    //修改时-查询
		@RequestMapping(value = "/updateGroupInput")
		public String updateGroupInput(final ModelMap model,@RequestParam Map<String, String> params,
				@RequestParam String  id,@RequestParam(value = "p", defaultValue = "1") int cpage) {
			
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			Page<Map<String, Object>> list = groupService.getGroupsById(id, page);
			model.put("p", cpage);
			model.put("list", list);
			return "/group/groupUpdateInput";
		}
	
		//删除集群信息
		@RequestMapping(value = "/removeGroup")
		public void removeGroup(HttpServletRequest request,
				HttpServletResponse response,
				@RequestParam Map<String, String> params) {
			String msg = "ERROR";
			Map<String, String> okMap = new HashMap<String, String>();
			try {
				String id = params.get("id");
				String group_code = params.get("group_code");
				if(null != id && null != group_code &&  !"".equals(id) && !"".equals(group_code)){
					//验证是否存在普通商户
					int gmerchantCount = groupMerchantService.getGroupsMerchantByCode(params);
					if(gmerchantCount == 0){
						//验证是否有收单商户
						int acqMerchantCount  = groupAcqMerchantService.getAcqMerchantByCode(params);
						if(acqMerchantCount == 0){
							//执行删除集群操作
							int remCount = groupService.removeGroupByIdAndCode(Integer.parseInt(id), Integer.parseInt(group_code));
							if(remCount > 0){
								msg = "OK";
							}
						}else{
							msg = "2";
						}
					}else{
						msg = "1";
					}
				}
				okMap.put("msg", msg);
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			} catch (Exception e) {
				okMap.put("msg", "ERROR");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				e.printStackTrace();
			}
		}
	
	
	//修改保存
	@RequestMapping(value = "/updateSubmit")
	public void updateSubmit(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		String msg = "ERROR";
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			String id = params.get("id");
			String acq_enname = params.get("acq_enname"); //集群所属收单机构
			String acq_name = params.get("acq_name"); //集群所属收单机构名称
			String agent_no = params.get("agent_no"); //集群所属所属代理商
			String agent_Name = params.get("agent_Name"); //所属代理商名称
			String accounts_period = params.get("accounts_period");  //集群结算周期
			String sales = params.get("sales"); //集群所属销售
			String sales_name = params.get("sales_name"); //集群所属销售名称
			String route_type = params.get("route_type");  //集群所属类型
			String merchant_type = params.get("merchant_type");  //集群所属商户类型
			String merchant_typeName = params.get("merchant_typeName");  //集群所属商户类型名称
			String route_last_name = params.get("route_last_name"); //集群后缀
			String province = params.get("province"); //集群所属省份
			if(null != id && null != acq_enname && null != acq_name &&  null != accounts_period  && 
					null != route_type && null != merchant_type && null != merchant_typeName){
				if(!"".equals(id) && !"".equals(acq_enname) && !"".equals(acq_name) && !"".equals(accounts_period)  && 
						!"".equals(route_type) && !"".equals(merchant_type) && !"".equals(merchant_typeName)){
					//集群名称规则：收单机构-结算周期-所属销售(根据集群类型决定存在与否)-所属代理商-后缀(本参数有即有，无则无)
					//组合集群名称
					String routeName = acq_name+"-T+"+accounts_period+"-"+merchant_typeName;
					if(null != sales && null != sales_name &&  !"".equals(sales) && !"".equals(sales_name)){ //所属销售
						routeName += "-"+sales_name;
					}
					
					if(null != agent_no && null != agent_Name && !"".equals(agent_no)  && !"".equals(agent_Name)   && !"全部".equals(agent_Name)){
						routeName += "-"+agent_Name; //加上所属代理
					}
					
					if(null != route_last_name && !"".equals(route_last_name)){ //集群后缀
						routeName += "-"+route_last_name; 
					}
					
					if(null != province  &&  !"".equals(province)){
						routeName += "("+province+")";
					}
					
					List<Map<String,Object>> groupList = groupService.existGroupInfo(Integer.parseInt(id), routeName); //检索集群名是否已存在
					if(groupList != null &&  groupList.size() > 0)
					{
						msg = "nameExist"; //集群名称已存在
					}else{
						params.put("group_name", routeName); // 生成集群名称
						int rowsuc = groupService.modifyGroupInfo(params);
						if (rowsuc > 0) {
							msg="OK";
						}
					}
					
				}
			}
			
			/*List<Map<String,Object>> groupList = groupService.groupNameQuery(group_name,id);
			List<Map<String,Object>> groupnameList = groupService.groupQuery(group_name);
			if(groupList == null || groupList.size() == 0)
			{
				if(groupnameList !=null && groupnameList.size()>0){
					okMap.put("msg", "nameExist");
					String json = JSONObject.fromObject(okMap).toString();
					outJson(json, response);
					return;
				}
			}*/
			
			/*int rowsuc = groupService.updateSubmit(params);
			if (rowsuc > 0) {
				msg="OK";
			}*/
			okMap.put("msg", msg);
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}

	
	
	// 新增input
	@RequestMapping(value = "/add")
	public String add(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		List<Map<String, Object>> acqOrgList = acqMerchantService
				.getMerchantList();
		List<Map<String,Object>> salesList = userGroupService.getUserToGroupList(16);
		model.put("salesList", salesList);
		model.put("acqOrgList", acqOrgList);
		return "/group/groupAdd";
	}

	
	
	// 新增 addSubmit
	@RequestMapping(value = "/addSubmit")
	public void addSubmit(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		Map<String, String> okMap = new HashMap<String, String>();
		String msg = "ERROR";
		try {
			String acq_enname = params.get("acq_enname"); //集群所属收单机构
			String acq_name = params.get("acq_name"); //集群所属收单机构名称
			String agent_no = params.get("agent_no"); //集群所属所属代理商
			String agent_Name = params.get("agent_Name"); //所属代理商名称
			String accounts_period = params.get("accounts_period");  //集群结算周期
			String sales = params.get("sales"); //集群所属销售
			String sales_name = params.get("sales_name"); //集群所属销售名称
			String route_type = params.get("route_type");  //集群所属类型
			String merchant_type = params.get("merchant_type");  //集群所属商户类型
			String merchant_typeName = params.get("merchant_typeName");  //集群所属商户类型名称
			String route_last_name = params.get("route_last_name"); //集群后缀
			String province = params.get("province"); //集群所属省份
			String my_settle = params.get("my_settle"); //是否优质
			String bag_settle = params.get("bag_settle"); //是否优质
			if(null != acq_enname && null != acq_name && null != accounts_period  && 
					null != route_type && null != merchant_type &&  null != merchant_typeName && null != my_settle && null != bag_settle){
				if(!"".equals(acq_enname) && !"".equals(acq_name) && !"".equals(accounts_period)  && 
						!"".equals(route_type) && !"".equals(merchant_type) && !"".equals(merchant_typeName) && !"".equals(my_settle) && !"".equals(bag_settle)){
					//集群名称规则：收单机构-结算周期-所属销售(根据集群类型决定存在与否)-所属代理商-后缀(本参数有即有，无则无)
							//组合集群名称
							String routeName = acq_name+"-T+"+accounts_period+"-"+merchant_typeName;
							if(null != sales && null != sales_name &&  !"".equals(sales) && !"".equals(sales_name)){ //所属销售
								routeName += "-"+sales_name;
							}
							
							if(null != agent_no && null != agent_Name && !"".equals(agent_no) &&  !"全部".equals(agent_Name)){
								routeName += "-"+agent_Name;
							}
							
							if(null != route_last_name && !"".equals(route_last_name)){ //集群后缀
								routeName += "-"+route_last_name; 
							}
							
							if(null != province &&  !"".equals(province)){
								routeName += "("+province+")";
							}
							
							List<Map<String,Object>> groupList = groupService.groupQuery(routeName); //检索集群名是否已存在
							if(groupList != null &&  groupList.size() > 0)
							{
								msg = "nameExist"; //集群名称已存在
							}else{
								Long nextGroupCode = Long.parseLong(GenSyncNo.getInstance().getGroupCodeSeq()); //获取当前集群最大编号数
								if(nextGroupCode > 0){
									params.put("group_code", String.valueOf(nextGroupCode)); // 生成集群编号
									params.put("group_name", routeName); // 生成集群名称
									
									int rowsuc = groupService.addGroupInfo(params);
									if (rowsuc > 0) {
										msg="OK";
									}
								}
							}
				}
			}
			
			okMap.put("msg", msg);
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			
			/*List<Map<String,Object>> groupList = groupService.groupQuery(group_name);
			if(groupList != null &&  groupList.size() > 0)
			{
				okMap.put("msg", "nameExist");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}
			
			String group_code=params.get("group_code"); //现在没有   需要改成自动生成
			List<Map<String,Object>> groupCodeList = groupService.groupCodeQuery(group_code);
			if(groupCodeList != null &&  groupCodeList.size() > 0)
			{
				okMap.put("msg", "codeExist");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}
			
			
			int rowsuc = groupService.addSubmit(params);
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}*/
		} catch (Exception e) {
			log.error("添加集群出现异常 Exception = " + e.getMessage());
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}
	
	
	
	
	@RequestMapping(value = "/del")
	public String del(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		System.out.println("group ................");
		String ids = (String) params.get("ids");

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		return "/group/groupDel";
	}
	
	/**
	 * 集群收单商户导出Excel
	 * @author swang
	 * @param params 集群编号
	 */
	@RequestMapping(value = "/exportGroupAcqMerchant")
	public void exportGroupAcqMerchant(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		//getGroupMerchantByCode
		String group_code = params.get("group_code");
		String acq_enname = params.get("acq_enname");
		String acq_merchant = params.get("acq_merchant");
		if((null != group_code && !"".equals(group_code)) || (null != acq_enname && !"".equals(acq_enname)) || (null != acq_merchant && !"".equals(acq_merchant))){
			List<Map<String, Object>> gMerchantList = groupAcqMerchantService.getGroupAcqMerchantListByCode(params);
			if(gMerchantList.size() > 0){
				int row = 2; // 从第三行开始写
				int col = 0; // 从第一列开始写
				String fileName = gMerchantList.get(0).get("group_name").toString()+"-集群收单商户(共计  "+gMerchantList.size()+" 条).xls";
				OutputStream os = null;
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename="
						+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				
				Workbook wb =  Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/GroupAcqMerchant.xls"));
				WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
				WritableSheet ws = wwb.getSheet(0);
				 
				WritableFont titleFont = new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD);   
				WritableCellFormat cellFormatTitle = new WritableCellFormat(titleFont);
				cellFormatTitle.setAlignment(Alignment.CENTRE);
				cellFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
				WritableCellFormat cellFormat = new WritableCellFormat();
				cellFormat.setAlignment(Alignment.RIGHT);
				for (int i = 0; i < gMerchantList.size(); i++) {
					String pos_type = "";
					String address = "";
					String fee_type = "";
					String fee_rate = "";
					String fee_max_amount = "";
					String merchant_fee_type = "";
					String merchant_fee_rate = "";
					String merchant_fee_max_amount = "";
					String my_settle = "";
					String real_flag = "";
					String bag_settle = "";
					ws.addCell(new Label(col++, row, String.valueOf(i+1)));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("acq_merchant_no").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_code").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("acq_cnname").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("agent_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("province").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("city").toString()));
					if(null != gMerchantList.get(i).get("address") && !"".equals(gMerchantList.get(i).get("address").toString())){
						address = gMerchantList.get(i).get("address").toString();
					}
					ws.addCell(new Label(col++, row, address));
					if(null != gMerchantList.get(i).get("pos_type") && !"".equals(gMerchantList.get(i).get("pos_type").toString())){
						pos_type = posTypeName(gMerchantList.get(i).get("pos_type").toString());
					}
					ws.addCell(new Label(col++, row, pos_type));
					if(null != gMerchantList.get(i).get("fee_type") && !"".equals(gMerchantList.get(i).get("fee_type").toString())){
						if("RATIO".equals(gMerchantList.get(i).get("fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							fee_rate = f + "%";
							fee_type="扣率";
						}else if("CAPPING".equals(gMerchantList.get(i).get("fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							fee_rate = f + "%";
							if(null != gMerchantList.get(i).get("fee_max_amount") && !"".equals(gMerchantList.get(i).get("fee_max_amount").toString())){
								fee_max_amount = gMerchantList.get(i).get("fee_max_amount").toString();
							}
							fee_type="封顶";
						}else{
							fee_type=gMerchantList.get(i).get("fee_type").toString();
						}
					}
					ws.addCell(new Label(col++, row, fee_type));
					ws.addCell(new Label(col++, row, fee_rate));
					ws.addCell(new Label(col++, row, fee_max_amount));//封顶手续费
					if(null != gMerchantList.get(i).get("merchant_fee_type") && !"".equals(gMerchantList.get(i).get("merchant_fee_type").toString())){
						if("RATIO".equals(gMerchantList.get(i).get("merchant_fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("merchant_fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							merchant_fee_rate = f + "%";
							merchant_fee_type="扣率";
						}else if("CAPPING".equals(gMerchantList.get(i).get("merchant_fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("merchant_fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							merchant_fee_rate = f + "%";
							if(null != gMerchantList.get(i).get("merchant_fee_max_amount") && !"".equals(gMerchantList.get(i).get("merchant_fee_max_amount").toString())){
								merchant_fee_max_amount = gMerchantList.get(i).get("merchant_fee_max_amount").toString();
							}
							merchant_fee_type="封顶";
						}else{
							merchant_fee_type=gMerchantList.get(i).get("merchant_fee_type").toString();
						}
						
					}
					ws.addCell(new Label(col++, row, merchant_fee_type));//阶梯
					ws.addCell(new Label(col++, row, merchant_fee_rate));//
					ws.addCell(new Label(col++, row, merchant_fee_max_amount));
					if(null != gMerchantList.get(i).get("bag_settle") && !"".equals(gMerchantList.get(i).get("bag_settle").toString())){
						if("0".equals(gMerchantList.get(i).get("bag_settle").toString())){
							bag_settle  = "否";
						}else if("1".equals(gMerchantList.get(i).get("bag_settle").toString())){
							bag_settle  = "是";
						}else{
							bag_settle  = gMerchantList.get(i).get("bag_settle").toString();
						}
					}
					ws.addCell(new Label(col++, row, bag_settle));
					if(null != gMerchantList.get(i).get("my_settle") && !"".equals(gMerchantList.get(i).get("my_settle").toString())){
						if("0".equals(gMerchantList.get(i).get("my_settle").toString())){
							my_settle  = "否";
						}else if("1".equals(gMerchantList.get(i).get("my_settle").toString())){
							my_settle  = "是";
						}else{
							my_settle  = gMerchantList.get(i).get("my_settle").toString();
						}
					}
					ws.addCell(new Label(col++, row, my_settle));
					if(null != gMerchantList.get(i).get("real_flag") && !"".equals(gMerchantList.get(i).get("real_flag").toString())){
						if("0".equals(gMerchantList.get(i).get("real_flag").toString())){
							real_flag  = "否";
						}else if("1".equals(gMerchantList.get(i).get("real_flag").toString())){
							real_flag  = "是";
						}else{
							real_flag  = gMerchantList.get(i).get("real_flag").toString();
						}
					}
					ws.addCell(new Label(col++, row, real_flag));
					row++;
					col = 0;
				}
				wwb.write();
				wwb.close();
				wb.close();
				os.close();
			}
		}
	}
	
	/**
	 * 检查集群编号的有效性（集群收单商户）
	 * @author swang
	 * @param group_code 集群编号
	 * @return 
	 */
	@RequestMapping("/checkGroupAcqCodeValidity")
	public void checkGroupAcqCodeValidity(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request){
		Map<String,Object> okMap=new HashMap<String, Object>();
		boolean success=false;
		int gCount = groupAcqMerchantService.getAcqMerchantByCode(params);
		if(gCount > 0){
			success=true;
		}
		okMap.put("success", success);
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
	}
	
	/**
	 * 检查集群编号的有效性(集群普通商户)
	 * @author swang
	 * @param group_code 集群编号
	 * @return 
	 */
	@RequestMapping("/checkGroupCodeValidity")
	public void checkGroupCodeValidity(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request){
		Map<String,Object> okMap=new HashMap<String, Object>();
		boolean success=false;
		int gCount = groupMerchantService.getGroupsMerchantByCode(params);
		if(gCount > 0){
			success=true;
		}
		okMap.put("success", success);
		String json = JSONObject.fromObject(okMap).toString();
		outJson(json, response);
	}
	
	/**
	 * 路由集群中普通商户查询（转移）导出Excel
	 * @author 王帅
	 * @date 2014年10月21日21:16:03
	 * @see 路由集群中普通商户查询（转移）导出Excel
	 * @param params  导出条件
	 */
	@RequestMapping(value = "/groupMerchantDistractExport")
	public void groupMerchantDistractExport(@RequestParam Map<String, String> params, 	HttpServletResponse response, HttpServletRequest request) {
		log.info("GroupController groupMerchantDistractExport start ...");
		if(null != params){
			List<Map<String, Object>> gMerchantList = groupMerchantService.getDistractGroups(params);
			if(gMerchantList.size() > 0){
				try {
					int row = 2; // 从第三行开始写
					int col = 0; // 从第一列开始写
					int maxCount = 60000; //每页最多显示60000行记录 超过60000行则写入第二页
					String fileName = "-集群普通商户转移(共计: "+gMerchantList.size()+" 条).xls";
					OutputStream os = null;
					request.setCharacterEncoding("UTF-8");
					os = response.getOutputStream(); // 取得输出流
					response.reset(); // 清空输出流
					response.setHeader("Content-disposition", "attachment;filename="
							+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
					response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
					Workbook wb =  Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/groupMerchantDistractExport.xls"));
					WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
					WritableSheet ws = wwb.getSheet(0);
					WritableSheet ws1 = wwb.getSheet(1);
					WritableSheet ws2 = wwb.getSheet(2);
					
					WritableFont titleFont = new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD);   
					WritableCellFormat cellFormatTitle = new WritableCellFormat(titleFont);
					cellFormatTitle.setAlignment(Alignment.CENTRE);
					cellFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
					WritableCellFormat cellFormat = new WritableCellFormat();
					cellFormat.setAlignment(Alignment.RIGHT);
					for (int i = 0; i < gMerchantList.size(); i++) {
						String  pos_type = "";
						String my_settle = "";
						if(i < maxCount){ //小于或等于60000条记录写在第一页
							ws.addCell(new Label(col++, row,  String.valueOf(i+1)));
							ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_no").toString()));
							ws.addCell(new Label(col++, row, gMerchantList.get(i).get("agent_name").toString()));
							ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_name").toString()));
							ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_code").toString()));
							ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_name").toString()));
							if(null != gMerchantList.get(i).get("pos_type") && !"".equals(gMerchantList.get(i).get("pos_type"))){
								pos_type = posTypeName(gMerchantList.get(i).get("pos_type").toString());
							}
							ws.addCell(new Label(col++, row, pos_type));
							if(null != gMerchantList.get(i).get("my_settle") && !"".equals(gMerchantList.get(i).get("my_settle").toString())){
								if("0".equals(gMerchantList.get(i).get("my_settle").toString())){
									my_settle = "否";
								}else if("1".equals(gMerchantList.get(i).get("my_settle").toString())){
									my_settle = "是";
								}else{
									my_settle = gMerchantList.get(i).get("my_settle").toString();
								}
							}
							ws.addCell(new Label(col++, row, my_settle));
							row++;
							col = 0;
						}else if(i >= maxCount && i < (maxCount * 2)){ //大于60000并且小于或等于120000显示在第二页
							if(i == maxCount){ //第一次进入Excel第二页从标题栏下开始写入
								row = 2; // 从第三行开始写
								col = 0; // 从第一列开始写
							}
							ws1.addCell(new Label(col++, row,  String.valueOf(i+1)));
							ws1.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_no").toString()));
							ws1.addCell(new Label(col++, row, gMerchantList.get(i).get("agent_name").toString()));
							ws1.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_name").toString()));
							ws1.addCell(new Label(col++, row, gMerchantList.get(i).get("group_code").toString()));
							ws1.addCell(new Label(col++, row, gMerchantList.get(i).get("group_name").toString()));
							if(null != gMerchantList.get(i).get("pos_type") && !"".equals(gMerchantList.get(i).get("pos_type"))){
								pos_type = posTypeName(gMerchantList.get(i).get("pos_type").toString());
							}
							ws1.addCell(new Label(col++, row, pos_type));
							if(null != gMerchantList.get(i).get("my_settle") && !"".equals(gMerchantList.get(i).get("my_settle").toString())){
								if("0".equals(gMerchantList.get(i).get("my_settle").toString())){
									my_settle = "否";
								}else if("1".equals(gMerchantList.get(i).get("my_settle").toString())){
									my_settle = "是";
								}else{
									my_settle = gMerchantList.get(i).get("my_settle").toString();
								}
							}
							ws1.addCell(new Label(col++, row, my_settle));
							row++;
							col = 0;
						}else{ //超出部分显示在第三页，第三页数据超过65536系统自动抛出异常返回空Excel
							if(i == (maxCount*2)){ //第一次进入Excel第三页从标题栏下开始写入
								row = 2; // 从第三行开始写
								col = 0; // 从第一列开始写
							}
							ws2.addCell(new Label(col++, row,  String.valueOf(i+1)));
							ws2.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_no").toString()));
							ws2.addCell(new Label(col++, row, gMerchantList.get(i).get("agent_name").toString()));
							ws2.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_name").toString()));
							ws2.addCell(new Label(col++, row, gMerchantList.get(i).get("group_code").toString()));
							ws2.addCell(new Label(col++, row, gMerchantList.get(i).get("group_name").toString()));
							if(null != gMerchantList.get(i).get("pos_type") && !"".equals(gMerchantList.get(i).get("pos_type"))){
								pos_type = posTypeName(gMerchantList.get(i).get("pos_type").toString());
							}
							ws2.addCell(new Label(col++, row, pos_type));
							if(null != gMerchantList.get(i).get("my_settle") && !"".equals(gMerchantList.get(i).get("my_settle").toString())){
								if("0".equals(gMerchantList.get(i).get("my_settle").toString())){
									my_settle = "否";
								}else if("1".equals(gMerchantList.get(i).get("my_settle").toString())){
									my_settle = "是";
								}else{
									my_settle = gMerchantList.get(i).get("my_settle").toString();
								}
							}
							ws2.addCell(new Label(col++, row, my_settle));
							row++;
							col = 0;
						}
					}
					wwb.write();
					wwb.close();
					wb.close();
					os.close();
					log.info("GroupController groupMerchantDistractExport SUCCESS");
				} catch (Exception e) {
					log.error("Exception GroupController groupMerchantDistractExport = " + e);
					e.printStackTrace();
				}
			}
		}
		log.info("GroupController groupMerchantDistractExport end");
	}
	
	/**
	 * 集群普通商户导出Excel
	 * @author swang
	 * @param params  集群编号
	 */
	@RequestMapping(value = "/exportGroupMerchant")
	public void exportGroupMerchant(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		String group_code = params.get("group_code");
		if(null != group_code && !"".equals(group_code)){
			//List<Map<String, Object>> gMerchantList = groupAcqMerchantService.getGroupAcqMerchantListByCode(params);
			List<Map<String, Object>> gMerchantList = groupMerchantService.getGroupMerchantByCode(group_code,params.get("real_flag"),params.get("merchant"));
			if(gMerchantList.size() > 0){
				int row = 2; // 从第三行开始写
				int col = 0; // 从第一列开始写
				String fileName = gMerchantList.get(0).get("group_name").toString()+"-集群收单商户(共计  "+gMerchantList.size()+" 条).xls";
				OutputStream os = null;
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename="
						+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				
				Workbook wb =  Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/GroupMerchant.xls"));
				WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
				WritableSheet ws = wwb.getSheet(0);
				 
				WritableFont titleFont = new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD);   
				WritableCellFormat cellFormatTitle = new WritableCellFormat(titleFont);
				cellFormatTitle.setAlignment(Alignment.CENTRE);
				cellFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
				WritableCellFormat cellFormat = new WritableCellFormat();
				cellFormat.setAlignment(Alignment.RIGHT);
				for (int i = 0; i < gMerchantList.size(); i++) {
					String pos_type = "";
					String address = "";
					String fee_type = "";
					String fee_rate = "";
					String fee_max_amount = "";
					String merchant_fee_type = "";
					String merchant_fee_rate = "";
					String merchant_fee_max_amount = "";
					String my_settle = "";
					String real_flag = "";
					String bag_settle = "";
					ws.addCell(new Label(col++, row, String.valueOf(i+1)));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_no").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_code").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("acq_cnname").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("agent_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("province").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("city").toString()));
					if(null != gMerchantList.get(i).get("address") && !"".equals(gMerchantList.get(i).get("address").toString())){
						address = gMerchantList.get(i).get("address").toString();
					}
					ws.addCell(new Label(col++, row, address));
					if(null != gMerchantList.get(i).get("pos_type") && !"".equals(gMerchantList.get(i).get("pos_type").toString())){
						pos_type = posTypeName(gMerchantList.get(i).get("pos_type").toString());
					}
					ws.addCell(new Label(col++, row, pos_type));
					
					if(null != gMerchantList.get(i).get("merchant_fee_type") && !"".equals(gMerchantList.get(i).get("merchant_fee_type").toString())){
						if("RATIO".equals(gMerchantList.get(i).get("merchant_fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("merchant_fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							merchant_fee_rate = f + "%";
							merchant_fee_type="扣率";
						}else if("CAPPING".equals(gMerchantList.get(i).get("merchant_fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("merchant_fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							merchant_fee_rate = f + "%";
							if(null != gMerchantList.get(i).get("merchant_fee_max_amount") && "".equals(gMerchantList.get(i).get("merchant_fee_max_amount").toString())){
								merchant_fee_max_amount = gMerchantList.get(i).get("merchant_fee_max_amount").toString();
							}
							merchant_fee_type="封顶";
						}else{
							merchant_fee_type=gMerchantList.get(i).get("merchant_fee_type").toString();
						}
						
					}
					ws.addCell(new Label(col++, row, merchant_fee_type));//阶梯
					ws.addCell(new Label(col++, row, merchant_fee_rate));//
					ws.addCell(new Label(col++, row, merchant_fee_max_amount));
					if(null != gMerchantList.get(i).get("fee_type") && !"".equals(gMerchantList.get(i).get("fee_type").toString())){
						if("RATIO".equals(gMerchantList.get(i).get("fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							fee_rate = f + "%";
							fee_type="扣率";
						}else if("CAPPING".equals(gMerchantList.get(i).get("fee_type").toString())){
							BigDecimal d = new BigDecimal(gMerchantList.get(i).get("fee_rate").toString());
							double f = d.multiply(new BigDecimal("100")).doubleValue();
							fee_rate = f + "%";
							if(null != gMerchantList.get(i).get("fee_max_amount") && !"".equals(gMerchantList.get(i).get("fee_max_amount").toString())){
								fee_max_amount = gMerchantList.get(i).get("fee_max_amount").toString();
							}
							fee_type="封顶";
						}else{
							fee_type=gMerchantList.get(i).get("fee_type").toString();
						}
					}
					ws.addCell(new Label(col++, row, fee_type));
					ws.addCell(new Label(col++, row, fee_rate));
					ws.addCell(new Label(col++, row, fee_max_amount));//封顶手续费
					if(null != gMerchantList.get(i).get("my_settle") && !"".equals(gMerchantList.get(i).get("my_settle").toString())){
						if("0".equals(gMerchantList.get(i).get("my_settle").toString())){
							my_settle  = "否";
						}else if("1".equals(gMerchantList.get(i).get("my_settle").toString())){
							my_settle  = "是";
						}else{
							my_settle  = gMerchantList.get(i).get("my_settle").toString();
						}
					}
					ws.addCell(new Label(col++, row, my_settle));
					if(null != gMerchantList.get(i).get("bag_settle") && !"".equals(gMerchantList.get(i).get("bag_settle").toString())){
						if("0".equals(gMerchantList.get(i).get("bag_settle").toString())){
							bag_settle  = "否";
						}else if("1".equals(gMerchantList.get(i).get("bag_settle").toString())){
							bag_settle  = "是";
						}else{
							bag_settle  = gMerchantList.get(i).get("bag_settle").toString();
						}
					}
					ws.addCell(new Label(col++, row, bag_settle));
					if(null != gMerchantList.get(i).get("real_flag") && !"".equals(gMerchantList.get(i).get("real_flag").toString())){
						if("0".equals(gMerchantList.get(i).get("real_flag").toString())){
							real_flag  = "否";
						}else if("1".equals(gMerchantList.get(i).get("real_flag").toString())){
							real_flag  = "是";
						}else{
							real_flag  = gMerchantList.get(i).get("real_flag").toString();
						}
					}
					ws.addCell(new Label(col++, row, real_flag));
					row++;
					col = 0;
				}
				wwb.write();
				wwb.close();
				wb.close();
				os.close();
			}
		}
		//getGroupMerchantByCode
		/*String group_code = params.get("group_code");
		if(null != group_code && !"".equals(group_code)){
			List<Map<String, Object>> gMerchantList = groupMerchantService.getGroupMerchantByCode(group_code,params.get("real_flag"),params.get("merchant"));
			if(gMerchantList.size() > 0){
				int row = 2; // 从第三行开始写
				int col = 0; // 从第一列开始写
				String fileName = gMerchantList.get(0).get("group_name").toString()+"-集群普通商户(共计: "+gMerchantList.size()+" 条).xls";
				OutputStream os = null;
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename="
						+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				
				Workbook wb =  Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/GroupMerchant.xls"));
				WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
				WritableSheet ws = wwb.getSheet(0);
				 
				WritableFont titleFont = new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD);   
				WritableCellFormat cellFormatTitle = new WritableCellFormat(titleFont);
				cellFormatTitle.setAlignment(Alignment.CENTRE);
				cellFormatTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
				WritableCellFormat cellFormat = new WritableCellFormat();
				cellFormat.setAlignment(Alignment.RIGHT);
				String pos_type = "";
				String fee_type = "";
				String fee_rate = "";
				
				String fee_cap_amount = "";
				String fee_max_amount = "";
				String fee_single_amount = "";
				String ladder_fee = "";
				String my_settle = "";
				String real_flag = "";
				for (int i = 0; i < gMerchantList.size(); i++) {
					ws.addCell(new Label(col++, row,  String.valueOf(i+1)));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_no").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("merchant_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_code").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("group_name").toString()));
					ws.addCell(new Label(col++, row, gMerchantList.get(i).get("agent_name").toString()));
					if(null != gMerchantList.get(i).get("pos_type") && !"".equals(gMerchantList.get(i).get("pos_type").toString())){
						pos_type = posTypeName(gMerchantList.get(i).get("pos_type").toString());
					}
					ws.addCell(new Label(col++, row, pos_type));
					if(null != gMerchantList.get(i).get("fee_type") && !"".equals(gMerchantList.get(i).get("fee_type").toString())){
						if("RATIO".equals(gMerchantList.get(i).get("fee_type").toString())){
							fee_type = "扣率";
						}else if("CAPPING".equals(gMerchantList.get(i).get("fee_type").toString())){
							fee_type = "封顶";
						}else if("LADDER".equals(gMerchantList.get(i).get("fee_type").toString())){
							fee_type = "阶梯";
						}else {
							fee_type = gMerchantList.get(i).get("fee_type").toString();
						}
					}
					ws.addCell(new Label(col++, row, fee_type));//费率类型
					if(null != gMerchantList.get(i).get("fee_rate") && !"".equals(gMerchantList.get(i).get("fee_rate").toString())){
						BigDecimal d = new BigDecimal(gMerchantList.get(i).get("fee_rate").toString());
						double f = d.multiply(new BigDecimal("100")).doubleValue();
						fee_rate = f+"%";
					}
					ws.addCell(new Label(col++, row, fee_rate));//扣率
					if(null != gMerchantList.get(i).get("fee_cap_amount") && !"".equals(gMerchantList.get(i).get("fee_cap_amount").toString())){
						fee_cap_amount = gMerchantList.get(i).get("fee_cap_amount").toString();
					}
					ws.addCell(new Label(col++, row, fee_cap_amount));
					if(null != gMerchantList.get(i).get("fee_max_amount") && !"".equals(gMerchantList.get(i).get("fee_max_amount").toString())){
						fee_max_amount = gMerchantList.get(i).get("fee_max_amount").toString();
					}
					ws.addCell(new Label(col++, row, fee_max_amount));
					if(null != gMerchantList.get(i).get("ladder_fee") && !"".equals(gMerchantList.get(i).get("ladder_fee").toString())){
						ladder_fee = gMerchantList.get(i).get("ladder_fee").toString();
					}
					ws.addCell(new Label(col++, row, ladder_fee));
					if(null != gMerchantList.get(i).get("my_settle") && !"".equals(gMerchantList.get(i).get("my_settle").toString())){
						if("0".equals(gMerchantList.get(i).get("my_settle").toString())){
							my_settle = "否";
						}else if("1".equals(gMerchantList.get(i).get("my_settle").toString())){
							my_settle = "是";
						}else{
							my_settle = gMerchantList.get(i).get("my_settle").toString();
						}
					}
					ws.addCell(new Label(col++, row, my_settle));
					if(null != gMerchantList.get(i).get("real_flag") && !"".equals(gMerchantList.get(i).get("real_flag").toString())){
						if("0".equals(gMerchantList.get(i).get("real_flag").toString())){
							real_flag = "否";
						}else if("1".equals(gMerchantList.get(i).get("real_flag").toString())){
							real_flag = "是";
						}else{
							real_flag = gMerchantList.get(i).get("real_flag").toString();
						}
					}
					ws.addCell(new Label(col++, row, real_flag));
					row++;
					col = 0;
				}
				wwb.write();
				wwb.close();
				wb.close();
				os.close();
			}
		}*/
	}
	
	// 查询groupMerchantQuery
	@RequestMapping(value = "/groupMerchantQuery")
	public String groupMerchantQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		
		BossUser bu = getUser();
		String realName = bu.getRealName();
		String specialName = SysConfig.value("specialNameViewRoute");//特殊集群特殊人员查看的人员姓名
		String specialCode = SysConfig.value("specialViewRouteCode");//特殊集群特殊人员查看的集群编号
		if(specialName != null && specialName.equals(realName)){
			params.put("specialName", "true");
		}
		if(specialCode != null){
			params.put("specialCode", specialCode);
		}
		
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = groupMerchantService.getGroups(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		// model.put("totalMsg", totalMsg);
		model.put("params", params);
		return "/group/groupMerchantQuery";
	}
	
	// 新增input
	@RequestMapping(value = "/groupMerchantAdd")
	public String groupMerchantAdd(final ModelMap model,	@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		log.info("GroupController groupMerchantAdd START");
		String groupCode = params.get("id").toString();
		if(!"".equals(groupCode)){
			Map<String, Object> groupInfo = groupMerchantService.getGroupInfo(groupCode);
			model.put("groupInfo", groupInfo);
		}
		log.info("GroupController groupMerchantAdd END");
		return "/group/groupMerchantAdd";
	}
	
	/**
	 * 
	 * @param merchantNo
	 * @param status
	 * @return
	 */
	@RequestMapping("/merchantChangeStatus")
	@ResponseBody
	public void changeAcqMerchantStatus(HttpServletResponse response,
			@RequestParam("acqMerchantNo") String acqMerchantNo,
			@RequestParam("status") int status){
		Map<String,Object> okMap=new HashMap<String, Object>();
		try {
			int rowsuc = groupAcqMerchantService.changeAcqGroupMerchantStatus(acqMerchantNo, status);
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}
	
	
	// 新增merchantAddCheck
	@RequestMapping(value = "/merchantAddCheck")
	public void merchantAddCheck(HttpServletRequest request,	HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		log.info("GroupController merchantAddCheck START");
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = groupMerchantService.addCheck_exists_route(params);
			if (rowsuc >= 1) {
				okMap.put("msg", "此商户已绑定集群");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}
			Map<String, Object> merchantInfoMap = merchantService.getFirstMerchantInfoByMerchantNo(params.get("merchant_no").toString());//groupMerchantService.addCheck_exists_merchant(params);
			if (merchantInfoMap == null) {
				okMap.put("msg", "无此商户");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}

			okMap.put("msg", "OK");
			okMap.put("bag_settle", merchantInfoMap.get("bag_settle").toString());
			okMap.put("my_settle", merchantInfoMap.get("my_settle").toString());
			String json = JSONObject.fromObject(okMap).toString();
			log.info("GroupController merchantAddCheck END");
			outJson(json, response);
		} catch (Exception e) {
			log.error("GroupController merchantAddCheck Exception = " + e.getMessage());
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}

	}
	
	/**
	 * 根据商户编号， 自动绑定机具、自动将商户状态变更为“正常”(录入机具数量超过20个不处理)
	 * @param merchant_no 商户编号
	 * @return true：操作成功
	 */
	public boolean openMerchantAndTerminal(String merchant_no) throws SQLException  {
		boolean flag = false;
		if(!"".equals(merchant_no)){
			Map<String, Object> merchantInfo = merchantService.getMerchantInfoByMerchantNo(merchant_no); //获取商户信息
			if(null != merchantInfo && "5".equals(merchantInfo.get("open_status"))){  //校验商户状态为“机具绑定”
				if(null != merchantInfo.get("pos_type") &&  !"".equals(merchantInfo.get("pos_type"))){ //校验设备类型
					if(!"".equals(merchantInfo.get("terminal_no").toString()) && !"".equals(merchantInfo.get("agent_no").toString())){ 
						String[] merchantTerminalInfo = merchantInfo.get("terminal_no").toString().split(";");
						if(merchantTerminalInfo.length < 21){  //机具超过20个不处理
							StringBuffer querryConditions = new StringBuffer();
							for(int i=0;i<merchantTerminalInfo.length;i++){
								if(i == (merchantTerminalInfo.length-1)){
									querryConditions.append("'");
									querryConditions.append(merchantTerminalInfo[i]);
									querryConditions.append("'");
								}else{
									querryConditions.append("'");
									querryConditions.append(merchantTerminalInfo[i]);
									querryConditions.append("'");
									querryConditions.append(",");
								}
							}
							//根据商户所属代理商编号和录入的SN 编号匹配所录入的机具有效性
							List<Map<String, Object>> terminalList = terminalService.checkTerminalByTerNo(querryConditions.toString(),merchantInfo.get("agent_no").toString());
							 //验证数量是否匹配
							if(terminalList.size() == merchantTerminalInfo.length){
								//根据商户编号绑定机具
								for(int j=0; j< terminalList.size(); j++){
									terminalService.setMerchantNo(merchant_no.trim(), Integer.parseInt(terminalList.get(j).get("id").toString()), Integer.parseInt(merchantInfo.get("agent_no").toString()));
								}
								//组装商户开启条件 并 开启商户
								Map<String, String> params = new HashMap<String, String>();
								params.put("merchant_no", merchant_no);
								params.put("status", "1");
								//开启商户状态
								int modifyMerchantCount = merchantService.merchantSetNormal(params);
								if(modifyMerchantCount > 0){
									flag = true;
								}
							}
						}
					}

					if (flag && !"test".equals(SysConfig.value("projectRunFlag"))) {

						String pos_type = merchantInfo.get("pos_type").toString();
						String agentNo = merchantInfo.get("agent_no").toString();
						String phoneNumber = merchantInfo.get("mobile_username").toString();
						String merchant_name = merchantInfo.get("merchant_name").toString();

						SmsUtil.sendSms(new SmsBean(phoneNumber, agentNo, pos_type, "1", new String[]{merchant_name}));
					}
				}
			}
		}
		return flag;
	}
	
		/**
		 * 普通商户新增集群
		 * @see 涉及到二清问题需慎重修改
		 * @param request
		 * @param response
		 * @param params
		 */
		@RequestMapping(value = "/merAddGroupAndModifyMerInfo")
		public void merAddGroupAndModifyMerInfo(HttpServletRequest request,
				HttpServletResponse response,	@RequestParam Map<String, String> params) {
			String msg = "ERROR";
			Map<String, String> okMap = new HashMap<String, String>();
			try {
				String merchant_no = params.get("merchant_no");
				boolean upmerchantInfo = false;
				//为商户添加集群时验证该集群是否为瑞银信通道，如果是，则将当前商户是否优质改为否
					Map<String, Object> groupInfo= groupMerchantService.getGroupInfo(params.get("group_code"));
					Map<String, Object>  merchantInfo = merchantService.getMerchantInfoByMNo(merchant_no);
					if(groupInfo != null){
						if(merchantInfo != null){
							if(null != merchantInfo.get("open_status") && !"".equals(merchantInfo.get("open_status").toString()) && 
									(!"1".equals(merchantInfo.get("open_status").toString()) || !"5".equals(merchantInfo.get("open_status").toString()))){
								if(null != groupInfo.get("my_settle") && !"".equals(groupInfo.get("my_settle").toString()) && !"-1".equals(groupInfo.get("my_settle").toString())){
									if(null != groupInfo.get("bag_settle") && !"".equals(groupInfo.get("bag_settle").toString()) && !"-1".equals(groupInfo.get("bag_settle").toString())){
										upmerchantInfo = true;
									}else{
										msg = "NOGB";//未找到集群是钱包结算
									}
								}else{
									msg = "NOGS";//未找到集群是否优质属性
								}
							}else{
								msg = "NOC";//商户未审核
							}
						}else{
							msg = "NOM";//无此商户
						}
					}else{
						msg = "NOG";//无此集群
					}
					if(upmerchantInfo){
						params.put("merchant_no", merchant_no);
						params.put("create_person", getUser().getRealName());
						params.put("my_settle", groupInfo.get("my_settle").toString());
						params.put("bag_settle", groupInfo.get("bag_settle").toString());
						//自动绑定机具开启商户
						int rowsuc = groupMerchantService.addSubmit(params);
						if (rowsuc > 0) {
							//根据商户编号获取非正常状态下对应的商户信息
							boolean openMerchant = openMerchantAndTerminal(merchant_no);
							if(openMerchant){
								msg = "OK";
							}else{
								msg = "ADDOKNOP";//添加集群成功，开启失败
							}
						}else{
							msg = "ADDFAIL";//添加集群失败
						}
					}
					
				okMap.put("msg", msg);
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			} catch (Exception e) {
				okMap.put("msg", msg);
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				e.printStackTrace();
			}
		}

	// 新增merchantAddSubmit
	@RequestMapping(value = "/merchantAddSubmit")
	public void merchantAddSubmit(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = groupMerchantService.addSubmit(params);
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/groupMerchantDel")
	public void groupMerchantDel(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = 0;
			if(params != null && null !=params.get("id") &&  !"".equals(params.get("id").toString())){
				rowsuc = groupMerchantService.delGroupMerchantNo(params.get("id").toString(), getUser().getRealName());
			}
			
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}else{
				okMap.put("msg", "FAIL");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}

	// 查询groupAcqMerchantQuery
	@RequestMapping(value = "/groupAcqMerchantQuery")
	public String groupAcqMerchantQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = groupAcqMerchantService.getGroups(params, page);

		List<Map<String, Object>> acqOrgList = acqMerchantService.getMerchantList(); //得到收单机构信息

		model.put("p", cpage);
		model.put("list", list);
		model.put("acqOrgList", acqOrgList);
		// model.put("totalMsg", totalMsg);
		model.put("params", params);
		return "/group/groupAcqMerchantQuery";
	}

	// 新增groupAcqMerchantAdd
	@RequestMapping(value = "/groupAcqMerchantAdd")
	public String groupAcqMerchantAdd(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		List<Map<String, Object>> acqOrgList = acqMerchantService.getMerchantList(); //得到收单机构信息
		List<Map<String, Object>> groupList = groupService.getGroupInfo();
		model.put("acqOrgList", acqOrgList);
		model.put("groupList", groupList);
		return "/group/groupAcqMerchantAdd";
	}

	// 新增merchantAcqAddCheck
	@RequestMapping(value = "/groupAcqMrchantAddCheck")
	public void groupAcqMrchantAddCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = groupAcqMerchantService.addCheck_exists_rel(params);
			if (rowsuc >= 1) {
				okMap.put("msg", "此商户已和该收单机构绑定，不能重复绑定");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}
			rowsuc = groupAcqMerchantService.addCheck_exists_merchant(params);
			if (rowsuc <= 0) {
				okMap.put("msg", "无此收单机构商户");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return;
			}

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

	// 新增merchantAcqAddSubmit
	@RequestMapping(value = "/acqMerchantAddSubmit")
	public void acqMerchantAddSubmit(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = groupAcqMerchantService.addSubmit(params);
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/groupAcqMerchantDel")
	public void groupAcqMerchantDel(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		Map<String, String> okMap = new HashMap<String, String>();
		try {
			int rowsuc = groupAcqMerchantService.del(params);
			if (rowsuc > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		} catch (Exception e) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据设备类型获取集群中拥有的扣率类型
	 * @author 王帅
	 * @see 集群普通商户转移功能-根据所选设备类型获取商户在集群中拥有的所有扣率类型并进行分组
	 * @date 2014年10月21日17:58:38
	 * @param response 扣率类型JSONArray
	 * @param params 设备类型
	 * @return JSONArray
	 */
	@RequestMapping(value = "/getMerchantFeeType")
	public void getMerchantFeeType(HttpServletResponse response,@RequestParam Map<String, String> params) {
		log.info("GroupController getMerchantFeeType start ...");
		String pos_type = "";
		if(params != null){
			if(!"".equals(params.get("pos_type").toString()) ){
				pos_type = params.get("pos_type").toString();
			}
		}
		List<Map<String, Object>> list = groupMerchantService.getMerchantFeeTypeGroup(pos_type);
		JSONArray json =JSONArray.fromObject(list);
		log.info("GroupController getMerchantFeeType end  list size = "+list.size());
		outJson(json.toString(), response);
	}
	
	/**
	 * 获取扣率类型对应的扣率
	 * @author 王帅
	 * @param response
	 * @param params
	 * @return JSONArray
	 */
	@RequestMapping(value = "/getGroupMerchantFeeType")
	public void getGroupMerchantFeeType(HttpServletResponse response,@RequestParam Map<String, String> params) {
		log.info("GroupController getGroupMerchantFeeType start ...");
		List<Map<String, Object>> list = groupMerchantService.getGroupMerchantFeeType(params);
		JSONArray json =JSONArray.fromObject(list);
		log.info("GroupController getGroupMerchantFeeType end  list size = "+list.size());
		outJson(json.toString(), response);
	}
	
	/**
	 * 集群中查询需转移的普通商户
	 * @author LJ
	 * @param model
	 * @param params
	 * @param cpage
	 * @return
	 */
	@RequestMapping(value = "/groupMerchantDistract")
	public String groupMerchantDistract(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = groupMerchantService.getDistractGroups(params,page);
		List<Map<String, Object>> acqOrgList = acqMerchantService	.getMerchantList(); //得到收单机构信息

		model.put("p", cpage);
		model.put("list", list);
		model.put("acqOrgList", acqOrgList);
		model.put("params", params);
		return "/group/groupMerchantDistract";
	}
	
	
	
	/**
	 *  在确定转移之前重新查询并取到商户编号
	 * @param model
	 * @author LJ
	 * @param params
	 */
	@RequestMapping(value = "/groupMerchantDistractOther")
	public void groupMerchantDistractOther(final ModelMap model,
			@RequestParam Map<String, String> params ) {
		
		List<Map<String, Object>> list = groupMerchantService.getDistractGroups(params);
		System.out.println("list集合个数："+list.size());
		StringBuffer sb = new StringBuffer();
		for (Map<String, Object> map : list) {;
			String merchant_no = (String) map.get("merchant_no");
			sb.append(merchant_no).append(";");
		}
		
		model.put("list", list);
		model.put("data", sb.toString());
		model.put("total", list.size());
	}
	
	

	
	
	/**
	 * 打开转移集群窗口
	 * @param model ModelMap对象
	 * @param params 条件参数
	 * @author LJ
	 * @return  返回到页面
	 * @throws Exception
	 */
	@RequestMapping(value = "/viewDistractGroup")
	public String viewDistractGroup(final ModelMap model,@RequestParam Map<String, String> params)
			throws Exception {
		
		List<Map<String, Object>> grouplist = groupMerchantService.getGroupList();
		model.put("grouplist", grouplist);
		//重新按条件查询一遍，取得结果集以及商户编号集合
		groupMerchantDistractOther(model, params);
		
		return "/group/groupDistractGroup";
	}

	
	/**
	 * 转移集群操作
	 * @param data  商户编号集合
	 * @param group_code  集群编号
	 * @author LJ
	 * @param response 
	 * @param request
	 * @throws Exception 
	 */
	@RequestMapping(value = "/MoveGroup")
	public void MoveGroup(@RequestParam String data,@RequestParam String group_code,HttpServletResponse response, HttpServletRequest request)
	throws Exception{
        try {
        	     boolean falg = false;
	    		 if (StringUtils.isNotEmpty(data) && data.endsWith(";")) {
	    			data = data.substring(0, data.length() - 1);
	    		 }else{
	    			 return;
	    		 }
	    		 Map<String, Object> map = groupMerchantService.getGroupInfo(group_code);
	    		 if(map != null && null !=map.get("my_settle") && null !=map.get("bag_settle") && !"".equals(map.get("my_settle").toString()) && !"".equals(map.get("bag_settle").toString())){
	    			 String[] params = data.split(";");
		    		 for (int i = 0; i < params.length; i++) {
						String merchant_no = params[i];
						 groupMerchantService.updateGroup(merchant_no,group_code,getUser().getRealName(),map.get("my_settle").toString(), map.get("bag_settle").toString());
						 falg = true;
					}
	    		 }
	    		if (falg) {
					response.getWriter().write("1");
				} else {
					response.getWriter().write("0");
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
