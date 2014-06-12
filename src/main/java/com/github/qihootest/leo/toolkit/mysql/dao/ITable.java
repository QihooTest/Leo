package com.github.qihootest.leo.toolkit.mysql.dao;

import java.util.List;

/**
 *	说明：数据库表实体类的基类
 * 			 当前有3个公共方法
 * @author lianghui (lianghui@360.cn) 2013-01-10
 *
 */
public interface ITable  {
	public List<String> getColList();
	public void set(String key,String value);	
	public String get(String key);
}
