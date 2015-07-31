<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
<script>
function close_dialog(){
	window.parent.closeSignReceipt_dialog();
}
function reviewSend(status){
	<%--status 1正常，空字符串、null、2待审核或者 ，3审核失败 --%>
	$.get("${ctx}/mer/receiptReview?id=${id}&status="+status,function(result){
		if(result.result){
			var down=$(".down");
			down.empty();
			if(status==1)
			down.append('<div class="pass">审核通过!</div>');
			else if(status==3)
			down.append('<div class="fail">审核失败!</div>');
		}else{
			alert('操作失败！');
		}		
	},"json");	
}

function ticketPreview(oper){
	if("${id}" == ""){
		alert("获取ID信息失败!");
		return false;
	}
	$.get("${ctx}/mer/addReceiptReviewLoad2/${id}",function(result){
		//alert(result.result);
		if(result.result){
			var down=$(".down");
			down.empty();
			if(result =1)
				down.append('<div class="pass">操作成功!</div>');
				else if(result<0)
				down.append('<div class="fail">操作失败!</div>');
				else if(result==0)
				down.append('<div class="fail">参数验证失败!</div>');
		}else{
			alert('操作失败！');
		}		
	},"json");	
	/*
	$.ajax({
			type:"post",
			url:"${ctx}/mer/addReceiptReviewLoad2",
			data:{"id":"${id}"},
			dataType: 'json',
	  	success: function(data){
	    	var ret = data.msg;
		  	if (ret == "OK"){
		  		successMsg1("已成功列入打印小票序列！");
		  		return false;
		  	}else if(ret == "exist"){
		  		successMsg1("小票已存在！");
		  		return false;
		  	}
	  }
		}
	);*/
	
}

</script>
<style>
.content {
	width: 300px;
	margin: 0px auto;
}

.top {
	margin-top: 25px;
}

.down {
	text-align: center;
	margin-top: 25px;
}
.pass{
	font-size:14px;
	color:#00BB00;
	font-weight: bold;
}
.fail{
	font-size:14px;
	color:#FF0000;
	font-weight: bold;
}
body{
text-align: center;
}
.double_line{
	border-top:1px dashed #000;
	border-bottom:1px dashed #000;
	height: 5px!important;
}
</style>
</head>
<body>
	<div class="item liHeight">
		<c:set var="date1"><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd" type="date"/></c:set>
		<c:set var="date2">2014-03-05</c:set>
		<!--startprint1-->
		<div id="printDiv_1" style="display: none;"><br/></div>
		<div class="title clear"> 签购单</div>
		<div id="printDiv_2" style="display: none;">
		<p class="double_line"></p>
		</div>
		<ul style="margin-left:0;">
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 99%">商户名称(MERCHANT)：</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 99%"><font size="3" color="#808080">${params['acq_merchant_name']}</font> </span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">商户编号(MERCHANT CODE)：${params['acq_merchant_no']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">终端编号(TERMINAL NUMBER)：${params['acq_terminal_no']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">卡号(CARD NUMBER)：</span><span style="float:right;width: 1%;text-align: right;">&nbsp;</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%"><font size="3" color="#808080"><u:cardcut content="${params['account_no']}"/></font></span><span style="float:right;width: 1%;text-align: right;">&nbsp;</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">发卡行(CARD ISSUER)：<font size="3" color="#808080">${params['bank_nameT']}</font></span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">交易类型(TRANS.TYPE)：<c:choose>
					<c:when test="${params['trans_type'] eq 'PURCHASE'}">消费(SALE)</c:when>
					<c:when test="${params['trans_type'] eq 'PURCHASE_VOID'}">
						<font  style="color: red;">消费撤销</font>
					</c:when>
					<c:when test="${params['trans_type'] eq 'PURCHASE_REFUND'}">
						<font  style="color: red;">退货</font>
					</c:when>
					<c:when test="${params['trans_type'] eq 'REVERSED'}">
						<font  style="color: red;">冲正</font>
					</c:when>
					<c:when test="${params['trans_type'] eq 'BALANCE_QUERY'}">
						<font  style="color: red;">余额查询</font>
					</c:when>
					<c:otherwise>${params['trans_type'] }</c:otherwise>
				</c:choose></span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">批次号(BATCH NO)：${params['acq_batch_no']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">参考号(REFER NO)：${params['acq_reference_no']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">授权号(AUTH NO)：${params['acq_auth_no']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">凭证号(VOUCHER NO)：${transId}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 100%">时间(DATE/TIME)：<fmt:formatDate value="${params['trans_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left; width: 100%">交易金额(AMOUNT)：</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left; width: 100%">
				<font size="4" style="font-weight:bold;float: left;">RMB
				<c:choose>
					<c:when test="${params['trans_amount'] == null}">***</c:when>		
					<c:otherwise>
						${params['trans_amount']}
					</c:otherwise>
				</c:choose>
				</font>
				</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 50%">持卡人签名(SIGNATURE)：</span></li>
			<c:if test="${date2 >= date1}">
				<img alt="签名" width="90%" height="100" align="middle"
						src="http://183.238.157.6:5780/${sysconfigPath}${transId}.png" />
			</c:if>
			<c:if test="${date2 < date1}">
				<img alt="签名" width="90%" height="100" align="middle"
					src="${signUrl}" />
			</c:if>
		</ul>
		<div class="clear"></div>
		<!--endprint1-->
		<div class="down">
			<input type="button" class="button blue medium" value="打印小票" onclick="ticketPreview(1);"/>
		</div>
	</div>
</body>
</html>