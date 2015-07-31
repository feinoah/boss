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
		
		function hlfSendMerchant(id)
		{
			$.post(
					'${ctx}/mer/hlfSendMerchant',
					{id:id},
					function(data)
					{
						var ret = data.status;
						if(ret == "100")
						{
							alert(data.message);
							$("#submitButton").click();
						}else{
						  	alert(data.message+",错误码:"+data.status);
						 }
					}
				);
			 // $.dialog({title:'商户详情',width: 720,height:530,resize: false,lock: true,max:false,content: 'url:merDetail?id='+id+'&layout=no'});
			//window.location.href='hlfSendMerchant?id='+id;
		}
	
	 function setNormal(merchant_no,mobile_username,merchant_name)
		{
	
			  $.dialog.confirm('确定要设置商户为正常状态？', function(){
				 $.ajax({
						    url: "merSetNormal?",
						    type:"POST",
					 		cache:false,
						    data:"merchant_no="+merchant_no+"&mobile_username="+mobile_username+"&merchant_name="+merchant_name+"&layout=no",
						    success: function(data){
							    	var ico="error.gif";
							    	var msg=data;
							    	if("SUCCESS"==data){
							    		ico="success.gif";
							    		msg = "商户状态已开启！";
							    	}
								   	var dialog = $.dialog({title: '提示',lock:true,content: msg,icon: ico,ok:null ,close:function(){
								   		$("#merQuery").submit();
								   		//location.href="${ctx}/mer/merQuery";
									}});
						     }
			        });
			 });
		}
		
		//冻结/解冻该商户的交易
		function freezeTrans(merchant_no, open_status, freezeType)
		{
			var confirmMsg = '确定要' + (freezeType === '1' ? '冻结' : '解冻')  + '该商户的交易？',
				sucMsg = '商户已被' + (freezeType === '1' ? '冻结' : '解冻');

			$.dialog.confirm(confirmMsg, function(){
				 $.ajax({
						    url: "merFreezeTrans?merchant_no="+merchant_no+"&freeze_status=" + freezeType + "&open_status=" + open_status + "&layout=no",
						    success: function(data){
							   	var dialog = $.dialog({title: '提示',lock:true,content: sucMsg,icon: 'success.gif',ok:null ,close:function(){
									//location.href="${ctx}/mer/merQuery";
									$("#merQuery").submit();
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
		 cache:false,
          success: function(data){
           var dialog = $.dialog({title: '提示',lock:true,content: '商户已为关闭状态',icon: 'success.gif',ok:null ,close:function(){
        	   $("#merQuery").submit();
         	//location.href="${ctx}/mer/merQuery";
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
						$("#merQuery").submit();
					}else if(ret == "ERROR"){
						var code = data.code;
						var errorMsg = "商户删除出错！";
						if(code != ""){
							errorMsg = "删除失败，已存在交易记录的商户不予删除！";
						}
					  	alert(errorMsg);
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
		
		//--------------lzj---------------------
		//锁定/解锁
		function lock(merchant_no){
			var lock=document.getElementById("lock").text;
			$.dialog.confirm('确定要'+lock+'该商户？', function(){
			     $.ajax({
			          url: '${ctx}/mer/merLock?merchant_no='+merchant_no+'&agent_lock='+lock,
			          error:function()
                      {
                          alert(lock+"出错");
                      },
			          success: function(){
			        	  if(lock=='锁定'){
			        		  $("#lock").text('解锁');
			        	  }else if(lock=='解锁'){
			        		  $("#lock").text('锁定');
			        	  }
			           	  alert("该商户已"+lock);
			           }
			         });
			   });
		}
		
		function exportExcel2(){
		   /*var action= $("form:first").attr("action"),
		   	   totalPage = parseInt('${list.totalPages}');
		   	   
		   //根据当前页查询的总页数来判断是否导出，必须先进行查询
		   if(totalPage <= 0){
		       $.dialog.alert("<pre>没有需要导出的数据！</pre>");
		   } else if(totalPage > 100){
		   	   $.dialog.alert("<pre>请选择一些必要的查询条件并进行查询，避免因导出数据过多导致的系统异常！</pre>");
		   } else {
		   	   $("form:first").attr("action","${ctx}/mer/merExport").submit();
			   $("form:first").attr("action",action);
		   }*/

			exportXls('${ctx}/mer/merExport', ${list.totalPages}, true);
		}
		
		function hlfAssemble(obj){
			obj.disabled = true;
			$.dialog.confirm('确定要绑定好乐付？', function(){
				 $.ajax({
					url: "${ctx}/mer/hlfAssembleACQMerchant",
					success: function(data){
						var dialog = $.dialog({title: '提示',lock:true,content: '绑定好乐付操作成功！',icon: "success.gif",ok:null ,close:function(){
							obj.disabled = false;
							$('form:first').submit();
						}});
					}
				});
			});
		}
		
		function assembleDetail(merchant_no){
			$.get('${ctx}/mer/getHlfAssembleFailReason?merchant_no='+merchant_no,
				function(data){
					var msg = !data || data === 'error' ? '系统出错啦！' : data.response_msg,
						dialog = $.dialog({title: '绑定失败详情',lock:true,content: msg,icon: "success.gif",ok:null ,close:null});
				}
			);
		}
		
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>商户查询</div>
   
   <form:form id="merQuery" action="${ctx}/mer/merQuery" method="post">
   <div style="position:absolute;margin-left:63px;margin-top:46px;">
         		<input name="agentInput" id="agentInput"  value="全部"  autocomplete="off"  style="position:absolute;top:1px;left:32px;height:10px;width: 230px !important;border: none;">
         	</div>
    <div id="search">
    	<div id="title">商户查询</div>
	      <ul>
  	      	<li><span>代理商名称：</span><u:select value="${params['agentNo']}"  stype="agent" sname="agentNo"  onlyThowParentAgent="true"  style="width: 260px;height: 24px"/></li>
	        <li><span style="width: 90px;">商户名称/编号：</span><input type="text"  value="${params['merchant']}" name="merchant"  style="width: 220px"/></li>
	      	<li><span>创建时间：</span>
			 	<input  type="text"  style="width:130px" readonly="readonly" name="createTimeBegin" value="${params['createTimeBegin']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 	~
			 	<input  type="text" style="width:130px" readonly="readonly" name="createTimeEnd" value="${params['createTimeEnd']}" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
			 </li>
	      </ul>
	      <ul>
			 <li><span>商户状态：</span> 
	         	<select style="padding:2px;width:87px" name="openStatus">
	         		<option value="-1" <c:out value="${params['openStatus'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['openStatus'] eq '1'?'selected':'' }"/>>正常</option>
	         		<option value="0" <c:out value="${params['openStatus'] eq '0'?'selected':'' }"/>>商户关闭</option>
	         		<option value="5" <c:out value="${params['openStatus'] eq '5'?'selected':'' }"/>>机具绑定</option>
	         		<option value="2" <c:out value="${params['openStatus'] eq '2'?'selected':'' }"/>>待审核</option>
	         		<option value="3" <c:out value="${params['openStatus'] eq '3'?'selected':'' }"/>>审核失败</option>
<!--	         		<option value="4" <c:out value="${params['openStatus'] eq '4'?'selected':'' }"/>>冻结</option>-->
	         		<option value="6" <c:out value="${params['openStatus'] eq '6'?'selected':'' }"/>>初审</option>
	         		<option value="8" <c:out value="${params['openStatus'] eq '8'?'selected':'' }"/>>等待复审</option>
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
			  <li><span style="width: 70px;">设备类型：</span> 
	         	<u:TableSelect sname="pos_type" style="padding:2px;width:87px" tablename="pos_type" fleldAsSelectValue="pos_type"  fleldAsSelectText="pos_type_name" value="${params['pos_type']}" otherOptions="needAll"/>
			 </li>
			  <li><span style="width: 70px;">手机号码：</span><input type="text"  style="width: 92px" value="${params['phone']}" name="phone" /></li>
			  <li><span style="width: 55px;">审核人：</span>
			  		<select style="width: 80px;height: 24px;vertical-align: top;" name="checker" id="checker">
			  			<option value='-1'>请选择</option>
						<c:forEach items="${checker}" var="item" varStatus="status">
							<option value="${item.real_name}"  <c:out value="${params['checker'] eq item.real_name ?'selected':'' }"/>>
								${item.real_name}
							</option>
						</c:forEach>
			  		</select> 
			  </li>
			 <li><span>开通方式：</span><select style="padding:2px;width:70px" name="open_type">
	         		<option value="-1" <c:out value="${params['open_type'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['open_type'] eq '1'?'selected':'' }"/>>手动</option>
	         		<option value="2" <c:out value="${params['open_type'] eq '2'?'selected':'' }"/>>自动</option>
	         	</select>
			 </li>
			 <li><span style="width: 80px;">持卡人姓名：</span><input type="text"  style="width: 118px" value="${params['account_name']}" name="account_name" /></li>
			 <li><span style="width: 75px;">所属省份：</span><select id="province" name="province"  style="width: 88px;height: 24px;vertical-align: top;" ></select></li>
			   <li><span  style="width: 70px;">所属市区：</span><select id="city" name="city" style="width: 87px;height: 24px;vertical-align: top;"></select></li>
	      	 <li><span style="width: 120px;">收单机构商户编号：</span><input type="text"  style="width: 192px"  value="${params['acq_merchant_no']}" name="acq_merchant_no" /></li>
	      	<li><span>同步情况：</span><select style="padding:2px;width:70px" name="send_hlf1">
	         		<option value="-1" <c:out value="${params['send_hlf1'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="1" <c:out value="${params['send_hlf1'] eq '1'?'selected':'' }"/>>已同步</option>
	         		<option value="0" <c:out value="${params['send_hlf1'] eq '0'?'selected':'' }"/>>未同步</option>
	         	</select>
			 </li>
			 <li><span>绑定情况：</span><select style="padding:2px;width:130px" name="send_hlf2">
	         		<option value="-1" <c:out value="${params['send_hlf2'] eq '-1'?'selected':'' }"/>>全部</option>
	         		<option value="0" <c:out value="${params['send_hlf2'] eq '0'?'selected':'' }"/>>已绑定</option>
	         		<option value="1" <c:out value="${params['send_hlf2'] eq '1'?'selected':'' }"/>>绑定失败</option>
	         		<option value="2" <c:out value="${params['send_hlf2'] eq '2'?'selected':'' }"/>>未绑定</option>
	         		<option value="3" <c:out value="${params['send_hlf2'] eq '3'?'selected':'' }"/>>审核失败</option>
	         	</select>
			 </li>
			 <li><span>身份证号：</span>
			 	<input type="text"  style="width:250px"  name="id_card_no"  id="id_card_no"  value="${params['id_card_no']}" />
			 </li>
	      </ul>
	      
	      <div class="clear"></div>
    </div>
    <div class="search_btn">
    	<input   class="button blue medium" type="submit" id="submitButton"  value="查询"/>
    	<input name="reset" class="button blue medium" type="reset" id="reset"  value="清空"/>
    	<input  id="exportExcel" class="button blue medium" type="button" onclick="exportExcel2()"  value="导出excel"/>
<!--    	<input  id="bindBtn" class="button blue medium" type="button" onclick="autoBind()"  value="自动绑定"/>-->
	<shiro:hasPermission name="MERCHANT_SYNCHRO_HLF">
		<input class="button blue medium" type="button" onclick="hlfAssemble(this)"  value="绑定好乐付"/>
	</shiro:hasPermission>
    </div>
    </form:form>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
        <tr><th width="3%">序号</th>
          <th  width="12%">代理商名称</th>
          <th width="10%">商户简称</th>
          <th  width="5%">商户状态</th>
          <th width="8%">创建时间</th>
          <th width="4%">开通方式</th>
          <th width="5%">同步好乐付</th>
          <th width="5%">绑定情况</th>
          <th width="17%">操作</th>
          <c:forEach items="${list.content}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td>${item.agent_name}</td>
	           <td>${item.merchant_short_name}</td>
	          <td >
	          <c:choose>
				  <c:when test="${item.open_status eq '1'}">正常</c:when>
				  <c:when test="${item.open_status eq '0'}">商户关闭</c:when> 
				 <c:when test="${item.open_status eq '2'}">待审核</c:when> 
				 <c:when test="${item.open_status eq '5'}">机具绑定</c:when> 
				 <c:when test="${item.open_status eq '4'}">冻结</c:when> 
				 <c:when test="${item.open_status eq '6'}">初审</c:when> 
				 <c:when test="${item.open_status eq '8'}">等待复审</c:when> 
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
	          	<c:choose>
				  <c:when test="${item.send_hlf != 0}"><span class="font_red"> 已同步</span></c:when>
				  <c:when test="${item.send_hlf == 0}">未同步</c:when>
				</c:choose> 
	          </td>
	          <td class="center">
	          	<c:choose>
  				  <c:when test="${item.send_hlf == 2000}">已绑定</c:when>
				  <c:when test="${item.send_hlf == 2001}">绑定失败</c:when>
  				  <c:when test="${item.send_hlf == 2002 || item.send_hlf == 200}">未绑定</c:when>
				  <c:when test="${item.send_hlf == -1}">审核失败</c:when>
				</c:choose> 
	          </td>
	          <td class="center">
	           <shiro:hasPermission name="COMMERCIAL_QUERY_DETAIL"><a href="javascript:showDetail(${item.id});" title="详情">详情</a> | </shiro:hasPermission>
	          <c:if test="${item.open_status eq '5'||item.open_status eq '0'}">
	            <shiro:hasPermission name="COMMERCIAL_SET_NORMAL"><a href="javascript:setNormal('${item.merchant_no}','${item.mobile_username}','${item.merchant_name}');" title="开启">开启</a> | </shiro:hasPermission>
	          </c:if>
	           <c:if test="${item.freeze_status eq '0'}">
	            <shiro:hasPermission name="COMMERCIAL_SET_FREEZE"><a href="javascript:freezeTrans(${item.merchant_no}, ${item.open_status}, '1');" title="冻结">冻结</a> | </shiro:hasPermission>
	          </c:if>
	          <c:if test="${item.freeze_status eq '1'}">
	            <shiro:hasPermission name="COMMERCIAL_SET_UNFREEZE"><a href="javascript:freezeTrans('${item.merchant_no}', ${item.open_status}, '0');" title="解冻">解冻</a> | </shiro:hasPermission>
	          </c:if>
              <c:if test="${item.open_status!='0'}">
                <shiro:hasPermission name="COMMERCIAL_SET_NORMAL"><a href="javascript:setClose(${item.merchant_no});" title="关闭">关闭</a> | </shiro:hasPermission>
              </c:if>
           
	           <shiro:hasPermission name="COMMERCIAL_QUERY_MODIFY"><a href="${ctx }/mer/merload?id=${item.id}" title="修改">修改</a></shiro:hasPermission>
	           <c:if test="${item.open_status eq '2'}">
				<%--<c:if test="${item.open_status eq '2' || item.open_status eq '3'}">--%>
	           		| <shiro:hasPermission name="MERCHANT_DEL"><a href="javascript:merDel(${item.id},${item.merchant_no})" title="删除">删除 </a></shiro:hasPermission>
	           </c:if>
	            <shiro:hasPermission name="COMMERCIAL_TEST">| <a href="javascript:testMerchant('${item.merchant_no}');" title="测试">测试</a></shiro:hasPermission>
	           
	            <shiro:hasPermission name="COMMERCIAL_RESET">
	              <c:choose>
				  <c:when test="${item.open_status eq '1'}">
				 	| <a href="javascript:changePassword('${item.merchant_no}');">重置</a>
				  </c:when>
			      </c:choose>
	            </shiro:hasPermission>
	            <shiro:hasPermission name="LOCK_AGENT"> 
	            <c:if test="${item.agent_lock=='0'}">
					|<a href="javascript:lock('${item.merchant_no}');" id="lock">解锁</a>
					<input type="text" id="agent_lock" name="agent_lock" style="display: none;" value="解锁">
				</c:if>
				<c:if test="${item.agent_lock=='1'}">
					|<a href="javascript:lock('${item.merchant_no}');" id="lock">锁定</a>
					<input type="text" id="agent_lock" name="agent_lock" style="display: none;" value="锁定">
				</c:if>
				</shiro:hasPermission>
				<c:if test="${item.send_hlf==0 && (item.open_status eq '1' || item.open_status eq '5') && !fn:contains(item.terminal_no, ';') && item.bag_settle eq '0'}">
				<shiro:hasPermission name="MERCHANT_SEND_HLF">| <a href="javascript:hlfSendMerchant(${item.id});" title="同步好乐付">同步好乐付</a>  </shiro:hasPermission>
				</c:if>
				<c:if test="${item.send_hlf==2001}">
				<shiro:hasPermission name="MERCHANT_SEND_HLF_SHOW">| <a href="javascript:assembleDetail('${item.merchant_no}');" title="绑定详情">绑定详情</a>  </shiro:hasPermission>
				</c:if>
	            </td>
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
