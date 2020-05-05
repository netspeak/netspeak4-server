<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- http://www.informit.com/articles/article.aspx?p=30946&seqNum=7 -->

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
<title>Netspeak Dashboard</title>
<style>
<!--
body {
	font-family: Verdana, Geneva, sans-serif;
	font-size: smaller;
}

pre {
	font-size: medium;
}

table {
	empty-cells: show;
	width: 100%;
}

th {
	background-color: gray;
	border: 1px solid gray;
	text-align: left;
	padding: 5px;
	color: white;
}

td {
	border: 1px dotted gray;
	vertical-align: top;
	text-align: left;
	padding: 0 5px;
}

.redbg {
	background-color: #cb0c29;
	border: 1px solid #cb0c29;
}
-->
</style>
</head>
<body>
<table>
	<tr>
		<th class="redbg" width="20%">Key</th>
		<th class="redbg">Value</th>
	</tr>
	<tr>
		<td><pre>Server Name</pre></td>
		<td><pre><c:out value="${pageContext.request.serverName}" /></pre></td>
	</tr>
	<tr>
		<td><pre>Servlet Container</pre></td>
		<td><pre><c:out value="${pageContext.servletContext.serverInfo}" /></pre></td>
	</tr>
	<tr>
		<td><pre>Servlet API Version</pre></td>
		<td><pre><c:out value="${pageContext.servletContext.majorVersion}.${pageContext.servletContext.minorVersion}" /></pre></td>
	</tr>
	<tr>
		<th colspan="2">Servlet Configuration (web.xml)</th>
	</tr>
	<c:forEach var="entry" items="${requestScope.initParameters}">
	<tr>
		<td><pre>${entry.key}</pre></td>
		<td><pre><c:out value="${entry.value}" escapeXml="true" /></pre></td>
	</tr>
	</c:forEach>
	<tr>
		<th colspan="2">Netspeak Properties</th>
	</tr>
	<c:forEach var="entry" items="${requestScope.netspeakProperties}">
	<tr>
		<td><pre>${entry.key}</pre></td>
		<td><pre><c:out value="${entry.value}" escapeXml="true" /></pre></td>
	</tr>
	</c:forEach>
</table>
</body>
</html>
