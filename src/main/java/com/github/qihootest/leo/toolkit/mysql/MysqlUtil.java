package com.github.qihootest.leo.toolkit.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.qihootest.leo.toolkit.util.LogUtil;
import com.github.qihootest.leo.toolkit.util.StringUtil;

/**
 * 数据库操作类，需先使用connMysql或reConnMysql方法先连接到数据库后，再执行其他操作
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 */
public class MysqlUtil {

	private static Connection con = null; // 创建用于连接数据库的Connection对象
	private static LogUtil log = LogUtil.getLogger(MysqlUtil.class);// 日志记录
	private ConnMysql mysql;// 数据库连接

	/**
	 * 连接mysql，如果已连接，则不再根据传入的参数创建连接
	 * 
	 * @param ip
	 * @param port
	 * @param dataName
	 * @param userName
	 * @param passWord
	 */
	public void connMysql(String ip, String port, String dataName,
			String userName, String passWord) {
		if (con == null) {
			if (null == mysql) {//
				mysql = new ConnMysql();
				mysql.ip = ip;
				mysql.port = port;
				mysql.dataName = dataName;
				mysql.userName = userName;
				mysql.passWord = passWord;
			}
			con = mysql.getConnection();
		}
	}

	/**
	 * 根据连接信息，重新连接mysql，如果已有连接，则先断开
	 * 
	 * @param ip
	 * @param port
	 * @param dataName
	 * @param userName
	 * @param passWord
	 */
	public void reConnMysql(String ip, String port, String dataName,
			String userName, String passWord) {
		MysqlUtil mu = new MysqlUtil();
		closedConn();
		mu.connMysql(ip, port, dataName, userName, passWord);
	}

	/**
	 * 说明：关闭数据库连接
	 */
	public void closedConn() {
		if (con != null) {
			mysql.closedConn();
			con = null;
		}
	}

	/**
	 * 说明：用于执行插入、更新、删除的sql语句，当受影响的行数为0和执行失败时返回false
	 * 
	 * @param sql
	 * @return boolean 成功返回true，失败返回false
	 */
	public boolean excSql(String sql) {
		if (null == con) {
//			log.info("尚未连接数据库");
			return false;// 数据库未连接
		}
		return mysql.excSql(sql);
	}

	/**
	 * 按指定的sql，查询表中所有数据，返回表中所有列
	 * 
	 * @param sql
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> queryBySql(String sql) {
		return getMapFromResult(querySql(sql));
	}

	/**
	 * 按指定的表名，查询表中所有数据，返回表中所有列
	 * 
	 * @param tablename
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> queryFromTable(String tablename) {
		String sql = "select * from " + tablename;
		return getMapFromResult(querySql(sql));
	}

	/**
	 * 按指定的表名，查询表中所有数据，返回表中所有列
	 * 
	 * @param tablename
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> queryFromTable(String tablename,
			List<String> keyList) {
		return getMapByKeyList(queryFromTable(tablename), keyList);
	}

	/**
	 * 按指定的查询条件和表名，查询表中所有数据，返回表中所有列
	 * 
	 * @param tablename
	 * @param wherestr
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> queryFromTableByIf(
			String tablename, String wherestr) {
		String sql = "select * from " + tablename + " where " + wherestr;
		return getMapFromResult(querySql(sql));
	}

	/**
	 * 按指定的查询条件和表名.查询表中记录，返回第一条记录的指定列的值
	 * 
	 * @param tablename
	 * @param wherestr
	 * @param keyname
	 * @return String
	 */
	public String queryFromTableByIf(String tablename, String wherestr,
			String keyname) {
		Map<String, String> map = new TreeMap<String, String>();
		try {
			map = queryFromTableByIf(tablename, wherestr).get(0);
			return map.get(keyname).toString();
		} catch (Exception e) {
			log.error("查询异常");
			log.error(e.getMessage());
			return "";
		}
	}

