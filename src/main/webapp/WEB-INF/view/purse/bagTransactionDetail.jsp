<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
<script type="text/javascript">

$(function() {
	var api = frameElement.api, W = api.opener;
	
	$("#handReversal").click(function() {

		 var id="${id}";
	        $("#handReversal").attr('disabled',true);
	        $("#handReversal").attr('value','正在交易，请稍后...');
	          $.ajax({
	               url:'${ctx}/purse/bagHandReversal',
	                     cache:false,
	                     data:{'id':id},
	                     type:'POST',
	                     error:function()
	                     {
	                    	 W.$.dialog({title:'提示',content: '请求失败，请稍后再试!',parent:api,close: function() {window.location.reload();}});
	                    	 $("#handReversal").attr('disabled',false);
	                    	 $("#handReversal").attr('value','手工冲正');
	                     },
	                     success:function(msg)
	                     {       
	                    	 $("#handReversal").attr('disabled',false);
	                    	 W.$.dialog({title:'提示',content: msg,parent:api,close: function() {window.location.reload();}});
	                     }
	               });
	          
          
  });
	
	
	
})
</script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">提现信息 </div>
	<ul>
		  <li style="width:310px;" id="id"><span style="width:100px;">交易ID：</span>
			 ${id}
		 </li>
		  <li style="width:310px;" id="mobile_no"><span style="width:100px;">手机号：</span>
		 	  ${mobile_no}
		 </li>
		 <li style="width:310px;" id="card_no"><span style="width:100px;">卡号：</span>
		    <u:cardcut content="${account_no}" />
		 </li>
		 <li style="width:310px;" id="merchant_name"><span style="width:100px;">户名：</span>	
		 	 ${account_name}
		 </li>		
		 <li style="width:310px;" id="amount"><span style="width:100px;">金额：</span>
		     ${amount}元
		 </li>
		 <li style="width:310px;" id="fee"><span style="width:100px;">手续费：</span>
		     ${fee}元
		 </li>
		 <li style="width:310px;" id="settle_days"><span style="width:100px;">提现类型：</span>
		     
		      <c:choose>
			  	   <c:when test="${settle_days==0}">
		   					当日余额
			  	   </c:when>
			       <c:otherwise>  
			    	           历史余额 
			       </c:otherwise>
	       </c:choose>
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">提现状态：</span>
		     ${statusDesc}
		 </li>
		 <li style="width:310px;" id="settle_days"><span style="width:100px;">开户行：</span>
		     ${bank_name}
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">提现时间：</span>
		     ${create_time}
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">提现信息：</span>
		     <c:choose>  
										    <c:when test="${f:length(cash_remark) > 15}">  
										        <p  title="${cash_remark}"><c:out value="${f:substring(cash_remark, 0, 15)}..." /> </p> 
										    </c:when>  
										   <c:otherwise>  
										        <c:out value="${cash_remark}" />  
										    </c:otherwise>  
										</c:choose>
		 </li>
		 <li style="width:310px;" id="settle_days"><span style="width:100px;">转账批次：</span>
		     ${cash_file_id}
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">转账时间：</span>
		     ${cash_time}
		 </li>
		 <li style="width:310px;" id="settle_days"><span style="width:100px;">是否冲正：</span>
		     <c:choose>
			  	   <c:when test="${is_back==1}">
		   					是
			  	   </c:when>
			       <c:otherwise>  
			    	           否 
			       </c:otherwise>
	       </c:choose>
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">冲正状态：</span>
		     ${backStatusDesc}
		 </li>
		 <li style="width:310px;" id="settle_days"><span style="width:100px;">冲正信息：</span>
              <c:choose>  
										    <c:when test="${f:length(back_remark) > 15}">  
										        <p  title="${back_remark}"><c:out value="${f:substring(back_remark, 0, 15)}..." /> </p> 
										    </c:when>  
										   <c:otherwise>  
										        <c:out value="${back_remark}" />  
										    </c:otherwise>  
							</c:choose>
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">审核人：</span>
		     ${check_person}
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">审核时间：</span>
		     ${check_time}
		 </li>

			<c:if test="${is_back==1}">
				<c:if test="${back_status==0||back_status==2||back_status==3}">
					<div class="title clear" align="center">
						<shiro:hasPermission name="BAG_HAND_REVERSAL">
							<input class="button blue medium" type="button" id="handReversal"
								value="手工冲正" />
						</shiro:hasPermission>
					</div>
				</c:if>
			</c:if>

			<div class="clear"></div>
</div>
</body>
</html>