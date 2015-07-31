<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<head>
<style type="text/css">
#limitChangeCreate {padding: 10px;}
#limitChangeCreate ul li {margin: 0;padding: 0;display: block;float: left;width: 250px;height: 32px;line-height: 32px;}
#limitChangeCreate ul li.column2 {width: 500px;}
#limitChangeCreate ul li.column3 {width: 750px;}
#limitChangeCreate ul li select {width: 128px;height: 24px;}
#limitChangeCreate ul li label {display: -moz-inline-box;display: inline-block;width: 90px;}
#limitChangeCreate ul li label.must {display: -moz-inline-box;display: inline-block;width: 5px;text-align: center;color: red;}
#limitChangeCreate ul li label.longLabel {width: 170px;}
#limitChangeCreate ul li .area {width: 75px;height: 24px;vertical-align: top;}
#limitChangeCreate ul li.long {width: 440px;}
#limitChangeCreate ul li textarea {padding:5px;}
.tip {padding: 20px;}
</style>
	<script type="text/javascript" src="${ctx}/scripts/listPage.js">
</script>
</head>
<body>
	<div id="content">
		<div id="nav"><img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理 &gt;修改限额</div>
		<form id="limitChangeCreate" name="limitChangeCreate"
			action="${ctx}/mer/limitChangeCreate" method="post">
		
			<div id="search" class="item" style="border: none;">
				<shiro:hasPermission name="COMMERCIAL_LIMITCHANGE_AGENT">
				<div class="title">代理商编号</div>
				<ul style="height: 90px;">
					<li style="width:500px;height:90px;">
						<input style="width: 400px" type="text" id="agent_no" name="agent_no" class="input agent" />
						<span style="width:400px;color:#f00;">如果您输入了代理商编号，表示您想修改该代理商下的所有的商户的限额，请谨慎操作。</span>
						<span style="width:400px;color:#f00;">注意：代理商编号和商户编号没有从属关系，系统会分开处理。</span>
					</li>
				</ul>
				</shiro:hasPermission>
			
				<div class="title">商户编号</div>
				<ul style="height: 100px;">
					<li style="width:500px;height:90px;">
						<textarea name="merchant_no" id="merchant_no" cols="50" rows="4" class="merchant"></textarea>
						<span style="width:400px;color:#f00;">请输入您需要修改限额的商户的商户编号，用英文分号";"隔开</span>
					</li>
				</ul>
				<div class="title">商户交易规则信息</div>
				<ul>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日终端最大交易额：</label>
						<input style="width: 70px" type="text" id="ed_max_amount" name="ed_max_amount" class="input money" />(元)
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">终端单笔最大交易额：</label>
						<input style="width: 70px" type="text" id="single_max_amount" name="single_max_amount" class="input money"/>(元)
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日单卡最大交易额：</label>
						<input style="width: 70px" type="text" id="ed_card_max_amount" name="ed_card_max_amount" class="input money"/>(元)
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日终端单卡最大交易笔数：</label>
						<input style="width: 70px" type="text" id="ed_card_max_items"name="ed_card_max_items" class="input integer"/>(笔)
					</li>
				</ul>
				<div class="clear"></div>
				<div class="title">备注</div>
				<ul style="height: 70px;">
					<li>
						<textarea name="remark" id="remark" cols="50" rows="4"></textarea>
					</li>
				</ul>
				<div class="clear"></div>
			</div>

			<div class="clear"></div>

			<div class="search_btn">
				<input class="button blue medium" type="button" id="addNew" name="addNew" value="添加" />
				<input class="button blue medium" type="button" onclick="javascript:history.go(-1);" value="返回" />
			</div>
		</form>
	</div>
	<script>
		$(function(){
			var INTEGER_REG = /^(0|([1-9][0-9]*))$/; //正整数
			var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
			var MERCHANT_REG = /^([\d+;]+)$/g;
		
			$('#addNew').on('click',function(){
				var agent_no = $.trim($('#agent_no').val()),
					merchant_no =  $.trim($('#merchant_no').val()),
					ed_max_amount = $.trim($('#ed_max_amount').val()),
					single_max_amount = $.trim($('#single_max_amount').val()),
					ed_card_max_amount = $.trim($('#ed_card_max_amount').val()),
					ed_card_max_items = $.trim($('#ed_card_max_items').val()),
					canSubmit = true;
					
				<shiro:hasPermission name="COMMERCIAL_LIMITCHANGE_AGENT">
				if(!agent_no && !merchant_no){
					var dialog = $.dialog({title : '错误',lock : true,
						content : '代理商编号或者商户编号必需选填其一',
						icon : 'error.gif'
					});
					return false;
				}
				</shiro:hasPermission>
				
				<shiro:lacksPermission name="COMMERCIAL_LIMITCHANGE_AGENT">
				if(!merchant_no){
					var dialog = $.dialog({title : '错误',lock : true,
						content : '商户编号不能为空',
						icon : 'error.gif'
					});
					return false;
				}
				</shiro:lacksPermission>
				
				if(!ed_max_amount && !single_max_amount && !ed_card_max_amount && !ed_card_max_items){
					var dialog = $.dialog({title : '错误',lock : true,
						content : '商户交易规则信息必需选填其一',
						icon : 'error.gif'
					});
					return false;
				}
					
				
				$.each($("input:text,select,textarea"),function(i, n){

					<shiro:hasPermission name="COMMERCIAL_LIMITCHANGE_AGENT">
					if ($(n).hasClass("agent")){
						var agent = $.trim($(n).val());
						if (agent.length > 0) {
							if (!agent.match(INTEGER_REG)) {
								var dialog = $.dialog({title : '错误',lock : true,
									content : '代理商编号格式出错：正确格式例子为：123457',
									icon : 'error.gif',
									ok : function() {$(n).focus();}
								});
							canSubmit = false;
							return false;
							}
						}
					}
					</shiro:hasPermission>
					
				
					if ($(n).hasClass("merchant")){
						var merchant = $.trim($(n).val());
						if (merchant.length > 0) {
							if (!merchant.match(MERCHANT_REG)) {
								var dialog = $.dialog({title : '错误',lock : true,
									content : '商户编号格式出错：正确格式例子为：123457或者1234567;1234567;或者123467;123456;12345',
									icon : 'error.gif',
									ok : function() {$(n).focus();}
								});
							canSubmit = false;
							return false;
							}
						}
					}
					
					if ($(n).hasClass("money")){
						var money = $.trim($(n).val());
						if (money.length > 0) {
							if (!money.match(INTEGER_REG)&& !money.match(MONEY_REG)) {
								var dialog = $.dialog({title : '错误',lock : true,
									content : $(n).parent().find("label").first().html()+ '格式出错：正确格式例子为：8.88或8',
									icon : 'error.gif',
									ok : function() {$(n).focus();}
								});
							canSubmit = false;
							return false;
							}
						}
					}
					
					if ($(n).hasClass("integer")) {
						var integer = $.trim($(n).val());
						if (integer.length > 0) {
							if (!integer.match(INTEGER_REG)) {
								var dialog = $.dialog({title : '错误',lock : true,
									content : $(n).parent().find("label").first().html()+ '格式出错：正确格式例子为：8',
									icon : 'error.gif',ok : function() {$(n).focus();}
								});
							canSubmit = false;
							return false;
							}
						}
					}
					
				});
				
				if(canSubmit){
					$.dialog.confirm('确定要修改商户交易规则信息吗,请谨慎操作？', function() {
						$('form:first').submit();
					});
				}
			});
		});
	</script>
</body>
