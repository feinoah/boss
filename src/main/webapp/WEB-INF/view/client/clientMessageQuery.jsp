<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
	function showDetail(id)
		{	    
			$.dialog({title:'手机客户端信息详情',width: 650,height:220,resize: false,lock: true,max:false,content: 'url:messageDetail?id='+id+'&layout=no'});
		}
	
		
		function offClientMessage(id,is_valid){
			if(confirm("确定要关闭此手机客户端信息吗？")){
				location.href="updateClientStatus?id="+id+"&is_valid="+is_valid;
				
	 		}
 	    }
 	    
 	    function onClientMessage(id,is_valid){ 	  
	 		if(confirm("确定要开启此手机客户端信息吗？")){
				location.href="updateClientStatus?id="+id+"&is_valid="+is_valid;
				
	 		}
 	    }
 	    
 	    
 	    function messageDel(id,is_delete)
	    {
			if(confirm("确定要删除此手机客户端信息吗？")){
				location.href="delMessageQuery?id="+id+"&is_delete="+is_delete;
				
			}
	    }
 	    
</script>

</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>手机客户端消息</div>
   <form:form id="messageQuery" action="${ctx}/clientMessage/clientMessageQuery" method="post">
   
   <div class="search_btn">
    	<input class="button blue medium" type="button" id="clientMessageAdd" onclick="javascript:window.location.href='${ctx}/clientMessage/clientMessageAdd'"  value="增加客户端消息"/>
   </div>
   </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%"  cellspacing="0" class="t2">
        <thead>
        <tr>
          <th width="1px;">序号</th>
          <th width="14px;">是否可继续登录</th>
          <th width="10px;">移小宝有效值</th>
          <th width="10px;">点付宝有效值</th>
          <th width="10px;">商宝有效值</th>
          <th width="10px;">消息是否有效</th>
          <th width="10px;">消息状态</th>
          <th width="13px;">消息是否删除</th>
          <th width="15px;">创建时间</th>
          <th width="17px;">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${item.id}</span></td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
	            <c:when test="${item.is_continue_login eq true}">是</c:when>
          		<c:when test="${item.is_continue_login eq false}">否</c:when>
	          </c:choose>
	          </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.is_smallbox eq true}">有效</c:when>
          		<c:when test="${item.is_smallbox eq false}">无效</c:when>
	          </c:choose>
	          </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.is_dot eq true}">有效</c:when>
          		<c:when test="${item.is_dot eq false}">无效</c:when>
	          </c:choose>
	          </td> 
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.is_shang_bao eq true}">有效</c:when>
          		<c:when test="${item.is_shang_bao eq false}">无效</c:when>
	          </c:choose>
	          </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.is_valid eq true}">有效</c:when>
          		<c:when test="${item.is_valid eq false}">无效</c:when>
	          </c:choose>	   
	          </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.is_valid eq true}">
          		<span class="font_gray">开通</span> |
				<a href="javascript:offClientMessage('${item.id}','${item.is_valid}');">禁用</a>
          		</c:when>
          		<c:when test="${item.is_valid eq false}">
          		<a href="javascript:onClientMessage('${item.id}','${item.is_valid}');">开通</a> |
				<span class="font_gray">禁用</span>
          		</c:when>
	          </c:choose>
	          </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.is_delete eq true}">已删</c:when>
          		<c:when test="${item.is_delete eq false}">正常</c:when>
	          </c:choose>
	          </td>
	          <td align="center" style="WORD-WRAP: break-word">
	          	<fmt:formatDate value="${item.create_time}" type="both"/>
	          </td>
			  <td align="center">
				 <shiro:hasPermission name="CLIENT_DETAIL">
				 	 <a href="javascript:showDetail(${item.id});" title="详情">详情</a>
				  </shiro:hasPermission>
				 <shiro:hasPermission name="CLIENT_UPDATE">
				 	| <a href="${ctx}/clientMessage/messageInput?id=${item.id}" title="修改">修改</a>
				 </shiro:hasPermission>
				  <shiro:hasPermission name="CLIENT_DELETE">
				  <c:choose>
				    <c:when test="${item.is_delete eq false}">
				        | <a href="javascript:messageDel('${item.id}','${item.is_delete}')" title="删除">删除</a>
				    </c:when>
				  </c:choose>
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
