<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户手续费列表</title>
<style>
	.t2 tr td{
		overflow: hidden;
		text-overflow:ellipsis;
	}
</style>
<script type="text/javascript">
function exportExcel2(){
	
	//var action = $("form:first").attr("action");
	$("form:first").attr("action", "${ctx}/mer/exportExcelMerchantHandlingChargeLost").submit();
	//$("form:first").attr("action", action);
	
	//location.href="${ctx}/mer/exportExcelMerchantHandlingChargeLost";
}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理&gt;亏损交易
		</div>
	  <form:form action="${ctx}/mer/merchantHandlingCharge" method="post">
		<div id="search">
		<div id="title">亏损交易查询</div>
			<ul>
				<li>
					<span>交易时间：</span>
					<%-- <input onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" style="width: 123px"
						name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
					~
					<input onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" style="width: 123px"
						name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})"> --%>
					<input onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})" type="text" style="width: 102px"
							name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
						~
				    <input onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})" type="text" style="width: 102px"
							name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
				</li>
		   </ul>
		   <div class="clear"></div>
		</div>
		<div class="search_btn">
		        <input class="button blue medium" type="submit" id="submitButton" value="查询" />
		        <input name="reset" class="button blue medium" type="reset" id="reset" value="清空" />
				<input id="exportExcel" title="导出excel查看详情" class="button blue medium" type="button" onclick="exportExcel2();" value="导出excel" />
		</div>
	 </form:form>
		<div>
			<p style="text-align: right;padding-right:7px;">金额单位：元</p>
		</div>
		<div class="tbdata">
			<a name="_table"></a>
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="16">普通商户名称</th>
						<th width="13">费率</th>
						<th width="8">普通商户费用</th>
						<th width="22">收单机构商户名称</th>
						<th width="15">费率</th>
						<th width="8">收单商户费用</th>
						<th width="8">亏损金额</th>
						<th width="9">交易时间</th>
					</tr>
					<c:if test="${list.totalElements==0}">
						<tr>
							<td colspan="7" style="text-align:center;">没有亏损数据</td>
						</tr>
					</c:if>
					<c:forEach items="${list.content}" var="item">
						<tr>
							<td title="${item.merchant_name}">${item.merchant_name}</td>
							<td title="${item.merchant_rate}">${item.merchant_rate}</td>
							<td title="${item.merchant_fee}">${item.merchant_fee}</td>
							<td title="${item.acq_merchant_name}">${item.acq_merchant_name}</td>
							<td title="${item.acq_merchant_rate}">${item.acq_merchant_rate}</td>
							<td title="${item.acq_merchant_fee}">${item.acq_merchant_fee}</td>
							<td>${item.acq_merchant_fee-item.merchant_fee}</td>
							<td>${item.trans_time}</td>
						</tr>
					</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table" />
		</div>
	</div>
</body>
