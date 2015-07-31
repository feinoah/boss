<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ ctx}/scripts/utils.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/transTimeLimit.js" id="transTimeLimit" data="<shiro:hasPermission name="TRANS_TIME_LIMIT">1</shiro:hasPermission><shiro:lacksPermission name="TRANS_TIME_LIMIT">0</shiro:lacksPermission>"></script>
<script type="text/javascript">
	var root = '${ctx}';
	function cleanEmpty(){
		$("input:text").removeAttr("value");
		$("select[name=status] option:selected").removeAttr("selected");
		$("form").reset();
	}
	
	//创建统计信息html结构文件
	function createTransCountInfoHtml(totalMsg) {
		var html = '<ul>\<li>\<b>笔数：'+ totalMsg.total_pur_count+ '</b>笔\</li>';
		html += '<li style="width: 300px;text-align: left;">';
		if(totalMsg.total_pur_amount>10000){
			var total_pur_amount_w = totalMsg.total_pur_amount/10000;
			total_pur_amount_w = parseInt(total_pur_amount_w+0.5);
			html +=  "<b>总金额约：";
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
	
	
	function showDetail(id, transSource) {
	
		$.dialog({
			title : '交易详情',
			width : 880,
			height : 620,
			resize : false,
			lock : true,
			max : false,
			content : 'url:fastDetail?id=' + id + '&layout=no'
		});

	}
	
	function showAirOrderDetail(id, transSource) {
		
		$.dialog({
			title : '订单详情',
			width : 700,
			height : 300,
			resize : false,
			lock : true,
			max : false,
			content : 'url:fastAirOrderDetail?id=' + id + '&layout=no'
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
			content : 'url:fastTransFreeze?id=' + id + '&layout=no',
			close:function(){
				top.$('form:first').submit();
				
			}
		});
	}
	
	
	//钱包冻结交易
	function bagTransFreeze(id) {
		window.freezeDialog=$.dialog({
			title : '钱包交易冻结',
			width : 600,
			height : 610,
			resize : false,
			lock : true,
			max : false,
			content : 'url:bagFastTransFreeze?id=' + id + '&layout=no',
			close:function(){
				top.$('form:first').submit();
				
			}
		});
	}
	
	
	//解冻交易
	function TransUnFreezed(id) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
	
		/* $.post(root + "/mer/freezedInfo",{"fast_id":id}, function(data) { */
		$.post(root + "/mer/freezedInfoNew",{"fast_id":id}, function(data) {
			var freezeDate = new Date(data.oper_time);
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+(freezeDate.getMinutes() > 9 ? freezeDate.getMinutes() :'0' +  freezeDate.getMinutes()) +":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.oper_name +"</br></br>解冻后清算日期为："+ndate.getFullYear() + "-" + (ndate.getMonth() + 1) + "-" + ndate.getDate()+ "" + "</br></br>确认解冻？",function(){
				$.ajax({
					/* url:"${ctx}/mer/transUnFreeze", */
					url:"${ctx}/mer/transUnFreezeNew",
					type:"post",
					data: {"fastId":id},
					dataType:"text",
					success:function(d,status){
						top.$('form:first').submit();
					}
				})
			})
		})
		
	}
	
	
	
	/* //解冻交易
	function TransUnFreezed(id) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
	
		$.post(root + "/mer/freezedInfo",{"fast_id":id}, function(data) {
			var freezeDate = new Date(data.create_time);
			
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+(freezeDate.getMinutes() > 9 ? freezeDate.getMinutes() :'0' +  freezeDate.getMinutes())+":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.freeze_people +"</br></br>解冻后清算日期为："+ndate.getFullYear() + "-" + (ndate.getMonth() + 1) + "-" + ndate.getDate()+ "" + "</br></br>确认解冻？",function(){
				$.ajax({
					url:"${ctx}/mer/transUnFreeze",
					type:"post",
					data: {"fastId":id},
					dataType:"text",
					success:function(d,status){
						/* location.href="${ctx}/mer/transfast"; */
						/*top.$('form:first').submit();
					}
				})
			})
		})
		
	}
	 */
	
	
	
	function bagTransUnFreezed(merNo,id,amount,fee) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
		amount = amount - fee;
		$.post(root + "/mer/freezedInfoNew",{"fast_id":id}, function(data) {
			var freezeDate = new Date(data.oper_time);
			
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+freezeDate.getMinutes()+":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.oper_name +"</br></br>确认解冻？",function(){
				$.ajax({
					url:"${ctx}/mer/bagTransUnFreezeNew",
					type:"post",
					data: {"fastId":id,"merNo":merNo,"amount":amount},
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
	
	
	/* //解冻钱包交易
	function bagTransUnFreezed(id,amount,fee) {
		var date = new Date();
		var ndate =  new Date(Date.parse(date) + 86400000);
		amount = amount-fee;
		$.post(root + "/mer/freezedInfo",{"fast_id":id}, function(data) {
			var freezeDate = new Date(data.create_time);
			var merNo= data.merchant_no;
			$.dialog.confirm("最后一次冻结时间："+freezeDate.getFullYear()+"-"+(freezeDate.getMonth() +1)+"-"+
					freezeDate.getDate()+"&nbsp;"+freezeDate.getHours() +":"+freezeDate.getMinutes()+":"+(freezeDate.getSeconds() > 9 ?freezeDate.getSeconds():'0'+freezeDate.getSeconds())
					+"</br></br>冻结操作人：" +data.freeze_people +"</br></br>确认解冻？",function(){
				$.ajax({
					url:"${ctx}/mer/bagTransUnFreeze",
					type:"post",
					data: {"fastId":id,"merNo":merNo,"amount":amount},
					dataType:"text",
					success:function(d,status){
						/* location.href="${ctx}/mer/transfast"; */
						/* alert(d);
						top.$('form:first').submit();
					}
				})
			})
		})
		
	}
	 */
	
	$(function(){
		$("#btnTransCountInfo").on("click", function() {
			this.value = "统计中，请稍后。。。";
			var params = $("#transfast").serialize();
			$.post(root + "/mer/countTransFast", params, function(data) {
				var html = createTransCountInfoHtml(data);
				$("#total_msg").html(html);
			});
		});
	});

</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理&gt;交易查询（快捷）
		</div>

		<form id="transfast" action="${ctx}/mer/transfast" method="post">
			<div id="search">

				<div id="title">交易查询</div>
				<ul>
					<li style="width: 216px;">
						<span>交易卡号：</span>
						<input type="text" value="${params['cardNo']}" name="cardNo" />
					</li>
					<li>
						<span style="width: 90px;">商户名称/编号：</span>
						<input type="text" value="${params['merchant']}" name="merchant"/>
					</li>
					<li>
						<span>交易时间：</span>
					<%-- 	<input onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})" type="text" style="width: 102px"
							name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
						~
						<input onFocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true})" type="text" style="width: 102px"
							name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
					 --%>
					<input id="createTimeBegin" type="text" style="width: 102px"
							name="createTimeBegin" value="${params['createTimeBegin']}" >
							<!-- onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d 00:00:00\'}'})" -->
						~
						<input id="createTimeEnd" type="text" style="width: 102px"
							name="createTimeEnd" value="${params['createTimeEnd']}" >
							<!-- onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'createTimeBegin\')}',maxDate:'%y-%M-%d 23:59:59'})" -->
					
					
					</li>
					
					
				</ul>
				<div class="clear"></div>
				<ul>
					<li style="width: 216px;">
						<span>订单号：</span>
						<input type="text" value="${params['orderNo']}" name="orderNo" />
					</li>
					<li style="width: 218px;">
						<span style="width:90px;">交易状态：</span>
						<select style="padding: 2px; width: 89px" name="status" title="状态查询">
							<option value="" ${params['status'] eq '' ?'selected':''}>全部</option>
							<option value="已成功" ${params['status'] eq '已成功' ?'selected':''}>已成功</option>
							<option value="待付款" ${params['status'] eq '待付款'?'selected':''}>待付款</option>
						</select>
					</li>
						<li>
						<span>收单机构：</span>
						<select style="padding: 2px; width: 89px" name="acqEnname" title="收单机构查询">
							<option value="" ${params['acqEnname'] eq '' ?'selected':''}>全部</option>
							<option value="yibao" ${params['acqEnname'] eq 'yibao' ?'selected':''}>易宝</option>
							<option value="tftpay" ${params['acqEnname'] eq 'tftpay'?'selected':''}>腾付通</option>
						</select>
						<%-- <u:fastSelectAcqOrg sname="acqEnname" value="${ params['fast_acq_enname'] }"/> --%>
					</li>
					
					<li style="width: 200px;">
						<span>冻结状态：</span>
						<select style="padding: 2px; width: 89px" id="freezeStatus" name="freezeStatus">
							<option value="-1" <c:out value="${params['freezeStatus'] eq '-1'?'selected':'' }"/>>全部</option>
							<option value="0" <c:out value="${params['freezeStatus'] eq '0'?'selected':'' }"/>>正常</option>
							<option value="1" <c:out value="${params['freezeStatus'] eq '1'?'selected':'' }"/>>已冻结</option>
							<option value="2" <c:out value="${params['freezeStatus'] eq '2'?'selected':'' }"/>>已解冻</option>
					      </select> 
					</li>
				</ul>
				<div class="clear"></div>
				<ul>
					<li>
						<span style="width: 76px;">交易金额：</span>
						<input type="text" value="${params['start_trans_amount']}" name="start_trans_amount"  style="width: 121px" maxlength="12"/>
						~
						<input type="text" value="${params['end_trans_amount']}" name="end_trans_amount"  style="width: 121px" maxlength="12"/>
					</li>
				
				</ul>
				<div class="clear"></div>
				
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton" value="查询" />
				<input name="reset" class="button blue medium" type="reset" onclick="cleanEmpty();" value="清空" />

			</div>
		</form>
		<div id="total_msg" class="total_msg">
			<input class="button blue medium" type="button" id="btnTransCountInfo" value="统计交易信息" />
		</div>
		<div class="tbdata">
			<a name="_table"></a>
			<table width="100%" cellspacing="0" class="t2">
				<thead>
					<tr>
					
						<!-- <th width="4%">序号</th>
						<th width="15%">商户简称</th>
						<th width="15%">商户编号</th>
						<th width="8%">收单机构</th>
						<th width="8%">交易类型</th>
						<th width="14%">交易卡号</th>
						<th width="10%">金额(元)</th>
						<th width="8%">状态</th>
						
						<th width="16%">创建时间</th>
						<th width="5%">操作</th> -->
						<th width="4%">序号</th>
						<th width="13%">商户简称</th>
						<th width="15%">商户编号</th>
						<th width="7%">收单机构</th>
						<th width="7%">交易类型</th>
						<th width="14%">交易卡号</th>
						<th width="8%">金额(元)</th>
						<th width="7%">交易状态</th>
						<th width="7%">冻结状态</th>
						<th width="13%">创建时间</th>
						<th width="7%">操作</th>
						
						</tr>
						<c:forEach items="${list.content}" var="item" varStatus="status">
							<tr class="${status.count % 2 == 0 ? 'a1' : ''}">
								<td class="center"><span class="center">${status.count}</span></td>
								<td><u:substring length="10" content="${item.merchant_short_name}" /></td>
								<td>${item.merchant_no}</td>
								<td>
									<c:if test="${item.acq_enname eq 'eptok' }">YS</c:if>
									<c:if test="${item.acq_enname eq 'tftpay'}">腾付通</c:if>
									<c:if test="${item.acq_enname eq 'bill'}">快钱</c:if>
									<c:if test="${item.acq_enname eq 'zypay'}">中意支付</c:if>
									<c:if test="${item.acq_enname eq 'yibao'}">易宝</c:if>
									<c:if test="${item.acq_enname eq 'xlink'}">讯联</c:if>
									<c:if test="${item.acq_enname eq 'hypay'}">翰亿</c:if>
									<c:if test="${item.acq_enname eq 'ubs'}">瑞银信</c:if>
								</td>
								<td><c:choose>
										<c:when test="${item.biz_name eq 'PURCHASE'}">消费</c:when>
										<c:when test="${item.biz_name eq 'BAG'}">钱包充值</c:when>
										<c:when test="${item.biz_name eq 'MOBILE'}">手机充值 </c:when>
										<c:when test="${item.biz_name eq 'PURCHASE_REFUND'}">退款 </c:when>
										<c:otherwise>${item.biz_name }</c:otherwise>
									</c:choose></td>
								<td>
									<shiro:hasPermission name="TRANS_CARD_NO">${item.card_no}</shiro:hasPermission>
									<shiro:lacksPermission name="TRANS_CARD_NO"><u:cardcut content="${item.card_no}" /></shiro:lacksPermission>
								</td>
								<td align="right">${item.amount}</td>
								<td><span title="${item.status}">
										<u:substring length="25" content="${item.status}" />
									</span></td>
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
								<td><fmt:formatDate value="${item.create_time}" type="both" /></td>
								<td class="center">
									<a href="javascript:showDetail('${item.id}');">交易详情</a>
									<a href="javascript:showAirOrderDetail('${item.id}');">订单详情</a>
									<shiro:hasPermission name="FREEZE_FREE_TRANS">
									<c:if test="${item.bag_settle  eq '1' and item.freeze_status  eq '1' }">
											<%-- <a href="javascript:bagTransUnFreezed(${item.id},${item.amount},${item.merchant_fee});">解冻</a> --%>
											<a href="javascript:bagTransUnFreezed(${item.merchant_no},${item.id},${item.amount},${item.merchant_fee});">解冻</a>
									</c:if>
									<c:if test="${item.status  eq '已成功' and item.freeze_status ne '1' and item.bag_settle eq 1}">
											<a href="javascript:bagTransFreeze(${item.id});">冻结</a>
									</c:if>
									<c:if test="${item.status  eq '已成功' and item.settle_date gt currdate  and item.freeze_status ne '1' and item.bag_settle ne 1}">
											<a href="javascript:TransFreeze(${item.id});">冻结</a>
									</c:if>
									<c:if test="${item.freeze_status  eq '1' and item.bag_settle  ne '1' }">
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
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
