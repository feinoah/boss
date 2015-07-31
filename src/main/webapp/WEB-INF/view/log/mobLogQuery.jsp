<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
		
		function showDetail(id)
		{
			//$.dialog({title:'商户详情',width: 720,height:530,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
			window.location.href='mobLogDetail?id='+id;
		}
		
	</script>

</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：日志管理&gt;手机日志查询
		</div>

		<form:form id="terQuery" action="${ctx}/log/mobLogQuery" method="post">
			<div id="search">
				<div id="title">手机日志查询</div>
				<ul>
 				

				<li><span>用户名：</span><input type="text"
					value="${params['user_name']}" name="user_name" /></li>
				<li><span>序列号：</span> <input type="text"
					value="${params['seq_no']}" name="seq_no" /></li>
				<li><span>psam卡号：</span><input type="text"
					value="${params['psam_no']}" name="psam_no" />
				</li>
				<li><span>交易码：</span><input type="text"
					value="${params['trade_id']}" name="trade_id" />
				</li>
				<li><span>平台：</span>
					<select name=platform style="padding:2px; width:128px">
						<option value="" >全部</option>
						<option value="0"  <c:if test="${params.platform eq '0'}">selected="selected"</c:if> >android系统</option>
						<option value="1"  <c:if test="${params.platform eq '1'}">selected="selected"</c:if> >ios系统</option>
	         	</select>
				</li>
				
				<li><span>创建时间：</span>
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
						<th width="60">用户名</th>
						<th width="100">序列号</th>
						<th width="90">psam卡号</th>
						<th width="80">交易码</th>
						<th width="55">平台</th>
						
						<th width="90">创建时间</th>
						<th width="30">操作</th>

					</tr>
				</thead>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<c:out value=""></c:out>
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
							<td class="center"><span class="center">${status.count}</span></td>
							<td>${item.user_name}</td>
							<td>${item.seq_no}</td>
							<td>${item.psam_no}</td>
							<td>${item.trade_id}</td>
							<td>
							
							<c:if test="${item.platform eq '0' }">android系统</c:if>					
 						 <c:if test="${item.platform eq '1' }">ios系统</c:if>
							
							
							</td>
							<td><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
							<td class="center">
	           <a href="javascript:showDetail(${item.ID});">详情</a>
	           </td>
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
