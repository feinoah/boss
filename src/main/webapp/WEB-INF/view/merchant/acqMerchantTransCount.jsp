<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	var root = '${ctx}';
	function exportExcel2() {
		var val = "";
		var transSources = $("[name=transSource]:checked");
		for(var i = 0; i < transSources.length ;i ++){
			val  += $(transSources[i]).val() + "," ;
		}
		$("[name=transSource]").val(val);
		var action = $("form:first").attr("action");
		$("form:first").attr("action", "${ctx}/acqTrans/export").submit();
		$("form:first").attr("action", action);
	}
	
	$(function(){
		$("#submitButton").bind("click",function(){
			var val = "";
			var transSources = $("[name=transSource]:checked");
			for(var i = 0; i < transSources.length ;i ++){
				val  += $(transSources[i]).val() + "," ;
			}
			$("[name=transSource]").val(val);
		});
		
		//后台返回数据时，显示选中的多选框
		var queryTrans = "${param.transSource}".split(",");
		for(var i = 0; i < queryTrans.length; i ++){
			$("#"+queryTrans[i]).attr("checked", true);
		}
	})
		
		
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理&gt;收单商户交易统计
		</div>
		<form:form action="${ctx}/acqTrans/transCount" method="post">
			<div id="search">
				<div id="title">交易查询</div>
				<ul>
					<li><span style="width:140px">收单机构商户名称/编号：</span><input type="text" style="width: 132px;" value="${params['acqMerchant']}" name="acqMerchant" /></li>
					<li>
						<span>交易时间：</span>
						<input onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" style="width: 123px"
							name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
						~
						<input onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" style="width: 123px"
							name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
					</li>
				</ul>
				<div class="clear"></div>
				<ul>
					<li style="width:780px;line-height: 25px;">
					 	<label style="width: 160px;">交易来源：</label>
						<input type="checkbox" value='COM_MOBILE_PHONE' id="COM_MOBILE_PHONE"  name="transSource" style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
							 readonly="readonly" />企业版&nbsp;&nbsp;
			            <input type="checkbox" value='POS'  id="POS" name="transSource"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            	  readonly="readonly"  />POS
			            <input type="checkbox" value='SMALLBOX_MOBOLE_PHONE' id="SMALLBOX_MOBOLE_PHONE"  name="transSource"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            	  readonly="readonly"  />移小宝	
			            <input type="checkbox" value='DOT' id="DOT"  name="transSource"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            	  readonly="readonly"  />点付宝	
			            <input type="checkbox" value='NEW_LAND_ME30' id="NEW_LAND_ME30"  name="transSource"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            	  readonly="readonly"  />YPOS08	
			            <input type="checkbox" value='NEW_LAND_ME31' id="NEW_LAND_ME31"  name="transSource"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            	  readonly="readonly"  />YPOS09	
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton" value="查询" />
				<input name="reset" class="button blue medium" type="reset" id="reset" value="清空" />
                <shiro:hasPermission name="TRANS_DOWNLOAD">
					<input id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()" value="导出excel" />
				</shiro:hasPermission>
			</div>
		</form:form>
		<a name="_table"></a>
		<div class="tbdata">
			<a name="_table"></a>
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="30">序号</th>
						<th width="110">收单机构商户编号</th>
						<th width="110">收单机构商户名称</th>
						<th width="120">代理商名称</th>
						<th width="50">所属销售</th>
						<th width="55">商户状态</th>
						<th width="145">交易周期</th>
						<th width="55">成功笔数</th>
						<th width="95">交易总额</th>
						</tr>
						<c:forEach items="${list.content}" var="item" varStatus="status">
							<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
								<td class="center"><span class="center">${status.count}</span></td>
								<td><u:substring length="25" content="${item.acq_merchant_no}" /></td>
								<td>${item.acq_merchant_name}</td>
								<td>${item.agent_name}</td>
								<td>${item.sale_name}</td>
								<td>
								<c:choose>
								      <c:when test="${item.locked eq '0'}">正常</c:when>
								      <c:when test="${item.locked eq '1'}">锁定</c:when>
								      <c:when test="${item.locked eq '2'}">废弃</c:when>
								      <c:otherwise>${item.locked}</c:otherwise>
								</c:choose>
								</td>
								<td>${item.trans_cycle}</td>
								<td>${item.total_pur_count}</td>
								<td>${item.total_pur_amount}元</td>
							</tr>
						</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table" />
		</div>
	</div>
</body>
