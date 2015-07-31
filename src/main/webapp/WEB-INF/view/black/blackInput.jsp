<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#blackAdd
		{
			padding:10px;
		}
	
		#blackAdd ul
		{
			overflow:hidden;
		} 
		
		#blackAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#blackAdd ul li.column2
		{
			width:540px;
		}
		
		#blackAdd ul li.column3
		{
			width:810px;
		}
		
		#blackAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#blackAdd ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#blackAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#blackAdd ul li.long
		{
			width:440px;
		}
		
		
		#blackAdd div.subject
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
				var dialog = $.dialog({title: '成功',lock:true,content: '新增黑名单'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新黑名单'+''+'成功',icon: 'success.gif',ok: function(){
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
					$("#blackAdd").submit();
				}
			});	
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>黑名单设置</div>
    <form:form id="blackAdd" action="${ctx}/black/blackSave" method="post">
    <div class="item">
    	<div class="title">黑名单信息</div>
    	<ul>
    		<li style="display:none"><label>id</label>
    		<input type="text" id="id"  name="id"  value="${params['id']}"/></li>
			<li><label>黑名单类型：</label>
			<select style="padding:2px;width:100px" name="black_type" class="required" id="black_type" >
	         		<option  value="">--请选择--</option>
	         		<option value="1" <c:out value="${params['black_type'] eq '1'?'selected':'' }"/>>卡号黑名单</option>
	         		<option value="2" <c:out value="${params['black_type'] eq '2'?'selected':'' }"/>>商户号黑名单</option>
	         		<option value="3" <c:out value="${params['black_type'] eq '3'?'selected':'' }"/>>身份证黑名单</option>
	         </select>
			<label class="must">*</label></li>
			<li><label>黑名单值：</label><input type="text" id="black_value"  name="black_value"  value="${params['black_value']}" class="required" /><label class="must">*</label></li>
			<li style="width:450px"><label>状态：</label>
			 <select  style="padding:2px;width:100px" name="status" id="status" class="required">
	         		<option  value="">--请选择--</option>
	         		<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>启用</option>
	         		<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>停用</option>
	         	</select>
			<label class="must">*</label></li>
			<li style="width:350px;height:110px;"><label>加入黑名单原因：</label>
			<textarea rows="3" cols="40" name="cause" id="cause" class="required">${params['cause']}</textarea>
	        <label class="must">*</label><li>
    	</ul>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"   value="保存"  />
    		<input name="reset" class="button blue " type="button" onclick="window.location.href='${ctx}/black/blackQuery'" value="返回"/>
    	</div>
    </div>
    </form:form>
   
  </div>
</body>
