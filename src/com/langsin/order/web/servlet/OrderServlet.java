package com.langsin.order.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.KeyValue;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import com.langsin.cart.domain.Cart;
import com.langsin.cart.domain.CartItem;
import com.langsin.order.domain.Order;
import com.langsin.order.domain.OrderItem;
import com.langsin.order.service.OrderException;
import com.langsin.order.service.OrderService;
import com.langsin.user.domain.User;

public class OrderServlet extends BaseServlet {
	
	private OrderService orderService = new OrderService();

	//一.购物车中点击购买 -> 往数据库中添加订单
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.生成订单对象  (一个订单就是一个购物车中的商品点击购买)
		 *      从session中得到cart, cart->order
		 * 2.生成订单项对象   并将该对象添加到订单中 (购物车条目其实是订单条目的子集)
		 * 		通过cart得到cartItem,  cartItem->orderItem  添加
		 * 3.调用service方法保存到数据库中
		 * 4.保存订单转发  /jsps/order/desc.jsp
		 */
		
			/**
			 * 
			 *cart--->order
			 * 	session: session_user,  session_cart 
			 * 	cart-->oder
			 * 	cart: total cartItem
			 * 	cartItem: 商品,数量,小计
			 * 	order:1主键  2下单时间  3订单金额(合计) 4订单状态  
			 * 		  5uid(订单用户User) 6收货地址7. 订单是由多个订单项组成的
			 */
		Cart cart = (Cart) request.getSession().getAttribute("session_cart");
		User user = (User) request.getSession().getAttribute("session_user");
		Order order = new Order();//收货地址暂时不设置
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(new Date());
		order.setTotal(cart.getTotal());
		order.setState(1);//设置订单状态为1表示未付款
		order.setUser(user);
			/**
			 * cartItem--->orderItem(循环)
			 *    cartItem: 商品,数量,小计
			 *    orderItem:主键,数量 ,小计 ,所属的订单对象(数据库仅仅只是oid),商品同理(bid)	
			 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartItem : cart.getAllCartItems()){
			OrderItem orderItem = new OrderItem();
			orderItem.setIid(CommonUtils.uuid());
			orderItem.setCount(cartItem.getCount());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setOrder(order);
			orderItem.setBook(cartItem.getBook());
			//将orderItem对象添加到集合中
			orderItemList.add(orderItem);
		}
		//将订单项添加到订单中
		order.setOrderItemList(orderItemList);
		//根据日常生活,购物车的商品购买后就清空购物车
		cart.clearCartItem();
		//调用service方法
		//保存order并转发
		orderService.addOrder(order);
		request.setAttribute("order", order);
		return "/jsps/order/desc.jsp";
	}
	
	//二.点击我的订单-->加载订单信息
	public String loadMyOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.得到uid
		 * 2.调用service方法得到 List<Order>
		 * 3.保存并转发 /jsps/order/list.jsp
		 */
		User user = (User) request.getSession().getAttribute("session_user");
		
