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
		    $("[name=agent_no]").find("option").each(function(i, n) {
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
	                            $("[name=agent_no]").find("option").each(function(i, n) {
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
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
	                
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
	    
	    $("[name=agent_no]").change(function(){
	    	var agent_Name =  $("[name=agent_no]").find("option:selected").text(); //集群所属所属代理商名称
			if(agent_Name != ""){
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
	    	});
	});
		
		function showDetail(id)
		{
			$.dialog({title:'收单机构商户详情',width: 650,height:350,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
		}

		function terminalInput(acq_merchant_no,acq_enname)
		{
			$.dialog({title:'添加收单机构终端',resize: false,lock: true,max:false,content: 'url:terminalInput?acq_merchant_no='+acq_merchant_no+'&acq_enname='+acq_enname+'&layout=no'});
		}
		
		
		
		function offMerchant(acq_merchant_no){		
		    
			 if(!confirm("确定要关闭  "+acq_merchant_no+" 收单机构商户吗？")){
			     return;
		      }		
		     var type="off";    
			 $.post(
			                '${ctx}/acq/updateMerchantStatus',
							{type:type,acq_merchant_no:acq_merchant_no},
							function(data)
							{
								if(data == 1)
								{
									alert("成功关闭");
									$('#submitButton').click();
								}else{
								    alert('关闭失败，请检查数据');
									$('#submitButton').click();
								}
							}
	            	 );
 	      }
 	    
 	    
 	  function onMerchant(acq_merchant_no, acq_enname, id){

		  if('halpay' === acq_enname){
			  $.get('${ctx}/acq/checkPosMerchant?id=' + id,function(msg){
				  if(msg === 'error'){
					  $.dialog.alert('系统出错啦，请重试！');
				  }else{
					if(msg != null){
						// 判断是否优质商户和钱包结算
					  $.dialog.confirm('确定要开启 ' + acq_merchant_no + ' 好乐付收单机构商户吗？'+ (msg.my_settle || msg.bag_settle === '1' ? '该收单商户对应的普通商户的是否优质商户和钱包结算会在你开启后自动置为否！' : ''), function(){
						  openAcqMerchant(acq_merchant_no);
					  });
					}else{
						alert("开启失败,未绑定实名商户!");
					}
					  
				  }
			  });
		  }else{
			  $.dialog.confirm('确定要开启 ' + acq_merchant_no + ' 收单机构商户吗？', function(){
				  openAcqMerchant(acq_merchant_no);
			  });
		  }
      }

		function openAcqMerchant(acq_merchant_no){
			$.post('${ctx}/acq/openMerchant', {acq_merchant_no:acq_merchant_no}, function(data) {
				if(data == 1) {
					$.dialog.alert('成功开启！',function(){
						$('#submitButton').click();
					});
				}else if(data == 2){
					$.dialog.alert("开启失败,请从集群中删除对应的实名商户！",function(){
						$('#submitButton').click();
					});
				}else{
					$.dialog.alert('开启失败，请检查数据！', function(){
						$('#submitButton').click();
					});
				}

			});
		}
 	   
 	   
		$(function(){
 	  		$('#export').on('click', function(){
 	  			var action= $("form:first").attr("action"),
					totalPage = parseInt('${list.totalPages}');
			   	   
					//根据当前页查询的总页数来判断是否导出，必须先进行查询
					if(totalPage <= 0){
						$.dialog.alert("<pre>没有需要导出的数据！</pre>");
					} else if(totalPage > 100){
						$.dialog.alert("<pre>请选择一些必要的查询条件并进行查询，避免因导出数据过多导致的系统异常！</pre>");
					} else {
						$("form:first").attr("action","${ctx}/acq/merExport").submit();
						$("form:first").attr("action",action);
					}
			});
		});
	</script>
	<style type="text/css">

	</style>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理&gt;收单机构商户</div>
   
   <form:form id="merQuery" action="${ctx}/acq/merchantQuery" method="post">
   <div style="position:absolute;margin-left:63px;margin-top:48px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 110px;border:none;">
         	</div>
    <div id="search">
    	<div id="title">收单机构商户</div>
	      <ul>
  	      	<li style="width: 220px;"><span>代理商名称：</span><u:select value="${params['agent_no']}"  stype="agent" sname="agent_no"  onlyThowParentAgent="true"  /></li>
	        <li><span style="width: 140px;">收单机构商户名称/编号：</span><input type="text"  style="width: 132px;" value="${params['acq_merchant']}" name="acq_merchant" /></li>
	      	<li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	      </ul>
	      <div class="clear"></div>
	      <ul>
	      <li style="width: 220px;"><span>是否A类：</span>
	         <select name="large_small_flag" style="padding:2px;width: 140px;">
	         	<option value="-1" <c:out value="${params['large_small_flag'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="1" <c:out value="${params['large_small_flag'] eq '1'?'selected':'' }"/>>是</option>
	         	<option value="0" <c:out value="${params['large_small_flag'] eq '0'?'selected':'' }"/>>否</option>
	         </select>
	         </li>
	      	<%--<li style="width: 220px;"><span>可否大套小：</span>
	         <select name="large_small_flag" style="padding:2px;width: 140px;">
	         	<option value="-1" <c:out value="${params['large_small_flag'] eq '-1'?'selected':'' }"/>>全部</option>
	         	<option value="1" <c:out value="${params['large_small_flag'] eq '1'?'selected':'' }"/>>可套</option>
	         	<option value="0" <c:out value="${params['large_small_flag'] eq '0'?'selected':'' }"/>>不可套</option>
	         </select>
	         </li>
	         --%><li><span style="width: 140px;">收单机构：</span>
					<select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
						<option value="">全部</option>
						<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>	
						</c:forEach>
					</select>
			 </li>	
			 <li style="width: 220px;"dir="ltr"><span>锁定状态：</span>
	         <select name="locked" style="padding:2px;width: 130px;margin-left:12px;" >
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
    	<shiro:hasPermission name="SYSTEM_MERCHANTQUERY_ADD">
    	<input   class="button blue medium" type="button" id="submitAdd" onclick="javascript:window.location.href='merchantAdd'"  value="增加"/>
    	</shiro:hasPermission>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    	<input name="export" class="button blue medium" type="button" id="export"  value="导出收单机构商户"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata"  style="overflow:scroll;overflow-y:auto;">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="30px;">序号</th>    
          <th width="110px;">代理商名称</th>
          <th width="116px;">收单机构商户编号</th>    
          <th width="124px;">收单机构商户名称</th> 
          <th width="30px;">代付</th>
          <th width="30px;">A类</th>
          <%--<th width="60px;">可否大套小</th>
          --%><th width="50px;">锁定状态</th>
          <th width="61px;">状态</th>
          <th width="115px;">创建时间</th>
          <th width="97px;">操作</th>
        
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td style="word-break: break-all ;"><u:substring length="22" content="${item.agent_name}"/></td>
	          <td style="word-break: break-all ;">${item.acq_merchant_no}</td>
	          <td style="word-break: break-all ;"><u:substring length="22" content="${item.acq_merchant_name}"/></td>
	          <td align="center">
	          <c:choose>
				  <c:when test="${item.rep_pay =='1' }">否</c:when> 
				  <c:when test="${item.rep_pay =='2' }">是</c:when>
				  </c:choose> 
	          </td>
	          <td align="center">
	          <c:choose>
	          <c:when test="${item.large_small_flag =='1' }">是</c:when>
				  <c:when test="${item.large_small_flag =='0' }">否</c:when> 
				  <%--<c:when test="${item.large_small_flag =='1' }">可套</c:when>
				  <c:when test="${item.large_small_flag =='0' }">不可套</c:when> 
			  --%></c:choose> 
	          </td>
	          <td align="center" title="${item.locked_msg}">
	            <c:choose>
				  <c:when test="${item.locked ==0 }">正常</c:when>
				  <c:when test="${item.locked ==1 }">锁定</c:when>
				  <c:when test="${item.locked ==2 }">废弃</c:when>
			  	</c:choose> 
	          </td>
	          <td align="center"> 
		          <c:choose>
				 	<c:when test="${item.locked eq '0'}">
				 		<span class="font_gray">开通</span> |
				 		<a href="javascript:offMerchant('${item.acq_merchant_no}');">关闭</a>
				 	</c:when>
				 	<c:when test="${item.locked eq '1'}">
				 		<a href="javascript:onMerchant('${item.acq_merchant_no}', '${item.acq_enname}', '${item.id}');">开通</a> |
				 		<a href="javascript:offMerchant('${item.acq_merchant_no}');">关闭</a>
				 	</c:when>
				 	<c:otherwise>
				 		<a href="javascript:onMerchant('${item.acq_merchant_no}', '${item.acq_enname}', '${item.id}');">开通</a> |
				 		<span class="font_gray">关闭</span>
				 	</c:otherwise>
				 </c:choose>
	          </td>
	          
<td><fmt:formatDate value="${item.create_time}" type="both"/></td>	       
<td align="center">
<shiro:hasPermission name="SYSTEM_MERCHANTQUERY_DETAIL"><a href="javascript:showDetail(${item.id});">详</a> | 
</shiro:hasPermission>
<shiro:hasPermission name="SYSTEM_MERCHANTQUERY_MODIFY"><a href="merchantInput?id=${item.id}">改</a>
|</shiro:hasPermission>
<shiro:hasPermission name="SYSTEM_MERCHANTQUERY_ADDEDTERMINAL">
 <a href="javascript:terminalInput('${item.acq_merchant_no}','${item.acq_enname}');">添加终端</a>
 </shiro:hasPermission> </td>
 </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
  <script type="text/javascript">
		$("#agentInput").val( $("[name=agent_no]").find("option:selected").text());
	</script>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
