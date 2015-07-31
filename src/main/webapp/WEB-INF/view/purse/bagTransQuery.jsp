<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
	function showDetail(id)
		{
			$.dialog({title:'入账详情',width: 700,height:400,resize: true,lock: true,max:false,content: 'url:bagTransDetail?id='+id+'&layout=no'});
		}
	
	function statusChange(){
		var status = document.getElementById("status").value;
		if(status==2 || status == 3){
			document.getElementById("operStatusLi").style.display='block';
			document.getElementById("cloumnStatus").style='word-break: break-all ;diaplay:block';
			for(var i=1;i<21;i++){
				document.getElementById("cloumnStatusValue"+i).style='word-break: break-all ;diaplay:block';
			}
		}else{
			document.getElementById("operStatusLi").style.display='none';
			document.getElementById("cloumnStatus").style.display='none';
			for(var i=1;i<21;i++){
				document.getElementById("cloumnStatusValue"+i).style.display='none';
			}
		}
	}
	
	
	/* function selectAll(){
		alert(1234);
        $("[name=items]:checkbox").attr("checked",true);  
	}
	
	function selectNo(){
		alert(123456);
		$("[name=items]:checkbox").attr("checked",false);   
	} */
	
	
         /* $("#selectAll").click(function(){  
        	alert(11111);
            $("[name=items]:checkbox").attr("checked",true);  
        });   
        $("#checkNo").click(function(){  
        	alert(2222222);
            $("[name=items]:checkbox").attr("checked",false);  
        });   
        $("#checkRev").click(function(){  
            $("[name=items]:checkbox").each(function(){  
                $(this).attr("checked",!$(this).attr("checked"));  
            });  
        });  
        //这里send的click事件根本就没有触发  
        $("#send").click(function(){  
            var str="你选中的是：\r\n";  
            $("[name=items]:checkbox:checked").each(function(){  
                str+=$(this).val()+"\r\n";  
            });  
            alert(str);  
        });   */
	
</script>
</head>
<body>
<script type="text/javascript">
window.onload = function(){
	var status = ${params.status};
	if(status==2 || status==3){
		document.getElementById("operStatusLi").style.display='block';
		document.getElementById("cloumnStatus").style='word-break: break-all ;diaplay:block';
		for(var i=1;i<21;i++){
			document.getElementById("cloumnStatusValue"+i).style='word-break: break-all ;diaplay:block';
		}
	}
}
</script> 
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包入账查询</div>
   
   <form:form id="bagTransQuery" action="${ctx}/purse/bagTransQuery" method="post">
    <div id="search">
    	<div id="title">钱包入账查询</div>
	      <ul>
	        <li><span>手机号：</span><input type="text"  value="${params['mobileNo']}" name="mobileNo" /></li>
	        <li><span>商户名：</span><input type="text"  value="${params['merchantName']}" name="merchantName" /></li>
	        <li><span>入账状态：</span>
	        <select  style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="status" name="status" onchange="statusChange();">
         		<option value="" <c:if test="${params['status'] eq ''}">selected='selected'</c:if>>全部</option>
		        <c:forEach items="${purseStatusList}" var="item">
		             <option value="${item.code_id}" <c:if test="${params['status'] eq item.code_id}">selected='selected'</c:if>>${item.code_name}</option>	
		        </c:forEach>
			</select>
			</li>
	      </ul>
	      <br/>
				<ul>
					<li>
					<span >入账时间:</span>
  	         		
  	         		<input  type="text"  style="width:102px" readonly="readonly" id="startDate" name="startDate" value="${params['startDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
			 					&nbsp;&nbsp;&nbsp;&nbsp;~&nbsp;&nbsp;&nbsp;&nbsp;
			 					<input  type="text" style="width:102px" readonly="readonly" id="endDate" name="endDate" value="${params['endDate']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
  	         
					</li>
					<li style="display: none;" id="operStatusLi" >
						<span>操作状态：</span>
				        <select  style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="operStatus" name="operStatus">
			         		<option value="" <c:if test="${params['operStatus'] eq ''}">selected='selected'</c:if>>全部</option>
					        <option value="1" <c:if test="${params['operStatus'] eq '1'}">selected='selected'</c:if>>已操作</option>
					        <option value="2" <c:if test="${params['operStatus'] eq '2'}">selected='selected'</c:if>>未操作</option>	
						</select>
					</li>
				</ul>
				<div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata" >
    	<!-- <input type="button"  value="全选"  style="cursor: pointer;" onclick="javascript:selectAll();">  
        <input type="button"  value="反选"  style="cursor: pointer;" onclick="selectNo();">  
        <input type="button" id="send" value="批量手工入账" style="cursor: pointer;" onclick="send();">  --> 
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <!-- <th width="3%">选择</th> -->
          <th width="4%">序号</th>
          <th width="8%">手机号</th>
          <th width="6%">客户端</th>
          <th width="12%">商户号</th>
          <th width="8%" >商户名</th>       
          <th width="14%">卡号</th>
          <th width="12%">金额</th>
          <th width="10%" id="cloumnStatus" style="display: none;">操作状态</th>
          <th width="8%">状态</th>
          <th width="10%">入账时间</th>
          <th width="6%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}"
	           <c:if test="${item.status=='2'||item.status=='3'}">style="color:red;"</c:if> 
	           <c:if test="${item.status=='4'}">style="color:blue;"</c:if> 
	        >
	          <%-- <td class="center">
	          	<span class="center">
					<input type="checkbox" name="chkN" value="${item.id}" onclick="getIds(this,'${item.id}')" <c:if test="${item.status!='2'&&item.status!='3'}">name="items" disabled="true"</c:if>/>
				</span>
			  </td> --%>
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.app_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.merchant_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.merchant_name}</td>
	          <td class="center"  style="word-break: break-all ;"><u:cardcut content="${item.card_no}" /></td>
	          <td class="center"  style="word-break: break-all ;">${item.amount}元</td>
	          <td class="center" id="cloumnStatusValue${status.count}"  style="word-break: break-all ;display: none;">
	          	<c:if test="${item.hand_num=='0'}">
	          		未操作
	          	</c:if>
	          	<c:if test="${item.hand_num!='0'}">
	          		已操作
	          	</c:if>
	          </td>
	          <td class="center">${item.statusDesc}</td>
	          <td align="center"><fmt:formatDate value="${item.create_time}" type="both"/></td>
			  <td align="center">
			 
			 <a href="javascript:showDetail(${item.id});">详情</a>
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
