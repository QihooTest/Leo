package com.github.qihootest.leo.dispatch.run;

import com.github.qihootest.leo.dispatch.report.TestReport;
import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * 说明：封装用例执行 单例模式
 * 
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/> 2012-12-04
 * 
 */
public class TestngRunSingle {
	private LogUtil log=LogUtil.getLogger(TestngRunSingle.class);//日志记录
	private static TestngRunSingle single;
	private static boolean Flag;//是否运行任务标识，为true时有任务在运行，false时无任务运行
	private TestRunInfo runInfo ;//任务名称
	private TestReport testReport;//执行完毕后，返回的测试报告信息
		
	//设置为单例模式
	private TestngRunSingle() 
	{
		setFlag(false);
		init();//第一次创建时，初始化属性
	}

	/**
	 * 运行前初始化相关信息
	 */
	private void init(){
		log.info("TestNG执行实例创建");
		runInfo=new TestRunInfo();
		testReport=new TestReport();
	}
	
	/**
	 * 获取类的实例
	 * @return TestngRunSingle
	 */
	public synchronized  static TestngRunSingle getInstance() {
	      if (single == null) {  
	          single = new TestngRunSingle();
	      }  
	     return single;
	}
	
	/**
	 * 设置任务信息
	 * @param runInfo 任务配置信息
	 */
	public void setRunInfo(TestRunInfo runInfo) {
		// 设置任务信息
		this.runInfo=runInfo;
	}
	
	/**
	 * 获取当前任务运行标识
	 * @return boolean
	 */
	public  boolean getFlag() {
		return Flag;
	}
	
	/**
	 * 说明：执行任务返回测试报告信息
	 * @return  TestReport
	 */
	public TestReport execTask() {
		setFlag(true);
		doTask();			
		setFlag(false);
		return this.testReport;		
	}
	
	//内部方法
	private static void setFlag(boolean flag) {
		Flag = flag;
	}

	/**
	 * 说明：执行测试过程
	 * 
	 * @return  boolean
	 */
	private boolean doTask() {
		log.info(this.runInfo.getTaskName()+" 开始执行");
		TestngRun testngRun = new TestngRun();
		testngRun.setRunInfo(runInfo);
		boolean result = testngRun.run();
		testReport = testngRun.getTestReport();
		testngRun = null;
		return result;
	}
}
