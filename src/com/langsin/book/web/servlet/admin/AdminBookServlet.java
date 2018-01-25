package com.langsin.book.web.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;

import com.langsin.book.service.BookService;
import com.langsin.category.service.CategoryService;
import com.langsin.order.service.OrderService;

public class AdminBookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	//1.查询所有图书
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("bookList", bookService.findAll());
		return "f:/adminjsps/admin/book/list.jsp";
	}
	//2.加载某类图书
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.bid-->book
		 * 2.查询categoryList
		 * 3.保存信息转发    
		 */
		request.setAttribute("book",
				bookService.findBookByBid(request.getParameter("bid")));
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	
	//3.删除图书
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		bookService.delete(request.getParameter("bid"));
		return findAll(request, response);
	}
	
	
	//4.添加图书之前:加载所有分类  添加图书功能在单独的一个servlet中
	public String modifyPreLoadCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/adminjsps/admin/book/add.jsp";
	}
	
	//5.修改图书之前的加载图书信息
	public String modifyPre(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("book",
				bookService.findBookByBid(request.getParameter("bid")));
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/adminjsps/admin/book/mod.jsp";
	}
	
	

	
}
