<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
<script type="text/javascript" src="${ ctx}/scripts/utils.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/transTimeLimit.js" id="transTimeLimit" data="<shiro:hasPermission name="TRANS_TIME_LIMIT">1</shiro:hasPermission><shiro:lacksPermission name="TRANS_TIME_LIMIT">0</shiro:lacksPermission>"></script>
<script type="text/javascript">
	var root = '${ctx}';

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
	
	//创建统计信息html结构文件
	//创建统计信息html结构文件
	function createTransCountInfoHtml(totalMsg) {
		var html = '<ul>\<li>\<b>笔数：'+ totalMsg.total_pur_count+ '</b>笔\</li>';
		html += '<li style="width: 210px;text-align: left;">';
		if(totalMsg.total_pur_amount>10000){
			var total_pur_amount_w = totalMsg.total_pur_amount/10000;
			total_pur_amount_w = parseInt(total_pur_amount_w+0.5);
			html +=  "<b>金额约：";
			html +=  total_pur_amount_w;
			html +=  "</b>&nbsp;万元<br>";
		}
		{
			html +=  "<b>总金额为：";
			html +=  totalMsg.total_pur_amount;
			html +=  "</b>&nbsp;元";
		}
		html += '\</li>\<li style="width: 190px">\<b>手续费总金额：'+ totalMsg.total_mer_fee+ '</b>元\</li>\<div class="clear"></div>\</ul>\<div class="clear"></div>';
		return html;
	}
	function test(tranId) {
		alert(tranId);
		var content = "";

		$.dialog({
			content : 'url:content/content.html'
		});

		var dialog = $.dialog({
			height : 540,
			width : 350,
			title : '欢迎',
			lock : true,
			content : '欢迎使用lhgdialog对话框组件！',
			icon : 'succeed',
			ok : function() {
				this.title('警告').content('请注意lhgdialog两秒后将关闭！').lock().time(2);
				return false;
			}
		});
	}

	function showDetail(id, transSource) {
		if ("SMALLBOX_MOBOLE_PHONE" != transSource) {
			$.dialog({
				title : '订单详情',
				width : 750,
				height : 750,
				resize : false,
				lock : true,
				max : false,
				content : 'url:detail?id=' + id + '&layout=no'
			});
		} else {
			$.dialog({
				title : '订单详情',
				width : 750,
				height : 550,
				resize : false,
				lock : true,
				max : false,
				content : 'url:detail?id=' + id + '&layout=no'
			});
		}

	}
	//显示移小宝刷卡签名dialog
	function showSignReceipt(id) {
		signRecepit_dialog = $.dialog({
			title : '小票',
			width : 450,
			height : 500,
			resize : false,
			lock : true,
			max : false,
			content : 'url:receiptReviewLoad/' + id + '?layout=no'
		});
	}
	function refund(id) {
		$.dialog({
			title : '退款',
			width : 550,
			height : 250,
			resize : false,
			lock : true,
			max : false,
			content : 'url:refund?id=' + id + '&layout=no'
		});
	}
	
	//冻结钱包交易
	function bagTransFreeze(id) {
		window.freezeDialog = $.dialog({
			title : '钱包交易冻结',
			width : 600,
			height : 610,
			resize : false,
			lock : true,
			max : false,
			content : 'url:bagTransFreeze?id=' + id + '&layout=no',
			close:function(){
				top.$('form:first').submit();
			}
		});
	}
	
	
	
	//冻结交易
	function TransFreeze(id) {
		window.freezeDialog=$.dialog({
			title : '交易冻结',
			width : 600,
			height : 610,
			resize : false,
			lock : true,
			max : false,
			content : 'url:transFreeze?id=' + id + '&layout=no',
			close:function(){
				top.$('form:first').submit();
			}
		});
	}
	
