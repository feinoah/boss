<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	function showDetail(id)
		{
			$.dialog({title:'用户详情',width: 700,height:300,resize: false,lock: true,max:false,content: 'url:purDetail?id='+id+'&layout=no'});
		}
	
		function resetPassword(id){
			if(!confirm("是否重置用户密码？"))
			{
				return;
			}
			$.post(		
					'${ctx}/purse/purseUserReset',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("重置用户密码成功,新密码:888888");
							location=location ;
						}
					}
			);
		}
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;用户管理</div>
   
   <form:form id="userQuery" action="${ctx}/purse/userQuery" method="post">
    <div id="search">
    	<div id="title">用户管理查询</div>
	      <ul>
	        <li><span>手机号：</span><input type="text"  value="${params['mobile_no']}" name="mobile_no" /></li>
	        <li><span>真实姓名：</span><input type="text"  value="${params['real_name']}" name="real_name" /></li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="12%">手机号</th>
          <th width="12%">真实姓名</th>       
          <th width="13%">余额</th>
          <th width="17%">最后使用时间</th>
          <th width="16%">最后一次登录时间</th>
          <th width="16%">创建时间</th>
          <th width="13%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.real_name}</td>
	          <td align="center">${item.balance}</td>
	          <td align="center"><fmt:formatDate value="${item.last_use_time}" type="both"/></td>
	          <td align="center"><fmt:formatDate value="${item.last_login_time}" type="both"/></td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			  <shiro:hasPermission name="PHONE_USERQUERY_RESETPASSWORD"><a href="javascript:resetPassword(${item.id});">重置密码</a>|
			  </shiro:hasPermission>
			 <shiro:hasPermission name="PHONE_USERQUERY_DETAIL"> <a href="javascript:showDetail(${item.id});">详情</a>
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
