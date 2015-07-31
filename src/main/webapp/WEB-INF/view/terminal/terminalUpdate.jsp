<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#terminalUpdate
		{
			padding:10px;
		}
	
		#terminalUpdate ul
		{
			overflow:hidden;
		} 
		
		#terminalUpdate ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:270px;
			heigth:32px;
			line-height:32px;
		}
		
		#terminalUpdate ul li.column2
		{
			width:540px;
		}
		
		#terminalUpdate ul li.column3
		{
			width:810px;
		}
		
		#terminalUpdate ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:90px;
		}
		#terminalUpdate ul li label.must		
		{
			display:-moz-inline-box;
			display:inline-block;
			width:5px;
			text-align:center;
			color:red;
		}
		#terminalUpdate ul li .area
		{
			width:75px;
		}
		
		
		
		#terminalUpdate ul li.long
		{
			width:440px;
		}
		
		
		#terminalUpdate div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
	</style>
	<script type="text/javascript">
	
	function modidyTerminal(){	
		var  model=$.trim($("#model").val());
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
		if(model==null || model==''){
	    	var dialog = $.dialog({title: '错误',lock:true,content: '机具型号不能为空',icon: 'error.gif',ok: function(){
		        	$("#model").focus();
				    }
			});
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
		
		
		var psamNo = $.trim($("#psamNo").val());
		var id = $("#id").val();
		flag =checkPasmNO(psamNo,id,sn);
		if(flag ==false)
		{
			return false;
		}
		
		if(psamNo ==null || psamNo==''){
			var dialog = $.dialog({title: '错误',lock:true,content: 'psam编号不能为空',icon: 'error.gif',ok: function(){
	        	$("#psamNo").focus();
			    }
			});
			return false;
		}
		
		var sn = $.trim($("#psamNo").val());
		/*if(sn.length!=16 && sn.length!=12){
			var dialog = $.dialog({title: '错误',lock:true,content: 'psam编号长度不正确，正确长度为12或16位',icon: 'error.gif',ok: function(){
	        	$("#psamNo").focus();
			    }
			});
			return false;
		}*/
		
		$.dialog.confirm('确定修改该条记录吗？？', function(){
						formSubmit('terminalUpdate', null,null,successMsg,null,null);
						
					});
		
	}
	function checkPasmNO(pasmNo,id,sn){
			var mark = true;
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/ter/terCheck",
		 			data:{"psam_no":pasmNo,"id":id,"sn":sn},
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
			var dialog = $.dialog({title: '提示',lock:true,content: '机具修改成功',icon: 'success.gif',ok:null });
			clearTerminal();
		}
	function successMsg1(){
		$('#success').text('机具修改成功');
	}
	function clearTerminal(){
		$('#sN').val('');
		$('#psamNo').val('');
		$('#psamNo').focus();
	}
	
	
	$(function(){
		/* 校验进入界面是否需要加载设备型号信息 */
		var defPosType = "${params.pos_type}";
		var defModel = "${params.model}";
		if(defPosType == ""){
			defPosType = "-1";
		}
		if(defModel == ""){
			defModel = "-1";
		}
		
		/* 默认加载设备型号信息 */
		searchPosModel(defPosType, defModel);
		
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
		$("<option value='-1'>--请选择--</option>").appendTo("#model");
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
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：机具管理&gt;机具修改</div>
    <form:form id="terminalUpdate" action="${ctx}/ter/terUpdate" method="post">
    	<input type="hidden" value="${params['id']}" name="id" id="id">
    	<div class="item">
    	<div class="title">机具修改</div>
    	<ul>
					<li>
						<span style="width:80px;">设备类型：</span> 
						<u:TableSelect	sid="pos_type" sname="pos_type" style="padding:2px;width:110px" tablename="pos_type"  fleldAsSelectValue="pos_type" fleldAsSelectText="pos_type_name"
									 value="${params.pos_type}"	otherOptions="needAll" /><label class="must">*</label>
					</li>
				</ul>
    	<ul>
			<li><label style="width:76px">机具型号： </label>
			<select name="model" id="model" style="padding:2px; width:155px">
					<option value="-1">--请选择--</option>
			</select>
			<label class="must">*</label></li>
    	</ul>
    	<ul>
			<li><label style="width:80px">psam编号：</label><input type="text" id="psamNo"  name="psamNo"  value="${params['psam_no']}"/><label class="must">*</label></li>
    	</ul>
    	<ul>
			<li style="width:280px" ><label style="width:80px">机具SN号：</label><input   style="width:150px" type="text" id="sN"  name="sN"  value="${params['sn']}" /><label class="must">*</label></li>
    	</ul>
    	
    	<ul>
			<li id="success" style="color:blue"></li>
    	</ul>
    	</div>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"  value="保存" onclick="javascript:modidyTerminal();"/>
    		<input   class="button blue  " type="button" id="clearButton"  value="清空" onclick="javascript:clearTerminal();"/>
    	</div>
    </form:form>
  </div>
</body>
