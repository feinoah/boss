<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
		
		function showDetail(id)
		{
			$.dialog({title:'YS商户详情',width: 650,height:350,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
		}

		function terminalInput(acq_merchant_no,acq_enname)
		{
			$.dialog({title:'YS添加终端',resize: false,lock: true,max:false,content: 'url:terminalInput?acq_merchant_no='+acq_merchant_no+'&acq_enname='+acq_enname+'&layout=no'});
		}
		
		
		
		function offMerchant(acq_merchant_no){		
		    
			 if(!confirm("确定要关闭  "+acq_merchant_no+" 收单机构商户吗？")){
			     return;
		      }		
		     var type="off";    
			 $.post(
			                '${ctx}/acq/updateMerchantStatus',
							{type:type,acq_merchant_no:acq_merchant_no},
							function(data)
							{
								if(data == 1)
								{
									alert("成功关闭");
									$("#submit").click();
								}else{
								    alert('关闭失败，请检查数据');
								    $("#submit").click();
								}
							}
	            	 );
 	      }
 	    
 	    
 	  function onMerchant(acq_merchant_no){	  
 	       
 	        if(!confirm("确定要开启  "+acq_merchant_no+" 收单机构商户吗？")){
			    return;
		    }		    
		     var type="on";
			 $.post(
							'updateMerchantStatus',
							{type:type,acq_merchant_no:acq_merchant_no},
							function(data)
							{
								if(data == 1)
								{
									alert("成功开启");
									$("#submit").click();
								}else{
								    alert('开启失败，请检查数据');
								    $("#submit").click();
								}
							}
	            	 );
      }
 	   
 	   
	</script>
	<style type="text/css">

	</style>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理&gt;收单机构商户</div>
   
   <form:form id="merQuery" action="${ctx}/sale/acqMerchantQuery" method="post">
    <div id="search">
    	<div id="title">收单机构商户</div>
	      <ul>
  	      	<li style="width: 220px;"><span>代理商名称：</span><u:select value="${params['agent_no']}"  stype="agent" sname="agent_no"  onlyThowParentAgent="true"  /></li>
	        <li><span style="width: 140px;">收单机构商户名称/编号：</span><input type="text"  style="width: 132px;" value="${params['acq_merchant']}" name="acq_merchant" /></li>
	      	<li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	      </ul>
	      <div class="clear"></div>
	      <ul>
	      <li style="width: 220px;"><span>是否A类：</span>
	         <select name="large_small_flag" style="padding:2px;width: 140px;">
	         	<option value="-1" <c:out value="${params['large_small_flag'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="1" <c:out value="${params['large_small_flag'] eq '1'?'selected':'' }"/>>是</option>
	         	<option value="0" <c:out value="${params['large_small_flag'] eq '0'?'selected':'' }"/>>否</option>
	         </select>
	         </li>
	      <%--
	      	<li style="width: 220px;"><span>可否大套小：</span>
	         <select name="large_small_flag" style="padding:2px;width: 140px;">
	         	<option value="-1" <c:out value="${params['large_small_flag'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="1" <c:out value="${params['large_small_flag'] eq '1'?'selected':'' }"/>>可套</option>
	         	<option value="0" <c:out value="${params['large_small_flag'] eq '0'?'selected':'' }"/>>不可套</option>
	         </select>
	         </li>
	         --%><li><span style="width: 140px;">收单机构：</span>
					<select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
						<option value="">全部</option>
						<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>	
						</c:forEach>
					</select>
			 </li>	
			 <li style="width: 220px;"dir="ltr"><span>锁定状态：</span>
	         <select name="locked" style="padding:2px;width: 130px;margin-left:12px;" >
	            <option value="-1" <c:out value="${params['locked'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="0" <c:out value="${params['locked'] eq '0'?'selected':'' }"/>>正常</option>
	         	<option value="1" <c:out value="${params['locked'] eq '1'?'selected':'' }"/>>锁定</option>
	         	<option value="2" <c:out value="${params['locked'] eq '2'?'selected':'' }"/>>废弃</option>
	         </select>
	         </li>	
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<%-- <shiro:hasPermission name="SYSTEM_MERCHANTQUERY_ADD">
    	<input   class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='merchantAdd'"  value="增加"/>
    	</shiro:hasPermission> --%>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata"  style="overflow-y:auto;">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="30px;">序号</th>    
          <th width="110px;">代理商名称</th>
          <th width="124px;">收单机构商户编号</th>    
          <th width="124px;">收单机构商户名称</th> 
          <th width="60px;">是否A类</th>
          <%--<th width="60px;">可否大套小</th>
          --%><th width="50px;">锁定状态</th>  
          <th width="115px;">创建时间</th>
          <th width="97px;">操作</th>
        </tr>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td style="word-break: break-all ;"><u:substring length="22" content="${item.agent_name}"/></td>
	          <td style="word-break: break-all ;">${item.acq_merchant_no}</td>
	          <td style="word-break: break-all ;"><u:substring length="22" content="${item.acq_merchant_name}"/></td>
	          <td align="center">
	          <c:choose>
	          <c:when test="${item.large_small_flag =='1' }">是</c:when>
				  <c:when test="${item.large_small_flag =='0' }">否</c:when>
				  <%--<c:when test="${item.large_small_flag =='1' }">可套</c:when>
				  <c:when test="${item.large_small_flag =='0' }">不可套</c:when> 
			  --%></c:choose> 
	          </td>
	          <td align="center" title="${item.locked_msg}">
	            <c:choose>
				  <c:when test="${item.locked ==0 }">正常</c:when>
				  <c:when test="${item.locked ==1 }">锁定</c:when>
				  <c:when test="${item.locked ==2 }">废弃</c:when>
			  	</c:choose> 
	          </td>
				<td><fmt:formatDate value="${item.create_time}" type="both"/></td>	       
				<td align="center">
					<shiro:hasPermission name="SYSTEM_SAlE_AcqMERQUERY_DETAIL"><a href="javascript:showDetail(${item.id});">详</a>
					</shiro:hasPermission>
					<%-- <shiro:hasPermission name="SYSTEM_MERCHANTQUERY_MODIFY"><a href="merchantInput?id=${item.id}">改</a>
					|</shiro:hasPermission>
					<shiro:hasPermission name="SYSTEM_MERCHANTQUERY_ADDEDTERMINAL">
					 <a href="javascript:terminalInput('${item.acq_merchant_no}','${item.acq_enname}');">添加终端</a>
					 </shiro:hasPermission> --%> 
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
