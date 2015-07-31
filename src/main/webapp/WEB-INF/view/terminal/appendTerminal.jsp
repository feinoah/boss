<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script>
function bind(){
 var api = frameElement.api, W = api.opener;
 var count=$("#count").val();
	if(count==0){
		alert("请输入增机数量");
	}else{
		$.get('${ctx}/agent/appendTerminalSave?agentNo='+$("#ano").val()+'&type='+$("#type").val()+'&count='+count,function(data){
				if(data.msg==1){
					alert('增机成功');
					api.close();
				}else{
					alert('增机失败,请检查库存可用POS机数量');
					api.close();
				}
				
		});
} }
</script>
</head>
<body>
	<ul style="padding:20px;font-size:13px">
	<li style="margin-top:3px"><span>代理商名称：</span><u:select id="ano" disabled="true" value="${agentNo}" style="width:157px;padding:2px"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" /></li>
	<li style="margin-top:3px"><span  style="width:78px;display:block;float:left">机具类型：</span>
	<select name="type" id="type" style="width:157px;padding:2px">
		<option value="MPOS-78">MPOS-78</option>
		<option value="MPOS-38">MPOS-38</option>
	</select>
	
	</li>
	<li style="margin-top:3px;margin-right:3px"><span>增机数量：</span>
	<input style="margin-left:9px" id="count"/>
	</li>

	</ul>
	<input onclick="javascript:bind();" style="margin-left:20px"   class="button rosy medium" type="submit" id="submit"  value="提交"/>
</body>
</html>