<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script>
     //--------转移集群--------
     
function moveGroup(){
    var group_code=$("#group_code").val();
	if(group_code==""){
		alert("请选择需转移的集群!");
	}else{
		if(window.confirm("确定转移集群?")){
			var datas = document.getElementById("datas").innerHTML;
			var api = frameElement.api, W = api.opener;
			var group_code=$("#group_code").val();
			var url = '${ctx}/group/MoveGroup';
			$.post(url,{"data":datas,"group_code":group_code},
			function(data){
					if(data==1){
						alert('转移成功');
					}else{
						alert('转移失败，请检查数据');
					}
					api.close();
			});
		}
	}
} 

</script>
</head>
<body>
  <div id="datas" style="display: none;">${data}</div>
	<ul style="padding:20px;font-size:13px">
	<li >商户总数量：${total}</li>
	<li style="margin-top:3px"><span>集群名称：</span>
		<select id="group_code">
		    <option value="">请选择</option>
			<c:forEach  items="${grouplist}" var="group">
				<option value="${group.group_code}">
					${group.group_name }
				</option> 
			</c:forEach>
		</select>
	</li>
	</ul>
	
	<input onclick="javascript:moveGroup()" style="margin-left:20px"   class="button rosy medium" type="submit" id="submit"  value="确定转移"/>
</body>
</html>