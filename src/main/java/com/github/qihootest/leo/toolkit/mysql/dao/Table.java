package com.github.qihootest.leo.toolkit.mysql.dao;

import java.util.List;





public abstract class Table implements ITable{


public List<String> getColList() {		
		return DaoUtil.getCoList(this);
	}
	public void set(String key,String value) {
		DaoUtil.set(key, value, this);
	}	
	public String get(String key) {
		return DaoUtil.get(key, this);
	}
}
