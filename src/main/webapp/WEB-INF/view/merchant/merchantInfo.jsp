<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
	height: 24px;
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
	height: 24px;
	vertical-align: top;
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

.tip {
	padding: 20px;
}

.autocomplete {
	list-style-type: none;
	margin: 0px;
	padding: 0px;
	border: #008080 1px solid
}

.autocomplete li {
	font-size: 12px;
	font-family: "Lucida Console", Monaco, monospace;
	font-weight: bold;
	cursor: pointer;
	height: 18px;
	line-height: 18px
}

.autocomplete2 {
	list-style-type: none;
	margin: 0px;
	padding: 0px;
	border: #008080 1px solid
}

.autocomplete2 li {
	font-size: 12px;
	font-family: "Lucida Console", Monaco, monospace;
	font-weight: bold;
	cursor: pointer;
	height: 18px;
	line-height: 18px
}

.hovers {
	background-color: #3368c4;
	color: fff
}
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript">
	$(function() {
		var agentT = $("[name=belong_to_agent]").find("option:selected").text();
		$("#agentInput").val(agentT);
		var cus = 0;
		var classname = "";
		var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
		
		$("#agentInput").keyup(function(event){
			var arry = new Array();
			$("[name=belong_to_agent]").find("option").each(function(i, n) {arry[i] = $(this).text();});
			
			if ((event.keyCode != 38) && (event.keyCode != 40)	&& (event.keyCode != 13)) {
				$autocomplete.empty();
				var $SerTxt = $("#agentInput").val().toLowerCase();
				if ($SerTxt != "" && $SerTxt != null) {
					for (var k = 0; k < arry.length; k++) {
						if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
							$("<li title=" + arry[k] + " class=" + classname +" style='background-color: #FFFFFF'></li>").text(arry[k])
								.appendTo($autocomplete)
								.mouseover(function() {
									$(	".autocomplete li")	.removeClass("hovers");
									$(this).css({background : "#3368c4",color : "#fff"});})
								.mouseout(function(){
									$(this).css({background : "#fff",color : "#000"});})
								.click(function() {
									var text = $(this).text();
									$("#agentInput").val(text);
									$("[name=belong_to_agent]").find("option").each(function(i,	n) {
										if ($(this).text() == text) {
											$(this).prop('selected','true');
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
					$("[name=belong_to_agent]").find("option").each(function(i, n) {
						if ($(this).text() == text) {
							$(this).prop('selected',	'true');
						}
					});
				} else {
					cus--;
					$(".autocomplete li").removeClass();
					$(".autocomplete li").eq(cus).addClass("hovers");
					var text = $(".autocomplete li").eq(cus).text();
					$("#agentInput").val(text);
					$("[name=belong_to_agent]").find("option").each(function(i, n) {
						if ($(this).text() == text) {
							$(this).prop('selected',	'true');
						}
					});
				}
			}
			if (event.keyCode == 40) {
				if (cus < (listsize - 1)) {
					cus++;
					$(".autocomplete li").removeClass();
					$(".autocomplete li").eq(cus).addClass("hovers");
					var text = $(".autocomplete li").eq(cus)	.text();
					$("#agentInput").val(text);
					$("[name=belong_to_agent]").find("option")	.each(function(i, n) {
						if ($(this).text() == text) {
							$(this).prop('selected',	'true');
						}
					});
				} else {
					cus = 0;
					$(".autocomplete li").removeClass();
					$(".autocomplete li").eq(cus).addClass("hovers");
					var text = $(".autocomplete li").eq(cus)	.text();
					$("#agentInput").val(text);
					$("[name=belong_to_agent]")	.find("option").each(function(i, n) {
						if ($(this).text() == text) {
							$(this).prop('selected',	'true');
						}
					});
				}
			}
			if (event.keyCode == 13) {
				$(".autocomplete li").removeClass();$autocomplete.hide();
			}
		});

		$("[name=belong_to_agent]").change(function() {
			var agent_Name = $("[name=belong_to_agent]").find("option:selected").text(); //集群所属所属代理商名称
			if (agent_Name != "") {
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
		});

		//初始省市联动
		var INIT_OPTION = "--请选择--";
		//电话验证
		var TEL_REG = /^[0-9]{3,4}\-[0-9]{7,8}$/;
		var MOBILE_REG = /^1[0-9]{10}$/;
		var EMAIL_REG = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

		var NUM_STR_REG = /^([0-9])+$/; //数字字符串

		var SPACE_REG = /\s+/g; //空格验证
		var ZH_REG = /^[\u4e00-\u9fa5]+$/gi; //中文
		var INTEGER_REG = /^(0|([1-9][0-9]*))$/; //正整数
		var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
		var BANK_STH = /^[·(（）)\u4e00-\u9fa5]+$/gi; // 开户行、开户名要支持小括号不区分中文、英文且支持支持中文的符号点
		var CONTACT = /^[·(（）)\u4e00-\u9fa5]+$/gi; // 企业法人、业务联系人支持中文的符号点要支持小括号不区分中文、英文
		var REG= /^[A-Za-z0-9;]+$/; //英文、数字、分号

		$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");

		$.each(provinceName, function(i, n) {
			$("<option></option>").val(n).text(n).appendTo("#province");
		});
		
		var pos_type = "${params['pos_type']}";
		if(pos_type == '2'){
			$("#app_type2").attr("disabled",false);
			$("#app_type").attr("disabled",true);
			document.getElementById("app_type_li").style.display = 'none';
		}else{
			$("#app_type2").attr("disabled",true);
			$("#app_type").attr("disabled",false);
			document.getElementById("app_type_li").style.display = 'block';
		}
		
		//设备类型联动加载
		//mySettleChange();

		//是否优质商户联动显示是否钱包结算信息
		var my_settle = "${params['my_settle']}";
		settleChange(my_settle);

		//文件上传
		//	uploadFileZip("attachment_file","attachment",false,"no","100","200");

		$("#province").change(function() {
			var province = $("#province").val();
			$("#city").empty();
			$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");
			if (province != "") {
				var provinceIndex = $("#province option:selected").index();
				var cityArray = eval("city" + provinceIndex);
				$.each(cityArray, function(i, n) {
					$("<option></option>").val(n).text(n).appendTo("#city");
				});
			}
		});

		var defaultProvince = '${params.province}';
		var defaultCity = '${params.city}';
		if ($.trim(defaultProvince).length > 0) {
			$("#province").val(defaultProvince);
			$("#province").change();
			if ($.trim(defaultCity).length > 0) {
				$("#city").val(defaultCity);
			}
		}

		var flagCount = "${params.flagCount}";
		if(flagCount == 2){
			alert("您无权修改本商户！");
		}
		
		
		//---------------------------------------------------------------------------init---------------------------------------------------------------------------
		//alert($("#flag").val());

		var flag = $("#flag").val();
		var errorMessage = $("#errorMessage").val();
		if (flag == "1") {

			var content = '更新商户' + '${posMerchant.merchantName }' + '成功';
			if(errorMessage){
				content += "," + errorMessage;
			}
				
			$.dialog({
				title : '成功',
				lock : true,
				content : content,
				icon : 'success.gif',
				ok : function() {
					$("form input:text").first().focus();
					location.href = '${ctx}/mer/merQuery';
				}
			});

		} else if (flag == "0") {
			$.dialog({
				title : '错误',
				lock : true,
				content : "${errorMessage}",
				icon : 'error.gif',
				ok : function() {
					$("form input:text").first().focus();
				}
			});
		}

		//---------------------------------------------------------------------------init---------------------------------------------------------------------------

		addButtonClick = function() {
			bagSettleChange();
			var at = $("#add_type").val(); //1
			var pt = $("#pos_type").val(); //3
			var checkT = true;
			if (at == 1 && pt == 3) { // 当进件方式为 客户端进件 并且设备类型为：移小宝时 则不需要验证 联系人、企业法人、联系电话
				checkT = false;
			}
			//var mobile_password = $.trim($("#mobile_password").val());
			//var mobile_password_confirm = $.trim($("#mobile_password_confirm").val());
			//验证部分
			var merchant_name = $.trim($("#merchant_name").val());
			if (merchant_name.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '商户全称不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#merchant_name").focus();
					}
				});
				return false;
			}
			
			var terminalNoVal = $.trim($("#terminalNo").val());
			if(terminalNoVal != ""){
				if(!terminalNoVal.match(REG)){
					alert("PASM卡号输入错误,请输入英文、数字或为空，多个PSAM卡号以英文分号隔开！");
					return  false;
				}
			}
			if (SPACE_REG.exec(merchant_name)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '商户全称不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#merchant_name").focus();
					}
				});
				return false;
			}

			var merchant_short_name = $.trim($("#merchant_short_name").val());
			if (merchant_short_name.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '商户简称不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#merchant_short_name").focus();
					}
				});
				return false;
			}

			if (SPACE_REG.exec(merchant_short_name)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '商户简称不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#merchant_short_name").focus();
					}
				});
				return false;
			}

			var merchant_type = $.trim($("#merchant_type").val());
			if (merchant_type.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '商户类型不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#merchant_type").focus();
					}
				});
				return false;
			}

			var main_business = $.trim($("#main_business").val());
			if (main_business.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '主营业务不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#main_business").focus();
					}
				});
				return false;
			}

			if (SPACE_REG.exec(main_business)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '主营业务不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#main_business").focus();
					}
				});
				return false;
			}

			var lawyer = $.trim($("#lawyer").val());

			if (checkT) {
				if (lawyer.length === 0) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '企业法人不能为空',
						icon : 'error.gif',
						ok : function() {
							$("#lawyer").focus();
						}
					});
					return false;
				}

				if (SPACE_REG.exec(lawyer)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '企业法人不能有空格',
						icon : 'error.gif',
						ok : function() {
							$("#lawyer").focus();
						}
					});
					return false;
				}

				if (!lawyer.match(CONTACT)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '企业法人只能为中文（可以包含中文“·”）',
						icon : 'error.gif',
						ok : function() {
							$("#lawyer").focus();
						}
					});
					return false;
				}
			} else if (lawyer.length > 0) {
				if (lawyer.length > 6) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '企业法人名称过长',
						icon : 'error.gif',
						ok : function() {
							$("#lawyer").focus();
						}
					});
					return false;
				}

				if (SPACE_REG.exec(lawyer)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '企业法人不能有空格',
						icon : 'error.gif',
						ok : function() {
							$("#lawyer").focus();
						}
					});
					return false;
				}

				if (!lawyer.match(CONTACT)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '企业法人只能为中文（可以包含中文“·”）',
						icon : 'error.gif',
						ok : function() {
							$("#lawyer").focus();
						}
					});
					return false;
				}
			}

			var province = $.trim($("#province").val());
			if (province.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '经营地址省份不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#province").focus();
					}
				});
				return false;
			}

			var city = $.trim($("#city").val());
			if (city.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '经营地址城市不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#city").focus();
					}
				});
				return false;
			}

			var address = $.trim($("#address").val());
			if (address.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '经营地址不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#address").focus();
					}
				});
				return false;
			}

			var address = $.trim($("#address").val());
			if (SPACE_REG.exec(address)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '经营地址不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#address").focus();
					}
				});
				return false;
			}

			var link_name = $.trim($("#link_name").val());
			if (checkT) {
				if (link_name.length === 0) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '业务联系人不能为空',
						icon : 'error.gif',
						ok : function() {
							$("#link_name").focus();
						}
					});
					return false;
				}

				if (SPACE_REG.exec(link_name)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '业务联系人不能有空格',
						icon : 'error.gif',
						ok : function() {
							$("#link_name").focus();
						}
					});
					return false;
				}

				if (!link_name.match(CONTACT)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '业务联系人只能为中文（可以包含中文“·”）',
						icon : 'error.gif',
						ok : function() {
							$("#link_name").focus();
						}
					});
					return false;
				}
			} else if (link_name.length > 0) {
				if (link_name.length > 6) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '业务联系人名称过长',
						icon : 'error.gif',
						ok : function() {
							$("#link_name").focus();
						}
					});
					return false;
				}

				if (SPACE_REG.exec(link_name)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '业务联系人不能有空格',
						icon : 'error.gif',
						ok : function() {
							$("#link_name").focus();
						}
					});
					return false;
				}

				if (!link_name.match(CONTACT)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '业务联系人只能为中文（可以包含中文“·”）',
						icon : 'error.gif',
						ok : function() {
							$("#link_name").focus();
						}
					});
					return false;
				}
			}
			var phone = $.trim($("#phone").val());
			if (checkT) {
				if (SPACE_REG.exec(phone)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '电话不能有空格',
						icon : 'error.gif',
						ok : function() {
							$("#phone").focus();
						}
					});
					return false;
				}

				if (phone.length === 0) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '电话不能为空',
						icon : 'error.gif',
						ok : function() {
							$("#phone").focus();
						}
					});
					return false;
				} else {
					if (!phone.match(TEL_REG) && !phone.match(MOBILE_REG)) {
						$
								.dialog({
									title : '错误',
									lock : true,
									content : '电话格式出错：正确格式例子为：0755-12345678或13912346789',
									icon : 'error.gif',
									ok : function() {
										$("#phone").focus();
									}
								});
						return false;
					}
				}

			} else if (phone.length > 0) {
				if (phone.length > 18) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '电话超过长度限制',
						icon : 'error.gif',
						ok : function() {
							$("#phone").focus();
						}
					});
					return false;
				}

				if (SPACE_REG.exec(phone)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '电话不能有空格',
						icon : 'error.gif',
						ok : function() {
							$("#phone").focus();
						}
					});
					return false;
				}

				if (!phone.match(TEL_REG) && !phone.match(MOBILE_REG)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '电话格式出错：正确格式例子为：0755-12345678或13912346789',
						icon : 'error.gif',
						ok : function() {
							$("#phone").focus();
						}
					});
					return false;
				}
			}

			var email = $.trim($("#email").val());
			if (email.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : 'Email不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#email").focus();
					}
				});
				return false;
			} else {

				if (!email.match(EMAIL_REG)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : 'Email格式出错：正确格式例子为：kh@eeepay.cn',
						icon : 'error.gif',
						ok : function() {
							$("#email").focus();
						}
					});
					return false;
				}
			}

			//移付宝销售 
			var sale_name = $.trim($("#sale_name").val());
			if(at == 0){ //进件方式为网站进件时则验证移付宝销售不允许为空
				if (sale_name.length === 0) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '所属销售不能为空',
						icon : 'error.gif',
						ok : function() {
							$("#sale_name").focus();
						}
					});
					return false;
				}
				
					if (SPACE_REG.exec(sale_name)) {
						$.dialog({
							title : '错误',
							lock : true,
							content : '所属销售不能有空格',
							icon : 'error.gif',
							ok : function() {
								$("#sale_name").focus();
							}
						});
						return false;
					}
				if (!sale_name.match(ZH_REG)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '所属销售只能为中文',
						icon : 'error.gif',
						ok : function() {
							$("#sale_name").focus();
						}
					});
					return false;
				}
			}
			
			
			//密码暗语 code_word
			var code_word = $.trim($("#code_word").val());
			if (code_word.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '密码暗语不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#code_word").focus();
					}
				});
				return false;
			}

			//法人身份证号 id_card_no
			var id_card_no = $.trim($("#id_card_no").val());

			if (SPACE_REG.exec(id_card_no)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '法人身份证号不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#id_card_no").focus();
					}
				});
				return false;
			}

			if (id_card_no.length === 0) {
				$.dialog.alert("法人身份证号不能为空");
				$("#id_card_no").focus();
				return false;
			} else {
				if (id_card_no.length > 18) {
					$.dialog.alert("法人身份证号不合法");
					$("#id_card_no").focus();
					return false;
				}
			}

			//商户机具数量  terminal_count
			var terminal_count = $.trim($("#terminal_count").val());
			if (terminal_count.length === 0) {
				$.dialog.alert("商户机具数量不能为空");
				$("#terminal_count").focus();
				return false;
			} else {
				if (!$.isNumeric(terminal_count)) {
					$.dialog.alert("商户机具数量不合法");
					$("#terminal_count").focus();
					return false;
				}
			}

			/*
			var rate1 = $.trim($("#rate1").val());
			if(rate1.length === 0)
			{
				$.dialog({title: '错误',lock:true,content: '签约扣率不能为空',icon: 'error.gif',ok: function(){
			        	$("#rate1").focus();
				    }
				});

				return false;
			}

			else
			{
				
				if(!$.isNumeric(rate1))
				{
					$.dialog({title: '错误',lock:true,content: '签约扣率为数字',icon: 'error.gif',ok: function(){
			        	$("#rate1").focus();
					    }
					});
			
					return false;
				}
			}

			var rate2 = $.trim($("#rate2").val());
			if(rate2.length !== 0 && (!$.isNumeric(rate1)))
			{
				$.dialog({title: '错误',lock:true,content: '签约扣率为空或为数字',icon: 'error.gif',ok: function(){
			        	$("#rate2").focus();
				    }
				});

				return false;
			}
			 */

			//商户状态
			var open_status = $.trim($("#open_status").val());
			if (open_status.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '商户状态不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#open_status").focus();
					}
				});
				return false;
			}

			//手续费类型判断
			var fee_type = $("#fee_type").val();
			if (fee_type == 'RATIO') {
				if ($.trim($("#fee_rate input:text").val()).length == 0) {
					$.dialog.alert("扣率不能为空");
					$("#fee_rate input:text").focus();
					return false;
				}
				
				if (Number($.trim($("#fee_rate input:text").val())) > 100) {
					$.dialog.alert("扣率不能大于100%");
					$("#fee_rate input:text").focus();
					return false;
				}
			} else if (fee_type == 'CAPPING') {
				if ($.trim($("#fee_rate input:text").val()).length == 0) {
					$.dialog.alert("扣率不能为空");
					$("#fee_rate input:text").focus();
					return false;
				}
				if (Number($.trim($("#fee_rate input:text").val())) > 100) {
					$.dialog.alert("扣率不能大于100%");
					$("#fee_rate input:text").focus();
					return false;
				}
				if ($.trim($("#fee_max_amount input:text").val()).length == 0) {
					$.dialog.alert("封顶手续费不能为空");
					$("#fee_max_amount input:text").focus();
					return false;
				}

			} else if (fee_type == 'LADDER') {
				if ($.trim($("#ladder_min").val()).length == 0
						|| $.trim($("#ladder_value").val()).length == 0
						|| $.trim($("#ladder_max").val()).length == 0) {
					$.dialog.alert("阶梯数据不完整");
					$("#fee_ladder input:text").focus();
					return false;
				}
			}

			var mobile_username = $.trim($("#mobile_username").val());
			if (SPACE_REG.exec(mobile_username)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '登录手机号不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#mobile_username").focus();
					}
				});
				return false;
			}

			if (mobile_username.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '登录手机号不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#mobile_username").focus();
					}
				});
				return false;
			} else {
				if (!mobile_username.match(MOBILE_REG)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '登录手机号格式出错：正确格式例子为：13912346789',
						icon : 'error.gif',
						ok : function() {
							$("#mobile_username").focus();
						}
					});
					return false;
				}
			}

			var real_flag = $.trim($("#real_flag").val());
			if (real_flag.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '是否实名不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#real_flag").focus();
					}
				});
				return false;
			}

			var pos_type = $.trim($("#pos_type").val());
			if (pos_type.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '设备类型不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#pos_type").focus();
					}
				});
				return false;
			}

			/* 	var terminalNo = $.trim($("#terminalNo").val());
			if (terminalNo.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : 'PSAM卡号不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#terminalNo").focus();
					}
				});

				return false;
			} 
			
			
			
			if (SPACE_REG.exec(terminalNo)){
				$.dialog({
					title : '错误',
					lock : true,
					content : 'PSAM卡号不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#terminalNo").focus();
					}
				});

				return false;
			}
			 */

			var settle_cycle = $.trim($("#settle_cycle").val());
			if (settle_cycle.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '结算周期不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#settle_cycle").focus();
					}
				});
				return false;
			}

			var account_name = $.trim($("#account_name").val());
			if (account_name.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户名不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#account_name").focus();
					}
				});
				return false;
			}

			if (SPACE_REG.exec(account_name)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户名不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#account_name").focus();
					}
				});
				return false;
			}

			if (!account_name.match(BANK_STH)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户名只能为中文（可以包含中英文括号）',
					icon : 'error.gif',
					ok : function() {
						$("#account_name").focus();
					}
				});
				return false;
			}

			var account_type = $.trim($("#account_type").val());
			if (account_type.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '账户类型不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#account_type").focus();
					}
				});
				return false;
			}

			var bank_name = $.trim($("#bank_name").val());
			if (bank_name.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户银行全称不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#bank_name").focus();
					}
				});
				return false;
			}

			if (SPACE_REG.exec(bank_name)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户银行全称不能有空格',
					icon : 'error.gif',
					ok : function() {
						$("#bank_name").focus();
					}
				});
				return false;
			}

			if (!bank_name.match(BANK_STH)) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户银行全称只能为中文（可以包含中英文括号）',
					icon : 'error.gif',
					ok : function() {
						$("#bank_name").focus();
					}
				});
				return false;
			}

			var account_no = $.trim($("#account_no").val());
			if (account_no.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '开户账号不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#account_no").focus();
					}
				});
				return false;
			} else {
				if (!account_no.match(NUM_STR_REG)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '开户账号格式出错：正确格式例子为：9558812345678901234',
						icon : 'error.gif',
						ok : function() {
							$("#account_no").focus();
						}
					});
					return false;
				}
			}

			var cnaps_no = $.trim($("#cnaps_no").val());
			if (cnaps_no.length === 0) {
				$.dialog({
					title : '错误',
					lock : true,
					content : '联行行号不能为空',
					icon : 'error.gif',
					ok : function() {
						$("#cnaps_no").focus();
					}
				});
				return false;
			} else {
				if (!cnaps_no.match(NUM_STR_REG)) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '联行行号格式出错：正确格式例子为：123456',
						icon : 'error.gif',
						ok : function() {
							$("#cnaps_no").focus();
						}
					});
					return false;
				}
			}

			var isSubmit = true;
			$.each($("input:text,select,textarea"),	function(i, n) {
								if ($(n).hasClass("required")) {
									if ($.trim($(n).val()).length === 0) {
										$.dialog({
											title : '错误',
											lock : true,
											content : $(n).parent().find(
													"label").first().html()
													+ '不能为空',
											icon : 'error.gif',
											ok : function() {
												$(n).focus();
											}
										});
										isSubmit = false;
										return false;
									}
								}

								if ($(n).hasClass("phone")) {
									var phone = $.trim($(n).val());
									if (phone.length > 0) {
										if (!phone.match(TEL_REG)
												&& !phone.match(MOBILE_REG)) {
											$
													.dialog({
														title : '错误',
														lock : true,
														content : $(n).parent()
																.find("label")
																.first().html()
																+ '格式出错：正确格式例子为：0755-12345678或13912346789',
														icon : 'error.gif',
														ok : function() {
															$(n).focus();
														}
													});
											isSubmit = false;
											return false;
										}
									}

								}

								if ($(n).hasClass("money")) {
									var money = $.trim($(n).val());
									if (money.length > 0) {
										if (!money.match(INTEGER_REG)
												&& !money.match(MONEY_REG)) {
											$
													.dialog({
														title : '错误',
														lock : true,
														content : $(n).parent()
																.find("label")
																.first().html()
																+ '格式出错：正确格式例子为：8.88或8',
														icon : 'error.gif',
														ok : function() {
															$(n).focus();
														}
													});
											isSubmit = false;
											return false;
										}
									}
								}

								if ($(n).hasClass("number")) {
									var number = $.trim($(n).val());
									if (number.length > 0) {
										if (!$.isNumeric(number)) {
											$.dialog({
														title : '错误',
														lock : true,
														content : $(n).parent().find("label").first().html()	+ '格式出错：正确格式例子为：8或8.88',
														icon : 'error.gif',
														ok : function() {
															$(n).focus();
														}
													});
											isSubmit = false;
											return false;
										}
									}
								}

								if ($(n).hasClass("integer")) {
									var integer = $.trim($(n).val());
									if (integer.length > 0) {
										if (!integer.match(INTEGER_REG)) {
											$.dialog({
												title : '错误',
												lock : true,
												content : $(n).parent().find("label").first().html()	+ '格式出错：正确格式例子为：8',
												icon : 'error.gif',
												ok : function() {
													$(n).focus();
												}
											});
											isSubmit = false;
											return false;
										}
									}
								}
							});

			if (isSubmit) {
				var attachment = $.trim($("#attachment").val());
				if (attachment.length === 0) {
					$.dialog({
						title : '错误',
						lock : true,
						content : '附件不能为空',
						icon : 'error.gif',
						ok : function() {
							$("#attachment").focus();
						}
					});
					return false;
				}

				//判断设备类型为移小宝时，晚上23点至早上6点期间不进行刷卡交易
				var trans_time_start = $("input[name='trans_time_start']").val();
				var trans_time_end = $("input[name='trans_time_end']").val();
				var tem = parseInt(trans_time_start.substring(0, 2));
				var end = parseInt(trans_time_end.substring(0, 2));
				var posType = $("#pos_type").val();
				if (posType == '3') {
					if (end == 23 || tem < 6) {
						$.dialog.alert("晚上11点至早上6点期间移小宝不进行刷卡交易！");
						return false;
					}
				}
				$.dialog.confirm('确定要修改该商户信息吗？', function() {
					$("#merUpdate").submit();
				});
			}
		};

		$("#fee_type").change(function() {
			var fee_type = $("#fee_type").val();
			if (fee_type == "RATIO") {
				$("#fee_rate").show();
				$("#fee_cap_amount").hide();
				$("#fee_max_amount").hide();
				$("#fee_ladder").hide();

			} else if (fee_type == "CAPPING") {
				$("#fee_rate").show();
				$("#fee_cap_amount").show();
				$("#fee_max_amount").show();
				$("#fee_ladder").hide();
			} else if (fee_type = "LADDER") {
				$("#fee_rate").hide();
				$("#fee_cap_amount").hide();
				$("#fee_max_amount").hide();
				$("#fee_ladder").show();
			}
		});

		$("#fee_type").change();
		var blongAgent = true;
		<shiro:hasPermission name="Select_Agent_name">
		blongAgent = false;
		</shiro:hasPermission>
		if (blongAgent) {
			document.getElementById("agent_name_LI").style.display = 'none';
			document.getElementById("agentInput1DIV").style.display = 'none';
			$("#agentInput2DIV").attr("style", "position:absolute;margin-left:67px;margin-top:-27px;");
			$("#agent")	.attr("onfocus", "this.defaultIndex=this.selectedIndex;");
			$("#agent").attr("onchange","this.selectedIndex=this.defaultIndex;");
			agentLock();
		}else{
			document.getElementById("agent_name_LI").style.display = 'block';
			document.getElementById("agentInput1DIV").style.display = 'block';
			$("#agentInput2DIV").attr("style", "position: absolute; margin-left: 67px; margin-top: -27px;");
			$("#agent")	.attr("onfocus", "");
			$("#agent").attr("onchange","");
			//document.getElementById("agentInput1DIV").style.display = 'block';//隐藏一级代理商搜索输入框  
		}

		$("#agent").change(function() {
			var agentNo = $("#agent").val();
			var agentNo2 = $("#agent").find("option:selected").text(); 
			$("#agentInput1").val(agentNo2);
					$.ajax({
						url : '${ctx}/agent/agentSelect',
						cache : false,
						data : {	'agent_no' : agentNo	},
						type : 'Get',
						dataType : 'json',
						timeout : 20000,
						error : function() {
							alert("出错啦");
						},
						success : function(json) {
							var len = json.length;
							if (len != 0) {
								$("#belong_to_agent").find("option").remove();
								for (var i = 0; i < len; i++) {
									$(	"<option value="+json[i].agent_no+">"+ json[i].agent_name + "</option>").appendTo(	"#belong_to_agent");
									if (i == 0) {
										$("#agentInput").val(json[i].agent_name);
									}
								}
							}
						}
					});
				});
		var appTypeSelect=$("#app_type option[value=${params.app_no}]");
		appTypeSelect.prop("selected",true);
		$("#app_type").on("change",function(){
			var app_no=$(this).children('option:selected').val();
			$("#app_no").val(app_no);
		});
	});

	function mySettleChange() {
		var pos_type = document.getElementById('pos_type').value;
		var ed_max_amount = "${params.ed_max_amount}"; //单日终端最大交易额
		var single_max_amount = "${params.single_max_amount}";//终端单笔最大交易额
		var ed_card_max_amount = "${params.ed_card_max_amount}";//单日单卡最大交易额
		var ed_card_max_items = "${params.ed_card_max_items}";//单日终端单卡最大交易笔数
		var oldpos_type = "${params.pos_type}";//原先的设备类型
		var agentNo = $("#agent").val();
		//alert(pos_type);
		if(pos_type == 2){
			$("#app_type").attr("disabled",true);
			$("#app_type2").attr("disabled",false);
			$("#app_no").val($("#app_type2").val());
			document.getElementById("app_type_li").style.display = 'none';
		}else{
			$("#app_type2").attr("disabled","disabled");
			$("#app_no").val($("#app_type").val());
			document.getElementById("app_type_li").style.display = 'block';
			$("#app_type").attr("disabled",false);
			
		}
		if (agentNo != '3846') {
			$("input[name='trans_time_start']").val("00:00:00");
			$("input[name='trans_time_end']").val("23:59:59");
			if (pos_type == '1') {
				//document.getElementById("my_settle").value = "0";
				document.getElementById("ylst").style.display = "none";
				$("#bagSettle").hide();
				$("#keycode_li").hide();
			} else if (pos_type == '2') {
				//document.getElementById("my_settle").value = "0";
				document.getElementById("ylst").style.display = "none";
				$("#bagSettle").hide();
				$("#keycode_li").hide();
			} else if (pos_type == '3') {
				//document.getElementById("my_settle").value = "1";
				document.getElementById("ylst").style.display = "none";
				$("input[name='trans_time_start']").val("06:00:00");
				$("input[name='trans_time_end']").val("22:59:59");
				$("#bagSettle").show();
				$("#keycode_li").hide();
			} else if (pos_type == '4') {
				//document.getElementById("my_settle").value = "1";
				$("#bagSettle").show();
				$("#keycode_li").show();
				document.getElementById("ylst").style.display = 'block';
				document.getElementById("ed_max_amount").value = "300000.00"; //单日终端最大交易额
				document.getElementById("single_max_amount").value = "20000.00"; //终端单笔最大交易额
				document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
				document.getElementById("ed_card_max_items").value = "2"; //单日终端单卡最大交易笔数
				//重置复选框和单选按钮
				document.getElementById("model1").checked = false;
				document.getElementById("model21").checked = false;
				document.getElementById("model22").checked = false;
				document.getElementById("model3").checked = false;
			} else if (pos_type == '5') {
				//document.getElementById("my_settle").value = "1";
				document.getElementById("ylst").style.display = "none";
				document.getElementById("ed_max_amount").value = "50000.00"; //单日终端最大交易额
				document.getElementById("single_max_amount").value = "20000.00"; //终端单笔最大交易额
				document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
				document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
				$("#bagSettle").show();
				$("#keycode_li").hide();
			}
			//原先默认的情况
			if (pos_type == oldpos_type) {
				document.getElementById("ed_max_amount").value = ed_max_amount; //单日终端最大交易额
				document.getElementById("single_max_amount").value = single_max_amount; //终端单笔最大交易额
				document.getElementById("ed_card_max_amount").value = ed_card_max_amount; //单日单卡最大交易额
				document.getElementById("ed_card_max_items").value = ed_card_max_items; //单日终端单卡最大交易笔数
			}
		} else {
			$("input[name='trans_time_start']").val("00:00:00");
			$("input[name='trans_time_end']").val("23:59:59");
			if (pos_type == '1') {
				$("#keycode_li").hide();
			} else if (pos_type == '2') {
				$("#keycode_li").hide();
			} else if (pos_type == '3') {
				//document.getElementById("my_settle").value = "0";
				$("input[name='trans_time_start']").val("06:00:00");
				$("input[name='trans_time_end']").val("22:59:59");
				$("#keycode_li").hide();
			} else if (pos_type = '4') {
				$("#keycode_li").show();
			}
		}
	}

	function checkPayMethod(op, ck) {
		var final_pay_method = $("#final_pay_method").val();
		if ("b" === op) {
			if (ck.checked) {
				final_pay_method = "1" + final_pay_method.substr(1, 1);
			} else {
				final_pay_method = "0" + final_pay_method.substr(1, 1);
			}
		}
		if ("a" === op) {
			if (ck.checked) {
				final_pay_method = final_pay_method.substr(0, 1) + "1";
			} else {
				final_pay_method = final_pay_method.substr(0, 1) + "0";
			}
		}
		$("#final_pay_method").val(final_pay_method);
	}

	//根据所填的激活码确定是否可用
	function keycodeChange() {
		var keycode = document.getElementById("keycode").value;
		if (keycode != '') {
			$.ajax({
				url : '${ctx}/mer/findKeyCode',
				cache : false,
				data : {
					'keycode' : keycode
				},
				type : 'POST',
				dataType : 'json',
				error : function() {
					alert("激活码出错，请检查数据！");
				},
				success : function(json) {
					if (json.msg == 'noexist') {
						$.dialog.alert("激活码不存在！");

					} else if (json.length != 0 && json.msg == 'available') {
						$.dialog.alert("激活码可用");

					} else if (json.length != 0 && json.msg == 'noavailable') {

						$.dialog.alert("此激活码已被使用，请重新输入！");
					}
				}
			});
		}
	}

	//-----------------------------start--------------------

	function bagSettleChange() {
		//是否钱包结算
		var bag_settle = $("#bag_settle").val();
		if (bag_settle == '1') { //选择是钱包结算，做钱包结算验证
			document.getElementById("trans_cancel").value = '0';
			var mobile_username = $("#mobile_username").val();
			$.ajax({
				url : '${ctx}/mer/bagVerification',
				cache : false,
				data : {'mobile_no' : mobile_username},
				type : 'POST',
				dataType : 'json',
				error : function() {
					alert("此商户信息有误，请检查数据！");
					isSubmit = false;
					return false;
				},
				success : function(date) {
					if (date.flag == 'notExist') {
						//$.dialog.alert("此商户不是钱包用户,不能使用钱包结算！");
						//document.getElementById("bag_settle").value = '0';
						isSubmit = false;
						return false;
					}
				}
			});
		}
	}

	//-----------------------lzj添加----------------------------
	function  agentLock(){
		var pos_type = "${params['pos_type']}";
		var agent_lock = "${params['agent_lock']}";
		if (pos_type == '4') {
			document.getElementById("ylst").style.display = 'block';
		}
		if (agent_lock == '0') {
			document.getElementById("agent_name_LI").style.display = 'none';
			document.getElementById("agentInput1DIV").style.display = 'none';
			$("#agentInput2DIV")	.attr("style", "position:absolute;margin-left:67px;margin-top:-27px;");
			$("#agent")	.attr("onfocus", "this.defaultIndex=this.selectedIndex;");
			$("#agent").attr("onchange","this.selectedIndex=this.defaultIndex;");
			//$("#agent").attr("disabled", true);
		} else if (agent_lock == '1') {
			document.getElementById("agent_name_LI").style.display = 'block';
			document.getElementById("agentInput1DIV").style.display = 'block';
			$("#agentInput2DIV").attr("style", "position: absolute; margin-left: 67px; margin-top: -27px;");
			$("#agent")	.attr("onfocus", "");
			$("#agent").attr("onchange","");
			//$("#agent").attr("disabled", false);
		}
	}

	function model1() {
		if (document.getElementById("model1").checked) {
			document.getElementById("ed_max_amount").value = Number(document
					.getElementById("ed_max_amount").value) + 100000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document
					.getElementById("single_max_amount").value) + 10000.00; //终端单笔最大交易额
		} else {
			document.getElementById("ed_max_amount").value -= 100000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
		}
	}
	function model21() {
		if (document.getElementById("model21").checked) {
			document.getElementById("ed_max_amount").value = Number(document
					.getElementById("ed_max_amount").value) + 100000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document
					.getElementById("single_max_amount").value) + 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = Number(document
					.getElementById("ed_card_max_amount").value) + 10000.00; //单日单卡最大交易额
			$("#model22").attr("disabled", true);
		} else {
			document.getElementById("ed_max_amount").value -= 100000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value -= 10000.00; //单日单卡最大交易额
			$("#model22").attr("disabled", false);
		}
	}
	function model22() {
		if (document.getElementById("model22").checked) {
			document.getElementById("ed_max_amount").value = Number(document
					.getElementById("ed_max_amount").value) + 300000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document
					.getElementById("single_max_amount").value) + 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = Number(document
					.getElementById("ed_card_max_amount").value) + 10000.00; //单日单卡最大交易额
			$("#model21").attr("disabled", true);
		} else {
			document.getElementById("ed_max_amount").value -= 300000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value -= 10000.00; //单日单卡最大交易额
			$("#model21").attr("disabled", false);
		}
	}
	function model3() {
		if (document.getElementById("model3").checked) {
			document.getElementById("ed_max_amount").value = Number(document
					.getElementById("ed_max_amount").value) + 100000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document
					.getElementById("single_max_amount").value) + 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = Number(document
					.getElementById("ed_card_max_amount").value) + 10000.00; //单日单卡最大交易额
		} else {
			document.getElementById("ed_max_amount").value -= 100000.00; //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value -= 10000.00;
		}
	}

	//-----------------------------end------------------------ 

	function settleChange(my_settle) {
		//var my_settle = document.getElementById("my_settle").value;
		if (my_settle == '1') {
			$("#bagSettle").show();
			bagSettleChange();
		} else if (my_settle == '0') {
			document.getElementById("bag_settle").value = "0";
			$("#bagSettle").hide();
		}
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>商户修改
		</div>
		<form:form id="merUpdate" action="${ctx}/mer/merUpdate" method="post">
			<div id="search" class="item" style="border: 0px;">
				<div class="title">基本信息${params.flagCount}</div>
				<c:set var="date1">
					<fmt:formatDate value="${params.create_time}" pattern="yyyy-MM-dd" type="date" />
				</c:set>
				<c:set var="date2">2014-01-14</c:set>
				<ul>
					<li style="display: none">
						<label>流水号</label>
						<input type="text" id="id" name="id" value="${params.id}" />
						<input type="text" id="app_no" name="app_no" value="${params.app_no}" />
					</li>
					<li style="display: none">
						<label>商户编号</label>
						<input type="text" id="merchant_no" name="merchant_no" value="${params.merchant_no}" />
					</li>
					<%-- 		<li style="display:none"><label>代理商编号</label><input type="text" id="agent_no"  name="agent_no" value="${params.agent_no}"/></li> --%>
					<li>
						<label>商户全称：</label>
						<input class="input" type="text" id="merchant_name" name="merchant_name" value="${params.merchant_name}" />
						<label class="must">*</label>
					</li>
					<li>
						<label>商户简称：</label>
						<input class="input" type="text" id="merchant_short_name" name="merchant_short_name" value="${params.merchant_short_name}" />
						<label class="must">*</label>
					</li>
					<li>
						<label>商户类型：</label>
						<select id="merchant_type" name="merchant_type">
								<option value="">--请选择--</option>
								<option value="5300" <c:if test="${params.merchant_type eq '5300' }">selected="selected"</c:if>>航空、加油、超市</option>
								<option value="6300" <c:if test="${params.merchant_type eq '6300' }">selected="selected"</c:if>>保险、公共事业</option>
								<option value="5812" <c:if test="${params.merchant_type eq '5812' }">selected="selected"</c:if>>餐娱类</option>
								<option value="5111" <c:if test="${params.merchant_type eq '5111' }">selected="selected"</c:if>>批发类</option>
								<option value="5541" <c:if test="${params.merchant_type eq '5541' }">selected="selected"</c:if>>民生类</option>
								<option value="5331" <c:if test="${params.merchant_type eq '5331' }">selected="selected"</c:if>>一般类</option>
								<option value="1520" <c:if test="${params.merchant_type eq '1520' }">selected="selected"</c:if>>房车类</option>
								<option value="1011" <c:if test="${params.merchant_type eq '1011' }">selected="selected"</c:if>>其他</option>
						</select>
						<label class="must">*</label>
					</li>
					<li class="column2">
						<label>主营业务：</label>
						<input type="text" style="width: 370px;" value="${params.main_business}" id="main_business" name="main_business" />
						<label class="must">*</label>
					</li>
					<li>
						<label>企业法人：</label>
						<input class="input" type="text" id="lawyer" name="lawyer" value="${params.lawyer}" />
						<label class="must">*</label>
					</li>
					<li class="column2">
						<label>经营地址：</label>
						<select id="province" name="province" class="area"></select><select id="city" name="city" class="area"></select>
						<input type="text" class="input" id="address" style="width: 220px" name="address" value="${params.address}" />
						<label class="must">*</label>
					</li>
					<li>
						<label>业务联系人：</label>
						<input class="input" type="text" id="link_name" name="link_name" value="${params.link_name}" />
						<label class="must">*</label>
					</li>
					<div class="clear"></div>
					<li>
						<label>电话：</label>
						<input class="input" type="text" id="phone" name="phone" value="${params.phone}" />
						<label class="must">*</label>
					</li>
					<li>
						<label>Email：</label>
						<input class="input" type="text" id="email" name="email" value="${params.email}" />
					</li>
					<%--			<li><label>签约扣率：</label><input style="width:43px" class="input" type="text" id="rate1"  name="rate1"  value="${params.rate1}"/>%到<input class="input" type="text"  style="width:43px" id="rate2"  name="rate2"  value="${params.rate2}"/>封顶<label class="must">*</label></li>--%>

					<li>
						<label>移付宝销售：</label>
						<input class="input" type="text" id="self_sale_name" name="self_sale_name" value="${params.self_sale_name}" readonly="readonly" />
					</li>
					<div class="clear"></div>
					<li>
						<label>登录手机号：</label>
						<input class="input" type="text" id="mobile_username" name="mobile_username" value="${params.mobile_username}" />
						<input class="input" type="hidden" id="old_mobile_username" name="old_mobile_username" value="${params.mobile_username}" />
						<label class="must">*</label>
					</li>

					<li>
						<label>是否实名：</label>
						<select name="real_flag" id="real_flag" class="required">
							<option value="">--请选择--</option>
							<option value="0" <c:if test="${params.real_flag == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.real_flag == '1' }">selected="selected"</c:if>>是</option>
						</select>
						<label class="must">*</label>
					</li>
					<li>
						<label>密码暗语：</label>
						<input type="text" value="${params['code_word']}" id="code_word" name="code_word" />
						<label class="must">*</label>
					</li>
					<div class="clear"></div>
					<li class="">
						<label>法人身份证号：</label>
						<input class="input" type="text" id="id_card_no" name="id_card_no" value="${params.id_card_no}" />
						<label class="must">*</label>
					</li>
					<li class="">
						<label>商户机具数量：</label>
						<input class="input" type="text" id="terminal_count" name="terminal_count" value="${params.terminal_count}" />
						<label class="must">*</label>
					</li>
					<li class="">
						<label>商户状态：</label>
						<select id="open_status" name="open_status">
							<option value="">--请选择--</option>
							<option value="1" <c:if test="${params.open_status eq '1' }">selected="selected"</c:if>>正常</option>
							<option value="0" <c:if test="${params.open_status eq '0' }">selected="selected"</c:if>>商户关闭</option>
							<option value="5" <c:if test="${params.open_status eq '5' }">selected="selected"</c:if>>机具绑定</option>
							<option value="2" <c:if test="${params.open_status eq '2' }">selected="selected"</c:if>>待审核</option>
							<option value="3" <c:if test="${params.open_status eq '3' }">selected="selected"</c:if>>审核失败</option>
<!--							<option value="4" <c:if test="${params.open_status eq '4' }">selected="selected"</c:if>>冻结</option>-->
							<option value="6" <c:if test="${params.open_status eq '6' }">selected="selected"</c:if>>初审</option>
						</select>
						<label class="must">*</label>
					</li>
					<div class="clear"></div>
						<li style="display: none;">
						<input id="app_type2"  name="app_type" type="hidden" value="100120">
						</li>
							
							<li class=""  id="app_type_li" >
								<label>客户端类型：</label>
								<select id="app_type" name="app_type">
									<c:forEach var="appPosType" items="${appPosTypes}">
										<option value="${appPosType.app_no}"  <c:out value="${params['app_no'] eq appPosType.app_no ?'selected':'' }"/>>${appPosType.app_name}-${appPosType.agent_name_oem}</option>
									</c:forEach>
								</select>
								<label class="must">*</label>
							</li>
							<li class=""  id="" >
								<label>是否冻结：</label>
								${params['freeze_status'] ne '1' ? '未冻结' : '已冻结'}
							</li>
					<div class="clear"></div>
					<li class="column3">
						<label>扣率类型：</label>
						<select id="fee_type" name="fee_type">
							<option value="RATIO" <c:out value="${params['fee_type'] eq 'RATIO'?'selected':'' }"/>>扣率</option>
							<option value="CAPPING" <c:out value="${params['fee_type'] eq 'CAPPING'?'selected':'' }"/>>封顶</option>
							<option value="LADDER" <c:out value="${params['fee_type'] eq 'LADDER'?'selected':'' }"/>>阶梯</option>
						</select>
					</li>
					<div class="clear"></div>
					<li id="fee_rate" style="">
						<label>扣率：</label>
						<input type="text" class="input number" value="${params['rate1']}" name="rate1" id="rate1" />
						%
					</li>
					<li id="fee_cap_amount" style="">
						<label>封顶金额：</label>
						<input type="text" style="width: 94px;" class="input money readonly" readonly="readonly" value="${params['fee_cap_amount']}"
							id="fee_cap_amount" name="fee_cap_amount" />
						（元）
					</li>
					<li id="fee_max_amount" style="">
						<label>封顶手续费：</label>
						<input type="text" style="width: 94px;" class="input money" value="${params['rate2']}" name="rate2" id="rate2" />
						（元）
					</li>
					<li id="fee_ladder" class="column3">
						<label>阶梯设置：</label>
						<input type="text" class="input number" name="ladder_min" id="ladder_min" value="${params['ladder_min']}" />
						% &lt;
						<input type="text" class="input money" name="ladder_value" id="ladder_value" value="${params['ladder_value']}" />
						（元） &lt;
						<input type="text" class="input number" name="ladder_max" id="ladder_max" value="${params['ladder_max']}" />
						%
					</li>
					<div class="clear"></div>
					<li style="width: 780px;" >
						<label>代理商名称：</label>
						<input type="text" style="width: 230px;" name="agent_name" id="agent_name" value="${params['agent_name']}" readonly="readonly" />
						<div style="position:absolute;margin-left:65px;margin-top:7px;"  id="agentInput1DIV">
			         		<input name="agentInput1" id="agentInput1"  value="全部A"  autocomplete2="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 120px;width: 110px !important;border: none;">
			         	</div>
					</li>
					<div class="clear"></div>
					<%--<shiro:hasPermission name="Select_Agent_name"> </shiro:hasPermission> 
					--%>
					<li id="agent_name_LI">
						<label>代理商名称：</label>
						<u:select value="${params['agent_no']}" stype="agent" sname="agent_no" onlyThowParentAgent="true" otherOptions="onlyInDB" id="agent" />
						<input type="hidden" value="${params['agent_no']}" name="old_agent_no" />
					</li>

					<!--   <li> <label>代理商：</label><u:select value="${params['belong_to_agent']}"  getChildByParentAgentno="${params['agent_no']}"  fieldAsValue="agent_no"  stype="agent" otherOptions="onlyInDB"   id="belong_to_agent"  sname="belong_to_agent"  /> -->
					<li>
						<label>所属代理商：</label>
						<u:select value="${params['belong_to_agent']}" getChildByParentAgentno="${params['agent_no']}" fieldAsValue="agent_no" stype="agent"
							otherOptions="onlyInDB" id="belong_to_agent" sname="belong_to_agent" />
							<div  id="agentInput2DIV">
								<input name="agentInput" id="agentInput" value="全部" autocomplete="off"
									style="position: absolute; top: 2px; left: 31px; height: 10px;width: 110px !important; border: none;">
							</div>
						<input type="hidden" value="${params['belong_to_agent']}" name="old_belong_to_agent" />
					<li>
						<label>设备类型：</label>
<!--						<select id="pos_type" name="pos_type" onchange="mySettleChange()" style="width: 128px; height: 24px; vertical-align: top;">-->
<!--							<option value="">--请选择--</option>-->
<!--							<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>-->
<!--							<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>-->
<!--							<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>-->
<!--							<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>-->
<!--							<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>-->
<!--						</select>-->
						<u:TableSelect sid="pos_type" sname="pos_type" style="width: 128px; height: 24px; vertical-align: top;" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="onlyInDB" onEvent="onchange=\"mySettleChange()\" "/>
					</li>
					<div class="clear"></div>
					<li>
						<label>进件方式：</label>
						<select id="add_type" name="add_type" style="width: 128px; height: 24px; vertical-align: top;">
							<option value="0" <c:out value="${params['add_type'] eq '0'?'selected':'' }"/>>网站进件</option>
							<option value="1" <c:out value="${params['add_type'] eq '1'?'selected':'' }"/>>客户端进件</option>
						</select>
					</li>
					<li>
						<label>所属销售：</label>
						<input class="input" type="text" id="sale_name" name="sale_name" value="${params.sale_name}" />
					</li>
					<li class="column3">
						<label>PSAM卡号：</label>
						<input type="text" class="input" id="terminalNo" style="width: 380px" name="terminalNo" value="${params.terminal_no}" />
						<label class="must">*</label>
						&nbsp;&nbsp;(以;分隔示例：1111111111;22222222)
					</li>
					<li id="keycode_li" class="column3">
						<label>激活码：</label>
						<input value="${params['keycode']}" name="keycode" id="keycode" style="width: 380px;" onchange="keycodeChange()" />
					</li>
					<li class="column2">
						<label>营业执照编号：</label>
						<input type="text" style="width: 160px;" value="${params['bus_license_no']}" name="bus_license_no" />
					</li>
				</ul>
				<div class="clear"></div>
				<br />
				<div class="title">结算信息</div>
				<ul>
					<li>
						<label>结算周期：</label>
						T+<select name="settle_cycle" id="settle_cycle" class="required" style="width: 92px;">
							<%--<option value="">--请选择--</option>
							--%>
							<option value="1" <c:if test="${params.settle_cycle == '1' }">selected="selected"</c:if>>1</option>
							<%--<option value="2" <c:if test="${params.settle_cycle == '2' }">selected="selected"</c:if>>2</option>
							<option value="3" <c:if test="${params.settle_cycle == '3' }">selected="selected"</c:if>>3</option>
							<option value="365" <c:if test="${params.settle_cycle == '365' }">selected="selected"</c:if>>365</option>
							--%>
							<option value="0" <c:if test="${params.settle_cycle == '0' }">selected="selected"</c:if>>0</option>
						</select>&nbsp;天
						<label class="must">*</label>
					</li>
					<li>
						<label>账户类型：</label>
						<select name="account_type" id="account_type">
							<option value="">--请选择--</option>
							<option value="对公" <c:if test="${params.account_type == '对公'}">selected="selected"</c:if>>对公</option>
							<option value="对私" <c:if test="${params.account_type == '对私'}">selected="selected"</c:if>>对私</option>
						</select>
						<label class="must">*</label>
					</li>
					<li>
						<label>开户名：</label>
						<input class="input" type="text" id="account_name" name="account_name" value="${params.account_name}" />
						<input class="input" type="hidden" id="old_account_name" name="old_account_name" value="${params.account_name}" />
						<label class="must">*</label>
					</li>
					<div class="clear"></div>
					<li>
						<label>开户账号：</label>
						<input class="input" type="text" id="account_no" name="account_no" value="${params.account_no}" />
						<label class="must">*</label>
					</li>
					<li>
						<label>开户行全称：</label>
						<input class="input" type="text" id="bank_name" name="bank_name" value="${params.bank_name}" />
						<label class="must">*</label>
					</li>
					<li>
						<label>联行行号：</label>
						<input class="input" type="text" id="cnaps_no" name="cnaps_no" value="${params.cnaps_no}" />
						<label class="must">*</label>
					</li>
				</ul>
				<div class="clear"></div>
				<br />
				<div class="title">交易规则信息</div>
				<div id="ylst" style="display: none">
					<input style="width: 50px;" type="checkbox" name="model1" value="1" id="model1" onclick="javascript:window.model1()">
					经营场所证明
					<input style="width: 50px;" type="checkbox" name="model2" value="21" id="model21" onclick="javascript:window.model21()">
					个体营业执照
					<input style="width: 50px;" type="checkbox" name="model2" value="22" id="model22" onclick="javascript:window.model22()">
					企业营业执照
					<input style="width: 50px;" type="checkbox" name="model3" value="3" id="model3" onclick="javascript:window.model3()">
					店面、店内、收银台照
				</div>
				<ul>
					<li style="width: 390px">
						<label class="longLabel">单日终端最大交易额：</label>
						<input type="text" id="ed_max_amount" name="ed_max_amount" class="input required money" value="${params.ed_max_amount}" />
						（元）
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label class="longLabel">终端单笔最大交易额：</label>
						<input type="text" id="single_max_amount" name="single_max_amount" class="input required money" value="${params.single_max_amount}" />
						（元）
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label class="longLabel">单日单卡最大交易额：</label>
						<input type="text" id="ed_card_max_amount" name="ed_card_max_amount" class="input required money" value="${params.ed_card_max_amount}" />
						（元）
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label class="longLabel">单日终端单卡最大交易笔数：</label>
						<input type="text" id="ed_card_max_items" name="ed_card_max_items" class="input required integer" value="${params.ed_card_max_items}" />
						（笔）
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">允许交易时间：</label>
						<c:choose>
							<c:when test="${params.pos_type=='3'}">
								<input type="text" style="width: 90px" readonly="readonly" name="trans_time_start" value="06:00:00" class="required"
									onClick="WdatePicker({dateFmt:'HH:mm:ss'})">
						        ~
								<input type="text" style="width: 90px" readonly="readonly" name="trans_time_end" value="22:59:59" class="required"
									onClick="WdatePicker({dateFmt:'HH:mm:ss'})">
							</c:when>
							<c:otherwise>
								<input type="text" style="width: 90px" readonly="readonly" name="trans_time_start" value="00:00:00" class="required"
									onClick="WdatePicker({dateFmt:'HH:mm:ss'})">
							    ~
							    <input type="text" style="width: 90px" readonly="readonly" name="trans_time_end" value="23:59:59" class="required"
									onClick="WdatePicker({dateFmt:'HH:mm:ss'})">
							</c:otherwise>
						</c:choose>
					</li>
					<li style="width: 390px">
						<label class="longLabel">是否优质商户：</label>
						<label>
							<c:if test="${params.my_settle == '0' }">否</c:if>
							<c:if test="${params.my_settle == '1' }">是</c:if>
							<input  type="hidden"  id="my_settle"  name="my_settle"  value="${params.my_settle}"/>
						</label>
						<!-- <select name="my_settle" id="my_settle" style="width: 60px; height: 24px; vertical-align: top;" class="required"    onchange="settleChange()">
							<option value="0" <c:if test="${params.my_settle == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.my_settle == '1' }">selected="selected"</c:if>>是</option>
							 <option value="1" <c:if test="${(params.my_settle == '1' || params.pos_type=='3' || params.pos_type=='4' || params.pos_type=='5') && params.agent_no !='3846' || params.agent_no =='4028'}">selected="selected"</c:if>>是</option> 
						</select>-->
					</li>
					<shiro:hasPermission name="MERCHANT_HIGH">
					</shiro:hasPermission>
					<li style="width: 390px">
						<label class="longLabel">手输卡号：</label>
						<select name="clear_card_no" id="clear_card_no" style="width: 60px; height: 24px; vertical-align: top;" class="required">
							<option value="0" <c:if test="${params.clear_card_no == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.clear_card_no == '1' }">selected="selected"</c:if>>是</option>
						</select>
					</li>
					<li style="width: 390px">
						<label class="longLabel">可否撤销交易：</label>
						<select name="trans_cancel" id="trans_cancel" style="width: 60px; height: 24px; vertical-align: top;" class="required">
							<option value="0" <c:if test="${params.trans_cancel == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.trans_cancel == '1' }">selected="selected"</c:if>>是</option>
						</select>
					</li>
					<li style="width: 390px; line-height: 25px;">
						<input type="hidden" value="${params['pay_method']}" id="final_pay_method" name="final_pay_method" />
						<label style="width: 160px;">支付方式:</label>
						<input type="checkbox" value='1' name="pay_method" style="width: 30px; vertical-align: middle; padding: 0px; border-width: 0px;"
							<c:if test="${fn:substring(params['pay_method'], 0, 1) eq '1'}">checked="checked"</c:if> onchange="checkPayMethod('b',this)"
							value="1" readonly="readonly" />
						Pos支付&nbsp;&nbsp;
						<input type="checkbox" value='1' name="pay_method" style="width: 30px; vertical-align: middle; padding: 0px; border-width: 0px;"
							<c:if test="${fn:substring(params['pay_method'], 1, 2) eq '1'}">checked="checked"</c:if> onchange="checkPayMethod('a',this)"
							value="1" readonly="readonly" />
						快捷支付
					</li>
					<li style="width: 390px;" id="bagSettle">
						<label class="longLabel">是否钱包结算：</label>
						<select id="bag_settle" name="bag_settle" style="width: 60px; height: 24px; vertical-align: top;" onchange="bagSettleChange()">
							<option value="0" <c:out value="${params['bag_settle'] eq '0'?'selected':'' }"/>>否</option>
							<option value="1" <c:out value="${params['bag_settle'] eq '1'?'selected':'' }"/>>是</option>
						</select>
					</li>
				</ul>
				<div class="clear"></div>
				<br />
				<div class="title">备注信息</div>
				<ul style="height: 70px;">

					<li>
						<textarea name="remark" id="remark" cols="50" rows="4">${params.remark}</textarea>
					</li>
				</ul>
				<div class="clear"></div>
				<div class="title">附件上传</div>
				<div class="tip">
					请上传合法有效的证件，包括身份证、结算账号银行卡、营业执照，保证图片清晰，使用zip，rar格式进行压缩后上传。

					<a href="#">样例下载</a>
				</div>
				<!--  
		<input type="file" style="margin-top:20px;" id="attachment"  name="attachment"  value="${params.attachment}"/>
		  -->
				<div class="picList tip" id="picList">
					<c:forTokens items="${params.attachment}" delims="," var="fileName">

						<div class="tupian">
							<div class="close_btn"></div>
							<div data-filename="${fileName}" class="tupian_box">

								<c:choose>
									<c:when test="${f:endsWith(fileName,'.zip')}">
										<a href='${fug:fileUrlGen(fileName)}' target="_blank">
											<img src="${ctx}/images/z_file_zip.png" />
										</a>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.rar')}">
										<a href='${fug:fileUrlGen(fileName)}' target="_blank">
											<img src="${ctx}/images/z_file_rar.png" />
										</a>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.jpg') or f:endsWith(fileName,'.png')}">
										<a href="${fug:fileUrlGen(fileName)}" target="_blank" title="点击查看">
											<img src='${fug:fileUrlGen(fileName)}' />
										</a>
									</c:when>
								</c:choose>

							</div>
							<div class="process">
								<div class="process_inner"></div>
							</div>
							<div class="filename">${fileName}</div>
						</div>
					</c:forTokens>
					<div class="clear_fix"></div>
				</div>
				<div class="tip">
					<div class="selecPic" id="browse">
						<div class="flag">+</div>
						选择文件
					</div>
					<!-- 	<div class="picListError">文件大小错误</div> -->
				</div>
				<div id="upload_error" class="tip"></div>
				<div class="search_btn clear">
				<c:if test="${params.flagCount ne '2' }">
						<input class="button blue" type="button" id="addButton" value="保存" />
				</c:if>
					<input class="button blue" type="button" id="backButton" value="返回" onclick="javascript:window.location.href='${ctx}/mer/merQuery'" />
				</div>
			</div>
			<input type="hidden" id="attachment" name="attachment" value="${params.attachment}" />
			<input type="hidden" id="oldAttachment" name="oldAttachment" value="${params.attachment}" />
		</form:form>
		<div style="display: none">
			<input type="text" id="flag" value="${flag}" />
			<input type="text" id="errorMessage" value="${errorMessage}" />
		</div>
	</div>
	<script type="text/javascript">
	$(function() {
		var agent_no1 = $("[name=agent_no]").find("option:selected").text();
		$("#agentInput1").val(agent_no1);
		var cus = 0;
		var classname2 = "";
		var $autocomplete2 = $("<ul class='autocomplete2' style='position:absolute;overflow-y:auto;width:328px;margin-left:-220px;margin-top:21px;background-color: #FFFFFF'></ul>")	.hide().insertAfter("#agentInput");
		$("#agentInput1").keyup(	function(event) {
							var arry = new Array();
							$("[name=agent_no]").find("option").each(
									function(i, n) {
										arry[i] = $(this).text();
									});
							if ((event.keyCode != 38) && (event.keyCode != 40)	&& (event.keyCode != 13)) {
								$autocomplete2.empty();
								var $SerTxt = $("#agentInput1").val().toLowerCase();
								if ($SerTxt != "" && $SerTxt != null) {
									for (var k = 0; k < arry.length; k++) {
										if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
											$(	"<li title=" + arry[k] + " class=" + classname2 +" style='background-color: #FFFFFF'></li>")	.text(arry[k]).appendTo($autocomplete2)
													.mouseover(function() {
																$(".autocomplete2 li").removeClass("hovers");
																$(this).css({background : "#3368c4",	color : "#fff"});
													}).mouseout(	function() {
																$(this).css({background : "#fff",color : "#000"});
													}).click(function() {
																var text = $(this).text();
																$("#agentInput1").val(text);
																$(	"[name=agent_no]")	.find("option").each(function(i,	n) {
																			if ($(this).text() == text) {
																				$(	this).prop('selected',	'true');
																			}
																});
																$('#agent').trigger('change');
																$autocomplete2.hide();
															});
										}
									}
								}
								$autocomplete2.show();
							}
							var listsize = $(".autocomplete2 li").size();
							$(".autocomplete2 li").eq(0).addClass("hovers");
							if (event.keyCode == 38) {
								if (cus < 1) {
									cus = listsize - 1;
									$(".autocomplete2 li").removeClass();
									$(".autocomplete2 li").eq(cus).addClass("hovers");
									var text = $(".autocomplete2 li").eq(cus).text();
									$("#agentInput1").val(text);
									$("[name=agent_no]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected',	'true');
												}
											}
									);
								} else {
									cus--;
									$(".autocomplete2 li").removeClass();
									$(".autocomplete2 li").eq(cus).addClass("hovers");
									var text = $(".autocomplete2 li").eq(cus).text();
									$("#agentInput1").val(text);
									$("[name=agent_no]").find("option").each(function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected',	'true');
												}
												}
									);
								}
							}
							if (event.keyCode == 40) {
								if (cus < (listsize - 1)) {
									cus++;
									$(".autocomplete2 li").removeClass();
									$(".autocomplete2 li").eq(cus).addClass("hovers");
									var text = $(".autocomplete2 li").eq(cus).text();
									$("#agentInput1").val(text);
									$("[name=agent_no]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected',	'true');
												}
											}
									);
								} else {
									cus = 0;
									$(".autocomplete2 li").removeClass();
									$(".autocomplete2 li").eq(cus).addClass("hovers");
									var text = $(".autocomplete2 li").eq(cus).text();
									$("#agentInput1").val(text);
									$("[name=agent_no]").find("option").each(
											function(i, n) {
												if ($(this).text() == text) {
													$(this).prop('selected',	'true');
											}
									});
								}
							}
							if (event.keyCode == 13) {
								$(".autocomplete2 li").removeClass();
								$autocomplete2.hide();
							}
						});
		$("select[name=agent_no]").change(
				function() {
					var agent_Name1 = $("select[name=agent_no]").find("option:selected").text(); //集群所属所属代理商名称
					if (agent_Name1 != "") {
						$("#agentInput1").val(agent_Name1);
						$autocomplete2.hide();
					}
				}
		);
		
	});
	</script>
</body>
