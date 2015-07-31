	<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
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
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="content">

		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理&gt;商户详情
		</div>
		<form:form id="merUpdate">
			<div class="item">
				<div class="title">基本信息</div>

				<c:set var="date1">
					<fmt:formatDate value="${params.create_time}" pattern="yyyy-MM-dd" type="date" />
				</c:set>
				<c:set var="date2">2014-01-14</c:set>
				<c:set var="date3">2014-04-01</c:set>
				<ul>
					<li style="display: none">
						<label>流水号</label>
						<input type="text" id="id" name="id" value="${params.id}" />
					</li>
					<li style="display: none">
						<label>商户编号</label>
						<input type="text" id="merchant_no" name="merchant_no" value="${params.merchant_no}" />
					</li>
					<li style="display: none">
						<label>代理商编号</label>
						<input type="text" id="agent_no" name="agent_no" value="${params.agent_no}" />
					</li>

					<li style="width: 500px;">
						<label>代理商名称：</label>
						${params.agent_name}
					</li>
					<li style="width: 500px;" class="column2">
						<label>商户全称：</label>
						${params.merchant_name}
					</li>

					<li>
						<label>商户编号：</label>
						${params.merchant_no}
					</li>
					<li style="width: 500px;" class="column2">
						<label>商户简称：</label>
						${params.merchant_short_name}
					</li>
					<li>
						<label>商户类型：</label>
							<c:if test="${params.merchant_type eq '5300' }">航空、加油、超市</c:if>
							<c:if test="${params.merchant_type eq '6300' }">保险、公共事业</c:if>
							<c:if test="${params.merchant_type eq '5812' }">餐娱类</c:if>
							<c:if test="${params.merchant_type eq '5111' }">批发类</c:if>
							<c:if test="${params.merchant_type eq '5541' }">民生类</c:if>
							<c:if test="${params.merchant_type eq '5331' }">一般类</c:if>
							<c:if test="${params.merchant_type eq '1520' }">房车类</c:if>
							<c:if test="${params.merchant_type eq '1011' }">其他</c:if>
					</li>
					<li>
						<label>企业法人：</label>
						${params.lawyer}
					</li>

					<li>
						<label>业务联系人：</label>
						${params.link_name}
					</li>
					<li>
						<label>移付宝销售：</label>
						${params.self_sale_name}
					</li>
					<li style="width: 250px;" class="column2">
						<label>所属代理商：</label>
						${params.belong_to_agent_name}
					</li>
                    <li>
						<label>所属销售：</label>
						${params.sale_name}
					</li>
					
					
 					<li>
						<label>电话：</label>
						<shiro:lacksPermission name="FENGKONG_SHOW_PERMISSION"  >
							${fn:substring(params.phone,0,3)}****${fn:substring(params.phone,7,11)}
						</shiro:lacksPermission>
						<shiro:hasPermission name="FENGKONG_SHOW_PERMISSION">
							${params.phone}
						</shiro:hasPermission>
							
					</li>
 					<li style="width: 500px;" class="column2">
						<label>Email：</label>
						<shiro:hasPermission name="SHOW_PHONE_PER">
						${fn:substring(params.email,0,2)}***${fn:substring(params.email,5,30)}
						</shiro:hasPermission>
						<shiro:hasPermission name="FENGKONG_SHOW_PERMISSION">
							${params.email}
						</shiro:hasPermission>
					</li>
					<%--			<li><label>签约扣率：</label><input style="width:43px" class="input" type="text" id="rate1"  name="rate1"  value="${params.rate1}"/>%到<input class="input" type="text"  style="width:43px" id="rate2"  name="rate2"  value="${params.rate2}"/>封顶<label class="must">*</label></li>--%>

					<li>
						<label>登录手机号：</label>
						<shiro:hasPermission name="SHOW_PHONE_PER">
							${fn:substring(params.mobile_username,0,3)}****${fn:substring(params.mobile_username,7,11)}
						</shiro:hasPermission>
						<shiro:hasPermission name="FENGKONG_SHOW_PERMISSION">
							${params.mobile_username}
						</shiro:hasPermission>
					</li>
					
					

					<li>
