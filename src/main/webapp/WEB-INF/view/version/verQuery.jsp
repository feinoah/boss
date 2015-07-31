<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
	function showDetail(id)
		{
			$.dialog({title:'版本详情',width: 650,height:300,resize: false,lock: true,max:false,content: 'url:verDetail?id='+id+'&layout=no'});
		}
	
			function verDel(id){
			$.dialog.confirm('确定要删除该条记录吗？', function(){
				ajaxVerDel(id);
			});
		}
		
		function ajaxVerDel(id){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/ver/verDel",
		 			data:{"id":id},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		successMsg("版本信息删除成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		
		function successMsg(contentMsg){
			var dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: 'success.gif',ok:null ,close:function(){
				location.href="verQuery";
			}});
		}
</script>

</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>版本信息</div>
   <form:form id="merQuery" action="${ctx}/ver/verQuery" method="post">
   <div id="search">
    	<div id="title">版本控制查询</div>
	      <ul>
	        <li><span>系统平台：</span>
	         	<select style="padding:2px;width:120px" name="platform">
	         	<option value="-1" <c:out value="${params['platform'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="0" <c:out value="${params['platform'] eq '0'?'selected':'' }"/>>android</option>
	         		<option value="1" <c:out value="${params['platform'] eq '1'?'selected':'' }"/>>iOS</option>
	         	</select>
		 </li>
		 <li><span>客户端类型：</span>
	         	<select style="padding:2px;width:120px" name="app_type">
	         	<option value="-1" <c:out value="${params['app_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="0" <c:out value="${params['app_type'] eq '0'?'selected':'' }"/>>银联商宝</option>
	         		<option value="1" <c:out value="${params['app_type'] eq '1'?'selected':'' }"/>>移小宝</option>
	         		<option value="2" <c:out value="${params['app_type'] eq '2'?'selected':'' }"/>>通付宝</option>
	         		<option value="3" <c:out value="${params['app_type'] eq '3'?'selected':'' }"/>>中宽支付</option>
	         	</select>
		 </li>
		 
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submit"  value="查询"/>
    	<shiro:hasPermission name="VERSION_ADD">
    	<input class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='${ctx}/ver/verInput'"  value="增加"/>
    	</shiro:hasPermission>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="15%">版本号</th>
          <th width="15%">手机系统平台</th>
        <!--   <th width="35%">APP地址</th> -->
          <th width="20%">客户端类型</th>
         <!--  <th width="20%">APP导航</th> -->
          <th width="20%">创建时间</th>
          <th width="25%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center" style="WORD-WRAP: break-word">
	          ${item.version}
	         </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.platform eq '1'}">iOS</c:when>
          		<c:when test="${item.platform eq '0'}">android</c:when>
	         </c:choose>
	          </td>
	         <td align="center" style="WORD-WRAP: break-word">
	         <c:choose>
	            <c:when test="${item.app_type eq '0'}">银联商宝</c:when>
	            <c:when test="${item.app_type eq '1'}">移小宝</c:when>
	            <c:when test="${item.app_type eq '2'}">通付宝</c:when>
	            <c:when test="${item.app_type eq '3'}">中宽支付</c:when>
	         </c:choose>
	         </td>
	          <td align="center" style="WORD-WRAP: break-word"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			 <shiro:hasPermission name="VERSION_DETAIL">
			 	 <a href="javascript:showDetail(${item.id});">详情</a>
			  </shiro:hasPermission>
			 <shiro:hasPermission name="VERSION_UPDATE">
			 	| <a href="verInput?id=${item.id}">修改</a>
			 </shiro:hasPermission>
			  <shiro:hasPermission name="VERSION_DELETE">
			 	| <a href="javascript:verDel(${item.id})">删除</a>
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
