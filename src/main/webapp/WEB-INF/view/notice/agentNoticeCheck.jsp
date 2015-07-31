<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ctx}/scripts/jqueryform.js"></script>
	 <script charset="utf-8" src="${ ctx}/scripts/jsCss/editor/kindeditor.js"></script>
	<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
	<style type="text/css">
#updateAgentNoticeSave {
	padding: 10px;
}

#updateAgentNoticeSave ul {
	overflow: hidden;
}

#updateAgentNoticeSave ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 350px;
	heigth: 32px;
	line-height: 32px;
}

#updateAgentNoticeSave ul li.column2 {
	width: 540px;
}

#updateAgentNoticeSave ul li.column3 {
	width: 810px;
}

#updateAgentNoticeSave ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 100px;
}

#updateAgentNoticeSave ul li label.must {
	width: 5px;
	color: red;
}

#updateAgentNoticeSave ul li .area {
	width: 75px;
}

#updateAgentNoticeSave ul li.long {
	width: 440px;
}

#updateAgentNoticeSave div.subject {
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
			id : 'notice_content',
			allowUpload : true, //允许上传图片
		    imageUploadJson : '${ctx}/servlet/UploadServletForKindeditor' //服务端上传图片处理URI
		});
		$(function(){
			var flag = '${flag}';
			 if(flag == "1")
			{
				   var dialog = $.dialog({title: '成功',lock:true,content: '新增代理商通告'+''+'成功',icon: 'success.gif',ok: function(){
					   window.location.href="${ctx}/agentNoticeSend/agentNoticeQuery";
			    	}});
			}
			else if(flag == "2")
            {
					var dialog = $.dialog({title: '成功',lock:true,content: '更新代理商通告'+''+'成功',icon: 'success.gif',ok: function(){
						window.location.href="${ctx}/agentNoticeSend/agentNoticeQuery";
					}});
			}
			else if(flag == "0")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: "${errorMessage}",icon: 'error.gif',ok: function(){
					$("form input:text").first().focus();
			    	}});
			}

			 addButtonClick = function(){
				   
				    var isSubmit = true;
					var id = $("#id").val();
					var notice_content=$("#notice_content").val();
					var notice_title=$("#notice_title").val();
					
					if(id !=""){
						if(notice_title.length==0){
							var dialog = $.dialog({title: '错误',lock:true,content: '标题不能为空',icon: 'error.gif',ok: function(){
				        		$("#notice_title").focus();
					    	}});
							isSubmit = false;
							return false;
						}
						if(notice_content.length==0){
							var dialog = $.dialog({title: '错误',lock:true,content: '通告内容不能为空',icon: 'error.gif',ok: function(){
				        		$("#notice_content").focus();
					    	}});
							isSubmit = false;
							return false;
						}
						
					}else{
						if(notice_content.length==0){
							var dialog = $.dialog({title: '错误',lock:true,content: '通告内容不能为空',icon: 'error.gif',ok: function(){
				        		$("#notice_content").focus();
					    	}});
							isSubmit = false;
							return false;
						}
					}
				if (isSubmit) {
				    //window.location ='${ctx}/agentNoticeSend/updateAgentNoticeSave';
				    $("#updateAgentNoticeSave").submit();
				}
			 };
		});
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：系统管理>代理商通告下发信息
		</div>
		<form:form id="updateAgentNoticeSave" action="${ctx}/agentNoticeSend/updateAgentNoticeSave" method="post" >
			<div>
				<div class="title">
					通告下发信息：
				</div> 
				<ul>
					<li style="display: none">
						<label>
							id
						</label>
						<input type="text" id="id" name="id" value="${params['id']}" />
					</li>
				</ul>
				<ul>
				    <li  style="width:610px;">
				        <label style="width: 60px;">通告标题 :</label>
				        <input type="text"  id="notice_title" name="notice_title"  style="width: 340px;"
						value="${params['notice_title']}" class="required"  />
						<c:choose>
						   <c:when test="${params['id'] eq null}"></c:when>
						   <c:otherwise>
						   <label class="must">
							*
						   </label>
						   </c:otherwise>
						</c:choose>
				    </li>
				   <li style="width: 750px; height: 350px;">
						<label>
							通告内容 : 
						</label>
						<textarea id="notice_content"   name="notice_content"     cols="100" rows="8"   style="width:700px;height:300px;">  ${params['notice_content']}</textarea>  
						<label class="must">
							*
						</label>
					</li>
				</ul>
				<div class="title">
					附件上传
					<span style="color:red;float: inherit;width: inherit;display: inline;">点击保存按钮，附件会自动开始上传</span>
				</div>
				<div class="tip">
					请上使用zip、rar压缩格式
				</div>
				<div class="picList tip" id="picList">
				<c:forTokens items="${params.attachment}" delims="," var="fileName">
						<div class="tupian">
							<div class="close_btn"></div>
							<div data-filename="${fileName}" class="tupian_box">
								<c:choose>
									<c:when test="${f:endsWith(fileName,'.zip')}">
										<img src="${ctx}/images/z_file_zip.png"/>
										</a>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.rar')}">
										<img src="${ctx}/images/z_file_rar.png"/>
										</a>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.jpg') or f:endsWith(fileName,'.png')}">
										<a target="_blank"">
											<img src=""/>
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
				<div class="tip" style="margin-top: 20px;">
					<div class="selecPic" id="browse" >
						<div class="flag">+</div>
						选择文件
					</div>
				</div>
				<div id="upload_error" class="tip"></div>
				
<%-- 				<div class="title">附件下载：<label  style="color:red;">图片文件可点击查看，压缩文件点击下载</label ></div>
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
				<input type="hidden" id="examinationMark" name="examinationMark" value="" /> --%>
			</div>
				<div class="search_btn clear" style="margin-top: 20px;">
					<input class="button blue " type="button" id="addButton" value="保存"/>
					<input name="reset" class="button blue " type="button" onclick="window.location.href='${ctx}/agentNoticeSend/agentNoticeQuery'" value="返回"/>
				</div>
			</div>
			<input type="hidden"  id="attachment" name="attachment" value="${params.attachment}"/>
		  	<input type="hidden"  id="oldAttachment" name="oldAttachment"  value="${params.attachment}"/>
		</form:form>
		<div style="display: none">
			<input type="text" id="flag" value="${flag}" />
		</div>
	</div>
</body>
