/**
 * 
 */
package com.github.qihootest.leo.toolkit;

import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.toolkit.dbunit.DbUnitUtil;
import com.github.qihootest.leo.toolkit.mysql.ConnMysql;

/**
 * @author lianghui (lianghui@360.cn)
 *
 */
public class TestDbunitUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConnMysql conn = new ConnMysql("127.0.0.1", "3306", "att", "auto", "auto");
		DbUnitUtil.writeToFileFromMysql(IftConf.RootPath+"test.xml", conn, "select * from att_click_info;select * from att_task_info");
		
		DbUnitUtil.writeToFileFromDataBase(IftConf.RootPath+"test.xls", DbUnitUtil.getDatabaseConnectionFromMysql(conn), "select * from att_click_info;select * from att_task_info");
		
	}

}
