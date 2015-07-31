<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />

<script type="text/javascript">
	var EMAIL_REG = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
	var MOBILE_REG = /^1[0-9]{10}$/;

	function addCheck() {
		var api = frameElement.api, W = api.opener;
		//验证部分
		var acq_merchant_no = $.trim($("#acq_merchant_no").val());
		/*
		if(acq_merchant_no ==null || acq_merchant_no==''){
				var dialog = $.dialog({title: '错误',lock:true,content: '银盛商户编号不能为空',icon: 'error.gif',ok: function(){
		        	$("#acq_merchant_no").focus();
				    }
				});
				return false;
		}
		 */
		var t = $.trim($("#acq_terminal_no").val());
		if (t.length < 3) {
			alert("银盛终端编号错误");
			$("#acq_terminal_no").focus();
			return false;
		}

		var flag = checkAcqMerNo(acq_merchant_no);
		if (!flag) {
			return false;
		}

		if (confirm('确定要添加该条记录吗？')) {
			$
					.ajax({
						type : "post",
						url : "${ctx}/acq/terminalAdd",
						data : {
							acq_enname : $("#acq_enname").val(),
							acq_merchant_no : $("#acq_merchant_no").val(),
							acq_terminal_no : $("#acq_terminal_no").val()
						},
						async : false,
						success : function(data) {
							var ret = data.msg;
							if (ret == "OK") {
								alert("插入成功");
								api.close();

							} else if (ret == "ERROR") {
								alert("收单机构终端编号" + $("#acq_terminal_no").val()
										+ "已使用，请检查。");
							} else {
								alert("银盛终端新增失败 " + ret);
							}
						}
					});
		}

	}

	function successMsg() {
		var dialog = $.dialog({
			title : '提示',
			lock : true,
			content : '银盛终端新增成功',
			icon : 'success.gif',
			ok : null,
			close : function() {
				location.href = "${ctx}/acq/terminalQuery";
			}
		});
	}

	function checkAcqMerNo(acqMerNo) {
		var mark = true;
		$.ajax({
			type : "post",
			url : "${ctx}/acq/checkTerAdd",
			data : {
				"acq_merchant_no" : acqMerNo
			},
			async : false,
			dataType : 'json',
			success : function(data) {
				var ret = data.msg;
				if (ret == "noexist") {
					$.dialog.alert("银盛商户编号不存在请确认。");
					$("#acq_merchant_no").focus();
					mark = false;
				} else {
					mark = true;
				}
			}
		});
		return mark;
	}
</script>
<style type="text/css">
#content {
	width: 320px;
	height: 140px;
	margin-top: 10px;
}

#content ul li {
	height: 32px;
	line-height: 32px;
	padding-left: 20px;
}
</style>
</head>
<body>

	<div id="content">
		<%--    <form:form id="agentAdd" action="${ctx}/acq/terminalAdd" method="post">--%>

		<ul>

			<li style="display:none;"><label>收单机构编号：</label><input
				type="text" id="acq_enname" name="acq_enname" readonly="readonly"
				value="${params['acq_enname']}" /></li>
			<li><label>收单机构商户编号：</label><input type="text"
				id="acq_merchant_no" name="acq_merchant_no" readonly="readonly"
				value="${params['acq_merchant_no']}" /></li>
			<li><label>收单机构终端编号：</label><input type="text"
				id="acq_terminal_no" name="acq_terminal_no" value="" /></li>

		</ul>
		<div class="search_btn">
			<input class="button rosy medium" type="button" id="addButton"
				value="保存" onclick="javascript:addCheck();" />
		</div>
	</div>
	<%--    </form:form>--%>

	</div>
</body>
