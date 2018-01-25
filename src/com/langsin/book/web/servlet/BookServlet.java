package com.langsin.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.langsin.book.service.BookService;

import cn.itcast.servlet.BaseServlet;

public class BookServlet extends BaseServlet {

	private BookService bookService = new BookService();
	
	//1.查询所有图书并显示
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.直接调用service方法   将list保存到request域中转发
		 * 2.将结果在 jsps/book/list.jsp中循环显示
		 */
		request.setAttribute("bookList", bookService.findAll()); 
		return "f:/jsps/book/list.jsp";
	}
	
	//2.根据cid查询指定图书集合    
	public String findByCid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取超链接中的cid
		 * 2.根据cid调用service方法 得到 list集合
		 * 3.将集合保存到 request域中  在jsps/book/list.jsp中循环遍历
		 */
		String cid = request.getParameter("cid");
		request.setAttribute("bookList", bookService.findBookListByCid(cid));
		return "f:/jsps/book/list.jsp";
	}
	
	//3.根据bid查询指定的图书 
	public String findBookByBid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取超链接中的bid
		 * 2.调用service方法 得到book对象
		 * 3.保存转发  在 jsps/book/desc.jsp显示
		 */
		String bid = request.getParameter("bid");
		request.setAttribute("book", bookService.findBookByBid(bid));
		return "jsps/book/desc.jsp";
	}
}