	/**
	 * 按指定的sql.查询表中记录，返回第一条记录的指定列的值
	 * 
	 * @param sql
	 * @return String
	 */
	public String queryTableBysqlMax(String sql) {
		String max = "";
		try {
			ResultSet res = querySql(sql);
			res.next();
			max = res.getString(1);
		} catch (SQLException e) {
			log.error("查询最大值失败！");
			log.error(e.getMessage());
		}
		return max;
	}

	/**
	 * 按指定的查询条件和表名，查询表中所有数据，返回指定的列
	 * 
	 * @param tableName
	 * @param whereStr
	 * @param keyList
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> getMapFromSql(String tableName,
			String whereStr, List<String> keyList) {
		return getMapByKeyList(queryFromTableByIf(tableName, whereStr), keyList);
	}

	/**
	 * 按指定的查询条件和表名，查询表中所有数据，返回Map中key对应的列
	 * 
	 * @param tablename
	 * @param wherestr
	 * @param expMap
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> getMapFromSql(String tablename,
			String wherestr, Map<String, String> expMap) {
		List<String> keyList = StringUtil.getKeyListFromMap(expMap);
		return getMapFromSql(tablename, wherestr, keyList);
	}

	// 私有方法

	private ResultSet querySql(String sql) {
		if (null == con) {
//			log.info("尚未连接数据库");
			return null;// 数据库未连接
		}
		return mysql.querySql(sql);
	}

	/**
	 * 说明：判断查询结果集内的记录个数，如果小于1条则返回true
	 * 
	 * @param res
	 *            查询结果集
	 * @return boolean 结果集异常或等于0条返回true，否则返回false
	 */
	private static boolean ResultIsNull(ResultSet res) {
		if (null == res) {
//			log.info("数据库连接异常");
			return true;
		}
		
		try {
			res.last();
			if (res.getRow() == 0) {
				log.info("查询结果集为0条");
				return true;
			} else {
				res.beforeFirst();
				return false;
			}
		} catch (SQLException e) {

			log.error("计算查询结果集个数失败!");
			log.error(e.getMessage());
			return true;
		}

	}

	/**
	 * 查询结果集转换为Map列表
	 * 
	 * @param rs
	 * @return List<Map<String, String>>
	 */
	private static List<Map<String, String>> getMapFromResult(ResultSet rs) {
		
		List<Map<String, String>> mapList = new ArrayList<>();
		Map<String, String> map;
		ResultSetMetaData rsmd;
		if (null == rs) {
//			log.info("数据库连接异常");
			return mapList;
		}
		
		
		try {
			if (MysqlUtil.ResultIsNull(rs)) {
				log.error(" 查询结果集为空");
				return mapList;
			}
			rsmd = rs.getMetaData();
			while (rs.next()) {
				map = new TreeMap<String, String>();

				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String key = "";
					String value = "";
					key = rsmd.getColumnName(i).toString();
					try {
						value = rs.getObject(i).toString();
					} catch (Exception e) {
						// log.error(" 查询结果中解析字段 "+key+"值异常");
						// log.error(e.getMessage());
					}
					map.put(key, value);
				}
				mapList.add(map);
			}
		} catch (SQLException e) {
			log.error("查询异常");
			log.error(e.getMessage());
		}
		return mapList;
	}

	/**
	 * 根据指定的key列表，从map中取出新的map
	 * 
	 * @param mapTmpList
	 * @param keyList
	 * @return List<Map<String, String>>
	 */
	private static List<Map<String, String>> getMapByKeyList(
			List<Map<String, String>> mapTmpList, List<String> keyList) {

		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		Map<String, String> mapTmp;

		if (null == keyList || keyList.size() < 1) {
			log.error("要获取的列信息为空，请检查");
			return mapList;
		}
		if (null == mapTmpList || mapTmpList.size() < 1) {
			return mapList;
		}

		for (int i = 0; i < mapTmpList.size(); i++) {// 处理多条查询结果
			mapTmp = new TreeMap<>();
			for (int j = 0; j < keyList.size(); j++) {// 获取指定列信息
				String key = keyList.get(j);
				String value = StringUtil.getValueFromMapByKey(
						mapTmpList.get(i), key);
				mapTmp.put(key, value);
			}
			mapList.add(mapTmp);
		}

		return mapList;

	}
}
