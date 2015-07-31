<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
#terminalAdd {
	padding: 10px;
}

#terminalAdd ul {
	overflow: hidden;
}

#terminalAdd ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 270px;
	heigth: 32px;
	line-height: 32px;
}

#terminalAdd ul li.column2 {
	width: 540px;
}

#terminalAdd ul li.column3 {
	width: 810px;
}

#terminalAdd ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#terminalAdd ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#terminalAdd ul li .area {
	width: 75px;
}

#terminalAdd ul li.long {
	width: 440px;
}

#terminalAdd div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}
</style>
<script type="text/javascript">
var groupbag_settle = "${groupInfo.bag_settle}";
var groupMy_settle = "${groupInfo.my_settle}";
	function validateCheckBox(){
		if($("#valiBox").is(':checked')){
			$("input[value=强行操作]").prop("disabled",false);
        }else{
        	$("input[value=强行操作]").prop("disabled",true);
        }
	}
	
	function addTerminal() {
		var merchant_no = $('#merchant_no').val();
		$.ajax({
			type : "post",
			url : "${ctx}/group/merchantAddCheck",
			data : {
				"merchant_no" : merchant_no
			},
			dataType : 'json',
			success : function(data) {
				var ret = data.msg;
				if (ret == "OK") {
					if(data.bag_settle == groupbag_settle && data.my_settle == groupMy_settle){
						$.dialog({
							lock : true,
							title : '警告',
							width : 340,
							height : 100,
							icon: 'alert.gif',
							resize : false,
							max : false,
							min : false,
						    id: 'testID2',
						    content: "确定添加商户到集群？",
						    button: [{
						            name: '确定',
						            disabled: false,
						            callback: function () {
						            	newAddMerchantToG(merchant_no);
						            }
						        },{name: '取消'} ]
						});
						//newAddMerchantToG(merchant_no);
					}else{
						//var content = "<font color='red'>添加集群后，该商户是否优质商户以及是否钱包结算将于当前集群保持一致！请谨慎操作！</font><br/><br/>";
						var content = "<font color='red'>当前商户是否优质以及是否钱包结算属性与集群存在差异，是否强行操作？</font><br/><br/>";
						content +="<input id='valiBox' type='checkbox' onclick='validateCheckBox()'>我同意<br/>";
						$.dialog({
							lock : true,
							title : '警告',
							width : 340,
							height : 100,
							icon: 'alert.gif',
							resize : false,
							max : false,
							min : false,
						    id: 'testID',
						    content: content,
						    button: [{
						            name: '强行操作',
						            disabled: true,
						            callback: function () {
						            	newAddMerchantToG(merchant_no);
						            }
						        },{name: '取消'} ]
						});
					}
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
	
	function newAddMerchantToG(merchant_no){
		var group_code = $("#group_code").val();
		if(group_code != ""){
			$.ajax({
				type : "post",
				url : "${ctx}/group/merAddGroupAndModifyMerInfo",
				data : {
					"merchant_no" : merchant_no,"group_code":group_code
				},
				dataType : 'json',
				success : function(data) {
					var ret = data.msg;
					if (ret == "OK") {
						$.dialog({title : "提示",	lock : true,content : "添加商户成功！",	icon : "success.gif",	ok : null});
						//alert("添加商户成功!");
						$('#merchant_no').val("");
					}else if(ret == "NOC"){
						alert("商户审核未通过！");
					}else if(ret == "NOGB"){
						alert("添加失败，未找到集群是否钱包结算信息！");
					}else if(ret == "NOGS"){
						alert("添加失败，未找到集群是否优质商户信息！");
					}else if(ret == "NOC"){
						alert("添加失败，商户未审核！");
					}else if(ret == "NOM"){
						alert("添加失败，无此商户！");
					}else if(ret == "ADDFAIL"){
						alert("添加商户失败！");
					}else if(ret == "ADDOKNOP"){
						$.dialog({title : "警告",	lock : true,content : "添加商户成功！商户状态开启失败，请手动开启！",	icon : "alert.gif"});
						//alert("添加商户成功！商户状态开启失败，请手动开启！");
					}else if(ret == "NOMSETTLE"){
						alert("添加失败,当前商户未指定是否优质信息!");
					}else if(ret == "NOGSETTLE"){
						alert("添加失败,集群未指定是否优质信息!");
					}else if(ret == "NOTEXISTM"){
						alert("添加失败,请核实该商户是否已被删除！");
					}else if(ret == "NOTEXISTGAM"){
						alert("添加失败,请核实当前集群或商户是否存在！");
					}else{
						alert("添加商户错误！");
					}
				}
			});
		}
	}

	function successMsg() {
		var dialog = $.dialog({
			title : '提示',
			lock : true,
			content : '录入成功',
			icon : 'success.gif',
			ok : null
		});
		clearTerminal();
	}
	function clearTerminal() {
		$('#merchant_no').val('');
		$('#merchant_no').focus();
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：收单机构管理>路由集群普通商户新增
		</div>
		
		<%--<form:form id="terminalAdd" action="${ctx}/group/merchantAddSubmit" --%>
		<form:form id="terminalAdd" action="${ctx}/group/merAddGroupAndModifyMerInfo"	method="post">

			<div class="item">
				<div class="title">路由集群编号新增</div>
				<ul>
					<li><label style="width: 80px">商户编号： </label>
					<input 	type="text" id="merchant_no" name="merchant_no" 	value="${params['merchant_no']}" /><label class="must">*</label></li>
				</ul>
				<c:choose>
					<c:when test="${empty param.id}">
						<ul>
							<li><label style="width: 80px">集群： </label> <u:TableSelect
									sname="group_code" tablename="trans_route_group" byField=""
									byField_value="" fleldAsSelectValue="group_code"
									fleldAsSelectText="group_name"></u:TableSelect>
						</ul>
					</c:when>
					<c:otherwise>
						<input type="hidden" id="group_code" name="group_code" value="${param.id}"/>
					</c:otherwise>
				</c:choose>
				<ul>
					<li id="success" style="color: blue"></li>
				</ul>
				<div class="search_btn clear">
					<shiro:hasPermission name="GROUP_ADD_MERCHANT">
					<input class="button blue  " type="button" id="addButton"
						value="保存" onclick="javascript:addTerminal();" /> 
						</shiro:hasPermission>
						<input
						class="button blue  " type="button" id="clearButton" value="清空"
						onclick="javascript:clearTerminal();" />
				</div>
			</div>
		</form:form>

	</div>
</body>
