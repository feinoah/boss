package com.eeepay.boss.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;





import com.eeepay.boss.service.AcqMerTransCountService;
import com.eeepay.boss.utils.StringUtil;



/**
 * 收单商户交易统计
 * @author LJ
 */
@Controller
@RequestMapping(value = "/acqTrans")
public class AcqMerTransCountController extends BaseController{

	@Resource
	private AcqMerTransCountService acqMerTrans;
	
	// 交易查询
	@RequestMapping(value = "/transCount")
	public String transCount(final ModelMap model, @RequestParam Map<String, String> params, 
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
			Page<Map<String, Object>> list = acqMerTrans.transCountQuery(params,page);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			
			return "/merchant/acqMerchantTransCount";
		}
	
	
	// 收单商户交易统计查询，导出excel
	@RequestMapping(value = "/export")
	public void export(@RequestParam Map<String, String> params, HttpServletResponse response, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int random = (int) (Math.random() * 1000);
		String fileName = "收单商户交易统计查询" + sdf.format(new Date()) + "_" + random + ".xls";
		
		OutputStream os = null;
		try {
			request.setCharacterEncoding("UTF-8");
			os = response.getOutputStream(); // 取得输出流
			response.reset(); // 清空输出流
			response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
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
	private void expordExcel(OutputStream os, Map<String, String> params, String fileName) throws Exception {
		int row = 2; // 从第三行开始写
		int col = 0; // 从第一列开始写
		PageRequest page = new PageRequest(0, 29999);
		/*if (params.get("createTimeBegin") == null && params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime + " 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}*/
		if ((params.get("createTimeBegin") == null || "".equals(params.get("createTimeBegin"))) && (params.get("createTimeEnd") == null || "".equals(params.get("createTimeEnd")))) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);

			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime + " 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}
		Page<Map<String, Object>> list = acqMerTrans.transCountQuery(params, page);
	
		Workbook wb = Workbook.getWorkbook(this.getClass().getResourceAsStream("/template/AcqMerTrans.xls"));

		WritableWorkbook wwb = Workbook.createWorkbook(os, wb);
		WritableSheet ws = wwb.getSheet(0);

		Iterator<Map<String, Object>> it = list.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			ws.addCell(new Label(col++, row, String.valueOf(map.get("acq_merchant_no")))); //收单商户编号
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("acq_merchant_name"), "无"))); //收单商户名称
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("agent_name"), "无"))); //代理商
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("sale_name"), "无"))); //所属销售
			String locked = StringUtil.ifEmptyThen(map.get("locked"), "无");
			if ("0".equals(locked)) {
				locked = "正常";
			} else if ("1".equals(locked)) {
				locked = "锁定";
			} else if ("2".equals(locked)) {
				locked = "废弃";
			} else {
				locked = "其它:" + locked;
			}
			ws.addCell(new Label(col++, row, locked));//状态
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("trans_cycle"), "无")));  //交易周期
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("total_pur_count"), "无"))); //成功笔数
			ws.addCell(new Label(col++, row, StringUtil.ifEmptyThen(map.get("total_pur_amount"), "无"))); //交易总额
			row++;
			col = 0;
		}
		wwb.write();
		wwb.close();
		wb.close();
		os.close();
	}
	
}
