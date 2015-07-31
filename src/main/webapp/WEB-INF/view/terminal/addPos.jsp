<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<style type="text/css">
#content ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}
</style>
<script type="text/javascript">
$(function(){
	
	$("#pos_type").change(function(){
		var pos_type_name = "";
		var pos_type = $("#pos_type option:selected").val();
		if(pos_type != "-1"){
			pos_type_name = $("#pos_type option:selected").text();
		}
		$("#pos_type_name").val(pos_type_name);
	});
	
	/* 清空 */
	$("#flushSearchCondition").click(function(){
		$("#pos_type_name").val("");
		$("#pos_model_name").val("");
		$("#pos_model").val("");
	});
	
	
	$("#submitButton").click(function(){
		var pos_type_name = $("#pos_type_name").val();
		var pos_model_name = $("#pos_model_name").val();
		var pos_model = $("#pos_model").val();
		
		if(pos_type_name == ""){
			alert("请输入设备名称!");
			return false;
		}
		
		if(pos_type_name.length < 2){
			alert("正确的设备名称长度限制为2~10位!");
			return false;
		}
		
		if(pos_model_name == ""){
			alert("请输入型号名称!");
			return false;
		}
		
		if(pos_model_name.length < 2){
			alert("正确的型号名称长度限制为2~10位!");
			return false;
		}
		
		if(pos_model == ""){
			alert("请输入型号编码!");
			return false;
		}
		
		if(pos_model.length < 2){
			alert("正确的型号编码长度限制为2~10位!");
			return false;
		}
			$.ajax({
	            cache: false,
	            type: "POST",
	            url:"${ctx}/ter/savePos",
	            data:$("#addPos").serialize(),
	            async: false,
	            dataType: 'json',
	            error: function(request) {
	                alert("提交设备信息时出现错误,请稍后重新尝试!");
	            },
	            success: function(data) {
	            	var code = data.code;
	            	if(code == "1001"){
	            		 alert("保存失败,参数为空!");
	            	}else if(code == "1002"){
	            		alert("保存失败,型号名称为空!");
	            	}else if(code == "1003"){
	            		alert("保存失败,型号名称已存在!");
	            	}else if(code == "1004"){
	            		alert("保存失败,型号编码为空!");
	            	}else if(code == "1005"){
	            		alert("保存失败,型号编码已存在!");
	            	}else if(code == "1006" || code == "1008"){
	            		alert("保存时出现错误,请重新尝试!");
	            	}else if(code == "1007"){
	            		$("#flushSearchCondition").click();
	            		$.dialog.confirm("保存成功！是否需要查询设备信息？继续添加设备请点取消", function(){
	            			location.href ="${ctx}/ter/searchPos";
	            		});
	            	}else if(code == "1009"){
	            		alert("保存失败,设备名称为空!");
	            	}else if(code == "1010"){
	            		alert("保存失败,设备名称已存在!");
	            	}else{
	            		alert(code);
	            	}
	            }
	        });
	});
});

</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：设备管理&gt;新增设备
		</div>
		<form:form id="addPos" action="${ctx}/ter/savePos" method="post">
			<div id="search">
				<div id="title">新增设备</div>
				<ul>
					<li><span style="width:65px;">设备类型：</span> 
							<input type="text"  id="pos_type_name" name="pos_type_name" style="padding:2px;width:140px" maxlength="10"/><label class="must">*</label>(例:移联商通、超级刷，长度:2~10)
					</li>
					</ul>
					<div class="clear"></div>
					<ul>
					<li><span style="width:65px;">型号名称：</span>
							<input type="text" id="pos_model_name"  name="pos_model_name" style="padding:2px;width:140px" maxlength="10"/><label class="must">*</label>(例:移联商通III、超级刷V，长度:2~10)
					</li>
					</ul>
					<div class="clear"></div>
					<ul>
					<li><span style="width:65px;">型号编码：</span>
					<input type="text" name="pos_model"	  id="pos_model" style="padding:2px;width:140px" maxlength="10"/><label class="must">*</label>(例:M368、SPOS_2，长度:2~10)
					</li>
				</ul>
				<div class="clear"></div>
				<ul>
				<li>
						<font color="red">注意：系统暂不提供设备修改以及删除功能，添加新设备后无法变更</font>
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<shiro:hasPermission name="POS_TYPE_ADD">
				<input class="button blue medium" type="button" id="submitButton"	value="保存" />
				</shiro:hasPermission>
				<input name="reset" class="button blue medium" type="button"	id="flushSearchCondition" value="清空" />
				<input class="button blue medium" type="button" onclick="location.href='${ctx}/ter/posManager'" value="返回" />
			</div>
		</form:form>
	</div>
</body>
