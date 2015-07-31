<%@page pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>

<head>
	<style type="text/css">
        .biaoge2 { width:100%; border:1px solid #c0de98; }
		.biaoge2 th { height:24x; line-height:24px; border:1px solid #C5D5C5; padding:0px 2px; color:#000; font-weight:normal;}
		.biaoge2 td { height:20x; line-height:20px; padding:2px 2px; border:1px solid #C5D5C5; text-align:left; background:#F4F8F4; }
	</style>
</head>
<body>
     <div style="clear:both;margin:5px;">
        <input type="button" value="返回" onclick="javascript:history.back(-1);"/>
     </div>
  <table  border="0" cellspacing="0" cellpadding="0">
  <tr> 
	<c:forEach items="${list}" var="item" varStatus="status">
    <td>
    <table  border="0" cellspacing="0" cellpadding="0" class="biaoge2" style="width:600px;">
      <tr>
       <th scope="row" width="30%">商户全称</th>
       <td> ${item.merchant_name} </td>
     </tr>
     <tr>
       <th scope="row" >商户简称</th>
       <td> ${item.merchant_short_name} </td>
     </tr>
     <tr>
       <th scope="row" >商户类型</th>
       <c:set var="date1">${item.create_time}</c:set>
	 <c:set var="date2">2014-01-14</c:set>
       <td> 
       <c:if test="${date2 >= date1}">
	      	<c:if test="${item.merchant_type eq '5812' }">宾馆、餐饮、娱乐</c:if>
			<c:if test="${item.merchant_type eq '1520' }">批发、房产、汽车</c:if>
			<c:if test="${item.merchant_type eq '5300' }">航空、加油、超市</c:if>
			<c:if test="${item.merchant_type eq '5111' }">医院、学校、政府</c:if>
			<c:if test="${item.merchant_type eq '6300' }">保险、公共事业</c:if>
			<c:if test="${item.merchant_type eq '1011' }">其他</c:if>
		</c:if>
		<c:if test="${date2 < date1}">
			<c:if test="${item.merchant_type eq '5812' }">餐娱类</c:if>
			<c:if test="${item.merchant_type eq '5111' }">批发类</c:if>
			<c:if test="${item.merchant_type eq '5541' }">民生类</c:if>
			<c:if test="${item.merchant_type eq '5331' }">一般类</c:if>
			<c:if test="${item.merchant_type eq '1520' }">房车类</c:if>
			<c:if test="${item.merchant_type eq '1011' }">其他</c:if>
		</c:if>
		</td>
     </tr>
    <tr>
       <th scope="row" >主营业务</th>
       <td> ${item.main_business} </td>
     </tr>
    <tr>
       <th scope="row" >企业法人</th>
       <td> ${item.lawyer} </td>
     </tr>
         <tr>
       <th scope="row" >经营地址：</th>
       <td>${item.province}${item.city}${item.address}</td>
     </tr>
         <tr>
       <th scope="row" >业务联系人</th>
       <td> ${item.link_name} </td>
     </tr>
         <tr>
       <th scope="row" >电话</th>
       <td> ${item.phone}  </td>
     </tr>
         <tr>
       <th scope="row" >Email</th>
       <td> ${item.email} </td>
     </tr>
         <tr>
       <th scope="row" >所属销售</th>
       <td > ${item.sale_name}  </td>
     </tr>
    
     <tr>
       <th scope="row" >登录手机号</th>
       <td>${item.mobile_username}</td>
     </tr>
     <tr>
       <th scope="row" >是否实名</th>
       <td>
       	<c:choose>
		  <c:when test="${item.real_flag eq '1'}">是</c:when>
		  <c:when test="${item.real_flag eq '0'}">否</c:when> 
	  	</c:choose>
       </td>
     </tr>
     <tr>
       <th scope="row" >密码暗语</th>
       <td >${item.code_word}</td>
     </tr>
     <tr>
       <th scope="row" >法人身份证号</th>
       <td >${item.id_card_no}</td>
     </tr>    
         <tr>
       <th scope="row" >商户机具数量</th>
       <td> ${item.terminal_count}  </td>
     </tr>
         <tr>
       <th scope="row" >商户状态</th>
       <td> 
        <c:if test="${item.open_status eq '1' }">正常</c:if> 
	 	<c:if test="${item.open_status eq '0' }">商户关闭</c:if>
	 	<c:if test="${item.open_status eq '2' }">待审核</c:if>
	 	<c:if test="${item.open_status eq '3' }">审核失败 </c:if>
	 	<c:if test="${item.open_status eq '4' }">冻结 </c:if>
	 	<c:if test="${item.open_status eq '5' }">机具绑定 </c:if>
       </td>
     </tr>
         <tr>
       <th scope="row" >扣率</th>
       <td><c:if test="${item.fee_type eq 'RATIO' }">${item.fee_rate}~不封顶</c:if>
			<c:if test="${item.fee_type eq 'CAPPING'}">${item.fee_rate}~<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.fee_max_amount}" />（元）封顶</c:if>
			<c:if test="${item.fee_type eq 'LADDER'}">${item.ladder_min}%&lt;<fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.ladder_value}" /> （元）&lt;${item.ladder_max}%</c:if>
		</td>
     </tr>
      <tr>
       <th scope="row" >所属代理商</th>
       <td> ${item.agent_name}  </td>
     </tr>
         <tr>
       <th scope="row" >结算周期</th>
       <td> T+${item.settle_cycle}天</td>
     </tr>
         <tr>
       <th scope="row" >账户类型 </th>
       <td>
      		${item.account_type}  
        </td>
     </tr>
         <tr>
       <th scope="row" >开户名</th>
       <td> ${item.account_name}  </td>
     </tr>
         <tr>
       <th scope="row" >开户账号</th>
       <td> ${item.account_no} </td>
     </tr>
         <tr>
       <th scope="row" >开户行全称</th>
       <td> ${item.bank_name}  </td>
     </tr>
         <tr>
       <th scope="row" >联行行号</th>
       <td> ${item.cnaps_no}  </td>
     </tr>
         <tr>
       <th scope="row" >单日终端最大交易额</th>
       <td> <fmt:formatNumber value="${item.ed_max_amount}" type="currency" pattern="#,##0.00#"/> </td>
     </tr>
         <tr>
       <th scope="row" >终端单笔最大交易额</th>
       <td> <fmt:formatNumber value="${item.single_max_amount}" pattern="#,##0.00#" type="number"/>  </td>
     </tr>
         <tr>
       <th scope="row" >单日终端单卡最大交易额</th>
       <td> <fmt:formatNumber value="${item.ed_card_max_amount}" type="currency" pattern="#,##0.00#"/>  </td>
     </tr>
         <tr>
       <th scope="row" >单日终端单卡最大交易笔数</th>
       <td> ${item.ed_card_max_items}</td>
     </tr>
         <tr>
       <th scope="row" >允许交易时间</th>
       <td><fmt:formatDate value="${item.trans_time_start}" type="both" pattern="HH:mm:ss" />  ~ <fmt:formatDate value="${item.trans_time_end}" type="both" pattern="HH:mm:ss" /></td>
     </tr>
      <tr>
       <th scope="row" >是否优质商户</th>
       <td><c:if test="${item.my_settle eq '1' }">是</c:if> 
	 	<c:if test="${item.my_settle eq '0' }">否</c:if></td>
     </tr>
         <tr>
       <th scope="row" >备注信息</th>
      
       <td> ${item.remark} </td>
      
     </tr>
     <tr>
       <th scope="row" >附件</th>
       <td> ${item.attachment} </td>
     </tr>
   
   </table>
   </td>
</c:forEach>
    </tr>
     </table>
    <div style="clear:both;margin:5px;">
         <input type="button" value="返回" onclick="javascript:history.back(-1);"/>
    </div>
   </body>


