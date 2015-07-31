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
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
		
		function showDetail(id)
		{
			$.dialog({title:'代理商详情',width: 800,height:500,resize: false,lock: true,max:false,content: 'url:agentDetail?id='+id+'&layout=no'});
		}
		function appendTermianl(id)
		{
			$.dialog({title:'代理商增机',width: 320,height:180,resize: false,lock: true,max:false,content: 'url:appendTerminal?agentNo='+id+'&layout=no',close: function(){
							 	$("#submit").click();
					      }});
		}
		
		function agentDel(agentNo){
			$.dialog.confirm('确定要删除该条记录吗？', function(){
				ajaxAgentDel(agentNo);
			});
			
		}
		
		function agentFreeze(agentNo){
			
			$.dialog.confirm('确定要冻结该代理商吗？', function(){
				ajaxAgentFreeze(agentNo);
			});
			
		}
		
		function ajaxAgentFreeze(agentNo){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentFreeze",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		successMsg("代理商冻结成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		
		function ajaxAgentDel(agentNo){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentDel",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		successMsg("代理商删除成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		
		function successMsg(contentMsg){
			var dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: 'success.gif',ok:null ,close:function(){
				location.href="agentQuery";
			}});
		}
		
		function setAgentNo(op){
			var value = ""+op.value;
			if(value!=null && value.trim().length>0){
				var obj = $("#agent-select");
				obj.get(0).selectedIndex=0;
				obj.val(value);
			}
		}
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：代理商管理>代理商查询</div>
   
   <form:form id="agentQuery" action="${ctx}/agent/agentQuery" method="post">
   <div style="position:absolute;margin-left:62px;margin-top:48px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 104px;border:none;">
         	</div>
    <div id="search">
    	<div id="title">代理商查询</div>
	      <ul>
	      	<li><span>代理商：</span><u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" /></li>

			<li><span>代理商编号：</span><input type="text" style="width:130px" class="input"  value="${params['agentNoText']}" name="agentNoText" onblur="setAgentNo(this);" /></li>
			<li><span>代理商名称：</span><input type="text" style="width:130px" class="input"  value="${params['agentName']}" name="agentName" id="agentName" /></li>
			 
		 </ul>
	      <div class="clear"></div>
	      <ul>
	        <li><span>扣率审核：</span> 
	         	<select style="padding:2px;width:140px" name="checked_status">
	         		<option value="-1" <c:out value="${params['checked_status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['checked_status'] eq '1'?'selected':'' }"/>>已审核</option>
	         		<option value="0" <c:out value="${params['checked_status'] eq '0'?'selected':'' }"/>>未审核</option>
	         	</select>
			 </li>   
	         <li><span>扣率锁定：</span> 
	         	<select style="padding:2px;width:140px" name="locked_status">
	         		<option value="-1" <c:out value="${params['locked_status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['locked_status'] eq '1'?'selected':'' }"/>>锁定</option>
	         		<option value="0" <c:out value="${params['locked_status'] eq '0'?'selected':'' }"/>>未锁定</option>
	         	</select>
			 </li> 
			  <li><span style="width:100px;">分润比例审核：</span> 
	         	<select style="padding:2px;width:111px" name="share_checked_status">
	         		<option value="-1" <c:out value="${params['share_checked_status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['share_checked_status'] eq '1'?'selected':'' }"/>>已审核</option>
	         		<option value="0" <c:out value="${params['share_checked_status'] eq '0'?'selected':'' }"/>>未审核</option>
	         	</select>
			 </li>	
		 </ul>
		 <div class="clear"></div>
		 <ul>
		     <li><span style="width:100px;">分润比例锁定：</span> 
	         	<select style="padding:2px;width:115px" name="share_locked_status">
	         		<option value="-1" <c:out value="${params['share_locked_status'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['share_locked_status'] eq '1'?'selected':'' }"/>>锁定</option>
	         		<option value="0" <c:out value="${params['share_locked_status'] eq '0'?'selected':'' }"/>>未锁定</option>
	         	</select>
			 </li>
			 <li><span>投资：</span> 
	         	<select style="padding:2px;width:140px" name="is_invest">
	         		<option value="-1" <c:out value="${params['is_invest'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['is_invest'] eq '1'?'selected':'' }"/>>是</option>
	         		<option value="0" <c:out value="${params['is_invest'] eq '0'?'selected':'' }"/>>否</option>
	         	</select>
			 </li>	
		 </ul>
		  <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="30">序号</th>
          <th width="40">编号</th>
          <th width="150">代理商名称</th>
          <th width="55">所属销售</th>
          <th width="45">机具数</th>
          <th width="30">投资</th>
          <th width="45">投资额</th>
          <th width="100">创建时间</th>
          <th width="40">状态</th>
          <th width="65">扣率</th>
          <th width="65">分润比例</th>
          <th width="130">操作</th>
          </tr>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.agent_no}</td>
	          <td align="left" ><u:substring length="18" content="${item.agent_name}"/></td>
	          <td align="left" >${item.sale_name}</td>
	          <td align="left">${item.terminal_count}</td>
	          <td>
		          <c:choose>
					  <c:when test="${item.is_invest eq '1'}">是</c:when>
					  <c:when test="${(item.is_invest eq null) || (item.is_invest eq '0') || (item.is_invest eq '')}">否</c:when> 
				  </c:choose>
			  </td>
			  <td><c:if test="${item.is_invest eq '1'}">${item.invest_amount}万</c:if></td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td class="center">
	          <c:choose>
				  <c:when test="${item.agent_status eq '1'}">正常</c:when>
				  <c:when test="${item.agent_status eq '2'}">冻结</c:when> 
			  </c:choose>
	          </td>
	          <td align="left">
		           <c:if test="${item.checked_status eq '1'}">已审核</c:if>
		          <c:if test="${item.locked_status eq '1'}">,已锁定</c:if>
		           <c:if test="${item.checked_status eq '0'}">未审核</c:if>
		          <c:if test="${item.locked_status eq '0'}">,未锁定</c:if>
	          </td>
	           <td align="left">
		          <c:if test="${item.share_checked_status eq '1'}">已审核</c:if>
		          <c:if test="${item.share_locked_status eq '1'}">,已锁定</c:if>
		           <c:if test="${item.share_checked_status eq '0'}">未审核</c:if>
		          <c:if test="${item.share_locked_status eq '0'}">,未锁定</c:if>
	          </td>
	          <td class="center">
	         <shiro:hasPermission name="AGENT_QUERY_DETAIL"> <a href="javascript:showDetail(${item.id});">详情</a> |
	         </shiro:hasPermission>
	         <shiro:hasPermission name="AGENT_QUERY_MODIFY"> <a href="${ctx }/agent/agentModload?id=${item.id}">修改</a>
	          | </shiro:hasPermission>
	          <shiro:hasPermission name="AGENT_QUERY_ADD"><a href="javascript:appendTermianl(${item.agent_no})">增机</a>|</shiro:hasPermission> 
	          <c:if test="${item.isDel=='true'}">
		         <shiro:hasPermission name="AGENT_QUERY_DELETE"> <a href="javascript:agentDel(${item.agent_no})">删除</a></shiro:hasPermission>
	          </c:if>
	           <c:if test="${item.isDel=='false'}">
	           	 <shiro:hasPermission name="AGENT_QUERY_FREEZE"><a href="javascript:agentFreeze(${item.agent_no})">冻结</a></shiro:hasPermission>
	           </c:if>
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
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
