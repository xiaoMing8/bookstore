package com.langsin.order.domain;

import java.util.Date;
import java.util.List;

import com.langsin.user.domain.User;

/**
 * 订单类:(操作数据库) 根据数据库来写
 * 	1主键  2下单时间  3订单金额(合计)  
 * 	4订单状态  5uid(订单用户User) 6收货地址
 * 	7. 订单是由多个订单项组成的(自定义的)
 * @author 桑晓明
 *
 */
public class Order {
	private String oid;//主键
	private Date ordertime;//下单时间
	private double total;//合计
	private int state;//订单状态 1:未付款,2:已付款未发货,3:已发货为收到货,4:收到货(交易完成)
	private User user;//该订单的用户
	private String adress;//收货地址
	
	//订单不像是购物车相同的商品可以累加,点击购买生成的订单不能在发生改变
	//所以这里用list不用map
	private List<OrderItem> orderItemList; //该订单中的所有订单条目(购物车中的东西)

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Date getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

	@Override
	public String toString() {
		return "Order [oid=" + oid + ", ordertime=" + ordertime + ", total="
				+ total + ", state=" + state + ", user=" + user + ", adress="
				+ adress + ", orderItemList=" + orderItemList + "]";
	}
	
	
	
	
}