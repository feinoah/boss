<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script language="javascript" type="text/javascript" src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
</head>
<body>
<div class="item liHeight">
	<div class="title clear">用户信息 </div>
	<ul>
		  <li style="width:310px;" id="mobile_no"><span style="width:60px;">手机号：</span>
			 ${params['mobile_no']}
		 </li>
		 <li style="width:310px;" id="mobile_no"><span style="width:60px;">客户端：</span>
			 ${params['app_name']}
		 </li>
		  <li style="width:310px;" id="status"><span style="width:70px;">状态：</span>
		  	<c:choose>
		          <c:when test="${params['status'] eq '0'}">锁定</c:when>
		          <c:when test="${params['status'] eq '1'}">正常</c:when>
		          <c:when test="${params['status'] eq '2'}">冻结</c:when>
		          <c:otherwise>其它</c:otherwise>
		    </c:choose>
		 </li>
		  <li style="width:310px;" id="real_name"><span style="width:70px;">真实姓名：</span>
		 	  ${params['real_name']}
		 </li>
		 <li style="width:310px;" id="balance"><span style="width:70px;">当天余额：</span>	
		 	 ${params['balance']}
		 </li>		
		 <li style="width:310px;" id="balance1"><span style="width:70px;">历史余额：</span>	
		 	 ${params['balance1']}
		 </li>		
		 <%-- <li style="width:310px;" id="settle_account_no"><span style="width:70px;">结算帐号：</span>
		 	<c:if test="${params['settle_account_no']!= null}">
			  	<u:cardcut content="${params['settle_account_no']}" />
		 	</c:if>
		 </li>
		 <li style="width:310px;" id="settle_account_name"><span style="width:80px;">结算帐户名：</span>
		     ${params['settle_account_name']}
		 </li> --%>
		 <li style="width:310px;" id="fee"><span style="width:50px;">手续费：</span>
		     ${params['fee']}
		 </li>
		 <li style="width:310px;" id="idcard"><span style="width:70px;">身份证号：</span>
		     ${params['idcard']}
		 </li>
	</ul>
	<ul>
	 <li style="width:310px;" id="source"><span style="width:50px;">来源：</span>
		     <c:choose>
		          <c:when test="${params['source'] eq 'client'}">登录同步</c:when>
		          <c:when test="${params['source'] eq 'byapi'}">接口注册</c:when>
		     </c:choose>
		 </li>
		 <li style="width:310px;" id="real_name_auth"><span style="width:60px;">实名认证：</span>
		     <c:choose>
		          <c:when test="${params['real_name_auth'] eq 0}">未提交</c:when>
		          <c:when test="${params['real_name_auth'] eq 1}">待审核</c:when>
		          <c:when test="${params['real_name_auth'] eq 2}">审核成功</c:when>
		          <c:when test="${params['real_name_auth'] eq 3}">审核失败</c:when>
		          <c:otherwise>${params['real_name_auth']}</c:otherwise>
		     </c:choose>
		 </li>
		 <li style="width:310px;" id="single_recharge_max_amount"><span style="width:90px">单笔充值限额：</span>
		     ${params['single_recharge_max_amount']}
		 </li>
		 <li style="width:310px;" id="day_recharge_max_amount"><span style="width:90px">当天充值限额：</span>
		     ${params['day_recharge_max_amount']}
		 </li>
		  <li style="width:310px;" id="day_increment_max_amount"><span style="width:110px">当天增值服务限额：</span>
		     ${params['day_increment_max_amount']}
		 </li>
		  <%-- <li style="width:310px;" id="day_extraction_max_amount"><span style="width:90px">当天提现限额：</span>
		     ${params['day_extraction_max_amount']}
		 </li> --%>
		  <li style="width:310px;" id="day_transfer_max_amount"><span style="width:90px">当天转账限额：</span>
		     ${params['day_transfer_max_amount']}
		 </li>
		 <%-- <li style="width:310px;" id="day_recharge_max_amount_count"><span style="width:140px">当天充值最大限额数量：</span>
		     ${params['day_recharge_max_amount_count']}
		 </li>
		 <li style="width:310px;" id="day_increment_max_amount_count"><span style="width:160px">当天增值服务最大限额数量：</span>
		     ${params['day_increment_max_amount_count']}
		 </li>
		 <li style="width:310px;" id="day_extraction_max_amount_count"><span style="width:140px;">当天提现最大限额数量：</span>
		     ${params['day_extraction_max_amount_count']}
		 </li> --%>
		 <li style="width:310px;" id="day_transfer_max_amount_count"><span style="width:140px">当天转账最大限额数量：</span>
		     ${params['day_transfer_max_amount_count']}
		 </li>
		 
		 <li style="width:310px;" id="day_transfer_max_amount_count"><span style="width:140px">滞留金额：</span>
		     ${params['retention_money']}
		 </li>
		 <li style="width:310px;" id="day_transfer_max_amount_count"><span style="width:140px">T+1每天免费提取次数：</span>
		     ${params['tone_free_count']}
		 </li>
		 <li style="width:310px;" id="is_tzero"><span style="width:140px;">是否T+0提现:</span>
		     <c:choose>
		          <c:when test="${params['is_tzero'] eq '0'}">否</c:when>
		          <c:when test="${params['is_tzero'] eq '1'}">是</c:when>
		     </c:choose>
		 </li>
		 <c:if test="${params['is_tzero'] eq '0'}">
			 <li style="width:310px;" id="is_tzero"><span style="width:210px;">是否体验过11元额度的T+0提现:</span>
			     <c:choose>
			          <c:when test="${params['is_experience'] eq '0'}">否</c:when>
			          <c:when test="${params['is_experience'] eq '1'}">是</c:when>
			     </c:choose>
			 </li>
		 </c:if>
		 <c:if test="${params['is_tzero'] eq '1'}">
		 	<li style="width:310px;" id="tzero_withdraws_max_amount"><span style="width:140px">T+0单笔提现限额：</span>
		     ${params['tzero_withdraws_max_amount']}
		 	</li>
		 	<li style="width:310px;" id="tzero_withdraws_max_amount"><span style="width:140px">T+0当日提现限额：</span>
		     ${params['day_extraction_max_amount']}
		 	</li>
		 	<li style="width:310px;" id="tzero_fee"><span style="width:140px">T+0提现最低手续费：</span>
		     ${params['tzero_fee']}
		 	</li>
		 </c:if>
		 <li style="width:310px;" id="day_transfer_max_amount_count"><span style="width:140px">T+1提现限额：</span>
		     ${params['tone_withdraws_max_amount']}
		 </li>
		 <%-- <li style="width:310px;" id="day_transfer_max_amount_count"><span style="width:140px">允许提现开始时间：</span>
		     ${params['withdraws_time_star_short']}~ ${params['withdraws_time_end_short']}
		 </li> --%>
	</ul>
	<ul>	 
		 <li style="width:310px;" id="create_time"><span style="width:70px;">创建时间：</span>
		   <fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
		 <li style="width:310px;" id="last_use_time"><span style="width:100px;">最后使用时间：</span>
		  <fmt:formatDate value="${params['last_use_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
		 <li style="width:310px;" id="last_login_time"><span style="width:120px;">最后一次登录时间：</span>
		 <fmt:formatDate value="${params['last_login_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/>
		 </li>