/* 	//解冻交易
	function TransUnFreezed(id) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
	
		$.post(root + "/mer/freezedInfo",{"trans_id":id}, function(data) {
			var freezeDate = new Date(data.create_time);
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+(freezeDate.getMinutes() > 9 ? freezeDate.getMinutes() :'0' +  freezeDate.getMinutes()) +":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.freeze_people +"</br></br>解冻后清算日期为："+ndate.getFullYear() + "-" + (ndate.getMonth() + 1) + "-" + ndate.getDate()+ "" + "</br></br>确认解冻？",function(){
				$.ajax({
					url:"${ctx}/mer/transUnFreeze",
					type:"post",
					data: {"id":id},
					dataType:"text",
					success:function(d,status){
						top.$('form:first').submit();
					}
				})
			})
		})
		
	} */
	
	//解冻交易
	function TransUnFreezed(id) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
	
		/* $.post(root + "/mer/freezedInfo",{"trans_id":id}, function(data) { */
		$.post(root + "/mer/freezedInfoNew",{"trans_id":id}, function(data) {
			var freezeDate = new Date(data.oper_time);
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+(freezeDate.getMinutes() > 9 ? freezeDate.getMinutes() :'0' +  freezeDate.getMinutes()) +":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.oper_name +"</br></br>解冻后清算日期为："+ndate.getFullYear() + "-" + (ndate.getMonth() + 1) + "-" + ndate.getDate()+ "" + "</br></br>确认解冻？",function(){
				$.ajax({
					/* url:"${ctx}/mer/transUnFreeze", */
					url:"${ctx}/mer/transUnFreezeNew",
					type:"post",
					data: {"id":id},
					dataType:"text",
					success:function(d,status){
						top.$('form:first').submit();
					}
				})
			})
		})
		
	}
	
	
	function bagTransUnFreezed(merNo,id,amount,fee) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
		amount = amount - fee;
		$.post(root + "/mer/freezedInfoNew",{"trans_id":id}, function(data) {
			var freezeDate = new Date(data.oper_time);
			
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+freezeDate.getMinutes()+":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.oper_name +"</br></br>确认解冻？",function(){
				$.ajax({
					url:"${ctx}/mer/bagTransUnFreezeNew",
					type:"post",
					data: {"id":id,"merNo":merNo,"amount":amount},
					dataType:"text",
					success:function(d,status){
						/* location.href="${ctx}/mer/transfast"; */
						alert(d);
						top.$('form:first').submit();
					}
				})
			})
		})
		
	}
	
	
	/* 	//解冻钱包交易
	function bagTransUnFreezed(id,amount,fee) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
		amount = amount - fee;
		$.post(root + "/mer/freezedInfo",{"trans_id":id}, function(data) {
			var freezeDate = new Date(data.create_time);
			var merNo= data.merchant_no;
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+freezeDate.getMinutes()+":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.freeze_people +"</br></br>确认解冻？",function(){
				$.ajax({
					url:"${ctx}/mer/bagTransUnFreeze",
					type:"post",
					data: {"id":id,"merNo":merNo,"amount":amount},
					dataType:"text",
					success:function(d,status){
						/* location.href="${ctx}/mer/transfast"; */
						/* alert(d);
						top.$('form:first').submit();
					}
				})
			})
		})
		
	} */
	
	//关闭移小宝刷卡的签名dialog
	function closeSignReceipt_dialog() {
		if (signRecepit_dialog) {
			signRecepit_dialog.close();
		}
	}
	
	function cleanEmpty() {
		$('#agentName').val("");
		$('#merchantName').val("");
		$('#transType').val("-1");
		$('#transStatus').val("-1");
		$('#acqMerchantNo').val("");
		$('#acqTerminalNo').val("");
	}

	$(function(){
		$("#btnTransCountInfo").on("click", function() {
			this.value = "统计中，请稍后。。。";
			var params = $("#trans").serialize();
			$.post(root + "/mer/countTransInfo", params, function(data) {
				var html = createTransCountInfoHtml(data);
				$("#total_msg").html(html);
			});
		});
	});
	
	function exportExcel2() {
		exportXls('${ctx}/mer/export2', ${list.totalPages}, false);

		/*var action = $("form:first").attr("action");
		$("form:first").attr("action", "${ctx}/mer/export2").submit();
		$("form:first").attr("action", action);*/
	}
	
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理>交易查询（移小宝）
		</div>

		<form:form id="trans" action="${ctx}/mer/smBoxTrans" method="post">
			<input type="hidden" id="transSource" name="transSource"  value="SMALLBOX_MOBOLE_PHONE" />
			<div style="position:absolute;margin-left:62px;margin-top:48px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 104px;border:none;">
         	</div>
			<div id="search">

				<div id="title">交易查询</div>
				<ul>
					<li style="width: 216px">
						<span>代理商名称：</span>
						<u:select value="${params['agentNo']}" style="width:128px;padding:2px" stype="agent" sname="agentNo" onlyThowParentAgent="true" />
					</li>
					<li>
						<span style="width: 90px;">商户名称/编号：</span>
						<input type="text" value="${params['merchant']}" name="merchant" />
					</li>
					<li>
						<span>交易类型：</span>
						<select style="padding: 2px; width: 73px" id="transType" name="transType">
							<option value="-1" <c:out value="${params['transType'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="PURCHASE" <c:out value="${params['transType'] eq 'PURCHASE'?'selected':'' }"/>>消费</option>
							<option value="PURCHASE_VOID" <c:out value="${params['transType'] eq 'PURCHASE_VOID'?'selected':'' }"/>>消费撤销</option>
							<option value="PURCHASE_REFUND" <c:out value="${params['transType'] eq 'PURCHASE_REFUND'?'selected':'' }"/>>退货</option>
							<option value="REVERSED" <c:out value="${params['transType'] eq 'REVERSED'?'selected':'' }"/>>冲正</option>
							<option value="BALANCE_QUERY" <c:out value="${params['transType'] eq 'BALANCE_QUERY'?'selected':'' }"/>>余额查询</option>
						</select>
					</li>

					<li>
						<span>交易状态：</span>
						<select style="padding: 2px; width: 73px" id="transStatus" name="transStatus">
							<option value="-1" <c:out value="${params['transStatus'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="SUCCESS" <c:out value="${params['transStatus'] eq 'SUCCESS'?'selected':'' }"/>>已成功</option>
							<option value="FAILED" <c:out value="${params['transStatus'] eq 'FAILED'?'selected':'' }"/>>已失败</option>
							<option value="INIT" <c:out value="${params['transStatus'] eq 'INIT'?'selected':'' }"/>>初始化</option>
							<option value="REVERSED" <c:out value="${params['transStatus'] eq 'REVERSED'?'selected':'' }"/>>已冲正</option>
							<option value="REVOKED" <c:out value="${params['transStatus'] eq 'REVOKED'?'selected':'' }"/>>已撤销</option>
							<option value="REFUND" <c:out value="${params['transStatus'] eq 'REFUND'?'selected':'' }"/>>已退款</option>
							<option value="SETTLE" <c:out value="${params['transStatus'] eq 'SETTLE'?'selected':'' }"/>>已结算</option>
						</select>
					</li>
				</ul>
				<div class="clear"></div>
				<ul><!--
					<li style="width: 216px;">
						<span>收单商户号：</span>
						<input type="text" value="${params['acqMerchantNo']}" id="acqMerchantNo" name="acqMerchantNo" />
					</li>
					<li>
						<span style="width: 90px;">收单终端号：</span>
						<input type="text" value="${params['acqTerminalNo']}" id="acqTerminalNo" name="acqTerminalNo" />
					</li>-->
					
				</ul>
				<div class="clear"></div>
				<ul>
					<li style="width: 216px;">
						<span>交易卡号：</span>
						<input type="text" value="${params['cardNo']}" name="cardNo" />
					</li>
					<li>
						<span style="width: 90px;">参考号：</span>
						<input type="text" value="${params['referenceNo']}" name="referenceNo" />
					</li>
					<!-- <li>
						<span style="width: 77px;">订单号：</span>
						<input type="text" value="${params['id']}" name="id" />
					</li> -->
					<li>
						<span style="width: 76px;">电子小票：</span>
						<select style="padding: 2px; width: 73px" id="review_status" name="review_status" title="移小宝小票状态查询">
							<option value="-1" ${params.review_status eq '-1' ?'selected':''}>全部</option>
							<option value="1" ${params.review_status eq '1'?'selected':''}>审核通过</option>
							<option value="2" ${params.review_status eq '2'?'selected':''}>未审核</option>
							<option value="3" ${params.review_status eq '3'?'selected':''}>审核失败</option>
						</select>
					</li>
					
					<li>
						<span>卡类型：</span>
						<select style="padding: 2px; width: 73px" id="cardType" name="cardType">
							<option value="-1" <c:out value="${params['cardType'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="DEBIT_CARD" <c:out value="${params['cardType'] eq 'DEBIT_CARD'?'selected':'' }"/>>借记卡</option>
							<option value="CREDIT_CARD" <c:out value="${params['cardType'] eq 'CREDIT_CARD'?'selected':'' }"/>>贷记卡</option>
							<option value="PREPAID_CARD" <c:out value="${params['cardType'] eq 'PREPAID_CARD'?'selected':'' }"/>>预付卡</option>
							<option value="SEMI_CREDIT_CARD" <c:out value="${params['cardType'] eq 'SEMI_CREDIT_CARD'?'selected':'' }"/>>准贷记卡</option>
							<option value="BUSINESS_CARD" <c:out value="${params['cardType'] eq 'BUSINESS_CARD'?'selected':'' }"/>>公务卡</option>
							<option value="UNKNOWN_CARD" <c:out value="${params['cardType'] eq 'UNKNOWN_CARD'?'selected':'' }"/>>未知卡</option>
						</select>
					</li>
				</ul>
				<div class="clear"></div>
				<ul><!--
					<li style="width: 216px;">
						<span>机具号：</span>
						<input type="text" value="${params['terminalNo']}" name="terminalNo" />
					</li>
					<li style="width: 216px;">
						<span>卡类型：</span>
						<select style="padding: 2px; width: 73px" id="cardType" name="cardType">
							<option value="-1" <c:out value="${params['cardType'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="DEBIT_CARD" <c:out value="${params['cardType'] eq 'DEBIT_CARD'?'selected':'' }"/>>借记卡</option>
							<option value="CREDIT_CARD" <c:out value="${params['cardType'] eq 'CREDIT_CARD'?'selected':'' }"/>>贷记卡</option>
							<option value="PREPAID_CARD" <c:out value="${params['cardType'] eq 'PREPAID_CARD'?'selected':'' }"/>>预付卡</option>
							<option value="SEMI_CREDIT_CARD" <c:out value="${params['cardType'] eq 'SEMI_CREDIT_CARD'?'selected':'' }"/>>准贷记卡</option>
							<option value="BUSINESS_CARD" <c:out value="${params['cardType'] eq 'BUSINESS_CARD'?'selected':'' }"/>>公务卡</option>
							<option value="UNKNOWN_CARD" <c:out value="${params['cardType'] eq 'UNKNOWN_CARD'?'selected':'' }"/>>未知卡</option>
						</select>
					</li>-->
					<li style="width: 216px;">
						<span>用户手机号：</span>
						<input type="text" value="${params['mobile_username']}" name="mobile_username" />
					</li>
					<li>
						<span style="width: 90px;">收单商户号：</span>
						<input type="text" value="${params['acqMerchantNo']}" id="acqMerchantNo" name="acqMerchantNo" />
					</li>
					<li>
						<span style="width: 76px;">创建时间：</span>
						<input id="createTimeBegin" type="text" style="width: 102px"
							name="createTimeBegin" value="${params['createTimeBegin']}" >
						~
						<input id="createTimeEnd" type="text" style="width: 102px"
							name="createTimeEnd" value="${params['createTimeEnd']}" >
					</li>
