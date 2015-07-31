<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
		function showDetail(id)
		{
			window.location.href='wzfkPayDetail?id='+id;
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
		   	   $("form:first").attr("action","${ctx}/mer/incrementExport/traffic").submit();
		   	   $("form:first").attr("action",action);
		   } 
		}

	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>车辆代缴费
		</div>

		<form:form id="merQuery" action="${ctx}/mer/wzfktrans" method="post">
			<div id="search">
				<div id="title">车辆代缴费查询</div>
				<ul>
					<li><span>订单号：</span><input type="text" id="order_no"
						name="order_no" value="${params.order_no }" /></li>
					<li>
					<li><span>车牌号：</span><input type="text" id="carnumber"
						name="carnumber" value="${params.carnumber }" /></li>
					<li>
					<%-- <li><span>商户名称/编号：</span><input type="text" id="merchant" name="merchant_no" value="${params.merchant }" /></li>--%>
						
					<li>
						<span>代缴状态：</span>
						<select style="padding: 2px; width: 128px" id="status" name="status">
							<option value="" <c:out value="${params.status eq ''?'selected':'' }"/>>全部</option>
							<option value="SUCCESS" <c:out value="${params.status eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
							<option value="FAILED" <c:out value="${params.status eq 'FAILED'?'selected':'' }"/>>已失败</option>
							<option value="INIT" <c:out value="${params.status eq 'INIT'?'selected':'' }"/>>初始化</option>
							<option value="SENDORDER" <c:out value="${params.status eq 'SENDORDER'?'selected':'' }"/>>已下单</option>
						</select>
					</li>

					<li><span>创建时间：</span> <input type="text" style="width:102px"
						readonly="readonly" name="createTimeBegin"
						value="${params['createTimeBegin']}"
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})"> ~ <input
						type="text" style="width:102px" readonly="readonly"
						name="createTimeEnd" value="${params['createTimeEnd']}"
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})"></li>
				</ul>

				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton" value="查询" /> 
				<input name="reset" class="button blue medium" type="reset" id="reset" value="清空" />
				<input id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()" value="导出excel"/>
			</div>
		</form:form>
		<a name="_table"></a>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
				</thead>
				<tr>
					<th width="5%">序号</th>
					<th width="150">订单号</th>
					<th width="80">车牌号号</th>
           			<th width="150">商户号</th>
					<th width="80">金额</th>
					<th width="80">状态</th>
					<th width="120">创建时间</th>
					<th width="60">操作</th>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td>${item.order_no}</td>
							<td>${item.carnumber}</td>
							<td>${item.merchant_no}</td>
							<td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.amount}" /></td>
				            <td>
							 	<c:choose>
								 	  <c:when test="${item.status eq 'SUCCESS'}">已成功</c:when>
									  <c:when test="${item.status eq 'FAILED'}"><span class="font_red">已失败</span></c:when> 
									  <c:when test="${item.status eq 'INIT'}"><span class="font_red">初始化</span></c:when> 
									  <c:when test="${item.status eq 'SENDORDER'}">已下单</c:when>
									  <c:otherwise>${item.status}</c:otherwise>
								</c:choose> 
				            </td>
							<td><fmt:formatDate value="${item.create_time}" type="both" /></td>
							<td class="center"><shiro:hasPermission
									name="WZFK_QUERY_DETAIL">
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
