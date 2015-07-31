<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<style type="text/css">
#raiseCheckDetailSubmit ul li label.must {display: -moz-inline-box;display: inline-block;width: 5px;text-align: center;color: red;}
#raiseCheckDetailSubmit ul li {margin: 0;padding: 0;display: block;float: left;width: 250px;margin-bottom: 3px;line-height: 32px;}
#raiseCheckDetailSubmit ul li.column2 {width: 500px;}
#raiseCheckDetailSubmit ul li.column3 {width: 750px;}
#raiseCheckDetailSubmit ul li label {display: -moz-inline-box;display: inline-block;width: 90px;}
#raiseCheckDetailSubmit ul li .area {width: 75px;height: 24px;vertical-align: top;}
.tip {border: 1px solid #EFEFEF;padding: 20px;background: #FBF9F9;}
.tupian{height:190px;border:0;margin-bottom:10px;}
.tupian h3{color:#666;font-size:16px;font-weight:normal;}
.tupian_box{height:120px;}
.tupian_box img{margin-top:10px;}
.pic_status0{background:url(${ctx}/images/raise/raise0.png) center no-repeat;}
.pic_status1{background:url(${ctx}/images/raise/raise1.png) center no-repeat;}
.pic_status2{background:url(${ctx}/images/raise/raise2.png) center no-repeat;}
.pic_status3{background:url(${ctx}/images/raise/raise3.png) center no-repeat;}
.tupian .optionBtn1{width:50px;height:18px;outline:0;border:0;color:#fff;margin:3px auto;background:url(${ctx}/images/raise/raise7.png);cursor:pointer;}
.tupian .optionBtn2{width:50px;height:18px;outline:0;border:0;color:#fff;margin:3px auto;background:url(${ctx}/images/raise/raise9.png);cursor:pointer;}
.tupian button.passed{background:url(${ctx}/images/raise/raise8.png);}
.tupian button.unpassed{background:url(${ctx}/images/raise/raise5.png);}
.tupian button.unload1{background:url(${ctx}/images/raise/raise4.png);}
.tupian button.unload2{background:url(${ctx}/images/raise/raise6.png);}
</style>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript">
        
	
	$(function(){
		
		$("#submitSuccess").on('click', function() {
			var ed_max_amount = parseInt($("#ed_max_amount").val()),
				single_max_amount = parseInt($("#single_max_amount").val()),
				ed_card_max_amount = parseInt($("#ed_card_max_amount").val()),
				ed_card_max_items = parseInt($("#ed_card_max_items").val()),
				isSubmit = true;
				
				if (ed_card_max_amount > ed_max_amount) {
					alert("单日终端单卡最大交易额 不能大于 单日终端最大交易额");
					isSubmit = false;
					return false;
				}
				if (single_max_amount > ed_card_max_amount) {
					alert("终端单笔最大交易额 不能大于 单日终端单卡最大交易额");
					isSubmit = false;
					return false;
				}
				if (single_max_amount > ed_max_amount) {
					alert("终端单笔最大交易额 不能大于 单日终端最大交易额");
					isSubmit = false;
					return false;
				}
				
				$('.option .category').each(function(){
					if(!$(this).val()){
						alert("还有未审核的选项！");
						isSubmit = false;
						return false;
					}
				});
				
				if(isSubmit){				
					$("#examinationMark").val("success");
					$("form:first").submit();
				}
		});
	
		//通过之后自动增加的额度数量
		var raiseConfig = {'yyzzpic':50000,
						   'zzjgdmpic':6000,
						   'swdjzpic':6000,
						   'gdzczmpic':6000,
						   'jszpic':6000,
						   'xybgpic':6000,
						   'grbxdpic':6000};
	
		$('.optionBtn1').on('click', function(){
			var that = $(this),
				amount = raiseConfig[that.val()],
				ed_max_amount = $('#ed_max_amount');
			
			if(!that.hasClass('passed')){
				that.addClass('passed')
					.siblings('.optionBtn2')
					.removeClass('unpassed')
					.end()
					.siblings(':hidden')
					.val('0');
				
				ed_max_amount.val(Number(ed_max_amount.val()) + Number(amount));
			}
		});
		
		$('.optionBtn2').on('click', function(){
			var that = $(this),
				amount = raiseConfig[that.val()],
				ed_max_amount = $('#ed_max_amount');
			
			if(!that.hasClass('unpassed')){
				
				if(that.siblings('.optionBtn1').hasClass('passed')){
					ed_max_amount.val(Number(ed_max_amount.val()) - Number(amount));
				}
				
				that.addClass('unpassed')
					.siblings('.optionBtn1')
					.removeClass('passed')
					.end()
					.siblings(':hidden')
					.val('1');
				
				
			}
		});
	});
</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：商户管理 &gt;商户提额审核
		</div>
		<form id="raiseCheckDetailSubmit" name="raiseCheckDetailSubmit"   action="${ctx}/mer/raiseCheckDetailSubmit" method="post">
			<div id="search" class="item" style="border: none;">
				<input type="hidden" value="${params['id']}" name="id" />
				<input type="hidden" value="${params['mrid']}" name="mrid" />
				<input type="hidden" value="${params['batch_no']}" name="batch_no" />
				<input type="hidden" name="terminal_no" id="terminal_no" value="${params.terminal_no}" />
				<input type="hidden" name="agent_no" id="agent_no" value="${params.agent_no}" />
				<input type="hidden" name="merchant_no" id="merchant_no" value="${params.merchant_no}" />
				
				<div class="title">商户信息 ${params.merchant_no}</div>
				<c:set var="date1">
					<fmt:formatDate value="${params.create_time}" pattern="yyyy-MM-dd" type="date" />
				</c:set>
				<c:set var="date2">2014-01-14</c:set>
				<ul>
					<li>
						<label>代理商名称：</label>
						${params['agent_name']}
					</li>
					
					<li>
						<label>商户名称：</label>
						${params['merchant_name']}
					</li>
					<li>
						<label>商户类型：</label>
						<c:if test="${date2 >= date1}">
							<c:if test="${params.merchant_type eq '5812' }">
								宾馆、餐饮、娱乐
							</c:if>
							<c:if test="${params.merchant_type eq '1520' }">
								批发、房产、汽车
							</c:if>
							<c:if test="${params.merchant_type eq '5300' }">
								航空、加油、超市
							</c:if>
							<c:if test="${params.merchant_type eq '5111' }">
								医院、学校、政府
							</c:if>
							<c:if test="${params.merchant_type eq '6300' }">
								保险、公共事业
							</c:if>
							<c:if test="${params.merchant_type eq '5541' }">
								民生类
							</c:if>
							<c:if test="${params.merchant_type eq '5331' }">
								一般类
							</c:if>
							<c:if test="${params.merchant_type eq '1011' }">
								其他
							</c:if>
						</c:if>
						<c:if test="${date2 < date1}">
							<c:if test="${params.merchant_type eq '5812' }">
								餐娱类
							</c:if>
							<c:if test="${params.merchant_type eq '5111' }">
								批发类
							</c:if>
							<c:if test="${params.merchant_type eq '5541' }">
								民生类
							</c:if>
							<c:if test="${params.merchant_type eq '5331' }">
								一般类
							</c:if>
							<c:if test="${params.merchant_type eq '1520' }">
								房车类
							</c:if>
							<c:if test="${params.merchant_type eq '1011' }">
								其他
							</c:if>
						</c:if>
					</li>
					<li class="column2">
						<label>主营业务：</label>
						${params['main_business']}
					</li>
					<li>
						<label>企业法人：</label>
						${params.lawyer}
					</li>
					<li class="column2">
						<label>经营地址：</label>
						${params.province} ${params.city} ${params['address']}
					</li>
					<li>
						<label>联系人：</label>
						${params['link_name']}
					</li>
					<li>
						<label>联系电话：</label>
						${params.phone}
					</li>
					<li>
						<label>Email：</label>
						${params.email}
					</li>
					<li>
						<label>登录手机号：</label>
						${params['mobile_username']}
					</li>
					<li>
						<label>移付宝销售：</label>
						${params['sale_name']}
					</li>
					<li>
						<label>是否实名：</label>
						<c:if test="${params.real_flag == '0' }">否</c:if>
						<c:if test="${params.real_flag == '1' }">是</c:if>
					</li>
					<li>
						<label>密码暗语：</label>
						${params['code_word']}
					</li>
					<li class="column2">
						<label>法人身份证号：</label>
						${params['id_card_no']}
					</li>
					<li class="">
						<label>商户机具数量：</label>
						${params['terminal_count']}
					</li>
					<li>
						<label>所属代理商：</label>
						<u:AgentNameForNo agentNo="${params['belong_to_agent']}"/>
					</li>
					<li>
						<label>设备类型：</label>
						<c:if test="${params['pos_type'] eq '1'}">移联商宝</c:if>
						<c:if test="${params['pos_type'] eq '2'}">传统POS</c:if>
						<c:if test="${params['pos_type'] eq '3'}">移小宝</c:if>
						<c:if test="${params['pos_type'] eq '4'}">移联商通</c:if>
						<c:if test="${params['pos_type'] eq '5'}">超级刷</c:if>
					</li>
					<li>
						<label>进件方式：</label>
						<c:if test="${params['add_type'] eq '0'}">网站进件</c:if>
						<c:if test="${params['add_type'] eq '1'}">客户端进件</c:if>
					</li>
				</ul>

				<ul id="s">
					<li class="column3">
						<label>SN或PSAM号：</label>
						${params.terminal_no}
					</li>
				</ul>
			<div>
			    <ul>
					<c:if test="${params['pos_type'] == '4'}">
					           <li class="column3">
						              <label>激活码：</label>
						           	  ${params['keycode']}
						       </li>
					</c:if>
				</ul>
				<ul>
				   <li class="column2">
				      <label>所属销售：</label>
					  ${params['self_sale_name']}
				   </li>
				   <li class="column2">
				      <label>营业执照编号：</label>
					  ${params['bus_license_no']}
				   </li>
				</ul>
			</div>
				<div class="clear"></div>
				<br />
				<div class="title">商户手续费信息</div>
				<ul>
					<li>
						<label>结算周期：</label>
						T+<c:if test="${params['settle_cycle'] eq '1'}">1</c:if>
						<c:if test="${params['settle_cycle'] eq '0'}">0</c:if>
						&nbsp;天
					</li>
					<li>
						<label>账户类型：</label>
						<c:if test="${params.account_type == '对公'}">
							对公
						</c:if>
						<c:if test="${params.account_type == '对私'}">
							对私
						</c:if>
					</li>
					<li>
						<label>开户名：</label>
						${params.account_name}
					</li>
					<li>
						<label>开户账号：</label>
						${params.account_no}
					</li>
					<li>
						<label>开户行全称：</label>
						${params.bank_name}
					</li>
					<li>
						<label>联行行号：</label>
						${params.cnaps_no}
					</li>
					
					
					
					<li class="column3" >
						<label>扣率类型：</label>
						<c:if test="${params['fee_type'] eq 'RATIO'}">扣率</c:if>
						<c:if test="${params['fee_type'] eq 'CAPPING'}">封顶</c:if>
						<c:if test="${params['fee_type'] eq 'LADDER'}">阶梯</c:if>
					</li>
					<div class="clear"></div>
					<c:if test="${params['fee_type'] eq 'RATIO'}">
					<li id="fee_rate" style="">
						<label>扣率：</label>
						${params['fee_rate']}
						%
					</li>
					</c:if>
					<c:if test="${params['fee_type'] eq 'CAPPING'}">
					<li id="fee_cap_amount" style="">
						<label>封顶金额：</label>
						${params['fee_cap_amount']}
						（元）
					</li>
					<li id="fee_max_amount" style="">
						<label>封顶手续费：</label>
						${params['fee_max_amount']}
						（元）
					</li>
					</c:if>
					<c:if test="${params['fee_type'] eq 'LADDER'}">
					<li id="fee_ladder" class="column3">
						<label>阶梯设置：</label>
						${params['ladder_min']}
						% &lt;
						${params['ladder_value']}
						（元） &lt;
						${params['ladder_max']}
						%
					</li>
					</c:if>
				</ul>
				
				<div class="clear"></div>
				<br />
				<div class="title">商户交易规则信息</div>
				<div class="tip" style="padding: 10px;">交易额或交易笔数为0时是无限制</div>
				<div id="ylst" style="height:30px;line-height:32px;margin-bottom:3px;<c:if test="${params.pos_type != '4'}">display:none;</c:if>">
					<input style="width: 30px;vertical-align:middle;" type="checkbox" name="model1" value="1" id="model1" onclick="javascript:window.model1()">经营场所证明
					<input style="width: 30px;vertical-align:middle;" type="checkbox" name="model2" value="21" id="model21" onclick="javascript:window.model21()" >个体营业执照
					<input style="width: 30px;vertical-align:middle;" type="checkbox" name="model2" value="22" id="model22" onclick="javascript:window.model22()" >企业营业执照
					<input style="width: 30px;vertical-align:middle;" type="checkbox" name="model3" value="3" id="model3" onclick="javascript:window.model3()">店面、店内、收银台照
				</div>
				<ul>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日终端最大交易额：</label>
				    	<input style="width: 70px" type="text" id="ed_max_amount" name="ed_max_amount" class="input required money"
							value="${params.ed_max_amount}" readonly="readonly"/>
						(元)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">终端单笔最大交易额：</label>
				    	<input style="width: 70px" type="text" id="single_max_amount" name="single_max_amount" class="input required money"
							value="${params.single_max_amount}"  readonly="readonly"/>
						(元)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日单卡最大交易额：</label>
				    	<input style="width: 70px" type="text" id="ed_card_max_amount" name="ed_card_max_amount" class="input required money"
							value="${params.ed_card_max_amount}"  readonly="readonly"/>
						(元)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">单日终端单卡最大交易笔数：</label>
				    	<input style="width: 70px" type="text" id="ed_card_max_items" name="ed_card_max_items" class="input required integer"
							value="${params.ed_card_max_items}"  readonly="readonly"/>
						(笔)
						<label class="must">*</label>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">允许交易时间：</label>
						<c:choose>
						    <c:when test="${params.pos_type=='3'}">
						        06:00:00
						        ~
								22:59:59					    
						    </c:when>
						    <c:otherwise>
							    00:00:00
							    ~
							    23:59:59
						    </c:otherwise>
						</c:choose>
					</li>	
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">是否优质商户： </label>
							<c:if test="${params.my_settle == '0' || params.agent_no =='3846'}">否</c:if>
							<c:if test="${(params.my_settle == '1' || params.pos_type=='3' || params.pos_type=='4' || params.pos_type=='5') && params.agent_no !='3846' || params.agent_no =='4028'}">是</c:if>
					</li>
					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">手输卡号：</label>
						<c:if test="${params.clear_card_no == '0' }">否</c:if>
						<c:if test="${params.clear_card_no == '1' }">是</c:if>
					</li>

					<li style="width: 390px">
						<label style="width: 160px" class="longLabel">可否撤销交易：</label>
						<c:if test="${params.trans_cancel == '0' }">否</c:if>
						<c:if test="${params.trans_cancel == '1' }">是</c:if>
					</li>
					<li style="width:390px;line-height: 25px;">
						<input type="hidden" value="${params['pay_method']}" id="final_pay_method" name="final_pay_method"/>
					 	<label style="width: 160px;">支付方式:</label>
			            <c:if test="${fn:substring(params['pay_method'], 0, 1) eq '1'}">√ Pos支付&nbsp;&nbsp;&nbsp;</c:if>
			            <c:if test="${fn:substring(params['pay_method'], 1, 2) eq '1'}">√ 快捷支付</c:if>
					</li>
					<li style="width: 390px" id="bagSettle">
				      <label style="width: 160px" class="longLabel">是否钱包结算：</label>
					  <c:if test="${params.bag_settle == '1' }">是</c:if>
					  <c:if test="${params.bag_settle == '0' }">否</c:if>
				   </li>
				</ul>
				<div class="clear"></div>

				<div class="title">备注</div>
				<ul style="height: 70px;">
					<li>
						${params['remark']}
					</li>
				</ul>
				<div class="clear"></div>
				<div class="title">代理商审核意见</div>
				<ul style="height: 70px;">
					<li>
						<textarea name="agent_opinion" id="agent_opinion" cols="50" rows="4" readonly="readonly">${params['agent_opinion']}</textarea>
					</li>
				</ul>
				
				<div class="clear"></div>

				<div class="title">额度提升审核意见</div>
				<ul style="height: 70px;">
					<li>
						<textarea name="check_opinion" id="check_opinion" cols="50" rows="4">${params['check_opinion']}</textarea>
					</li>
				</ul>
				
				<div class="clear"></div>
				<div class="title">历史审核意见</div>
				
				<div class="tbdata" style="margin-bottom:20px;">
					<table width="100%" cellspacing="0" class="t2" style="line-height: 1.5; font: 12px/1.5 \5FAE\8F6F\96C5\9ED1, Tahoma, Verdana, Arial, Helvetica, sans-serif">
						<thead>
							<tr>
								<th width="10">批次</th>
								<th width="45">代理商审核意见</th>
								<th width="45">额度提升审核意见</th>
							</tr>
							<c:forEach items="${opinions}" var="opinion" varStatus="status">
								<c:if test="${opinion.batch_no ne params['batch_no']}">
									<tr id="tr${status.count}" class="${status.count % 2 == 0 ? 'a1' : ''}">
										<td class="center">${opinion.batch_no}</td>
										<td>${opinion.agent_opinion}</td>
										<td>${opinion.check_opinion}</td>
									</tr>
								</c:if>
							</c:forEach>
					</table>
				</div>
								
				<div class="clear"></div>
				<div class="title">附件下载：<label  style="color:red;">图片文件可点击查看，压缩文件点击下载</label ></div>
				
				<c:forEach items="${categories}" var="category" varStatus="status">
					<div class="tupian">
						<h3>
							<c:choose>
								<c:when test="${category eq 'yyzzpic'}">营业执照</c:when>
								<c:when test="${category eq 'zzjgdmpic'}">组织结构代码</c:when>
								<c:when test="${category eq 'swdjzpic'}">税务登记证</c:when>
								<c:when test="${category eq 'gdzczmpic'}">固定资产证明</c:when>
								<c:when test="${category eq 'jszpic'}">驾驶证</c:when>
								<c:when test="${category eq 'xybgpic'}">央行信用报告</c:when>
								<c:when test="${category eq 'grbxdpic'}">个人保险单</c:when>
							</c:choose>
						</h3>
						
						<c:set var="existFlag" value="0" />						
						<c:forEach items="${raiseDetails}" var="raiseDetail" varStatus="status1">
							<c:if test="${category eq raiseDetail.raise_key}">
								<c:set var="existFlag" value="1" />
								<div data-filename="${raiseDetail.raise_content}" class="tupian_box 
									<c:choose>
										<c:when test="${raiseDetail.raise_status eq '3'}">pic_status3</c:when>
										<c:when test="${raiseDetail.raise_status eq '2'}">pic_status2</c:when>
										<c:when test="${raiseDetail.raise_status eq '1'}">pic_status1</c:when>
										<c:when test="${raiseDetail.raise_status eq '0'}">pic_status0</c:when>
									</c:choose>">
									<a href='${fug:fileUrlGen(raiseDetail.raise_content)}' title="点击下载" target="_blank">
										<img src='${fug:fileUrlGen(raiseDetail.raise_content)}' />
									</a>
								</div>
								<div class="option">
									<button class="optionBtn1 <c:if test="${raiseDetail.raise_status eq '0'}">passed</c:if>" type="button" <c:if test="${raiseDetail.raise_status ne '2'}">disabled="disabled"</c:if> <c:if test="${raiseDetail.raise_status eq '2'}">value="${raiseDetail.raise_key}"</c:if>>&nbsp;</button>
									<br/>
									<button class="optionBtn2 <c:if test="${raiseDetail.raise_status eq '1'}">unpassed</c:if>" type="button" <c:if test="${raiseDetail.raise_status ne '2'}">disabled="disabled"</c:if> <c:if test="${raiseDetail.raise_status eq '2'}">value="${raiseDetail.raise_key}"</c:if>>&nbsp;</button>
									<c:if test="${raiseDetail.raise_status eq '2'}">
										<input type="hidden" name="${raiseDetail.raise_key}" value="" class="category"/>
									</c:if>
								</div>
							</c:if>
						</c:forEach>
						<c:if test="${existFlag eq '0'}">
							<div class="tupian_box pic_status3"></div>
							<div class="option">
								<button class="optionBtn1 unload1" type="button" disabled="disabled">&nbsp;</button>
								<br/>
								<button class="optionBtn2 unload2" type="button" disabled="disabled">&nbsp;</button>
							</div>
						</c:if>
					</div>
				</c:forEach>

				<div class="clear_fix"></div>
				<input type="hidden" id="examinationMark" name="examinationMark" value="" />
			</div>
			
			<div class="clear"></div>
			 
			<div class="search_btn">
				<input class="button blue medium" type="button" id="submitSuccess"   name="submitSuccess"   value="确认审核" />
				<input name="reset" class="button blue medium" type="button" onclick="javascript:history.go(-1);" value="返回" />
			</div>
		</form>
       <div style="display: none">
			<input type="text" id="flag" value="${flag}" />
			<input type="text" id="errorMessage" value="${errorMessage}" />
	   </div>
	</div>
</body>
