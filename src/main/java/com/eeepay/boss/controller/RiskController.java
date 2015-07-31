package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.OutputStream;
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

import com.eeepay.boss.service.RiskRulService;
import com.eeepay.boss.service.RiskService;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.MessageUtil;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;
import com.eeepay.boss.utils.pub.Sms;

/**
 * 风险管理
 * 
 * 
 */
@Controller
@RequestMapping(value = "/risk")
public class RiskController extends BaseController {

	@Resource
	private RiskService riskervice;

	@Resource
	private RiskRulService riskRulService;

	private static final Logger log = LoggerFactory
			.getLogger(AgentController.class);

	// 风险查询
	@RequestMapping(value = "/riskQuery")
	public String riskQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = riskervice.getRuleResultList(params,
				page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);

		return "/risk/riskQuery";

	}

	// 交易查询，到处excel,不是csv。zxm.
	@RequestMapping(value = "/export")
	public void export(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String fileName = "风险数据" + sdf.format(new Date()) + "_" + random
				+ ".xls";

		// PageRequest page = new PageRequest(0, 10000);

		// Page<Map<String, Object>> list = transService.getTrans(params, page);
		// Map<String,Object> totalMsg = transService.countTransInfo(params);

		OutputStream os = null;
		try {
			request.setCharacterEncoding("UTF-8");
			os = response.getOutputStream(); // 取得输出流
			response.reset(); // 清空输出流
			response.setHeader("Content-disposition", "attachment;filename="
					+ new String(fileName.getBytes("GBK"), "ISO8859-1"));
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型
			expordExcel(os, params, fileName);
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
	}

	/**
	 * 导出Excel报表
	 * 
	 */
	private void expordExcel(OutputStream os, Map<String, String> params,
			String fileName) throws Exception {

		int row = 2; // 从第三行开始写
		int col = 0; // 从第一列开始写

		PageRequest page = new PageRequest(0, 9999);

		Page<Map<String, Object>> list = riskervice.getRuleResultList(params,
				page);

		Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream(
				"/template/RiskTrans.xls"));

		WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
		WritableSheet ws = wwb.getSheet(0);

		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();

			ws.addCell(new Label(col++, row, String.valueOf(map.get("case_no"))));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("agent_no"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("merchant_no"), "无")));
			ws.addCell(new Label(col++, row, String.valueOf(map
					.get("terminal_no"))));
			ws.addCell(new Label(col++, row, String.valueOf(map
					.get("merchant_name"))));
			ws.addCell(new Label(col++, row, String.valueOf(map
					.get("create_time"))));
			row++;
			col = 0;
		}

		// settleBatchService.updateSettleFileName(settleBatchNo, fileName);
		wwb.write();
		wwb.close();
		wb.close();
		os.close();
	}

	// 规则组织新增input
	@RequestMapping(value = "/ruleGroupInput")
	public String ruleGroupInput(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		return "/risk/ruleGroupInput";

	}

	// 子规则add
	@RequestMapping(value = "/subRuleAdd")
	public String subRuleAdd(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		// Map<String, String> okMap = new HashMap<String, String>();

		try {
			// String clientLogo = params.get("clientLogo");
			// String managerLogo = params.get("managerLogo");
			// int rowsuc = riskRulService.subRuleAdd(params);
		} catch (Exception e) {
			log.info(e.getMessage());
			// TODO: handle exception
		}
		return "/risk/subRuleInput";
	}

	// 规则组织新增input
	@RequestMapping(value = "/subRuleInput")
	public String subRuleInput(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		return "/risk/subRuleInput";

	}

	// 参数设置查询
	@RequestMapping(value = "/paramQuery")
	public String paramQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> list = riskervice.getRuleParameterList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/risk/paramQuery";
	}

	// 参数字段设置
	@RequestMapping(value = "/paramInput")
	public String paramInput(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception {
		return "/risk/paramInput";
	}

	// 参数保存
	@RequestMapping(value = "/paramSave")
	public String paramSave(final ModelMap model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {
		String id = params.get("id");
		if (id == null || id.length() == 0) {
			try {
				riskervice.paramSave(params);
				model.put("flag", "1");
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage", e.getMessage());
				model.put("params", params);
			}
		} else {
			try {
				riskervice.paramModify(params);
				model.put("flag", "2");
				model.put("params", params);
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage", e.getMessage());
				model.put("params", params);
			}
		}
		return "/risk/paramInput";
	}

	// 风险商户增加
	@RequestMapping(value = "/riskMerchantInput")
	public String riskMerchantInput(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception {
		return "/risk/riskMerchantInput";
	}

	// 参数保存
	@RequestMapping(value = "/riskMerchantSave")
	public String riskMerchantSave(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException {

		String id = params.get("id");
		if (id == null || id.length() == 0) {
			try {
				riskervice.riskMerchantSave(params);
				model.put("flag", "1");
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage", e.getMessage());
				model.put("params", params);
			}
		} else {
			try {
				riskervice.riskMerchantModify(params);
				model.put("flag", "2");
				model.put("params", params);
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage", e.getMessage());
				model.put("params", params);
			}
		}
		return "/risk/riskMerchantInput";
	}

	// 风险商户删除
	@RequestMapping(value = "/riskMerchantDel")
	public void riskMerchantDel(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception {

		Map<String, String> okMap = new HashMap<String, String>();
		String id = params.get("id");
		try {
			riskervice.deleteRiskMerchant(Long.parseLong(id));
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

	// 风险结果新增
	@RequestMapping(value = "/riskResultAdd")
	public void riskResultAdd() throws SQLException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("trans_amount", SysConfig.value("credit_card_risk_amount"));
		params.put("warnCount", SysConfig.value("credit_card_risk_ratio"));// 预警系数
		List<Map<String, Object>> riskWarnList = riskervice
				.riskWarnQuery(params);
		for (int i = 0; i < riskWarnList.size(); i++) {
			Map<String, Object> riskParams = riskWarnList.get(i);
			double ratio = Double.parseDouble(riskParams.get("ratio")
					.toString());
			double ratioValue = (ratio - 0.6) / 0.6;
			if (ratioValue > 0.25) {
				if (ratioValue > 0.25 && ratioValue <= 0.5) {
					riskParams.put("risk_class", "0");
				} else if (ratioValue > 0.5 && ratioValue <= 0.75) {
					riskParams.put("risk_class", "1");
				} else if (ratioValue > 0.75 && ratioValue <= 1) {
					riskParams.put("risk_class", "2");
				} else if (ratioValue > 1) {
					riskParams.put("risk_class", "3");
				}
				riskParams.put("case_no", GenSyncNo.getInstance()
						.getNextCaseNo());
				riskParams.put("case_type", "0");
				riskervice.riskResultAdd(riskParams);
			}
		}
	}

	public void getTimeOutRecord() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			log.info("\n按指定时间周期检查一次交易信息" + sdf.format(new Date()));
			String phoneNumber = SysConfig.value("phoneNumberMonitor");
			String[] phones = null;
			if ((phoneNumber != null) && (phoneNumber.indexOf("|") > -1)) {
				phones = phoneNumber.split("\\|");
			} else {
				phones = new String[] { phoneNumber };
			}
			List<String> list = riskervice.getTimeOutRecord();
			Sms sms = null;
			for (String acq_enname : list) {
				String context = "【" + acq_enname + "】，告警，系统中断，请及时处理。";
				context = context +"\n短信生成时间："+ sdf.format(new Date());
				{//使用线程发送信息
					sms = new Sms("phones", null, context, null, phones);
					new Thread(sms).start();
					//MessageUtil mu = new MessageUtil("标题---测试短信", "标题---测试短信       "+acq_enname+"  zja@eeepay.cn" +context, "zja@eeepay.cn");
					//new Thread(mu).start();
					log.info("使用线程发送!\n接收的手机号列表：" + phoneNumber + "；\t\n发送的短信内容：" + context);
				}
				{//阻塞式发送信息
					//Sms.sendMsg(phones, context);
					//log.info("按指定时间周期检查一次交易信息!\n接收的手机号列表：" + phoneNumber + "；\t\n发送的短信内容：" + context);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

}
