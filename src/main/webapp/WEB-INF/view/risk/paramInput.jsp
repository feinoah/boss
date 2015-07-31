<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#paramInput
		{
			padding:10px;
		}
	
		#paramInput ul
		{
			overflow:hidden;
		} 
		
		#paramInput ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#paramInput ul li.column2
		{
			width:540px;
		}
		
		#paramInput ul li.column3
		{
			width:810px;
		}
		
		#paramInput ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#paramInput ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#paramInput ul li .area
		{
			width:75px;
		}
		
		
		
		#paramInput ul li.long
		{
			width:440px;
		}
		
		
		#paramInput div.subject
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
			
			 var flag = '${flag}';
			 if(flag == "1")
			{
				var dialog = $.dialog({title: '成功',lock:true,content: '新增参数'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新参数'+''+'成功',icon: 'success.gif',ok: function(){
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
					$("#paramInput").submit();
				}
			});	
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：风险管理>参数字段设置</div>
    <form:form id="paramInput" action="${ctx}/risk/paramSave" method="post">
    <div class="item">
    	<div class="title">参数字段设置</div>
    	<ul>
    		<li style="display:none"><label>id</label>
    		<input type="text" id="id"  name="id"  value="${params['id']}"/></li>
			<li style="width:600px;"><label style="width:30px;">编号</label>：
			<input type="text" id="parameter_no"  name="parameter_no"  value="${params['parameter_no']}" class="required" /><label class="must">*</label></li>
			<li style="width:600px;"><label  style="width:30px;">名称</label>：
			<input type="text" id="parameter_name"  name="parameter_name"  value="${params['parameter_name']}" class="required" /><label class="must">*</label></li>
    	</ul>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"   value="保存"  />
    		<input name="reset" class="button blue " type="button" onclick="window.location.href='${ctx}/risk/paramQuery'" value="返回"/>
    	</div>
    </div>
    </form:form>
   
  </div>
</body>
