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
             
					if(hiddenOpenStatus !== "0")//已分配状态
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
					  $.dialog({title: '错误',lock:true,content: "没有选择任何用户",icon: 'error.gif'});
					  return false;
				    }else{
				    	  $.dialog({title: '审核',lock: true,drag: false,resize: false,max: false,content: 'url:viewChecker?ids='+chk_value+'&layout=no',close: function(){
						 	   $("form:first").submit();
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
		
		
		$("#cashStatistic").on("click", function() {
			
			  $("#cashStatistic").attr('disabled',true);
			  $("#cashStatistic").attr('value',"统计中，请稍后...");
			  var param = $("form:first").serialize();
			  
			  $.post("${ctx}/purse/cashStatistic", param, function(data) {
				     var html = cashStatisticToHtml(data);
			       $("#cash_total_msg").html(html);
			       $("#cash_total_msg").show();
				     
       	     $("#cashStatistic").attr('value',"提现信息统计");
       	     $("#cashStatistic").attr('disabled',false);
				});
			  
			  
		});
		
		$("#exportT1").on("click", function() {
			var t1Date = $("#T1ExportDate").val();
			$.ajax({
				url:"${ctx}/purse/checkT1Excel",
				type:"GET",
				data:"t1Date="+t1Date,
				error:function()
                {
                    alert("请求异常");
                },
	          	success: function(data){
	           	 	if(data=="true"){
	           	 		location.href = "${bagUrl}/excel/"+t1Date+"钱包T+1结算.xls";
	           	 	}else{
	           	 		alert("请选择正确的时间");
	           	 	}
	           	}
			}); 
			
		}); 
		
		
		
	 });
	 
	 
	 function checkfailed(id){
	     location.href='${ctx}/purse/viewReason?id='+id;
	 }
	 
	 
	 function showDetail(id)
		{
			$.dialog({title:'提现详情',width: 700,height:300,resize: false,lock: true,max:false,content: 'url:bagExtraDetail?id='+id+'&layout=no'});
		}
		
	 //充值		
	 function bagRechargeQuery(mobile_no){
          window.location.href='bagRechargeQuery?mobile_no='+mobile_no;
     }	
	 
	 
	 //充值		
	 function checkRecharge(id){
		  if(!confirm("确定要审核吗？")){
			    return;
		    }		
		  $.post(
					'checkRecharge',
					{id:id},
					function(json)
					{
						alert(json);
						$("form:first").submit();
					}
   	    );
     }	
	 
		function cashStatisticToHtml(totalMsg) {
			var html ="\<ul>";
			html +="\<li style='width:300px'>"+totalMsg+"\</li>";
			html +="\</ul>";
			html+="<div style='clear:both;'></div>";
			return html;
		}
		
		function exportExcel2(){
			
			var action = $("form:first").attr("action");
			$("form:first").attr("action", "${ctx}/purse/bagHisExport").submit();
			$("form:first").attr("action", action);
			
		}

	$(function(){
			$("#reset").click(function(){
				$(":text").val("");
				$("select").val("");
			})
		})
		
	function extractionManualSwitch(){
		window.freezeDialog = $.dialog({
		title : '提现开关',
		width : 450,
		height : 210,
		resize : false,
		lock : true,
		max : false,
		content : 'url:${ctx}/purse/bagExtractionManualSwitch?layout=no',
		close:function(){
				window.location.reload();
			}
		});
	}
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包提现查询</div>
   
   <form:form id="bagExtractionQuery" action="${ctx}/purse/bagExtractionQuery" method="post">
    <div id="search">
    	<div id="title">钱包提现查询</div>
	      <ul>
	        <li><span style="width: 60px;">手机号  ：</span><input type="text" style="width: 100px;" value="${params['mobileNo']}" name="mobileNo" /></li>	    
	        <li><span style="width: 60px;">账户名  ：</span><input type="text" style="width: 100px;" value="${params['accountName']}" name="accountName" /></li>	    
	        <li><span style="width: 60px;">审核状态：</span>
	        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="openStatus" name="openStatus">
            			<option value="" <c:if test="${params['openStatus'] eq ''}">selected='selected'</c:if>>全部</option>
					        <c:forEach items="${purseCashStatusList}" var="item">
					             <option value="${item.code_id}" <c:if test="${params['openStatus'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
					        </c:forEach>
					</select>
	        </li>	    
	        <li><span style="width: 60px;">分页记录：</span>
	           <select style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" name="pageSize">
	         		   <option value="20" <c:out value="${params['pageSize'] eq '20'?'selected':'' }"/>>20</option>
	         		   <option value="50" <c:out value="${params['pageSize'] eq '50'?'selected':'' }"/>>50</option>
	         		   <option value="100" <c:out value="${params['pageSize'] eq '100'?'selected':'' }"/>>100</option>
	         		   <option value="500" <c:out value="${params['pageSize'] eq '500'?'selected':'' }"/>>500</option>
	           </select>
	        </li>	    
	      </ul>
	      <br/>
	      <ul>
		      <%-- <li><span style="width: 60px;">钱包类型：</span>
		      		<select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="purseBalanceType" name="purseBalanceType">
            			<option value="" <c:if test="${params['purseBalanceType'] eq ''}">selected='selected'</c:if>>全部</option>
					        <c:forEach items="${purseBalanceTypeList}" var="item">
					             <option value="${item.code_id}" <c:if test="${params['purseBalanceType'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
					        </c:forEach>
					</select>
		      </li> --%>
		      <li><span style="width: 60px;">审核方式:</span>
		      <%-- <input type="text" style="width: 100px;" value="${params['checkPerson']}" name="checkPerson" /> --%>
		        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="checkPerson" name="checkPerson">
           			<option value="" <c:if test="${params['checkPerson'] eq ''}">selected='selected'</c:if>>全部</option>
			        <c:forEach items="${checkerList}" var="item">
			             <option value="${item.check_person}" <c:if test="${params['checkPerson'] eq item.check_person}">selected='selected'</c:if>>${item.check_person}</option>	
			        </c:forEach>
				</select>
		      </li>
		      <li><span style="width: 60px;">代付通道:</span>
		        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="channel" name="channel">
           			<option value="" <c:if test="${params['channel'] eq ''}">selected='selected'</c:if>>全部</option>
			        <c:forEach items="${channelList}" var="item">
			             <option value="${item.channel_code}" <c:if test="${params['channel'] eq item.channel_code}">selected='selected'</c:if>>${item.channel_name}</option>	
			        </c:forEach>
				</select>
		      </li>
		      <li><span style="width: 60px;">账号  ：</span><input type="text" style="width: 100px;" value="${params['accountNo']}" name="accountNo" /></li>
		      <li><span style="width: 60px;">商户号  ：</span><input type="text" style="width: 100px;" value="${params['merchantNo']}" name="merchantNo" /></li>
	      </ul>
	       <br/>
	      <ul>
	        <li><span span style="width: 60px;">提现时间:</span><input  type="text"  style="width:100px" readonly="readonly" id="startDate" name="startDate" value="${params['startDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'%y-%M-%d'})"></li>
	        <li><span span style="width: 60px;text-align:center;">~</span><input  type="text"  style="width:100px" readonly="readonly" id="endDate" name="endDate" value="${params['endDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'%y-%M-%d'})"></li>
	        <li><span span style="width: 60px;">审核时间:</span><input  type="text"  style="width:100px" readonly="readonly" id="checkStartDate" name="checkStartDate" value="${params['checkStartDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'%y-%M-%d'})"></li>
	        <li><span span style="width: 60px;text-align:center;">~</span><input  type="text"  style="width:100px" readonly="readonly" id="checkEndDate" name="checkEndDate" value="${params['checkEndDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'%y-%M-%d'})"></li>
					
					
	      </ul>
	      
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
        <input   class="button blue medium" type="button" id="select"  value="反选"/>
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>  	
    	<shiro:hasPermission name="BAG_EXTRACTION_QUERY_CHECK">
    	<input class="button blue medium" type="button" id="check"  value="审核"/>
    	</shiro:hasPermission>
    	<input id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()" value="导出" />
    	<input name="reset" class="button blue medium" type="button" id="reset"  value="清空"/>
    </div>
    </form:form>
    
    <div id="total_msg">
			<input class="button blue medium" type="button" id="cashStatistic" value="提现信息统计" />
			<span style="padding-left:50px;">
			<input  type="text"  style="width:80px" readonly="readonly" id="T1ExportDate" name="T1ExportDate" value="${today}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
			<input class="button blue medium" type="button" id="exportT1" value="导出该天T+1自动结算Excel" />
			</span>
			<span style="padding-left: 50px;"><a href="javascript:extractionManualSwitch();">提现开关</a></span>
	</div>
	<div id="cash_total_msg" class="total_msg" style="display:none;"></div>
		
    <a name="_table"></a>
      <div class="tbdata"  id="tbdata"   style="overflow-y:hidden;overflow-x:scroll;width:99%" >
      <table width="100%"   cellspacing="0" class="t2"  >
        <thead>
          <tr>
        <shiro:hasPermission name="BAG_EXTRACTION_QUERY_CHECK">
        <th width="30">选择</th>
        </shiro:hasPermission>
          <th width="30">序号</th>
          <th width="120">操作</th>
          <th width="120">商户号</th>
          <th width="65">账户名</th>
          <th width="55">状态</th>
          <th width="80">提现时间</th>
          <th width="80">手机号</th>
          <th width="80">客户端</th>
          <!-- <th width="58">钱包类型</th> -->
          <th width="85">提现金额（元）</th>
          <th width="85">手续费（元）</th>
          <th width="85">结算金额（元）</th>
          <th width="65">审核方式</th>
          <th width="65">代付通道</th>
          <th width="86">联行号</th>
          <th width="145">开户行</th>
          <th width="135">帐号</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"  >
	           <shiro:hasPermission name="BAG_EXTRACTION_QUERY_CHECK">
	              <td class="center">
				 	 <c:choose>
					  <c:when test="${item.open_status eq '1'}"><input type="checkbox"  disabled="disabled"  name="ids"  value="${item.id}"/></c:when>
					  <c:when test="${item.open_status eq '0'}"><input type="checkbox" name="ids"  value="${item.id}"/></c:when> 
					  <c:otherwise><input type="checkbox" disabled name="ids"  value="${item.id}"/></c:otherwise>
	    			 </c:choose>
	    			 <input type="hidden" class="hiddenOpenStatus" value="${item.open_status}"/>
		          </td>
	          </shiro:hasPermission>
	        
	          <td class="center"><span class="center  orderNo">${status.count}</span></td>
	         
	          <td align="center">
			  <c:choose>
			       <c:when test="${item.open_status eq '0'}">
                 		  <shiro:hasPermission name="PHONE_EXTRACTIONQUERY_CHECK"> 
                 		  <a href="javascript:checkRecharge(${item.id});"  title="审核">审核</a>|
			              </shiro:hasPermission>     
			              <shiro:hasPermission name="PHONE_EXTRACTIONQUERY_CHECKFAILED"> 
                 		  <a href="javascript:checkfailed(${item.id});"  title="审核失败">审核失败</a>|
		                  </shiro:hasPermission> 
			       </c:when>
			  </c:choose>
			 <shiro:hasPermission name="PHONE_EXTRACTIONQUERY_DETAIL"> <a href="javascript:showDetail(${item.id});"  title="详情">详情</a>
			 </shiro:hasPermission>
			 </td>
			  <td class="center">${item.merchant_no}</td>
	          <td align="center" style="word-wrap : break-word ;">${item.account_name}</td>
	          <td align="center">${item.openStatusDesc}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td align="center">${item.mobile_no}</td>
	          <td align="center">${item.app_name}</td>
	          <%-- <td align="center">${item.purseBalanceTypeDesc}</td> --%>
	          <td style="word-wrap : break-word ;">${item.amount}</td>
	          <td align="center">${item.fee}</td>
	          <td style="word-wrap : break-word ;">${item.settle_amount}</td>
	          <td style="word-wrap : break-word ;">${item.check_person}</td>
	          <td style="word-wrap : break-word ;">${item.cashChannelDesc}</td>
	          <td style="word-wrap : break-word ;">${item.cnaps}</td>
	          <td style="word-wrap : break-word ;">${item.bank_name}</td>
	          <td style="word-wrap : break-word ;">${item.account_no}</td> 
			  
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
