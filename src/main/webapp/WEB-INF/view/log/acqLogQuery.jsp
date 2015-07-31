<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>


</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：日志记录>收单日志查询
		</div>

		<form:form id="terQuery" action="${ctx}/log/acqLogQuery" method="post">
			<div id="search">
				<div id="title">
					日志查询
				</div>
			 <ul>
	      <li><span style="width:140px">收单机构商户名称/编号：</span><input type="text" style="width: 132px;" value="${params['acqMerchant']}" name="acqMerchant" /></li>
	        <li><span>终端编号：</span><input type="text" style="width: 132px;" value="${params['acq_terminal_no']}" name="acq_terminal_no" /></li>
	        <li ><span style="width:140px">创建时间：</span>
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
						<th width="120">收单机构商户名称</th>
						<th width="120">收单机构英文简称</th>
						<th width="70">交易类型</th>
						<th width="70">批次号</th>
						<th width="70">流水号</th>
						<th width="70">终端号</th>
						<th width="120">创建时间</th>
						<th width="60">操作</th>
					</tr>
				</thead>
				<c:forEach items="${list.content}" var="item" varStatus="status">
					<c:out value=""></c:out>
					<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
						<td class="center"><span class="center">${status.count}</span></td>
						<td>${item.acq_merchant_name}</td>
						<td>${item.acq_enname}</td>
						<td>${item.mti}</td>
						<td>${item.batch_no}</td>
						<td>${item.serial_no}</td>
						<td>${item.acq_terminal_no}</td>
						<td>
							<fmt:formatDate value="${item.create_time}"
								pattern="yyyy-MM-dd HH:mm:ss" type="both" />
						</td>
						<td class="center">
							<a href="${ctx}/log/acqLogDetail?id=${item.id}">详情</a>
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
