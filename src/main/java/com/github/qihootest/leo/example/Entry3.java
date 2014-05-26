package com.github.qihootest.leo.example;


import java.util.ArrayList;
import java.util.List;



import com.github.qihootest.leo.dispatch.DispatchConf;
import com.github.qihootest.leo.dispatch.ExecTask;
import com.github.qihootest.leo.dispatch.report.TestReport;
import com.github.qihootest.leo.dispatch.run.TestRunInfo;
import com.github.qihootest.leo.dispatch.testcase.ICase;
import com.github.qihootest.leo.example.project.DemoCasesUtils;
import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.ift.testcase.autocreate.IftDataFileCase;


public class Entry3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//清空临时目录
		DispatchConf.delTmpPath();
		IftConf.delTmpPath();
		
		//检查相关配置，不存在则创建
		
		
		//依赖的jar文件路径信息必须设置 否则不执行
		if (!IftConf.updateJarFile(args)) return;
		
		//其它设置  
		IftConf.ProxyEnable="Y";//启用代理，默认为不启用
		
		ExecTask exec = new ExecTask();
		TestRunInfo runInfo = new TestRunInfo();
		List<ICase> caseList = new ArrayList<>();//用例列表
		
		//接口数据格式用例			
		IftDataFileCase dataCase = new IftDataFileCase();
		dataCase.setIftTaskName("接口测试Demo");
		//Excel文件  sheet表名 执行用例的类  类中的方法  
		dataCase.addCase(IftConf.RootPath+"demo.xlsx","Sheet1","用例名称1",DemoCasesUtils.class,"DemoMethod1");
		dataCase.addCase(IftConf.RootPath+"demo.xlsx","Sheet2","用例名称2",DemoCasesUtils.class,"DemoMethod2");
		
		caseList.add(dataCase);
		
		//设置运行配置信息
		runInfo.setTaskName(dataCase.getTaskName());//任务名称
		runInfo.setCaseList(caseList);//用例
		runInfo.setHtmlReportOutPath(dataCase.getHtmlReportPath());//设置测试报告输出目录，
		
		//可选运行参数设置
//		runInfo.setTestng_OutPut(IftConf.IftPath+"testng-out/");//设置TestNG输出目录，--可选
//		runInfo.setHtmlReportOutPath(IftConf.IftPath+"report/");//设置测试报告输出目录，---可选
//		runInfo.setHtmlReportTitle("设置测试报告标题-可选");//设置测试报告标题 ---可选
//		TestngLog.setOutputTestNGLog(false);//不记录TestNG日志，--可选
		
		//执行
		exec.setRunInfo(runInfo);
		TestReport report=exec.Exec();
		
		//输出执行结果
		System.out.print("任务执行结果："+report.getResMsg()+"\n");
		if (report.getResNo()>-1) {
			System.out.print("任务名称："+runInfo.getTaskName()+"\n");
			System.out.print("任务执行时间："+report.getSumTime()+"毫秒\n");
			System.out.print("Html报告："+report.getHtmlReport()+"/index.html\n");
			System.out.print("Excel报告："+dataCase.getExcelReportPath());
		}

	}

}
