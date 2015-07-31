	<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
#formcss {
	padding: 10px;
}

#formcss ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 250px;
	height: 32px;
	line-height: 32px;
}

#formcss ul li.column2 {
	width: 500px;
}

#formcss ul li.column3 {
	width: 750px;
}

#formcss ul li select {
	width: 128px;
}

#formcss ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#formcss ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#formcss ul li label.longLabel {
	width: 170px;
}

#formcss ul li .area {
	width: 75px;
}

#formcss ul li.long {
	width: 440px;
}

#formcss div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#attachment_fileUploader {
	vertical-align: middle;
	margin-left: 10px;
}

.tip {
	padding: 20px;
}

.black_overlay{  display: none;  position: fixed;  top: 0%;  left: 0%;  width: 100%;  height: 100%;  background-color: black;  z-index:1004;  -moz-opacity: 0.8;  opacity:.80;  filter: alpha(opacity=80);  }  
.white_content {  display: none;  position: fixed;  top: 25%;  left: 35%;  width: 30%;  height: 25%;  padding: 15px;  border: 1px solid #FFFFFF;  background-color: #FFFFFF;  z-index:1005;  overflow: auto;  }
</style>
<script type="text/javascript">
var checkFlag = 0;
var id,mobileNo,appTye;
function submitSuccess(id,mobileNo,appType){
	 $.dialog.confirm('确定审核通过？', function(){
	     $.ajax({
	          url: "${ctx}/purse/bagTzeroCheckResult",
	          type:"POST",
			  data:"id="+id+"&mobileNo="+mobileNo+"&appType="+appType+"&checkStatus=1&basicOrRichData=0&layout=no",
	          error:function()
                   {
                       alert("网络异常");
                   },
               success: function(result){
 	        	  if(result=='1'){
 	        		  alert("审核通过操作成功");
 	        		 document.getElementById("basicNotice").innerHTML = "(申请资料状态：审核通过)";
 	        		 document.getElementById("submitSuccessButton").disabled = true;
 	        		 document.getElementById("checkSuccessImage").style.display="block";
 	        	  }else{
 	        		  alert("系统异常");
 	        	  }
		    	  /* ico="success.gif";
		    	  msg = "审核成功";
	      	   		var dialog = $.dialog({title: '提示',lock:true,content: msg,icon: ico,ok:null ,close:function(){
					location.href="${ctx}/purse/bagLoginQuery?mobile_no="+mobile_no+"&real_name="+real_name;
				}}); */
	           	 
	           }
	         });
	   });
}

function submitFail(){
	document.getElementById("light").style.display='block';
	document.getElementById("fade").style.display='block';

}

function closeWin(){
	document.getElementById("light").style.display='none';
	document.getElementById("light2").style.display='none';
	document.getElementById("fade").style.display='none';
}

function sure(id,mobileNo,appType){
	document.getElementById("light").style.display='none';
	document.getElementById("fade").style.display='none';
	var opinion = document.getElementById("opinionContent").value;
	opinion = opinion.replace(/[\r\n]/g, "");
	if(opinion==""){
		alert("审核意见不能为空");
	}else{
		$.ajax({
	        url: "${ctx}/purse/bagTzeroCheckResult",
	        type:"POST",
			data:"id="+id+"&mobileNo="+mobileNo+"&appType="+appType+"&checkOpinion="+opinion+"&checkStatus=2&basicOrRichData=0&layout=no",
	        error:function()
	             {
	                 alert("网络异常");
	             },
	         success: function(result){
	       	  if(result=='1'){
	       		  alert("审核失败操作成功");
	       		document.getElementById("basicNotice").innerHTML = "(申请资料状态：审核不通过)";
	       		document.getElementById("submitSuccessButton").disabled = false;
	       		document.getElementById("checkSuccessImage").style.display="none";
	       	  }else{
	       		  alert("系统异常");
	       	  }
	         	 
	         }
	       });
	}
}

var statusId,buttonId,dataType;
function access(id,statusId,buttonId,dataType,mobileNo,appType){
	$.dialog.confirm('确定审核通过？', function(){
		$.ajax({
	        url: "${ctx}/purse/bagTzeroCheckResult",
	        type:"POST",
			data:"id="+id+"&mobileNo="+mobileNo+"&appType="+appType+"&checkStatus=1&basicOrRichData=1&dataType="+dataType+"&layout=no",
	        error:function()
	             {
	                 alert("网络异常");
	             },
	         success: function(result){
	       	  if(result=='1'){
	       		  alert("审核通过操作成功");
	       		  var statusIdText = document.getElementById(statusId).innerHTML;
	       		  document.getElementById(statusId).innerHTML = "(审核通过)";
	       		  document.getElementById(buttonId).style.display="none";
	       		  if(statusIdText.indexOf('(未审核)')>-1){
	       			checkFlag = checkFlag+1;
	       		  }
	       	  }else{
	       		  alert("系统异常");
	       	  }
	         	 
	         }
	      });
	});
}

var richId,richDataType,richButtonId,richStatusId;
var batch = new Date().getTime().toString().substring(5,10)+Math.round(Math.random()*90000+10000).toString();
function noAccess(id,dataType,statusId,buttonId,mobileNo,appType){
	document.getElementById("light2").style.display='block';
	document.getElementById("fade").style.display='block';
	richId = id;
	richStatusId = statusId; 
	richDataType = dataType;
	/* $.dialog.confirm('确定审核不通过？', function(){
		$.ajax({
	        url: "${ctx}/purse/bagTzeroCheckResult",
	        type:"POST",
			data:"id="+id+"&mobileNo="+mobileNo+"&appType="+appType+"&checkStatus=2&basicOrRichData=1&layout=no",
	        error:function()
	             {
	                 alert("网络异常");
	             },
	         success: function(result){
	       	  if(result=='1'){
	       		  alert("审核不通过操作成功");
	       		  var statusIdText = document.getElementById(statusId).innerHTML;
	       		  document.getElementById(statusId).innerHTML = "(审核不通过)";
	       		  if(statusIdText.indexOf('(未审核)')>-1){
	       			checkFlag = checkFlag+1;
	       		  }
	       	  }else{
	       		  alert("系统异常");
	       	  }
	         	 
	         }
	      });
	}); */
}

 function richSure(mobileNo,appType){
	document.getElementById("light2").style.display='none';
	document.getElementById("fade").style.display='none';	
	var richOpinion = document.getElementById("richOpinionContent").value;
	richOpinion = richOpinion.replace(/[\r\n]/g, "");
	if(richOpinion==""){
		alert("审核意见不能为空");
	}else{
		$.dialog.confirm('确定审核不通过？', function(){
			$.ajax({
		        url: "${ctx}/purse/bagTzeroCheckResult",
		        type:"POST",
				data:"id="+richId+"&richDataType="+richDataType+"&mobileNo="+mobileNo+"&appType="+appType+"&richOpinion="+richOpinion+"&batch="+batch+"&checkStatus=2&basicOrRichData=1&layout=no",
		        error:function()
		             {
		                 alert("网络异常");
		             },
		         success: function(result){
		       	  if(result=='1'){
		       		  alert("审核不通过操作成功");
		       		  var statusIdText = document.getElementById(richStatusId).innerHTML;
		       		  document.getElementById(richStatusId).innerHTML = "(审核不通过)";
		       		  if(statusIdText.indexOf('(未审核)')>-1){
		       			checkFlag = checkFlag+1;
		       		  }
		       	  }else{
		       		  alert("系统异常");
		       	  }
		         	 
		         }
		      });
		});
	} 
}

$(function() {
	var defaultProvince = '${basicMap.check_status}';
	if(defaultProvince=='1'){
		document.getElementById("checkSuccessImage").style.display="block";
	}
})

//离开页面时触发
 window.onbeforeunload = function() { 
	var unCheckNum = "${unCheckNum}";
	if(checkFlag!=0 && checkFlag!=unCheckNum){
		var left = unCheckNum-checkFlag;
		//alert("还有"+left+"个项目未审，请操作");
		return "还有"+left+"个项目未审，请操作";
		/* if(window.confirm("还有"+left+"个项目未审，请操作")){
			return true;
		}else{ return false;} */
		//event.returnValue="还有"+left+"个项目未审，请操作"; 
	}
}  

</script>
</head>
<body>
	<div id="content">

		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：手机钱包管理&gt;提升额度审核
		</div>
		<div >
			<p style="color: red;font-weight: bold;">&nbsp;&nbsp;&nbsp;&nbsp;${note}</p>
		</div>
		<form:form id="formcss">
			<div class="item">
				<div class="title">商户基本信息</div>

				<c:set var="date1">
					<fmt:formatDate value="${basicMap.create_time}" pattern="yyyy-MM-dd" type="date" />
				</c:set>
				<c:set var="date2">2014-01-14</c:set>
				<c:set var="date3">2014-04-01</c:set>
				<ul>
					<li 
					<c:if test="${f:length(basicMap.merchant_name) >= 12 }">
						style="width: auto;"
					</c:if>
					<c:if test="${f:length(basicMap.merchant_name) < 12 }">
					    style="white-space:nowrap;overflow:hidden; text-overflow: ellipsis; "
					</c:if>
					>
						<label>商户名称：</label>
						${basicMap.merchant_name}&nbsp;
					</li>

					<li>
						<label>商户编号：</label>
						${basicMap.merchant_no}
					</li>
					<li>
						<label>商户类型：</label>
						<c:if test="${basicMap.merchant_type eq '5812' }">餐娱类</c:if>
						<c:if test="${basicMap.merchant_type eq '5111' }">批发类</c:if>
						<c:if test="${basicMap.merchant_type eq '5541' }">民生类</c:if>
						<c:if test="${basicMap.merchant_type eq '5331' }">一般类</c:if>
						<c:if test="${basicMap.merchant_type eq '1520' }">房车类</c:if>
						<c:if test="${basicMap.merchant_type eq '1011' }">其他</c:if>
					</li>

					<li>
						<label style="width: 100px;">商户主营业务：</label>
						${basicMap.main_business}
					</li>
					<li>
						<label style="width: 100px;">法人身份证：</label>
						${basicMap.id_card_no}
					</li>
					<li>
						<label>电话：</label>
						${basicMap.phone}
					</li>
                    <li>
						<label>Email：</label>
						${basicMap.email}
					</li>
 					<li>
						<label>企业法人：</label>
						${basicMap.applicant_name}
					</li>
 					<%-- <li>
						<label>婚姻状况：</label>
						<c:choose>
							<c:when test="${basicMap['is_marriage'] eq '0'}">已婚</c:when>
							<c:when test="${basicMap['is_marriage'] eq '1'}">未婚</c:when>
						</c:choose>
					</li> --%>
					<li 
					<c:if test="${f:length(basicMap.address) >= 12 }">
						style="width: auto;"
					</c:if>
					<c:if test="${f:length(basicMap.address) < 12 }">
					    style="white-space:nowrap;overflow:hidden; text-overflow: ellipsis; "
					</c:if>
					>经营地址：</label>
							${basicMap.address}&nbsp;
					</li>
					<%-- <li>
						<label>受教育程度：</label>
						<c:choose>
							<c:when test="${basicMap['education'] eq '0'}">小学</c:when>
							<c:when test="${basicMap['education'] eq '1'}">初中</c:when>
							<c:when test="${basicMap['education'] eq '2'}">高中</c:when>
							<c:when test="${basicMap['education'] eq '3'}">中专</c:when>
							<c:when test="${basicMap['education'] eq '4'}">大专</c:when>
							<c:when test="${basicMap['education'] eq '5'}">本科</c:when>
							<c:when test="${basicMap['education'] eq '6'}">硕士研究生</c:when>
							<c:when test="${basicMap['education'] eq '7'}">博士研究生</c:when>
						</c:choose>
					</li>
					<li >
						<label>其他联系人：</label>
						${basicMap.other_name}
					</li>
					<li >
						<label style="width: 100px;">其他联系人关系：</label>
						${basicMap.other_ship}
					</li>
					<li >
						<label style="width: 100px;">其他联系人电话：</label>
						${basicMap.other_phone}
					</li> --%>
				</ul>
				<%-- <div class="clear"></div>
				<br />
				<div class="title">商户选填资料</div>
				<ul>
					<li>
						<label>亲属名称：</label>
						${basicMap.relatives_name}
					</li>
					<li >
						<label>亲属关系：</label>
						${basicMap.relatives_ship}
					</li>
					<li class="">
						<label>亲属联系电话：</label>
						${basicMap.relatives_phone}
					</li>
					<li 
					<c:if test="${f:length(basicMap.applicant_company_name) >= 12 }">
						style="width: 360px;"
					</c:if>
					<c:if test="${f:length(basicMap.applicant_company_name) < 12 }">
					    style="white-space:nowrap;overflow:hidden; text-overflow: ellipsis; "
					</c:if>
					>
						<label style="width: 120px;">申请人所在公司名字：</label>
						${basicMap.applicant_company_name}
					</li>
					<li 
					<c:if test="${f:length(basicMap.applicant_company_job) >= 12 }">
						style="width: 350px;"
					</c:if>
					<c:if test="${f:length(basicMap.applicant_company_job) < 12 }">
					    style="white-space:nowrap;overflow:hidden; text-overflow: ellipsis; "
					</c:if>
					>
						<label style="width: 100px;">申请人公司职务：</label>
						${basicMap.applicant_company_job}
					</li>
					<li >
						<label style="width: 100px;">申请人公司电话：</label>
						${basicMap.applicant_company_phone}
					</li>
					<li 
					<c:if test="${f:length(basicMap.applicant_company_business) >= 12 }">
						style="width: 400px;"
					</c:if>
					<c:if test="${f:length(basicMap.applicant_company_business) < 12 }">
					    style="white-space:nowrap;overflow:hidden; text-overflow: ellipsis; "
					</c:if>
					>
						<label style="width: 100px;">申请人公司行业：</label>
						${basicMap.applicant_company_business}
					</li>
					<!-- <li  style="width: 300px;"> -->
					<li 
					<c:if test="${f:length(basicMap.applicant_company_address) >= 12 }">
						style="width: 300px;"
					</c:if>
					<c:if test="${f:length(basicMap.applicant_company_address) < 12 }">
					    style="white-space:nowrap;overflow:hidden; text-overflow: ellipsis; "
					</c:if>
					>
						<label style="width: 100px;">申请人公司地址：</label>
						${basicMap.applicant_company_address}&nbsp;
					</li>
					<li >
						<label>一般刷卡用途：</label>
						${basicMap.brush_use}
					</li>
				</ul> --%>
				<div class="clear"></div>
				<br />
				<div id="checkSuccessImage" style="position:absolute;padding-left: 500px;display: none;">
					<img alt="" src="${ ctx}/images/checkSuccess.png">
				</div>
				<div class="title" >申请资料附件</div>
				<div class="picList tip" id="picList">
					<c:forTokens items="${basicMap.basic_attachment}" delims=";" var="fileName">
					<div style="float: left;">
						<div style="width: 140px;text-align: center;">
							<%-- <c:if test="${fn:contains(fileName, 'image1')}">手持信用卡照片</c:if>
							<c:if test="${fn:contains(fileName, 'image2')}">社保或公积金证明</c:if>
							<c:if test="${fn:contains(fileName, 'image3')}">社保或公积金照片</c:if> --%>
							<c:choose>
								<c:when test="${basicMap['data_type'] eq '0'}">通讯录</c:when>
								<c:when test="${basicMap['data_type'] eq '1'}">户口本、结婚证</c:when>
								<c:when test="${basicMap['data_type'] eq '2'}">学历证明</c:when>
								<c:when test="${basicMap['data_type'] eq '3'}">公司股东证明</c:when>
								<c:when test="${basicMap['data_type'] eq '4'}">个人完税证明</c:when>
								<c:when test="${basicMap['data_type'] eq '5'}">信用报告</c:when>
								<c:when test="${basicMap['data_type'] eq '6'}">大额存款单.股票.债券等有价证券证明</c:when>
								<c:when test="${basicMap['data_type'] eq '7'}">车辆证明</c:when>
								<c:when test="${basicMap['data_type'] eq '8'}">房产证明</c:when>
								<c:when test="${basicMap['data_type'] eq '9'}">更多</c:when>
								<c:when test="${basicMap['data_type'] eq '10'}">手持信用卡照片</c:when>
								<c:when test="${basicMap['data_type'] eq '11'}">社保或公积金证明</c:when>
								<c:when test="${basicMap['data_type'] eq '12'}">营业执照</c:when>
							</c:choose>
						</div>
						<div class="tupian">
							<div class="close_btn" style="display: none;"></div>
							<div data-filename="${fileName}" class="tupian_box">
								<c:choose>
									<c:when test="${f:endsWith(fileName,'.zip')}">
										<c:if test="${date3 < basicMap.create_time}">
											<a href='${fug:bagFileUrlGen(basicMap.phone,fileName)}' title="点击下载" target="_blank">
											<img src="${ctx}/images/z_file_zip.png" />
											</a>
										</c:if>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.rar')}">
										<c:if test="${date3 < basicMap.create_time}">
											<a href='${fug:bagFileUrlGen(basicMap.phone,fileName)}' title="点击下载" target="_blank">
												<img src="${ctx}/images/z_file_rar.png" />
											</a>
										</c:if>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.jpg') or f:endsWith(fileName,'.png')}">
										<a href='${fug:bagFileUrlGen(basicMap.phone,fileName)}' target="_blank" title="点击查看">
											<img src='${fug:bagFileUrlGen(basicMap.phone,fileName)}' alt="商户附件" />
										</a>
									</c:when>
								</c:choose>

							</div>
							<div class="process">
								<div class="process_inner"></div>
							</div>
							<div class="filename">
								${fileName}
							</div>
						</div>
					</div>
					</c:forTokens>
					<div class="clear_fix"></div>
				</div>
				<div class="clear"></div>
				<div class="search_btn" style="height: 50px;">
					<c:choose>
						<c:when test="${basicMap['check_status'] eq '0'}"><p style="color: red;" id="basicNotice">(申请资料状态：未审核)</p></c:when>
						<c:when test="${basicMap['check_status'] eq '1'}"><p style="color: red;" id="basicNotice">(申请资料状态：审核通过)</p></c:when>
						<c:when test="${basicMap['check_status'] eq '2'}"><p style="color: red;" id="basicNotice">(申请资料状态：审核不通过)</p></c:when>
					</c:choose>
					<c:if test="${basicMap['check_status'] eq '1'}">
						<input class="button blue medium" id="submitSuccessButton" type="button"   value="审核通过"  style="cursor:default;" disabled="disabled"/>
					</c:if>
					<c:if test="${basicMap['check_status'] != '1'}">
						<input class="button blue medium" id="submitSuccessButton" type="button" onclick="javascript:submitSuccess(${basicMap.id},${basicMap.mobile_no},${basicMap.app_type});"  value="审核通过" />
					</c:if>
					<input class="button blue medium" type="button" onclick="javascript:submitFail();"  value="审核失败" />
					<input name="reset" class="button blue medium" type="button" style="width: 92px;" onclick="javascript:history.go(-1);" value="返回" />
				</div>
				<c:if test="${notice eq 1}">
					<br>
					<div class="title">申请资料审核失败历史记录</div>
					<div class="tbdata" >
	      				<table width="100%" cellspacing="0" class="t2">
				        <thead>
				          <tr>
				          <th width="10%">序号</th>
				          <th width="15%" >审核人</th>  
				          <th width="30%">审核失败原因</th>     
				          <th width="15%">审核时间</th>
				        </tr>
				        </thead>
	          			<c:forEach items="${checkFailHisList}" var="checkFailHisList" varStatus="status">
		        		<tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
		          			<td class="center"><span class="center">${status.count}</span></td>
		          			<td class="center"  style="word-break: break-all ;">${checkFailHisList.checker}</td>
					        <td class="center"  style="word-break: break-all ;">${checkFailHisList.check_opinion}</td>
					        <td class="center"  style="word-break: break-all ;">${checkFailHisList.check_time}</td>
					    </tr>
		          		</c:forEach>
		         		 </table>
		            </div>
				</c:if>
				<div class="clear"></div>
				<c:if test="${richNotice eq 1}">
					<br>
					<div class="title">提额资料审核失败历史记录</div>
					<div class="tbdata" >
	      				<table width="100%" cellspacing="0" class="t2">
				        <thead>
				          <tr>
				          <th width="10%">序号</th>
				          <th width="10%" >审核人</th>  
				          <th width="25%" >失败项目</th>  
				          <th width="40%">审核失败原因</th>     
				          <th width="15%">审核时间</th>
				        </tr>
				        </thead>
	          			<c:forEach items="${richCheckFailHisList}" var="richFailMap" varStatus="status">
		        		<tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
		          			<td class="center"><span class="center">${status.count}</span></td>
		          			<td class="center"  style="word-break: break-all ;">${richFailMap.checker}</td>
		          			<td class="center"  style="word-break: break-all ;">
		          				<c:choose>
									<c:when test="${richFailMap['data_type'] eq '0'}">通讯录</c:when>
									<c:when test="${richFailMap['data_type'] eq '1'}">户口本、结婚证</c:when>
									<c:when test="${richFailMap['data_type'] eq '2'}">学历证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '3'}">公司股东证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '4'}">个人完税证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '5'}">信用报告</c:when>
									<c:when test="${richFailMap['data_type'] eq '6'}">大额存款单.股票.债券等有价证券证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '7'}">车辆证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '8'}">房产证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '9'}">更多</c:when>
									<c:when test="${richFailMap['data_type'] eq '10'}">手持信用卡照片</c:when>
									<c:when test="${richFailMap['data_type'] eq '11'}">社保或公积金证明</c:when>
									<c:when test="${richFailMap['data_type'] eq '12'}">营业执照</c:when>
								</c:choose>
		          			</td>
					        <td class="center"  style="word-break: break-all ;">${richFailMap.check_opinion}</td>
					        <td class="center"  style="word-break: break-all ;">${richFailMap.check_time}</td>
					    </tr>
		          		</c:forEach>
		         		 </table>
		            </div>
				</c:if>
				<div class="clear"></div>
				<br />
				<div class="title">提额资料附件</div>
				<div class="picList tip" id="picList" style="padding:10px;">
				<c:forEach items="${richList}" var="richMap" varStatus="status">
				<div style="float: left;border: 1px solid;height: 260px;" >
				<br />
					<div align="center">
						<c:choose>
							<c:when test="${richMap['data_type'] eq '0'}">通讯录</c:when>
							<c:when test="${richMap['data_type'] eq '1'}">户口本、结婚证</c:when>
							<c:when test="${richMap['data_type'] eq '2'}">学历证明</c:when>
							<c:when test="${richMap['data_type'] eq '3'}">公司股东证明</c:when>
							<c:when test="${richMap['data_type'] eq '4'}">个人完税证明</c:when>
							<c:when test="${richMap['data_type'] eq '5'}">信用报告</c:when>
							<c:when test="${richMap['data_type'] eq '6'}">大额存款单.股票.债券等有价证券证明</c:when>
							<c:when test="${richMap['data_type'] eq '7'}">车辆证明</c:when>
							<c:when test="${richMap['data_type'] eq '8'}">房产证明</c:when>
							<c:when test="${richMap['data_type'] eq '9'}">更多</c:when>
							<c:when test="${richMap['data_type'] eq '10'}">手持信用卡照片</c:when>
							<c:when test="${richMap['data_type'] eq '11'}">社保或公积金证明</c:when>
							<c:when test="${richMap['data_type'] eq '12'}">营业执照</c:when>
						</c:choose>
						<p style="color: red;" id="status${status.count}">
							<c:choose>
								<c:when test="${richMap['check_status'] eq '0'}">(未审核)</c:when>
								<c:when test="${richMap['check_status'] eq '1'}">(审核通过)</c:when>
								<c:when test="${richMap['check_status'] eq '2' && richMap['data_type'] ne '9'}">(审核不通过)</c:when>
							</c:choose>
						</p>
					</div>
					<c:forTokens items="${richMap.rich_attachment}" delims=";" var="richFileName">
						<div class="tupian" style="margin-left:18px;">
							<div class="close_btn" style="display: none;"></div>
							<div data-filename="${richFileName}" class="tupian_box">

								<c:choose>
									<c:when test="${f:endsWith(richFileName,'.zip')}">
										<c:if test="${date3 < basicMap.create_time}">
											<a href='${fug:bagFileUrlGen(basicMap.phone,richFileName)}' title="点击下载" target="_blank">
											<img src="${ctx}/images/z_file_zip.png" />
											</a>
										</c:if>
									</c:when>
									<c:when test="${f:endsWith(richFileName,'.rar')}">
										<c:if test="${date3 < basicMap.create_time}">
											<a href='${fug:bagFileUrlGen(basicMap.phone,richFileName)}' title="点击下载" target="_blank">
												<img src="${ctx}/images/z_file_rar.png" />
											</a>
										</c:if>
									</c:when>
									<c:when test="${f:endsWith(richFileName,'.jpg') or f:endsWith(richFileName,'.png')}">
										<a href='${fug:bagFileUrlGen(basicMap.phone,richFileName)}' target="_blank" title="点击查看">
											<img src='${fug:bagFileUrlGen(basicMap.phone,richFileName)}' alt="商户附件" />
										</a>
									</c:when>
								</c:choose>

							</div>
							<div class="process">
								<div class="process_inner"></div>
							</div>
							<div class="filename">
								${richFileName}
							</div>
						</div>
					</c:forTokens>
					<c:if test="${richMap['check_status']!='1'}">
						<div style="margin-top: 165px;height: 10px;" align="center" id="button${status.count}">
							<input type="button" value="通过" onclick="javascript:access(${richMap.id},'status${status.count}','button${status.count}',${richMap.data_type },${basicMap.mobile_no},${basicMap.app_type});" style="cursor: pointer;"/>
							<c:if test="${richMap['data_type']!='9'}">
								<input type="button" value="未通过" onclick="javascript:noAccess(${richMap.id},${richMap.data_type},'status${status.count}','button${status.count}',${basicMap.mobile_no},${basicMap.app_type});" style="cursor: pointer;"/>
							</c:if>
						</div>
					</c:if>
				</div>
				</c:forEach>
				</div>
				<div class="clear_fix"></div>
				</div>
				
				<br />

		</form:form>
		<div id="light" class="white_content">
			申请资料审核意见：
			<br />
			<textarea id="opinionContent" style="width: 100%;height: 70%;border: 2px solid black;"></textarea>
			<br />
			<input type="button"  onclick="javascript:sure(${basicMap.id},${basicMap.mobile_no},${basicMap.app_type});" value="确定" style="cursor:pointer;"/>
			<input type="button"  onclick="javascript:closeWin();" value="返回" style="cursor:pointer;float: right;"/>
		</div>
		<div id="light2" class="white_content">
			提额资料审核意见：
			<br />
			<textarea id="richOpinionContent" style="width: 100%;height: 70%;border: 2px solid black;"></textarea>
			<br />
			<input type="button"  onclick="javascript:richSure(${basicMap.mobile_no},${basicMap.app_type});" value="确定" style="cursor:pointer;"/>
			<input type="button"  onclick="javascript:closeWin();" value="返回" style="cursor:pointer;float: right;"/>
		</div>
		<div id="fade" class="black_overlay"></div>

	</div>
</body>
