<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
		function showDetail(id)
		{
			 // $.dialog({title:'商户详情',width: 720,height:530,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
			window.location.href='creditTransDetail?id='+id;
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
		   	   $("form:first").attr("action","${ctx}/mer/incrementExport/credit").submit();
		   	   $("form:first").attr("action",action);
		   } 
		}
		
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>信用卡还款
		</div>

		<form:form id="merQuery" action="${ctx}/mer/creditTrans" method="post">
			<div id="search">
				<div id="title">信用卡还款查询</div>
				<ul>
					<li><span>信用卡卡号：</span><input type="text" id="credit_no"
						name="credit_no" value="${params.credit_no }" /></li>
						
					<li>
						<span>还款状态：${params.status}</span>
						<select style="padding: 2px; width: 128px" id="creditStatus" name="creditStatus">
							<option value="" <c:out value="${params.creditStatus eq ''?'selected':'' }"/>>全部</option>
							<option value="SUCCESS" <c:out value="${params.creditStatus eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
							<option value="FAILED" <c:out value="${params.creditStatus eq 'FAILED'?'selected':'' }"/>>已失败</option>
							<option value="INIT" <c:out value="${params.creditStatus eq 'INIT'?'selected':'' }"/>>初始化</option>
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
					<th width="40">序号</th>
					<th width="135">信用卡卡号</th>
					<th width="90">还款金额</th>
					<th width="120">商户号</th>
					<th width="80">状态</th>
					<th width="115">创建时间</th>
					<th width="60">操作</th>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td><u:cardcut content="${item.credit_no}"/></td>
							<td  align="right"><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.amount}" />元</td>
							<td>${item.merchant_no}</td>
							<td><c:choose>
							 	  <c:when test="${item.status eq 'SUCCESS'}">已成功</c:when>
								  <c:when test="${item.status eq 'FAILED'}"><span class="font_red">已失败</span></c:when> 
								  <c:when test="${item.status eq 'INIT'}"><span class="font_red">初始化</span></c:when> 
								  <c:otherwise>${item.status}</c:otherwise>
								</c:choose> 
							</td>
							<td><fmt:formatDate value="${item.create_time}" type="both" /></td>
							<td class="center"><shiro:hasPermission
									name="CREDIT_QUERY_DETAIL">
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
