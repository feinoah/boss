package com.eeepay.boss.controller;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeepay.boss.service.BagBizService;


/**
 * 钱包管理
 * 
 */
@Controller
@RequestMapping(value = "/bag")
public class BagController extends BaseController {
	@Resource
	private BagBizService bagBizService;

	public static void main(String[] args) {
		String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher("13811111111");
		System.out.println(m.find());
	}

	@RequestMapping(value = "/reg")
	public void savepwd(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {
		String nick = params.get("nick");
		String mobile = params.get("mobile");
		String regExp = "^[1]([3][0-9]{1}|59|58|88|89|86|80|81|82)[0-9]{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(mobile);
		if(m.find()){
			if (bagBizService.exists(mobile)) {
				outText("注册失败,手机号已经存在", response);
			} else {
				if(StringUtils.isEmpty(nick)||nick.length()<2){
					outText("注册失败，真实姓名不正确", response);
				}else{
					try {
						bagBizService.reg(mobile,nick);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					outText("注册成功，初始密码已经发送至您的手机，请注意查收", response);
				}
			}
		}else { 
			outText("注册失败,手机号有误", response);
		}
	}
}
