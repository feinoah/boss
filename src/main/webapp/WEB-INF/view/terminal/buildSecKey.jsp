<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>


<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>

<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>

<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>

<script>

function addTerminal(){
		 var INTEGER_REG = /^(0|([1-9][0-9]*))$/; //正整数
		 var count = $.trim($("#count").val());
			if (!count.match(INTEGER_REG)) {
				var dialog = $.dialog({title: '提示',lock:true,content: '请填写正确的数量',icon: 'error.gif',ok:null });
				return false;
			}
		 var device_type = $.trim($("#device_type").val());
		 if("-1"===device_type){
				var dialog = $.dialog({title: '提示',lock:true,content: '请选择机具类型',icon: 'error.gif',ok:null });
				return false;
		 }
		 
		 $("#keyExportFrom").submit();
		 
	
 	/*  	 $('#keyExportFrom').ajaxSubmit( {
			  
			   	beforeSubmit : function(){
	  				pop_waiting_info("请稍候...");
			    },
			    dataType: 'json',
			    type: 'POST',
			    iframe:false,
	 			// type:"post",
	 			// url:"${ctx}/ter/terImp",
	 			// data:{"excelFileName":excelFileName,"model":model},
	 			// dataType: 'json',
			  success: function(data){
				  	pop_waiting_close();
			    	var ret = data.msg;
				  	if (ret == "OK"){
				  		successMsg();
				  	}else{
				  		var dialog = $.dialog({title: '提示',lock:true,content: '密钥生成失败',icon: 'error.gif',ok:null });
				  	}
			  },
			  error:function(obj,status,errorInfo){
				  var dialog = $.dialog({title: '提示',lock:true,content: '密钥生成失败',icon: 'error.gif',ok:null });
			  }
	 		}
	 	);  */
		

	
}

function successMsg(){
		
	var dialog = $.dialog({title: '提示',lock:true,content: '密钥生成成功',icon: 'success.gif',ok:null });
} 


</script>

</head>
<body>

	
	<div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：机具管理>生成密钥</div>
    
    <form:form id="keyExportFrom" name="keyExportFrom" action="${ctx}/ter/keyExport" method="post">
    	
    	<div class="item">
    	<div class="title">生成密钥</div>
    	</br>
    	
    	<ul>
    		<li style="width: 250px;"><label style="width:120px">厂家： </label><select id="maker" name="maker" style="padding:2px; width:120px">
				<option value="-1">---请选择---</option>
				<option value="ITRON">艾创</option>
				<option value="NEWLAND">新大陆</option>
				<option value="BBPOS">BBPOS</option>
				<option value="TY">天喻</option>
			</select><label class="must">*</label></li>  			
			<li style="width: 250px;">
				<label style="width:120px">机具名称： </label><u:tername sname="device" value="" id="device"  style="padding:2px; width:120px"></u:tername><label class="must">*</label>
			</li>

			<li style="width: 250px"><label style="width:120px">生成数量：</label><input   style="width:120px" type="text" id="count"  name="count"  value="" /></li>
    	</ul>
    		<div class="clear"></div>
    	 <ul>
			<li style="width: 250px;"><label style="width:120px">机具类型： </label><select id="device_type" name="device_type" style="padding:2px; width:120px">
				<option value="-1">---请选择---</option>
				<option value="POS">POS</option>
				<option value="ITRON_BOX">爱刷</option>
				<option value="ITRON_DOT">点付宝</option>
				<option value="NEW_LAND_ME30">新大陆ME30</option>
				<option value="NEW_LAND_ME31">新大陆ME31</option>
				<option value="SPOS">超级刷</option>
				<option value="TY">天喻</option>
				<option value="M368">M368</option>
				<option value="M188">M188</option>
			</select><label class="must">*</label></li> 
    	</ul>
    	</div>
    	<div class="clear"></div>
    	</br>
    	
    	<div class="search_btn clear">
    		<%-- <img id="jindu" name="jindu" src="${ctx}/images/jindu.gif" style="display:none;"  />--%>
    		<input   class="button blue  " type="button" id="addButton"  value="生成密钥" onclick="javascript:addTerminal();"/>
    		<input   class="button blue  " type="button" id="clearButton"  value="返回" onclick="javascript:window.location.href='${ctx}/ter/terQuery'"/>
    		<input   class="button blue  " type="button"   value="刷新词典" onclick="javascript:window.location.href='${ctx}/ter/refreshDict'"/>
    	</div>
    </form:form>
   
  </div>
</body>



</html>