		request.setAttribute("orderList", orderService.loadMyOrder(user.getUid()));
		return "f:/jsps/order/list.jsp";
	}
	
	//三.加载支付的订单界面(在订单中点击付款)
	public String loadPayPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取oid--->order
		 * 2.调用orderService 得到order对象
		 * 3.保存order 转发到 /jsps/order/desc.jsp
		 */
		String oid = request.getParameter("oid");
		Order order = orderService.loadPayPage(oid);
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	
	//四.支付
	public String payment(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//加载配置文件
		Properties pro = new Properties();
		pro.load(this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties"));
		/**
		 * 1.准备13参数
		 */
		String p0_Cmd = "Buy"; //业务类型 固定值"Buy"
		String p1_MerId = pro.getProperty("p1_MerId");//商户编号(唯一标识)
		String p2_Order = request.getParameter("oid");//商户订单号 (非必填),这里是为了后面回调函数查询数据库
		String p3_Amt = "0.01";//支付金额(测试用)
		String p4_Cur = "CNY";//只有一个值:人民币
		String p5_Pid = "";//商品名称
		String p6_Pcat = "";//商品种类
		String p7_Pdesc = "";//商品描述
		String p8_Url = pro.getProperty("p8_Url");//商户接收支付成功消息的地址(不填则得不到支付通知)
		String p9_SAF = "";//送货地址
		String pa_MP = "";//商户拓展信息
		String pd_FrpId = request.getParameter("pd_FrpId");//支付通道编码(去哪个银行)
		String pr_NeedResponse = "1";//应答机制 固定值为1
		
		/**
		 * 2.计算hmac:
		 * 	 	由上述的13个参数以及秘钥通过MD5加密得到
		 * 
		 */
		String keyValue = pro.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		
		/**
		 * 4.连接13+1个参数---->支付网关
		 */
		StringBuilder url = new StringBuilder(pro.getProperty("url"));
		url.append("?p0_Cmd=").append(p0_Cmd);
		url.append("&p1_MerId=").append(p1_MerId);
		url.append("&p2_Order=").append(p2_Order);
		url.append("&p3_Amt=").append(p3_Amt);
		url.append("&p4_Cur=").append(p4_Cur);
		url.append("&p5_Pid=").append(p5_Pid);
		url.append("&p6_Pcat=").append(p6_Pcat);
		url.append("&p7_Pdesc=").append(p7_Pdesc);
		url.append("&p8_Url=").append(p8_Url);
		url.append("&p9_SAF=").append(p9_SAF);
		url.append("&pa_MP=").append(pa_MP);
		url.append("&pd_FrpId=").append(pd_FrpId);
		url.append("&pr_NeedResponse=").append(pr_NeedResponse);
		url.append("&hmac=").append(hmac);
		
		System.out.println(url);
		
		/**
		 * 5.重定向到易宝
		 */
		response.sendRedirect(url.toString());
		return null;
	}
	
	//四.易宝回调方法: 我们必须要判断调用本方法的是不是易宝！
	public String back(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取11+1个参数
		 */
		String p1_MerId = request.getParameter("p1_MerId");//商户编号
		String r0_Cmd = request.getParameter("r0_Cmd");//业务类型
		String r1_Code = request.getParameter("r1_Code");//支付结果
		String r2_TrxId = request.getParameter("r2_TrxId");//支付流水号
		String r3_Amt = request.getParameter("r3_Amt");//支付金额
		String r4_Cur = request.getParameter("r4_Cur");//交易币种
		String r5_Pid = request.getParameter("r5_Pid");//商品名称
		String r6_Order = request.getParameter("r6_Order");//订单号id
		String r7_Uid = request.getParameter("r7_Uid");//易宝支付会员id
		String r8_MP = request.getParameter("r8_MP");//商户扩展信息
		String r9_BType = request.getParameter("r9_BType");//交易结果返回类型 : 点对点,浏览器重定向
		String hmac = request.getParameter("hmac");
		/**
		 * 2.验证访问者的身份:
		 * 		判断用自己的秘钥和访问者传过来的参数生成hmac,与他传过来的比较
		 * 		相同说明是易宝,不同说明是坏人!!	
		 */
		Properties pro = new Properties();
		pro.load(this.getClass().getClassLoader().getResourceAsStream("merchantInfo.properties"));
		String keyValue = pro.getProperty("keyValue");
		boolean flag = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
				r8_MP, r9_BType, keyValue);
		if(!flag){//校验失败
			request.setAttribute("msg", "小子,你还嫩点!");
		}
		/**
		 * 3.调用orderService方法:
		 * 			修改订单状态,以及其他业务操作
		 */
		orderService.paySuccsess(r6_Order);
		
		/*
		 * 4. 判断当前回调方式:
		 * 		r9_BType:1浏览器重定向(我们这能使用这个)  2点对点(实现不了,没域名没固定ip)      
		 *   如果为点对点，需要回馈以success开头的字符串
		 */
		if(r9_BType.equals("2")) {
			response.getWriter().print("success");
		}
		
		/*
		 * 5. 保存成功信息，转发到msg.jsp
		 */
		request.setAttribute("msg", "支付成功！等待卖家发货！估计是等不到了~~~~");
		return "f:/jsps/order/msg.jsp";
		
	}
	

	//五.确认收货
	public String confirm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * 1.获取oid-->order
		 * 2.调用orderService方法
		 * 		为防止有坏人,要先判断state是否是3,如果不是3说明是恶意攻击 抛出异常
		 * 3.保存信息到 并转发
		 */
		String oid = request.getParameter("oid");
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "恭喜您,交易完成!");
			return "f:/jsps/order/msg.jsp";
		} catch (OrderException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/jsps/order/msg.jsp";
		}
	}
	
	
}
