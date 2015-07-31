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
	<div class="title clear">交易信息 </div>
	<ul>
		<li style="width:220px;"><span>订单编号：</span>${params['order_no']}</li>
		<li style="width:200px;"><span>交易类型：</span>${params['biz_name']}</li>
		<li style="width:200px;"><span>交易金额：</span>${params['amount']}</li>
		<li style="width:220px;"><span>收单机构：</span>
			<c:choose>
				  <c:when test="${params['acq_enname'] eq 'eptok'}">YS</c:when>
				  <c:when test="${params['acq_enname'] eq 'tftpay'}">腾付通</c:when> 
				  <c:when test="${params['acq_enname'] eq 'bill'}">快钱</c:when> 
				  <c:when test="${params['acq_enname'] eq 'zypay'}">中意支付</c:when> 
				  <c:when test="${params['acq_enname'] eq 'yibao'}">易宝</c:when>
				  <c:when test="${params['acq_enname'] eq 'xlink'}">讯联</c:when>
				  <c:when test="${params['acq_enname'] eq 'hypay'}">翰亿</c:when>
				  <c:when test="${params['acq_enname'] eq 'ubs'}">瑞银信</c:when>
				  <c:otherwise>${params['acq_enname']}</c:otherwise>
			</c:choose> 
		</li>
		<li style="width:200px;"><span>交易卡号：</span>
			<shiro:hasPermission name="TRANS_CARD_NO">${params['card_no']}</shiro:hasPermission>
			<shiro:lacksPermission name="TRANS_CARD_NO"><u:cardcut content="${params['card_no']}" /></shiro:lacksPermission>
		</li>
		<li style="width:200px;"><span>卡类型：</span>${params['card_type']}</li>
		<li style="width:220px;"><span>发卡行：</span>${params['bank_name']}</li>
		<li style="width:250px;"><span>卡种：</span>${params['card_name']}</li>
		<li style="width:220px;"><span>创建时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
		<li style="width:400px;"><span>交易状态：</span>${params['status']}</li>
		<li style="width:220px;"><span>商户编号：</span>${params['merchant_no']}</li>
		<li style="width:200px;"><span>商户名称：</span>${params['merchant_name']}</li>
		
	</ul>
		<div class="clear"></div>
</div>
</body>
</html>