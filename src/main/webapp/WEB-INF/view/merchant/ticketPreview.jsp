<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@page import="com.eeepay.boss.utils.SysConfig"%>
<head>
<%
	String sysconfig1 = (String)SysConfig.value("special_agent_id");
	String sysconfig2 = (String)SysConfig.value("special_agent_zk");
 %>
  <c:set value="<%=sysconfig1 %>" var="speagent"></c:set>
 <c:set value="<%=sysconfig2 %>" var="specialAgentZk"></c:set>
 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta name="format-detection" content="telephone=no" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<title>小票预览</title>
<style type="text/css">

* {
margin:0px;
padding:0px;
}

body {
font-size:20px;
font-family: Arial, Helvetica, sans-serif;
}

img {
border:none;
}


#main {
width:100%;
height:100%;
}

#main .info {
width:95%;
margin:0px auto;
background-color:#FFFFFF;
background-repeat:no-repeat;
background-position:bottom;
padding-bottom: 20px;
}

#main .info p {
height:30px;
line-height:30px;
width: 95%;
font-size:14px;
margin: 0 auto;
}

#main .info p span.left {
float:left;
}

#main .info p span.right {
float:right;
}

.double_line{
	border-top:1px dashed #000;
	border-bottom:1px dashed #000;
	height: 5px!important;
}
.clear_fix{
	clear:both;
}
.sign {
	width: 95%;
	text-align: center;
	margin: 0 auto;
}

.sign img {
	width: 100%;
}
.download_btn{
	width:95%;
	background-color:#fff;
	margin: 30px auto 30px;
	font-size:14px;
	height: 25px;
	line-height: 25px;
}
.android,.ios{
	width: 49%;
	float: left;
	text-align: center;
	padding:5px 0;
}
.android{
	border:1px solid #ccc;
	border-radius:15px 0 0 15px;
}
.ios{
	border-width:1px 1px 1px 0;
	border-style:solid;
	border-color:#ccc;
	border-radius:0 15px 15px 0;
}
.active{
	background-color:#ccc;
	color:#fff;
}
</style>
<script>
	$(function(){
		var a=document.createElement("a");
		a.target="_blank";
		a.style.display="none";
		$(".android,.ios").on("touchstart",function(){
			$(this).addClass("active");
		}).on("touchend",function(){
			var url=$(this).removeClass("active").attr("data-url");
			var ev = document.createEvent('HTMLEvents');
			a.href=url;
			ev.initEvent('click', false, true); 
			a.dispatchEvent(ev); 
		});
		
	});
</script>
</head>

<body>

<div id="main">
		<c:set var="date1"><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd" type="date"/></c:set>
		<c:set var="date2">2014-03-05</c:set>
		<div class="info">
				<p align="center"><span><font style="font-size: 16px;font-weight: bold;">POS签购单</font></span></p>
				<p class="double_line"></p>
				<c:choose>
				   <c:when test="${params['agent_no'] eq '6397'}">
				      <p><span class="left">商户名称：</span><span class="right">${params['merchant_name']}</span></p>
				   </c:when>
				   <c:otherwise>
				     <p><span class="left">商户名称：</span><span class="right">${params['acq_merchant_name']}</span></p>
				   </c:otherwise>
				</c:choose>
				<p><span class="left">商户编号：</span><span class="right">${params['merchant_no']}</span></p>
				<p><span class="left">终端编号：</span><span class="right">${params['terminal_no']}</span></p>
				<p><span class="left">卡号：</span><span class="right"><u:cardcut   content="${params['account_no']}"/></span></p>
				<p><span class="left">交易类型：</span><span class="right">
				<c:choose>
					<c:when test="${params['trans_type'] eq 'PURCHASE'}">消费</c:when>
					<c:when test="${params['trans_type'] eq 'PURCHASE_VOID'}">
						<span class="font_red">消费撤销</span>
					</c:when>
					<c:when test="${params['trans_type'] eq 'PURCHASE_REFUND'}">
						<span class="font_red">退货</span>
					</c:when>
					<c:when test="${params['trans_type'] eq 'REVERSED'}">
						<span class="font_red">冲正</span>
					</c:when>
					<c:when test="${params['trans_type'] eq 'BALANCE_QUERY'}">
						<span class="font_gray">余额查询</span>
					</c:when>
					<c:otherwise>${params['trans_type'] }</c:otherwise>
				</c:choose></span></p>
				<p><span class="left">凭证号：</span><span class="right">${params['acq_reference_no']}</span></p>
				<p><span class="left">订单号：</span><span class="right">${params['trans_id']}</span></p>
				<p><span class="left">交易时间：</span><span class="right"><fmt:formatDate value="${params['trans_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></span></p>
				<p><span class="left">交易金额：</span><span class="right">RMB
				<c:choose>
					<c:when test="${params['trans_amount'] == null}">***</c:when>		
					<c:otherwise>
						${params['trans_amount']}
					</c:otherwise>
				</c:choose></span></p>
				<p><span class="left">备注：</span></p>
				<p><span class="left">持卡人签名</span></p>
				<div class="sign">
						<img alt="签名" src="${signUrl}" />
				</div>
				<c:choose>
					<c:when test="${params['agentId']==specialAgentZk || params['agentParentId']==specialAgentZk}">
						
					</c:when>
					<c:when test="${params['agentId']==speagent || params['agentParentId']==speagent}">
						
					</c:when>
					<c:otherwise>
<!-- 						<div class="download_btn">
							<div class="android" data-url="http://empos.posp.cn:5780/BPayBoxSmallBox.apk">安卓移小宝下载</div>
							<div class="ios" data-url="https://itunes.apple.com/us/app/yi-xiao-bao/id726932051?ls=1&mt=8">苹果移小宝下载</div>
							<div class="clear_fix"></div>
						</div> -->
					</c:otherwise>
				</c:choose>
			
		</div>
</div>
</body>
</html>
