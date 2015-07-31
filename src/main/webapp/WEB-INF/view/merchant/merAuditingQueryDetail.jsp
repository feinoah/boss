<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户审核统计>商户审核统计详情</div>
    <div id="search" style="height: 60px">
    	<div id="title">商户审核统计详情</div>
    	<font style="color: red;">说明：审核详情显示审核商户数少于统计总数实为正常现象，原因是：部分商户在审核(通过/不通过)后可能已被删除。 </font>
		<div class="search_btn">
    	<input   class="button blue medium" type="button" id="query"  value="返回"  onclick="javascript:location.href='${ctx}/mer/merAuditingQuery'"/>
    </div>
    </div>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="30">序号</th>
          <th width="120">所属代理商</th>
          <th width="110">商户简称</th>
          <th width="110">商户编号</th>
          <th width="115">录入时间</th>
          <th width="50">审核状态</th>
          <th width="130">描述</th>
          <th width="115">审核时间</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td><u:substring length="25" content="${item.agent_name}" /></td>
	           <td>${item.merchant_short_name}</td>
	           <td>${item.merchant_no}</td>
	           <td><fmt:formatDate value="${item.pct}" type="both"/></td>
	           <td>
	           			<c:if test="${item.open_status eq 5 }">审核成功</c:if>
	           			<c:if test="${item.open_status eq 3 }">审核失败</c:if>
	           </td>
	           <td><u:substring length="25" content="${item.examination_opinions}" /></td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	        </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
</body>
