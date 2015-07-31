<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	
		//创建统计信息html结构文件
	function createTransCountInfoHtml(totalMsg) {
		var html = '<li style="width: 210px;text-align: left;">\<b>游戏参与人数：'+totalMsg.total_user+'</b>人\</li>';
		html += '<ul>\<li style="width: 210px;text-align: left;">\<b>游戏产生金额：'+ totalMsg.total_amount+ '</b>元\</li>';
		html += '\<div class="clear"></div>\</ul>\<div class="clear"></div>';
		return html;
	}
		
		$(function(){
		$("#btnCountInfo").on("click", function() {
			this.value = "统计中，请稍后。。。";
			var param = $("form:first").serialize();
			$.post("${ctx}/purse/countRedBagInfo",param, function(data) {
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
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;移联红包</div>
   
   <form:form id="bagLoginQuery" action="${ctx}/purse/redBagQuery" method="post">
    <div id="search">
    	<div id="title">移联红包管理查询</div>
	      <ul>
	        <li><span>手机号：</span><input type="text"  value="${params['mobileNo']}" name="mobileNo" /></li>
	        <li><span>真实姓名：</span><input type="text"  value="${params['realName']}" name="realName" /></li>
	        <li><span>游戏金额：</span>
				<input  type="text"  style="width:102px" name="amountBegin" value="${params['amountBegin']}" >
					 ~
				<input  type="text"  style="width:102px" name="amountEnd" value="${params['amountEnd']}" >
			</li>
	        <li><span>创建时间：</span>
				<input onFocus="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" class="input" style="width:102px" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
					 ~
				<input onFocus="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" class="input" style="width:102px" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
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
          <th width="8%">序号</th>
          <th width="20%">手机号</th>
          <th width="12%">客户端</th>
          <th width="20%" >真实姓名</th>       
          <th width="20%">游戏金额</th>
          <th width="20%">创建时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.app_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.real_name}</td>
	          <td align="center">${item.red_balance}</td>
	          <td align="center">${item.create_time}</td>
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
