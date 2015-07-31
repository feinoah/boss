<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<style type="text/css">
	#main
	{
		width: 300px;
		height:220px;
		overflow:hidden;
	}
	
	#main ul li
	{
		height:32px;
		line-height:32px;
		width:320px;
	}
	
	#main ul li label
	{
		display:-moz-inline-box;
		display:inline-block;
		width:120px;
	}
	
	#main ul li label.must
	{
		width:10px;
		color:red;
	}
	
	#main ul li input
	{
		width:140px;
	}
</style>
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>

<script>
	$(function(){
		var api = frameElement.api, W = api.opener;
		$("#submit").click(function(){
			//api.close();

			//check
			var merchant_no = $("#merchant_no").val();
			if($.trim(merchant_no).length === 0)
			{
				alert("商户号不能为空");
				$("#merchant_no").focus();
				return;
			}

			$.post(
				"${ctx}/acq/merchantBindSaveCheck",
				{
					acq_merchant_no : $("#acq_merchant_no").html(),
					acq_terminal_no : $("#acq_terminal_no").html(),
					merchant_no : $("#merchant_no").val(),
					terminal_no : $("#terminal_no").val()
				},
				function(data)
				{
					

					if(data >= 0)
					{
						if(data == 0)
						{
							 if(!confirm("输入的商户号，终端号记录已存在，是否覆盖?"))
							 {
								 return;
							 }

						}

						//执行更新或插入操作
						 $.post(
								 "${ctx}/acq/merchantBindSave",
									{	
									 	acq_enname : $("#acq_enname").val(),
										acq_merchant_no : $("#acq_merchant_no").html(),
										acq_terminal_no : $("#acq_terminal_no").html(),
										merchant_no : $("#merchant_no").val(),
										terminal_no : $("#terminal_no").val()
									},
									function(resp)
									{
										if(resp == 1 )
										{
											alert("保存成功");
											api.close();
										}
										else
										{
											alert(resp);
											return;
										}
									}
						 );
						 //执行更新或插入操作
						

					}
					else
					{
						if(data == -1)
						{
							alert("商户号不存在");
							return;
						}
						else if(data == -2)
						{
							alert("终端号不存在");
							return;
						}
						else if(data == -3)
						{
							alert("终端号不属于此商户号商户");
							return;
						}
						else if(data == -4)
						{
							alert("该关系已经绑定，无需重复绑定");
							return;
						}
						else if(data == -5)
						{
							alert("该商户为实名商户，不允许绑定");
							return;
						}

						else if(data == -6)
						{
							alert("普通商户与收单机构商户，不属于同一代理商");
							return;
						}

						
							
					}
				}
			);
			
		});
	});
</script>
</head>
<body>
	<div id="main">
	<ul  style="padding:20px;font-size:13px">
		 <li><label>收单机构：</label>
			 <span>
				    <c:if test="${params.acq_enname eq 'eptok' }">银盛</c:if>
					<c:if test="${params.acq_enname eq 'tftpay'}">腾付通</c:if>
			</span>
		 </li>
		 <input type="hidden" id="acq_enname" value="${params.acq_enname}">
		<li><label>收单机构商户号：</label><span id="acq_merchant_no">${params.acq_merchant_no}</span></li>
		<li><label>收单机构终端号：</label><span id="acq_terminal_no">${params.acq_terminal_no}</span></li>
		<li><label>商户号：</label><input id="merchant_no" name="merchant_no" value=""/><label class="must">*</label></li>
		<li><label>终端号：</label><input id="terminal_no" name="terminal_no" value=""/></li>
	</ul>
	<input  style="margin-left:20px"   class="button rosy medium" type="submit" id="submit"  value="绑定"/>
	</div>
</body>
</html>