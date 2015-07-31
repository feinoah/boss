<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
<style type="text/css">
.tupian {
	width: 400px;
	height: 380px;
	float: left;
	position: relative;
	padding: 6px 0;
	border: #d3d3d3 1px solid;
	margin-right: 12px;
	margin-bottom: 35px;
	text-align: center;
	margin-top: 10px;
}
.tupian img{
	height: 380px!important;
	width:360px!important;
	
}
</style>
<!--<script type="text/javascript">
$(function() {
		var flag = false;
		var cardNo = "${params['card_no']}";
		<shiro:hasPermission name="FAST_TRANS_CARD_NO">
		flag = true;
		</shiro:hasPermission>
		if(!flag){
			cardNo = "<u:cardcut content="${params['card_no']}" />";
		}
		$("#cardNo").html(cardNo);
});
</script>
--></head>
<body>
<div class="item liHeight">
	<div class="title clear">交易信息 </div>
	<ul>
		<li style="width:220px;"><span>订单编号：</span>${params['order_no']}</li>
		<li style="width:220px;"><span>交易类型：</span>${params['biz_name']}</li>
		<li style="width:200px;"><span>交易金额：</span>${params['amount']}</li>
		<li style="width:220px;"><span>收单机构：</span>
			<c:choose>
				  <c:when test="${params['acq_enname'] eq 'eptok'}">YS</c:when>
				  <c:when test="${params['acq_enname'] eq 'tftpay'}">腾付通</c:when> 
				  <c:when test="${params['acq_enname'] eq 'bill'}">快钱</c:when> 
				  <c:when test="${params['acq_enname'] eq 'zypay'}">中意支付</c:when> 
				  <c:when test="${params['acq_enname'] eq 'yibao'}">易宝</c:when>
				  <c:when test="${params['acq_enname'] eq 'xlink'}">讯联</c:when>
				  <c:when test="${params['acq_enname'] eq 'hypay'}">翰亿</c:when>
				  <c:when test="${params['acq_enname'] eq 'ubs'}">瑞银信</c:when>
				  <c:otherwise>${params['acq_enname']}</c:otherwise>
			</c:choose> 
		</li>
		<li style="width:220px;"><span>交易卡号：</span><shiro:hasPermission name="TRANS_CARD_NO">${params['card_no']}</shiro:hasPermission>
									<shiro:lacksPermission name="TRANS_CARD_NO"><u:cardcut content="${params['card_no']}" /></shiro:lacksPermission></li>
		<li style="width:220px;"><span>卡类型：</span>${params['card_type']}</li>
		<li style="width:220px;"><span>发卡行：</span>${params['bank_name']}</li>
		<li style="width:220px;"><span>卡种：</span>${params['card_name']}</li>
		<li style="width:200px;"><span>持卡人姓名：</span>${params['order_user_name']}</li>
		<shiro:hasPermission name="SMS_MOBILE_VIEW">
			<li style="width:220px;"><span>交易手机号：</span>${params['sms_mobile']}</li>
		</shiro:hasPermission>
		<li style="width:220px;"><span>订单时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
		<li style="width:220px;"><span>交易状态：</span>${params['status']}</li>
		<li style="width:220px;"><span>商户编号：</span>${params['merchant_no']}</li>
		<li style="width:400px;"><span>商户名称：</span>${params['merchant_name']}</li>
		<li style="width:400px;"><span>身份证号：</span>${params['id_card']}</li>
	
		
	</ul>
		<div class="clear"></div>
		<div class="title clear">审核图片 </div>
		<div class="picList tip" id="picList">
			<c:forTokens items="${params['pic_name']}" delims="," var="fileName">
	
				<div class="tupian">
					<div class="close_btn" style="display: none;"></div>
					<div data-filename="${fileName}" class="tupian_box">
						<c:choose>
							<c:when test="${f:endsWith(fileName,'.jpg') or f:endsWith(fileName,'.png')}">
								<a href='${fug:fileUrlGen(fileName)}' target="_blank" title="点击查看">
									<img src='${fug:fileUrlGen(fileName)}' />
								</a>
							</c:when>
						</c:choose>
					</div>
					<div class="process">
						<div class="process_inner"></div>
					</div>
					<div class="filename" style="width: 394px;">
						${fileName}
					</div>
				</div>
			</c:forTokens>
			<div class="clear_fix"></div>
		</div>
		
		<div class="title clear">最近操作</div>
	<ul>
		<li style="width:110px;"><span>时间</span></li>
		<li style="width:110px;"><span>操作内容</span></li>
		<li style="width:110px;"><span>操作人</span></li>
		<li style="width:110px;"><span>冻结类型</span></li>
		<li style="width:110px;"><span>冻结天数</span></li>
		<li style="width:200px;"><span>原因</span></li>
	</ul>
	<c:forEach items="${freezeLogs}" var="item" varStatus="status">
		<ul>
			<li style="width:110px;"><span><fmt:formatDate value="${item.oper_time}" pattern="yyyy-MM-dd" type="both" /></span></li>
			<li style="width:110px;"><span>
				<c:choose>
					<c:when test="${item.oper_type eq '0'}">冻结</c:when>
					<c:when test="${item.oper_type eq '1'}">解冻</c:when>
					<c:otherwise>-</c:otherwise>
				</c:choose>
			</span></li>
			<li style="width:110px;"><span>${item.oper_name}</span></li>
			<li style="width:110px;"><span>
				<c:choose>
					<c:when test="${item.freeze_way eq '0' && item.oper_type eq '0'}">无期</c:when>
					<c:when test="${item.freeze_way eq '1' && item.oper_type eq '0'}">有期</c:when>
					<c:otherwise>-</c:otherwise>
				</c:choose>
			</span></li>
			<li style="width:110px;"><span>${(item.freeze_day ne null && item.freeze_day ne '0') ? item.freeze_day : '-'}</span></li>
			<li style="width:200px;"><span>${item.oper_reason ne null ? item.oper_reason : '-'}</span></li>
		</ul>
	</c:forEach>
	<div class="clear"></div>
</div>

</body>
</html>