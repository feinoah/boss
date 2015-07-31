<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
		function showDetail(id)
		{
			window.location.href='${ctx}/increment/pubPayInfo?id='+id;
		}
	
		
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>公共缴费
		</div>

		<form:form id="merQuery" action="${ctx}/increment/pubPayList" method="post">
			<div id="search">
				<div id="title">公共缴费查询</div>
				<ul>
					<li><span>订单编号：</span><input type="text" id=order_no
						name="order_no" value="${params.order_no }" /></li>
					<li><span>商户编号：</span><input type="text" id="merchant_no"
						name="merchant_no" value="${params.merchant_no }" /></li>
					<li>
						<span>缴费类型：</span>
						<select style="padding: 2px; width: 128px" id="order_type" name="order_type">
							<option value="" <c:out value="${params.order_type eq ''?'selected':'' }"/>>全部</option>
							<option value="water" <c:out value="${params.order_type eq 'water'?'selected':'' }"/>>水费</option>
							<option value="elec" <c:out value="${params.order_type eq 'elec'?'selected':'' }"/>>电费</option>
							<option value="coal" <c:out value="${params.order_type eq 'coal'?'selected':'' }"/>>燃气费</option>
						</select>
					</li>
					<li>
						<span>缴费状态：</span>
						<select style="padding: 2px; width: 128px" id="status" name="status">
							<option value="" <c:out value="${params.status eq ''?'selected':'' }"/>>全部</option>
							<option value="SUCCESS" <c:out value="${params.status eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
							<option value="FAILED" <c:out value="${params.status eq 'FAILED'?'selected':'' }"/>>已失败</option>
							<option value="INIT" <c:out value="${params.status eq 'INIT'?'selected':'' }"/>>初始化</option>
						</select>
					</li>

					<li><span>缴费时间：</span> <input type="text" style="width:102px"
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
				<input class="button blue medium" type="submit" id="submit"
					value="查询" /> <input name="reset" class="button blue medium"
					type="reset" id="reset" value="清空" />
			</div>
		</form:form>
		<a name="_table"></a>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
				</thead>
				<tr>
					<th width="5%">序号</th>
					<th width="18%">订单编号</th>
					<th width="10%">缴费类型</th>
					<th width="80">金额</th>
					<th width="60">状态</th>
           			<th width="15%">商户号</th>
					<th width="120">创建时间</th>
					<th width="60">操作</th>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td>${item.order_no}</td>
							<td>
								<c:choose>
								 	  <c:when test="${item.order_type eq 'water'}">水费</c:when>
									  <c:when test="${item.order_type eq 'elec'}">电费</c:when> 
									  <c:when test="${item.order_type eq 'coal'}">燃气费</c:when> 
									  <c:otherwise>${item.order_type}</c:otherwise>
								</c:choose> 
							</td>
							<td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.pay_amount}" /></td>
				            <td>
							 	<c:choose>
								 	  <c:when test="${item.status eq 'SUCCESS'}">已成功</c:when>
									  <c:when test="${item.status eq 'FAILED'}"><span class="font_red">已失败</span></c:when> 
									  <c:when test="${item.status eq 'INIT'}"><span class="font_red">初始化</span></c:when> 
									  <c:otherwise>${item.status}</c:otherwise>
								</c:choose> 
				            </td>
							<td>${item.merchant_no}</td>
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
</body>
