<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
		function userAuthDel(id){
			if(!confirm("是否删除权限？"))
			{
				return;
			}

			$.post(
					'${ctx}/auth/userAuthDel',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("删除权限成功");
							location=location ;
						}else if(data == 0){
							alert("请先删除其子类权限节点");
							location=location ;
						}else{
							alert("没有此权限");
							location=location ;
						}
					}
			);
		}

	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>权限管理</div>
   <form:form id="merQuery" action="${ctx}/auth/userAuthQuery" method="post">
   <div id="search">
    	<div id="title">权限管理查询</div>
	      <ul>
	        <li><span>上级权限：</span>
	        <select id="status"  name="parent_id" style="padding:2px"  class="required" >
	        	<c:forEach items="${authList}" var="authItem" varStatus="status">
	         		<option value="${authItem.id }" <c:out value="${params['parent_id'] eq authItem.id?'selected':'' }"/>>${authItem.auth_name }</option>
	         	</c:forEach>
	         </select>
	        <li><span>权限名称：</span><input type="text"  value="${params['auth_name']}" name="auth_name" /></li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<shiro:hasPermission name="SYSTEM_USERAUTH_ADD">
    	<input class="button blue medium" type="button" id="submitAdd" onclick="javascript:window.location.href='${ctx}/auth/userAuthInput'"  value="增加"/>
    	</shiro:hasPermission>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="20%">权限名称</th>
          <th width="25%">权限代码</th>
          <th width="25%">上级权限</th>
          <th width="10%">权限类别</th>
          <th width="15%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center" style="WORD-WRAP: break-word"><span class="center">${item.auth_name}</span></td>
	          <td class="center" style="WORD-WRAP: break-word"><span class="center">${item.auth_code}</span></td>
	          <td align="center" style="WORD-WRAP: break-word">
	          <c:choose>
	          	<c:when test="${empty item.parent_name}">
	          	无
	          	</c:when>
	          	<c:otherwise>
	          	${item.parent_name}
	          	</c:otherwise>
	          </c:choose>
	          </td>
	          <td align="center">
	          	<c:choose>
	          		<c:when test="${item.category_id eq 1}">
	          		  菜单权限 
	          		</c:when>
	          		<c:otherwise>
	          		   链接权限 
	          		</c:otherwise>
	          	</c:choose>
	          </td>
			  <td align="center">
			  <shiro:hasPermission name="SYSTEM_USERAUTH_DELETE"><a href="javascript:userAuthDel(${item.id});">删除</a></shiro:hasPermission> 
			 &nbsp; 
			 <shiro:hasPermission name="SYSTEM_USERAUTH_UPDATE">
			 	|<a href="userAuthInput?id=${item.id}">修改</a>
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
  <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
