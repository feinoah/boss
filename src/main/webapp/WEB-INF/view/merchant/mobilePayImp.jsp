<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
	
		#file_uploadUploader {
			vertical-align:middle;
			margin-left:10px;
		}
		
	</style>
	<script type="text/javascript">
	uploadFileExcel("file_upload","excelFileName",false,"no","100","200");
	
	function addMobile(){	
		
		var excelFileName = $.trim($("#excelFileName").val());
		
		if(excelFileName ==null || excelFileName==''){
			var dialog = $.dialog({title: '错误',lock:true,content: '导入文件不能为空',icon: 'error.gif',ok: function(){
	        	
			    }
			});
			return false;
		}
		
		
		$.dialog.confirm('确定要导入该文件吗？', function(){
			
			// formSubmit('mobileImp', null,null,successMsg,null,true);
			
		 	 $('#mobileImp').ajaxSubmit( {
			 //  $.ajax({
				  
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
				    	var line = data.line;
					  	if (ret == "OK"){
					  		successMsg(line);
					  	}else{
					  		var dialog = $.dialog({title: '提示',lock:true,content: '充值号码导入失败',icon: 'error.gif',ok:null });
					  	}
				  },
				  error:function(obj,status,errorInfo){
					  var dialog = $.dialog({title: '提示',lock:true,content: '充值号码导入失败',icon: 'error.gif',ok:null });
				  }
		 		}
		 	);
			
			
			// formSubmit('mobileImp', null,null,successMsg,null,true);
			
		});
		
	}

	function successMsg(line){
			var dialog = $.dialog({title: '提示',lock:true,content: '成功导入'+line+"行数据",icon: 'success.gif',ok:null });
			clearTerminal();
		}
	function clearTerminal(){
		$('#excelFileName').val('');
	}
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>充值号码导入</div>
    
    <form:form id="mobileImp" name="mobileImp" action="${ctx}/mer/mobileImp" method="post">
    	
    	<div class="item">
    	<div class="title">充值号码导入</div>
    	</br>
    	
    	<ul>
			<li style="width: 410px"><label style="width:80px">导入文件：</label><input id="excelFileName" name="excelFileName" type="text" readOnly="true" value=""/><input id="file_upload" name="file_upload" type="file" /></li>
    	</ul>
    	<ul>
			<li id="success" style="color:blue"></li>
    	</ul>
    	</div>
    	<div class="clear"></div>
    	</br>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"  value="导入" onclick="javascript:addMobile();"/>
    	</div>
    </form:form>
   
  </div>
</body>
