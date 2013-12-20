package com.github.qihootest.leo.toolkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 配置文件
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 */
public class TkConf  {
	
	/**
	 * 根目录
	 */
	public static String RootPath = getRootPath();
	/**
	 * log4j配置文件目录
	 */
	public static String Log4jConf = RootPath+"leo/toolkit/log4j.properties";

	/**
	 * 如果配置文件不存在，写入
	 * @return boolean 已存在或写入失败返回false  重新写入成功返回true
	 */
	public static boolean writeConf(){
		if (!new File(Log4jConf).exists()) {
			return copyFile(TkConf.class.getResourceAsStream("/log4j.properties"),new File(Log4jConf));
		}
		return false;
	}

	/**
	 * 获取项目根目录，如果是在Tomcat中运行，则返回部署根目录
	 * @return String
	 */
	private   static String getRootPath() {
		String path="";
		if (TkConf.class.getClassLoader().getResource("").toString().indexOf("WEB-INF")!=-1) {
			path=TkConf.class.getClassLoader().getResource("").toString().substring(6, TkConf.class.getClassLoader().getResource("").toString().indexOf("WEB-INF"));
			path=path+"/";
		}else {
			String temp=System.getProperty("user.dir");
			temp=temp.replace("\\","/");
			path=temp+"/";
		}
		return path;
	}
	
	private static boolean copyFile(InputStream from, File to) {
			try {
				if (! to.getParentFile().exists()) {
				      to.getParentFile().mkdirs();
				 }
				OutputStream os = new FileOutputStream(to);
				 byte[] buffer = new byte[65536];
				 int count = from.read(buffer);
				 while (count > 0) {
				      os.write(buffer, 0, count);
				      count = from.read(buffer);
				 }
				 os.close();
				 return true;
			} catch (IOException e) {
//				e.printStackTrace();
				return false;
			}
		  }
}

