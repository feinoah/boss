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
	
	$("#queryMerchant").click(function() {

		      var merchantNo=$("#merchantNo").val();
		      var merchantType=$("#merchantType").val();
		      
		      $("#queryMerchant").attr('disabled',true);
		      $("#queryMerchant").attr('value','正在查询...');
		        
	        if(merchantNo==''){
	        	W.$.dialog({title:'出错啦',content:'商户号不能为空',max: false,min: false,parent:api,
		             cancelVal: '关闭',cancel:  function(){
		            	     $("#queryMerchant").attr('disabled',false);
			            	   $("#queryMerchant").attr('value','验证');}
		            	 
	        	});
	        	return false;
	        }
	        if(merchantType==''){
	        	W.$.dialog({title:'出错啦',content:'请选择商户类型',max: false,min: false,parent:api,
		             cancelVal: '关闭',cancel:  function(){
		            	     $("#queryMerchant").attr('disabled',false);
			            	   $("#queryMerchant").attr('value','验证');}
		            	 
	        	});
	        	return false;
	        }
		      
	        
	          $.ajax({
	               url:'${ctx}/purse/queryMerchant',
	                     cache:false,
	                     data:{'merchantNo':merchantNo,
	                    	     'merchantType':merchantType},
	                     type:'POST',
	                     error:function()
	                     {
	                    	 $("#queryMerchant").attr('disabled',false);
	                    	 $("#queryMerchant").attr('value','验证');
	                    	 $("#merchantNameDiv").show();
	                    	 $("#merchantName").html("验证商户或者代理失败,请稍后重试");
	                    	 
	                     },
	                     success:function(msg)
	                     {       
	                    	 $("#queryMerchant").attr('disabled',false);
	                    	 $("#queryMerchant").attr('value','验证');
	                    	 
	                    	 var data=msg.split("#");
	                    	 $("#merchantDesc").html(data[2]+"名称:");
	                    	 $("#merchantName").html(data[1]);
	                    	 $("#merchantNameDiv").show();
	                    	 if(data[0]=='success'){
	                    		 $("#addMerchantRuleButtonDiv").show(); 
	                    	 }
	                     }
	               });
	          
         
 });
	
	$("#addMerchantRule").click(function() {
		

		      var merchantNo=$("#merchantNo").val();
	        var merchantType=$("#merchantType").val();
	        var ruleType=$("#ruleType").val();
	        if(merchantNo==''){
	        	 W.$.dialog({title:'出错啦',content:'商户号不能为空',max: false,min: false,parent:api,
			             cancelVal: '关闭',cancel: true});
	        	return false;
	        }
	        if(merchantType==''){
	        	W.$.dialog({title:'出错啦',content:'商户类型未选择',max: false,min: false,parent:api,
		             cancelVal: '关闭',cancel: true});
	        	return false;
	        }
	        if(ruleType==''){
	        	W.$.dialog({title:'出错啦',content:'规则类型未选择',max: false,min: false,parent:api,
		             cancelVal: '关闭',cancel: true});
	        	
	        	return false;
	        }
	      
	        $("#addMerchantRule").attr('disabled',true);
	        $("#addMerchantRule").attr('value','正在交易，请稍后...');
	          $.ajax({
	               url:'${ctx}/purse/addPurseMerchantRule',
	                     cache:false,
	                     data:{'merchantNo':merchantNo,'merchantType':merchantType,'ruleType':ruleType},
	                     type:'POST',
	                     error:function()
	                     {
	                    	 W.$.dialog({title:'出错啦',content:'请求失败，请稍后再试!',max: false,min: false,parent:api,
         			             cancelVal: '关闭',cancel: function(){
         			            	   $("#addMerchantRule").attr('disabled',false);
         			            	   $("#addMerchantRule").attr('value','新增');
         			             
         			             }});
	                    	 
	                     },
	                     success:function(msg)
	                     {   
	                    	 var data=msg.split("#");
	                    	 if(data[0]=='success'){
	                    	   
	                    		 W.$.dialog({title:'成功啦',content:data[1],max: false,min: false,parent:api,
             			             cancelVal: '关闭',cancel:function(){api.close();W.location.reload();}});
             			             
	                    	 }else if(data[0]=='faild'){
	                    		 W.$.dialog({title:'出错啦',content:data[1],max: false,min: false,parent:api,
             			             cancelVal: '关闭',cancel: function(){
             			            	   $("#addMerchantRule").attr('disabled',false);
             			            	   $("#addMerchantRule").attr('value','新增');
             			             
             			             }});
	                    		 
	                    	 }
	                    	 
	                    	 
	                    	 
	                     }
	               });
	          
          
  });
	
	
	
})
</script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear" >新增钱包商户规则 </div>
		<ul>
			<li style="width: 360px;" id="id"><span style="width: 80px;">商户类型：</span>
				<select
				style="width: 138px; padding: 3px; border: 1px solid #A4A4A4;"
				id="merchantType" name="merchantType">
					<option value="" selected='selected'>请选择</option>
					<c:forEach items="${merchantTypeList}" var="item">
						<option value="${item.code_id}"
							<c:if test="${params['merchantType'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>
					</c:forEach>
			</select></li>
		</ul>
		<br /> <br />
		<ul>
			<li style="width: 360px;" id="id"><span style="width: 80px;">规则类型：</span>
				<select
				style="width: 138px; padding: 3px; border: 1px solid #A4A4A4;"
				id="ruleType" name="ruleType">
					<option value="" selected='selected'>请选择</option>
					<c:forEach items="${ruleTypeList}" var="item">
						<option value="${item.code_id}"
							<c:if test="${params['ruleType'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>
					</c:forEach>
			</select></li>
		</ul>
		<br/>
		<ul>
			<li style="width: 360px;" id="merchant"><span style="width: 80px;">商户编号：</span> 
			      <input type="text" style="width: 138px;"  name="merchantNo" id="merchantNo" />
			      <input class="button blue medium" type="button" id="queryMerchant" value="验证" />
			</li>
		</ul>
		<div id="merchantNameDiv" style="display:none">
		<br/>
			<ul>
				<li style="width: 360px;" id="mobile_name">
					<span style="width: 80px;color:red;" id="merchantDesc"></span>
					<span style="width: 160px;color:red;" id="merchantName"></span>
				</li>
			</ul>
		</div>
		<br/>
		<div id="addMerchantRuleButtonDiv" class="title clear" align="center" style="display:none">
						<shiro:hasPermission name="BAG_HAND_REVERSAL">
							<input class="button blue medium" type="button" id="addMerchantRule"
								value="新增" />
						</shiro:hasPermission>
					</div>

			<div class="clear"></div>
</div>
</body>
</html>