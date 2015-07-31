<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	
	 
	 $(function(){
		 
		$("#check").click(function(){
			var chk_value =[];    
			var isValidate = true;
			  $('input[name="ids"]:checked').each(function(i,n){
					//判断是否全为已入库
					var hiddenOpenStatus = $(n).parent().find(".hiddenOpenStatus").val();
				    var orderNo = $(n).parent().parent().find(".orderNo").html();
             
					if(hiddenOpenStatus !== "2")
					{
						$.dialog({title: '错误',lock:true,content: "序号为"+orderNo+"状态不为已审核状态不能再进行审核！",icon: 'error.gif',ok: function(){
							 	$(n).focus();
				    		}
						});
						isValidate = false;
						return false;
					}
			   		chk_value.push($(this).val());    
			  });    
			  if(isValidate)
			  {
				  if(chk_value.length==0){
					  $.dialog({title: '错误',lock:true,content: "没有选择任何记录",icon: 'error.gif'});
					  return false;
				    }else{
				    	  $.dialog({title: '审核',lock: true,drag: false,resize: false,max: false,content: 'url:viewChecker?ids='+chk_value+'&layout=no',close: function(){
						 	   $("#submit").click();
					       }}); 
						  //$.dialog({lock: true,drag: false,resize: false,max: false,content: 'url:viewChecker?ids='+chk_value+'&layout=no'});
					
					   }
			  }
		});
			
			
		$("#select").click(function(){
			 $("#tbdata :checkbox").each(function (i,n) {
				 if(!$(n).attr("disabled"))
				 {
					 $(this).prop("checked",!$(this).prop("checked"));
			     }
            });
		});
		
		
	 });
	 
	 
	 function checkfailed(id){
	     location.href='${ctx}/purse/checkReason?id='+id;
	 }
	 
	 function showDetail(id)
		{
			$.dialog({title:'提现详情',width: 700,height:300,resize: false,lock: true,max:false,content: 'url:bagExtraDetail?id='+id+'&layout=no'});
		}
		
	 //充值		
	 function bagRechargeQuery(mobile_no){
          window.location.href='bagRechargeQuery?mobile_no='+mobile_no;
     }	
	 
	 
	 //审核	
	 function checkRecharge(id){
		  if(!confirm("确定要审核吗？")){
			    return;
		    }		
		  $.post(
					'bagCheckRecharge',
					{id:id},
					function(json)
					{
						if(json.flag == 1)
						{
							alert("审核成功！");
							$("#submit").click();
						}else{
						    alert('审核失败，请检查数据');
						    $("#submit").click();
						}
					}
   	    );
     }	
	 
	 //调账
	 function checkAccountSubmit(id){
		  if(!confirm("确定要调账吗？")){
			    return;
		    }		
		  $.post(
					'bagCheckSubmit',
					{id:id},
					function(json)
					{
						if(json.flag == 1)
						{
							alert("调账成功！");
							$("#submit").click();
						}else{
						    alert('调账失败，'+json.msg);
						    $("#submit").click();
						}
					}
   	    );
     }	
		

	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包调账查询</div>
   
   <form:form id="bagCheckQuery" action="${ctx}/purse/bagCheckQuery" method="post">
    <div id="search">
    	<div id="title">钱包提现查询</div>
	      <ul>
	        <li><span style="width: 60px;">手机号  ：</span><input type="text" style="width: 100px;" value="${params['mobileNo']}" name="mobileNo" /></li>	    
	        <li><span style="width: 60px;">审核状态：</span>
	        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="checkStatus" name="checkStatus">
            		<option value="" <c:out value="${params['checkStatus'] eq ''?'selected':'' }"/>>全部</option>
	         		<option value="1"  <c:out value="${params['checkStatus'] eq '1'?'selected':'' }"/>>审核通过</option>
	         		<option value="2"  <c:out value="${params['checkStatus'] eq '0'?'selected':'' }"/>>待审核</option>
	         		<option value="3"  <c:out value="${params['checkStatus'] eq '5'?'selected':'' }"/>>审核失败</option>
			</select>
	        </li>
	        <li><span style="width: 60px;">调账状态：</span>
	        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="transStatus" name="transStatus">
            		<option value="" <c:out value="${params['transStatus'] eq ''?'selected':'' }"/>>全部</option>
	         		<option value="1"  <c:out value="${params['transStatus'] eq '0'?'selected':'' }"/>>已下单</option>
	         		<option value="2"  <c:out value="${params['transStatus'] eq '1'?'selected':'' }"/>>成功</option>
	         		<option value="3"  <c:out value="${params['transStatus'] eq '2'?'selected':'' }"/>>失败</option>
			</select>
	        </li>	    
			<li><span style="width: 60px;">审核人  ：</span><input type="text" style="width: 100px;" value="${params['checkPerson']}" name="checkPerson" /></li>
	      </ul>
	      <br/>
	      <ul>
	        <li><span span style="width: 60px;">创建时间:</span><input  type="text"  style="width:100px" readonly="readonly" id="startDate" name="startDate" value="${params['startDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"></li>
	        <li><span span style="width: 60px;text-align:center;">~</span><input  type="text"  style="width:100px" readonly="readonly" id="endDate" name="endDate" value="${params['endDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"></li>
	        <li><span span style="width: 60px;">审核时间:</span><input  type="text"  style="width:100px" readonly="readonly" id="checkStartDate" name="checkStartDate" value="${params['checkStartDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"></li>
	        <li><span span style="width: 60px;text-align:center;">~</span><input  type="text"  style="width:100px" readonly="readonly" id="checkEndDate" name="checkEndDate" value="${params['checkEndDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})"></li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
