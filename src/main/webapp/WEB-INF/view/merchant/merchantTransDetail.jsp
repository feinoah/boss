<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
<!--<script type="text/javascript">
$(function() {
		var flag = false;
		var cardNo = "${params['card_no']}";
		<shiro:hasPermission name="TRANS_CARD_NO">
		flag = true;
		</shiro:hasPermission>
		if(!flag){
			cardNo = "<u:cardcut content="${params['card_no']}" />";
		}
		$("#cardNo").html(cardNo);
});
</script>
--></head>
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
		
		
		
		<li style="width:230px;"><span>交易卡号：</span><shiro:hasPermission name="TRANS_CARD_NO">${params['card_no']}</shiro:hasPermission>
									<shiro:lacksPermission name="TRANS_CARD_NO"><u:cardcut content="${params['card_no']}" /></shiro:lacksPermission></li>
		
		<li style="width:400px;"><span>发卡行：</span>${params['bank_name']}</li>
		<li style="width:200px;"><span>卡种：</span>${params['card_name']}</li>
		<li style="width:200px;"><span>卡类型：</span>${params['card_type']}</li>
		
		<li style="width:200px;"><span>创建时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
		<li style="width:200px;"><span>交易时间：</span><fmt:formatDate value="${params['trans_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
		<li style="width:200px;"><span>交易来源：</span>
		<u:posmodel svalue="${params.trans_source}" />
		<%-- 
			<c:choose>
				  <c:when test="${params['trans_source'] eq 'COM_MOBILE_PHONE'}">企业版</c:when>
				  <c:when test="${params['trans_source'] eq 'MOBOLE_PHONE'}">个人版</c:when> 
				  <c:when test="${params['trans_source'] eq 'POS'}">POS</c:when> 
				  <c:when test="${params['trans_source'] eq 'SMALLBOX_MOBOLE_PHONE'}">移小宝</c:when> 
				  <c:when test="${params['trans_source'] eq 'DOT'}">点付宝</c:when>
				  <c:when test="${params['trans_source'] eq 'NEW_LAND_ME30'}">YPOS08</c:when>
				  <c:when test="${params['trans_source'] eq 'NEW_LAND_ME31'}">YPOS09</c:when>
				  <c:when test="${params['trans_source'] eq 'SPOS'}">超级刷</c:when>
				  <c:when test="${params['trans_source'] eq 'SPOS_2'}">超级刷II</c:when>
				  <c:when test="${params['trans_source'] eq 'M188'}">超级刷III</c:when>
				  <c:when test="${params['trans_source'] eq 'TY'}">移联商通III</c:when>
				  <c:when test="${params['trans_source'] eq 'M368'}">移联商通V</c:when>
				  <c:otherwise>${params['trans_source']}</c:otherwise>
			</c:choose>  --%>
		</li>
		<li style="width:500px;"><span>交易地址:</span>
			<c:choose>
				<c:when test="${!empty params.address}">
					${params.address}
				</c:when>
				<c:when test="${!empty params.mobile_address}">
					${params.mobile_address}
				</c:when>
				<c:otherwise>
					未知					
				</c:otherwise>
			</c:choose>
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
				<c:if test="${device_sn ne ''}">
			<li style="width:200px;"><span>设备编号：</span>${params['device_sn']}</li>
		</c:if>
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
		<li style="width:200px;"><span>参考号：</span>${params['acq_reference_no']}</li>
		<li style="width:200px;"><span>授权号：</span>${params['acq_auth_no']}</li>
		
		
	</ul>
	<div class="clear"></div>
	
	<div class="title clear">最近操作</div>
	<ul>
		<li style="width:110px;"><span>时间</span></li>
		<li style="width:110px;"><span>操作内容</span></li>
		<li style="width:110px;"><span>操作人</span></li>
		<li style="width:110px;"><span>冻结类型</span></li>
		<li style="width:110px;"><span>冻结天数</span></li>
		<li style="width:150px;"><span>原因</span></li>
	</ul>
	<c:forEach items="${freezeLogs}" var="item" varStatus="status">
		<ul>
			<li style="width:110px;"><span><fmt:formatDate value="${item.oper_time}" pattern="yyyy-MM-dd" type="both" /></span></li>
			<li style="width:110px;"><span>
				<c:choose>
					<c:when test="${item.oper_type eq '0'}">冻结</c:when>
					<c:when test="${item.oper_type eq '1'}">解冻</c:when>
					<c:otherwise>-</c:otherwise>
				</c:choose>
			</span></li>
			<li style="width:110px;"><span>${item.oper_name}</span></li>
			<li style="width:110px;"><span>
				<c:choose>
					<c:when test="${item.freeze_way eq '0' && item.oper_type eq '0'}">无期</c:when>
					<c:when test="${item.freeze_way eq '1' && item.oper_type eq '0'}">有期</c:when>
					<c:otherwise>-</c:otherwise>
				</c:choose>
			</span></li>
			<li style="width:110px;"><span>${(item.freeze_day ne null && item.freeze_day ne '0') ? item.freeze_day : '-'}</span></li>
			<li style="width:150px;"><span>${item.oper_reason ne null ? item.oper_reason : '-'}</span></li>
		</ul>
	</c:forEach>
	<div class="clear"></div>
</div>
</body>
</html>