<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<script type="text/javascript">
	$(function() {
		//初始省市联动
		var INIT_OPTION = "--请选择--";
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");

		$.each(provinceName, function(i, n) {
			$("<option></option>").val(n).text(n).appendTo("#province");
		});
		
		$("#province").change(function() {
			var province = $("#province").val();
			$("#city").empty();
			$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");
			if (province != "") {
				var provinceIndex = $("#province option:selected").index();
				var cityArray = eval("city" + provinceIndex);
				$.each(cityArray, function(i, n) {
					$("<option></option>").val(n).text(n).appendTo("#city");
				});

			}
		});

		var defaultProvince = '${params.province}';
		var defaultCity = '${params.city}';

		if ($.trim(defaultProvince).length > 0) {
			$("#province").val(defaultProvince);
			$("#province").change();

			if ($.trim(defaultCity).length > 0) {
				$("#city").val(defaultCity);
			}
		}
		
	});
	
	
	
	
	
		function showDetail(id)
		{
			//$.dialog({title:'商户详情',width: 720,height:530,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
			window.location.href='${ctx}/mer/merDetail?id='+id;
		}
		
		
		function merDel(id,merchant_no){
			if(!confirm("是否删除该商户？"))
			{
				return;
			}
			$.post(
				'${ctx}/mer/merDel',
				{id:id,merchant_no:merchant_no},
				function(data)
				{
					var ret = data.msg;
					if(ret == "OK")
					{
						alert("商户删除成功");
						$("#submit").click();
					}else if(ret == "ERROR"){
					  	alert("商户删除出错！");
						$("#submit").click();
					 }
				}
			);
		}
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：销售管理>商户查询</div>
   <form:form id="merQuery" action="${ctx}/sale/merQuery" method="post">
    <div id="search">
    	<div id="title">商户查询</div>
	      <ul>
  	      		      <li style="width:216px"><span>代理商名称：</span><select name="agentNo" style="width:140px;padding:2px"> 
<option value="">全部</option> 
  <c:forEach items="${list1.content}" var="item"> 
      <option value="${item.agent_no}" <c:if test="${params['agentNo'] eq item.agent_no}">selected="selected"</c:if>>${item.agent_name}</option> 
  </c:forEach> 
</select></li>
	        <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	      	<li><span>创建时间：</span>
			 	<input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	      <ul>
			 <li style="width:216px"><span>商户状态：</span> 
	         	<select style="padding:2px;width:140px" name="openStatus">
	         		<option value="-1" <c:out value="${params['openStatus'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['openStatus'] eq '1'?'selected':'' }"/>>正常</option>
	         		<option value="0" <c:out value="${params['openStatus'] eq '0'?'selected':'' }"/>>商户关闭</option>
	         		<option value="2" <c:out value="${params['openStatus'] eq '2'?'selected':'' }"/>>待审核</option>
	         		<option value="3" <c:out value="${params['openStatus'] eq '3'?'selected':'' }"/>>审核失败</option>
	         		<option value="5" <c:out value="${params['openStatus'] eq '5'?'selected':'' }"/>>机具绑定</option>
	         		<option value="6" <c:out value="${params['openStatus'] eq '6'?'selected':'' }"/>>初审</option>
	         	</select>
			 </li>
			<li><span style="width: 90px;">设备类型：</span> 
	         	<u:TableSelect sname="pos_type" style="padding:2px;width:128px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
			 </li>
			 <li><span style="width: 76px;">手机号码：</span><input type="text"  value="${params['phone']}" name="phone" /></li>
			 <li><span>开通方式：</span><select style="padding:2px;width:140px" name="open_type">
	         		<option value="-1" <c:out value="${params['open_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['open_type'] eq '1'?'selected':'' }"/>>手动</option>
	         		<option value="2" <c:out value="${params['open_type'] eq '2'?'selected':'' }"/>>自动</option>
	         	</select>
			 </li>
			 <li><span style="width: 90px;">持卡人姓名：</span><input type="text"  value="${params['account_name']}" name="account_name" /></li>
			 <li><label style="width: 65px;">省份：<select id="province" name="province"  style="	width: 75px;height: 24px;vertical-align: top;" ></select></label>
			       <label  style="width: 65px;">市区：<select id="city" name="city" style="	width: 75px;height: 24px;vertical-align: top;"></select></label>
			 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5%">序号</th>
          
          <th>代理商名称</th>
          <th>商户简称</th>
          <th  width="80">商户状态</th>
          <th width="120">创建时间</th>
          <th width="60">开通方式</th>
          <th width="100">操作</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.agent_name}</td>
	           <td>${item.merchant_short_name}</td>
	          <td >
	          <c:choose>
				 <c:when test="${item.open_status eq '1'}">正常</c:when>
				 <c:when test="${item.open_status eq '0'}">商户关闭</c:when> 
				 <c:when test="${item.open_status eq '2'}">待审核</c:when> 
				 <c:when test="${item.open_status eq '5'}">机具绑定</c:when> 
				 <c:when test="${item.open_status eq '6'}">初审</c:when> 
				 <c:when test="${item.open_status eq '3'}"><span class="font_red">审核失败</span></c:when> 
			  </c:choose>
			  </td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td class="center">
	          	<c:choose>
				  <c:when test="${item.open_type eq '1'}">手动</c:when>
				  <c:when test="${item.open_type eq '2'}">自动</c:when>
				</c:choose> 
	          </td>
	          <td class="center">
	          <shiro:hasPermission name="B_SALE_MANAGER_DETAIL">
	           <a href="javascript:showDetail(${item.id});">详情</a> 
	          </shiro:hasPermission>
	          <c:if test="${item.open_status eq '2' || item.open_status eq '3'}">
	           		<shiro:hasPermission name="SALE_MERCHANT_DEL">| <a href="javascript:merDel(${item.id},${item.merchant_no})">删除 </a></shiro:hasPermission>
	           </c:if>
	          </td>
	        </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>
  </div>
  <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
