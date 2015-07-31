<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#systemUserAdd
		{
			padding:10px;
		}
	
		#systemUserAdd ul
		{
			overflow:hidden;
		} 
		
		#systemUserAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#systemUserAdd ul li.column2
		{
			width:540px;
		}
		
		#systemUserAdd ul li.column3
		{
			width:810px;
		}
		
		#systemUserAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#systemUserAdd ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#systemUserAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#systemUserAdd ul li.long
		{
			width:440px;
		}
		
		
		#systemUserAdd div.subject
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
				var dialog = $.dialog({title: '成功',lock:true,content: '新增用户信息'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新用户信息'+''+'成功',icon: 'success.gif',ok: function(){
			        	$("form input:text").first().focus();
				    	}
					});
			}else if(flag == "3")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: '用户名已经存在',icon: 'error.gif',ok: function(){
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

				
				$.each($("input:text,input:password,select,textarea"),function(i,n){
    
     
     
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
					
     
       //密码长度不能小余6位,且必须包含数字字母。-----start-
     if($(n).hasClass("password6"))
        {
         var number = $.trim($(n).val());
         var reg=new RegExp(/[A-Za-z].*[0-9]|[0-9].*[A-Za-z]/);
        // alert((number.length+'---'+!reg.test(number))); 
         if(number.length > 0 ){
                if(number.length <6 ||(!reg.test(number)))
               {
                var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'密码长度不能小余6位,且必须包含数字字母',icon: 'error.gif',ok: function(){
                  $(n).focus();
                     }
                 });
                 isSubmit = false;
                 return false;
               }
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
          var pwd1=$.trim($("#password").val())
          $("#passwordmd5").val($.md5(pwd1));
          $("#password").val($.md5(pwd1));
					$("#systemUserAdd").submit();
				}
			});	
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>系统用户新增</div>
    <form:form id="systemUserAdd" action="${ctx}/acq/systemUserSave" method="post">
    <div class="item">
    	<div class="title">用户信息</div>
    	<ul>
    		<li style="display:none"><label>id</label><input type="text" id="id"  name="id"  value="${params['id']}"/></li>
    	 <c:choose>
	         	<c:when test="${empty params['id']}">
				<li><label>用户名：</label><input type="text" id="user_name"  name="user_name"  value="${params['user_name']}" class="required" /><label class="must">*</label></li>
	         	</c:when>
	         	<c:otherwise>
			<li><label>用户名：</label><input type="text" id="user_name"  name="user_name"  value="${params['user_name']}" class="required" readonly="readonly" disabled="disabled"/><label class="must">*</label></li>
	         	</c:otherwise>
	      </c:choose>
    		<li><label>EMAIL：</label><input type="text" id="email"  name="email"  value="${params['email']}" class="required email" /><label class="must">*</label></li>
			<li><label>真实姓名：</label><input type="text" id="real_name"  name="real_name"   value="${params['real_name']}" class="required" /><label class="must">*</label></li>
			<li><label>状态：</label>
			<select id="status"  name="status" style="padding:2px"  class="required" >
	         	<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>启用</option>
	         	<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>停用</option>
	         </select><label class="must">*</label></li>
	         <c:choose>
	         	<c:when test="${empty params['id']}">
					<li><label>密码：</label><input type="password" id="password"  name="password"  value="${params['password']}" class="required password6" />
                                  <input type="hidden" id="passwordmd5"  name="passwordmd5"    />
     <label class="must">*</label></li>
	         	</c:when>
	         </c:choose>
    	</ul>
    	<div class="title clear">用户所属组</div>
    	<ul>
    		<c:forEach items="${list}" var="item" varStatus="status">
	    		<li>
	    			<c:choose>
	    				<c:when test="${item.checked eq 'show'}">
				   			<input type="radio" name="userGroup" value="${item.id}" checked/><label>${item.group_name }</label>
				   		</c:when>
				   		<c:otherwise>
				   			<input type="radio" name="userGroup" value="${item.id}"/><label>${item.group_name }</label>
				   		</c:otherwise>
				   </c:choose>
				</li>
    		</c:forEach>
    	</ul>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"   value="保存"  />
    	</div>
    </div>
    </form:form>
   
  </div>
</body>
