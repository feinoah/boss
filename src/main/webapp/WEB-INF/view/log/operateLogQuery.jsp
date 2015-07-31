<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>


</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：日志记录&gt;操作用户查询
		</div>

		<form:form id="terQuery" action="${ctx}/log/operateLogQuery" method="post">
			<div id="search">
				<div id="title">
					日志查询
				</div>
			 <ul>
	      <li><span>用户名：</span></span><input type="text"
					value="${params['user_name']}" name="user_name" /></li>
	        <li><span>操作时间：</span>
					<input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
				</li>
	      </ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn clear">
				<input class="button blue medium" type="submit" id="submit"
					value="查询" />
				<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
			</div>
		</form:form>
		<a name="_table"></a>
		<div id="tbdata" class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="5%">序号</th>
						<th width="120">用户名</th>
						<th width="140">备注</th>
						<th width="120">操作时间</th>
					</tr>
				</thead>
				<c:forEach items="${list.content}" var="item" varStatus="status">
					<c:out value=""></c:out>
					<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
						<td class="center"><span class="center">${status.count}</span></td>
						<td>${item.user_name}</td>
						<td>${item.remark}</td>
						<td><fmt:formatDate value="${item.operate_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
					</tr>

				</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}"
				current="${list.number + 1}" anchor="_table"/>
		</div>
	</div>



</body>
