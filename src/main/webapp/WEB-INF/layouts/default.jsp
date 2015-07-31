<%@ page contentType="text/html;charset=UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="sitemesh"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@include file="/tag.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>前海移联 - 业务运营系统 - <sitemesh:title /></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<link type="image/x-icon" href="${ ctx}/images/small_logo.ico" rel="icon"/>
<link type="image/x-icon" href="${ ctx}/images/small_logo.ico" rel="shortcut icon"/>
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/blue.css" />
<link rel="stylesheet" type="text/css" href="${ ctx}/thems/button.css" />
<script type="text/javascript" src="${ ctx}/scripts/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jquery.md5.js"></script>
<script language="javascript" type="text/javascript"
	src="${ ctx}/scripts/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript"
	src="${ ctx}/scripts/lhgdialog/lhgcore.lhgdialog.min.js"></script>
<script type="text/javascript">
	$(function() {
		$(".leftTitle").click(function() {
			$(this).next("ul").toggle();
		});
		var tipTimeOut=undefined;
		$.ajaxSetup({
			beforeSend:function(){
				tipTimeOut=setTimeout(function(){
					$('<div class="ajaxloading"></div>').appendTo('body');
				},1000);
			},
			complete:function(){
				clearTimeout(tipTimeOut);
				$('.ajaxloading').remove();
			},
			ajaxError:function(){
				alert("操作失败！");
				$('.ajaxloading').remove();
			}
		});
	});
</script>
<style type="text/css">

