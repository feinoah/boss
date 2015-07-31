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
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：交易管理>黑名单设置</div>
   <form:form id="merQuery" action="${ctx}/black/blackQuery" method="post">
   <div id="search">
    	<div id="title">黑名单查询</div>
	      <ul>
	        <li><span>黑名单类型：</span>
	         	<select style="padding:2px;width:100px" name="black_type">
	         		<option value="-1" <c:out value="${params['black_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['black_type'] eq '1'?'selected':'' }"/>>卡号黑名单</option>
	         		<option value="2" <c:out value="${params['black_type'] eq '2'?'selected':'' }"/>>商户号黑名单</option>
	         		<option value="3" <c:out value="${params['black_type'] eq '3'?'selected':'' }"/>>身份证黑名单</option>
	         	</select>
		 </li>
	        <li><span style="width:40px;">状态：</span>
	        
	        <select style="padding:2px;width:80px" name="status">
	         		<option value="-1" <c:out value="${params['status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>启用</option>
	         		<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>停用</option>
	         	</select>
	         	
	         	 </li>
	      	<li><span style="width:60px;">商户编号：</span>
				<input type="text" style="width:70px;" name="merchant_no" value="${params.merchant_no}"/>			        
	         	 </li>
	        <li><span style="width:90px;">交易银行卡号：</span>
				<input type="text" style="width:70px;" name="account_no" value="${params.account_no}"/>			        
	         	 </li>
	        <li><span style="width:60px;">身份证号：</span>
				<input type="text" style="width:70px;" name="id_card_no" value="${params.id_card_no}"/>			        
	         	 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<shiro:hasPermission name="TRANS_BLACK_ADD">
    	<input class="button blue medium" type="button" id="addButton" onclick="javascript:window.location.href='${ctx}/black/blackInput'"  value="增加"/>
    	</shiro:hasPermission>
    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="5%">序号</th>
          <th width="20%">黑名单类型</th>
          <th width="25%">黑名单值</th>
          <th width="10%">状态</th>
          <th width="15%">创建时间</th>
          <th width="15%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
	          		<c:when test="${item.black_type eq '1'}">卡号黑名单</c:when>
	          		<c:when test="${item.black_type eq '2'}">商户号黑名单</c:when>
	          		<c:when test="${item.black_type eq '3'}">身份证黑名单</c:when>
	          	</c:choose>
	         </td>
	          <td class="center" style="WORD-WRAP: break-word"><span class="center">${item.black_value}</span></td>
	          <td align="center" style="WORD-WRAP: break-word">
	          	<c:choose>
	          		<c:when test="${item.status eq '1'}">启用</c:when>
	          		<c:when test="${item.status eq '0'}">停用</c:when>
	          	</c:choose>
	          </td>
	          <td align="center" style="WORD-WRAP: break-word"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			   <shiro:hasPermission name="TRANS_BLACK_DETAIL">
			 	 <a href="javascript:showDetail(${item.id});">详情</a>
			  </shiro:hasPermission>
			 <shiro:hasPermission name="TRANS_BLACK_UPDATE">
			 	| <a href="blackInput?id=${item.id}">修改</a>
			 </shiro:hasPermission>
			  <shiro:hasPermission name="TRANS_BLACK_DELETE">
			 	| <a href="javascript:blackDel(${item.id})">删除</a>
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
