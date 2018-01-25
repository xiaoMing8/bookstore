package com.langsin.book.service;

import java.util.List;

import com.langsin.book.dao.BookDao;
import com.langsin.book.domain.Book;

public class BookService {
	private BookDao bookDao = new BookDao();

	//1.查询所有图书
	public List<Book> findAll() {
		return bookDao.findAll();
	}

	//2.根据cid获取指定图书集合
	public List<Book> findBookListByCid(String cid) {
		return bookDao.findBookListByCid(cid);
	}

	//3.根据bid 获取指定图书
	public Book findBookByBid(String bid) {
		return bookDao.findBookByBid(bid);
	}

	//4.根据bid 删除图书信息
	public void delete(String bid) {
		bookDao.delete(bid);
	}

	//5.将book添加到数据库
	public void addBook(Book book) {
		bookDao.addBook(book);
	}

	//6.修改book
	public void modifyBook(Book book) {		
		bookDao.modifyBook(book);
	}

	
}
