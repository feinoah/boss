<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadJs.jsp"%>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
#merUpdate {
	padding: 10px;
}

#merUpdate ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 250px;
	height: 32px;
	line-height: 32px;
}

#merUpdate ul li.column2 {
	width: 500px;
}

#merUpdate ul li.column3 {
	width: 750px;
}

#merUpdate ul li select {
	width: 128px;
}

#merUpdate ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#merUpdate ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#merUpdate ul li label.longLabel {
	width: 170px;
}

#merUpdate ul li .area {
	width: 75px;
}

#merUpdate ul li.long {
	width: 440px;
}

#merUpdate div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#attachment_fileUploader {
	vertical-align: middle;
	margin-left: 10px;
}
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
</head>
<body>
	<div id="content">

		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：手机充值>充值详情
		</div>
		<form:form id="merUpdate">
			<div class="item">
				<div class="title">基本信息</div>
				<ul>
					<li style="">订单号：${params.journal}</li>
					<li><label>商户号：</label>${params.merchant_no}</li>
					<li><label>手机号：</label>${params.mobile}</li>
					<li><label>充值面额：</label><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params.price}" />元</li>
					<li><label>支付金额：</label><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params.amount}" />元</li>
					<li><label>支付状态：</label>
               			 <c:choose>
							  <c:when test="${params['trans_status'] eq 'SUCCESS'}">成功</c:when>
							  <c:when test="${params['trans_status'] eq 'FAILED'}">失败</c:when> 
							  <c:when test="${params['trans_status'] eq 'INIT'}">初始化</c:when> 
							  <c:otherwise>${params['trans_status']}</c:otherwise>
						</c:choose> 
         			 </li>
         			<li><label>充值状态：</label>
         				<c:choose>
							  <c:when test="${params['status'] eq 'SUCCESS'}">成功</c:when>
							  <c:when test="${params['status'] eq 'FAILED'}">失败</c:when> 
							  <c:when test="${params['status'] eq 'INIT'}">初始化</c:when> 
							  <c:when test="${params['status'] eq 'SENDORDER'}">已下单</c:when>
							  <c:when test="${params['status'] eq 'PARTSUCCESS'}">部分成功</c:when>
							  <c:otherwise>${params['status']}</c:otherwise>
						</c:choose> 
         			</li>
					<li><label>创建时间：</label>${params.create_time}</li>
					<li></li>
				</ul>


				<div class="search_btn clear">
					<input class="button blue" type="button" id="backButton" value="返回"
						onclick="history.go(-1)" />
				</div>
			</div>
		</form:form>

	</div>
</body>






























