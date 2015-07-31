<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<script type="text/javascript">
	function showDetail(id)
		{	    
			//$.dialog({title:'代理商通告下发详情',width: 750,height:800,resize: false,lock: true,max:false,content: 'url:noticeDetail?id='+id+'&layout=no'});
			location.href="noticeDetail?id="+id;
		}
	
		
		function offNotice(id,notice_is_valid){
			if(confirm("确定要关闭此通告信息吗？")){
				location.href="updateNoticeStatus?id="+id+"&notice_is_valid="+notice_is_valid;
	 		}
 	    }
 	    
 	    function onNotice(id,notice_is_valid){ 	  
	 		if(confirm("确定要开启此通告信息吗？")){
				location.href="updateNoticeStatus?id="+id+"&notice_is_valid="+notice_is_valid;
				
	 		}
 	    }
 	    
 	    function noticeDel(id)
	    {
 	    	var data="id="+id;
 	    	$.dialog.confirm("确定要删除此通告信息吗？", function() {
 	    		$.post('${ctx}/agentNoticeSend/delNotice',data,function(msg) {
 	    			if (msg.msg == "OK") {
						successMsg(function(){
							location.href="${ctx}/agentNoticeSend/agentNoticeQuery";
						});
					} else {
						$.dialog({
							title : "错误",
							lock : true,
							content : '删除失败',
							ok : null
						});
					}
 	    		});
 	    	});
	    }
 	    
 	   function successMsg(callback) {
 			callback = callback || function() {
 			};
 			$.dialog({
 				title : '提示',
 				lock : true,
 				content : '删除成功',
 				icon : 'success.gif',
 				ok : callback
 			});
 		}
</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理>代理商通告下发</div>
   <form:form id="agentNoticeQuery" action="${ctx}/agentNoticeSend/agentNoticeQuery" method="post">
   <div id="search">
    	<div id="title">代理商通告查询</div>
	      <ul>
	         <li><span style="width: 50px;">标题：</span>
	         	<input type="text"  value="${params['notice_title']}" name="notice_title" />
		     </li>
			 <li><span style="width: 90px;">通告签名日期：</span>
		         <input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
				  ~
				 <input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
   <div class="search_btn">
        <input class="button blue medium" type="submit" id="submit"  value="查询"/>
        <input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    	<input class="button blue medium" type="button" id="clientMessageAdd" onclick="javascript:window.location.href='${ctx}/agentNoticeSend/agentNoticeAdd'"  value="增加"/>
   </div>
   </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%"  cellspacing="0" class="t2">
        <thead>
        <tr>
          <th width="1px;">序号</th>
          <th width="14px;">标题</th>
          <th width="10px;">通告是否有效</th>
          <th width="10px;">消息状态</th>
          <th width="13px;">通告签名日期</th>
          <th width="17px;">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${item.id}</span></td>
	          <td class="center"><span class="center">${item.notice_title}</span></td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.notice_is_valid eq true}">有效</c:when>
          		<c:when test="${item.notice_is_valid eq false}">无效</c:when>
	          </c:choose>	   
	          </td>
	          <td class="center" style="WORD-WRAP: break-word">
	          <c:choose>
          		<c:when test="${item.notice_is_valid eq true}">
          		<span class="font_gray">开通</span> |
				<a href="javascript:offNotice('${item.id}','${item.notice_is_valid}');">关闭</a>
          		</c:when>
          		<c:when test="${item.notice_is_valid eq false}">
          		<a href="javascript:onNotice('${item.id}','${item.notice_is_valid}');">开通</a> |
				<span class="font_gray">关闭</span>
          		</c:when>
	          </c:choose>
	          </td>
	          <td align="center" style="WORD-WRAP: break-word">
	          	<fmt:formatDate value="${item.create_time}" type="both"/>
	          </td>
			  <td align="center">
				 <shiro:hasPermission name="AGENT_NOTICE_DETAIL">
				 	   <a href="javascript:showDetail(${item.id});" title="详情">详情</a>
				 </shiro:hasPermission>
				 <shiro:hasPermission name="AGENT_NOTICE_UPDATE">
				 	   | <a href="${ctx}/agentNoticeSend/agentNoticeInput?id=${item.id}" title="修改">修改</a>
				 </shiro:hasPermission>
				 <shiro:hasPermission name="AGENT_NOTICE_DEl">
					   | <a href="javascript:noticeDel(${item.id})" title="删除">删除</a>
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
