<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
		
		function showDetail(id)
		{
			$.dialog({title:'代理商详情',width: 750,height:450,resize: false,lock: true,max:false,content: 'url:${ctx}/agent/agentDetail?id='+id+'&layout=no'});
		}
		
		
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：销售管理>代理商查询</div>
   
   <form:form id="agentQuery" action="${ctx}/sale/agentQuery" method="post">
    <div id="search">
    	<div id="title">代理商查询</div>
	      <ul>
	      
	      <li style="width:216px"><span>代理商名称：</span><select name="agentName" style="width:128px;padding:2px"> 
<option value="">全部</option> 
  <c:forEach items="${list1.content}" var="item"> 
      <option value="${item.agent_name}" <c:if test="${params['agentName'] eq item.agent_name}">selected="selected"</c:if>>${item.agent_name}</option> 
  </c:forEach> 
</select></li> 
		 </ul> 
	      <div class="clear"></div>
    </div>
     <div class="search_btn"> 
     	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/> 
     	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/> 
   </div> 
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="50">序号</th>
          <th width="75">代理商编号</th>
          <th>代理商名称</th>
          
          <th width="70">机具总数</th>
          <th width="150">创建时间</th>
          <th width="40">状态</th>
          <th width="150">操作</th>
          </tr>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.agent_no}</td>
	          <td align="left" ><u:substring length="12" content="${item.agent_name}"/></td>
	         
	          <td align="left">${item.terminal_count}</td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td class="center">
	          <c:choose>
				  <c:when test="${item.agent_status eq '1'}">正常</c:when>
				  <c:when test="${item.agent_status eq '2'}">冻结</c:when> 
			  </c:choose>
	          </td>
	          <td class="center">
	           <shiro:hasPermission name="C_SALE_MANAGER_DETAIL">
	         	<a href="javascript:showDetail(${item.id});">详情</a> 
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
