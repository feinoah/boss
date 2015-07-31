<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	
	
	function showDetail(id)
	{
		  $.dialog({title:'冲正详情',width: 700,height:400,
			resize: true,lock: true,max:false,
			content: 'url:bagReversalDetail?id='+id+'&layout=no'
					});
	}
	
	$(function(){
	$("#countBagReverSal").on("click", function() {
			
			  $("#countBagReverSal").attr('disabled',true);
			  $("#countBagReverSal").attr('value',"统计中，请稍后...");
			  var param = $("form:first").serialize();
			  
			  $.post("${ctx}/purse/countBagReverSal", param, function(data) {
				     var html = cashStatisticToHtml(data);
				     
			       $("#total_msg").html(html);
			       $("#total_msg").show();
				   
			});
			  
			  
		});
});
		
		function cashStatisticToHtml(totalMsg) {
			var html ="\<ul>";
			html +="\<li style='width:200px'>\<b>冲正金额："+ totalMsg.totalCount+ "笔/"+totalMsg.totalAmount+"元</b>\</li>";
			html +="\<li style='width:200px'>\<b>冲正成功："+ totalMsg.totalSuccess+ "</b>笔</li>";
			html +="\<li style='width:200px'>\<b>冲正失败："+ totalMsg.totalFail+ "</b>笔</li>";
			html +="\</ul>";
			html+="<div style='clear:both;'></div>";
			return html;
		}

		$(function(){
			$("#reset").click(function(){
				$(":text").val("");
				$("select").val("-1");
			})
		})
		
	
	
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包冲正查询</div>
   
   <form:form id="bagReversalQuery" action="${ctx}/purse/bagReversalQuery" method="post">
    <div id="search">
    	<div id="title">钱包需手工冲正提现记录查询</div>
	      <ul>
					<li>
					<span >提现时间:</span>
  	         		<input  type="text"  style="width:102px" readonly="readonly" id="startDate" name="startDate" value="${params['startDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
			 					&nbsp;&nbsp;~&nbsp;&nbsp;
			 					<input  type="text" style="width:102px" readonly="readonly" id="endDate" name="endDate" value="${params['endDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
					</li>
					<li><span>手机号：</span><input type="text"  value="${params['mobileNo']}" name="mobileNo" /></li>
	        <li><span>账户名：</span><input type="text"  value="${params['accountName']}" name="accountName" /></li>
	        <%-- <li><span>操作状态：</span>
						<select  style="padding: 2px; width: 80px" name="operaStatus">
							<option <c:out value="${params['operaStatus'] eq '-1'?'selected':'' }"/> value="-1">全部</option>
							<option <c:out value="${params['operaStatus'] eq '0'?'selected':'' }"/> value="0">已操作</option>
							<option <c:out value="${params['operaStatus'] eq '1'?'selected':'' }"/> value="1">未操作</option>
						</select>
					
					</li> --%>
					<li>
						<span>冲正状态：</span>
						<select  style="padding: 2px; width: 100px" name="bankRemark">
							<option <c:out value="${params['bankRemark'] eq '-1'?'selected':'' }"/> value="-1">全部</option>
							<option <c:out value="${params['bankRemark'] eq '1'?'selected':'' }"/> value="1">冲正成功</option>
							<option <c:out value="${params['bankRemark'] eq '0'?'selected':'' }"/> value="0">冲正失败</option>
							<option <c:out value="${params['bankRemark'] eq '3'?'selected':'' }"/> value="3">发送冲正超时</option>
							<option <c:out value="${params['bankRemark'] eq '4'?'selected':'' }"/> value="4">无需冲正或已冲正</option>
						</select>
					</li>
				</ul>
				<div class="clear"></div>
				
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="button" id="reset"  value="清空"/>
    </div>
    </form:form>
    <div id="total_msg" class="total_msg">
			<input class="button blue medium" type="button" id="countBagReverSal" value="冲正信息统计" />
		</div>
    
    <a name="_table"></a>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="11%">手机号</th>
          <th width="11%">客户端</th>
          <th width="10%">账户名</th>
          <th width="14%">卡号</th>
          <th width="12%">金额</th>
          <th width="8%" >提现状态</th> 
          <th width="8%">冲正状态</th>
          <th width="10%">提现时间</th>
          <th width="20%">描述</th>
          <th width="8%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"
	           <c:if test="${item.status=='2'||item.status=='3'}">style="color:red;"</c:if> 
	           <c:if test="${item.status=='4'}">style="color:blue;"</c:if> 
	        >
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.app_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.account_name}</td>
	          <td class="center"  style="word-break: break-all ;"><u:cardcut content="${item.account_no}" /></td>
	          <td class="center"  style="word-break: break-all ;">${item.amount}元</td>
	          <td class="center">${item.statusDesc}</td>
	          <td class="center">${item.backStatusDesc}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td class="center" >
										<c:choose>  
										    <c:when test="${f:length(item.back_remark) > 15}">  
										        <span  title="${item.back_remark}"><c:out value="${f:substring(item.back_remark, 0, 15)}..." /> </span> 
										    </c:when>  
										   <c:otherwise>  
										      <c:out value="${back_remark}" />  
										    </c:otherwise>  
										</c:choose>
           </td>
			  <td align="center">
			 
			 <a href="javascript:showDetail(${item.id});">详情</a>
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
