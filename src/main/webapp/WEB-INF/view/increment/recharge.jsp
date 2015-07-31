<%@page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta name="format-detection" content="telephone=no" />
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script type="text/javascript">
	function btn_click(selector,start,end){
		$(selector).on("touchstart",start);
		$(selector).on("touchend",end);
	}
	$(function() {
		$(".selectValueGroup div").on("touchstart",function() {
			$("div.active").removeClass("active");
			$(this).addClass("active");
			$("#selectValue").val($(this).attr("data-value"));
		});
		btn_click("#btn_send",function(){
			$(this).data("oldColor",$(this).css("backgroundColor"));
			$(this).css("backgroundColor","green");
		},function(){
			$(this).css("backgroundColor",$(this).data("oldColor"))
		});
		
	});
</script>
<head>

<script>
	function frm_submit() {
		mobileNo = $('#mobileNo').val();
		if(mobileNo=='请输入要充值的手机号码'){
			alert('请输入要充值的手机号码');
			return;
		}
		var re = /^1[0-9]{10}$/;
		if(!re.test(mobileNo)){
			alert('手机号码有误');
			$('#mobileNo').select();
			return;
		}
		par=$("#selectValue").val();
		if(par==''){
			alert("请选择要充值的金额");return;
			
		}
		$('#f').submit();
		
	}
</script>


<style type="text/css">
html,body,h1,h2,p,span,a,div,ol,ul,li,table,tbody,tfoot,thead,tr,th,td,canvas
	{
	margin: 0;
	padding: 0
}

body {
	text-align: center;
	font-size-adjust: none;
	-webkit-text-size-adjust: none;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 19px;
	border-radus: 0;
	-webkit-border-radius: 0;
}

ol,ul {
	list-style: none
}

.div_btn {
	margin-top: 10px;
	padding-bottom: 10px;
}

a {
	text-decoration: none;
	color: #545164
}

.n {
	float: left;
	font-size: 14px;
}

.ipt {
	font-size: 14px;
}

.t li {
	overflow: hidden;
	height: 44px;
	margin: 5px 0;
	line-height: 44px;
}

input[type="submit"],input[type="reset"],input[type="button"],button {
	-webkit-appearance: none;
	border-radius: 0;
}

.ipt input {
	border: 1px solid #565656;
	height: 40px;
	padding: 0px 5px;
	-webkit-appearance: none;
	-webkit-box-sizing: border-box;
	border-radius: 0;
}


.div_btn input,.ipt input {
	width: 95%;
	height: 40px;
}

/* button 
---------------------------------------------- */
.button {
	display: inline-block;
	zoom: 1; /* zoom and *display = ie7 hack for display:inline-block */
	*display: inline;
	vertical-align: baseline;
	margin: 0 2px;
	outline: none;
	cursor: pointer;
	text-align: center;
	text-decoration: none;
	font: 14px/100% Arial, Helvetica, sans-serif;
	padding: .5em 2em .55em;
	text-shadow: 0 1px 1px rgba(0, 0, 0, .3);
	-webkit-border-radius: .5em;
	-moz-border-radius: .5em;
	border-radius: .2em;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .2);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .2);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .2);
	-webkit-tap-highlight-color:rgba(0,0,0,0);
}

.button:hover {
	text-decoration: none;
}

.button:active {
	position: relative;
	top: 1px;
}

.bigrounded {
	-webkit-border-radius: 2em;
	-moz-border-radius: 2em;
	border-radius: 2em;
}

.medium {
	font-size: 12px;
	padding: .4em 1.5em .42em;
}

.small {
	font-size: 11px;
	padding: .2em 1em .275em;
}

input,button,select,textarea {
	outline: none;
}
/* color styles 
- 
/* blue */
.blue {
	color: #d9eef7;
	border: solid 1px #0076a3;
	background: #0095cd;
}


.blue:active {
	color: #80bed6;
}

input {
	font-size: 14px;
	outline: none;
	-webkit-appearance: none;
	-webkit-box-sizing: border-box;
	border-radius: 0;
}

.ipt input {
	border: 1px solid #ccc;
	height: 35px;
	padding: 0px 5px;
	outline: none;
	-webkit-box-flex: 1;
	-webkit-box-sizing: border-box;
	letter-spacing: normal;
	box-flex: 1;
	border-radius: 0;
	-webkit-border-radius: 0;
	background-color: transparent;
	-webkit-tap-highlight-color: rgba(255, 0, 0, 0);
	-webkit-appearance: none;
	-webkit-box-sizing: border-box;
}

.selectValueGroup .active {
	background-color:green;
}
.selectOption {
	height: 35px;
	line-height:35px;
	width:30%;
	background: #f6f6f6;
	border:1px solid #d8d8d8;
	margin-right: 3px;
	-webkit-appearance: none;
	-webkit-box-sizing: border-box;
	border-radius: 0;
	-webkit-tap-highlight-color:rgba(0,0,0,0);
	
}
.left{float: left}
.right{float:right}
.ml5{margin-left: 5%}
.selectValueGroup {
	width: 95%;
	margin: 0 auto;
}
</style>
</head>
<body>
	<form action="rechargeSave" id="f" method="post" style="margin-top: 50px">
		<input name="data" type="hidden" id="dd" />
		<!-- 选择的金额 -->
		<input name="selectValue" type="hidden" id="selectValue" value="50"/>
		<input name="merchantNo" type="hidden" value="111130073750001" />
		<ul class="t">

			<li>
				<div class="ipt">
					<input type="text" name="mobileNo" id="mobileNo" value="请输入要充值的手机号码" onfocus="if(this.value=='请输入要充值的手机号码'){this.value=''}"
						onblur="if(this.value==''){this.value='请输入要充值的手机号码'}" />
				</div>
			</li>
			<li>
				<div class="ipt selectValueGroup">
					<div class="selectOption left" data-value="30">30元</div>
					<div class="selectOption left ml5" data-value="50">50元</div>
					<div class="selectOption right" data-value="100">100元</div>
					<div style="clear: both;"></div>
				</div>

			</li>
		</ul>
		<div class="div_btn">
			<input type="button" value="确认" class="button blue big" style="height: 40px" onclick="frm_submit();" id="btn_send" />
		</div>
	</form>
</body>
</html>