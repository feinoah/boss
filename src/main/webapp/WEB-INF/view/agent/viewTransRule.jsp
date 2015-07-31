<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<script src="/scripts/lhgdialog/lhgcore.lhgdialog.min.js" type="text/javascript"></script>
<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<script>

$(function() {
	$("#submitConfirm").click(function(){
		var api = frameElement.api, 
			W = api.opener,
			url = '${ctx}/agent/transRuleModify',
			INTEGER_REG =  /^(0|([1-9][0-9]*))$/, 			//正整数
			MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/, 		//金额正则表达式
			ed_max_amount = $.trim($("#ed_max_amount").val()),
			single_max_amount = $.trim($("#single_max_amount").val()),
			ed_card_max_amount = $.trim($("#ed_card_max_amount").val()),
			ed_card_max_items = $.trim($("#ed_card_max_items").val()),
			checkedVal = $('input[name="changeWay"]:checked').val(),
			submitFlag = true;
			
			if(checkedVal){
				if(checkedVal === '1'){
					INTEGER_REG =  /^((-)?(0|([1-9][0-9]*)))$/; 	//正负整数
					MONEY_REG = /((-)?([0-9]+\.[0-9]{1,2}))$/; 	//正负数金额正则表达式
				}else{
					INTEGER_REG =  /^(0|([1-9][0-9]*))$/; 			//正整数
					MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; 		//金额正则表达式
				}
				
				if(ed_max_amount.length === 0 && single_max_amount.length === 0 && ed_card_max_amount.length === 0 && ed_card_max_items.length === 0){
					alert('请至少输入一个需要修改的额度类型');
					submitFlag = false;
				}
				
				if (ed_max_amount.length !== 0 && !ed_max_amount.match(INTEGER_REG) && !ed_max_amount.match(MONEY_REG)) {
					alert('单日最大交易额,请输入正确的数字！');
					submitFlag = false;
				}
				
				if (single_max_amount.length !== 0 && !single_max_amount.match(INTEGER_REG) && !single_max_amount.match(MONEY_REG)) {
					alert('单笔最大交易额,请输入正确的数字！');
					submitFlag = false;
				}
				
				if (ed_card_max_amount.length !== 0 && !ed_card_max_amount.match(INTEGER_REG) && !ed_card_max_amount.match(MONEY_REG)) {
					alert('单日单卡最大交易额,请输入正确的数字！');
					submitFlag = false;
				}
				
				if (ed_card_max_items.length !== 0 && !ed_card_max_items.match(INTEGER_REG)) {
					alert('单日单卡最大交易笔数,请输入正确的数字！');
					submitFlag = false;
				}
				
				if(submitFlag){
					$.post(url ,$('#transRuleForm').serialize(),function(data){
						api.close();
					});
				}
			}else{
				alert('请选择一个额度类型！');
			}
			
	});
	
});

</script>
</head>
<body>
	<form id="transRuleForm" name="transRuleForm" >
	<ul style="padding:20px;font-size:13px;">
		<li>
			<input type="hidden" name="ids" id="ids" value="${ids}" />
			<input type="radio" name="changeWay" id="baseWay" value="0" /><label for="baseWay">规则额度</label>
			<input type="radio" name="changeWay" id="specWay" value="1" style="margin-left:20px;"/><label for="specWay">存量代理商额度</label>
			<input type="radio" name="changeWay" id="allWay" value="2" style="margin-left:20px;"/><label for="allWay">所有额度</label>
		</li>
		<br/>
		<li style="margin-top:8px;"><span style="width: 150px;display:inline-block;*display:inline;*zoom:1;">单日最大交易额：</span>
			<input style="width: 150px" id="ed_max_amount" name="ed_max_amount" class="input required money" value=""  type="text">（元）
		</li>
		<li style="margin-top:8px;"><span style="width: 150px;display:inline-block;*display:inline;*zoom:1;">单笔最大交易额：</span>
			<input style="width: 150px" id="single_max_amount" name="single_max_amount" class="input required money" value=""  type="text">（元）
		</li>
		<li style="margin-top:8px;"><span style="width: 150px;display:inline-block;*display:inline;*zoom:1;">单日单卡最大交易额：</span>
			<input style="width: 150px" id="ed_card_max_amount" name="ed_card_max_amount" class="input required money" value=""  type="text">（元）
		</li>
		<li style="margin-top:8px;"><span style="width: 150px;display:inline-block;*display:inline;*zoom:1;">单日单卡最大交易笔数：</span>
			<input style="width: 150px" id="ed_card_max_items" name="ed_card_max_items" class="input required integer" value=""  type="text">（笔）
		</li>
	</ul>
	<input style="margin-left:20px" class="button rosy medium" type="button" id="submitConfirm"  value="确定" />
	</form>
</body>
</html>