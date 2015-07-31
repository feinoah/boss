<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
<script type="text/javascript" src="${ ctx}/scripts/utils.js"></script>
	<script type="text/javascript">
	//可输入下拉菜单
	$(function(){
		        var cus = 0;
			    var classname = "";
			    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
			    $("#agentInput").keyup(function(event) {
				    var arry = new Array();
				    $("[name=agent_no]").find("option").each(function(i, n) {
				        arry[i] = $(this).text();
				    });
			        if ((event.keyCode != 38) && (event.keyCode != 40) && (event.keyCode != 13)) {
			            $autocomplete.empty();
			            var $SerTxt = $("#agentInput").val().toLowerCase();
			            if ($SerTxt != "" && $SerTxt != null) {
			                for (var k = 0; k < arry.length; k++) {
			                    if (arry[k].toLowerCase().indexOf($SerTxt) >= 0) {
			                    	
			                        $("<li title=" + arry[k] + " class=" + classname +" style='background-color: #FFFFFF'></li>").text(arry[k]).appendTo($autocomplete).mouseover(function() {
			                            $(".autocomplete li").removeClass("hovers");
			                            $(this).css({
			                                background: "#3368c4",
			                                color: "#fff"
			                            });
			                        }).mouseout(function() {
			                            $(this).css({
			                                background: "#fff",
			                                color: "#000"
			                            });
			                        }).click(function() {
			                        	var text=$(this).text();
			                            $("#agentInput").val(text);
			                            $("[name=agent_no]").find("option").each(function(i, n) {
			                                if($(this).text()==text){
			                             	   $(this).prop('selected', 'true');
			                                }
			                             });
			                            $autocomplete.hide();
			                        });
			                    }
			                }
			            }
			            $autocomplete.show();
			        }
			        var listsize = $(".autocomplete li").size();
			        $(".autocomplete li").eq(0).addClass("hovers");
			        if (event.keyCode == 38) {
			            if (cus < 1) {
			                cus = listsize - 1;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#agentInput").val(text);
			                $("[name=agent_no]").find("option").each(function(i, n) {
			                   if($(this).text()==text){
			                	   $(this).prop('selected', 'true');
			                   }
			                });
			            } else {
			                cus--;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#agentInput").val(text);
			                $("[name=agent_no]").find("option").each(function(i, n) {
			                    if($(this).text()==text){
			                    	$(this).prop('selected', 'true');
			                    }
			                 });
			            }
			        }
			        if (event.keyCode == 40) {
			            if (cus < (listsize - 1)) {
			                cus++;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#agentInput").val(text);
			                $("[name=agent_no]").find("option").each(function(i, n) {
			                    if($(this).text()==text){
			                    	$(this).prop('selected', 'true');
			                    }
			                 });
			            } else {
			                cus = 0;
			                $(".autocomplete li").removeClass();
			                $(".autocomplete li").eq(cus).addClass("hovers");
			                var text = $(".autocomplete li").eq(cus).text();
			                $("#agentInput").val(text);
			                
			                $("[name=agent_no]").find("option").each(function(i, n) {
			                    if($(this).text()==text){
			                 	   $(this).prop('selected', 'true');
			                    }
			                 });
			            }
			        }
			        if (event.keyCode == 13) {
			            $(".autocomplete li").removeClass();
						$autocomplete.hide();

			        }
			    });/*.blur(function() {
			        setTimeout(function() {
			            $autocomplete.hide();
			        },
			        3000);
			    });*/
			    
			    $("[name=agent_no]").change(function(){
			    	var agent_Name =  $("[name=agent_no]").find("option:selected").text(); //集群所属所属代理商名称
					if(agent_Name != ""){
						$("#agentInput").val(agent_Name);
						$autocomplete.hide();
					}
			    	});
	 });
	function checkDetail(id){
		var url = "${ctx}/mer/checkDetail?id="+id+"";
		window.location.href = url; 
	}
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>商户审核</div>
    
    <form:form id="merCheck" action="${ctx}/mer/checkQuery" method="post">
    <div style="position:absolute;margin-left:63px;margin-top:46px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 120px;border:none;">
         	</div>
    <%--
    border:none;
    --%><div id="search">
    	<div id="title">商户审核查询</div>
	      <ul>
	         <li><span>代理商名称：</span><u:select value="${params['agent_no']}"  stype="agent" sname="agent_no" onlyThowParentAgent="true"  /></li>
	         <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	      	<li><span>创建时间：</span>
			 	<input  type="text" style="width:102px" name="create_time_begin" class="input" readonly="readonly"  value="${params['create_time_begin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" name="create_time_end" class="input" readonly="readonly"  value="${params['create_time_end']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	       <div class="clear"></div>
	      <ul>
			 <li><span>进件方式：</span> 
	         	<select style="padding:2px;width:140px" name="add_type">
	         		<option value="-1" <c:out value="${params['add_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="0" <c:out value="${params['add_type'] eq '0'?'selected':'' }"/>>网站进件</option>
	         		<option value="1" <c:out value="${params['add_type'] eq '1'?'selected':'' }"/>>客户端进件</option>
	         	</select>
			 </li>
			 
