package com.github.qihootest.leo.toolkit;

import java.util.ArrayList;
import java.util.List;

import com.github.qihootest.leo.toolkit.mysql.MysqlUtil;
import com.github.qihootest.leo.toolkit.mysql.dao.DaoUtil;
import com.github.qihootest.leo.toolkit.mysql.dao.ITable;


/**
 * @author lianghui (lianghui@360.cn)
 *
 */
public class TestMysqlUtil {

	public static void main(String args[]){
		String ip1 = "106.120.162.231";
		String port1 = "3302";
		String dataName1 = "ad_dianjing07";
		String userName1 = "qa_dj";
		String passWord1 = "kY9RS12NvPXL";
		
		String ip2 = "106.120.162.230";
		String port2 = "3302";
		String dataName2 = "ad_dianjing";
		String userName2 = "qa_dj";
		String passWord2 = "kY9RS12NvPXL";
		
		String mysql01 = "update ad_group set status=1 where id=\"606379236\"";
		String mysql03 = "update ad_group set status=-1 where title=\"test\"";
		String mysql02 = "update ad_user_app set status= -1 where id =282";
		
		
		
		MysqlUtil mysqlUtil1 = new MysqlUtil();
		mysqlUtil1.connMysql(ip1, port1, dataName1, userName1, passWord1);
		mysqlUtil1.excSql(mysql01);
		mysqlUtil1.closedConn();
		
		
		
		MysqlUtil mysqlUtil2 = new MysqlUtil();
		mysqlUtil2.connMysql(ip2, port2, dataName2, userName2, passWord2);
		mysqlUtil2.excSql(mysql02);
		mysqlUtil2.closedConn();
//		
		System.out.println("done!!!!!!!!!!!");
	}

}
