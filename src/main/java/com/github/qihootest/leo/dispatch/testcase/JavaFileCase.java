package com.github.qihootest.leo.dispatch.testcase;

import java.util.Arrays;
import java.util.List;

import com.github.qihootest.leo.toolkit.util.CommUtils;
import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * .java文件类型的测试用例
 * @author lianghui (lianghui@360.cn)
 *
 */
public class JavaFileCase extends SuperCase{
	private LogUtil log=LogUtil.getLogger(JavaFileCase.class);//日志记录
	private CreateXmlFile createXmlFile;
	/**
	 * 构造函数
	 */
	public JavaFileCase(){
		super();
		createXmlFile = new CreateXmlFile();
	}
	
	/**
	 * 构造函数
	 * @param threadCont 线程数
	 */
	public JavaFileCase(int threadCont,String setParallel){
		super();
		createXmlFile = new CreateXmlFile(threadCont,setParallel);
	}
	
	/**
	 * 获取用例列表，返回的是xml文件路径信息
	 * @return List<String>
	 */
	public List<String> getCaseList(){
		String xmlFilePath = createXmlFile.getXmlFilePath();
		if (null!=xmlFilePath){
			return Arrays.asList(xmlFilePath);
		}else{
			return null;
		}
		
	}
	/**
	 * 添加用例
	 * @param cls
	 */
	public void addCase(Class<?> cls){
		if (null==cls) {
			log.error("添加的java用例类为null");
			return ;
		}
		String caseName=cls.getSimpleName();
		addCase(caseName, cls);
	}
	/**
	 * 添加用例
	 * @param caseName
	 * @param cls
	 */
	public void addCase(String caseName,Class<?> cls) {
		if (null==cls ) {
			log.error("添加的java用例类为null");
			return;
		}
		if (null==caseName || caseName.length()<1) {
			log.info("设置的用例名为空");
			caseName="未命名测试集"+CommUtils.getRandomStr(5);
			log.info("使用默认名称："+caseName);
		}
		createXmlFile.addJavaCase(caseName, cls);
	}

	/**
	 * 设置测试套名称 未设置使用默认名称 "未命名测试用例"+5随机字符
	 * @param suiteName
	 */
	public void setSuiteName(String suiteName) {
		createXmlFile.setSuiteName(suiteName);
	}
	
	/**
	 * 设置生成的xml文件存放文件夹  未设置使用默认 qtaf/dispatch/suites/
	 * @param suiteName
	 */
	public void setXmlFileFolder(String xmlFileFolder) {
		createXmlFile.setXmlFileFolder(xmlFileFolder);
	}
	
}
