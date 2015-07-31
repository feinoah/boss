<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
	<script type="text/javascript">
	$(function(){
		var cus = 0;
	    var classname = "";
	    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
	    $("#agentInput").keyup(function(event) {
		    var arry = new Array();
		    $("[name=agentNo]").find("option").each(function(i, n) {
		        arry[i] = $(this).text();
		    });
	        if ((event.keyCode != 38) && (event.keyCode != 40) && (event.keyCode != 13)) {
	            $autocomplete.empty();
	            var $SerTxt = $("#agentInput").val().toLowerCase();
	            if ($SerTxt != "" && $SerTxt != null) {
	                for (var k = 0; k < arry.length; k++) {
	                    if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
	                    	
	                        $("<li title=" + arry[k] + " class=" + classname +" style='background-color: #FFFFFF'></li>").text(arry[k]).appendTo($autocomplete).mouseover(function() {
	                            $(".autocomplete li").removeClass("hovers");
	                            $(this).css({
	                                background: "#3368c4",
	                                color: "#fff"
	                            });
	                        }).mouseout(function() {
	                            $(this).css({
	                                background: "#fff",
	                                color: "#000"
	                            });
	                        }).click(function() {
	                        	var text=$(this).text();
	                            $("#agentInput").val(text);
	                            $("[name=agentNo]").find("option").each(function(i, n) {
	                                if($(this).text()==text){
	                             	   $(this).prop('selected', 'true');
	                                }
	                             });
	                            $autocomplete.hide();
	                        });
	                    }
	                }
	            }
	            $autocomplete.show();
	        }
	        var listsize = $(".autocomplete li").size();
	        $(".autocomplete li").eq(0).addClass("hovers");
	        if (event.keyCode == 38) {
	            if (cus < 1) {
	                cus = listsize - 1;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                   if($(this).text()==text){
	                	   $(this).prop('selected', 'true');
	                   }
	                });
	            } else {
	                cus--;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                    if($(this).text()==text){
	                    	$(this).prop('selected', 'true');
	                    }
	                 });
	            }
	        }
	        if (event.keyCode == 40) {
	            if (cus < (listsize - 1)) {
	                cus++;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                    if($(this).text()==text){
	                    	$(this).prop('selected', 'true');
	                    }
	                 });
	            } else {
	                cus = 0;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                    if($(this).text()==text){
	                 	   $(this).prop('selected', 'true');
	                    }
	                 });
	            }
	        }
	        if (event.keyCode == 13) {
	            $(".autocomplete li").removeClass();
				$autocomplete.hide();

	        }
	    });
	    
	    $("[name=agentNo]").change(function(){
	    	var agent_Name =  $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
			if(agent_Name != ""){
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
	    	});
	});
	
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>收单机构终端</div>
   
   <form:form id="merQuery" action="${ctx}/acq/terminalQuery" method="post">
   <div style="position:absolute;margin-left:63px;margin-top:48px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 110px;border:none;">
         	</div>
    <div id="search">
    	<div id="title">收单机构终端</div>
	      <ul>
	     	<li style="width: 220px;"><span>代理商名称：</span><u:select value="${params['agentNo']}"  stype="agent" sname="agentNo" onlyThowParentAgent="true"  /></li>
	      <li><span style="width:140px">收单机构商户名称/编号：</span><input type="text" style="width: 132px;" value="${params['acqMerchant']}" name="acqMerchant" /></li>
	        <li><span  style="width:90px">终端编号：</span><input type="text" style="width: 132px;" value="${params['acq_terminal_no']}" name="acq_terminal_no" /></li>
	      </ul>
	      <div class="clear"></div>
	      <ul>
  	      	<li style="width: 220px;"><span>可否大套小：</span>
	         <select name="large_small_flag" style="padding:2px;width: 140px;">
	         	<option value="-1" <c:out value="${params['large_small_flag'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="1" <c:out value="${params['large_small_flag'] eq '1'?'selected':'' }"/>>可套</option>
	         	<option value="0" <c:out value="${params['large_small_flag'] eq '0'?'selected':'' }"/>>不可套</option>
	         </select>
	         </li>
	         <li><span style="width: 140px;">收单机构：</span>
					<select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
						<option value="">全部</option>
						<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>	
						</c:forEach>
					</select>
			 </li>	
			 <li style="width: 240px;"><span>锁定状态：</span>
	         <select name="locked" style="padding:2px;width: 140px;margin-left:12px;" >
	            <option value="-1" <c:out value="${params['locked'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="0" <c:out value="${params['locked'] eq '0'?'selected':'' }"/>>正常</option>
	         	<option value="1" <c:out value="${params['locked'] eq '1'?'selected':'' }"/>>锁定</option>
	         	<option value="2" <c:out value="${params['locked'] eq '2'?'selected':'' }"/>>废弃</option>
	         </select>
	         </li>	
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    <%--	<shiro:hasPermission name="SYSTEM_MERCHANTQUERY_ADD">
    	<input  class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='${ctx}/acq/terminalInput'"  value="增加"/>
    	</shiro:hasPermission>
