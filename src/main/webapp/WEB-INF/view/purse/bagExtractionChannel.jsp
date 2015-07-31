<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<script type="text/javascript">
	function showDetail(id)
	{
		$.dialog({title:'通道详情',width: 700,height:300,resize: false,lock: true,max:false,content: 'url:bagExtractionChannelDetail?id='+id+'&layout=no'});
	}

function channelSet(id){
	window.freezeDialog = $.dialog({
	title : '设置',
	width : 300,
	height : 250,
	resize : false,
	lock : true,
	max : false,
	content : 'url:${ctx}/purse/bagExtractionChannelSet?id='+id+'&layout=no',
	close:function(){
			window.location.reload();
		}
	});
}
	
	function autoCheck(){
		var autoCheck = $("#autoCheck").val();
		/* if(autoCheckState==1){
			window.freezeDialog = $.dialog({
			title : '自动审核',
			width : 300,
			height : 150,
			resize : false,
			lock : true,
			max : false,
			content : 'url:${ctx}/purse/autoCheckSet?id='+id+'&layout=no',
			close:function(){
					window.location.reload();
				}
			});
		}
		if(autoCheckState==0){
			if(!confirm("确认要关闭自动审核吗？"))
			{
				return;
			}
			$.ajax({
		          url: '${ctx}/purse/updateChannelAutoCheck',
		          type:"POST",
				  data:{'id':id,'auto_check_state':autoCheckState},
		          error:function(){
                   alert("提交出错");
               },
		          success: function(data){
		        	  alert(data);
		        	  window.location.reload();
		          }
		     });
		} */
		var Status;
		if(autoCheck==1){
			Status = "开启";
		}else if(autoCheck==0){
			Status = "关闭";
		}else{
			alert("请选择正确的状态");
			return;
		}
		if(!confirm("确认要"+Status+"自动审核吗？")){
			return;
		}
		$.ajax({
	          url: '${ctx}/purse/updateChannelAutoCheck',
	          type:"POST",
			  data:{'auto_check_state':autoCheck},
	          error:function(){
               alert("提交出错");
           },
	          success: function(data){
	        	  alert(data);
	        	  window.location.reload();
	          }
	     });
	}
	
	function realTimeCheckStatusChange(){
		var realTimeState = $("#realTimeCheckStatus").val();
		var Status;
		if(realTimeState==1){
			Status = "开启";
		}else if(realTimeState==0){
			Status = "关闭";
		}else{
			alert("请选择正确的状态");
			return;
		}
		if(!confirm("确认要"+Status+"秒出账吗？")){
			return;
		}
		$.ajax({
	          url: '${ctx}/purse/realTimeCheckStatusChange',
	          type:"POST",
			  data:{'real_time_state':realTimeState},
	          error:function(){
               alert("提交出错");
           },
	          success: function(data){
	        	  alert(data);
	        	  window.location.reload();
	          }
	     });
	}
	
	//修改预警手机号
	function modifyWarnPhone(){
		var MOBILE_REG = /^1[3|4|5|7|8][0-9]\d{8}$/;//手机号
		var warnMobileNo = $.trim($("#warnMobileNo").val());
		if (!warnMobileNo.match(MOBILE_REG)) {
			alert("请填写正确的手机号码");
			return ;
		}
		$.ajax({
	          url: '${ctx}/purse/updateWarnPhone',
	          type:"POST",
			  data:{'warnMobileNo':warnMobileNo},
	          error:function(){
	             alert("提交出错");
	          },
	          success: function(data){
	        	  alert(data);
	        	  window.location.reload();
	          }
	     });
	}
	
	function channelStateFun(id,channelState){
		var isChannelUsed = "${isChannelUsed}";
		var msg;
		if(channelState==1){
			if(isChannelUsed==1){
				alert("请先关闭其它通道");
				return;
			}
			msg = "开启";
		}
		if(channelState==0){
			msg = "关闭";
		}
		if(!confirm("确认要"+msg+"该通道吗？")){
			return;
		}
		$.ajax({
	          url: '${ctx}/purse/updateChannelState',
	          type:"POST",
			  data:{'id':id,'channelState':channelState},
	          error:function(){
                  alert("提交出错");
              },
	          success: function(data){
	        	  alert(data);
	        	  window.location.reload();
	          }
	     });
		
	}
	</script>
