<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>登录</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<script type="text/javascript">
		function change(){
			var ele = document.getElementById("vCode");
			ele.src = "<c:url value='/VerifyCodeServlet'/>?xxx=" + new Date().getTime();
		}
	
	</script>
  </head>
  
  <body>
  <h1>登录</h1>
<p style="color: red; font-weight: 900">${msg }</p>
<form action="<c:url value='/UserServlet'/>" method="post">
		   <input type="hidden" name="method" value="login" />	
	用户名：<input type="text" name="username" id="username" value=""/><br/>
	密　码：<input type="password" name="password" id="password" /><br/>
<div style="overflow:hidden">
	<div style="line-height: 30px; margin-right: 60px;float: left;">
		验证码：<input type="text"  name="verifyCode" id="verifyCode" />
	</div>
	<div style="float: left;">
		<img id="vCode" src="<c:url value='/VerifyCodeServlet'/>" style="margin-top: 2px; border:1px solid black"  />
		<a id="image" href="javascript:change()" style="text-decoration: none;" >换一张</a> <br/>
	</div>
</div>
	<input type="submit" value="登录"/>
</form>
  </body>
</html>
