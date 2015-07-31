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
	
	
	
	$("#handTrans").click(function() {
        var id="${id}";
        $("#handTrans").attr('disabled',true);
        $("#handTrans").attr('value','正在交易，请稍后...');
          $.ajax({
               url:'${ctx}/purse/bagHandTrans',
                     cache:false,
                     data:{'id':id},
                     type:'POST',
                     error:function()
                     {
                    	 $.dialog.alert("请求失败，请稍后重试");
                    	 $("#handTrans").attr('disabled',false);
                    	 $("#handTrans").attr('value','手工入账');
                    	 alert("请求失败，请稍后再试!");
                     },
                     success:function(msg)
                     {       
                    	 $("#handTrans").attr('disabled',false);
                    	 alert(msg);
                    	 window.location.reload();
                     }
               });         
  });
	
	
	
})
</script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">交易信息 </div>
	<ul>
		  <li style="width:310px;" id="trans_id"><span style="width:100px;">原始交易ID：</span>
			 ${trans_id}
		 </li>
		  <li style="width:310px;" id="order_no"><span style="width:100px;">订单号：</span>
		     ${order_no}
		 </li>
		  <li style="width:310px;" id="mobile_no"><span style="width:100px;">手机号：</span>
		 	  ${mobile_no}
		 </li>
		 <li style="width:310px;" id="agent_no"><span style="width:100px;">代理商编号：</span>
		 	  ${agent_no}
		 </li>
		 <li style="width:310px;" id="merchant_no"><span style="width:100px;">商户号：</span>	
		 	 ${merchant_no}
		 </li>		
		 <li style="width:310px;" id="merchant_name"><span style="width:100px;">商户名：</span>	
		 	 ${merchant_name}
		 </li>		
		 <li style="width:310px;" id="card_no"><span style="width:100px;">卡号：</span>
		    <u:cardcut content="${card_no}" />
		 </li>
		 <li style="width:310px;" id="amount"><span style="width:100px;">金额：</span>
		     ${amount}元
		 </li>
		 <li style="width:310px;" id="trans_from"><span style="width:100px;">交易来源：</span>
		     ${trans_from}
		 </li>
		 <li style="width:310px;" id="trans_source"><span style="width:100px;">终端：</span>
		     ${trans_source}
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">入账状态：</span>
		     ${statusDesc}
		 </li>
		 <li style="width:310px;" id="trans_source"><span style="width:100px;">入账类型：</span>
		     <c:choose>
			  	   <c:when test="${status!=4}">
		   					自动入账
			  	   </c:when>
			       <c:otherwise>  
			    	           手工入账 
			       </c:otherwise>
	       </c:choose>
		 </li>
		 <li style="width:310px;" id="statusDesc"><span style="width:100px;">自动入账码：</span>
		     ${err_code}
		 </li>
		 <li style="width:310px;" id="trans_source"><span style="width:100px;">自动入账信息：</span>
		     ${err_msg}
		 </li>
	</ul>

		<c:if test="${status==2||status==3}">
			<div class="title clear" align="center">
				<shiro:hasPermission name="BAG_HAND_TRANS">
					<input class="button blue medium" type="button" id="handTrans"
						value="手工入账" />
				</shiro:hasPermission>
			</div>
		</c:if>

		<c:if test="${status==2||status==3||status==4}">  

		  <div class="title clear">手工入账信息 </div>
			<ul>	
			   <li style="width:310px;" id="handStatusDesc"><span style="width:100px;">手工入账状态：</span>
				     ${handStatusDesc}
				 </li> 
				 <c:if test="${hand_status!=0}">
				 
				 <li style="width:310px;" id="hand_num"><span style="width:100px;">手工入账次数：</span>
				     ${hand_num}
				 </li>
				 <li style="width:310px;" id="trans_source"><span style="width:100px;">手工入账人：</span>
				     ${hander}
				 </li>
				 <li style="width:310px;" id="hand_time"><span style="width:100px;">手工入账时间：</span>
				   <fmt:formatDate value="${hand_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
				 </li>
				 <li style="width:310px;" id="hand_err_code"><span style="width:100px;">手工入账码：</span>
				     ${hand_err_code}
				 </li>
				 <li style="width:310px;" id="hand_err_msg"><span style="width:100px;">手工入账信息：</span>
				     ${hand_err_msg}
				 </li>
				 </c:if>
				 
		    </ul>
    </c:if>
		  <div class="clear"></div>
</div>
</body>
</html>