<!--         <input   class="button blue medium" type="button" id="select"  value="反选"/> -->
    	<input class="button blue medium" type="submit" id="submit"  value="查询"/>  	
<%--     	<shiro:hasPermission name="BAG_QUERY_CHECK">
    	<input class="button blue medium" type="button" id="check"  value="审核"/>
    	</shiro:hasPermission> --%>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    

    <a name="_table"></a>
      <div class="tbdata"  id="tbdata"   style="overflow-y:hidden;overflow-x:scroll;width:99%;" >
      <table width="100%"   cellspacing="0" class="t2"  >
        <thead>
          <tr>
<%--         <shiro:hasPermission name="BAG_EXTRACTION_QUERY_CHECK">
        <th width="30px;">选择</th>
        </shiro:hasPermission> --%>
		  <th width="30px;">序号</th>
          <th width="120px;">操作</th>
          <th width="65px;">用户名</th>
          <th width="55px;">审核状态</th>
          <th width="80px;">手机号</th>
          <th width="85px;">调账金额（元）</th>
          <th width="85px;">调账方向</th>
          <th width="55px;">调账状态</th>
          <th width="120px;">调账原因</th>
          <th width="80px;">调账时间</th>
          <th width="65px;">审核人</th>
          <th width="120px;">审核原因</th>
          <th width="80px;">审核时间</th>
          <th width="80px;">创建时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"  >
	 <%--           <shiro:hasPermission name="BAG_EXTRACTION_QUERY_CHECK">
	              <td class="center">
				 	 <c:choose>
					  <c:when test="${item.check_status eq '1'||item.check_status eq '3'}"><input type="checkbox"  disabled="disabled"  name="ids"  value="${item.id}"/></c:when>
					  <c:when test="${item.check_status eq '2'}"><input type="checkbox" name="ids"  value="${item.id}"/></c:when> 
					  <c:otherwise><input type="checkbox" disabled name="ids"  value="${item.id}"/></c:otherwise>
	    			 </c:choose>
	    			 <input type="hidden" class="hiddenOpenStatus" value="${item.check_status}"/>
		          </td>
	          </shiro:hasPermission> --%>
	          <td class="center"><span class="center  orderNo">${status.count}</span></td>
	         
	          <td align="center">
			  <c:choose>
			       <c:when test="${item.check_status eq '2'}">
                 		  <shiro:hasPermission name="BAG_ACCOUNT_CHECK"> 
                 		  <a href="javascript:checkRecharge(${item.id});"  title="审核">审核</a>|
			              </shiro:hasPermission>     
			              <shiro:hasPermission name="BAG_ACCOUNT_CHECK"> 
                 		  <a href="javascript:checkfailed(${item.id});"  title="审核失败">审核失败</a>
		                  </shiro:hasPermission> 
			       </c:when>
			       <c:when test="${item.check_status eq '1'}">
			       		<c:if test="${item.trans_status eq '0'}">
			       		 <shiro:hasPermission name="BAG_ACCOUNT_TRANS"> 
				        	<a href="javascript:checkAccountSubmit(${item.id});"  title="调账">调账</a>
				         </shiro:hasPermission>
			       		</c:if>
			       </c:when>
			  </c:choose>
			 </td>
	          <td align="center" style="word-wrap : break-word ;">${item.real_name}</td>
	          <td align="center">
	          <c:choose>
				 <c:when test="${item.check_status eq '1'}">审核通过</c:when>
				 <c:when test="${item.check_status eq '2'}">待审核</c:when> 
				 <c:when test="${item.check_status eq '3'}"><span class="font_red">审核失败</span></c:when> 
			  </c:choose>
	          </td>
	          <td align="center">${item.mobile_no}</td>
	          <td style="word-wrap : break-word ;">${item.amount}</td>
	          <td align="center">
	             <c:choose>
					 <c:when test="${item.adjust_type eq '0'}">公司账户到商户账户</c:when> 
					 <c:when test="${item.adjust_type eq '1'}">商户账户到公司账户</c:when>
			  	</c:choose>
	          </td>
	          <td align="center">
	             <c:choose>
					 <c:when test="${item.trans_status eq '0'}">已下单</c:when> 
					 <c:when test="${item.trans_status eq '1'}">成功</c:when>
					 <c:when test="${item.trans_status eq '2'}">失败</c:when> 
			  	</c:choose>
	          </td>
	          <td align="center"><u:substring length="30" content="${item.check_msg}"/></td>
	          <td align="center"><fmt:formatDate value="${item.trans_time}" type="both"/></td>
			  <td align="center">${item.check_user}</td>
			  <td align="center">${item.check_fail_msg}</td>
			  <td align="center"><fmt:formatDate value="${item.check_time}" type="both"/></td>
			  <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
</body>
