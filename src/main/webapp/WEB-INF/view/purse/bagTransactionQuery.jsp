<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	$(function(){
	

		$("#exportButton").click(function() {
				$("#bagTransaction").attr({action : "${ctx}/purse/bagTransactionExport"}).submit();
				$("#bagTransaction").attr({action : "${ctx}/purse/bagTransactionQuery"});
		});
		
		
		$("#transactionStatistic").on("click", function() {
			
			  $("#transactionStatistic").attr('disabled',true);
			  $("#transactionStatistic").attr('value',"统计中，请稍后...");
			  
			  var startDate=$("#startDate").attr('value');
			  var endDate=$("#endDate").attr('value');
			  $.post("${ctx}/purse/transactionStatistic", {startDate:startDate,endDate:endDate}, function(data) {
				     var html = transactionStatisticToHtml(data,startDate,endDate);
			       $("#transaction_total_msg").html(html);
			       $("#transaction_total_msg").show();
				     
     	     $("#transactionStatistic").attr('value',"提现信息统计");
     	     $("#transactionStatistic").attr('disabled',false);
				});
			  
			  
		});

	});
	
	function transactionStatisticToHtml(totalMsg,startDate,endDate) {
		var data=totalMsg.split("#");
		var html ="\<ul>";
		if(data[0]!="success"){
			html +="\<li>"+data[1]+"\</li>";
		}else{
			html +="\<li style='width: 720px;text-align: left;'>\<b>"+startDate+"~"+endDate+"[&nbsp;&nbsp;出账："+data[2]+"元，入账："+data[3]+"元&nbsp;&nbsp;]\</b>\</li>";

			html+="\<br/>";
			html +="\<li style='width: 240px;text-align: left;'>\<b>当日总余额："+data[4]+"元\</b>\</li>";
			html +="\<li style='width: 240px;text-align: left;'>\<b>历史总余额："+data[5]+"元\</b>\</li>";
			html +="\<li style='width: 240px;text-align: left;'>\<b>总余额："+data[6]+"元\</b>\</li>";
		}
		
		html +="\</ul>";
		html+="<div style='clear:both;'></div>";
		return html;
	}
	
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包交易查询</div>
   
   <form:form id="bagTransaction" action="${ctx}/purse/bagTransactionQuery" method="post">
    <div id="search">
    	<div id="title">钱包交易查询</div>
	      <ul>
	          <li><span style="width: 60px;">手机号 ：</span><input type="text" style="width: 100px;" value="${params['mobileNo']}" name="mobileNo" id="mobileNo"  /></li>  
			      <li><span style="width: 60px;">类型：</span>
			        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="transType" name="transType">
		            			<option value="" <c:if test="${params['transType'] eq ''}">selected='selected'</c:if>>全部</option>
							        <c:forEach items="${purseTransTypeList}" var="item">
							             <option value="${item.code_id}" <c:if test="${params['transType'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
					        </c:forEach>
					    </select>
	        </li>	    
			      <li><span style="width: 60px;">状态：</span>
			        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="status" name="status">
		            			<option value="" <c:if test="${params['status'] eq ''}">selected='selected'</c:if>>全部</option>
							        <c:forEach items="${purseTransactionStatusList}" var="item">
							             <option value="${item.code_id}" <c:if test="${params['status'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
					        </c:forEach>
					    </select>
	        </li>	    
				</ul>
				<br/>
				<ul>
					<li><span span style="width: 60px;">交易时间:</span><input
						type="text" style="width: 100px" readonly="readonly"
						id="startDate" name="startDate" value="${params['startDate']}"
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"></li>
						
					<li><span span style="width: 60px; text-align: center;">~</span><input
						type="text" style="width: 100px" readonly="readonly" id="endDate"
						name="endDate" value="${params['endDate']}"
						onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"></li>
				</ul>

				<div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit"   value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    	<input class="button blue medium" type="button" id="exportButton"  value="导出"/>
    </div>
    </form:form>
    
     <div id="total_msg">
			<input class="button blue medium" type="button" id="transactionStatistic" value="交易信息统计" />
		</div>
		<div id="transaction_total_msg" class="total_msg" style="display:none"></div>
    <a name="_table"></a>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="40%">交易内容</th>
          <th width="6%">类型</th>
          <th width="16%">金额</th>
          <th width="8%" >状态</th> 
          <th width="12%" >手机号</th> 
          <th width="12%" >客户端</th> 
          <th width="10%">交易时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"
	           <c:if test="${item.result_status=='0'||item.result_status=='2'}">style="color:red;"</c:if> 
	        >
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.msg}</td>
	          <td class="center"  style="word-break: break-all ;">${item.transTypeDesc}</td>
	          <td class="center"  style="word-break: break-all ;">${item.amount}元</td>
	          <td class="center">${item.transactionStatusDesc}</td>
	          <td class="center">${item.user_name}</td>
	          <td class="center">${item.app_name}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
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
