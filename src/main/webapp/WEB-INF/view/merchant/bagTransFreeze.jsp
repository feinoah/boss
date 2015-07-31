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
				
				 var transId = $("#transId").val().substr(8);
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
					data: {"amount":amount,"merNo":merNo,"createTime":createDate,"settleTime":merDate,"id":transId,"freezeReson":freezeReason},
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
		<input type="hidden" id="transId" value="${params['trans_id']}" />
		<input type="hidden" id="createDate" value="${params['create_time']}" />
		<input type="hidden" id ="merDate" value="${params['merchant_settle_date']}" />
		<input type="hidden" id="merNo" value="${params['merchant_no']}" />
		<input type="hidden" id="amount" value="${params['trans_amount']}" />
		<input type="hidden" id="fee" value="${params['merchant_fee']}" />
		</li>
		<li id="freezeReason" style="width:400px;height:auto;"><span>冻结原因:</span><textarea  name="freezeReason" cols="23" rows="2"></textarea></li>
		<li style=""><div class="clear"><input class="button blue medium" id="freezeTransbtn" value="冻结" type="submit"></div></li>
	</ul>		
	
</div>
</body>
</html>