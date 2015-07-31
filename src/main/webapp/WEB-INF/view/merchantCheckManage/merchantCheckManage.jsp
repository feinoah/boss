<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
<script type="text/javascript">
$(function() {
	var agentT = $("[name=agent_no]").find("option:selected").text();
	$("#agentInput").val(agentT);
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

//删除
function removeCMP(id){
	if(confirm('您是否要删除？')){
		$.ajax({
			type:"post",
			url:"${ctx}/merCP/removeMerchantCheckStatusById",
			data:{'id':id},
			async:false,
			dataType: 'json',
	  		success: function(json){
	  			var removeCount = json.removeCount;
		  		if(removeCount > 0){
		  			alert("删除成功!");
		  			submitForm();
		  		}else{
		  			alert("删除失败!");
		  		}
	 		}
		});
	}
}

//关闭
function closeCMP(id){
	if(confirm('您是否要关闭？')){
		$.ajax({
			type:"post",
			url:"${ctx}/merCP/closeMerchantCheckStatusById",
			data:{'id':id},
			async:false,
			dataType: 'json',
	  		success: function(json){
	  			var removeCount = json.removeCount;
		  		if(removeCount > 0){
		  			alert("关闭成功!");
		  			submitForm();
		  		}else{
		  			alert("关闭失败!");
		  		}
	 		}
		});
	}
}

//开启
function openCMP(id){
	if(confirm('您是否要开启？')){
		$.ajax({
			type:"post",
			url:"${ctx}/merCP/openMerchantCheckStatusById",
			data:{'id':id},
			async:false,
			dataType: 'json',
	  		success: function(json){
	  			var removeCount = json.removeCount;
		  		if(removeCount > 0){
		  			alert("开启成功!");
		  			submitForm();
		  		}else{
		  			alert("开启失败!");
		  		}
	 		}
		});
	}
}

function submitForm(){
	 $("#merQuery").submit();
}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：审核人管理&gt;审核人管理
		</div>
		<form:form id="merQuery" action="${ctx}/merCP/queryMerchantCheckInfo" method="post">
		<div style="position:absolute;margin-left:538px;margin-top:46px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 105px !important;border: none;">
         	</div>
			<input type="hidden" name="id" id="trans_route_group_merchant_id" />
			<div id="search">
				<div id="title">审核人管理</div>
				<ul>
					<li>
						<span style="width: 80px;">审  核  人：</span>
						<select id="user_id" name="user_id" style="padding:2px;width: 140px;">
							<option value="">--全部--</option>
							<c:forEach items="${customServiceList}" var="customService">
								<option value="${customService.id}" <c:if test="${customService.id eq params['user_id']}">selected = "selected"</c:if>>${customService.real_name}</option>		
							</c:forEach>
						</select>
					</li>
					<li>
						<span style="width: 80px;">审核类型：</span>
						<select id="check_type"  name="check_type"  style="width:140px;padding:2px">
							<option value="-1"  <c:out value="${params['check_type'] eq '-1'?'selected':'' }"/>>--全部--</option>
							<option value="1"  <c:out value="${params['check_type'] eq '1'?'selected':'' }"/>>顺序审核</option>
							<option value="2"  <c:out value="${params['check_type'] eq '2'?'selected':'' }"/>>设备审核</option>
							<option value="3"  <c:out value="${params['check_type'] eq '3'?'selected':'' }"/>>代理商审核</option>
							<option value="4"  <c:out value="${params['check_type'] eq '4'?'selected':'' }"/>>代理设备审核</option>
						</select>
					</li>
					<li>
						<span style="width: 80px;">代理商名称：</span>
						<u:select  style="width:128px;padding:2px" value="${params['agent_no']}"  stype="agent" sname="agent_no" onlyThowParentAgent="true"  />
					</li>
				</ul>
				<ul>
					<li>
						<span style="width: 80px;">设备类型：</span>
						<u:TableSelect  sname="pos_type" style="width: 128px; height: 24px; vertical-align: top;" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll" />
						<%--<select style="padding:2px;width:140px" name="pos_type">
			         		<option value="-1" <c:out value="${params['pos_type'] eq '-1'?'selected':'' }"/>>--全部--</option>
			         		<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>
			         		<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>
			         		<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>
			         		<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>
			         		<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>
			         	</select>
					--%></li>
					<li>
						<span style="width: 80px;">状      态：</span>
						<select id="check_status"  name="check_status"  style="width:140px;padding:2px">
							<option value="-1" <c:out value="${params['check_status'] eq '-1'?'selected':'' }"/>>--全部--</option>
							<option value="1" <c:out value="${params['check_status'] eq '1'?'selected':'' }"/>>开启</option>
							<option value="2" <c:out value="${params['check_status'] eq '21'?'selected':'' }"/>>关闭</option>
						</select>
					 </li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="query" value=" 查 询 " />
					<input class="button blue medium" type="button" id="add"
						onclick="javascript:window.location.href='${ctx}/merCP/merchantCheckAdd'" value=" 增 加 " />
			</div>
		</form:form>
		<div class="tbdata">
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="30">序号</th>
						<%--  <th width="80">收单机构代码</th> --%>
						<th width="80">审核人</th>
						<th width="80">审核类型</th>
						<th width="200">代理商</th>
						<th width="60">设备类型</th>
						<th width="60">状态</th>
						<th width="80">操作</th>
						<c:forEach items="${list.content}" var="item" varStatus="status">
							<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
								<td class="center"><span class="center">${status.count}</span></td>
								<td class="center">${item.real_name}</td>
								<td class="center"><span class="center">
									<c:if test="${item.check_type==1}">
											顺序审核
									</c:if>
									<c:if test="${item.check_type==2}">
											设备审核
									</c:if>
									<c:if test="${item.check_type==3}">
											代理商审核
									</c:if>
									<c:if test="${item.check_type==4}">
											代理设备审核
									</c:if>
								</span></td>
								<td class="center"><u:AgentNameForNo agentNo="${item.agent_no}"/></td>
								<td class="center">
								<u:postype svalue="${item.pos_type}" />
								<%--<c:if test="${item.pos_type==1}">
											移联商宝
										</c:if>
										<c:if test="${item.pos_type==2}">
											传统POS
										</c:if>
										<c:if test="${item.pos_type==3}">
											移小宝
										</c:if>
										<c:if test="${item.pos_type==4}">
											移联商通
										</c:if>
										<c:if test="${item.pos_type==5}">
											超级刷
										</c:if>
								--%></td>
								<td class="center">
									<c:if test="${item.check_status==1}">
											启用
										</c:if>
										<c:if test="${item.check_status==2}">
											关闭
										</c:if>
								</td>
								<td align="center">
										<a  href="${ctx}/merCP/merchantCheckModify?id=${item.id}" >修改 |</a>
										<shiro:hasPermission name="MERCHANT_CHECK_PERSON_REMOVE">
										<a  href="javascript:void(0)"  onclick="removeCMP(${item.id})">删除 |</a>
										</shiro:hasPermission>
										<c:if test="${item.check_status==1}">
											<a  href="javascript:void(0)"  onclick="closeCMP(${item.id})">关闭</a>
										</c:if>
										<c:if test="${item.check_status==2}">
											<a  href="javascript:void(0)"  onclick="openCMP(${item.id})">开启</a>
										</c:if>
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
