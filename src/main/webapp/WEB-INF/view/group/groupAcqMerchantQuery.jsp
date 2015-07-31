<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>

<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<script type="text/javascript">
	function successMsg(callback) {
		callback = callback || function() {
		};
		$.dialog({
			title : '提示',
			lock : true,
			content : '删除成功',
			icon : 'success.gif',
			ok : callback
		});
	}
	$(function() {
		$(".changeStatus").on(
				"click",
				function() {
					var $this = $(this);
					var status = $this.attr("data-status");
					var p = status == 1 ? '启用' : '关闭';
					$.dialog.confirm("确定要" + p + "吗？", function() {
						var acqMerchantNo = $this.attr("data-merchantNo");
						var data = "acqMerchantNo=" + acqMerchantNo
								+ "&status=" + status;
						$.get("${ctx}/group/merchantChangeStatus", data,
								function(msg) {
									if (msg.msg == "OK") {
										if (status == 1) {
											$this.attr("data-status", 0);
											$this.text("关闭");
										} else {
											$this.attr("data-status", 1);
											$this.text("启用");
										}
									} else {
										$.dialog({
											title : "错误",
											lock : true,
											content : msg.msg,
											ok : null
										});
									}
								});
					});
				});
		$(".del").on("click", function() {
			var $this = $(this);
			$.dialog.confirm("确定要删除", function() {
				var dd = "id=" + $this.attr("data-id");
				$.post('${ctx}/group/groupAcqMerchantDel', dd, function(msg) {
					if (msg.msg == "OK") {
						successMsg(function(){
							window.location.reload();
						});
					} else {
						$.dialog({
							title : "错误",
							lock : true,
							content : '删除失败',
							ok : null
						});
					}
				});
			});
		});
		
		//导出集群收单商户
		$("#groupAcqMerchantExport").click(function(){
			var checkCode = /^([0-9])+$/; 
			 var group_code = $("#group_code").val();
			 var acq_enname = $("#acq_enname").val();
			 var acq_merchant = $("#acq_merchant").val();
			 if(group_code != "" || acq_enname !="" || acq_merchant != ""){
				 if((group_code != "" && group_code.length > 0) || (acq_enname != "")){
					 if(!group_code.match(checkCode)){
						 alert("集群编号仅允许输入数字。");
						 return false;
					 }
					 
					 if(group_code.length > 6 || group_code.length < 3){
						 alert("集群编号仅允许输入3~6位数以内的数字。");
						 return false;
					 }
				 }	 
					 $.ajax({
				 			type:"post",
				 			url:"${ctx}/group/checkGroupAcqCodeValidity",
				 			data:{"group_code":group_code,"acq_enname":acq_enname,"acq_merchant":acq_merchant},
				 			dataType: 'json',
						  	success: function(data){
						    	var ret = data.success;
							  	if (ret == true){
							  		$("#merQuery").attr({action:"${ctx}/group/exportGroupAcqMerchant"}).submit();
							  		$("#merQuery").attr({action:"${ctx}/group/groupAcqMerchantQuery"});
							  	}else{
							  		alert("该集群编号不存在，或该集群下没有可导出的收单商户信息。");
							  	}
						  }
				 		}
				 	);
				 
			 }else{
				 alert("请指定任意一种查询条件!");
			 }
			 
		});
	});

</script>

</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：收单机构管理&gt;路由集群商户查询
		</div>
		<form:form id="merQuery" action="${ctx}/group/groupAcqMerchantQuery" method="post">
			<input type="hidden" name="id" id="trans_route_group_merchant_id" />
			<div id="search">
				<div id="title">路由集群商户查询</div>
				<ul>
					<li>
						<span style="width: 160px;">集群编号：</span>
						<input type="text" style="width: 132px;" value="${params['group_code']}" name="group_code"  id="group_code" />
					</li>
					<li>
						<%--<span style="width: 140px;">收单机构代码：</span>
						<input type="text" style="width: 132px;" value="${params['acq_enname']}" name="acq_enname" />--%>

						<span style="width: 70px;">收单机构：</span>
						<select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
							<option value="">全部</option>
							<c:forEach items="${acqOrgList}" var="m">
							<option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>
							</c:forEach>
						</select>
					</li>
					<li>
						<span style="width: 160px;">收单机构商户名称/编号：</span>
						<input type="text" style="width: 132px;" value="${params['acq_merchant']}" name="acq_merchant"  id="acq_merchant"/>
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="query" value="查询" />
				<shiro:hasPermission name="GROUP_ACQMERCHANT_ADD">
					<input class="button blue medium" type="button" id="add"
						onclick="javascript:window.location.href='${ctx}/group/groupAcqMerchantAdd'" value="增加" />
				</shiro:hasPermission>
				<input name="reset" class="button blue medium" type="reset" id="reset" value="清空" />
				<input class="button blue medium" type="button" id="groupAcqMerchantExport" value="导出集群收单商户" />
			</div>

		</form:form>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="30">序号</th>
						<%--  <th width="80">收单机构代码</th> --%>
						<th width="80">收单机构名称</th>
						<th width="120">收单机构商户编号</th>
						<th width="200">收单机构商户名称</th>
						<th width="40">编号</th>
						<th width="80">集群名称</th>
						<th width="130">最后使用时间</th>
						<th width="80">操作</th>
						<c:forEach items="${list.content}" var="item" varStatus="status">
							<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
								<td class="center"><span class="center">${item.id}</span></td>
								<%--    <td class="center"><span class="center">${item.acq_name}</span></td> --%>
								<td><u:substring length="11" content="${item.acq_cnname}" /></td>
								<td class="center"><span class="center">${item.acq_merchant_no}</span></td>
								<td style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${item.acq_merchant_name}">${item.acq_merchant_name}</td>
								<td class="center"><span class="center">${item.group_code}</span></td>
								<td><u:substring length="8" content="${item.group_name}" /></td>
								<td><fmt:formatDate value="${item.last_use_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both" /></td>
								<td align="center"><shiro:hasPermission name="GROUP_ACQMERCHANT_DEL">
										<a class="del" href="javascript:void(0);" data-id="${item.id}">删除</a>
										<c:if test="${item.status==1}">
											<a class="changeStatus" href="javascript:void(0);" data-merchantNo="${item.acq_merchant_no}" data-status="0">关闭</a>
										</c:if>
										<c:if test="${item.status==0}">
											<a class="changeStatus" href="javascript:void(0);" data-merchantNo="${item.acq_merchant_no}" data-status="1">启用</a>
										</c:if>
									</shiro:hasPermission></td>
							</tr>
						</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
		</div>
	</div>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
