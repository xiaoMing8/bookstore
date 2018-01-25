package com.langsin.cart.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;

import com.langsin.book.domain.Book;
import com.langsin.book.service.BookService;
import com.langsin.cart.domain.Cart;
import com.langsin.cart.domain.CartItem;

public class CartServlet extends BaseServlet {
	
	private BookService bookService = new BookService();
	
	//一.添加购物车
	public String addItem(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.从session中得到车
		 * 2.得到条目
		 * 3.将条目添加到车中
		 * 4.转发到/jsps/cart/list.jsp
		 */
		System.out.println("dddd");
		//1.1得到车
		Cart cart = (Cart) request.getSession().getAttribute("session_cart");
		
		if(cart==null){
			response.getWriter().write("<script type=\"text/javascript\">alert(\"请先去登录\");</script>");
			return null;
		}
		
		
		/**
		 * 2.得到条目: CartItem: book count
		 *    表单中: bid count  bid->book
		 */
		Book book = bookService.findBookByBid(request.getParameter("bid"));
		int count = Integer.parseInt(request.getParameter("count"));
		CartItem cartItem = new CartItem();
		cartItem.setBook(book);
		cartItem.setCount(count);
		
		//3.将条目添加到车中
		cart.addItem(cartItem);
		return "f:/jsps/cart/list.jsp";
	}
	
	//二.清空购物车
	public String clear(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 得到车 ->清空
		 */
		Cart cart = (Cart) request.getSession().getAttribute("session_cart");
		cart.clearCartItem();
		return "f:/jsps/cart/list.jsp";
	}
	
	//三.删除指定的条目
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 得到车 ->删除
		 */
		String bid = request.getParameter("bid");
		Cart cart = (Cart) request.getSession().getAttribute("session_cart");
		cart.deleteByBid(bid);
		return "f:/jsps/cart/list.jsp";
	}
	
}
