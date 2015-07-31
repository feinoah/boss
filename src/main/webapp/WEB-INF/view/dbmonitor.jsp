<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据库连接池监控</title>
<style type="text/css">
table {
	border-collapse: collapse;
	border: none;
	width: 600px;
}

td {
	border: solid #000 1px;
}
</style>
</head>
<body>
	<table>
		<tr>
			<td>数据源ID</td>
			<td>活动连接数</td>
			<td>空闲连接数</td>
			<td>最大连接数</td>
		</tr>
		<tr>
			<td>mainDataSource</td>
			<td>${mainDataSource.numActive }</td>
			<td>${mainDataSource.numIdle}</td>
			<td>${mainDataSource.maxActive }</td>
		</tr>
		<tr>
			<td>bagDataSource</td>
			<td>${bagDataSource.numActive }</td>
			<td>${bagDataSource.numIdle}</td>
			<td>${bagDataSource.maxActive }</td>
		</tr>
		<tr>
			<td>readOnlyDataSource</td>
			<td>${readOnlyDataSource.numActive }</td>
			<td>${readOnlyDataSource.numIdle}</td>
			<td>${readOnlyDataSource.maxActive }</td>
		</tr>
	
	</table>
</body>
</html>