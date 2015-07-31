<%@page pageEncoding="utf-8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	function showDetail(id)
		{
			$.dialog({title:'用户详情',width: 700,height:600,resize: false,lock: true,max:false,content: 'url:bagDetail?id='+id+'&layout=no'});
		}
	
		function resetPassword(id){
			if(!confirm("是否重置用户密码？"))
			{
				return;
			}
			$.post(		
					'${ctx}/purse/bagUserReset',
					{id:id},
					function(data)
					{
						if(data == 1)
						{
							alert("重置用户密码成功,新密码:888888");
							location=location ;
						}
					}
			);
		}
		
		
		function bagFreeze(mobileNo,appType){
			
			window.freezeDialog = $.dialog({
			title : '账户冻结',
			width : 500,
			height : 300,
			resize : false,
			lock : true,
			max : false,
			content : 'url:bagFreezeDetail?mobileNo=' + mobileNo + '&appType='+appType+'&type=1&layout=no',
			close:function(){
				top.$('form:first').submit();
			}
		});
		}
		
		function bagUnFreeze(mobileNo,appType){
			
			window.freezeDialog = $.dialog({
			title : '账户解冻',
			width : 500,
			height : 300,
			resize : false,
			lock : true,
			max : false,
			content : 'url:bagFreezeDetail?mobileNo=' + mobileNo + '&appType='+appType+'&type=2&layout=no',
			close:function(){
				top.$('form:first').submit();
			}
		});
		}
		
		function lock(userId,status_lock){
			var lock=document.getElementById("lock").text;
			var mobile_no = "${params['mobile_no']}";
			var real_name = "${params['real_name']}";
						
			$.dialog.confirm('确定要'+lock+'该商户？', function(){
			     $.ajax({
			          url: "${ctx}/purse/bagUserLock",
			          type:"POST",
					  data:"userId="+userId+"&status_lock="+status_lock+"&layout=no",
			          error:function()
                      {
                          alert(lock+"出错");
                      },
			          success: function(){
				    	  ico="success.gif";
				    	  msg = "该商户已"+lock;
			      	   	var dialog = $.dialog({title: '提示',lock:true,content: msg,icon: ico,ok:null ,close:function(){
							location.href="${ctx}/purse/bagLoginQuery?mobile_no="+mobile_no+"&real_name="+real_name;
						}});
			           	 
			           }
			         });
			   });
	
		}
		
		
		//创建统计信息html结构文件
	function createTransCountInfoHtml(totalMsg) {
		var html = '<ul>\<li style="width: 210px;text-align: left;">\<b>全部金额：'+ totalMsg.total_amount+ '</b>元\</li>';
		html += '<li style="width: 210px;text-align: left;">\<b>当天金额：'+totalMsg.total_today+'</b>元\</li>';
		html += '<li style="width: 210px;text-align: left;">\<b>历史金额：'+totalMsg.total_his+'</b>元\</li>';
		html += '\<div class="clear"></div>\</ul>\<div class="clear"></div>';
		return html;
	}
		
		$(function(){
		$("#btnCountInfo").on("click", function() {
			this.value = "统计中，请稍后。。。";
			$.post("${ctx}/purse/countBagUserInfo", function(data) {
				var html = createTransCountInfoHtml(data);
				$("#total_msg").html(html);
			});
		});
	});
		
		function toTransAcc(realName,mobileNo,balance,balance1,appType){
			 $.dialog({height:300,width: 420,lock: true,drag: false,resize: false,max: false,
				 content: 'url:${ctx}/purse/checkTransferAcc?realName='+realName+'&mobileNo='+mobileNo+'&balance='+balance+'&balance1='+balance1+'&appType='+appType+'&layout=no',close: function(){
				// $("#submit").click();
		     }});
		}
		
		$(function(){
			$("#reset").click(function(){
				$(":text").val("");
				$("select").val("-1");
			})
		})
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包用户管理</div>
   
   <form:form id="bagLoginQuery" action="${ctx}/purse/bagLoginQuery" method="post">
    <div id="search">
    	<div id="title">钱包用户管理查询</div>
	      <ul>
	        <li><span>手机号：</span><input type="text"  value="${params['mobile_no']}" name="mobile_no" /></li>
	        <li><span>真实姓名：</span><input type="text"  value="${params['real_name']}" name="real_name" /></li>
	        <li><span>创建时间：</span>
				<input onFocus="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" class="input" style="width:102px" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
					 ~
				<input onFocus="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" class="input" style="width:102px" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
			</li>
			<li><span style="width: 76px;">余额类型：</span>
				<select name="balanceType">
					<option value="-1" <c:out value="${params['balanceType'] eq '-1'?'selected':'' }"/>>全部</option>
					<option value="0" <c:out value="${params['balanceType'] eq '0'?'selected':'' }"/>>当天余额</option>
					<option value="1" <c:out value="${params['balanceType'] eq '1'?'selected':'' }"/>>历史余额</option>
				</select>
			</li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="button" id="reset"   value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div id="total_msg" class="total_msg">
			<input class="button blue medium" type="button" id="btnCountInfo" value="统计用户信息" />
		</div>
    <div class="tbdata" >
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="10%">手机号</th>
          <th width="6%">客户端</th>
          <th width="8%" >真实姓名</th>       
          <th width="10%">当天余额</th>
          <th width="10%">历史余额</th>
          <th width="6%">状态</th>
          <th width="10%">交易滞留金</th>
          <th width="10%">风险冻结金</th>
          <th width="10%">总冻结金额</th>
          <th width="10%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.app_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.real_name}</td>
	          <td align="center">${item.balance}</td>
	          <td align="center">${item.balance1}</td>
	          <td align="center"><c:choose>
				  <c:when test="${item.status eq '1'}">正常</c:when>
				  <c:when test="${item.status eq '0'}"><span class="font_red">锁定</span></c:when> 
			  </c:choose></td>
	          <td align="center"><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.retention_money}" /></td>
	          <td align="center"><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.manual_retention_money}" /></td>
	          <td align="center"><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.manual_retention_money + item.retention_money}" /></td>
			  <td align="center">
			  <shiro:hasPermission name="PHONE_USERQUERY_RESETPASSWORD">
			  <a href="javascript:resetPassword(${item.id});">重置密码</a>|
			  </shiro:hasPermission>
			  <shiro:hasPermission name="PHONE_USERQUERY_FREEZE">
			  <c:if test="${item.status=='0'}">
					<a href="javascript:lock('${item.id}','1');" id="lock">解锁</a>|
				</c:if>
				<c:if test="${item.status=='1'}">
					<a href="javascript:lock('${item.id}','0');" id="lock">锁定</a>|
				</c:if>
			 	<c:if test="${item.manual_retention_money gt 0}">
					<a href="javascript:bagUnFreeze('${item.mobile_no}','${ item.app_type }');" id="lock">解冻</a>|
				</c:if>
				<a href="javascript:bagFreeze('${item.mobile_no}','${ item.app_type }');" id="lock">冻结</a>|
				
			  </shiro:hasPermission>
			 <shiro:hasPermission name="PHONE_USERQUERY_DETAIL"> 
			 <a href="javascript:showDetail(${item.id});">详情</a>
			 </shiro:hasPermission>
			 <shiro:hasPermission name="PHONE_USERQUERY_DETAIL"> 
			  |<a href="${ctx}/purse/bagUserModify?id=${item.id}">修改</a>
			 </shiro:hasPermission>
			 <shiro:hasPermission name="BAG_ACCOUNT_APPLY"> 
			  |<a href="javascript:toTransAcc('${item.real_name}','${item.mobile_no}','${item.balance}','${item.balance1}','${item.app_type}','${item.real_name}');">调账</a>
			 </shiro:hasPermission>
			</td>
			</tr>
          </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
  <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
