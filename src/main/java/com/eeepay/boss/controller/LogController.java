package com.eeepay.boss.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.LogService;
import com.eeepay.boss.utils.HEXChannel;

/**
 * 机具管理
 * 
 * 
 */
@Controller
@RequestMapping(value = "/log")
public class LogController extends BaseController {

	@Resource
	private LogService logService;

	// 日志查询
	@RequestMapping(value = "/logQuery")
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
	
		Page<Map<String, Object>> list = logService.getLogList(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/log/logQuery";
	
	
	}
	// 日志查询
	@RequestMapping(value = "/acqLogQuery")
	public String acqLogQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00";
			String createTimeEnd = createTime + " 23:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}
	
		Page<Map<String, Object>> list = logService.getAcqLogList(params,
				page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/log/acqLogQuery";
	
	
	}
	//收单日志详情
	@RequestMapping(value = "/acqLogDetail")
	public String terDetail(final ModelMap model, @RequestParam Long id) {
		Map<String, Object> terInfo = logService.getAcqLogDetailById(id);
		String msg = (String) terInfo.get("msg");
		msg = HEXChannel.getIsoMsg(msg);
		terInfo.put("msg", msg);
		model.put("paramsResq", terInfo);
		
		
		Map<String, Object> reqInfo = new HashMap<String, Object>();
		reqInfo.put("trans_code", terInfo.get("trans_code"));
		reqInfo.put("batch_no", terInfo.get("batch_no"));
		reqInfo.put("serial_no", terInfo.get("serial_no"));
		reqInfo.put("acq_merchant_no", terInfo.get("acq_merchant_no"));
		reqInfo.put("acq_terminal_no", terInfo.get("acq_terminal_no"));
		
		
		terInfo = logService.getAcqLogDetailResp(reqInfo);
		if(terInfo!=null){
			String msg1 = (String) terInfo.get("msg");
			msg1 = HEXChannel.getIsoMsg(msg1);
			terInfo.put("msg", msg1);
		}
		model.put("paramsResp", terInfo);
		
		return "/log/acqLogDetail";
	}
	// 手机日志查询
		@RequestMapping(value = "/mobLogQuery")
		public String mobLogQuery(final ModelMap model,
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
		
			Page<Map<String, Object>> list = logService.getMobLogList(params,
					page);

			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			return "/log/mobLogQuery";
		
		
		}
		
		
		// 手机日志详情
		@RequestMapping(value = "/mobLogDetail")
		public String mobLogDetail(final ModelMap model, @RequestParam Long id)throws SQLException 
		{
			Map<String, Object> params = new HashMap<String, Object>();
			

			Map<String,Object> mobReqInfo = logService.queryMobRequest(id);
			Map<String, Object> mobResInfo = logService.queryMobResponse((String)mobReqInfo.get("seq_no"));
			

			params.put("mobReqInfo",mobReqInfo);
			if(mobResInfo!=null)
			params.put("mobResInfo", mobResInfo);
	 
	 
			
			
			
			model.put("params", params);
			return "/log/mobLogDetail";
		}
	
		

	  // 日志查询 来自数据库mobile_rquest_dev
	  @RequestMapping(value = "/rquestDevQuery")
	  public String rquestDevQuery(final ModelMap model, @RequestParam
	  Map<String, String> params, @RequestParam(value = "p", defaultValue = "1")
	  int cpage) {

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

	    Page<Map<String, Object>> list = logService.getRequestDevList(params, page);

	    model.put("p", cpage);
	    model.put("list", list);
	    model.put("params", params);
	    return "/log/rquestDevQuery";

	  }
	  
	// 日志查询
		@RequestMapping(value = "/operateLogQuery")
		public String operateLogQuery(final ModelMap model,
				@RequestParam Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1") int cpage) {

			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			Page<Map<String, Object>> list = logService.getOperateLogList(params,
					page);

			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			return "/log/operateLogQuery";
		
		
		}
		
		// 商户日志查询
		@RequestMapping(value = "/merLogQuery")
		public String merLogQuery(final ModelMap model,
				@RequestParam Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1") int cpage) {

			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

			if (params.size() == 110) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				String operTimeBegin = createTime + " 00:00";
				String operTimeEnd = createTime + " 23:59";
				params.put("operTimeBegin", operTimeBegin);
				params.put("operTimeEnd", operTimeEnd);
			}
		
			Page<Map<String, Object>> list = logService.getMerLogList(params,page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			return "/log/merLogQuery";
		
		
		}
		
		// 商户日志比较
		@RequestMapping(value = "/compareMerLogs")
		public String compareMerLogs(final ModelMap model,
				@RequestParam Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1") int cpage) {
			
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			String id = params.get("ids");
			if (StringUtils.isNotEmpty(id)) {
				String[] ids = id.split(",");
				for (String i : ids) {
					Map<String, Object> map = logService.getMerLogDetailById(i);
					list.add(map);
				}
			}
			model.put("list", list);
			return "/log/merLogDetail";
		}
		
		// 代理商扣率日志查询
		@RequestMapping(value = "/agentLogQuery")
		public String agentLogQuery(final ModelMap model,
				@RequestParam Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1") int cpage) {

			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

			if (params.size() == 110) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				String operTimeBegin = createTime + " 00:00";
				String operTimeEnd = createTime + " 23:59";
				params.put("operTimeBegin", operTimeBegin);
				params.put("operTimeEnd", operTimeEnd);
			}
		
			Page<Map<String, Object>> list = logService.getAgentLogList(params,page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			return "/log/agentLogQuery";
		
		
		}
		// 代理商扣率日志比较
		@RequestMapping(value = "/compareAgentLogs")
		public String compareAgentLogs(final ModelMap model,
				@RequestParam Map<String, String> params,
				@RequestParam(value = "p", defaultValue = "1") int cpage) {
			
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			String id = params.get("ids");
			if (StringUtils.isNotEmpty(id)) {
				String[] ids = id.split(",");
				for (String i : ids) {
					Map<String, Object> map = logService.getAgentLogDetailById(i);
					list.add(map);
				}
			}
			model.put("list", list);
			return "/log/agentLogDetail";
		}
		
}
