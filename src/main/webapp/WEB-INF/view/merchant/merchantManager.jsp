<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
  <head>
  <script type="text/javascript">
        function showDetai(id){
             window.location.href='merDetail?id='+id;
        }
  </script>
  </head>
  <body>
       <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>未交易商户查询</div>
   <form:form id="notransQuery" action="${ctx}/mer/notransQuery" method="post">
    <div id="search">
    	<div id="title">未交易商户查询</div>
	      <ul>
	        <li><span>代理商名称：</span><u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" /></li>
	      	<li><span>创建时间：</span>
			 	<input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submit"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5%">序号</th>
          <th width="150">商户简称</th>      
          <th>商户编号</th>
          <th>代理商名称</th>
          <th width="80">状态</th>
          <th width="120">创建时间</th>
          <th width="80">操作</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center">${item.merchant_short_name}</td>
	          <td class="center">${item.merchant_no}</td>
	          <td>${item.agent_name}</td>
	           <td >
	          <c:choose>
				  <c:when test="${item.open_status eq '1'}">正常</c:when>
				  <c:when test="${item.open_status eq '0'}">商户关闭</c:when> 
				 <c:when test="${item.open_status eq '2'}">待审核</c:when> 
				 <c:when test="${item.open_status eq '5'}">机具绑定</c:when> 
				 <c:when test="${item.open_status eq '4'}">冻结</c:when> 
				 <c:when test="${item.open_status eq '6'}">初审</c:when> 
				 <c:when test="${item.open_status eq '3'}"><span class="font_red">审核失败</span></c:when> 
			  </c:choose>
			  </td>    
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td align="center">
	          <shiro:hasPermission name="COMMERCIAL_QUERY_DETAIL"><a href="javascript:showDetai(${item.id});">详情</a></shiro:hasPermission>
               </td>
	        </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table" />
		</div>
  </div>
  </body>
