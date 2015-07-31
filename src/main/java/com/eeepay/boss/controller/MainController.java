package com.eeepay.boss.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.service.AgentService;
import com.eeepay.boss.service.UserService;
import com.eeepay.boss.utils.GenSyncNo;
import com.eeepay.boss.utils.MD5;

/**
 * 系统登录，注销，首页等
 * 
 * @author dj
 * 
 */
@Controller
public class MainController extends BaseController {
	@Resource
	private AgentService agentService;
	@Resource
	private UserService userService;
	
	public static final int MAX_PASSWORD_ERROR_COUNT=2;
	// private ReentrantLock  lock=new ReentrantLock();

	// 系统登录后的首页
	@RequestMapping(value = {"/","index"})
	public String main(Model model) {
		return "index";
	}

	// 用于未通过系统验证输入URL的跳转页面
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String login(Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Subject subject=SecurityUtils.getSubject();
		if(subject.isAuthenticated()){
			return "redirect:/";
		}
		return "login";
	}
	
	// 用户登录验证,用于提交form表单数据验证
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam Map<String, String> params,
			HttpServletRequest request, Model model) {
		
		
		BossUser user = null;
		try {

			user = userService.getUserByPwd((String) params.get("username"),
					MD5.toMD5((String) params.get("password")));
			
/*			lock.lock();
			ServletContext application = request.getSession().getServletContext();
			if(user!=null){
				if(application.getAttribute(user.getUserName())!=null){
					request.setAttribute("LOGINFLAG","USERISLOGINING");
					return "login";
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			// lock.unlock();
		}
		if (SecurityUtils.getSubject().isAuthenticated() && user != null
				&& Integer.parseInt(user.getStatus()) != 0) {
			
			return "redirect:/";
		} else {
			user = userService.getByUserName(params.get("username"));
			if (null != user && null != user.getFailTimes()) {
				if (user.getFailTimes() > 6) {
					userService.status(params.get("username"), 0);
				}
			}
			String errorStr = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
			if(errorStr!=null&&!errorStr.endsWith("CaptchaException")){
				userService.failTimes(params.get("username"));
			}
		}
		if (user == null) {
			request.setAttribute(
					FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME,
					"IncorrectCredentialsException");
		}
		
/*		else {
			request.setAttribute(
					FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME,
					"UnknownAccountException");
		}*/
		model.addAttribute("username", params.get("username"));
		return "login";
	}

	// 修改密码
	@RequestMapping(value = "/changepwd", method = RequestMethod.GET)
	public String changepwd(Model model) {

		return "changepwd";
	}

	// 修改密码
	@RequestMapping(value = "/savepwd", method = RequestMethod.POST)
	public void savepwd(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params) {

		Map<String, String> okMap = new HashMap<String, String>();
		BossUser user = (BossUser) request.getSession().getAttribute("user");
		String userName = user.getUserName();
		String pwd = params.get("pwd");
		String pwd1 = params.get("pwd1");
		String pwd2 = params.get("pwd2");
		if (!pwd1.equals(pwd2)) {
			okMap.put("msg", "ERROR");
			String json = JSONObject.fromObject(okMap).toString();
			outJson(json, response);
		}
		try {
			int affectedRows = userService.changUserPwd(userName, pwd, pwd1);
			if (affectedRows > 0) {
				okMap.put("msg", "OK");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			} else {
				okMap.put("msg", "ERROR");
				String json = JSONObject.fromObject(okMap).toString();
				outJson(json, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//验证用户是否需要输入验证码
	@RequestMapping("/servlet/isMustValidate")
	@ResponseBody
	public Map<String,Object> isMustValidate(@RequestParam("u") String username){
		Map<String, Object> rmap=new HashMap<String, Object>(1);
		BossUser bu=userService.getByUserName(username);
		//如果用户不存在就不显示验证码
		if(bu==null){
			rmap.put("y",false);
		}else{
			Integer failTimes=bu.getFailTimes();
			rmap.put("y", failTimes != null && failTimes > MAX_PASSWORD_ERROR_COUNT);
		}
		return rmap;
	}

	@RequestMapping(value = {"/getExportTime"})
	public void getExportTime(HttpServletResponse response) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");

		Calendar cal = Calendar.getInstance();
		Date dateNow = cal.getTime();

		// 早上8点
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date dateStart = cal.getTime();

		// 晚上6点
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date dateEnd = cal.getTime();

		outText(dateNow.before(dateStart) || dateNow.after(dateEnd) ? "1" : "0", response);
	}
}
