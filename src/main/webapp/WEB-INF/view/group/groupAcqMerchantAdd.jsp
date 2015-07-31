<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
#GroupAcqMerchantAddForm {
	padding: 10px;
}

#GroupAcqMerchantAddForm ul {
	overflow: hidden;
}

#GroupAcqMerchantAddForm ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 270px;
	heigth: 32px;
	line-height: 32px;
}

#GroupAcqMerchantAddForm ul li.column2 {
	width: 540px;
}

#GroupAcqMerchantAddForm ul li.column3 {
	width: 810px;
}

#GroupAcqMerchantAddForm ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#GroupAcqMerchantAddForm ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#GroupAcqMerchantAddForm ul li .area {
	width: 75px;
}

#GroupAcqMerchantAddForm ul li.long {
	width: 440px;
}

#GroupAcqMerchantAddForm div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}
</style>
<script type="text/javascript">
	function addGroupAcqMerchant() {
		var acq_merchant_no = $('#acq_merchant_no').val();
		var acq_enname = $("select[name='acq_enname']").val();
		var group_code = $("#group_code").val();
		if(group_code == "-1"){
			alert("请选择集群信息！");
			return false;
		}
		
		if(acq_enname == "-1"){
			alert("请选择收单机构信息！");
			return false;
		}
		
		if(acq_merchant_no == ""){
			alert("请输入收单机构商户编号！");
			return false;
		}
		

		//var canAdd = false;
		$.ajax({
			type : "post",
			url : "${ctx}/group/groupAcqMrchantAddCheck",
			data : {
				"acq_merchant_no" : acq_merchant_no,
				"acq_enname" : acq_enname
			},
			dataType : 'json',
			success : function(data) {
				var ret = data.msg;
				if (ret == "OK") {
					canAdd = true;
					$.dialog.confirm('确定新增该条记录吗？？', function() {
						formSubmit('GroupAcqMerchantAddForm', null, null, successMsg, null,null);
					});
				} else {
					$.dialog({
						title : '提示',
						lock : true,
						content : ret + '',
						icon : 'error.gif',
						ok : null
					});
				}
			}
		});
	}

	function successMsg() {
		var dialog = $.dialog({
			title : '提示',
			lock : true,
			content : '录入成功',
			icon : 'success.gif',
			ok : null
		});
		clearGroupAcqMerchant();
	}
	function clearGroupAcqMerchant() {
		$('#acq_merchant_no').val("");
		$('#acq_merchant_no').focus();
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：收单机构管理&gt;路由集群收单商户
		</div>
		<form:form id="GroupAcqMerchantAddForm" action="${ctx}/group/acqMerchantAddSubmit"	method="post">
			<div class="item">
				<div class="title">路由集群商户新增</div>
				<c:choose>
					<c:when test="${empty param.id}">
						<ul>
							<li><label style="width: 40px">集群：</label>
							<select id="group_code" name="group_code" style="width:220px;height: 24px">
								<option value="-1">--请选择--</option>
								<c:forEach items="${groupList}" var="item" varStatus="status">
							          <option value="${item.group_code}">${item.group_name}</option>
							       </c:forEach>
							</select>
							 <%--<u:TableSelect
									sname="group_code" tablename="trans_route_group" byField=""
									byField_value="" fleldAsSelectValue="group_code"
									fleldAsSelectText="group_name" style="width:140px;"></u:TableSelect>
							--%></li>
						</ul>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="group_code" id="group_code" value="${param.id}" />
					</c:otherwise>
				</c:choose>

				<ul>
					<li><label style="width: 60px">收单机构： </label> 
					<select id="acq_enname" name="acq_enname" style="padding:2px;width:200px;height: 24px" class="required">
					<option value="-1">--请选择--</option>
					<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" id="acq_enname">${m.acq_cnname}</option>	
					</c:forEach>
				</select>
					<%--<u:TableSelect
							sname="acq_enname" tablename="acq_org" byField=""
							byField_value="" fleldAsSelectValue="acq_enname"
							fleldAsSelectText="acq_cnname"></u:TableSelect>
					--%></li>
				</ul>

				<ul>
					<!-- 收单机构商户编号 -->
					<li style="width: 320px">收单机构商户编号: <input type="text" id="acq_merchant_no" style="width: 155px"
						name="acq_merchant_no" value="${params['acq_merchant_no']}" /><label
						class="must">*</label></li>
				</ul>
				<ul>
					<li id="success" style="color: blue"></li>
				</ul>
				<div class="search_btn clear">
					 <shiro:hasPermission name="GROUP_ADD_ACQ_MERCHANT">
					<input class="button blue  " type="button" id="addButton"
						value="保存" onclick="javascript:addGroupAcqMerchant();" />
						</shiro:hasPermission>
						 <input
						class="button blue  " type="button" id="clearButton" value="清空"
						onclick="javascript:clearGroupAcqMerchant();" />
				</div>
			</div>
		</form:form>
	</div>
</body>
