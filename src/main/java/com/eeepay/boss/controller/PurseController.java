package com.eeepay.boss.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.domain.BagCheckBackMsg;
import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.service.LogService;
import com.eeepay.boss.service.PurseRecService;
import com.eeepay.boss.service.PurseService;
import com.eeepay.boss.service.PurseTransService;
import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.DictCache;
import com.eeepay.boss.utils.Http;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.XmlUtil;
import com.eeepay.boss.utils.md5.Md5;
import com.eeepay.boss.utils.pub.Sms;

/**
 * 手机钱包
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping(value = "/purse")
public class PurseController extends BaseController {

	@Resource
	private PurseService purseSerivce;
  @Resource
  private PurseRecService purseRecService;	
  @Resource
  private PurseService purseService; 
  
	@Resource
	private LogService logService;
	
	
	private static final Logger log = LoggerFactory
		      .getLogger(PurseController.class);

	// 手机钱包用户查询
	@RequestMapping(value = "/userQuery")
	public String userQuery(final ModelMap model, @RequestParam
	Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
	int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = purseSerivce.getUserList(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/purse/userQuery";
	}

	// 手机钱包用户详情
	@RequestMapping(value = "/purDetail")
	public String purDetail(final ModelMap model, @RequestParam
	Map<String, String> params) throws Exception {
		String id = params.get("id");
		Map<String, Object> blackMap = purseSerivce.getPurseDetailById(Long
				.parseLong(id));
		model.put("params", blackMap);
		return "/purse/purDetail";
	}

	// 重置用户密码
	@RequestMapping(value = "/purseUserReset")
	public void purseUserReset(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam
			Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1")
			int cpage) throws SQLException {
		String id = params.get("id");
		if (id != null && id.trim().length() > 0) {
			int rowNum = purseSerivce.resetUserPassword(Long.parseLong(id));
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

	/*// 手机充值查询
	@RequestMapping(value = "/rechargeQuery")
	public String rechargeQuery(final ModelMap model, @RequestParam
	Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
	int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = purseSerivce.getRechargeList(params,
				page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/purse/rechargeQuery";
	}
*/
	// 手机提现详情
	@RequestMapping(value = "/extraDetail")
	public String extraDetail(final ModelMap model, @RequestParam
	Map<String, String> params) throws Exception {
		String id = params.get("id");
		Map<String, Object> blackMap = purseSerivce
				.getExtractionDetailById(Long.parseLong(id));
		model.put("params", blackMap);
		return "/purse/extractionDetail";
	}

	/*// 手机提现查询
	@RequestMapping(value = "/extractionQuery")
	public String extractionQuery(final ModelMap model, @RequestParam
	Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
	int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = purseSerivce.getExtractionList(params,
				page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/purse/extractionQuery";
	}*/

	// 手机充值详情
	@RequestMapping(value = "/rechargeDetail")
	public String rechargeDetail(final ModelMap model, @RequestParam
	Map<String, String> params) throws Exception {
		String id = params.get("id");
		Map<String, Object> blackMap = purseSerivce.getRechargeDetailById(Long
				.parseLong(id));
		model.put("params", blackMap);
		return "/purse/rechargeDetail";
	}

	

	// 钱包用户查询
	@RequestMapping(value = "/bagLoginQuery")
	public String bagLoginQuery(final ModelMap model, @RequestParam
	Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
	int cpage) {
		try {
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			
			if (params.get("createTimeBegin") == null
					&& params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime +" 23:59:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}
			Page<Map<String, Object>> list = purseSerivce.getBagLoginList(params, page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "/purse/bagUserQuery";
	}

	
	// 重置用户密码
		@RequestMapping(value = "/bagUserReset")
		public void bagUserReset(final ModelMap model,
				HttpServletRequest request, HttpServletResponse response,
				@RequestParam
				Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1")
				int cpage) throws SQLException {
			String id = params.get("id");
			if (id != null && id.trim().length() > 0) {
				int rowNum = purseSerivce.resetbagUserPassword(Long.parseLong(id));
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
		
		 //钱包用户修改保存
		@RequestMapping(value = "/bagUserModifySave")
		public void bagUserModifySave(HttpServletResponse response,final ModelMap model, @RequestParam
		Map<String, String> params) throws Exception {
			JSONObject json = new JSONObject();
			int modifyCount = 0;
			try {
				if(params != null){
					String id = params.get("id");
					if(id != null && !"".equals(id)){
						/*if(!"".equals(params.get("withdraws_time_star_short").toString()) && !"".equals(params.get("withdraws_time_end_short").toString())){
							String withdraws_time_star_short = "1970-10-1 " + params.get("withdraws_time_star_short").toString();
							String withdraws_time_end_short = "1970-10-1 " + params.get("withdraws_time_end_short").toString();
							params.put("withdraws_time_star_short", withdraws_time_star_short);
							params.put("withdraws_time_end_short", withdraws_time_end_short);
						}*/
						System.out.println("----is_tzero---"+params.get("is_tzero"));
						int count =purseSerivce.modifySave(params);
						if(count > 0){
							modifyCount = count;
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			json.put("modifyCount", modifyCount);
			outJson(json.toString(), response);
		}
		
		 //钱包用户修改界面
		@RequestMapping(value = "/bagUserModify")
		public String bagUserModify(final ModelMap model, @RequestParam
		Map<String, String> params) throws Exception {
			String id = params.get("id");
			Map<String, Object> blackMap = purseSerivce.getBagDetailById(Long.parseLong(id));
			Object withdraws_time_starObj = blackMap.get("withdraws_time_star");
			Object withdraws_time_endObj = blackMap.get("withdraws_time_end");
			String withdraws_time_star = "";
			String withdraws_time_end = "";
			if(withdraws_time_starObj!=null && withdraws_time_endObj!=null){
				withdraws_time_star = withdraws_time_starObj.toString();
				withdraws_time_end = withdraws_time_endObj.toString();
			}
			blackMap.put("withdraws_time_star_short", "".equals(withdraws_time_star) ? ""
					: withdraws_time_star.substring(11, 19));
			blackMap.put("withdraws_time_end_short", "".equals(withdraws_time_end) ? ""
					: withdraws_time_end.substring(11, 19));
			model.put("params", blackMap);
			return "/purse/bagUserModify";
		}

	
	    //钱包用户详情
		@RequestMapping(value = "/bagDetail")
		public String bagDetail(final ModelMap model, @RequestParam
		Map<String, String> params)  {
			String id = params.get("id");
			Map<String, Object> blackMap;
			try {
				blackMap = purseSerivce.getBagDetailById(Long.parseLong(id));
				if(blackMap != null && blackMap.size() > 0){
				 	 String mobile_no  =  blackMap.get("mobile_no").toString(); 
				 	 Object app_typeO = blackMap.get("app_type"); 
				 	 String app_type = null;
				 	 if(app_typeO!=null){
				 		app_type = app_typeO.toString();
				 	 }
				 	 List<Map<String, Object>> settleAccountList = purseSerivce.getSettleAccount(mobile_no,app_type);
				 	 model.put("settleAccountList", settleAccountList);
				 	 
				 	List<Map<String, Object>> bagFreezes = purseService.getBagFreezes(mobile_no,app_type);
				 	model.put("bagFreezes", bagFreezes);
				}
				model.put("params", blackMap);
				
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*String withdraws_time_star = blackMap.get("withdraws_time_star").toString();
			String withdraws_time_end = blackMap.get("withdraws_time_end").toString();
			blackMap.put("withdraws_time_star_short", "".equals(withdraws_time_star) ? ""
					: withdraws_time_star.substring(11, 19));
			blackMap.put("withdraws_time_end_short", "".equals(withdraws_time_end) ? ""
					: withdraws_time_end.substring(11, 19));*/
			return "/purse/bagDetail";
		}

		/**
		   * 
		   * 功能：统计当前提现数据
		   *
		   * @param params
		   * @param response
		   * @throws IOException
		   */
		  @RequestMapping(value = "/cashStatistic")
		  public void cashStatistic(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
			  
			  String openStatus  = "";
			  if(StringUtil.isBlank(params.get("openStatus"))){
				  openStatus = "全部";
			  }else{
				  openStatus =  DictCache.getDictName("purse_cash_status", (String)params.get("openStatus"));
			  }
			 
			  
			  Map<String, Object> list=purseSerivce.queryCashStatistic(params);
			  if(list.get("totalCount").toString().equals("0")){
				  outText("暂无数据", response);
				  return ;
			  }
			  BigDecimal amount = new BigDecimal(list.get("totalAmount").toString());
			  BigDecimal totalFee = new BigDecimal(list.get("totalFee").toString());
			  String html = openStatus + ": "+list.get("totalCount")+"笔/"+new DecimalFormat("#,##0.00#").format(amount.doubleValue())+"元&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手续费:"+new DecimalFormat("#,##0.00#").format(totalFee.doubleValue())+"元";
			  outText(html, response);
		  }
		  
		  
	// 提现管理（通道管理）
		@RequestMapping(value = "/bagExtractionChannel")
		public String bagExtractionChannel(final ModelMap model,@RequestParam Map<String, String> params) {
			List<Map<String, Object>> list = purseSerivce.getExtractionChannel(params);
			model.put("list", list);
			if(list!=null){
				/*String auto_check_state = String.valueOf(list.get(0).get("auto_check_state"));
				String warn_mobile_no = String.valueOf(list.get(0).get("warn_mobile_no"));*/
				String auto_check_state = purseSerivce.getParamValue("autoCheckStatus");
				String real_time_state = purseSerivce.getParamValue("realTimeCheckStatus");
				String warn_mobile_no = purseSerivce.getParamValue("extractionChannelWarnPerson");
				List<Map<String, Object>> logList = purseSerivce.getOtherExtractionChannelLog();
				model.put("auto_check_state", auto_check_state);
				model.put("real_time_state", real_time_state);
				model.put("warn_mobile_no", warn_mobile_no);
				model.put("logList", logList);
				int isChannelUsed = 0;//是否有通道正在用  0没有  1有
				for(Map<String, Object> map:list){
					int channel_state = (Integer)map.get("channel_state");
					if(channel_state==1){
						isChannelUsed = 1;
					}
				}
				model.put("isChannelUsed", isChannelUsed);
			}
			return "/purse/bagExtractionChannel";
		}
		
		//通道设置
		@RequestMapping(value = "/bagExtractionChannelSet")
		public String bagExtractionChannelSet(final ModelMap model,@RequestParam Map<String, String> params) {
			List<Map<String, Object>> list = purseSerivce.getExtractionChannel(params);
			Map<String, Object> channelMap = list.get(0);
			model.put("channelMap", channelMap);
			return "/purse/bagExtractionChannelSet";
		}
		//更新通道设置
		@RequestMapping(value = "/updateChannel")
		public void updateChannel(final ModelMap model,HttpServletResponse response,@RequestParam Map<String, String> params) {
			String id = params.get("id");
			BigDecimal add_amount = new BigDecimal(params.get("add_amount"));
			BigDecimal warn_amount = new BigDecimal(params.get("warn_amount"));
			BigDecimal old_warn_amount = new BigDecimal(params.get("old_warn_amount"));
			System.out.println("add_amount="+add_amount+",warn_amount="+warn_amount+",old_warn_amoung="+old_warn_amount);
			String operater = getUser().getRealName();
			String content = "新增额度"+add_amount+"元";
			if(old_warn_amount.compareTo(warn_amount)!=0){
				content = content+"且预警额度设置为"+warn_amount+"元";
			}
			Boolean result = purseSerivce.updateChannel(Integer.parseInt(id), add_amount, warn_amount, operater, content);
			if(result){
				outText("操作成功", response);
			}else{
				outText("系统异常", response);
			}
		}
		
		//提现通道详情
		@RequestMapping(value = "/bagExtractionChannelDetail")
		public String bagExtractionChannelDetail(final ModelMap model,@RequestParam Map<String, String> params) {
			String id = params.get("id");
			List<Map<String, Object>> channelList = purseSerivce.getExtractionChannel(params);
			Map<String, Object> channelMap = channelList.get(0);
			List<Map<String, Object>> logList = purseSerivce.getExtractionChannelLog(Integer.parseInt(id));
			model.put("channelMap", channelMap);
			model.put("logList", logList);
			return "/purse/bagExtractionChannelDetail";
		}
		
		//自动审核时长
		@RequestMapping(value = "/autoCheckSet")
		public String autoCheckSet(final ModelMap model, @RequestParam
				Map<String, String> params) {
			List<Map<String, Object>> list = purseSerivce.getExtractionChannel(params);
			Map<String,Object> map = list.get(0);
			//String check_rate = map.get("check_rate")
			model.put("check_rate", map.get("check_rate"));
			model.put("params", params);
			return "/purse/autoCheckSet";
		}
		//更新自动审核的审核时长
		@RequestMapping(value = "/updateChannelAutoCheck")
		public void updateChannelAutoCheck(final ModelMap model, @RequestParam
				Map<String, String> params,HttpServletResponse response) {
			//String id = params.get("id");
			String check_rate = params.get("check_rate");
			String auto_check_state = params.get("auto_check_state");
			String operater = getUser().getRealName();
			String content = null;
			if(StringUtils.isNotEmpty(auto_check_state)){
				if("1".equals(auto_check_state)){
					//content = "开启通道自动审核且时长设置为"+check_rate;
					content = "开启通道自动审核";
				}else if("0".equals(auto_check_state)){
					content = "关闭通道自动审核";
				}
			}
			System.out.println("check_rate="+check_rate+",auto_check_state="+auto_check_state);
			if(StringUtils.isNotEmpty(auto_check_state)){
				Boolean result = purseSerivce.updateChannelAutoCheck(Integer.parseInt(auto_check_state), check_rate,operater,content);
				if(result){
					outText("操作成功", response);
				}else{
					outText("系统异常", response);
				}
			}
		}
		
		//更改秒出账开关
		@RequestMapping(value = "/realTimeCheckStatusChange")
		public void realTimeCheckStatusChange(final ModelMap model, @RequestParam
				Map<String, String> params,HttpServletResponse response) {
			String real_time_state = params.get("real_time_state");
			String operater = getUser().getRealName();
			String content = null;
			if(StringUtils.isNotEmpty(real_time_state)){
				if("1".equals(real_time_state)){
					//content = "开启通道自动审核且时长设置为"+check_rate;
					content = "开启秒出账";
				}else if("0".equals(real_time_state)){
					content = "关闭秒出账";
				}
			}
			System.out.println("real_time_state="+real_time_state);
			if(StringUtils.isNotEmpty(real_time_state)){
				Boolean result = purseSerivce.updateRealTimeExtraction(Integer.parseInt(real_time_state), operater,content);
				if(result){
					outText("操作成功", response);
				}else{
					outText("系统异常", response);
				}
			}
		}
		
		//修改预警手机号码
		@RequestMapping(value = "/updateWarnPhone")
		public void updateWarnPhone(final ModelMap model, @RequestParam
				Map<String, String> params,HttpServletResponse response) {
			String warnMobileNo = params.get("warnMobileNo");
			String operater = getUser().getRealName();
			String content = operater+"修改预警手机号码为"+warnMobileNo;
			System.out.println("content="+content);
			Boolean result = purseSerivce.updateWarnPhone(warnMobileNo, operater, content);
			if(result){
				outText("修改预警手机号成功", response);
			}else{
				outText("系统异常", response);
			}
		}
		
		//开启、关闭通道
		@RequestMapping(value = "/updateChannelState")
		public void updateChannelState(final ModelMap model, @RequestParam
				Map<String, String> params,HttpServletResponse response) {
			//id为1->中信银行ecitic    id为2->结算中心szfs
			String id = params.get("id");
			String channelState = params.get("channelState");
			String operater = getUser().getRealName();
			String content = null;
			//String channel = null;
			if(StringUtils.isNotEmpty(channelState)){
				if("1".equals(channelState)){
					content = "开启通道";
					Map<String, Object> channelMap = purseSerivce.getExtractionChannelById(id);
					/*if("1".equals(id)){
						channel = "ecitic";
					}else if("2".equals(id)){
						channel = "szfs";
					}*/
					//channel = channelMap.get("channel_code").toString();
				}else if("0".equals(channelState)){
					content = "关闭通道";
				}
			}
			System.out.println("id="+id+",channelState="+channelState);
			if(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(channelState)){
				Boolean result = purseSerivce.updateChannelState(Integer.parseInt(id), Integer.parseInt(channelState),operater,content);
				if(result){
					outText("操作成功", response);
				}else{
					outText("系统异常", response);
				}
			}
		}
		  

		/**
		 * 
		 * 功能：钱包提现查询
		 *
		 * @param model
		 * @param params
		 * @param cpage
		 * @return
		 */
	  @RequestMapping(value = "/bagExtractionQuery")
	  public String bagExtractionQuery(final ModelMap model,
	      @RequestParam Map<String, String> params,
	      @RequestParam(value = "p", defaultValue = "1") int cpage) {
	    Page<Map<String, Object>> list=null;
	    int pageNum = PAGE_NUMERIC;
	    String pageSize = params.get("pageSize");
	    
	    try {
	      pageNum = Integer.parseInt(pageSize);
	    } catch (Exception e) {
	    }
	    if (20 != pageNum && 50 != pageNum && 100 != pageNum && 500 != pageNum) {
	      pageNum = PAGE_NUMERIC;
	    }
	    if (params.get("startDate") == null
				&& params.get("endDate") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime +" 23:59:59";
			params.put("startDate", createTimeBegin);
			params.put("endDate", createTimeEnd);
		}
	    log.info("---------提现查询------pageNum="+pageNum);
	    long time1=System.currentTimeMillis(); 
	    log.info("----提现查询开始时间time1----"+time1);
	    PageRequest page = new PageRequest(cpage - 1, pageNum);
	    //提现所有审核人
	    List<Map<String, Object>> checkerList = purseSerivce.getChecker();
	    //所有的代付通道
	    List<Map<String, Object>> channelList = purseSerivce.getExtractionChannel(null);
	    list = purseSerivce.getBagExtractionList(params,page);
	    long time2=System.currentTimeMillis(); 
	    log.info("----查询运行时间-----"+(time2-time1)+"ms");
	    for (Map<String, Object> map : list) {
	      map.put("openStatusDesc", DictCache.getDictName("purse_cash_status", (String)map.get("open_status")));
	      map.put("purseBalanceTypeDesc", DictCache.getDictName("purse_balance_type", map.get("settle_days").toString()));
	      String cashChannel=(String)map.get("cash_channel");
	      if (!StringUtil.isBlank(cashChannel)) {
	        cashChannel=cashChannel.toLowerCase();
          }
	      map.put("cashChannelDesc", DictCache.getDictName("settle_transfer_bank", cashChannel));
	    }
	    long time3=System.currentTimeMillis(); 
	    log.info("----遍历运行时间-----"+(time3-time3)+"ms");
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    model.put("p", cpage);
	    model.put("list", list);
	    model.put("checkerList", checkerList);
	    model.put("channelList", channelList);
	    model.put("params", params);
	    model.put("today", sdf.format(new Date()));
	    model.put("bagUrl", SysConfig.value("bagUrl"));
	    model.put("purseCashStatusList", DictCache.getList("purse_cash_status"));
	    model.put("purseBalanceTypeList", DictCache.getList("purse_balance_type"));
	    return "/purse/bagExtractionQuery";
	  }
	  
	  
	//钱包提现手工开关
	@RequestMapping(value = "/bagExtractionManualSwitch")
	public String bagExtractionManualSwitch(final ModelMap model, @RequestParam
			Map<String, String> params) {
		Map<String, Object> map = purseSerivce.bagExtractionManualSwitchQuery();
		model.put("map", map);
		return "/purse/bagExtractionManualSwitch";
	}
	
	//更新钱包提现手工开关信息
	@RequestMapping(value = "/updateExtractionManualSwitch")
	public void updateExtractionManualSwitch(final ModelMap model, @RequestParam
			Map<String, String> params,HttpServletResponse response) {
		String id = params.get("id");
		String hoursBegin = params.get("hoursBegin");
		String minutesBegin = params.get("minutesBegin");
		String hoursEnd = params.get("hoursEnd");
		String minutesEnd = params.get("minutesEnd");
		String opinionTextArea = params.get("opinionTextArea");
		String manualSwitch = params.get("manualSwitch");
		String notice = null;
		if(StringUtils.isNotEmpty(hoursBegin) && StringUtils.isNotEmpty(minutesBegin) && 
				StringUtils.isNotEmpty(hoursEnd) && StringUtils.isNotEmpty(minutesEnd)){
			String minutesBeginStr = minutesBegin;
			String minutesEndStr = minutesEnd;
			if("0".equals(minutesBegin)){
				minutesBeginStr = "00";
			}
			if("0".equals(minutesEnd)){
				minutesEndStr = "00";
			}
			notice = "提现时间为:"+hoursBegin+":"+minutesBeginStr+"—"+hoursEnd+":"+minutesEndStr;
		}
		if("0".equals(manualSwitch)){
			notice = opinionTextArea;
		}
		if(StringUtils.isNotEmpty(id)){
			try {
				purseSerivce.bagExtractionManualSwitchUpdate(Integer.parseInt(id), hoursBegin, 
						minutesBegin, hoursEnd, minutesEnd, Integer.parseInt(manualSwitch), 
						opinionTextArea, notice);
				outText("操作成功", response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				outText("系统异常", response);
			}
			
		}
	}

	  
	  	// 钱包提现查询导出excel
		@RequestMapping(value = "/bagHisExport")
		public void bagHisExport(@RequestParam Map<String, String> params,
				HttpServletResponse response, HttpServletRequest request) {
			OutputStream os = null;
			
			try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);
			String fileName = "bag_his"+sdf.format(new Date())+"_"+random+".xls";
			
			
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename="
						+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				expordBagCashExcel(os, params, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(os != null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		
		/**
		 * 钱包提现导出Excel报表
		 * 
		 * */
		private void expordBagCashExcel(OutputStream os, Map<String, String> params, String fileName)
				throws Exception {
			
			try {
				
				List<Map<String, Object>> list = purseSerivce.getBagExpordList(params);
				
				int row = 3; // 从第三行开始写
				int col = 0; // 从第一列开始写
				for (Map<String, Object> map : list) {
				      map.put("openStatusDesc", DictCache.getDictName("purse_cash_status", (String)map.get("open_status")));
				      map.put("purseBalanceTypeDesc", DictCache.getDictName("purse_balance_type", map.get("settle_days").toString()));
				}
				    

				Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/BOSS_BAG_HIS.xls"));

				WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
				WritableSheet ws = wwb.getSheet(0);
				
				
				
				WritableCellFormat cellFormatRight = new WritableCellFormat();
				cellFormatRight.setAlignment(Alignment.RIGHT);
				
				
				for (int i = 0; i < list.size(); i++) {
					
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("merchant_no"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("account_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("openStatusDesc"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("create_time"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("mobile_no"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("app_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("purseBalanceTypeDesc"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("amount")),cellFormatRight));
					
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("fee"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("settle_amount"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("check_person"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("cnaps"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("bank_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("account_no"))));
								
					row++;
					col = 0;
				}
				
				
				wwb.write();
				wwb.close();
				wb.close();
				os.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
				
			
		}
		
		
		/**
		 * 红包换购导出Excel报表
		 * 
		 * */
		private void redBuyMachinesExcel(OutputStream os, Map<String, String> params, String fileName)
				throws Exception {
			
			try {
				List<Map<String, Object>> list = purseSerivce.redBuyMachinesExport(params);
				
				int row = 3; // 从第三行开始写
				int col = 0; // 从第一列开始写
				for (Map<String, Object> map : list) {
					Object machines1Name = map.get("machines1_name");
					Object machines2Name = map.get("machines2_name");
					Object status = map.get("status");
					if(machines1Name!=null && !"".equals(machines1Name)){
						if("0".equals(machines1Name.toString())){
							map.put("machines1_name", "超级刷");
						}else if("1".equals(machines1Name.toString())){
							map.put("machines1_name", "M-posⅢ代");
						}
					}
					if(machines2Name!=null && !"".equals(machines2Name)){
						if("0".equals(machines2Name.toString())){
							map.put("machines2_name", "超级刷");
						}else if("1".equals(machines2Name.toString())){
							map.put("machines2_name", "M-posⅢ代");
						}
					}
					if(status!=null && !"".equals(status)){
						if("0".equals(status.toString())){
							map.put("status", "有效");
						}else if("1".equals(status.toString())){
							map.put("status", "已被受理");
						}else if("2".equals(status.toString())){
							map.put("status", "受理成功");
						}
					}
				}
				    

				Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/BOSS_RED.xls"));

				WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
				WritableSheet ws = wwb.getSheet(0);
				
				
				
				WritableCellFormat cellFormatRight = new WritableCellFormat();
				cellFormatRight.setAlignment(Alignment.RIGHT);
				
				
				for (int i = 0; i < list.size(); i++) {
					
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("mobile_no"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("app_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("real_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("machines1_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("machines1_amount")),cellFormatRight));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("machines1_num"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("contact_people1"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("contact_phone1"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("address1"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("machines2_name"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("machines2_amount"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("machines2_num"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("contact_people2"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("contact_phone2"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("address2"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("status"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("agent_no"))));
					ws.addCell(new Label(col++,  row, StringUtil.filterNull(list.get(i).get("agent_sure_time"))));
								
					row++;
					col = 0;
				}
				
				
				wwb.write();
				wwb.close();
				wb.close();
				os.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
				
			
		}
		
		// 红包换购导出Excel报表
		@RequestMapping(value = "/redExport")
		public void redExport(@RequestParam Map<String, String> params,
				HttpServletResponse response, HttpServletRequest request) {
			OutputStream os = null;
			log.info("---------红包换购导出Excel报表----------");
			try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int random = (int) (Math.random() * 1000);
			String fileName = "red"+sdf.format(new Date())+"_"+random+".xls";
			
			
				request.setCharacterEncoding("UTF-8");
				os = response.getOutputStream(); // 取得输出流
				response.reset(); // 清空输出流
				response.setHeader("Content-disposition", "attachment;filename="
						+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
				redBuyMachinesExcel(os, params, fileName);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(os != null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
  		
		
		// 钱包充值查询
		@RequestMapping(value = "/bagRechargeQuery")
		public String bagRechargeQuery(final ModelMap model, @RequestParam
		Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
		int cpage) {
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			Page<Map<String, Object>> list = purseSerivce.getBagRechargeList(params,
					page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			return "/purse/bagRechargeQuery";
		}
		
		// 钱包提现详情
		@RequestMapping(value = "/bagExtraDetail")
		public String bagExtraDetail(final ModelMap model, @RequestParam
		Map<String, String> params) throws Exception {
			try {
				String id = params.get("id");
				Map<String, Object> blackMap = purseSerivce.getBagExtractionDetailById(Long.parseLong(id));
				model.put("params", blackMap);
				model.put("openStatusDesc", DictCache.getDictName("purse_cash_status", blackMap.get("open_status").toString()));
				model.put("purseBalanceType", DictCache.getDictName("purse_balance_type", blackMap.get("settle_days").toString()));
				String cashChannel=(String)blackMap.get("cash_channel");
				if (!StringUtil.isBlank(cashChannel)) {
				  cashChannel=cashChannel.toLowerCase();
        }
				model.put("cashChannelDesc", DictCache.getDictName("settle_transfer_bank", cashChannel));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return "/purse/bagExtraDetail";
		}
		
		
		// 钱包充值详情
		@RequestMapping(value = "/bagRechargeDetail")
		public String bagRechargeDetail(final ModelMap model, @RequestParam
		Map<String, String> params) throws Exception {
			String id = params.get("id");
			Map<String, Object> blackMap = purseSerivce.getBagRechargeDetailById(Long
					.parseLong(id));
			model.put("params", blackMap);
			return "/purse/bagRechargeDetail";
		}
		
		//审核
		@RequestMapping(value = "/checkRecharge")
		public void checkRecharge(final ModelMap model,
				@RequestParam Map<String, String> params,HttpServletRequest request,HttpServletResponse response) throws SQLException{
			String id=params.get("id");
			Map<String, Object> paramMap = purseSerivce.getBagExtractionDetailById(new Long(id));
			
			int num = 0;
			if(paramMap.get("bank_no") == null){
				params.put("check_remark", "提现银行为空");
				purseSerivce.extractionModify(params,getUser().getId(),getUser().getRealName());
				Map<String, Object> map=new HashMap<String, Object>();
			    map.put("id",paramMap.get("id"));
			    map.put("mobile_no",paramMap.get("mobile_no"));
			    map.put("account_no",paramMap.get("account_no"));
			    map.put("account_name",paramMap.get("account_name"));
			    map.put("amount", paramMap.get("amount"));
			    map.put("settle_days",paramMap.get("settle_days"));
			    List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			    list.add(map);
			    Map<String, Object> tem = new HashMap<String, Object>();
			    tem.put("remark", "审核失败,余额冲正");
			    tem.put("channel", "CASH_CHECK");
			    tem.put("list", list);
			    log.info("调用钱包结算：" + tem);
//			    purseRecService.setParams(tem);
//			    new Thread(purseRecService).start();
			    new PurseRecService(tem,purseSerivce).start();
			    outText("提现银行为空，审核失败，已经进行余额冲正。", response);
			    return ;
			}else{
				num=purseSerivce.checkRecharges(id,getUser().getId(),getUser().getRealName());
			}
			if(num>0){
				outText("审核成功。", response);
			}else{
				outText("审核失败，请检查数据。", response);
				
			}
		}
		
		
		//累计审核数量到批量审核页面
		@RequestMapping(value = "/viewChecker")
		public String viewChecker(final ModelMap model, @RequestParam(value = "p", defaultValue = "1") int cpage, @RequestParam String ids,HttpServletRequest request,HttpServletResponse response)
				throws Exception {
			String[] is = null;
			if (StringUtils.isNotEmpty(ids)) {
				is = ids.split(",");
			}
			model.put("ids", ids);
			model.put("c", is.length);
			return "/purse/bagExtractionCheck";
		}
		
		// 批量审核
		@RequestMapping(value = "/check")
		public void check(final ModelMap model,
				@RequestParam String id,HttpServletResponse response, HttpServletRequest request)
				throws Exception {
			try {
				boolean falg = false;
				if (StringUtils.isNotEmpty(id)) {
					String[] ids = id.split(",");
					 List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					for (String i : ids) {
						Map<String, Object> paramMap = purseSerivce.getBagExtractionDetailById(new Long(i));
						
						if(paramMap.get("bank_no") == null){
							
							Map<String, String> params = new HashMap<String, String>();
							params.put("id", i);
							params.put("check_remark", "提现银行为空");
							purseSerivce.extractionModify(params,getUser().getId(),getUser().getRealName());
							Map<String, Object> map=new HashMap<String, Object>();
						    map.put("id",paramMap.get("id"));
						    map.put("mobile_no",paramMap.get("mobile_no"));
						    map.put("account_no",paramMap.get("account_no"));
						    map.put("account_name",paramMap.get("account_name"));
						    map.put("amount", paramMap.get("amount"));
						    map.put("settle_days",paramMap.get("settle_days"));
						    list.add(map);
						    
						}else{
							purseSerivce.checkRecharges(i,getUser().getId(),getUser().getRealName());      //改变审核状态
							falg = true;
						}
					}
					if(list.size() >0){
						Map<String, Object> tem = new HashMap<String, Object>();
					    tem.put("remark", "审核失败,余额冲正");
					    tem.put("channel", "CASH_CHECK");
					    tem.put("list", list);
					    log.info("调用钱包结算：" + tem);
//					    purseRecService.setParams(tem);
//					    new Thread(purseRecService).start();
					    new PurseRecService(tem,purseSerivce).start();
					    falg = false;
					}
				}
				if (falg) {
					outText("已全部审核成功", response);
				} else {
					outText("部分提现提现银行为空，已经自动审核失败", response);
				}
			} catch (Exception e) {
				e.printStackTrace();
				outText("系统忙 稍后再试", response);
			}
		}
		
		
		//跳转到审核失败页面
		@RequestMapping(value = "/viewReason")
		public String viewReason(final ModelMap model, @RequestParam(value = "p", defaultValue = "1") int cpage, @RequestParam String id,HttpServletRequest request,HttpServletResponse response)
				throws Exception {
			Map<String, Object> map = purseSerivce.getBagExtractionDetailById(Long.parseLong(id));
			model.put("params", map);
			return "/purse/bagExtractionFail";
		}
				
		
		/**
		 * 审核失败保存
		 * @param model
		 * @param request
		 * @param response
		 * @param params
		 * @return
		 * @throws SQLException
		 */
		@RequestMapping(value = "/extractionFailSave")
		public String  extractionFailSave(final ModelMap model, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> params) throws SQLException {
			try
			{
				//将审核失败信息存入数据库
				purseSerivce.extractionModify(params,getUser().getId(),getUser().getRealName());
				List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
			    Map<String, Object> map=new HashMap<String, Object>();
			    map.put("id",params.get("id"));
			    map.put("mobile_no",params.get("mobile_no"));
			    map.put("account_no",params.get("account_no"));
			    map.put("account_name",params.get("account_name"));
			    map.put("amount", params.get("amount"));
			    map.put("settle_days",params.get("settle_days"));
			    list.add(map);
			    Map<String, Object> tem = new HashMap<String, Object>();
			    tem.put("remark", "审核失败,余额冲正");
			    tem.put("channel", "CASH_CHECK");
			    tem.put("list", list);
			    log.info("调用钱包结算：" + tem);
//			    purseRecService.setParams(tem);
//			    new Thread(purseRecService).start();
			    new PurseRecService(tem,purseSerivce).start();
				model.put("params", params);
				model.put("flag", "1");
				model.put("errorMessage","");
		
			}catch (SQLException e)
			{
				e.printStackTrace();
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
			return "/purse/bagExtractionFail";
	}
		
	/**
	 * 
	 * 功能：入账查询
	 *
	 * @param model
	 * @param params
	 * @param cpage
	 * @return
	 */
	  @RequestMapping(value = "/bagTransQuery")
	  public String bagTransQuery(final ModelMap model, @RequestParam
	  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
	    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
	   
	    try {
	    	if (params.get("startDate") == null
					&& params.get("endDate") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime +" 23:59:59";
				params.put("startDate", createTimeBegin);
				params.put("endDate", createTimeEnd);
			}
		    Page<Map<String, Object>> list = purseSerivce.queryPurseTrans(params, page);
		    for (Map<String, Object> map : list.getContent()) {
	        map.put("statusDesc", DictCache.getDictName("purse_trans_status", (String)map.get("status")));
	      }
		    model.put("p", cpage);
		    model.put("list", list);
		    model.put("params", params);
		    model.put("purseStatusList", DictCache.getList("purse_trans_status"));
		    
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	    
	    
	    return "/purse/bagTransQuery";
	  }
	  
	  /**
	   * 
	   * 功能：入账详情
	   *
	   * @param model
	   * @param params
	   * @return
	   * @throws Exception
	   */
	  @RequestMapping(value = "/bagTransDetail")
    public String bagTransDetail(final ModelMap model, @RequestParam
    Map<String, String> params) throws Exception {
		  try {
		  String id = params.get("id");
	      Map<String, Object> transMap = purseSerivce.queryPureseTransDetail(id);
	      model.addAllAttributes(transMap);
	      model.addAttribute("statusDesc", DictCache.getDictName("purse_trans_status", (String)transMap.get("status")));
	      model.addAttribute("handStatusDesc", DictCache.getDictName("purse_trans_hand_status", (String)transMap.get("hand_status")));
	     
		  } catch (Exception e) {
				// TODO: handle exception
			  e.printStackTrace();
		  }
	       return "/purse/bagTransDetail";
    }
	  
	  
	  /**
	   * 
	   * 功能：手工入账
	   *
	   * @param params
	   * @param response
	   * @throws IOException
	   */
	  @RequestMapping(value = "/bagHandTrans")
	  public void bagHandTrans(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
	    String msg="";
	    String id = params.get("id");
      Map<String, Object> transMap = purseSerivce.queryPureseTransDetail(id);
      if (transMap==null) {
        msg="原始入账流水不存在";
        response.getWriter().write(msg);
        return ;
      }
	    String status=(String)transMap.get("status");
	    if (!"2".equals(status)&&!"3".equals(status)) {
	      msg="原始入账流水状态为【"+DictCache.getDictName("purse_trans_status", status)+"】，不能手工入账";
        response.getWriter().write(msg);
        return ;
      }
	    
	    String merchantNo=transMap.get("merchant_no").toString();
	    Map<String, Object> merchantMap=purseSerivce.queryMerchantByNo(merchantNo);
	    String appType=""+merchantMap.get("app_no").toString();
	    
	    Map<String, String> paramsMap=new HashMap<String, String>();
	    paramsMap.put("settleDays", "0");
		if(transMap.get("create_time")!=null){
			try {
				String createTime = transMap.get("create_time").toString();
				Date date = new Date();  
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");  
				String nowDateStr = sf.format(date);
				Date nowDate = sf.parse(nowDateStr);
				Date createDate = sf.parse(createTime);
				log.info("-------nowDate="+nowDate+"-------createDate="+sf.format(createDate));
				if(nowDate.after(createDate)){
					paramsMap.put("settleDays", "1"); 
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.info("------钱包手工入账判断时间出错---------");
				e.printStackTrace();
			}
		}
	    paramsMap.put("id", id);
	    paramsMap.put("transId", transMap.get("trans_id").toString());
	    paramsMap.put("mobileNo", transMap.get("mobile_no").toString());
	    paramsMap.put("amount", transMap.get("amount").toString());
	    paramsMap.put("orderNo", transMap.get("order_no").toString());
	    paramsMap.put("cardNo", transMap.get("card_no").toString());
	    paramsMap.put("merchantNo", transMap.get("merchant_no").toString());
	    paramsMap.put("merchantName", transMap.get("merchant_name").toString());
	    paramsMap.put("handNum", transMap.get("hand_num").toString());
	    paramsMap.put("appType",appType);
	    
//	    msg=purseTransService.purseTrans(paramsMap);
	    PurseTransService purseTrans=new PurseTransService(purseService, paramsMap);
	    msg=purseTrans.purseTrans();
	    response.getWriter().write(msg);
	    return ;
	  }
	  

	  
		

		// 锁定钱包商户
	  @RequestMapping(value = "/bagUserLock")
	  public void bagUserLock(HttpServletRequest request,
				HttpServletResponse response,
				@RequestParam Map<String, String> params) {
			String userId = params.get("userId");
			String lockStr = params.get("status_lock");
			try {
				purseSerivce.updateBagUserStatus(userId,lockStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("----------解冻/冻结出错------------"+e.getMessage());
				e.printStackTrace();
			}
	  }
	  
	  @RequestMapping(value="/bagFreezeDetail")
	  public String bagFreezeDetail(ModelMap model, @RequestParam Map<String, String> params){
		  
		  String mobileNo = params.get("mobileNo");
		  String appType = params.get("appType");
		  String type = params.get("type");
		  log.info("==========冻结信息查询==========开始====");
		  Map<String, Object>	freezeInfo = purseSerivce.selectFreezeInfo(mobileNo, appType);
		  Map<String, Object> freezeAmount = purseSerivce.selectFreezeAmount(mobileNo, appType);
		  if(freezeInfo == null){
			  freezeInfo = new HashMap<String, Object>();
			  freezeInfo.put("operater", "暂无");
		  }
		  
		  if(freezeAmount == null){
			  freezeAmount = new HashMap<String, Object>();
		  }
		  log.info("==========计算金额===========");
		  double transAmount = Double.parseDouble(freezeAmount.get("retention_money") == null ? "0":freezeAmount.get("retention_money").toString());
		  double userAmount = Double.parseDouble(freezeAmount.get("manual_retention_money") == null ? "0":freezeAmount.get("manual_retention_money").toString());
		  double totalAmount = transAmount + userAmount;
		  freezeInfo.put("transAmount", transAmount);
		  freezeInfo.put("userAmount", userAmount);
		  freezeInfo.put("totalAmount", totalAmount);
		  freezeInfo.put("mobile_no", mobileNo);
		  freezeInfo.put("app_type", appType);
		  freezeInfo.put("curdate", DateUtils.getCurrentDateTime()); 
		 
		  freezeInfo.put("freezeDay", freezeInfo.get("freeze_day"));
		  
		  model.put("params", freezeInfo);
		  
		  if(type.equals("1")){
			  log.info("==============返回冻结====type："+type);
			  return "/purse/bagFreeze";
		  } else{
			  log.info("==============返回解冻====type："+type);
			  return "/purse/bagUnFreeze";
		  }
	  }
	  
	  
	  @RequestMapping(value="/freezeBag")
	  public void freezeBagAmount(HttpServletResponse response,@RequestParam Map<String, String> params){
		  params.put("operater", getUser().getRealName());
		  params.put("createTime", DateUtils.getCurrentDateTime());
		  params.put("operation", "0");
		  String freezeDay = params.get("freezeDay");
		  String date ="";
		  if(freezeDay.equals("0")){
			  date = DateUtils.getCurrentDateTime();
		  }else{
			  date = DateUtils.format(DateUtils.addDate(new Date(), Integer.parseInt(freezeDay)), "yyyy-MM-dd HH:mm:ss");
		  }
		  
		  log.info("=========修改风险冻结金额开始==========");
		  int res = purseSerivce.freezeUserAmount(0,params.get("mobileNo"),date, params.get("appType"), params.get("amount"));
		  log.info("========风险冻结金额修改====res:"+res);
		  if(res>0){
			  log.info("--------------插入冻结记录------开始--");
			 try {
				purseSerivce.addFreezeRecord(params);
				outText("冻结成功", response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("========出异常了======="+e.getMessage());
				outText("有点小问题 ，等下再试咯~~", response);
			}
			 
		  }else{
			  
			  	log.info("========修改风险冻结失败======="+com.alibaba.fastjson.JSONObject.toJSONString(params));
				outText("修改风险冻结失败了，等下再试咯~~", response);
		  }
		  
	  }
	  
	  @RequestMapping(value="/unFreezeBag")
	  public void unFreezeBagAmount(HttpServletResponse response,@RequestParam Map<String, String> params){
		  log.info("============解冻开始===========");
		  params.put("operater", getUser().getRealName());
		  params.put("createTime", DateUtils.getCurrentDateTime());
		  params.put("operation", "1");
		  
		  log.info("=========修改风险冻结金额开始==========mobileNo:"+params.get("mobileNo")+"apptype:"+params.get("appType")+"date:");
		  int res = purseSerivce.freezeUserAmount(1,params.get("mobileNo"),null, params.get("appType"), params.get("amount"));
		  log.info("========风险冻结金额修改====res:"+res);
		  if(res>0){
			  log.info("--------------插入解冻记录------开始--");
			 try {
				purseSerivce.addFreezeRecord(params);
				outText("解冻金额成功", response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("========出异常了======="+e.getMessage());
				outText("有点小问题 ，等下再试咯~~", response);
			}
			 
		  }else{
			  
			  	log.info("========修改风险冻结失败======="+com.alibaba.fastjson.JSONObject.toJSONString(params));
				outText("修改风险冻结失败了，等下再试咯~~", response);
		  }
		  
	  }
	  
	  
	  
	  /**
	   * 
	   * 功能：冲正查询
	   *
	   * @param model
	   * @param params
	   * @param cpage
	   * @return
	   */
	    @RequestMapping(value = "/bagReversalQuery")
	    public String bagReversalQuery(final ModelMap model, @RequestParam
	    Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
	      PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
	      try {
	    	  if(params.get("startDate")== null &&params.get("endDate")==null){
		    	  	Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String createTime = sdf.format(date);
					
					String createTimeBegin = createTime + " 00:00:00";
					String createTimeEnd = createTime +" 23:59:59";
					params.put("startDate", createTimeBegin);
					params.put("endDate", createTimeEnd);
		      }
		      Page<Map<String, Object>> list = purseSerivce.queryReversalTrans(params, page);
		      
		      for (Map<String, Object> map : list.getContent()) {
		        map.put("statusDesc", DictCache.getDictName("purse_cash_status", (String)map.get("open_status")));
		        map.put("backStatusDesc", DictCache.getDictName("purse_reversal_status", (String)map.get("back_status")));
		      }
		      model.put("p", cpage);
		      model.put("list", list);
		      model.put("params", params);
		      
		      
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	      return "/purse/bagReversalQuery";
	    }
	  
	    @RequestMapping(value = "/countBagReverSal")
	    public void countBagReverSal(HttpServletResponse response,@RequestParam Map<String, String> params){
	    	Map<String, Object> param = purseSerivce.countBagReverSal(params);
	    	String json = com.alibaba.fastjson.JSONObject.toJSONString(param);
	    	outJson(json, response);
	    }
	    
	  
	    /**
	     * 
	     * 功能：需手动冲正的提现详情
	     *
	     * @param model
	     * @param params
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/bagReversalDetail")
	    public String bagReversalDetail(final ModelMap model, @RequestParam
	    Map<String, String> params) throws Exception {
	      try {
	    	  String id = params.get("id");
		      Map<String, Object> reversalMap = purseSerivce.getBagExtractionDetailById(Long.parseLong(id));
		      
		      model.addAllAttributes(reversalMap);
		      model.addAttribute("statusDesc", DictCache.getDictName("purse_cash_status", (String)reversalMap.get("open_status")));
		      model.addAttribute("backStatusDesc", DictCache.getDictName("purse_reversal_status", (String)reversalMap.get("back_status")));
		      
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	      return "/purse/bagReversalDetail";
	    }
	    
	     
	    /**
	     * 
	     * 功能：手工冲正
	     *
	     * @param params
	     * @param response
	     * @throws IOException
	     */
	    @RequestMapping(value = "/bagHandReversal")
	    public void bagHandReversal(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
	      String msg="";
	      String id = params.get("id");
	      Map<String, Object> reversalMap=null;
        try {
          reversalMap = purseSerivce.getBagExtractionDetailById(Long.parseLong(id));
        } catch (NumberFormatException e) {
          e.printStackTrace();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        
	      if (reversalMap==null) {
	        msg="原始提现流水不存在";
	        response.getWriter().write(msg);
	        return ;
	      }
	      String status=(String)reversalMap.get("open_status");
	      if (!"2".equals(status)&&!"5".equals(status)) {
	        msg="原始提现流水状态为【"+DictCache.getDictName("purse_cash_status", status)+"】，不能手工冲正";
	        response.getWriter().write(msg);
	        return ;
	      }
	      
	      List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
	      list.add(reversalMap);
	      
	      Map<String, Object> reversalParams= new HashMap<String, Object>();
	      reversalParams.put("remark", "手工冲正");
	      reversalParams.put("channel", "Hand");
	      reversalParams.put("list", list);
	      
	      purseRecService.setParams(reversalParams);
	      CloseableHttpClient http = HttpClients.createDefault();
	      purseRecService.balanceRec(http,purseRecService.getParams());
	      
	      msg="手工冲正请求已经发送";
	      response.getWriter().write(msg);
	      return ;
	    }
	    
	    
	    /**
	     * 
	     * 功能：钱包交易查询
	     *
	     * @param model
	     * @param params
	     * @param cpage
	     * @return
	     */
	    @RequestMapping(value = "/bagTransactionQuery")
      public String bagTransactionQuery(final ModelMap model, @RequestParam
      Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
	      int totalNum=0;
	      int lessNum=0;
	      int moreNum=0;
	      int successNum=0;
	      int faildNum=0;
	      int unNum=0;
	      BigDecimal totalBigDecimal=new BigDecimal("0");
	      BigDecimal lessBigDecimal=new BigDecimal("0");
	      BigDecimal moreBigDecimal=new BigDecimal("0");
	      BigDecimal successBigDecimal=new BigDecimal("0");
	      BigDecimal faildBigDecimal=new BigDecimal("0");
	      BigDecimal unBigDecimal=new BigDecimal("0");
	      
        PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
        String today=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        /*if (StringUtil.isBlank(params.get("startDate"))) {
          params.put("startDate", today);
        }
        if (StringUtil.isBlank(params.get("endDate"))) {
          params.put("endDate", today);
        }*/
        if (params.get("startDate") == null
				&& params.get("endDate") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime +" 23:59:59";
			params.put("startDate", createTimeBegin);
			params.put("endDate", createTimeEnd);
		}
        
        Page<Map<String, Object>> list = purseSerivce.queryTransaction(params, page);
        for (Map<String, Object> map : list.getContent()) {
          String transTye=map.get("trans_type").toString();
          String status=map.get("result_status").toString();
          BigDecimal amountBigDecimal=(BigDecimal)map.get("amount");
          if ("less".equals(transTye)) {
            lessNum++;
            lessBigDecimal=lessBigDecimal.add(amountBigDecimal);
          }else if ("more".equals(transTye)) {
            moreNum++;
            moreBigDecimal=moreBigDecimal.add(amountBigDecimal);
          }
          if ("1".equals(status)) {
            successNum++;
            successBigDecimal=successBigDecimal.add(amountBigDecimal);
          }else if ("2".equals(status)) {
            faildNum++;
            faildBigDecimal=faildBigDecimal.add(amountBigDecimal);
          }else {
            unNum++;
            unBigDecimal=unBigDecimal.add(amountBigDecimal);
          }
          
          totalNum++;
          totalBigDecimal=totalBigDecimal.add(amountBigDecimal);
          
          map.put("transTypeDesc", DictCache.getDictName("purse_trans_type", (String)map.get("trans_type")));
          map.put("transactionStatusDesc", DictCache.getDictName("purse_transaction_status", map.get("result_status").toString()));
        }
        model.put("p", cpage);
        model.put("list", list);
        model.put("params", params);
        model.put("purseTransTypeList", DictCache.getList("purse_trans_type"));
        model.put("purseTransactionStatusList", DictCache.getList("purse_transaction_status"));
        
        SecurityUtils.getSubject().getSession().setAttribute("totalNum", ""+totalNum);
        SecurityUtils.getSubject().getSession().setAttribute("lessNum", ""+lessNum);
        SecurityUtils.getSubject().getSession().setAttribute("moreNum", ""+moreNum);
        SecurityUtils.getSubject().getSession().setAttribute("successNum", ""+successNum);
        SecurityUtils.getSubject().getSession().setAttribute("faildNum", ""+faildNum);
        SecurityUtils.getSubject().getSession().setAttribute("unNum", ""+unNum);
        SecurityUtils.getSubject().getSession().setAttribute("totalAmount", totalBigDecimal.toPlainString());
        SecurityUtils.getSubject().getSession().setAttribute("lessAmount", lessBigDecimal.toPlainString());
        SecurityUtils.getSubject().getSession().setAttribute("moreAmount", moreBigDecimal.toPlainString());
        SecurityUtils.getSubject().getSession().setAttribute("successAmount", successBigDecimal.toPlainString());
        SecurityUtils.getSubject().getSession().setAttribute("faildAmount", faildBigDecimal.toPlainString());
        SecurityUtils.getSubject().getSession().setAttribute("unAmount", unBigDecimal.toPlainString());
        
        SecurityUtils.getSubject().getSession().setAttribute("purseTransList", list.getContent());
        return "/purse/bagTransactionQuery";
      }
	    
	    
	    /**
	     * 
	     * 功能：钱包交易数据统计
	     *
	     * @param params
	     * @param response
	     * @throws IOException
	     */
	    @RequestMapping(value = "/transactionStatistic")
	    public void transactionStatistic(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
	      String errCode="success";
	      String errMsg="";
	      BigDecimal lessBigDecimal=new BigDecimal("0");
	      BigDecimal moreBigDecimal=new BigDecimal("0");
	      BigDecimal todayBalanceBigDecimal=new BigDecimal("0");
	      BigDecimal hisBalanceBigDecimal=new BigDecimal("0");
	      BigDecimal totalBalanceBigDecimal=new BigDecimal("0");
	      
	      List<Map<String, Object>> transactionList=purseSerivce.queryTotalPurseTransaction(params);
	      Map<String, Object>  balanceMap=purseSerivce.queryTotalPurseBalance();
	      if (transactionList==null||transactionList.size()==0||balanceMap==null) {
	        errCode="faild";
	        errMsg="统计失败，请稍后再试...";
	      }else{
	        for (Map<String, Object> map : transactionList) {
            String transType=map.get("trans_type").toString();
            BigDecimal totalAmount=(BigDecimal)map.get("totalAmount"); 
            if ("less".equals(transType)) {
              lessBigDecimal=totalAmount;
            }else if ("more".equals(transType)) {
              moreBigDecimal=totalAmount;
            }
          }
	        todayBalanceBigDecimal=(BigDecimal)balanceMap.get("todayTotalBalance");
	        hisBalanceBigDecimal=(BigDecimal)balanceMap.get("hisTotalBalance");
	        totalBalanceBigDecimal=todayBalanceBigDecimal.add(hisBalanceBigDecimal);
	        
	      }
	      
	      String data=errCode+"#"+errMsg+"#"
	          +new DecimalFormat("#,##0.00#").format(lessBigDecimal.doubleValue())+"#"
	          +new DecimalFormat("#,##0.00#").format(moreBigDecimal.doubleValue())+"#"
	          +new DecimalFormat("#,##0.00#").format(todayBalanceBigDecimal.doubleValue())+"#"
	          +new DecimalFormat("#,##0.00#").format(hisBalanceBigDecimal.doubleValue())+"#"
	          +new DecimalFormat("#,##0.00#").format(totalBalanceBigDecimal.doubleValue());
	      
	      response.getWriter().write(data);
	      log.info("统计信息："+data);
	      return ;
	    }
	    
	    
	    /**
       * 
       * 功能：钱包商户规则查询
       *
       * @param model
       * @param params
       * @param cpage
       * @return
       */
      @RequestMapping(value = "/bagMerchantQuery")
      public String bagMerchantQuery(final ModelMap model, @RequestParam
      Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
        PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
        
        String purseMechantRuleOffOn="false";
        String offOn=SysConfig.valueNoCache("purse_check_boolean");
        if (!StringUtil.isBlank(offOn)&&("true".equals(offOn)||"false".equals(offOn))) {
          purseMechantRuleOffOn=offOn;
        }
        
        Page<Map<String, Object>> list = purseSerivce.queryPurseMerchantRule(params, page);
        for (Map<String, Object> map : list.getContent()) {
          map.put("merchantTypeDesc", DictCache.getDictName("purse_rule_merchant_type", (String)map.get("merchant_type")));
          map.put("ruleTypeDesc", DictCache.getDictName("purse_rule_merchant_rule_type", (String)map.get("rule_type")));
        }
        
        model.put("p", cpage);
        model.put("list", list);
        model.put("params", params);
        model.put("merchantOffOn", purseMechantRuleOffOn);
        model.put("merchantTypeList", DictCache.getList("purse_rule_merchant_type"));
        model.put("ruleTypeList", DictCache.getList("purse_rule_merchant_rule_type"));
        return "/purse/bagMerchantQuery";
      }
      
      
      /**
       * 
       * 功能：修改钱包商户规则开关
       *
       * @param params
       * @param response
       * @throws IOException
       */
      @RequestMapping(value = "/updatePruseMecantOffOn")
      public void updatePruseMecantOffOn(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
        String errCode="success";
        String errMsg="钱包商户规则修改成功";
        
        String offOn=params.get("merchantOffOn");
        if (StringUtil.isBlank(offOn)||(!"true".equals(offOn)&&!"false".equals(offOn))) {
          errCode="faild";
          errMsg="异常的数据，请重试";
          response.getWriter().write(errCode+"#"+errMsg);
          log.info("修改钱包商户规则开关："+errCode+"#"+errMsg);
          return ;
        }
        String offOnDb=SysConfig.valueNoCache("purse_check_boolean");
        if (StringUtil.isBlank(offOnDb)) {
          //插入开关标志
          int ret=purseSerivce.insertPurseMerchantOffOn(offOn);
          if (ret==0) {
            errCode="faild";
            errMsg="修改钱包商户规则开关";
          }
        }else if (!offOn.equals(offOnDb)) {
          //修改开关标志
          int ret=purseSerivce.updatePurseMerchantOffOn(offOn);
          if (ret==0) {
            errCode="faild";
            errMsg="修改钱包商户规则开关";
          }
          
        }
        
        
        String data=errCode+"#"+errMsg;
        response.getWriter().write(data);
        log.info("修改钱包商户规则开关：："+data);
        return ;
      }
      
      
      /**
       * 
       * 功能：新增钱包商户规则页面
       *
       * @param model
       * @param params
       * @param cpage
       * @return
       */
      @RequestMapping(value = "/addPurseMerchantRulePage")
      public String addPurseMerchantRulePage(final ModelMap model, @RequestParam
          Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
        model.put("merchantTypeList", DictCache.getList("purse_rule_merchant_type"));
        model.put("ruleTypeList", DictCache.getList("purse_rule_merchant_rule_type"));
        return "/purse/addPurseMerchantRule";
      }
      
      /**
       * 
       * 功能：查询校验商户或者代理商
       *
       * @param params
       * @param response
       * @throws IOException
       */
      @RequestMapping(value = "/queryMerchant")
      public void queryMerchant(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
        String errCode="success";
        String errMsg="";
        String merchantTypeDesc="";
        
        String merchantType=params.get("merchantType");
        String merchantNo=params.get("merchantNo");
        Map<String, Object> merchantMap=null;
        if ("1".equals(merchantType)) {
          merchantTypeDesc="代理商";
          merchantMap=purseSerivce.queryAgent(merchantNo);
        }else if ("2".equals(merchantType)) {
          merchantTypeDesc="商户";
          merchantMap=purseSerivce.queryMerchant(merchantNo);
        }
        if (merchantMap==null) {
          errCode="faild";
          errMsg+="不存在";
        }else {
          if ("1".equals(merchantType)) {
            errMsg=merchantMap.get("agent_name").toString();
          }else if ("2".equals(merchantType)) {
            errMsg=merchantMap.get("merchant_name").toString();
          }
        }
        
        String data=errCode+"#"+errMsg+"#"+merchantTypeDesc;
        response.getWriter().write(data);
        log.info("查询商户："+data);
        return ;
      }
	    
      /**
       * 
       * 功能：新增钱包商户规则
       *
       * @param params
       * @param response
       * @throws IOException
       */
      @RequestMapping(value = "/addPurseMerchantRule")
      public void addPurseMerchantRule(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
        String errCode="success";
        String errMsg="新增钱包商户规则成功";
        
        String merchantNo=params.get("merchantNo");
        String merchantType=params.get("merchantType");
        String ruleType=params.get("ruleType");
        if (StringUtil.isBlank(merchantNo,merchantType,ruleType)) {
          errCode="faild";
          errMsg="新增数据不完整，请重试";
          String data=errCode+"#"+errMsg;
          response.getWriter().write(data);
          return ;
        }
        if ((!"1".equals(merchantType) && !"2".equals(merchantType))
            || ("1".equals(ruleType) && "0".equals(ruleType))) {
          errCode = "faild";
          errMsg = "新增数据无效，请重试";
          String data = errCode + "#" + errMsg;
          response.getWriter().write(data);
          return;
        }
        
        Map<String, Object> merchantDbMap=purseSerivce.queryPurseMerchantRule(merchantNo, merchantType);
        if (merchantDbMap!=null) {
          errCode="faild";
          errMsg="该钱包商户规则已经存在";
          String data = errCode + "#" + errMsg;
          response.getWriter().write(data);
          return ;
        }
        
        Map<String, Object> merchantMap=null;
        if ("1".equals(merchantType)) {
          merchantMap=purseSerivce.queryAgent(merchantNo);
        }else if ("2".equals(merchantType)) {
          merchantMap=purseSerivce.queryMerchant(merchantNo);
        }
        if (merchantMap==null) {
          errCode="faild";
          errMsg="新增商户数据不存在，请重试";
          String data = errCode + "#" + errMsg;
          response.getWriter().write(data);
          return ;
        }
        String merchantName="";
        if ("1".equals(merchantType)) {
          merchantName=merchantMap.get("agent_name").toString();
        }else if ("2".equals(merchantType)) {
          merchantName=merchantMap.get("merchant_name").toString();
        }
        
        params.put("merchantName", merchantName);
        params.put("createPersonId", ((BossUser) SecurityUtils.getSubject().getSession().getAttribute("user")).getId().toString());
        params.put("createPersonname", ((BossUser) SecurityUtils.getSubject().getSession().getAttribute("user")).getRealName());
        
        int ret=purseSerivce.addPurseMerchantRule(params);
        if (ret==0) {
          errCode="faild";
          errMsg+="新增钱包商户规则失败";
        }
        
        String data=errCode+"#"+errMsg;
        response.getWriter().write(data);
        log.info(data);
        return ;
      }
      
      
      /**
       * 
       * 功能：删除钱包商户规则
       *
       * @param params
       * @param response
       * @throws IOException
       */
      @RequestMapping(value = "/delPurseMerchantRule")
      public void delPurseMerchantRule(@RequestParam Map<String, String> params,HttpServletResponse response) throws IOException {
        String errCode="success";
        String errMsg="删除钱包商户规则成功";
        String id=params.get("id");
        Map<String, Object> ruleMap=purseSerivce.queryPurseMerchantRuleById(id);
        if (ruleMap!=null) {
          int ret=purseSerivce.delPurseMerchantRule(id);
          if (ret==0) {
            errCode="faild";
            errMsg+="删除钱包商户规则失败";
          }
        }
        
        String data=errCode+"#"+errMsg;
        response.getWriter().write(data);
        log.info("删除钱包商户规则："+data);
        return ;
      }
      
	    /**
	     * 
	     * 功能：钱包交易明细到导出
	     *
	     * @param params
	     * @param response
	     * @param request
	     */
	    @RequestMapping(value = "/bagTransactionExport")
	    public void bagTransactionExport(@RequestParam Map<String, String> params,
	        HttpServletResponse response,HttpServletRequest request,Model model) {
	      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	      int random = (int) (Math.random() * 1000);
	      
	      String fileName = "钱包交易明细_"+sdf.format(new Date())+"_"+random+".xls";
	      OutputStream os = null;
	      try {
	        request.setCharacterEncoding("UTF-8");
	        os = response.getOutputStream(); // 取得输出流
	        response.reset(); // 清空输出流
	        response.setHeader("Content-disposition", "attachment;filename="
	            + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
	        response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
	        
	        @SuppressWarnings("unchecked")
          List<Map<String, Object>> cycData=( List<Map<String, Object>>)SecurityUtils.getSubject().getSession().getAttribute("purseTransList");
	        if (cycData!=null) {
	          expordExcel(os);
	        }
	        
	      } catch (IOException e) {
	        e.printStackTrace();
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        if(os != null){
	          try {
	            os.close();
	          } catch (IOException e) {
	            e.printStackTrace();
	          }
	        }
	      }
	    }
	    
	    
	    /**
	     * list导出Excel
	     * 
	     * */
	    @SuppressWarnings("unchecked")
	    private void expordExcel(OutputStream os)
	        throws Exception {
	      
	      Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/PURSE_TRANSACTION_DETAIL.xls"));
	      WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
	      WritableSheet ws = wwb.getSheet(0);
	      
	      List<Map<String, Object>> list=( List<Map<String, Object>>)SecurityUtils.getSubject().getSession().getAttribute("purseTransList");
	      
	      String totalNum=(String)SecurityUtils.getSubject().getSession().getAttribute("totalNum");
	      String lessNum=(String)SecurityUtils.getSubject().getSession().getAttribute("lessNum");
	      String moreNum=(String)SecurityUtils.getSubject().getSession().getAttribute("moreNum");
	      String successNum=(String)SecurityUtils.getSubject().getSession().getAttribute("successNum");
	      String faildNum=(String)SecurityUtils.getSubject().getSession().getAttribute("faildNum");
	      String unNum=(String)SecurityUtils.getSubject().getSession().getAttribute("unNum");
	      String totalAmount=(String)SecurityUtils.getSubject().getSession().getAttribute("totalAmount");
	      String lessAmount=(String)SecurityUtils.getSubject().getSession().getAttribute("lessAmount");
	      String moreAmount=(String)SecurityUtils.getSubject().getSession().getAttribute("moreAmount");
	      String successAmount=(String)SecurityUtils.getSubject().getSession().getAttribute("successAmount");
	      String faildAmount=(String)SecurityUtils.getSubject().getSession().getAttribute("faildAmount");
	      String unAmount=(String)SecurityUtils.getSubject().getSession().getAttribute("unAmount");
	      
	      
	      if (list != null) {
	        
	        WritableCell wc = ws.getWritableCell(1, 1);
	        if (wc.getType() == CellType.EMPTY){
	          ws.addCell(new Label(1, 1,totalNum));
	        }else if(wc.getType() == CellType.LABEL) {
	          Label label = (Label) wc;
	          label.setString(totalNum);
	        }
	        
	        WritableCell wc1 = ws.getWritableCell(3, 1);
	        if (wc1.getType() == CellType.EMPTY){
	          ws.addCell(new Label(3, 1,totalAmount));
	        }else if (wc1.getType() == CellType.LABEL) {
	          Label label = (Label) wc1;
	          label.setString(totalAmount);
	        }
	        
	        WritableCell wc2 = ws.getWritableCell(1, 2);
	        if (wc2.getType() == CellType.EMPTY){
	          ws.addCell(new Label(1, 2,lessNum));
	        }else if (wc2.getType() == CellType.LABEL) {
	          Label label = (Label) wc2;
	          label.setString(lessNum);
	        }
	        
	        WritableCell wc3 = ws.getWritableCell(3, 2);
	        if (wc3.getType() == CellType.EMPTY){
	          ws.addCell(new Label(3, 1,lessAmount));
	        }else if (wc3.getType() == CellType.LABEL) {
	          Label label = (Label) wc3;
	          label.setString(lessAmount);
	        }
	        
	        WritableCell wc4 = ws.getWritableCell(1, 3);
	        if (wc4.getType() == CellType.EMPTY){
	          ws.addCell(new Label(1, 2,moreNum));
	        }else if (wc4.getType() == CellType.LABEL) {
	          Label label = (Label) wc4;
	          label.setString(moreNum);
	        }
	        
	        WritableCell wc5 = ws.getWritableCell(3, 3);
	        if (wc5.getType() == CellType.EMPTY){
	          ws.addCell(new Label(3, 1,moreAmount));
	        }else if (wc5.getType() == CellType.LABEL) {
	          Label label = (Label) wc5;
	          label.setString(moreAmount);
	        }
	        
	        WritableCell wc6 = ws.getWritableCell(1, 4);
	        if (wc6.getType() == CellType.EMPTY){
	          ws.addCell(new Label(1, 4,successNum));
	        }else if (wc6.getType() == CellType.LABEL) {
	          Label label = (Label) wc6;
	          label.setString(successNum);
	        }
	        
	        WritableCell wc7 = ws.getWritableCell(3, 4);
	        if (wc7.getType() == CellType.EMPTY){
	          ws.addCell(new Label(3, 4,successAmount));
	        }else if (wc7.getType() == CellType.LABEL) {
	          Label label = (Label) wc7;
	          label.setString(successAmount);
	        }
	        
	        WritableCell wc8 = ws.getWritableCell(1, 5);
	        if (wc8.getType() == CellType.EMPTY){
	          ws.addCell(new Label(1, 5,faildNum));
	        }else if (wc8.getType() == CellType.LABEL) {
	          Label label = (Label) wc8;
	          label.setString(faildNum);
	        }
	        
	        WritableCell wc9 = ws.getWritableCell(3, 5);
	        if (wc9.getType() == CellType.EMPTY){
	          ws.addCell(new Label(3, 5,faildAmount));
	        }else if (wc9.getType() == CellType.LABEL) {
	          Label label = (Label) wc9;
	          label.setString(faildAmount);
	        }
	        
	        WritableCell wc10 = ws.getWritableCell(1, 6);
          if (wc10.getType() == CellType.EMPTY){
            ws.addCell(new Label(1, 5,unNum));
          }else if (wc10.getType() == CellType.LABEL) {
            Label label = (Label) wc10;
            label.setString(unNum);
          }
          
          WritableCell wc11 = ws.getWritableCell(3, 6);
          if (wc11.getType() == CellType.EMPTY){
            ws.addCell(new Label(3, 5,unAmount));
          }else if (wc11.getType() == CellType.LABEL) {
            Label label = (Label) wc11;
            label.setString(unAmount);
          }
	        
	      }
	      
	      int row = 8; // 从第6行开始写
	      int col = 0; // 从第一列开始写
	      
	      for (int i = 0; i < list.size(); i++) {
	        Map<String, Object> map=(Map<String,Object>)list.get(i);
	        
	        ws.addCell(new Label(col++, row, ""+(i+1)));
	        ws.addCell(new Label(col++, row, map.get("msg").toString()));
	        ws.addCell(new Label(col++, row, map.get("transTypeDesc").toString()));
	        BigDecimal amountBigDecimal=(BigDecimal)map.get("amount");
	        DecimalFormat df = new DecimalFormat();
	        df.applyPattern("#####.00");
	        
	        ws.addCell(new Label(col++, row,new BigDecimal(df.format(amountBigDecimal.doubleValue())).toPlainString()));
	        ws.addCell(new Label(col++, row, map.get("transactionStatusDesc").toString()));
	        ws.addCell(new Label(col++, row, map.get("user_name").toString()));
	        ws.addCell(new Label(col++, row, map.get("create_time").toString()));
	        
	        row++;
	        col = 0;
	      }
	      
	      wwb.write();
	      wwb.close();
	      wb.close();
	      os.close();
	    }
	   
	    
		// 手机钱包用户调账页面
		@RequestMapping(value = "/checkTransferAcc")
		public String checkTransferAcc(final ModelMap model, @RequestParam
				Map<String, String> params) {

			model.put("params", params);
			return "/purse/transferInput";
		}
	    
		// 手机钱包用户调账
		@RequestMapping(value = "/userTransferAcc")
		public void userTransferAcc(final ModelMap model,HttpServletResponse response, @RequestParam
				Map<String, String> params) {

			
			//获取参数
			 String adjustType=params.get("adjustType");
			 String mobileNo=params.get("mobileNo");
			 String transReason=params.get("transReason");
			 String accAmount=params.get("accAmount");
			 String appType=params.get("appType");
			 String realName=params.get("realName");
			 Map<String, String> okMap = new HashMap<String, String>();
			
			//查询调账记录
			int res = purseSerivce.queryCheckTrans(mobileNo,"2");
			if(res>0){
				okMap.put("res", "0");
				okMap.put("msg", "已存在待审核的转账记录，请等待处理。");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
				return ;
			}
			
			//保存待审核订单
			Map<String, String> accParams = new HashMap<String, String>();
			accParams.put("mobileNo", mobileNo);
			accParams.put("amount", accAmount);
			accParams.put("checkStatus", "2");
			accParams.put("transStatus", "0");
			accParams.put("adjustType", adjustType);
			accParams.put("checkMsg", transReason);
			accParams.put("appType", appType);
			accParams.put("realName", realName);
			try {
				int ret = purseSerivce.addTransferAcc(accParams);
				if (ret==1) {
					okMap.put("res", "1");
					okMap.put("msg", "提交成功");
//					response.getWriter().write("1");
				} else {
					okMap.put("res", "0");
					okMap.put("msg", "提交失败");
				}
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			} catch (Exception e) {
				log.warn("新增钱包用户调账失败:"+e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		 //查询审核调账记录
		 @RequestMapping(value = "/bagCheckQuery")
		 public String bagCheckQuery(final ModelMap model,
		      @RequestParam Map<String, String> params,
		      @RequestParam(value = "p", defaultValue = "1") int cpage) {
		    
		    Page<Map<String, Object>> list=null;
		    int pageNum = PAGE_NUMERIC;
		    String pageSize = params.get("pageSize");
		    
		    try {
		      pageNum = Integer.parseInt(pageSize);
		    } catch (Exception e) {
		    }
		    if (20 != pageNum && 50 != pageNum && 100 != pageNum && 500 != pageNum) {
		      pageNum = PAGE_NUMERIC;
		    }

		    PageRequest page = new PageRequest(cpage - 1, pageNum);
		    list = purseSerivce.getBagCheckList(params,page);
		    
		    model.put("p", cpage);
		    model.put("list", list);
		    model.put("params", params);
		    return "/purse/bagCheckQuery";
		 }
		 
		//跳转到审核失败页面
		@RequestMapping(value = "/checkReason")
		public String checkReason(final ModelMap model, @RequestParam(value = "p", defaultValue = "1")
		int cpage, @RequestParam String id,HttpServletRequest request,HttpServletResponse response)
				throws Exception {
			Map<String, Object> map = purseSerivce.getCheckAccountDetailById(Long.parseLong(id));
			model.put("params", map);
			return "/purse/checkAccFail";
		}
		
		//审核
		@RequestMapping(value = "/bagCheckRecharge")
		public void bagCheckRecharge(final ModelMap model,
				@RequestParam Map<String, String> params,HttpServletRequest request,
				HttpServletResponse response) throws SQLException{
			Map<String, String> okMap = new HashMap<String, String>();
			String id=params.get("id");
			int num=purseSerivce.bagCheckRecharges(id,getUser().getRealName());
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
		
		
		@RequestMapping(value = "/checkAccountFailSave")
		public String  checkAccountFailSave(final ModelMap model, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> params) throws SQLException {
			try
			{
				//将审核失败信息存入数据库
				purseSerivce.bagCheckModify(params,getUser().getRealName());
				model.put("params", params);
				model.put("flag", "1");
				model.put("errorMessage","");
			}catch (SQLException e)
			{
				e.printStackTrace();
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
			return "/purse/checkAccFail";
	}
		
		//调账
		@RequestMapping(value = "/bagCheckSubmit")
		public void bagCheckSubmit(final ModelMap model,
				@RequestParam Map<String, String> params,HttpServletRequest request,
				HttpServletResponse response) throws SQLException{
			
			String id=params.get("id");
			Map<String, String> okMap = new HashMap<String, String>();
			Map<String, Object> checkMap = purseSerivce.getCheckAccountDetailById(Long.parseLong(id));
			//调账调用接口
			String mobileNo= checkMap.get("mobile_no").toString();
			String appType= checkMap.get("app_type").toString();
			String amount= checkMap.get("amount").toString();
			String adjustType= checkMap.get("adjust_type").toString();
			String msg= checkMap.get("check_msg").toString();
	
			String newHmac = Md5.md5Str(mobileNo + appType+amount+adjustType+id+msg+ Constants.BAG_HMAC);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mobileNo", checkMap.get("mobile_no"));
			map.put("amount", checkMap.get("amount"));
			map.put("appType", checkMap.get("app_type"));
			map.put("adjustType", checkMap.get("adjust_type"));
			map.put("channelId", id);
			map.put("msg", checkMap.get("check_msg"));
			map.put("hmac", newHmac);
			
			String url=SysConfig.value("account_tran_url");
			String backXmlMsg = Http.send(url, map, "UTF-8");
			BagCheckBackMsg backMsg = XmlUtil.xmlToObj(BagCheckBackMsg.class, backXmlMsg, "UTF-8");
			if("true".equalsIgnoreCase(backMsg.getSuccess())){
				purseSerivce.checkAccountSub(id,"1");
				okMap.put("flag", "1");
				okMap.put("msg", backMsg.getMsg());
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}else{
				purseSerivce.checkAccountSub(id,"2");
				okMap.put("flag", "0");
				okMap.put("msg", backMsg.getMsg());
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
			
	
		}
		
		/**
		 * 
		 * 功能：提升额度审核查询
		 *
		 * @param model
		 * @param params
		 * @param cpage
		 * @return
		 */
		  @RequestMapping(value = "/bagTzeroAmountLimitQuery")
		  public String bagTzeroAmountLimitQuery(final ModelMap model, @RequestParam
		  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
		    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		    if (params.get("startDate") == null
					&& params.get("endDate") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime +" 23:59:59";
				params.put("startDate", createTimeBegin);
				params.put("endDate", createTimeEnd);
			}
		    Page<Map<String, Object>> list = purseSerivce.bagTzeroAmountLimitQuery(params, page);
		    model.put("p", cpage);
		    model.put("list", list);
		    model.put("params", params);
		    System.out.println(params);
		    return "/purse/bagTzeroAmountLimitQuery";
		  }
		  
		  /**
		 * 
		 * 功能：T+0额度修改查询
		 *
		 * @param model
		 * @param params
		 * @param cpage
		 * @return
		 */
		  @RequestMapping(value = "/bagTzeroAmountModifyQuery")
		  public String bagTzeroAmountModifyQuery(final ModelMap model, @RequestParam
		  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
		    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		    Page<Map<String, Object>> list = purseSerivce.bagTzeroAmountModifyQuery(params, page);
		    List<Map<String, Object>> agentList = purseSerivce.selectAgentOrSale("agent");
		    List<Map<String, Object>> saleList = purseSerivce.selectAgentOrSale("sale");
		    /*Page<Map<String, Object>> list = purseSerivce.bagTzeroAmountLimitQuery(params, page);
		    model.put("p", cpage);
		    model.put("list", list);
		    model.put("params", params);
		    System.out.println(params);*/
		    model.put("params", params);
		    model.put("agentList", agentList);
		    model.put("saleList", saleList);
		    model.put("list", list);
		    return "/purse/bagTzeroAmountModifyQuery";
		  }
		  
		// T+0额度修改详情
		@RequestMapping(value = "/bagTzeroAmountModifyDetail")
		public String bagTzeroAmountModifyDetail(final ModelMap model, @RequestParam
		Map<String, String> params) throws Exception {
			try {
				String id = params.get("id");
				/*if(id.contains(",")){
					String[] idArr = id.split(",");
					id = idArr[0];
					model.put("idArr", idArr);
				}*/
				String type = params.get("type");
				System.out.println("id="+id+"---type="+type);
				Map<String, Object> bagMerchantMap = purseSerivce.selectBagMerchantById(id);
				String mobileNo = String.valueOf(bagMerchantMap.get("mobile_no"));
				String appType = String.valueOf(bagMerchantMap.get("app_type"));
				params.put("mobileNo", mobileNo);
				params.put("appType", appType);
				Map<String, Object> bagLoginMap = purseSerivce.selectBagLogin(mobileNo, appType);
				if("agent".equals(type)){
					Map<String, Object> agentAmountMap = purseSerivce.selectAgentDataAmount(String.valueOf(bagMerchantMap.get("agent_no")));
					String name = String.valueOf(bagMerchantMap.get("one_level_agent_name"));
					String agentNo = String.valueOf(bagMerchantMap.get("agent_no"));
					params.put("name", name);
					params.put("agentNo", agentNo);
					model.put("agentAmountMap", agentAmountMap);
				}else if("merchant".equals(type)){
					String name = String.valueOf(bagMerchantMap.get("merchant_name"));
					String day_extraction_max_amount = String.valueOf(bagLoginMap.get("day_extraction_max_amount"));
					params.put("name", name);
					params.put("day_extraction_max_amount", day_extraction_max_amount);
				}
				model.put("params", params);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return "/purse/bagTzeroAmountModifyDetail";
		}
		
		//修改额度
		@RequestMapping(value = "/bagTzeroAmountModify")
		public void bagTzeroAmountModify(final ModelMap model,HttpServletResponse response,@RequestParam Map<String, String> params) {
			String operater = getUser().getRealName();
			Boolean result = purseSerivce.agentDataAmountModify(params, operater);
			if(result){
				outText("操作成功", response);
			}else{
				outText("系统异常", response);
			}
		}
		  
		  
		  /**
		   * 移联红包
		   * @param model
		   * @param params
		   * @param cpage
		   * @return
		   */
		  @RequestMapping(value = "/redBagQuery")
		  public String redBagQuery(final ModelMap model, @RequestParam
		  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
		    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		    if (params.get("createTimeBegin") == null
					&& params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime +" 23:59:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}
		    Page<Map<String, Object>> list = purseSerivce.redBagQuery(params, page);
		    model.put("p", cpage);
		    model.put("list", list);
		    model.put("params", params);
		    System.out.println(params);
		    return "/purse/redBagQuery";
		  }
		  
		//钱包红包信息统计
		  @RequestMapping(value="/countRedBagInfo")
		  public void countRedBagInfo(HttpServletResponse response,@RequestParam Map<String, String> params){
			  Map<String, Object> param = purseSerivce.countRedBagInfo(params);
			  DecimalFormat df = new DecimalFormat("#,##0.00#");
			  BigDecimal bdTotal = new BigDecimal(param.get("total_amount")==null?"0":param.get("total_amount").toString());
			  BigDecimal bdTotalUser = new BigDecimal(param.get("total_user")==null?"0":param.get("total_user").toString());
			  param.put("total_amount", df.format(bdTotal));
			  param.put("total_user", bdTotalUser);
			  
			  String json = com.alibaba.fastjson.JSONObject.toJSONString(param);
			  outJson(json, response) ;
			  
		  }
		  
		  /**
		   * 红包换购管理
		   * @param model
		   * @param params
		   * @param cpage
		   * @return
		   */
		  @RequestMapping(value = "/redBuyMachines")
		  public String redBuyMachines(final ModelMap model, @RequestParam
		  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")int cpage) {
		    PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		    if (params.get("agentSureTimeBegin") == null
					&& params.get("agentSureTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				
				String agentSureTimeBegin = createTime + " 00:00:00";
				String agentSureTimeEnd = createTime +" 23:59:59";
				params.put("agentSureTimeBegin", agentSureTimeBegin);
				params.put("agentSureTimeEnd", agentSureTimeEnd);
			}
		    Page<Map<String, Object>> list = purseSerivce.redBuyMachines(params, page);
		    model.put("p", cpage);
		    model.put("list", list);
		    model.put("params", params);
		    System.out.println(params);
		    return "/purse/redBuyMachines";
		  }
		  
		  
		  /**
		   * 
		   * 功能：钱包T+0提升额度审核详情
		   *
		   * @param model
		   * @param params
		   * @return
		   * @throws Exception
		   */
		  @RequestMapping(value = "/bagTzeroAmountLimitDetail")
	    public String bagTzeroAmountLimitDetail(final ModelMap model, @RequestParam
	    Map<String, String> params){
	      String id = params.get("id");
	      String mobileNo = params.get("mobileNo");
	      String appType = params.get("appType");
	      try {
			Map<String, Object> basicMap = purseSerivce.bagTzeroAmountLimitDetail(id);
			model.put("basicMap", basicMap);
			List<Map<String, Object>> checkFailHisList = purseSerivce.bagTzeroCheckFailHis(mobileNo, appType);
			if(checkFailHisList.isEmpty()){
				model.put("notice", 0);
			}else{
				model.put("checkFailHisList", checkFailHisList);
				model.put("notice", 1);
			}
			List<Map<String, Object>> richCheckFailHisList = purseSerivce.bagTzeroRichCheckFailHis(mobileNo, appType);
			if(richCheckFailHisList.isEmpty()){
				model.put("richNotice", 0);
			}else{
				model.put("richCheckFailHisList", richCheckFailHisList);
				model.put("richNotice", 1);
			}
			List<Map<String, Object>> richList = purseSerivce.bagTzeroAmountLimitRichDetail(mobileNo, appType);
			model.put("richList", richList);
			if(richList!=null){
				int unCheckNum = 0;
				for(Map<String, Object> map:richList){
					if("0".equals(map.get("check_status").toString())){
						unCheckNum = unCheckNum+1;
						if("1".equals(basicMap.get("check_status").toString())){
							if("0".equals(map.get("last_check_status").toString())){
								log.info("-----基础资料已审核通过，用户新增丰富资料-----");
								model.put("note", "注：基础资料已审核通过，用户新增丰富资料");
							}else if("2".equals(map.get("last_check_status").toString())){
								log.info("-----基础资料已审核通过，丰富资料未通过，用户修改丰富资料-----");
								model.put("note", "注：基础资料已审核通过，丰富资料未通过，用户修改丰富资料");
							}
						}else if("0".equals(basicMap.get("check_status").toString())){
							log.info("-----基础资料审核中，用户新增丰富资料-----");
							model.put("note", "注：基础资料审核中，用户新增丰富资料");
						}
					}
				}
				model.put("unCheckNum", unCheckNum);
			}
			if("0".equals(basicMap.get("check_status").toString())){
				if(!checkFailHisList.isEmpty()){
					log.info("-----基础资料审核未通过，用户修改基础资料-----");
					model.put("note", "注：基础资料审核未通过，用户修改基础资料");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return "/purse/bagTzeroDetail";
	    }
		  
	  /**
	   * 审核后更改状态与T+0提现额度
	   * @param model
	   * @param params
	   * @return
	   * @throws Exception
	   */
	  @RequestMapping(value = "/bagTzeroCheckResult")
	    public void bagTzeroCheckResult(final ModelMap model, @RequestParam
	    Map<String, String> params,HttpServletResponse response){
		  log.info("-------审核后更改状态与T+0提现额度----------");
		try {
	      String id = params.get("id");
	      String richDataType = params.get("richDataType");
	      String batch = params.get("batch");
	      String mobileNo = params.get("mobileNo");
	      String appType = params.get("appType");
	      String checker = getUser().getRealName();
	      //审核结果   1为通过   2为不通过
	      String checkStatus = params.get("checkStatus");
	      String checkOpinion = params.get("checkOpinion");
	      //丰富资料的审核意见
	      String richOpinion = params.get("richOpinion");
	      //审核的资料类型  0为基本资料   1为丰富资料
	      String basicOrRichData = params.get("basicOrRichData");
	      BigDecimal amount = new BigDecimal("0");
	      BigDecimal moreAmount = new BigDecimal("0");
	      //String appName = purseSerivce.getAppName(appType);
	      log.info("---bagTzeroCheckResult---id="+id+"--checker="+checker+"--checkStatus="+checkStatus+"--checkOpinion="+checkOpinion+"--richOpinion="+richOpinion+"--basicOrRichData="+basicOrRichData+"--mobileNo="+mobileNo+"--appType="+appType);
		  Map<String, Object> bagMap = purseSerivce.selectBagLogin(mobileNo, appType);
		  Map<String, Object> bagMerchantMap = purseSerivce.selectBagMerchant(mobileNo, appType);
		  if(bagMap==null){
			  log.info("---------不存在该用户----------");
			  outText("0", response);
		  }else{
			  amount = new BigDecimal(bagMap.get("day_extraction_max_amount").toString()); 
			  log.info("当前该用户的T+0限额为amount="+amount);
			  String agentNo = "-1";
			  if(bagMerchantMap!=null){
				  if(bagMerchantMap.get("agent_no")!=null){
					  agentNo = bagMerchantMap.get("agent_no").toString();
				  }
			  }
			  log.info("----agentNo-----"+agentNo);
			  if("1".equals(checkStatus)){
				  Map<String, Object> agentDataMap = purseSerivce.selectAgentDataAmount(agentNo);
				  
				  if("0".equals(basicOrRichData)){
					  moreAmount = new BigDecimal("20000");
					  //moreAmount = new BigDecimal(agentDataMap.get("basic_data_amount").toString());
					  if("4028".equals(agentNo)  || "9927".equals(agentNo)){
						  Sms.sendMsg(mobileNo, "【新支付】感谢您关注新支付，您的T+0提现申请已开通，提现额度为"+agentDataMap.get("basic_data_amount").toString()+"元，提高提现额度请于客户端点击“调整提现额度”");
					  }
				  }else if("1".equals(basicOrRichData)){
					  //丰富资料的类型  0通讯录  1户口本、结婚证  2学历证明  3公司股东证明  4个人完税证明  5信用报告  6大额存款单、股票、债券等有价证券证明  7车辆证明  8房产证明
					  String dataType = params.get("dataType");
					  log.info("dataType="+dataType);
					  /*if("0".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data0_amount").toString());
					  }else if("1".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data1_amount").toString());
					  }else if("2".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data2_amount").toString());
					  }else if("3".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data3_amount").toString());
					  }else if("4".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data4_amount").toString());
					  }else if("5".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data5_amount").toString());
					  }else if("6".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data6_amount").toString());
					  }else if("7".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data7_amount").toString());
					  }else if("8".equals(dataType)){
						  moreAmount = new BigDecimal(agentDataMap.get("rich_data8_amount").toString());
					  }*/
					  moreAmount = new BigDecimal("20000");
					  /*if("0".equals(dataType) || "1".equals(dataType) || "2".equals(dataType)){
						  moreAmount = new BigDecimal("2000");
					  }else if("4".equals(dataType)){
						  moreAmount = new BigDecimal("3000");
					  }else if("3".equals(dataType) || "5".equals(dataType) || "6".equals(dataType)){
						  moreAmount = new BigDecimal("5000");
					  }else if("7".equals(dataType) || "8".equals(dataType)){
						  moreAmount = new BigDecimal("8000");
					  }*/
				  }
				  log.info("提升的额度为moreAmount="+moreAmount);
				  amount = amount.add(moreAmount);
				  if(amount.compareTo(new BigDecimal("60000"))==1){
					  amount = new BigDecimal("60000");
				  }
			  }else if("2".equals(checkStatus)){
				  if("0".equals(basicOrRichData)){
					  amount = new BigDecimal("0");
					  if("4028".equals(agentNo) || "9927".equals(agentNo)){
						  Sms.sendMsg(mobileNo, "【新支付】感谢您关注新支付，您的T+0提现申请失败，失败原因为"+checkOpinion);
					  }
				  }
			  }
			  log.info("审核通过后该用户的T+0限额为amount="+amount);
			  purseSerivce.bagTzeroCheckResult(id,richDataType, checker, checkStatus, checkOpinion,richOpinion,basicOrRichData,mobileNo,appType,amount,batch);
		      outText("1", response);
		  }
	      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			outText("0", response);
		}
	      
	    }
	  
	  
	// T+1自动结算导出excel表的判断
	  @RequestMapping(value = "/checkT1Excel")
	  public void checkT1Excel(HttpServletRequest request,
				HttpServletResponse response,@RequestParam Map<String, String> params) {
			String t1Date = params.get("t1Date");
			String excelUrl = SysConfig.value("bagUrl")+"/excel/"+t1Date+"钱包T+1结算.xls";
			System.out.println(excelUrl);
			InputStream netFileInputStream = null;
			try {
				URL url = new URL(excelUrl);
				URLConnection rulConnection = url.openConnection();
				netFileInputStream = rulConnection.getInputStream();
				if (null != netFileInputStream) {
					outText("true", response);
				} else {
					outText("false", response);
				}
			} catch (Exception e) {
				outText("false", response);
			}finally{
				try {
					if (netFileInputStream != null)
						netFileInputStream.close();
				} catch (IOException e) {
				}
			}
	  }
	  
	  
	  //钱包用户信息统计
	  @RequestMapping(value="/countBagUserInfo")
	  public void countBagUserInfo(HttpServletResponse response,@RequestParam Map<String, String> params){
		  
		  Map<String, Object> param = purseSerivce.countBagUserInfo();
		  DecimalFormat df = new DecimalFormat("#,##0.00#");
		  BigDecimal bdTotal = new BigDecimal(param.get("total_amount")==null?"0":param.get("total_amount").toString());
		  BigDecimal bdTotalTod = new BigDecimal(param.get("total_today")==null?"0":param.get("total_today").toString());
		  BigDecimal bdTotalHis = new BigDecimal(param.get("total_his")==null?"0":param.get("total_his").toString());
		  param.put("total_amount", df.format(bdTotal));
		  param.put("total_today", df.format(bdTotalTod));
		  param.put("total_his", df.format(bdTotalHis));
		  
		  String json = com.alibaba.fastjson.JSONObject.toJSONString(param);
		  outJson(json, response) ;
		  
	  }
		  

}
