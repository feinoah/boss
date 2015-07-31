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
	var freezeDay;
	var userAmount;
		
	
	
	$(function(){
		
		$("#freezebtn").click(function(){
			var mobileNo = $("#mobileNo").val();
			var appType=$("#appType").val();
			
			var c1 = validMoney();
			var c2 = validDay();
			var c3 = validReason();
			if(c1&&c2&&c3){
				userAmount = $("#userAmount").val();
				
				$.ajax({
					url:"${ctx}/purse/freezeBag",
					type:"post",
					data:{"mobileNo":mobileNo,"appType":appType,"userAmount":userAmount,"amount":amount,"freezeDay":freezeDay,"freezeReason":freezeReason},
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
	var flag = true;
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
	function validDay(){
	
			var flag = true;
			$(".dayMsg").html("");
		    freezeDay = $("#freezeDay").val();
			if(freezeDay == ""){
				$(".dayMsg").html("天数不能为空！");
				flag = false;
				return flag;
			}
			if(!freezeDay.match(INTEGER_REG)){
				$(".dayMsg").html("天数只能为正整数！");
				flag = false;
				return flag;
			}
			if(parseInt(freezeDay) > 5000){
				$(".dayMsg").html("天数在5000天以内");
				flag = false;
				return flag;
			}
			
			if(flag){
			
				var date = $("#date").val().substr(0,10).split("-");
				
				var odate = new Date();
				odate.setFullYear(parseInt(date[0]));
 				odate.setMonth(parseInt(date[1]) -1);
 				odate.setDate(parseInt(date[2]));
			    var ndate =  new Date(Date.parse(odate) + (86400000 * parseInt(freezeDay)));
				$(".dayMsg").html("至:" +ndate.getFullYear()+"年" + (ndate.getMonth() + 1)+"月"+ndate.getDate()+"日解冻");
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
		
		<li style="width:400px;"><span style="width:100px">交易滞留金:</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${ params.transAmount }" />元</li>
		<li style="width:300px;height:auto;"><span style="width:100px">风险冻结金:</span><input onblur="javascript:validMoney()" type="text" id="freezeAmount" name="freezeAmount" />&nbsp;元</li><li><span style="width:150px;color:red;" class="amountMsg"></span></li>
		<li style="width:300px;height:auto;"><span style="width:100px">冻结天数:</span><input onblur="javascript:validDay()" type="text" id="freezeDay" name="freezeDay"/>&nbsp;天</li><li><span style="width:150px;color:red;" class="dayMsg"></span></li>
		<li style="width:300px;height:auto;"><span style="width:100px">冻结原因:</span><textarea  onblur="javascript:validReason()" name="freezeReason" id="freezeReason" cols="20" rows="3"></textarea></li><li><span style="width:150px;color:red;" class="reasonMsg"></span></li>
		<li style="width:300px;height:auto;"><span style="width:100px">总冻结金额:</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${ params.totalAmount }" />元</li>
		<li style="width:300px;height:auto;"><span style="width:100px">最近操作人:</span>
			<span style="width:100px">${ params.operater }</span>
			<fmt:formatDate value="${params.create_time}" pattern="yyyy-MM-dd" type="both" />
		</li>
		<li style="width:300px;height:auto;"><span style="width:100px">&nbsp;</span><input class="button blue medium" id="freezebtn"  value="冻结" type="button"></li>
	</ul>
	
		  
</div>
</body>
</html>