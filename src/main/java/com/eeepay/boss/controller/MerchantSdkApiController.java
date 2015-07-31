package com.eeepay.boss.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeepay.boss.common.bean.MerchantApiAddBean;
import com.eeepay.boss.service.MerchantService;

@Controller
@RequestMapping("/merchantSdkApi")
public class MerchantSdkApiController extends BaseController {
	@Resource
	private MerchantService merchantService;

	@RequestMapping("merchantSdkApiAddPage")
	public String goAddPage() {
		return "merchantSdkApi/merchantSdkApiAdd";
	}

	@RequestMapping("merchantSdkApiQuery")
	public String merchantApiQuery(@RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage, Model model) {
		PageRequest pageRequest = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> page = merchantService.findMerchantSdkApiList(params, pageRequest);
		model.addAttribute("p", cpage);
		model.addAttribute("list", page);
		model.addAttribute("totalMsg", "");
		model.addAttribute("params", params);
		return "merchantSdkApi/merchantSdkApiQuery";
	}

	@RequestMapping("searchMerchant4SdkApi")
	@ResponseBody
	public List<Map<String, Object>> searchMerchant(@RequestParam("kw") String kw) {
		return merchantService.findMerchantByName4sdk(kw);
	}

	@RequestMapping("merchantSdkApiAdd")
	@ResponseBody
	public Map<String, Object> merchantApiAdd(MerchantApiAddBean maab) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			merchantService.addMerchantSdkApi(maab, getUser().getId());
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("success", success);
		return map;
	}

	@RequestMapping("changeForbindden")
	@ResponseBody
	public Map<String, Object> changeForbindden(@RequestParam("status") String status, @RequestParam("merchantId") Long merchantId) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			int s = Integer.parseInt(status);
			if (s > 1) {
				s = 0;
			}
			merchantService.updateMerchantSdkApiUsedStatus(s, merchantId);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("success", success);
		return map;
	}

	@RequestMapping("delMerchantSdkApi")
	@ResponseBody
	public Map<String, Object> delMerchantApi(@RequestParam("id") String id) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			long l = Long.parseLong(id);
			merchantService.delMerchantSdkApi(l);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("success", success);
		return map;
	}

	@RequestMapping("goEditPage")
	public String goEditPage(@RequestParam("id") Long id, Model model) {
		model.addAttribute("m", merchantService.findMerchantSdkApiById(id));
		return "merchantSdkApi/merchantSdkApiEdit";
	}
	@RequestMapping("updateMerchantSdkApi")
	@ResponseBody
	public Map<String, Object> updateMerchantApi(MerchantApiAddBean maab) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			merchantService.updateMerchantSdkApi(maab);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("success", success);
		return map;
	}
	@RequestMapping("merchantSdkApiDetail")
	public String merchantApiDetail(@RequestParam("id") Long id, Model model){
		goEditPage(id, model);
		Map<String, Object> m= merchantService.findMerchantSdkApiById(id);
		model.addAttribute("m",m);
		return "merchantSdkApi/merchantSdkApiDetail";
	}
}
