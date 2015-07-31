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
	<div class="title clear">黑名单信息 </div>
	<ul>
		  <li style="width:310px;" id="sn"><span style="width:100px;">黑名单类型：</span>
		   <c:choose>
          		<c:when test="${params['black_type'] eq '1'}">卡号黑名单</c:when>
          		<c:when test="${params['black_type'] eq '2'}">商户号黑名单</c:when>
          		<c:when test="${params['black_type'] eq '3'}">身份证黑名单</c:when>
	       	</c:choose>
		 </li>
		  <li style="width:310px;" id="psam_no"><span style="width:100px;">黑名单值：</span>${params['black_value']}</li>
		  <li style="width:310px;" id="terminal_no"><span style="width:100px;">状态：</span>
		 	 <c:choose>
          		<c:when test="${params['status'] eq '1'}">启用</c:when>
          		<c:when test="${params['status'] eq '0'}">停用</c:when>
	         </c:choose>
		 </li>
		  <li style="width:310px;" id="open_status"><span style="width:100px;">创建时间：</span>
		 <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		   </li>
  		  <li style="width:500px;" id="model"><span style="width:100px;">加入黑名单原因：</span>${params['cause']}</li>
    </ul>
		  <div class="clear"></div>
</div>
</body>
</html>