<!--						<label>是否实名：</label>-->
						<label>资料是否齐全：</label>
						<c:choose>
							<c:when test="${params['real_flag'] eq '1'}">是</c:when>
							<c:when test="${params['real_flag'] eq '0'}">否</c:when>
						</c:choose>

					</li>
					<li>
						<label>密码暗语：</label>
						${params.code_word}
					</li>
					<li class="">
						<label>法人身份证号：</label>
						${params.id_card_no}
					</li>
					<li class="">
						<label>商户机具数量：</label>
						${params.terminal_count}
					</li>
					<%-- 	<li class="column3"><label>扣率类型：</label> 
	         	 <c:out value="${params['fee_type'] eq 'RATIO'?'扣率':'' }"/>
	         	 <c:out value="${params['fee_type'] eq 'CAPPING'?'封顶 ':'' }"/>
	           <c:out value="${params['fee_type'] eq 'LADDER'?'阶梯 ':'' }"/>
	        	 
			</li>
			<c:if test="${params['fee_type'] eq 'RATIO'}">
			            <li id="fee_rate" style=""><label>比例：</label> ${params['rate1']}%</li>
			</c:if>
			<c:if test="${params['fee_type'] eq 'CAPPING'}">
			            <li id="fee_rate" style=""><label>比例：</label> ${params['rate1']}%</li>
									<li id="fee_cap_amount" style=""><label>封顶金额：</label> ${params['fee_cap_amount']} （元）</li>
									<li id="fee_max_amount" style=""><label>封顶手续费：</label> ${params['rate2']} （元）</li>
			</c:if>
			<c:if test="${params['fee_type'] eq 'LADDER'}">
						<li id="fee_ladder" class="column3">
							<label>阶梯设置：</label> ${params['ladder_min']} %
							&lt;
							 ${params['ladder_value']} （元）
							&lt;
							 ${params['ladder_max']} %
						</li>
			</c:if>
			--%>


					<li style="width: 500px;">
						<label>签约扣率：</label>
						<c:if test="${params.fee_type eq 'RATIO' }">${params.fee_rate}~不封顶</c:if>
						<c:if test="${params.fee_type eq 'CAPPING'}">${params.fee_rate}~<fmt:formatNumber type="currency" pattern="#,##0.00#"
								value="${params.fee_max_amount}" />（元）封顶</c:if>
						<c:if test="${params.fee_type eq 'LADDER'}">${params.ladder_min}%&lt;<fmt:formatNumber type="currency" pattern="#,##0.00#"
								value="${params.ladder_value}" /> （元）&lt;${params.ladder_max}%</c:if>
					</li>
					<li>
						<label>客户端类型：</label>
						${appTypeName}
					</li>


					<li>
						<label>商户状态：</label>
						<c:if test="${params.open_status eq '1' }">正常</c:if>
						<c:if test="${params.open_status eq '0' }">商户关闭</c:if>
						<c:if test="${params.open_status eq '2' }">待审核</c:if>
						<c:if test="${params.open_status eq '3' }">审核失败 </c:if>
						<c:if test="${params.open_status eq '4' }">冻结 </c:if>
						<c:if test="${params.open_status eq '5' }">机具绑定 </c:if>
						<c:if test="${params.open_status eq '6' }">初审 </c:if>
					</li>
					<li>
						<label>设备类型：</label>
						<u:postype svalue="${params.pos_type}" />
