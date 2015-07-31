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

<script type="text/javascript">
	var INTEGER_REG =  /((^[1-9][0-9]*))$/; //正整数
	var MONEY_REG = /^(([1-9]\d*)(\.\d{1,2})?)$|(0\.0?([1-9]\d?))$/; //金额正则表达式
	var transAmount;
	var amount;
	var freezeReason;
	var userAmount;
		
	
	
	$(function(){
		
		$("#freezebtn").click(function(){
			var mobileNo = $("#mobileNo").val();
			var appType=$("#appType").val();
			var c1 = validMoney();
			
			var c3 = validReason();
			if(c1&&c3){
			
				userAmount = $("#userAmount").val();
			
				$.ajax({
					url:"${ctx}/purse/unFreezeBag",
					type:"post",
					data:{"mobileNo":mobileNo,"appType":appType,"userAmount":userAmount,"amount":amount,"freezeReason":freezeReason},
					dataType:"text",
					success:function(d){
						
						alert(d);
						top.freezeDialog.close();
					},
					error:function(){
						alert("网络不太好 ，等下再来试试吧！")	
					}
				})
			}
		
		})
	})
	
	function validMoney(){
			var flag = true;
			amount = $("#freezeAmount").val();
			userAmount = $("#userAmount").val();
			$(".amountMsg").html("");
			if(amount == ""){
				$(".amountMsg").html("金额不能为空");
				flag = false;
				return flag;
			}
			if(!amount.match(MONEY_REG)){
				$(".amountMsg").html("金额必须为大于0的数字！");
				flag = false;
				return flag;
			}
			if(parseFloat(amount) - parseFloat(userAmount) > 0){
				$(".amountMsg").html("解冻金额要小于已冻结的金额！！");
				flag = false;
				return flag;
			}
			if(parseFloat(amount) == parseFloat(userAmount)){
					$(".day").hide();
					freezeDay = 0;
			}
			if(parseFloat(amount) < parseFloat(userAmount)){
				$(".day").show();
			}
		return flag;
	}
	
	function validReason(){
		var flag = true;
		$(".reasonMsg").html("");
		freezeReason = $("#freezeReason").val();
		if(freezeReason == ""){
			$(".reasonMsg").html("原因不能为空");
			flag = false;
			return flag;
		}
		return flag;	
	}
	
	

</script>
<div class="item liHeight">
	<input type="hidden" id="transAmount" value="${ params.transAmount }"/>
	<input type="hidden" id="userAmount" value="${ params.userAmount }"/>
	<input type="hidden" id="mobileNo" value="${ params.mobile_no }"/>
	<input type="hidden" id="appType" value="${ params.app_type }"/>
	<input type="hidden" id="date" value="${ params.curdate }"/>
	<ul>
		<li style="width:150px;"><span style="width:80px">交易滞留金:</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${ params.transAmount }" />元</li>
		<li style="width:150px;"><span style="width:80px">风险冻结金:</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${ params.userAmount }" />元</li>
		<li style="width:120px;"><span style="width:60px">冻结天数:</span>${ params.freeze_day }天</li>
		<li style="width:300px;height:auto;"><span style="width:80px">风险解冻金:</span><input onblur="javascript:validMoney()" style="width:173px" type="text" id="freezeAmount" name="freezeAmount" />&nbsp;元</li><li><span style="width:150px;color:red;" class="amountMsg"></span></li>
		<!-- <li class="day" style="width:300px;height:auto;"><span style="width:80px">冻结天数:</span><input onblur="javascript:validDay()" type="text" id="freezeDay" name="freezeDay"/>&nbsp;天</li><li class="day"><span style="width:150px;color:red;" class="dayMsg"></span></li> -->
		<li style="width:300px;height:auto;"><span style="width:80px">解冻原因:</span><textarea onblur="javascript:validReason()" name="freezeReason" id="freezeReason" cols="22" rows="3"></textarea></li><li><span style="width:150px;color:red;" class="reasonMsg"></span></li>
		<li style="width:300px;height:auto;"><span style="width:80px">总冻结金额:</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${ params.totalAmount }" />元</li>
		<li style="width:300px;height:auto;"><span style="width:80px">最近操作人:</span>
			<span style="width:100px">${ params.operater }</span>
			<fmt:formatDate value="${params.create_time}" pattern="yyyy-MM-dd" type="both" />
		</li>
		<li style="width:300px;height:auto;"><span style="width:100px">&nbsp;</span><input class="button blue medium" id="freezebtn" value="解冻" type="submit"></li>
	</ul>
	
		  
</div>
</body>
</html>