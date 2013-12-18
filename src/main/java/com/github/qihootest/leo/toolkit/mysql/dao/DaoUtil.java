package com.github.qihootest.leo.toolkit.mysql.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.github.qihootest.leo.toolkit.mysql.MysqlUtil;
import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * 说明：数据库通用操作类，
 * 当前只支持数据类型为String
 * 所有实现ITable接口的实体类都可使用此类的方法完成增删改查
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/> 2013-01-08
 *
 */
public class DaoUtil {

	private static LogUtil log=LogUtil.getLogger(DaoUtil.class);//日志记录
	
	/**
	 * 说明：根据传入的实体类，返回对应数据库表所有记录List
	 * @param table 实体类对象
	 * @return List<ITable>实体类对应数据库表的所有记录List
	 */
	public static List<ITable> getAllInfo(ITable table){		
		String tablename=getNameOfClass(table);
		List<String> keyList=getCoList(table);	
		List<Map<String, String>>resMapList=MysqlUtil.queryFromTable(tablename, keyList);
		return getTableFromMap(resMapList,table);		
	}
	
	/**
	 * 说明：根据传入的实体类，按指定的查询条件返回对应数据库表记录List
	 * @param where
	 * @param table
	 * @return List<ITable>
	 */
	public static List<ITable>  getInfoByKey(String where,ITable table) {
		String tablename=getNameOfClass(table);
		List<String> keyList=getCoList(table);	
		List<Map<String, String>>resMapList=MysqlUtil.getMapFromSql(tablename, where,keyList);
		return getTableFromMap(resMapList,table);
	}

	/**
	 * 说明：根据传入的实体类，按自定义sql语句返回对应数据库表记录List
	 * @param sql
	 * @param table
	 * @return List<ITable>
	 */
	public static List<ITable>  getInfoBySql(String sql,ITable table) {
		List<Map<String, String>>resMapList=MysqlUtil.queryBySql(sql);
		return getTableFromMap(resMapList,table);		
	}
	
	/**
	 * 说明：根据传入的实体类，按指定列名返回此列的最大值
	 * @param key
	 * @param cls
	 * @return String
	 */
	public static String getMaxInfoByKey(String key,ITable table) {
		String tablename=getNameOfClass(table);
		String max="";
		String sql="SELECT MAX("+key+") from "+tablename;
		max=MysqlUtil.queryTableBysqlMax(sql);		
		if (null==max || max.length()<1) {
			max="0";
		}
		return max;
	}
	/**
	 * 说明：根据传入的实体类，按指定的条件更新一条记录
	 * @param key
	 * @param value
	 * @param cls
	 * @return boolean
	 */
	public static boolean updateInfo(String key,String value,ITable table) {
		boolean flag=false;
		String sql=getUpdateSql(key, value, table);
		flag=MysqlUtil.excSql(sql);		
		return flag;
	}
	
	/**
	 * 说明：根据传入的实体类，插入到数据库对应的表中一条数据
	 * @param cls
	 * @return boolean 插入成功返回true，失败返回false
	 */
	public static boolean insterInfo(ITable table) {
		boolean flag=false;
		String sql=getInsertSql(table);
		flag=MysqlUtil.excSql(sql);		
		return flag;
	}

	/**
	 * 说明：根据传入的实体类，按指定的条件删除对应数据库表中一条记录
	 * @param key
	 * @param value
	 * @param cls
	 * @return  boolean 删除成功返回true，失败返回false
	 */
	public static boolean deleteInfoByKey(String key,String value,ITable table) {
		boolean flag=false;
		String sql="DELETE from "+getNameOfClass(table)+" where "
						+key+"='"+value+"'";		
		
		flag=MysqlUtil.excSql(sql);		
		return flag;
	}

	
	/**
	 * 说明：根据实体类对象，获取实体类对应table的所有列名
	 * @param 实体类对象
	 * @return List<String> table的所有列名的List
	 */
	public static  List<String> getCoList(ITable table) {
		 List<String> colList=new ArrayList<String>();
		 Field[] field=table.getClass().getDeclaredFields();
		for (int i = 0; i < field.length; i++) {
				Field f= field[i];
				f.setAccessible(true);
				try {
					colList.add(f.getName());
				} catch (IllegalArgumentException e1) {
					colList=null;
					log.error("获取实体类"+table.getClass().getSimpleName()+"的第"+(i+1)+"个字段失败");
					log.error(e1.getMessage());
				}
			}
		 return colList;
	}
	
	/**
	 * 说明：根据key-value键值对，对实体类赋值
	 * @param key 
	 * @param value
	 * @param 实体类对象
	 * @return boolean  赋值成功返回true，失败返回false
	 */
	public static boolean set(String key,String value,ITable table) {
		boolean flag=false;
		List<String> coList= getCoList(table);
		for (int i = 0; i <coList.size(); i++) {
			if (key.equals(coList.get(i))) {
				try {
					Class<?>[] parameterTypes = new Class[1];  
					Field field = table.getClass().getDeclaredField(key); 
					parameterTypes[0] = field.getType(); 
					Method m = (Method) table.getClass().getMethod(getMethodByName("set", key),parameterTypes);
					m.invoke(table, value);		
					flag=true;
					break;
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
					flag=false;
					log.error("对实体类"+table.getClass().getSimpleName()+"的字段"+key+"赋值失败");
					log.error(e.getMessage());
					break;
					
				}
			}
		}
		return flag;
	}
	
