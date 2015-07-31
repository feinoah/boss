<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>

<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>


<style type="text/css">

</style>
<script type="text/javascript">
$(function() {
		var flag = false;
		var cardNo = "${params['card_no']}";
		<shiro:hasPermission name="FAST_TRANS_CARD_NO">
		flag = true;
		</shiro:hasPermission>
		if(!flag){
			cardNo = "<u:cardcut content="${params['card_no']}" />";
		}
		$("#cardNo").html(cardNo);
});
</script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">订单详情 </div>
	<ul>
		<li style="width:220px;"><span>订单编号：</span>${params['order_no']}</li>
		<li style="width:200px;"><span>交易状态：</span>
		<c:if test="${params['status']=='已成功'}">已出票</c:if>
		<c:if test="${params['status']!='已成功'}">未出票</c:if>
		</li>
		<li style="width:200px;"><span>交易金额：</span>${params['amount']}</li>
		<li style="width:220px;"><span>支付卡号：</span> <span id="cardNo"></span></li>
		<li style="width:200px;"><span>发卡行：</span>${params['bank_name']}</li>
		<li style="width:200px;"><span>卡类型：</span>${params['card_type']}</li>
		<li style="width:220px;"><span>乘机人：</span>${params['order_user_name']}</li>
		<li style="width:200px;"><span>类型：</span>成人</li>
		<shiro:hasPermission name="SMS_MOBILE_VIEW">
			<li style="width:200px;"><span>交易手机号：</span>${params['sms_mobile']}</li>
		</shiro:hasPermission>
		<li style="width:460px;"><span>身份证：</span>${params['id_card']}</li>
		<li style="width:220px;"><span>订单时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
	</ul>

	<div class="clear"></div>
		<div class="title clear">航班信息 </div>
		<ul>	
			<li style="width:200px;">${params['city']}</li>
			<li style="width:220px;">${params['airTime']}</li>
			<li style="width:200px;">${params['airport']}</li>
			<li style="width:200px;">直飞</li>
			<li style="width:220px;">${params['plane']}</li>
			<li style="width:200px;">舱位：${params['seat']}</li>
			<li style="width:250px;">报销凭证配送：不需要报销凭证</li>
		</ul>
	</div>


</body>
</html>