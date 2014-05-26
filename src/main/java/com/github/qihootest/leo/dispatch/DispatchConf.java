package com.github.qihootest.leo.dispatch;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;


/**
 * 配置文件
 * @author lianghui (lianghui@360.cn)
 *
 */
public class DispatchConf {
	
	/**
	 * 类文件输出路径
	 */
	public static String ClsPath = getClassPath();
	/**
	 * 项目根目录
	 */
	public static String RootPath = getRootPath();
	/**
	 * suites默认存储目录
	 */
	public static String SuitsXmlPath = RootPath+"leo/dispatch/suites/";//getPropValue("SuitsXmlPath", "tba/suites/");
	/**
	 * testng默认输出目录
	 */
	public static String TestNgOutPath = RootPath+"leo/dispatch/testng-out/";
	/**
	 * html测试报告默认输出目录
	 */
	public static String HtmlReportOutPath = RootPath+"leo/dispatch/report/"+System.currentTimeMillis()+"/";
	/**
	 * html测试报告默认标题
	 */
	public static String HtmlReportTitle = "测试报告";
	/**
	 * testNGXslt插件配置文件路径
	 */
	public static String TestNGXsltFile = RootPath+"leo/dispatch/testng-results.xsl";
	
	/**
	 * 写配置文件，如果不存在则创建
	 * @return boolean 已存在或创建失败时返回false  创建成功返回true
	 */
	public static boolean writeConf() {
		if (!new File(DispatchConf.TestNGXsltFile).exists()) {
			return copyFile(DispatchConf.class.getResourceAsStream("/testng-results.xsl"),new File(TestNGXsltFile));
		}
		return false;
	}
	
	/**
	 * 删除默认临时目录
	 */
	public static void delTmpPath() {
		try {
			// 清空xml文件生成目录
			FileUtils.deleteDirectory(new File(SuitsXmlPath));
			// TestNG输出目录
			FileUtils.deleteDirectory(new File(TestNgOutPath));
		} catch (IOException e) {
//			e.printStackTrace();
		}
		
	}
	
	private static String getClassPath() {
		return DispatchConf.class.getClassLoader().getResource("").toString();
	}
	private   static String getRootPath() {
		String path="";
		if (DispatchConf.class.getClassLoader().getResource("").toString().indexOf("WEB-INF")!=-1) {
			path=DispatchConf.class.getClassLoader().getResource("").toString().substring(6, DispatchConf.class.getClassLoader().getResource("").toString().indexOf("WEB-INF"));
			path=path+"/";
		}else {
//			path = GlobalSettings.class.getClassLoader().getResource("").toString();
			String temp=System.getProperty("user.dir");
			temp=temp.replace("\\","/");
			path=temp+"/";
		}
		return path;
	}
	
	@SuppressWarnings("unused")
	private static String getPropValue(String key, String defaultValue) {
		return getProperties().getProperty(key, defaultValue);
	}
	
	private static Properties getProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream file = new FileInputStream("conf.properties");
			prop.load(new InputStreamReader(file,"UTF-8"));
			file.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return prop;
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
