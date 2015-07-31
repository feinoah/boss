<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	function showDetail(id)
		{
			$.dialog({title:'充值详情',width: 700,height:220,resize: false,lock: true,max:false,content: 'url:bagRechargeDetail?id='+id+'&layout=no'});
		}
	
	  //提现		
	  function bagExtractionQuery(mobile_no){
             window.location.href='bagExtractionQuery?mobile_no='+mobile_no;
        }
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包充值查询</div>
   
   <form:form id="bagRechargeQuery" action="${ctx}/purse/bagRechargeQuery" method="post">
    <div id="search">
    	<div id="title">钱包充值查询</div>
	      <ul>
	        <li><span>手机号：</span><input type="text"  value="${params['mobile_no']}" name="mobile_no" /></li>	        
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submit"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="20%">手机号</th>       
          <th width="25%">金额</th>
          <th width="25%">创建时间</th>
          <th width="25%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center">${item.mobile_no}</td>
	          <td align="center">${item.amount}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			 <shiro:hasPermission name="PHONE_RECHARGEQUERY_DETAIL"> <a href="javascript:showDetail(${item.id});">详情</a>|
			 </shiro:hasPermission>
			 <shiro:hasPermission name="PHONE_EXTRACTION"> <a href="javascript:bagExtractionQuery(${item.mobile_no});">提现</a>
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
</body>
