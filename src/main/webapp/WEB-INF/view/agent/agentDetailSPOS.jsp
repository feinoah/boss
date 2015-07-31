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
	<div class="title">超级刷代理商信息 </div>
	<ul>
	<li style="width:650px;" id="agent_name"><span>超代名称：</span>${params['agent_name']}</li>
		  <li style="width:650px;" id="pagentname"><span>上级超代：</span>${params['pagentname']}</li>
		  <li style="width:250px;" id="agent_no"><span>超代编号：</span>${params['agent_no']}</li>
		  		 
		  
		  
<%--		  <li style="width:200px;" id="agentType"><span>代理商类型：</span>${params['agentType']}</li>--%>
<%--		  <li style="width:200px;" id="parent_id"><span>上级代理商：</span>${params['pagentname']}</li>--%>
		  <li style="width:250px;" id="agent_link_name"><span>联系人：</span>${params['agent_link_name']}</li>
		  <li style="width:250px;" id="agent_link_tel"><span>联系电话：</span>${params['agent_link_tel']}</li>
		  <li style="width:250px;" id="agent_link_mail"><span>联系邮箱：</span>${params['agent_link_mail']}</li>
		  <li style="width:250px;" id="agent_name"><span>原始密码：</span>${params['default_password']}</li>
		  <li style="width:250px;" id="agent_area"><span>超代区域：</span>${params['agent_area']}</li>
		  <li style="width:500px;" id="agent_address"><span>超代地址：</span>${params['agent_address']}</li>
		  <li style="width:250px;" id="sale_name"><span>销售人员：</span>${params['sale_name']}</li>
		  <li style="width:250px;" id="sale_name"><span>超代接入费：</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params['agent_pay']}" />元</li>
		  <li style="width:250px;" id="sale_name"><span style="width: 100">最小清算金额：</span><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${params['mix_amout']}" />元</li>
		  <li style="width:250px;" id="sale_name"><span>协议费率：</span>${params['agent_rate']}</li>
		  
		  <li style="width:250px;"><span>开户名：</span>${params['account_name']}</li>
		  <li style="width:250px;"><span>账户类型：</span>${params['account_type']}</li>
		  <li style="width:250px;"><span>开户账号：</span>${params['account_no']}</li>
		  <li style="width:250px;"><span>开户行全称：</span>${params['bank_name']}</li>
		  <li style="width:250px;" id="bag_settle"><span style="width: 100px;">是否钱包结算：</span>
		  <c:choose>
				  <c:when test="${params['bag_settle'] eq '0'}">否</c:when>
				  <c:when test="${params['bag_settle'] eq '1'}">是</c:when> 
		  </c:choose>
		  </li>
		  <li style="width:250px;"><span>联行行号：</span>${params['cnaps_no']}</li>
		
		  <li style="width:250px;" id="sale_name"><span>超代状态：</span>
		        <c:choose>
				  <c:when test="${params['agent_status'] eq '1'}">正常</c:when>
				  <c:when test="${params['agent_status'] eq '2'}">冻结</c:when> 
			  </c:choose>
		  </li>
		  <li style="width:250px;" id="agent_area_type"><span>超代级别：</span>
		        <c:choose>
				  <c:when test="${params['agent_area_type'] eq '1'}">市级超代</c:when>
				  <c:when test="${params['agent_area_type'] eq '2'}">省级超代</c:when> 
			  </c:choose>
		  </li>
		  <li style="width:250px;" id="profit_sharing"><span>是否分润：</span>
		        <c:choose>
				  <c:when test="${params['profit_sharing'] eq '1'}">是</c:when>
				  <c:when test="${params['profit_sharing'] eq '2'}">否</c:when> 
			  </c:choose>
		  </li>
		  <li style="width:250px;" id="is_invest"><span>是否投资：</span>
		        <c:choose>
				  <c:when test="${params['is_invest'] eq '1'}">是</c:when>
				  <c:when test="${(params['is_invest'] eq null) || (params['is_invest'] eq '') || (params['is_invest'] eq '0')}">否</c:when> 
			  </c:choose>
		  </li>
		  <c:if test="${params['is_invest'] eq '1'}">
		  	<li style="width:250px;"><span>投资额度：</span>${params['invest_amount']}万</li>
		  	<li style="width:250px;"><span style="width:90px;">常规提现额度：</span>${params['common_deposit_amount']}万</li>
		  	<li style="width:250px;"><span style="width:110px;">常规提现分润比例：</span>${params['common_deposit_rate']}%</li>
		  	<li style="width:250px;"><span style="width:110px;">超额提现分润比例：</span>${params['over_deposit_rate']}%</li>
		  </c:if>
		  <c:if test="${(params['is_invest'] eq null) || (params['is_invest'] eq '') || (params['is_invest'] eq '0')}">
		  <li style="width:250px;"><span style="width:90px;">提现分润比例：</span>${params['deposit_rate']}%</li>
		  </c:if>
		 <li style="width:750px;"><span style="width: 100">分润规则：</span>
		 <c:if test="${params['ruleline1']!=null}">
			${params['ruleline1'][0]}(万)<<fmt:formatNumber value="${params['ruleline1'][1]}" type="percent"/><<fmt:formatNumber type="number" value="${params['ruleline1'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['ruleline2'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['ruleline2'][1]}" type="percent"/>≤<fmt:formatNumber type="number" value="${params['ruleline2'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['ruleline3'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['ruleline3'][1]}" type="percent"/>≤<fmt:formatNumber type="number" value="${params['ruleline3'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['ruleline4'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['ruleline4'][1]}" type="percent"/>≤∞(万);&nbsp;&nbsp;
		 </c:if>
		</li>
		
		<li style="width:750px;"><span style="width: 100">超级刷分润规则：</span>
		 <c:if test="${params['smruleline1']!=null}">
			${params['smruleline1'][0]}(万)<<fmt:formatNumber value="${params['smruleline1'][1]}" type="percent"/><<fmt:formatNumber type="number" value="${params['smruleline1'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['smruleline2'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['smruleline2'][1]}" type="percent"/>≤<fmt:formatNumber type="number" value="${params['smruleline2'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['smruleline3'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['smruleline3'][1]}" type="percent"/>≤<fmt:formatNumber type="number" value="${params['smruleline3'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['smruleline4'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['smruleline4'][1]}" type="percent"/>≤∞(万);&nbsp;&nbsp;
		 </c:if>
		</li>
		<li style="width:750px;display:none;"><span style="width: 110">移联商通分润规则：</span>
		 <c:if test="${params['dto1ruleline1']!=null}">
			${params['dto1ruleline1'][0]}(万)<<fmt:formatNumber value="${params['dto1ruleline1'][1]}" type="percent"/><<fmt:formatNumber type="number" value="${params['dto1ruleline1'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['dto1ruleline2'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['dto1ruleline2'][1]}" type="percent"/>≤<fmt:formatNumber type="number" value="${params['dto1ruleline2'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['dto1ruleline3'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['dto1ruleline3'][1]}" type="percent"/>≤<fmt:formatNumber type="number" value="${params['dto1ruleline3'][2]}" maxFractionDigits="0"/>(万);&nbsp;&nbsp;
			<fmt:formatNumber type="number" value="${params['dto1ruleline4'][0]}" maxFractionDigits="0"/>(万)<<fmt:formatNumber value="${params['dto1ruleline4'][1]}" type="percent"/>≤∞(万);&nbsp;&nbsp;
		 </c:if>
		</li>
  
     <!--代理商行业计算成本 start-->
       		<li style="width:750px;"><span style="width: 100">行业计算成本：</span>
               <label> 民生A类</label>       ${params.live_a_type}&nbsp;&nbsp;
               <label> 民生B类 </label>  ${params.live_b_type} &nbsp;&nbsp;
               <label> 批发对公 </label>   ${params.wholesale_pub_type}&nbsp;&nbsp;
               <label> 批发对私封顶类 </label>  ${params.wholesale_pri_cap_type}&nbsp;&nbsp;
            </li>
            <li  style="width: 750px;"><span style="width: 100">&nbsp;</span>
               <label>批发对私非封顶类    </label>  ${params.wholesale_pri_nocap_type}&nbsp;&nbsp;
               <label>房地产及汽车销售类    </label>  ${params.estate_car_type}&nbsp;&nbsp;
               <label>一般A类    </label> ${params.general_type}&nbsp;&nbsp;
               <label>一般B类    </label> ${params.general_b_type}&nbsp;&nbsp;
            </li>
            <li  style="width: 750px;"><span style="width: 100">&nbsp;</span>
            	 <label>餐饮类     </label>  ${params.catering_type}&nbsp;&nbsp;
               <label>超级刷    </label>  ${params.smbox_type}&nbsp;&nbsp;
            </li>  
     <!--代理商行业计算成本 end    -->
       
     
     
		  <li style="width:500px;"><span>创建时间：</span><fmt:formatDate value="${params['create_time']}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
		  
    </ul>
		  <div class="clear"></div>
	<br/>
	<ul>
		  <li style="width:320px;" id="manager_logo"><span style="width:100px">管理系统LOGO：</span><img alt="管理系统LOGO" width="100" height="55" src="${params['manager_logo']}"/></li>
		  
		  <li  id="client_logo"><span  style="width:100px">客户端LOGO：</span><img alt="客户端LOGO" width="100" height="55" src="${params['client_logo']}"/> </li>
	</ul>
	<div class="clear"></div>
</div>
<div class="clear"></div>

<div class="tbdata"  style="display: none;">
      <table width="100%" cellspacing="0" class="t2" style="line-height: 1.5;font:12px/1.5 \5FAE\8F6F\96C5\9ED1,Tahoma,Verdana,Arial,Helvetica,sans-serif">
        <thead>
        <tr><th width="35">序号</th>
          <th >增机批次号</th>
          <th >机具数量</th>
          <th > 机具类型 </th>
          <th >操作人员</th>
          <th width="120">创建时间</th>
          </tr>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.batch_no}</td>
	          <td align="left" >${item.terminal_count}</td>
	          <td align="left">${item.terminal_type}</td>
	          <td align="left">${item.operator}</td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	        </tr>
          </c:forEach>
      </table>
    </div>
	<div id="page" style="display: none;">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>

</body>
</html>