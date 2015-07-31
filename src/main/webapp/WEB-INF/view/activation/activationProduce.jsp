<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script>
function produce(){
    var api = frameElement.api, W = api.opener;
    var amount=document.getElementById("amount").value;
	if(amount==""|| amount==null){
		alert("请输入需生成的激活码数量！");
	}else{
		$.post('${ctx}/activation/activationProduce?amount='+amount,function(data){
						if(data==1){
							alert('成功生成激活码');
							location.href='${ctx}/activation/activationQuery';
						}else{
							alert('生成激活码失败，请检查数据！');
						}
						api.close();
					});
			}
     } 
</script>
</head>
<body>
	<ul style="padding:20px;font-size:13px">
	<%-- <li><span style="width: 55px;">激活码数量：</span><input type="text"  value="${params['amount']}" id="amount" name="amount" /></li> --%>
	<li><span style="width: 55px;">激活码数量：</span><input type="text"   id="amount" name="amount" /></li>
	</ul>
	<input onclick="javascript:produce();" style="margin-left:100px"   class="button rosy medium" type="submit" id="submit"  value="确定生成"/>
</body>
</html>