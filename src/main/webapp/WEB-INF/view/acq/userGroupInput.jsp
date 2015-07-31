<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#userGroupAdd
		{
			padding:10px;
		}
	
		#userGroupAdd ul
		{
			overflow:hidden;
		} 
		
		#userGroupAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#userGroupAdd ul li.column2
		{
			width:540px;
		}
		
		#userGroupAdd ul li.column3
		{
			width:810px;
		}
		
		#userGroupAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#userGroupAdd ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#userGroupAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#userGroupAdd ul li.long
		{
			width:440px;
		}
		
		
		#userGroupAdd div.subject
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
				var dialog = $.dialog({title: '成功',lock:true,content: '新增用户组信息'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新用户组信息'+''+'成功',icon: 'success.gif',ok: function(){
			        	$("form input:text").first().focus();
				    	}
					});
			}else if(flag == "3")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: '用户组名已经存在',icon: 'error.gif',ok: function(){
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

			 $("#allSelect").click(function(){
	 				$(".title").parent().find("input").each(function (i,n) {
	 					$(this).prop("checked",true);
	 				});
				});
			 
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
					var num=0;
					$("input[name='authGroup']").each(function (i,n) {
						if(!$(n).attr("disabled")){
							$(this).prop("checked")?num++:0;
						}						
					});
					if(num<=0){
						var dialog = $.dialog({title: '错误',lock:true,content: '请选择相应的权限',icon: 'error.gif',ok: function(){
						    }
						});
						return false;
					}
					$("#userGroupAdd").submit();
				}
			});	
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>用户组新增</div>
    <form:form id="userGroupAdd" action="${ctx}/acq/userGroupSave" method="post">
    <div class="item">
    	<div class="title">用户组信息</div>
    	<ul>
    		<li style="display:none"><label>id</label>
    		<input type="text" id="id"  name="id"  value="${params['id']}"/></li>
			<li><label>组名称：</label><input type="text" id="group_name"  name="group_name"  value="${params['group_name']}" class="required" /><label class="must">*</label></li>
			<li><label>说明：</label><input type="text" id="group_desc"  name="group_desc"  value="${params['group_desc']}" /></li>
    	</ul>
    	<div class="title clear">权限列表</div>
    	<c:forEach items="${list}" var="authItem" varStatus="status">
	    	<c:choose>
	    		<c:when test="${authItem.checked eq 'show'}">
			    	<div><input type="checkbox" name="authGroup" value="${authItem.id}" checked/>${authItem.parent_name}：<input class="button blue medium" type="button" name="selItem" value="反选"/></div> 
	    		</c:when>
	    		<c:otherwise>
		    		<div><input type="checkbox" name="authGroup" value="${authItem.id}" />${authItem.parent_name}：<input class="button blue medium" type="button" name="selItem"  value="反选"/></div>
	    		</c:otherwise>
	    	</c:choose>
	    	<ul> 
	    	 <c:forEach items="${authItem.nodeList}" var="item" varStatus="status">
				<li style="width:330px">
					<c:choose>
						<c:when test="${item.checked eq 'show'}">
						   <input type="checkbox" name="authGroup" value="${item.id}" checked/><label style="width:250px">${item.parent_name}</label>
						</c:when>
						<c:otherwise>
						 	<input type="checkbox" name="authGroup" value="${item.id}" /><label style="width:250px">${item.parent_name}</label>
						</c:otherwise>
					</c:choose>
				</li>
				<c:forEach items="${item.nodeList}" var="nodeItem" varStatus="status">
				 <li style="width:330px">
					<c:choose>
						<c:when test="${nodeItem.checked eq 'show'}">
						   <input type="checkbox" name="authGroup" value="${nodeItem.id}" checked/><label style="width:250px">${nodeItem.parent_name}</label>
						</c:when>
						<c:otherwise>
						 	<input type="checkbox" name="authGroup" value="${nodeItem.id}" /><label style="width:250px">${nodeItem.parent_name}</label>
						</c:otherwise>
					</c:choose>
				</li>
				</c:forEach>
			</c:forEach>
	    	</ul>
    	</c:forEach>
    	<script type="text/javascript">
			$("input[name='selItem']").click(function(){
				$(this).parent().next("ul").find("input").each(function (i,n) {
					 if(!$(n).attr("disabled"))
					 {
						 $(this).prop("checked",!$(this).prop("checked"));
				     }
					 
	           });
				$(this).parent().find("input").prop("checked",!$(this).parent().find("input").prop("checked"));
			});
		</script>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"   value="保存"  />
    		<input   class="button blue  " type="button" id="allSelect"   value="全选"  />
    	</div>
    </div>
    </form:form>
   
  </div>
</body>
