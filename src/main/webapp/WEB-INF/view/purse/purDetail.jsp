<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">用户信息 </div>
	<ul>
		  <li style="width:310px;" id="mobile_no"><span style="width:60px;">手机号：</span>
			 ${params['mobile_no']}
		 </li>
		  <li style="width:310px;" id="real_name"><span style="width:70px;">真实姓名：</span>
		 	  ${params['real_name']}
		 </li>

		 <li style="width:310px;" id="balance"><span style="width:50px;">余额：</span>	
		 	 ${params['balance']}
		 </li>		
		 <li style="width:310px;" id="settle_account_no"><span style="width:70px;">结算帐号：</span>
		     ${params['settle_account_no']}
		 </li>
		 <li style="width:310px;" id="settle_account_name"><span style="width:80px;">结算帐户名：</span>
		     ${params['settle_account_name']}
		 </li>
		 <li style="width:310px;" id="fee"><span style="width:50px;">手续费：</span>
		     ${params['fee']}
		 </li>
		 <li style="width:310px;" id="idcard"><span style="width:70px;">身份证号：</span>
		     ${params['idcard']}
		 </li>
		 <li style="width:310px;" id="create_time"><span style="width:70px;">创建时间：</span>
		   <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
		 <li style="width:310px;" id="last_use_time"><span style="width:100px;">最后使用时间：</span>
		  <fmt:formatDate value="${params['last_use_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
		 <li style="width:610px;" id="last_login_time"><span style="width:120px;">最后一次登录时间：</span>
		 <fmt:formatDate value="${params['last_login_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
		 <li style="width:310px;" id="login_key"><span style="width:70px;">登录密钥：</span>
		     ${params['login_key']}
		 </li>
    </ul>
    <ul>
	     <li style="width:610px;" id="password"><span style="width:70px">密码：</span>
		     ${params['password']}
		 </li>
		 <li style="width:610px;" id="pay_password"><span style="width:70px">支付密码：</span>
		     ${params['pay_password']}
		 </li>
	</ul>
		  <div class="clear"></div>
</div>
</body>
</html>