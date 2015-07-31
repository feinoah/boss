<%@page pageEncoding="utf8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<div id="header" style="position:relative;">
  <div id="top">
  	<div id="logo"> <img src="${ ctx}/images/logo1.jpg"/></div>
    <div id="info">欢迎您：<shiro:user><shiro:principal property="realName"/>&nbsp;&nbsp;<a href="${ctx}/changepwd">修改密码</a> | <a href="${ctx}/logout">退出</a></shiro:user></div>
    <p style="position:absolute;right:23px;font-size:20px;color:white;">客服电话：400-600-2999</p>
  </div>
</div>
