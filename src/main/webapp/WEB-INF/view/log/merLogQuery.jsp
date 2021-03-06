<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
$(function(){
	
	$("#compareSubmit").click(function(){
		  var chk_value =[];    
		  $('input[name="ids"]:checked').each(function(i,n){
		   		chk_value.push($(this).val());    
		  }); 
		  if(chk_value.length==0){
			  $.dialog({title: '错误',lock:true,content: "请选择至少一条记录",icon: 'error.gif'
				});
			  	return false;
		  }else{
			  showDetail(chk_value);
		  }
  
	});
	
	
	
	
});

function showDetail(chk_value)
{
  	window.location.href="${ctx}/log/compareMerLogs?ids="+chk_value+"&layout=no";
}
		
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：日志管理>商户信息日志查询
		</div>

		<form:form id="merQuery" action="${ctx}/log/merLogQuery" method="post">
			<div id="search">
				<div id="title">商户信息日志查询</div>
				<ul>

				<li><span style="width: 90px;">操作人名称/ID：</span><input type="text"
					value="${params['operator']}" name="operator" /></li>
				<li><span style="width: 90px;">商户名称/编号：</span> <input type="text"
					value="${params['merchant']}" name="merchant" /></li>
				<li><span>操作类别：</span>
					<select name="opertype" style="padding:2px; width:128px">
						<option value="" >全部</option>
						<option value="1"  <c:if test="${params.opertype eq '1'}">selected="selected"</c:if> >修改</option>
						<option value="2"  <c:if test="${params.opertype eq '2'}">selected="selected"</c:if> >删除</option>
						<option value="3"  <c:if test="${params.opertype eq '3'}">selected="selected"</c:if> >冻结</option>
						<option value="4"  <c:if test="${params.opertype eq '4'}">selected="selected"</c:if> >解冻</option>
						<option value="5"  <c:if test="${params.opertype eq '5'}">selected="selected"</c:if> >关闭</option>
						<option value="6"  <c:if test="${params.opertype eq '6'}">selected="selected"</c:if> >打开</option>
	         	</select>
				</li>
				
				<li><span style="width: 90px;">操作时间：</span>
					<input  type="text"  style="width:102px" readonly="readonly" name="operTimeBegin" value="${params['operTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="operTimeEnd" value="${params['operTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
				</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn clear">
				<input class="button blue medium" type="submit" id="submit"
					value="查询" />
				<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
				<input class="button blue medium" type="button" id="compareSubmit"  value="日志对比" onclick="toCompare();return false;"/>
			</div>
		</form:form>
		<a name="_table"></a>
		<div id="tbdata" class="tbdata">
		<form:form id="compareForm" action="${ctx}/log/compareMerLogs" method="post">
			<table width="100%" cellspacing="0" class="t2">
				<thead>

					<tr>
						<th width="30">选择</th>
						<th width="30">序号</th>
						<th width="90">商户简称</th>
						<th width="90">商户编号</th>
						<th width="60">操作人ID</th>
						<th width="60">操作人姓名</th>
						<th width="60">操作类别</th>
						<th width="90">操作时间</th>
						
						
					</tr>
				</thead>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<c:out value=""></c:out>
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
						<td class="center">
							<input type="checkbox"  name="ids"  value="${item.id}"/>
						</td>
							<td class="center"><span class="center">${status.count}</span></td>
							<td>${item.merchant_short_name}</td>
							<td>${item.merchant_no}</td>
							<td>${item.oper_user_id}</td>
							<td>${item.oper_user_name}</td>
							<td>
								<c:if test="${item.oper_type eq '1' }">修改</c:if>					
 						 		<c:if test="${item.oper_type eq '2' }">删除</c:if>
 						 		<c:if test="${item.oper_type eq '3' }">冻结</c:if>					
 						 		<c:if test="${item.oper_type eq '4' }">解冻</c:if>
 						 		<c:if test="${item.oper_type eq '5' }">关闭</c:if>					
 						 		<c:if test="${item.oper_type eq '6' }">打开</c:if>
 						 	</td>
							<td><fmt:formatDate value="${item.operate_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
							
	           </td>
	        </tr>
					</c:forEach>
			</table>
			</form:form>
		</div>
		
		
		
		
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}"
				current="${list.number + 1}" anchor="_table"/>
		</div>
	</div>



</body>
