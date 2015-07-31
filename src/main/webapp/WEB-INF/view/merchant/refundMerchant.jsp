<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ctx}/scripts/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#merUpdate
		{
			padding:10px;
		}
	
		
		#merUpdate ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:250px;
			height:32px;
			line-height:32px;
		}
		
		#merUpdate ul li.column2
		{
			width:500px;
		}
		
		#merUpdate ul li.column3
		{
			width:750px;
		}
		
		#merUpdate ul li select
		{
			width:128px;
		}
		
		#merUpdate ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:90px;
		}
		
		#merUpdate ul li label.must		
		{
			display:-moz-inline-box;
			display:inline-block;
			width:5px;
			text-align:center;
			color:red;
		}
		
		#merUpdate ul li label.longLabel
		{
			width:170px;
		}
		
		#merUpdate ul li .area
		{
			width:75px;
		}
		
		
		
		#merUpdate ul li.long
		{
			width:440px;
		}
		
		
		#merUpdate div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
		
		#attachment_fileUploader {
			vertical-align:middle;
			margin-left:10px;
		}
	</style>
	<script type="text/javascript" src="${ctx}/scripts/provinceCity.js" ></script>
	<script type="text/javascript">
		var api = frameElement.api, W = api.opener;
		function refund() {
			if (confirm('确定要退款?')) {
				$.ajax({
							type : "post",
							url : "${ctx}/mer/refundUpdate",
							data : {
								id : $("#refunId").val(),
								reason : $("#reason").val()
							},
							success : function(data) {
								if (data == "1") {
									alert("退款成功");
									api.close();
								} 
							}
						});
			}

		}
	</script>
</head>
<body>
  <div id="content">
   退款原因： 
<input type="text" id="refunId" name="id" readonly="readonly"
				value="${params['id']}" style="display:none"/>
   <textarea rows="" cols="" name="account" id="reason"></textarea>
   <input type="button"  value="提交" onclick="javascript:refund();"  id="refundButton">
  </div>
</body>
