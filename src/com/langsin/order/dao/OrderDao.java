package com.langsin.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

import com.langsin.book.domain.Book;
import com.langsin.order.domain.Order;
import com.langsin.order.domain.OrderItem;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();

	//1.添加订单
	public void addOrder(Order order) {
		String sql = "insert into orders values(?,?,?,?,?,?)";
		/**
		 * 处理util的Date转换成sql的Timestamp
		 */
		Timestamp time = new Timestamp(order.getOrdertime().getTime());
		Object[] params = { order.getOid(), time, order.getTotal(),
				order.getState(), order.getUser().getUid(), order.getAdress() };
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//添加订单条目
	public void addOrderItemList(List<OrderItem> orderItemList) {
		/**
		 * 1.批处理:
		 *     一个订单中一般有很多订单条目,所以我们这里使用批处理提高效率
		 * 2.QueryRunner类的batch(String sql, Object[][] params)
		 * 	      其中params是多个一维数组(二维数组)！
		 * 	      每个一维数组都与sql在一起执行一次，多个一维数组就执行多次  
		 */
		String sql = "insert into orderitem values(?,?,?,?,?)";
		/**
		 * 将orderItemList转换成二维数组
		 * 将orderItem转换成一个一维数组
		 */
		//定义一个二维数组		
		Object[][] params = new Object[orderItemList.size()][];
		//循环遍历orderItemList为每个orderItem赋值
		for (int i = 0; i < orderItemList.size(); i++) {
			OrderItem orderItem = orderItemList.get(i);
			params[i] = new Object[] { orderItem.getIid(),
					orderItem.getCount(), orderItem.getSubtotal(),
					orderItem.getOrder().getOid(), orderItem.getBook().getBid() };
		}
		try {
			qr.batch(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
		
	}
	
	//3.通过uid查找订单集合(用于显示订单信息,包括商品(book)的)
	public List<Order> findAllOrderByUid(String uid) {
		/**
		 * 查表顺序: orders(uid,oid)-->orderItem(oid,bid)-->book(bid)
		 * 1.uid查找orders表-->orderList
		 * 2.遍历order,用oid查找orderItem表-->orderItem
		 * 3.遍历orderItem得到bid,通过bid查找book表-->book
		 * 		备注:因为orderItem跟book都有bid  可以进行多表查询
		 * 4,将book添加到orderItem中,将orderItem添加到order中
		 * 5.最后返回list<Order>集合
		 */
		try {
			String sql = "select * from orders where uid=? ORDER BY ordertime DESC";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(
					Order.class), uid);
			for (Order order : orderList) {
				loadOrderItem(order);// 为order对象添加它的所有订单条目
			}
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//3.1为每个订单加载订单项
	public void loadOrderItem(Order order) throws SQLException {
		/**
		 *1.多表查询:orderItem(有oid决定) book
		 *     查询出来的结果集是OrderItem跟book数据库信息的总和
		 *2.因为多表查询出的结果不再是javaBean 所有用MapListHandler
		 *		返回的结果是一个map集合,每个map对应着一个结果集:某件商品(book),数量金额等(orderItem)
		 *3.遍历map,将每个map转换成2个对象: book,orderItem
		 *4.最终建立关系 :将book保存到orderItem中 将orderItem添加到orderItemList中 将orderItemList
		 *				添加给order
		 */
		String sql = "select * from orderitem i, book b where i.bid=b.bid and oid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), order.getOid());
		
		//将map转换成book,orderItem对象,并把book保存到OrderItem中, 返回orderItem
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		//将orderItemList保存到Order中
		order.setOrderItemList(orderItemList);
	}

	
	//3.2将map转换成book,orderItem对象,并把book保存到OrderItem中, 返回orderItem
	public List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map : mapList ){
			OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
			Book book = CommonUtils.toBean(map, Book.class);
			orderItem.setBook(book);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}

	//4.根据oid查找订单
	public Order findOrderByOid(String oid) {
		try {
			String sql = "select * from orders where oid=?";
			return qr.query(sql, new BeanHandler<Order>(Order.class), oid);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//5.根据oid获取查看订单状态
	public int getStateByOid(String oid) {
		try {
			String sql = "select * from orders where oid=?";
			return qr.query(sql, new BeanHandler<Order>(Order.class), oid).getState();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//6.根据oid设置订单状态
	public void setStateByOid(String oid, int state) {
		try {
			String sql = "update orders set state =? where oid=?";
			qr.update(sql, state,oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	
	}

	//查找所有订单
	public List<Order> findAllOrders() {

		/**
		 * 查表顺序: orders(oid)-->orderItem(oid,bid)-->book(bid)
		 * 1.查找orders表-->orderList
		 * 2.遍历order,用oid查找orderItem表-->orderItem
		 * 3.遍历orderItem得到bid,通过bid查找book表-->book
		 * 		备注:因为orderItem跟book都有bid  可以进行多表查询
		 * 4,将book添加到orderItem中,将orderItem添加到order中
		 * 5.最后返回list<Order>集合
		 */
		try {
			String sql = "select * from orders ORDER BY ordertime DESC";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(
					Order.class));
			for (Order order : orderList) {
				loadOrderItem(order);// 为order对象添加它的所有订单条目
			}
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	
	}

	//查询订单状态      并加载订单信息
	public List<Order> findNotFinish(int state) {
		String sql = "select * from orders where state=? ORDER BY ordertime DESC";
		try {
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),state);
			for (Order order : orderList) {
				loadOrderItem(order);// 为order对象添加它的所有订单条目
			}
			return orderList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
	


}
