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
			//-------------------------------------------------bind-------------------------------------------------
			$("#bind").click(function(){
				var chk_value =[];
				var isValidate = true;
				var lastAgentNo = null;    
				  $('input[name="ids"]:checked').each(function(i,n){
					//判断是否全为已分配状态下数据，且分配为同一代理商
					var hiddenOpenStatus = $(n).parent().find(".hiddenOpenStatus").val();
					var hiddenAgentNo = $(n).parent().find(".hiddenAgentNo").val();
					var orderNo = $(n).parent().parent().find(".orderNo").html();

					if(hiddenOpenStatus !== "1")//已分配状态
					{
						//alert("序号为"+orderNo+"状态不为已分配状态，不能绑定");
						 $.dialog({title: '错误',lock:true,content: "序号为"+orderNo+"状态不为已分配状态，不能绑定",icon: 'error.gif',ok: function(){
							 	$(n).focus();
				    		}
						});
						
						isValidate = false;
						return false;
					}

					if(lastAgentNo !=null && lastAgentNo!=hiddenAgentNo)
					{
						//alert("序号为"+orderNo+"代理商与前面已选择的代理商为不同代理商");
						$.dialog({title: '错误',lock:true,content: "序号为"+orderNo+"代理商与前面已选择的代理商为不同代理商",icon: 'error.gif',ok: function(){
							 	$(n).focus();
				    		}
						});
						isValidate = false;
						return false;
					}
					else
					{
						lastAgentNo = hiddenAgentNo;
					}
					    
				   	chk_value.push($(this).val());    
				  });
				 

				  if(isValidate)
				  {
					  if(chk_value.length==0){
					  		//alert("没有选择任何机具！");
						  $.dialog({title: '错误',lock:true,content: "没有选择任何机具",icon: 'error.gif'
							});
					  }else{
					  
						  $.dialog({height:200,width: 450,lock: true,drag: false,resize: false,max: false,content: 'url:viewTerminal?ids='+chk_value+'&agentNo='+lastAgentNo+'&layout=no',close: function(){
							 	//$("#submitButton").click();
							 	$('form:first').submit();
					      }});
					  }
				  }    
				 
			});
			//-------------------------------------------------bind-------------------------------------------------
			
		$("#distribute").click(function(){
			var chk_value =[];    
			var isValidate = true;
			  $('input[name="ids"]:checked').each(function(i,n){
					//判断是否全为已入库
					var hiddenOpenStatus = $(n).parent().find(".hiddenOpenStatus").val();
					var orderNo = $(n).parent().parent().find(".orderNo").html();

					if(hiddenOpenStatus !== "0")//已分配状态
					{
						//alert("序号为"+orderNo+"状态不为已入库状态，不能分配");
						$.dialog({title: '错误',lock:true,content: "序号为"+orderNo+"状态不为已入库状态，不能分配",icon: 'error.gif',ok: function(){
							 	$(n).focus();
				    		}
						});
						isValidate = false;
						return false;
					}

					
			   		chk_value.push($(this).val());    
			  });    
			  if(isValidate)
			  {
				  if(chk_value.length==0){
					  //alert("没有选择任何机具！");
					  $.dialog({title: '错误',lock:true,content: "没有选择任何机具",icon: 'error.gif'
						});
					  }else{
					  
					  $.dialog({height:200,width: 510,lock: true,drag: false,resize: false,max: false,content: 'url:viewDistributeTerminal?ids='+chk_value+'&layout=no',close: function(){
// 					  		location=location
						//$("#submitButton").click();
						$('form:first').submit();
				      }});
					  }
			  }
			  
		});
			
			
		$("#select").click(function(){
			 $("#tbdata :checkbox").each(function (i,n) {
				 if(!$(n).attr("disabled"))
				 {
					 $(this).prop("checked",!$(this).prop("checked"));
			     }
				 
           });
		});
			
			
		});
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：机具管理&gt;机具查询</div>
   
   <form:form id="terQuery" action="${ctx}/ter/terQuery" method="post">
   <div style="position:absolute;margin-left:290px;margin-top:91px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 330px;border:none;">
         	</div>
    <div id="search">
    	<div id="title">机具查询</div>
	      <ul>
	        <li><span style="width:80px;">机器SN号：</span><input type="text"  value="${params['sN']}" name="sN" /></li>
	      	<li><span>终端号：</span><input type="text"  value="${params['terminalNo']}" name="terminalNo" /></li>
	      	<li><span style="width:90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	      	<li><span style="width:90px;">PSAM编号：</span>
	      		<input type="text"  value="${params['psamNo']}" name="psamNo"  style="width: 140px"/>  ~  <input type="text"  value="${params['psamNo1']}" name="psamNo1"  style="width: 140px" />
	      	</li>
	      </ul>
	      <div class="clear"></div>
	      <ul>
	      		<li><span style="width:80px;">分配批次号：</span><input type="text"  value="${params['allot_batch']}" name="allot_batch" /></li>
	      		<li><span style="width:78px;">代理商：</span><u:select  style="width:360px;padding:2px" value="${params['agentNo']}"  stype="agent" sname="agentNo" onlyThowParentAgent="true"  /></li>
	      		<li><span style="width:86px;">机具状态：</span> 
			         	<select name="openStatus" style="padding:2px; width:148px">
								<option value="" >全部</option>
								<option value="0"  <c:if test="${params['openStatus'] eq '0'}">selected="selected"</c:if> >已入库</option>
								<option value="1"  <c:if test="${params['openStatus'] eq '1'}">selected="selected"</c:if> >已分配</option>
								<option value="2"  <c:if test="${params['openStatus'] eq '2'}">selected="selected"</c:if> >已使用</option>
			         	</select>
					 </li>
	      	</ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<shiro:hasPermission name="TERMINAL_QUERY_ADD">
    	<input   class="button blue medium" type="button" onclick="location.href='${ctx}/ter/terAddLoad'" value="增加"/>
    	</shiro:hasPermission>
    	<shiro:hasPermission name="TERMINAL_QUERY_IMP">
    	<input   class="button blue medium" type="button" onclick="location.href='${ctx}/ter/terImpExcel'" value="导入机具"/>
    	</shiro:hasPermission>
    	
    	<shiro:hasPermission name="TERMINAL_BUILD_KEY">
    	<input   class="button blue medium" type="button" id="buildkey" onclick="location.href='${ctx}/ter/viewSecKey'"  value="密钥生成"/>
    	</shiro:hasPermission>
    	
    	<input   class="button blue medium" type="button" id="select"  value="反选"/>
    	<shiro:hasPermission name="TERMINAL_QUERY_DISTRIBUTE">
    	<input   class="button blue medium" type="button" id="distribute"  value="分配"/>
    	</shiro:hasPermission>
    	<shiro:hasPermission name="TERMINAL_QUERY_BIND">
    	<input   class="button blue medium" type="button" id="bind"  value="绑定"/>
    	</shiro:hasPermission>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    	<shiro:hasPermission name="POS_MANAGER">
    	<input   class="button blue medium" type="button" id="posManager"  onclick="location.href='${ctx}/ter/posManager'"   value="设备管理"/>
    	</shiro:hasPermission>
    </div>
    </form:form>
    <a name="_table"></a>
    <div id="tbdata" class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        
        <tr>
        <shiro:hasPermission name="TERMINAL_QUERY_DISTRIBUTE">
        <th width="40">选择</th>
        </shiro:hasPermission>
        <th width="40">序号</th>
        <th width="160">代理商名称</th>
        <th width="60">机具状态</th>
        <th width="75">分配批次号</th>
        
          <th width="160">机器SN号</th>
           <th width="110">终端号</th>
          
