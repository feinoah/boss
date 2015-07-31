<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>

<head>
    <title>商户批量操作</title>
    <%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
	<script type="text/javascript">
			uploadFileExcel("file_upload","excelFileName",false,"no","100","200");
			uploadFileExcel("CancelHLFB","CancelHLF",false,"no","100","200");
			uploadFileExcel("TransferAgentB","TransferAgent",false,"no","100","200");
			uploadFileExcel("groupFileNameB","groupFileName",false,"no","100","200");
			$(function(){
				/* 商户批量修改 */
				$("#addButton").on("click", function(){
					var merchant_type = $("#merchant_type").val();
					var real_flag = $("#real_flag").val();
					var open_status = $("#open_status").val();
					if(merchant_type == "-1" && real_flag == "-1" && open_status == "-1"){
						alert("请选择需要批量修改的内容,至少选择一项!");
						return false;
					}
					var excelFileName = $.trim($("#excelFileName").val());
					if(excelFileName == ""){
						alert("请选择需要上传的文件!");
						return false;
					}
					var lastIndex = excelFileName.lastIndexOf(".");
					if(lastIndex < 1){
						alert("无效的文件");
						return false;
					}
					if(excelFileName.substring(lastIndex+1) != "xls"){
						alert("请选择文件后缀名为.xls文件！");
						return false;
					}
					$.dialog.confirm('批量操作风险较大,请确认是否导入该文件批量操作？', function(){
						 	 $('#batchMerchant').ajaxSubmit( {
								   	beforeSubmit : function(){
						  				pop_waiting_info("请稍候...");
								    },
								    dataType: "json",
								    type: "POST",
								    iframe:false,
								 	success: function(data){
									  	pop_waiting_close();
								    	var ret = data.code;
								    	var merchant_no = data.merchant_no;
									  	if (ret == "2000"){
									  		alert("批量修改操作成功！");
									  	}else if(ret == "1002"){
									  		$.dialog({title: "提示",lock:true,content: "以下商户操作失败，请手动修改！商户编号："+merchant_no,icon: "alert.gif",ok:null });
									  	}else if(ret == "1003"){
									  		$.dialog({title: "错误",lock:true,content: "请选择需要修改的内容！",icon: 'error.gif',ok:null });
									  	}else if(ret == "1004"){
									  		$.dialog({title: "错误",lock:true,content: "批量修改内容过多，请分批操作，单次最大操作数1000以内！",icon: 'error.gif',ok:null });
									  	}else if(ret == "1005"){
									  		$.dialog({title: "错误",lock:true,content: "系统未发现需要批量处理的商户！",icon: 'error.gif',ok:null });
									  	}else{
									  		$.dialog({title: "错误",lock:true,content: "批量修改商户操作失败！",icon: 'error.gif',ok:null });
									  	}
								  	}, error:function(obj,status,errorInfo){
									  var dialog = $.dialog({title: "错误",lock:true,content: "通讯故障,请退出重新登录后再次尝试！",icon: 'error.gif',ok:null });
								  }
						 		}
						 	);
					});
				});
				
				<shiro:hasPermission name="MERCHANT_CANCEL_HLF">
				/* 取消同步好乐付 */
				$("#addCancelHLFButton").on("click", function(){
					var cancelhlf = $("#CancelHLF").val();
					if(cancelhlf == ""){
						alert("请选择需要上传的文件!");
						return false;
					}
					var lastIndex = cancelhlf.lastIndexOf(".");
					if(lastIndex < 1){
						alert("无效的文件");
						return false;
					}
					if(cancelhlf.substring(lastIndex+1) != "xls"){
						alert("请选择文件后缀名为.xls文件！");
						return false;
					}
					$.dialog.confirm('批量操作风险较大,请确认是否导入该文件批量操作？', function(){
						$('#batchCancelHLF').ajaxSubmit( {
						   	beforeSubmit : function(){
				  				pop_waiting_info("请稍候...");
						    },
						    dataType: "json",
						    type: "POST",
						    iframe:false,
						 	success: function(data){
							  	pop_waiting_close();
						    	var ret = data.code;
						    	var merchant_no = data.merchant_no;
							  	if (ret == "2000"){
							  		alert("批量修改操作成功！");
							  	}else if(ret == "1002"){
							  		$.dialog({title: "提示",lock:true,content: "以下商户操作失败，请确认商户是否存在，如已存在请重试！商户编号："+merchant_no,icon: "alert.gif",ok:null });
							  	}else if(ret == "1004"){
							  		$.dialog({title: "错误",lock:true,content: "批量修改内容过多，请分批操作，单次最大操作数1000以内！",icon: 'error.gif',ok:null });
							  	}else if(ret == "1005"){
							  		$.dialog({title: "错误",lock:true,content: "系统未发现需要批量处理的商户！",icon: 'error.gif',ok:null });
							  	}else{
							  		$.dialog({title: "错误",lock:true,content: "批量修改商户操作失败！",icon: 'error.gif',ok:null });
							  	}
						  	}, error:function(obj,status,errorInfo){
							  var dialog = $.dialog({title: "错误",lock:true,content: "通讯故障,请退出重新登录后再次尝试！",icon: 'error.gif',ok:null });
						  }
				 		}
				 	);
					});
				});
				</shiro:hasPermission>
				
				<shiro:hasPermission name="MERCHANT_TRANSFER_AGENT">
				/* 后期维护人员请注意：商户批量转移代理商，需要遵循当月修改次月生效的规则，避免引起分润错乱 */
				$("#agentNo").on("change", function(){
					var agent2 = $("#agentNo").find("option:selected").text();
					//alert(agent2);
					var agent = $("#agentNo").val();
					if(agent != "" && agent != "-1"){
						$("#agentInput").val(agent2);
						$autocomplete.hide();
						$("#agentInput").val("");
						$("#agentInput").val(agent2);
						$.ajax({
							url : "${ctx}/agent/agentSelect",
							cache : false,
							data : {"agent_no" : agent},
							type : "Get",
							dataType : "json",
							timeout : 20000,
							error : function() {
								alert("加载直属代理商信息时出错,请刷新当前界面重试！");
							},
							success : function(json) {
								$("#belong_to_agent").find("option").remove();
								var len = json.length;
								if (len != 0) {
									for (var i = 0; i < len; i++) {
										/*if(json[i].agent_no != agent){
											$(	"<option value="+json[i].agent_no+">"+ json[i].agent_name + "</option>").appendTo(	"#belong_to_agent");
										}*/
										$(	"<option value="+json[i].agent_no+">"+ json[i].agent_name + "</option>").appendTo(	"#belong_to_agent");
									}
								}
							}
						});
					}else{
						alert("拉取代理商编号信息错误，请刷新界面重试!");
					}
				});
				
				var agentT = $("[name=agentNo]").find("option:selected").text();
				$("#agentInput").val(agentT);
				        var cus = 0;
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
					                        $("<li title=" + arry[k] + "  style='background-color: #FFFFFF'></li>").text(arry[k]).appendTo($autocomplete).mouseover(function() {
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
					                             $("#agentNo").trigger('change');
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
					                	   $("#agentNo").trigger('change');
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
					                    	$("#agentNo").trigger('change');
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
					                    	$("#agentNo").trigger('change');
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
					                 	   $("#agentNo").trigger('change');
					                    }
					                 });
					            }
					        }
					        if (event.keyCode == 13) {
					            $(".autocomplete li").removeClass();
								$autocomplete.hide();
					        }
					    });
					    
						$("#addTransferAgent").on("click", function(){
						
							if(!$('#agentNo').val() || $('#agentNo').val() === '-1'){
								$.dialog({title: "提示",lock:true,content: "请选择您要转移的代理商！",icon: "alert.gif",ok:null });
								return false;
							}
								
							if(!$('#belong_to_agent').val() || $('#belong_to_agent').val() === '-1'){
								$.dialog({title: "提示",lock:true,content: "请选择您的所属代理商！",icon: "alert.gif",ok:null });
								return false;
							}
								
							if(!/^[\u4e00-\u9fa5]+$/gi.test($('#sale_name').val())){
								$.dialog({title: "提示",lock:true,content: "请填写所属销售并且只能为中文！",icon: "alert.gif",ok:null });
								return false;
							}
								
							if(!/^\w+\.xls$/gi.test($("#TransferAgent").val())){
								$.dialog({title: "提示",lock:true,content: "请选择需要上传的文件!",icon: "alert.gif",ok:null });
								return false;
							}
								
							$.dialog.confirm('批量操作风险较大,请确认是否导入该文件批量操作？', function(){
						    	$('#batchTransferAgent').ajaxSubmit({
								   	beforeSubmit : function(){pop_waiting_info("请稍候...");},
								    dataType: "json",
								    type: "POST",
								    iframe:false,
								 	success: function(data){
									  	pop_waiting_close();
								    	var ret = data.code;
								    	var merchant_no = data.merchant_no;
									  	if (ret == "2000"){
									  		$.dialog({title: "提示",lock:true,content: "批量修改操作成功！",icon: "success.gif",ok:null });
									  	}else if(ret == "1001"){
									  		$.dialog({title: "提示",lock:true,content: "代理商和所属代理商非从属关系",icon: "alert.gif",ok:null });
									  	}else if(ret == "1002"){
									  		$.dialog({title: "提示",lock:true,content: "以下商户操作失败，请确认商户是否存在，如已存在请重试！商户编号："+merchant_no,icon: "alert.gif",ok:null });
									  	}else if(ret == "1004"){
									  		$.dialog({title: "错误",lock:true,content: "批量修改内容过多，请分批操作，单次最大操作数1000以内！",icon: 'error.gif',ok:null });
									  	}else if(ret == "1005"){
									  		$.dialog({title: "错误",lock:true,content: "系统未发现需要批量处理的商户！",icon: 'error.gif',ok:null });
									  	}else{
									  		$.dialog({title: "错误",lock:true,content: "批量修改商户操作失败！",icon: 'error.gif',ok:null });
									  	}
								  	},
								  	error:function(obj,status,errorInfo){
									  var dialog = $.dialog({title: "错误",lock:true,content: "通讯故障,请退出重新登录后再次尝试！",icon: 'error.gif',ok:null });
								  	}
						 		});
						 	});
						});
					    </shiro:hasPermission>
					    
					    /*----------------------------- 集群批量转移-------------------------------------- */
					    <shiro:hasPermission name="MERCHANT_GROUP_BATCH_TRANSFER">
					    //获取集群详细信息
					    $("#groupName").on("change",function(){
					    	var groupName = $("#groupName").val();
					    	$("#groupInfo").find("li").remove(); 
					    	if(groupName != "-1"){
					    		$.ajax({
									url : "${ctx}/sc/getGroupInfo",
									cache : false,
									data : {"gid" : groupName},
									type : "post",
									dataType : "json",
									timeout : 20000,
									error : function() {
										alert("加载直属代理商信息时出错,请刷新当前界面重试！");
									},
									success : function(json) {
										if(json != null && json.group_code !=null && json.my_settle != null && json.bag_settle != null){
											$("#batchMerchantGroupButton").attr("class","button blue");
											$("#batchMerchantGroupButton").attr("disabled",false);
											$(	"<li><br/></li>").appendTo(	"#groupInfo");
											$(	"<li style='width: 660px;background-color: #808080'><font color='#FFFFFF' size='2'>集群名称："+json.group_name+"</font></li>").appendTo(	"#groupInfo");
											$(	"<li style='width: 150px;background-color: #808080'><font color='#FFFFFF' size='2'>集群编号："+json.group_code+"</font></li>").appendTo(	"#groupInfo");
											var mySettle = "";
											var bagSettle = "";
											if(json.my_settle == "0"){
												mySettle = "否";
											}else if(json.my_settle == "1"){
												mySettle = "是";
											}else{
												mySettle = json.my_settle;
											}
											if(json.bag_settle == "0"){
												bagSettle = "否";
											}else if(json.bag_settle == "1"){
												bagSettle = "是";
											}else{
												bagSettle = json.bag_settle;
											}
											$(	"<li style='width: 150px;background-color: #808080'><font color='#FFFFFF' size='2'>是否优质商户："+mySettle+"</font></li>").appendTo(	"#groupInfo");
											$(	"<li style='width: 150px;background-color: #808080;font-weight:normal'><font color='#FFFFFF' size='2'>是否钱包结算："+bagSettle+"</font></li>").appendTo("#groupInfo");
										}else{
											$("#batchMerchantGroupButton").attr("class","");
											$("#batchMerchantGroupButton").attr("disabled",true);
											alert("所选集群存在异常或已被删除，请选择其他集群！");
										}
									}
								});
					    	}
					    });
					    
					    /*------------------------- 提交批量转移集群信息------------------------------*/
					    $("#batchMerchantGroupButton").on("click", function(){
					    	var groupName = $("#groupName").val();
					    	var groupFileName = $("#groupFileName").val();
							var operType = $('#operType').val();

							if(!operType || operType === '-1'){
								alert('请选择操作类型!');
								return false;
							}

							if(groupName == -1){
					    		alert("请选择需要转入的集群信息！");
					    		return false;
					    	}
					    	
					    	if(groupFileName == ""){
					    		alert("请选择需要批量转移的Excel文件！");
					    		return false;
					    	}
					    	
					    	var lastIndex = groupFileName.lastIndexOf(".");
							if(lastIndex < 1){
								alert("无效的文件");
								return false;
							}
							
							if(groupFileName.substring(lastIndex+1) != "xls"){
								alert("请选择文件后缀名为.xls文件！");
								return false;
							}

					    	$.dialog.confirm('批量转移集群风险较大,请确认是否导入该文件进行批量转移操作？', function(){
						    	$('#batchMerchantGroup').ajaxSubmit({
								   	beforeSubmit : function(){pop_waiting_info("请稍候，正在批量转移...");},
								    dataType: "json",
								    type: "POST",
								    iframe:false,
								 	success: function(data){
									  	pop_waiting_close();
								    	var ret = data.code;
								    	var merchant_no = data.merchant_no;
									  	if (ret == "2000"){
									  		$.dialog({title: "提示",lock:true,content: "批量转移操作成功！",icon: "success.gif",ok:null });
									  	}else if(ret == "1002"){
									  		$.dialog({title: "提示",lock:true,content: "请确认商户是否存在或已在集群中，如已存在请重试！以下商户编号操作失败："+merchant_no,icon: "alert.gif",ok:null });
									  	}else if(ret == "1004"){
									  		$.dialog({title: "错误",lock:true,content: "批量转移内容过多，请分批操作，单次最大操作数5000以内！",icon: 'error.gif',ok:null });
									  	}else if(ret == "1005"){
									  		$.dialog({title: "错误",lock:true,content: "系统未发现需要批量转移的商户！",icon: 'error.gif',ok:null });
									  	}else if(ret == "1006"){
									  		$.dialog({title: "错误",lock:true,content: "批量转移操作失败，所选集群不存在或已被删除！",icon: 'error.gif',ok:null });
									  	}else{
									  		$.dialog({title: "错误",lock:true,content: "批量转移集群操作失败！",icon: 'error.gif',ok:null });
									  	}
								  	},
								  	error:function(obj,status,errorInfo){
								  		pop_waiting_close();
										var dialog = $.dialog({title: "错误",lock:true,content: "通讯故障,请退出重新登录后再次尝试！",icon: 'error.gif',ok:null });
								  	}
						 		});
						 	});
					    	
					    });
					    </shiro:hasPermission>
			});
	</script>
  </head>
  
  <body>
    <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理&gt;商户批量操作</div>
    <form:form id="batchMerchant" action="${ctx}/sc/batchMerchantUpdate" method="post">
    	<div class="item">
	    	<div class="title">修改商户信息</div>
	    	<ul>
				<li style="width: 185px"><label>商户类型：</label>
					<select id="merchant_type" name="merchant_type" style="width: 104px;height: 24px">
							<option value="-1">--请选择--</option>
							<option value="5812">餐娱类</option>
							<option value="5111">批发类</option>
							<option value="5541">民生类</option>
							<option value="5331">一般类</option>
							<option value="1520">房车类</option>
							<option value="1011" >其他</option>
					</select>
				</li>
				<li style="width: 185px"><label>是否实名：</label>
					<select name="real_flag" id="real_flag" class="required" style="width: 104px;height: 24px">
							<option value="-1">--请选择--</option>
							<option value="0">否</option>
							<option value="1" >是</option>
					</select>
				</li>
				<li style="width: 185px"><label>商户状态：</label>
					<select name="open_status" id="open_status" class="required" style="width: 104px;height: 24px">
							<option value="-1">--请选择--</option>
							<option value="0">关闭</option>
							<option value="4" >冻结</option>
					</select>
				</li>
				<li style="width: 400px;"><label style="vertical-align:top;line-height:25px;">导入文件：</label><input id="excelFileName" name="excelFileName" type="text" readOnly="readOnly" style="vertical-align:top;"/><label class="must" style="vertical-align:top;line-height:25px;">*</label><input id="file_upload" name="file_upload" type="file" /></li>
				<li style="width: 185px"><input   class="button blue  " type="button" id="addButton"  value="批量修改" /></li>
			</ul>
			<ul><li><font color="red">说明：为降低大批量误操作现象，单次批量修改商户数为1000，超过限制请分批进行！批量修改商户操作风险较大，请谨慎操作！</font></li></ul>
		</div>
    </form:form>
    <shiro:hasPermission name="MERCHANT_CANCEL_HLF">
    <br/>
    <br/>
    <br/>
    <br/>
    <form:form id="batchCancelHLF" action="${ctx}/sc/batchCancelHLF" method="post">
    	<div class="item">
	    	<div class="title">取消同步好乐付商户</div>
	    	<ul>
				<li style="width: 400px"><label style="vertical-align:top;line-height:25px;">导入文件：</label><input id="CancelHLF" name="CancelHLF" type="text"  readOnly="readOnly"  style="vertical-align:top;"/><label class="must" style="vertical-align:top;line-height:25px;">*</label><input id="CancelHLFB" name="CancelHLFB" type="file" /></li>
				<li style="width: 185px"><input   class="button blue  " type="button" id="addCancelHLFButton"  value="批量取消" /></li>
			</ul>
		</div>
    </form:form>
    </shiro:hasPermission>
    <shiro:hasPermission name="MERCHANT_TRANSFER_AGENT">
	    <br/>
	    <br/>
	    <br/>
	    <br/>
	    <div style="position:absolute;margin-left:92px;margin-top:48px;">
	         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:12px;width: 235px !important;border: none;">
	         	</div>
	    <form:form id="batchTransferAgent" action="${ctx}/sc/batchTransferAgent" method="post">
	    	<div class="item">
		    	<div class="title">转移商户所属代理商</div>
		    	<ul>
					<li style="width: 385px"><label>一级代理商名称：</label>
						<u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"    id="agentNo"  onlyThowParentAgent="true"  style="width: 260px;height: 24px"/>
					</li>
					<li style="width: 385px"><label>所属代理商：</label>
						<select id="belong_to_agent" name="belong_to_agent" style="width: 260px;height: 24px">
							<option value="-1">所属代理商</option>
						</select>
					</li>
					<li style="width: 300px"><label>所属销售：</label><input type="text" name="sale_name" id="sale_name"/></li>
					<li style="width: 500px"><label style="vertical-align:top;line-height:25px;">导入文件：</label><input id="TransferAgent" name="TransferAgent" type="text" readOnly="readOnly" style="vertical-align:top;"/><label class="must" style="vertical-align:top;line-height:25px;">*</label><input id="TransferAgentB" name="TransferAgentB" type="file" /></li>
					<li><div class="clear"></div></li>
					<li style="width: 385px"><input   class="button blue  " type="button" id="addTransferAgent"  value="转移" /></li>
				</ul>
				<div class="clear"></div>
				<ul><li><font color="red">说明：凡涉及修改商户所属一级代理商，操作完成后系统将在次月生效，请勿重复操作！如,仅修改所属二级代理商，则即时生效！</font></li></ul>
			</div>
	    </form:form>
    </shiro:hasPermission>
    <shiro:hasPermission name="MERCHANT_GROUP_BATCH_TRANSFER">
	    <br/>
	    <br/>
	    <br/>
	    <br/>
	    <form:form id="batchMerchantGroup" action="${ctx}/sc/batchMerchantGroup" method="post">
	    	<div class="item">
		    	<div class="title">集群普通商户批量转移</div>
		    	<ul id="groupInfo" style="background-color: red"></ul>
		    	<div class="clear"></div>
		    	<div class="clear"></div>
		    	<ul>
					<li style="width: 165px"><label>操作类型：</label>
						<select style="height: 24px;" name="operType" id="operType">
							<option value="-1">---请选择---</option>
							<option value="0">批量转移</option>
							<option value="1">批量添加</option>
						</select>
					</li>

					<li style="width: 525px"><label>集群名称：</label>
						<select id="groupName" name="groupName" style="width: 404px;height: 24px">
							<option value="-1">--请选择--</option>
							<c:forEach items="${groupList}" var="item" varStatus="status">
						          <option value="${item.id}">${item.group_name}</option>
						       </c:forEach>
						</select>
					</li>
					<li style="width: 400px"><label style="vertical-align:top;line-height:25px;">导入文件：</label><input id="groupFileName" name="groupFileName" type="text" readOnly="readOnly"  style="vertical-align:top;"/><label class="must" style="vertical-align:top;line-height:25px;">*</label><input id="groupFileNameB" name="groupFileNameB" type="file" /></li>
					<li style="width: 185px"><input   class="button blue  " type="button" id="batchMerchantGroupButton"  value="批量转移集群" /></li>
				</ul>
				<div class="clear"></div>
				<ul>
					<li><font color="red" size="2">说明：批量转移集群仅针对已存在集群中的商户，且单次批量操作最大数为5000，超过数量限制请分批操作，转移后的商户系统将自动修改其是否优质、是否钱包结算属性将与目标集群保持一致！</font></li>
				</ul>
			</div>
	    </form:form>
    </shiro:hasPermission>
    </div>
  </body>
</html>