</style>
<sitemesh:head />
</head>
<body>
	<div id="container">
		<div id="main">
			<%@ include file="header.jsp"%>
			<div id="left_menu">
				<shiro:hasPermission name="COMMERCIAL_MANAGER">
					<div class="title leftTitle" style="border-top:none">商户管理</div>
				</shiro:hasPermission>
				<ul>
					<%--<shiro:hasPermission name="COMMERCIAL_CHECK">
						<li><a href="${ctx}/mer/checkQuery">超级刷商户审核</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="COMMERCIAL_CHECK">
						<li><a href="${ctx}/mer/checkQuery">超级刷商户查询</a></li>
					</shiro:hasPermission>
					--%>
					<shiro:hasPermission name="COMMERCIAL_CHECK">
						<li><a href="${ctx}/mer/checkQuery">商户审核</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="COMMERCIAL_REPEAT_CHECK">
						<li><a href="${ctx}/mer/checkRepeatQuery">商户复审</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="COMMERCIAL_QUERY">
						<li><a href="${ctx}/mer/merQuery">商户查询</a></li>
					</shiro:hasPermission>
					<%--<shiro:hasPermission name="COMMERCIAL_QUERY_NEW">
						<li><a href="${ctx}/mer/merQueryN">商户检索</a></li>
					</shiro:hasPermission>
					--%><shiro:hasPermission name="COMMERCIAL_AUDITING">
						<li><a href="${ctx}/mer/merAuditingQuery">商户审核统计</a></li>
					</shiro:hasPermission>
     				<%--<shiro:hasPermission name="COMMERCIAL_TRANS_NEW">
						<li><a href="${ctx}/mer/transN">交易查询</a></li>
					</shiro:hasPermission>
					--%><shiro:hasPermission name="TRANS_HISTORY_QUERY">
						<li><a href="${ctx}/history/transInfoHistory">历史交易查询</a></li>
						</shiro:hasPermission>
						<shiro:hasPermission name="COMMERCIAL_TRANS">
						<li><a href="${ctx}/mer/trans">交易查询(POS)</a></li>
						<li><a href="${ctx}/mer/transfast">交易查询(快捷)</a></li>
						<li><a href="${ctx}/mer/smBoxTrans">交易查询(移小宝)</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="TRANSFER_ACCOUNTS">
						<li><a href="${ctx}/mer/transferAccountsQuery">转账查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="COMMERCIAL_MOBILETRANS">
						<li><a href="${ctx}/mer/mobiletrans">手机充值</a></li>
						<li><a href="${ctx}/mer/mobileflowtrans">手机流量充值</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="CREDIT_QUERY">
						<li><a href="${ctx}/mer/creditTrans">信用卡还款</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="WZFK_QUERY">
						<li><a href="${ctx}/mer/wzfktrans">车辆代缴费</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="TERMINAL_QUERY">
						<li><a href="${ctx}/ter/terQuery">机具分配</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="ACQMERCHANT_TRANS_COUNT">
						<li><a href="${ctx}/acqTrans/transCount">收单商户交易统计</a></li>
					</shiro:hasPermission>
					
					<shiro:hasPermission name="LONGBAO_QUERY">
					<li><a href="${ctx}/longBao/longBaoQuery">龙宝查询</a></li>
					</shiro:hasPermission>
					
					<shiro:hasPermission name="ACTIVATION_MANAGE">
					<li><a href="${ctx}/activation/activationQuery">激活码管理</a></li>
					</shiro:hasPermission>
					
									
					<!-- <shiro:hasPermission name="DATA_STATISTICS"> -->
					<li><a href="${ctx}/sta/bar">数据统计</a></li>
					<!--</shiro:hasPermission> -->

					<shiro:hasPermission name="KSJY_UPDATE">
					<li><a href="${ctx}/mer/merchantHandlingCharge">亏损交易</a></li>
					</shiro:hasPermission>
					
					<shiro:hasPermission name="ONTRANS_QUERY">
					<li><a href="${ctx}/mer/notransQuery">未交易商户查询</a></li>
					</shiro:hasPermission>
					
					<shiro:hasPermission name="COMMERCIAL_LIFT_QUERY">
						<li><a href="${ctx}/mer/liftQuery">额度提升审核</a></li>
					</shiro:hasPermission>
					
					<shiro:hasPermission name="COMMERCIAL_LIMITCHANGE_QUERY">
						<li><a href="${ctx}/mer/limitChangeQuery">额度修改查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="COMMERCIAL_RAISE_QUERY">
						<li><a href="${ctx}/mer/raiseCheckQuery">商户提额审核</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SPECIAL_MERCHANT">
						<li><a href="${ctx}/sc/batchMerchant">商户批量操作</a></li>
					</shiro:hasPermission>
				</ul>
				
				<shiro:hasPermission name="INCREMENT_MANAGER">
					<div class="title leftTitle" style="border-top:none">增值服务</div>
				</shiro:hasPermission>
				<ul>
					<shiro:hasPermission name="INCREMENT_PUBPAY">
					<li><a href="${ctx}/increment/pubPayList">公共缴费</a></li>
					</shiro:hasPermission>
				</ul>
				
				<shiro:hasPermission name="AGENT_MANAGER">
					<div class="title leftTitle" style="margin-top:20px">代理商管理</div>
				</shiro:hasPermission>
				<ul>
					<shiro:hasPermission name="AGENT_QUERY">
						<li><a href="${ctx}/agent/agentQuery">代理商查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="AGENT_ADD">
						<li><a href="${ctx}/agent/agentInput">代理商新增</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="AGENT_RULE_CONTROL">
						<li><a href="${ctx}/agent/transRuleList">代理商交易额度控制</a></li>
					</shiro:hasPermission>
				</ul>
				
				<shiro:hasPermission name="AGENT_MANAGER_SPOS">
					<div class="title leftTitle" style="margin-top:20px">超代管理</div>
				</shiro:hasPermission>
				<ul>
					<shiro:hasPermission name="AGENT_QUERY_SPOS">
						<li><a href="${ctx}/agent/agentQuerySPOS">超代查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="AGENT_ADD_SPOS">
						<li><a href="${ctx}/agent/agentInputSPOS">超代新增</a></li>
					</shiro:hasPermission>
				</ul>
				
				<shiro:hasPermission name="ORDER_ORGANIZATION">
					<div class="title leftTitle" style="margin-top:20px">收单机构管理</div>
				</shiro:hasPermission>
				<ul>
    
         <shiro:hasPermission name="GROUP_QUERY">
            <li><a href="${ctx}/group/query">路由集群管理</a></li>
         </shiro:hasPermission>
     
      <shiro:hasPermission name="GROUP_MERCHANT_QUERY">
            <li><a href="${ctx}/group/groupMerchantQuery">集群中普通商户</a></li>
         </shiro:hasPermission>
         
	      <shiro:hasPermission name="GROUP_MERCHANT_DISTRACT">
	            <li><a href="${ctx}/group/groupMerchantDistract">集群普通商户(转)</a></li>
	         <%--    <li><a href="${ctx}/group/groupDistractQuery">集群普通商户(转)</a></li> --%>
	      </shiro:hasPermission>
       
             <shiro:hasPermission name="GROUP_ACQMERCHANT_QUERY">
            <li><a href="${ctx}/group/groupAcqMerchantQuery">集群中收单商户</a></li>
         </shiro:hasPermission><%--
         <shiro:hasPermission name="GROUP_QUERY">
            <li><a href="${ctx}/group/queryGroupRule">集群规则管理</a></li>
         </shiro:hasPermission>
           
					--%><shiro:hasPermission name="SYSTEM_MERCHANTQUERY">
						<li><a href="${ctx}/acq/merchantQuery">收单机构商户</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SYSTEM_TERMINALQUERY">
						<li><a href="${ctx}/acq/terminalQuery">收单机构终端</a></li>
					</shiro:hasPermission>
				</ul>
				
				<shiro:hasPermission name="SYSTEM_MANAGER">
					<div class="title leftTitle" style="margin-top:20px">系统管理</div>
				</shiro:hasPermission>
				<ul>
					<shiro:hasPermission name="SYSTEM_USERQUERY">
						<li><a href="${ctx}/acq/sysUserQuery">用户管理</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SYSTEM_USERGROUPQUERY">
						<li><a href="${ctx}/acq/userGroupQuery">用户组管理</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SYSTEM_USERAUTH">
						<li><a href="${ctx}/auth/userAuthQuery">权限管理</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="VERSION_MANAGER">
						<li><a href="${ctx}/ver/verQuery">版本管理</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="MERCHANT_API_MANAGER">
					<li><a href="${ctx}/merchantApi/merchantApiQuery">商户接口管理</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="MERCHANT_API_MANAGER">
					<li><a href="${ctx}/merchantSdkApi/merchantSdkApiQuery">商户sdk接口管理</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="MOBILE_CLIENT_MESSAGE">
					<li><a href="${ctx}/clientMessage/clientMessageQuery">手机客户端消息</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="AGENT_NOTICE_SEND">
					<li><a href="${ctx}/agentNoticeSend/agentNoticeQuery">代理商通告下发</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SMS_SEND">
					<li><a href="${ctx}/clientMessage/toMsgInputAdd">批量发送短信</a></li>
					</shiro:hasPermission>
				    
				</ul>
				<shiro:hasPermission name="LOG_MANAGER">
					<div class="title leftTitle" style="margin-top:20px">日志管理</div>
				</shiro:hasPermission>
				<ul>
					<shiro:hasPermission name="LOG_MANAGER_RECORD">
						<li><a href="${ctx}/log/logQuery">日志记录</a></li>
					</shiro:hasPermission>

					<shiro:hasPermission name="MOB_LOG_MANAGER_RECORD">
						<li><a href="${ctx}/log/mobLogQuery">手机日志</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="ACQ_LOG_MANAGER_RECORD">
						<li><a href="${ctx}/log/acqLogQuery">收单日志</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="DEV_LOG_MANAGER_RECORD">
						<li><a href="${ctx}/log/rquestDevQuery">手机访问记录</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="OPERATE_LOG_RECORD">
					<li><a href="${ctx}/log/operateLogQuery">操作用户记录</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="MERCHANT_LOG_RECORD">
					<li><a href="${ctx}/log/merLogQuery">商户操作记录</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="AGENT_LOG_RECORD">
					<li><a href="${ctx}/log/agentLogQuery">代理商日志</a></li>
					</shiro:hasPermission>
				</ul>

				<shiro:hasPermission name="SALE_MANAGER">
					<div class="title leftTitle" style="margin-top:20px">销售管理</div>
				</shiro:hasPermission>
				<ul>
					<shiro:hasPermission name="A_SALE_MANAGER">
						<li><a href="${ctx}/sale/trans">交易查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SALE_MANAGER_TRANS_FAST">
						<li><a href="${ctx}/sale/transFast">快捷交易查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="B_SALE_MANAGER">
						<li><a href="${ctx}/sale/merQuery">商户查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="C_SALE_MANAGER">
						<li><a href="${ctx}/sale/agentQuery">代理商查询</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="SALE_ACQMERCHANT">
						<li><a href="${ctx}/sale/acqMerchantQuery">收单机构商户</a></li>
					</shiro:hasPermission>
				</ul>
    
    <shiro:hasPermission name="RISE_MANAGER">
     <div class="title leftTitle" style="margin-top:20px">风险管理</div>
    </shiro:hasPermission>

    <ul>
       <%-- <li><a href="${ctx}/risk/variableInput">变量设置</a></li>
       <li><a href="${ctx}/risk/paramQuery">参数字段设置</a></li>
       <li><a href="${ctx}/risk/transRuleQuery">交易规则查询</a></li>
      <li><a href="${ctx}/risk/ruleGroupInput">规则组新增</a></li>
      <li><a href="${ctx}/risk/subRuleInput">子规则新增</a></li>  --%>
      <shiro:hasPermission name="TRANS_BLACK">
			<li><a href="${ctx}/black/blackQuery">黑名单设置</a></li>
	  </shiro:hasPermission>
	  <shiro:hasPermission name="TRANS_BLACKFILTER">
			<li><a href="${ctx}/black/blackFilterQuery">黑名单过滤</a></li>
	  </shiro:hasPermission>
	  <shiro:hasPermission name="TRANS_EXCESS">
			<li><a href="${ctx}/black/excessTrans">超额交易过滤</a></li>
	  </shiro:hasPermission>
      <shiro:hasPermission name="RISE_QUERY">
      	<li><a href="${ctx}/risk/riskQuery">商户风险管理</a></li>
      </shiro:hasPermission>
    </ul>
    
    <%-- 
     <shiro:hasPermission name="RISK_MANAGER">
     <div class="title leftTitle" style="margin-top:20px">风险管理</div>
    </shiro:hasPermission>
    <ul>
     <shiro:hasPermission name="RISK_VAR_SET">
      <li><a href="${ctx}/log/logQuery">变量设置</a></li>
     </shiro:hasPermission>

     <shiro:hasPermission name="RISE_CONDITON_SET">
      <li><a href="${ctx}/log/mobLogQuery">参数字段设置</a></li>
     </shiro:hasPermission>
     <shiro:hasPermission name="RULE_QUERY">
      <li><a href="${ctx}/log/acqLogQuery">规则查询</a></li>
     </shiro:hasPermission>
     <shiro:hasPermission name="RULE_ADD">
      <li><a href="${ctx}/log/rquestDevQuery">规则新增</a></li>
     </shiro:hasPermission>
      <shiro:hasPermission name="RISE_QUERY">
      <li><a href="${ctx}/log/rquestDevQuery">风险查询</a></li>
     </shiro:hasPermission>
    </ul>
    --%>
    <shiro:hasPermission name="PHONE_PURSE">
     <div class="title leftTitle" style="margin-top:20px">手机钱包管理</div>
    </shiro:hasPermission>
    <ul>
	    <shiro:hasPermission name="BAG_LOGIN_QUERY">
			<li><a href="${ctx}/purse/bagLoginQuery">钱包用户管理</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="BAG_EXTRACTION_CHANNEL">
			<li><a href="${ctx}/purse/bagExtractionChannel">钱包提现管理</a></li>
	   </shiro:hasPermission>
	    <shiro:hasPermission name="BAG_EXTRACTION_QUERY">
			<li><a href="${ctx}/purse/bagExtractionQuery">钱包提现查询</a></li>
	   </shiro:hasPermission>
	    <shiro:hasPermission name="BAG_TRANS_QUERY">
			<li><a href="${ctx}/purse/bagTransQuery">钱包入账查询</a></li>
	   </shiro:hasPermission>
	    <shiro:hasPermission name="BAG_REVERSAL_QUERY">
			<li><a href="${ctx}/purse/bagReversalQuery">钱包冲正查询</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="BAG_TRANSACTION_QUERY">
			<li><a href="${ctx}/purse/bagTransactionQuery">钱包交易查询</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="BAG_MERCHANT_MANAGER">
			<li><a href="${ctx}/purse/bagMerchantQuery">钱包商户管理</a></li>
	   </shiro:hasPermission>
	  <shiro:hasPermission name="BAG_CHECK_MANAGER">
	   <li><a href="${ctx}/purse/bagCheckQuery">钱包调账管理</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="BAG_TZERO_CHECK">
	   <li><a href="${ctx}/purse/bagTzeroAmountLimitQuery">提升额度审核</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="BAG_TZERO_AMOUNT_MODIFY">
	   <li><a href="${ctx}/purse/bagTzeroAmountModifyQuery">T+0额度修改</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="RED_BAG_QUERY">
	   <li><a href="${ctx}/purse/redBagQuery">移联红包管理</a></li>
	   </shiro:hasPermission>
	   <shiro:hasPermission name="RED_BAG_QUERY">
	   <li><a href="${ctx}/purse/redBuyMachines">红包换购管理</a></li>
	   </shiro:hasPermission>
    </ul>
    
    
    <shiro:hasPermission name="MERCHANT_CHECK_MANAGE">
     <div class="title leftTitle" style="margin-top:20px">审核人管理</div>
    </shiro:hasPermission>
    <ul>
    	<shiro:hasPermission name="MERCHANT_CHECK_MANAGE_PERSON">
			<li><a href="${ctx}/merCP/queryMerchantCheckInfo">审核人管理</a></li>
	   </shiro:hasPermission>
    </ul>
			</div>
			<sitemesh:body />
		</div>
	</div>
	<div class="clear"></div>
	<div id="copyright" style=" margin-bottom:80px">前海移联科技版权所有
		2010粤ICP备09161251号 客户服务热线： 400-600-2999(5*8小时)</div>
</body>
</html>