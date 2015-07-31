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
function channelSet(){
	var INTEGER_REG = /^(0|(([1-9]|[-])[0-9]*))$/; //正整数
	var api = frameElement.api, W = api.opener;
	var id="${channelMap.id}";
	var old_warn_amount = "${channelMap.warn_amount}";
	var add_amount = $.trim($("#add_amount").val());
	var warn_amount = $.trim($("#warn_amount").val());
	if(add_amount==null || add_amount==""){
		alert("请输入新增额度");
		return ;
	}
	if(warn_amount==null || warn_amount==""){
		alert("请输入预警额度");
		return ;
	}
	if (!add_amount.match(INTEGER_REG)) {
		alert("请填写正确的新增额度");
		return ;
	}
	if (!warn_amount.match(INTEGER_REG)) {
		alert("请填写正确的预警额度");
		return ;
	}
	
     $.ajax({
          url: '${ctx}/purse/updateChannel',
          type:"POST",
		  data:{"id":id,"add_amount":add_amount,"warn_amount":warn_amount,"old_warn_amount":old_warn_amount},
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
<body>
<div class="item liHeight">
	<div class="title clear">通道设置 </div>
	<ul>
		  <li style="margin-top: 5px;width: 200px;"><span style="width:70px;">代付通道：</span>
			 ${channelMap.channel_name}
		 </li>
		  <li style="margin-top: 5px;width: 200px;"><span style="width:70px;">剩余额度：</span>
		  	 ${channelMap.remain_amount}
		 </li>
		 <li style="margin-top: 5px;width: 200px;"><span style="width:70px;">预警额度：</span>
		  	 ${channelMap.warn_amount}
		 </li>
		  <li style="margin-top: 5px;" ><span style="width:70px;">新增额度：</span>
		 	 <input type="text" width="70px" id="add_amount">
		 </li>
		 <li style="margin-top: 5px;" ><span style="width:70px;">预警额度：</span>	
		 	 <input type="text" width="70px" id="warn_amount">
		 </li>
		 <li style="margin-top: 5px;">	
		 	 <input onclick="javascript:channelSet();" style="margin-left:20px"   class="button rosy medium" type="button" id="submit"  value="确定"/>
		 </li>		
    </ul>
	<div class="clear"></div>
</div>
</body>
</html>