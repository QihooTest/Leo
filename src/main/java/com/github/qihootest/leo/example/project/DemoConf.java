package com.github.qihootest.leo.example.project;

public class DemoConf {

	public static String SecretKey="参与签名的密钥";
	public static String Host = ""; //配置全局Host,使用时excel中URL值应为“host”关键字
	
//	public static String heard1 = "value1";  //可以在此处添加多个全局heard,key必须包含于HeardPara数组中
	
	public static final String[]  HeardPara ={""};
	public static final String[] GetPara = {"para1","para2","para3","para4","para5","sign"};
	public static final String[] PostPara = {"para1","para2","para3","para4","para5","sign"};
	public static final String ENCODING="UTF-8";
	
}
