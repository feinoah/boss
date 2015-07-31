<?xml version="1.0" encoding="UTF-8"?>
<%@page pageEncoding="utf8"%>
<%@include file="/tag.jsp"%>
<head>
	<%@ include file="/WEB-INF/uploadJs.jsp" %>
	<script type="text/javascript" src="${ ctx}/scripts/listPage.js"></script>
	<script type="text/javascript" src="${ ctx}/scripts/jqueryform.js"></script>
	<style type="text/css">
		#merUpdate
		{
			padding:10px;
		}
	
		
		#merUpdate ul li
		{
			margin:0;
			padding:0;
			display:block;
			float:left;
			width:250px;
			height:32px;
			line-height:32px;
		}
		
		#merUpdate ul li.column2
		{
			width:500px;
		}
		
		#merUpdate ul li.column3
		{
			width:750px;
		}
		
		#merUpdate ul li select
		{
			width:128px;
		}
		
		#merUpdate ul li label
		{
			display:-moz-inline-box;
			display:inline-block;
			width:90px;
		}
		
		#merUpdate ul li label.must		
		{
			display:-moz-inline-box;
			display:inline-block;
			width:5px;
			text-align:center;
			color:red;
		}
		
		#merUpdate ul li label.longLabel
		{
			width:170px;
		}
		
		#merUpdate ul li .area
		{
			width:75px;
		}
		
		
		
		#merUpdate ul li.long
		{
			width:440px;
		}
		
		
		#merUpdate div.subject
		{
			font-size:12px;
			font-weight:bold;
			marigin:20px 4px;
			
		}
		
		#attachment_fileUploader {
			vertical-align:middle;
			margin-left:10px;
		}
	</style>
	<script type="text/javascript" src="${ctx}/scripts/provinceCity.js" ></script>
	<script type="text/javascript">
 
	</script>
</head>
<body>
  <div id="content">
  
    <div id="nav"><img class="left" src="${ctx}/images/home.gif"/>当前位置：商户管理>商户详情</div>
	<form:form id="merUpdate"    >
    <div  class="item">
    	<div class="title">收单机构请求日志信息</div>
    	<ul>
    		<li style="width:250px;"><label style="width:120px;">收单机构商户编号：</label> ${paramsResq.acq_merchant_no}</li>
    		<li style="width:250px;"><label style="width:120px;">收单机构商户名称：</label> ${paramsResq.acq_merchant_name}</li>
			<li style="width:250px;"><label style="width:120px;">收单机构英文简称：</label> ${paramsResq.acq_enname}</li>
			<li style="width:250px;"><label style="width:120px;">交易类型：</label> ${paramsResq.mti}</li>
			<li style="width:250px;"><label style="width:120px;">3域处理码：</label> ${paramsResq.trans_code} </li>
			<li style="width:250px;"><label style="width:120px;">批次号：</label> ${paramsResq.batch_no}</li>
			<li style="width:250px;"><label style="width:120px;">流水号：</label> ${paramsResq.serial_no} </li>
			<li style="width:250px;"><label style="width:120px;">终端号：</label> ${paramsResq.acq_terminal_no}</li>
			<li style="width:250px;"><label style="width:120px;">创建时间：</label> <fmt:formatDate value="${paramsResq.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/></li>
			
			<li style="width:750px;height:430px;" ><label >请求报文：<textarea name="details" cols="95" style="border:0px;" 
                        rows="25" wrap="off" readonly="true"><c:out value="${paramsResq.msg}"></c:out>
                    </textarea></label></li>
			 
    	</ul>
    	<div class="clear"></div>
		<br/>
    	<div class="title">收单机构响应日志信息</div>
    	<ul>
    		<li style="width:250px;"><label style="width:120px;">收单机构英文简称：</label> ${paramsResp.acq_enname}</li>
			<li style="width:250px;"><label style="width:120px;">交易类型：</label> ${paramsResp.mti}</li>
<!--			<li style="width:250px;"><label style="width:120px;">3域处理码：</label>${paramsResp.trans_code} </li>-->
<!--			<li style="width:250px;"><label style="width:120px;">批次号：</label>${paramsResp.batch_no}</li>-->
<!--			<li style="width:250px;"><label style="width:120px;">流水号：</label>${paramsResp.serial_no} </li>-->
<!--			<li style="width:250px;"><label style="width:120px;">商户编号：</label>${paramsResp.acq_merchant_no} </li>-->
<!--			<li style="width:250px;"><label style="width:120px;">终端号：</label>${paramsResp.acq_terminal_no}</li>-->
			<li style="width:250px;"><label style="width:120px;">响应代码：</label> ${paramsResp.response_code} </li>
			<li style="width:250px;"><label style="width:120px;">创建时间：</label> <fmt:formatDate value="${paramsResp.create_time}" pattern="yyyy-MM-dd HH:mm:ss" type="both"/> </li>
			<li style="width:750px;height:430px;" ><label >请求报文：<textarea name="details" cols="95" style="border:0px;" 
                        rows="25" wrap="off" readonly="true"><c:out value="${paramsResp.msg}"></c:out>
                    </textarea></label></li>
    	</ul>
    	<div class="clear"></div>
		<br/>
    	
    </form:form>
   <div style="display:none">
   	<input type="text" id="flag" value="${flag}"/>
   	<input type="text" id="errorMessage" value="${errorMessage}"/>
   </div>
   
  </div>
</body>
