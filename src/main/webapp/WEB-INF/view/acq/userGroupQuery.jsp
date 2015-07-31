<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	
	  function showDetail(id)
		{
		  	window.location.href='userGroupDetail?id='+id;
// 			$.dialog({title:'组成员',width: 650,height:350,resize: false,lock: true,max:false,content: 'url:terDetail?id='+id+'&layout=no'});
		}
	
		function userGroupDel(id){
			if(!confirm("是否删除用户组？"))
			{
				return;
			}

			$.post(
					'${ctx}/acq/userGroupDel',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("删除用户组成功");
							location=location ;
						}else if(data==2){
							alert("该用户组存在可用用户，不能删除");
							location=location ;
						}
					}
			);
		}
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>用户组管理</div>
   
   <form:form id="merQuery" action="${ctx}/acq/sysUserQuery" method="post">
    <div class="search_btn">
    	<input class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='${ctx}/acq/userGroupInput'"  value="增加组"/>
    <shiro:hasPermission name="SYSTEM_USERGROUPQUERY_ADD">
    	</shiro:hasPermission>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="20%">组名称</th>
          <th width="25%">组说明</th>
          <th width="25%">创建时间</th>
          <th width="25%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center">${item.group_name}</td>
	          <td class="center">${item.group_desc}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center"><shiro:hasPermission name="SYSTEM_USERGROUPQUERY_DELETE"><a href="javascript:userGroupDel(${item.id});">删除</a>
			 </shiro:hasPermission>
			   <shiro:hasPermission name="SYSTEM_USERGROUPQUERY_MODIFY"><a href="userGroupInput?id=${item.id}">|修改</a></shiro:hasPermission>
			   <shiro:hasPermission name="SYSTEM_USERGROUPQUERY_DETAIL"><a href="javascript:showDetail(${item.id})">|详情</shiro:hasPermission></a></td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
</body>
