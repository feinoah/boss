package com.eeepay.boss.controller;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.web.util.WebUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.domain.CardBin;
import com.eeepay.boss.enums.CurrencyType;
import com.eeepay.boss.enums.TransSource;
import com.eeepay.boss.enums.TransStatus;
import com.eeepay.boss.enums.TransType;
import com.eeepay.boss.service.AcqMerchantService;
import com.eeepay.boss.service.BankCardService;
import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.StatisticsService;
import com.eeepay.boss.service.TransService;
import com.eeepay.boss.utils.DateUtils;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.StringUtil;
import com.eeepay.boss.utils.SysConfig;

/**
 * 数据统计
 * 
 * 
 */
@Controller
@RequestMapping(value = "/sta")
public class StatisticsController extends BaseController {

	@Resource
	private StatisticsService statisticsService;

	@Resource
	private TransService transService;

	@Resource
	private MerchantService merchantService;

	@Resource
	private AcqMerchantService acqMerchantService;

	@Resource
	private BankCardService bankCardService;

	@RequestMapping(value = "/bar")
	public String barChart(final ModelMap model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// 获取最近7天的日统计数据
		// List<Map<String, Object>> staMap =
		// statisticsService.query7DaysCounts();
		// String sevenBar = getBarChart(staMap, request, getDataSet(staMap),
		// "最近7天日交易量统计", "金额", "bar1.png");
		// // 查询代理商日交易量统计
		// List<Map<String, Object>> agentList = statisticsService
		// .queryAgentDayCounts();
		// String agentBar = getBarChart(agentList, request,
		// getDataSet1(agentList), "代理商日交易量统计", "金额", "bar2.png");
		// // 获取活跃商户
		// List<Map<String, Object>> actMerList = statisticsService
		// .queryActiveMer();
		// String merPie = getPieChart(actMerList, request, "pie.png");
		//
		// List<Map<String, Object>> list =
		// statisticsService.queryOrderForThree();
		// model.put("chart", sevenBar);
		// model.put("agentChart", agentBar);
		// model.put("merPie", merPie);
		// model.put("list", list);

		return "statistics/chart";

	}

	// 最近30天日交易统计
	@RequestMapping(value = "/dayTotal")
	public void dayTotal(HttpServletResponse response) {
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=GBK");
		StringBuffer xml = new StringBuffer(
				"<graph baseFontSize='12' xAxisName='日期' yAxisName='交易金额' showNames='1' decimalPrecision='0' formatNumberScale='0'>");
		List<Map<String, Object>> list = statisticsService.queryCountForDay();
		for (Map<String, Object> m : list) {
			String d = m.get("d").toString();
			d = d.substring(d.indexOf("-") + 1);
			xml.append(" <set name='" + d + "' value='" + m.get("a") + "'  />");
		}
		xml.append("</graph>");
		outXml(xml.toString(), response);
	}

	// 代理商日交易
	@RequestMapping(value = "/agentTotal")
	public void agentTotal(HttpServletResponse response) {
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=GBK");
		StringBuffer xml = new StringBuffer(
				"<graph baseFontSize='12' xAxisName='代理商名称' yAxisName='交易金额' showNames='1' decimalPrecision='0' formatNumberScale='0'>");

		List<Map<String, Object>> agentList = statisticsService
				.queryAgentDayCounts();
		double agentAmountDouble = 0.00;
		for (Map<String, Object> m : agentList) {
			String amount = m.get("trans_amount").toString();
			String name = m.get("agent_name").toString();
			xml.append(" <set name='" + name + "' value='" + amount + "'  />");
			agentAmountDouble = agentAmountDouble + Double.parseDouble(amount);
		}

		// 计算其他代理商总额
		Map<String, Object> totalMap = statisticsService.queryTotalPurchase();
		String total_amount = totalMap.get("total_amount").toString();
		BigDecimal a = new BigDecimal(total_amount);
		BigDecimal b = new BigDecimal(agentAmountDouble);
		String otherAmount = a.subtract(b).toString();
		xml.append(" <set name='其他代理商' value='" + otherAmount + "'  />");
		xml.append("</graph>");
		outXml(xml.toString(), response);
	}

