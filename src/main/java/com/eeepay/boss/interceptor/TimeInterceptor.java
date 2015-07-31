package com.eeepay.boss.interceptor;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.eeepay.boss.domain.BossUser;
import com.eeepay.boss.utils.IPParser;

public class TimeInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory
			.getLogger(TimeInterceptor.class);
	private ThreadLocal<Long> startTime = new ThreadLocal<Long>();

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		startTime.set(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer();
		if (handler instanceof HandlerMethod) {
			sb.append(printHandler((HandlerMethod) handler));
		}

		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		sb = new StringBuffer();
		sb.append("[进来]:");
		sb.append("[" + "线程:" + Thread.currentThread().getId() + "]");
		sb.append(":");
		sb.append("[" + "用户名:" + getUser() + "]");
		sb.append(":");
		sb.append("[" + "请求地址:" + request.getRequestURI() + "]");
		sb.append(":");
		sb.append("[" + "IP:" + ip + "]");
		sb.append(":");
		sb.append("[" + "地理位置:" + IPParser.parse(ip).toString() + "]");
		logger.info(sb.toString());
		return true;
	}

	public String getUser() {
		BossUser bossUser = (BossUser) SecurityUtils.getSubject().getSession()
				.getAttribute("user");
		if (null != bossUser)
			return bossUser.getUserName();
		return null;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		Map params = request.getParameterMap();
		Iterator it = params.keySet().iterator();

		StringBuffer sbb = new StringBuffer();
		while (it.hasNext()) {
			String paramName = (String) it.next();
			String paramValue = request.getParameter(paramName);
			// 处理你得到的参数名与值
			sbb.append(paramName);
			sbb.append("=");
			sbb.append(paramValue);
			sbb.append(",");
		}

		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("[执行]:");
		sb.append("[" + "线程:" + Thread.currentThread().getId() + "]");
		sb.append(":");
		sb.append("[" + "用户名:" + getUser() + "]");
		sb.append(":");
		sb.append("[" + "请求地址:" + request.getRequestURI() + "]");
		sb.append(":");
		sb.append("[" + "参数:" + sbb.toString() + "]");
		sb.append(":");
		sb.append("[" + "IP:" + ip + "]");
		sb.append(":");
		sb.append("[" + "地理位置:" + IPParser.parse(ip).toString() + "]");

		logger.info(sb.toString());
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long costTime = System.currentTimeMillis() - startTime.get();
		StringBuffer sb = new StringBuffer();
		if (handler instanceof HandlerMethod) {
			sb.append(printHandler((HandlerMethod) handler));
		}

		/*String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		sb = new StringBuffer();

		sb = new StringBuffer();
		sb.append("[完成]:");
		sb.append("[" + "线程:" + Thread.currentThread().getId() + "]");
		sb.append(":");
		sb.append("[" + "用户名:" + getUser() + "]");
		sb.append(":");
		sb.append("[" + "请求地址:" + request.getRequestURI() + "]");
		sb.append(":");
		sb.append("[" + "执行时间:" + costTime + "ms]");
		sb.append(":");
		sb.append("[" + "IP:" + ip + "]");
		sb.append(":");
		sb.append("[" + "地理位置:" + IPParser.parse(ip).toString() + "]");

		final String content = sb.toString();

		if (costTime > 5000) {
			new Thread(new Runnable() { // 这条线程 检查 coll 是否工作完成
						public void run() {
							String title = "BOSS-长-";
							logger.info(title);
							String[] s = { "18123978207", "15999527344",
									"18675566630" };
							// Sms.sendMsg(s, title + "," + content);
						}
					}).start();

		}*/

		logger.info(sb.toString());

	}

	private String printHandler(HandlerMethod hm) {
		StringBuffer sb = new StringBuffer();
		sb.append("handler class:" + hm.getBeanType().getName() + "."
				+ hm.getMethod().getName() + "()");
		return sb.toString();
	}
}
