<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<head>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px;width:328px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
 <script type="text/javascript">
//可输入下拉菜单
	$(function(){
		var INIT_OPTION = "--请选择--";
		var pr = "${params['province']}";
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
		$.each(provinceName, function(i, n) {
			if(pr != "" && pr == n){
				$("<option selected='selected'></option>").val(n).text(n).appendTo("#province");
			}else{
				$("<option></option>").val(n).text(n).appendTo("#province");
			}
		});
		var agentT = $("[name=agentNo]").find("option:selected").text();
		$("#agentInput").val(agentT);
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
			    });/*.blur(function() {
			        setTimeout(function() {
			            $autocomplete.hide();
			        },
			        3000);
			    });*/
			    
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
   $.dialog({title:'YS商户详情',width: 650,height:350,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
  }

  function terminalInput(acq_merchant_no,acq_enname)
  {
   $.dialog({title:'YS添加终端',resize: false,lock: true,max:false,content: 'url:terminalInput?acq_merchant_no='+acq_merchant_no+'&acq_enname='+acq_enname+'&layout=no'});
  }
  
  function removeGroupInfo(id,group_code){	  
	  if(id>0 && group_code != ""){
       if(!confirm("是否确定要删除编号为："+group_code+" 的集群信息？")){
		    return;
	    }		    
		 $.post(
						'${ctx}/group/removeGroup',
						{"id":id,"group_code":group_code},
						function(data)
						{
							if(data.msg == "1"){
								alert("删除失败，请转移该集群下所有普通商户信息!");
							}else if(data.msg == "2"){
								alert("删除失败，请转移该集群下所有收单商户信息!");
							}else if(data.msg == "OK"){
								alert("删除成功!");
								$("#merQuery").submit();
							}else if(data.msg == "ERROR"){
								alert("删除失败!");
							}
						}
          	 );
	  }else{
		  alert("删除失败，系统出现错误！");
	  }
}
  
 </script>
 <style type="text/css">

 </style>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>路由集群查询</div>
   
   <form:form id="merQuery" action="${ctx}/group/query" method="post"><%--
   <div style="position:absolute;top:202px; left:367px\9;height:10px;*height:200px;*margin:20px;padding:0px;background-color: red">
         		--%>
         	
    <div id="search">
     <div id="title">路由集群查询</div>
       <ul>
          <li><span style="width: 70px;">集群编号：</span><input type="text"  style="width: 132px;" value="${params['group_code']}" name="group_code"  maxlength="7"/></li>
          <li><span style="width: 70px;">集群名称：</span><input type="text"  style="width: 220px;" value="${params['group_name']}" name="group_name" /></li>
           <li><span style="width: 50px;">状态：</span>
                 <select style="padding:2px;width:160px" name="status">
	         		<option value="" <c:out value="${params['status'] eq ''?'selected':'' }"/>>--请选择--</option>
	         		<option value="0" <c:out value="${params['status'] eq '0'?'selected':'' }"/>>正常</option>
	         		<option value="1" <c:out value="${params['status'] eq '1'?'selected':'' }"/>>停用</option>
	         	</select>
		  </li>
       </ul>
       
       <ul>
          <li><span style="width: 80px;">所属代理商：</span><u:select  style="width: 220px;height: 23px"  value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" />
          		<div style="position:absolute;margin-left:51px;margin-top:-20px;">
	         		<input name="agentInput" id="agentInput"  value="全部bububu"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 197px !important;border: none;">
	         	</div>
          </li>
          <li><span style="width: 70px;">收单机构：</span>
					<select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
						<option value="">全部</option>
						<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>	
						</c:forEach>
					</select>
		</li>
           <li><span style="width: 45px;">属性：</span>
                 <select style="padding:2px;width: 253px;" id="route_type"  name="route_type">
                 			<option value="">全部</option>
                 			<option value="1"  <c:if test="${params['route_type'] eq '1'}">selected = "selected"</c:if>>销售专用</option>
                 			<option value="2" <c:if test="${params['route_type'] eq '2'}">selected = "selected"</c:if>>公司自建</option>
                 			<option value="3" <c:if test="${params['route_type'] eq '3'}">selected = "selected"</c:if>>技术测试</option>
                 	</select>
		  </li>
       </ul>
       <ul>
          <li><span style="width: 70px;">所属销售：</span>
          		<select id="sales" name="sales" style="padding:2px;width: 140px;">
						<option value="">全部</option>
						<c:forEach items="${salesList}" var="sales">
						<option value="${sales.id}" <c:if test="${sales.id eq params['sales']}">selected = "selected"</c:if>>${sales.real_name}</option>		
						</c:forEach>
					</select>
          </li>
          <li><span style="width: 70px;">商户类型：</span>
				<select id="merchant_type" name="merchant_type" style="padding:2px;width:230px">
							<option value="">--请选择--</option>
								<option value="5541" <c:if test="${params.merchant_type eq '5541' }">selected="selected"</c:if>>民生类</option>
								<option value="5812" <c:if test="${params.merchant_type eq '5812' }">selected="selected"</c:if>>餐娱类</option>
								<option value="5111" <c:if test="${params.merchant_type eq '5111' }">selected="selected"</c:if>>批发类</option>
								<option value="5331" <c:if test="${params.merchant_type eq '5331' }">selected="selected"</c:if>>一般类</option>
								<option value="1520" <c:if test="${params.merchant_type eq '1520' }">selected="selected"</c:if>>房车类</option>
								<option value="1011" <c:if test="${params.merchant_type eq '1011' }">selected="selected"</c:if>>其他</option>
						</select>
		</li>
		<li><span style="width: 70px;">结算周期：</span>
				<select style="padding:2px;width:140px" id="accounts_period"  name="accounts_period">
						<option value="">全部</option>
						<option value="1"  <c:if test="${params.accounts_period eq '1' }">selected="selected"</c:if>>T+1</option>
						<option value="0"  <c:if test="${params.accounts_period eq '0' }">selected="selected"</c:if>>T+0</option>
					</select> 
		</li>
       	<li><span style="width: 70px;">所属省份：</span><select id="province" name="province"  style="	width: 228px;height: 24px;vertical-align: top;" ></select>	</li>
       	<li>
       		<span style="width: 70px;">是否优质：</span>
       		<select id="my_settle" name="my_settle"  style="width: 140px;height: 24px;vertical-align: top;" >
       			<option value="-1">--全部--</option>
       			<option value="1"  <c:if test="${params.my_settle eq '1' }">selected="selected"</c:if>>是</option>
       			<option value="0"  <c:if test="${params.my_settle eq '0' }">selected="selected"</c:if>>否</option>
       		</select>
       	</li>
       	<li>
       		<span style="width: 90px;">是否钱包结算：</span>
       		<select id="bag_settle" name="bag_settle"  style="width: 210px;height: 24px;vertical-align: top;" >
       			<option value="-1">--全部--</option>
       			<option value="1"  <c:if test="${params.bag_settle eq '1' }">selected="selected"</c:if>>是</option>
       			<option value="0"  <c:if test="${params.bag_settle eq '0' }">selected="selected"</c:if>>否</option>
       		</select>
       	</li>
       </ul>
       <div class="clear"></div>
    </div>
    <div class="search_btn">
     <input   class="button blue medium" type="submit" id="submitA"  value="查询"/>
     <shiro:hasPermission name="GROUP_ADD">
    <input   class="button blue medium" type="button" id="submitA" onclick="javascript:window.location.href='${ctx}/group/add'"  value="增加"/>
     </shiro:hasPermission>
     <input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5">序号</th>
          <th width="5">集群编号</th>
          <th width="60">集群名称</th>
          <th width="5">是否优质</th>
          <th width="5">是否钱包结算</th>
          <%--
          <th width="16">收单机构</th>
          <th width="15">商户类型</th>
           <th width="15">结算周期</th>
         <th width="47">集群描述</th>
          <th width="10">属性</th>
          --%><th width="5" >状态</th>
          <th width="40">操作</th>
        <c:forEach items="${list.content}" var="item" varStatus="status">
         <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
           <td class="center"><span class="center">${status.count}</span></td>
           <td class="center"><span class="center">${item.group_code}</span></td>
           
           <td style="word-break: break-all ;" title="">
           <c:choose>
                  <c:when test="${item.status eq '0'}">
                   <span class="font_red" title=" 备注: ${item.route_describe}"> ${fn:substring(item.group_name,0,70)} </span>
                   <%-- <u:substring length="20" content="${item.group_name}"  class="font_red" /> --%>
                  
                  </c:when>
                   <c:when test="${item.status eq '1'}">
                <%--    <u:substring length="20" content="${item.group_name}"  class="font_gray"/>  ${fn:substring(item.group_name,0,35)} --%>
                    <span class="font_gray" title="备注: ${item.route_describe}">  ${fn:substring(item.group_name,0,70)}</span>
                  </c:when>
           </c:choose>
          </td>
          <%--<td class="center">
          <c:forEach items="${acqOrgList}" var="m">
						<c:if test="${m.acq_enname eq item.acq_no}">${m.acq_cnname}</c:if>
			</c:forEach>
          </td>
          <td class="center">
								<c:if test="${item.merchant_type eq '5812' }">餐娱类</c:if>
								<c:if test="${item.merchant_type eq '5111' }">批发类</c:if>
								<c:if test="${item.merchant_type eq '5541' }">民生类</c:if>
								<c:if test="${item.merchant_type eq '5331' }">一般类</c:if>
								<c:if test="${item.merchant_type eq '1520' }">房车类</c:if>
								<c:if test="${item.merchant_type eq '1011' }">其他</c:if>
          </td>
          <td class="center">
          	<c:if test="${item.accounts_period ne null}">T+${item.accounts_period}</c:if>
          </td>
          <td class="center">
          <c:choose>
				 	  <c:when test="${item.status eq '0'}"><span class="font_red"><u:substring length="27" content="${item.route_describe}"/></span></c:when>
					  <c:when test="${item.status eq '1'}"><span class="font_gray"><u:substring length="27" content="${item.route_describe}"/></span></c:when> 
					  <c:otherwise>${item.route_describe}</c:otherwise>
				</c:choose> 
          </td>--%>
          <%--<td class="center">
          	<c:if test="${item.route_type eq '1'}">销售</c:if>
          	<c:if test="${item.route_type eq '2'}">公司</c:if>
          	<c:if test="${item.route_type eq '3'}">测试</c:if>
          </td>
           --%>
           <td class="center">
			 	<c:choose>
				 	  <c:when test="${item.my_settle eq '0'}">否</c:when>
					  <c:when test="${item.my_settle eq '1'}">是</c:when> 
					  <c:otherwise>${item.my_settle}</c:otherwise>
				</c:choose> 
			</td>
			<td class="center">
			 	<c:choose>
				 	  <c:when test="${item.bag_settle eq '0'}">否</c:when>
					  <c:when test="${item.bag_settle eq '1'}">是</c:when> 
					  <c:otherwise>${item.bag_settle}</c:otherwise>
				</c:choose> 
			</td>
           <td class="center">
			 	<c:choose>
				 	  <c:when test="${item.status eq '0'}"><span class="font_red">正常</span></c:when>
					  <c:when test="${item.status eq '1'}"><span class="font_gray">停用</span></c:when> 
					  <c:otherwise>${item.status}</c:otherwise>
				</c:choose> 
			</td>
             <td align="center">
               <shiro:hasPermission name="GROUP_UPDATE">
    				<%--<a href="${ctx}/group/updateInput?id=${item.id}">修改</a>--%>           
    				<c:choose>
    				    <c:when test="${item.status eq '1'}">
    				        <shiro:hasPermission name="GROUP_MER_UPDATE">
    				        <a href="${ctx}/group/getGroupInfoById?id=${item.id}">修改</a>
    				        </shiro:hasPermission>
    				        <shiro:hasPermission name="GROUP_MER_DELETE">
    				        | <a href="javascript:void(0);"  onclick="removeGroupInfo(${item.id}, ${item.group_code})">删除</a>
    				        </shiro:hasPermission>
    				    </c:when>
    				    <c:otherwise>
    				    	<shiro:hasPermission name="GROUP_ADD_MERCHANT">
    				    		<a href="${ctx}/group/groupMerchantAdd?id=${item.group_code}">增加普通商户</a>|
    				    	</shiro:hasPermission>
    				        <shiro:hasPermission name="GROUP_ADD_ACQ_MERCHANT">
	               		    <a href="${ctx}/group/groupAcqMerchantAdd?id=${item.group_code}">增加收单商户</a>|
	               		    </shiro:hasPermission>
	               		    <shiro:hasPermission name="GROUP_MER_UPDATE">
	               		    <a href="${ctx}/group/getGroupInfoById?id=${item.id}">修改</a>|
	               		    </shiro:hasPermission>
	               		    <shiro:hasPermission name="GROUP_MER_DELETE">
	               		    <a href="javascript:void(0);"  onclick="removeGroupInfo(${item.id}, ${item.group_code})">删除</a>
    				        </shiro:hasPermission> 
    				    </c:otherwise>
    				</c:choose>
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
