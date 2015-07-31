<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@page import="com.eeepay.boss.utils.SysConfig"%>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
.hovers{ background-color:#3368c4; color:fff}
#form_div{width: 100%;border: 0px solid red;}
.ul_addTranLimit{width: 98%;border: 0px solid red;float: right;}
.ul_addTranLimit li{width: 40%;height: 25px;line-height: 25px;padding-top: 5px;float: left;}
.ul_addTranLimit li span{display:-moz-inline-box;display:inline-block;width: 85px;border:0px solid red;}
.must{border:0px solid red;height:23px;line-height:23px;width: 5px;color: red;}
.area {width: 75px;}
#attachment_fileUploader {vertical-align: middle;margin-left: 10px;} 
.merchant_title {padding: 6px 6px 6px 13px;background: none repeat scroll 0% 0% #DFE9F0;margin: 10px auto;font-weight: bold;}
.selecPic {border: 1px solid red;display: inline;color: #fff;background-color: #5cb85c;border-color: #4cae4c;padding: 6px 10px 6px 8px;cursor: pointer;-webkit-user-select: none;user-select: none;border-radius: 4px;}
.selecPic .flag {font-weight: bold;display: inline;font-size: 16px;}
</style>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<script type="text/javascript">

	$(function(){
		var cus = 0;
	    var classname = "";
	    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
	    $("#agentInput").keyup(function(event) {
		    var arry = new Array();
		    $("[name=agentNo]").find("option").each(function(i, n) {
		        arry[i] = $(this).text();
		    });
	        if ((event.keyCode != 38) && (event.keyCode != 40) && (event.keyCode != 13)) {
	            $autocomplete.empty();
	            var $SerTxt = $("#agentInput").val().toLowerCase();
	            if ($SerTxt != "" && $SerTxt != null) {
	                for (var k = 0; k < arry.length; k++) {
	                    if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
	                    	
	                        $("<li title=" + arry[k] + " class=" + classname +" style='background-color: #FFFFFF'></li>").text(arry[k]).appendTo($autocomplete).mouseover(function() {
	                            $(".autocomplete li").removeClass("hovers");
	                            $(this).css({
	                                background: "#3368c4",
	                                color: "#fff"
	                            });
	                        }).mouseout(function() {
	                            $(this).css({
	                                background: "#fff",
	                                color: "#000"
	                            });
	                        }).click(function() {
	                        	var text=$(this).text();
	                            $("#agentInput").val(text);
	                            $("[name=agentNo]").find("option").each(function(i, n) {
	                                if($(this).text()==text){
	                             	   $(this).prop('selected', 'true');
	                                }
	                             });
	                            $autocomplete.hide();
	                        });
	                    }
	                }
	            }
	            $autocomplete.show();
	        }
	        var listsize = $(".autocomplete li").size();
	        $(".autocomplete li").eq(0).addClass("hovers");
	        if (event.keyCode == 38) {
	            if (cus < 1) {
	                cus = listsize - 1;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                $("[name=agent_no]").find("option").each(function(i, n) {
	                   if($(this).text()==text){
	                	   $(this).prop('selected', 'true');
	                   }
	                });
	            } else {
	                cus--;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                    if($(this).text()==text){
	                    	$(this).prop('selected', 'true');
	                    }
	                 });
	            }
	        }
	        if (event.keyCode == 40) {
	            if (cus < (listsize - 1)) {
	                cus++;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                    if($(this).text()==text){
	                    	$(this).prop('selected', 'true');
	                    }
	                 });
	            } else {
	                cus = 0;
	                $(".autocomplete li").removeClass();
	                $(".autocomplete li").eq(cus).addClass("hovers");
	                var text = $(".autocomplete li").eq(cus).text();
	                $("#agentInput").val(text);
	                
	                $("[name=agentNo]").find("option").each(function(i, n) {
	                    if($(this).text()==text){
	                 	   $(this).prop('selected', 'true');
	                    }
	                 });
	            }
	        }
	        if (event.keyCode == 13) {
	            $(".autocomplete li").removeClass();
				$autocomplete.hide();

	        }
	    });
	    
	    $("[name=agentNo]").change(function(){
	    	var agent_Name =  $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
			if(agent_Name != ""){
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
	    	});
	});
	
	function transAdd() {
		var INTEGER_REG =  /^(0|([1-9][0-9]*))$/; //正整数
		var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
		
		var agentNo = $.trim($("#agent-select").val());
		var agentInput = $.trim($("#agentInput").val());
		if(agentNo !== '-1' && agentNo.length<3 && 'default' !== agentInput){
			alert("请选择代理商！");
			return;
		}
		var pos_type = $.trim($("#pos_type").val());
		var merchant_type = $.trim($("#merchant_type").val());
		var real_flag = $.trim($("#real_flag").val());
		var fee_type = $.trim($("#fee_type").val());
		var account_type = $.trim($("#account_type").val());
		
		var ed_max_amount = $.trim($("#ed_max_amount").val());
		if (!ed_max_amount.match(INTEGER_REG) && !ed_max_amount.match(MONEY_REG)) {
			var dialog = $.dialog({
				title : '错误',
				lock : true,
				content : '单日最大交易额,请输入正确的数字！',
				icon : 'error.gif',
				ok : function() {
					$("#ed_max_amount").focus();
				}
			});
			flag = false;
			return false;
		}
		var single_max_amount = $.trim($("#single_max_amount").val());
		if (!single_max_amount.match(INTEGER_REG) && !single_max_amount.match(MONEY_REG)) {
			var dialog = $.dialog({
				title : '错误',
				lock : true,
				content : '单笔最大交易额,请输入正确的数字！',
				icon : 'error.gif',
				ok : function() {
					$("#single_max_amount").focus();
				}
			});
			flag = false;
			return false;
		}
		var ed_card_max_amount = $.trim($("#ed_card_max_amount").val());
		if (!ed_card_max_amount.match(INTEGER_REG) && !ed_card_max_amount.match(MONEY_REG)) {
			var dialog = $.dialog({
				title : '错误',
				lock : true,
				content : '单日单卡最大交易额,请输入正确的数字！',
				icon : 'error.gif',
				ok : function() {
					$("#ed_card_max_amount").focus();
				}
			});
			flag = false;
			return false;
		}
		var ed_card_max_items = $.trim($("#ed_card_max_items").val());
		if (!ed_card_max_items.match(INTEGER_REG)) {
			var dialog = $.dialog({
				title : '错误',
				lock : true,
				content : '单日单卡最大交易笔数,请输入正确的数字！',
				icon : 'error.gif',
				ok : function() {
					$("#ed_card_max_items").focus();
				}
			});
			flag = false;
			return false;
		}
		
		$.dialog.confirm('确定要添加该条记录吗？', function() {
			formSubmit('addTranLimit', null, null, successMsg, null, true);
		});
		
		//新增成功的回调方法
		function successMsg() {
			var dialog = $.dialog({
				title : '提示',
				lock : true,
				content : '代理商交易额度控制录入成功',
				icon : 'success.gif',
				ok : null,
				close : function() {
					location.href = "${ctx}/agent/transRuleList";
				}
			});
		}
	}
	
	function delLimit(id){
		if(confirm("确定要删除当前记录吗?")){
			$.get('${ctx}/back/delLimit?id='+id,function(data){
				alert(data);
			});
		}
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：代理商管理>代理商交易额度控制录入
		</div>
	<!-- 表单开始 -->
		
		<form:form id="addTranLimit" action="${ctx}/agent/addTranRule" method="post">
			<div style="position:absolute;margin-left:75px;margin-top:50px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;width: 125px;border:none;">
         	</div>
         	<div id="search">
			<div id="title">基本信息</div>
			<ul class="ul_addTranLimit">
				<li><span>代理商：</span>
					<u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" style="padding:2px;width:157px"  />
				</li>
				<li><span>设备类型：</span>
					<u:TableSelect sname="pos_type" style="padding:2px;width:157px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
				</li>
				<li><span>商户类型：</span>
					<select id="merchant_type" name="merchant_type" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="5812">餐娱类</option>
						<option value="5111">批发类</option>
						<option value="5541">民生类</option>
						<option value="5331">一般类</option>
						<option value="1520">房车类</option>
						<option value="1011">其他</option>
					</select>
				</li>
				<li><span>是否实名：</span>
					<select name="real_flag" id="real_flag" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="0">否</option>
						<option value="1">是</option>
					</select>
				</li>
				<li><span>扣率类型：</span>
					<select id="fee_type" name="fee_type" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="RATIO">扣率</option>
						<option value="CAPPING">封顶</option>
						<option value="LADDER">阶梯</option>
					</select>
				</li>
				<li><span>账户类型：</span>
					<select name="account_type" id="account_type" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="对公">对公</option>
						<option value="对私">对私</option>
					</select>
				</li>
			</ul>
			<div class="clear"></div>
			<div id="title">限额信息</div>				
			<ul class="ul_addTranLimit">
				<li >
				<span style="width: 160px" class="longspan">单日最大交易额：</span>
				<input style="width: 70px" id="ed_max_amount" name="ed_max_amount" class="input required money" value=""  type="text">（元）
				</li>
				<li >
				<span style="width: 160px" class="longspan">单笔最大交易额：</span>
				<input style="width: 70px" id="single_max_amount" name="single_max_amount" class="input required money" value=""  type="text">（元）
				</li>
				<li >
				<span style="width: 160px" class="longspan">单日单卡最大交易额：</span>
				<input style="width: 70px" id="ed_card_max_amount" name="ed_card_max_amount" class="input required money" value=""  type="text">（元）
				</li>
				<li >
				<span style="width: 160px" class="longspan">单日单卡最大交易笔数：</span>
				<input style="width: 70px" id="ed_card_max_items" name="ed_card_max_items" class="input required integer" value=""  type="text">（笔）
				</li>
			</ul>
			<div class="clear"></div>
			<div class="search_btn clear" style="text-align: left;padding-left:270px;">
				<input class="button blue" type="button" id="submitBut" onclick="transAdd();" value="保存" />
				<input class="button blue" type="button" id="returnBut" onclick="javascript:history.go(-1);" value="返回" />
			</div>
		</div>
		</form:form>
	<!-- 表单结束 -->
	<div class="clear"></div>
	</div>
</body>
