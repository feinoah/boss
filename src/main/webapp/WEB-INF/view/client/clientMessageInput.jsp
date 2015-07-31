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
			 if(flag == "1")
			{
				   var dialog = $.dialog({title: '成功',lock:true,content: '新增手机客户端信息'+''+'成功',icon: 'success.gif',ok: function(){
					   window.location.href="${ctx}/clientMessage/clientMessageQuery";
			    	}				 
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新手机客户端信息'+''+'成功',icon: 'success.gif',ok: function(){
						window.location.href="${ctx}/clientMessage/clientMessageQuery";
					}
				});
			}else if(flag == "0")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: "${errorMessage}",icon: 'error.gif',ok: function(){
					$("form input:text").first().focus();
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
		/* 		var params = $('#clientMessageSave').serialize();
				alert("参数："+params); */
				$("#clientMessageSave").submit();
			}
			});	
		});
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：系统管理>手机客户端信息
		</div>
		<form:form id="clientMessageSave" action="${ctx}/clientMessage/clientMessageSave" method="post">
			<div class="item">
				<div class="title">
					客户端信息：
				</div>
				<ul>
					<li style="display: none">
						<label>
							id
						</label>
						<input type="text" id="id" name="id" value="${params['id']}" />
					</li>
				</ul>
				<ul>
				    <li  style="width:610px;">
				       <label style="width: 100px;">是否可继续登录:</label>
				       <c:choose>
				           <c:when test="${params['is_continue_login'] eq true}">
				           	<input type="radio" name="is_continue_login" id="is_continue_login" value="1" checked="checked"  />
						    <label for="is_continue_login"  style="width: 50px;">是</label>
						    <input type="radio" name="is_continue_login" id="is_continue_login1" value="0" />
						   <label for="is_continue_login1" style="width: 70px;">否</label>
				           </c:when>
				           <c:when test="${params['is_continue_login'] eq false}">
				           <input type="radio" name="is_continue_login" id="is_continue_login" value="1" />
						    <label for="is_continue_login"  style="width: 50px;">是</label>
						    <input type="radio" name="is_continue_login" id="is_continue_login1" value="0"  checked="checked" />
						   <label for="is_continue_login1" style="width: 70px;">否</label>
				           </c:when>
				           <c:otherwise>
				            <input type="radio" name="is_continue_login" id="is_continue_login" value="1" checked="checked"  />
						    <label for="is_continue_login"  style="width: 50px;">是</label>
				           	<input type="radio" name="is_continue_login" id="is_continue_login1" value="0" />
						   <label for="is_continue_login1" style="width: 70px;">否</label>
				           </c:otherwise>
				       </c:choose>
				    </li>
					<li style="width:610px;">
					    <c:choose>
					        <c:when test="${params['is_smallbox'] eq true }">
					         	<label style="width: 100px;">移小宝有效值:</label>
								<input type="radio" name="is_smallbox"  value="1"  checked="checked" >
								<label style="width: 50px;">有效</label>
					            <input type="radio" name="is_smallbox" value="0">
					            <label style="width: 50px;">无效</label>
					        </c:when>
					        <c:when test="${params['is_smallbox'] eq false }">
					         	<label style="width: 100px;">移小宝有效值:</label>
								<input type="radio" name="is_smallbox"  value="1" >
								<label style="width: 50px;">有效</label>
					            <input type="radio" name="is_smallbox" value="0"  checked="checked" >
					            <label style="width: 50px;">无效</label>
					        </c:when>
					        <c:otherwise>
					            <label style="width: 100px;">移小宝有效值:</label>
								<input type="radio" name="is_smallbox"  value="1" >
								<label style="width: 50px;">有效</label>
					            <input type="radio" name="is_smallbox" value="0"   checked="checked" >
					            <label style="width: 50px;">无效</label>
					        </c:otherwise>
					    </c:choose>
					</li>
					<li style="width:610px;">
					    <c:choose>
					         <c:when test="${params['is_dot'] eq true }">
					             	<label style="width: 100px;">点付宝有效值:</label>
									<input type="radio" name="is_dot"  value="1"  checked="checked" >
									<label style="width: 50px;">有效</label>
						            <input type="radio" name="is_dot" value="0">
						            <label style="width: 50px;">无效</label>
					         </c:when>
					         <c:when test="${params['is_dot'] eq false}">
					             	<label style="width: 100px;">点付宝有效值:</label>
									<input type="radio" name="is_dot"  value="1" >
									<label style="width: 50px;">有效</label>
						            <input type="radio" name="is_dot" value="0"  checked="checked" >
						            <label style="width: 50px;">无效</label>
					         </c:when>
					         <c:otherwise>
					                <label style="width: 100px;">点付宝有效值:</label>
									<input type="radio" name="is_dot"  value="1" >
									<label style="width: 50px;">有效</label>
						            <input type="radio" name="is_dot" value="0"  checked="checked" >
						            <label style="width: 50px;">无效</label>
					         </c:otherwise>
					    </c:choose>    
					</li>
					<li style="width:610px;">
					    <c:choose>
					          <c:when test="${params['is_shang_bao'] eq true}">
					                <label style="width: 100px;">商宝有效值:</label>
									<input type="radio" name="is_shang_bao"  value="1"  checked="checked" >
									<label style="width: 50px;">有效</label>
						            <input type="radio" name="is_shang_bao" value="0">
						            <label style="width: 50px;">无效</label>
					          </c:when>
					           <c:when test="${params['is_shang_bao'] eq false}">
					                <label style="width: 100px;">商宝有效值:</label>
									<input type="radio" name="is_shang_bao"  value="1" >
									<label style="width: 50px;">有效</label>
						            <input type="radio" name="is_shang_bao" value="0"  checked="checked" >
						            <label style="width: 50px;">无效</label>
					          </c:when>
					          <c:otherwise>
					                <label style="width: 100px;">商宝有效值:</label>
									<input type="radio" name="is_shang_bao"  value="1">
									<label style="width: 50px;">有效</label>
						            <input type="radio" name="is_shang_bao" value="0"    checked="checked" >
						            <label style="width: 50px;">无效</label>
					          </c:otherwise>
					    </c:choose>
					</li>
				   <li style="width: 320px; height: 110px;">
						<label>
							消息内容：
						</label>
						<textarea rows="4" cols="71" name="msg" id="msg"
							class="required">${params['msg']}</textarea>
						<label class="must">
							*
						</label>
					</li>
				</ul>
				<div class="search_btn clear">
					<input class="button blue " type="button" id="addButton" value="保存"/>
					<input name="reset" class="button blue " type="button" onclick="window.location.href='${ctx}/clientMessage/clientMessageQuery'" value="返回"/>
				</div>
			</div>
		</form:form>
	</div>
</body>
