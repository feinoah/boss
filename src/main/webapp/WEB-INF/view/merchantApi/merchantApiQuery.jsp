<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
	var root = '${ctx}';
	function del(merchant_id) {
		$.dialog.confirm('确定要删除商户接口吗？', function() {
			var data = "id=" + merchant_id;
			$.get(root + "/merchantApi/delMerchantApi", data, function(data) {
				if (data.success) {
					showInfo("操作成功", function() {
						location.reload();
					}, "success.gif");
				} else {
					showInfo("操作失败");
				}
			});
		});
	}
	function startUsing(merchant_id) {
		$.dialog.confirm('确定要启用商户接口吗？', function() {
			changeForbidden("0", merchant_id);
		});
	}
	function endUsing(merchant_id) {
		$.dialog.confirm('确定要禁用商户接口吗？', function() {
			changeForbidden("1", merchant_id);
		});
	}
	function edit(merchant_id) {

	}
	function changeForbidden(status, merchant_id) {
		var data = "status=" + status + "&merchantId=" + merchant_id;
		$.get(root + "/merchantApi/changeForbindden", data, function(data) {
			if (data.success) {
				showInfo("操作成功", function() {
					$("#merQuery").submit();
				}, "success.gif");
			} else {
				showInfo("操作失败");
			}

		});
	}
	function showInfo(msg, ok, icon) {
		return $.dialog({
			title : '提示',
			lock : true,
			content : msg,
			icon : icon || 'error.gif',
			ok : ok
		});
	}
	function clearForm(){
		$(":text").attr("value","");
	}
	
	// 防止表单重复提交
	$(function(){
			$('input[type="submit"]').attr('disabled',true);
			setTimeout(function(){$('input[type="submit"]').attr('disabled',false);},3000);
		
			$('input[type="submit"]').on('click', function(event){
				event.preventDefault();
				$(this).attr("disabled",true);
				$('form:first').submit();		
			});
		});
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：系统管理&gt;商户接口查询
		</div>
		<form id="merQuery" action="${ctx}/merchantApi/merchantApiQuery" method="post">
			<div id="search">
				<div id="title">商户查询</div>
				<ul>
					<li>
						<span style="width: 90px;">商户名称/编号：</span>
						<input type="text" value="${params['merchantName']}" name="merchantName" />
					</li>
					<li>
						<span>创建时间：</span>
						<input type="text" style="width: 102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}"
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
						~
						<input type="text" style="width: 102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}"
							onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="button" onclick="location.href='${ctx}/merchantApi/merchantApiAddPage'" value="添加" />
				<input class="button blue medium" type="submit" value="查询" />
				<input name="reset" class="button blue medium" type="reset" value="清空" onclick="clearForm();"/>
			</div>
		</form>
		<a name="_table"></a>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="5%">序号</th>
						<th width="150">商户名称</th>
						<th width="150">商户编号</th>
						<th width="80">接口状态</th>
						<th width="120">创建时间</th>
						<th width="150">操作</th>
						<c:forEach items="${list.content}" var="item" varStatus="status">
							<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
								<td class="center"><span class="center">${status.count}</span></td>
								<td>${item.merchant_name}</td>
								<td>${item.merchant_no}</td>
								<td>${item.forbidden==1?"禁用":"正常"}</td>
								<td><fmt:formatDate value="${item.create_time}" type="both" /></td>
								<td class="center"><a href="${ctx}/merchantApi/merchantApiDetail?id=${item.id}">详情</a> <c:choose>
										<c:when test="${item.forbidden==1}">
											<a href="javascript:startUsing(${item.merchant_id});">启用</a>
										</c:when>
										<c:otherwise>
											<a href="javascript:endUsing(${item.merchant_id});">禁用</a>
										</c:otherwise>
									</c:choose> <a href="javascript:del(${item.id});">删除</a> <a href="${ctx}/merchantApi/goEditPage?id=${item.id}">修改</a></td>
							</tr>
						</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table" />
		</div>
	</div>
</body>
