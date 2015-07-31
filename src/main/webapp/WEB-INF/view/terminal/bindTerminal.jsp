<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script>
$(function() {
	$("#agentNo").val("");
	$("#agentNo").val("${agentNo}");
	$("#merchantInfo").html("");
	$("#submitConfirm").attr("disabled",true);
	$("#submitConfirm").attr("class","");
	$("#submitConfirm").click(function(){
		var api = frameElement.api;
		var ano = $("#ano").val();
		var agentNo = '${agentNo}';
			$.get('${ctx}/ter/validateMerchant?merchantNo='+ano+'&agentNo='+agentNo,function(data){
				if(data==1){
					$.get('${ctx}/ter/bindTerminal?id=${ids}&merchantNo='+ano,function(data){
							if(data==1){
								alert('绑定成功');
							}else{
								alert('绑定失败，请检查数据');
							}
							api.close();
						});
				}
				else if(data==-1)
				{
					alert('绑定失败，所输入商户编号不属于已分配代理商下面的商户');
				}
				else{
					alert('绑定失败，商户编号不存在');
				}
			});
	});
	
	
	$("#ano").keyup(function(){
		var merchant_no = $(this).val();
		var agent_no = $("#agentNo").val();
		findMerchantInfo(merchant_no,agent_no);
		
	});
	
	$("#serachMerchantInfo").click(function(){
		var merchant_no = $("#ano").val();
		var agent_no = $("#agentNo").val();
		if(merchant_no == ""){
			alert("请输入需要绑定的商户编号!");
			return false;
		}
		
		if(merchant_no.trim().length < 15){
			alert("请输入有效的商户编号!");
			return false;
		}
		findMerchantInfo(merchant_no,agent_no);
		
	});
	
	
	
	function findMerchantInfo(merchant_no,agent_no){
		$("#merchantInfo").html("");
		$("#submitConfirm").attr("disabled",true);
		$("#submitConfirm").attr("class","");
		if(merchant_no !="" && merchant_no.length > 0){
			if(merchant_no.trim().length >= 15 && merchant_no.trim().length <= 20){
				$.ajax({
					url : "${ctx}/mer/getMerchantInfoByMerchantNo",
					cache : false,
					data : {"merchant_no" : merchant_no},
					type : "post",
					dataType : "json",
					timeout : 20000,
					error : function() {
						alert("加载商户信息时出错,请重试！");
					},
					success : function(json) {
						if(json.code == "1000"){
							if(json.agentNo == agent_no){
								$("#merchantInfo").html("<font color='red'>"+json.merchant_short_name+"</font>");
								$("#submitConfirm").attr("disabled",false);
								$("#submitConfirm").attr("class","button rosy medium");
							}else{
								alert("输入的商户编号与机具所分配的代理商不一致，请核实！");
							}
						}else{
							alert("系统未查询到商户信息,请检查商户编号是否正确！");
						}
					}
				});
			}
		}
	}
	
});

function setValue(index) {
    var ddl = document.getElementById("ano");
    var Value = ddl.options[index].text;
    document.getElementById("box4").value = Value;
}
</script>

</head>
<body>
	<ul style="padding:20px;font-size:13px;">
		<li >终端总数量：${c} 台</li>
		<li>商户编号：<input id="ano" class="input" style="height:24px;line-height:24px" />
					<input type="button" value="搜索" id="serachMerchantInfo"/>
		</li> 
		<li >商户简称：<label id="merchantInfo"></label> </li>
		<%--<li style="position:relative;">商户编号：<select id="ano"  style="width:120px;height:24px;"  onChange="setValue(this.selectedIndex)">
			<option value="">--请选择--</option>
			<c:forEach items="${list}" var="item">
			<option value="${item.merchant_no}" >${item.merchant_short_name}</option>				
			</c:forEach>
		</select>
		<input name="box4" id="box4" value="--请选择--" style="position:absolute;top:2px;left:68px;height:18px;width:100px;border:none;">   
		</li>
	--%></ul>
	<input type="hidden" id="agentNo" name="agentNo" />
	<input style="margin-left:20px"   class="button rosy medium" type="button" id="submitConfirm"  value="确定绑定" />
</body>
</html>