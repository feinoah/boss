<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadJs.jsp"%>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
#file_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

.autocomplete {
	list-style-type: none;
	margin: 0px;
	padding: 0px;
	border: #008080 1px solid
}

.autocomplete li {
	font-size: 12px;
	font-family: "Lucida Console", Monaco, monospace;
	font-weight: bold;
	cursor: pointer;
	height: 18px;
	line-height: 18px
}

.hovers {
	background-color: #3368c4;
	color: fff
}
</style>
<script type="text/javascript">
	uploadFileExcel("file_upload", "excelFileName", false, "no", "100", "200");
	$(function() {
		var cus = 0;
		var classname = "";
		var $autocomplete = $(
				"<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>")
				.hide().insertAfter("#agentInput");
		$("#agentInput")
				.keyup(
						function(event) {
							var arry = new Array();
							$("[name=agentNo]").find("option").each(
									function(i, n) {arry[i] = $(this).text();
									});
							if ((event.keyCode != 38) && (event.keyCode != 40)
									&& (event.keyCode != 13)) {
								$autocomplete.empty();
								var $SerTxt = $("#agentInput").val()
										.toLowerCase();
								if ($SerTxt != "" && $SerTxt != null) {
									for ( var k = 0; k < arry.length; k++) {
										if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
											$(	"<li title=" + arry[k] + " class=" + classname +" style='background-color: #FFFFFF'></li>").text(arry[k]).appendTo($autocomplete).mouseover(
															function() {$(".autocomplete li").removeClass("hovers");
																$(this).css(	{background : "#3368c4",color : "#fff"});
															}).mouseout(
															function() {$(this).css({background : "#fff",color : "#000"});
															}).click(
															function() {
																var text = $(this).text();
																$("#agentInput").val(	text);
																$(	"[name=agentNo]").find("option").each(
																				function(i,n) {
																					if ($(this).text() == text) {
																						$(this).prop('selected','true');
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
									$(".autocomplete li").eq(cus).addClass(
											"hovers");
									var text = $(".autocomplete li").eq(cus)
											.text();
									$("#agentInput").val(text);
									$("[name=agentNo]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected','true');
												}
											});
								} else {
									cus--;
									$(".autocomplete li").removeClass();
									$(".autocomplete li").eq(cus).addClass("hovers");
									var text = $(".autocomplete li").eq(cus).text();
									$("#agentInput").val(text);
									$("[name=agentNo]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected','true');
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
									$("[name=agentNo]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected','true');
												}
											});
								} else {
									cus = 0;
									$(".autocomplete li").removeClass();
									$(".autocomplete li").eq(cus).addClass("hovers");
									var text = $(".autocomplete li").eq(cus).text();
									$("#agentInput").val(text);
									$("[name=agentNo]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected','true');
												}
											});
								}
							}
							if (event.keyCode == 13) {
								$(".autocomplete li").removeClass();
								$autocomplete.hide();
							}
						});

		$("[name=agentNo]").change(
				function() {
					var agent_Name = $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
					if (agent_Name != "") {
						$("#agentInput").val(agent_Name);
						$autocomplete.hide();
					}
				});
		
		var defPosType = "${params.pos_type}";
		if(defPosType == ""){
			defPosType = "-1";
		}
		/* 默认加载设备型号信息 */
		searchPosModel(defPosType, "-1");
		
	/* 基于设备类型加载所属型号 */
	$("#pos_type").change(function(){
		var pos_type = $("#pos_type").val();
		if(pos_type == "00"){
			alert("该设备不提供子机型选项,请重新选择设备，如无目标设备类型，请在设备管理中添加新设备!");
			$("#pos_type option[value=-1]").attr("selected", true); 
			searchPosModel("-1","-1");
		}else{
			searchPosModel(pos_type,"-1");
		}
	});
	
	/* 传入pos_type 加载  对应的型号列表至SELECT元素,传入defSelectModel 自动选中默认选项*/
	function searchPosModel(pos_type, defSelectModel){
		$("#model").empty();
		$("<option value='-1'>--全部--</option>").appendTo("#model");
		if(pos_type != "-1"){
				$.ajax({
					type:"post",
					url:"${ctx}/ter/searchPosModel",
					data:{"pos_type":pos_type},
					async:false,
					dataType: 'json',
				  	success: function(json){
					  for(var i=0;i<json.length;i++){
						  if(defSelectModel == json[i].pos_model && defSelectModel != "-1"){
							  $("<option value="+json[i].pos_model+" selected>"+json[i].pos_model_name+"</option>").appendTo("#model");
						  }else{
							  $("<option value="+json[i].pos_model+">"+json[i].pos_model_name+"</option>").appendTo("#model");
						  }
					  }
				  }
			});
		}
	}
		
	});
	function addTerminal() {
		var excelFileName = $.trim($("#excelFileName").val());
		var model = $.trim($("#model").val());
		
		var pos_type = $("#pos_type").val();
		if(pos_type == "00"){
			alert("该设备不提供子机型选项,请重新选择设备，如无目标设备类型，请在设备管理中添加新设备!");
			return false;
		}
		
		if(pos_type == "-1"){
			alert("请选择设备类型!");
			return false;
		}
		
		var model = $("#model").val();
		if(model == "-1"){
			alert("请选择机具型号!");
			return false;
		}

		if (model == null || model == '') {
			var dialog = $.dialog({
				title : '错误',
				lock : true,
				content : '请选择机具类型',
				icon : 'error.gif',
				ok : function() {

				}
			});
			return false;
		}

		if (excelFileName == null || excelFileName == '') {
			var dialog = $.dialog({
				title : '错误',
				lock : true,
				content : '导入文件不能为空',
				icon : 'error.gif',
				ok : function() {

				}
			});
			return false;
		}

		$.dialog.confirm('确定要导入该文件吗？',function() {
							$('#terminalImp').ajaxSubmit(	{
												beforeSubmit : function() {
													pop_waiting_info("机具导入中，请稍候……");
												},
												dataType : 'json',
												type : 'POST',
												iframe : false,
												success : function(data) {
													pop_waiting_close();
													var ret = data.code;
													if (ret == "1004") {
														$.dialog({
																	title : "导入成功",
																	lock : true,
																	content : "机具导入成功！",
																	icon : "success.gif",
																	ok : null
																});
													} else if (ret == "1002") {
														$.dialog({
																	title : "错误",
																	lock : true,
																	content : "机具单次导入失败，数量超过最大限制3000台，超过限制请分批导入！",
																	icon : 'error.gif',
																	ok : null
																});
													} else if (ret == "1001") {
														$.dialog({
																	title : "错误",
																	lock : true,
																	content : "未发现需要导入的机具信息！",
																	icon : 'error.gif',
																	ok : null
																});
													} else if (ret == "1003") {
														var line = data.failList;
														$.dialog({
																	title : "警告",
																	lock : true,
																	content : "以下机具导入失败，请检查是已否存在或已使用,格式SN:PSAM！"
																			+ line,
																	icon : 'alert.gif',
																	ok : null
																});
													} else {
														$.dialog({
																	title : "错误",
																	lock : true,
																	content : "系统故障，机具导入失败，请稍后重试！",
																	icon : 'error.gif',
																	ok : null
																});
													}
												}
											});

						});
	}

	function successMsg(line) {
		var dialog = $.dialog({
			title : '提示',
			lock : true,
			content : '成功导入' + line + "行数据",
			icon : 'success.gif',
			ok : null
		});
		clearTerminal();
	}
	function clearTerminal() {
		$('#excelFileName').val('');
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：机具管理&gt;机具导入
		</div>
		<form:form id="terminalImp" name="terminalImp"	action="${ctx}/ter/terImp" method="post">
			<div style="position:absolute;margin-left:68px;margin-top:114px;">
				<input name="agentInput" id="agentInput" value="全部"		autocomplete="off"	style="position:absolute;top:1px;left:32px;height:10px;width: 277px;border: none;">
			</div>
			<div class="item">
				<div class="title">机具导入</div>
				<ul>
					<li><label style="width:80px;">设 备 类 型：</label>
						 <u:TableSelect		sid="pos_type" sname="pos_type" style="padding:2px;width:160px"		tablename="pos_type" fleldAsSelectValue="pos_type"
							fleldAsSelectText="pos_type_name" value="${params.pos_type}"		otherOptions="needAll" /><label class="must">*</label>
					</li>
				</ul>
				<div class="clear"></div>
				<ul>
					<li><label style="width:80px">机 具 型 号： </label>
						<select id="model"		name="model" style="padding:2px; width:160px">
							<option value="-1">--全部--</option>
						</select>
						<label class="must">*</label>
					</li>
				</ul>
				<div class="clear"></div>
				<ul>
					<li><label style="width:80px">代理商名称：	</label> 
						<u:select value="${params['agentNo']}" stype="agent"		sname="agentNo" style="width: 302px;height: 24px"		onlyThowParentAgent="true" /></li>
				</ul>
				<div class="clear"></div>
				<ul>
					<li><label style="width:80px">导 入 文 件：</label>
						<input id="excelFileName" name="excelFileName" type="text"	readOnly="true" value="" /><input id="file_upload"	name="file_upload" type="file" />
					</li>
				</ul>
				<ul>
					<li id="success" style="color:blue"></li>
				</ul>
				<ul>
					<li></li>
				</ul>
			</div>
			<div class="clear"></div>
			<br />
			<div class="search_btn clear">
				<input class="button blue  " type="button" id="addButton" value="导入"		onclick="javascript:addTerminal();" />
				 <input class="button blue  "	type="button" id="clearButton" value="返回"			onclick="javascript:window.location.href='${ctx}/ter/terQuery'" />
			</div>
		</form:form>
		<script type="text/javascript">
			$("#agentInput").val(
					$("[name=agentNo]").find("option:selected").text());
		</script>
	</div>
</body>