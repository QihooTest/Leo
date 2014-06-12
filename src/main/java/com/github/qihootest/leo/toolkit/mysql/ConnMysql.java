package com.github.qihootest.leo.toolkit.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.qihootest.leo.toolkit.util.LogUtil;

import com.mysql.jdbc.Statement;

/**
 * 说明：连接数据库配置信息
 * 
 * @author lianghui (lianghui@360.cn) 2013-01-10
 * 
 */
public class ConnMysql {

	private static Connection con = null; // 创建用于连接数据库的Connection对象
	private static LogUtil log = LogUtil.getLogger(ConnMysql.class);// 日志记录

	/** IP地址 */
	public String ip = "127.0.0.1";
	/** 端口号 */
	public String port = "3306";
	/** 数据库名 */
	public String dataName = "";
	/** 用户名 */
	public String userName = "root";
	/** 密码 */
	public String passWord = "";

	/**
	 * 默认构造方法
	 */
	public ConnMysql() {

	}

	/**
	 * 构造方法
	 * 
	 * @param ip
	 * @param port
	 * @param dataName
	 * @param userName
	 * @param passWord
	 */
	public ConnMysql(String ip, String port, String dataName, String userName,
			String passWord) {
		this.ip = ip;
		this.port = port;
		this.dataName = dataName;
		this.userName = userName;
		this.passWord = passWord;
	}

	/**
	 * 说明：数据库连接信息，并创建数据库连接 编码为UTF-8
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {
		try {
			// 如果连接未释放，先断开连接
			if (con != null) {
				// con.close();
				// con=null;
				// log.info("手动关闭数据库连接成功");
				return con;// 未关闭时，不再重新连接，直接返回
			}
			Class.forName("com.mysql.jdbc.Driver");// 加载Mysql数据驱动
			con = DriverManager
					.getConnection(
							"jdbc:mysql://"
									+ ip
									+ ":"
									+ port
									+ "/"
									+ dataName
									+ "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=round",
							userName, passWord);// 创建数据连接
		} catch (Exception e) {
			log.error("数据库连接失败");
			log.error(e.getMessage());
		}
		return con; // 返回所建立的数据库连接

	}

	/**
	 * 说明：关闭数据库连接
	 */
	public void closedConn() {

		try {
			if (con != null)
				con.close();
			con = null;
			log.info("关闭数据库连接成功【" + this.ip + "_" + this.port + "_"
					+ this.dataName + "】");
		} catch (SQLException e) {
			log.error("关闭数据库连接失败【" + this.ip + "_" + this.port + "_"
					+ this.dataName + "】");
			log.error(e.getMessage());
		}
	}

	/**
	 * 说明：用于执行插入、更新、删除的sql语句，当受影响的行数为0和执行失败时返回false
	 * 
	 * @param sql
	 * @return boolean 成功返回true，失败返回false
	 */
	public boolean excSql(String sql) {
		if (con == null)
			getConnection(); // 连接到数据库

		try {
			Statement st = (Statement) con.createStatement(); // 创建用于执行静态sql语句的Statement对象
			int counts = st.executeUpdate(sql); // 执行操作的sql语句
			if (0 == counts) {
				log.info("执行成功,，共0条数据受到影响，没有完成操作！：SQL语句-->【" + sql + "】");
				return false;
			}
			log.info("执行成功,共" + counts + "条数据受到影响:" + "SQL语句-->【" + sql + "】");
			return true;
		} catch (SQLException e) {
			log.error("执行失败：SQL语句-->【" + sql + "】");
			log.error(e.getMessage());
			return false;
		}

	}

	/**
	 * 说明：用户select的查询语句，返回查询结果集
	 * 
	 * @param  sql
	 * @return ResultSet 查询结果集
	 */
	public ResultSet querySql(String sql) {

		ResultSet rs = null;
		if (con == null)
			getConnection(); // 连接到数据库
		try {
			Statement st = null; // 创建用于执行静态sql语句的Statement对象，st属局部变量
			st = (Statement) con
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(sql); // 执行sql查询语句，返回查询数据的结果集
			log.info("执行成功：SQL查询语句-->【" + sql + "】");
		} catch (SQLException e) {
			log.error("执行失败：SQL查询语句-->【" + sql + "】");
			log.error(e.getMessage());
			rs = null;
		}
		return rs;
	}

}
