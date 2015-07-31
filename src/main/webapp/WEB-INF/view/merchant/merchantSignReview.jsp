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
			if(status==1){
				down.append('<div class="pass">审核通过!</div>');
			}	else if(status==3) {
				down.append('<div class="fail">审核未通过!</div>');
			}
			$("#sign_check_person").html(result.sign_check_person);
			$("#sign_check_time").html(result.sign_check_time);
			if(status == 3){
				$("#checkFail").attr("disabled",true);
				$("#checkFail").removeAttr("class");
				$("#checkOK").attr("disabled",false);
				 $("#checkOK").removeAttr("button blue medium"); 
			}
			
			if(status ==1){
				 $("#checkOK").attr("disabled",true);
				 $("#checkOK").removeAttr("class"); 
				 $("#checkFail").attr("disabled",true);
				$("#checkFail").removeAttr("class");
			 }
			alert("操作成功！");
			/*var api = frameElement.api;
			var W = api.opener;
			api.close();
  			W.location.reload();*/
			//刷新父界面
		}else{
			alert("操作失败！");
		}		
	},"json");	
}



function ticketPreview(oper){
	
	// var url="${ctx}/mer/smallTicketPreview?id=31396&layout=no";
	// window.open(url);
	
	 document.getElementById("printDiv_1").style.display="";
	 document.getElementById("printDiv_2").style.display="";
	 if (oper < 10){
		  bdhtml = window.document.body.innerHTML;//获取当前页的html代码
		  sprnstr = "<!--startprint"+oper+"-->";//设置打印开始区域
		  eprnstr = "<!--endprint"+oper+"-->";//设置打印结束区域
		  prnhtml = bdhtml.substring(bdhtml.indexOf(sprnstr)+18); //从开始代码向后取html
		
		  prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));//从结束代码向前取html
		  window.document.body.innerHTML=prnhtml;
		  window.print();
		  //window.document.body.innerHTML=bdhtml;
	} else{
		 window.print();
	}
}

$(function() {
	var reviewStatus = "${reviewStatus}";
	 if(reviewStatus == 3){
		 $("#checkFail").attr("disabled",true);
		 $("#checkFail").removeAttr("class"); 
	 }
	 
	 if(reviewStatus ==1){
		 $("#checkOK").attr("disabled",true);
		 $("#checkOK").removeAttr("class"); 
	 }
});
 
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
		<div class="title clear">POS签购单</div>
		<div id="printDiv_2" style="display: none;">
		<p class="double_line"></p>
		</div>
		<ul style="margin-left:0;">
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 40%">商户名称(MERCHANT)：</span><span style="float:right;width: 60%;text-align: right;">${params['merchant_name']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 50%">商户编号(MERCHANT CODE)：</span><span style="float:right;width: 50%;text-align: right;">${params['merchant_no']}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 50%">终端编号(TERMINAL NUMBER)：</span><span style="float:right;width: 50%;text-align: right;">${params['terminal_no']}</span></li>
			<li style="width:100%;height: 3px"><div style="border-bottom:1px dashed #000000;"/> </li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left; width: 50%">交易金额(AMOUNT)：</span><span style="float:right;width: 50%;text-align: right;">RMB
				<font style="font-weight:bold;float: right;">
				<c:choose>
					<c:when test="${params['trans_amount'] == null}">***</c:when>		
					<c:otherwise>
						${params['trans_amount']}
					</c:otherwise>
				</c:choose>
				</font>
				</span></li>
				<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 50%">交易类型(TRANS.TYPE)：</span><span style="float:right;width: 50%;text-align: right;"><c:choose>
					<c:when test="${params['trans_type'] eq 'PURCHASE'}">消费</c:when>
					<c:when test="${params['trans_type'] eq 'PURCHASE_VOID'}">
						<span class="font_red" style="float: right;">消费撤销</span>
					</c:when>
					<c:when test="${params['trans_type'] eq 'PURCHASE_REFUND'}">
						<span class="font_red" style="float: right;">退货</span>
					</c:when>
					<c:when test="${params['trans_type'] eq 'REVERSED'}">
						<span class="font_red" style="float: right;">冲正</span>
					</c:when>
					<c:when test="${params['trans_type'] eq 'BALANCE_QUERY'}">
						<span class="font_gray" style="float: right;">余额查询</span>
					</c:when>
					<c:otherwise>${params['trans_type'] }</c:otherwise>
				</c:choose></span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 40%">卡号(CARD NUMBER)：</span><span style="float:right;width: 60%;text-align: right;"><u:cardcut content="${params['account_no']}"/></span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 40%">发卡行(CARD ISSUER)：</span><span style="float:right;width: 60%;text-align: right;">${params['bank_nameT']}</span></li>
			<li style="width:100%;height: 3px"><div style="border-bottom:1px dashed #000000;"/> </li>
			<%--<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;">凭证号：</span><span style="float:right;width: 70%;text-align: right;">${params['acq_reference_no']}</span></li>
			--%><li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 50%">订单号(ORDER NUMBER)：</span><span style="float:right;width: 50%;text-align: right;">${transId}</span></li>
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 50%">交易时间(TRANS TIME)：</span><span style="float:right;width: 50%;text-align: right;"><fmt:formatDate value="${params['trans_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></span></li>
			<li style="width:100%;clear:both;height: 40px"><span style="float:left;text-indent:5px;text-align:left;width: 35%">交易地点(LOCATION)：</span>
					<span style="float:right;width: 65%;text-align: right;">
					<c:choose>
					<c:when test="${params['address'] == null}">${params['address2']}</c:when>		
					<c:otherwise>
						${params['address']}
					</c:otherwise>
				</c:choose>
				</span>
			</li>
			<%--<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;">备注</span></li>
			--%><li style="width:100%;height: 3px"><div style="border-bottom:1px dashed #000000;"/> </li>
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
		<%--<c:if test="${reviewStatus!=1 && reviewStatus!=3}">
		--%><shiro:hasPermission name="COMMERCIAL_SIGN_CHECK">
			<input type="button" class="button blue medium" id="checkOK" value="审核通过" onclick="reviewSend(1);"/> 
			<input type="button" class="button blue medium"  id="checkFail" value="审核未通过" onclick="reviewSend(3);"/>
			<%--<input type="button" class="button blue medium" value="打印小票" onclick="ticketPreview(1);"/>
		--%>
		</shiro:hasPermission>
		<%--</c:if>
		--%><c:if test="${reviewStatus==1}">
			<div class="pass">审核通过!</div>
		</c:if>
		<c:if test="${reviewStatus==3}">
			<div class="fail">审核失败!</div>
		</c:if>
		<c:if test="${reviewStatus==4}">
			<div class="fail">已重签!</div>
		</c:if>
		</div>
		<ul style="margin-left:0;">
			<li style="width:100%;clear:both;"><span style="float:left;text-indent:5px;text-align:left;width: 40%">审方：<label id="sign_check_person">${params['sign_check_person']}</label> </span><span style="float:right;width: 60%;text-align: right;">审核时间: <label id="sign_check_time"><fmt:formatDate value="${params['sign_check_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></label> </span></li>
			</ul>
	</div>
</body>
</html>