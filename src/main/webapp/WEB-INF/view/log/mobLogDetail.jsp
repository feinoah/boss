<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadJs.jsp"%>
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
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="content">

		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：日志管理>手机日志查询>详情
		</div>
		<form:form id="merUpdate">
			<div class="item">
				<div class="title">请求信息</div>
				<ul>

					<li><label>用户名：</label> ${params.mobReqInfo.user_name}</li>
					<li><label>序列号：</label>${params.mobReqInfo.seq_no}</li>
					<li><label>psam卡号：</label>${params.mobReqInfo.psam_no}</li>
					<li><label>交易名：</label>${params.mobReqInfo.mobReqInfo.trade_id}
					</li>
					<li><label>手机系统：</label>
					
 						 <c:if test="${params.mobReqInfo.platform eq '0' }">android系统</c:if>					
 						 <c:if test="${params.mobReqInfo.platform eq '1' }">ios系统</c:if>					
					
					</li>
					<li><label>系统版本号：：</label>${params.mobReqInfo.client_version}
					</li>
					<li><label>登录手机号：</label>${params.mobReqInfo.login_mobile}
					</li>
					<li><label>创建时间：</label><fmt:formatDate value="${params.mobReqInfo.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
					<li></li>
					<li><label>报文详细内容：</label></li>
					
                    
				</ul>
				<textarea cols="90" rows="12" style="margin-left:15px;">
                            ${params.mobReqInfo.content}
                </textarea>


				<div class="clear"></div>
				<br />
				<div class="title clear">响应信息</div>
				<ul>

					<li><label>用户名：</label> ${params.mobResInfo.user_name}</li>
					<li><label>序列号：</label>${params.mobResInfo.seq_no}</li>
					<li><label>交易名：</label>${params.mobResInfo.trade_id}</li>
					<li><label>交易成功：</label> <c:choose>
							<c:when test="${params.mobResInfo.succeed}">
				成功
				</c:when>
							<c:otherwise>
					失败
				</c:otherwise>
						</c:choose></li>
					<li><label>交易返回码：</label>${params.mobResInfo.err_code}</li>
					<li><label>交易返回信息：</label>${params.mobResInfo.err_msg}</li>
					<li><label>创建时间：</label><fmt:formatDate value="${params.mobReqInfo.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
					<li></li>
					<li></li>
					<li><label>报文详细内容：</label></li>
					


				</ul>
				<textarea cols="90" rows="12" style="margin-left:15px;">
                            ${params.mobResInfo.content}
                </textarea>


				<div class="search_btn clear">
					<input class="button blue" type="button" id="backButton" value="返回"
						onclick="javascript:history.go(-1)" />
				</div>
			</div>
		</form:form>
	</div>
	<div style="display:none">
		<input type="text" id="flag" value="${flag}" /> <input type="text"
			id="errorMessage" value="${errorMessage}" />
	</div>


</body>
