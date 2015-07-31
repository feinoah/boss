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
	margin-top: 40px;
	padding-bottom:40px;
}
a {
	text-decoration: none;
	color: #545164
}

.t {
	width: 80%;
	margin: 0 auto;
}

.n {
	float: left;
	font-size: 14px;
}

.ipt {
	float: right;
	font-size: 14px;
}

.t li {
	overflow: hidden;
	height: 40px;
	margin: 5px 0;
	line-height: 40px;
}
input[type="submit"],
input[type="reset"],
input[type="button"],
button {
-webkit-appearance: none;
border-radius: 0;
}
.ipt input {
	border: 1px solid #565656;
	height: 30px;
	padding: 0px 5px;
	-webkit-appearance:none;
	-webkit-box-sizing:border-box;
	border-radius: 0;
}

.logo img {
	width: 80%;
}

.div_btn {
	margin-top: 40px;
}

.div_btn input {
	width: 80%;
	height: 30px;
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
	text-shadow: 0 1px 1px rgba(0,0,0,.3);
	-webkit-border-radius: .5em; 
	-moz-border-radius: .5em;
	border-radius: .2em;
	-webkit-box-shadow: 0 1px 2px rgba(0,0,0,.2);
	-moz-box-shadow: 0 1px 2px rgba(0,0,0,.2);
	box-shadow: 0 1px 2px rgba(0,0,0,.2);
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
input,button,select,textarea{outline:none;} 
/* color styles 
- 
/* blue */
.blue {
	color: #d9eef7;
	border: solid 1px #0076a3;
	background: #0095cd;
	background: -webkit-gradient(linear, left top, left bottom, from(#00adee), to(#0078a5));
	background: -moz-linear-gradient(top,  #00adee,  #0078a5);
	filter:  progid:DXImageTransform.Microsoft.gradient(startColorstr='#00adee', endColorstr='#0078a5');
}
.blue:hover {
	background: #007ead;
	background: -webkit-gradient(linear, left top, left bottom, from(#0095cc), to(#00678e));
	background: -moz-linear-gradient(top,  #0095cc,  #00678e);
	filter:  progid:DXImageTransform.Microsoft.gradient(startColorstr='#0095cc', endColorstr='#00678e');
}
.blue:active {
	color: #80bed6;
	background: -webkit-gradient(linear, left top, left bottom, from(#0078a5), to(#00adee));
	background: -moz-linear-gradient(top,  #0078a5,  #00adee);
	filter:  progid:DXImageTransform.Microsoft.gradient(startColorstr='#0078a5', endColorstr='#00adee');
}
input{
	font-size: 14px;
}

.ipt input {
	border: 1px solid #ccc;
	height: 30px;
	padding: 0px 5px;
	outline: none;
	-webkit-box-flex:1;
	-webkit-box-sizing:border-box;
	letter-spacing:normal;
	box-flex: 1;
	border-radius: 0;
	-webkit-border-radius: 0;
	background-color: transparent;
	-webkit-tap-highlight-color: rgba(255,0,0,0);
	-webkit-appearance: none;
	-webkit-box-sizing: border-box;
}
</style>
</head>
<body>
		<ul class="t">
			<li>
			<div class="n">充值号码：</div>
			<div class="ipt">${confirm.mobileNo }</div>
			
			</li>
			<li>
				<div class="n">归属地：</div>
				<div class="ipt">${confirm.province }</div>
			</li>
			<li>
				<div class="n">运营商：</div>
				<div class="ipt">${confirm.cat }</div>
			</li>
			<li>
			<div class="n">充值金额：</div>
			<div class="ipt">${confirm.selectValue }元</div>
			
			</li>
			<li>
			<div class="n">支付金额：</div>
			<div class="ipt">${confirm.selectValue }元</div>
			</li>
			 
		</ul>
		
		<div class="div_btn">
			<a href="orderNo=${confirm.orderNo}"><input type="button" value="开始支付" class="button blue big" style="height: 40px" onclick="frm_submit();" id="btn_send" /></a>
		</div>
</body>
</html>