<!--			 <li><span style="width: 90px;">设备类型：</span> -->
<!--	         	<select style="padding:2px;width:128px" name="pos_type">-->
<!--	         		<option value="-1" <c:out value="${params['pos_type'] eq '-1'?'selected':'' }"/>>全部</option>-->
<!--	         		<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>-->
<!--	         		<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>-->
<!--	         		<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>-->
<!--	         		<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>-->
<!--	         		<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>-->
<!--	         	</select>-->
<!--			 </li>-->
			<li><span style="width: 90px;">设备类型：</span> 
	         	<u:TableSelect sname="pos_type" style="padding:2px;width:128px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
			 </li>
			 <li><span style="width: 75px;">审核人：</span>	
			 <select name="param_value" id="param_value" style="width:157px;padding: 3px;">
				<option value='-1'>请选择</option>
				<c:forEach items="${checkPerson}" var="item" varStatus="status">
					<option value="${item.real_name}"   <c:out value="${params['param_value'] eq item.real_name ?'selected':'' }"/>>
						${item.real_name}
					</option>
				</c:forEach>
			</select>
			 </li>
	      </ul>
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <tr>
          <th width="4%">序号</th>
          <th width="26%">代理商名称</th>
          <th width="20%">商户简称</th>
          <th width="10%">设备类型</th>
          <th width="5%">实名</th>
          <th width="6%">审核人</th>
          <th width="7%">审核状态</th>	
          <th width="17%">创建时间</th>
          <th width="5%">操作</th>
 		</tr>
 		<c:forEach items="${list.content}" var="item" varStatus="status">
 		
 			<c:set var="date1"><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd" type="date"/></c:set>
			<c:set var="date2">2014-01-14</c:set>
			
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.agent_name}</td>
	          <td>${item.merchant_short_name}</td>
	          <td>
	          	<u:postype svalue="${item.pos_type}" />
<!--	          	 <c:if test="${item.pos_type eq '1' }">移联商宝</c:if> -->
<!--				 <c:if test="${item.pos_type eq '2' }">传统POS</c:if>-->
<!--				 <c:if test="${item.pos_type eq '3' }">移小宝</c:if>-->
<!--				 <c:if test="${item.pos_type eq '4' }">移联商通</c:if>-->
<!--				 <c:if test="${item.pos_type eq '5' }">超级刷</c:if>-->
	         	<%-- <c:if test="${date2 >= date1}">
	          		<c:if test="${item.merchant_type eq '5812' }">宾馆、餐饮、娱乐</c:if>
					<c:if test="${item.merchant_type eq '1520' }">批发、房产、汽车</c:if>
					<c:if test="${item.merchant_type eq '5300' }">航空、加油、超市</c:if>
					<c:if test="${item.merchant_type eq '5111' }">医院、学校、政府</c:if>
					<c:if test="${item.merchant_type eq '6300' }">保险、公共事业</c:if>
					<c:if test="${item.merchant_type eq '5541' }">民生类</c:if>
					<c:if test="${item.merchant_type eq '5331' }">一般类</c:if>
					<c:if test="${item.merchant_type eq '1011' }">其他</c:if>
				</c:if>	
				<c:if test="${date2 < date1}">
					<c:if test="${item.merchant_type eq '5812' }">餐娱类</c:if>
					<c:if test="${item.merchant_type eq '5111' }">批发类</c:if>
					<c:if test="${item.merchant_type eq '5541' }">民生类</c:if>
					<c:if test="${item.merchant_type eq '5331' }">一般类</c:if>
					<c:if test="${item.merchant_type eq '1520' }">房车类</c:if>
					<c:if test="${item.merchant_type eq '1011' }">其他</c:if>
				</c:if>--%>
				
					
				</td>
	          <td align="center"><c:if test="${item.real_flag == '0' }">否</c:if><c:if test="${item.real_flag == '1' }">是</c:if></td>
	          <td>
	          	${item.checker}
	          </td>
	          <td  align="center">
	          	<c:choose>
				  <c:when test="${item.open_status eq '0'}"><span class="font_red">关闭</span></c:when>
				  <c:when test="${item.open_status eq '1'}">开通</c:when>
				  <c:when test="${item.open_status eq '5'}">机具绑定</c:when>
				  <c:when test="${item.open_status eq '2' || item.open_status eq null || item.open_status eq ''}"><span class="font_red">未审核</span></c:when>
				  <c:when test="${item.open_status eq '3'}"><span class="font_red">审核失败</span></c:when>    
				  <c:when test="${item.open_status eq '8'}">等待复审</c:when>
				  <c:otherwise>${item.open_status }</c:otherwise>
			  	</c:choose>
	          </td>
	          
	          <td><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></td>
	          <td class="center"> <shiro:hasPermission name="COMMERCIAL_CHECK_CHECK"><a href="javascript:checkDetail(${item.id});">审核</a></shiro:hasPermission></td>	         
	        </tr>
         </c:forEach>
 		
 		         
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
  <script type="text/javascript">
		$("#agentInput").val( $("[name=agent_no]").find("option:selected").text());
	</script>
	<script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
