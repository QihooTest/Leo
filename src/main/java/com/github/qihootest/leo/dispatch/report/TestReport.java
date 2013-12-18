package com.github.qihootest.leo.dispatch.report;

import java.util.ArrayList;

/**
 * 测试任务执行完毕后输出的测试报告摘要信息<br/>
 * 	
 *	@author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 */
public class TestReport {

	/**
	 * 任务ID
	 */
	private String taskId;
	/**
	 * 任务名称
	 */
	private String taskName;
	/**
	 * html报告存储位置
	 */
	private String htmlReport;
	/**
	 * 测试任务执行时长，单位：毫秒
	 */
	private Long  sumTime;
	/**
	 * 测试套/集 结果信息
	 */
	private TngCount tngSuiteCount;
	/**
	 * 测试套中所有测试集结果信息
	 */
	private ArrayList<TngCount> tngTestCountList;//
	/**
	 * 任务运行结果返回码
	 */
	private int resNo;
	/**
	 * 任务运行结果返回信息
	 */
	private String resMsg;
	
	
	public String getHtmlReport() {
		return htmlReport;
	}
	public void setHtmlReport(String htmlReport) {
		this.htmlReport = htmlReport;
	}
	public TngCount getTngSuiteCount() {
		return tngSuiteCount;
	}
	public void setTngSuiteCount(TngCount tngSuiteCount) {
		this.tngSuiteCount = tngSuiteCount;
	}
	public ArrayList<TngCount> getTngTestCountList() {
		return tngTestCountList;
	}
	public void setTngTestCountList(ArrayList<TngCount> tngTestCountList) {
		this.tngTestCountList = tngTestCountList;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Long getSumTime() {
		return sumTime;
	}
	public void setSumTime(Long sumTime) {
		this.sumTime = sumTime;
	}
	public int getResNo() {
		return resNo;
	}
	public void setResNo(int resNo) {
		this.resNo = resNo;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	
}
