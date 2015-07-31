<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
<script type="text/javascript">
$(function(){
	
	/* 校验进入界面是否需要加载设备型号信息 */
	var defPosType = "${params.pos_type}";
	var defModel = "${params.pos_model_name}";
	if(defPosType == ""){
		defPosType = "-1";
	}
	if(defModel == ""){
		defModel = "-1";
	}
	
	/* 默认加载设备型号信息 */
	searchPosModel(defPosType, defModel);
	
	/* 基于设备类型加载所属型号 */
	$("#pos_type").change(function(){
		var pos_type = $("#pos_type").val();
		searchPosModel(pos_type,"-1");
	});
	
	/* 清空 */
	$("#flushSearchCondition").click(function(){
		$("#pos_type option[value=-1]").attr("selected", true); 
		searchPosModel("-1","-1");
		$("#start_create_time").val("");
		$("#end_create_time").val("");
		$("#create_person").val("");
	});
	
	/* 传入pos_type 加载  对应的型号列表至SELECT元素,传入defSelectModel 自动选中默认选项*/
	function searchPosModel(pos_type, defSelectModel){
		$("#pos_model_name").empty();
		$("<option value='-1'>--全部--</option>").appendTo("#pos_model_name");
		if(pos_type != "-1"){
				$.ajax({
					type:"post",
					url:"${ctx}/ter/searchPosModel",
					data:{"pos_type":pos_type},
					async:false,
					dataType: 'json',
				  	success: function(json){
					  for(var i=0;i<json.length;i++){
						  if(defSelectModel == json[i].pos_model && defSelectModel != "-1"){
							  $("<option value="+json[i].pos_model+" selected>"+json[i].pos_model_name+"</option>").appendTo("#pos_model_name");
						  }else{
							  $("<option value="+json[i].pos_model+">"+json[i].pos_model_name+"</option>").appendTo("#pos_model_name");
						  }
					  }
				  }
			});
		}
	}
});

</script>
</head>
<body>
	<div id="content">
		<div id="nav">
			<img class="left" src="${ctx}/images/home.gif" />当前位置：机具管理&gt;设备管理
		</div>
		<form:form id="searchPos" action="${ctx}/ter/searchPos" method="post">
			<div id="search">
				<div id="title">设备管理</div>
				<ul>
					<li><span style="width:65px;">设备类型：</span> 
							<u:TableSelect	sid="pos_type" sname="pos_type" style="padding:2px;width:100px" tablename="pos_type"  fleldAsSelectValue="pos_type" fleldAsSelectText="pos_type_name"
							 value="${params.pos_type}"	otherOptions="needAll" />
					</li>
					<li><span>型号名称：</span>
							<select id="pos_model_name"  name="pos_model_name" style="padding:2px;width:100px">
							</select>
					</li>
					<%--<li><span style="width:65px;">型号编码：</span>
					<input type="text"	value="${params['pos_model']}" name="pos_model"	style="width: 90px" />
					</li>
					--%><li><span>创建时间：</span> 
							<input type="text" value="${params['start_create_time']}" name="start_create_time"  id="start_create_time"  style="width: 140px" onClick="WdatePicker({dateFmt:'yyyy-MM-dd 00:00:00',readOnly:true})"  />
							 ~ 
							 <input type="text" value="${params['end_create_time']}" name="end_create_time"  id="end_create_time"  style="width: 140px" onClick="WdatePicker({dateFmt:'yyyy-MM-dd 23:59:59',readOnly:true})"  />
					</li>
					<li><span style="width:50px;">创建人：</span>
							<input type="text" value="${params['create_person']}" name="create_person"  id="create_person"	style="width: 100px" maxlength="7"/>
					</li>
				</ul>
				<div class="clear"></div>
			</div>
			<div class="search_btn">
				<input class="button blue medium" type="submit" id="submitButton"	value="查询" />
				<input name="reset" class="button blue medium" type="button"	id="flushSearchCondition" value="清空" />
				<shiro:hasPermission name="POS_TYPE_ADD">
					<input class="button blue medium" type="button" onclick="location.href='${ctx}/ter/addPos'" value="新增设备" />
				</shiro:hasPermission>
				<shiro:hasPermission name="POS_MODEL_ADD">
					<input class="button blue medium" type="button" onclick="location.href='${ctx}/ter/addPosModel'" value="新增机型" />
				</shiro:hasPermission>
				<input class="button blue medium" type="button" onclick="location.href='${ctx}/ter/terQuery'"	value="返回" />
			</div>
		</form:form>
		<div id="tbdata" class="tbdata">
			<table style="width:100%;cellspacing:0" class="t2">
				<thead>
					<tr>
						<th width="15">序号</th>
						<th width="60">设备名称</th>
						<th width="40">设备代码</th>
						<th width="50">型号名称</th>
						<th width="70">型号编码</th>
						<th width="60">应用范围</th>
						<th width="100">创建时间</th>
						<th width="30">创建人</th>
					</tr>
					<c:forEach items="${list.content}" var="item" varStatus="status">
						<c:out value=""></c:out>
						<tr class="${status.count % 2 == 0 ? 'a1' : ''}" align="left">
							<td class="center">${status.count}</td>
							<td>${item.pos_type_name}</td>
							<td>${item.pos_type}</td>
							<td>${item.pos_model_name}</td>
							<td>${item.pos_model}</td>
							<td>
								<c:if test="${item.pos_status eq '1'}">默认</c:if>
								<c:if test="${item.pos_status eq '0'}">进件</c:if>
								<c:if test="${item.pos_status eq '2'}">交易查询</c:if>
								<c:if test="${item.pos_status eq '3'}">机具管理</c:if>
							</td>
							<td><fmt:formatDate value="${item.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both" /></td>
							<td>${item.create_person}</td>
						</tr>
					</c:forEach>
			</table>
		</div>
		<div id="page">
			<pagebar:pagebar total="${list.totalPages}"	current="${list.number + 1}" anchor="_table" />
		</div>
	</div>
</body>
