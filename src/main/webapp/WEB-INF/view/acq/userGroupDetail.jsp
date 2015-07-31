<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>用户组详情</div>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="10%">用户名</th>
          <th width="11%">真实姓名</th>
          <th width="16%">email</th>
          <th width="5%">状态</th>
          <th width="20%">最后一次登录时间</th>
          <th >创建时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center">${item.user_name}</td>
	          <td class="center">${item.real_name}</td>
	          <td align="center" style="WORD-WRAP: break-word">${item.email}</td>
	          <td align="center">
	          <c:choose>
				  <c:when test="${item.status eq '1'}">启用</c:when>
				  <c:when test="${item.status eq '0'}">停用</c:when> 
			  </c:choose> 
	          </td>
	          <td align="center"><fmt:formatDate value="${item.last_login_time}" type="both"/></td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>
	<div class="search_btn clear">
     		<input class="button blue" type="button" id="backButton"  value="返回" onclick="javascript:window.location.href='${ctx}/acq/userGroupQuery'"/>
    </div>
  </div>
  
</body>