	/**
	 * 说明：根据key值，从实体类中获取对应的value值
	 * @param key
	 * @param table 实体类对象
	 * @return String 实体类中key对应的value值
	 */
	public static String get(String key,ITable table) {
		String res="";
		List<String> coList= getCoList(table);
		for (int i = 0; i <coList.size(); i++) {
			if (key.equals(coList.get(i))) {
				try {
					Method m = (Method) table.getClass().getMethod(getMethodByName("get", key));					
					res=m.invoke(table).toString();
					break;
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					res=null;
					log.error("从实体类"+table.getClass().getSimpleName()+"的字段"+key+"取值失败");
					log.error(e.getMessage());
					break;					
				}
			}
		}
		return res;
	}
	
	//私有方法
	/**
	 * 说明： 根据类对象获取此类的名称，用于获取实体类名称，代表对应数据库table名称
	 * @param 实体类对象
	 * @return  String table名称
	 */
	private static  String getNameOfClass(Object cls) {
		 return cls.getClass().getSimpleName();		
	}
	
	
	
	/**
	 * 说明：根据传入的参数，拼接用于数据库update的sql语句
	 * @param key 列名
	 * @param value 对应的值
	 * @param cls  实体类对象
	 * @return String 返回拼接后的sql语句
	 */
	private static String getUpdateSql(String key, String value, ITable table) {
		String sql="update "+getNameOfClass(table)+" set ";
		List<String> colList=getCoList(table);
		
		for (int i = 0; i < colList.size(); i++) {
			try {
				Method m = (Method) table.getClass().getMethod(getMethodByName("get", colList.get(i)));		
				String temp=m.invoke(table).toString();
				sql+=colList.get(i)+" = '"+temp+"',";
				m=null;
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("拼接表"+table.getClass().getSimpleName()+"的列"+colList.get(i)+"的update语句异常");
				log.error(e.getMessage());
			}		
			
		}
		sql=sql.substring(0, sql.length()-1);
		sql+=" where "+key+" = "+value;
		return sql;
	}

	/**
	 * 说明：根据传入的实体类，拼接用户数据库插入的sql语句
	 * @param cls 实体类对象
	 * @return  String 拼接后的sql语句
	 */
	private static String getInsertSql(ITable table) {
		String sql="insert into "+getNameOfClass(table);
		List<String> keyList=getCoList(table);
		String key="";
		String value="";
		
		for (int i = 0; i < keyList.size(); i++) {
			try {
				Method m = (Method) table.getClass().getMethod(getMethodByName("get", keyList.get(i)));		
				String temp=m.invoke(table).toString();
				key+=keyList.get(i)+",";
				value+="'"+temp+"',";
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("拼接表"+table.getClass().getSimpleName()+"的列"+keyList.get(i)+"的Insert语句异常");
				log.error(e.getMessage());
			}			
		}
		key=key.substring(0, key.length()-1);
		value=value.substring(0, value.length()-1);
		sql+=" ("+key+") values("+value+")";
		return sql;
	} 

	private static List<ITable> getTableFromMap(List<Map<String, String>> resMapList,ITable table) {
		List<ITable>tableList=new ArrayList<>();
		if (null==resMapList || resMapList.size()<1) {
			return tableList;
		}
		
		for (int i = 0; i < resMapList.size(); i++) {
			ITable tableTmp=getNewInstanceByClassdeep(table);
			Map<String, String> mapTmp=new TreeMap<String, String>();
			//把查询结果赋值给实体类
			mapTmp=resMapList.get(i);
			for (Entry<String, String> entry : mapTmp.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				set(key, value, tableTmp);
			}
			//赋值给实体类完毕
			tableList.add(tableTmp);
		}		
		return tableList;
	}
	
	
	
	/**
	 * 说明：内部使用，根据传入的set或get、实体类属性名称拼接对应的set或get方法名
	 * @param method
	 * @param fildeName
	 * @return String 对应的set或get方法名
	 */
	private static String getMethodByName(String method,String fildeName) {
		return method+getFirstCapitalized(fildeName);
	}
	
	/**
	 * 说明：内部使用，把传入的字符串首字母大写并返回
	 * @param fildeName
	 * @return  String 首字母大写之后的字符串
	 */
	private static String getFirstCapitalized(String fildeName) {
		  byte[] items = fildeName.getBytes();
		  items[0] = (byte) ((char) items[0] - 'a' + 'A');
		  return new String(items);
	}
	
	/**
	 * 说明：内部使用，根据传入的类，返回new之后的此类的实例化对象
	 * @param cls 类对象
	 * @return   ITable 实例化之后的类对象
	 */
	private static ITable getNewInstanceByClassdeep(ITable table) {        
		ITable table2;	
		 
		try {
			Class<?> clazz = Class.forName(table.getClass().getName());
			table2=(ITable) clazz.newInstance();
			return table2;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage());
			return null;
		}		
		
	}


	
}
	
