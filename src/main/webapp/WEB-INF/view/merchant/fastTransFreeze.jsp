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
		<div class="title clear">冻结期限</div>
		<script type="text/javascript">
		$(function(){
			
			$("#freezeTransbtn").on("click",function(){
				 var freezeTypes=document.getElementsByName("freeze");
				 var freezeType = 0;
				 var fastId = $("#fastId").val();
				 var freezeReason = $("[name='freezeReason']").val();
				 var merNo = $("#merNo").val();
				 var merDate = $("#merDate").val();
				 var createDate = $("#createDate").val();
				
				for(var i = 0 ;i < freezeTypes.length;i++){
					
					if(freezeTypes[i].checked){
						freezeType = freezeTypes[i].value;
					}
				} 
			
			
				var freezeDay =  $("[name='freezeDay']").val();
				if(freezeType == "1"){
					if(freezeDay == ""){
						alert("请输入冻结天数" );
						
						return false;
					}else if(freezeReason == ""){
						alert("请输入冻结原因" );
					
						return false;
					}
				}else{
					if(freezeReason == ""){
						alert("请输入冻结原因" );
						return false;
					}
				}
				
				$.ajax({
					/* url:"${ctx}/mer/freezeTrans", */
					url:"${ctx}/mer/freezeTransNew",
					type:"post",
					data: {"merNo":merNo,"createTime":createDate,"settleTime":merDate,"freezeDay":freezeDay,"fastId":fastId,"freezeReson":freezeReason},
					dataType:"text",
					success:function(d,status){
						if(d == "true"){
							 alert("冻结成功");
							 top.freezeDialog.close();
						}else{
							alert("冻结失败，刷新后重新冻结" );
						}
					},
					error:function(){
						
						alert("网络异常。。。请刷新后重试");
					}
				})
					
				return false;
			
			});
			
			
			
		})
		function showFreezeTime(feezeday){
				
				var day = $(feezeday).val()
				
				if(isNaN(day)){
					$("#freezeTime").html("冻结天数输入有误");
					$(feezeday).val("");
					return false;
				}
				if (day.indexOf(".") != -1){
					$("#freezeTime").html("冻结天数不能为小数");
					$(feezeday).val("");
					return false;
				}
				if(day.indexOf("-") != -1){
					$("#freezeTime").html("冻结天数不能为负数");
					$(feezeday).val("");
					return false;
				}
				if(day =="0"){
					$("#freezeTime").html("冻结天数不能为0天");
					$(feezeday).val("");
					return false;
				}
				if(day == ""){
					$("#freezeTime").html("");
					$(feezeday).val("");
					return false;
				}
				if(parseInt(day) > 5000){
					$("#freezeTime").html("冻结天数不能超过5000天");
					$(feezeday).val("");
					return false;
				}
				
				var settime = $("#merDate").val().substr(0,10).split("-");
				var odate = new Date();
				odate.setFullYear(parseInt(settime[0]));
 				odate.setMonth(parseInt(settime[1]) -1);
 				odate.setDate(parseInt(settime[2]));
 				var ndate ;
				if(day != "" ){
					ndate =  new Date(Date.parse(odate) + (86400000 * parseInt(day)));
 					$("#freezeTime").html("清算日期:" +ndate.getFullYear()+"年" + (ndate.getMonth() + 1)+"月"+ndate.getDate()+"日");
				}
			}
		function changeFreezeType(type){
			if(type==1){
				$("#freezeDay").show();
				$("#freezeTime").html("");
			}else{
				$("[name='freezeDay']").val("");
				$("#freezeDay").hide();
				$("#freezeTime").html("清算日期:2037年12月31日");
			}
		}
	</script>
	<ul>
		<li style="width:400px">
		<input type="hidden" id="fastId" value="${params['id']}" />
		<input type="hidden" id="createDate" value="${params['create_time']}" />
		<input type="hidden" id ="merDate" value="${params['settle_date']}" />
		<input type="hidden" id="merNo" value="${params['merchant_no']}" />
		<div >有期冻结 <input type="radio" name="freeze" checked="checked" value="1" onclick="javascript:changeFreezeType(1)"/>&nbsp;&nbsp;&nbsp;无期冻结 <input type="radio" value="2" name="freeze" onclick="javascript:changeFreezeType(2)"/></div>
		</li>
		<li id="freezeDay" style="width:315px;height:auto;"><span >冻结天数:</span><input type="text"  onblur="showFreezeTime(this)" name="freezeDay"/></li><li style="width:150px;" id="freezeTime"></li>
		<li id="freezeReason" style="width:400px;height:auto;"><span>冻结原因:</span><textarea  name="freezeReason" cols="23" rows="2"></textarea></li>
		<li style=""><div class="clear"><input class="button blue medium" id="freezeTransbtn" value="冻结" type="submit"></div></li>
	</ul>
</div>
</body>
</html>