<%-- 		 <li style="width:610px;" id="login_key"><span style="width:70px;">登录密钥：</span>
		     ${params['login_key']}
		 </li> --%>
    </ul>
    <div class="title clear">结算账户信息 </div>
    		<ul>	
			   <li style="width:260px;" id="handStatusDesc"><span style="width:62px;">结算卡号：</span>
				     ${params['settle_account_no']}
				 </li> 
				 <li style="width:200px;" id="handStatusDesc"><span style="width:62px;">清算行号：</span>
				     ${params['bank_no']}
				 </li> 
				 <li style="width:120px;" id="handStatusDesc"><span style="width:50px;">开户名：</span>
				     ${params['settle_account_name']}
				 </li> 
	       </ul>
	       <br />
	       <br />
    		<c:forEach items="${settleAccountList}" var="item" varStatus="status">
				<ul>	
				   <li style="width:260px;" id="handStatusDesc"><span style="width:62px;">提现卡号：</span>
					     ${item.account_no}
					 </li> 
					 <li style="width:200px;" id="handStatusDesc"><span style="width:62px;">清算行号：</span>
					     ${item.bank_no}
					 </li> 
					 <li style="width:120px;" id="handStatusDesc"><span style="width:50px;">开户名：</span>
					     ${item.account_name}
					 </li> 
		       </ul>
			</c:forEach>
		  <div class="clear"></div>
		  
		<div class="title clear">最近风险操作 </div>
			<ul>	
				<li style="width:100px;" id=""><span>时间</span></li> 
				<li style="width:100px;" id=""><span>操作内容</span></li> 
				<li style="width:100px;" id=""><span>操作人</span></li>
				<li style="width:110px;" id=""><span style="width:100px;">风控金额（元）</span></li>
				<li style="width:110px;" id=""><span style="width:100px;">冻结天数（天）</span></li> 
				<li style="width:110px;" id=""><span>原因</span></li>
		    </ul>
    		<c:forEach items="${bagFreezes}" var="item" varStatus="status">
				<ul>	
				 	<li style="width:100px;" id=""><span><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd" type="both" /></span></li> 
					<li style="width:100px;" id=""><span>
						<c:choose>
							<c:when test="${item.operation eq '0' && (item.channel eq '0' || item.channel eq '1')}">交易冻结</c:when>
							<c:when test="${item.operation eq '1' && (item.channel eq '0' || item.channel eq '1')}">交易解冻</c:when>
							<c:when test="${item.operation eq '0' && item.channel eq '2'}">风险冻结</c:when>
							<c:when test="${item.operation eq '1' && item.channel eq '2'}">风险解冻</c:when>
							<c:otherwise>-</c:otherwise>
						</c:choose>
					</span></li> 
					<li style="width:100px;" id=""><span>${item.operater}</span></li>
					<li style="width:110px;" id=""><span style="width:100px;">${item.amount ne null ? item.amount : '-'}</span></li>
					<li style="width:110px;" id=""><span style="width:100px;">${item.freeze_day ne null ? item.freeze_day : '-'}</span></li> 
					<li style="width:110px;" id=""><span>${item.msg ne null ? item.msg : '-'}</span></li>
		       </ul>
			</c:forEach>
		 <div class="clear"></div>
</div>
</body>
</html>