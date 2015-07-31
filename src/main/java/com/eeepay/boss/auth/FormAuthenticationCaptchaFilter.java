package com.eeepay.boss.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.service.AgentService;
import com.eeepay.boss.service.UserService;

public class FormAuthenticationCaptchaFilter extends FormAuthenticationFilter {
	
	@Resource
	private AgentService agentService;
	@Resource
	private UserService userService;
	
	private ReentrantLock  lock=new ReentrantLock();

	public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

	private String captchaParam = DEFAULT_CAPTCHA_PARAM;

	public String getCaptchaParam() {

		return captchaParam;

	}

	protected String getCaptcha(ServletRequest request) {

		return WebUtils.getCleanParam(request, getCaptchaParam());

	}

	protected AuthenticationToken createToken(ServletRequest request,
			ServletResponse response) {

		String username = getUsername(request);

		String password = getPassword(request);

		String captcha = getCaptcha(request);

		boolean rememberMe = isRememberMe(request);

		String host = getHost(request);

		return new UsernamePasswordCaptchaToken(username,

		password.toCharArray(), rememberMe, host, captcha);

	}
	
	// 登录成功操作,这里设置了代理商常用信息
		@Override
		protected boolean onLoginSuccess(AuthenticationToken token,
				Subject subject, ServletRequest request, ServletResponse response)
				throws Exception {
			BossUser bossUser = (BossUser) SecurityUtils.getSubject()
					.getPrincipal();
			HttpSession session = WebUtils.toHttp(request).getSession(true);

			session.setAttribute("user", bossUser);
			session.setAttribute("power", userService.checkPower(bossUser.getId()));
			Map<String, String> param = new HashMap<String, String>();
			WebUtils.issueRedirect(request, response, getSuccessUrl(), param, true);
			// save log
			String ip = request.getRemoteAddr();
			//复位该用户密码错误次数
			userService.failTimesReset(bossUser.getUserName());
			userService.saveLog(ip, bossUser);
/*			try{
				lock.lock();
				ServletContext application = ((HttpServletRequest)request).getSession().getServletContext();
				if(application.getAttribute(bossUser.getUserName())==null){
					application.setAttribute(bossUser.getUserName(), session);
				}
			}catch (Exception e) {
				throw new Exception(e.getMessage());
			}finally{
				lock.unlock();
			}*/
			return false;
		}

}
