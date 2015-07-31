<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script>
 function extractionCheck(){
    var api = frameElement.api, W = api.opener;
    if(window.confirm("确定审核?")){
			$.get('${ctx}/purse/check?id=${ids}',function(data){
					alert(data);
					api.close();
				});
        }	
   }  
</script>
</head>
<body>
	<ul style="padding:20px;font-size:13px">
	<li >总数量：${c} 人</li>
	</ul>
	<input onclick="javascript:extractionCheck();" style="margin-left:20px"   class="button rosy medium" type="submit" id="submit"  value="确定审核"/>
</body>
</html>