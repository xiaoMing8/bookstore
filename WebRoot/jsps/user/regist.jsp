<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>注册</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<script type="text/javascript" src="../../js/jquery-1.11.3.js" ></script>
	
<script type="text/javascript">
		
		var usernameFlag = false;
		var emailFlag = false;
		var passwordFlag = false;
		var verifyCodeFlag = false;
	

	//加载文档
	$(function() {
		//一.为输入用户名添加监听事件
		$("#username").blur(function() {
			
			
			//1.判断输入的用户名格式是否正确
			var username = $("#username").val();
			//1.1判断用户名是否为空	
			if(username == "" || username == null){
					$("#usernameInfor").css("color","red");
					$("#usernameInfor").html("用户名不能为空");
					return;
			}else{
					$("#usernameInfor").html("");
			}
			//1.2判断用户名长度是否合适
			if(username.length>0){
			if(username.length>6||username.length<2){
					$("#usernameInfor").css("color","red");
					$("#usernameInfor").html("用户名长度必须在2-6之间");
					return;
			}else{
					$("#usernameInfor").html("");
			}	
			}
			//2.用ajax异步请求查看该用户名是否已经注册!
			$.ajax({
			url: "<c:url value='/UserServlet'/>",
			async: true,
			type: "post",
			dataType: "json",
			data: {
				"username":username,
				"method":"checkUsernameByAjax",
				},
			success: function(data){
				var flag = data.flag;
				if(flag){//用户名存在
					$("#usernameInfor").css("color","red");
					$("#usernameInfor").html("该用户名已存在,请重新注册");
				}else{
					$("#usernameInfor").html("");
					usernameFlag = true;
				}
				},
			error: function(){
					alert("请求失败");
				}
			});			
		});
		
		//二.为密码添加监听事件
		$("#password").blur(function(){
			
			
			var password = $("#password").val();
			//1.0判断密码是否为空
			if(password == "" || password == null){
				$("#passwordInfor").css("color","red");
				$("#passwordInfor").html("密码不能为空");
				return;
			}else{
				$("#passwordInfor").html("");
			}
			//1.1判断密码长度是否正确
			if(password.length>0){
				if(password.length<6||password.length>10){
					$("#passwordInfor").css("color","red");
					$("#passwordInfor").html("密码长度必须在6-10之间");
					
				}else{
					$("#passwordInfor").html("");
					passwordFlag = true;
				}
		}
			
		});
		//三.为邮箱添加监听事件
		$("#email").blur(function(){
						
		var email = $("#email").val();
		//检查邮箱格式是否正确
		//1.1判断邮箱是否为空
		if(email ==""||email ==null){
			$("#emailInfor").css("color","red");
			$("#emailInfor").html("邮箱不能为空");
			return;
		}
		if(email.length>0){
			//1.2判断邮箱格式
			var reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
			if(!reg.test(email)){
				$("#emailInfor").css("color","red");
				$("#emailInfor").html("邮箱格式不正确");
				return;
			}else{
				$("#emailInfor").html("");
				$.ajax({
						url: "<c:url value='/UserServlet' />",
						async: true,
						type: "post",
						dataType: "json",
						data: {
							"email":email,
							"method":"checkEmailByAjax",
							},
						success: function(data){
							var flag = data.flag;
							if(flag){//邮箱被注册
								$("#emailInfor").css("color","red");
								$("#emailInfor").html("该邮箱已被注册");								
							}else{
								$("#emailInfor").html("");
								emailFlag = true ;
							}
							},
						error: function(){
							alert("请求失败");
							}
			});			
			}
		}
		});
		
		
		//四.为验证码添加监听事件
		$("#verifyCode").blur(function(){
			
			//判断验证码是否为空
			var code = $("#verifyCode").val();
			if(code==""||code==null){
				$("#codeInfor").css("color","red");
				$("#codeInfor").html("验证码不能为空");
			}else if(code.length!=4){
				$("#codeInfor").css("color","red");
				$("#codeInfor").html("验证码长度为4");
			}else{
				$("#codeInfor").html("");
				$.ajax({
					url:"<c:url value='/UserServlet'/>",
					async:true,
					type:"post",
					dataType: "json",
					data: {
						"verifyCode":code,
						"method":"checkCodeByAjax"
					},
					success:function(data){
						var flag = data.flag;
						if(!flag){
							$("#codeInfor").css("color","red");
							$("#codeInfor").html("验证码错误");
						}else{
							$("#codeInfor").html("");
							verifyCodeFlag = true;
						}
					},
					error:function(){
						alert("请求失败");
					}
				});
			
			}
		});
		
		
	});
	
	
	
	
	function checkForm(){
		if(usernameFlag && passwordFlag &&emailFlag && verifyCodeFlag){
			document.forms["form"].submit();
			
		}else{
			alert("您输入的信息有错误,请重新输入");
		}
	}
	
	function change(){
		var ele = document.getElementById("vCode");
		ele.src = "<c:url value='/VerifyCodeServlet'/>?xxx=" + new Date().getTime();
	}
	
</script>
  </head>
  
  <body>
  <h1>注册</h1>
<form action="<c:url value='/UserServlet'/>" method="post" name="form">
	<input type="hidden" name="method" value="regist"/>
	用户名：<input type="text" name="username" id="username" />
	<span id="usernameInfor"></span><br/>
	密　码：<input type="password" name="password" id="password" />
	<span id="passwordInfor"></span><br/>
	邮　箱：<input type="text"  name="email" id="email" />
	<span id="emailInfor"></span><br/>
<div style="overflow:hidden">
	<div style="line-height: 30px; margin-right: 60px;float: left;">
		验证码：<input type="text"  name="verifyCode" id="verifyCode" />
		<span id="codeInfor"></span>
	</div>
	<div style="float: left;">
		<img id="vCode" src="<c:url value='/VerifyCodeServlet'/>" style="margin-top: 2px; border:1px solid black"  />
		<a id="image" href="javascript:change()" style="text-decoration: none;" >换一张</a> <br/>
	</div>
</div>                  
	<input type="button" onclick="checkForm()" value="注册"/>
</form>
  </body>
</html>
