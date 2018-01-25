package com.langsin.user.service;

import com.langsin.user.dao.UserDao;
import com.langsin.user.domain.User;

/**
 * 业务处理层
 * @author 桑晓明
 */
public class UserService {
	private UserDao userDao = new UserDao();

	//1.ajax异步检查用户名是否存在
	public User checkUsernameByAjax(String username) {
		return userDao.checkUsernameByAjax(username);
	}

	//2.ajax异步检查用户名是否存在
	public User checkEmailByAjax(String email) {
		return userDao.checkUserEmailByAjax(email);
	}

	//3.注册信息
	public void regist(User userForm) {
		userDao.regist(userForm);
	}

	//4.激活邮箱
	public void active(String code) throws UserException {
		/**
		 * 1.通过调用userDao的 checkUserBycode 得到user对象
		 * 2.如果user对象不存在说明是恶意访问,抛出异常  ->激活码无效
		 * 3.如果user对象存在,判断该对象的state状态,如果为1(true) 则抛出异常  ->邮箱已经注册成功了
		 * 4.state不为1,将该对象的state状态改为1
		 */
		User user = userDao.checkUserByCode(code);
		if(user==null) throw new UserException("激活码无效");
		if(user.isState()) throw new UserException("你已经激活了,请不要重复激活");
		userDao.updataState(user.getUid(),true);
	}

	//5.用户登录
	public User login(User userForm) throws UserException{
		/**
		 * 1.根据用户名->user对象
		 * 2.如果user==null : 抛出异常(该用户名不存在)
		 * 3.user!=null 
		 *      密码错误:抛出异常(密码错误)
		 *      密码正确:登录成功
		 */
		User user = userDao.checkUsernameByAjax(userForm.getUsername());
		if(user==null) throw new UserException("该用户名不存在");
		if(!user.getPassword().equals(userForm.getPassword()))
			throw new UserException("密码错误");
		if(!user.isState()) throw new UserException("邮箱未激活");
		return user;
	}
		
}
