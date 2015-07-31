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
	<div class="title clear">通道详情 </div>
	<ul>
		  <li style="width:310px;" id="mobile_no"><span style="width:60px;">代付通道：</span>
			 ${channelMap.channel_name}
		 </li>
		  <li style="width:310px;" id="status"><span style="width:70px;">通道状态：</span>
		  	<c:choose>
		          <c:when test="${channelMap.channel_state eq 0}">关闭</c:when>
		          <c:when test="${channelMap.channel_state eq 1}">正常</c:when>
		    </c:choose>
		 </li>
		  <li style="width:310px;" id="real_name"><span style="width:70px;">通道费率：</span>
		 	  ${channelMap.channel_fee}%
		 </li>
		 <li style="width:310px;" id="balance"><span style="width:70px;">剩余额度：</span>	
		 	 ${channelMap.remain_amount}
		 </li>
		 <li style="width:310px;" id="balance"><span style="width:70px;">预警额度：</span>	
		 	 ${channelMap.warn_amount}
		 </li>		
		 <%-- <li style="width:310px;" id="status"><span style="width:70px;">自审状态：</span>
		  	<c:choose>
		          <c:when test="${channelMap.auto_check_state eq 0}">关闭</c:when>
		          <c:when test="${channelMap.auto_check_state eq 1}">开启</c:when>
		    </c:choose>
		 </li> --%>	
    </ul>
    <div class="title clear">最近操作</div>
   	<ul>	
		<li style="width:150px;" id="handStatusDesc">时间</li> 
		<li style="width:150px;" id="handStatusDesc">操作人</li> 
		<li style="width:300px;" id="handStatusDesc">操作内容</li> 
    </ul>
 	<c:forEach items="${logList}" var="item" varStatus="status">
		<ul>	
		    <li style="width:150px;" id="handStatusDesc">${item.create_time}</li> 
			<li style="width:150px;" id="handStatusDesc">${item.operater}</li> 
			<li style="width:300px;" id="handStatusDesc">${item.content}</li> 
      	</ul>
	</c:forEach>
	<div class="clear"></div>
</div>
</body>
</html>