<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ctx}/scripts/jqueryform.js"></script>
	 <script charset="utf-8" src="${ ctx}/scripts/jsCss/editor/kindeditor.js"></script>
	<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
	<style type="text/css">
#agentNoticeDetail {
	padding: 10px;
}

#agentNoticeDetail ul {
	overflow: hidden;
}

#agentNoticeDetail ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 350px;
	heigth: 32px;
	line-height: 32px;
}

#agentNoticeDetail ul li.column2 {
	width: 540px;
}

#agentNoticeDetail ul li.column3 {
	width: 810px;
}

#agentNoticeDetail ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 100px;
}

#agentNoticeDetail ul li label.must {
	width: 5px;
	color: red;
}

#agentNoticeDetail ul li .area {
	width: 75px;
}

#agentNoticeDetail ul li.long {
	width: 440px;
}

#agentNoticeDetail div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}

#file_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

#mlogo_uploadUploader {
	vertical-align: middle;
	margin-left: 10px;
}

#attachment_fileUploader {
	vertical-align: middle;
	margin-left: 10px;
}

.selecPic {
	border: 1px solid red;
	display: inline;
	color: #fff;
	background-color: #5cb85c;
	border-color: #4cae4c;
	padding: 6px 10px 6px 8px;
	cursor: pointer;
	-webkit-user-select: none;
	user-select: none;
	border-radius: 4px;
}

.selecPic .flag {
	font-weight: bold;
	display: inline;
	font-size: 16px;
}
</style>
	<script type="text/javascript">
		KE.show({
			id : 'notice_content'
		});
		
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：系统管理>代理商通告下发详情
		</div>
		<form:form id="agentNoticeDetail"   method="post" >
			<div>
				<div class="title">
					通告下发详情：
				</div> 
				<ul>
					<li style="display: none">
						<label>
							id
						</label>
						${params['id']}
					</li>
				</ul>
				<ul>
				    <li  style="width:610px;">
				        <label style="width: 60px;">通告标题 :</label>
				        ${params['notice_title']}
				    </li>
				   <li style="width: 750px; height: 350px;">
						<label>
							通告内容 : 
						</label>
						<textarea id="notice_content"   name="notice_content"   cols="100" rows="8"   style="width:700px;height:300px;">  ${params['notice_content']}</textarea>  
						<label class="must">
							*
						</label>
					</li>
				</ul>
				<div class="title">附件下载：<label  style="color:red;">图片文件可点击查看，压缩文件点击下载</label ></div>
				<div class="picList tip" id="picList">
					<c:forTokens items="${params.attachment}" delims="," var="fileName">
						<div class="tupian">
							<div class="close_btn" style="display: none;"></div>
							<div data-filename="${fileName}" class="tupian_box">
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
							<div class="filename">
								${fileName}
							</div>
						</div>
					</c:forTokens>
					<div class="clear_fix"></div>
				</div>
				<div class="clear"></div>
				<input type="hidden" id="examinationMark" name="examinationMark" value="" />
			</div>
				<div class="search_btn clear" style="margin-top: 20px;">
					<input name="reset" class="button blue " type="button" onclick="window.location.href='${ctx}/agentNoticeSend/agentNoticeQuery'" value="返回"/>
				</div>
			</div>
		</form:form>
		<div style="display: none">
			<input type="text" id="flag" value="${flag}" />
		</div>
	</div>
</body>
