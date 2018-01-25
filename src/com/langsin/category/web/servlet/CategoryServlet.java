package com.langsin.category.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.langsin.category.service.CategoryService;

import cn.itcast.servlet.BaseServlet;

public class CategoryServlet extends BaseServlet {
    private	CategoryService categoryService = new CategoryService();
    
    //1.查看所有分类的图书
    public String findAll(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
    	/**
    	 * 1.直接调用categoryService方法 ->List<category>
    	 * 2.将list保存到request域中  转发到 left.jsp中(将数据库中分类的名称显示在左侧)
    	 */
    	request.setAttribute("categoryList", categoryService.findAll());
    	return "f:/jsps/left.jsp";
    }
}
