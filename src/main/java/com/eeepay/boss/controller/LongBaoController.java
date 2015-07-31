package com.eeepay.boss.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.eeepay.boss.service.LongBaoService;



@Controller
@RequestMapping(value = "/longBao")
public class LongBaoController extends BaseController{

	@Resource
	private LongBaoService baoService;
	
	
    // 龙宝查询
	@RequestMapping(value = "/longBaoQuery")
	public String longBaoQuery(final ModelMap model,
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
		
		Page<Map<String, Object>> list = baoService.getLongBaoList(params,page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);
		return "/long/longBaoQuery";
	}
	
	
}
