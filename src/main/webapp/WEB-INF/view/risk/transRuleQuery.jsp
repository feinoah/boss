<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
	function showDetail(id)
		{
			$.dialog({title:'黑名单详情',width: 650,height:250,resize: false,lock: true,max:false,content: 'url:blackDetail?id='+id+'&layout=no'});
		}
		
		
			function blackDel(id){
			$.dialog.confirm('确定要删除该条记录吗？', function(){
				ajaxBlackDel(id);
			});
			
		}
		
		function ajaxBlackDel(id){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/black/blackDel",
		 			data:{"id":id},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		successMsg("黑名单删除成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		
		function successMsg(contentMsg){
			var dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: 'success.gif',ok:null ,close:function(){
				location.href="blackQuery";
			}});
		}
</script>

</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：风险管理>交易规则查询</div>
   <form:form id="merQuery" action="${ctx}/black/blackQuery" method="post">
   <div id="search">
    	<div id="title">交易规则查询</div>
	      <ul>
	       	<li style="width:216px"><span>规则编号：</span><input type="text"  value="${params['auth_name']}" name="auth_name" /></li>
	        <li style="width:216px"><span>规则名称：</span><input type="text"  value="${params['auth_name']}" name="auth_name" /></li>
	        <li style="width:216px"><span>规则类型：</span>
	        
	        <select style="padding:2px;width:128px" name="status">
	         		<option value="-1" <c:out value="${params['status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>规则组</option>
	         		<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>子规则</option>
	         	</select>
	         	
	         	 </li>
	         	 <li style="width:216px"><span>子类型：</span>
	        
	        <select style="padding:2px;width:128px" name="status">
	         		<option value="-1" <c:out value="${params['status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>单日</option>
	         		<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>多日</option>
	         	</select>
	         	
	         	 </li>
	         <li style="width:216px"><span>状态：</span>
	        
	        <select style="padding:2px;width:128px" name="status">
	         		<option value="-1" <c:out value="${params['status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>启用</option>
	         		<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>禁用</option>
	         	</select>
	         	
	         	 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submit"  value="查询"/>
    	<shiro:hasPermission name="TRANS_BLACK_ADD">
    	<input class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='${ctx}/risk/paramInput'"  value="增加"/>
    	</shiro:hasPermission>
    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="20%">规则编号</th>
          <th width="25%">规则名称</th>
            <th width="100">规则类型</th>
          <th width="100">子类型</th>
            <th width="100">状态</th>
          <th width="100">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center" style="WORD-WRAP: break-word">
	          ${item.black_type}
	         </td>
	         <td class="center" style="WORD-WRAP: break-word">
	          ${item.black_type}
	         </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
	          		<c:when test="${item.black_type eq '1'}">规则组</c:when>
	          		<c:when test="${item.black_type eq '2'}">子规则</c:when>
	          	</c:choose>
	         </td>
	         <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
	          		<c:when test="${item.black_type eq '1'}">单日</c:when>
	          		<c:when test="${item.black_type eq '2'}">多日</c:when>
	          	</c:choose>
	         </td>
	         <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
	          		<c:when test="${item.black_type eq '1'}">启用</c:when>
	          		<c:when test="${item.black_type eq '2'}">禁用</c:when>
	          	</c:choose>
	         </td>
	         <td align="center">
			 	 <a href="javascript:showDetail(${item.id});">详情</a>
			 	| <a href="blackInput?id=${item.id}">修改</a>
			 	| <a href="javascript:blackDel(${item.id})">删除</a>
			  </td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>
  </div>
</body>
