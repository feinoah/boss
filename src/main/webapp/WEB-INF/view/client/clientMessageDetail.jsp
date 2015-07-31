<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
	<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
	<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
    <script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">客户端信息: </div>
				<ul>
					<li style="display: none">
						<span style="width:100px;">id</span>
						<input type="text" id="id" name="id" value="${params['id']}" />
					</li>
					<li style="width:310px;">
						<span style="width:100px;">是否可继续登录：</span>
					    <c:choose>
							<c:when test="${params['is_continue_login'] eq true}">是</c:when>
							<c:when test="${params['is_continue_login'] eq false}">否</c:when>
						</c:choose>
					</li>
					<li style="width:310px;">
						<span style="width:100px;">移小宝有效值：</span>
						<c:choose>
							<c:when test="${params['is_smallbox'] eq true}">有效</c:when>
							<c:when test="${params['is_smallbox'] eq false}">无效</c:when>
						</c:choose>
					</li>
					<li style="width:310px;">
						<span style="width:100px;">点付宝有效值：</span>
						<c:choose>
							<c:when test="${params['is_dot'] eq true}">有效</c:when>
							<c:when test="${params['is_dot'] eq false}">无效</c:when>
						</c:choose>
					</li>
					<li style="width:310px;">
						<span style="width:100px;">商宝有效值：</span>
						<c:choose>
							<c:when test="${params['is_shang_bao'] eq true}">有效</c:when>
							<c:when test="${params['is_shang_bao'] eq false}">无效</c:when>
						</c:choose>
					</li>
					<li style="width:310px;">
						<span style="width:100px;">消息有效值：</span>
						<c:choose>
							<c:when test="${params['is_valid'] eq true}">有效</c:when>
							<c:when test="${params['is_valid'] eq false}">无效</c:when>
						</c:choose>
					</li>
	
					<li style="width:310px;">
						<span style="width:100px;">消息是否删除：</span>
						<c:choose>
							<c:when test="${params['is_delete'] eq true}">已删</c:when>
							<c:when test="${params['is_delete'] eq false}">正常</c:when>
						</c:choose>
					</li>
				</ul>
				<ul>
				   <li style="width:310px;">
						<span style="width:100%">消息内容：</span>
						${params['msg']}
				   <li>
				</ul>
	</div>
</body>
