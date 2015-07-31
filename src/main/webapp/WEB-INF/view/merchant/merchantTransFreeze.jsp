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
		<li style="width:200px;"><span>订单编号：</span>${params['trans_id']}</li>
		<li style="width:200px;"><span>交易类型：</span>${params['trans_type']}</li>
		<li style="width:200px;"><span>交易状态：</span>${params['trans_status']}</li>
		
		<li style="width:200px;"><span>响应码：</span><u:bankcode code="${params['acq_response_code']}"/></li>
		
		<li style="width:200px;"><span>交易币种：</span>${params['currency_type']}</li>
		<li style="width:200px;"><span>交易金额：</span>
			<c:choose>
					<c:when test="${params['trans_amount'] == null}">***</c:when>		
					<c:otherwise>
						${params['trans_amount']}
					</c:otherwise>
			</c:choose>元
		</li>
		
		
		
		
		
	</ul>
		<div class="clear"></div>
	
	<div class="title clear">商户信息</div>
	<ul>
		<li style="width:200px;"><span>代理商名称：</span>	
			 <u:substring length="8" content="${params['agent_name']}"/>
		</li>
		<li style="width:400px;"><span>商户名称：</span>
		 <u:substring length="12" content="${params['merchant_name']}"/>(${params['merchant_no']})
		 </li>
		<li style="width:200px;"><span>终端号：</span>${params['terminal_no']}</li>
		<li style="width:200px;"><span>批次号：</span>${params['batch_no']}</li>
		<li style="width:200px;"><span>流水号：</span>${params['serial_no']}</li>
		

		<c:choose>
			<c:when test="${params['trans_type'] == '消费'}">
				<li style="width:400px;"><span>签约扣率：</span><u:merchantRate content="${params['merchant_rate']}"/></li>
				<li style="width:200px;"><span>手续费：</span>
					<c:choose>
						<c:when test="${params['merchant_fee'] == null }">0.00</c:when>		
						<c:otherwise>
						<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params['merchant_fee']}" />
						</c:otherwise>
					</c:choose>
					元
				</li>
				<li style="width:200px;"><span>结算日期：</span>
					<c:choose>
						<c:when test="${params['merchant_settle_date'] == null}">无</c:when>		
						<c:otherwise>
						<fmt:formatDate value="${params['merchant_settle_date']}" pattern="yyyy-MM-dd" type="both"/>
						</c:otherwise>
					</c:choose>	
				</li>
			</c:when>
		</c:choose>
				
	</ul>
		<div class="clear"></div>
	
	<div class="title clear">收单机构</div>
	<ul>
		<li style="width:200px;"><span>机构名称：</span>${params['acq_cnname']}</li>
		<li style="width:400px;"><span>商户名称：</span>
		<u:substring length="12" content="${params['acq_merchant_name']}"/>(${params['acq_merchant_no']})
		 </li>
		
		<li style="width:200px;"><span>终端号：</span>${params['acq_terminal_no']}</li>
		<li style="width:200px;"><span>批次号：</span>${params['acq_batch_no']}</li>
		<li style="width:200px;"><span>流水号：</span>${params['acq_serial_no']}</li>
		

		<c:choose>
			<c:when test="${params['trans_type'] == '消费'}">
				<li style="width:400px;"><span>签约扣率：</span><u:merchantRate content="${params['acq_merchant_rate']}"/></li>
				<li style="width:200px;"><span>手续费：</span>
				<c:choose>
					<c:when test="${params['acq_merchant_fee'] == null}">0.00</c:when>		
					<c:otherwise>
						<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params['acq_merchant_fee']}" />
					</c:otherwise>
				</c:choose>
				元
				</li>
				<li style="width:200px;"><span>结算日期：</span>
					<c:choose>
					<c:when test="${params['acq_settle_date'] == null}">无</c:when>		
					<c:otherwise>
						<fmt:formatDate value="${params['acq_settle_date']}" pattern="yyyy-MM-dd" type="both"/>
					</c:otherwise>
					</c:choose>
				</li>
			</c:when>
		</c:choose>
	</ul>
	<div class="clear"></div>
	<div class="title clear">冻结期限</div>

	<script type="text/javascript">
		
		$(function(){
			
			
			$("#freezeTransbtn").on("click",function(){
				 var freezeTypes=document.getElementsByName("freeze");
				 var freezeType = 0;
				 var transId = $("#transId").val().substr(8);
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
						
						alert("请输入冻结天数")
						return false;
					}else if(freezeReason == ""){
						alert("请输入冻结原因");
						return false;
					}
				}else{
					if(freezeReason == ""){
						alert("请输入冻结原因");
						return false;
					}
				}
				
				$.ajax({
					/* url:"${ctx}/mer/freezeTrans", */
					url:"${ctx}/mer/freezeTransNew",
					type:"post",
					data: {"merNo":merNo,"createTime":createDate,"settleTime":merDate,"freezeDay":freezeDay,"id":transId,"freezeReson":freezeReason},
					dataType:"text",
					success:function(d,status){
						
						if(d == "true"){
							alert("冻结成功");
							top.freezeDialog.close();
							
						}else{
							alert("冻结失败，刷新后重新冻结");
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
		<input type="hidden" id="transId" value="${params['trans_id']}" />
		<input type="hidden" id="createDate" value="${params['create_time']}" />
		<input type="hidden" id ="merDate" value="${params['merchant_settle_date']}" />
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