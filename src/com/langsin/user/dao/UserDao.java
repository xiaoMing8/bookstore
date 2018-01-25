package com.langsin.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.langsin.user.domain.User;

import cn.itcast.jdbc.TxQueryRunner;

/**
 * 数据持久层
 * @author 桑晓明
 */
public class UserDao {
	
	private QueryRunner qr = new TxQueryRunner();

	//1.ajax异步检查用户名是否存在
	public User checkUsernameByAjax(String username) {
		String sql = "select * from tb_user where username=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class), username);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//2.ajax异步检查邮箱是否存在
	public User checkUserEmailByAjax(String email) {
		String sql = "select * from tb_user where email=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class), email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//3.注册信息
	public void regist(User userForm) {
		String sql = "insert into tb_user values(?,?,?,?,?,?)";
		Object[] params = { userForm.getUid(), userForm.getUsername(),
				userForm.getPassword(), userForm.getEmail(),
				userForm.getCode(), userForm.isState() };
		try {
			 qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//4.通过code查找是否存在相应的user对象
	public User checkUserByCode(String code) {
		String sql = "select * from tb_user where code=?";
		try {
			return qr.query(sql, new BeanHandler<User>(User.class), code);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//5.修改指定用户状态
	public void updataState(String uid, boolean state) {
		String sql = "update tb_user set state=? where uid=?";
		try {
			qr.update(sql, state, uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
