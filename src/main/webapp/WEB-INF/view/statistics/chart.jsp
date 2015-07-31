<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/tag.jsp"%>

<%

if ((Long)session.getAttribute("power")==null){
		response.sendRedirect(request.getContextPath()+"/");
  }else	if((Long)session.getAttribute("power")==16){
	response.sendRedirect(request.getContextPath()+"/sale/trans");
	}
		
	

%>

<head>
<script language="JavaScript"
	src="${ ctx}/FusionCharts/JS/FusionCharts.js"></script>
<meta http-equiv="content-type" content="text/html;charset=UTF-8">
</head>
<body>
<body>
	<div id="content">
		<div class="item">
			<div class="title">最近两周日交易量统计</div>
			<div id="chartDiv" align="center"></div>
			<script type="text/javascript">
				var myChart1 = new FusionCharts(
						"${ ctx}/FusionCharts/Charts/Line.swf", "myChartId",
						"827", "400");
				myChart1.setDataURL("${ ctx}/sta/dayTotal");
				myChart1.render("chartDiv");
			</script>

			<div class="title" style="margin-top:10px">代理商今日交易量统计</div>
			<div id="chartDiv1" align="center"></div>
			<script type="text/javascript">
				var myChart1 = new FusionCharts(
						"${ ctx}/FusionCharts/Charts/Pie2D.swf", "myChartId",
						"827", "400");
				myChart1.setDataURL("${ ctx}/sta/agentTotal");
				myChart1.render("chartDiv1");
			</script>
		</div>
	</div>
	</div>
</body>
</body>
