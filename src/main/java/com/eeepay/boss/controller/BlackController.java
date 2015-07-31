package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.BlackService;
import com.eeepay.boss.service.TransService;
import com.eeepay.boss.utils.StringUtil;


@Controller
@RequestMapping(value = "/black")
public class BlackController extends BaseController {
	@Resource
	private BlackService blackService;
	
	@Resource
	private TransService transService;
	//查询黑名单
	@RequestMapping(value = "/blackQuery")
	public String userAuthQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = blackService.getBlackList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/black/blackQuery";
	}
	
	@RequestMapping(value = "/blackInput")
	public String userAuthInput(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		if(id!=null&&id.length()!=0){
			Map<String,Object> blackMap = blackService.getBlackById(Long.parseLong(id));
			model.put("params", blackMap);
		}
		return "/black/blackInput";
	}
	
	@RequestMapping(value = "/blackSave")
	public String blackSave(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws SQLException{
		String id=params.get("id");
		if(id==null||id.length()==0){
			try {
				blackService.blackSave(params);
				model.put("flag", "1");
			} catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
		}else{
			try {
				blackService.blackModify(params);
				model.put("flag", "2");
				model.put("params", params);
			}catch (Exception e) {
				model.put("flag", "0");
				model.put("errorMessage",e.getMessage());
				model.put("params", params);
			}
		}
		
		
		
		return "/black/blackInput";
	}
	
	//查询黑名单详情
	@RequestMapping(value = "/blackDetail")
	public String blackDetail(final ModelMap model,
			@RequestParam Map<String, String> params) throws Exception{
		String id=params.get("id");
		Map<String,Object> blackMap = blackService.getBlackById(Long.parseLong(id));
		model.put("params", blackMap);
		return "/black/blackDetail";
	}
	
	//删除黑名单
	@RequestMapping(value = "/blackDel")
	public void blackDel(final ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws Exception{
		
		Map<String, String> okMap = new HashMap<String, String>();
		String id=params.get("id");
		try{
		    blackService.deleteBlack(Long.parseLong(id));
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
	
	//查询黑名单
	@RequestMapping(value = "/blackFilterQuery")
	public String blackFilterQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage){
		
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		Page<Map<String, Object>> list = blackService.getBlackFilterList(
				params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/black/blackFilterQuery";
	}
	
	// 超额交易查询
	@RequestMapping(value = "/excessTrans")
	public String trans(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		String ids = (String) params.get("ids");

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

		Page<Map<String, Object>> list = transService.getExcessTrans(params, page);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/black/excessTransQuery";
	}
	@RequestMapping(value = "/export")
	public void export(@RequestParam Map<String, String> params,
			HttpServletResponse response, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String fileName = "交易查询" + sdf.format(new Date()) + "_" + random
				+ ".xls";
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

		Page<Map<String, Object>> list = transService.getExcessTrans(params,
				page);

		Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream(
				"/template/ExcessTrans.xls"));

		WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
		WritableSheet ws = wwb.getSheet(0);

		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("agent_name"), "无")));
			ws.addCell(new Label(col++, row, String.valueOf(map
					.get("merchant_short_name"))));

			String trans_type = StringUtil.ifEmptyThen(map.get("trans_type"),
					"无");
			if ("PURCHASE".equals(trans_type)) {
				trans_type = "消费";
			} 
			ws.addCell(new Label(col++, row, trans_type));

			String trans_status = StringUtil.ifEmptyThen(
					map.get("trans_status"), "无");
			if ("OVERLIMIT".equals(trans_status)) {
				trans_status = "已超额";
			} 
			ws.addCell(new Label(col++, row, trans_status));

			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("mermcc"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("create_time"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("merchant_no"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("terminal_no"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("trans_time"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("merchant_fee"), "无")));
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(
					map.get("trans_amount"), "无")));
			row++;
			col = 0;
		}

		// settleBatchService.updateSettleFileName(settleBatchNo, fileName);
		wwb.write();
		wwb.close();
		wb.close();
		os.close();
	}
}
