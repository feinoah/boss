package com.eeepay.boss.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeepay.boss.service.AgentService;
import com.eeepay.boss.service.IncrementService;
import com.eeepay.boss.utils.Dao;
import com.eeepay.boss.utils.MD5;
import com.eeepay.boss.vo.IncOrder;
import com.eeepay.boss.vo.MessageVO;

@Controller
@RequestMapping(value = "/increment")
public class IncrementController extends BaseController {
	@Resource
	private Dao dao;

	@Resource
	private IncrementService  incrementService;
	
	
	// 手机充值
	@RequestMapping(value = "/recharge")
	public String recharge() {
		return "/increment/recharge";
	}

	public Map<Object, Object> buildResult(String errorCode, String msg,
			Map<Object, Object> content) {
		Map<Object, Object> head = new HashMap<Object, Object>();
		head.put("status", errorCode);
		head.put("error", msg);

		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("head", head);
		result.put("content", content);
		return result;
	}

	@RequestMapping(value = "/agent")
	public void agent(@RequestParam String userName,
			HttpServletResponse response, @RequestParam String password) {
		password = MD5.toMD5(password);
		Map<String, Object> m = dao
				.findFirst(
						"select i.agent_name from agent_user a ,agent_info i where a.agent_no=i.agent_no and   a.user_name=? and a.password=?",
						new Object[] { userName, password });
		if (null == m) {
			Map<Object, Object> result = buildResult("300", "用户名密码不正确", null);

			String text = JSON.toJSONString(result);
			outJson(text, response);
		} else {

			String name = m.get("agent_name").toString();

			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("name", name);

			Map<Object, Object> result = buildResult("200", "", map);

			String text = JSON.toJSONString(result);
			outJson(text, response);
		}

	}

	// 手机充值保存
	@RequestMapping(value = "/rechargeSave")
	public String rechargeSave(final ModelMap model,
			@RequestParam Map<String, String> params) {

		try {
			// 创建订单
			String order = executeGet("http://120.132.132.106:5780/mer/module/create.do?transType=mobile&&amount="
					+ params.get("selectValue"));

			IncOrder orders = JSONObject.parseObject(order, IncOrder.class);
			System.out.println(orders.getOrderNo());

			String mobileNo = params.get("mobileNo");
			String body = executeGet("http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="
					+ mobileNo);
			body = body.substring(body.indexOf("{"), body.length());
			MessageVO vo = JSONObject.parseObject(body, MessageVO.class);
			params.put("cat", vo.getCatName());
			params.put("province", vo.getProvince());
			params.put("orderNo", orders.getOrderNo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("confirm", params);
		return "/increment/rechargeConfirm";
	}

	@SuppressWarnings("finally")
	public String executeGet(String url) throws Exception {
		BufferedReader in = null;
		String content = null;
		try {
			// 定义HttpClient
			HttpClient client = new DefaultHttpClient();
			// 实例化HTTP方法
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			content = sb.toString();
		} finally {
			if (in != null) {
				try {
					in.close();// 最后要关闭BufferedReader
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return content;
		}
	}
	
	
	/*
	 * 公共缴费查询
	 */
	@RequestMapping(value = "/pubPayList")
	public String pubPayList(final ModelMap model, @RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage) {
		PageRequest page = new PageRequest(cpage - 1, PAGE_NUMERIC);
		
		if (params.get("createTimeBegin") == null && params.get("createTimeEnd") == null) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(date);

			String createTimeBegin = createTime + " 00:00";
			String createTimeEnd = createTime + " 23:59";
			params.put("createTimeBegin", createTimeBegin);
			params.put("createTimeEnd", createTimeEnd);
		}

		Page<Map<String, Object>> list = incrementService.getPubPayList(params, page);
		model.put("p", cpage);
		model.put("list", list);
		model.put("params", params);

		return "/increment/pubPaytrans";
	}

	/*
	 * 公共缴费查询详情
	 */
	@RequestMapping(value = "/pubPayInfo")
	public String pubPayInfo(final ModelMap model, @RequestParam Long id) {
		Map<String, Object> list = incrementService.getPubPayDetail(id);
		model.put("params", list);
		return "/increment/pubPayDetail";
	}
}
