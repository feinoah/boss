<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#userAuthAdd
		{
			padding:10px;
		}
	
		#userAuthAdd ul
		{
			overflow:hidden;
		} 
		
		#userAuthAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#userAuthAdd ul li.column2
		{
			width:540px;
		}
		
		#userAuthAdd ul li.column3
		{
			width:810px;
		}
		
		#userAuthAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#userAuthAdd ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#userAuthAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#userAuthAdd ul li.long
		{
			width:440px;
		}
		
		
		#userAuthAdd div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
		
		#file_uploadUploader {
			vertical-align:middle;
			margin-left:10px;
		}
		#mlogo_uploadUploader{
			vertical-align:middle;
			margin-left:10px;
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
				var dialog = $.dialog({title: '成功',lock:true,content: '新增权限信息'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新权限信息'+''+'成功',icon: 'success.gif',ok: function(){
			        	$("form input:text").first().focus();
				    	}
					});
			}else if(flag == "3")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: '新增权限上级权限'+''+'不存',icon: 'error.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}else if(flag == "4")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: '权限代码已经存在',icon: 'error.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "0")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: "${errorMessage}",icon: 'error.gif',ok: function(){
					$("form input:text").first().focus();
			    	}
				});
			}

			$("#addButton").click(function(){
				var isSubmit = true;

				
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
					
					if($(n).hasClass("email")){
						var email = $.trim($(n).val());
						if(email.length > 0 )
						{
							if(!email.match(EMAIL))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：xxx@xx.xxx',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
					}
					
				    if($(n).hasClass("phone"))
					{
						var phone = $.trim($(n).val());
						if(phone.length > 0 )
						{
							if(!phone.match(TEL_REG) && !phone.match(MOBILE_REG))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：0755-12345678或13912346789',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
						
					}

				    if($(n).hasClass("money"))
					{
						var money = $.trim($(n).val());
						
						if(money.length > 0 )
						{
							
							if(  !money.match(INTEGER_REG) && !money.match(MONEY_REG))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：8.88或8',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
						
					}

					if($(n).hasClass("number"))
					{
						var number = $.trim($(n).val());
						if(number.length > 0 )
						{
							if(!$.isNumeric(number))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：8或8.88',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
					}

				    if($(n).hasClass("integer"))
					{
						var integer = $.trim($(n).val());
						if(integer.length > 0 )
						{
							if(!integer.match(INTEGER_REG))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：8',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
						
					}

				});

				if(isSubmit)
				{
					$("#userAuthAdd").submit();
				}
			});	
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>权限新增</div>
    <form:form id="userAuthAdd" action="${ctx}/auth/userAuthSave" method="post">
    <div class="item">
    	<div class="title">权限信息</div>
    	<ul>
    		<li style="display:none"><label>id</label>
    		<input type="text" id="id"  name="id"  value="${params['id']}"/></li>
			<li><label>权限名称：</label><input type="text" id="auth_name"  name="auth_name"  value="${params['auth_name']}" class="required" /><label class="must">*</label></li>
			<li><label>权限代码：</label><input type="text" id="auth_code"  name="auth_code"  value="${params['auth_code']}" class="required" /><label class="must">*</label></li>
			<li style="width:450px;"><label>上级权限：</label><select id="status"  name="parent_id" style="padding:2px"  class="required" >
	        	<c:forEach items="${authList}" var="authItem" varStatus="status">
	         		<option value="${authItem.id }" <c:out value="${params['parent_id'] eq authItem.id?'selected':'' }"/>>${authItem.auth_name }</option>
	         	</c:forEach>
	         </select>
	        <li>
	        <li>
	        	<label>权限类别：</label><select  id="category"  name="category_id" style="padding:2px"  class="required" >
	        		<option value="1" <c:out value="${params['category_id'] eq '1'?'selected':'' }"/>>菜单权限</option>
	        		<option value="2" <c:out value="${params['category_id'] eq '2'?'selected':'' }"/>>链接权限</option>
	        	</select>
	        </li>
    	</ul>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"   value="保存"  />
    	</div>
    </div>
    </form:form>
   
  </div>
</body>
