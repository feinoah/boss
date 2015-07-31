<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#lockedSave
		{
			padding:10px;
		}
	
		#lockedSave ul
		{
			overflow:hidden;
		} 
		
		#lockedSave ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#lockedSave ul li.column2
		{
			width:540px;
		}
		
		#lockedSave ul li.column3
		{
			width:810px;
		}
		
		#lockedSave ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#lockedSave ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#lockedSave ul li .area
		{
			width:75px;
		}
		
		
		
		#lockedSave ul li.long
		{
			width:440px;
		}
		
		
		#lockedSave div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
	</style>
	<script type="text/javascript">
		$(function(){
			$("#saveButton").click(function(){
				$("#lockedSave").ajaxSubmit({
					dataType	: "json",
					type        : "POST",
					cache       : false,
					success		: function(data){
					var flag=data.flag;
						if(flag== '1'){
							var dialog = $.dialog({title: '成功',lock:true,content: '修改锁定状态信息'+''+'成功',icon: 'success.gif',ok: function(){
					             location.href='${ctx}/acq/terminalQuery';
							}});
						}else{
							var dialog = $.dialog({title: '错误',lock:true,content: '修改锁定状态信息'+''+'失败',icon: 'error.gif',ok: function(){
					             location.href='${ctx}/acq/terminalQuery';
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
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>收单机构终端</div>
    <form:form id="lockedSave" action="${ctx}/acq/acqLockedSave" method="post">
    <li style="display:none"><label>id</label><input type="text" id="acq_merchant_no"  name="acq_merchant_no"  value="${params['acq_merchant_no']}"/></li>
    <div class="item">
    	<div class="title">终端状态信息</div>
    	<ul>
    		<li style="display:none"><label>id</label><input type="text" id="id"  name="id"  value="${params['id']}"/></li>
			<li><label>收单机构商户名称：</label><input type="text" id="acq_merchant_name"  name="acq_merchant_name"  value="${params['acq_merchant_name']}" class="required" readonly="readonly" /></li>
			<li><label>锁定状态：</label>
			<select  style="padding:2px;width:157px" name="locked" id="locked" class="required">
      		    <option value="0" <c:out value="${params['locked'] eq '0'?'selected':'' }"/>>正常</option>
      		    <option value="1" <c:out value="${params['locked'] eq '1'?'selected':'' }"/>>锁定</option>
      		    <option value="2" <c:out value="${params['locked'] eq '2'?'selected':'' }"/>>废弃</option>
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
