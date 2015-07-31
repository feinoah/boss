package com.eeepay.boss.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
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
import com.eeepay.boss.domain.CardBin;
import com.eeepay.boss.enums.TransType;
import com.eeepay.boss.service.AcqMerchantService;
import com.eeepay.boss.service.AgentService;
import com.eeepay.boss.service.BankCardService;
import com.eeepay.boss.service.MerchantService;
import com.eeepay.boss.service.TransService;


/**
 * 销售管理
 * 
 * @author chens
 * 
 */
@Controller
@RequestMapping(value = "/sale")
public class SalesController extends BaseController {
	@Resource
	private AgentService agentService;

	@Resource
	private TransService transService;

	@Resource
	private MerchantService merchantService;
	
	@Resource
	private BankCardService bankCardService;

	@Resource
	private AcqMerchantService acqMerchantService;
	
	private static final Logger log = LoggerFactory.getLogger(SalesController.class);
	
			// 快捷交易详情
			@RequestMapping(value = "/fastDetail")
			public String fastDetail(final ModelMap model, @RequestParam Long id) {
				Map<String, Object> params = new HashMap<String, Object>();
				Map<String, Object> transInfoMap = transService.queryFastTransInfoById(id);
				if (TransType.PURCHASE.toString().equals(
						(String) transInfoMap.get("biz_name"))) {
					transInfoMap.put("biz_name", "消费");
				} else if ("BAG".equals(
						(String) transInfoMap.get("biz_name"))) {
					transInfoMap.put("biz_name", "钱包充值");
				} else if ("MOBILE".equals(
						(String) transInfoMap.get("biz_name"))) {
					transInfoMap.put("biz_name", "手机充值");
				} 
				CardBin cardBin = bankCardService.cardBin((String) transInfoMap.get("card_no"));
				params.put("bank_name", cardBin.getBankName());
				params.put("card_type", cardBin.getCardType());
				params.put("card_name", cardBin.getCardName());
				params.putAll(transInfoMap);
				model.put("params", params);
				return "/sale/fastTransDetail";
			}
	
	
		/**
		 * 销售管理 - 快捷交易统计
		 * @param params
		 * @return
		 */
		@RequestMapping("/saleCountTransFast")
		@ResponseBody
		public Map<String, Object> saleCountTransFast(
				@RequestParam Map<String, String> params) {
			BossUser bossUser = (BossUser) SecurityUtils.getSubject() .getPrincipal();
	    	String sale_name= bossUser.getRealName();
	    	params.put("sale_name",sale_name );
			return transService.saleCountTransFast(params);
		}
	
