<%@page import="org.springframework.web.servlet.mvc.condition.ParamsRequestCondition"%>
<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ page isELIgnored="false"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>前海移联-业务运营管理系统</title>
<link type="image/x-icon" href="${ ctx}/images/small_logo.ico" rel="icon" />
<link type="image/x-icon" href="${ ctx}/images/small_logo.ico" rel="shortcut icon" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jquery.md5.js"></script>
<script type="text/javascript">
    
    // 检测登录页面是否是被嵌入到 iframe中，是的话弹到登录页面
    if(self.frameElement != null && (self.frameElement.tagName == "IFRAME" || self.frameElement.tagName == "iframe")){
    	top.location = "login.jsp";
    }
    
    var root='${ctx}';
    function refreshCaptcha() {
    	var _captcha_id = document.getElementById("img_captcha");
        _captcha_id.src="servlet/captchaCode?t=" + Math.random();
    }
    /*$(function(){
    	var u=$("input[name=username]");
    	u.on("blur",function(){

    			var u=$(this).val();
    			if(u==""){
    				return;
    			}
    		   var data="u="+u;
    		   $.ajax({
    			   url:root+"/servlet/isMustValidate",
    			   cache:false,
    			   data:data,
    			   success:function(r){
    				   var v=$(".validateLi");
    				  if(r.y){
    					  v.show();
    				  }else{
    					  v.hide();
    				  }
    			   }

    		   });
    	});
    	if(u.val()!=""){
    		u.trigger("blur");
    	}
    });*/

	$(function(){
		$('#loginForm').submit(function(event){
			$('#password').val($.md5($('#password').val()));
		});
	});
</script>

</head>
<body style="background-color: #FBFBFB">
	<div id="login">
		<div id="login_header">
			<img src="${ctx }/images/logo.jpg" alt="" />
		</div>
		<div id="left"></div>
		<div id="right">
			<form:form id="loginForm" action="${ctx}/login" method="post">
				<div id="top">用户登录</div>
				<ul>
					<li>
						<span>用户名：</span>
						<input id="username" style="width: 140px" name="username" value="${username}" />
					</li>
					<li>
						<span>密码：</span>
						<input type="password" style="width: 140px" id="password" name="password" />
					</li>
					<li class="validateLi" <%--style="display:none;"--%>>
						<span>验证码：</span>
						<input type="text" style="vertical-align: top" id="captcha" name="captcha" size="4" maxlength="4" class="required" />
						<img title="点击更换" id="img_captcha" onclick="javascript:refreshCaptcha();" src="servlet/captchaCode" />
					</li>
				</ul>

				<input name="submit" class="button blue" style="width: 140px" type="submit" id="submit" value="登&nbsp;录" />
				<%
						String msg = "";
						String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
						String flag = (String) request.getAttribute("LOGINFLAG");
						if(flag!=null){
							if(flag.endsWith("NoUpdatePasswordException")){
								msg = "口令过期";
							}else if(flag.endsWith("USERISLOGINING")){
								msg = "该用户已经登录";
							}
						}else if(error != null){
							
							if(error.endsWith("UnknownAccountException")){
								msg = "用户状态异常或用户名不存在";
							}
							if(error.endsWith("FailTimesException")){
								msg = "密码错误次数超限";
							}
							if(error.endsWith("IncorrectCredentialsException")){
								msg = "用户名或密码错";
							}
							if(error.endsWith("CaptchaException")){
								msg = "验证码错误，请重试.";
							}
						}
					%>
				<%=msg %>
			</form:form>
		</div>

	</div>

	<div id="copyright">前海移联科技版权所有 2010粤ICP备09161251号 客户服务热线： 400-600-2999(5*8小时)</div>
</body>
</html>
