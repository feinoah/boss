<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
 <script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
 <script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
 <script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:15px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
	<script type="text/javascript">
	$(function(){
		var INIT_OPTION = "--请选择--";
		var pr = "${params['province']}";
		$("<option></option>").val("-1").text(INIT_OPTION).appendTo("#province");
		$.each(provinceName, function(i, n) {
			if(pr != "" && pr == n){
				$("<option selected='selected'></option>").val(n).text(n).appendTo("#province");
			}else{
				$("<option></option>").val(n).text(n).appendTo("#province");
			}
		});
		var cus = 0;
	    //var classname = "";
	   var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:31px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
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
					                        $("<li title=" + arry[k] + "  style='background-color: #FFFFFF;width:328px'></li>").text(arry[k]).appendTo($autocomplete).mouseover(function() {
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
	    
	    $("[name=agentNo]").change(function(){
	    	var agent_Name =  $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
			if(agent_Name != ""){
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
	    	});
	});
	
	function validateCheckBox(){
		if($("#valiBox").is(':checked')){
			$("input[value=同意]").prop("disabled",false);
        }else{
        	$("input[value=同意]").prop("disabled",true);
        }
	}
	  //--------------------------------------转移集群-----------------------------------------
		function  move(){
			var content = "<font color='red'>转移集群后，所有转入到集群的商户是否优质商户将于目标集群保持一致！请谨慎操作！</font><br/><br/>";
			content +="<input id='valiBox' type='checkbox' onclick='validateCheckBox()'>我同意<br/>";
			$.dialog({
				lock : true,
				title : '警告',
				width : 340,
				height : 100,
				icon: 'alert.gif',
				resize : false,
				max : false,
				min : false,
			    id: 'testID',
			    content: content,
			    button: [{
			            name: '同意',
			            disabled: true,
			            callback: function () {
			            	var params = "";
							params += "acq_enname=" + $('[name=acq_enname]').val();
							params += "&group_code=" + $("[name=group_code]").val();
							params += "&pos_type=" + $("[name=pos_type]").val();
							params += "&my_settle=" + $("[name=my_settle]").val();
							params += "&bag_settle=" + $("[name=bag_settle]").val();
							params += "&agentNo=" +  $("[name=agentNo]").val();
							params += "&fee_type=" +  $("[name=fee_type]").val();
							params += "&fee_rate=" +  $("[name=fee_rate]").val();
							params += "&real_flag=" +  $("[name=real_flag]").val();
							params += "&province=" +  $("[name=province]").val();
							params += "&city=" +  $("[name=city]").val();
							$.dialog({lock: true,drag: false,resize: false,max: false,content: 'url:viewDistractGroup?'+ params +'&layout=no',close: function(){
								$("#distractQuery").click();
						    }});  
			            }
			        },{name: '关闭'} ]
			});
				
		}
	  
	  //集群商户导出
	  function groupMerchantExport(){
		  $("#groupMerchantDistract").attr({action:"${ctx}/group/groupMerchantDistractExport"}).submit();
		  $("#groupMerchantDistract").attr({action:"${ctx}/group/groupMerchantDistract"});
	  }
	  
		$(document).ready(function(){ 
			$("#province").empty();
			var INIT_OPTION = "--请选择--";
			$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
			$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");

			$.each(provinceName, function(i, n) {
				$("<option></option>").val(n).text(n).appendTo("#province");
			});
			
			$("#province").change(function() {
				var province = $("#province").val();
				$("#city").empty();
				$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");
				if (province != "") {
					var provinceIndex = 0;
					provinceIndex = $("#province option:selected").index();
					var cityArray = eval("city" + provinceIndex);
					$.each(cityArray, function(i, n) {
						$("<option></option>").val(n).text(n).appendTo("#city");
					});

				}
			});

			var defaultProvince = '${params.province}';
			var defaultCity = '${params.city}';

			if ($.trim(defaultProvince).length > 0) {
				$("#province").val(defaultProvince);
				$("#province").change();

				if ($.trim(defaultCity).length > 0) {
					$("#city").val(defaultCity);
				}
			}
				var  ft = "${params['fee_rate']}";
				var fee_type = "${params['fee_type']}";
				var pos_type = "${params['pos_type']}";
				if(pos_type != null && pos_type != ""  && pos_type != "-1"){
					 $.ajax({
				 			type:"post",
				 			url:"${ctx}/group/getMerchantFeeType",
				 			data:{'pos_type':pos_type},
				 			async:false,
				 			dataType: 'json',
						  	success: function(json){
							  $("#fee_type").empty();
							  $("<option value='-1'>--请选择--</option>").appendTo("#fee_type");
							  var typeName = "";
							  for(var i=0;i<json.length;i++){
								  if(json[i].fee_type == "RATIO"){ //扣率
									  typeName = "扣率";
								  }else if(json[i].fee_type == "CAPPING"){
									  typeName = "封顶";
								  }else if(json[i].fee_type == "LADDER"){
									  typeName = "阶梯";
								  }else{
									  typeName = json[i].fee_type;
								  }
								  $("<option value="+json[i].fee_type+">"+typeName+"</option>").appendTo("#fee_type");
							  }
						  }
				 		}
				 	);
				}
				
				if(fee_type != null && fee_type != ""  && fee_type != "-1"){
					$("#fee_type option[value="+fee_type+"]").attr("selected","selected");
						$.ajax({
				 			type:"post",
				 			url:"${ctx}/group/getGroupMerchantFeeType",
				 			data:{'fee_type':fee_type,'pos_type':pos_type},
				 			async:false,
				 			dataType: 'json',
						  success: function(json){
							  $("#fee_rate").empty();
							  $("<option value='-1'>--请选择--</option>").appendTo("#fee_rate");
							  for(var i=0;i<json.length;i++){
								  if(fee_type == "RATIO"){ //扣率
									  if(ft != null && ft !="" && ft == json[i].fee_rate){
										  $("<option value='"+json[i].fee_rate+"'selected>"+(json[i].fee_rate*100).toFixed(2)+"%</option>").appendTo("#fee_rate");
									  }else{
										  $("<option value="+json[i].fee_rate+">"+(json[i].fee_rate*100).toFixed(2)+"%</option>").appendTo("#fee_rate");
									  }
								  }else if(fee_type == "CAPPING"){ //封顶
									  var cg =json[i].fee_rate+"="+json[i].fee_max_amount;
									  if(ft != null && ft !="" && ft == cg){
										  $("<option value='"+json[i].fee_rate+"="+json[i].fee_max_amount+"'selected>"+(json[i].fee_rate*100).toFixed(2)+"%, "+json[i].fee_max_amount+"</option>").appendTo("#fee_rate");
									  }else{
										  $("<option value='"+json[i].fee_rate+"="+json[i].fee_max_amount+"'>"+(json[i].fee_rate*100).toFixed(2)+"%, "+json[i].fee_max_amount+"</option>").appendTo("#fee_rate");
									  }
								  }else if(fee_type == "LADDER"){ //阶梯
									  var j = json[i].ladder_fee.toString().split("<");
								  		var sh = (j[0]*100).toFixed(2)+"%<"+j[1]+"<"+(j[2]*100).toFixed(2)+"%";
									  if(ft != null && ft !="" && ft == json[i].ladder_fee){
										  $("<option value='"+json[i].ladder_fee+"'selected>"+sh+"</option>").appendTo("#fee_rate");  
									  }else{
										  $("<option value="+json[i].ladder_fee+">"+sh+"</option>").appendTo("#fee_rate");
									  }
								  }
							  }
						  }
				 		}
				 	);
				}
				
			}); 
	  
	  
	  //根据扣率类型，获取相应的扣率信息
	  function getMerchantFee(){
		  var fee = $("#fee_type").val();
		  var pos_type = $("#pos_type").val();
		  $.ajax({
	 			type:"post",
	 			url:"${ctx}/group/getGroupMerchantFeeType",
	 			data:{'fee_type':fee,'pos_type':pos_type},
	 			async:false,
	 			dataType: 'json',
			  success: function(json){
				  $("#fee_rate").empty();
				  $("<option value='-1'>--请选择--</option>").appendTo("#fee_rate");
				  for(var i=0;i<json.length;i++){
					  if(fee == "RATIO"){ //扣率
						  $("<option value="+json[i].fee_rate+">"+(json[i].fee_rate*100).toFixed(2)+"%</option>").appendTo("#fee_rate");
					  }else if(fee == "CAPPING"){ //封顶
						  $("<option value='"+json[i].fee_rate+"="+json[i].fee_max_amount+"'>"+(json[i].fee_rate*100).toFixed(2)+"%, "+json[i].fee_max_amount+"</option>").appendTo("#fee_rate");
					  }else if(fee == "LADDER"){ //阶梯
						  var j = json[i].ladder_fee.toString().split("<");
					  		var sh = (j[0]*100).toFixed(2)+"%<"+j[1]+"<"+(j[2]*100).toFixed(2)+"%";
						  $("<option value="+json[i].ladder_fee+">"+sh+"</option>").appendTo("#fee_rate");
					  }
				  }
			  }
	 		}
	 	);
	  }
	  
	//根据设备类型，获取集群中相应的扣率类型
	  function getMerchantFeeType(){
		  var pos_type = $("#pos_type").val();
		  $.ajax({
	 			type:"post",
	 			url:"${ctx}/group/getMerchantFeeType",
	 			data:{'pos_type':pos_type},
	 			async:false,
	 			dataType: 'json',
			  	success: function(json){
				  $("#fee_type").empty();
				  $("<option value='-1'>--请选择--</option>").appendTo("#fee_type");
				  $("#fee_rate").empty();
				  $("<option value='-1'>--请选择--</option>").appendTo("#fee_rate");
				  var typeName = "";
				  for(var i=0;i<json.length;i++){
					  if(json[i].fee_type == "RATIO"){ //扣率
						  typeName = "扣率";
					  }else if(json[i].fee_type == "CAPPING"){
						  typeName = "封顶";
					  }else if(json[i].fee_type == "LADDER"){
						  typeName = "阶梯";
					  }else{
						  typeName = json[i].fee_type;
					  }
					  $("<option value="+json[i].fee_type+">"+typeName+"</option>").appendTo("#fee_type");
				  }
			  }
	 		}
	 	);
	  }
	  
 </script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>路由集群中普通商户查询（转移）</div>
   <form:form id="groupMerchantDistract" action="${ctx}/group/groupMerchantDistract" method="post" >
   
    <div id="search">
     <div id="title">路由集群编号查询</div>
       <ul>
		  <li>
			   <span style="width: 60px;">收单机构：</span>
			   <select id="acq_enname" name="acq_enname" style="padding:2px;width: 140px;">
				   <option value="">全部</option>
				   <c:forEach items="${acqOrgList}" var="m">
					   <option value="${m.acq_enname}" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>
				   </c:forEach>
			   </select>
		  </li>
          <li><span style="width: 60px;">集群编号：</span><input type="text"  style="width: 132px;" value="${params['group_code']}" name="group_code" /></li>
          <li><span style="width: 60px;">代理商：</span><u:select value="${params['agentNo']}" style="width: 249px;height: 24px"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" />
          		<div style="position:absolute;margin-left:20px;margin-top:-21px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:42px;height:10px;width: 223px;border:none;">
         	</div>
          </li>
          <li>
	         <span>设备类型：</span>
	         <u:TableSelect sname="pos_type" style="padding:2px;width:140px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
			 <%--<select id="pos_type" name="pos_type" style="width: 140px; height: 24px; vertical-align: top;"  onchange="getMerchantFeeType()">
					<option value="-1">--请选择--</option>
					<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>
					<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>
					<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>
					<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>
					<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>
			 </select>
		 --%></li> 
		  <li>
	         <span style="width: 60px;">扣率类型：</span>
			 <select id="fee_type" name="fee_type" style="width: 140px; height: 24px; vertical-align: top;" onchange="getMerchantFee();">
					<option value="-1">--请选择--</option>
					<%--<option value="RATIO" >扣率</option>
					<option value="CAPPING" >封顶</option>
					<option value="LADDER" >阶梯</option>
			 --%></select>
		 </li> 
		 </ul>
		 <ul>
		 <li>
	         <span style="width: 60px;">扣率：</span>
			 <select id="fee_rate" name="fee_rate" style="width: 140px; height: 24px; vertical-align: top;">
					<option value="-1">--请选择--</option>
			 </select>
		 </li> 
		 <li style="width:310px;">
		    <label>是否优质商户：</label>
			<select id="my_settle" name="my_settle"  style="width: 220px; height: 24px; vertical-align: top;">
			    <option value="-1">-请选择-</option>
				<option value="0" <c:out value="${params['my_settle'] eq '0'?'selected':'' }"/>>否</option>
				<option value="1" <c:out value="${params['my_settle'] eq '1'?'selected':'' }"/>>是</option>
			</select>
		 </li>	
		 <li>
		    <label>是否钱包结算：</label>
			<select id="bag_settle" name="bag_settle"  style="width: 128px; height: 24px; vertical-align: top;">
			    <option value="-1">-请选择-</option>
				<option value="0" <c:out value="${params['bag_settle'] eq '0'?'selected':'' }"/>>否</option>
				<option value="1" <c:out value="${params['bag_settle'] eq '1'?'selected':'' }"/>>是</option>
			</select>
		 </li>
		<div class="clear"></div>
		 <li><span style="width: 60px;">所属省份：</span><select id="province" name="province"  style="width: 140px;height: 24px;vertical-align: top;" ></select>
		 	</li>
		 	<li>
		 		<span style="width: 60px;">所属城市：</span><select id="city" name="city" style="width: 140px;height: 24px;vertical-align: top;"></select>
		 	</li>
		  <li>
		    <label>是否实名：</label>
			<select id="real_flag" name="real_flag"  style="width: 246px; height: 24px; vertical-align: top;">
			    <option value="-1">-请选择-</option>
				<option value="0" <c:out value="${params['real_flag'] eq '0'?'selected':'' }"/>>否</option>
				<option value="1" <c:out value="${params['real_flag'] eq '1'?'selected':'' }"/>>是</option>
			</select>
		 </li>	
       </ul>
       <div class="clear"></div>
    </div>
    <div class="search_btn">
     <input   class="button blue medium" type="submit" id="distractQuery"  value="查询"/>
     <input   class="button blue medium" type="button" id="distract"  value="转移集群" onclick="move()"/>
     <input   class="button blue medium" type="button" id="distractExport"  value="集群导出" onclick="groupMerchantExport()"/>
     <input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5%">序号</th>
          <th width="120">商户编号</th>
          <th width="150">代理商名称</th>
          <th width="120">商户名称</th>
          <th width="50">集群编号</th>
          <th width="110">集群名称</th>
          <th width="50">设备类型</th>
          <th width="40">是否优质</th>
          <th width="60">是否钱包结算</th>
          <th width="40">是否实名</th>
          <th width="50">所属省份</th>
          <th width="50">所属城市</th>
        <c:forEach items="${list.content}" var="item" varStatus="status">
         <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
           <td class="center"><span class="center">${status.count}</span></td>
           <td class="center"><span class="center">${item.merchant_no}</span></td>
           <td align="left"  class="agent_name"><u:substring length="14" content="${item.agent_name}"/></td>       
           <td><u:substring length="11" content="${item.merchant_name}"/></td>
           <td class="center"><span class="center">${item.group_code}</span></td>
           <td><u:substring length="8" content="${item.group_name}"/></td>
            <td>
            	<u:postype svalue="${item.pos_type}" />
           </td>
            <td>
                 <c:if test="${item.my_settle eq '0' }">否</c:if> 
				 <c:if test="${item.my_settle eq '1' }">是</c:if>
           </td>
           <td>
                 <c:if test="${item.bag_settle eq '0' }">否</c:if> 
				 <c:if test="${item.bag_settle eq '1' }">是</c:if>
           </td>
           <td>
                 <c:if test="${item.real_flag eq '0' }">否</c:if> 
				 <c:if test="${item.real_flag eq '1' }">是</c:if>
           </td>
           <td>
				 ${item.province}
           </td>
           <td>
				 ${item.city}
           </td>
          </tr>
       </c:forEach>
      </table>
    </div>
 <div id="page">
   <pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
 </div>
  </div>
  <script type="text/javascript">
		$("#agentInput").val( $("[name=agentNo]").find("option:selected").text());
	</script>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
