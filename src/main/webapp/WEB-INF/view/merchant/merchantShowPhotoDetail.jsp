<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<style>
.tupian_box {
	text-align: center;
}
.tupian_box img{
	width:288px;
	margin: 0 auto;	
}
#search{
	margin-top:2px;
}
#picList{
	margin-top:-2px;
	margin-left:-15px;
}
</style>
<script type="text/javascript">
	function getMerchantIdFail(){
		var api = frameElement.api;
		var W = api.opener;
		var id = $("#mid").val();
		var failMsg = $("#failMsg").val();
		if(failMsg.length == 0){
			alert("请输入审核意见!");
			return false;
		}
		if(failMsg.length >= 50){
			alert("复核意见字数超过长度限制!!");
			return false;
		}
		if(id != "" && failMsg !=""){
			$.ajax({
		        cache: true,
		        type: "POST",
		        url:"${ctx}/mer/checkRepeatFail",
		        data:{'id':id,'failMsg':failMsg},
		        async: false,
		        dataType: 'json',
		        error: function(request) {
		            alert("出错了！");
		        },
		        success: function(json){
			  			var statusOpen = json.statusOpen;
				  		if(statusOpen == 1){
				  			alert("操作成功!");
				  			api.close();
				  			W.location.reload();
				  			//window.location.href="${ctx}/mer/checkRepeatQuery";
				  		}else if(statusOpen == 2){
				  			alert("操作失败!");
				  		}else if(statusOpen == 4){
				  			alert("商户已被复审!");
				  		}else if(statusOpen == 5){
				  			alert("商户信息不存在!");
				  		}else if(statusOpen == 3){
				  			alert("数据错误，请刷新界面重试!");
				  		}else if(statusOpen == 6){
				  			alert("系统错误，请重试!");
				  		}else if(statusOpen == 8){
				  			alert("操作失败，系统未找到商户所在集群信息!");
				  		}else if(statusOpen == 7){
				  			alert("操作失败，系统未找到商户机具绑定信息！!");
				  		}else if(statusOpen == 9){
				  			alert("操作失败，系统未找到商户用户信息信息！!");
				  		}else if(statusOpen == 10){
				  			alert("操作失败，系统未查到商户信息！!");
				  		}
			 		}
		    });
		}else{
			alert("数据出现错误");
		}
	}


	function getMerchantIdSucess(){
		var api = frameElement.api;
		var W = api.opener;
		var id = $("#mid").val();
		if(id != ""){
			$.ajax({
		        cache: true,
		        type: "POST",
		        url:"${ctx}/mer/checkRepeatSucess",
		        data:{'id':id},
		        async: false,
		        dataType: 'json',
		        error: function(request) {
		            alert("出错了！");
		        },
		        success: function(json){
			  			var statusOpen = json.statusOpen;
				  		if(statusOpen == 1){
				  			alert("复审成功!");
				  			api.close();
				  			W.location.reload();
				  		}else if(statusOpen == 2){
				  			alert("复审操作失败!");
				  		}else if(statusOpen == 4){
				  			alert("商户已被复审!");
				  		}else if(statusOpen == 5){
				  			alert("商户信息不存在!");
				  		}else if(statusOpen == 3){
				  			alert("数据错误,请刷新界面重试!");
				  		}else if(statusOpen == 6){
				  			alert("系统错误，请重试!");
				  		}
			 		}
		    });
		}else{
			alert("数据出现错误");
		}
	}
</script>
</head>
<body>

<div id="search" style="width: 870px;height: 12px">
<div id="title">商户编号：${params.merchant_no}  &nbsp; &nbsp; &nbsp;  商户名称: ${params.merchant_name} &nbsp; &nbsp; &nbsp; 
	法人：${params.lawyer}  &nbsp; &nbsp; &nbsp; 身份证号：${params.id_card_no}
</div>
</div>
	<input id='mid' name='mid' type="hidden" value="${params.id}">
		<div class="picList tip2" id="picList">
					<c:forTokens items="${params.attachment}" delims="," var="fileName">
						<div >
							<div class="close_btn2" style="display: none;"></div>
							<div data-filename="${fileName}" class="tupian_box"  >
								<c:choose>
									<c:when test="${f:endsWith(fileName,'.zip')}">
										<a href='${fug:fileUrlGen(fileName)}' title="点击下载" target="_blank">
										<img src="${ctx}/images/z_file_zip.png" />
										</a>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.rar')}">
										<a href='${fug:fileUrlGen(fileName)}' title="点击下载" target="_blank">
										<img src="${ctx}/images/z_file_rar.png" />
										</a>
									</c:when>
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
						</div>
					</c:forTokens>
					<div class="clear_fix"></div>
				</div>
 <div class="clear"></div>
 &nbsp; &nbsp;备注信息:<textarea rows="5" cols="80"  name="failMsg" id="failMsg"></textarea>
 <div class="search_btn">
 <input   class="button blue medium" type="submit" id="submit"  onclick="getMerchantIdSucess();" value="复审通过"/>
	<input   class="button blue medium" type="submit" id="submit"  value="复审失败"  onclick="getMerchantIdFail();"/>
 </div>
</body>
</html>