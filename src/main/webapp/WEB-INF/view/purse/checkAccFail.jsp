<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp"%>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
#extractionFailSave {
	padding: 10px;
}

#extractionFailSave ul {
	overflow: hidden;
}

#extractionFailSave ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 300px;
	heigth: 32px;
	line-height: 32px;
}

#extractionFailSave ul li.column2 {
	width: 540px;
}

#extractionFailSave ul li.column3 {
	width: 810px;
}

#extractionFailSave ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 110px;
}

#extractionFailSave ul li label.must {
	width: 5px;
	color: red;
}

#extractionFailSave ul li .area {
	width: 75px;
}

#extractionFailSave ul li.long {
	width: 440px;
}

#extractionFailSave div.subject {
	font-size: 12px;
	font-weight: bold;
	marigin: 20px 4px;
}


</style>
	<script type="text/javascript">

		$(function(){
			var TEL_REG = /^[0-9]{3,4}\-[0-9]{7,8}$/;
			var MOBILE_REG =  /^1[0-9]{10}$/;
			var EMAIL_REG = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

			var NUM_STR_REG = /^([0-9])+$/; //数字字符串

			var INTEGER_REG =  /^(0|([1-9][0-9]*))$/; //正整数
			var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
			
			var EMAIL = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;//email 正则表达式
			
			 var flag = '${flag}';
			 if(flag == "1")
			{
			   var dialog = $.dialog({title: '成功',lock:true,content: '保存成功',icon: 'success.gif',ok: function(){
				   location.href='${ctx}/purse/bagCheckQuery';
		    	}});
			}
			else if(flag == "0")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: "${errorMessage}",icon: 'error.gif',ok: function(){
				$("form input:text").first().focus();
			    }});
			}
			 
		 $("#addButton").click(function(){
			   
			   var check_remark = $.trim($("#check_remark").val());
			   if (check_remark.length === 0) {
					var dialog = $.dialog({
						title : '错误',
						lock : true,
						content : '审核失败原因不能为空',
						icon : 'error.gif',
						ok : function() {
							$("#check_remark").focus();
						}
					});
					return false;
				}
				$.dialog.confirm('确定要保存该审核失败信息吗？', function() {	    
					    $("#extractionFailSave").submit(); 
			    });
				
		   });	 
		});
		   
	</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />
			当前位置：手机钱包管理>调整审核
		</div>
		 <form:form id="extractionFailSave" action="${ctx}/purse/checkAccountFailSave" method="post">
			<div class="item">
				<div class="title">
					审核失败信息
				</div>
				<ul>
					<li style="display: none">
						<label>
							id
						</label>
						<input type="text" id="id" name="id" value="${params['id']}" />
					</li>
					<li style="width: 320px">
						<label>
							用户名：
						</label>
						<input type="text" id="real_name" name="real_name" value="${params['real_name']}" readonly="readonly"/>
					</li>
					<li>
						<label>
							手机号：
						</label>
						<input type="text"  id="mobile_no" name="mobile_no" value="${params['mobile_no']}" readonly="readonly">
					</li>
					<li style="width: 320px">
						<label>
							调账金额：
						</label>
						<input type="text"  id="amount" name="amount" value="${params['amount']}" readonly="readonly">
					</li>			

					<li style="width: 620px; height: 110px;">
						<label>
							审核失败原因:
						</label>
						<textarea rows="4" cols="71" name="check_remark" id="check_remark"
							class="required">${params['check_remark']}</textarea>
						<label class="must">
							*
						</label>
					<li>
				</ul>
				<div class="search_btn clear">
					<input class="button blue " type="button" id="addButton" value="保存"/>
					<input name="reset" class="button blue " type="button"
						onclick="window.location.href='${ctx}/purse/bagCheckQuery'" value="返回"/>
				</div>
			</div>
		</form:form>
	</div>
</body>
