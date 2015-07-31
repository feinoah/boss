<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<head>
   <script type="text/javascript">
   
   $(function(){
		  $("#activationProduce").click(function(){
			  $.dialog({lock: true,drag: false,resize: false,max: false,
				 content: 'url:viewActivationProduce?layout=no',close: function(){
				$("#submit").click();
			  }});
		 });
   });
   
   
    function  updateState(keycode,code_user){
	    if(keycode != ''){
		   if(code_user==null || ""==code_user){
			   $.ajax({
		           url:'${ctx}/activation/findActivation',
		                 cache:false,
		                 data:{'keycode':keycode},
		                 type:'POST',
		                 dataType:'json',
		                 error:function()
		                 {
		                      alert("查询出错，请检查数据！");
		                 },
		                 success:function(json)
		                 {       
		                       if(json.flag=='1'){
		                            location.href="${ctx }/activation/stateLoad?keycode="+keycode;
		                       }else{
		                    	   $.dialog.alert("激活码不存在！");
		                       }
		                 }
		           });            
		   }else{
			   $.dialog.alert("激活码已经被使用不能被修改！");
		   }
       } 
   } 
   </script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：系统管理&gt;激活码管理</div>
   <form:form id="activationQuery" action="${ctx}/activation/activationQuery" method="post">
    <div id="search">
    	<div id="title">激活码查询</div>
	      <ul>
	        <li>
	        <span style="width: 55px;">状态：</span>
	        <select style="padding: 2px; width: 73px" id="state" name="state">
				<option value="-1" <c:out value="${params['state'] eq '-1'?'selected':'' }"/>>全部</option>
				<option value="0" <c:out value="${params['state'] eq '0'?'selected':'' }"/>>初始化</option>
				<option value="1" <c:out value="${params['state'] eq '1'?'selected':'' }"/>>激活</option>
				<option value="2" <c:out value="${params['state'] eq '2'?'selected':'' }"/>>锁定</option>
				<option value="3" <c:out value="${params['state'] eq '3'?'selected':'' }"/>>使用中</option>
			</select>
			</li>
	        <li><span style="width: 45px;">批次：</span><input type="text"  style="width: 75px;"  value="${params['batch']}" name="batch" /></li>
	        <li><span style="width: 55px;">使用者：</span><input type="text"  value="${params['code_user']}" name="code_user" /></li>
	        <li><span style="width: 55px;">激活码：</span><input type="text"  style="width: 150px;"  value="${params['keycode']}" name="keycode" /></li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<shiro:hasPermission name="ACTIVATION_PRODUCE">
    	<input   class="button blue medium" type="button" id="activationProduce"  value="生成激活码"/>
    	</shiro:hasPermission>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="30%">激活码</th>
          <th width="10%">状态</th>
          <th width="22%" >购买者</th>       
          <th width="22%" >使用者</th>       
          <th width="17%" >操作用户</th> 
          <th width="13%" >批次</th> 
          <th width="22%" >激活时间</th>       
          <th width="10%" >操作</th>       
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr>
	          <td class="center"><span class="center">${item.keycode}</span></td>
	          <td align="center"  style="WORD-WRAP: break-word">
	          <c:choose>
	             <c:when test="${item.state eq '0'}">初始化</c:when>
	             <c:when test="${item.state eq '1'}">激活</c:when>
	             <c:when test="${item.state eq '2'}">锁定</c:when>
	             <c:when test="${item.state eq '3'}">使用中</c:when>
	             <c:otherwise>${item.state}</c:otherwise>
	          </c:choose>
	          </td>
	          <td  style="WORD-WRAP: break-word">
	          <span>${item.buyer}</span>
	          </td>
	          <td  style="WORD-WRAP: break-word">
	          <span>${item.code_user}</span>
	          </td>
	          <td align="center"  style="WORD-WRAP: break-word">
	          <span>${item.operator}</span>
	          </td>
	          <td align="center"  style="WORD-WRAP: break-word">
	          <span >${item.batch}</span>
	          </td>
	          <td style="WORD-WRAP: break-word"><fmt:formatDate value="${item.usertime}" type="both"/></td>
	     <%--      <td><shiro:hasPermission name="ACTIVATION_QUERY_MODIFY"><a href="${ctx }/activation/stateLoad?keycode=${item.keycode}" title="修改状态">改</a></shiro:hasPermission></td> --%>
	          <td><shiro:hasPermission name="ACTIVATION_QUERY_MODIFY"><a href="javascript:updateState('${item.keycode}','${item.code_user}');" title="修改状态">改</a></shiro:hasPermission></td>
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