		/**
		 * 销售管理- 快捷交易查询
		 * @author 王帅
		 * @date 2014年10月21日11:39:05
		 * @see 根据销售名称，查询所属代理商快捷交易信息
		 * @param model 返回分页数据信息
		 * @param params 查询条件
		 * @param cpage 分页信息
		 * @return  model 返回分页数据信息
		 */
		@RequestMapping(value = "/transFast")
		public String transFast(final ModelMap model, 	@RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage) {
			log.info("SalesController transFast start ...");
			PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
			if (params.get("createTimeBegin") == null	&& params.get("createTimeEnd") == null) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String createTime = sdf.format(date);
				String createTimeBegin = createTime + " 00:00:00";
				String createTimeEnd = createTime + " 23:59:59";
				params.put("createTimeBegin", createTimeBegin);
				params.put("createTimeEnd", createTimeEnd);
			}
			BossUser bossUser = (BossUser) SecurityUtils.getSubject() .getPrincipal();
	    	String sale_name= bossUser.getRealName();
	    	params.put("sale_name",sale_name );
			Page<Map<String, Object>> list = transService.getSaleTransFastPat(params, page);
			Page<Map<String, Object>> list1 = agentService.getAgentListAccordLinkTag(params, new PageRequest(0, 1000));
			model.put("list1", list1);
			model.put("p", cpage);
			model.put("list", list);
			model.put("params", params);
			log.info("SalesController transFast end");
			return "/sale/saleTransFastQuery";
		}
		
	
	// 交易查询
	@RequestMapping(value = "/trans")
	public String trans(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage,
			HttpServletRequest request) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		if (params.get("createTimeBegin") == null
				&& params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime + " 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}

		params.put("sale_name",
			((BossUser) request.getSession().getAttribute("user"))
						.getRealName());
		
    BossUser bossUser = (BossUser) SecurityUtils.getSubject()
    .getPrincipal();
    

    String sale_name= bossUser.getRealName();
    
    
    params.put("sale_name",sale_name );
    
    

		Page<Map<String, Object>> list = transService.getTrans(params, page);
//		Map<String, Object> totalMsg = transService.countTransInfo(params);

		Page<Map<String, Object>> list1 = agentService
				.getAgentListAccordLinkTag(params, new PageRequest(0, 1000));
		model.put("list1", list1);
		model.put("p", cpage);
		model.put("list", list);
//		model.put("totalMsg", totalMsg);
		model.put("params", params);
		return "/sale/merchantTransQuery";
	}

	// 统计交易信息
	@RequestMapping("/countTransInfo")
	@ResponseBody
	public Map<String, Object> countTransInfo(@RequestParam Map<String, String> params) {
		
	    BossUser bossUser = (BossUser) SecurityUtils.getSubject()
	    	    .getPrincipal();
	    String sale_name= bossUser.getRealName();
	    params.put("sale_name",sale_name );
		return transService.countTransInfo(params);
	}
	
	// 商户查询
	@RequestMapping(value = "/merQuery")
	public String merQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage,
			HttpServletRequest request) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);

		if (params.size() == 110) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime + " 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}

	  BossUser bossUser = (BossUser) SecurityUtils.getSubject().getPrincipal();
    

    String belongTo_sale_name= bossUser.getRealName();
    
    params.put("belongTo_sale_name",belongTo_sale_name );
    params.put("sale_name",belongTo_sale_name );
    
		Page<Map<String, Object>> list = merchantService.getMerList(params,
				page);

		Page<Map<String, Object>> list1 = agentService
				.getAgentListAccordLinkTag(params, new PageRequest(0, 1000));
		model.put("list1", list1);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/sale/merchantQuery";
	}

	// 代理商查询
	@RequestMapping(value = "/agentQuery")
	public String agentQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage,
			HttpServletRequest request) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		if (params.size() == 110) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);
			String createTimeBegin = createTime + " 00:00:00";
			String createTimeEnd = createTime + " 23:59:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}


		BossUser bossUser = (BossUser) SecurityUtils.getSubject()
    .getPrincipal();
    

    String belongTo_sale_name= bossUser.getRealName();;
    
    params.put("belongTo_sale_name",belongTo_sale_name );
    params.put("sale_name",belongTo_sale_name );
    

		Page<Map<String, Object>> list = agentService.getAgentListAccordLink(
				params, page);
		Page<Map<String, Object>> list1 = agentService
				.getAgentListAccordLinkTag(params, new PageRequest(0, 1000));
		model.put("list1", list1);

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);

		return "/sale/agentQuery";
	}
	
	// 商户查询
	@RequestMapping(value = "/acqMerchantQuery")
	public String acqMerchantQuery(final ModelMap model,
			@RequestParam Map<String, String> params,
			@RequestParam(value = "p", defaultValue = "1") int cpage) {

		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		
		BossUser bossUser = (BossUser) SecurityUtils.getSubject().getPrincipal();
		    

	    String belongTo_sale_name= bossUser.getRealName();
	    
	    params.put("belongTo_sale_name",belongTo_sale_name );
	    params.put("sale_name",belongTo_sale_name );

		Page<Map<String, Object>> list = acqMerchantService.getMerchantList(
				params, page);

		List<Map<String, Object>> acqOrgList = acqMerchantService
				.getMerchantList();

		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		model.put("acqOrgList", acqOrgList);
		return "/sale/acqMerchantQuery";
	}
	
	// 商户详情
	@RequestMapping(value = "/merDetail")
	public String merDetail(final ModelMap model,
			@RequestParam Map<String, String> params) {
		String id = params.get("id");
		Map<String, Object> map = acqMerchantService.queryAcqMerchantInfo(Long
				.valueOf(id));

		Object ladder_fee = map.get("ladder_fee");
		if (ladder_fee != null && ladder_fee.toString().length() > 0) {
			if (ladder_fee.toString().indexOf("<") > 0) {
				String[] ladder = ladder_fee.toString().split("<");
				String ladder_min = ladder[0];
				String ladder_value = ladder[1];
				String ladder_max = ladder[2];

				map.put("ladder_min",
						new BigDecimal(ladder_min).movePointRight(2));
				map.put("ladder_value", ladder_value);
				map.put("ladder_max",
						new BigDecimal(ladder_max).movePointRight(2));
			}
		}
		
		Object merchant_rates = map.get("merchant_rate");
		
		String fee_type = map.get("fee_type").toString();
		if(null != merchant_rates && !"".equals(merchant_rates)){
			if("RATIO".equals(fee_type)){
				String merchant_min = merchant_rates.toString();
				map.put("merchant_Drate",
						new BigDecimal(merchant_min).movePointRight(2));
			}else if("CAPPING".equals(fee_type)){
				String[] mrate = merchant_rates.toString().split("~");
				String merchant_min = mrate[0];
				String merchant_value = mrate[1];
				map.put("merchant_Drate",	new BigDecimal(merchant_min).movePointRight(2));
				map.put("merchant_max_amount", merchant_value);
				
			}else if("LADDER".equals(fee_type)){
				String[] mrate = merchant_rates.toString().split("<");
				String merchant_min = mrate[0];
				String merchant_value = mrate[1];
				String merchant_max = mrate[2];
				
				map.put("merchant_min",	new BigDecimal(merchant_min).movePointRight(2));
				map.put("merchant_value", merchant_value);
				map.put("merchant_max",
						new BigDecimal(merchant_max).movePointRight(2));
			}
		}

		model.put("params", map);
		return "/sale/acqMerchantDetail";
	}


}
