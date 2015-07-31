<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	var root='${ctx}';
	$(function() {
		//校验
		 $("#addButton").click(function(){
			if($("#ips").val()==""){
				showInfo("IP必须输入");
			}else{
				var reg=/^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/;
				var ips=$("#ips").val();
				var ipArray=ips.split(",");
				var pass=true;
				for(var i in ipArray){
					if(!reg.test(ipArray[i])){
						showInfo("输入的ip地址有误！");
						pass=false;
						break;
					}
				}
				if(pass){
					var data=$("form").serialize();
					$.post(root+"/merchantApi/updateMerchantApi",data,function(data){
						if(data.success){
							showInfo("修改成功",function(){
								location.href=root+"/merchantApi/merchantApiQuery";
							},"success.gif");
						}else{
							showInfo("修改失败");
						}
					});
				}
			}
		});
		
	});
	function showInfo(msg,ok,icon){
		return  $.dialog({
			title : '提示',
			lock : true,
			content : msg,
			icon : icon||'error.gif',
			ok : ok
		});
	}
</script>
<style type="text/css">
.list li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 350px;
	heigth: 32px;
	line-height: 32px;
}

.list li.column2 {
	width: 540px;
}

.list li.column3 {
	width: 810px;
}

.list li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 110px;
}

.list li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

.list ul li .area {
	width: 75px;
}

.list ul li.long {
	width: 440px;
}

.list div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#file_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

#mlogo_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

.autoComplete div {
	color: #000;
	padding-left: 3px;
}

.autoComplete div:hover,.autoComplete div.curr {
	background: #DFE9F0;
	cursor: pointer;
}
</style>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：系统管理&gt;商户接口修改
		</div>
		<form id="form" action="asdfsf" method="get">
			<div class="item">
				<div class="title">基本信息</div>

				<ul class="list" id="merchant_info">
					<li style="position: relative;">
						<label>商户名称：</label>
						<%--借用bean的 merchantId属性存放merchant_api的id--%>
						<input type="hidden" name="merchantId" id="merchantId" value="${m.id}"/> 
						<input type="text" id="merchantName" name="merchantName" value="${m.merchant_name}" disabled="disabled"/>
						<label class="must">*</label>
					</li>
					<li>
						<label>允许的IP地址：</label>
						<input type="text" id="ips" name="ips" value="${m.allow_ip}"/>
						<label class="must">*</label>
						多个用,隔开
					</li>
				</ul>
			</div>
			<div class="clear"></div>
			<div class="item">
				<div class="title">技术联系人</div>
				<ul class="list">
					<li>
						<label>联系人姓名：</label>
						<input type="text" id="linkmanName" name="linkmanName" value="${m.linkman_name}"/>
						
					</li>
					<li>
						<label>联系人电话：</label>
						<input type="text" id="linkmanPhone" name="linkmanPhone" value="${m.linkman_phone}"/>
						
					</li>
				</ul>

			</div>
			<div class="clear"></div>
			<div class="search_btn">
				<input class="button blue" type="button" id="addButton" value="保存">
			</div>
		</form>
	</div>
</body>
</html>