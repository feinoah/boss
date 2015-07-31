<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
	<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
	<style type="text/css">
		#acqMerchantAdd
		{
			padding:10px;
		}
	
		#acqMerchantAdd ul
		{
			1overflow:hidden;
		} 
		
		#acqMerchantAdd ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:300px;
			heigth:32px;
			line-height:32px;
		}
		
		#acqMerchantAdd ul li.column2
		{
			width:700px;
		}
		
		#acqMerchantAdd ul li.column3
		{
			width:810px;
		}
		
		#acqMerchantAdd ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#acqMerchantAdd ul li label.must
		{
			width:5px;
			color:red;
		}
		
		#acqMerchantAdd ul li .area
		{
			width:75px;
		}
		
		
		
		#acqMerchantAdd ul li.long
		{
			width:440px;
		}
		
		
		#acqMerchantAdd div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
		

		
		.merchant_no{width:197;height:20px!important; height:17px;margin-left:-180px!important; margin-left:-179px}
		/* .sp{margin-left:179px;width:18px;overflow:hidden; } */
		.bo4{width:178px;position:absolute;left:0px!important;height:20px!important;top:0.5px!important; left:1px; top:0px; height:20px}
		.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:20px; line-height:20px}
		.hovers{ background-color:#3368c4; color:fff}

	</style>
	<script type="text/javascript">

		$(function(){
			
			var TEL_REG = /^[0-9]{3,4}\-[0-9]{7,8}$/;
			var MOBILE_REG =  /^1[0-9]{10}$/;
			var EMAIL_REG = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;

			var NUM_STR_REG = /^([0-9])+$/; //数字字符串

			var INTEGER_REG =  /^(0|([1-9][0-9]*))$/; //正整数
			var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
			
			
			//可输入下拉列表
			 var cus = 0;
			    var classname = "";
			    var $autocomplete = $("<ul class='autocomplete' style='overflow-y:auto;width:260px;margin-left:110px;'></ul>").hide().insertAfter("#box4");
			    $("#box4").keyup(function(event) {
				    var arry = new Array();
				    $("#merchant_no").find("option").each(function(i, n) {
				        arry[i] = $(this).text();
				    });
			    	
			        if ((event.keyCode != 38) && (event.keyCode != 40) && (event.keyCode != 13)) {
			            $autocomplete.empty();
			            var $SerTxt = $("#box4").val().toLowerCase();
			            if ($SerTxt != "" && $SerTxt != null) {
			                for (var k = 0; k < arry.length; k++) {
			                    if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
			                    	
			                        $("<li title=" + arry[k] + " class=" + classname + "></li>").text(arry[k]).appendTo($autocomplete).mouseover(function() {
			                            $(".autocomplete li").removeClass("hovers");
			                            $(this).css({
			                                background: "#3368c4",
			                                color: "#fff"
			                            })
			                        }).mouseout(function() {
			                            $(this).css({
			                                background: "#fff",
			                                color: "#000"
			                            })
			                        }).click(function() {
			                        	var text=$(this).text();
			                            $("#box4").val(text);
			                            $("#merchant_no").find("option").each(function(i, n) {
			                                if($(this).text()==text){
			                             	   $(this).prop('selected', 'true');
			                                }
			                             });
			                            $autocomplete.hide();
			                        });
			                    }
			                }
			            }
			            $autocomplete.show()
			        }
			        var listsize = $(".autocomplete li").size();
			        $(".autocomplete li").eq(0).addClass("hovers");
			        if (event.keyCode == 38) {
			            if (cus < 1) {
			                cus = listsize - 1;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#box4").val(text)
			                $("#merchant_no").find("option").each(function(i, n) {
			                   if($(this).text()==text){
			                	   $(this).prop('selected', 'true');
			                   }
			                });
			            } else {
			                cus--;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#box4").val(text)
			                $("#merchant_no").find("option").each(function(i, n) {
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
			                $("#box4").val(text)
			                $("#merchant_no").find("option").each(function(i, n) {
			                    if($(this).text()==text){
			                    	$(this).prop('selected', 'true');
			                    }
			                 });
			            } else {
			                cus = 0;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#box4").val(text);
			                
			                $("#merchant_no").find("option").each(function(i, n) {
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
			    }).blur(function() {
			        setTimeout(function() {
			            $autocomplete.hide()
			        },
			        3000)
			    });
			
			 var flag = '${flag}';
			 if(flag == "1")
			{
				var dialog = $.dialog({title: '成功',lock:true,content: '新增银盛商户'+''+'成功',icon: 'success.gif',ok: function(){
		        	$("form input:text").first().focus();
			    	}
				});
			}
			else if(flag == "2")
				{
					var dialog = $.dialog({title: '成功',lock:true,content: '更新银盛商户'+''+'成功',icon: 'success.gif',ok: function(){
			        	$("form input:text").first().focus();
				    	}
					});
			}
			else if(flag == "0")
			{
				var dialog = $.dialog({title: '错误',lock:true,content: "${errorMessage}",icon: 'error.gif',ok: function(){
					$("form input:text").first().focus();
			    	}
				});
			}

			 $("#fee_type").change(function(){
					var fee_type = $("#fee_type").val();
					var acq_enname=$("#acq_enname").val();
					if(fee_type == "RATIO")
					{
						$("#fee_rate_li").show();
						$("#rate_type_li").hide();
						$("#special_fee_rate").hide();
						$("#fee_max_amount_li").hide();
						$("#fee_ladder_li").hide();
						$("#merchant_rate_li").show();
						//$("#merchant_rate").val("");
						$("#merchant_max_amount_li").hide();
						$("#merchant_ladder_li").hide();
						ennameChange();
					}
					else if(fee_type =="CAPPING")
					{
						$("#fee_rate_li").show();
						$("#rate_type_li").show();
						$("#special_fee_rate").hide();
						$("#fee_max_amount_li").show();
						$("#fee_ladder_li").hide();
						$("#merchant_rate_li").show();
						//$("#merchant_rate").val("");
						$("#merchant_max_amount_li").show();
						$("#merchant_ladder_li").hide();
						ennameChange();
					}
					else if(fee_type="LADDER")
					{
						$("#fee_rate_li").hide();
						$("#fee_rate_li_two").hide();
						$("#fee_max_amount_li").hide();
						$("#special_fee_rate").hide();
						$("#rate_type_li").show();
						$("#fee_ladder_li").show();
						$("#merchant_rate_li").hide();
						$("#merchant_max_amount_li").hide();
						$("#merchant_ladder_li").show();
					/* 	var obj_fee_rate= document.getElementById('acq_enname');
         			    var innerHTML_fee_rate=' <option value="eptok" >银盛</option> ';
         			    obj_fee_rate.innerHTML=innerHTML_fee_rate; */
						ennameChange();
					}
						
				});

				$("#fee_type").change();		

			$("#addButton").click(function(){
				var isSubmit = true;
				
				
				if($("#agent_no").val()== "-1")
				{
					$.dialog.alert("请选择代理商");
					$("#agent_no").focus();
					return false;
				}
				
				if($("#rep_pay").val()== "-1")
				{
					$.dialog.alert("请选择是否代付");
					$("#rep_pay").focus();
					return false;
				}
				
				if($("#acq_enname").val()== null || $("#acq_enname").val()== '')
				{
					$.dialog.alert("请选择收单机构");
					$("#acq_enname").focus();
					return false;
				}
				
				 var inn = document.getElementsByName("terNoCount");
				 var validateSN = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/; //限制输入中文 英文 数字
				 if(inn.length > 0){
				 	for(var k=0;k<inn.length;k++){
				 		if(null != inn[k].value && inn[k].value != ""){
				 			if(inn[k].value.length > 18){
				 				alert("第："+(k+1)+"个收单机构终端编号超出长度限制，请输入18位以内终端编号！");
					 			return false;
				 			}
				 		
				 			if(!inn[k].value.match(validateSN)){
					 			alert("第："+(k+1)+"个收单机构终端编号格式错误，正确格式为：中文或拼音或数字 或 中文+拼音+数字！");
					 			return false;
					 		}
				 		}else{
				 				alert("第："+(k+1)+"个收单机构终端编号格式错误，不允许为空！");
					 			return false;
				 		}
				 	}
				 }
				
				$.each($("input:text,select,textarea"),function(i,n){
					if($(n).hasClass("required"))
					{
						if($.trim($(n).val()).length === 0)
						{
							var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'不能为空',icon: 'error.gif',ok: function(){
					        		$(n).focus();
						    	}
							});

							isSubmit = false;
							return false;
						}
					}
					
				    if($(n).hasClass("phone"))
					{
						var phone = $.trim($(n).val());
						if(phone.length > 0 )
						{
							if(!phone.match(TEL_REG) && !phone.match(MOBILE_REG))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：0755-12345678或13912346789',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
						
					}

				    if($(n).hasClass("money"))
					{
						var money = $.trim($(n).val());
						if(money.length > 0 )
						{
							
							if(  !money.match(INTEGER_REG) && !money.match(MONEY_REG))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：8.88或8',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
						
					}

					if($(n).hasClass("number"))
					{
						var number = $.trim($(n).val());
						if(number.length > 0 )
						{
							if(!$.isNumeric(number))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：8或8.88',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
					}

				    if($(n).hasClass("integer"))
					{
						var integer = $.trim($(n).val());
						if(integer.length > 0 )
						{
							if(!integer.match(INTEGER_REG))
							{
								var dialog = $.dialog({title: '错误',lock:true,content: $(n).parent().find("label").first().html()+'格式出错：正确格式例子为：8',icon: 'error.gif',ok: function(){
									$(n).focus();
								    }
								});
								isSubmit = false;
								return false;
							}
						}
						
					}
				    
				    
				});

				
				
				function checkAcq(acq_merchant_no,acq_merchant_name,id){
					 var mark = true;
					 $.ajax({
				 			type:"post",
				 			url:"${ctx}/acq/acqCheck",
				 			data:{"acq_merchant_no":acq_merchant_no,"acq_merchant_name":acq_merchant_name,"id":id},
				 			async:false,
				 			dataType: 'json',
						  success: function(data){
						    	var ret = data.msg;
							    if(ret == "existAcqMerchantNo"){
							  		$.dialog.alert("收单机构商户编号已被占用");
							  		$("#acq_merchant_no").focus();
							  		mark =  false; 
							  	}else if(ret == "existAcqMerchantName"){
							  		$.dialog.alert("收单机构商户名称已被占用");
							  		$("#acq_merchant_name").focus();
							  		mark =  false; 
							  	}
							  	else{
							  		mark = true;
							  	}
						  }
				 		}
				 	);
					 return mark;
			}	
				if(isSubmit)
				{
					var acq_merchant_no = $("#acq_merchant_no").val();
					var acq_merchant_name = $("#acq_merchant_name").val();	
					var merchant_no = $("#merchant_no").val();
					var id = $("#id").val();
					if(!checkAcq(acq_merchant_no,acq_merchant_name,id)){
						return false;
					}
					
					
					//手续费类型判断
					var fee_type = $("#fee_type").val();
					var 	fee_forCa=$("#fee_rate_ForCa").val();
					if(fee_type == 'RATIO')
					{

						var fee_rate=$("#fee_rate").val();
						if(fee_rate=='0'){
							if($.trim($("#fee_rate_ForCa").val()).length == 0)
							{
								 $.dialog.alert("比例（特殊）不能为空",function(){$("#fee_rate_ForCa").focus();});
								 return false;
							}else{
							            //if(fee_forCa !=null && !""===fee_forCa){
								//判断比例（特殊）小于100%
								if($.isNumeric($("#fee_rate_ForCa").val()))
								{
									//比例小于100%
									if($("#fee_rate_ForCa").val() >= 100)
									{
										 $.dialog.alert("比例不能大于100%",function(){$("#fee_rate_ForCa").focus();});
										 return false;
									}
									
									//比例小于0%
									if($("#fee_rate_ForCa").val() == 0 || $("#fee_rate_ForCa").val()<0)
									{
										 $.dialog.alert("比例不能小于或等于0",function(){$("#fee_rate_ForCa").focus();});
										 return false;
									}
									
									 
									//小数位不能超过四位
									 var decimal4 =/(([0-9]+\.[0-9]{1,4}))$/;
									 if(!$("#fee_rate_ForCa").val().match(decimal4) && !$("#fee_rate_ForCa").val().match(INTEGER_REG))
									 {
										 $.dialog.alert("比例不能超过四位小数",function(){$("#fee_rate_ForCa").focus();});
										 return false;
									 }
								}else{
									$.dialog.alert("比例（特殊）为数字");
									$("#fee_rate_ForCa").focus();
									 return false;
								}
							}
						}


						//---------------------------------------------------------------
						
						if($.trim($("#fee_rate").val()).length == 0)
						{
							 $.dialog.alert("比例不能为空",function(){$("#fee_rate").focus();});
							 return false;
						}
						else
						{
							if($.isNumeric($("#fee_rate").val()))
							{
								//扣率小于100%
								if($("#fee_rate").val() >= 100)
								{
									 $.dialog.alert("比例不能大于100%",function(){$("#fee_rate").focus();});
									 return false;
								}
								//小数位不能超过四位
								 var decimal4 =/(([0-9]+\.[0-9]{1,4}))$/;
								 if(!$("#fee_rate").val().match(decimal4) && !$("#fee_rate").val().match(INTEGER_REG))
								 {
									 $.dialog.alert("比例不能超过四位小数",function(){$("#fee_rate").focus();});
									 return false;
								 }
							}
							else
							{
								$.dialog.alert("比例为数字");
								 $("#fee_rate").focus();
								 return false;
							}
						}
					}
					else if(fee_type == 'CAPPING')
					{
					  var acq_enname=$("#acq_enname").val();
					  if(acq_enname =='zypay'){
						
							if($.trim($("#fee_rate_ForCa").val()).length == 0)
							{
								 $.dialog.alert("比例（特殊）不能为空",function(){$("#fee_rate_ForCa").focus();});
								 return false;
							}else{
							     // if(fee_forCa !=null && !""===fee_forCa){
								//判断比例（特殊）小于100%
								if($.isNumeric($("#fee_rate_ForCa").val()))
								{
									//比例小于100%
									if($("#fee_rate_ForCa").val() >= 100)
									{
										 $.dialog.alert("比例不能大于100%",function(){$("#fee_rate_ForCa").focus();});
										 return false;
									}
									
									//比例小于0%
									if($("#fee_rate_ForCa").val() == 0 || $("#fee_rate_ForCa").val()<0)
									{
										 $.dialog.alert("比例不能小于或等于0",function(){$("#fee_rate_ForCa").focus();});
										 return false;
									}
									
									 
									//小数位不能超过四位
									 var decimal4 =/(([0-9]+\.[0-9]{1,4}))$/;
									 if(!$("#fee_rate_ForCa").val().match(decimal4) && !$("#fee_rate_ForCa").val().match(INTEGER_REG))
									 {
										 $.dialog.alert("比例不能超过四位小数",function(){$("#fee_rate_ForCa").focus();});
										 return false;
									 }
									
									 
								}else{
									$.dialog.alert("比例（特殊）为数字");
									$("#fee_rate_ForCa").focus();
									 return false;
									
								}
						 }
					 }	
						
	/* 					if($.trim($("#fee_rate").val()).length == 0)
						{
							 $.dialog.alert("比例不能为空",function(){$("#fee_rate").focus();});
							 return false;
						}
						else
						{
							if($.isNumeric($("#fee_rate").val()))
							{
								//扣率小于100%
								if($("#fee_rate").val()  >= 100)
								{
									 $.dialog.alert("比例不能大于100%",function(){$("#fee_rate").focus();});
									 return false;
								}
								//小数位不能超过四位
								 var decimal4 =/(([0-9]+\.[0-9]{1,4}))$/;
								 if(!$("#fee_rate").val().match(decimal4) && !$("#fee_rate").val().match(INTEGER_REG))
								 {
									 $.dialog.alert("比例不能超过四位小数",function(){$("#fee_rate").focus();});
									 return false;
								 }
							}
							else
							{
								$.dialog.alert("比例为数字",function(){$("#fee_rate").focus();});
								return false;
							}
						} */
						
						if($.trim($("#fee_max_amount ").val()).length == 0)
						{
							 $.dialog.alert("封顶手续费不能为空");
							 $("#fee_max_amount ").focus();
							 return false;
						}
		
					}
					else if(fee_type == 'LADDER')
					{
						if($.trim($("#ladder_min").val()).length == 0 || $.trim($("#ladder_value").val()).length == 0 || $.trim($("#ladder_max").val()).length == 0)
						{
							 $.dialog.alert("阶梯数据不完整");
							 $("#fee_ladder input:text").focus();
							 return false;
						}

						if($("#ladder_min").val() >= 100){
							 $.dialog.alert("扣率最小比例不能大于100%",function(){$("#ladder_min").focus();});
							 return false;
						}else if($("#ladder_max").val() >= 100){
							 $.dialog.alert("扣率最大比例不能大于100%",function(){$("#ladder_max").focus();});
							 return false;
						}
						
					}

					//----------大商户扣率验证-----------
					if(fee_type == 'RATIO')
					{
						if($.trim($("#merchant_Drate").val()).length == 0)
						{
							 $.dialog.alert("大商户扣率比例不能为空",function(){$("#merchant_Drate").focus();});
							 return false;
						}
						else
						{
							if($.isNumeric($("#merchant_Drate").val()))
							{
								//扣率小于100%
								if($("#merchant_Drate").val() >= 100)
								{
									 $.dialog.alert("大商户扣率比例不能大于100%",function(){$("#merchant_Drate").focus();});
									 return false;
								}
								
								//扣率不能小于等于0%
								if($("#merchant_Drate").val() <= 0)
								{
									 $.dialog.alert("大商户扣率比例不能小于等于0",function(){$("#merchant_Drate").focus();});
									 return false;
								}
								
								//小数位不能超过四位
								 var decimal4 =/(([0-9]+\.[0-9]{1,4}))$/;
								 if(!$("#merchant_Drate").val().match(decimal4) && !$("#merchant_Drate").val().match(INTEGER_REG))
								 {
									 $.dialog.alert("大商户扣率比例不能超过四位小数",function(){$("#merchant_Drate").focus();});
									 return false;
								 }
								 
							}
							else
							{
								$.dialog.alert("大商户扣率比例为数字");
								 $("#merchant_Drate").focus();
								 return false;
							}
						}
					}
					else if(fee_type == 'CAPPING')
					{
						if($.trim($("#merchant_Drate").val()).length == 0)
						{
							 $.dialog.alert("大商户扣率比例不能为空",function(){$("#merchant_Drate").focus();});
							 return false;
						}
						else
						{
							if($.isNumeric($("#merchant_Drate").val()))
							{
								//扣率小于100%
								if($("#merchant_Drate").val()  >= 100)
								{
									 $.dialog.alert("大商户扣率比例不能大于100%",function(){$("#fee_rate").focus();});
									 return false;
								}
								

								//扣率不能小于等于0%
								if($("#merchant_Drate").val() <= 0)
								{
									 $.dialog.alert("大商户扣率比例不能小于等于0",function(){$("#merchant_Drate").focus();});
									 return false;
								}
								
								//小数位不能超过四位
								 var decimal4 =/(([0-9]+\.[0-9]{1,4}))$/;
								 if(!$("#merchant_Drate").val().match(decimal4) && !$("#merchant_Drate").val().match(INTEGER_REG))
								 {
									 $.dialog.alert("大商户扣率比例不能超过四位小数",function(){$("#merchant_Drate").focus();});
									 return false;
								 }
							}
							else
							{
								$.dialog.alert("大商户扣率比例为数字",function(){$("#merchant_Drate").focus();});
								return false;
							}
						}
						
						if($.trim($("#merchant_max_amount ").val()).length == 0)
						{
							 $.dialog.alert("大商户扣率封顶手续费不能为空");
							 $("#merchant_max_amount ").focus();
							 return false;
						}
							
					}
					else if(fee_type == 'LADDER')
					{
						if($.trim($("#merchant_min").val()).length == 0 || $.trim($("#merchant_value").val()).length == 0 || $.trim($("#merchant_max").val()).length == 0)
						{
							 $.dialog.alert("大商户扣率阶梯数据不完整");
							 $("#merchant_ladder input:text").focus();
							 return false;
						}

						if($("#merchant_min").val() >= 100){
							 $.dialog.alert("大商户扣率最小比例不能大于100%",function(){$("#merchant_min").focus();});
							 return false;
						}else if($("#merchant_max").val() >= 100){
							 $.dialog.alert("大商户扣率最大比例不能大于100%",function(){$("#merchant_max").focus();});
							 return false;
						}
						
					}
					//----------大商户扣率验证-----------
					
					//大套小可套时，实名商户编号不能输入值
					//修改--调整业务商户编号不进行控制。
					
					if($("#large_small_flag").val() == "0")
					{
						if($.trim($("#merchant_no").val())=='-1' )
						{
							 $.dialog.alert("大套小不可套时，请选择商户名称");
							 return false;
						}else{
							if(!checkMer(merchant_no,id)){
								return false;
							}
						}
					} 
					
	/* 				if($.trim($("#merchant_no").val())=='-1' )
					{
						 $.dialog.alert("请选择商户名称");
						 return false;
					}else{
						if(!checkMer(merchant_no,id)){
							return false
						}
					} */
					if($("#acq_enname").val()!= null && ($("#acq_enname").val()== 'ubs' 
							|| $("#acq_enname").val()== 'hypay' || $("#acq_enname").val()== 'qlhdpay') )
					{
						if($("#secretKey").val()!= null && $("#secretKey").val()== '')
						{
							$.dialog.alert("该通道需添加终端密钥");
							$("#secretKey").focus();
							return false;
						}
					}
					setTerNo();
					 $("#acqMerchantAdd").submit();
				}
				

				
			});	

				
		});
		
		function checkMer(merchant_no,id){
			 var mark = true;
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/acq/merCheck",
		 			data:{"merchant_no":merchant_no,"id":id},
		 			async:false,
		 			dataType: 'json',
				 	success: function(data){
				    	var ret = data.msg;
					    if(ret == "exist"){
					  		$.dialog.alert("该商户已被绑定，请重新选择！");
					  		$("#acq_merchant_no").focus();
					  		mark =  false; 
					  	}
				 	 }
		 			});
					 return mark;
			}
		
          
		
		jQuery(document).ready(function($){
		    var magent_no= "${params['agent_no']}";
		   	showMerchant(null);
            $.ajax({
                  url:'${ctx}/agent/agentUserSelect',//请求的URL
                  cache: false, //不从缓存中取数据
                  data:{'area_level':'2'},//发送的参数
                  type:'POST',//请求类型
                  dataType:'json',//返回类型是JSON
                  timeout:120000,//超时
                  error:function()//出错处理
                  {
                            alert("程序出错!");
                  },
                  success:function(json)//成功处理
                  {
                             var len=json.length;//得到查询到数组长度
                             // $("<select id='agent_no' style='padding:2px;width:157px' onchange='show(this)' class='required' ></select><label class='must'>*</label>").appendTo("#agentContent");//在content中添加select元素
                           	 // $("商户名称：<select id='merchant_no' style='padding:2px;width:157px'  onchange='show(this)'></select>").appendTo("#merchantContent");//在content中添加select元素
                             // $("<option value='-1'>请选择</option>").appendTo("#agent_no");
                             // $("<option value='-1'>请选择</option>").appendTo("#merchant_no");
                       
                            for(var i=0;i<len;i++)//把查询到数据循环添加到select中
                            {
                            	
                            	if(json[i].agent_no == magent_no){
	                            	$("<option value="+json[i].agent_no+" selected >"+json[i].agent_name+"</option>").appendTo("#agent_no");
                            		
                            	}else{
                            		$("<option value="+json[i].agent_no+">"+json[i].agent_name+"</option>").appendTo("#agent_no");
                            	}
                            }
                            
                            if(magent_no!=null && magent_no!=''){
                            	addModifyOption(magent_no);
                            }
                  }
               });
          });
          
            function show(obj){
                         //var obj=event.srcElement; 取得当前事件的对象,也就是你点了哪个select,这里得到的就是那个对象
                       var currentObj=$(obj);//将JS对象转换成jQuery对象,这样才能使用其方法
                       
                 /*  var objNext=$(obj).next("select");
                     var selectNext;
                     objNext.each(function(i){
                               selectNext = this;
                       }); */
                    // var s1=$(obj).nextAll("select");//找到当前点击的后面的select对象
                     document.getElementById("box4").value ="--请选择--";
                     var s1=$("#merchant_no");//找到当前点击的后面的select对象
                     s1.each(function(i){
                        $(this).find("option").each(function(){
                              if($(this).val()!="-1"){
                                  $(this).remove();//循环把它们删除
                              }
                              });
                       });
                     
                         var agentNo=$(obj).val();
                            if(agentNo != '-1'){
                           // alert(agentNo);	
                            
                                  $.ajax({
                                   url:'${ctx}/agent/agentMerchantSelect',
                                         cache:false,
                                         data:{'agentNo':agentNo},
                                         type:'Get',
                                         dataType:'json',
                                         timeout:120000,
                                         error:function()
                                         {
                                              alert("出错啦");
                                         },
                                         success:function(json)
                                         {       
                                               var len=json.length;
                                               if(len!=0)
                                               { 
                                                   for(var i=0;i<len;i++)
                                                   {
                                                        $("<option value="+json[i].merchant_no+">"+json[i].merchant_name+"</option>").appendTo("#merchant_no");
                                                   }
                                               }
                                         }
                                   });            
                            }
       }

          
          function addModifyOption(agentNo){
        	  
        	  var mMerchant_no= "${params['merchant_no']}";
        	  
	          var s1=$("#merchant_no");//找到当前点击的后面的select对象
	          s1.each(function(i){
	             $(this).find("option").each(function(){
	                   if($(this).val()!="-1"){
	                       $(this).remove();//循环把它们删除
	                   }
	                   });
	            });
	          
              // var agentNo=$(obj).val();
             //  var agent_name = obj.options[obj.selectedIndex].text;
                 if(agentNo != '-1'){
                       
                       $.ajax({
                        url:'${ctx}/agent/agentMerchantSelect',
                              cache:false,
                              data:{'agentNo':agentNo},
                              type:'Get',
                              dataType:'json',
                              timeout:120000,
                              error:function()
                              {
                                   alert("出错啦");
                              },
                              success:function(json)
                              {       
                                    var len=json.length;
                                    if(len!=0)
                                    { 
                                        for(var i=0;i<len;i++)
                                        {
                                        	if(json[i].merchant_no == mMerchant_no){
                                             	$("<option value="+json[i].merchant_no+" selected>"+json[i].merchant_name+"</option>").appendTo("#merchant_no");
                                             	document.getElementById("box4").value = json[i].merchant_name;
                                        	}else{
                                        		$("<option value="+json[i].merchant_no+">"+json[i].merchant_name+"</option>").appendTo("#merchant_no");
                                        	}
                                        }
                                	    
                                    }
                              }
                      
                        });            
                 }
             
		}   
          
          function showMerchant(largeSmallSelect){
        	  var large_small_val = $("#large_small_flag").val();
        	  if(large_small_val=='0'){
        		  $("#merchantli").show();
        	  }
        	  if(large_small_val=='1'){
        		  $("#merchantli").hide();
        	  }
          }
		
          function setValue(index) {
        	    var ddl = document.getElementById("merchant_no");
        	    var Value = ddl.options[index].text;
        	    document.getElementById("box4").value = Value;
          }
          
   //-------------------新加系列联动内容LJ----------------------------------   
          
          //根据收单机构联动显示对应比例类型以及大商户扣率、封顶手续费
          function ennameChange(){
        	  //alert("进入事件！");
        	  var acq_enname=$("#acq_enname").val();
        	  var fee_type=$("#fee_type").val();
        	  if(fee_type=='RATIO'){
        		  if(acq_enname =='eptok'){
        			  var obj=document.getElementById('fee_rate');
        			  var innerHTML=' <option value="0.32" >民生类 0.32%</option>  <option value="0.65" >一般类 0.65%</option>  <option value="1.05" >餐娱类 1.05%</option>  <option value="0" >其它</option>';
        			  obj.innerHTML=innerHTML;
              	  }else if(acq_enname =='bill'){
              		  var obj=document.getElementById('fee_rate');
       			      var innerHTML=' <option value="0.307" >民生类 0.307%</option>  <option value="0.64" >一般类 0.64%</option>  <option value="1.05" >餐娱类 1.05%</option> <option value="0" >其它</option>';
       			      obj.innerHTML=innerHTML;
              	  }else if(acq_enname =='tftpay'){
              		  var obj=document.getElementById('fee_rate');
     			      var innerHTML=' <option value="0.33" >民生类 0.33%</option>  <option value="0.663" >一般类 0.663%</option>  <option value="1.0625" >餐娱类 1.0625%</option>  <option value="0" >其它</option>';
     			      obj.innerHTML=innerHTML;
              	  }else if(acq_enname =='zypay'){   
              		  var obj=document.getElementById('fee_rate');
              		  var innerHTML=' <option value="0.32" >民生类 0.32%</option>  <option value="0.65" >一般类 0.65%</option>  <option value="1.05" >餐娱类 1.05%</option> <option value="0" >其它</option>';
    			      obj.innerHTML=innerHTML;
              	  }else if(acq_enname =='ubs'){
              		var obj=document.getElementById('fee_rate');
            		  var innerHTML=' <option value="0.34" >民生类 0.34%</option>  <option value="0.65" >批发类 0.65%</option><option value="0" >其它</option> ';
  			      	obj.innerHTML=innerHTML;
              	  }else if(acq_enname =='hypay'){
                		var obj=document.getElementById('fee_rate');
                		var innerHTML=' <option value="0.34" >民生类 0.34%</option>  <option value="0.65" >批发类 0.65%</option><option value="0.70" >一般类 0.70%</option>  <option value="1.10" >餐娱类 1.10%</option><option value="0" >其它</option> ';
    			      	obj.innerHTML=innerHTML;
                	  }
        	  } else if(fee_type=='CAPPING'){
        		  if(acq_enname =='eptok'){
        			  $("#rate_type_li").show();
              		  $("#fee_rate_li").show();
         			  $("#special_fee_rate").hide();
        			  var obj=document.getElementById('rate_type');
        			  var innerHTML=' <option value="101"  > 批发对私封顶A类</option>  <option value="102" > 批发对公类</option>  <option value="103" > 车房对公类</option>'   ;
        			  obj.innerHTML=innerHTML;
        			  var obj_fee_rate= document.getElementById('fee_rate');
             		  var innerHTML_fee_rate=' <option value="0.65" >0.65%</option> '   ;
             		  obj_fee_rate.innerHTML=innerHTML_fee_rate;   
             		  document.getElementById('fee_max_amount').value='24';
        		  }
                  if(acq_enname =='bill'){
                	  $("#rate_type_li").show();
              		  $("#fee_rate_li").show();
         			  $("#special_fee_rate").hide();
        			  var obj=document.getElementById('rate_type');
        			  var innerHTML=' <option value="103" > 批发类</option>  <option value="104" > 车房类</option>';
        			  obj.innerHTML=innerHTML;
        			  var obj_fee_rate= document.getElementById('fee_rate');
             		  var innerHTML_fee_rate=' <option value="0.64" >0.64%</option> ';
             		  obj_fee_rate.innerHTML=innerHTML_fee_rate; 
             		  document.getElementById('fee_max_amount').value='22';
              	  } 
                  if(acq_enname =='tftpay'){
                	  $("#rate_type_li").show();
              		  $("#fee_rate_li").show();
         			  $("#special_fee_rate").hide();
       			      var obj=document.getElementById('rate_type');
       			      var innerHTML=' <option value="103" > 批发类</option>  <option value="104" > 车房类</option>';
       			      obj.innerHTML=innerHTML;
       			      var obj_fee_rate= document.getElementById('fee_rate');
       			      var innerHTML_fee_rate=' <option value="0.78" >0.78%</option>';
       			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
       			      document.getElementById('fee_max_amount').value='26';
                 }
                 if(acq_enname =='zypay'){
       			      /* var obj=document.getElementById('rate_type');
       			      var innerHTML=' <option value="103" > 批发类</option>  <option value="104" > 车房类</option>'   ;
       			      obj.innerHTML=innerHTML;
       			      var obj_fee_rate= document.getElementById('fee_rate');
           		      var innerHTML_fee_rate=' <option value="0.78" >0.78%</option> '   ;
           		      obj_fee_rate.innerHTML=innerHTML_fee_rate; 
           		      document.getElementById('fee_max_amount').value='26'; */
           		      
                		$("#rate_type_li").hide();
                		$("#fee_rate_li").hide();
           			    $("#special_fee_rate").show(); 
            	 }else if(acq_enname =='ubs'){
            		 $("#rate_type_li").show();
             		  $("#fee_rate_li").show();
        			  $("#special_fee_rate").hide();
	       			  var obj=document.getElementById('rate_type');
	       			  var innerHTML=' <option value="101"  > 批发对私封顶A类</option>  <option value="102" > 批发对公类</option>  <option value="103" > 车房对公类</option>'   ;
	       			  obj.innerHTML=innerHTML;
	       			  var obj_fee_rate= document.getElementById('fee_rate');
	            		  var innerHTML_fee_rate=' <option value="0.65" >0.65%</option> '   ;
	            		  obj_fee_rate.innerHTML=innerHTML_fee_rate;   
	            		  document.getElementById('fee_max_amount').value='24';
            	 }
        	  }else if(fee_type=='LADDER'){
        		
        			
        			 var obj=document.getElementById('rate_type');
       			     var innerHTML=' <option value="105"  > 批发对私不封顶类</option>  <option value="106" > 汽车对私不封顶类</option>'   ;
       			     obj.innerHTML=innerHTML;
       			     document.getElementById('ladder_min').value='0.65'; 
       			     document.getElementById('ladder_value').value='7000'; 
       			     document.getElementById('ladder_max').value='0.35'; 
        		 
        	  } 
          } 
          
          
          
          //根据收单机构商户名称显示大商户扣率，大商户封顶手续费
          function rateChange(){
        	  var acq_merchant_name=$("#acq_merchant_name").val();
        	  var fee_type=$("#fee_type").val();
        	  if(acq_merchant_name != ''){
                   $.ajax({
                   url:'${ctx}/acq/findMerchantRate',
                         cache:false,
                         data:{'acq_merchant_name':acq_merchant_name},
                         type:'POST',
                         dataType:'json',
                         error:function()
                         {
                              alert("查询出错，请检查数据！");
                         },
                         success:function(json)
                         {       
                              if(json.length!=0 && json.msg=='exist')  //扣率类型为扣率时，查询显示对应大商户扣率
                              { 
                            	  if(fee_type=='RATIO'){
                            		  var fee_rate=json.fee_rate;
    	                         	  document.getElementById('merchant_Drate').value=fee_rate; 
                            	  }else if(fee_type=='CAPPING'){  //扣率类型为封顶时，查询显示对应大商户扣率以及大商户封顶手续费
                            		  var fee_rate=json.fee_rate;
    	                         	  document.getElementById('merchant_Drate').value=fee_rate; 
    	                         	  var merchant_max_amount=json.fee_max_amount;
    	                         	  if(merchant_max_amount==null|| merchant_max_amount==""){
    	                         		 $.dialog.alert("该收单机构商户无对应封顶手续费！");
    	                         	  }else{
    	                         		 document.getElementById('merchant_max_amount').value=merchant_max_amount; 
    	                         	  }
                            		  
	                              }else if(fee_type=='LADDER'){  //扣率类型为阶梯时
    	                         	  var ladder_min=json.ladder_min;
    	                         	  var ladder_value=json.ladder_value;
    	                         	  var ladder_max=json.ladder_max;
    	                         	  if(ladder_min==null|| ladder_value==null|| ladder_max==null){
    	                         		 $.dialog.alert("该收单机构商户无对应大商户扣率！");
    	                         	  }else{
    	                         		  document.getElementById('merchant_min').value=ladder_min; 
        	                         	  document.getElementById('merchant_value').value=ladder_value; 
        	                         	  document.getElementById('merchant_max').value=ladder_max; 
    	                         	  }
	                              }
                              }else if(json.msg=='noexist'){
                            	  $.dialog.alert("该收单机构商户无对应扣率信息！");
                              }
                         }
                   });            
            }
          }
          
          
          //根据比例类型联动封顶手续费、比例
          function showfeeRate(){
        	  var acq_enname=$("#acq_enname").val();
        	  var fee_type=$("#fee_type").val();
        	  var rate_type=$("#rate_type").val();
        	  if(fee_type=='CAPPING'){
        		  if(acq_enname=='eptok'){
        			  if(rate_type=='101'){
        				  document.getElementById('fee_max_amount').value='24';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="0.65" >0.65%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
        				  
        				  
        				/*   var tmp = document.getElementById('fee_rate');
        				  for(var i= 0;i<=tmp.length;i++){
        				        if(tmp.options[i].value == '0.65'){
        				        	tmp.options[i].selected = true;
        				        }
        				   } */
        				  
        			  }else if(rate_type=='102'){
        				  document.getElementById('fee_max_amount').value='25';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="0.65" >0.65%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
           			      
        			  }else if(rate_type=='103'){
        				  document.getElementById('fee_max_amount').value='71';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="1.05" >1.05%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
        				  
        			  } 
        		  }else if(acq_enname=='bill'){
        			  if(rate_type=='103'){
        				  document.getElementById('fee_max_amount').value='22';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="0.64" >0.64%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
        				  
        			  }else if(rate_type=='104'){
        				  document.getElementById('fee_max_amount').value='70';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="1.05" >1.05%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
           			      
        			  }
        		  }else if(acq_enname =='tftpay'){
        			  if(rate_type=='103'){
        				  document.getElementById('fee_max_amount').value='26';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="0.78" >0.78%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
           			      
        
        			  }else if(rate_type=='104'){
        				  document.getElementById('fee_max_amount').value='80';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="1.25" >1.25%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
           			      
        			  }
        		  }else if(acq_enname=='ubs'){
        			  if(rate_type=='101'){
        				  document.getElementById('fee_max_amount').value='24';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="0.65" >0.65%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
        				  
        				  
        				/*   var tmp = document.getElementById('fee_rate');
        				  for(var i= 0;i<=tmp.length;i++){
        				        if(tmp.options[i].value == '0.65'){
        				        	tmp.options[i].selected = true;
        				        }
        				   } */
        				  
        			  }else if(rate_type=='102'){
        				  document.getElementById('fee_max_amount').value='25';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="0.65" >0.65%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
           			      
        			  }else if(rate_type=='103'){
        				  document.getElementById('fee_max_amount').value='71';
        				  var obj_fee_rate= document.getElementById('fee_rate');
           			      var innerHTML_fee_rate=' <option value="1.05" >1.05%</option> ';
           			      obj_fee_rate.innerHTML=innerHTML_fee_rate;
        				  
        			  } 
        		  }
        	  }else if(fee_type=='LADDER'){
        		  if(acq_enname=='eptok'){
        			  if(rate_type=='105'){
        				  document.getElementById('ladder_min').value='0.65'; 
            			  document.getElementById('ladder_value').value='7000'; 
            			  document.getElementById('ladder_max').value='0.35'; 
        			  }else if(rate_type=='106'){
        				  document.getElementById('ladder_min').value='1.05'; 
            			  document.getElementById('ladder_value').value='10000'; 
            			  document.getElementById('ladder_max').value='0.8';
        			  }
        		  } else  if(acq_enname=='ubs'){
        			  if(rate_type=='105'){
        				  document.getElementById('ladder_min').value='0.65'; 
            			  document.getElementById('ladder_value').value='7000'; 
            			  document.getElementById('ladder_max').value='0.35'; 
        			  }else if(rate_type=='106'){
        				  document.getElementById('ladder_min').value='1.05'; 
            			  document.getElementById('ladder_value').value='10000'; 
            			  document.getElementById('ladder_max').value='0.8';
        			  }
        		  } 
        	  }
          }
          
          
          //若比例选择其它则出现比例（特殊）输入框
          function showSpecialRate(){
               var fee_rate=$("#fee_rate").val();
               if(fee_rate=='0'){
            	   $("#special_fee_rate").show();
               }else{
            	   $("#special_fee_rate").hide();  
               }
          }
          
     
          
          
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：收单机构管理>收单机构商户信息</div>
    <form:form id="acqMerchantAdd" action="${ctx}/acq/acqMerchantAddSave" method="post">
    <div style="position:absolute;margin-left:100px;margin-top:53px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 130px;border:none;">
         	</div>
    <div class="item">
    	<div class="title">基本信息</div>
    	
    	<ul>
    		<li style="display:none"><label>id</label><input type="text" id="id"  name="id"  value="${params['id']}"/></li>
    		
    	<%-- 	<li style=""><label>acq_enname</label><input type="text" id="hidden_acq_enname"  name="hidden_acq_enname"  value="${params['acq_enname']}"/></li> --%>
    		
			<%--<li><label>代理商：</label><u:select value="${params['agent_no']}"  stype="agent" sname="agent_no" id="agent_no" style="width:159px;"/><label class="must">*</label></li>--%>
			
			<li id="agentContent"><label>代理商：</label><select id="agent_no"  name="agent_no"   style="padding:2px;width:159px"  onchange="show(this)" class="required" >
				<option value='-1'>请选择</option>
			</select><label class="must">*</label></li>
			
			<li><label>收单机构：</label><select id="acq_enname" name="acq_enname" style="padding:2px;width:159px" class="required" onchange="ennameChange();" >
					<c:forEach items="${acqOrgList}" var="m">
						<option value="${m.acq_enname}" id="acq_enname" <c:if test="${m.acq_enname eq params['acq_enname']}">selected = "selected"</c:if>>${m.acq_cnname}</option>	
					</c:forEach>
				</select><label class="must">*</label></li>
			<li><label>收单机构商户编号：</label><input type="text" id="acq_merchant_no"  name="acq_merchant_no"   value="${params['acq_merchant_no']}" class="required" /><label class="must">*</label></li>
			<li><label>收单机构商户名称：</label><input type="text" id="acq_merchant_name"  name="acq_merchant_name"  value="${params['acq_merchant_name']}" class="required" onchange="rateChange()" /><label class="must">*</label></li>
			<li><label>MCC：</label><input type="text" id="mcc"  name="mcc"  value="${params['mcc']}" class="required" /><label class="must">*</label></li>
<%--			<li><label>签约扣率</label><input type="text" style="width:45px;" id="fee_rate"  name="fee_rate"  value="${params['fee_rate']}" class="required  number" />%--%>
<%--				到<input type="text"  style="width:45px;"  id="fee_max_amount"  name="fee_max_amount"  value="${params['fee_max_amount']}" class="money" />封顶<label class="must">*</label>--%>
<%--			</li>--%>
			<li style="width: 400px"><label style="width: 90px">是否A类标志：</label><select style="padding:2px;width:69px" id="large_small_flag" name="large_small_flag"  onchange="showMerchant(this);" >
				<option value="0" <c:if test="${params['large_small_flag'] eq '0' }">selected="selected"</c:if>>否</option>
				<option value="1" <c:if test="${params['large_small_flag'] eq '1' }">selected="selected"</c:if>>是</option>
				</select>
				是否代付：
            	<select  name="rep_pay"  id="rep_pay"  style="padding:2px;width:85px">
            		<option value="-1">--请选择--</option>
            		<option value="1">否</option>
            		<option value="2">是</option>
            	</select><label class="must">*</label>
				</li>
				<%--<li id="merchant_rate_li" style="width: 130px"><label >是否代付：</label>
            	<select  name="rep_pay"  id="rep_pay"  style="padding:2px;width:60px">
            		<option value="-1">--请选择--</option>
            		<option value="1">否</option>
            		<option value="2">是</option>
            	</select><label class="must">*</label>
            </li>
			--%><li class=""><label style="width:105px">扣率类型：</label>
			    <select id="fee_type" name="fee_type" style="padding:2px;width:159px">
	         		<option value="RATIO" <c:out value="${params['fee_type'] eq 'RATIO'?'selected':'' }"/>>扣率</option>
	         		<option value="CAPPING" <c:out value="${params['fee_type'] eq 'CAPPING'?'selected':'' }"/>>封顶</option>
	         		<option value="LADDER" <c:out value="${params['fee_type'] eq 'LADDER'?'selected':'' }"/>>阶梯</option>
	        	</select>
	        </li>
	        <li id="rate_type_li" style=""><label style="width:105px">比例类型：</label>
	            <select id="rate_type" name="rate_type" style="padding:2px;width:160px;" onchange="showfeeRate()">
	                <option value="101" <c:out value="${params['rate_type'] eq '101'?'selected':'' }"/>>批发对私封顶A类</option>
	         		<option value="102" <c:out value="${params['rate_type'] eq '102'?'selected':'' }"/>>批发对公类</option>
	         		<option value="103" <c:out value="${params['rate_type'] eq '103'?'selected':'' }"/>>车房对公类</option>
	            </select>
	        </li>
            <li id="fee_rate_li" style=""><label style="width:105px">比例：</label>
                <select id="fee_rate" name="fee_rate" style="padding:2px;width:160px" onchange="showSpecialRate()" >
                    <option value="0.32" <c:out value="${params['fee_rate'] eq '0.32'?'selected':'' }"/>>0.32%</option>
       		        <option value="0.65" <c:out value="${params['fee_rate'] eq '0.65'?'selected':'' }"/>>0.65%</option>
       		        <option value="1.05" <c:out value="${params['fee_rate'] eq '1.05'?'selected':'' }"/>>1.05%</option>
                </select>
	        </li> 
            <li id="merchant_rate_li" style="width: 300px"><label style="width:105px">大商户扣率：</label>
            <input type="text" style="width:150px;" class="input number" value="${params['merchant_Drate']}" name="merchant_Drate" id="merchant_Drate"  />%</li>
	         
	        <li id="special_fee_rate"><label style="width:106px">比例（特殊）：</label>
               <input type="text"  style="width:151px"  class="input number" value="${params['special_fee_rate']}" name="fee_rate_ForCa" id="fee_rate_ForCa"  />%
            </li>
			<li id="fee_max_amount_li" style="width: 400px"><label>封顶手续费：</label><input type="text"  class="input money" style="width:150px;" value="${params['fee_max_amount']}" name="fee_max_amount" id="fee_max_amount" />（元）</li>
			<li id="fee_ladder_li"  class="column2">
				<label>阶梯设置：</label>
				<input type="text"  class="input number" name="ladder_min" id="ladder_min" value="${params['ladder_min']}"  style="width: 90px"/>%
				&lt;
				<input type="text"  class="input money" name="ladder_value" id="ladder_value" value="${params['ladder_value']}"  style="width: 90px"/>（元）
				&lt;
				<input type="text"  class="input number" name="ladder_max" id="ladder_max" value="${params['ladder_max']}"  style="width: 90px"/>%
			</li>
			<div class="clear"></div>
			
			<li id="merchant_max_amount_li" style="width: 400px"><label>大商户封顶手续费：</label><input type="text"  class="input money" style="width:150px;" value="${params['merchant_max_amount']}" name="merchant_max_amount" id="merchant_max_amount" />（元）</li>
			<li id="merchant_ladder_li"  class="column3">
				<label>大商户扣率：</label><input type="text"  class="input number" name="merchant_min" id="merchant_min" value="${params['merchant_min']}"  style="width: 90px"/>%
				&lt;
				<input type="text"  class="input money" name="merchant_value" id="merchant_value" value="${params['merchant_value']}"  style="width: 90px"/>（元）
				&lt;
				<input type="text"  class="input number" name="merchant_max" id="merchant_max" value="${params['merchant_max']}"  style="width: 90px"/>%
			</li>
    	</ul>
    	<div class="clear"></div>
    	<div id="merchantDiv">
	    	<div class="title">实名商户</div>
	    	<ul style="font-size:13px;width: 500px;">
	    		<li id="merchantli" style="position:relative;width: 400px;"><label>商户编号：</label>
	    		<input id="merchant_no"  name="merchant_no"  maxlength="20">
	    		<!-- (大套小无需填写) -->(A类无需填写)   
	    		</li>
	    	</ul>
    	</div>
    	<div class="clear"></div>
    	<div id="merchantDiv">
    		<div class="title">添加终端</div>
    		<ul id="addTerUl">
    			<li id="addTer0">
    				<input type="hidden"  name="terNo"  id="terNo">
    				<label>收单机构终端编号：</label><input type="text"  name="terNoCount" id="addTerInput0"  maxlength="20" class="required"/>   
    			</li>
    			<li id="addTer0">
    				<input class="button blue"  type="button"  id="addInp" value="+"  />
    				<input class="button blue"  type="button"  id="redInp"  value="-" /> 
    			</li>
    		</ul>
    		<div>
    		<ul id="addTerUl0">
    		</ul>
    		</div>
    		<ul id="secretUl" style="display:none">
    			<li id="addTerSecret1">
    				<label>收单机构终端密钥：</label><input type="text"  name="secretKey" id="secretKey" />   
    			</li>
    		</ul>
    		<div class="clear"></div>
    	</div>
    	<div id="merchantDivT">
    		<ul id="TerUl">
    			<li id="add"></li><li>
    			</ul>
    	</div>
    	<div class="clear"></div>
    	<div class="search_btn clear">
    		<input   class="button blue  " type="button" id="addButton"   value="保存"  />
    		<input class="button blue" type="button" id="backButton" value="返回" onclick="javascript:window.location.href='${ctx}/acq/merchantQuery'" />
    	</div>
    </div>
    </form:form>
   
  </div>
  <script type="text/javascript">
 function setTerNo(){
	 var terminal_no = document.getElementsByName("terNoCount");
		var terminalNoValue = "";
		for (var i = 0; i < terminal_no.length; i++) {
			if (terminal_no[i].value != null && terminal_no[i].value != "") {
				if (terminalNoValue == "") {
					terminalNoValue = terminal_no[i].value;
				} else {
					terminalNoValue = terminalNoValue + ";"
							+ terminal_no[i].value;
				}
			}
		}
		document.getElementById("terNo").value = terminalNoValue;
	  //alert(terminalNoValue);
 }
  $(function(){
	  //添加
	  $("#addInp").click(function(){
		 var count = (($("#addTerUl0 li").length+1)+1);
		 if(count < 21){
		  $("#addTerUl0").append("<li id='addTer"+count+"'><label>"+count+"</label><input type='text'  name='terNoCount' id='addTer"+count+"' maxlength='20'  /></li><div class='clear' id='addTerDiv"+count+"'></div>");
		  //$("#terNo").val(count);
		 }else{
			 alert("当前最多可添加20个!");
		 }
	 });
	  
	//删除
	  $("#redInp").click(function(){
		  var rcount = ($("#addTerUl0 li").length+1);
		  if(rcount == 1){
			  alert("至少要保留一个!");
		  }else{
			  $("#addTer"+rcount).remove();
		  }
	 });
	  $("#acq_enname").change(function(){
			var acq_enname=$("#acq_enname").val();
			if(acq_enname == "ubs" || acq_enname == "hypay" || acq_enname == "qlhdpay")
			{
				$("#secretUl").show();
			}else{
				$("#secretUl").hide();
			}
		});

  });
  </script>
  <script type="text/javascript">
  $(function(){
		var cus = 0;
	    var classname = "";
	    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
	    $("#agentInput").keyup(function(event) {
		    var arry = new Array();
		    $("[name=agent_no]").find("option").each(function(i, n) {
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
	                            $("[name=agent_no]").find("option").each(function(i, n) {
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
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
	                
	                $("[name=agent_no]").find("option").each(function(i, n) {
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
	    
	    $("[name=agent_no]").change(function(){
	    	var agent_Name =  $("[name=agent_no]").find("option:selected").text(); //集群所属所属代理商名称
			if(agent_Name != ""){
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
	    	});
	});
  </script>
</body>
