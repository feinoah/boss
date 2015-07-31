<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>

 <script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
 <script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
 <script type="text/javascript">
   
 function del(trans_route_group_merchant_id){ 
  $.dialog.confirm('确定删除记录吗？', function(){
	  $.ajax({
			type:"post",
			url:"${ctx}/group/groupMerchantDel",
			data:{"id":trans_route_group_merchant_id},
			dataType: 'json',
		  	success: function(data){
		    	var ret = data.msg;
			  	if (ret == "OK"){
			  		alert("删除成功!");
			  		//document.getElementById('trans_route_group_merchant_id').value=trans_route_group_merchant_id;
			        //formSubmit('merQuery', null,null,successMsg,null,null);
			  	}else{
			  		alert("删除失败,请重试!");
			  	}
		  }
		}
	);
      /*document.getElementById('merQuery').action="${ctx}/group/groupMerchantDel";
      document.getElementById('trans_route_group_merchant_id').value=trans_route_group_merchant_id;
       formSubmit('merQuery', null,null,successMsg,null,null);*/
     });
 } 
 
 function successMsg(){
   var dialog = $.dialog({title: '提示',lock:true,content: '删除成功',icon: 'success.gif',ok:null,close:function(){
	   location.href="${ctx}/group/groupMerchantQuery";
   }});
   
  }
 
 
 $(function(){
	 //集群普通商户导出Excel
	 $("#exportMerchant").click(function(){
		var checkCode = /^([0-9])+$/; 
		 var group_code = $("#group_code").val();
		 var merchant = $("#merchant").val();
		 if(group_code != "" && group_code.length > 0){
			 
			 if(!group_code.match(checkCode)){
				 alert("集群编号仅允许输入数字。");
				 return false;
			 }
			 
			 if(group_code.length > 6 || group_code.length < 3){
				 alert("集群编号仅允许输入3~6位数以内的数字。");
				 return false;
			 }
			 
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/group/checkGroupCodeValidity",
		 			data:{"group_code":group_code,"merchant":merchant},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.success;
					  	if (ret == true){
					  		$("#merQuery").attr({action:"${ctx}/group/exportGroupMerchant"}).submit();
					  		$("#merQuery").attr({action:"${ctx}/group/groupMerchantQuery"});
					  	}else{
					  		alert("该集群编号不存在，或该集群下没有可导出的商户信息。");
					  	}
				  }
		 		}
		 	);
		 }else{
			 alert("请输入集群编号!");
		 }
	 });
 });
  
 </script>
 <style type="text/css">

 </style>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>路由集群编号查询</div>
   
   <form:form id="merQuery" action="${ctx}/group/groupMerchantQuery" method="post">
   <input type="hidden" name="id" id="trans_route_group_merchant_id" />
    <div id="search">
     <div id="title">路由集群编号查询</div>
       <ul>
          <li><span style="width: 140px;">集群编号：</span><input type="text"  style="width: 132px;" value="${params['group_code']}" name="group_code"  id="group_code"/></li>
             <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant"  id="merchant"/></li>
             <li>
		    <label>是否实名：</label>
			<select id="real_flag" name="real_flag"  style="width: 129px; height: 24px; vertical-align: top;">
			    <option value="-1">-请选择-</option>
				<option value="0" <c:out value="${params['real_flag'] eq '0'?'selected':'' }"/>>否</option>
				<option value="1" <c:out value="${params['real_flag'] eq '1'?'selected':'' }"/>>是</option>
			</select>
		 </li>	
           </ul>
       <div class="clear"></div>
    </div>
    <div class="search_btn">
     <input   class="button blue medium" type="submit" id="query"  value="查询"/>
 <%--     <shiro:hasPermission name="GROUP_MERCHANT_ADD">
     <input   class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='${ctx}/group/groupMerchantAdd'"  value="增加"/>
     </shiro:hasPermission> --%>
     <input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
     <input name="exportMerchant" class="button blue medium" type="button" id="exportMerchant"  value="导出普通商户"/>
    </div>
    
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5%">序号</th>
          <th width="100">商户编号</th>
          <th width="140">商户名称</th>
          <th width="120">集群编号</th>
          <th width="120">集群名称</th>
          <th width="120">操作</th>
        <c:forEach items="${list.content}" var="item" varStatus="status">
         <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
           <td class="center"><span class="center">${item.id}</span></td>
           <td class="center"><span class="center">${item.merchant_no}</span></td>
           <td><u:substring length="11" content="${item.merchant_name}"/></td>
           <td class="center"><span class="center">${item.group_code}</span></td>
           <td><u:substring length="8" content="${item.group_name}"/></td>
           </td>
            <td align="center">
               <shiro:hasPermission name="GROUP_MERCHANT_DEL"><a href="javascript:del(${item.id})">删除</a>
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