<!--
					<li>
						<span style="width: 77px;">交易来源：</span>
						<select style="padding: 2px; width: 73px" id="transSource" name="transSource">
							<option value="-1" <c:out value="${params['transSource'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="COM_MOBILE_PHONE" <c:out value="${params['transSource'] eq 'COM_MOBILE_PHONE'?'selected':'' }"/>>企业版</option>
							<option value="POS" <c:out value="${params['transSource'] eq 'POS'?'selected':'' }"/>>POS</option>
							<option value="SMALLBOX_MOBOLE_PHONE" <c:out value="${params['transSource'] eq 'SMALLBOX_MOBOLE_PHONE'?'selected':'' }"/>>移小宝</option>
							<option value="MOBOLE_PHONE" <c:out value="${params['transSource'] eq 'MOBOLE_PHONE'?'selected':'' }"/>>个人手机</option>
						</select>
					</li>-->
					
				</ul>
				<div class="clear"></div>
				<ul>
					<li style="width: 216px;">
						<span>交易地址：</span>
						<input type="text" value="${params['address']}" id="address" name="address" />
					</li>
					<li><span  style="width: 90px;">收单机构：</span><u:selectAcqOrg   value="${params['ACQ_ENNAME']}"  style="width: 127px;height: 25px;" sname="ACQ_ENNAME"  /></li>
					
					<li>
						<span style="width: 76px;">交易金额：</span>
						<input type="text" value="${params['start_trans_amount']}" style="width: 102px"  name="start_trans_amount"  maxlength="12"/>
						~
						<input type="text" value="${params['end_trans_amount']}" style="width: 102px" name="end_trans_amount"  maxlength="12"/>
					</li>
					
				</ul>
				<div class="clear"></div>
				<ul>
					<li style="width: 216px;">
						<span>冻结状态：</span>
						<select style="width:128px;padding:2px" id="freezeStatus" name="freezeStatus">
							<option value="-1" <c:out value="${params['freezeStatus'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="0" <c:out value="${params['freezeStatus'] eq '0'?'selected':'' }"/>>正常</option>
							<option value="1" <c:out value="${params['freezeStatus'] eq '1'?'selected':'' }"/>>已冻结</option>
							<option value="2" <c:out value="${params['freezeStatus'] eq '2'?'selected':'' }"/>>已解冻</option>
					      </select> 
					</li>
					<li>
						<span style="width: 76px;">交易时间：</span>
						<input id="transTimeBegin" type="text" style="width: 102px"
							name="transTimeBegin" value="${params['transTimeBegin']}" >
						~
						<input id="transTimeEnd" type="text" style="width: 102px"
							name="transTimeEnd" value="${params['transTimeEnd']}" >
					</li>
					
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton" value="查询" />
				<input name="reset" class="button blue medium" type="reset" onclick="cleanEmpty()" value="清空" />
				<shiro:hasPermission name="TRANS_DOWNLOAD">
					<input id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()" value="导出excel" />
				</shiro:hasPermission>
			</div>
		</form:form>
		<div id="total_msg" class="total_msg">
			<input class="button blue medium" type="button" id="btnTransCountInfo" value="统计交易信息" />
		</div>
		<div class="tbdata">
			<a name="_table"></a>
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
						<th width="30">序号</th>
						<th width="130">商户简称</th>
						<th width="60">交易类型</th>
						<!-- <th width="50">卡种</th> -->
						<th width="110">交易卡号</th>
						<th width="70">金额(元)</th>
						<th width="60">交易状态</th>
						<th width="50">冻结状态</th>
						<th width="120">创建时间</th>
						<th width="60">操作</th>
						<c:forEach items="${list.content}" var="item" varStatus="status">
							<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
								<td class="center"><span class="center">${status.count}</span></td>
								<td><u:substring length="10" content="${item.merchant_short_name}" /></td>
								<td><c:choose>
										<c:when test="${item.trans_type eq 'PURCHASE'}">消费</c:when>
										<c:when test="${item.trans_type eq 'PURCHASE_VOID'}">
											<span class="font_red">消费撤销</span>
										</c:when>
										<c:when test="${item.trans_type eq 'PURCHASE_REFUND'}">
											<span class="font_red">退货</span>
										</c:when>
										<c:when test="${item.trans_type eq 'REVERSED'}">
											<span class="font_red">冲正</span>
										</c:when>
										<c:when test="${item.trans_type eq 'BALANCE_QUERY'}">
											<span class="font_gray">余额查询</span>
										</c:when>
										<c:otherwise>${item.trans_type }</c:otherwise>
									</c:choose></td>
								<%-- <td><c:choose>
										<c:when test="${item.card_type eq 'DEBIT_CARD'}">借记卡</c:when>
										<c:when test="${item.card_type eq 'CREDIT_CARD'}">贷记卡</c:when>
										<c:when test="${item.card_type eq 'PREPAID_CARD'}">预付卡</c:when>
										<c:when test="${item.card_type eq 'SEMI_CREDIT_CARD'}">准贷记卡</c:when>
										<c:when test="${item.card_type eq 'BUSINESS_CARD'}">公务卡</c:when>
										<c:when test="${item.card_type eq 'UNKNOWN_CARD'}">未知卡</c:when>
										<c:otherwise>${item.card_type}</c:otherwise>
									</c:choose></td> --%>
								<td>
									<shiro:hasPermission name="TRANS_CARD_NO">${item.account_no}</shiro:hasPermission>
									<shiro:lacksPermission name="TRANS_CARD_NO"><u:cardcut content="${item.account_no}" /></shiro:lacksPermission>
								</td>
								<td align="right"><c:choose>
										<c:when test="${item.trans_amount == null}">***</c:when>
										<c:otherwise>
						${item.trans_amount}
					</c:otherwise>
									</c:choose></td>
								<td><c:choose>
										<c:when test="${item.trans_status eq 'SUCCESS'}">已成功</c:when>
										<c:when test="${item.trans_status eq 'REVOKED'}">已撤销</c:when>
										<c:when test="${item.trans_status eq 'FAILED'}">
											<span class="font_red">已失败</span>
										</c:when>
										<c:when test="${item.trans_status eq 'REFUND'}">已退货</c:when>
										<c:when test="${item.trans_status eq 'REVERSED'}">
											<span class="font_red">已冲正</span>
										</c:when>
										<c:when test="${item.trans_status eq 'SETTLE'}">已结算</c:when>
										<c:when test="${item.trans_status eq 'INIT'}">
											<span class="font_red">初始化</span>
										</c:when>
									</c:choose></td>
									<td>
										<c:if test="${item.freeze_status  eq '1' }">
											<span class="font_red">已冻结</span>
									</c:if>
									<c:if test="${item.freeze_status  eq '0' }">
											正常
									</c:if>
									<c:if test="${item.freeze_status  eq '2' }">
											已解冻
									</c:if>
									</td>
								<td><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both" /></td>
								<td class="center"><shiro:hasPermission name="COMMERCIAL_TRANS_DETAIL">
										<a href="javascript:showDetail('${item.id}','${item.trans_source}');">详情</a>
									</shiro:hasPermission> <shiro:hasPermission name="COMMERCIAL_TRANS_REFUND">
										<c:if test="${item.trans_status eq 'SUCCESS' and item.trans_type eq 'PURCHASE'}">
											<a href="javascript:refund(${item.id});">退款</a>
										</c:if>
									</shiro:hasPermission>
									<shiro:hasPermission name="COMMERCIAL_SIGN_RECEIPT">
									 <c:if test="${item.trans_source eq 'SMALLBOX_MOBOLE_PHONE'&&item.trans_type ne 'BALANCE_QUERY'&&item.trans_status ne 'FAILED'}">
										<a href="javascript:showSignReceipt('${item.id}');">小票</a>
									</c:if>
									</shiro:hasPermission>
									<shiro:hasPermission name="FREEZE_FREE_TRANS">
									<c:if test="${item.bag_settle  eq '1' and item.freeze_status  eq '1' }">
											<%-- <a href="javascript:bagTransUnFreezed(${item.id},${item.trans_amount},${item.merchant_fee});">解冻</a> --%>
											<a href="javascript:bagTransUnFreezed(${item.merchant_no},${item.id},${item.trans_amount},${item.merchant_fee});">解冻</a>
										</c:if>
										
										<c:if test="${item.trans_status  eq 'SUCCESS' and item.trans_type eq 'PURCHASE'  and item.freeze_status ne '1' and item.bag_settle eq 1 }">
											<a href="javascript:bagTransFreeze(${item.id});">冻结</a>
										</c:if>
										<c:if test="${item.trans_status  eq 'SUCCESS' and item.trans_type eq 'PURCHASE' and item.bag_settle ne 1 and item.freeze_status ne '1'  and item.merchant_settle_date gt currdate }">
											<a href="javascript:TransFreeze(${item.id});">冻结</a>
										</c:if>
										<c:if test="${item.freeze_status eq '1' and item.bag_settle  ne '1' }">
											<a href="javascript:TransUnFreezed(${item.id});">解冻</a>
										</c:if>
									</shiro:hasPermission>
										</td>
									
							</tr>
						</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table" />
		</div>
	</div>
	<script type="text/javascript">
		$("#agentInput").val( $("[name=agentNo]").find("option:selected").text());
	</script>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
