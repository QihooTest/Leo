package com.github.qihootest.leo.toolkit.dbunit;


import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlMetadataHandler;

import com.github.qihootest.leo.toolkit.dbunit.data.excel.XlsDataSet;
import com.github.qihootest.leo.toolkit.mysql.ConnMysql;
import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * 从数据库中导出到excel、xml文件、创建dbunit的DatabaseConnection、IDataSet
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/> 2013-02-07
 *
 */
public class DbUnitUtil {
	
private static final LogUtil log = LogUtil.getLogger(DbUnitUtil.class);
 
//针对Mysql数据库专用的

/**
 * 从数据库中读取数据写入到文件中，支持excel和xml格式，以文件后缀名区分
 * @param destFilePath 写入文件的路径
 * @param mysql 连接MySql数据库的实例
 * @param querySqlsSplitBySemicolon 查询的sql语句，支持多个，使用;分开
 */
public static void writeToFileFromMysql(String destFilePath,ConnMysql mysql,String querySqlsSplitBySemicolon){
	writeToFileFromDataBase(destFilePath,mysql.getConnection(),querySqlsSplitBySemicolon);
}

/**
 * 根据connection和查询sql语句，返回ITable
 * @param mysql  mysql数据库连接实例
 * @param resultName 查询结果集对应的名称
 * @param querySql 单个sql查询语句
 * @return ITable
 */
public static ITable getTableFromMysql(ConnMysql mysql,String resultName,String querySql){
	return getTableFromDataBase(mysql.getConnection(), resultName, querySql);
}

/**
 * 根据connection和查询sql语句，返回IDataSet
 * @param mysql mysql数据库连接实例
 * @param querySqlsSplitBySemicolon 查询的sql语句，支持多个，使用;分开
 * @return  IDataSet
 */
public static IDataSet getDataSetFromMysql(ConnMysql mysql,String querySqlsSplitBySemicolon){
	return getDataSetFromDataBase(mysql.getConnection(), querySqlsSplitBySemicolon);
}

/**
 * 从Mysql实例转换为DatabaseConnection
 * @param mysql
 * @return DatabaseConnection
 */
public static DatabaseConnection getDatabaseConnectionFromMysql(ConnMysql mysql) {
	return getDatabaseConnection(mysql.getConnection());
}


//根据jdbc的connection连接,参照上述Mysql的，其它数据库类型Oracle、MSSql等可复用

/**
 * 从数据库中读取数据写入到文件中，支持excel和xml格式，以文件后缀名区分
 * @param databaseConnection  使用的IDatabaseConnection数据连接
 * @param destFilePath 写入文件的路径
 */
public static void writeToFileFromDataBase(IDatabaseConnection databaseConnection,String destFilePath){
	IDataSet dataSet=null;
	
	try {
		dataSet=databaseConnection.createDataSet();
	} catch (Exception e) {
		log.error("未获取到测试集");
		log.error(e.getMessage());
		return;
	}
	writeToFileFromDataSet(dataSet,destFilePath);
}

/**
 * 从数据库中读取数据写入到文件中，支持excel和xml格式，以文件后缀名区分
 * @param destFilePath 写入文件的路径
 * @param databaseConnection  使用的IDatabaseConnection数据连接
 * @param querySqlsSplitBySemicolon  查询的sql语句，支持多个，使用;分开
 */
public static void writeToFileFromDataBase(String destFilePath,IDatabaseConnection databaseConnection,String querySqlsSplitBySemicolon){
	writeToFileFromDataSet(getDataSetFromDataBase(databaseConnection, querySqlsSplitBySemicolon),destFilePath);
}

/**
 * 从数据库中读取数据写入到文件中，支持excel和xml格式，以文件后缀名区分
 * @param destFilePath 写入文件的路径
 * @param connection  使用的数据连接
 * @param querySqlsSplitBySemicolon  查询的sql语句，支持多个，使用;分开
 */
public static void writeToFileFromDataBase(String destFilePath,Connection connection,String querySqlsSplitBySemicolon){
	writeToFileFromDataSet(getDataSetFromDataBase(connection, querySqlsSplitBySemicolon),destFilePath);
}

/**
 * 把数据集写入到文件中
 * @param dataSet
 * @param destFilePath
 */
public static void writeToFileFromDataSet(IDataSet dataSet,String destFilePath) {
	File destFile = new File(destFilePath);
	try {
			String destFileName = destFile.getName();
			//转换为excel格式
			if(destFileName.endsWith(".xls")){
				XlsDataSet.write(dataSet, new FileOutputStream(destFile));
			}else if(destFileName.endsWith(".xml")){
				FlatXmlDataSet.write(dataSet, new FileOutputStream(destFile));
			}else{
				log.error("文件格式不是xls或者xml，不支持");
				return;
			}
			log.info("写入数据到文件 : "+destFile.getAbsolutePath());
	} catch (Exception e) {
			log.error("写入数据到文件失败");
			log.error(e.getMessage());
	}
}

/**
 * 根据connection和查询sql语句，返回IDataSet
 * @param connection 使用的数据连接
 * @param querySqlsSplitBySemicolon 查询的sql语句，支持多个，使用;分开
 * @return  IDataSet
 */
public static IDataSet getDataSetFromDataBase(Connection connection,String querySqlsSplitBySemicolon){
	return getDataSetFromDataBase(getDatabaseConnection(connection), querySqlsSplitBySemicolon);
}

/**
 * 根据databaseConnection和多个查询sql语句，返回IDataSet
 * @param databaseConnection 使用的IDatabaseConnection类型数据连接
 * @param querySqlsSplitBySemicolon 查询的sql语句，支持多个，使用;分开
 * @return queryDataSet
 */
public static IDataSet getDataSetFromDataBase(IDatabaseConnection databaseConnection,String querySqlsSplitBySemicolon){
	//分割多个sql语句
	String[] querySqls = querySqlsSplitBySemicolon.split(";");
	QueryDataSet queryDataSet=null;
	try {
		queryDataSet = new QueryDataSet(databaseConnection);
		//处理多个语句情况
		for (int i = 0; i < querySqls.length; i++) {
			queryDataSet.addTable(getTableNameFromQuerySql(querySqls[i]), querySqls[i]);
		}
	} catch (Exception e) {
		log.error("查询数据失败!");
		log.error(e.getMessage());
	}
	return queryDataSet;
}

/**
 * 根据connection和查询sql语句，返回ITable
 * @param connection
 * @param resultName 指定查询结果集对应的表名
 * @param querySql 单个sql查询语句
 * @return ITable
 */
public static ITable getTableFromDataBase(Connection connection,String resultName,String querySql){
	//分割多个sql语句
	ITable table=null;
	try {
		table=getDatabaseConnection(connection).createQueryTable(resultName, querySql);
	} catch (Exception e) {
		log.error("查询数据失败!");
		log.error(e.getMessage());
	}
	return table;
}

/**
 * 从connection转换为DatabaseConnection
 * @param connection
 * @return DatabaseConnection
 */
public static DatabaseConnection getDatabaseConnection(Connection connection) {
	DatabaseConnection databaseConnection = null;
	try {
		databaseConnection= new DatabaseConnection(connection);
		DatabaseConfig config = databaseConnection.getConfig();
		//数据库为mysql
		if ("MySQL".equalsIgnoreCase(connection.getMetaData()
				.getDatabaseProductName())) {
			config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER,
					new MySqlMetadataHandler());
			config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, 
					new MySqlDataTypeFactory());
		} //else if ("Oracle".equalsIgnoreCase(connection.getMetaData()
//				.getDatabaseProductName())) {
//			config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
//					new Oracle10DataTypeFactory());
//		}
	} catch (DatabaseUnitException | SQLException e) {
		log.error("创建dbunit的数据库连接失败");
		log.error(e.getMessage());
		return null;
	}
	
	return databaseConnection;
}



	//私有方法
	
	/**
	 * 从sql语句中查找到表名，第一个from之后的表名
	 * @param querySql
	 * @return String
	 */

	private static String getTableNameFromQuerySql(String querySql){
		String tableName = null;
		String[] array = querySql.split(" ");
		for (int i = 0; i < array.length; i++) {
			if("From".equalsIgnoreCase(array[i].trim())){
				tableName = array[i+1].trim();
				break;
			}
		}
		return tableName;
		
	}
	
	
}