<%--    	<input   class="button blue medium" type="button" id="submit" onclick="javascript:window.location.href='${ctx}/acq/terminalInput'"  value="增加"/>--%>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="25">序号</th>
          <th width="110">收单机构商户名称</th>
          <th width="105">收单机构商户编号</th>
          <th width="95">收单机构终端编号</th>
          <th  width="40">批次号</th>
          <th width="40">流水号</th>
          <th width="50">锁定状态</th>
          <th width="60">状态</th>
          <th width="120">更新时间</th>
        <th width="100">操作</th>
       </tr> 
       </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td><u:substring length="8" content="${item.acq_merchant_name}"/></td>
	          <td>${item.acq_merchant_no}</td>
	          <td>${item.acq_terminal_no}</td>
	          <td>${item.batch_no}</td>
	          <td>${item.serial_no}</td>
	          <td align="center" title="${item.locked_msg}">
	            <c:choose>
				  <c:when test="${item.locked ==0 }">正常</c:when>
				  <c:when test="${item.locked ==1 }">锁定</c:when>
				  <c:when test="${item.locked ==2 }">废弃</c:when>
			  	</c:choose> 
	          </td>
	          <td> 
		          <c:choose>
				 	<c:when test="${item.status eq '1'}">
				 		<span class="font_gray">开通</span> |
				 		<a href="javascript:offTerminal('${item.acq_merchant_no}','${item.acq_terminal_no}');">关闭</a>
				 	</c:when>
				 	<c:otherwise>
				 		<a href="javascript:onTerminal('${item.acq_merchant_no}','${item.acq_terminal_no}');">开通</a> |
				 		<span class="font_gray">关闭</span>
				 	</c:otherwise>
				 </c:choose>
	          </td>
			<td><fmt:formatDate value="${item.last_update_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
			 <td  class="center">
			 <shiro:hasPermission name="SYSTEM_TERMINALQUERY_UPDATE_LOCKED">
			 <a href="javascript:updateLocked('${item.id}');" title="修改锁定状态">改|</a>
			 </shiro:hasPermission>
			 <c:if test="${item.large_small_flag=='1'}">
			 <shiro:hasPermission name="SYSTEM_TERMINALQUERY_BIND">
			 <a href="javascript:bind('${item.acq_merchant_no}','${item.acq_terminal_no}','${item.acq_enname }');">大套小|</a>
			 </shiro:hasPermission>
			 </c:if>
			 <c:if test="${item.large_small_flag !='1'}">
			 	<span class="font_gray">大套小</span>|
			 </c:if>
			 <shiro:hasPermission name="SYSTEM_TERMINALQUERY_DETAIL">
			 <a href="javascript:showDetail('${item.acq_merchant_no}','${item.acq_terminal_no}');" title="详情">详</a> 
			 </shiro:hasPermission>
			 <shiro:hasPermission name="SYSTEM_TERMINALQUERY_DELETE">
			  <a href="javascript:del('${item.acq_merchant_no}','${item.acq_terminal_no}','${item.id}');" title="删除">|删</a>
			 </shiro:hasPermission>
			 </td>	        
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
  <script type="text/javascript">
		$("#agentInput").val( $("[name=agentNo]").find("option:selected").text());
	</script>
 <script type="text/javascript">
 
 	function onTerminal(acq_merchant_no,acq_terminal_no){
 		if(confirm("确定要开启 "+acq_terminal_no+" 终端吗？")){
			location.href="updateTerminalStatus?type=on&acq_terminal_no="+acq_terminal_no;
 		}
 	}
 	
 	function offTerminal(acq_merchant_no,acq_terminal_no){
		if(confirm("确定要关闭  "+acq_terminal_no+" 终端吗？")){
			location.href="updateTerminalStatus?type=off&acq_terminal_no="+acq_terminal_no;
 		}
 	}
 
 	function bind(acq_merchant_no,acq_terminal_no,acq_enname)
 	{
		$.dialog({title:'大套小绑定操作',width: 340,height:240,resize: false,lock: true,max:false,
			content: 'url:merchantBind?acq_merchant_no='+acq_merchant_no+'&acq_terminal_no='+acq_terminal_no+'&acq_enname='+acq_enname+'&layout=no'});
		
 	}
 	
	function showDetail(acq_merchant_no,acq_terminal_no)
	{
		$.dialog({id:'bandList',title:'收单机构终端详情',width: 850,height:550,resize: false,lock: true,max:false,content: 'url:merchantBindDetail?acq_merchant_no='+acq_merchant_no+'&acq_terminal_no='+acq_terminal_no+'&layout=no'});
	}
	function del(acq_merchant_no,acq_terminal_no,terminalId)
	{
		if(confirm("确定要删除 "+acq_terminal_no+" 终端吗？")){
			location.href="delTerminalQuery?terminalId="+terminalId;
		}
	}
	
	
	function updateLocked(id)
	{
		location.href="${ctx}/acq/updateTerminalLocked?id="+id;
	}
 </script> 
 <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
