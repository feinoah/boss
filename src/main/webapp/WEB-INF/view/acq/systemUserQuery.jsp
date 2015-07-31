<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
		function restartUser(id){
			if(!confirm("是否重新启用用户？"))
			{
				return;
			}

			$.post(
					'${ctx}/acq/systemUserRestart',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("重新启用用户成功");
							location=location ;
						}
					}
			);
		}
		function delUser(id){
			if(!confirm("是否停用用户？"))
			{
				return;
			}

			$.post(
					'${ctx}/acq/systemUserDel',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("停用用户成功");
							location=location ;
						}
					}
			);
		}
		function resetPassword(id){
			if(!confirm("是否重置用户密码？"))
			{
				return;
			}

			$.post(
					'${ctx}/acq/systemUserReset',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("重置用户密码成功,新密码:eeepay888");
							location=location ;
						}
					}
			);
		}

	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理&gt;用户管理</div>
   
   <form:form id="merQuery" action="${ctx}/acq/sysUserQuery" method="post">
    <div id="search">
    	<div id="title">用户管理查询</div>
	      <ul>
	        <li><span>用户名：</span><input type="text"  value="${params['user_name']}" name="user_name" /></li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<shiro:hasPermission name="SYSTEM_USERQUERY_ADD">
    	<input class="button blue medium" type="button" id="submitAdd" onclick="javascript:window.location.href='${ctx}/acq/systemUserInput'"  value="增加"/>
    	</shiro:hasPermission>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="10%">用户名</th>
          <th width="8%">真实姓名</th>
          <th width="8%">所属组</th>
          <th width="17%">email</th>
          <th width="5%">状态</th>
          <th width="15%">最后一次登录时间</th>
          <th width="15%">创建时间</th>
          <th width="18%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center">${item.user_name}</td>
	          <td class="center">${item.real_name}</td>
	          <td class="center">${item.group_name}</td>
	          <td align="center" style="WORD-WRAP: break-word">${item.email}</td>
	          <td align="center">
	          <c:choose>
				  <c:when test="${item.status eq '1'}">启用</c:when>
				  <c:when test="${item.status eq '0'}">停用</c:when> 
			  </c:choose> 
	          </td>
	          <td align="center"><fmt:formatDate value="${item.last_login_time}" type="both"/></td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			  <shiro:hasPermission name="SYSTEM_USERQUERY_RESETPASSWORD"><a href="javascript:resetPassword(${item.id});">重置密码</a>|
			  </shiro:hasPermission>
			 <shiro:hasPermission name="SYSTEM_USERQUERY_MODIFY"> <a href="systemUserInput?id=${item.id}">修改</a>
			 </shiro:hasPermission>
			 <c:choose>
			 	<c:when test="${item.status eq '1'}">
			 		<shiro:hasPermission name="SYSTEM_USERQUERY_DELETE">
					 |<a href="javascript:delUser(${item.id});">停用</a>
					  </shiro:hasPermission>
			 	</c:when>
			 	<c:otherwise>
			 		<shiro:hasPermission name="SYSTEM_USERQUERY_RESTART">
					 |<a href="javascript:restartUser(${item.id});">重新启用</a>
			  </shiro:hasPermission>
			 	</c:otherwise>
			 </c:choose>
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