<%--          <th width="60">机具型号</th>--%>
<%--          <th width="60">机具类型</th>--%>
          
          <th width="75">操作</th>
          </tr>
          <c:forEach items="${list.content}" var="item" varStatus="status">
          	<c:out value=""></c:out>
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	        <shiro:hasPermission name="TERMINAL_QUERY_DISTRIBUTE">
	        	<td class="center">
			 	 <c:choose>
<%--				  <c:when test="${item.open_status eq '1'}"><input type="checkbox" disabled name="ids"  value="${item.id}"/></c:when>--%>
				  <c:when test="${item.open_status eq '1'}"><input type="checkbox"  name="ids"  value="${item.id}"/></c:when>
				  <c:when test="${item.open_status eq '0'}"><input type="checkbox" name="ids"  value="${item.id}"/></c:when> 
				  <c:otherwise><input type="checkbox" disabled name="ids"  value="${item.id}"/></c:otherwise>
			  </c:choose>
			    <input type="hidden" class="hiddenOpenStatus" value="${item.open_status}"/> 
			  	<input type="hidden" class="hiddenAgentNo" value="${item.agent_no}"/> 
	        	</td>
	        	</shiro:hasPermission>
	          <td class="center"><span class="center orderNo">${status.count}</span></td>
	          <td align="left"  class="agent_name"><u:substring length="14" content="${item.agent_name}"/></td>
	          <td class="center"  >
	          <c:choose>
				  <c:when test="${item.open_status eq '0'}">已入库</c:when>
				  <c:when test="${item.open_status eq '1'}">已分配</c:when>
				  <c:when test="${item.open_status eq '2'}">已使用</c:when>  
				  <c:otherwise>${item.open_status}</c:otherwise>
			  </c:choose> 
			  </td>
			  <td>${item.allot_batch}</td>
	          <td>${item.sn}</td>
	          <td>${item.terminal_no}</td>
	          
