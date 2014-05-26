package com.github.qihootest.leo.ift.testcase.autocreate;

import java.util.List;

import com.github.qihootest.leo.dispatch.testcase.CreateXmlFile;
import com.github.qihootest.leo.dispatch.testcase.SuperCase;
import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.ift.testcase.format.FormatCase;
import com.github.qihootest.leo.toolkit.util.CompilerUtil;
import com.github.qihootest.leo.toolkit.util.FileUtil;
import com.github.qihootest.leo.toolkit.util.LogUtil;
import com.github.qihootest.leo.toolkit.util.StringUtil;


/**
 * 数据文件类型的测试用例
 * @author lianghui (lianghui@360.cn)
 *
 */
public class IftDataFileCase extends SuperCase{
	
	private LogUtil log =LogUtil.getLogger(IftDataFileCase.class);//日志记录
	//任务名称
	private String taskName;//测试任务名称
	//根据用例数据文件，创建java、xml文件相关配置信息
	private String allReportPath ;// html、excel测试报告存储的上级目录
	private String excelReportName;//excel格式测试报告的名称
	
	private CreateJavaFile createJavaFile;//生成java文件
	private CreateXmlFile createXmlFile;//生成xml文件
	
	/**
	 * 构造函数
	 */
	public   IftDataFileCase() {
		super();
		createJavaFile = new CreateJavaFile();
		createXmlFile = new CreateXmlFile();
		setIftTaskName("未命名测试任务");
		excelReportName="未命名接口测试";
	}

	public List<String> getCaseList() {
		createXmlFile();
		return this.xmlPathNameList;
	}
	/**
	 * 添加用例
	 * @param casePath 用例路径 必填
	 * @param sheetName Excel的sheet表名 可选
	 * @param caseName  用例名称 必填
	 * @param cls		执行用例的类 必填
	 * @param method    类中的方法 必填
	 */
	public void addCase(String casePath, String sheetName, String caseName,
			Class<?> cls,String method) {
		if (StringUtil.IsNullOrEmpty(casePath) || StringUtil.IsNullOrEmpty(caseName)) {
			return;
		}//任一项空值或长度小于1时，不做处理
		JavaCaseInfo javaCaseInfo = new JavaCaseInfo();
		//读取用例
		FormatCase formatcase=new FormatCase();
		formatcase.FormatCaseFromObj(casePath,sheetName);
		//存储用例实体列表信息
		javaCaseInfo.setAllCase(formatcase.getTestCase());
		//获取测试集名称作为输出的测试报告名称
		this.excelReportName="测试报告_"+formatcase.getCasesetName();
		//存储javaCaseInfo其余信息
		javaCaseInfo.setPackageName(IftConf.PackageName);
		javaCaseInfo.setJavaFileName(caseName.replace(".", "_"));
		javaCaseInfo.setJavaSavePath(IftConf.JavaPath);
		javaCaseInfo.setCaseDataPathName(casePath);
		javaCaseInfo.setCaseDataSheetName(sheetName);
		javaCaseInfo.setExcelReportSheetName(sheetName);
		javaCaseInfo.setExcelReportName(this.excelReportName);
		javaCaseInfo.setExcelReportPath(getReportPath());
		javaCaseInfo.setCls(cls);
		javaCaseInfo.setMethod(method);
		//创建java文件 失败则返回
		if (!createJavaFile.creatJavaSrcFile(javaCaseInfo)){
			return;
		}
		//编译java文件为class 失败则返回
		if(!CompilerUtil.dynamicCompiler(javaCaseInfo.getJavaSavePath()+javaCaseInfo.getJavaFileName()+".java", 
				IftConf.DistPath, IftConf.LibPath,IftConf.JarFile)){
			return;
		}
		//添加到xmlSuite	
		try {
			createXmlFile.addJavaCase(caseName.replace(".", "_"), 
					Class.forName(javaCaseInfo.getPackageName()+"."+javaCaseInfo.getJavaFileName()));
			log.info("添加测试集："+javaCaseInfo.getJavaFileName()+"成功");
		} catch (ClassNotFoundException e) {
			log.error("添加测试集："+javaCaseInfo.getJavaFileName()+"失败");
			log.error(e.getMessage());
		}
		
	}
	
	/**
	 * 添加用例
	 * @param casePath 用例路径 必填
	 * @param caseName  用例名称 必填
	 * @param cls		执行用例的类 必填
	 * @param method    类中的方法 必填
	 */
	public void addCase(String casePath, String caseName,Class<?> cls,String method) {
		addCase(casePath,"TestCase",caseName,cls,method);
	}

	/**
	 * 设置任务名称 
	 * @param setTaskName
	 */
	public void setIftTaskName(String setTaskName) {
		this.taskName =setTaskName;
		this.createXmlFile.setSuiteName(this.taskName);
		setReportPath(IftConf.ReportPath+this.taskName+"/");// 测试报告存储路径
		this.excelReportName = this.taskName;
		
	}
	
	
	/**
	 * 如果路径无效 则测试报告默认保存在 [qtaf/ift/report/任务名称 ]目录下
	 * @param reportPath
	 */
	public void setReportPath(String reportPath){
		if (FileUtil.createDictory(reportPath)) {
			this.allReportPath=reportPath;
			//清空指定的测试报告目录
			FileUtil.delFolder(getReportPath());
		}
	}
	public String getReportPath(){
		return this.allReportPath;
	}
	public String getExcelReportPath() {
		return getReportPath()+this.excelReportName+".xlsx";
	}
	public String getHtmlReportPath() {
		return this.allReportPath+"html";
	}

	public String getTaskName() {
		return taskName;
	}

	/**
	 * 创建java、xml文件 更新xmlPathNameList列表
	 * @return boolean 设置成功返回true
	 */
	private boolean createXmlFile() {
		//创建java、xml文件
		String xmlFilePath = createXmlFile.getXmlFilePath();
		if (FileUtil.isExist(xmlFilePath)) {
			xmlPathNameList.add(xmlFilePath);
			log.info("添加xml文件成功："+xmlFilePath);
			return true;
		}else{
			log.error("添加xml文件失败："+xmlFilePath);
			return false;
		}
	}
}
