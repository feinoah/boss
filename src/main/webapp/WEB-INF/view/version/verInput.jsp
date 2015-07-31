<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp"%>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
#versionAdd {
	padding: 10px;
}

#versionAdd ul {
	overflow: hidden;
}

#versionAdd ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 300px;
	heigth: 32px;
	line-height: 32px;
}

#versionAdd ul li.column2 {
	width: 540px;
}

#versionAdd ul li.column3 {
	width: 810px;
}

#versionAdd ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 110px;
}

#versionAdd ul li label.must {
	width: 5px;
	color: red;
}

#versionAdd ul li .area {
	width: 75px;
}

#versionAdd ul li.long {
	width: 440px;
}

#versionAdd div.subject {
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
			var TEL_REG = /^[0-9]{3,4}\-[0-9]{7,8}$/;
			var MOBILE_REG =  /^1[0-9]{10}$/;
			var EMAIL_REG = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

			var NUM_STR_REG = /^([0-9])+$/; //数字字符串

			var INTEGER_REG =  /^(0|([1-9][0-9]*))$/; //正整数
			var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
			
			var EMAIL = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;//email 正则表达式
			
			
			 var flag = '${flag}';
			 if(flag == "1")
			{
				   var dialog = $.dialog({title: '成功',lock:true,content: '新增版本控制信息'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新版本控制信息'+''+'成功',icon: 'success.gif',ok: function(){
			        	$("form input:text").first().focus();
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
				var version = $.trim($("#version").val());
				var platform = $.trim($("#platform").val());
				var id = $("#id").val();
				checkVersion(version,platform,id);
				
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
			
			
			function checkVersion(version,platform,id){
				 $.ajax({
			 			type:"post",
			 			url:"${ctx}/ver/verCheck",
			 			data:{"version":version,"platform":platform,"id":id},
			 			async:false,
			 			dataType: 'json',
					  	success: function(data){
					    	var ret = data.msg;
						  	if (ret == "exist"){
						  		$.dialog.alert("该版本号已使用");
						  		$("#version").focus();
						  		isSubmit =  false; 
						  		return false;
						  	}
						  	else{
						  		isSubmit = true;
						  	}
					   }
			 		}
			 	);
			 }	
				if(isSubmit)
				{
					$("#versionAdd").submit();
				}
			});	
		});
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：系统管理>版本控制设置
		</div>
		<form:form id="versionAdd" action="${ctx}/ver/verSave" method="post">
			<div class="item">
				<div class="title">
					版本信息
				</div>
				<ul>
					<li style="display: none">
						<label>
							id
						</label>
						<input type="text" id="id" name="id" value="${params['id']}" />
					</li>
					<li style="width: 320px">
						<label>
							操作系统：
						</label>
						<select style="padding: 2px; width: 150px" name="platform"
							id="platform" class="required">
							<option value="">
								--请选择--
							</option>
							<option value="1"
								<c:out value="${params['platform'] eq '1'?'selected':'' }"/>>
								iOS
							</option>
							<option value="0"
								<c:out value="${params['platform'] eq '0'?'selected':'' }"/>>
								android
							</option>
						</select>
						<label class="must">
							*
						</label>
					</li>
					<li>
						<label>
							版本号：
						</label>
						<input type="text"  id="version" name="version"
							value="${params['version']}" class="required" />
						<label class="must">
							*
						</label>
					</li>
					<li style="width: 320px">
						<label>
							下载标志：
						</label>
						<select  style="padding:2px;width:150px" name="down_flag" id="down_flag" class="required">
	         		    <option value="">--请选择--</option>
	         		    <option value="0" <c:out value="${params['down_flag'] eq '0'?'selected':'' }"/>>不需要</option>
	         		    <option value="1" <c:out value="${params['down_flag'] eq '1'?'selected':'' }"/>>需要更新</option>
	         		    <option value="2" <c:out value="${params['down_flag'] eq '2'?'selected':'' }"/>>需要强制下载</option>
	         	        </select>
						<label class="must">
							*
						</label>
					</li>					
					<li>
						<label>
							客户端类型：
						</label>
						<select  style="padding:2px;width:157px" name="app_type" id="app_type" class="required">
	         		    <option value="">--请选择--</option>
	         		    <option value="0" <c:out value="${params['app_type'] eq '0'?'selected':'' }"/>>银联商宝</option>
	         		    <option value="1" <c:out value="${params['app_type'] eq '1'?'selected':'' }"/>>移小宝</option>
	         		    <option value="2" <c:out value="${params['app_type'] eq '2'?'selected':'' }"/>>通付宝</option>
	         		    <option value="3" <c:out value="${params['app_type'] eq '3'?'selected':'' }"/>>中宽支付</option>
	         	        </select>
						<label class="must">
							*
						</label>
					</li>
					<li style="width: 100%">
						<label>
							APP导航：
						</label>
						<input type="text" size=73 id="app_logo" name="app_logo"
							value="${params['app_logo']}" class="required" />
						<label class="must">
							*
						</label>
					</li>
					<li style="width: 100%">
						<label>
							APP地址：
						</label>
						<input type="text" size=73 id="app_url" name="app_url"
							value="${params['app_url']}" class="required" />
						<label class="must">
							*
						</label>
					</li>
					<li style="width: 320px; height: 110px;">
						<label>
							版本描述：
						</label>
						<textarea rows="4" cols="71" name="ver_desc" id="ver_desc"
							class="required">${params['ver_desc']}</textarea>
						<label class="must">
							*
						</label>
					<li>
				</ul>
				<div class="search_btn clear">
					<input class="button blue " type="button" id="addButton" value="保存"/>
					<input name="reset" class="button blue " type="button"
						onclick="window.location.href='${ctx}/ver/verQuery'" value="返回"/>
				</div>
			</div>
		</form:form>
	</div>
</body>
