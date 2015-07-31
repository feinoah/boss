<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
#merUpdate {
	padding: 10px;
}

#merUpdate ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 250px;
	height: 32px;
	line-height: 32px;
}

#merUpdate ul li.column2 {
	width: 500px;
}

#merUpdate ul li.column3 {
	width: 750px;
}

#merUpdate ul li select {
	width: 128px;
	height: 24px;
}

#merUpdate ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#merUpdate ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#merUpdate ul li label.longLabel {
	width: 170px;
}

#merUpdate ul li .area {
	width: 75px;
	height: 24px;
	vertical-align: top;
}

#merUpdate ul li.long {
	width: 440px;
}

#merUpdate div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#attachment_fileUploader {
	vertical-align: middle;
	margin-left: 10px;
}
.tip{
	padding: 20px;
}
</style>
<script type="text/javascript">
function save(){
	var single_recharge_max_amount = $("#single_recharge_max_amount").val(); //单笔充值限额
	var day_recharge_max_amount = $("#day_recharge_max_amount").val(); //当天充值限额
	var day_increment_max_amount = $("#day_increment_max_amount").val(); //当天增值服务限额
	var day_extraction_max_amount = $("#day_extraction_max_amount").val(); //当天提现限额
	var day_transfer_max_amount = $("#day_transfer_max_amount").val(); //当天转账限额
	var day_recharge_max_amount_count = $("#day_recharge_max_amount_count").val(); //当天充值最大限额数量
	var day_increment_max_amount_count = $("#day_increment_max_amount_count").val(); //当天增值服务最大限额数量
	var day_extraction_max_amount_count = $("#day_extraction_max_amount_count").val();  //当天提现最大限额数量
	var day_transfer_max_amount_count = $("#day_transfer_max_amount_count").val(); //当天转账最大限额数量
	var tone_free_count = $("#tone_free_count").val();  //T+1每天免费提取次数
	//var is_weekend_withdraws = $("#is_weekend_withdraws").val();  //是否周末提取
	var is_tzero = $("#is_tzero").val();//是否允许T+0提现
	var tzero_withdraws_max_amount = $("#tzero_withdraws_max_amount").val();  //T+0提现限额
	var tone_withdraws_max_amount = $("#tone_withdraws_max_amount").val();  //T+1提现限额
	var tzero_fee = $("#tzero_fee").val(); //T+0提现最低手续费
	var withdraws_time_star_short = $("#withdraws_time_star_short").val(); //允许提现开始时间
	var withdraws_time_end_short = $("#withdraws_time_end_short").val();  //允许提现开截止时间
	/* var retention_money = $("#retention_money").val();  //滞留金额 */
	var validateFlaot = /^\d+\.\d+$/;
	var validateInt = /^-?\d+$/;
	if(!validateFlaot.test(single_recharge_max_amount)){
		alert("单笔充值限额格式错误，正确格式为小数!");
		$("#single_recharge_max_amount").focus();
		return false;
	}
	
	/* if(!validateFlaot.test(retention_money)){
		alert("滞留金额格式错误，正确格式为小数!");
		$("#retention_money").focus();
		return false;
	} */
	
	if(!validateFlaot.test(day_recharge_max_amount)){
		alert("当天充值限额格式错误，正确格式为小数!");
		$("#day_recharge_max_amount").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_increment_max_amount)){
		alert("当天增值服务限额格式错误，正确格式为小数!");
		$("#day_increment_max_amount").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_extraction_max_amount)){
		alert("当天提现限额格式错误，正确格式为小数!");
		$("#day_extraction_max_amount").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_transfer_max_amount)){
		alert("当天转账限额格式错误，正确格式为小数!");
		$("#day_transfer_max_amount").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_recharge_max_amount_count)){
		alert("当天充值最大限额数量格式错误，正确格式为小数!");
		$("#day_recharge_max_amount_count").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_increment_max_amount_count)){ 
		alert("当天增值服务最大限额数量格式错误，正确格式为小数!");
		$("#day_increment_max_amount_count").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_extraction_max_amount_count)){ 
		alert("当天提现最大限额数量格式错误，正确格式为小数!"); 
		$("#day_extraction_max_amount_count").focus();
		return false;
	}
	
	if(!validateFlaot.test(day_transfer_max_amount_count)){ 
		alert("当天转账最大限额数量格式错误，正确格式为小数!");
		$("#day_transfer_max_amount_count").focus();
		return false;
	}
	
	if(!validateInt.test(tzero_withdraws_max_amount)){ 
		alert("T+0提现限额格式错误，正确格式为正整数!");
		$("#tzero_withdraws_max_amount").focus();
		return false;
	}
	
	if(!validateInt.test(tone_withdraws_max_amount)){ 
		alert("T+1提现限额格式错误，正确格式为正整数!");
		$("#tone_withdraws_max_amount").focus();
		return false;
	}
	
	if(!validateFlaot.test(tzero_fee)){ 
		alert("T+0提现最低手续费格式错误，正确格式为小数!");
		$("#tzero_fee").focus();
		return false;
	}
	
	if(withdraws_time_star_short >= withdraws_time_end_short){ 
		alert("提现开始时间必须大于截止时间!");
		return false;
	}
	
	if(!validateInt.test(tone_free_count)){ 
		alert("T+1每天免费提取次数格式错误，正确格式为正整数!");
		$("#tone_free_count").focus();
		return false;
	}

	$.ajax({
        cache: true,
        type: "POST",
        url:'${ctx}/purse/bagUserModifySave',
        data:$('#bagUserUpdate').serialize(),
        async: false,
        dataType: 'json',
        error: function(request) {
            alert("出错了！");
        },
        success: function(json) {
        	var modifyCount = json.modifyCount;
	  		if(modifyCount > 0){
	  			alert("修改成功!");
	  			window.location.href="${ctx}/purse/bagLoginQuery";
	  		}else{
	  			alert("修改失败!");
	  		}
        }
    });
	//$("#bagUserUpdate").submit();
}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：钱包用户管理>钱包用户修改
		</div>
		<form:form id="bagUserUpdate" action="${ctx}/purse/bagUserModifySave" method="post">
			<div id="search"  style="height: 550px">
			<div id="title" >钱包用户修改</div>
			<input type="hidden" name="id" value="${params['id']}"	 id="id">
			<ul>
				<li style="width:310px;"><span style="width:80px;">手 机 号 ：</span> ${params['mobile_no']}</li>
				<li style="width:310px;" ><span style="width:90px;">状态：</span>
						<c:if test="${params['status'] eq 0}">未提交</c:if>
						<c:if test="${params['status'] eq 1}">待审核</c:if>
						<c:if test="${params['status'] eq 2}">审核成功</c:if>
						<c:if test="${params['status'] eq 3}">审核失败</c:if>
				</li>
				<li style="width:310px;" ><span style="width:80px;">真实姓名：</span> ${params['real_name']}</li>
				<li style="width:310px;" ><span style="width:90px;">当天余额：</span>${params['balance']}</li>
				<li style="width:310px;" ><span style="width:80px;">历史余额：</span> ${params['balance1']}</li>
				<%-- <li style="width:310px;" ><span style="width:90px;">结算帐户名：</span>${params['settle_account_name']}</li>
				<li style="width:310px;" ><span style="width:80px;">结算帐号：</span>${params['settle_account_no']}</li> --%>
				<li style="width:310px;" ><span style="width:90px;">手续费：</span>${params['fee']}</li>
				<li style="width:310px;" ><span style="width:80px;">身份证号：</span>${params['idcard']}</li>
			</ul>
			<ul>
				<li style="width:310px;" ><span style="width:90px;">来源：</span>${params['source']}</li>
				<li style="width:310px;" ><span style="width:80px;">实名认证：</span> 
						<c:if test="${params['real_name_auth'] eq 0}">未提交</c:if>
						<c:if test="${params['real_name_auth'] eq 1}">待审核</c:if>
						<c:if test="${params['real_name_auth'] eq 2}">审核成功</c:if>
						<c:if test="${params['real_name_auth'] eq 3}">审核失败</c:if>
					</li>
				<li style="width:310px;" ><span style="width:80px;">交易滞留金：</span>${params['retention_money']}</li>
				<li style="width:310px;" ><span style="width:90px;">风险冻结金：</span>${params['manual_retention_money']}</li>
				<li style="width:310px;" ><span style="width:110px;">风险冻结解冻时间：</span><fmt:formatDate value="${params['manual_arrive_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
				<li style="width:310px;"><span style="width:90px">当天充值限额：</span> 
					<input type="text"	name="day_recharge_max_amount" value="${params['day_recharge_max_amount']}" id="day_recharge_max_amount"  maxlength="10"  style="width: 170px">
				</li>
				<li style="width:310px;" ><span	style="width:110px">当天增值服务限额：</span>
					<input type="text"	name="day_increment_max_amount"	value="${params['day_increment_max_amount']}"  maxlength="10"	id="day_increment_max_amount"  style="width: 170px">
				</li>
				<li style="width:310px;" ><span	style="width:90px">当天提现限额：</span> 
					<input type="text"	name="day_extraction_max_amount"	value="${params['day_extraction_max_amount']}"	id="day_extraction_max_amount"  maxlength="10"  style="width: 170px">
				</li>
				<li style="width:310px;"><span	style="width:110px">当天转账限额：</span> 
					<input type="text" name="day_transfer_max_amount"  value="${params['day_transfer_max_amount']}" 	id="day_transfer_max_amount"  maxlength="10"  style="width: 170px">
				</li>
				<li style="width:310px;" ><span style="width:140px">当天充值最大限额数量：</span> 
					<input type="text"	name="day_recharge_max_amount_count"	value="${params['day_recharge_max_amount_count']}"  maxlength="10"	id="day_recharge_max_amount_count">
				</li>
				<li style="width:310px;"><span	style="width:160px">当天增值服务最大限额数量：</span> 
					<input type="text"  name="day_increment_max_amount_count"  value="${params['day_increment_max_amount_count']}"	 maxlength="10"	id="day_increment_max_amount_count">
				</li>
				<li style="width:310px;" ><span	style="width:140px;">当天提现最大限额数量：</span> 
					<input type="text"	name="day_extraction_max_amount_count"	value="${params['day_extraction_max_amount_count']}"	id="day_extraction_max_amount_count">
				</li>
				<li style="width:310px;"><span	style="width:160px">当天转账最大限额数量：</span> 
					<input type="text" name="day_transfer_max_amount_count" 	value="${params['day_transfer_max_amount_count']}" 	id="day_transfer_max_amount_count" maxlength="10">
				</li>
				<li style="width:310px;" ><span style="width:110px">单笔充值限额：</span> 
					<input type="text"	name="single_recharge_max_amount"		value="${params['single_recharge_max_amount']}" maxlength="10"	id="single_recharge_max_amount"  style="width: 150px">
				</li>
				<li style="width:350px;"><span	style="width:160px">T+1每天免费提取次数：</span> 
					<input type="text"  name="tone_free_count" value="${params['tone_free_count']}"  id="tone_free_count"  maxlength="5">
				</li>
				<li style="width:310px;" ><span	style="width:110px">是否周末提取 ：</span>
					<select name="is_weekend_withdraws"  id="is_weekend_withdraws" style="width: 159px;height: 25px">
						<option value="0"  <c:if test="${params['is_weekend_withdraws'] eq 0}">selected = "selected"</c:if>>否</option>
						<option value="1"  <c:if test="${params['is_weekend_withdraws'] eq 1}">selected = "selected"</c:if>>是</option>
					</select>
				</li>
				<li style="width:310px;" ><span	style="width:160px">T+0提现限额：</span>
					<input type="text"  name="tzero_withdraws_max_amount" 	value="${params['tzero_withdraws_max_amount']}" 	id="tzero_withdraws_max_amount"  maxlength="12">
				</li>
				<li style="width:310px;"><span	style="width:110px">T+1提现限额：</span> 
					<input type="text" name="tone_withdraws_max_amount"  value="${params['tone_withdraws_max_amount']}" id="tone_withdraws_max_amount"  style="width: 150px" maxlength="12">
				</li>
				<li style="width:310px;"><span	style="width:160px">T+0提现最低手续费：</span> 
					<input type="text" name="tzero_fee" value="${params['tzero_fee']}" id="tzero_fee" maxlength="10">
				</li>
				<%-- <li style="width:310px;" ><span style="width:110px">允许提现开始时间：</span>
					<input type="text" name="withdraws_time_star_short" value="${params['withdraws_time_star_short']}"  id="withdraws_time_star_short"  style="width: 150px" onClick="WdatePicker({dateFmt:'HH:mm:ss'})" readonly="readonly">
				</li>
				<li style="width:310px;" ><span style="width:110px">允许提现截止时间：</span>
					<input type="text" name="withdraws_time_end_short" value="${params['withdraws_time_end_short']}"  id="withdraws_time_end_short"  style="width: 170px" onClick="WdatePicker({dateFmt:'HH:mm:ss'})" readonly="readonly">
				</li> --%>
				<li style="width:310px;" ><span	style="width:115px">是否允许T+0提现 ：</span>
					<select name="is_tzero"  id="is_tzero" style="width: 153px;height: 25px">
						<option value="0"  <c:if test="${params['is_tzero'] eq 0}">selected = "selected"</c:if>>否</option>
						<option value="1"  <c:if test="${params['is_tzero'] eq 1}">selected = "selected"</c:if>>是</option>
					</select>
				</li>
			</ul>
			</div>
			<div class="clear"></div>
			<div class="clear"></div>
			<div class="search_btn clear">
					<input class="button blue" type="button" id="addButton" value="保存" onclick="save();" />
					<input class="button blue" type="button" id="backButton" value="返回" onclick="javascript:window.location.href='${ctx}/purse/bagLoginQuery'" />
				</div>
		</form:form>
	</div>
</body>
