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
		$("#pos_type option[value=-1]").attr("selected", true); 
		$("#pos_type_name").val("");
		$("#pos_model_name").val("");
		$("#pos_model").val("");
	});
	
	
	$("#submitButton").click(function(){
		var pos_type = $("#pos_type").val();
		var pos_model_name = $("#pos_model_name").val();
		var pos_model = $("#pos_model").val();
		if(pos_type == "-1"){
			alert("请选择设备类型!");
			return false;
		}
		
		if(pos_type == "00"){
			alert("该设备类型不支持新增型号，请重新选择设备类型!");
			$("#pos_type option[value=-1]").attr("selected", true); 
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
            url:"${ctx}/ter/savePosModel",
            data:$("#addPos").serialize(),
            async: false,
            dataType: 'json',
            error: function(request) {
                alert("提交型号信息时出现错误,请稍后重新尝试!");
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
            		$.dialog.confirm("保存成功！是否需要查询型号信息？点击取消留在当前页面", function(){
            			location.href ="${ctx}/ter/searchPos";
            		});
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
			<img class="left" src="${ctx}/images/home.gif" />当前位置：设备管理&gt;新增型号
		</div>
		<form:form id="addPos" action="${ctx}/ter/savePosModel" method="post">
			<div id="search">
				<div id="title">新增型号</div>
				<ul>
					<li><span style="width:65px;">设备类型：</span> 
							<u:TableSelect	sid="pos_type" sname="pos_type" style="padding:2px;width:145px" tablename="pos_type"  fleldAsSelectValue="pos_type" fleldAsSelectText="pos_type_name"
							 value="${params.pos_type}"	otherOptions="needAll" /><label class="must">*</label>
							 <input type="hidden"  id="pos_type_name" name="pos_type_name"/>
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
						<font color="red">注意：系统暂不提供设备型号修改以及删除功能，添加新型号后无法变更</font>
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<shiro:hasPermission name="POS_MODEL_ADD">
					<input class="button blue medium" type="button" id="submitButton"	value="保存" />
				</shiro:hasPermission>
				<input name="reset" class="button blue medium" type="button"	id="flushSearchCondition" value="清空" />
				<input class="button blue medium" type="button" onclick="location.href='${ctx}/ter/posManager'" value="返回" />
			</div>
		</form:form>
	</div>
</body>
