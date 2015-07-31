<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理&gt;龙宝查询</div>
   <form:form id="longBaoQuery" action="${ctx}/longBao/longBaoQuery" method="post">
    <div id="search">
    	<div id="title">龙宝查询</div>
	      <ul>
	        <li><span style="width: 65px;">订单号：</span><input type="text"  value="${params['order_no']}" name="order_no" /></li>
	        <li><span style="width: 55px;">卡号：</span><input type="text"  value="${params['card_no']}" name="card_no" /></li>
	        <li><span>创建时间：</span>
			 	<input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			</li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submit"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="30px;">序号</th>
          <th width="124px;">订单号</th>
          <th width="50px;" >总额</th>       
          <th width="79px;">帐号</th>
          <th width="115px;">卡号</th>
          <th width="160px;">Hmac</th>
          <th width="30px;">状态</th>
          <th width="87px;">龙宝响应码</th>
          <th width="114px;">创建时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="WORD-WRAP: break-word">${item.order_no}</td>
	          <td class="center"  style="WORD-WRAP: break-word">${item.amount}</td>
	          <td style="WORD-WRAP: break-word" >${item.account_no}</td>
	          <td style="WORD-WRAP: break-word">${item.card_no}</td>
	          <td style="WORD-WRAP: break-word">${item.hmac}</td>
	          <td style="WORD-WRAP: break-word">
	          <c:choose>
	             <c:when test="${item.status eq 'success'}">成功</c:when>
	             <c:when test="${item.status eq 'faild'}">失败</c:when>
	             <c:otherwise>${item.status}</c:otherwise>
	          </c:choose>
	          </td>
	          <td style="WORD-WRAP: break-word">
	          <c:choose>
	             <c:when test="${item.longbao_response_code eq '0'}">正常</c:when>
	             <c:when test="${item.longbao_response_code eq '1'}">手机号码不存在</c:when>
	             <c:when test="${item.longbao_response_code eq '2'}">金额错误</c:when>
	             <c:when test="${item.longbao_response_code eq '10'}">md5值不正确</c:when>
	             <c:otherwise>${item.longbao_response_code}</c:otherwise>
	          </c:choose>
	          </td>
			  <td style="WORD-WRAP: break-word"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
</body>
