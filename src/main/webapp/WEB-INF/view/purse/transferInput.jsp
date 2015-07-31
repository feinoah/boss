<%@page pageEncoding="utf-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<style type="text/css">

	</style>
	<script type="text/javascript">
	
		function bind(){
			var INTEGER_REG = /^(0|([1-9][0-9]*))$/; //正整数
			var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
			var api = frameElement.api, W = api.opener;
			var mobileNo="${params.mobileNo}";
			var balance=parseFloat("${params.balance}");
			var balance1=parseFloat("${params.balance1}");
			var appType="${params.appType}";
			var accAmount = $.trim($("#accAmount").val());
			var adjustType = $.trim($("#adjustType").val());
			var transReason = $.trim($("#transReason").val());
			var realName = "${params.realName}";
			var floAcmount= parseFloat(accAmount);
			if(accAmount==null || accAmount==""){
				alert("请输入调账金额");
				return ;
			}
			if(adjustType=="-1"){
				alert("请选择调账方向");
				return ;
			}
			if(transReason==null || transReason==""){
				alert("请填写调账原因");
				return ;
			}
			
			if (!accAmount.match(INTEGER_REG)&& !accAmount.match(MONEY_REG)) {
				alert("请填写正确的调账金额");
				return ;
			}
			
			if(adjustType=='1'){
				if(floAcmount>(balance+balance1)){
					alert("调账金额不能大于用户钱包金额，请检查数据。");
					return ;
				}
			}
			
		     $.ajax({
		          url: '${ctx}/purse/userTransferAcc',
		          type:"POST",
				  data:{"adjustType":adjustType,"transReason":transReason,"accAmount":accAmount,"mobileNo":mobileNo,"appType":appType,"realName":realName},
		      	  dataType: "json",
		          error:function(){
                     alert("提交出错");
                 },
		          success: function(data){
		        	  alert(data.msg);
					  api.close();
		          }
		     });
		
		}
 
</script>
</head>
<body>

	<ul style="padding:20px;font-size:13px">
	<li >用户姓名：${params.realName}</li>
	<li >用户手机：${params.mobileNo}</li>
	<li style="margin-top:3px"><span>调账金额：</span><input type="text" id="accAmount" name="accAmount" style="width:90px;">&nbsp;元</li>
	<li style="margin-top:3px"><span>调账方向：</span><select name=adjustType id="adjustType" style="width:210px;padding:3px">
		<option value="-1">请选择</option>
		<option value="1">客户钱包=====>>公司账户</option>
		<option value="0">公司账户=====>>客户钱包</option>
		</select>
	</li>
	<li style="margin-top:3px" >调账原因：<textarea name="transReason" id="transReason" cols="35" rows="4"></textarea> </li>
	</ul>
	<input onclick="javascript:bind();" style="margin-left:20px"   class="button rosy medium" type="button" id="submit"  value="确定提交"/>
	
</body>
</html>