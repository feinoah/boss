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
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：风险管理>参数字段查询</div>
   <form:form id="merQuery" action="${ctx}/black/blackQuery" method="post">
   <div id="search">
    	<div id="title">参数字段查询</div>
	      <ul>
	        <li><span style="width: 90px;">参数名称/编号：</span><input type="text"  value="${params['parameter']}" name="parameter" /></li>
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
          <th width="8%">序号</th>
          <th>编号</th>
          <th>名称</th>
          <th>创建时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	    	  <td>${item.parameter_no}</td>
	          <td>${item.parameter_name}</td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>
  </div>
</body>
