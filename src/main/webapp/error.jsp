<%@page language="java" contentType="text/html; charset=UTF-8" isErrorPage="true"  pageEncoding="UTF-8" %>
<%@include file="/tag.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<html>
<head>
<script language="javascript" type="text/javascript"> 
// 以下方式直接跳转
window.location.href='/boss/';
 
</script>
</head>
<%response.setStatus(HttpServletResponse.SC_OK);%>
 <%
 /**
 * 本页面是在客户查找的页面无法找到的情况下调用的
 */
 response.setStatus(HttpServletResponse.SC_OK);
 %>
<body>
 


<div style="height:277px;width:731px;background-image:url(${ ctx}/images/notfound.jpg);position:relative;margin:150px auto;">
		<a href="javascript:history.go(-1)" style="position:absolute;top:210px;left:372px;">返回</a>
</div>





</body>
</html>