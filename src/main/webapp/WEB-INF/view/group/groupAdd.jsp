<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
	<style type="text/css">
		#terminalAdd
		{
			padding:10px;
		}
	
		#terminalAdd ul
		{
			overflow:hidden;
		} 
		
		#terminalAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:270px;
			heigth:32px;
			line-height:32px;
		}
		
		#terminalAdd ul li.column2
		{
			width:540px;
		}
		
		#terminalAdd ul li.column3
		{
			width:810px;
		}
		
		#terminalAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:90px;
		}
		#terminalAdd ul li label.must		
		{
			display:-moz-inline-box;
			display:inline-block;
			width:5px;
			text-align:center;
			color:red;
		}
		#terminalAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#terminalAdd ul li.long
		{
			width:440px;
		}
		
		
		#terminalAdd div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
		
		.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
	<script type="text/javascript">
	$(function(){
		var INIT_OPTION = "--请选择--";
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
		$.each(provinceName, function(i, n) {
			$("<option></option>").val(n).text(n).appendTo("#province");
		});
        var cus = 0;
	    var classname = "";
	    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:358px;margin-left:33px;margin-top:18px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
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
	function addTerminal(){	
		var dialog;
		//var sn = $.trim($("#group_code").val()); //route_type
		
		var acq_enname = $.trim($("#acq_enname").val()); //集群所属收单机构
		var acq_name = $("#acq_enname").find("option:selected").text(); //集群所属收单机构
		var agent_no =  $("[name=agentNo]").val(); //集群所属所属代理商
		var agent_Name =  $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
		var accounts_period =  $("#accounts_period").val(); //集群结算周期
		var sales = $("#sales").val(); //集群所属销售
		var sales_name = $("#sales").find("option:selected").text(); //集群所属销售名称
		var route_type = $("#route_type").val(); //集群所属类型
		var merchant_type = $("#merchant_type").val(); //集群所属商户类型
		var merchant_typeName = $("#merchant_type").find("option:selected").text(); //集群所属商户类型名称
		var province = $("#province").val();
		var my_settle = $("#my_settle").val();
		var bag_settle = $("#bag_settle").val();
		var provinceText = $("#province").find("option:selected").text(); 
		var route_last_name = $.trim($("#route_last_name").val()); //集群后缀
		var route_describe = $.trim($("#route_describe").val()); //商户描述
		
		if(acq_enname == ""){
			dialog = $.dialog({title: '错误',lock:true,content: '请选择所属通道名称!',icon: 'error.gif',ok: function(){
				}
			});
			return false;
		}
		
		if(my_settle == "-1"){
			dialog = $.dialog({title: '错误',lock:true,content: '请选择是否优质属性!',icon: 'error.gif',ok: function(){
				}
			});
			return false;
		}
		
		if(bag_settle == "-1"){
			dialog = $.dialog({title: '错误',lock:true,content: '请选择是否钱包结算!',icon: 'error.gif',ok: function(){
				}
			});
			return false;
		}
		
		/*if(agent_no == ""){
			dialog = $.dialog({title: '错误',lock:true,content: '请选择所属代理商!',icon: 'error.gif',ok: function(){
			}
		});
			return false;
		}*/
		
		if(route_type == 1){
			if(sales == "" || sales == 0){
				dialog = $.dialog({title: '错误',lock:true,content: '请选择所属销售!',icon: 'error.gif',ok: function(){
				}
			});
				return false;
			}
		}
		
		if(merchant_type == ""){
			dialog = $.dialog({title: '错误',lock:true,content: '请选择商户类型!',icon: 'error.gif',ok: function(){
				}
			});
			return false;
		}
		if(route_last_name !=""){ //集群后缀超过长度限制
		
			if(route_last_name.length > 10){
				dialog = $.dialog({title: '错误',lock:true,content: '集群后缀超过长度限制0~10字符',icon: 'error.gif',ok: function(){
		        	$("#route_last_name").focus();
				    }
			});
				return false;
			}
		}
		
		if(route_describe != ""){
			if(route_describe > 50){
				dialog = $.dialog({title: '错误',lock:true,content: '集群集群描述超过长度限制0~50字符',icon: 'error.gif',ok: function(){
		        	$("#route_describe").focus();
				    }
			});
				return false;
			}
		}
	
		
		
			$.dialog.confirm('确定新增该条记录吗？？', function(){
				$.ajax({
					   type: "POST",
					   url: "${ctx}/group/addSubmit",
					   data: {"acq_enname":acq_enname,"acq_name":acq_name,"agent_no":agent_no,"agent_Name":agent_Name,"accounts_period":accounts_period,
						   "sales":sales,"sales_name":sales_name,"route_type":route_type,"merchant_type":merchant_type,"merchant_typeName":merchant_typeName,
						   "route_last_name":route_last_name,"route_describe":route_describe,"province":province,"my_settle":my_settle,"bag_settle":bag_settle},
					   success: function(data){
						   var ret = data.msg;
							if (ret == "nameExist"){
						  		successMsg("集群名称已存在",'error.gif');
						  		return false;
					        }else if(ret == "codeExist"){
					        	successMsg("集群编号已存在",'error.gif');
						  		return false;
					        }else if(ret == "OK"){
					        	successMsg("录入成功",'success.gif');
						  		return false;
					        }else if(ret == "ERROR"){
					        	successMsg("录入失败，请检查数据！",'error.gif');
					        	return false;
					        }
					   }		
					});
			});
	} 
	
	function successMsg(contentMsg,icons){
		var dialog;
		if(contentMsg=="集群名称已存在" || contentMsg=="集群编号已存在" || contentMsg=="录入失败，请检查数据！"){
			 dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: icons,ok:null ,close:function(){
				$("#group_name").focus();
			}
			});
		}else{
			 dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: icons,ok:null ,close:function(){
				clearTerminal();
				location.href="${ctx}/group/query";
			}});
		}
		
	}	
		

	
	function clearTerminal(){
 		$('#group_code').val('');
        $('#group_name').val('');
		$('#group_code').focus();
	}
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>路由集群新增</div>
    
    <form:form id="terminalAdd" action="${ctx}/group/addSubmit" method="post">
    	<div class="item">
    	<div class="title">路由集群新增</div>
    	<ul>
			<li><label style="width:72px">收单机构： </label>
				<select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
						<option value="">全部</option>
						<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>	
						</c:forEach>
					</select>
					 <label class="must">*</label></li>
			<li>
				<label style="width:80px">所属代理商： </label>
				<u:select style="width: 180px;height: 24px"   value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" />
				<div style="position:absolute;margin-left:55px;margin-top:-25px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 158px;border:none;">
         	</div>
			</li>
    	</ul>
    	<ul>
			<li><label style="width:60px">结算周期：</label>T+<select style="width: 140px;height: 24px" id="accounts_period"  name="accounts_period"><option value="1">1</option><option value="0">0</option></select> <label class="must">*</label></li>
			<li><label style="width:80px">属性：</label>
				<select  style="width: 180px;height: 24px" id="route_type"  name="route_type">
					<option value="1">销售专用</option>
					<option value="2">公司自建</option>
					<option value="3">技术测试</option>
					</select> <label class="must">*</label></li>
    	</ul>
    	<ul>
			<li><label style="width:72px">所属销售：</label>
				<select id="sales" name="sales"  style="width: 140px;height: 24px">
						<option value="">全部</option>
						<c:forEach items="${salesList}" var="sales">
						<option value="${sales.id}" <c:if test="${sales.real_name eq params['real_name']}">selected = "selected"</c:if>>${sales.real_name}</option>	
						</c:forEach>
					</select>
			</li>
			<li><label style="width:80px">商户类型：</label>
					<select id="merchant_type" name="merchant_type"   style="width: 180px;height: 24px">
							<option value="">--请选择--</option>
								<option value="5812" <c:if test="${params.merchant_type eq '5812' }">selected="selected"</c:if>>餐娱类</option>
								<option value="5111" <c:if test="${params.merchant_type eq '5111' }">selected="selected"</c:if>>批发类</option>
								<option value="5541" <c:if test="${params.merchant_type eq '5541' }">selected="selected"</c:if>>民生类</option>
								<option value="5331" <c:if test="${params.merchant_type eq '5331' }">selected="selected"</c:if>>一般类</option>
								<option value="1520" <c:if test="${params.merchant_type eq '1520' }">selected="selected"</c:if>>房车类</option>
								<option value="1011" <c:if test="${params.merchant_type eq '1011' }">selected="selected"</c:if>>其他</option>
						</select>
				<label class="must">*</label></li>
    	</ul>
    	<ul>
			<li style="width: 270px"><label style="width:75px">所属省份：</label><select id="province" name="province"   style="width: 140px;height: 24px" ></select> </li>
			<li style="width: 270px"><label style="width:81px">是否优质：</label>
			<select id="my_settle" name="my_settle"   style="width: 180px;height: 24px" >
				<option value="0" <c:if test="${params.my_settle eq '0' }">selected="selected"</c:if>>否</option>
				<option value="1" <c:if test="${params.my_settle eq '1' }">selected="selected"</c:if>>是</option>
			</select><label class="must">*</label> </li>
    	</ul>
    	<ul>
    		<li style="width: 270px"><label style="width:72px">是钱包结算：</label>
			<select id="bag_settle" name="bag_settle"   style="width: 140px;height: 24px" >
				<option value="0" <c:if test="${params.bag_settle eq '0' }">selected="selected"</c:if>>否</option>
				<option value="1" <c:if test="${params.bag_settle eq '1' }">selected="selected"</c:if>>是</option>
			</select><label class="must">*</label> </li>
			<li style="width: 370px"><label style="width:75px">集群后缀：</label><input  type="text" id="route_last_name"   name="route_last_name"  value="${params['group_name']}" maxlength="10"/><font color="red"> (选填,0~10个字符)</font></li>
    	</ul>
    	<ul>
			<li style="height: 200px"><label style="width:80px;">集群描述：</label><textarea rows="5" cols="60"  id="route_describe"  name="route_describe" maxlength="150"></textarea><font color="red"> (选填,0~150个字符)</font> </li>
    	</ul>
    	<ul>
			<li id="success" style="color:blue"></li>
    	</ul>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"  value="保存" onclick="javascript:addTerminal();"/>
    		<input   class="button blue "  type="button"  onclick="window.location.href='${ctx}/group/query'" value="返回"/>
    	</div>
    </form:form>
  </div>
</body>
