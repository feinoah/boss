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
	width: 350px;
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

#merUpdate ul li div {
	display: -moz-inline-box;
	display: inline-block;
	width: 230px;
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
<script type="text/javascript">
$(function() {
	var accountFlag = false;
	var flag = false;
	var payfor_account_no = "${params.payfor_account_no}";//转账卡号
	var payee_account_no = "${params.payee_account_no}";//收款卡号
	var agent_name = "${params.agent_name}"; //代理商名称
	var agent_no = "${params.agent_no}"; //代理商编号
	<shiro:hasPermission name="TRANS_DETAIL_ACCOUNT_NO">
	accountFlag = true;
	</shiro:hasPermission>
	<shiro:hasPermission name="TRANS_ACCOUNT_DETAIL_AGENT">
	flag = true;
	</shiro:hasPermission>
	
	if(!accountFlag){
		payfor_account_no = "<u:cardcut content="${params['payfor_account_no']}" />";
		payee_account_no = "<u:cardcut content="${params['payee_account_no']}" />";
	}
	
	if(!flag){
		agent_name = "******";
		agent_no = "******";
	}
	$("#payfor_account_no").html(payfor_account_no);
	$("#payee_account_no").html(payee_account_no);
	$("#agent_name").html(agent_name);
	$("#agent_no").html(agent_no);
});
</script>
</head>
<body>
	<div id="content">

		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：转账查询>转账详情
		</div>
		<form:form id="merUpdate">
			<div class="item">
				<div class="title">基本信息</div>
				<ul>
					<li>订单号：${params.order_no}</li>
					<li><label>交易商户号：</label>${params.merchant_no}</li>
					<li><label>转账金额：</label><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params.amount}" />元</li>
					<li><label >代理商编号：</label><div id="agent_no" > </div></li>
					<li><label>代理商名称：</label><div id="agent_name" > </div></li>
					<li><label >转账卡号：</label><div id="payfor_account_no" > </div></li>
					<li><label>转账所属银行：</label>${params.forBank}</li>
					<li><label >转账人身份证：</label>${params.id_card_no}</li>
					<li><label >收款卡号：</label><div id="payee_account_no"> </div></li>
					<li><label >收款所属银行：</label>${params.payeeBank}</li>
					<li><label >转账手续费：</label>${params.fee}</li>
					<li><label >转账人姓名：</label>${params.payfor_account_name}</li>
					<li><label >收款人姓名：</label>${params.payee_account_name}</li>
         			<li><label>转账状态：</label>
         				<c:choose>
							  <c:when test="${params['order_status'] eq 'SUCCESS'}">成功</c:when>
							  <c:when test="${params['order_status'] eq 'FAILED'}">失败</c:when> 
							  <c:when test="${params['order_status'] eq 'INIT'}">初始化</c:when> 
							  <c:when test="${params['order_status'] eq 'SENDORDER'}">已下单</c:when>
							  <c:when test="${params['order_status'] eq 'PARTSUCCESS'}">部分成功</c:when>
							  <c:otherwise>${params['order_status']}</c:otherwise>
						</c:choose> 
         			</li>
					<li><label>创建时间：</label><fmt:formatDate value="${params.create_time}" type="both" /></li>
					<li></li>
				</ul>
				<div class="clear"></div>
				<div class="search_btn clear">
					<input class="button blue" type="button" id="backButton" value="返回"
						onclick="history.go(-1)" />
				</div>
			</div>
		</form:form>
	</div>
</body>






























