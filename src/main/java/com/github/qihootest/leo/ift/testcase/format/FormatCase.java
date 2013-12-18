package com.github.qihootest.leo.ift.testcase.format;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.ift.testcase.IftTestCase;
import com.github.qihootest.leo.toolkit.util.FileUtil;
import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * 功能说明：格式化测试用例数据， 目前只支持读取excel格式数据，XML、数据库等后续扩展
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 */
public class FormatCase {

	private static LogUtil log=LogUtil.getLogger(FormatCase.class);//日志记录

	private List<LinkedHashMap<String, String>> arrCase;
	private List<IftTestCase> testCase;
	private String url;
	private String httpMethod;
	private int argcount;
	private String cookie;
	private String casesetName;
	private String sheetName;
	
	/**
	 * 构造函数
	 */
	public FormatCase() {
		this.arrCase = new ArrayList<LinkedHashMap<String, String>>();
		this.testCase = new ArrayList<IftTestCase>();
		this.url = "";
		this.httpMethod = "";
		this.argcount = 0;
		this.cookie = "";
		this.casesetName = "";
		this.sheetName = "";
	}
	
	/**
	 * 从数据文件中读取测试用例
	 * @param casePath
	 * @param sheetName
	 */
	public void FormatCaseFromObj(String casePath,String sheetName) {
		this.FormatCaseFromExcel(casePath,sheetName);
		this.casesetName = casePath.substring(casePath.lastIndexOf("/") + 1,casePath.length() - (FileUtil.getExtensionName(casePath).length()+1));
		this.sheetName=sheetName;
	}

	/**
	 * 
	 * @param pathName
	 * @param sheetIndex
	 */
	private void getTestCaseFromExcel(String pathName,String sheetName) {
		// 记录读取到的用例数据信息
		ReadCaseFromExcel readCase = new ReadCaseFromExcel(pathName,sheetName);
		// 读取表表信息
		url = readCase.readUrl();
		httpMethod = readCase.readHttpMethod();
		argcount = readCase.readArgCount();// 参与sign计算的参数个数
		this.cookie = readCase.readCookie();
		String[] argKey = readCase.readArgKey();// 获取参数key
		// 设置测试集名称,Excel中未存储，从判断传入的path为excel时设置
		// casesetName=readcase();
		// 获取用例数据
		for (int j = IftConf.paramStartRow; j < readCase.getRowNum(); j++) {
			IftTestCase tempcase = new IftTestCase();
			tempcase.setArgCount(getArgcount());
			tempcase.setHttpMethod(getHttpMethod());
			tempcase.setUrl(getUrl());
			tempcase.setCookie(this.cookie);
			// 读取【j+1】行的数据
			String[] argValue = readCase.readArgValue(j);
//			System.out.print("行："+j+"    参数个数"+argValue.length+argValue[1]+"\n");
			if (!argValue[IftConf.isRunCol].equals("Y")) {
				continue;
			} else {
				// 把数据和标题一一对应存入LinkedHashMap有序键值对中
				LinkedHashMap<String, String> mycase = new LinkedHashMap<String, String>();
				for (int i = 0; i < argKey.length; i++) {
					// 判断此条记录是否有cookie参数，如果有且不为空替换全局cookie
					if (null != argValue[i] && argKey[i].equals("cookie") && argValue[i].length() > 0) {
						tempcase.setCookie(argValue[i]);
					}
					mycase.put(argKey[i], argValue[i]);
				}
				arrCase.add(mycase);
				tempcase.setCaseMap(mycase);
				tempcase.setCaseId(mycase.get("CaseID"));
				tempcase.setTestPoint(mycase.get("TestPoint"));
			}
			this.testCase.add(tempcase);
		}
	}

	/**
	 * 功能：格式化Excel格式的测试用例
	 * 
	 * @param   casefilepath  读取Sheet1表格
	 */
	public void FormatCaseFromExcel(String casefilepath) {
		// 记录读取到的用例数据信息
		getTestCaseFromExcel(casefilepath, "Sheet1");

	}

	/**
	 * 功能：格式化Excel格式的测试用例
	 * 
	 * @param casefilepath
	 * @param casesheetname 如果为空,默认读取Sheet1
	 */
	public void FormatCaseFromExcel(String casefilepath, String casesheetname) {
		if (null==casesheetname | casesheetname.length()<1) {
			casesheetname="Sheet1";
		}
		getTestCaseFromExcel(casefilepath, casesheetname);
	}

	/**
	 * 功能：格式化Xml格式的测试用例
	 * 
	 * @param casefilepath
	 */
	public void FormatCaseFromXml(String casefilepath) {
		
	}

	/**
	 * @return the arrCase
	 */
	public List<LinkedHashMap<String, String>> getArrCase() {
		return arrCase;
	}

	/**
	 * @return the testCase
	 */
	public List<IftTestCase> getTestCase() {
		return testCase;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @return the argcount
	 */
	public int getArgcount() {
		return argcount;
	}

	/**
	 * @return the cookie
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * @return the casesetName
	 */
	public String getCasesetName() {
		return casesetName;
	}

	public String getSheetName() {
		return sheetName;
	}


}
