<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<style type="text/css">
#merUpdate {
	padding: 10px;
}

#merUpdate ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 250px;
	height: 32px;
	line-height: 32px;
}

#merUpdate ul li.column2 {
	width: 500px;
}

#merUpdate ul li.column3 {
	width: 750px;
}

#merUpdate ul li select {
	width: 128px;
}

#merUpdate ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#merUpdate ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#merUpdate ul li label.longLabel {
	width: 170px;
}

#merUpdate ul li .area {
	width: 75px;
}

#merUpdate ul li.long {
	width: 440px;
}

#merUpdate div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#attachment_fileUploader {
	vertical-align: middle;
	margin-left: 10px;
}
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：系统管理&gt;商户接口详情
		</div>
		<div class="item" id="merUpdate">
			<div class="title">基本信息</div>
			<ul>
				<li>
					<label>商户名称：</label>
					${m.merchant_name}
				</li>
				<li>
					<label>允许的IP：</label>
					${m.allow_ip}
				</li>

				<li>
					<label>商户编号：</label>
					${m.merchant_no}
				</li>
				<li>
					<label>联系人姓名：</label>
					${m.linkman_name}
				</li>
				<li>
					<label>联系人电话：</label>
					${m.linkman_phone}
				</li>
			</ul>

			<div class="clear"></div>
			<br />
			<div class="title">商户RSA加密算法公钥</div>
			<ul style="height: 200px;">
				<li>
					${m.public_key_base64}
				</li>
			</ul>
			<div class="title">商户RSA加密算法公钥modulus Hex</div>
			<ul style="height: 70px;word-break:break-all;">
				<li>
					${modulus}
				</li>
			</ul>
			<div class="clear"></div>
			</div>
		</div>
</body>
