package com.github.qihootest.leo.dispatch.run;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import com.github.qihootest.leo.dispatch.DispatchConf;
import com.github.qihootest.leo.dispatch.report.ExportReportHtml;
import com.github.qihootest.leo.dispatch.report.TestReport;
import com.github.qihootest.leo.dispatch.report.TngCount;
import com.github.qihootest.leo.dispatch.testcase.ICase;
import com.github.qihootest.leo.toolkit.util.CommUtils;
import com.github.qihootest.leo.toolkit.util.FileUtil;
import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * 执行testng，
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/> 2012-10-29
 *
 */
public class TestngRun {
	private LogUtil log=LogUtil.getLogger(TestngRun.class);//日志记录
	private TestNG tng;//运行TestNG
	private TestListenerAdapter listener;//运行的监听器

	private TestRunInfo runInfo;
	private List<String> xmlFileList;
	
	private TestReport testReport;
	
	/*
	 * 构造函数 初始化
	 */
	public  TestngRun() {
		tng=new TestNG();
		listener=new TestListenerAdapter();//定义监听器类型
		tng.addListener(listener);
		xmlFileList=new ArrayList<>();//记录测试使用的xml文件路径列表
		testReport=new TestReport();//记录测试报告测试报告信息
		runInfo=new TestRunInfo();
	}
	
	/**
	 * 设置运行配置信息
	 * @param runInfo
	 */
	public void setRunInfo(TestRunInfo runInfo) {
		this.runInfo=runInfo;
		log.info(this.runInfo.getTaskName()+"配置任务信息成功");
		for (ICase icase : this.runInfo.getCaseList()) {
			addXmlFileList(icase.getCaseList());
		}
	}
	
	/**
	 * 执行用例
	 * @return boolean 执行成功返回true
	 */
	public boolean run() {
		if (getXmlFileList().size()<1) {
			this.testReport.setResNo(-6000);
			this.testReport.setResMsg("Xml文件列表为空");
			log.error(getTaskName()+" :Xml文件列表为空");
			return false;
		}
		//运行相关参数配置
		//检查xlst文件，不存在则创建
		DispatchConf.writeConf();
		tng.setOutputDirectory(getTestNgOut());
		
		try {
			tng.setTestSuites(getXmlFileList());
			tng.run();
		} catch (Exception e) {
//			e.printStackTrace();
			this.testReport.setResNo(-7000);
			this.testReport.setResMsg("执行用例异常： "+e.getMessage());
			log.error(getTaskName()+" :执行用例异常--"+e.getMessage());
			return false;
		}
		//记录测试报告摘要
		createTestReport();
		//输出html测试报告
		if (!createHtmlReport()) {
			this.testReport.setResNo(1);
			this.testReport.setResMsg("任务执行成功，转换Html格式报告出错");
			log.info(getTaskName()+" :任务执行成功，转换Html格式报告出错");
		}else{
			this.testReport.setResNo(0);
			this.testReport.setResMsg("任务执行成功");
			log.info(getTaskName()+" :任务执行成功");
		}
		return true;
	}

	/**
	 * 获取测试报告
	 * @return TestReport
	 */
	public TestReport getTestReport() {
		return this.testReport;
	}
	
	//私有方法
	private String getTestNgOut(){
		String out= this.runInfo.getTestng_OutPut();
		if (out == null || out.length()<1) {
			log.info("设置TestNG输出目录失败："+this.runInfo.getTestng_OutPut());
			log.info("使用默认路径："+DispatchConf.TestNgOutPath);
			return DispatchConf.TestNgOutPath;//默认的TestNG输出目录
		}
		if (!out.endsWith("/")) {
			out=out+"/";
		}
		return out;
	}
	
	private String getTaskName() {
		if (this.runInfo.getTaskName() == null || this.runInfo.getTaskName().length()<1) {
			String taskName = "未命名测试任务"+CommUtils.getRandomStr(5);
			log.info("设置任务名失败："+this.runInfo.getTaskName());
			log.info("使用默认任务名："+taskName);
			return taskName;//默认的TestNG输出目录
		}
		return this.runInfo.getTaskName();
	}
	
	private String getHtmlReportOut(){
		if (this.runInfo.getHtmlReportOutPath() == null || this.runInfo.getHtmlReportOutPath().length()<1) {
			log.info("设置Html输出目录失败："+this.runInfo.getHtmlReportOutPath());
			log.info("使用默认Html输出目录："+DispatchConf.HtmlReportOutPath);
			return DispatchConf.HtmlReportOutPath;//默认的html报告输出目录
		}
		return this.runInfo.getHtmlReportOutPath();
	}
	
	private String getHtmlReportTitle(){
		if (null == this.runInfo.getHtmlReportTitle() || this.runInfo.getHtmlReportTitle().length()<1) {
			log.info("设置Html报告标题失败："+this.runInfo.getHtmlReportTitle());
			log.info("使用默认Html报告标题："+DispatchConf.HtmlReportTitle);
			return  DispatchConf.HtmlReportTitle;
		}
		return this.runInfo.getHtmlReportTitle();
	}
	
	private List<String> getXmlFileList() {
		return xmlFileList;
	}

	private void addXmlFileList(List<String> xmlFileList) {
		if (null==xmlFileList) {
			log.error("添加的Xml文件列表为null，添加失败");
			return;
		}
		for (String xmlFile : xmlFileList) {
			addXmlFile(xmlFile);
		}
	}
	
	private boolean addXmlFile(String xmlPathName){
		if (null==xmlPathName){
			log.error("添加的xml文件为null，添加失败");
			return false;
		}
		if (!FileUtil.getExtensionName(xmlPathName).equals("xml")) xmlPathName=xmlPathName+".xml";
		
		if (FileUtil.isExist(xmlPathName)) {
			log.info("执行队列添加xml文件成功："+xmlPathName);
			this.xmlFileList.add(xmlPathName);
			return true;
		}else{
			log.error("添加的xml文件不存在："+xmlPathName);
			return false;
		}
	}


	private boolean createHtmlReport() {
		if (ExportReportHtml.createHtmlReport(getTestNgOut()+"testng-results.xml", 
				getHtmlReportOut(),getHtmlReportTitle())) {
			
			this.testReport .setHtmlReport(getHtmlReportOut());
			return true;
		}
		return false;
	}
	
	/**
	 * 从监听器中获取需要的报告信息
	 * @param listener
	 */
	private void createTestReport() {
		log.info("从TestNG监听器中获取任务执行的用例信息");
		ArrayList<TngCount> testCountList=new ArrayList<TngCount>();;
		TngCount tngCount=new TngCount();
		tngCount.setName(getTaskName());
		tngCount.setFailed(this.listener.getFailedTests().size());
		tngCount.setPassed(this.listener.getPassedTests().size());
		tngCount.setSkipped(this.listener.getSkippedTests().size());
		this.testReport.setTngSuiteCount(tngCount);
		List<ITestContext> testContextList=this.listener.getTestContexts();
		for (int i = 0; i < testContextList.size(); i++) {
			tngCount=new TngCount();
			tngCount.setName(testContextList.get(i).getName());
			tngCount.setSuiteName(testContextList.get(i).getSuite().getName());
			tngCount.setFailed(testContextList.get(i).getFailedTests().size());
			tngCount.setPassed(testContextList.get(i).getPassedTests().size());
			tngCount.setSkipped(testContextList.get(i).getSkippedTests().size());
			
			testCountList.add(tngCount);
		}
		this.testReport .setTaskName(getTaskName());
		this.testReport .setTngTestCountList(testCountList);
	}
}
