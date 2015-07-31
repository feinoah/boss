<%@page pageEncoding="utf8"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/utils.js"></script>
 <script type="text/javascript">
  
	
function riskDel(id){
	$.dialog.confirm('确定要解除监控吗？', function(){
		ajaxRiskDel(id);
	});
	
}

function ajaxRiskDel(id){
	 $.ajax({
			type:"post",
			url:"${ctx}/risk/riskMerchantDel",
			data:{"id":id},
			dataType: 'json',
		  	success: function(data){
		    	var ret = data.msg;
			  	if (ret == "OK"){
			  		successMsg("解除监控成功");
			  		return false;
			  	}
		  }
		}
	);
}

function successMsg(contentMsg){
	var dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: 'success.gif',ok:null ,close:function(){
		location.href="${ctx}/risk/riskQuery";
	}});
}

 </script>
</head>
<body>
   <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：风险管理>风险查询</div>
   
   <form:form id="trans" action="${ctx}/risk/riskQuery" method="post">
    <div id="search">
    
     <div id="title">风险查询</div>
       <ul>
          <li style="width: 300px;"><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
          <li><span>风险等级：</span> 
           <select style="padding:2px;width:73px" id="risk_class" name="risk_class">
            <option value="-1" <c:out value="${params['risk_class'] eq '-1'?'selected':'' }"/>>全部</option>
            <option value="0" <c:out value="${params['risk_class'] eq '0'?'selected':'' }"/>>提示级</option>
            <option value="1" <c:out value="${params['risk_class'] eq '1'?'selected':'' }"/>>关注级</option>
            <option value="2" <c:out value="${params['risk_class'] eq '2'?'selected':'' }"/>>预警级</option>
            <option value="3" <c:out value="${params['risk_class'] eq '3'?'selected':'' }"/>>警告级</option>
           </select>
      </li>
      </ul>
       <div class="clear"></div>
       <ul>
        <li style="width: 300px;"><span style="width: 90px;">案例编号：</span><input type="text"  value="${params['case_no']}" name="case_no" /></li>
        <li><span>创建时间：</span>
                <input onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})"   type="text"  style="width:102px" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
             ~
                 <input  onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})"  type="text" style="width:102px" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
       </li>
    </ul>
       <div class="clear"></div>
    </div>
    <div class="search_btn">
    
     <input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
     <input name="reset" class="button blue medium" type="button" onclick="cleanEmpty()"  value="清空"/>
     <shiro:hasPermission name="RISK_ADD">
    	 <input class="button blue medium" type="button" id="addButton" onclick="javascript:window.location.href='${ctx}/risk/riskMerchantInput'"  value="增加"/>
 	     	 <input class="button blue medium" type="button" id="exportButton" onclick="javascript:window.location.href='${ctx}/risk/export'"  value="导出"/>
	</shiro:hasPermission>
    </div>
    </form:form>
    
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr>
        <th width="30">序号</th>
        <th  width="55">案例编号</th>
          <th width="120">商户编号</th>
          <th width="120">商户名称</th>
          <th width="120">终端号</th>
          <th width="60">风险等级</th>
          <th width="110">创建时间</th>
           <th width="50">操作</th>
        <c:forEach items="${list.content}" var="item" varStatus="status">
	         <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	           <td>${item.case_no}</td>
	           <td> ${item.merchant_no}</td>
	           <td><u:substring length="13" content="${item.merchant_short_name}"/> </td>
	           <td> ${item.terminal_no}  </td>
	           <td>
	           <c:choose>
				  <c:when test="${item.risk_class eq '0'}">提示级</c:when>
				  <c:when test="${item.risk_class eq '1'}">关注级</c:when> 
				  <c:when test="${item.risk_class eq '2'}"><span class="font_red">预警级</span></c:when> 
				  <c:when test="${item.risk_class eq '3'}"><span class="font_red">警告级</span></c:when> 
			  </c:choose>
	           </td>
	           <td ><fmt:formatDate value="${item.create_time}" type="both"/></td>
	           <c:set value="${item.create_time}" var="create_time" scope="request"></c:set>
	           <%
	           		Timestamp t = (Timestamp)request.getAttribute("create_time");
		            Date  d = new Date(t.getTime());		         
			        Calendar cal = Calendar.getInstance(); 
			        cal.setTime(d);		       
			        cal.add(Calendar.DATE,-1); 
			        String beginDate = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime())+" 00:00";			   
			        String endDate = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime())+" 23:59";
	            %>
	            <c:set value="<%=beginDate %>" var="beginDate"></c:set>
	            <c:set value="<%=endDate %>" var="endDate"></c:set>
	           <td class="center">
	             <shiro:hasPermission name="RISK_TRANS">
	                  <a href="${ctx}/mer/trans?terminalNo=${item.terminal_no}&createTimeBegin=${beginDate}&createTimeEnd=${endDate}&transType=PURCHASE&transStatus=SUCCESS&cardType=CREDIT_CARD">查看交易</a>
	             </shiro:hasPermission>
	             <shiro:hasPermission name="RISK_REMOVE_MONITOR">
	                  <a href="javascript:riskDel(${item.id});">解除监控</a>
	             </shiro:hasPermission>
	             
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
