package com.langsin.category.web.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.langsin.category.domain.Category;
import com.langsin.category.service.CategoryException;
import com.langsin.category.service.CategoryService;

public class AdminCategoryServlet extends BaseServlet {
	
	private CategoryService categoryService = new CategoryService();
	
	//1查询所有分类
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.调用sevice方法 -->lsit<category>
		 * 2.保存信息转发
		 */
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
	//2.点击修改分类之后的   加载当前分类的信息
	public String modifyPre(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取cid-->category
		 * 2.转发 adminjsps/admin/category/mod.jsp
		 */
		request.setAttribute("category", categoryService.getCategoryByCid(request.getParameter("cid")));
		return "f:/adminjsps/admin/category/mod.jsp";
	}
	
	//3.修改分类 
	public String modify(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取cid,调用service方法 对数据库进行修改
		 * 2.调用 findAll,重新加载页面
		 */
		try {
			categoryService.modify(request.getParameter("cid"),request.getParameter("cname"));
			return findAll(request,response);
		} catch (CategoryException e) {
			request.setAttribute("msgs", e.getMessage());
			return "f:/adminjsps/admin/category/mod.jsp";
		}
	}
	//4.删除分类
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取cid
		 * 2.调用service方法,
		 * 	   抛出异常,保存异常信息,返回本页面
		 * 3.调用个findAll(),重新加载页面 
		 */
		try {
			categoryService.deleteByCid(request.getParameter("cid"));
			return findAll(request, response);
		} catch (CategoryException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
		
	}
	
	//5.点击添加分类-->跳转到add.jsp页面
	public String addCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.封装表单数据  补全:cid
		 * 2.调用service方法 插入数据库
		 * 		抛异常: 转发异常信息-->msg.jsp
		 * 3.不抛异常 调用findAll()方法
		 */
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());
		try {
			categoryService.addCategory(category);
			return findAll(request, response);

		} catch (CategoryException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
	}
}