	// 交易详情
	@RequestMapping(value = "/detail")
	public String transDetail(final ModelMap model, @RequestParam Long id) {

		Map<String, Object> params = new HashMap<String, Object>();

		Map<String, Object> transInfoMap = transService.queryTransInfoById(id);

		if (TransStatus.SUCCESS.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已成功");
		} else if (TransStatus.FAILED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已失败");
		} else if (TransStatus.INIT.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "初始化");
		} else if (TransStatus.REVOKED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已撤销");
		} else if (TransStatus.REFUND.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已退货");
		} else if (TransStatus.REVERSED.toString().equals(
				(String) transInfoMap.get("trans_status"))) {
			transInfoMap.put("trans_status", "已冲正");
		}

		if (TransType.PURCHASE.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "消费");
		} else if (TransType.PURCHASE_VOID.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "消费撤销");
		} else if (TransType.PURCHASE_REFUND.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "退货");
		} else if (TransType.REVERSED.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "冲正");
		} else if (TransType.BALANCE_QUERY.toString().equals(
				(String) transInfoMap.get("trans_type"))) {
			transInfoMap.put("trans_type", "余额查询");
		}

		if (CurrencyType.CNY.toString().equals(
				(String) transInfoMap.get("currency_type"))) {
			transInfoMap.put("currency_type", "人民币");
		}

		if (TransSource.COM_MOBILE_PHONE.toString().equals(
				(String) transInfoMap.get("trans_source"))) {
			transInfoMap.put("trans_source", "企业版");
		} else if (TransSource.MOBOLE_PHONE.toString().equals(
				(String) transInfoMap.get("trans_source"))) {
			transInfoMap.put("trans_source", "个人版");
		} else if (TransSource.POS.toString().equals(
				(String) transInfoMap.get("trans_source"))) {
			transInfoMap.put("trans_source", "POS");
		}

		Map<String, Object> merchantInfoMap = merchantService
				.queryMerchantInfoByNo((String) transInfoMap.get("merchant_no"));

		Map<String, Object> acqMerchantInfoMap = acqMerchantService
				.queryAcqMerchantInfo((String) transInfoMap
						.get("acq_merchant_no"));

		CardBin cardBin = bankCardService.cardBin((String) transInfoMap
				.get("account_no"));

		String accountNo = (String) transInfoMap.get("account_no");
		String card_no = accountNo.substring(0, 6)
				+ "*****"
				+ accountNo.substring(accountNo.length() - 4,
						accountNo.length());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String transId = sdf.format(new Date())
				+ StringUtil.stringFillLeftZero(String.valueOf(id), 6);

		params.put("trans_id", transId);
		params.put("card_no", card_no);
		params.put("bank_name", cardBin.getBankName());
		params.put("card_type", cardBin.getCardType());
		params.put("card_name", cardBin.getCardName());

		params.putAll(transInfoMap);
		params.putAll(merchantInfoMap);
		params.putAll(acqMerchantInfoMap);

		// Object fee_rate = merchantInfoMap.get("fee_rate");
		// if(fee_rate !=null && fee_rate.toString().length() > 0)
		// {
		// params.put("fee_rate", new
		// BigDecimal(merchantInfoMap.get("fee_rate").toString()).movePointRight(2).toString()+"%");
		// }

		// params.put("fee_max_amount", merchantInfoMap.get("fee_max_amount"));
		//
		// Object ladder_fee = merchantInfoMap.get("ladder_fee");
		// if(ladder_fee != null && ladder_fee.toString().length() >0)
		// {
		// if(ladder_fee.toString().indexOf("<") > 0 )
		// {
		// String[] ladder = ladder_fee.toString().split("<");
		// String ladder_min = ladder[0];
		// String ladder_value = ladder[1];
		// String ladder_max = ladder[2];
		//
		// params.put("ladder_min", new
		// BigDecimal(ladder_min).movePointRight(2));
		// params.put("ladder_value", ladder_value);
		// params.put("ladder_max", new
		// BigDecimal(ladder_max).movePointRight(2));
		// }
		// }
		//
		// params.put("fee_type", merchantInfoMap.get("fee_type"));
		// params.put("fee_cap_amount", merchantInfoMap.get("fee_cap_amount"));

		// String acqFeeRate =
		// ((BigDecimal)acqMerchantInfoMap.get("acq_fee_rate")).multiply(new
		// BigDecimal("100")).setScale(4,RoundingMode.HALF_UP).toString()+"%";
		// params.put("acq_fee_rate",acqFeeRate );

		model.put("params", params);

		return "/merchant/merchantTransDetail";
	}

	/**
	 * 柱状图设置
	 * 
	 * @param staMap
	 * @param request
	 * @param dataset
	 * @param DirName
	 * @param lable
	 * @return
	 * @throws Exception
	 */
	private static String getBarChart(List<Map<String, Object>> staMap,
			HttpServletRequest request, CategoryDataset dataset,
			String DirName, String lable, String fileName) throws Exception {
		JFreeChart chart = ChartFactory.createBarChart(DirName, "", lable,
				dataset, PlotOrientation.VERTICAL, false, false, false);
		chart.setBackgroundPaint(Color.white); // 设定背景色为白色
		// chart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
		chart.getTitle().setFont(new Font("黑体", Font.BOLD, 26));// 设置标题字体
		CategoryPlot plot = chart.getCategoryPlot();// 获取图表区域对象
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		// 横坐标 区域
		CategoryAxis domainAxis = plot.getDomainAxis(); // 获取横坐标
		domainAxis.setVisible(true);
		// domainAxis.setLabelFont(new Font("黑体", Font.BOLD, 26)); //
		// 设置横坐标的标题字体和大小
		domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12)); // 设置横坐标的坐标值的字体
		domainAxis.setTickLabelPaint(Color.BLACK); // 设置横坐标的坐标值的字体颜色
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(0.4));// 设置横坐标的显示

		ValueAxis rangeAxis = plot.getRangeAxis();// 获取柱状
		rangeAxis.setLabelFont(new Font("宋体", Font.PLAIN, 15));
		rangeAxis.setTickLabelPaint(Color.BLACK);
		rangeAxis.setUpperMargin(0.2);// 设置最高的一个 Item 与图片顶端的距离

		BarRenderer renderer = new BarRenderer();
		renderer.setSeriesPaint(0, new Color(828109)); // 设置柱的颜色
		renderer.setBaseItemLabelPaint(Color.BLACK);// 数据字体的颜色
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		plot.setRenderer(renderer);

		// 保存图片
		HttpSession session = WebUtils.toHttp(request).getSession(true);
		ServletUtilities.saveChartAsPNG(chart, 250, 100, null, session);
		String savePath = request.getSession().getServletContext()
				.getRealPath("/");
		String tempurl = SysConfig.value("uploadtemp");
		String fileSavePath = savePath + tempurl;
		if (fileSavePath.lastIndexOf("/") == -1) {
			fileSavePath = fileSavePath + "/";
		}
		File temp = new File(fileSavePath);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(fileSavePath + fileName);
		ChartUtilities.writeChartAsJPEG(fos, 1, chart, 500, 250);
		fos.close();
		return tempurl + fileName;
	}

	/**
	 * 设置饼图
	 * 
	 * @param list
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getPieChart(List<Map<String, Object>> list,
			HttpServletRequest request, String fileName) throws Exception {
		DefaultPieDataset dfp = getDataSet2(list);
		JFreeChart chart = ChartFactory.createPieChart3D("", dfp, false, true,
				true);
		// 定义字体格式
		Font font = new Font("黑体", Font.BOLD, 26);
		TextTitle title = new TextTitle("活跃商户区域分析");
		title.setFont(font);
		chart.setTitle(title);
		chart.setBorderVisible(false);
		chart.setBackgroundPaint(Color.WHITE);// 图片背景色
		// 取得3D饼图对象
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setOutlinePaint(Color.WHITE); // 设置绘图面板外边的填充颜色
		plot.setShadowPaint(Color.WHITE); // 设置绘图面板阴影的填充颜色
		plot.setBackgroundPaint(Color.WHITE);
		plot.setNoDataMessage("无数据显示"); // 没有数据的时候显示的内容
		plot.setCircular(true);
		plot.setLabelFont(new Font("宋体", Font.ITALIC, 12)); // 设置饼图各部分标签字体
		plot.setStartAngle(360); // 设置第一个 饼块section 的开始位置，默认是12点钟方向
		plot.setMaximumLabelWidth(0.3);
		plot.setToolTipGenerator(new StandardPieToolTipGenerator()); // 设置鼠标悬停提示
		StandardPieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator(
				("{0},{2}"), NumberFormat.getNumberInstance(),
				new DecimalFormat("0.00%"));
		plot.setLabelGenerator(generator);

		// 保存文件
		HttpSession session = WebUtils.toHttp(request).getSession(true);
		String savePath = request.getSession().getServletContext()
				.getRealPath("/");
		String tempurl = SysConfig.value("uploadtemp");
		String fileSavePath = savePath + tempurl;
		if (fileSavePath.lastIndexOf("/") == -1) {
			fileSavePath = fileSavePath + "/";
		}
		File temp = new File(fileSavePath);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(fileSavePath + fileName);
		ChartUtilities.writeChartAsJPEG(fos, 1, chart, 800, 300);
		fos.close();// 关闭流
		return tempurl + fileName;
	}

	// 近7天日交易数据
	private static CategoryDataset getDataSet(List<Map<String, Object>> list) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// 声明变量
		String[] dates = new String[7];
		Date temp = null;
		// 得到最近7天的日期 格式 2010-07-08
		for (int i = 0; i < 7; i++) {
			temp = DateUtils.diffDate(new Date(), 6 - i);
			dates[i] = DateUtils.format(temp, "yyyy-MM-dd");
		}
		List<Map<String, Object>> listbar = new ArrayList<Map<String, Object>>();
		String tempTime = null;
		double moneyTemp = 0;
		Map<String, Object> m = new HashMap<String, Object>();
		boolean flag = false;
		for (int i = 0; i < dates.length; i++) {
			flag = false;
			Map<String, Object> map = new HashMap<String, Object>();
			for (int j = 0; j < list.size(); j++) {
				map = list.get(j);
				if (map.get("money") == null) {
					map.put("money", 0);
				}
				moneyTemp = new BigDecimal(map.get("money").toString())
						.doubleValue();
				tempTime = (String) map.get("time");
				if (tempTime.equals(dates[i])) {
					dataset.addValue(moneyTemp, "", dates[i]);// 时间
					flag = true;
					break;
				}
			}
			if (!flag) {
				dataset.addValue(0, "", dates[i]);// 时间
			}
		}
		return dataset;
	}

	private static CategoryDataset getDataSet1(List<Map<String, Object>> list) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<String, Object> map = new HashMap<String, Object>();
		String tempTime = null;
		double moneyTemp = 0;
		for (int i = 0; i < list.size(); i++) {
			map = list.get(i);
			if (map.get("trans_amount") == null) {
				map.put("trans_amount", 0);
			}
			moneyTemp = new BigDecimal(map.get("trans_amount").toString())
					.doubleValue();
			tempTime = (String) map.get("agent_name");
			dataset.addValue(moneyTemp, "", tempTime);
		}
		return dataset;
	}

	private static DefaultPieDataset getDataSet2(List<Map<String, Object>> list) {
		DefaultPieDataset dfp = new DefaultPieDataset();
		Map<String, Object> map = new HashMap<String, Object>();
		String tempCity = null;
		double moneyTemp = 0;
		for (int i = 0; i < list.size(); i++) {
			map = list.get(i);
			if (map.get("trans_amount") == null) {
				map.put("trans_amount", 0);
			}
			moneyTemp = new BigDecimal(map.get("trans_amount").toString())
					.doubleValue();
			tempCity = (String) map.get("city");
			dfp.setValue(tempCity, moneyTemp);
		}
		return dfp;
	}
}
