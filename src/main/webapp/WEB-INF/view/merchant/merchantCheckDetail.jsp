<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<style type="text/css">
#checkDetailSubmit ul li label.must {
	display: -moz-inline-box;
	display: inline-block;
	width: 5px;
	text-align: center;
	color: red;
}

#checkDetailSubmit ul li {
	margin: 0;
	padding: 0;
	display: block;
	float: left;
	width: 250px;
	margin-bottom: 3px;
	line-height: 32px;
}

#checkDetailSubmit ul li.column2 {
	width: 500px;
}

#checkDetailSubmit ul li.column3 {
	width: 750px;
}

#checkDetailSubmit ul li label {
	display: -moz-inline-box;
	display: inline-block;
	width: 90px;
}

#checkDetailSubmit ul li .area {
	width: 75px;
	height: 24px;
	vertical-align: top;
}

.tip {
	border: 1px solid #EFEFEF;
	padding: 20px;
	background: #FBF9F9;
}
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript">



$(function() {
	// $("#final_pay_method").val(11); 
	var agentT = $("[name=belong_to_agent]").find("option:selected").text();
	$("#agentInput").val(agentT);
	        var cus = 0;
		    var classname = "";
		    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
		    $("#agentInput").keyup(function(event) {
			    var arry = new Array();
			    $("[name=belong_to_agent]").find("option").each(function(i, n) {
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
		                            $("[name=belong_to_agent]").find("option").each(function(i, n) {
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
		                $("[name=belong_to_agent]").find("option").each(function(i, n) {
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
		                $("[name=belong_to_agent]").find("option").each(function(i, n) {
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
		                $("[name=belong_to_agent]").find("option").each(function(i, n) {
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
		                
		                $("[name=belong_to_agent]").find("option").each(function(i, n) {
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
		    });/*.blur(function() {
		        setTimeout(function() {
		            $autocomplete.hide();
		        },
		        3000);
		    });*/
		    
		    $("[name=belong_to_agent]").change(function(){
		    	var agent_Name =  $("[name=belong_to_agent]").find("option:selected").text(); //集群所属所属代理商名称
				if(agent_Name != ""){
					$("#agentInput").val(agent_Name);
					$autocomplete.hide();
				}
		    	});
	
	
	
});



	function feeTypeChange() {
		var feeType = document.getElementById('fee_type').value;

		if (feeType == 'RATIO') {
			document.getElementById("fee_rate").style.display = "";
			document.getElementById("fee_cap_amount").style.display = "none";
			document.getElementById("fee_max_amount").style.display = "none";
			document.getElementById("fee_ladder").style.display = "none";

		} else if (feeType == 'CAPPING') {
			document.getElementById("fee_rate").style.display = "";
			document.getElementById("fee_cap_amount").style.display = "";
			document.getElementById("fee_max_amount").style.display = "";
			document.getElementById("fee_ladder").style.display = "none";
		} else if (feeType == 'LADDER') {
			document.getElementById("fee_rate").style.display = "none";
			document.getElementById("fee_cap_amount").style.display = "none";
			document.getElementById("fee_max_amount").style.display = "none";
			document.getElementById("fee_ladder").style.display = "";
		}
	}

	$(function() {
        
		//身份验证按钮初始化
	  //initIdCard();
		
		
		//
		var TEL_REG = /^[0-9]{3,4}\-[0-9]{7,8}$/;
		var MOBILE_REG = /^1[0-9]{10}$/;
		var INTEGER_REG = /^(0|([1-9][0-9]*))$/; //正整数
		var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式

		//初始省市联动
		var INIT_OPTION = "--请选择--";
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
		$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");

		$.each(provinceName, function(i, n) {
			$("<option></option>").val(n).text(n).appendTo("#province");
		});

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

		//--------------------------身份证验证-----------------------------------------------------------
			$("#idcardVerification").click(function() {
			        //企业法人
				    var  account_name=document.getElementById("account_name").value;
			        var id_card_no=document.getElementById("id_card_no").value;
			          $.ajax({
		                   url:'${ctx}/mer/idcardVerification',
		                         cache:false,
		                         data:{'account_name':account_name,'id_card_no':id_card_no},
		                         type:'POST',
		                         dataType:'json',
		                         error:function()
		                         {
		                              alert("身份验证有误，请核对身份证信息！");
		                              //document.getElementById("submitSuccess").disabled=false;
		                         },
		                         success:function(flag)
		                         {       
	                               if(flag=='1'){
	                            	   alert("身份验证成功！");
	                            	   //恢复按钮
	                            	   //document.getElementById("submitSuccess").disabled=false;
	                               }else{
	                            	    alert("身份验证有误，请核对身份证信息！");
	                            	    //document.getElementById("submitSuccess").disabled=false;
	                               }
	                         	   $("#idcardVerification").removeClass("button blue medium");
		                           $("#idcardVerification").attr('disabled',true);
		                         }
		                   });         
			});
		
			$("#szfsAccountVerify").click(function() {
				
			        var disSettleIdentity = "${params.disSettleIdentity}",
			        	id="${params.id}";
			        	if(disSettleIdentity){
			        		$.dialog.alert("<pre>此验证通道不可用，请使用备用验证通道！</pre>");
			        		return false;
			        	}
			        	
			        $("#szfsAccountVerify").attr('disabled',true);
			          $.ajax({
		                   url:'${ctx}/mer/szfsAccountVerify',
		                         cache:false,
		                         data:{'id':id},
		                         type:'POST',
		                         error:function()
		                         {
		                        	 $.dialog.alert("校验失败，请稍后重试");
		                        	 $("#szfsAccountVerify").attr('disabled',false);
		                         },
		                         success:function(msg)
		                         {       
		                        	 $.dialog.alert("<pre>"+msg+"</pre>");
		                        	//  $("#szfsAccountVerify").attr('disabled',false);
		                        	 $("#szfsAccountVerify").removeClass("button blue medium");
		                        	 $("#szfsAccountVerify").attr('disabled',true);
		                        	
		                         }
		                   });         
			});

		
			$("#sysAccountVerify").click(function() {
				
			        $("#sysAccountVerify").attr('disabled',true);
			          $.ajax({
		                   url:'${ctx}/mer/realNameAuthentication',
		                         cache:false,
		                         data:{'id':'${params.id}'},
		                         type:'POST',
		                         error:function()
		                         {
		                        	 $.dialog.alert("校验失败，请稍后重试");
		                        	 $("#sysAccountVerify").attr('disabled',false);
		                         },
		                         success:function(msg)
		                         {       
		                        	 $.dialog.alert("<pre>"+msg+"</pre>");
		                        	//  $("#szfsAccountVerify").attr('disabled',false);
		                        	 $("#sysAccountVerify").removeClass("button blue medium");
		                        	 $("#sysAccountVerify").attr('disabled',true);
		                        	
		                         }
		                   });         
			});
		
		
		
		  
		
		
		

		feeTypeChange();
		terminalNosLoad();
		$("#submitSuccess")
				.click(
						function() {
							var isSubmit = true;

							$.each($("input:text,select,textarea"),
											function(i, n) {
												if ($(n).hasClass("required")) {
													if ($.trim($(n).val()).length === 0) {
														var dialog = $
																.dialog({
																	title : '错误',
																	lock : true,
																	content : $(
																			n)
																			.parent()
																			.find(
																					"label")
																			.first()
																			.html()
																			+ '不能为空',
																	icon : 'error.gif',
																	ok : function() {
																		$(n)
																				.focus();
																	}
																});

														isSubmit = false;
														return false;
													}
												}

												
												
												if ($(n).hasClass("phone")) {
													var phone = $.trim($(n)
															.val());
													if (phone.length > 0) {
														if (!phone
																.match(TEL_REG)
																&& !phone
																		.match(MOBILE_REG)) {
															var dialog = $
																	.dialog({
																		title : '错误',
																		lock : true,
																		content : $(
																				n)
																				.parent()
																				.find(
																						"label")
																				.first()
																				.html()
																				+ '格式出错：正确格式例子为：0755-12345678或13912346789',
																		icon : 'error.gif',
																		ok : function() {
																			$(n)
																					.focus();
																		}
																	});
															isSubmit = false;
															return false;
														}
													}

												}

												if ($(n).hasClass("money")) {
													var money = $.trim($(n)
															.val());

													if (money.length > 0) {

														if (!money
																.match(INTEGER_REG)
																&& !money
																		.match(MONEY_REG)) {
															var dialog = $
																	.dialog({
																		title : '错误',
																		lock : true,
																		content : $(
																				n)
																				.parent()
																				.find(
																						"label")
																				.first()
																				.html()
																				+ '格式出错：正确格式例子为：8.88或8',
																		icon : 'error.gif',
																		ok : function() {
																			$(n)
																					.focus();
																		}
																	});
															isSubmit = false;
															return false;
														}
													}

												}

												if ($(n).hasClass("number")) {
													var number = $.trim($(n)
															.val());
													if (number.length > 0) {
														if (!$
																.isNumeric(number)) {
															var dialog = $
																	.dialog({
																		title : '错误',
																		lock : true,
																		content : $(
																				n)
																				.parent()
																				.find(
																						"label")
																				.first()
																				.html()
																				+ '格式出错：正确格式例子为：8或8.88',
																		icon : 'error.gif',
																		ok : function() {
																			$(n)
																					.focus();
																		}
																	});
															isSubmit = false;
															return false;
														}
													}
												}

												if ($(n).hasClass("integer")) {
													var integer = $.trim($(n)
															.val());
													if (integer.length > 0) {
														if (!integer
																.match(INTEGER_REG)) {
															var dialog = $
																	.dialog({
																		title : '错误',
																		lock : true,
																		content : $(
																				n)
																				.parent()
																				.find(
																						"label")
																				.first()
																				.html()
																				+ '格式出错：正确格式例子为：8',
																		icon : 'error.gif',
																		ok : function() {
																			$(n)
																					.focus();
																		}
																	});
															isSubmit = false;
															return false;
														}
													}

												}

											});

							if (isSubmit) {
								//手续费类型判断
								var fee_type = $("#fee_type").val();
								if (fee_type == 'RATIO') {
									if ($.trim($("#fee_rate input:text").val()).length == 0) {
										$.dialog.alert("扣率不能为空");
										$("#fee_rate input:text").focus();
										isSubmit = false;
										return false;
									}
								} else if (fee_type == 'CAPPING') {
									if ($.trim($("#fee_rate input:text").val()).length == 0) {
										$.dialog.alert("扣率不能为空");
										$("#fee_rate input:text").focus();
										isSubmit = false;
										return false;
									}
									if ($.trim($("#fee_max_amount input:text")
											.val()).length == 0) {
										$.dialog.alert("封顶手续费不能为空");
										$("#fee_max_amount input:text").focus();
										isSubmit = false;
										return false;
									}

								} else if (fee_type == 'LADDER') {
									if ($.trim($("#ladder_min").val()).length == 0
											|| $.trim($("#ladder_value").val()).length == 0
											|| $.trim($("#ladder_max").val()).length == 0) {
										$.dialog.alert("阶梯数据不完整");
										$("#fee_ladder input:text").focus();
										isSubmit = false;
										return false;
									}

								}
								ed_max_amount = parseInt($("#ed_max_amount")
										.val());
								single_max_amount = parseInt($(
										"#single_max_amount").val());
								ed_card_max_amount = parseInt($(
										"#ed_card_max_amount").val());
								ed_card_max_items = parseInt($(
										"#ed_card_max_items").val());

								if (ed_card_max_amount > ed_max_amount) {
									alert("单日终端单卡最大交易额 不能大于 单日终端最大交易额");
									return false;
								}

								if (single_max_amount > ed_card_max_amount) {
									alert("终端单笔最大交易额 不能大于 单日终端单卡最大交易额");
									return false;
								}
								if (single_max_amount > ed_max_amount) {
									alert("终端单笔最大交易额 不能大于 单日终端最大交易额");
									return false;
								}
								$("#examinationMark").val("success");
								
								
								//判断设备类型为移小宝时，晚上23点至早上6点期间不进行刷卡交易
								var trans_time_start = $("input[name='trans_time_start']").val();
								var trans_time_end = $("input[name='trans_time_end']").val();
								var tem = parseInt(trans_time_start.substring(0,2));
								var end = parseInt(trans_time_end.substring(0,2));
								var posType=$("#pos_type").val();
								
								if(posType=='3'){
									if(end==23 || tem<6){
										$.dialog.alert("晚上11点至早上6点期间移小宝不进行刷卡交易！");
										return false;
									}else{
										$("#submitSuccess").attr("disabled", "disabled");
										$("#submitSuccess").val("请稍候，正在处理中...");
										$("form:first").submit();
									}
								}else{
									$("#submitSuccess").attr("disabled", "disabled");
									$("#submitSuccess").val("请稍候，正在处理中...");
									$("form:first").submit();
								}
								
							}
						});

		$("#submitFail").click(
				function() {

					var failSubmit = true;
					var examination_opinions = $
							.trim($("#examination_opinions").val());
					if (examination_opinions == null
							|| examination_opinions == '') {
						var dialog = $.dialog({
							title : '错误',
							lock : true,
							content : '审核意见不能为空',
							icon : 'error.gif',
							ok : function() {
								$("#examination_opinions").focus();
							}
						});
						failSubmit = false;
						return false;
					}

					if (failSubmit) {
						$("#examinationMark").val("fail");
						$("form:first").submit();
					}

				});
		

		$("#groupadd").click(
				function() { 
					groupId = $('#groupId').val();
					newAddMerchantToG("${params.merchant_no}",groupId,"${params.mobile_username}","${params.merchant_name}");
				});
		//setMerchantFeeOnLoad();
		settleChange();
	});

	
function newAddMerchantToG(merchant_no,group_code,mobile_username,merchant_name){
		if(merchant_no==''||group_code==''){
			alert('数据有误');
			return false;
		} 
		if(group_code != ""){
			$.ajax({
				type : "post",
				url : "${ctx}/group/merAddGroupAndModifyMerInfo",
				data : {
					"merchant_no" : merchant_no,"group_code":group_code
				},
				dataType : 'json',
				success : function(data) {
					var ret = data.msg;
					if (ret == "OK") {
						alert("添加商户成功，并开启商户");
						$('#merchant_no').val("");
					}else if(ret == "ADDOKNOP"){
						 $.ajax({
							    url: "${ctx}/mer/merSetNormal?",
							    type:"POST",
							    data:"merchant_no="+merchant_no+"&mobile_username="+mobile_username+"&merchant_name="+merchant_name+"&layout=no",
							    success: function(data){
								    	var ico="error.gif";
								    	var msg=data;
								    	if("SUCCESS"==data){
								    		ico="success.gif";
								    		msg = "商户状态已开启！";
								    	}
									   	var dialog = $.dialog({title: '提示',lock:true,content: msg,icon: ico,ok:null });
							     }
				        });
						 
					}else{
						alert("添加商户失败");
					}
				}
			});
		}
		
		
	}
	
	
	function terminalNosLoad() {
		var terminalNostr = $("#terminal_no").val();
		if (terminalNostr != null && terminalNostr != "") {
			var terminal_no = terminalNostr.split(";");
			var s = document.getElementById('s');
			$("#terminalNo_0").val(terminal_no[0]);
			for ( var i = 1; i < terminal_no.length; i++) {
				var li = document.createElement("li");
				li.className = 'column3';
				li.id = "terminalli_" + i;
				li.innerHTML = "<label>&nbsp;&nbsp;</label><input type='text' class='input' id='terminalNo_"+i+"' style='width: 370px' name='terminalNo' readonly = 'readonly'  value='"+terminal_no[i]+"' />";
				s.appendChild(li);
			}
		}
	}
	
	
	function mySettleChange() {
		var agent_no=document.getElementById('agent_no').value;
		var pos_type = document.getElementById('pos_type').value;
		if(agent_no!='3846'){
			$("input[name='trans_time_start']").val("00:00:00");
			$("input[name='trans_time_end']").val("23:59:59");
			if (pos_type == '1') {
				document.getElementById("my_settle").value = "0";
				document.getElementById("ylst").style.display="none";
				$("#bagSettle").hide();
			} else if (pos_type == '2') {
				document.getElementById("my_settle").value = "0";
				document.getElementById("ylst").style.display="none";
				$("#bagSettle").hide();
			} else if (pos_type == '3') {
				document.getElementById("my_settle").value = "1";
				document.getElementById("ylst").style.display="none";
				$("#bagSettle").show();
				$("input[name='trans_time_start']").val("06:00:00");
				$("input[name='trans_time_end']").val("22:59:59");
				
			} else if (pos_type == '4') {
				$("#bagSettle").show();
				document.getElementById("my_settle").value = "1";
				document.getElementById("ylst").style.display='block';
			} else if (pos_type == '5') {
				$("#bagSettle").show();
				document.getElementById("my_settle").value = "1";
				document.getElementById("ylst").style.display="none";
			} 
		}
		
	}
	
	//当设备类型为传统POS时 系统自动做如下设置
	function setMerchantFeeOnLoad(){
		//fee_type
		var condition = 3;
		var agent_no = "${params.agent_no}";
		var pos_type = "${params['pos_type']}"; //设备类型 条件：传统POS
		var fee_type = "${params['fee_type']}"; //扣率类型 条件：封顶
		var merchant_type = "${params.merchant_type}"; //编号：5541 表示： 民生类
		var account_type = "${params.account_type}"; //账户类型  条件对私
		//设备类型：传统POS  扣率类型：封顶   账户类型：对私   商户类型：5111 表示： 批发类 
		if(pos_type == '2' && fee_type=='CAPPING' && account_type == '对私' && merchant_type=='5111'){
			condition=1;
		}else if(pos_type == '2' && fee_type=='RATIO' && account_type == '对私' && merchant_type=='5541'){
			//设备类型：传统POS  扣率类型：扣率   账户类型：对私   商户类型：5541 表示： 民生类 
			condition=2;
		}else if(pos_type == '4'){ //移联商通 默认限额该举动将无视原有默认值
			condition=4;
		}else if(pos_type == '5'){ //移联商通 默认限额该举动将无视原有默认值
			condition=5;
		}
		
		if(agent_no == '4028'){
			if(agent_no == '4028' && pos_type == '2'){ 
				//4028:新支付 ，设备类型=2：传统POS 
				condition=7;
			}
			
			if(agent_no == '4028' && pos_type == '2' && account_type == '对私' && fee_type=='CAPPING'){  
				//4028:新支付 设备类型=2：传统POS  扣率类型：封顶   账户类型：对私 
				condition=6;
			}
		}
		
		if(agent_no == '2179'){ //诚付天下
			if(pos_type == '5'){
				condition=8;
			}
		}
		
		if(agent_no == '11853'){ //富卡
			if(pos_type == '5'){
				condition=9;
			}
		}
		
		if(agent_no == '9896'){ //中宽
			if(pos_type == '5'){
				condition=10;
			}
		}
		
		setMerchantFee(condition);
		
		//兴华通付和金元宝付、资汇通富 所有商户默认为优质商户
		if(agent_no == '8396' || agent_no == '10023'  || agent_no == '12433'   || agent_no == '12435'){
			$("#bagSettle").show();
			document.getElementById("my_settle").value = "1";
			document.getElementById("ylst").style.display='block';
		}
	}
	
	function setMerchantFeeOnChange(){
		var pos_type = document.getElementById('pos_type').value;
		var fee_type = document.getElementById('fee_type').value;
		var ed_max_amount = "${params.ed_max_amount}"; //单日终端最大交易额
		var single_max_amount = "${params.single_max_amount}";//终端单笔最大交易额
		var ed_card_max_amount = "${params.ed_card_max_amount}";//单日单卡最大交易额
		var ed_card_max_items = "${params.ed_card_max_items}";//单日终端单卡最大交易笔数
		var merchant_type = "${params.merchant_type}"; //编号：5541 表示： 民生类 
		var account_type = "${params.account_type}"; //账户类型  条件对私
		var oldpos_type = "${params.pos_type}";//原先的设备类型
		var condition = 3;
		//默认  
		if(pos_type==oldpos_type){
			document.getElementById("ed_max_amount").value = ed_max_amount;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = single_max_amount; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = ed_card_max_amount; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = ed_card_max_items; //单日终端单卡最大交易笔数
		}else{
			//设备类型：传统POS  扣率类型：封顶   账户类型：对私   商户类型：5111 表示： 批发类 
			if(pos_type == '2' && fee_type=='CAPPING' && account_type == '对私' && merchant_type=='5111'){
				condition=1;
			}else if(pos_type == '2' && fee_type=='RATIO' && account_type == '对私' && merchant_type=='5541'){
				//设备类型：传统POS  扣率类型：封顶   账户类型：对私   商户类型：5541 表示： 民生类 
				condition=2;
			}else if(pos_type=='4'){
				//移联商通
				condition=4;
			}else if(pos_type == '5'){
				//超级刷
				condition=5;
			}
			setMerchantFee(condition);
		}
		
	}
	
	function setMerchantFee(condition){
		if(condition == 1){
			document.getElementById("ed_max_amount").value = "400000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "40000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "40000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(condition == 2){
			document.getElementById("ed_max_amount").value = "200000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "0.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "80000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(condition == 5){
			document.getElementById("ed_max_amount").value = "50000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "20000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(condition == 4){
			document.getElementById("ed_max_amount").value = "300000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "20000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "2"; //单日终端单卡最大交易笔数
			//重置复选框和单选按钮
			document.getElementById("model1").checked = false;
			document.getElementById("model21").checked = false;
			document.getElementById("model22").checked = false;
			document.getElementById("model3").checked = false;
		}else if(condition == 6){
			document.getElementById("ed_max_amount").value = "400000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "40000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "40000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(condition == 7){
			document.getElementById("ed_max_amount").value = "200000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "0"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "80000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(condition == 8){ //诚付天下超级刷
			document.getElementById("ed_max_amount").value = "800000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "50000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "50000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "2"; //单日终端单卡最大交易笔数
		}else if(condition == 9){ //富卡
			document.getElementById("ed_max_amount").value = "50000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "30000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(condition == 10){ //中宽
			document.getElementById("ed_max_amount").value = "60000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "30000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else{
			//默认  
			var ed_max_amount = "${params.ed_max_amount}"; //单日终端最大交易额
			var single_max_amount = "${params.single_max_amount}";//终端单笔最大交易额
			var ed_card_max_amount = "${params.ed_card_max_amount}";//单日单卡最大交易额
			var ed_card_max_items = "${params.ed_card_max_items}";//单日终端单卡最大交易笔数
			document.getElementById("ed_max_amount").value = ed_max_amount;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = single_max_amount; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = ed_card_max_amount; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = ed_card_max_items; //单日终端单卡最大交易笔数
		}
		/*document.getElementById("single_max_amount").value = "5000.00"; //终端单笔最大交易额
		document.getElementById("ed_card_max_items").value = "2"; //单日终端单卡最大交易笔数
		document.getElementById("ed_card_max_amount").value = "10000.00"; //单日单卡最大交易额*/
	}
	
	
	function checkPayMethod(op,ck){
		var final_pay_method = $("#final_pay_method").val();
		if("b"===op){
			if(ck.checked){
				final_pay_method = "1" + final_pay_method.substr(1,1);
			}else{
				final_pay_method = "0" + final_pay_method.substr(1,1);
			}
		}
		if("a"===op){
			if(ck.checked){
				final_pay_method = final_pay_method.substr(0,1) + "1";
			}else{
				final_pay_method = final_pay_method.substr(0,1) + "0";
			}
		}
		$("#final_pay_method").val(final_pay_method);
	}
	
	
	/* //身份证验证之前审核通过、审核失败按钮禁用
	function  initIdCard(){
		   $("#submitSuccess").attr("disabled","disabled");
		   $("#submitFail").attr("disabled","disabled");
	} */
	
	
	function changeRule(){
		var rule_choose_a =document.getElementById("rule_choose_a"); 
		var rule_choose_b =document.getElementById("rule_choose_b"); 
		var rule_choose_c =document.getElementById("rule_choose_c"); 
		var rule_choose_w =document.getElementById("rule_choose_w"); 
		if(rule_choose_a.checked){
			rule_choose_b.checked=false;
			rule_choose_c.checked=false;
			rule_choose_w.checked=false;
			document.getElementById("ed_max_amount").value = "50000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "30000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(rule_choose_b.checked){
			rule_choose_a.checked=false;
			rule_choose_c.checked=false;
			rule_choose_w.checked=false;
			document.getElementById("ed_max_amount").value = "100000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "30000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(rule_choose_c.checked){
			rule_choose_a.checked=false;
			rule_choose_b.checked=false;
			rule_choose_w.checked=false;
			document.getElementById("ed_max_amount").value = "120000.00";  //单日终端最大交易额
			document.getElementById("single_max_amount").value = "300000.00"; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = "30000.00"; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = "4"; //单日终端单卡最大交易笔数
		}else if(rule_choose_w.checked){
			//默认  
			var ed_max_amount = "${params.ed_max_amount}"; //单日终端最大交易额
			var single_max_amount = "${params.single_max_amount}";//终端单笔最大交易额
			var ed_card_max_amount = "${params.ed_card_max_amount}";//单日单卡最大交易额
			var ed_card_max_items = "${params.ed_card_max_items}";//单日终端单卡最大交易笔数
			document.getElementById("ed_max_amount").value = ed_max_amount;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = single_max_amount; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = ed_card_max_amount; //单日单卡最大交易额
			document.getElementById("ed_card_max_items").value = ed_card_max_items; //单日终端单卡最大交易笔数
		}
		/*document.getElementById("single_max_amount").value = "5000.00"; //终端单笔最大交易额
		document.getElementById("ed_card_max_items").value = "2"; //单日终端单卡最大交易笔数
		document.getElementById("ed_card_max_amount").value = "10000.00"; //单日单卡最大交易额*/
	}
	
	
	//-----------------------------start--------------------
	
	function bagSettleChange(){
		//是否钱包结算
		var bag_settle=$("#bag_settle").val();
		if(bag_settle=='1'){  //选择是钱包结算，做钱包结算验证
			document.getElementById("trans_cancel").value='0';
				var mobile_username=$("#mobile_username").val();
		          $.ajax({
	                   url:'${ctx}/mer/bagVerification',
	                         cache:false,
	                         data:{'mobile_no':mobile_username},
	                         type:'POST',
	                         dataType:'json',
	                         error:function()
	                         {
	                              alert("此商户信息有误，请检查数据！");
	                              isSubmit = false;
	                              return false;
	                         },
	                         success:function(date)
	                         { 
                               if(date.flag=='notExist'){
                            	    $.dialog.alert("此商户不是钱包用户,不能使用钱包结算！");
                            	    document.getElementById("bag_settle").value='0';
                            	    isSubmit = false;
                            	    return false;
                               }
	                         }
	                 });         
		}
	}
	
	//-----------------------lzj添加----------------------------
	/*window.onload = function(){
		var pos_type = "${params['pos_type']}";
		if(pos_type=='4'){
			document.getElementById("ylst").style.display='block';
		}
	}*/
	
	function model1(){
		if(document.getElementById("model1").checked){
			document.getElementById("ed_max_amount").value = Number(document.getElementById("ed_max_amount").value)+100000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document.getElementById("single_max_amount").value)+10000.00; //终端单笔最大交易额
		}else{
			document.getElementById("ed_max_amount").value -= 100000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
		}
	}
	function model21(){
		if(document.getElementById("model21").checked){
			document.getElementById("ed_max_amount").value = Number(document.getElementById("ed_max_amount").value)+100000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document.getElementById("single_max_amount").value)+10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = Number(document.getElementById("ed_card_max_amount").value)+10000.00; //单日单卡最大交易额
			$("#model22").attr("disabled",true);
		}else{
			document.getElementById("ed_max_amount").value -= 100000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value -= 10000.00; //单日单卡最大交易额
			$("#model22").attr("disabled",false);
		}
	}
	function model22(){
		if(document.getElementById("model22").checked){
			document.getElementById("ed_max_amount").value = Number(document.getElementById("ed_max_amount").value)+300000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document.getElementById("single_max_amount").value)+10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = Number(document.getElementById("ed_card_max_amount").value)+10000.00; //单日单卡最大交易额
			$("#model21").attr("disabled",true);
		}else{
			document.getElementById("ed_max_amount").value -= 300000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value -= 10000.00; //单日单卡最大交易额
			$("#model21").attr("disabled",false);
		}
	}
	function model3(){
		if(document.getElementById("model3").checked){
			document.getElementById("ed_max_amount").value = Number(document.getElementById("ed_max_amount").value)+100000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value = Number(document.getElementById("single_max_amount").value)+10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value = Number(document.getElementById("ed_card_max_amount").value)+10000.00; //单日单卡最大交易额
		}else{
			document.getElementById("ed_max_amount").value -= 100000.00;  //单日终端最大交易额
			document.getElementById("single_max_amount").value -= 10000.00; //终端单笔最大交易额
			document.getElementById("ed_card_max_amount").value -=10000.00;
		}
	}
	
	 //-----------------------------end------------------------
	
	function settleChange(){
	   var my_settle=document.getElementById("my_settle").value;
	   if(my_settle=='1'){
	     $("#bagSettle").show();
	     bagSettleChange();
	   }else if(my_settle=='0'){
		 document.getElementById("bag_settle").value="0";
		 $("#bagSettle").hide();
	   } 
	 }
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理 &gt;商户审核
		</div>
		<form id="checkDetailSubmit" name="checkDetailSubmit"   action="${ctx}/mer/checkDetailSubmit" method="post">
			<div style="position:absolute;margin-left:79px;margin-top:250px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:12px;width: 120px;width: 110px !important;border: none;">
         	</div>
			<div id="search" class="item" style="border: none;">
				<input type="hidden" value="${params['id']}" name="id" />
				<input type="hidden" name="terminal_no" id="terminal_no" value="${params.terminal_no}" />
				<input type="hidden" name="agent_no" id="agent_no" value="${params.agent_no}" />
				
				
				<div class="title">商户信息 ${params.merchant_no}</div>
				<c:set var="date1">
					<fmt:formatDate value="${params.create_time}" pattern="yyyy-MM-dd" type="date" />
				</c:set>
				<c:set var="date2">2014-01-14</c:set>
				<ul>
					<li>
						<label>代理商名称：</label>
						<input type="text" class="input" value="${params['agent_name']}" name="agent_name" readonly="readonly" />
					</li>
					
					<li>
						<label>商户名称：</label>
						<input type="text" class="input" value="${params['merchant_name']}" name="merchant_name"  id="merchant_name"   readonly="readonly" />
					</li>
					<li>
						<label>商户类型：</label>
						<c:if test="${date2 >= date1}">
							<c:if test="${params.merchant_type eq '5812' }">
								<input type="text" class="input" value="宾馆、餐饮、娱乐" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '1520' }">
								<input type="text" class="input" value="批发、房产、汽车" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5300' }">
								<input type="text" class="input" value="航空、加油、超市" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5111' }">
								<input type="text" class="input" value="医院、学校、政府" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '6300' }">
								<input type="text" class="input" value="保险、公共事业" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5541' }">
								<input type="text" class="input" value="民生类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5331' }">
								<input type="text" class="input" value="一般类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '1011' }">
								<input type="text" class="input" value="其他" name="merchant_type" readonly="readonly" />
							</c:if>
						</c:if>
						<c:if test="${date2 < date1}">
							<c:if test="${params.merchant_type eq '5812' }">
								<input type="text" class="input" value="餐娱类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5111' }">
								<input type="text" class="input" value="批发类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5541' }">
								<input type="text" class="input" value="民生类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '5331' }">
								<input type="text" class="input" value="一般类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '1520' }">
								<input type="text" class="input" value="房车类" name="merchant_type" readonly="readonly" />
							</c:if>
							<c:if test="${params.merchant_type eq '1011' }">
								<input type="text" class="input" value="其他" name="merchant_type" readonly="readonly" />
							</c:if>
						</c:if>
					</li>
					<li class="column2">
						<label>主营业务：</label>
						<input type="text" style="width: 370px;" value="${params['main_business']}" name="main_business" readonly="readonly" />
					</li>
					<li>
						<label>企业法人：</label>
						<input class="input" type="text" id="lawyer"  name="lawyer" value="${params.lawyer}" readonly="readonly" />
					</li>
					<li class="column2">
						<label>经营地址：</label>
						<select id="province" name="province" class="area"></select><select id="city" name="city" class="area"></select>
						<input type="text" class="input" id="address" style="width: 220px" name="address" value="${params['address']}"
							class="required validate[required]" />
						<label class="must">*</label>
					</li>
					<li>
						<label>联系人：</label>
						<input type="text" size="20" value="${params['link_name']}" name="link_name" />
					</li>
					<div class="clear"></div>
					<li>
						<label>联系电话：</label>
						<input type="text" class="input" id="phone" name="phone" value="${params.phone}" readonly="readonly" />
					</li>
					<li>
						<label>Email：</label>
						<input type="text" class="input" id="email" name="email" value="${params.email}" readonly="readonly" />
					</li>
					<%--			<li  ><label>MCC：</label><input type="text" size="20" value="${params['mcc']}" name="mcc" id="mcc" class="required validate[required]"/><label class="must">*</label></li>--%>
					<li style="display: none;">
						<label>MCC：</label>
						<input type="text" size="20" value="0000" name="mcc" id="mcc" class="required validate[required]" />
						<label class="must">*</label>
					</li>
					<li>
						<label>登录手机号：</label>
						<input type="text" size="20" value="${params['mobile_username']}" name="mobile_username" id="mobile_username"  readonly="readonly" />
					</li>
					<div class="clear"></div>
					<li>
						<label>移付宝销售：</label>
						<input type="text" size="20" value="${params['sale_name']}" name="sale_name" />
					</li>
					<li>
						<label>是否实名：</label>
						<select name="real_flag" id="real_flag" style="width: 128px; height: 24px; vertical-align: top;" class="required">
							<option value="">--请选择--</option>
							<option value="0" <c:if test="${params.real_flag == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.real_flag == '1' }">selected="selected"</c:if>>是</option>
						</select>
						<label class="must">*</label>
					</li>
					<li>
						<label>密码暗语：</label>
						<input type="text" size="30" value="${params['code_word']}" name="code_word" readonly="readonly" />
					</li>
					<li class="column3">
						<label>法人身份证号：</label>
						<input type="text" style="width: 210px;" value="${params['id_card_no']}" name="id_card_no"  id="id_card_no"    readonly="readonly" />
						<%--<input class="button blue medium"  type="button"    style="width: 90px;text-align: center;"  id="sysAccountVerify"  name="sysAccountVerify"  value="账户验证" />
						--%><input class="button blue medium"  type="button"    style="width: 90px;text-align: center;"  id="szfsAccountVerify"  name="szfsAccountVerify"  value="身份验证" />
						<input class="button blue medium"  type="button"    style="width: 90px;text-align: center;"  id="idcardVerification"  name="idcardVerification"  value="备用验证1" />
					</li>
					<div class="clear"></div>
					<li>
						<label>所属代理商：</label>
						<u:select value="${params['belong_to_agent']}" style="width:150px;height: 24px;" getChildByParentAgentno="${params['agent_no']}"
							fieldAsValue="agent_no" stype="agent" otherOptions="onlyInDB" id="belong_to_agent" sname="belong_to_agent" />
					</li>
					<li>
						<label>设备类型：</label>
<!--						<select id="pos_type" name="pos_type" onchange="mySettleChange(),setMerchantFeeOnChange()"  style="width: 128px; height: 24px; vertical-align: top;">-->
<!--						 	<option value="">--请选择--</option> -->
<!--							<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>-->
<!--							<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>-->
<!--							<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>-->
<!--							<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>-->
<!--							<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>-->
<!--						</select>-->
						<u:TableSelect sid="pos_type" sname="pos_type" style="width: 128px; height: 24px; vertical-align: top;" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="onlyInDB" onEvent="onchange=\"mySettleChange();setMerchantFeeOnChange();\" "/>
					</li>
					<li>
						<label>进件方式：</label>
						<select id="add_type" name="add_type" style="width: 128px; height: 24px; vertical-align: top;">
							<option value="0" <c:out value="${params['add_type'] eq '0'?'selected':'' }"/>>网站进件</option>
							<option value="1" <c:out value="${params['add_type'] eq '1'?'selected':'' }"/>>客户端进件</option>
						</select>
					</li>
				</ul>

				<ul id="s">
					<li class="column2">
						<label>SN或PSAM号：</label>
						<input type="text" class="input" id="terminalNo_0"  style="width: 370px" name="terminalNo" readonly="readonly" value="" />
					</li>
					<li class="">
						<label>商户机具数量：</label>
						<input type="text" value="${params['terminal_count']}" name="terminal_count" readonly="readonly" />
					</li>
				</ul>
			<div>
			    <ul>
					<c:if test="${params['pos_type'] == '4'}">
					           <li   class="column3">
						              <label>激活码：</label>
						           	  <input  type="text"   style="width: 370px"   value="${params['keycode']}"  name="keycode"   id="keycode"   readonly="readonly" />
						       </li>
					</c:if>
				</ul>
				<ul>
				   <li class="column2">
				      <label>所属销售：</label>
					  <input type="text" style="width: 160px;"value="${params['self_sale_name']}" name="self_sale_name"  readonly="readonly" />
				   </li>
				   <div class="clear"></div>
				   <li class="column2">
				      <label>营业执照编号：</label>
					  <input type="text" style="width: 160px;" value="${params['bus_license_no']}" name="bus_license_no"  readonly="readonly" />
				   </li>
				</ul>
			</div>
				<div class="clear"></div>
				<br />
				<div class="title">商户手续费信息</div>
				<ul>
					<li>
						<label>结算周期：</label>
						T+<select name="settle_cycle" class="required" style="width: 108px; height: 24px">
							<%--<option value="">--请选择--</option>
							--%><option value="1" <c:out value="${params['settle_cycle'] eq '1'?'selected':'' }"/>>1</option>
							<%--<option value="2" <c:out value="${params['settle_cycle'] eq '2'?'selected':'' }"/>>2</option>
							<option value="3" <c:out value="${params['settle_cycle'] eq '3'?'selected':'' }"/>>3</option>
							<option value="365" <c:out value="${params['settle_cycle'] eq '365'?'selected':'' }"/>>365</option>
							--%><option value="0" <c:out value="${params['settle_cycle'] eq '0'?'selected':'' }"/>>0</option>
						</select>&nbsp;天
					</li>
					<li>
						<label>账户类型：</label>
						<c:if test="${params.account_type == '对公'}">
							<input class="input" type="text" id="account_type" name="account_type" value="对公" readonly="readonly" />
						</c:if>
						<c:if test="${params.account_type == '对私'}">
							<input class="input" type="text" id="account_type" name="account_type" value="对私" readonly="readonly" />
						</c:if>
					</li>
					<li>
						<label>开户名：</label>
						<input class="input" type="text" id="account_name" name="account_name" value="${params.account_name}" readonly="readonly" />
					</li>
					<div class="clear"></div>
					<li>
						<label>开户账号：</label>
						<input class="input" type="text" id="account_no" name="account_no" value="${params.account_no}" readonly="readonly" />
					</li>
					<%--    		<li><label>账户类型：</label><select name="account_type" id="account_type" style="width:157px">--%>
					<%--    		 <option value="" >--请选择--</option>--%>
					<%--				<option value="对公" <c:if test="${params.account_type == '对公'}">selected="selected"</c:if>>对公</option>--%>
					<%--				<option value="对私" <c:if test="${params.account_type == '对私'}">selected="selected"</c:if>>对私</option>--%>
					<%--			</select><label class="must">*</label>--%>
					<li>
						<label>开户行全称：</label>
						<input class="input" type="text" id="bank_name" name="bank_name" value="${params.bank_name}" readonly="readonly" />
					</li>
					<li>
						<label>联行行号：</label>
						<input class="input" type="text" id="cnaps_no" name="cnaps_no" value="${params.cnaps_no}" readonly="readonly" />
					</li>
					
					
					
					<li class="column3" >
						<label>扣率类型：</label>
						<select id="fee_type" name="fee_type" onchange="feeTypeChange(),setMerchantFeeOnChange()" style="width: 128px; height: 24px; vertical-align: top;">
							<option value="RATIO" <c:out value="${params['fee_type'] eq 'RATIO'?'selected':'' }"/>>扣率</option>
							<option value="CAPPING" <c:out value="${params['fee_type'] eq 'CAPPING'?'selected':'' }"/>>封顶</option>
							<option value="LADDER" <c:out value="${params['fee_type'] eq 'LADDER'?'selected':'' }"/>>阶梯</option>
						</select>
					</li>
					<div class="clear"></div>
					<li id="fee_rate" style="">
						<label>扣率：</label>
						<input type="text" size="20" value="${params['fee_rate']}" name="fee_rate" />
						%
					</li>
					<li id="fee_cap_amount" style="">
						<label>封顶金额：</label>
						<input type="text"  style="width: 94px;"    value="${params['fee_cap_amount']}" name="fee_cap_amount" class="money" />
						（元）
					</li>
					<li id="fee_max_amount" style="">
						<label>封顶手续费：</label>
						<input type="text"  style="width: 94px;"    value="${params['fee_max_amount']}" name="fee_max_amount" class="money" />
						（元）
					</li>
					<li id="fee_ladder" class="column3">
						<label>阶梯设置：</label>
						<input type="text" name="ladder_min" id="ladder_min" value="${params['ladder_min']}" class="number" />
						% &lt;
						<input type="text" name="ladder_value" id="ladder_value" value="${params['ladder_value']}" class="money" />
						（元） &lt;
						<input type="text" name="ladder_max" id="ladder_max" value="${params['ladder_max']}" class="number" />
						%
					</li>
				</ul>
				
				<div class="clear"></div>
				<br />
				<div class="title">商户交易规则信息</div>
				<div class="tip" style="padding: 10px;">交易额或交易笔数为0时是无限制</div>
				<div id="ylst" <c:if test="${params.pos_type != '4'}">style="display:none"</c:if>>
					<input style="width: 50px;" type="checkbox" name="model1" value="1" id="model1" onclick="javascript:window.model1()">经营场所证明
					<input style="width: 50px;" type="checkbox" name="model2" value="21" id="model21" onclick="javascript:window.model21()" >个体营业执照
					<input style="width: 50px;" type="checkbox" name="model2" value="22" id="model22" onclick="javascript:window.model22()" >企业营业执照
					<input style="width: 50px;" type="checkbox" name="model3" value="3" id="model3" onclick="javascript:window.model3()">店面、店内、收银台照
				</div>
				<ul>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日终端最大交易额：</label>
				    	<input style="width: 70px" type="text" id="ed_max_amount" name="ed_max_amount" class="input required money"
							value="${params.ed_max_amount}" />
						(元)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">终端单笔最大交易额：</label>
				    	<input style="width: 70px" type="text" id="single_max_amount" name="single_max_amount" class="input required money"
							value="${params.single_max_amount}" />
						(元)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日单卡最大交易额：</label>
				    	<input style="width: 70px" type="text" id="ed_card_max_amount" name="ed_card_max_amount" class="input required money"
							value="${params.ed_card_max_amount}" />
						(元)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日终端单卡最大交易笔数：</label>
				    	<input style="width: 70px" type="text" id="ed_card_max_items" name="ed_card_max_items" class="input required integer"
							value="${params.ed_card_max_items}" />
						(笔)
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
						<label style="width: 160px" class="longLabel">是否优质商户： </label>
						<select name="my_settle" id="my_settle" style="width: 80px; height: 24px; vertical-align: top;" class="required"  onfocus="this.defaultIndex=this.selectedIndex;" onchange="this.selectedIndex=this.defaultIndex;" >
							<option value="0" <c:if test="${params.my_settle == '0' || params.agent_no =='3846'}"></c:if>>否</option>
							<option value="1" <c:if test="${(params.my_settle == '1' || params.pos_type=='3' || params.pos_type=='4' || params.pos_type=='5') && params.agent_no !='3846' || params.agent_no =='4028'}">selected="selected"</c:if>>是</option>
						</select>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">手输卡号：</label>
						<select name="clear_card_no" id="clear_card_no" style="width: 80px; height: 24px; vertical-align: top;" class="required">
							<option value="0" <c:if test="${params.clear_card_no == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.clear_card_no == '1' }">selected="selected"</c:if>>是</option>
						</select>
					</li>

					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">可否撤销交易：</label>
						<select name="trans_cancel" id="trans_cancel" style="width: 80px; height: 24px; vertical-align: top;" class="required">
							<option value="0" <c:if test="${params.trans_cancel == '0' }">selected="selected"</c:if>>否</option>
							<option value="1" <c:if test="${params.trans_cancel == '1' }">selected="selected"</c:if>>是</option>
						</select>
					</li>
					<li style="width:390px;line-height: 25px;">
						<input type="hidden" value="${params['pay_method']}" id="final_pay_method" name="final_pay_method"  />
					 	<label style="width: 160px;">支付方式:</label>
						<input type="checkbox" value='1'  name="pay_method" style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            <c:if test="${fn:substring(params['pay_method'], 0, 1) eq '1'}">checked="checked"</c:if>
							onchange="checkPayMethod('b',this)" value="1" readonly="readonly" />Pos支付&nbsp;&nbsp;
			            <input type="checkbox" value='1'  name="pay_method"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;" 
			            <c:if test="${fn:substring(params['pay_method'], 1, 2) eq '1'}">checked="checked"</c:if>
			            	onchange="checkPayMethod('a',this)" value="1" readonly="readonly" />快捷支付
					</li>
					<li style="width: 390px" id="bagSettle">
				      <label style="width: 160px" class="longLabel">是否钱包结算：</label>
				      <select name="bag_settle" id="bag_settle" style="width: 80px; height: 24px; vertical-align: top;" class="required" onchange="bagSettleChange()">
							<option value="1" <c:if test="${params.bag_settle == '1' }">selected="selected"</c:if>>是</option>
							<option value="0" <c:if test="${params.bag_settle == '0' }">selected="selected"</c:if>>否</option>
					  </select>
				   </li>
				</ul>
				<div class="clear"></div>
				<%--<div class="title">添加集群</div>
				<ul style="height: 30px;">
					<li style="width: 400px">所属集群：
						<select style="width: 130px" id="gCode" name="gCode">
							<option value="">--请选择集群--</option>
							<c:forEach items="${glist}" var="m">
								<option value="${m.group_code}"  <c:if test="${m.group_code eq params['group_code']}">selected = "selected"</c:if>>${m.group_name}</option><label  style="color:red;">	(选填)</label>
							</c:forEach>
						</select>
					</li>
					
					</ul>
				<ul style="height: 30px;">
						<li style="width: 400px"><font  style="color:red;">备注：该处为选填，仅针对添加普通商户到集群；选择集群后，系统将自动绑定机具，并开启商户。</font ></li>
					</ul>	--%>
				<div class="title">备注</div>
				<ul style="height: 70px;">
					<li>
						<textarea readonly="readonly" style="border: 0px; overflow-x: hidden; overflow-y: hidden; overflow: hidden;" cols="100" rows="5">${params['remark']}</textarea>

					</li>
				</ul>
				<div class="clear"></div>

			

				<div class="title">审核意见</div>
				<ul style="height: 70px;">
					<li>
						<textarea name="examination_opinions" id="examination_opinions" cols="50" rows="4">${params['examination_opinions']}</textarea>
					</li>
				</ul>
				<!-- <div class="title">集群</div>
				<ul style="height: 70px;">
					<li>
					集群编号：	<input id="groupId" name="groupId" value="1046"/> </li>
				<li><input class="button blue medium" type="button" id="groupadd" name="groupadd"  value="添加" />
					</li>
				</ul> -->
				
				<div class="clear"></div>
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
			
			<div class="clear"></div>
			 
			<div class="search_btn">
				<shiro:hasPermission name="COMMERCIAL_CHECK_OK">
					<input class="button blue medium" type="button" id="submitSuccess"   name="submitSuccess"   value="审核通过" />
				</shiro:hasPermission>
				<input class="button blue medium" type="button" id="submitFail" name="submitFail"  value="审核失败" />
				<input name="reset" class="button blue medium" type="button" onclick="javascript:history.go(-1);" value="返回" />
			</div>
		</form>
       <div style="display: none">
			<input type="text" id="flag" value="${flag}" />
			<input type="text" id="errorMessage" value="${errorMessage}" />
	   </div>
	</div>
</body>
