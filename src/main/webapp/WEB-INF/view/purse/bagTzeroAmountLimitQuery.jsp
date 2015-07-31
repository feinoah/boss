<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	var id;
	var mobileNo;
	var appType;
	function showDetail(id,mobileNo,appType)
		{
			/* $.dialog({title:'入账详情',width: 700,height:400,resize: true,lock: true,max:false,content: 'url:bagTzeroAmountLimitDetail?id='+id+'&layout=no'}); */
			window.location.href='bagTzeroAmountLimitDetail?id='+id+"&mobileNo="+mobileNo+"&appType="+appType;
		}
	
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包管理&gt;提升额度审核</div>
   
   <form:form id="bagTransQuery" action="${ctx}/purse/bagTzeroAmountLimitQuery" method="post">
    <div id="search">
    	<div id="title">提升额度审核</div>
	      <ul>
	        <li><span style="width: 100px;">商户名称：</span><input type="text"  value="${params['merchantName']}" name="merchantName" /></li>
	        <li><span style="width: 100px;">商户编号：</span><input type="text"  value="${params['merchantNo']}" name="merchantNo" /></li>
	        <li><span style="width: 100px;">手机号：</span><input type="text"  value="${params['mobileNo']}" name="mobileNo" /></li>
			<li>
			<span >创建时间:</span>
	         		
	         		<input  type="text"  style="width:102px" readonly="readonly" id="startDate" name="startDate" value="${params['startDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				&nbsp;&nbsp;&nbsp;&nbsp;~&nbsp;&nbsp;&nbsp;&nbsp;
				<input  type="text" style="width:102px" readonly="readonly" id="endDate" name="endDate" value="${params['endDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
	         
			</li>
	      <!-- </ul>
	      <ul> -->
	      	<%-- <li><span>审核人：</span>
		        <select  style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="checker" name="checker">
	         		<option value="" <c:if test="${params['status'] eq ''}">selected='selected'</c:if>>全部</option>
			        <c:forEach items="${purseStatusList}" var="item">
			             <option value="${item.code_id}" <c:if test="${params['status'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
			        </c:forEach> 
				</select>
			</li>--%>
			<li><span>审核阶段：</span>
		        <select  style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="phase" name="phase">
	         		<option value="" <c:if test="${params['phase'] eq ''}">selected='selected'</c:if>>全部</option>
			        <option value="0" <c:if test="${params['phase'] eq '0'}">selected='selected'</c:if>>申请资料</option>	
			        <option value="1" <c:if test="${params['phase'] eq '1'}">selected='selected'</c:if>>提额资料</option>
			        <option value="2" <c:if test="${params['phase'] eq '2'}">selected='selected'</c:if>>审核完成</option>	
				</select>
			</li>
			<li><span>审核状态：</span>
		        <select  style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="chechStatus" name="chechStatus">
	         		<option value="" <c:if test="${params['chechStatus'] eq ''}">selected='selected'</c:if>>全部</option>
			        <option value="0" <c:if test="${params['chechStatus'] eq '0'}">selected='selected'</c:if>>未审核</option>
			        <option value="1" <c:if test="${params['chechStatus'] eq '1'}">selected='selected'</c:if>>审核通过</option>
			        <option value="2" <c:if test="${params['chechStatus'] eq '2'}">selected='selected'</c:if>>审核不通过</option>	
				</select>
			</li>
	      </ul>
	      <br/>
			<div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="16%" >商户名称</th>  
          <th width="12%">手机号</th>     
          <th width="16%">审核阶段</th>
          <th width="16%">审核人</th>
          <th width="8%">审核状态</th>
          <th width="10%">创建时间</th>
          <th width="6%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"
	           <c:if test="${item.status=='2'||item.status=='3'}">style="color:red;"</c:if> 
	           <c:if test="${item.status=='4'}">style="color:blue;"</c:if> 
	        >
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.merchant_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">
	          	<c:choose>
					<c:when test="${item.check_status=='0' || item.check_status=='2'}">申请资料</c:when>
					<c:when test="${item.check_status=='1' && item.is_rich_check=='1'}">提额资料</c:when>
					<c:when test="${item.check_status=='1' && item.is_rich_check=='0'}">审核完成</c:when>
				</c:choose>
				<%-- <c:if test="${basicMap['check_status'] eq '0' || basicMap['check_status'] eq '2'}">基础资料</c:if>
				<c:if test="${basicMap['check_status'] eq '1' && basicMap['is_rich_check'] eq '1'}">丰富资料</c:if> --%>
	          </td>
	          <td class="center"  style="word-break: break-all ;">${item.checker}</td>
	          <td class="center">
	          	<c:if test="${item.check_status eq '0'}">未审核</c:if>
	          	<c:if test="${item.check_status eq '1'}">审核通过</c:if>
	          	<c:if test="${item.check_status eq '2'}">审核不通过</c:if>
	          </td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			 
			 <a href="javascript:showDetail(${item.id},${item.mobile_no},${item.app_type});">详情</a>
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
