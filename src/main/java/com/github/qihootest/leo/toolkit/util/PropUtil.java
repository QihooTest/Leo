package com.github.qihootest.leo.toolkit.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;


/**
 * Properties配置文件相关处理
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 */
public class PropUtil {
	private static LogUtil log= LogUtil.getLogger(PropUtil.class);

	/**
	 * 获取指定位置的property文件中property文件信息
	 * @param fullFilePath property文件的全路径信息
	 * @return Properties 属性文件的信息
	 * */
	public static Properties getPropertyInfo(String fullFilePath){
		Properties properties=new Properties();
		try {
			properties.load(FileUtil.readToFileInputStream(fullFilePath));
		} catch (IOException e) {
			properties=null;
			log.error(e.getMessage());
		}
		return properties;
	}
	
	/**获得properties文件里面指定属性名字的属性值，如果不存在该属性或者该属性的值为""，抛出异常
	 * @param properties 给定的properties文件
	 * @param propertiesName 属性名字
	 * @return String 属性值
	 *  */
	public static String getValue(Properties properties,String propertiesName){
		String value=properties.getProperty(propertiesName);
		if(value==null||"".equalsIgnoreCase(value)){
			log.error("获取的属性值不存在或者为空串!");
		}return value;
	}
	/**获得properties文件里面指定属性名字的属性值，如果不存在该属性或者该属性的值为""，返回默认值
	 * @param properties 给定的properties文件
	 * @param propertiesName 属性名字
	 * @param defaultValue 在属性值不存在或者为""的时候的返回的默认值
	 * @return String 属性值
	 *  */
	public static String getValue(Properties properties,String propertiesName,String defaultValue){
		String value=properties.getProperty(propertiesName);
		if(value==null||"".equalsIgnoreCase(value)){
			return defaultValue;
		}return value;
	}
	/**通常在一个properties文件里面一个属性会有很多值，他们之间用','隔开，那么这个方法的作用就是获得所有的值<br>
	 * 属性值为""的不加入返回的列表，如果属性值不存在或者所有的值都为"",抛出异常
	 * @param properties 给定的properties文件
	 * @param propertiesName 属性名字
	 * @return List<String> 属性值列表*/
	public static List<String> getValueList(Properties properties,String propertiesName){
		ArrayList<String>valuesList;
		String values=getValue(properties, propertiesName);
		valuesList=new ArrayList<String>(values.length());
		String[]propertieValues=values.split(",");
		for(String value:propertieValues){
			if(value.trim().equalsIgnoreCase(""))
				continue;
			valuesList.add(value.trim());
		}
		if(valuesList.isEmpty()){
			log.error("获取的属性列表所有的属性都为空串!");
		}return valuesList;
	}
	
	/**
	 * 从配置文件中读取所有信息到map表中
	 * @param properties
	 * @return Map<String, String>
	 */
	public static Map<String, String> getAllInfoToMap(Properties properties) {
		Map<String, String> map=new TreeMap<String, String>();
		try {
			Iterator<Entry<Object, Object>> it=properties.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Object, Object> entry=(Map.Entry<Object, Object>)it.next();
			    String key = entry.getKey().toString();			    
			    String value = entry.getValue().toString();
			    map.put(key, value);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return map;
	}
	
}
