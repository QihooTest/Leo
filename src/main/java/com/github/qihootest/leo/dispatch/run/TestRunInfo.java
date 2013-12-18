package com.github.qihootest.leo.dispatch.run;

import java.util.List;

import com.github.qihootest.leo.dispatch.testcase.ICase;


/**
 * TestNG运行信息
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 */
public class TestRunInfo {
	private String taskName;//任务名称
	private String testng_OutPut;//TestNG输出目录
	private String htmlReportOutPath;//Html报告输出路径
	private String htmlReportTitle;//Html报告标题
	private List<ICase>  caseList;//用例信息
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTestng_OutPut() {
		return testng_OutPut;
	}
	public void setTestng_OutPut(String testng_OutPut) {
		this.testng_OutPut = testng_OutPut;
	}
	public String getHtmlReportOutPath() {
		return htmlReportOutPath;
	}
	public void setHtmlReportOutPath(String htmlReportOutPath) {
		this.htmlReportOutPath = htmlReportOutPath;
	}
	public List<ICase> getCaseList() {
		return caseList;
	}
	public void setCaseList(List<ICase> caseList) {
		this.caseList = caseList;
	}
	public String getHtmlReportTitle() {
		return htmlReportTitle;
	}
	public void setHtmlReportTitle(String htmlReportTitle) {
		this.htmlReportTitle = htmlReportTitle;
	}
}
