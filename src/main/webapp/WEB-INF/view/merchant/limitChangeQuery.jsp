<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ ctx}/scripts/utils.js">
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：商户管理>额度修改查询
		</div>

		<form:form id="merCheck" action="${ctx}/mer/limitChangeQuery"
			method="post">
			<div id="search">
				<div id="title">
					额度修改查询
				</div>
				<ul>
					<li>
						<span>代理商编号：</span>
						<input type="text" value="${params['agent_no']}" name="agent_no" />
					</li>
					<li>
						<span>商户编号：</span>
						<input type="text" value="${params['merchant_no']}"
							name="merchant_no" />
					</li>
					<li>
						<span>创建时间：</span>
						<input type="text" style="width: 90px" name="create_time_begin"
							class="input" readonly="readonly"
							value="${params['create_time_begin']}"
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
						~
						<input type="text" style="width: 90px" name="create_time_end"
							class="input" readonly="readonly"
							value="${params['create_time_end']}"
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton"
					value="查询" />
				<input name="reset" class="button blue medium" type="reset"
					id="reset" value="清空" />
				<shiro:hasPermission name="COMMERCIAL_LIMITCHANGE_ADD">
				<input name="addNew" class="button blue medium" type="button"
					id="addNew" value="修改限额" />	
				</shiro:hasPermission>
			</div>
		</form:form>
		<a name="_table"></a>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<tr>
					<th width="5%">
						序号
					</th>
					<th width="25%">
						代理商编号
					</th>
					<th width="30%">
						商户编号
					</th>
					<th width="20%">
						操作时间
					</th>
					<th width="10%">
						操作人
					</th>
					<th width="10%">
						操作
					</th>
				</tr>
				<c:forEach items="${list.content}" var="item" varStatus="status">

					<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
						<td class="center">
							<span class="center">${status.count}</span>
						</td>
						<td>
							<u:substring content="${item.agent_no}" length="31"/>
						</td>
						<td>
							<u:substring content="${item.merchant_no}" length="31"/>
						</td>
						<td class="center">
							<fmt:formatDate value="${item.create_time}" type="both"/>
						</td>
						<td class="center">
							${item.oper_name}
						</td>
						<td class="center">
							<a href="javascript:showDetail(${item.id});">详情</a>
						</td>
					</tr>
				</c:forEach>

			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}"
				current="${list.number + 1}" anchor="_table" />
		</div>
	</div>
	<script>
		$(function(){
			$('#addNew').on('click', function(){
				window.location.href = '${ctx}/mer/limitChangeLoad';
			});
		});
		
		function showDetail(id){
			$.dialog({title:'限额修改详情',width: 700,height:400,resize: false,lock: true,max:false,content: 'url:limitChangeDetail?id='+id+'&layout=no'});
		}
	</script>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js">
</script>
</body>
