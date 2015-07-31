<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
		function showDetail(id)
		{
			 // $.dialog({title:'商户详情',width: 720,height:530,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
			window.location.href='transferAccountsDetail?id='+id;
		}
		
		function exportExcel2(){
		   var action= $("form:first").attr("action"),
		   	   totalPage = parseInt('${list.totalPages}');
		   	   
		   //根据当前页查询的总页数来判断是否导出，必须先进行查询
		   if(totalPage <= 0){
		       $.dialog.alert("<pre>没有需要导出的数据！</pre>");
		   } else if(totalPage > 100){
		   	   $.dialog.alert("<pre>请选择一些必要的查询条件并进行查询，避免因导出数据过多导致的系统异常！</pre>");
		   } else {
		   	   $("form:first").attr("action","${ctx}/mer/transferAccountsExport").submit();
		   	   $("form:first").attr("action",action);
		   } 
		}
		
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>转账查询
		</div>

		<form:form id="merQuery" action="${ctx}/mer/transferAccountsQuery" method="post">
			<div id="search">
				<div id="title">转账查询</div>
				<ul>
					<li>	<span>转账人卡号：</span><input type="text" id="payfor_account_no"  maxlength="24" name="payfor_account_no" value="${params.payfor_account_no }" style="width: 256px"/></li>
					<li><span>转账状态：</span>
						<select style="padding: 2px; width: 128px" id="order_status" name="order_status">
								<option value="" <c:out value="${params.order_status eq ''?'selected':'' }"/>>全部</option>
								<option value="SUCCESS" <c:out value="${params.order_status eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
								<option value="FAILED" <c:out value="${params.order_status eq 'FAILED'?'selected':'' }"/>>已失败</option>
								<option value="INIT" <c:out value="${params.order_status eq 'INIT'?'selected':'' }"/>>初始化</option>
								<option value="SENDORDER" <c:out value="${params.order_status eq 'SENDORDER'?'selected':'' }"/>>已下单</option>
								<%--<option value="PARTSUCCESS" <c:out value="${params.order_status eq 'PARTSUCCESS'?'selected':'' }"/>>部分成功</option>
							--%></select>
					</li>
					<li><span>商户编号：</span><input type="text" id="merchant_no"  style="width: 145px" name="merchant_no" value="${params.merchant_no }"  maxlength="20"/></li>
					<li><span>创建时间：</span> 
						<input type="text" style="width:120px" readonly="readonly" name="start_time"  id="start_time"  	value="${params['start_time']}"	onClick="WdatePicker({dateFmt:'yyyy-MM-dd 00:00:00',isShowClear:false,readOnly:true,maxDate:'%y-%M-%d 00:00:00'})"> 
						~ 
						<input 	type="text" style="width:120px" readonly="readonly" name="end_time" id="end_time" value="${params['end_time']}"	onClick="WdatePicker({dateFmt:'yyyy-MM-dd 23:59:59',isShowClear:false,readOnly:true,maxDate:'%y-%M-%d 23:59:59'})">
					</li>
					<li><span style="width: 112px">代理商编号/名称：</span><input type="text" id="agent_non" name="agent_non" value="${params.agent_non }"  style="width: 220px" maxlength="50" /></li>
				</ul>

				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton" value="查询" /> 
				<input name="reset" class="button blue medium" type="reset" id="reset" value="清空" />
				<shiro:hasPermission name="TRANS_ACCOUNT_EXPORT">
					<input id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()" value="导出excel"/>
				</shiro:hasPermission>
			</div>
		</form:form>
		<a name="_table"></a>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
				</thead>
				<tr>
					<th width="5%">序号</th>
					<th width="11%">转账人卡号</th>
					<th width="11%">收款人卡号</th>
					<th width="6%">代理商编号</th>
           			<th width="10%">交易商户号</th>
					<th width="5%">转账金额</th>
					<th width="5%">交易状态</th>
					<th width="120">创建时间</th>
					<th width="120">操作</th>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td><u:cardcut content="${item.payfor_account_no}" /></td>
							<td><u:cardcut content="${item.payee_account_no}" /></td>
							<td><u:cardcut content="${item.agent_no}" /></td>
							<td>${item.merchant_no}</td>
							<td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.amount}" /></td>
				            <td>
							 	<c:choose>
								 	  <c:when test="${item.order_status eq 'SUCCESS'}">已成功</c:when>
									  <c:when test="${item.order_status eq 'FAILED'}"><span class="font_red">已失败</span></c:when> 
									  <c:when test="${item.order_status eq 'INIT'}"><span class="font_red">初始化</span></c:when> 
									  <c:when test="${item.order_status eq 'SENDORDER'}">已下单</c:when>
									  <c:when test="${item.order_status eq 'PARTSUCCESS'}">部分成功</c:when>
									  <c:otherwise>${item.order_status}</c:otherwise>
								</c:choose> 
				            </td>
							<td><fmt:formatDate value="${item.create_time}" type="both" /></td>
							<td class="center"><shiro:hasPermission
									name="MOBILE_QUERY_DETAIL">
									<a href="javascript:showDetail(${item.id});">详情</a>
								</shiro:hasPermission></td>

						</tr>
					</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}"
				current="${list.number + 1}" anchor="_table"/>
		</div>
	</div>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
