package com.eeepay.boss.controller;

import java.io.IOException;
import java.util.Map;

import com.eeepay.boss.utils.SysConfig;
import org.apache.commons.httpclient.HttpException;

import com.eeepay.boss.utils.Constants;
import com.eeepay.boss.utils.Http;
import com.eeepay.boss.utils.md5.Md5;

public class RunnableController implements Runnable {

	private Map<String, Object> params;

	private String statusLine;

	private String responseBody;

	/**
	 * 获取返回状态
	 * 
	 * @return
	 * @author zengja
	 * @date 2014年5月27日 下午6:29:17
	 */
	public String getStatusLine() {
		return statusLine;
	}

	/**
	 * 获取返回的信息
	 * 
	 * @return
	 * @author zengja
	 * @date 2014年5月27日 下午6:29:36
	 */
	public String getResponseBody() {
		return responseBody;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	/**
	 * 实例化一个支持多线程的控制器，以实现在新注册商户时，创建对应的登陆用户
	 * 
	 * @param params
	 *            包含登陆用户所需信息的参数
	 * @param request
	 * @param response
	 * @param attr
	 */
	public RunnableController(Map<String, Object> params) {
		super();
		this.params = params;
	}

	/**
	 * 调用接口注册用户，注册信息来源新注册商户
	 * 
	 * @param params
	 * @author zengja
	 * @return 返回RunnableController对象
	 * @throws IOException
	 * @throws HttpException
	 * @date 2014年5月27日 下午1:26:29
	 */
	public RunnableController apiRegUser(Map<String, Object> params) {
		//StringBuffer url = new StringBuffer("http://115.28.36.50:9280/bag/apiReg.do");

		String url = SysConfig.value("bagApiReg");
		String hmac = Md5.md5Str(params.get("mobile_username").toString() + params.get("mobile_password").toString() + Constants.BAG_HMAC);
		params.put("hmac", hmac);
		String xmlStr = Http.send(url.toString(), params, "UTF-8");
		System.out.println("审核通过同步钱包返回信息="+xmlStr);
		return null;
	}

	@Override
	public void run() {
		apiRegUser(this.params);
	}

	/**
	 * 
	 @Override public void run() { Class<? extends RunnableController> c =
	 *           this.getClass(); try { Method m1 = c.getMethod(this.methodName,
	 *           String.class); Object returnS = m1.invoke(this, new Object[] {
	 *           "Success!" }); System.out.println(returnS); } catch
	 *           (SecurityException e1) { e1.printStackTrace(); } catch
	 *           (NoSuchMethodException e1) { e1.printStackTrace(); } catch
	 *           (IllegalArgumentException e) { e.printStackTrace(); } catch
	 *           (IllegalAccessException e) { e.printStackTrace(); } catch
	 *           (InvocationTargetException e) { e.printStackTrace(); }
	 * 
	 *           }
	 */

}
