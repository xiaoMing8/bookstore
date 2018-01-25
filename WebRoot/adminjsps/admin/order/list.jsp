<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>订单列表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<script type="text/javascript" src="../../js/jquery-1.11.3.js" ></script>
<style type="text/css">
	* {
		font-size: 11pt;
	}
	div {
		border: solid 2px gray;
		width: 75px;
		height: 75px;
		text-align: center;
	}
	li {
		margin: 10px;
	}
	

</style>
  </head>
  <body style="background: rgb(254,238,189);">
<h1>我的订单</h1>

<c:if test="${empty orderList }">
	<span style="color:red;font-size:30px">暂时没有相关订单</span> 
</c:if>


<table border="1" width="100%" cellspacing="0" background="black">
<c:forEach var="order" items="${orderList }">
	<tr bgcolor="rgb(78,78,78)" bordercolor="rgb(78,78,78)" style="color: white;">
		<td colspan="6">
		
			订单编号：${order.oid }　成交时间：<fmt:formatDate value="${order.ordertime }" pattern="yyyy-MM-dd HH:mm:ss" />    
		     　金额：<font color="red"><b>${order.total }</b></font>
			<c:choose>
				<c:when test="${order.state eq 1 }">未付款</c:when>
				<c:when test="${order.state eq 2 }">
					<a href="<c:url value='/admin/AdminOrderServlet?method=deliver&oid=${order.oid }'/>">发货</a>
				</c:when>
				<c:when test="${order.state eq 3 }">等待买家收货</c:when>
				<c:when test="${order.state eq 4 }">交易完成(买家已收货)</c:when>
			</c:choose>
		</td>
	</tr>
	<c:forEach var="orderItem" items="${order.orderItemList }">
	<tr bordercolor="rgb(78,78,78)" align="center">
		<td width="15%">
			<div><img src="<c:url value='/${orderItem.book.image }'/>" height="75"/></div>
		</td>
		<td>书名：${orderItem.book.bname }</td>
		<td>单价：${orderItem.book.price }元</td>
		<td>作者：${orderItem.book.author }</td>
		<td>数量：${orderItem.count }</td>
		<td>小计：${orderItem.subtotal }元</td>
	</tr>
	</c:forEach>
</c:forEach>
</table>
  </body>
</html>
