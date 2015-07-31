<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
	<script type="text/javascript">
	
	function redExportExcel(){
			var action = $("form:first").attr("action");
			$("form:first").attr("action", "${ctx}/purse/redExport").submit();
			$("form:first").attr("action", action);
			
		}
	
		//创建统计信息html结构文件
	/* function createTransCountInfoHtml(totalMsg) {
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
	}); */
		
		
		$(function(){
			$("#reset").click(function(){
				$(":text").val("");
				$("#agentSureTimeBegin").val("${params['agentSureTimeBegin']}");
				$("#agentSureTimeEnd").val("${params['agentSureTimeEnd']}");
				$("select").val("-1");
			})
			
			//初始省市联动
			var INIT_OPTION = "--请选择--";
			$("<option></option>").val("").text(INIT_OPTION).appendTo("#province");
			$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");

			$.each(provinceName, function(i, n) {
				$("<option></option>").val(n).text(n).appendTo("#province");
			});
			$("#province").change(function() {
				var province = $("#province").val();
				$("#city").empty();
				$("<option></option>").val("").text(INIT_OPTION).appendTo("#city");
				if (province != "") {
					var provinceIndex = $("#province option:selected").index();
					var cityArray = eval("city" + provinceIndex);
					$.each(cityArray, function(i, n) {
						$("<option></option>").val(n).text(n).appendTo("#city");
					});
				}
			});
			var defaultProvince = '${params.province}';
			var defaultCity = '${params.city}';
			if ($.trim(defaultProvince).length > 0) {
				$("#province").val(defaultProvince);
				$("#province").change();
				if ($.trim(defaultCity).length > 0) {
					$("#city").val(defaultCity);
				}
			}
		})
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;红包换购管理</div>
   
   <form:form id="redBuyMachinesQuery" action="${ctx}/purse/redBuyMachines" method="post">
    <div id="search">
    	<div id="title">红包换购情况管理</div>
	      <ul>
	        <li><span>手机号：</span><input type="text"  value="${params['mobileNo']}" name="mobileNo" /></li>
	        <li><span>真实姓名：</span><input type="text"  value="${params['realName']}" name="realName" /></li>
	        <li><span style="width: 100px;">代理商抢购时间：</span>
				<input onFocus="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" class="input" style="width:140px" id="agentSureTimeBegin" name="agentSureTimeBegin" value="${params['agentSureTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
					 ~
				<input onFocus="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd HH:mm:ss',readOnly:true})" type="text" class="input" style="width:140px" id="agentSureTimeEnd" name="agentSureTimeEnd" value="${params['agentSureTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
			</li>
			<li><span style="width: 60px;">换购机具：</span>
	        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="machines" name="machines">
          			<option value="" <c:if test="${params['machines'] eq ''}">selected='selected'</c:if>>全部</option>
			    	<option value="0" <c:if test="${params['machines'] eq '0'}">selected='selected'</c:if>>超级刷</option>	
			    	<option value="1" <c:if test="${params['machines'] eq '1'}">selected='selected'</c:if>>M-posⅢ代</option>	
			</select>
	        </li>	
	        <li><span>代理商编号：</span><input type="text"  value="${params['agentNo']}" name="agentNo" /></li>
	        <li><span style="width: 60px;">换购状态：</span>
	        <select  style="width:108px;padding: 3px;border: 1px solid #A4A4A4;" id="status" name="status">
          			<option value="" <c:if test="${params['status'] eq ''}">selected='selected'</c:if>>全部</option>
			    	<option value="0" <c:if test="${params['status'] eq '0'}">selected='selected'</c:if>>有效</option>	
			    	<option value="1" <c:if test="${params['status'] eq '1'}">selected='selected'</c:if>>已被受理</option>
			    	<option value="2" <c:if test="${params['status'] eq '2'}">selected='selected'</c:if>>受理成功</option>	
			</select>
	        </li>	
	        <li>
				<span style="width: 60px;">收货地址：</span>
				<select id="province" name="province" style="width:108px;padding: 3px;border: 1px solid #A4A4A4;"></select>
				<select id="city" name="city" style="width:108px;padding: 3px;border: 1px solid #A4A4A4;"></select>
			</li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="button" id="reset"   value="清空"/>
    	<input id="exportExcel" class="button blue medium" type="button" onclick="javascript:redExportExcel();" value="导出" />
    </div>
    </form:form>
    <a name="_table"></a>
    <!-- <div id="total_msg" class="total_msg">
			<input class="button blue medium" type="button" id="btnCountInfo" value="统计用户信息" />
		</div> -->
    <div class="tbdata"  style="overflow-y:hidden;overflow-x:scroll;width:99%">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="30px">序号</th>
          <th width="100px">手机号</th>
          <th width="50px">客户端</th>
          <th width="80px" >真实姓名</th>       
          <th width="80px">换购的机具一</th>
          <th width="100px">机具一抵扣金额</th>
          <th width="100px">机具一换购数量</th>
          <th width="100px">机具一收货联系人</th>
          <th width="100px">机具一收货人电话</th>
          <th width="200px">机具一的收货地址</th>
          <th width="80px">换购的机具二</th>
          <th width="100px">机具二抵扣金额</th>
          <th width="100px">机具二换购数量</th>
          <th width="100px">机具二收货联系人</th>
          <th width="100px">机具二收货人电话</th>
          <th width="200px">机具二的收货地址</th>
          <th width="60px">换购状态</th>
          <th width="100px" >抢购的代理商编号</th>
          <th width="150px" >代理商确认受理时间</th>
        </tr>
        </thead>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.mobile_no}</td>
	          <td class="center"  style="word-break: break-all ;">${item.app_name}</td>
	          <td class="center"  style="word-break: break-all ;">${item.real_name}</td>
	          <td class="center"  style="word-break: break-all ;">
	          	  <c:choose>
		          	<c:when test="${item.machines1_name eq '0'}">
		          	超级刷
		          	</c:when>
		          	<c:when test="${item.machines1_name eq '1'}">
		          	M-posⅢ代
		          	</c:when>
		          </c:choose>
	          </td>
	          <td class="center"  style="word-break: break-all ;">
	          	<c:choose>
		          	<c:when test="${item.machines1_amount eq '0.00'}">
		          	</c:when>
		          	<c:otherwise>
		          		${item.machines1_amount}
		          	</c:otherwise>
		          </c:choose>
	          </td>
	          <td class="center"  style="word-break: break-all ;">${item.machines1_num}</td>
	          <td class="center"  style="word-break: break-all ;">${item.contact_people1}</td>
	          <td class="center"  style="word-break: break-all ;">${item.contact_phone1}</td>
	          <td class="center"  style="word-break: break-all ;">${item.address1}</td>
	          <td class="center"  style="word-break: break-all ;">
	          	<c:choose>
		          	<c:when test="${item.machines2_name eq '0'}">
		          	超级刷
		          	</c:when>
		          	<c:when test="${item.machines2_name eq '1'}">
		          	M-posⅢ代
		          	</c:when>
		          </c:choose>
	          </td>
	          <td class="center"  style="word-break: break-all ;">
	          	<c:choose>
		          	<c:when test="${item.machines2_amount eq '0.00'}">
		          	</c:when>
		          	<c:otherwise>
		          		${item.machines2_amount}
		          	</c:otherwise>
		          </c:choose>
	          </td>
	          <td class="center"  style="word-break: break-all ;">${item.machines2_num}</td>
	          <td class="center"  style="word-break: break-all ;">${item.contact_people2}</td>
	          <td class="center"  style="word-break: break-all ;">${item.contact_phone2}</td>
	          <td class="center"  style="word-break: break-all ;">${item.address2}</td>
	          <td class="center"  style="word-break: break-all ;">
	          	<c:if test="${item.status eq '0'}">
	          		有效
	          	</c:if>
	          	<c:if test="${item.status eq '1'}">
	          		已被受理
	          	</c:if>
	          	<c:if test="${item.status eq '2'}">
	          		受理成功
	          	</c:if>
	          </td>
	          <td class="center"  style="word-break: break-all ;">${item.agent_no}</td>
	          <td class="center"  style="word-break: break-all ;"><fmt:formatDate value="${item.agent_sure_time}" type="both"/></td>
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
