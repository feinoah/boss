<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	 function showDetail(id,type)
		{
			$.dialog({title:'T+0额度修改',width: 700,height:500,resize: false,lock: true,max:false,content: 'url:bagTzeroAmountModifyDetail?id='+id+'&type='+type+'&layout=no'}); 
		}
		
	$(function(){
		var chooseObj = "${params['chooseObj']}";
		if(chooseObj=="agent"  || chooseObj==""){
			$("#agent").show();
			$("#merchant").hide();
		}else if(chooseObj=="merchant"){
			$("#agent").hide();
			$("#merchant").show();
		}
	})
		
	function objectChange(){
		var chooseObj = $("#chooseObj").val();
		if(chooseObj=="agent" || chooseObj==""){
			$("#agent").show();
			$("#merchant").hide();
		}else if(chooseObj=="merchant"){
			$("#agent").hide();
			$("#merchant").show();
		}
	}
	
	function reSelect(){
		$("#tbdata :checkbox").each(function (i,n) {
			 if(!$(n).attr("disabled"))
			 {
				 $(this).prop("checked",!$(this).prop("checked"));
		     }
        });
	};
	
	function selectAll(){
		$("#tbdata :checkbox").attr("checked", true); 
	}
	
	function batchDo(){
		var type = $("#chooseObj").val();
		// 判断是否至少选择一项
		var checkedNum = $("input[name='ids']:checked").length;
		if(checkedNum == 0) {
			alert("请选择至少一项！");
			return;
		}
		// 批量选择
		if(confirm("确定要批量修改所选项目？")) {
			var checkedList = new Array();
			$("input[name='ids']:checked").each(function() {
				checkedList.push($(this).val());
			});
			alert(checkedList);
			$.dialog({title:'T+0额度修改',width: 700,height:500,resize: false,lock: true,max:false,content: 'url:bagTzeroAmountModifyDetail?id='+checkedList+'&type='+type+'&layout=no'});
			/* $.ajax({
				type: "POST",
				url: "deletemore",
				data: {'delitems':checkedList.toString()},
				success: function(result) {
					$("[name ='subChk']:checkbox").attr("checked", false);
					window.location.reload();
				}
			}); */
		}
	}
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;T+0额度修改</div>
   
   <form:form id="bagExtractionQuery" action="${ctx}/purse/bagTzeroAmountModifyQuery" method="post">
    <div id="search">
    	<div id="title">T+0额度修改</div>
	      <ul>
	        <li><span style="width: 80px;">对象选择：</span>
	        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="chooseObj" name="chooseObj" onchange="objectChange();">
         		<option value="agent" <c:if test="${params['chooseObj'] eq 'agent' || params['chooseObj'] eq ''}">selected='selected'</c:if>>代理商</option>
		        <option value="merchant" <c:if test="${params['chooseObj'] eq 'merchant'}">selected='selected'</c:if>>商户</option>	
			</select>
	        </li>
	      </ul>
	      <br/>
	      <ul id="agent">
	        <li ><span style="width: 80px;">代理商编号：</span><input type="text" style="width: 100px;" value="${params['agentNo']}" name="agentNo" /></li>
	        <li ><span style="width: 80px;">代理商名称：</span>
	        	<%-- <input type="text" style="width: 100px;" value="${params['agentName']}" name="agentName" /> --%>
	        	<select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="agentName" name="agentName">
	         		<option value="" <c:if test="${params['agentName'] eq ''}">selected='selected'</c:if>></option>
	         		<c:forEach items="${agentList }" var="agentNameItem">
	         			<option value="${agentNameItem.one_level_agent_name }" <c:if test="${params['agentName'] eq agentNameItem.one_level_agent_name}">selected='selected'</c:if>>${agentNameItem.one_level_agent_name }</option>
	         		</c:forEach>
				</select>
	        </li>	    
	      </ul>
	      <ul id="merchant">
	        <li ><span style="width: 80px;">商户编号：</span><input type="text" style="width: 100px;" value="${params['merchantNo']}" name="merchantNo" /></li>
	        <li ><span style="width: 80px;">商户名称：</span><input type="text" style="width: 100px;" value="${params['merchantName']}" name="merchantName" /></li>
	        <li ><span style="width: 80px;">所属销售：</span>
	        	<%-- <input type="text" style="width: 100px;" value="${params['merchantSaleName']}" name="merchantSaleName" /> --%>
	        	<select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="merchantSaleName" name="merchantSaleName">
	         		<option value="" <c:if test="${params['merchantSaleName'] eq ''}">selected='selected'</c:if>></option>
	         		<c:forEach items="${saleList }" var="saleNameItem">
	         			<option value="${saleNameItem.sale_name }" <c:if test="${params['merchantSaleName'] eq saleNameItem.sale_name}">selected='selected'</c:if>>${saleNameItem.sale_name}</option>
	         		</c:forEach>
				</select>
	        </li>	    
	      </ul>
	      <br/>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>  	
    	<!-- <input class="button blue medium" type="button" id="modify"  value="修改"/> -->
    </div>
    </form:form>
    
    <!-- <div id="total_msg">
		<span style="padding-left: 20px;"><a href="javascript:selectAll();">全选</a></span>
		<span style="padding-left: 20px;"><a href="javascript:reSelect();">反选</a></span>
		<span style="padding-left: 20px;"><a href="javascript:batchDo();">批量修改</a></span>
	</div> -->
		
    <a name="_table"></a>
      <div class="tbdata"  id="tbdata"   style="width:99%" >
      <table width="100%"   cellspacing="0" class="t2"  >
        <thead>
          <tr>
        	<!-- <th width="30">选择</th> -->
          <th width="30">序号</th>
          <c:choose>
			  <c:when test="${params['chooseObj'] eq 'merchant'}">
			  	<th width="120">商户编号</th>
			  	<th width="120">手机号</th>
          		<th width="120">商户名称</th>
          	  </c:when>
			  <c:otherwise>
			  	<th width="120">代理商编号</th>
			  	<th width="120">代理商名称</th>
			  </c:otherwise>
   		  </c:choose>
          <th width="120">额度详情</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"  >
              <%-- <td class="center">
				  <input type="checkbox" name="ids"  value="${item.id}"/>
	          </td> --%>
	          <td class="center"><span class="center  orderNo">${status.count}</span></td>
	         
	          <c:choose>
				  <c:when test="${params['chooseObj'] eq 'merchant'}">
				  	<td class="center">${item.merchant_no}</td>
				  	<td class="center">${item.mobile_no}</td>
	          		<td class="center">${item.merchant_name}</td>
	          	  </c:when>
				  <c:otherwise>
				  	<td class="center">${item.agent_no}</td>
				  	<td class="center">${item.one_level_agent_name}</td>
				  </c:otherwise>
   		  	  </c:choose>
			  <td class="center"><a href="javascript:showDetail('${item.id }','${params.chooseObj }');">查看</a></td>
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
