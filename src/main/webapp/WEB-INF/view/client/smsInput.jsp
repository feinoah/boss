<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp"%>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
#clientMessageSave {
	padding: 10px;
}

#clientMessageSave ul {
	overflow: hidden;
}

#clientMessageSave ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 350px;
	heigth: 32px;
	line-height: 32px;
}

#clientMessageSave ul li.column2 {
	width: 540px;
}

#clientMessageSave ul li.column3 {
	width: 810px;
}

#clientMessageSave ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 100px;
}

#clientMessageSave ul li label.must {
	width: 5px;
	color: red;
}

#clientMessageSave ul li .area {
	width: 75px;
}

#clientMessageSave ul li.long {
	width: 440px;
}

#clientMessageSave div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#file_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

#mlogo_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

#attachment_fileUploader {
	vertical-align: middle;
	margin-left: 10px;
}
</style>
	<script type="text/javascript">
		$(function(){
			var flag = '${flag}';
			 if(flag == "1"){
				   var dialog = $.dialog({title: '成功',lock:true,content: '发送完成',icon: 'success.gif',ok: function(){
					   window.location.href="${ctx}/clientMessage/toMsgInputAdd";
			    	}				 
				});
			}
			

			
			$("#addButton").click(function(){
			    var isSubmit = true;
				var id = $("#id").val();
				$.each($("input:text,select,textarea"),function(i,n){
					if($(n).hasClass("required"))
					{
						if($.trim($(n).val()).length === 0)
						{
							var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'不能为空',icon: 'error.gif',ok: function(){
					        		$(n).focus();
						    	}
							});
							isSubmit = false;
							return false;
						}
					}		
				});
				
			
			if(isSubmit)
			{
				if(confirm('确定要发送短信吗？')){
					$("#sendSmsContent").submit();
			    }
			}
			});	
		});
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：系统管理>批量发送手机短信
		</div>
		<form:form id="sendSmsContent" action="${ctx}/clientMessage/sendSmsContent" method="post">
			<div class="item">
				<ul>
					<div class="title" >重要提示 &nbsp;&nbsp;&nbsp;<label  style="color:red;">如群发信息，手机号请用英文,号隔开(如:13799999999,13877777777)</label ></div>
					<li style="width:610px; height: 130px;">
						<label>
							手机号码：
						</label>
						<textarea rows="6" cols="71" name="mobileNo" id="mobileNo"
							class="required"></textarea>
						<label class="must">
							*
						</label>
					</li>

					<li style="width: 610px; height: 130px;">
						<label>
							发送内容：
						</label>
						<textarea rows="4" cols="71" name="smsContent" id="smsContent"
							class="required"></textarea>
							<label class="must">*</label>
					<li>
				</ul>
				<div class="search_btn clear">
					<input class="button blue " type="button" id="addButton" value="发送"/>
				</div>
			</div>
		</form:form>
	</div>
</body>
