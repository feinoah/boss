<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#agentModify
		{
			padding:10px;
		}
	
		#agentModify ul
		{
			overflow:hidden;
		} 
		
		#agentModify ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:310px;
			heigth:32px;
			line-height:32px;
		}
		
		#agentModify ul li.column2
		{
			width:540px;
		}
		
		#agentModify ul li.column3
		{
			width:810px;
		}
		
		#agentModify ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:110px;
		}
		
		#agentModify ul li label.must		
		{
			display:-moz-inline-box;
			display:inline-block;
			width:5px;
			text-align:center;
			color:red;
		}
		
		#agentModify ul li .area
		{
			width:75px;
		}
		
		
		
		#agentModify ul li.long
		{
			width:440px;
		}
		
		
		#agentModify div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
		
		#file_uploadUploader {
			vertical-align:middle;
			margin-left:10px;
		}
		#mlogo_uploadUploader{
			vertical-align:middle;
			margin-left:10px;
		}
		
		input[type="text"].readonly{background:#eee;}
	</style>
	<script type="text/javascript">
		
		var EMAIL_REG = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
		var MOBILE_REG =  /^1[0-9]{10}$/;
		
		var NUM_STR_REG = /^([0-9])+$/; //数字字符串
		var INTEGER_REG =  /^(0|([1-9][0-9]*))$/; //正整数
		var MONEY_REG = /(([0-9]+\.[0-9]{1,2}))$/; //金额正则表达式
		var RATE_REG = /(([0-9]+\.[0-9]{1,4}))$/; //费率正则表达式
		
		
		//分润时使用的的正则
		var INTEGER_REG_0=  /^(0|([1-9][0-9]*))$/; //非负整数：金额
		//var INTEGER_REG_fee=  /^(0|([1-9][0-9]{0,1}))$/; //非负整数且是两位：0~99.
		var INTEGER_REG_fee=  /^(?:0|[1-9][0-9]?|100)$/; //非负整数且是：0~100..
		var INTEGER_REG_fee2 = /^(?:0|[1-9][0-9]?|100)$/;//非负整数且是两位：0~100.
		
	
		
 		uploadFile("file_upload","clientLogo",false,"no","100","200");
		uploadFile("mlogo_upload","managerLogo",false,"no","100","200"); 
		
		function updateAgentInfo(agentNo){	
		 //验证部分
   
				var  fr_flag=true;	
        var  lastAmout=0;	
		 	//分润信息验证----输入的金额部分：----------start
			$('#fr_add_input input:even').each(function(i,n){
         var  inputCount=$('#fr_add_input input:even').length;
		    	//alert(i+'---'+'---'+'---'+$(n).val());
			    var val=$.trim($(n).val());
          
			    //1.必须是非负整数
			    if(!INTEGER_REG_0.test(val)){
			        fr_flag=false;	
						     	var dialog = $.dialog({title: '验证分润规则',lock:true,content: '分润规则:金额必须为非负整数！',icon: 'error.gif',ok: function(){
						         	$(n).focus().select();
								    }
								});
			       return false;
			     }
         //2.对于金额输入框中的奇数下标（也即行尾），其int值必须大于行开头的int值。
         if((i%2==1)&&(i!=inputCount-1)&&parseInt(val)<=parseInt(lastAmout)){
             //alert(i+'-'+val+':'+lastAmout+'=='+(val<=lastAmout));
              fr_flag=false; 
              var dialog = $.dialog({title: '验证分润规则',lock:true,content: '阶梯结尾金额必须大于阶梯开始金额！',icon: 'error.gif',ok: function(){
                $(n).focus().select();
                   }
               });
           return false;
         }
         //3..对于金额输入框中的偶数下标（也即行开头），其int值必须等于是上一阶梯的尾的int值得。
           if((i%2==0)&&parseInt(val)!=parseInt(lastAmout)){
              fr_flag=false; 
              var dialog = $.dialog({title: '验证分润规则',lock:true,content: '阶梯开始金额必须等于上一阶梯结尾金额！',icon: 'error.gif',ok: function(){
                $(n).focus().select();
                   }
               });
           return false;
         }
         //每一个each最后，都把该input的值，保存到lastAmout中，供each()的下一次使用。
         lastAmout=val;
         
			}) 
					//each函数结尾---------分润信息验证----输入的金额部分：--------end
			
			if(!fr_flag){
			    return false;
			}
			
			 	//分润信息验证--验证费率部分：-----------start
				$('#fr_add_input input:odd').each(function(i,n){
		    	//alert(i+'---'+'---'+'---'+$(n).val());
			   var val=$.trim($(n).val());
			 
			    if(!INTEGER_REG_fee.test(val)){
			      fr_flag=false;	
						     	var dialog = $.dialog({title: '验证分润规则',lock:true,content: '分润规则:费率必须在0%~100%之间！',icon: 'error.gif',ok: function(){
						         	$(n).focus().select();
								    }
								});
			     return false;
			     }
			}) 
   
			 //each函数结尾---------分润信息验证----验证费率部分：---------------end
       
			if(!fr_flag){
			    return false;
			}
			
     //分润部分验证完毕-------------end。
     
     
		 var  smlastAmout=0;	
		 	//分润信息验证----输入的金额部分：----------start
			$('#smfr_add_input input:even').each(function(i,n){
         var  inputCount=$('#smfr_add_input input:even').length;
		    	//alert(i+'---'+'---'+'---'+$(n).val());
			    var val=$.trim($(n).val());
          
			    //1.必须是非负整数
			    if(!INTEGER_REG_0.test(val)){
			        fr_flag=false;	
						     	var dialog = $.dialog({title: '验证分润规则',lock:true,content: '分润规则:金额必须为非负整数！',icon: 'error.gif',ok: function(){
						         	$(n).focus().select();
								    }
								});
			       return false;
			     }
         //2.对于金额输入框中的奇数下标（也即行尾），其int值必须大于行开头的int值。
         if((i%2==1)&&(i!=inputCount-1)&&parseInt(val)<=parseInt(smlastAmout)){
             //alert(i+'-'+val+':'+smlastAmout+'=='+(val<=smlastAmout));
              fr_flag=false; 
              var dialog = $.dialog({title: '验证分润规则',lock:true,content: '阶梯结尾金额必须大于阶梯开始金额！',icon: 'error.gif',ok: function(){
                $(n).focus().select();
                   }
               });
           return false;
         }
         //3..对于金额输入框中的偶数下标（也即行开头），其int值必须等于是上一阶梯的尾的int值得。
           if((i%2==0)&&parseInt(val)!=parseInt(smlastAmout)){
              fr_flag=false; 
              var dialog = $.dialog({title: '验证分润规则',lock:true,content: '阶梯开始金额必须等于上一阶梯结尾金额！',icon: 'error.gif',ok: function(){
                $(n).focus().select();
                   }
               });
           return false;
         }
         //每一个each最后，都把该input的值，保存到lastAmout中，供each()的下一次使用。
         smlastAmout=val;
         
			}) 
			//each函数结尾---------分润信息验证----输入的金额部分：--------end
			
			if(!fr_flag){
			    return false;
			}
	 	
		 	//小宝分润信息验证--验证费率部分：-----------start
			$('#smfr_add_input input:odd').each(function(i,n){
	    	//alert(i+'---'+'---'+'---'+$(n).val());
		   var val=$.trim($(n).val());
		 
		    if(!INTEGER_REG_fee2.test(val)){
		      fr_flag=false;	
					     	var dialog = $.dialog({title: '验证分润规则',lock:true,content: '分润规则:费率必须在0%~100%之间！',icon: 'error.gif',ok: function(){
					         	$(n).focus().select();
							    }
							});
		     return false;
		     }
		}); 
			
      //each函数结尾---------分润信息验证----验证费率部分：---------------end
   
		if(!fr_flag){
		    return false;
		}
      
		//小宝分润信息验证--验证费率部分：-----------start
		$('#ylst_add_input input:odd').each(function(i,n){
    	//alert(i+'---'+'---'+'---'+$(n).val());
	   var val=$.trim($(n).val());
	 
	    if(!INTEGER_REG_fee.test(val)){
	      fr_flag=false;	
				     	var dialog = $.dialog({title: '验证分润规则',lock:true,content: '分润规则:费率必须在0%~100%之间！',icon: 'error.gif',ok: function(){
				         	$(n).focus().select();
						    }
						});
	     return false;
	     }
	}); 
		
  //each函数结尾---------分润信息验证----验证费率部分：---------------end

	if(!fr_flag){
	    return false;
	}
     //分润部分验证完毕-------------end。
	      
	      
			
   //代理商行业计算成本----start
   var locked_status = '${params.locked_status}';
   if(locked_status!=1){
	     var feeByYeWuFlag=true;
	      $('#feeByYeWu_add_input input').each(function(i,n){
	       
	      var val=$.trim($(n).val());
	       //alert(i+'--'+val.length);
	       if(val==''){
	            feeByYeWuFlag=false;
	            var dialog = $.dialog({title: '验证手续费扣率',lock:true,content: '手续费扣率不能为空！',icon: 'error.gif',ok: function(){
	                $(n).focus().select();
	            }
	        });
	        return false;
	        }
	        
	   	}) ;
	   
	   if(!feeByYeWuFlag){
	     return false;
	   }
   }
    
      //代理商行业计算成本-------end
      
			
				var flag = true;
				
				var agentName = $.trim($("#agentName").val());
				if(agentName ==null || agentName==''){
					var dialog = $.dialog({title: '错误',lock:true,content: '代理商名称不能为空',icon: 'error.gif',ok: function(){
			        	$("#agentName").focus();
					    }
					});
					return false;
				}
				
				flag =checkAgentName(agentName,agentNo);
				if(flag ==false)
				{
					return false;
				}
				
				var agentLinkName = $.trim($("#agentLinkName").val());
				if(agentLinkName ==null || agentLinkName==''){
					var dialog = $.dialog({title: '错误',lock:true,content: '代理商联系人不能为空',icon: 'error.gif',ok: function(){
			        	$("#agentLinkName").focus();
					    }
					});
					return false;
				}
				
				var agentLinkTel = $.trim($("#agentLinkTel").val());
				if(agentLinkTel ==null || agentLinkTel==''){
					var dialog = $.dialog({title: '错误',lock:true,content: '联系电话不能为空',icon: 'error.gif',ok: function(){
			        	$("#agentLinkTel").focus();
					    }
					});
					return false;
				}
				
				var agentArea = $.trim($("#agentArea").val());
				if(agentArea ==null || agentArea==''){
					var dialog = $.dialog({title: '错误',lock:true,content: '代理商区域不能为空',icon: 'error.gif',ok: function(){
			        	$("#agentArea").focus();
					    }
					});
					return false;
				}
				
				var agentAreaType = $.trim($("#agentAreaType").val());
				if(agentAreaType == '-1')
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '请选择代理商级别',icon: 'error.gif',ok: function(){
				        	$("#agentAreaType").focus();
					    }
					});
					return false;
				}
				
				var profitSharing = $.trim($("#profitSharing").val());
				if(profitSharing == '-1')
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '请选择是否分润',icon: 'error.gif',ok: function(){
				        	$("#profitSharing").focus();
					    }
					});
					return false;
				}
				
				var account_name = $.trim($("#account_name").val());
				if(account_name.length == 0)
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '开户名不能为空',icon: 'error.gif',ok: function(){
				        	$("#account_name").focus();
					    }
					});
					flag = false;
					return false;
				}

				var account_type = $.trim($("#account_type").val());
				if(account_type.length == 0)
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '账户类型不能为空',icon: 'error.gif',ok: function(){
				        	$("#account_type").focus();
					    }
					});
					flag = false;
					return false;
				}


				var bank_name = $.trim($("#bank_name").val());
				if(bank_name.length == 0)
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '开户银行全称不能为空',icon: 'error.gif',ok: function(){
				        	$("#bank_name").focus();
					    }
					});

					flag = false;
					return false;
				}

				var account_no = $.trim($("#account_no").val());
				if(account_no.length == 0)
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '开户账号不能为空',icon: 'error.gif',ok: function(){
				        	$("#account_no").focus();
					    }
					});

					flag = false;
					return false;
				}
				else
				{
					if( !account_no.match(NUM_STR_REG))
					{
						var dialog = $.dialog({title: '错误',lock:true,content: '开户账号格式出错：正确格式例子为：9558812345678901234',icon: 'error.gif',ok: function(){
				        	$("#account_no").focus();
						    }
						});
	
						flag = false;
						return false;
					}
				}

				var cnaps_no = $.trim($("#cnaps_no").val());
				if(cnaps_no.length == 0)
				{
					var dialog = $.dialog({title: '错误',lock:true,content: '联行行号不能为空',icon: 'error.gif',ok: function(){
				        	$("#cnaps_no").focus();
					    }
					});

					flag = false;
					return false;
				}
				else
				{
					if( !cnaps_no.match(NUM_STR_REG))
					{
						var dialog = $.dialog({title: '错误',lock:true,content: '联行行号格式出错：正确格式例子为：123456',icon: 'error.gif',ok: function(){
				        	$("#cnaps_no").focus();
						    }
						});
	
						flag = false;
						return false;
					}
				}
				
				
				var agentRate = $.trim($("#agentRate").val());
				if(agentRate.length != 0){
					if(!agentRate.match(INTEGER_REG) && !agentRate.match(RATE_REG))
					{
						var dialog = $.dialog({title: '错误',lock:true,content: '协议费率应为数字，并且小数点后最大4位小数',icon: 'error.gif',ok: function(){
				        	$("#agentRate").focus();
						    }
						});
	
						return false;
					}
				}
				
				var agentPay = $.trim($("#agentPay").val());
				if(agentPay.length != 0){
					if(!agentPay.match(INTEGER_REG) && !agentPay.match(MONEY_REG))
					{
						var dialog = $.dialog({title: '错误',lock:true,content: '代理商接入费应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
				        	$("#agentPay").focus();
						    }
						});
	
						return false;
					}
				}
				
				
				var mixAmout = $.trim($("#mixAmout").val());
				if(mixAmout.length != 0){
					if(!mixAmout.match(INTEGER_REG) && !mixAmout.match(MONEY_REG))
					{
						var dialog = $.dialog({title: '错误',lock:true,content: '最小清算金额应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
				        	$("#mixAmout").focus();
						    }
						});
	
						return false;
					}
				}
				
				var is_invest = $.trim($('#is_invest').val());
				var invest_amount = $.trim($('#invest_amount').val());
				var common_deposit_amount = $.trim($('#common_deposit_amount').val());
				var common_deposit_rate = $.trim($("#common_deposit_rate").val());
				var over_deposit_rate = $.trim($("#over_deposit_rate").val());
				var deposit_rate = $.trim($("#deposit_rate").val());
				
				if(is_invest === '1'){
					if(invest_amount.length === 0){
						var dialog = $.dialog({title: '错误',lock:true,content: '投资金额不能为空',icon: 'error.gif',ok: function(){
				        	$("#is_invest").focus();
						    }
						});
						flag = false;
						return false;
					}else{
						if(!invest_amount.match(RATE_REG) && !invest_amount.match(INTEGER_REG)){
							var dialog = $.dialog({title: '错误',lock:true,content: '投资金额应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
				        		$("#is_invest").focus();
						    	}
							});
							flag = false;
							return false;
						}
					}
					
					if(common_deposit_amount.length === 0){
						var dialog = $.dialog({title: '错误',lock:true,content: '常规提现额度不能为空',icon: 'error.gif',ok: function(){
				        	$("#common_deposit_amount").focus();
						    }
						});
						flag = false;
						return false;
					}else{
						if(!common_deposit_amount.match(RATE_REG) && !common_deposit_amount.match(INTEGER_REG)){
							var dialog = $.dialog({title: '错误',lock:true,content: '常规提现额度应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
				        		$("#common_deposit_amount").focus();
						    	}
							});
							flag = false;
							return false;
						}
					}
					
					if(common_deposit_rate.length === 0){
						var dialog = $.dialog({title: '错误',lock:true,content: '常规提现分润比例不能为空',icon: 'error.gif',ok: function(){
				        	$("#common_deposit_rate").focus();
						    }
						});
						flag = false;
						return false;
					}else{
						if(!common_deposit_rate.match(RATE_REG) && !common_deposit_rate.match(INTEGER_REG))
						{
							var dialog = $.dialog({title: '错误',lock:true,content: '常规提现分润比例应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
					        	$("#common_deposit_rate").focus();
							    }
							});
							flag = false;
							return false;
						}
						
						if(Number(common_deposit_rate) > 100 || Number(common_deposit_rate) < 0){
							var dialog = $.dialog({title: '错误',lock:true,content: '常规提现分润比例应在0~100之间',icon: 'error.gif',ok: function(){
					        	$("#common_deposit_rate").focus();
							    }
							});
							flag = false;
							return false;
						}
					}
					
					
					if(over_deposit_rate.length === 0){
						var dialog = $.dialog({title: '错误',lock:true,content: '超额提现分润比例不能为空',icon: 'error.gif',ok: function(){
				        	$("#over_deposit_rate").focus();
						    }
						});
						flag = false;
						return false;
					}else{
						if(!over_deposit_rate.match(RATE_REG) && !over_deposit_rate.match(INTEGER_REG))
						{
							var dialog = $.dialog({title: '错误',lock:true,content: '超额提现分润比例应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
					        	$("#over_deposit_rate").focus();
							    }
							});
							flag = false;
							return false;
						}
						
						if(Number(over_deposit_rate) > 100 || Number(over_deposit_rate) < 0){
							var dialog = $.dialog({title: '错误',lock:true,content: '超额提现分润比例应在0~100之间',icon: 'error.gif',ok: function(){
					        	$("#over_deposit_rate").focus();
							    }
							});
							flag = false;
							return false;
						}
					}
				}else if(is_invest === '0'){
				
					if(deposit_rate.length === 0){
						var dialog = $.dialog({title: '错误',lock:true,content: '提现分润比例不能为空',icon: 'error.gif',ok: function(){
				        	$("#deposit_rate").focus();
						    }
						});
						flag = false;
						return false;
					}else{
						if(!deposit_rate.match(RATE_REG) && !deposit_rate.match(INTEGER_REG))
						{
							var dialog = $.dialog({title: '错误',lock:true,content: '提现分润比例应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
					        	$("#deposit_rate").focus();
							    }
							});
							flag = false;
							return false;
						}
						
						if(Number(deposit_rate) > 100 || Number(deposit_rate) < 0){
							var dialog = $.dialog({title: '错误',lock:true,content: '提现分润比例应在0~100之间',icon: 'error.gif',ok: function(){
					        	$("#deposit_rate").focus();
							    }
							});
							flag = false;
							return false;
						}
					}
				}
				
				$.dialog.confirm('确定要修改该条记录吗？', function(){
					formSubmit('agentModify', null,null,successMsg,null,null);
				});
				
		}
		
		function successMsg(){
			var dialog = $.dialog({title: '提示',lock:true,content: '代理商修改成功',icon: 'success.gif',ok:null });
		}
		
		function checkAgentName(agentName,agentNo){
			var mark = true;
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentNameCheck",
		 			data:{"agentName":agentName,"agentNo":agentNo},
		 			async:false,
		 			dataType: 'json',
				  success: function(data){
				    	var ret = data.msg;
					  	if (ret == "exist"){
					  		$.dialog.alert("代理商名称已被占用");
					  		$("#agentName").focus();
					  		mark =  false; 
					  	}else{
					  		mark = true;
					  	}
				  }
		 		}
		 	);
			 return mark;
		}
		
		function changefrVal(sel){
			var selopt= sel.value;
			if(selopt=='2'){
				$("#fr_2_fee").val("60");
				$("#fr_3_fee").val("70");
				$("#fr_4_fee").val("80");
			}else {
				$("#fr_2_fee").val("55");
				$("#fr_3_fee").val("65");
				$("#fr_4_fee").val("75");
			}
		}
		
		
		jQuery(document).ready(function(){
			//扣率锁定审核
			$("#lock_status").change(function(){
				var agentNo = $(this).attr("agentno");
				if($(this).prop("checked")){
					agentLocked(agentNo);
					$("#locked_status").val('1');
				}else{
					agentNotLocked(agentNo);
					$("#locked_status").val('0');
				} 
			});
			$("#check_status").change(function(){
				if($(this).prop("checked")){
					agentChecked($(this).attr("agentno"));
					$("#checked_status").val('1');
				}
			});
			//分润锁定审核
			$("#share_lock_status").change(function(){
				var agentNo = $(this).attr("agentno");
				if($(this).prop("checked")){
					agentShareLocked(agentNo);
					$("#share_locked_status").val('1');
				}else{
					agentShareNotLocked(agentNo);
					$("#share_locked_status").val('0');
				} 
			});
			$("#share_check_status").change(function(){
				if($(this).prop("checked")){
					agentShareChecked($(this).attr("agentno"));
				}
			});
		});
		
		/**财务审核、锁定功能**/
		function agentLocked(agentNo){
			
			$.dialog.confirm('确定要锁定该代理商的扣率吗？', function(){
				ajaxAgentLocked(agentNo);
			});
		}
		function ajaxAgentLocked(agentNo){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentLocked",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		$('#feeByYeWu_add_input input').attr("disabled",true);   
					  		successMsg1("代理商扣率锁定成功！");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		function agentNotLocked(agentNo){
			$.dialog.confirm('确定要解锁该代理商的扣率吗？', function(){
				ajaxAgentNotLocked(agentNo);
			});
			
		}
		function ajaxAgentNotLocked(agentNo){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentNotLocked",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		 $('#feeByYeWu_add_input input').attr("disabled",false);   
					  		successMsg1("代理商扣率解锁成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		function agentChecked(agentNo){
			$.dialog.confirm('确定审核通过该代理商的扣率吗？', function(){
				ajaxAgentChecked(agentNo);
			});
		}
		function ajaxAgentChecked(agentNo){
			 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentChecked",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		data.checked_status
					  		$('#check_status').attr("disabled",'true'); 
					  		$('#lock_status').attr("disabled",false);  
					  		successMsg1("代理商扣率审核通过！");
					  		return false;
					  	}
				  }
		 		}
		 	);
		}
		/**财务扣率审核、锁定功能**/
		
		
		/**财务分润比例审核、锁定功能**/
		function agentShareLocked(agentNo){
			$.dialog.confirm('确定要锁定该代理商的分润比例吗？', function(){
				 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentShareLocked",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		  $('#fr_add_input input').attr("disabled",true);   
					  		  $('#smfr_add_input input').attr("disabled",true); 
					  		 $('#ylst_add_input input').attr("disabled",true); 
					  		successMsg1("代理商分润比例锁定成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
			});
		}
		function agentShareNotLocked(agentNo){
			$.dialog.confirm('确定要解锁该代理商的分润比例吗？', function(){
				$.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentShareNotLocked",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		 $('#fr_add_input input').attr("disabled",false); 
					  		 $('#smfr_add_input input').attr("disabled",false); 
					  		$('#ylst_add_input input').attr("disabled",false); 
					  		successMsg1("代理商分润比例解锁成功");
					  		return false;
					  	}
				  }
		 		}
		 	);
			});
			
		}
		
		function agentShareChecked(agentNo){
			$.dialog.confirm('确定审核通过该代理商的分润比例吗？', function(){
				 $.ajax({
		 			type:"post",
		 			url:"${ctx}/agent/agentShareChecked",
		 			data:{"agentNo":agentNo},
		 			dataType: 'json',
				  	success: function(data){
				    	var ret = data.msg;
					  	if (ret == "OK"){
					  		$('#share_check_status').attr("disabled",'true'); 
					  		$('#share_lock_status').attr("disabled",false);  
					  		successMsg1("代理商分润比例审核通过！");
					  		return false;
					  	}
				  }
		 		}
		 	);
			});
		}
		/**财务分润比例审核、锁定功能**/
		
		
		function successMsg1(contentMsg){
			var id = $.trim($("#id").val());
			var dialog = $.dialog({title: '提示',lock:true,content: contentMsg,icon: 'success.gif',ok:null ,close:function(){
				//location.href="${ctx }/agent/agentModload?id="+id;
			}});
		}
		function disabled(){
			 $('#feeByYeWu_add_input input').attr("disabled",true);   
		}
		$(function(){
			//扣率
			var locked_status = '${params.locked_status}';
			var checked_status = '${params.checked_status}';
			if("1"==locked_status){
				disabled();
			}
			if("1"==checked_status){
				$('#check_status').attr("checked",true);
			}
			if("0"==checked_status){
				$('#lock_status').attr("disabled",true);
			}
			//分润比例
			var share_locked_status = '${params.share_locked_status}';
			var share_checked_status = '${params.share_checked_status}';
			if("1"==share_locked_status){
				$('#share_check_status').attr("checked",true);   
		  		 $('#fr_add_input input').attr("disabled",true);   
		  		 $('#smfr_add_input input').attr("disabled",true);
		  		 $('#ylst_add_input input').attr("disabled",true);
			}
			if("1"==share_checked_status){
				$('#share_check_status').attr("checked",true);
			}
			if("0"==share_checked_status){
				$('#share_lock_status').attr("disabled",true);    
			}
		});
		
		
		$(function(){
			// 选择是否投资的处理
			$('#is_invest').on('change', function(){
				if(this.value === '1'){
					$('#invest_amount').attr('readonly', false).removeClass('readonly');
					$('#common_deposit_amount').removeClass('readonly');
					$('#common_deposit_rate').removeClass('readonly').val('50');
					$('#over_deposit_rate').attr('readonly', false).removeClass('readonly');
					$('#deposit_rate').attr('readonly', true).addClass('readonly').val('');
				}else{
					$('#invest_amount').attr('readonly', true).addClass('readonly').val('');
					$('#common_deposit_amount').addClass('readonly').val('');
					$('#common_deposit_rate').addClass('readonly').val('');
					$('#over_deposit_rate').attr('readonly', true).addClass('readonly').val('');
					$('#deposit_rate').attr('readonly', false).removeClass('readonly');
				}
			});
			
			$('#invest_amount').on('blur',function(){
				var is_invest = $.trim($('#is_invest').val());
				var invest_amount = $.trim($(this).val());
				if(is_invest === '1' && invest_amount.length === 0){
					var dialog = $.dialog({title: '错误',lock:true,content: '投资金额不能为空',icon: 'error.gif',ok: function(){
				        $('#common_deposit_amount').val('');
						}
					});
				}else if(is_invest === '1' && !invest_amount.match(RATE_REG) && !invest_amount.match(INTEGER_REG)){
					var dialog = $.dialog({title: '错误',lock:true,content: '投资金额应为数字， 正确格式例子是：8.88或8',icon: 'error.gif',ok: function(){
				   		$('#common_deposit_amount').val('');
						}
					});
				}else {
					if(invest_amount)
						$('#common_deposit_amount').val(2 * Number(invest_amount));					
				}
			});
		});
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：代理商管理>代理商修改</div>
    <form:form id="agentModify" action="${ctx}/agent/agentModify" method="post">
    <input type="hidden"  id="agentNo" name="agentNo"  value="${params.agent_no}"/>
     <input type="hidden"  id="id" name="id"  value="${params.id}"/>
     <input type="hidden" name="locked_status"  id="locked_status"  value="${params.locked_status}"/>
     <input type="hidden" name="share_locked_status"  id="share_locked_status"  value="${params.share_locked_status}"/>
     <input type="hidden" name="checked_status"  id="checked_status"  value="${params.checked_status}"/>
		<input type="hidden" name="old_agent_name" id="old_agent_name" value="${params.agent_name}" />
    <div class="item">
    	<div class="title">基本信息</div>
    	<ul>
			<li><label>代理商名称：</label><input type="text" id="agentName"  name="agentName"  value="${params.agent_name}"/><label class="must">*</label></li>
			<li><label>代理商联系人：</label><input type="text" id="agentLinkName"  name="agentLinkName"  value="${params.agent_link_name}"/><label class="must">*</label></li>
			<div class="clear"></div>
			<li><label>联系电话：</label><input type="text" id="agentLinkTel"  name="agentLinkTel"  value="${params.agent_link_tel}"/><label class="must">*</label></li>
			<li style="width:450px"><label>登录邮箱：</label><input style="background-color:rgb(235, 235, 228);" type="text" id="agentLinkMail"  name="agentLinkMail" readonly="readonly" value="${params.agent_link_mail}"/><label class="must">*</label>用作登录代理商系统使用</li>
			<div class="clear"></div>
			<li><label>代理商区域：</label><input type="text" id="agentArea"  name="agentArea"  value="${params.agent_area}"/><label class="must">*</label></li>
			<li><label>代理商级别：</label><select name="agentAreaType" id="agentAreaType" style="width:157px;padding: 3px;" onchange="changefrVal(this)">
				<option value='-1'>请选择</option>
				<option value="1" <c:if test="${params.agent_area_type == '1'}">selected="selected"</c:if>>市级代理</option>
				<option value="2" <c:if test="${params.agent_area_type == '2'}">selected="selected"</c:if>>省级代理</option>
			</select><label class="must">*</label>
			</li>
			<div class="clear"></div>
			<li><label>是否分润：</label><select name="profitSharing" id="profitSharing" style="width:157px;padding: 3px;" >
				<option value='-1'>请选择</option>
				<option value="1" <c:if test="${params.profit_sharing == '1'}">selected="selected"</c:if>>是</option>
				<option value="2" <c:if test="${params.profit_sharing == '2'}">selected="selected"</c:if>>否</option>
			</select><label class="must">*</label>
			</li>
			
			<li><label>代理商地址：</label><input type="text" id="agentAddress"  name="agentAddress"  value="${params.agent_address}"/></li>
			<div class="clear"></div>
			<li><label>销售名称：</label><input type="text" id="saleName"  name="saleName"  value="${params.sale_name}"/></li>
			<li><label>代理商接入费：</label><input type="text" id="agentPay"  name="agentPay"  value="${params.agent_pay}"/></li>
			<div class="clear"></div>
			<li><label>协议费率：</label><input type="text" id="agentRate"  name="agentRate"  value="${params.agent_rate}"/>%</li>
			<li><label>最小清算金额：</label><input type="text" id="mixAmout"  name="mixAmout"  value="${params.mix_amout}"/></li>
		 	<li  style=" display:none;"><label>上级代理商：</label><c:if test="${isParent=='0'}">
		 	        <u:select value="${params['parent_id']}" style="padding:2px;width:157px"  fieldAsValue="id"  stype="agent" sname="parentId"   otherOptions="haveWU"   onlyThowParentAgent="true"  />
		 	  </c:if>
		 	   <c:if test="${isParent!='0'}">
		 	        <u:select value="${params['parent_id']}" style="padding:2px;width:157px"  fieldAsValue="id"  stype="agent" sname="parentId"   otherOptions="haveWU"   onlyThowParentAgent="true" disabled="true" />
		 	   </c:if>     
		   </li>
			<div class="clear"></div>
			<li><label style="width: 106px;">代理商状态：</label>
				<select id="agentStatus" name="agentStatus" style="padding:2px;width: 157px;">
	         		<option value="1" <c:out value="${params['agent_status'] eq '1'?'selected':'' }"/>>正常</option>
	         		<option value="2" <c:out value="${params['agent_status'] eq '2'?'selected':'' }"/>>冻结</option>
	        	</select>
			</li>
			<li>
			<label>归属集群：</label><select name="group_code" id="group_code" style="width:157px;padding: 3px;" onchange="changefrVal(this)">
				<option value='-1'>请选择</option>
				<c:forEach items="${gpList}" var="item" varStatus="status">
					<option value="${item.group_code}" <c:if test="${params.group_code == item.group_code}">selected="selected"</c:if>>
						${item.group_name}
					</option>
				</c:forEach>
			</select>
			</li>
			<div class="clear"></div>
			<li class="column2">
			<label style="width: 106px;">是否钱包结算：</label>
				<select id="bag_settle" name="bag_settle" style="padding:2px;width: 157px;">
	         		<option value="0" <c:out value="${params['bag_settle'] eq '0'?'selected':'' }"/>>否</option>
	         		<option value="1" <c:out value="${params['bag_settle'] eq '1'?'selected':'' }"/>>是</option>
	        	</select>
			</li>
			<div class="clear"></div>
			<li>
				<label>是否投资：</label><select name="is_invest" id="is_invest" style="width:157px;padding: 3px;">
					<option value="0" <c:if test="${params.is_invest == '0'}">selected="selected"</c:if>>否</option>
					<option value="1" <c:if test="${params.is_invest == '1'}">selected="selected"</c:if>>是</option>
				</select>
				<label class="must">*</label>
			</li>
			<li><label>投资金额：</label><input type="text" id="invest_amount"  name="invest_amount" value="${params.invest_amount}" <c:if test="${(params.is_invest eq null) || (params.is_invest eq '0') || (params.is_invest eq '')}">readonly="readonly"  class="readonly"</c:if> />万</li>
    	</ul>
    	<div class="clear"></div>
    	<div class="title">分润账户信息</div>
    	<ul>
  			<li><label>常规提现额度：</label><input class="input<c:if test="${(params.is_invest eq null) || (params.is_invest eq '0') || (params.is_invest eq '')}"> readonly</c:if>" type="text" id="common_deposit_amount"  name="common_deposit_amount" value="${params.common_deposit_amount}" <c:if test="${(params.is_invest eq null) || (params.is_invest eq '0') || (params.is_invest eq '')}">readonly="readonly"</c:if> />万</li>
    		<li><label>常规提现分润比例：</label><input class="input<c:if test="${(params.is_invest eq null) || (params.is_invest eq '0') || (params.is_invest eq '')}"> readonly</c:if>" type="text" id="common_deposit_rate"  name="common_deposit_rate"  value="${params.is_invest eq '1' ? params.common_deposit_rate : ''}" readonly="readonly" />%</li>
    		<div class="clear"></div>
    		<li><label>超额提现分润比例：</label><input class="input<c:if test="${(params.is_invest eq null) || (params.is_invest eq '0') || (params.is_invest eq '')}"> readonly</c:if>" type="text" id="over_deposit_rate"  name="over_deposit_rate" value="${params.over_deposit_rate}"  <c:if test="${(params.is_invest eq null) || (params.is_invest eq '0') || (params.is_invest eq '')}">readonly="readonly"</c:if> />%</li>
    		<li><label>提现分润比例：</label><input class="input<c:if test="${params.is_invest eq '1'}"> readonly</c:if>" type="text" id="deposit_rate"  name="deposit_rate" value="${params.deposit_rate}"  <c:if test="${params.is_invest eq '1'}">readonly="readonly"</c:if> />%</li>
	 	</ul>
    	<ul id="share_agent_input">
    		<li><label>开户名：</label><input class="input" type="text" id="account_name"  name="account_name"  value="${params.account_name}"/><label class="must">*</label></li>
    		<li><label>账户类型：</label><select name="account_type" id="account_type" style="width:157px;padding:3px;">
				<option value="对公" <c:if test="${params.account_type == '对公'}">selected="selected"</c:if>>对公</option>
				<option value="对私" <c:if test="${params.account_type == '对私'}">selected="selected"</c:if>>对私</option>
			</select><label class="must">*</label>
    		</li>
    		<div class="clear"></div>
    		<li><label>开户行全称：</label><input class="input" type="text" id="bank_name"  name="bank_name"  value="${params.bank_name}"/><label class="must">*</label></li>
    		<li><label>开户账号：</label><input class="input" type="text" id="account_no"  name="account_no"  value="${params.account_no}"/><label class="must">*</label></li>
    		<div class="clear"></div>
    		<li><label>联行行号：</label><input class="input" type="text" id="cnaps_no"  name="cnaps_no"  value="${params.cnaps_no}"/><label class="must">*</label></li>
			<li><a href="http://www.posp.cn" target="_blank">联行行号查询</a></li>
    	</ul>
    	
    	<div class="title" style="height:20px;"><span style="float:left;width:200px;" >分润比例</span>
      	<shiro:hasPermission name="AGENT_SHARE_CHECK">
	      <span style="float:right;width:100px;">
	     	 <input type="checkbox" agentno="${params.agent_no}"
	     	 	<c:if test="${'1' eq params.share_checked_status }">
	     	 		checked disabled
	     	 	</c:if>
	     	 id="share_check_status">已审核&nbsp;&nbsp;
	     	 </shiro:hasPermission>
	     	 <shiro:hasPermission name="AGENT_SHARE_LOCK">
			<input type="checkbox" 
	     	 	 <c:if test="${'1' eq params.share_locked_status }">
	     	 		checked
	     	 	</c:if>
	     	 	id="share_lock_status" agentno="${params.agent_no}">锁定</span>
	     	 	</shiro:hasPermission></div>
 	<ul  id="fr_add_input">
      <!--分润 start-->
      <c:set var="arrayAB" value="${params.rsharing_rule_chaifen}"></c:set>
       <li style="width: 700px;"><label>分润规则：</label> 
          <input type="text" name="fr_1_mix"  value="${arrayAB[0][0]}"   readonly="readonly" style="width:80px;"/>万元&lt;<input type="text" name="fr_1_fee" value="${arrayAB[0][1]}"   style="width:80px;"/>%≤<input type="text" name="fr_1_max"   value="${arrayAB[0][2]}"    style="width:80px;"/>万元<br/>
          <input type="hidden" value="81"/>
		   </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="fr_2_mix"  value="${arrayAB[1][0]}"   style="width:80px;"/>万元&lt;<input type="text" id="fr_2_fee" name="fr_2_fee" value="${arrayAB[1][1]}"   style="width:80px;"/>%≤<input type="text" name="fr_2_max"  value="${arrayAB[1][2]}"    style="width:80px;"/>万元<br/>
		      <input type="hidden" value="82"/>
		   </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="fr_3_mix"   value="${arrayAB[2][0]}"   style="width:80px;"/>万元&lt;<input type="text" id="fr_3_fee" name="fr_3_fee"   value="${arrayAB[2][1]}"  style="width:80px;"/>%≤<input type="text" name="fr_3_max"  value="${arrayAB[2][2]}" style="width:80px;"/>万元<br/>
		  	  <input type="hidden" value="83"/>
		  </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="fr_4_mix"   value="${arrayAB[3][0]}"  style="width:80px;"/>万元&lt;<input type="text" id="fr_4_fee" name="fr_4_fee"   value="${arrayAB[3][1]}"  style="width:80px;"/>%≤<input type="text" name="fr_4_max"   readonly="readonly"   value="${arrayAB[3][2]}"  style="width:80px;"/>万元<br/>
		  	  <input type="hidden" value="84"/>
		  </li> 
      <!--分润 end    -->
   </ul>
     <div class="clear"></div> 
     
     <ul  id="smfr_add_input">
      <!--分润 start-->
      <c:set var="smArrayAB" value="${params.smrsharing_rule_chaifen}"></c:set>
       <li style="width: 700px;" ><label>移小宝分润规则：</label> 
          <input type="text" name="smfr_1_mix"  value="${smArrayAB[0][0]}"    readonly="readonly" style="width:80px;"/>万元&lt;<input type="text" name="smfr_1_fee" value="${smArrayAB[0][1]}"   style="width:80px;"/>%≤<input type="text" name="smfr_1_max"   value="${smArrayAB[0][2]}"   style="width:80px;"/>万元<br/>
          <input type="hidden" value="81"/>
		   </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="smfr_2_mix"    value="${smArrayAB[1][0]}"   style="width:80px;"/>万元&lt;<input type="text" id="smfr_2_fee" name="smfr_2_fee" value="${smArrayAB[1][1]}"   style="width:80px;"/>%≤<input type="text" name="smfr_2_max" value="${smArrayAB[1][2]}"   style="width:80px;"/>万元<br/>
		      <input type="hidden" value="82"/>
		   </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="smfr_3_mix"  value="${smArrayAB[2][0]}"   style="width:80px;"/>万元&lt;<input type="text" id="smfr_3_fee"  name="smfr_3_fee"  value="${smArrayAB[2][1]}"  style="width:80px;"/>%≤<input type="text" name="smfr_3_max"  value="${smArrayAB[2][2]}"  style="width:80px;"/>万元<br/>
		  	  <input type="hidden" value="83"/>
		  </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="smfr_4_mix"  value="${smArrayAB[3][0]}"  style="width:80px;"/>万元&lt;<input type="text" id="smfr_4_fee"  name="smfr_4_fee"   value="${smArrayAB[3][1]}"  style="width:80px;"/>%≤<input type="text" name="smfr_4_max"   readonly="readonly"   value="${smArrayAB[3][2]}"  style="width:80px;"/>万元<br/>
		  	  <input type="hidden" value="84"/>
		  </li> 
      <!--分润 end    -->
       </ul>
       
       <ul  id="ylst_add_input">
      <!--分润 start-->
      <c:set var="ylstArrayAB" value="${params.ylstsharing_rule_chaifen}"></c:set>
       <li style="width: 700px;" ><label>移联商通分润规则：</label> 
          <input type="text" name="ylst_1_mix"  value="${ylstArrayAB[0][0]}"    readonly="readonly" style="width:80px;"/>万元&lt;<input type="text" name="ylst_1_fee" value="${ylstArrayAB[0][1]}"   style="width:80px;"/>%≤<input type="text" name="ylst_1_max"   value="${ylstArrayAB[0][2]}"   style="width:80px;"/>万元<br/>
          <input type="hidden" value="81"/>
		   </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="ylst_2_mix"    value="${ylstArrayAB[1][0]}"   style="width:80px;"/>万元&lt;<input type="text" id="ylst_2_fee" name="ylst_2_fee" value="${ylstArrayAB[1][1]}"   style="width:80px;"/>%≤<input type="text" name="ylst_2_max" value="${ylstArrayAB[1][2]}"   style="width:80px;"/>万元<br/>
		      <input type="hidden" value="82"/>
		   </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="ylst_3_mix"  value="${ylstArrayAB[2][0]}"   style="width:80px;"/>万元&lt;<input type="text" id="ylst_3_fee"  name="ylst_3_fee"  value="${ylstArrayAB[2][1]}"  style="width:80px;"/>%≤<input type="text" name="ylst_3_max"  value="${ylstArrayAB[2][2]}"  style="width:80px;"/>万元<br/>
		  	  <input type="hidden" value="83"/>
		  </li> 
		   <li style="width: 700px;"><label>&nbsp;</label> 
          <input type="text" name="ylst_4_mix"  value="${ylstArrayAB[3][0]}"  style="width:80px;"/>万元&lt;<input type="text" id="ylst_4_fee"  name="ylst_4_fee"   value="${ylstArrayAB[3][1]}"  style="width:80px;"/>%≤<input type="text" name="ylst_4_max"   readonly="readonly"   value="${ylstArrayAB[3][2]}"  style="width:80px;"/>万元<br/>
		  	  <input type="hidden" value="84"/>
		  </li> 
      <!--分润 end    -->
       </ul>
       
     
     
      <div class="title" style="height:20px;"><span style="float:left;width:200px;" >代理商行业计算成本</span>
      	<shiro:hasPermission name="AGENT_FEE_CHECK">
	      <span style="float:right;width:100px;">
	     	 <input type="checkbox" agentno="${params.agent_no}"
	     	 	<c:if test="${'1' eq params.checked_status }">
	     	 		checked disabled
	     	 	</c:if>
	     	 id="check_status">已审核&nbsp;&nbsp;
	     	 </shiro:hasPermission>
	     	 <shiro:hasPermission name="AGENT_FEE_LOCK">
			<input type="checkbox" 
	     	 	 <c:if test="${'1' eq params.locked_status }">
	     	 		checked
	     	 	</c:if>
	     	 	id="lock_status" agentno="${params.agent_no}">锁定</span>
	     	 	</shiro:hasPermission>
      </div>
   
        <ul  id="feeByYeWu_add_input">
            <!--代理商行业计算成本 start-->
            <li><label>民生A类 :</label>    <input type="text" name="live_a_type"  value="${params.live_a_type}" /><label class="must">*</label></li>
            <li><label style="width: 120px;">民生B类 :</label>    <input type="text" name="live_b_type"  value="${params.live_b_type}"  /><label class="must">*</label></li>
            <div class="clear"></div>
            <li><label>批发对公:</label>  <input type="text" name="wholesale_pub_type"  value="${params.wholesale_pub_type}"  /><label class="must">*</label></li>
            <li><label style="width: 120px;">批发对私封顶类:</label>  <input type="text" name="wholesale_pri_cap_type"  value="${params.wholesale_pri_cap_type}"  /><label class="must">*</label></li>
            <div class="clear"></div>
            <li><label>批发对私非封顶类:</label>  <input type="text" name="wholesale_pri_nocap_type"  value="${params.wholesale_pri_nocap_type}"  /><label class="must">*</label></li>
            <li><label style="width: 120px;">房地产及汽车销售类 :</label> <input type="text" name="estate_car_type"  value="${params.estate_car_type}"  /><label class="must">*</label></li>
            <div class="clear"></div>
            <li><label>一般A类:</label>  <input type="text" name="general_type"  value="${params.general_type}"  /><label class="must">*</label></li>
            <li><label style="width: 120px;">一般B类:</label>  <input type="text" name="general_b_type"  value="${params.general_b_type}"  /><label class="must">*</label></li>
            <div class="clear"></div>
            <li><label >餐饮类:</label>  <input type="text" name="catering_type"  value="${params.catering_type}"  /><label class="must">*</label></li>
            <li><label style="width: 120px;">移小宝:</label>  <input type="text" id="smbox_type" name="smbox_type"  value="${params.smbox_type}"  /><label class="must">*</label></li>
            <li><label style="width: 70px;">移联商通II:</label>  <input type="text" id="ylst2_type" name="ylst2_type"  value="${params.ylst2_type}"  /><label class="must">*</label></li>
            <!--代理商行业计算成本 end    -->
         </ul>
     
   
    	<div class="clear"></div>
    	<div class="title">LOGO上传</div>
    	
	    <ul>
  			<li style="width: 100%">
  			<c:if test="${!empty params.client_logo}">
		  		<a href="${params.client_logo}" target="_blank">查看图片</a>
		  	</c:if>
		  	<label style="width: 114px;">客户端LOGO：</label><input id="clientLogo" name="clientLogo" type="text" readOnly="true" value="${params.client_logo}"/><input id="file_upload" name="file_upload" type="file" /><li>
  			<li style="width: 100%">
  			<c:if test="${!empty params.manager_logo}">
		  		<a href="${params.manager_logo}" target="_blank">查看图片</a>
		  	</c:if>
  			<label>管理系统LOGO：</label>	<input id="managerLogo" name="managerLogo" type="text" readOnly="true" value="${params.manager_logo}"/><input id="mlogo_upload" name="mlogo_upload" type="file" /><li>
  			<input type="hidden"  id="oldClientLogo" name="oldClientLogo"  value="${params.client_logo}"/>
  			<input type="hidden"  id="oldManagerLogo" name="oldManagerLogo"  value="${params.manager_logo}"/>
	    </ul>
    	<div class="search_btn clear">
    		<shiro:hasPermission name="AGENT_INFO_UPDATE_CONFIRM">
    			<input   class="button blue  " type="button" id="addButton"   value="保存" onclick="javascript:updateAgentInfo('${params.agent_no}')" />
    		</shiro:hasPermission>
    		<input name="reset" class="button blue " type="button" onclick="javascript:history.go(-1);" value="返回"/>
    	</div>
    </div>
    </form:form>
   
  </div>
</body>
