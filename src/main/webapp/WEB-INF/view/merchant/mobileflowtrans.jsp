<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
		function showDetail(id)
		{
			window.location.href='flowRechargeDetail?id='+id;
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
		   	   $("form:first").attr("action","${ctx}/mer/incrementExport/dataflow").submit();
		   	   $("form:first").attr("action",action);
		   } 
		}
		
		$(function(){
			$('#reset').on('click', function(){
				$(':input','#merQuery')
					.not(':button, :submit, :reset, :hidden').val('') 
					.removeAttr('checked')   
					.removeAttr('selected');
			});
		});
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>手机流量充值
		</div>

		<form:form id="merQuery" action="${ctx}/mer/mobileflowtrans" method="post">
			<div id="search">
				<div id="title">手机流量充值查询</div>
				<ul>
					<li><span>手机号：</span><input type="text" id="mobile"
						name="mobile" value="${params.mobile }" /></li>
					<li>
					<li><span>商户编号：</span><input type="text" id="merchant_no"
						name="merchant_no" value="${params.merchant_no }" /></li>
					<li>
						
					<li>
						<span>充值状态：</span>
						<select style="padding: 2px; width: 128px" id="mobileStatus" name="mobileStatus">
							<option value="" <c:out value="${params.mobileStatus eq ''?'selected':'' }"/>>全部</option>
							<option value="SUCCESS" <c:out value="${params.mobileStatus eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
							<option value="FAILED" <c:out value="${params.mobileStatus eq 'FAILED'?'selected':'' }"/>>已失败</option>
							<option value="INIT" <c:out value="${params.mobileStatus eq 'INIT'?'selected':'' }"/>>初始化</option>
							<option value="SENDORDER" <c:out value="${params.mobileStatus eq 'SENDORDER'?'selected':'' }"/>>已下单</option>
							<option value="PARTSUCCESS" <c:out value="${params.mobileStatus eq 'PARTSUCCESS'?'selected':'' }"/>>部分成功</option>
						</select>
					</li>
					<li><span>充值时间：</span> <input type="text" style="width:102px"
						readonly="readonly" name="createTimeBegin"
						value="${params['createTimeBegin']}"
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})"> ~ <input
						type="text" style="width:102px" readonly="readonly"
						name="createTimeEnd" value="${params['createTimeEnd']}"
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})"></li>
						
					<li>
						<span>流量：</span>
						<select style="padding: 2px; width: 128px" id="amount" name="amount">
							<option value="" <c:out value="${params.amount eq ''?'selected':'' }"/>>全部</option>
							<option value="10" <c:out value="${params.amount eq '10'?'selected':'' }"/>>10M</option>
							<option value="30" <c:out value="${params.amount eq '30'?'selected':'' }"/>>30M</option>
							<option value="50" <c:out value="${params.amount eq '50'?'selected':'' }"/>>50M</option>
							<option value="70" <c:out value="${params.amount eq '70'?'selected':'' }"/>>70M</option>
							<option value="150" <c:out value="${params.amount eq '150'?'selected':'' }"/>>150M</option>
							<option value="200" <c:out value="${params.amount eq '200'?'selected':'' }"/>>200M</option>
							<option value="300" <c:out value="${params.amount eq '300'?'selected':'' }"/>>300M</option>
						</select>
					</li>
				</ul>

				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton" value="查询" /> 
				<input name="reset" class="button blue medium" type="button" id="reset" value="清空" />
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
					<th width="150">手机号</th>
					<th width="80" class="center">流量</th>
					<th width="80">金额</th>
					<th width="80">状态</th>
           			<th width="150">商户号</th>
					<th width="150">创建时间</th>
					<th width="80">操作</th>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td>${item.mobile}</td>
							<td>${item.amount}M</td>
							<td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.price}" /></td>
				            <td class="center">
							 	<c:choose>
								 	  <c:when test="${item.status eq 'SUCCESS'}">已成功</c:when>
									  <c:when test="${item.status eq 'FAILED'}"><span class="font_red">已失败</span></c:when> 
									  <c:when test="${item.status eq 'INIT'}"><span class="font_red">初始化</span></c:when> 
									  <c:when test="${item.status eq 'SENDORDER'}">已下单</c:when>
									  <c:when test="${item.status eq 'PARTSUCCESS'}">部分成功</c:when>
									  <c:otherwise>${item.status}</c:otherwise>
								</c:choose> 
				            </td>
							<td>${item.merchant_no}</td>
							<td class="center"><fmt:formatDate value="${item.create_time}" type="both" /></td>
							<td class="center">
								<shiro:hasPermission name="MOBILE_QUERY_DETAIL">
									<a href="javascript:showDetail(${item.id});">详情</a>
								</shiro:hasPermission>
							</td>
						</tr>
					</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
		</div>
	</div>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
