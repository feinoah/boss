<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>

<script type="text/javascript">
		
		function bindRemove(merchant_no,terminal_no,trcount){
			 if(confirm('确定要解除该终端的大套小吗？')){
				 ajaxBindRemove(merchant_no,terminal_no,trcount);
		     }
		}
		
		function ajaxBindRemove(merchant_no,terminal_no,trcount){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/acq/bindRemove",
		 			data:{"merchant_no":merchant_no,"terminal_no":terminal_no},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		$("#tr"+trcount).remove();
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
	</script>
</head>
<body>
<div class="item liHeight">
	<div class="title">收单机构终端信息 </div>
	<ul>
		  <li style="width:350px;" id="acq_merchant_name"><span style="width: 120px;">收单机构：</span>
		  	 <c:if test="${params['acq_enname'] eq 'eptok' }">银盛</c:if>
			 <c:if test="${params['acq_enname'] eq 'tftpay'}">腾付通</c:if>
			 <c:if test="${params['acq_enname'] eq 'halpay'}">好乐付</c:if>
		  </li>
		  <li style="width:350px;" id="acq_merchant_name"><span style="width: 120px;">收单机构商户名称：</span><u:substring length="8" content="${params['acq_merchant_name']}"/></li>
		  <li style="width:350px;" id="acq_merchant_no"><span style="width: 120px;">收单机构商户编号：</span>${params['acq_merchant_no']}</li>
		  <li style="width:350px;" id="acq_terminal_no"><span style="width: 120px;">收单机构终端编号：</span>${params['acq_terminal_no']}</li>
		  <li style="width:350px;" id="merchant_no"><span style="width: 120px;">实名商户名称：</span><u:substring length="8" content="${params['merchant_name']}"/></li>
		  <li style="width:350px;" id="merchant_no"><span style="width: 120px;">实名商户编号：</span>${params['merchant_no']}</li>
		  <li style="width:350px;" id="agent_name"><span style="width: 120px;"> 代理商名称：</span>${params['agent_name']}</li>
		  <div class="clear"></div>
    </ul>
</div>
<div class="clear"></div>

<div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2" style="line-height: 1.5;font:12px/1.5 \5FAE\8F6F\96C5\9ED1,Tahoma,Verdana,Arial,Helvetica,sans-serif">
        <thead>
        <tr><th width="35">序号</th>
          <th width="100">商户号</th>
          <th width="150">商户名称</th>
          <th width="80">终端号</th>
          <th width="105">最后更新时间</th>
          <th width="105">创建时间</th>
          <th width="40">操作</th>
          </tr>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr id="tr${status.count}"   class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.merchant_no}</td>
	          <td align="left"> <u:substring length="15" content="${item.merchant_short_name}"/></td>
	          <td align="left" >${item.terminal_no}</td>
	          <td><fmt:formatDate value="${item.last_update_time}" type="both"/></td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td align="center">
	    		<a href="javascript:bindRemove('${item.merchant_no}','${item.terminal_no}','${status.count}')">解除</a>
	    	  </td>
	        </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>

</body>
</html>