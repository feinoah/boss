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
	<div class="title clear">钱包充值信息 </div>
	<ul>
		  <li style="width:310px;" id="mobile_no"><span style="width:60px;">手机号：</span>
			 ${params['mobile_no']}
		 </li>
		 <li style="width:310px;" id="amount"><span style="width:50px;">金额：</span>	
		 	 ${params['amount']}
		 </li>		
		 <li style="width:310px;" id="result"><span style="width:70px;">充值结果：</span>
		   <c:choose>
				   <c:when test="${params['result'] eq 'SUCCESS'}">成功</c:when>
				   <c:when test="${params['result'] eq 'INIT'}">初始化</c:when>
				   <c:when test="${params['result'] eq 'FAIL'}">失败</c:when>
				   <c:otherwise>${params['result']}</c:otherwise>
		   </c:choose>		  
		 </li>
		 <li style="width:310px;" id="source"><span style="width:50px;">来源：</span>
		     ${params['source']}
		 </li>
		  <li style="width:310px;" id="order_no"><span style="width:70px;">订单编号：</span>
		     ${params['order_no']}
		 </li>
 		 <li style="width:310px;" id="create_time"><span style="width:70px;">创建时间：</span>
		   <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>    	
		 <li style="width:650px;" id="error_msg"><span style="width:80px;">错误信息：</span>
		     ${params['error_msg']}
		 </li>	
    </ul>
		  <div class="clear"></div>
</div>
</body>
</html>