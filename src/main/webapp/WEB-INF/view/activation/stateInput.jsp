<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#activationStateSave
		{
			padding:10px;
		}
	
		#activationStateSave ul
		{
			overflow:hidden;
		} 
		
		#activationStateSave ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#activationStateSave ul li.column2
		{
			width:540px;
		}
		
		#activationStateSave ul li.column3
		{
			width:810px;
		}
		
		#activationStateSave ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#activationStateSave ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#activationStateSave ul li .area
		{
			width:75px;
		}
		
		
		
		#lactivationStateSave ul li.long
		{
			width:440px;
		}
		
		
		#activationStateSave div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
	</style>
	<script type="text/javascript">
		$(function(){
			$("#saveButton").click(function(){
				$("#activationStateSave").ajaxSubmit({
					dataType	: "json",
					type        : "POST",
					cache       : false,
					success		: function(data){
					var flag=data.flag;
						if(flag== '1'){
							var dialog = $.dialog({title: '成功',lock:true,content: '修改锁定状态信息'+''+'成功',icon: 'success.gif',ok: function(){
					             location.href='${ctx}/activation/activationQuery';
							}});
						}else{
							var dialog = $.dialog({title: '错误',lock:true,content: '修改锁定状态信息'+''+'失败',icon: 'error.gif',ok: function(){
					             location.href='${ctx}/activation/activationQuery';
							}});
						}
					}
				});
			});	
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：激活码管理>修改激活码状态</div>
    <form:form id="activationStateSave" action="${ctx}/activation/activationStateSave" method="post">
    <li style="display:none"><label>id</label><input type="text" id="id"  name="id"  value="${params['id']}"/></li>
    <div class="item">
    	<div class="title">激活码状态信息</div>
    	<ul>
    		<li style="display:none"><label>id</label><input type="text" id="id"  name="id"  value="${params['id']}"/></li>
			<li><label>激活码：</label><input type="text" id="keycode"  name="keycode"  value="${params['keycode']}" class="required" readonly="readonly" /></li>
			<li><label>状态：</label>
			<select  style="padding:2px;width:157px" name="state" id="state" class="required">
      		    <option value="0" <c:out value="${params['state'] eq '0'?'selected':'' }"/>>初始化</option>
      		    <option value="1" <c:out value="${params['state'] eq '1'?'selected':'' }"/>>激活</option>
      		    <option value="2" <c:out value="${params['state'] eq '2'?'selected':'' }"/>>锁定</option>
      		    <option value="3" <c:out value="${params['state'] eq '3'?'selected':'' }"/>>使用中</option>
	        </select>
			</li>
    	</ul>
    	<div class="search_btn clear">
    		<input   class="button blue"  type="button" id="saveButton"   value="保存"  />
    		<input name="reset" class="button blue" type="button" onclick="javascript:history.go(-1);" value="返回" />
    	</div>
    </div>
    </form:form>
  </div>
</body>
