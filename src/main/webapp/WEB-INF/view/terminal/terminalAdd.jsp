<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#terminalAdd
		{
			padding:10px;
		}
	
		#terminalAdd ul
		{
			overflow:hidden;
		} 
		
		#terminalAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:270px;
			heigth:32px;
			line-height:32px;
		}
		
		#terminalAdd ul li.column2
		{
			width:540px;
		}
		
		#terminalAdd ul li.column3
		{
			width:810px;
		}
		
		#terminalAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:90px;
		}
		#terminalAdd ul li label.must		
		{
			display:-moz-inline-box;
			display:inline-block;
			width:5px;
			text-align:center;
			color:red;
		}
		#terminalAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#terminalAdd ul li.long
		{
			width:440px;
		}
		
		
		#terminalAdd div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
	</style>
	<script type="text/javascript">
	
	function addTerminal(){	
		var pos_type = $("#pos_type").val();
		if(pos_type == "00"){
			alert("该设备不提供子机型选项,请重新选择设备，如无目标设备类型，请在设备管理中添加新设备!");
			return false;
		}
		
		if(pos_type == "-1"){
			alert("请选择设备类型!");
			return false;
		}
		
		var model = $("#model").val();
		if(model == "-1"){
			alert("请选择机具型号!");
			return false;
		}
		
		var sn = $.trim($("#sN").val());
		if(sn ==null || sn==''){
			var dialog = $.dialog({title: '错误',lock:true,content: '机器SN号不能为空',icon: 'error.gif',ok: function(){
	        	$("#sN").focus();
			    }
			});
			return false;
		}
		
/* 		if(sn.length!=20){
			var dialog = $.dialog({title: '错误',lock:true,content: 'SN号长度不正确，正确长度为20位',icon: 'error.gif',ok: function(){
	        	$("#sN").focus();
			    }
			});
			return false;
		} */
		
		var psamNo = $.trim($("#psamNo").val());
		if(psamNo ==null || psamNo==''){
			var dialog = $.dialog({title: '错误',lock:true,content: 'psam编号不能为空',icon: 'error.gif',ok: function(){
	        	$("#psamNo").focus();
			    }
			});
			return false;
		}
		
		flag =checkPasmNO(psamNo,sn);
		if(flag ==false)
		{
			alert("PSAM 或 SN 已存在!");
			return false;
		}
		
		
		/*	var psamNo = $.trim($("#psamNo").val());
 		if(psamNo.length!=16){
			var dialog = $.dialog({title: '错误',lock:true,content: 'psam编号长度不正确，正确长度为16位',icon: 'error.gif',ok: function(){
	        	$("#psamNo").focus();
			    }
			});
			return false;
		} */
		
		$.dialog.confirm('确定新增该条记录吗？？', function(){
						formSubmit('terminalAdd', null,null,successMsg,null,null);
						
					});
		
	}
	function checkPasmNO(pasmNo,sn){
			var mark = true;
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/ter/terCheck",
		 			data:{"psam_no":pasmNo,"sn":sn},
		 			async:false,
		 			dataType: 'json',
				  success: function(data){
				    	var ret = data.msg;
					  	if (ret == "existPsamNo"){
					  		//$("#agentNameError").html("代理商名称已被占用");
					  		$.dialog.alert("psam编号已被占用");
					  		$("#psamNo").focus();
					  		mark =  false; 
					  	}else if(ret == "existSN"){
					  		$.dialog.alert("机具SN号已被占用");
					  		$("#sN").focus();
					  		mark =  false; 
					  	}
					  	else{
					  		mark = true;
					  	}
				  }
		 		}
		 	);
			 return mark;
	}
	function successMsg(){
			var dialog = $.dialog({title: '提示',lock:true,content: '机具录入成功',icon: 'success.gif',ok:null });
			clearTerminal();
		}
	function clearTerminal(){
		$('#sN').val('');
		$('#psamNo').val('');
		$('#psamNo').focus();
	}
	$(function(){
		var defPosType = "${params.pos_type}";
		if(defPosType == ""){
			defPosType = "-1";
		}
		/* 默认加载设备型号信息 */
		searchPosModel(defPosType, "-1");
		
	/* 基于设备类型加载所属型号 */
	$("#pos_type").change(function(){
		var pos_type = $("#pos_type").val();
		if(pos_type == "00"){
			alert("该设备不提供子机型选项,请重新选择设备，如无目标设备类型，请在设备管理中添加新设备!");
			$("#pos_type option[value=-1]").attr("selected", true); 
			searchPosModel("-1","-1");
		}else{
			searchPosModel(pos_type,"-1");
		}
	});
	
	/* 传入pos_type 加载  对应的型号列表至SELECT元素,传入defSelectModel 自动选中默认选项*/
	function searchPosModel(pos_type, defSelectModel){
		$("#model").empty();
		$("<option value='-1'>--全部--</option>").appendTo("#model");
		if(pos_type != "-1"){
				$.ajax({
					type:"post",
					url:"${ctx}/ter/searchPosModel",
					data:{"pos_type":pos_type},
					async:false,
					dataType: 'json',
				  	success: function(json){
					  for(var i=0;i<json.length;i++){
						  if(defSelectModel == json[i].pos_model && defSelectModel != "-1"){
							  $("<option value="+json[i].pos_model+" selected>"+json[i].pos_model_name+"</option>").appendTo("#model");
						  }else{
							  $("<option value="+json[i].pos_model+">"+json[i].pos_model_name+"</option>").appendTo("#model");
						  }
					  }
				  }
			});
		}
	}
	});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：机具管理&gt;机具录入</div>
    
    <form:form id="terminalAdd" action="${ctx}/ter/terAdd" method="post">
    	
    	<div class="item">
    	<div class="title">机具录入</div>
    	<ul>
			<li>
				<span style="width:80px;">设备类型：</span> 
				<u:TableSelect	sid="pos_type" sname="pos_type" style="padding:2px;width:155px" tablename="pos_type"  fleldAsSelectValue="pos_type" fleldAsSelectText="pos_type_name"
							 value="${params.pos_type}"	otherOptions="needAll" /><label class="must">*</label>
			</li>
		</ul>
    	<ul>
			<li><label style="width:80px">机具型号： </label><select name="model"  id="model" style="padding:2px; width:155px">
			</select><label class="must">*</label>
			<input type="text" id="model"  name="model"  value="${params['model']}" />
			
			<label class="must">*</label></li>
    	</ul>
    	<ul>
			<li><label style="width:80px">psam编号：</label><input type="text" id="psamNo"  name="psamNo"  value="${params['psamNo']}"/><label class="must">*</label></li>
    	</ul>
    	<ul>
			<li style="width:280px" ><label style="width:80px">机具SN号：</label><input   style="width:150px" type="text" id="sN"  name="sN"  value="${params['sN']}" /><label class="must">*</label></li>
    	</ul>
    	
    	<ul>
			<li id="success" style="color:blue"></li>
    	</ul>
    	</div>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"  value="保存" onclick="javascript:addTerminal();"/>
    		<input   class="button blue  " type="button" id="clearButton"  value="清空" onclick="javascript:clearTerminal();"/>
    		<input class="button blue " type="button" onclick="location.href='${ctx}/ter/terQuery'"	value="返回" />
    	</div>
    </form:form>
   
  </div>
</body>
