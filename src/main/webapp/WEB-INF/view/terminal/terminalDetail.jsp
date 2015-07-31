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
	<div class="title clear">机具信息 </div>
	<ul>
		  <li style="width:310px;" id="sn"><span>机器SN号：</span>${params['SN']}</li>
		  <li style="width:310px;" id="psam_no"><span>psam编号：</span>${params['PSAM_NO']}</li>
		  <li style="width:310px;" id="terminal_no"><span>终端号：</span>${params['TERMINAL_NO']}</li>
		  <li style="width:310px;" id="open_status"><span>机具状态：</span>
		  <c:choose>
				  <c:when test="${params['open_status'] eq '0'}">已入库</c:when>
				  <c:when test="${params['open_status'] eq '1'}">已分配</c:when> 
				  <c:when test="${params['open_status'] eq '2'}">已使用</c:when> 
				  <c:otherwise>${params['open_status']}</c:otherwise>
			</c:choose> 
		   </li>
  		  <li style="width:310px;" id="model"><span>机具型号：</span>
  		  <u:posmodel svalue="${params.model}" />
  		  <%--<c:choose>
  		      <c:when test="${params['model'] eq 'dot'}"> 点付宝</c:when> 
  		      <c:when test="${params['model'] eq 'YPOS08'}"> YPOS08</c:when> 
  		      <c:when test="${params['model'] eq 'YPOS09'}"> YPOS09</c:when> 
  		      <c:otherwise> ${params['model']}</c:otherwise>		      
  		  </c:choose>
  		 
  		  --%></li>
		  <li style="width:310px;" id="type"><span>机具类型：</span><u:postype svalue="${params.pos_type}" /></li>
		  <li style="width:310px;" id="start_time"><span>启用时间：</span>
		  <fmt:formatDate value="${params['START_TIME']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/> </li>
		  <li style="width:310px;"><span>入库时间：</span><fmt:formatDate value="${params['CREATE_TIME']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
    </ul>
		  <div class="clear"></div>


	<div class="title clear">代理商信息 </div>
	<ul>
		 <li style="width:310px;" id="merchant_no"><span>代理商编号：</span>${params['agent_no']}</li>
		  <li style="width:310px;" id="merchant_short_name"><span>代理商名称：</span>${params['agent_name']}</li>
    </ul>
		  <div class="clear"></div>
    
    <div class="title clear">商户信息 </div>
	 <ul>
		  <li style="width:310px;" id="merchant_no"><span>商户编号：</span>${params['MERCHANT_NO']}</li>
		  <li style="width:310px;" id="merchant_short_name"><span>商户简称：</span>${params['merchant_short_name']}</li>
	 </ul>
	 	 <div class="clear"></div>
</div>
</body>
</html>