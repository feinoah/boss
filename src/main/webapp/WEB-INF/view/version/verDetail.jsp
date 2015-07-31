<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">版本信息 </div>
	<ul>
		  <li style="width:310px;" id="version"><span style="width:100px;">版本号：</span>
			 ${params['version']}
		 </li>
		  <li style="width:310px;" id="platform"><span style="width:100px;">系统平台：</span>
		 	 <c:choose>
          		<c:when test="${params['platform'] eq '1'}">iOS</c:when>
          		<c:when test="${params['platform'] eq '0'}">android</c:when>
	         </c:choose>
		 </li>
		 <li style="width:310px;" id="app_type"><span style="width:100px;">客户端类型：</span>
		     <c:choose>
          		<c:when test="${params['app_type'] eq '0'}">银联商宝</c:when>
          		<c:when test="${params['app_type'] eq '1'}">移小宝</c:when>
          		<c:when test="${params['app_type'] eq '2'}">通付宝</c:when>
          		<c:when test="${params['app_type'] eq '3'}">中宽支付</c:when>
	         </c:choose>
		 </li>
		 <li style="width:310px;" id="down_flag"><span style="width:100px;">下载标志：</span>
		 <c:choose>
          		<c:when test="${params['down_flag'] eq '0'}">不需要</c:when>
          		<c:when test="${params['down_flag'] eq '1'}">需要更新</c:when>
          		<c:when test="${params['down_flag'] eq '2'}">需要强制下载</c:when>
	     </c:choose>		
		 </li>		  
         <li style="width:580px;" id="app_url"><span style="width:70px;">APP地址：</span>
		  ${params['app_url']}
		 </li>
		 <li style="width:580px;" id="app_logo"><span style="width:70px;">APP导航：</span>
			 ${params['app_logo']}
		 </li>
		 <li style="width:580px;" id="create_time"><span style="width:70px;">创建时间：</span>
		 <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		  </li>	
		 <li style="width:580px;height:80px;" id="ver_desc"><span style="width:100%">版本描述：</span>
			 ${params['ver_desc']}
		 </li>
    </ul>
		  <div class="clear"></div>
</div>
</body>
</html>