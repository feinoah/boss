<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="${ctx}/thems/blue.css" />
		<link rel="stylesheet" type="text/css" href="${ctx}/thems/button.css" />
	</head>
	<body>
		<div class="item liHeight">
			<div class="title">
				限额修改信息
			</div>
			<ul>
				<li style="width: 650px;">
					<span>代理商编号：</span><span style="width:570px;">${params['agent_no']}</span>
				</li>
				<li style="width: 650px;height:auto!important;height:20px;min-height:20px;">
					<span>商户编号：</span><span style="width:570px;word-break:break-all;word-wrap:break-word;">${params['merchant_no']}</span>
				</li>
				<li style="width: 650px;">
					<span>操作人：</span>${params['oper_name']}
				</li>
				<li style="width: 650px;">
					<span>创建时间：</span>
					<fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both" />
				</li>
				<li style="width: 650px;height:auto!important;height:20px;min-height:20px;">
					<span>备注：</span><span style="width:570px;word-break:break-all;word-wrap:break-word;">${params['remark']}</span>
				</li>
			</ul>
			<div class="clear"></div>
			<div class="title">
				商户交易规则信息
			</div>
			<ul>
				<li style="width: 650px;">
					<span style="width: 170px;">单日终端最大交易额：</span>${params['ed_max_amount']}(元)
				</li>
				<li style="width: 650px;"">
					<span style="width: 170px;">终端单笔最大交易额：</span>${params['single_max_amount']}(元)
				</li>
				<li style="width: 650px;">
					<span style="width: 170px;">单日单卡最大交易额：</span>${params['ed_card_max_amount']}(元)
				</li>
				<li style="width: 650px;">
					<span style="width: 170px;">单日终端单卡最大交易笔数：</span>${params['ed_card_max_items']}(笔)
				</li>
			</ul>
			<div class="clear"></div>

		</div>

	</body>
</html>