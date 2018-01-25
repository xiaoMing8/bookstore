package com.langsin.cart.domain;

import java.math.BigDecimal;

import com.langsin.book.domain.Book;

/**
 * 购物车条目类:
 *   (1种)商品,数量,小计
 * @author 桑晓明
 */
public class CartItem {
	private Book book; //商品
	private int count; //购买数量
	
	/**
	 * 小计:不能设为可设置的属性,应该是算出来的
	 * 只能get 不能set
	 * 计算时注意处理二进制误差问题
	 * @return
	 */
	public double getSubtotal(){
		BigDecimal price = new BigDecimal(book.getPrice()+"");
		BigDecimal sum = new BigDecimal(count+"");
		return price.multiply(sum).doubleValue();
	}
	
	
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
