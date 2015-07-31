<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>


</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：日志管理&gt;日志查询
		</div>

		<form:form id="terQuery" action="${ctx}/log/logQuery" method="post">
			<div id="search">
				<div id="title">日志查询</div>
				<ul>
 				<li><span>系统入口：</span><select name="sys_name" style="padding:2px; width:128px">
						<option value="" >全部</option>
						<option value="BOSS"  <c:if test="${params['sys_name'] eq 'BOSS'}">selected="selected"</c:if> >BOSS</option>
						<option value="AGENT"  <c:if test="${params['sys_name'] eq 'AGENT'}">selected="selected"</c:if> >AGENT</option>
						<option value="CUSTOMER"  <c:if test="${params['sys_name'] eq 'CUSTOMER'}">selected="selected"</c:if> >CUSTOMER</option>
						<option value="SETTLE"  <c:if test="${params['sys_name'] eq 'SETTLE'}">selected="selected"</c:if> >SETTLE</option>
	         	</select>
				</li>

				<li><span>用户名：</span><input type="text"
					value="${params['user_name']}" name="user_name" /></li>
				<li><span>登录IP：</span><input type="text"
					value="${params['login_ip']}" name="login_ip" /></li>
				<li><span>登录地址：</span><input type="text"
					value="${params['location']}" name="location" />
				</li>
				<li><span>登录时间：</span>
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
						<th width="5%">序列</th>
						<th width="90">系统入口</th>
						<th width="120">用户名</th>
						<th width="90">登录IP</th>
						<th width="130">登录地址</th>
						<th width="110">登录时间</th>
					</tr>
				</thead>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<c:out value=""></c:out>
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td>${item.sys_name}</td>
							<td>${item.user_name}</td>
							<td>${item.login_ip}</td>
							<td>${item.location}</td>
							<td><fmt:formatDate value="${item.last_login_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
	        </tr>
					</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}"
				current="${list.number + 1}"  anchor="_table"/>
		</div>
	</div>
</body>
