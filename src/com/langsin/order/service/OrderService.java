package com.langsin.order.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.jdbc.JdbcUtils;

import com.langsin.order.dao.OrderDao;
import com.langsin.order.domain.Order;

public class OrderService {
	private OrderDao orderDao = new OrderDao();

	//1.添加订单
	public void addOrder(Order order) {
		/**
		 * 事务控制:
		 * 	一个订单中必然需要订单条目的信息,不然就是一个空的订单(什么都没买)
		 * 	 而订单信息,订单条目信息是分两个表来存储的,一旦插入数据库时,某环节出现问题
		 * 	 就会出现业务数据前后不一致的问题,所以这里我们使用事务加以控制!!
		 * 
		 * 1.保存订单信息到数据库
		 * 2.保存订单条目信息到数据库
		 * 	  备注:order对象中已经包含了订单条目信息
		 */
		
		try {
			//开启事务
			JdbcUtils.beginTransaction();
			orderDao.addOrder(order); //插入订单信息
			orderDao.addOrderItemList(order.getOrderItemList());//插入订单中的所有订单条目
			//提交事务
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				//回滚事务
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
		}
	}

	//2.加载我的订单  
	public List<Order> loadMyOrder(String uid) {
		return orderDao.findAllOrderByUid(uid);
		
	}

	//3.点击某个订单的付款---->order(包含orderItemList)
	public Order loadPayPage(String oid) {
		//得到order
		Order order = orderDao.findOrderByOid(oid);
		//为order添加orderItemList信息
		try {
			orderDao.loadOrderItem(order);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return order;
	}

	//4.回调认证成功--->修改订单信息
	public void paySuccsess(String oid) {
		/**
		 * 为防止用户F5刷新页面修改订单信息时应该加以判断:
		 * 		如果state==1 ---->2
		 * 		如果state!=1  不做处理
		 */
		int state = orderDao.getStateByOid(oid);
		if(state==1){
			orderDao.setStateByOid(oid,2);
		}
		
	}

	//确认收货
	public void confirm(String oid) throws OrderException {
		/**
		 * 为防止有人找漏洞从中牟利 没付款 直接确认收货
		 *    先判断state是否是3,如果不是3说明是恶意攻击 抛出异常
		 *    是3,修改订单状态
		 */
		int state = orderDao.getStateByOid(oid);
		if(state!=3) throw new OrderException("小伙子你还嫩了点 !");
		orderDao.setStateByOid(oid, 4);
	}

	//查找所有订单 并加载所有订单条目信息
	public List<Order> findAllOrders() {
		return orderDao.findAllOrders();
	}
	public List<Order> find(int state) {
		return orderDao.findNotFinish(state);
	}
	
	//管理员发货
	public void deliver(String oid) {
		orderDao.setStateByOid(oid, 3);
	}
	
	
}
