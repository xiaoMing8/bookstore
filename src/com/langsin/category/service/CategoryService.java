package com.langsin.category.service;

import java.util.List;

import com.langsin.book.dao.BookDao;
import com.langsin.book.domain.Book;
import com.langsin.category.dao.CategoryDao;
import com.langsin.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();

	//1.查询所有分类
	public List<Category> findAll() {
		return categoryDao.findAll();
	}

	//2.根据cid-->category
	public Category getCategoryByCid(String cid) {
		return categoryDao.getCategoryByCid(cid);
	}

	//3.根据cid修改分类
	public void modify(String cid,String cname) throws CategoryException {
		/**
		 * 1.先根据cid-->category
		 *     判断category.cname是否等于cname
		 *     相等:不做任何操作
		 * 2.不相等:在根据 cname查找数据库 看是否存在
		 *         存在:抛异常
		 * 3.不存在:修改          
		 */
		Category categoryByCid = categoryDao.getCategoryByCid(cid);
		if(!cname.equals(categoryByCid.getCname())){
			Category categoryByCname = categoryDao.getCategoryByCname(cname);		
			if(categoryByCname!=null) throw new CategoryException("该分类已存在,请重新修改");
			categoryDao.updateCnameByCid(cid,cname);
		}
		
	}

	//4.根据 cid删除分类
	public void deleteByCid(String cid) throws CategoryException {
		/**
		 * 原则: 该分类下还有图书,就不能删除
		 * 1.首先 cid--category
		 * 2.判断是否有图书
		 */
		//使用BookDao的方法
		List<Book> bookList = bookDao.findBookListByCid(cid);
		if(bookList.size()!=0) throw new CategoryException("该分类下还有图书,不能删除");
		categoryDao.delete(cid);
	}

	//5.添加分类
	public void addCategory(Category category) throws CategoryException {
		/**
		 * 1.使用cname查询 数据库看是否存在
		 *     存在:抛异常
		 * 2.不存在:添加    
		 */
		Category category2 = categoryDao.getCategoryByCname(category.getCname());
		if(category2!=null) throw new CategoryException("该分类已存,请重新添加!");
		categoryDao.addCategory(category);
		
	}

}
