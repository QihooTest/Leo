package com.github.qihootest.leo.dispatch;

import com.github.qihootest.leo.dispatch.report.TestReport;
import com.github.qihootest.leo.dispatch.run.TestRunInfo;
import com.github.qihootest.leo.dispatch.run.TestngRunSingle;
import com.github.qihootest.leo.toolkit.util.CommUtils;
import com.github.qihootest.leo.toolkit.util.LogUtil;


/**
 * 任务执行入口
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 *
 */
public class ExecTask {

	private LogUtil log=LogUtil.getLogger(ExecTask.class);//日志记录
	private TestngRunSingle task;
	private TestRunInfo runInfo;
	private TestReport report;
	
	/**
	 * 构造函数
	 */
	public ExecTask() {
		task    = TestngRunSingle.getInstance();
		report  = new TestReport();
	}
	/**
	 * 任务执行
	 * @return TestReport
	 */
	public TestReport Exec() {
		//传入参数校验
		if (runInfo.getCaseList() == null || runInfo.getCaseList().size()<1) {
			setResNoAndMsg(-100,"待执行的用例不存在");
			log.error(runInfo.getTaskName()+" 待执行的用例不存在");
			return report;
		}
		Long startTimeMS=System.currentTimeMillis();
		Long startTime=startTimeMS/1000;
		int sumTime=0;//记录等待时长，单位秒
		while (true) {
			//获取等待的时间
			sumTime=(int) (System.currentTimeMillis()/1000-startTime);
			//判断当前是否有任务运行
			//当前无任务执行，执行当前任务
			if (!task.getFlag()) {			
				//运行任务 设置任务信息
				task.setRunInfo(this.runInfo);
				report= task.execTask();
				long execTimes=System.currentTimeMillis()-startTimeMS;
				log.info(this.runInfo.getTaskName()+" 执行时间："+execTimes+"毫秒");
				report.setSumTime(execTimes);
				return report;//执行完毕后返回测试报告信息
			}
			
			//当前有任务在运行  判断等待时间
			if (sumTime>600) {//设置等待超时时间600秒
				log.info(this.runInfo.getTaskName()+" 已等待"+sumTime+"秒，超时退出");
				setResNoAndMsg(-203,"任务等待超时，已等待"+sumTime+"秒");
				return report;
			}
			// 当前有任务在运行 等待10秒后再试试
			log.info(this.runInfo.getTaskName()+" 在等待执行，当前有任务在执行中。。。等待10秒后再尝试执行");
			CommUtils.sleep(10000);
		}//死循环			
	}
	
	/**
	 * 设置任务运行配置信息
	 * @param runInfo
	 */
	public void setRunInfo(TestRunInfo runInfo) {
		this.runInfo = runInfo;
	}
	
	private void setResNoAndMsg(int resNo,String resMsg) {
		this.report.setResNo(resNo);
		this.report.setResMsg(resMsg);
	}
}
