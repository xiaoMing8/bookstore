<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>修改图书</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
<style type="text/css">
	body {
		font-size: 10pt;
		background: rgb(254,238,189);
	}
	div {
		margin:20px;
		border: solid 2px gray;
		width: 150px;
		height: 150px;
		text-align: center;
	}
	li {
		margin: 10px;
	}
</style>


  </head>
  
  <body>
  <div>
    <img src="<c:url value='/${book.image }'/>" border="0"/>
  </div>
    <form action="<c:url value='/admin/AdminUploadBookServlet' />" method="post" enctype="multipart/form-data">
    	图书名称：<input style="width: 150px; height: 20px;" type="text" name="bname" value="${book.bname }" /><br/>
	         图书图片：<input style="width: 150px; height: 20px;" type="file" name="image" /><br/>
	         图书单价：<input style="width: 150px; height: 20px;" type="text" name="price" value="${book.price }" /><br/>
	         图书作者：<input style="width: 150px; height: 20px;" type="text" name="author" value="${book.author }"  /><br/>
    	图书分类：<select style="width: 150px; height: 20px;" name="cid">
<c:forEach items="${categoryList }" var="category">
			<c:choose>
				<c:when test="${category.cid eq book.category.cid }">
					<option value="${category.cid }" selected="selected">${category.cname }</option>
				</c:when>
				<c:otherwise>
					<option value="${category.cid }">${category.cname }</option>
				</c:otherwise>
			</c:choose>		
</c:forEach>
    		</select><br/>
  	<input type="hidden" name="modifyImage" value="${book.image }"/>
  	<input type="hidden" name="bid" value="${book.bid }"/>
  	<input type="submit" value="确认修改" />
  </form>
  </body>
</html>
