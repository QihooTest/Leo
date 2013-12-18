package ${javaInfo.packageName};

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import cn.baiweigang.qtaf.ift.testcase.IftTestCase;
import cn.baiweigang.qtaf.ift.testcase.format.FormatCase;
import cn.baiweigang.qtaf.ift.core.IFtResultInfo;
import cn.baiweigang.qtaf.dispatch.log.TestngLog;

import ${clsInfo.importInfo};

/**
 * 自动生成的测试用例
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 *
 */
public class ${javaInfo.javaFileName}  {
	
	private ${clsInfo.className} cau;//执行用例的类
	private String caseFilePath;//用例文件路径
	private String caseSheetName;//用例的Sheet名
	private String excelReportFilePath;	//Excel测试报告输出目录
	private String excelReportName;//EXcel报告文件名	
	private String excelReportSheetName;//Excel报告sheet名	
	private List<IftTestCase> testCaseList ;//所有用例列表
	
	//记录预期值与实际值比对情况
	private List<LinkedHashMap<String,String>> compareResultList;
	
	//记录类名称
	private String classSimpleName;
	
	@BeforeTest
	public void beforeTest() {   
		
		cau=new ${clsInfo.className}();
		testCaseList=new ArrayList<IftTestCase>() ;		
		caseFilePath="${javaInfo.caseDataPathName}";
		caseSheetName="${javaInfo.caseDataSheetName}";
		excelReportFilePath="${javaInfo.excelReportPath}";
		excelReportName="${javaInfo.excelReportName}";
		excelReportSheetName="${javaInfo.excelReportSheetName}";
		compareResultList=new ArrayList<LinkedHashMap<String,String>>();
		
		 FormatCase formatcase=new FormatCase();		 
		 formatcase.FormatCaseFromObj(caseFilePath,caseSheetName);
		 testCaseList=formatcase.getTestCase();
		 
		 classSimpleName="${javaInfo.javaFileName}";
	 }
	//根据待执行用例列表，生成对应的测试用例java源码
	<#list javaInfo.allCase as testCase>
	@Test(description="${testCase.testPoint}()")
	  public void ${testCase.caseId}(){
			
		IftTestCase testCase=new 	IftTestCase();
		testCase=testCaseList.get(${testCase_index});
		LinkedHashMap<String,String> result=new LinkedHashMap<String,String>();//记录Excel测试报告
		IFtResultInfo iftResInfo=new IFtResultInfo();
		String httpurl="";//记录请求发送的Url
		String expres="";//期望的结果
		String response="";//请求响应的返回字符串
		String actres="";//过滤后实际的结果
		boolean res=false;//记录比对结果
		
			
		//用例开始

		TestngLog.CaseStart("测试集："+classSimpleName+"--的用例："+testCase.getCaseMap().get("CaseID")+"--"+testCase.getCaseMap().get("TestPoint"));
		
		//发起http请求，
		iftResInfo=cau.${clsInfo.method}(testCase);
		
		//获取返回结果信息和发起的http信息
		response=iftResInfo.getResponseInfo().getResBodyInfo();
		httpurl=iftResInfo.getResponseInfo().getHttpUrl();
		
		//获取比对结果信息
		res=iftResInfo.getCompareRes();
		actres=iftResInfo.getActRes();
		expres=iftResInfo.getExpRes();
		
		//记录执行结果到ArrayList,写Excel报告要用到
		result.put("CaseID", testCase.getCaseMap().get("CaseID"));
		result.put("TestPoint", testCase.getCaseMap().get("TestPoint"));
		result.put("ExpRes", expres);
		result.put("ActRes", actres);
		result.put("ResponseRes", response);
		result.put("Httpurl", httpurl);
		
		//比对结果
		if (res==true) {
			result.put("ExcResult", "Pass");
		}
		else {
			result.put("ExcResult", "Fail");
		}
		compareResultList.add(result);
		
		//写入TestNG日志
		TestngLog.Log("此用例预期结果为："+expres);
		TestngLog.Log("此用例实际结果为："+actres);
		TestngLog.Log("此用例请求返回的完整字符串为："+response);
		TestngLog.Log("此用例发送的请求URL为："+httpurl);
		TestngLog.Log("执行结果为："+res);

		//用例结束
		TestngLog.CaseEnd("测试集："+classSimpleName+"--的用例："+testCase.getCaseMap().get("CaseID")+"--"+testCase.getCaseMap().get("TestPoint"));
	 
		//比较结果记入TestNG断言中
		org.testng.Assert.assertTrue(res, "实际结果:"+actres+"  -预期结果:"+expres);
		//org.testng.Assert.assertEquals(actres, expres,res);
	}
	</#list>
	
	@AfterTest
	public void afterTest() {   
		cau.closeConn();
		//执行结果写入excel
		cau.CreatReportExcel(excelReportFilePath,excelReportName,excelReportSheetName,compareResultList);
		//记录到TestNG日志
		TestngLog.Log("所有用例执行完毕");
		TestngLog.Log("共验证检查点数为："+compareResultList.size());
		TestngLog.Log("生成Excel测试报告："+excelReportFilePath+excelReportName+".xlsx"+" 的sheet表--"+excelReportSheetName);
		 //此测试套执行完毕记入TestNG日志
		TestngLog.Log("********************测试套：【"+classSimpleName+"】执行完毕**************************");
	 }
}
