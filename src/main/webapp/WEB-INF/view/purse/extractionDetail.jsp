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
	<div class="title clear">提现信息 </div>
	<ul>
		  <li style="width:310px;" id="mobile_no"><span style="width:60px;">手机号：</span>
			 ${params['mobile_no']}
		 </li>
		 <li style="width:310px;" id="amount"><span style="width:70px;">金额：</span>	
		 	 ${params['amount']}
		 </li>		
		 <li style="width:310px;" id="account_no"><span style="width:60px;">帐号：</span>
		     ${params['account_no']}
		 </li>
		 <li style="width:310px;" id="account_name"><span style="width:80px;">帐户名：</span>
		     ${params['account_name']}
		 </li>
		 <li style="width:310px;" id="fee"><span style="width:50px;">手续费：</span>
		     ${params['fee']}
		 </li>
		 <li style="width:310px;" id="settle_time"><span style="width:70px;">结算时间：</span>
		  <fmt:formatDate value="${params['settle_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
 		 <li style="width:310px;" id="create_time"><span style="width:70px;">创建时间：</span>
		   <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>    		
    </ul>
		  <div class="clear"></div>
</div>
</body>
</html>