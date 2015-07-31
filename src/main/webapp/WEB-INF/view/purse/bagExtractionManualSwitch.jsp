<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<style type="text/css">

	</style>
	<script type="text/javascript">
	
		function updateManualSwitch(){
			var HOUR_REG = /^(([0-9])|([0-1][0-9])|([2][0-4]))$/; 
			var MINUTE_REG = /^(([0-9])|([0-5][0-9]))$/; 
			var api = frameElement.api, W = api.opener;
			var id="${map.id}";
			var hoursBegin = $.trim($("#hoursBegin").val());
			var minutesBegin = $.trim($("#minutesBegin").val());
			var hoursEnd = $.trim($("#hoursEnd").val());
			var minutesEnd = $.trim($("#minutesEnd").val());
			var opinionTextArea = $.trim($("#opinionTextArea").val());
			var manualSwitch = $("#manualSwitch").val();
			if(manualSwitch==0 && (opinionTextArea==null || opinionTextArea=="")){
				alert("请输入关闭提现意见");
				return ;
			}
			if(manualSwitch==1){
				if(hoursBegin!=null && hoursBegin!=""){
					if (!hoursBegin.match(HOUR_REG)) {
						alert("请填写正确的时间-开始小时");
						return ;
					}
				}
				if(minutesBegin!=null && minutesBegin!=""){
					if (!minutesBegin.match(MINUTE_REG)) {
						alert("请填写正确的时间-开始分钟");
						return ;
					}
				}
				if(hoursEnd!=null && hoursEnd!=""){
					if (!hoursEnd.match(HOUR_REG)) {
						alert("请填写正确的时间-结束小时");
						return ;
					}
				}
				if(minutesEnd!=null && minutesEnd!=""){
					if (!minutesEnd.match(MINUTE_REG)) {
						alert("请填写正确的时间-结束分钟");
						return ;
					}
				}
			}
			
		    $.ajax({
		          url: '${ctx}/purse/updateExtractionManualSwitch',
		          type:"POST",
				  data:{"id":id,"hoursBegin":hoursBegin,"minutesBegin":minutesBegin,"hoursEnd":hoursEnd,"minutesEnd":minutesEnd,"manualSwitch":manualSwitch,"opinionTextArea":opinionTextArea},
		      	  //dataType: "json",
		          error:function(){
                     alert("提交出错");
                 },
		          success: function(data){
		        	  alert(data);
					  api.close();
		          }
		     }); 
		
		}
		
	function winClose(){
		var api = frameElement.api, W = api.opener;
		api.close();
	}
	
	function showOpinion(){
		var manualSwitch = $("#manualSwitch").val();
		if(manualSwitch==0){
			$(":input[type='text']").attr("disabled","true");
			$("#opinion")[0].style.display="block";
		}
		if(manualSwitch==1){
			$(":input[type='text']").removeAttr("disabled");
			$("#opinion")[0].style.display="none";
		}
	}
	
	$(function(){
		var manual_switch="${map.manual_switch}";
		if(manual_switch==0){
			$(":input[type='text']").attr("disabled","true");
		}
	})
 
</script>
</head>
<body>

	<ul style="padding:20px;font-size:13px">
	<li style="margin-top:3px">
	<span>提现手动开关:</span>
	<select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="manualSwitch" onchange="showOpinion()">
   		<option value="1" <c:if test="${map.manual_switch eq 1}">selected='selected'</c:if>>开</option>
        <option value="0" <c:if test="${map.manual_switch eq 0}">selected='selected'</c:if>>关</option>	
	</select>
	</li>
	<li style="margin-top:3px;" id="time">
	<span>提现时间开关:</span>
	<input id="hoursBegin" type="text" style="width: 50px;" value="${map.extraction_hours_begin }"> : <input id="minutesBegin" type="text" style="width: 50px;" value="${map.extraction_minutes_begin }">
	~
	<input id="hoursEnd" type="text" style="width: 50px;" value="${map.extraction_hours_end }"> : <input id="minutesEnd" type="text" style="width: 50px;" value="${map.extraction_minutes_end }">
	</li>
	<li style="margin-top:3px;display: none;" id="opinion">
	<span>关闭意见:</span>
	<textarea rows="2" cols="40" id="opinionTextArea"></textarea>
	</li>
	</ul>
	<div align="center">
	<input onclick="javascript:updateManualSwitch();" style="margin-left:20px"   class="button rosy medium" type="button" id="submit"  value="确定"/>
	<input onclick="javascript:winClose();" style="margin-left:20px"   class="button rosy medium" type="button" id="submit"  value="取消"/>
	</div>
	
	
</body>
</html>