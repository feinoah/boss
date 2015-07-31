<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<script type="text/javascript" src="${ctx}/scripts/provinceCity.js"></script>
<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
<style type="text/css">
.autocomplete{list-style-type:none; margin:0px; padding:0px; border:#008080 1px solid }
		.autocomplete li{font-size:12px; font-family:"Lucida Console", Monaco, monospace; font-weight:bold; cursor:pointer; height:18px; line-height:18px}
		.hovers{ background-color:#3368c4; color:fff}
	</style>
	<script type="text/javascript">
	$(function() {
		var agentT = $("[name=agentNo]").find("option:selected").text();
		$("#agentInput").val(agentT);
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
			                $("[name=agentNo]").find("option").each(function(i, n) {
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
			    });/*.blur(function() {
			        setTimeout(function() {
			            $autocomplete.hide();
			        },
			        3000);
			    });*/
			    
			    $("[name=agentNo]").change(function(){
			    	var agent_Name =  $("[name=agentNo]").find("option:selected").text(); //集群所属所属代理商名称
					if(agent_Name != ""){
						$("#agentInput").val(agent_Name);
						$autocomplete.hide();
					}
			    	});
		
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
		
	});
	
	
	
	
		function showDetail(id)
		{
			 // $.dialog({title:'商户详情',width: 720,height:530,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
			window.location.href='merDetail?id='+id;
		}
	
	 function setNormal(merchant_no,mobile_username,merchant_name)
		{
	
			  $.dialog.confirm('确定要设置商户为正常状态？', function(){
				 $.ajax({
						    url: "merSetNormal?",
						    type:"POST",
						    data:"merchant_no="+merchant_no+"&mobile_username="+mobile_username+"&merchant_name="+merchant_name+"&layout=no",
						    success: function(data){
							    	var ico="error.gif";
							    	var msg=data;
							    	if("SUCCESS"==data){
							    		ico="success.gif";
							    		msg = "商户状态已开启！";
							    	}
								   	var dialog = $.dialog({title: '提示',lock:true,content: msg,icon: ico,ok:null ,close:function(){
										location.href="${ctx}/mer/merQuery";
									}});
						     }
			        });
			 });
		}
		
		//冻结该商户的交易
		function freezeTrans(merchant_no)
		{
	
			$.dialog.confirm('确定要冻结该商户的交易？', function(){
				 $.ajax({
						    url: "merFreezeTrans?merchant_no="+merchant_no+"&layout=no",
						    success: function(data){
							   	var dialog = $.dialog({title: '提示',lock:true,content: '商户的交易状态已被冻结',icon: 'success.gif',ok:null ,close:function(){
									location.href="${ctx}/mer/merQuery";
								}});
						     }
						   });
			});
	
		}
		
  //
   function setClose(merchant_no)
  {
 
   $.dialog.confirm('确定要设置商户为关闭状态？', function(){
     $.ajax({
          url: "merSetNormal?merchant_no="+merchant_no+"&layout=no&status=0",
          success: function(data){
           var dialog = $.dialog({title: '提示',lock:true,content: '商户已为关闭状态',icon: 'success.gif',ok:null ,close:function(){
         location.href="${ctx}/mer/merQuery";
        }});
           }
         });
   });
 
  }
  
  
		//解除绑定
		function merDel(id,merchant_no){
			if(!confirm("是否删除该商户？"))
			{
				return;
			}
			$.post(
				'${ctx}/mer/merDel',
				{id:id,merchant_no:merchant_no},
				function(data)
				{
					var ret = data.msg;
					if(ret == "OK")
					{
						alert("商户删除成功");
						$("#submit").click();
					}else if(ret == "ERROR"){
					  	alert("商户删除出错！");
						$("#submit").click();
					 }
				}
			);
		}
		
		
		function testMerchant(merchant_no)
		{
	
			$.dialog.confirm('确定要测试商户？', function(){
				 $.ajax({
						    url: "testMerchant?merchant_no="+merchant_no+"&layout=no",
						    success: function(data){
						    	var ico="error.gif";
						    	var msg=data;
						    	if("SUCCESS"==data){
						    		ico="success.gif";
						    	}
							   	var dialog = $.dialog({title: '提示',lock:true,content: msg,icon: ico,ok:null ,close:function(){
						
								}});
						     }
						   });
			});
	
		}
		

		function changePassword(merchant_no){
			if(confirm("确定重置手机客户端登陆密码吗?")){
				 $.get('${ctx}/mer/changePassword?merchantNo='+merchant_no,function(data){
					if(data==1){
						alert('手机登陆密码已经重置为888888');
					}else{
						alert('密码重置失败，请检查数据');
					}
				});
			}
		}
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>商户检索</div>
   
   <form:form id="merQuery" action="${ctx}/mer/merQueryN" method="post">
   <div style="position:absolute;margin-left:63px;margin-top:46px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 120px;width: 110px !important;border: none;">
         	</div>
    <div id="search">
    	<div id="title">商户检索</div>
	      <ul>
  	      	<li><span>代理商名称：</span><u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true" /></li>
	        <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant" /></li>
	      	<li><span>创建时间：</span>
			 	<input  type="text"  style="width:102px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:102px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	      <ul>
			 <li><span>商户状态：</span> 
	         	<select style="padding:2px;width:140px" name="openStatus">
	         		<option value="-1" <c:out value="${params['openStatus'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['openStatus'] eq '1'?'selected':'' }"/>>正常</option>
	         		<option value="0" <c:out value="${params['openStatus'] eq '0'?'selected':'' }"/>>商户关闭</option>
	         		<option value="5" <c:out value="${params['openStatus'] eq '5'?'selected':'' }"/>>机具绑定</option>
	         		<option value="2" <c:out value="${params['openStatus'] eq '2'?'selected':'' }"/>>待审核</option>
	         		<option value="3" <c:out value="${params['openStatus'] eq '3'?'selected':'' }"/>>审核失败</option>
	         		<option value="4" <c:out value="${params['openStatus'] eq '4'?'selected':'' }"/>>冻结</option>
	         		<option value="6" <c:out value="${params['openStatus'] eq '6'?'selected':'' }"/>>初审</option>
	         	</select>
			 </li>
			 <li><span style="width: 90px;">设备类型：</span> 
	         	<select style="padding:2px;width:128px" name="pos_type">
	         		<option value="-1" <c:out value="${params['pos_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['pos_type'] eq '1'?'selected':'' }"/>>移联商宝</option>
	         		<option value="2" <c:out value="${params['pos_type'] eq '2'?'selected':'' }"/>>传统POS</option>
	         		<option value="3" <c:out value="${params['pos_type'] eq '3'?'selected':'' }"/>>移小宝</option>
	         		<option value="4" <c:out value="${params['pos_type'] eq '4'?'selected':'' }"/>>移联商通</option>
	         		<option value="5" <c:out value="${params['pos_type'] eq '5'?'selected':'' }"/>>超级刷</option>
	         	</select>
			 </li>
			  <li><span style="width: 76px;">手机号码：</span><input type="text"  style="width: 82px" value="${params['phone']}" name="phone" /></li>
			  <li><span style="width: 55px;">审核人：</span>
			  		<select style="width: 75px;height: 24px;vertical-align: top;" name="checker" id="checker">
			  			<option value='-1'>请选择</option>
						<c:forEach items="${checker}" var="item" varStatus="status">
							<option value="${item.real_name}"  <c:out value="${params['checker'] eq item ?'selected':'' }"/>>
								${item.real_name}
							</option>
						</c:forEach>
			  		</select> 
			  </li>
			 <li><span>开通方式：</span><select style="padding:2px;width:140px" name="open_type">
	         		<option value="-1" <c:out value="${params['open_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['open_type'] eq '1'?'selected':'' }"/>>手动</option>
	         		<option value="2" <c:out value="${params['open_type'] eq '2'?'selected':'' }"/>>自动</option>
	         	</select>
			 </li>
			 <%--<li><span style="width: 90px;">持卡人姓名：</span><input type="text"  value="${params['account_name']}" name="account_name" /></li>
			 --%><li><label style="width: 65px;">省份：<select id="province" name="province"  style="	width: 75px;height: 24px;vertical-align: top;" ></select></label>
			       <label  style="width: 65px;">市区：<select id="city" name="city" style="	width: 75px;height: 24px;vertical-align: top;"></select></label></li>
	      </ul>
	      
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submit"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="5%">序号</th>
          <th>代理商名称</th>
          <th>商户简称</th>
          <th  width="80">商户状态</th>
          <th width="120">创建时间</th>
          <th width="60">开通方式</th>
          <th width="150">操作</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
          	<tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.agent_name}</td>
	           <td>${item.merchant_short_name} </td>
	          <td >
	          <c:choose>
				  <c:when test="${item.open_status eq '1'}">正常</c:when>
				  <c:when test="${item.open_status eq '0'}">商户关闭</c:when> 
				 <c:when test="${item.open_status eq '2'}">待审核</c:when> 
				 <c:when test="${item.open_status eq '5'}">机具绑定</c:when> 
				 <c:when test="${item.open_status eq '4'}">冻结</c:when> 
				 <c:when test="${item.open_status eq '6'}">初审</c:when> 
				 <c:when test="${item.open_status eq '3'}"><span class="font_red">审核失败</span></c:when> 
			  </c:choose>
			  </td>
	          <td><fmt:formatDate value="${item.create_time}" type="both"/></td>
	          <td class="center">
	          	<c:choose>
				  <c:when test="${item.open_type eq '1'}">手动</c:when>
				  <c:when test="${item.open_type eq '2'}">自动</c:when>
				</c:choose> 
	          </td>
	          <td class="center">
	           <shiro:hasPermission name="COMMERCIAL_QUERY_DETAIL_NEW"><a href="javascript:showDetail(${item.id});" title="详情">详</a> | </shiro:hasPermission>
	          <c:if test="${item.open_status eq '5'||item.open_status eq '0'}">
	            <shiro:hasPermission name="COMMERCIAL_SET_NORMAL"><a href="javascript:setNormal('${item.merchant_no}','${item.mobile_username}','${item.merchant_name}');" title="开启">开</a> | </shiro:hasPermission>
	          </c:if>
	           <c:if test="${item.open_status eq '1'}">
	            <shiro:hasPermission name="COMMERCIAL_SET_FREEZE"><a href="javascript:freezeTrans(${item.merchant_no});" title="冻结">冻</a> | </shiro:hasPermission>
	          </c:if>
	          <c:if test="${item.open_status eq '4'}">
	            <shiro:hasPermission name="COMMERCIAL_SET_UNFREEZE"><a href="javascript:setNormal('${item.merchant_no}','${item.mobile_username}','${item.merchant_name}');" title="解冻">解</a> | </shiro:hasPermission>
	          </c:if>
              <c:if test="${item.open_status!='0'}">
                <shiro:hasPermission name="COMMERCIAL_SET_NORMAL"><a href="javascript:setClose(${item.merchant_no});" title="关闭">关</a> | </shiro:hasPermission>
              </c:if>
           
	           <shiro:hasPermission name="COMMERCIAL_QUERY_MODIFY_NEW"><a href="${ctx }/mer/merload?id=${item.id}" title="修改">改</a></shiro:hasPermission>
	           <c:if test="${item.open_status eq '2' || item.open_status eq '3'}">
	           		| <shiro:hasPermission name="MERCHANT_DEL"><a href="javascript:merDel(${item.id},${item.merchant_no})" title="删除">删 </a></shiro:hasPermission>
	           </c:if>
	            <shiro:hasPermission name="COMMERCIAL_TEST">| <a href="javascript:testMerchant('${item.merchant_no}');" title="测试">测</a></shiro:hasPermission>
	           
	            <shiro:hasPermission name="COMMERCIAL_RESET">
	              <c:choose>
				  <c:when test="${item.open_status eq '1'}">
				 	| <a href="javascript:changePassword('${item.merchant_no}');">置</a></td>
				  </c:when>
			      </c:choose>
	            </shiro:hasPermission>
	            </td>
	        </tr>
	        </c:forEach>
      </table>
    </div>
	<div id="page">
			<pagebar:pagebar total="${list.totalPages}" current="${list.number + 1}" anchor="_table"/>
	</div>
  </div>
</body>
