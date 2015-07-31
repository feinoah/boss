<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<%@page import="com.eeepay.boss.utils.SysConfig"%>
<head>
<%@ include file="/WEB-INF/uploadPLupload.jsp"%>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
.hovers{ background-color:#3368c4; color:fff}
#form_div{width: 100%;border: 0px solid red;}
.ul_addTranLimit{width: 98%;border: 0px solid red;float: right;}
.ul_addTranLimit li{width: 28%;height: 25px;line-height: 25px;padding-top: 5px;float: left;}
.ul_addTranLimit li span{display:-moz-inline-box;display:inline-block;width: 85px;border:0px solid red;}
.must{border:0px solid red;height:23px;line-height:23px;width: 5px;color: red;}
.area {width: 75px;}
#attachment_fileUploader {vertical-align: middle;margin-left: 10px;} 
.merchant_title {padding: 6px 6px 6px 13px;background: none repeat scroll 0% 0% #DFE9F0;margin: 10px auto;font-weight: bold;}
.selecPic {border: 1px solid red;display: inline;color: #fff;background-color: #5cb85c;border-color: #4cae4c;padding: 6px 10px 6px 8px;cursor: pointer;-webkit-user-select: none;user-select: none;border-radius: 4px;}
.selecPic .flag {font-weight: bold;display: inline;font-size: 16px;}
</style>
<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/cm_ajax.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<script type="text/javascript">
	$(function(){
		var cus = 0;
	    var classname = "";
	    var $autocomplete = $("<ul class='autocomplete' style='position:absolute;overflow-y:auto;width:328px;margin-left:33px;margin-top:21px;background-color: #FFFFFF'></ul>").hide().insertAfter("#agentInput");
	    $("#agentInput").keyup(function(event) {
		    var arry = new Array();
		    $("[name=agentNo]").find("option").each(function(i, n) {
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
	                            $("[name=agentNo]").find("option").each(function(i, n) {
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
	                $("[name=agentNo]").find("option").each(function(i, n) {
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
	                $("[name=agentNo]").find("option").each(function(i, n) {
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
	                
	                $("[name=agentNo]").find("option").each(function(i, n) {
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
	    });
	    
	    $("[name=agentNo]").change(function(){
	    	var agent_Name =  $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
			if(agent_Name != ""){
				$("#agentInput").val(agent_Name);
				$autocomplete.hide();
			}
	    	});
	});
	
	function ajaxTransRuleDel(id){
		if(confirm("确定要删除当前记录吗?")){
			$.ajax({type:"post", url:"${ctx}/agent/agentTransRuleDel", data:{"id":id}, dataType: 'text json',
				success: function(data){
					var ret = data.msg;
					if (ret == "OK"){
						$.dialog({title: '提示',lock:true,content: '代理商交易额度控制删除成功',icon: 'success.gif',ok:null ,close:function(){
							location.href="${ctx}/agent/transRuleList";
						}});
					}
				}
			});
		}
	}
	
	function setAgentNo(op){
		var value = op.value,
			obj = $("#agent-select");
			obj.val('');
		if(value){
			obj.get(0).selectedIndex=0;
			obj.val(value);
		}
		$("#agentInput").val($("[name=agentNo]").find("option:selected").text());
	}
	
	function ajaxTransRuleMod(id){
		
		$.dialog({title:'调额',width:400,height:250,lock: true,drag: true,resize: false,max: false,content: 'url:viewTransRule?ids=' + id + '&layout=no',close: function(){
			$('form:first').submit();
		}});
		
	}
	
	function allCheck(){
		$('input[name="ids"]').prop('checked', true);
	}
	
	function oppsiteCheck(){
		$('input[name="ids"]').each(function(){
			var that = $(this);
			that.prop("checked", !that.prop('checked'));
		});
	}
	
	function batchMod(){
		if($('input[name="ids"]:checked').length){
			var ids = '';
			$('input[name="ids"]:checked').each(function(){
				ids += $(this).val() + ',';
			});
			ids = ids.substr(0,ids.lastIndexOf(","));
			ajaxTransRuleMod(ids);
		}else{
			$.dialog({title: '错误',lock:true,content: "没有选择任何记录",icon: 'error.gif'});
		}
	}
</script>
</head>
<body>
	<div id="content">
		<div id="nav"><img class="left" src="${ctx}/images/home.gif" />当前位置：代理商管理&gt;代理商交易额度控制</div>
		<form:form id="addTranLimit" action="${ctx}/agent/transRuleList" method="post" >
			<div style="position:absolute;margin-left:75px;margin-top:50px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;width: 125px;border:none;">
         	</div>
			<div id="search">
			<div id="title">代理商交易额度控制</div>
			<ul class="ul_addTranLimit">
				<li><span>代理商：</span><u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" style="padding:2px;width:157px"  /></li>
				<li><span>代理商编号：</span><input type="text" style="padding:2px;width:150px" class="input"  value="${params['agentNoText']}" name="agentNoText" onblur="setAgentNo(this);" /></li>
				<li><span>设备类型：</span>
					<u:TableSelect sname="pos_type" style="padding:2px;width:157px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
				</li>
				<li><span>商户类型：</span>
					<select id="merchant_type" name="merchant_type" style="padding:2px;width: 157px" >
						<option value="">--请选择--</option>
						<option value="5812" <c:if test="${params.merchant_type eq '5812' }">selected="selected"</c:if>>餐娱类</option>
						<option value="5111" <c:if test="${params.merchant_type eq '5111' }">selected="selected"</c:if>>批发类</option>
						<option value="5541" <c:if test="${params.merchant_type eq '5541' }">selected="selected"</c:if>>民生类</option>
						<option value="5331" <c:if test="${params.merchant_type eq '5331' }">selected="selected"</c:if>>一般类</option>
						<option value="1520" <c:if test="${params.merchant_type eq '1520' }">selected="selected"</c:if>>房车类</option>
						<option value="1011" <c:if test="${params.merchant_type eq '1011' }">selected="selected"</c:if>>其他</option>
					</select>
				</li>
				<li><span>是否实名：</span>
					<select name="real_flag" id="real_flag" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="0" <c:if test="${params.real_flag == '0' }">selected="selected"</c:if>>否</option>
						<option value="1" <c:if test="${params.real_flag == '1' }">selected="selected"</c:if>>是</option>
					</select>
				</li>
				<li><span>扣率类型：</span>
					<select id="fee_type" name="fee_type" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="RATIO" <c:out value="${params['fee_type'] eq 'RATIO'?'selected':'' }"/>>扣率</option>
							<option value="CAPPING" <c:out value="${params['fee_type'] eq 'CAPPING'?'selected':'' }"/>>封顶</option>
							<option value="LADDER" <c:out value="${params['fee_type'] eq 'LADDER'?'selected':'' }"/>>阶梯</option>
					</select>
				</li>
				<li><span>账户类型：</span>
					<select name="account_type" id="account_type" style="padding:2px;width: 157px">
						<option value="">--请选择--</option>
						<option value="对公" <c:if test="${params.account_type == '对公'}">selected="selected"</c:if>>对公</option>
							<option value="对私" <c:if test="${params.account_type == '对私'}">selected="selected"</c:if>>对私</option>
					</select>
				</li>
			</ul>
			<div class="clear"></div>
			<div class="search_btn clear">
				<input class="button blue medium" type="submit" id="submitButton"  value="查询"/>
				<input class="button blue medium" type="button" id="addButton" onclick="location.href='${ctx}/agent/agentTransRuleInput'" value="新增" />
			</div>
		</div>	
	</form:form>
	<!-- 表单结束 -->
	<div class="clear"></div>
	
	<div id="tbdata" class="tbdata">
	<div style="width:100%;overflow-x:auto;">
	<a href="javascript:allCheck();" style="padding:5px;">全选</a>
	<a href="javascript:oppsiteCheck();" style="padding:5px;">反选</a>
	<a href="javascript:batchMod();" style="padding:5px;">批量修改额度</a>
      <table width="100%" cellspacing="0" class="t2" >
        <thead>
        <tr>
          <th width="35px">选择</th>
          <th width="180px">代理商</th>
          <th width="90px">设备类型</th>
          <th width="90px">商户类型</th>
          <th width="80px">费率类型</th>
          <th width="80px">是否实名</th>
          <th width="80px">账户类型</th>
          <th width="120px">单卡单笔</th>
          <th width="120px">单卡单日</th>
          <th width="120px">单终端</th>
          <th width="80px">单日笔数</th>
<!--          <th width="80px">所属销售</th>-->
          <th width="80px">操作</th>
	 </tr>
     </thead>     
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	       	  <td class="center"><input type="checkbox" name="ids"  value="${item.id}" /></td>
	          <td><u:AgentNameForNo agentNo="${item.agent_no}"/></td>
	          <td class="center"><u:postype svalue="${item.pos_type}" /></td>
	          <td class="center">
		          <c:choose>
		          	<c:when test="${item.merchant_type eq '5812'}">餐娱类</c:when>
		          	<c:when test="${item.merchant_type eq '5111'}">批发类</c:when>
		          	<c:when test="${item.merchant_type eq '5541'}">民生类</c:when>
		          	<c:when test="${item.merchant_type eq '5331'}">一般类</c:when>
		          	<c:when test="${item.merchant_type eq '1520'}">房车类</c:when>
		          	<c:when test="${item.merchant_type eq '1011'}">其他</c:when>
		          	<c:otherwise>${item.merchant_type}</c:otherwise>
		          </c:choose>
	          </td>
	          <td class="center">
	          	<c:choose>
	          		<c:when test="${item.fee_type eq 'RATIO'}">扣率</c:when>
	          		<c:when test="${item.fee_type eq 'CAPPING'}">封顶</c:when>
	          		<c:when test="${item.fee_type eq 'LADDER'}">阶梯</c:when>
					<c:otherwise>${item.fee_type}</c:otherwise>	          		
	          	</c:choose>
	          </td>
	          <td class="center">
	          	<c:choose>
	          		<c:when test="${item.real_flag eq '0'}">否</c:when>
	          		<c:when test="${item.real_flag eq '1'}">是</c:when>
	          	</c:choose>
	          </td>
	          <td class="center">${item.account_type}</td>
	          <td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.single_max_amount}" />元</td>
	          <td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.ed_card_max_amount}" />元</td>
	          <td><fmt:formatNumber type="currency" pattern="#,##0.00#" value="${item.ed_max_amount}" />元</td>
	          <td>${item.ed_card_max_items}笔</td>
<!--	          <td>${item.sale_name}</td>-->
	          <td class="center">
	          	<a href="javascript:ajaxTransRuleDel('${item.id}');">删除</a>	
	          	<a href="javascript:ajaxTransRuleMod('${item.id}');">修改</a>
	          </td>
	        </tr>
          </c:forEach>
      </table>
      </div>
    </div>
	<div id="page">
		<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" />
	</div>
	</div>
	<script type="text/javascript">
		$("#agentInput").val($("[name=agentNo]").find("option:selected").text());
	</script>
</body>
