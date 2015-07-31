<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	var root='${ctx}';
	$(function() {
		$("#merchant_info").on(
				"keydown",
				"input[name=merchantName]",
				function(event) {
					var div = $(this).next();
					var curr = div.find(".curr");
					curr = curr.length == 0 ? div.index(0) : curr;
					if (event.keyCode == 40) {
						if (curr.length > 0) {
							var next = curr.next();
							curr.removeClass("curr");
							if (next.length > 0) {
								next.addClass("curr");
							} else {
								div.children().eq(0).addClass("curr");
								div.scrollTop(0);
							}
						} else {
							div.children().eq(0).addClass("curr");
						}
					} else if (event.keyCode == 38) {
						if (curr.length > 0) {
							var prev = curr.prev();
							curr.removeClass("curr");
							if (prev.length > 0) {
								prev.addClass("curr");
							} else {
								div.children().last().addClass("curr");
							}
						} else {
							div.children().last().addClass("curr");
						}
					}
					if (event.keyCode == 38 || event.keyCode == 40) {
						event.preventDefault();
						curr = div.find(".curr");
						var divHeight = div.height();
						console.log(divHeight);
						var offsetTop = curr.get(0).offsetTop;
						var scrollTop = div.get(0).scrollTop;
						if (offsetTop<scrollTop||offsetTop+curr.height()>scrollTop
								+ divHeight) {
							div.animate({
								"scrollTop" : offsetTop
							},100);
						}
					} else if (event.keyCode == 13) {
						$(this).val(curr.text()).prop("title",curr.text());
						$(this).parents("form").find("input[name=merchantId]")
								.val(curr.attr("id"));
						div.fadeOut(300);
					}
				});
		$("#merchant_info").on("click", ".autoComplete div", function() {
			var form = $(this).parents("form");
			var curr = $(this);
			form.find("input[name=merchantId]").val(curr.attr("id"));
			form.find("input[name=merchantName]").val(curr.text()).prop("title",curr.text());
			curr.parent().fadeOut(300);
		});
		$("#merchant_info").on("input","input[name=merchantName]",function(){
			var _self=this;
			var div=$(_self).next();
			div.empty();
			if(!this.value){
				div.hide();
				return;
			}
			$.post(root+"/merchantSdkApi/searchMerchant4SdkApi","kw="+this.value,function(data){
				if(data.length>0){
					for(var i in data){
						div.append("<div id='"+data[i].id+"'>"+data[i].merchant_name+"</div>");
					}
					div.show();
				}else{
					div.hide();
					$(this).prev().val("");
				}
			});
		});
		//校验
		 $("#addButton").on("click",function(){
			var form=$("form");
			var selected=$(".autoComplete").css("display")=="none";
			selected=selected&&form.find("#merchantId").val()!="";
			
			if(!selected){
				showInfo("商户名称不正确");
			}else if($("#ips").val()==""){
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
					$(this).off("click");
					$.post(root+"/merchantSdkApi/merchantSdkApiAdd",data,function(data){
						if(data.success){
							showInfo("添加成功",function(){
								location.href=root+"/merchantSdkApi/merchantSdkApiQuery";
							},"success.gif");
						}else{
							showInfo("添加失败");
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
			<img class="left" src="${ctx}/images/home.gif" />当前位置：系统管理&gt;商户sdk接口新增
		</div>
		<form id="form" action="asdfsf" method="get">
			<div class="item">
				<div class="title">基本信息</div>

				<ul class="list" id="merchant_info">
					<li style="position: relative;">
						<label>商户名称：</label>
						<input type="hidden" name="merchantId" id="merchantId"/> 
						<input type="text" id="merchantName" name="merchantName" data-required="true" data-error="商户姓名必须输入"/>
						<div style="position: absolute; background-color:#fff;border:solid 1px #000; width: 80%; height: 300px; display:none; overflow-y: auto; left: 114px; top: 27px;"
							class="autoComplete">
							<div>no data</div>
						</div>
						<label class="must">*</label>
					</li>
					<li>
						<label>允许的IP地址：</label>
						<input type="text" id="ips" name="ips" data-required="true" data-error="IP地址必须输入"/>
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
						<input type="text" id="linkmanName" name="linkmanName" data-required="true" data-error="联系人姓名必须输入"/>
						
					</li>
					<li>
						<label>联系人电话：</label>
						<input type="text" id="linkmanPhone" name="linkmanPhone" data-required="true" data-error="联系人电话必须输入"/>
						
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