<!--						<c:if test="${params.pos_type eq '1' }">移联商宝</c:if>-->
<!--						<c:if test="${params.pos_type eq '2' }">传统POS</c:if>-->
<!--						<c:if test="${params.pos_type eq '3' }">移小宝</c:if>-->
<!--						<c:if test="${params.pos_type eq '4' }">移联商通</c:if>-->
<!--						<c:if test="${params.pos_type eq '5' }">超级刷</c:if>-->
					</li>
					<li>
					    <label>进件方式：</label>
						<c:if test="${params.add_type eq '0' }">网站进件</c:if>
						<c:if test="${params.add_type eq '1' }">客户端进件</c:if>
					</li>
					<li>
						<label>密保问题：</label>
						${params.question}
					</li>
					<li>
						<label>密保答案：</label>
						${params.answer}
					</li>
					<li style="width: 300px;">
						<label>是否冻结：</label>
						${params['freeze_status'] ne '1' ? '未冻结' : '已冻结'}
					</li>
					<shiro:hasPermission name="FENGKONG_SHOW_PERMISSION">
					<li style="width: 770px;" class="column2">
						<label>经营地址：</label>
							${params['province']}${params['city']}${params['address']}
					</li> 
					</shiro:hasPermission>
					<li style="width: 770px;">
						<label>主营业务：</label>
						${params.main_business}
					</li>
					<li style="width: 770px;">
						<label>psam卡号：</label>
						${params.terminal_no}
					</li>
	                <li style="width: 770px;">
						<label>激活码：</label>
						${params.keycode}
					</li>
					<li style="width: 770px;">
				      <label>营业执照编号：</label>
					  ${params.bus_license_no}
				   </li>
				</ul>

				<div class="clear"></div>
				<br />
				<div class="title">结算信息</div>
				<ul>
					<li>
						<label>结算周期：</label>
						T+${params['settle_cycle']}天
					</li>
					<li>
						<label>账户类型：</label>
						${params['account_type']}
					</li>
					<li>
						<label>开户名：</label>
						${params['account_name']}
					</li>
					<li style="width: 750px;">
						<label>开户行全称：</label>
						${params['bank_name']}
					</li>
					<li>
						<label>开户账号：</label>
						${params['account_no']}
					</li>
					<li>
						<label>联行行号：</label>
						${params['cnaps_no']}
					</li>
				</ul>
				<div class="clear"></div>
				<br />
				<div class="title">交易规则信息</div>
				<ul>
					<li style="width: 380px">
						<label class="longLabel">单日终端最大交易额：</label>
						<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params.ed_max_amount}" />
						（元）
					</li>
					<li style="width: 380px">
						<label class="longLabel">终端单笔最大交易额：</label>
						<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params.single_max_amount}" />
						（元）
					</li>
					<li style="width: 380px">
						<label class="longLabel">单日单卡最大交易额：</label>
						<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params.ed_card_max_amount}" />
						（元）
					</li>
					<li style="width: 380px">
						<label class="longLabel">单日终端单卡最大交易笔数：</label>
						${params.ed_card_max_items} （笔）
					</li>
					<li style="width: 380px">
						<label class="longLabel">允许交易时间：</label>
						${params['trans_time_start_short']}~ ${params['trans_time_end_short']}
					</li>
					<li style="width: 380px">
						<label class="longLabel">是否优质商户：</label>
						<c:choose>
							<c:when test="${params['my_settle'] eq '0'}">否</c:when>
							<c:when test="${params['my_settle'] eq '1'}">是</c:when>
							<%--<c:when test="${(params.my_settle == '1' || params.pos_type=='3' || params.pos_type=='4' || params.pos_type=='5') && params.agent_no !='3846' || params.agent_no =='4028'}">是</c:when>
						--%></c:choose>
					</li>

					<li style="width: 380px">
						<label class="longLabel">手输卡号：</label>
						<c:choose>
							<c:when test="${params['clear_card_no'] eq '0'}">否</c:when>
							<c:when test="${params['clear_card_no'] eq '1'}">是</c:when>
						</c:choose>
					</li>
					
					<li style="width: 380px">
						<label class="longLabel">可否撤销交易：</label>
						<c:choose>
							<c:when test="${params['trans_cancel'] eq '0'}">否</c:when>
							<c:when test="${params['trans_cancel'] eq '1'}">是</c:when>
						</c:choose>
					</li>
                    <li style="width:780px;line-height: 25px;">
					 	<label style="width: 160px;">支付方式:</label>
						<input type="checkbox" name="pay_method" style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;"  display: disabled="disabled" 
			            <c:if test="${fn:substring(params['pay_method'], 0, 1) eq '1'}">checked="checked"</c:if>
						value="1" readonly="readonly" />Pos支付&nbsp;&nbsp;
			            <input type="checkbox" name="pay_method"  style="width: 30px;vertical-align:middle;padding: 0px;border-width: 0px;"  display: disabled="disabled" 
			            <c:if test="${fn:substring(params['pay_method'], 1, 2) eq '1'}">checked="checked"</c:if>
			            value="1" readonly="readonly"  />快捷支付
					</li>
					<li style="width: 780px;">
						<label class="longLabel">是否钱包结算：</label>
						<c:choose>
							<c:when test="${params['bag_settle'] eq '0'}">否</c:when>
							<c:when test="${params['bag_settle'] eq '1'}">是</c:when>
						</c:choose>
					</li>
				</ul>
				<div class="clear"></div>

				<br />

				<div class="title">备注</div>
				<ul style="height: 70px;">
					<li>
						<textarea readonly="readonly" style="border: 0px; overflow: hidden" cols="98" rows="5">${params['remark']}</textarea>
					</li>
				</ul>
				<div class="clear"></div>


				<div class="title">商户信息审核</div>

				<div class="tbdata">
					<table width="100%" cellspacing="0" class="t2"
						style="line-height: 1.5; font: 12px/1.5 \5FAE\8F6F\96C5\9ED1, Tahoma, Verdana, Arial, Helvetica, sans-serif">
						<thead>
							<tr>
								<th width="35">状态</th>
								<th width="100">意见</th>
								<th width="100">时间</th>
								<th width="100">操作员</th>
							</tr>
							<c:forEach items="${posMerchantShenheList.content}" var="m" varStatus="status">
								<tr id="tr${status.count}" class="${status.count % 2 == 0 ? 'a1' : ''}">
									<td class="center"><span class="center">
											<c:if test="${m.open_status=='1'}">审核成功</c:if>
											<c:if test="${m.open_status=='3'}">审核失败</c:if>
											<c:if test="${m.open_status=='5'}">机具绑定</c:if>
										</span></td>
									<td>${m.examination_opinions}</td>
									<td><fmt:formatDate value="${m.create_time}" type="both" /></td>
									<td>${m.operator}</td>
								</tr>
							</c:forEach>
					</table>
				</div>
				<div class="clear"></div>
				
				<br/>
				<div class="title">收单商户信息</div>

				<div class="tbdata">
					<table width="100%" cellspacing="0" class="t2"
						style="line-height: 1.5; font: 12px/1.5 \5FAE\8F6F\96C5\9ED1, Tahoma, Verdana, Arial, Helvetica, sans-serif">
						<thead>
							<tr>
								<th width="100">编号</th>
								<th width="100">名称</th>
							</tr>
							<c:forEach items="${posAcqMerchantList}" var="acq" varStatus="status">
								<tr>
									<td>${acq.acq_merchant_no}</td>
									<td>${acq.acq_merchant_name}</td>
								</tr>
							</c:forEach>
					</table>
				</div>
				<div class="clear"></div>



				<br />
				<div class="title">附件</div>
				<div class="tip">合法有效的证件，包括身份证、结算账号银行卡、营业执照。</div>
				<div class="picList tip" id="picList">
					<c:forTokens items="${params.attachment}" delims="," var="fileName">

						<div class="tupian">
							<div class="close_btn" style="display: none;"></div>
							<div data-filename="${fileName}" class="tupian_box">

								<c:choose>
									<c:when test="${f:endsWith(fileName,'.zip')}">
										<c:if test="${date3 >= params.create_time}">
											<a href='http://120.132.177.194/uploads/files/merchant/${params.attachment}' title="点击下载" target="_blank">
												<img src="${ctx}/images/z_file_zip.png" />
											</a>
										</c:if>
										<c:if test="${date3 < params.create_time}">
											<a href='${fug:fileUrlGen(fileName)}' title="点击下载" target="_blank">
											<img src="${ctx}/images/z_file_zip.png" />
											</a>
										</c:if>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.rar')}">
										<c:if test="${date3 >= params.create_time}">
											<a href='http://120.132.177.194/uploads/files/merchant/${params.attachment}' title="点击下载" target="_blank">
												<img src="${ctx}/images/z_file_zip.png" />
											</a>
										</c:if>
										<c:if test="${date3 < params.create_time}">
											<a href='${fug:fileUrlGen(fileName)}' title="点击下载" target="_blank">
												<img src="${ctx}/images/z_file_rar.png" />
											</a>
										</c:if>
									</c:when>
									<c:when test="${f:endsWith(fileName,'.jpg') or f:endsWith(fileName,'.png')}">
										<a href='${fug:fileUrlGen(fileName)}' target="_blank" title="点击查看">
											<img src='${fug:fileUrlGen(fileName)}' alt="商户附件" />
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
				<div id="upload_error" class="tip"></div>
				<div class="search_btn clear">
					<input class="button blue" type="button" id="backButton" value="返回" onclick="javascript:window.history.go(-1)" />
				</div>
			</div>
		</form:form>
		<div style="display: none">
			<input type="text" id="flag" value="${flag}" />
			<input type="text" id="errorMessage" value="${errorMessage}" />
		</div>

	</div>
</body>
