<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>菜单</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="<c:url value='/menu/mymenu.js'/>"></script>
	<link rel="stylesheet" href="<c:url value='/menu/mymenu.css'/>" type="text/css" media="all">
<script language="javascript">
/*
 * bar1：必须与对象名相同！
   2210网络图书商城：大标题
 */
var bar1 = new Q6MenuBar("bar1", "2210网络图书商城");
function load() {	
	//设置本色方案！一共4种：0、1、2、3
	bar1.colorStyle = 2;
	
	//指定图片目录
	bar1.config.imgDir = "<c:url value='/menu/img/'/>";

	
	//菜单之间是否相互排斥
	bar1.config.radioButton=false;
	
	/*
	分类管理：指定要添加的菜单名称（如果这个名称的菜单已经存在，不会重复添加）
	查看分类：指定要添加的菜单项名称
	<c:url value='/adminjsps/admin/category/list.jsp'/>：指定菜单项时要请求的地址
	body：结果的显示框架页名称
	*/
	bar1.add("分类管理", "查看分类", "<c:url value='/admin/AdminCategoryServlet?method=findAll'/>", "body");
	bar1.add("分类管理", "添加分类", "<c:url value='/adminjsps/admin/category/add.jsp'/>", "body");

	bar1.add("图书管理", "查看图书", "<c:url value='/admin/AdminBookServlet?method=findAll'/>", "body");
	bar1.add("图书管理", "添加图书", "<c:url value='/admin/AdminBookServlet?method=modifyPreLoadCategory'/>", "body");

	bar1.add("订单管理", "所有订单", "<c:url value='/admin/AdminOrderServlet?method=findAllOrder'/>", "body");
	bar1.add("订单管理", "未付款订单", "<c:url value='/admin/AdminOrderServlet?method=find&state=1'/>", "body");
	bar1.add("订单管理", "已付款订单", "<c:url value='/admin/AdminOrderServlet?method=find&state=2'/>", "body");
	bar1.add("订单管理", "未收货订单", "<c:url value='/admin/AdminOrderServlet?method=find&state=3'/>", "body");
	bar1.add("订单管理", "已完成订单", "<c:url value='/admin/AdminOrderServlet?method=find&state=4'/>", "body");
	// 获取div元素
	var d = document.getElementById("menu");
	// 把菜单对象转换成字符串，赋给<div>元素做内容
	d.innerHTML = bar1.toString();
}
</script>

</head>

<body onload="load()" style="margin: 0px; background: rgb(254,238,189);">
<div id="menu"></div>

</body>
</html>
