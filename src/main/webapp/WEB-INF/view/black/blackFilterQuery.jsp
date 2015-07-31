<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>

</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：交易管理>黑名单过滤</div>
   <form:form id="merQuery" action="${ctx}/black/blackFilterQuery" method="post">
   <div id="search">
    	<div id="title">黑名单过滤查询</div>
	      <ul>
	        <li><span>交易类型：</span>
	         	<select style="padding:2px;width:120px" id="transType" name="transType">
	         		<option value="-1" <c:out value="${params['transType'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="PURCHASE" <c:out value="${params['transType'] eq 'PURCHASE'?'selected':'' }"/>>消费</option>
	         		<option value="PURCHASE_VOID" <c:out value="${params['transType'] eq 'PURCHASE_VOID'?'selected':'' }"/>>消费撤销</option>
	         		<option value="PURCHASE_REFUND" <c:out value="${params['transType'] eq 'PURCHASE_REFUND'?'selected':'' }"/>>退货</option>
	         		<option value="REVERSED" <c:out value="${params['transType'] eq 'REVERSED'?'selected':'' }"/>>冲正</option>
	         		<option value="BALANCE_QUERY" <c:out value="${params['transType'] eq 'BALANCE_QUERY'?'selected':'' }"/>>余额查询</option>
	         	</select>
		 </li>
		 <li><span>商户编号：</span>
				<input type="text" style="width:90px;" name="merchant_no" value="${params.merchant_no}"/>			        
	         	 </li>
	        <li><span style="width:90px;">交易银行卡号：</span>
				<input type="text" style="width:90px;" name="account_no" value="${params.account_no}"/>			        
	         	 </li>
	        <li><span>身份证号：</span>
				<input type="text" style="width:90px;" name="id_card_no" value="${params.id_card_no}"/>			        
	         	 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="15%">交易类型</th>
          <th width="25%">拦截条件</th>
          <th width="15%">创建时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
	          	  <c:when test="${item.trans_type eq 'PURCHASE'}">消费</c:when>
				  <c:when test="${item.trans_type eq 'PURCHASE_VOID'}"><span class="font_red">消费撤销</span></c:when> 
				  <c:when test="${item.trans_type eq 'PURCHASE_REFUND'}"><span class="font_red">退货</span></c:when>
				  <c:when test="${item.trans_type eq 'REVERSED'}"><span class="font_red">冲正</span></c:when>
				  <c:when test="${item.trans_type eq 'BALANCE_QUERY'}"><span class="font_gray">余额查询</span></c:when>  
				  <c:otherwise>${item.trans_type }</c:otherwise>
	          	</c:choose>
	         </td>
	          <td class="center" style="WORD-WRAP: break-word"><span class="center">${item.filter_condition}</span></td>
	          <td align="center" style="WORD-WRAP: break-word"><fmt:formatDate value="${item.create_time}" type="both"/></td>
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
