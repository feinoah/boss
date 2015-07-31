/**
 * 
 */
package com.eeepay.boss.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * hdb
 * 2013-7-11 上午11:20:35 
 */
public class EncodingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest=((HttpServletRequest)request);
		
		String original="";
		// 获取原始的请求来源
		if(httpServletRequest.getAttribute("javax.servlet.forward.request_uri")!=null){
			original=(String)httpServletRequest.getAttribute("javax.servlet.forward.request_uri");
		} 
		Holder<Boolean> flag=new Holder<Boolean>(false);
		// 如果原始请求地址和需要跳转的地址一致，抛出异常
		if(original.equals(httpServletRequest.getRequestURI())){
			System.out.println("[error] find circle");
			// 可以选择抛出异常，或者用其他的处理方式
			chain.doFilter(request, response);
			try {
				throw new Exception("死循环");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		
		String url=httpServletRequest.getServletPath();
		String params=encodingParams(httpServletRequest,flag);
		if(flag.getValue()){
			StringBuffer msUrl=new StringBuffer(url);
			if(params!=null&&params.length()>1){
				msUrl.append("?").append(params);
			}
			
			System.out.println(msUrl.toString());
			httpServletRequest.getRequestDispatcher(msUrl.toString()).forward(request, response);
		}else{
			chain.doFilter(request, response);
		}
		
//		((HttpServletResponse)response).sendRedirect(msUrl.toString());
//		httpServletRequest.getR
//		new EncodingRequestWrapper()
	}
	
	private String encodingParams(HttpServletRequest httpServletRequest,Holder<Boolean> flagHolder){
		Enumeration paramNames=httpServletRequest.getParameterNames();
		StringBuffer params=new StringBuffer();
		boolean flag=false;
		while ((paramNames != null) && (paramNames.hasMoreElements())) {
		      String paramName = (String)paramNames.nextElement();
		      if (paramName!=null&&!"".equals(paramName.trim()) ) {
		        String[] values = httpServletRequest.getParameterValues(paramName);
		        //TODO
		        if(values.length>1){
//		        	List<String> list=new ArrayList<String>();
		        	for(String val:values){
		        		if(val.contains("<")||val.contains(">")){
		        			flagHolder.setValue(true);
		        		}
//		        		list.add(StringEscapeUtils.escapeHtml(val));
		        		params.append(paramName).append("=").append(StringEscapeUtils.escapeHtml(val)).append("&");
		        	}
//		        	httpServletRequest.setAttribute(paramName, list);
		        }else{
		        	if(values[0].contains("<")||values[0].contains(">")){
	        			flagHolder.setValue(true);
	        		}
//		        	httpServletRequest.setAttribute(paramName, StringEscapeUtils.escapeHtml(values[0]));
			        	params.append(paramName).append("=").append(StringEscapeUtils.escapeHtml(values[0])).append("&");
			        	flag=true;
		        }
		      }
		    }
//		return httpServletRequest;
		return flag?params.toString().substring(0, params.toString().length()-1):params.toString();
	}
	
	@Override
	public void destroy() {
		
	}
}
