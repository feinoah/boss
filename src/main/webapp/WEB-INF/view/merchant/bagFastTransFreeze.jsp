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
	<div class="title clear">交易信息 </div>
	<ul>
		<li style="width:220px;"><span>订单编号：</span>${params['order_no']}</li>
		<li style="width:200px;"><span>交易类型：</span>${params['biz_name']}</li>
		<li style="width:200px;"><span>交易金额：</span>${params['amount']}</li>
		<li style="width:220px;"><span>收单机构：</span>
			<c:choose>
				  <c:when test="${params['acq_enname'] eq 'eptok'}">YS</c:when>
				  <c:when test="${params['acq_enname'] eq 'tftpay'}">腾付通</c:when> 
				  <c:when test="${params['acq_enname'] eq 'bill'}">快钱</c:when> 
				  <c:when test="${params['acq_enname'] eq 'zypay'}">中意支付</c:when> 
				  <c:when test="${params['acq_enname'] eq 'yibao'}">易宝</c:when>
				  <c:when test="${params['acq_enname'] eq 'xlink'}">讯联</c:when>
				  <c:when test="${params['acq_enname'] eq 'hypay'}">翰亿</c:when>
				  <c:when test="${params['acq_enname'] eq 'ubs'}">瑞银信</c:when>
				  <c:otherwise>${params['acq_enname']}</c:otherwise>
			</c:choose> 
		</li>
		<li style="width:200px;"><span>交易卡号：</span><u:cardcut content="${params['card_no']}" /></li>
		<li style="width:200px;"><span>卡类型：</span>${params['card_type']}</li>
		<li style="width:220px;"><span>发卡行：</span>${params['bank_name']}</li>
		<li style="width:200px;"><span>卡种：</span>${params['card_name']}</li>
		<shiro:hasPermission name="SMS_MOBILE_VIEW">
			<li style="width:200px;"><span>交易手机号：</span>${params['sms_mobile']}</li>
		</shiro:hasPermission>
		<li style="width:220px;"><span>创建时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
		<li style="width:400px;"><span>交易状态：</span>${params['status']}</li>
		<li style="width:220px;"><span>商户编号：</span>${params['merchant_no']}</li>
		<li style="width:200px;"><span>商户名称：</span>${params['merchant_name']}</li>
		
	</ul>
		<div class="clear"></div>
		<div class="title clear">冻结信息</div>
		<script type="text/javascript">
		$(function(){
			
			$("#freezeTransbtn").on("click",function(){
				
				 var fastId = $("#fastId").val();
				 var freezeReason = $("[name='freezeReason']").val();
				 var merNo = $("#merNo").val();
				 var merDate = $("#merDate").val();
				 var createDate = $("#createDate").val();
				 var am = $("#amount").val();
				 
				 var fee = $("#fee").val();
				 var amount = parseFloat(am) - parseFloat(fee);
				 if(freezeReason == ""){
					 alert("请输入冻结原因");
					 return false;
				 }
				$.ajax({
					/* url:"${ctx}/mer/bagFreezeTrans", */
					url:"${ctx}/mer/bagFreezeTransNew",
					type:"post",
					data: {"amount":amount,"merNo":merNo,"createTime":createDate,"settleTime":merDate,"fastId":fastId,"freezeReson":freezeReason},
					dataType:"text",
					success:function(d){
						
							 alert(d);
							 top.freezeDialog.close();
						
					},
					error:function(){
						
						alert("网络异常。。。请刷新后重试");
					}
				})
					
				return false;
			
			});
			
		})
		
			
	
	</script>
	<ul>
		<li style="width:400px">
		<input type="hidden" id="fastId" value="${params['id']}" />
		<input type="hidden" id="amount" value="${params['amount']}" />
		<input type="hidden" id="createDate" value="${params['create_time']}" />
		<input type="hidden" id ="merDate" value="${params['settle_date']}" />
		<input type="hidden" id="merNo" value="${params['merchant_no']}" />
		<input type="hidden" id="fee" value="${params['merchant_fee']}" />
		</li>
		<li id="freezeReason" style="width:400px;height:auto;"><span>冻结原因:</span><textarea  name="freezeReason" cols="23" rows="2"></textarea></li>
		<li style=""><div class="clear"><input class="button blue medium" id="freezeTransbtn" value="冻结" type="submit"></div></li>
	</ul>
</div>
</body>
</html>