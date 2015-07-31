<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>


</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：手机访问&gt;手机访问查询
		</div>

		<form:form id="terQuery" action="${ctx}/log/rquestDevQuery" method="post">
			<div id="search">
				<div id="title">手机访问查询</div>
				<ul>
 				<li><span>手机平台：</span><select name="os" style="padding:2px; width:128px">
						<option value="" >全部</option>
						<option value="0"  <c:if test="${params['os'] eq '0'}">selected="selected"</c:if> >android</option>
						<option value="1"  <c:if test="${params['os'] eq '1'}">selected="selected"</c:if> >iOS</option>
 	         	</select>
				</li>

				<li><span>手机号：</span></span><input type="text" value="${params['mobile']}" name="mobile" /></li>
				<li><span>访问时间：</span>
					<input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			  	~
			 	  <input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
				</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn clear">
				<input class="button blue medium" type="submit" id="submit" value="查询" />
			</div>
		</form:form>
		<a name="_table"></a>
		<div class="tbdata" style="overflow-x:scroll;overflow-x:auto;overflow-y:scroll; overflow-y:hidden;">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="250">手机唯一标识</th>
						<th width="50">平台</th>
						<th width="30">版本	</th>
						<th width="65">分辨率</th>
						<th width="80">号码</th>
						<th width="70">客户端版本</th>
						<th width="100">地址</th>
						<th width="100">创建时间</th>

					</tr>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<c:out value=""></c:out>
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td style="width:250px;">${item.uuid}</td>
							<td>
									<c:if test="${item.os=='0'}">android</c:if>
									<c:if test="${item.os=='1'}">iOS</c:if>
							</td>
							<td>${item.os_version}</td>
							<td>${item.display}</td>
							<td>${item.mobile}</td>
							<td>${item.app_version}</td>
							<td> <u:substring length="16" content="${item.province}${item.city}${item.stree}"/></td>
							<td><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
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
