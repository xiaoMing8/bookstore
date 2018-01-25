package com.langsin.user.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;

import com.langsin.cart.domain.Cart;
import com.langsin.user.domain.User;
import com.langsin.user.service.UserException;
import com.langsin.user.service.UserService;

public class UserServlet extends BaseServlet {
	
	private UserService userService = new UserService();

	// 1.ajax异步判断用户名是否存在
	public String checkUsernameByAjax(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.获取表单中的数据
		 * 2.调用service方法得到user对象
		 * 3.判断user对象是否存在 返回json
		 */		
		String username = request.getParameter("username");
		User user = userService.checkUsernameByAjax(username);
		boolean flag = true;
		if(user==null){//该用户名不存在可以注册
			flag =false;
		}		
		response.getWriter().write("{\"flag\":"+flag+"}");
		return null;
	}
	
	//2.ajax异步判断邮箱是否被注册
	public String checkEmailByAjax(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.获取表单中的邮箱
		 * 2.调用service方法得到user对象
		 * 3.判断user对象是否存在  返回json
		 */
		String email = request.getParameter("email");
		User user = userService.checkEmailByAjax(email);
		boolean flag = true;
		if(user==null){//该用户名不存在可以注册
			flag =false;
		}		
		response.getWriter().write("{\"flag\":"+flag+"}");
		
		return null;
	}
	
	//3.ajax异步判断验证码是否正确
	public String checkCodeByAjax(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		/**
		 * 1.获取表单中的验证码
		 * 2.从session域中得到 验证码中的数据 验证验证码是否正确
		 * 3.返回 json
		 */
		String code = request.getParameter("verifyCode");
		String vcode = (String) request.getSession().getAttribute("session_vcode");
		boolean flag = false;
		if(code!=null){
			if(code.equalsIgnoreCase(vcode)){
				flag = true;
			}
		}
		System.out.println(flag);
		response.getWriter().write("{\"flag\":"+flag+"}");
		return null;
	}
	
	//4.用户注册信息(格式都没错误)
	public String regist(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.封装表单数据-> userForm (数据都符合要求)
		 * 2.补全数据: uid(主键) code(邮件激活码)
		 * 3.调用service方法 :实现注册功能
		 * 4.发邮件
		 * 5.转发成功信息 到 msg.jsp中
		 */
		//1.封装表单信息
		User userForm = CommonUtils.toBean(request.getParameterMap(), User.class);
		//2.补全数据
		userForm.setUid(CommonUtils.uuid());
		userForm.setCode(CommonUtils.uuid()+CommonUtils.uuid());
		//3.调用service方法完成注册
		userService.regist(userForm);
		
		/**
		 * 4.发邮件
		 *   准备配置文件
		 */
		//获取配置文件的内容
		Properties pro = new Properties();
		//获取输入流
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("email_template.properties");
		pro.load(inputStream);
		String host = pro.getProperty("host");//获取服务器主机
		String uname = pro.getProperty("uname");//获取用户名
		String pwd = pro.getProperty("pwd");//获取密码
		String from = pro.getProperty("from");//获取发件人
		String to = userForm.getEmail();//获取收件人
		String subject = pro.getProperty("subject");//获取主题
		String content = pro.getProperty("content");//获取邮件内容
		content = MessageFormat.format(content, userForm.getCode());//替换{0} 激活码
		
		/**
		 * 发邮件三步
		 */
		//1.获取session
		Session session = MailUtils.createSession(host, uname, pwd);
		//2.创建邮件对象
		Mail mail = new Mail(from, to, subject, content);
		//发送邮件
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		//保存成功信息 装发
		request.setAttribute("msg", "恭喜您,注册成功!请马上到邮箱激活");
		return "f:/jsps/msg.jsp";
	}
	
	//5.邮箱激活
	public String active(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.获取参数中的code
		 * 2.调用service方法完成激活
		 * 3.保存信息 转发到msg.jsp
		 *     UserException
		 *     成功信息
		 */
		String code = request.getParameter("code");
		try {
			//激活成功
			userService.active(code);
			request.setAttribute("msg", "恭喜你激活成功请前去登录");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
	
	//6.用户登录
	public String login(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.判断验证码是否正确:
		 * 2.封装表单数据 ->userForm
		 * 3.调用userService的 login方法
		 *      抛异常: 保存异常信息  回显 ->login.jsp
		 * 4.不抛异常: 登录成功  向session域中保存userForm对象
		 * 		 重定向到index.jsp中(修改index.jsp页面)
		 */
		
		String code = request.getParameter("verifyCode");
		String vcode = (String) request.getSession().getAttribute("session_vcode");
		
		if(!code.equalsIgnoreCase(vcode)){
			request.setAttribute("msg", "验证码错误");
			return "f:/jsps/user/login.jsp";
		}
		User userForm = CommonUtils.toBean(request.getParameterMap(), User.class);	
		try{
			User user = userService.login(userForm);
			request.getSession().setAttribute("session_user", user);
			//添加一辆车
			request.getSession().setAttribute("session_cart", new Cart());
			return "r:/index.jsp";
		}catch(UserException e){
			request.setAttribute("msg",e.getMessage());
			request.setAttribute("user", userForm);
			return "f:/jsps/user/login.jsp";
		}
	}
	
	//7.退出
	public String quit(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//清空session 重定向到  index,jsp
		request.getSession().invalidate();
		return "r:/index.jsp";
	}
	
	
}
