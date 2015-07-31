<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
</head>
<script type="text/javascript">
$(function(){
	var type = "${params['type']}";
	if(type=="agent"){
		$("#agent").show();
		$("#merchant").hide();
	}else if(type=="merchant"){
		$("#agent").hide();
		$("#merchant").show();
	}
})

function merchantExchange(){
	var merchantChange = $("#merchantChange").val();
	if(merchantChange=="0"){
		$("#merchant").show();
		$("#agent").hide();
	}else if(merchantChange=="1"){
		$("#merchant").hide();
		$("#agent").show();
	}
}

function sureModify(){
	var api = frameElement.api, W = api.opener;
	var INTEGER_REG = /^(?!0+(?:\.0+)?$)\d+(?:\.\d{1,2})?$/; //小数可有可无,最多两位小数,必须大于零
	var type = "${params['type']}";
	var idArr = "${params['idArr']}";
	var merchantChange = $("#merchantChange").val();
	var agentNo = "${params['agentNo']}";
	if(type=="agent" && merchantChange=="1"){
		var basicData = $("#basicData").val();
		var richData0 = $("#richData0").val();
		var richData1 = $("#richData1").val();
		var richData2 = $("#richData2").val();
		var richData3 = $("#richData3").val();
		var richData4 = $("#richData4").val();
		var richData5 = $("#richData5").val();
		var richData6 = $("#richData6").val();
		var richData7 = $("#richData7").val();
		var richData8 = $("#richData8").val();
		if (!basicData.match(INTEGER_REG) || !richData0.match(INTEGER_REG) || !richData1.match(INTEGER_REG)
				|| !richData2.match(INTEGER_REG) || !richData3.match(INTEGER_REG)
				|| !richData4.match(INTEGER_REG) || !richData5.match(INTEGER_REG)
				|| !richData6.match(INTEGER_REG) || !richData7.match(INTEGER_REG) || !richData8.match(INTEGER_REG)) {
			alert("请填写正确的数值");
			return ;
		}
		$.ajax({
	        url: '${ctx}/purse/bagTzeroAmountModify',
	        type:"POST",
			data:{"agentNo":agentNo,"type":type,"merchantChange":merchantChange,"basicData":basicData,"richData0":richData0,"richData1":richData1,"richData2":richData2,"richData3":richData3,"richData4":richData4,"richData5":richData5,"richData6":richData6,"richData7":richData7,"richData8":richData8},
	        error:function(){
	           alert("提交出错");
	        },
	        success: function(data){
	      	  alert(data);
			  api.close();
	        }
	   });
	}else{
		var day_extraction_max_amount = $("#allAmount").val();
		if (!day_extraction_max_amount.match(INTEGER_REG)){
			alert("请填写正确的数值");
			return ;
		}
		var mobileNo = "${params['mobileNo']}";
		var appType = "${params['appType']}";
		$.ajax({
	        url: '${ctx}/purse/bagTzeroAmountModify',
	        type:"POST",
			data:{"agentNo":agentNo,"mobileNo":mobileNo,"appType":appType,"type":type,"merchantChange":merchantChange,"day_extraction_max_amount":day_extraction_max_amount},
	        error:function(){
	           alert("提交出错");
	        },
	        success: function(data){
	      	  alert(data);
			  api.close();
	        }
	   });
	}
	
}
function cancelModify(){
	var api = frameElement.api, W = api.opener;
	api.close();
}
</script>
<body>
<div class="item liHeight">
    <form:form>
    <div id="search" style="border: 0px solid #EAEAEA;">
    	<div id="title">修改额度</div>
    	  <ul><strong style="color: red;">注意：额度修改将即刻改变，请谨慎操作!</strong></ul>
	      <ul>
	        <li style="margin-top: 5px;width: 300px;">
				<span style="width:80px;">
					<c:if test="${params['type'] eq 'agent' }">
						代理商名称:
					</c:if>
					<c:if test="${params['type'] eq 'merchant' }">
						商户名称:
					</c:if>
				</span>
				 ${params.name}
			 </li>
			  <li style="margin-top: 5px;margin-left:20px;width: 200px;"><span style="width:70px;">修改范围：</span>
			  	 <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="merchantChange" name="merchantChange" onchange="javascript:merchantExchange();">
			        <c:if test="${params['type'] eq 'agent' }">
				        <option value="1"  selected='selected'>未来商户</option>	
			        </c:if>
	         		<option value="0">现存商户</option>
				</select>
			 </li>
	      </ul>
	      <br/>
	      <br/>
	      <ul id="merchant">
	        <li ><span style="width: 100px;"><strong>现在总额度：</strong></span><input type="text" style="width: 100px;" value="${params.day_extraction_max_amount}" name="allAmount" id="allAmount"/>元</li>    
	      	<br/>
	      </ul>
	      <ul id="agent">
	      	<!-- -1基础资料  0通讯录  1户口本、结婚证  2学历证明  3公司股东证明  4个人完税证明  5信用报告  6大额存款单、股票、债券等有价证券证明  7车辆证明  8房产证明  9更多 -->
	        <li ><span style="width: 100px;"><strong>基础资料额度：</strong></span><input type="text" style="width: 100px;" value="${agentAmountMap['basic_data_amount']}" id="basicData" />元</li>
	        <br/>
	        <br/>
	        <li ><span style="width: 80px;"><strong>丰富资料：</strong></span></li>
	        <br/>
	        <br/>
	        <li style="width: 300px;"><span style="width: 120px;">房产证：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data8_amount']}" id="richData8" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">车辆证明：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data7_amount']}" id="richData7" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;word-break: break-all ;line-height: 15px;">大额存款单、股票、债券等有价证券证明：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data6_amount']}" id="richData6" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">信用报告：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data5_amount']}" id="richData5" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">个人完税证明：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data4_amount']}" id="richData4" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">公司股东证明：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data3_amount']}" id="richData3" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">学历证明：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data2_amount']}" id="richData2" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">户口本、结婚证：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data1_amount']}" id="richData1" />元</li>
	        <li style="width: 300px;margin-top:10px;"><span style="width: 120px;">通讯单：</span><input type="text" style="width: 100px;" value="${agentAmountMap['rich_data0_amount']}" id="richData0" />元</li>
	     </ul>
	      <br/>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="button" id="submitButton"  value="确认修改" onclick="sureModify()"/>  	
    	<input class="button blue medium" type="button" id="modify"  value="取消修改" onclick="cancelModify()"/>
    </div>
    </form:form>
	<div class="clear"></div>
</div>
</body>
</html>