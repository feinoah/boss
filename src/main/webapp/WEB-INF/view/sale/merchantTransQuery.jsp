<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/utils.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/transTimeLimit.js" id="transTimeLimit" data="<shiro:hasPermission name="TRANS_TIME_LIMIT">1</shiro:hasPermission><shiro:lacksPermission name="TRANS_TIME_LIMIT">0</shiro:lacksPermission>"></script>
	<script type="text/javascript">
	var root = '${ctx}';
	function createTransCountInfoHtml(totalMsg) {
		var html = '<ul>\<li>\<b>笔数：'+ totalMsg.total_count+ '</b>笔\</li>';
		html += '<li style="width: 210px;text-align: left;">';
		if(totalMsg.total_pur_amount>10000){
			var total_pur_amount_w = totalMsg.total_pur_amount/10000;
			total_pur_amount_w = parseInt(total_pur_amount_w+0.5);
			html +=  "<b>金额约：";
			html +=  total_pur_amount_w;
			html +=  "</b>&nbsp;万元<br>";
		}
		{
			html +=  "<b>总金额为：";
			html +=  totalMsg.total_pur_amount;
			html +=  "</b>&nbsp;元";
		}
		html += '\</li>\<li style="width: 190px">\<b>手续费总金额：'+ totalMsg.total_mer_fee+ '</b>元\</li>\<div class="clear"></div>\</ul>\<div class="clear"></div>';
		return html;
	}
	
		function test(tranId){
			alert(tranId);
			var content = "";
			
			$.dialog({content: 'url:content/content.html'});
			
			var dialog = $.dialog({ height: 540,width:350,title: '欢迎',lock:true,content: '欢迎使用lhgdialog对话框组件！',icon: 'succeed',ok: function(){
		        this.title('警告').content('请注意lhgdialog两秒后将关闭！').lock().time(2);
		        return false;
			    }
			});
		}
		
		
		function showDetail(id, transSource) {
			if ("SMALLBOX_MOBOLE_PHONE" != transSource) {
				$.dialog({
					title : '订单详情',
					width : 650,
					height : 450,
					resize : false,
					lock : true,
					max : false,
					content : 'url:${ctx}/mer/detail?id=' + id + '&layout=no'
				});
			} else {
				$.dialog({
					title : '订单详情',
					width : 650,
					height : 550,
					resize : false,
					lock : true,
					max : false,
					content : 'url:${ctx}/mer/detail?id=' + id + '&layout=no'
				});
			}

		}
		
		function cleanEmpty(){
			$('#agentNo').val("");
			$('#merchantName').val("");totalMsg
			$('#transType').val("-1");
			$('#transStatus').val("-1");
			$('#acqMerchantNo').val("");
			$('#acqTerminalNo').val("");
		}
		
		//显示移小宝刷卡签名dialog
		function showSignReceipt(id) {
			signRecepit_dialog = $.dialog({
				title : '小票',
				width : 350,
				height : 500,
				resize : false,
				lock : true,
				max : false,
				content : 'url:${ctx}/mer/receiptReviewLoad/' + id + '?layout=no'
			});
		}
		
		$(function(){
			$("#btnTransCountInfo").on("click", function() {
				this.value = "统计中，请稍后。。。";
				var params = $("#trans").serialize();
				$.post(root + "/sale/countTransInfo", params, function(data) {
					var html = createTransCountInfoHtml(data);
					$("#total_msg").html(html);
				});
			});
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：销售管理>交易查询</div>
   
   <form:form id="trans" action="${ctx}/sale/trans" method="post">
    <div id="search">
    	<div id="title">交易查询</div>
	      <ul>
	      	      <li style="width:216px"><span>代理商名称：</span><select name="agentNo" style="width:128px;padding:2px">
<option value="">全部</option> 
  <c:forEach items="${list1.content}" var="item"> 
      <option value="${item.agent_no}" <c:if test="${params['agentNo'] eq item.agent_no}">selected="selected"</c:if>>${item.agent_name}</option> 
  </c:forEach> 
</select></li>
	         <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	         <li><span>交易类型：</span> 
	         	<select style="padding:2px;width:73px" id="transType" name="transType">
	         		<option value="-1" <c:out value="${params['transType'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="PURCHASE" <c:out value="${params['transType'] eq 'PURCHASE'?'selected':'' }"/>>消费</option>
	         		<option value="PURCHASE_VOID" <c:out value="${params['transType'] eq 'PURCHASE_VOID'?'selected':'' }"/>>消费撤销</option>
	         		<option value="PURCHASE_REFUND" <c:out value="${params['transType'] eq 'PURCHASE_REFUND'?'selected':'' }"/>>退货</option>
	         		<option value="REVERSED" <c:out value="${params['transType'] eq 'REVERSED'?'selected':'' }"/>>冲正</option>
	         		<option value="BALANCE_QUERY" <c:out value="${params['transType'] eq 'BALANCE_QUERY'?'selected':'' }"/>>余额查询</option>
	         	</select>
			 </li>
			 
			 
			 
			 
			 <li><span>交易状态：</span> 
	         	<select style="padding:2px;width:73px"  id="transStatus" name="transStatus">
	         		<option value="-1" <c:out value="${params['transStatus'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="SUCCESS" <c:out value="${params['transStatus'] eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
	         		<option value="FAILED" <c:out value="${params['transStatus'] eq 'FAILED'?'selected':'' }"/>>已失败</option>
	         		<option value="INIT" <c:out value="${params['transStatus'] eq 'INIT'?'selected':'' }"/>>初始化</option>
	         		<option value="REVERSED" <c:out value="${params['transStatus'] eq 'REVERSED'?'selected':'' }"/>>已冲正</option>
	         		<option value="REFUND" <c:out value="${params['transStatus'] eq 'REFUND'?'selected':'' }"/>>已退货</option>
	         		<option value="REVOKED" <c:out value="${params['transStatus'] eq 'REVOKED'?'selected':'' }"/>>已撤销</option>
	         		<option value="SETTLE" <c:out value="${params['transStatus'] eq 'SETTLE'?'selected':'' }"/>>已结算</option>
	         		<option value="SETTLE" <c:out value="${params['transStatus'] eq 'FREEZED'?'selected':'' }"/>>已冻结</option>
	         	</select>
			 </li>
	      </ul>
	      <div class="clear"></div>
	      <ul>
	      	<li style="width: 216px;"><span>收单商户号：</span><input type="text"  value="${params['acqMerchantNo']}" id="acqMerchantNo" name="acqMerchantNo" /></li>
	      	<li><span style="width: 90px;">收单终端号：</span><input type="text"  value="${params['acqTerminalNo']}" id="acqTerminalNo" name="acqTerminalNo" /></li>
	      	<li><span>交易时间：</span>
			 	<input id="createTimeBegin" type="text" style="width: 102px"
							name="createTimeBegin" value="${params['createTimeBegin']}" >
						~
						<input id="createTimeEnd" type="text" style="width: 102px"
							name="createTimeEnd" value="${params['createTimeEnd']}" >
					 </li>
	      </ul>
	      <div class="clear"></div>
	       <ul>
	       	<li style="width: 216px;"><span>交易卡号：</span><input type="text"  value="${params['cardNo']}" name="cardNo" /></li>
	      	<li><span style="width: 90px;">参考号：</span><input type="text"  value="${params['referenceNo']}" name="referenceNo" /></li>
	      	<li><span style="width: 77px;">订单号：</span><input type="text"  value="${params['id']}" name="id" /></li>
	      	<li style="width: 216px;">
				<span>交易来源：</span>
				<u:POSModelSelectTag sname="pos_type" style="padding:2px;width:87px" tablename="pos_type" fleldAsSelectValue="pos_model"  type="2" fleldAsSelectText="pos_model_name" value="${params['pos_model']}" otherOptions="needAll"/>
				<%--<select style="padding: 2px; width: 103px" id="transSource" name="transSource">
					<option value="-1" <c:out value="${params['transSource'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="COM_MOBILE_PHONE" <c:out value="${params['transSource'] eq 'COM_MOBILE_PHONE'?'selected':'' }"/>>企业版</option>
							<option value="POS" <c:out value="${params['transSource'] eq 'POS'?'selected':'' }"/>>POS</option>
							<option value="SMALLBOX_MOBOLE_PHONE" <c:out value="${params['transSource'] eq 'SMALLBOX_MOBOLE_PHONE'?'selected':'' }"/>>移小宝</option>
					        <option value="DOT" <c:out value="${params['transSource'] eq 'DOT'?'selected':'' }"/>>点付宝</option>
					        <option value="NEW_LAND_ME30" <c:out value="${params['transSource'] eq 'NEW_LAND_ME30'?'selected':'' }"/>>YPOS08</option>
					        <option value="NEW_LAND_ME31" <c:out value="${params['transSource'] eq 'NEW_LAND_ME31'?'selected':'' }"/>>YPOS09</option>
					        <option value="SPOS" <c:out value="${params['transSource'] eq 'SPOS'?'selected':'' }"/>>超级刷</option>
					        <option value="SPOS_2" <c:out value="${params['transSource'] eq 'SPOS_2'?'selected':'' }"/>>超级刷II</option>
					        <option value="TY_POS" <c:out value="${params['transSource'] eq 'TY_POS'?'selected':'' }"/>>天喻收银台</option>
					        <option value="TY" <c:out value="${params['transSource'] eq 'TY'?'selected':'' }"/>>移联商通III</option>
					        <option value="M368" <c:out value="${params['transSource'] eq 'M368'?'selected':'' }"/>>移联商通Ⅴ</option>
				</select>
		    --%></li>
	       </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="button" onclick="cleanEmpty()"  value="清空"/>
    </div>
    </form:form>
	<div id="total_msg" class="total_msg">
			<input class="button blue medium" type="button" id="btnTransCountInfo" value="统计交易信息" />
	</div>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="35">序号</th>
          <th width="130">商户简称</th>
          <th width="70">交易类型</th>
          <!-- <th width="70">卡种</th> -->
          <th width="120">交易卡号</th>
          <th width="90">金额(元)</th>
          <th width="60">状态</th>
          <th width="70">冻结状态</th>
          <th width="130">创建时间</th>
           <th width="60">操作</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td><u:substring length="10" content="${item.merchant_short_name}"/></td>
	          <td>
	          <c:choose>
				  <c:when test="${item.trans_type eq 'PURCHASE'}">消费</c:when>
				  <c:when test="${item.trans_type eq 'PURCHASE_VOID'}"><span class="font_red">消费撤销</span></c:when> 
				  <c:when test="${item.trans_type eq 'PURCHASE_REFUND'}"><span class="font_red">退货</span></c:when>
				  <c:when test="${item.trans_type eq 'REVERSED'}"><span class="font_red">冲正</span></c:when>
				  <c:when test="${item.trans_type eq 'BALANCE_QUERY'}"><span class="font_gray">余额查询</span></c:when>  
				  <c:otherwise>${item.trans_type }</c:otherwise>
			  </c:choose></td>
	         <%--  <td>
	          <c:choose>
				  <c:when test="${item.card_type eq 'CREDIT_CARD'}">贷记卡</c:when>
				  <c:when test="${item.card_type eq 'DEBIT_CARD'}">借记卡</c:when> 
				  <c:when test="${item.card_type eq 'SEMI_CREDIT_CARD'}">准贷记卡</c:when>
				  <c:otherwise>${item.card_type}</c:otherwise>
			  </c:choose></td> --%>
	          <td>
				<shiro:hasPermission name="TRANS_CARD_NO">${item.account_no}</shiro:hasPermission>
				<shiro:lacksPermission name="TRANS_CARD_NO"><u:cardcut content="${item.account_no}" /></shiro:lacksPermission>
			  </td>
	          <td  align="right">
	          <c:choose>
					<c:when test="${item.trans_amount == null}">***</c:when>		
					<c:otherwise>
						${item.trans_amount}
					</c:otherwise>
			</c:choose>元 </td>
	          <td >
	          <c:choose>
				  <c:when test="${item.trans_status eq 'SUCCESS'}">已成功</c:when>
				  <c:when test="${item.trans_status eq 'REVOKED'}">已撤销</c:when>
				  <c:when test="${item.trans_status eq 'FAILED'}"><span class="font_red">已失败</span></c:when>
				  <c:when test="${item.trans_status eq 'REFUND'}">已退货</c:when>
				  <c:when test="${item.trans_status eq 'REVERSED'}"><span class="font_red">已冲正</span></c:when>
				  <c:when test="${item.trans_status eq 'SETTLE'}">已结算</c:when> 
				  <c:when test="${item.trans_status eq 'INIT'}"><span class="font_red">初始化</span></c:when>
			  </c:choose> 
			  </td>
			 <td>
				<c:if test="${item.freeze_status  eq '1' }">
						<span class="font_red">已冻结</span>
				</c:if>
				<c:if test="${item.freeze_status  eq '0' }">
						正常
				</c:if>
			</td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td class="center">
	          <shiro:hasPermission name="A_SALE_MANAGER_DETAIL">
		         <a href="javascript:showDetail('${item.id}','${item.trans_source}');">详情</a>
		       </shiro:hasPermission>
		       <shiro:hasPermission name="SALE_TRANS_SIGN">
		       	 <c:if test="${item.trans_source eq 'SMALLBOX_MOBOLE_PHONE'&&item.trans_type ne 'BALANCE_QUERY'}">
					<a href="javascript:showSignReceipt('${item.id}');">小票</a>
				</c:if>
				<c:if test="${item.trans_source eq 'DOT'&&item.trans_type ne 'BALANCE_QUERY'}">
					<a href="javascript:showSignReceipt('${item.id}');">小票</a>
				</c:if>
				<c:if test="${item.trans_source eq 'NEW_LAND_ME30'&&item.trans_type ne 'BALANCE_QUERY'}">
					<a href="javascript:showSignReceipt('${item.id}');">小票</a>
				</c:if>
				<c:if test="${item.trans_source eq 'NEW_LAND_ME31'&&item.trans_type ne 'BALANCE_QUERY'}">
					<a href="javascript:showSignReceipt('${item.id}');">小票</a>
				</c:if>
				<c:if test="${item.trans_source eq 'SPOS'&&item.trans_type ne 'BALANCE_QUERY'}">
					<a href="javascript:showSignReceipt('${item.id}');">小票</a>
				</c:if>
				</shiro:hasPermission>
	          </td>
	        </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>
  </div>
  <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
