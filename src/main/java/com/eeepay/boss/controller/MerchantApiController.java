package com.eeepay.boss.controller;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
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
@RequestMapping("/merchantApi")
public class MerchantApiController extends BaseController {
	@Resource
	private MerchantService merchantService;

	@RequestMapping("merchantApiAddPage")
	public String goAddPage() {
		return "merchantApi/merchantApiAdd";
	}

	@RequestMapping("merchantApiQuery")
	public String merchantApiQuery(@RequestParam Map<String, String> params, @RequestParam(value = "p", defaultValue = "1") int cpage, Model model) {
		PageRequest pageRequest = new PageRequest(cpage - 1, PAGE_NUMERIC);
		Page<Map<String, Object>> page = merchantService.findMerchantApiList(params, pageRequest);
		model.addAttribute("p", cpage);
		model.addAttribute("list", page);
		model.addAttribute("totalMsg", "");
		model.addAttribute("params", params);
		return "merchantApi/merchantApiQuery";
	}

	@RequestMapping("searchMerchant")
	@ResponseBody
	public List<Map<String, Object>> searchMerchant(@RequestParam("kw") String kw) {
		return merchantService.findMerchantByName(kw);
	}

	@RequestMapping("merchantApiAdd")
	@ResponseBody
	public Map<String, Object> merchantApiAdd(MerchantApiAddBean maab) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			merchantService.addMerchantApi(maab, getUser().getId());
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
			merchantService.updateMerchantApiUsedStatus(s, merchantId);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("success", success);
		return map;
	}

	@RequestMapping("delMerchantApi")
	@ResponseBody
	public Map<String, Object> delMerchantApi(@RequestParam("id") String id) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			long l = Long.parseLong(id);
			merchantService.delMerchantApi(l);
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
		model.addAttribute("m", merchantService.findMerchantApiById(id));
		return "merchantApi/merchantApiEdit";
	}
	@RequestMapping("updateMerchantApi")
	@ResponseBody
	public Map<String, Object> updateMerchantApi(MerchantApiAddBean maab) {
		Map<String, Object> map = new HashMap<String, Object>(1);
		boolean success = false;
		try {
			merchantService.updateMerchantApi(maab);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("success", success);
		return map;
	}
	@RequestMapping("merchantApiDetail")
	public String merchantApiDetail(@RequestParam("id") Long id, Model model){
		goEditPage(id, model);
		Map<String, Object> m= merchantService.findMerchantApiById(id);
		String publicKeyBase64=m.get("public_key_base64").toString();
		model.addAttribute("m",m);
		KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("rsa");
			X509EncodedKeySpec xeks=new X509EncodedKeySpec(Base64.decodeBase64(publicKeyBase64));
			PublicKey publicKey=kf.generatePublic(xeks);
			RSAPublicKey rk=(RSAPublicKey) publicKey;
			String modulus=new String(Hex.encodeHex(rk.getModulus().toByteArray()));
			model.addAttribute("modulus", modulus);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		
		return "merchantApi/merchantApiDetail";
	}
}
