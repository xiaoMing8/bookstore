package com.langsin.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

import com.langsin.book.domain.Book;
import com.langsin.category.dao.CategoryDao;
import com.langsin.category.domain.Category;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	private CategoryDao categoryDao = new CategoryDao();

	//1.查询所有图书
	public List<Book> findAll() {
		String sql = "select * from book where del=false";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//2.根据cid获取指定图书集合
	public List<Book> findBookListByCid(String cid) {
		String sql = "select * from book where cid=? and del=false ";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class),cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//3.根据bid 获取指定图书
	public Book findBookByBid(String bid) {
		String sql = "select * from book where bid=?";
		try {
			 //我们要在book对象中加载category的信息
			Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
			//使用map映射出两个对象,在建立关系
			Book book = CommonUtils.toBean(map, Book.class);
			//只有cid没有cname
			Category category = CommonUtils.toBean(map, Category.class);
			Category category2 = categoryDao.getCategoryByCid(category.getCid());
			book.setCategory(category2);
			return book;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//4.根据bid删除指定图书(假删除)
	public void delete(String bid) {
		String sql = "update book set del=true where bid=?";
		try {
			qr.update(sql, bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//5.将book添加到数据库
	public void addBook(Book book) {
		try {
			String sql = "insert into book values(?,?,?,?,?,?,?)";
			Object[] params = { book.getBid(), book.getBname(),
					book.getPrice(), book.getAuthor(), book.getImage(),
					book.getCategory().getCid() , book.isDel()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}}

	
	//6.修改图书
	public void modifyBook(Book book) {
		System.out.println(book);
		String sql = "update book set bname=?, price=?,author=?, image=?, cid=? ,del=? where bid=?";
		Object[] params = { book.getBname(), book.getPrice(), book.getAuthor(),
				book.getImage(), book.getCategory().getCid(), book.isDel(),
				book.getBid() };
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
