package com.github.qihootest.leo.dispatch;

import java.util.ArrayList;
import java.util.List;

import com.github.qihootest.leo.dispatch.report.TestReport;
import com.github.qihootest.leo.dispatch.run.TestRunInfo;
import com.github.qihootest.leo.dispatch.testcase.ICase;
import com.github.qihootest.leo.dispatch.testcase.JavaFileCase;


public class TestExecTask {

	public  void test2() {
		ExecTask exec = new ExecTask();
		TestRunInfo runInfo = new TestRunInfo();
		List<ICase> caseList = new ArrayList<>();//用例列表
		
		runInfo.setTaskName("框架测试99999");
		
		//java格式用例			
		JavaFileCase jCase = new JavaFileCase();
		jCase.addCase(TestTestngLog.class);
		caseList.add(jCase);
		
		runInfo.setCaseList(caseList);
		runInfo.setHtmlReportTitle("测试报告的标题");
		exec.setRunInfo(runInfo);
		TestReport report=exec.Exec();
		System.out.print("任务执行结果："+report.getResMsg()+"\n");
		if (report.getResNo()>-1) {
			System.out.print("任务名称："+report.getTaskName()+"\n");
			System.out.print("任务执行时间："+report.getSumTime()+"毫秒\n");
			System.out.print("Html报告："+report.getHtmlReport()+"index.html\n");
			System.out.print(report.getTngTestCountList().get(0).getSuiteName());
		}
	}

}
