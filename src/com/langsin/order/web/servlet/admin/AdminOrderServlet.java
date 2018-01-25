package com.langsin.order.web.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.langsin.order.service.OrderService;

import cn.itcast.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet {
	
	private OrderService orderService = new OrderService();
	
	//查找所有订单  adminjsps/admin/order/list.jsp
	public String findAllOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("orderList", orderService.findAllOrders());
		return "f:/adminjsps/admin/order/list.jsp";
	}
	//查询某种状态的订单
	public String find(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int state = Integer.parseInt(request.getParameter("state"));
		request.setAttribute("orderList", orderService.find(state));
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	//发货
	public String deliver(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String oid  = request.getParameter("oid");
		orderService.deliver(oid);
		return findAllOrder(request,response);
	}
}
