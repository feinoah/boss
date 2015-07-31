<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
 
	<script type="text/javascript">
	function changpwd(){	
		//验证部分
		var pwd = $.trim($("#pwd").val());
		if(pwd.length == 0){
			var dialog = $.dialog({title: '错误',lock:true,content: '原密码不能为空',icon: 'error.gif',ok: function(){
		        	$("#pwd").focus();
			    }
			});
			return false;
		}
	
		var pwd1 = $.trim($("#pwd1").val());
		if(pwd1.length == 0){
			var dialog = $.dialog({title: '错误',lock:true,content: '新密码不能为空',icon: 'error.gif',ok: function(){
		        	$("#pwd1").focus();
			    }
			});
			return false;
		}
		
		var reg = /^([a-zA-Z0-9]){6,20}$/;
		if(!reg.test(pwd1)){
			alert("密码长度必须为英文字母及数字组合，且长度为6至20个字符");
			return false;
		}
		var pwd2 = $.trim($("#pwd2").val());
		if(pwd2.length == 0){
			var dialog = $.dialog({title: '错误',lock:true,content: '确认的密码不能为空',icon: 'error.gif',ok: function(){
		        	$("#pwd2").focus();
			    }
			});
			return false;
		}
		
		if(pwd1 != pwd2){
			var dialog = $.dialog({title: '错误',lock:true,content: '新密码不一致',icon: 'error.gif',ok: function(){
		        	$("#pwd1").focus();
			    }
			});
			return false;
		}
		
		$.dialog.confirm('您确定要修改密码吗？', function(){
			  $.ajax({
		 			type:"post",
		 			url:"${ctx}/savepwd",
		 			data:{"pwd":$.md5(pwd),"pwd1":$.md5(pwd1),"pwd2":$.md5(pwd2)},
		 			dataType: 'json',
				  success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		successMsg();
					  	}else{
					  		var dialog = $.dialog({title: '提示',lock:true,content: '修改密码失败，请确认您输入的密码是否正确',icon: 'error.gif',ok:null });
					  	}
				  },
				  error:function(obj,status,errorInfo){
					  var dialog = $.dialog({title: '提示',lock:true,content: '修改密码失败',icon: 'error.gif',ok:null });
				  }
		 		}
		 	);
			
			// formSubmit('savepwdForm', null,null,successMsg,null,null);
		});
		
}

function successMsg(){

	var dialog = $.dialog({title: '提示',lock:true,content: '修改密码成功',icon: 'success.gif',ok:null });
}

</script>
</head>
<body><form:form id="savepwdForm" name="savepwdForm" action="${ctx}/savepwd" method="post">
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：修改密码</div>
     <div class="item">
    	<div class="title">修改密码</div>
    	<ul>
			<li style="clear: both "><label style="width:80px;display:block;float:left">原密码：</label><input name="pwd" type="password" id="pwd"    value="" />*</li>
		 </ul>
		 <ul>
			<li style="clear: both;"><label style="width:80px;display:block;float:left">新密码：</label><input name="pwd1"  type="password" id="pwd1"  value="" />*</br></li>
		 </ul> <ul>
			<li style="clear: both;"><label style="width:80px;display:block;float:left"> 确认新密码：</label><input name="pwd2"  type="password" id="pwd2"    value="" />*</li>
			<li style="clear: both;">   <a  class="button blue medium"   href="javascript:changpwd();">确认修改</a></li>
		 </ul>
		
	 	</div>
    </div>
  </div></form:form>
</body>