<%--	          <td align="left"  ><u:substring length="8" content="${item.model}"/></td>--%>
<%--	          <td class="center" >--%>
<%--	             <c:choose>--%>
<%--				  <c:when test="${item.type eq 'MPOS'}">MPOS</c:when>--%>
<%--				  <c:when test="${item.type eq 'POS'}">POS</c:when> --%>
<%--				  <c:otherwise>${item.type}</c:otherwise>--%>
<%--			 	 </c:choose>--%>
<%--	          </td>--%>
	          
	          <td class="center">
	          	 <shiro:hasPermission name="TERMINAL_QUERY_DETAIL">
		          	<a href="javascript:showDetail(${item.id});">详情</a>
		          </shiro:hasPermission>
	          	<c:choose>
	          	<c:when test="${item.open_status eq '0'}">
		          <shiro:hasPermission name="TERMINAL_QUERY_MODIFY">
				          	<a href="${ctx}/ter/toTerUpdate?id=${item.id}">修改</a>
			       </shiro:hasPermission> 
		         </c:when>
	          	 <c:when test="${item.open_status eq '1'}">
	          	 <shiro:hasPermission name="TERMINAL_QUERY_UNDISTRIBUTE">
	          	 <a   href="javascript:unDistribute(${item.id});">解分</a>
	          	 </shiro:hasPermission>
	          	 </c:when>
	          	 
	          	 <c:when test="${item.open_status eq '2'}">
	          	 <shiro:hasPermission name="TERMINAL_QUERY_UNBIND">
	          	 	<a   href="javascript:unBind(${item.id},${item.agent_no});">解绑</a>
          		</shiro:hasPermission>
	          	 </c:when>
	          	 </c:choose>
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
    function toTerUpdate(id)
	{
		
		$.dialog({title:'机具修改',width: 650,height:350,resize: false,lock: true,max:false,content: 'url:terDetail?id='+id+'&layout=no'});
	}
  
  function showDetail(id)
	{
		$.dialog({title:'机具详情',width: 650,height:350,resize: false,lock: true,max:false,content: 'url:terDetail?id='+id+'&layout=no'});
	}

	//解除分配
	function unDistribute(id)
	{
		if(!confirm("是否解除分配？"))
		{
			return;
		}
	
		$.post(
				'${ctx}/ter/unDistributeTerminal',
				{id:id},
				function(data)
				{
					if(data == 1)
					{
						alert("解除分配成功");
						//$("#submitButton").click();
						$('form:first').submit();
					}
				}
		);
	}
	//解除绑定
	function unBind(id,agentNoStr)
	{
		if(!confirm("是否解除绑定？"))
		{
			return;
		}

		$.post(
				'${ctx}/ter/unBindTerminal',
				{id:id,agentNo:agentNoStr},
				function(data)
				{
					if(data == 1)
					{
						alert("解除绑定成功");
						//$("#submitButton").click();
						$('form:first').submit();
					}
				}
		);
	}

  </script>
  <script type="text/javascript">
		$("#agentInput").val( $("[name=agentNo]").find("option:selected").text());
	</script>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