</head>
<body>
  <div id="content">
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：手机钱包&gt;钱包提现管理</div>
   
    <div id="search">
    	<!-- <div id="title">钱包提现管理</div> -->
      <ul>
        <li><span>自审开关：</span>
        <select style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="autoCheck">
        	<option value="1" <c:if test="${auto_check_state eq 1}">selected='selected'</c:if>>开启</option>
        	<option value="0" <c:if test="${auto_check_state eq 0}">selected='selected'</c:if>>关闭</option>
        </select>
        <input class="button blue medium" type="button"  value="确定" onclick="javascript:autoCheck();"/>
        </li>
        <br />
        <br />
        <li><span>秒出账开关：</span>
        <select style="width:120px;padding: 3px;border: 1px solid #A4A4A4;" id="realTimeCheckStatus">
        	<option value="1" <c:if test="${real_time_state eq 1}">selected='selected'</c:if>>开启</option>
        	<option value="0" <c:if test="${real_time_state eq 0}">selected='selected'</c:if>>关闭</option>
        </select>
        <input class="button blue medium" type="button"  value="确定" onclick="javascript:realTimeCheckStatusChange();"/>
        </li>
        <br />
        <br />
        <li><span>报警手机号 ：</span>
        <input type="text" style="width: 112px;" value="${warn_mobile_no}" id="warnMobileNo" />
        <input class="button blue medium" type="button"  value="确定" onclick="javascript:modifyWarnPhone();"/>
        </li>
      </ul>
      <div class="clear"></div>
    </div>
    <a name="_table"></a>
    <div class="tbdata">
      <table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="4%">序号</th>
          <th width="12%">代付通道</th>
          <th width="8%">通道状态</th>       
          <th width="13%">通道费率(%)</th>
          <th width="16%">剩余额度(元)</th>
          <!-- <th width="8%">自审状态</th>
          <th width="12%">审核时长(分)</th> -->
          <th width="17%">操作</th>
        </tr>
        </thead>
          <c:forEach items="${list}" var="item" varStatus="status">
	        <tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
	          <td class="center"><span class="center">${status.count}</span></td>
	          <td class="center"  style="word-break: break-all ;">${item.channel_name}</td>
	          <td class="center"  style="word-break: break-all ;">
	          <c:if test="${item.channel_state eq 1}">正常</c:if>
			  <c:if test="${item.channel_state eq 0}">关闭</c:if>
	          </td>
	          <td align="center" style="word-break: break-all ;">${item.channel_fee}</td>
	          <td align="center" style="word-break: break-all ;">${item.remain_amount}</td>
	          <%-- <td align="center" style="word-break: break-all ;">
	          <c:if test="${item.auto_check_state eq 1}">开启</c:if>
			  <c:if test="${item.auto_check_state eq 0}">关闭</c:if>
			  </td>
	          <td align="center" style="word-break: break-all ;">${item.check_rate}</td> --%>
			  <td align="center">
			  <a href="javascript:showDetail(${item.id});">详情</a> |
			  <a href="javascript:channelSet(${item.id});">设置</a> |
			  <%-- <c:if test="${item.auto_check_state eq 1}">
			  	<a href="javascript:autoCheck(${item.id},0);">关闭自审</a> |
			  </c:if>
			  <c:if test="${item.auto_check_state eq 0}">
			  	<a href="javascript:autoCheck(${item.id},1);">开启自审</a> |
			  </c:if> --%>
			  <c:if test="${item.channel_state eq 1}">
			  	<a href="javascript:channelStateFun(${item.id},0);">关闭通道</a>
			  </c:if>
			  <c:if test="${item.channel_state eq 0}">
			  	<a href="javascript:channelStateFun(${item.id},1);">开启通道</a>
			  </c:if>
			 </td>
			</tr>
          </c:forEach>
      </table>
    </div>
    </br>
    </br>
    <div style="padding: 6px 6px 6px 13px;background: #DFE9F0 none repeat scroll 0% 0%;margin-bottom: 10px;font-weight: bold;">
    其它信息修改历史记录
    </div>
	<div class="tbdata" >
  		<table width="100%" cellspacing="0" class="t2">
        <thead>
          <tr>
          <th width="10%">序号</th>
          <th width="15%" >修改人</th>  
          <th width="30%">修改内容</th>     
          <th width="15%">修改时间</th>
        </tr>
        </thead>
      		<c:forEach items="${logList}" var="item" varStatus="status">
      		<tr  class="${status.count % 2 == 0 ? 'a1' : ''}">
       			<td class="center"><span class="center">${status.count}</span></td>
       			<td class="center"  style="word-break: break-all ;">${item.operater}</td>
		        <td class="center"  style="word-break: break-all ;">${item.content}</td>
		        <td class="center"  style="word-break: break-all ;">${item.create_time}</td>
	    	</tr>
       		</c:forEach>
      	</table>
    </div>
  </div>
  <script type="text/javascript" src="${ ctx}/scripts/throttle.js"></script>
</body>
