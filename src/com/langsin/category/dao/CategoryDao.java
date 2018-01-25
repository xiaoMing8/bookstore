package com.langsin.category.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.langsin.category.domain.Category;

import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();

	//1.查询所有分类
	public List<Category> findAll() {
		String sql = "select * from category";
		try {
			 return qr.query(sql, new BeanListHandler<Category>(Category.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//2.根据cid得到category
	public Category getCategoryByCid(String cid) {
		String sql = "select * from category where cid=?";
		try {
			 return qr.query(sql, new BeanHandler<Category>(Category.class),cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//3.根据cid修改分类名称
	public void updateCnameByCid(String cid, String cname) {
		String sql = "update category set cname=? where cid=? ";
		try {
			qr.update(sql, cname,cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//4.根据cname获取category
	public Category getCategoryByCname(String cname) {
		String sql = "select * from category where cname=?";
		try {
			 return qr.query(sql, new BeanHandler<Category>(Category.class),cname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	//5.根据cid删除分类
	public void delete(String cid) {
		String sql = "delete from category where cid=?";
		try {
			qr.update(sql, cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
	}

	//6.添加分类
	public void addCategory(Category category) {
		String sql = "insert into category values(?,?)";
		try {
			qr.update(sql, category.getCid(),category.getCname());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


}
