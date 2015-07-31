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
	
		function autoCheckRateSet(){
			var INTEGER_REG = /^(0|([1-9][0-9]*))$/; //正整数
			var api = frameElement.api, W = api.opener;
			var id="${params.id}";
			var autoCheckRate = $.trim($("#autoCheckRate").val());
			if(autoCheckRate==null || autoCheckRate==""){
				alert("请输入审核时长");
				return ;
			}
			if (!autoCheckRate.match(INTEGER_REG)) {
				alert("请填写正确的审核时长");
				return ;
			}
			
		     $.ajax({
		          url: '${ctx}/purse/updateChannelAutoCheck',
		          type:"POST",
				  data:{"id":id,"check_rate":autoCheckRate,"auto_check_state":1},
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
 
</script>
</head>
<body>

	<ul style="padding:20px;font-size:13px">
	<li >审核设置</li>
	<li style="margin-top:3px"><span>审核时长：</span><input type="text" id="autoCheckRate" name="autoCheckRate" style="width:90px;" value="${check_rate }">&nbsp;分钟</li>
	</ul>
	<input onclick="javascript:autoCheckRateSet(${params.id});" style="margin-left:20px"   class="button rosy medium" type="button" id="submit"  value="开启"/>
	
</body>
</html>