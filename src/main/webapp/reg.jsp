<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<title>欢迎注册移联商宝</title>
<style type="text/css">
ul,li{
	list-style: none;
}
li{
	margin-bottom: 12px;
}
li a{
	text-decoration: none;
	color: blue;
}
</style>


<script type="text/javascript">
function reg(){
	 $.ajax({url:'${ctx}/bag/reg',data:{'mobile':$('#test').val(),'nick':$('#test1').val()},success:function(msg){
		 alert(msg);
	 }});
}
</script>

</head>
<body>
	<div style="padding:20px">
	<img src="${ctx }/images/logo1jpg.jpg" width="200"/>
	<ul>
	 	<li style="float:left"><input id="test1" value="请输入真实姓名" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue;this.style.color='#999'}"  style="color:#999999;border:1px solid #b1b1b1;font-size:14px; padding:4px;width:180px;margin-left:10px"/></li>
	 </ul>
	 <ul style="clear:both">
	 	<li style="float:left"><input id="test" value="请输入手机号码" onFocus="if(value==defaultValue){value='';this.style.color='#000'}" onBlur="if(!value){value=defaultValue;this.style.color='#999'}"  style="color:#999999;border:1px solid #b1b1b1;font-size:14px; padding:4px;width:180px;margin-left:10px"/></li>
	 </ul>
	 <div style="clear:both"></div>
	 <input style="margin-left:9px;width:180px" onclick="javascript:reg();"   class="button rosy big" type="submit"  id="submit"  value="即刻注册"/>
	 </div>
</body>
</html>