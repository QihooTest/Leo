package com.github.qihootest.leo.toolkit.util;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.github.qihootest.leo.toolkit.TkConf;

/**
 * 自定义日志
 *	@author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 */
public class LogUtil {
	private Logger log ;
	private LogUtil(String name) {
		configLogProperties();
		log = Logger.getLogger(name);
	}
	private LogUtil(Class<?> clazz){
		configLogProperties();
		log = Logger.getLogger(clazz);
	}
	
	/**
	 * 
	 * @param name
	 * @return LogUtil
	 */
	public static LogUtil getLogger(String name){
		return new LogUtil(name);
	}
	/**
	 * 
	 * @param clazz
	 * @return LogUtil
	 */
	public static LogUtil getLogger(Class<?> clazz){
		return new LogUtil(clazz);
	}
	
	/**
	 * error级别日志
	 * @param message
	 */
	public void error(Object message){
		log.error(message);
	}
	/**
	 * info 级别日志
	 * @param message
	 */
	public void info(Object message){
		log.info(message);
	}
	/**
	 * debug级别日志
	 * @param message
	 */
	public void debug(Object message){
		log.debug(message);
	}
	/**
	 * warn级别日志
	 * @param message
	 */
	public void warn(Object message){
		log.error(message);
	}
	
	/**
	 * 读取日志配置文件
	 */
	private  void configLogProperties(){
		try {
			TkConf.writeConf();
			PropertyConfigurator.configure(TkConf.Log4jConf);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	
}
