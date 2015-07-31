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
	var agentT = $("[name=agentNo]").find("option:selected").text();
	$("#agentInput").val(agentT);
	        var cus = 0;
		    var classname = "";
		    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:28px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
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

function saveMerchantCheckInfo(){
		$.ajax({
        cache: true,
        type: "POST",
        url:"${ctx}/merCP/merchantCheckModifySave",
        data:$('#checkModifySave').serialize(),
        async: false,
        dataType: 'json',
        error: function(request) {
            alert("出错了！");
        },
        success: function(json){
	  			var removeCount = json.removeCount;
		  		if(removeCount > 0){
		  			alert("修改成功!");
		  			window.location.href="${ctx}/merCP/queryMerchantCheckInfo";
		  		}else{
		  			alert("修改失败!");
		  		}
	 		}
    });
}



	function checkChange(){
		var check_type = document.getElementById('check_type').value;
		if(check_type=='1'){
			$("#pos_type option[value=-1]").attr("selected", true); 
			$("#agentNo option[value=-1]").attr("selected", true); 
			$("#agentInput").val("全部");
			$("#agentInput").attr("disabled",true);
			$("[name=agentNo]").attr("disabled",true);
			$("#pos_type").attr("disabled",true);
		}else if(check_type=='2'){
			$("#agentNo option[value=-1]").attr("selected", true); 
			$("#agentInput").val("全部");
			$("#agentInput").attr("disabled",true);
			$("[name=agentNo]").attr("disabled","disabled");
			$("#pos_type").attr("disabled",false);
		}else if(check_type=='3'){
			$("#pos_type option[value=-1]").attr("selected", true); 
			$("#agentInput").attr("disabled",false);
			$("[name=agentNo]").attr("disabled",false);
			$("#pos_type").attr("disabled",true);
		}else if(check_type=='4'){
			$("#agentInput").attr("disabled",false);
			$("[name=agentNo]").attr("disabled",false);
			$("#pos_type").attr("disabled",false);
		}
	}
	
	window.onload = function(){
		var check_type = "${checkPersonInfo.check_type}";
		if(check_type=='1'){
			$("#pos_type option[value=-1]").attr("selected", true); 
			$("#agentNo option[value=-1]").attr("selected", true); 
			$("#agentInput").val("全部");
			$("#agentInput").attr("disabled",true);
			$("[name=agentNo]").attr("disabled",true);
			$("#pos_type").attr("disabled",true);
		}else if(check_type=='2'){
			$("#agentNo option[value=-1]").attr("selected", true); 
			$("#agentInput").val("全部");
			$("#agentInput").attr("disabled",true);
			$("[name=agentNo]").attr("disabled","disabled");
			$("#pos_type").attr("disabled",false);
			$("#pos_type option[value=${checkPersonInfo.pos_type}]").attr("selected", true); 
		}else if(check_type=='3'){
			$("#pos_type option[value=-1]").attr("selected", true); 
			$("#agentInput").attr("disabled",false);
			$("[name=agentNo]").attr("disabled",false);
			$("#pos_type").attr("disabled",true);
		}else if(check_type=='4'){
			$("#agentInput").attr("disabled",false);
			$("[name=agentNo]").attr("disabled",false);
			$("#pos_type").attr("disabled",false);
			$("#pos_type option[value=${checkPersonInfo.pos_type}]").attr("selected", true); 
		}
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：审核人管理&gt;修改审核人
		</div>
		<form:form id="checkModifySave" action="${ctx}/merCP/merchantCheckModifySave" method="post">
		<div style="position:absolute;margin-left:70px;margin-top:92px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 215px !important;border: none;">
         	</div>
			<div id="search">
				<div id="title">修改审核人</div>
				<input type="hidden"  value="${checkPersonInfo['id']}" id="id"  name="id"/> 
				<ul>
					<li>
						<span style="width: 80px;">审  核  人：</span>
						<select id="user_id" name="user_id" style="padding:2px;width: 240px;">
							<c:forEach items="${customServiceList}" var="customService">
								<option value="${customService.id}" <c:if test="${customService.id eq checkPersonInfo['user_id']}">selected = "selected"</c:if>>${customService.real_name}</option>		
							</c:forEach>
						</select>
					</li>
					<li>
						<span style="width: 80px;">审核类型：</span>
						<select id="check_type"  name="check_type" onclick="checkChange()"  style="width:140px;padding:2px">
							<option value="1" <c:if test="${'1' eq checkPersonInfo['check_type']}">selected = "selected"</c:if>>顺序审核</option>
							<option value="2" <c:if test="${'2' eq checkPersonInfo['check_type']}">selected = "selected"</c:if>>设备审核</option>
							<option value="3" <c:if test="${'3' eq checkPersonInfo['check_type']}">selected = "selected"</c:if>>代理商审核</option>
							<option value="4" <c:if test="${'4' eq checkPersonInfo['check_type']}">selected = "selected"</c:if>>代理设备审核</option>
						</select>
					</li>
					
				</ul>
				<div class="clear"></div>
				<ul>
					<li id="agentName">
						<span style="width: 80px;">代理商名称：</span>
						<u:select  style="width:240px;padding:2px" value="${checkPersonInfo['agent_no']}"  stype="agent" sname="agentNo"  id="agentNo" onlyThowParentAgent="true"  />
					</li>
					<li id="posType">
						<span style="width: 80px;">设备类型：</span>
						<u:TableSelect  sid="pos_type" sname="pos_type" style="width: 140px; height: 24px; vertical-align: top;" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="" otherOptions="needAll" />
						<%--<select style="padding:2px;width:140px" name="pos_type" id="pos_type">
			         		<option value="-1" <c:out value="${checkPersonInfo['pos_type'] eq NULL ?'selected':'' }"/>>--全部--</option>
			         		<option value="1" <c:out value="${checkPersonInfo['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>
			         		<option value="2" <c:out value="${checkPersonInfo['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>
			         		<option value="3" <c:out value="${checkPersonInfo['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>
			         		<option value="4" <c:out value="${checkPersonInfo['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>
			         		<option value="5" <c:out value="${checkPersonInfo['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>
			         	</select>
					--%></li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="button" id="query" value=" 保 存 "  onclick="saveMerchantCheckInfo()"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<input class="button blue medium" type="button" id="add"
						onclick="javascript:window.location.href='${ctx}/merCP/queryMerchantCheckInfo'" value=" 返 回 " />
			</div>
		</form:form>
	</div>
</body>
