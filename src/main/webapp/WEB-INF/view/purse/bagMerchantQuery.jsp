<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
    
<head>
	<script type="text/javascript">
	function divOffOn(){
		$("#merchantOffOnDiv").toggle();
		if($("#merchantOffOnDiv").is(":visible")){
			$("#divDisplayLable").text('收起');
		}else if($("#merchantOffOnDiv").is(":hidden")){
			$("#divDisplayLable").text('展开');
		}
		
	}
	
	
	function showAddDetail()
	{
		  $.dialog({title:'新增钱包商户规则',width: 400,height:250,
			resize: false,lock: true,max:false,
			content: 'url:addPurseMerchantRulePage?'+'layout=no'
					});
	}
	
	
	function delPurseMerchantRule(id,merchantNo,merhcantName){
		var alertMsg="您将删除【"+merhcantName+"/"+merchantNo+"】，是否继续？";
		$.dialog.confirm(alertMsg, function() {
			del(id);
			});
		
		
	}
	
	function del(id){
		$.post('${ctx}/purse/delPurseMerchantRule',
				  {id:id},
				  function(data){
					   var datas=data.split("#");
					   if(datas[0]=='success'){
						   $.dialog.alert(datas[1],function(){window.location.reload();});
					   }else{
						   $.dialog.alert(datas[1]);
					   }
						 
			  });
		
		
	}
	
	function updateMerchantOffOn(merchantOffOn){
		$("#updateMerchantOffOn").attr('disabled', true);
		$("#updateMerchantOffOn").attr('value', '正在交易...');
		$.ajax({
			url : '${ctx}/purse/updatePruseMecantOffOn',
			cache : false,
			data : {'merchantOffOn' : merchantOffOn},
			type : 'POST',
			error : function() {
				$("#updateMerchantOffOn").attr('disabled',false);
				$("#updateMerchantOffOn").attr('value', '确定');
				$.dialog.alert('请求失败，请稍后再试!');

			},
			success : function(msg) {
				$("#updateMerchantOffOn").attr('disabled',false);
				$("#updateMerchantOffOn").attr('value', '确定');

				var data = msg.split("#");
				$.dialog.alert(data[1]);
			}
		});
		
		
	}
	
	
$(function(){
		
		$("#updateMerchantOffOn").click(function() {
			   var merchantOffOn=$("input[name='merchantOffOn']:checked").val();
			   var alertMsg='';
			   if(merchantOffOn=='true'){
				   alertMsg='您的操作将开启钱包商户规则，继续操作吗？';
			   }else{
				   alertMsg='您的操作将关闭钱包商户规则，所有商户规则符合条件，继续操作吗？';
			   }

				$.dialog.confirm(alertMsg, function() {
					    updateMerchantOffOn(merchantOffOn);
						});

		});
		
		

});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包商户管理</div>

		<div id="search">
    	<div id="title">钱包商户规则开关&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:divOffOn()" id="divDisplayLable">展开</a></div>
    	<div id="merchantOffOnDiv" style="display:none">
	       <ul>
	          <li><span style="width: 120px;">开通钱包商户规则 ：</span><input name="merchantOffOn" type="radio" value="true" <c:if test="${merchantOffOn eq 'true'}">checked='checked'</c:if>/></li> 
	          <li><span style="width: 120px;">关闭钱包商户规则 ：</span><input name="merchantOffOn" type="radio" value="false" <c:if test="${merchantOffOn eq 'false'}">checked='checked'</c:if>/></li>  
	          <li><div class="search_btn">
	              <shiro:hasPermission name="OFF_ON_PURSE_MERCHANT_RULE">
	                    <input class="button blue medium" type="button" value="确定" id="updateMerchantOffOn" />
	              </shiro:hasPermission>
	          </div></li>
				 </ul>
       </div>
				<div class="clear"></div>
		</div>
		
		<form:form id="bagMerchantQueryForm" action="${ctx}/purse/bagMerchantQuery" method="post">
    <div id="search">
    	<div id="title">钱包商户规则</div>
	      <ul>
			     <li><span style="width: 60px;">商户类型：</span>
				        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="merchantType" name="merchantType">
			            			<option value="" <c:if test="${params['merchantType'] eq ''}">selected='selected'</c:if>>全部</option>
								        <c:forEach items="${merchantTypeList}" var="item">
								             <option value="${item.code_id}" <c:if test="${params['merchantType'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
								        </c:forEach>
								</select>
	         </li>	
	         <li><span style="width: 60px;">规则类型：</span>
				        <select  style="width:128px;padding: 3px;border: 1px solid #A4A4A4;" id="ruleType" name="ruleType">
			            			<option value="" <c:if test="${params['ruleType'] eq ''}">selected='selected'</c:if>>全部</option>
								        <c:forEach items="${ruleTypeList}" var="item">
								             <option value="${item.code_id}" <c:if test="${params['ruleType'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
								        </c:forEach>
								</select>
	        </li>
	        <li><span style="width: 68px;">商户编号 ：</span><input type="text" style="width: 100px;" value="${params['merchantNo']}" name="merchantNo" id="merchantNo"  /></li>
	        <li><span style="width: 68px;">商户名称 ：</span><input type="text" style="width: 100px;" value="${params['merchantName']}" name="merchantName" id="merchantName"  /></li>    
				  
				</ul>
				</ul>
							
				<div class="clear"></div>
    </div>
    
		<div class="search_btn">
			<li>
					<input class="button blue medium" type="submit" value="查询" />
					<shiro:hasPermission name="ADD_PURSE_MERCHANT_RULE">
					    <input class="button blue medium" type="button" value="新增" id='addMerchantRule' onclick='showAddDetail()'/>
			    </shiro:hasPermission>
			</li>
		</div>
    </form:form>

		<a name="_table"></a>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="16%">商户编号</th>
          <th width="20%">商户名称</th>
          <th width="8%">商户类型</th>
          <th width="12%" >规则</th> 
          <th width="16%" >增加时间</th> 
          <th width="10%">增加人</th>
          <th width="10%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.merchant_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.merchant_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.merchantTypeDesc}</td>
	          <td class="center">${item.ruletypeDesc}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
		        <td class="center">${item.create_person_name}</td>
			      <td class="center">
			        <shiro:hasPermission name="DEL_PURSE_MERCHANT_RULE">
	          			<a href="javascript:delPurseMerchantRule('${item.id}','${item.merchant_no}','${item.merchant_name}');">删除</a>
	          	</shiro:hasPermission>
	          </td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
  <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
