package com.langsin.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 购物车类 :是由购物条目组成的(1种商品组成一个条目)
 *  合计,添加条目到购物车,清空购物车,删除单个条目,,获取所有条目
 * @author 桑晓明
 */
public class Cart {
	//因为一件商品有可能购买多次 用map可判断集合中是否是否有该商品(key=bid)
	private Map<String,CartItem> map = new HashMap<String,CartItem>();
	
	//1.合计
	public double getTotal() {
		// 合计=所有小计之和
		BigDecimal total = new BigDecimal("0");
		for (CartItem item : map.values()) {
			BigDecimal subtotal = new BigDecimal(item.getSubtotal() + "");
			total = total.add(subtotal);
		}
		return total.doubleValue();
	}
	
	//2.添加条目到购物车
	public void addItem(CartItem cartItem){
		/**
		 * 1.根据key判断是否购物车中存在该条目
		 * 		如果不存在: 直接添加
		 * 2.如果存在: 先返回原条目,在加上新条目	(数量上的相加)
		 */
		if(map.containsKey(cartItem.getBook().getBid())){//存在
			CartItem c = map.get(cartItem.getBook().getBid());
			c.setCount(c.getCount()+cartItem.getCount());
			map.put(c.getBook().getBid(), c);
			
		}else{//不存在
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	
	//3.清空购物车
	public void clearCartItem(){
		map.clear();
	}
	//4.删除指定单个条目
	public void deleteByBid(String bid){
		map.remove(bid);
	}
	//5.获取所有条目
	public Collection<CartItem> getAllCartItems(){
		return map.values();
	}
	
}
