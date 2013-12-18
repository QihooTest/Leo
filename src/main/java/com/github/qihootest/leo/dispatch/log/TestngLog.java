package com.github.qihootest.leo.dispatch.log;

import org.testng.Reporter;
import com.github.qihootest.leo.toolkit.util.LogUtil;



/**
 * 封装TestNG log记录
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 *
 */
public class TestngLog {
	private static LogUtil log=LogUtil.getLogger(TestngLog.class);//日志记录
	private static boolean OutputTestNGLog=true;//是否输出到TestNG Log日志
	/**
	 * 说明：记录用例开始，写入TestNG日志
	 * 
	 * @param testCaseName 用例名称
	 */
	public static void CaseStart(String testCaseName) {
		TestNGLog("用例-------【" + testCaseName
				+ "】----------开始执行---------------");
	}

	/**
	 * 说明：记录用例结束，写入TestNG日志
	 * 
	 * @param testCaseName 用例名称
	 */
	public static void CaseEnd(String testCaseName) {
		TestNGLog("用例-------【" + testCaseName
				+ "】----------执行完毕---------------");
		TestNGLog(
				"///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
	}

	/**
	 * 说明：把指定信息写入TestNG日志
	 * @param str 字符串
	 */
	public static void Log(String str) {
		TestNGLog(str);
	}

	/**
	 * 设置是否输出TestNG执行记录到TestNG日志中
	 * @param outputTestNGLog 默认true
	 */
	public static void setOutputTestNGLog(boolean outputTestNGLog) {
			OutputTestNGLog = outputTestNGLog;
	}
	
	private static void TestNGLog(String info) {
		log.info(info);
		if (OutputTestNGLog) {
			Reporter.log(info,false);
		}
	}
}
