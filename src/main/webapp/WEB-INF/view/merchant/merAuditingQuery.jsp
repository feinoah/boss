<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<script type="text/javascript">
	function showDetail(operator,pos_type)
	{
		if(operator != ""){
			$("#detail_operator").val(operator);
			$("#merQuery").attr("action", "${ctx}/mer/merAuditingQueryDetail?pos_type=" + pos_type).submit();
			$("#merQuery").attr("action", "${ctx}/mer/merAuditingQuery");
		}
	}
	
		
	$(function(){
		$('#exportExcel2').on('click',function(){
		   /*var action= $("form:first").attr("action"),
		   	   totalPage = parseInt('${list.totalPages}');
		   	   
		   //根据当前页查询的总页数来判断是否导出，必须先进行查询
		   if(totalPage <= 0){
		       $.dialog.alert("<pre>没有需要导出的数据！</pre>");
		   } else if(totalPage > 100){
		   	   $.dialog.alert("<pre>请选择一些必要的查询条件并进行查询，避免因导出数据过多导致的系统异常！</pre>");
		   } else {
		   	   $("form:first").attr("action","${ctx}/mer/merAuditingExport").submit();
		   	   $("form:first").attr("action",action);
		   }*/

			exportXls('${ctx}/mer/merAuditingExport', ${list.totalPages}, true);
		});
	
		$('#exportExcel3').on('click',function(){
		   /*var action= $("form:first").attr("action"),
		   	   totalPage = parseInt('${list.totalPages}');
		   	   
		   //根据当前页查询的总页数来判断是否导出，必须先进行查询
		   if(totalPage <= 0){
		       $.dialog.alert("<pre>没有需要导出的数据！</pre>");
		   } else if(totalPage > 100){
		   	   $.dialog.alert("<pre>请选择一些必要的查询条件并进行查询，避免因导出数据过多导致的系统异常！</pre>");
		   } else {
		   	   $("form:first").attr("action","${ctx}/mer/merCheckStateExport").submit();
		   	   $("form:first").attr("action",action);
		   } */

			exportXls('${ctx}/mer/merCheckStateExport', ${list.totalPages}, true);
		});
	});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>商户审核统计</div>
   
   <form:form id="merQuery" action="${ctx}/mer/merAuditingQuery" method="post">
    <div id="search">
    	<div id="title">商户审核统计</div>
	      <ul>
	      <li><span style="width: 90px;">审核人员：</span>
	      <input  type="hidden"  name="detail_operator"  id="detail_operator"/>
			  		<select style="width: 128px;height: 24px;vertical-align: top;" name="real_name" id="real_name">
			  			<option value=''>请选择</option>
						<c:forEach items="${checker}" var="item" varStatus="status">
							<option value="${item.real_name}"  <c:out value="${params['real_name'] eq item.real_name ?'selected':'' }"/>>
								${item.real_name}
							</option>
						</c:forEach>
			  		</select> 
			  </li>
	      	<li><span>审核日期：</span>
	      	<input  type="hidden"  name="detail_Start_time"  id="detail_Start_time"  value="${params['start_time']}"/>
	      	<input  type="hidden"  name="detail_End_time"  id="detail_End_time"  value="${params['end_time']}" />
			 	<input  type="text"  style="width:102px" readonly="readonly" name="start_time"  id="start_time" value="${params['start_time']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="end_time"  id="end_time" value="${params['end_time']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
			 </li>
			 <li><span style="width: 90px;">审核状态：</span>
			 <input  type="hidden"  name="detailStatus"  id="detailStatus"   value="${params['open_status']}"/>
			 	<select style="width: 128px;height: 24px;vertical-align: top;" name="open_status"  id="open_status">
			 		<option value="">--全部--</option>
			 		<option value="1"  <c:if test="${params['open_status'] eq 1}">selected = "selected"</c:if>>正常</option>
			 		<option value="5"  <c:if test="${params['open_status'] eq 5}">selected = "selected"</c:if>>机具绑定</option>
			 		<option value="3"  <c:if test="${params['open_status'] eq 3}">selected = "selected"</c:if>>审核失败</option>
			 	</select>
			 </li>
			 
			 <li><span style="width: 90px;">设备类型：</span> 
	         	<!--<select style="padding:2px;width:128px" name="pos_type">
	         		<option value="">--全部--</option>
	         		<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>
	         		<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>
	         		<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>
	         		<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>
	         		<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>
	         	</select>
			 -->
			 	<u:TableSelect sname="pos_type" style="padding:2px;width:128px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
			 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="query"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    	<input id="exportExcel2" class="button blue medium" type="button" value="导出已审核商户"/>
    	<input id="exportExcel3" class="button blue medium" type="button" value="导出进度"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5%">序号</th>
          <th>审核人</th>
          <th>设备类型</th>
          <th>审核商户数</th>
          <th>待审核数</th>
          <th width="120">最后审核时间</th>
          <th width="150">操作</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.real_name}</td>
	          <td>${item.pos_type_name}</td>
	           <td>${item.checked}</td>
	           <td><c:if test="${(item.remain eq null) || (item.remain eq '')}">0</c:if><c:if test="${(item.remain ne null) && (item.remain ne '')}">${item.remain}</c:if></td>
	          <td><fmt:formatDate value="${item.last_check_time}" type="both"/></td>
	          <td class="center">
	           <shiro:hasPermission name="COMMERCIAL_QUERY_DETAIL"><a href="javascript:showDetail('${item.real_name}','${item.pos_type}');" title="详情">详情</a> </shiro:hasPermission>
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
