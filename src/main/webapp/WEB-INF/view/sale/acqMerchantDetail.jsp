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
	<div class="title">收单机构商户信息 </div>
	<ul>
		  <li style="width:300px;" id=" "><span style="width:120px;">代理商编号：</span>${params['agent_no']}</li>
	
		  <li style="width:300px;" id=" "><span style="width:120px;">代理商名称：</span><u:substring length="10" content="${params['agent_name']}"/></li>
		  
		  <li style="width:300px;" id="acq_enname"><span style="width:120px;">收单机构：</span>
			    <c:if test="${params['acq_enname'] eq 'eptok' }">YS</c:if>
				<c:if test="${params['acq_enname'] eq 'tftpay'}">腾付通</c:if>
				<c:if test="${params['acq_enname'] eq 'bill'}">快钱</c:if>
				<c:if test="${params['acq_enname'] eq 'zypay'}">中意支付</c:if>
		  </li>
		  <li style="width:300px;" id="acq_merchant_no"><span style="width:120px;">收单机构商户编号：</span>${params['acq_merchant_no']}</li>
		  <li style="width:300px;" id="acq_code"><span style="width:120px;">收单机构商户名称：</span><u:substring length="10" content="${params['acq_merchant_name']}"/></li>
		  <li style="width:300px;" id="serial_no"><span style="width:120px;">行业代码：</span>${params['mcc']}</li>
		  <li style="width:300px;" id="account_no"><span style="width:120px;">签约费率</span>
			  <c:if test="${params['fee_type'] eq 'RATIO' }">${params['fee_rate']}~不封顶</c:if>
			  <c:if test="${params['fee_type'] eq 'CAPPING'}">${params['fee_rate']}~${params['fee_max_amount']}封顶</c:if>
			  <c:if test="${params['fee_type'] eq 'LADDER'}">${params['ladder_min']}%&lt;${params['ladder_value']}(元)&lt;${params['ladder_max']}%</c:if>
		  </li>
		  <li style="width:300px;"><span style="width:120px;">大商户扣率：</span>
		  	  <c:if test="${params['fee_type'] eq 'RATIO' }">${params['merchant_Drate']}%~不封顶</c:if>
			  <c:if test="${params['fee_type'] eq 'CAPPING'}">${params['merchant_Drate']}%~${params['merchant_max_amount']}封顶</c:if>
			  <c:if test="${params['fee_type'] eq 'LADDER'}">${params['merchant_min']}%&lt;${params['merchant_value']}(元)&lt;${params['merchant_max']}%</c:if>
		  </li>
		  <li style="width:300px;"><span style="width:120px;">创建时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
	</ul>
	<div class="clear"></div>
	
	<div class="title clear">实名商户信息</div>
	<ul>
		<li style="width:300px;"><span style="width:120px;">实名商户编号：</span>${params['merchant_no']}</li>
		<li style="width:300px;"><span style="width:120px;">实名商户名称：</span><u:substring length="10" content="${params['merchant_name']}"/></li>
	</ul>
	<div class="clear"></div>

</div>
</body>
</html>