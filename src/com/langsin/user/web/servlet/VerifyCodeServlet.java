package com.langsin.user.web.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.vcode.utils.VerifyCode;

public class VerifyCodeServlet extends HttpServlet {

	
	/**
	 * 用来单独生成验证码
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		//创建验证码类
		VerifyCode verifyCode = new VerifyCode();
		//得到验证码图片
		BufferedImage image = verifyCode.getImage();
		//把图片中的文本保存到session中
		request.getSession().setAttribute("session_vcode", verifyCode.getText());
		//把图片响应给浏览器
		VerifyCode.output(image, response.getOutputStream());
	}

}
