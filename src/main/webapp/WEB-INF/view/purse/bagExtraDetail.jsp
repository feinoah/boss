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
	<div class="title clear">钱包提现信息 </div>
	<ul>
		  <li style="width:310px;" id="mobile_no"><span style="width:100px;">手机号：</span>
			 ${params['mobile_no']}
		 </li>
		 <li style="width:310px;" id="amount"><span style="width:100px;">提现金额：</span>	
		 	 ${params['amount']}元
		 </li>	
		 <li style="width:310px;" id="settle_amount"><span style="width:100px;">结算金额：</span>	
		 	 ${params['settle_amount']}元
		 </li>		
		 <li style="width:310px;" id="account_no"><span style="width:100px;">帐号：</span>
		     ${params['account_no']}
		 </li>
		 <li style="width:310px;" id="account_name"><span style="width:100px;">帐户名：</span>
		     ${params['account_name']}
		 </li>
		 <li style="width:310px;" id="fee"><span style="width:100px;">手续费：</span>
		     ${params['fee']}元
		 </li>
		 <li style="width:310px;" id="settle_days"><span style="width:100px;">钱包类型：</span>
		     ${purseBalanceType}
		 </li>
		 <li style="width:310px;" id="cnaps"><span style="width:100px;">联行号：</span>
		     ${params['cnaps']}
		 </li>
		  <li style="width:310px;" id="bank_name"><span style="width:100px;">开户行：</span>
		     ${params['bank_name']}
		 </li>
		 <li style="width:310px;" id="open_status"><span style="width:100px;">审核状态：</span>
		  	${openStatusDesc}
		 </li>
		 <li style="width:620px;height:initial" id="cash_remark"><span style="width:100px;">提现信息：</span>
		     ${params['cash_remark']}
		 </li>
		 <c:if test="${params['open_status'] eq '2'}">
			 <li style="width:620px;height:initial" id="check_remark"><span style="width:100px;">审核失败原因：</span>
			     ${params['check_remark']}
			 </li>
		 </c:if>
		 <li style="width:310px;" id="check_person"><span style="width:100px;clear:both;">审核方式：</span>
			     ${params['check_person']}
		 </li>
		 <li style="width:310px;" id="check_person"><span style="width:100px;clear:both;">代付通道：</span>
			${cashChannelDesc}
		 </li>
	     <li style="width:310px;" id="check_time"><span style="width:100px;clear:both;">审核时间：</span>
			     ${params['check_time']}
		 </li>
		 <li style="width:310px;" id="settle_time"><span style="width:100px;">结算时间：</span>
		  <fmt:formatDate value="${params['settle_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
 		 <li style="width:310px;" id="create_time"><span style="width:100px;">提现时间：</span>
		   <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>    		
    </ul>
		  <div class="clear"></div>
</div>
</body>
</html>