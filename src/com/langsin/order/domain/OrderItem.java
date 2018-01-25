package com.langsin.order.domain;

import com.langsin.book.domain.Book;

/**
 * 订单条目类:(操作数据库):在我们这个项目中说白了就是一次购物车中的东西
 *  1.主键 2.数量   3.小计 4.所属的订单 数据库中数oid, 5商品bid
 * @author 桑晓明
 *
 */
public class OrderItem {
	private String iid;
	private int count;
	private double subtotal;
	private Order order;
	private Book book;
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	@Override
	public String toString() {
		return "OrderItem [iid=" + iid + ", count=" + count + ", subtotal="
				+ subtotal + ", order=" + order + ", book=" + book + "]";
	}
}
