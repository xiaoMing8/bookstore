package com.langsin.user.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.langsin.user.domain.User;


public class UserLoginFileter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		/**
		 * 1.从session中获取user对象
		 * 2.查看user对象是否存在
		 */
		System.out.println("过滤器");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		User user =(User) request.getSession().getAttribute("session_user");
		if(user!=null){
			chain.doFilter(req, resp);
		}else{
			System.out.println(request.getContextPath() +"/jsps/user/login.jsp");
			response.sendRedirect(request.getContextPath() +"/jsps/user/login.jsp");
		}
		
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
