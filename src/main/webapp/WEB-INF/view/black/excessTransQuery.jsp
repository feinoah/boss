<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/utils.js"></script>
	<script type="text/javascript">
	 function exportExcel2(){
	  // $("#trans").attr("action","${ctx}/mer/export");
	   //alert( $("#trans").attr("action"));
	 	 // $("#trans").submit();
	 
	   //alert(document.forms[0]);
	   //document.forms[0].submit();
	   var action= $("form:first").attr("action");
	   $("form:first").attr("action","${ctx}/black/export").submit();
	   $("form:first").attr("action",action);
	   //alert($("form:first").attr("action"));
	 }
	 
		
		function test(tranId){
			alert(tranId);
			var content = "";
			
			$.dialog({content: 'url:content/content.html'});
			
			var dialog = $.dialog({ height: 540,width:350,title: '欢迎',lock:true,content: '欢迎使用lhgdialog对话框组件！',icon: 'succeed',ok: function(){
		        this.title('警告').content('请注意lhgdialog两秒后将关闭！').lock().time(2);
		        return false;
			    }
			});
		}
		
		function showDetail(id)
		{
			$.dialog({title:'订单详情',width: 650,height:450,resize: false,lock: true,max:false,content: 'url:detail?id='+id+'&layout=no'});
		}
		
		function cleanEmpty(){
			$('#merchant').val("");
		}
		
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：超额交易查询&gt;超额交易过滤</div>
   
   <form:form id="trans" action="${ctx}/black/excessTrans" method="post">
    <div id="search">
    
    	<div id="title">超额交易查询</div>
	      <ul>
	         <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" id="merchant"/></li>
	      	<li><span>交易时间：</span>
			 	<input onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})"   type="text"  style="width:106px" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  onFocus="WdatePicker({isShowClear:false,dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})"  type="text" style="width:106px" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	      <ul>
	         <li><span style="width: 50px;">金额：</span><input type="text"  value="${params['s_trans_amount']}" name="s_trans_amount" id="s_trans_amount" maxlength="10"/>元~
	         <input type="text"  value="${params['m_trans_amount']}" name="m_trans_amount" id="m_trans_amount"  maxlength="10"/>元
			 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="button" onclick="cleanEmpty()"  value="清空"/>
    <shiro:hasPermission name="EXCESS_TRANS_DOWNLOAD">
    	<input  id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()"  value="导出excel"/>
  	</shiro:hasPermission>

    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr>
        <th width="30">序号</th>
          <th  width="130">商户简称</th>
          <th width="60">交易类型</th>
          <th width="50">卡种</th>
          <th width="110">交易卡号</th>
          <th width="90">金额(元)</th>
          <th width="60">状态</th>
          <th width="120">创建时间</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
            <td class="center"><span class="center">${status.count}</span></td>
	          <td><u:substring length="10" content="${item.merchant_short_name}"/></td>
	          <td>
	          <c:choose>
				  <c:when test="${item.trans_type eq 'PURCHASE'}">消费</c:when>
				  <c:otherwise>${item.trans_type }</c:otherwise>
			  </c:choose></td>
	          <td>
	          <c:choose>
				  <c:when test="${item.card_type eq 'CREDIT_CARD'}">贷记卡</c:when>
				  <c:when test="${item.card_type eq 'DEBIT_CARD'}">借记卡</c:when> 
				  <c:when test="${item.card_type eq 'SEMI_CREDIT_CARD'}">准贷记卡</c:when>
				  <c:otherwise>${item.card_type}</c:otherwise>
			  </c:choose></td>
	          <td> <u:cardcut content="${item.account_no}"/></td>
	          <td  align="right">
	          <c:choose>
					<c:when test="${item.trans_amount == null}">***</c:when>		
					<c:otherwise>
						${item.trans_amount}
					</c:otherwise>
			</c:choose>元 </td>
	          <td >
	          <c:choose>
				  <c:when test="${item.trans_status eq 'OVERLIMIT'}">已超额</c:when>
			  </c:choose> 
			  </td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
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
