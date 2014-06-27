package com.github.qihootest.leo.example.project;

import java.util.TreeMap;

import com.github.qihootest.leo.ift.core.CasesUtils;
import com.github.qihootest.leo.ift.core.IFtResultInfo;
import com.github.qihootest.leo.ift.testcase.IftTestCase;
import com.github.qihootest.leo.ift.util.CommonSign;
import com.github.qihootest.leo.toolkit.httpclient.ResponseInfo;

/**
 * 说明：接口执行类，必须继承自类CasesUtils
 * 
 * @author 
 *
 */
public class DemoCasesUtils extends CasesUtils{

	/**
	 * 接口业务逻辑处理方法，此方式的传入参数是 IftTestCase  返回结果类型是IFtResultInfo 方法中如何处理业务逻辑 
	 * 调用httpclient发起请求，处理返回结果等可自定义
	 * @param testcase
	 * @return IFtResultInfo
	 */
	public IFtResultInfo DemoMethod1(IftTestCase testcase)  {
		//暂停100毫秒
//		BaseTools.sleep(100);
		//设置用例编码--可选
		testcase.setEnCoding(DemoConf.ENCODING);
		//设置用例使用的header信息--可选
		testcase.setHeaderMap(new TreeMap<String,String>());
		//更新DemoConf中的header参数值
		DemoConf conf = new DemoConf();
		testcase = updateAllToConfForCase(testcase, conf, DemoConf.HeardPara);
		//更新用例的签名计算、url参数、form参数、header参数--必须
		//是接口参数的父集
		testcase=updateAllToListForCase(testcase, DemoConf.GetPara, DemoConf.PostPara, DemoConf.HeardPara);
		//更新用例参数值，针对rand等特殊标识处理--必须
		testcase=updateAllParaForCase(testcase,10);
		//更新用例签名值--可选  如果无需计算签名  则不需要
		testcase=updateSignValueForCase(testcase, CommonSign.signMethodThird(getSignMap(testcase), DemoConf.SecretKey));
		//发起请求
		ResponseInfo resInfo = execResquest(testcase);
		//预期结果格式为key1=value1&key2=value2  或key1=value11#value12&key2=value2 支持一个可以对应多个值
		String expRes=testcase.getCaseMap().get("Expres");
		if (expRes=="" || null==expRes) {
			expRes="预期结果为空";
		}
		//获取处理后的实际结果 目前只支持json\xml格式  如果接口返回结果不是此两种格式 则需要把actRes转换为json或xml格式
		String actRes= resInfo.getResBodyInfo();
		//预期值与实际值比对 并返回IFtResultInfo类型
		return getIFtResultInfo(resInfo, expRes, actRes);
	}
	
	public IFtResultInfo DemoMethod2(IftTestCase testcase)  {
		IFtResultInfo iftResInfo=new IFtResultInfo();
		ResponseInfo resInfo = new ResponseInfo(); 
		//执行用例  可以使用CasesUtils中已封装的ExecPostResquest、ExecGetResquest方法 或者直接调用httpclient
		//。。。。
		//返回的类型为IFtResultInfo即可
		
		resInfo.setHttpUrl("this a demo2");
		resInfo.setResBodyInfo("demo");
		
		iftResInfo.setResponseInfo(resInfo);//接口返回信息resInfo
		iftResInfo.setActRes("demo");//实际结果
		iftResInfo.setExpRes("demo");//期望结果
		iftResInfo.setCompareRes(true);//比对结果
		return iftResInfo;
	}

}

 
	
	

