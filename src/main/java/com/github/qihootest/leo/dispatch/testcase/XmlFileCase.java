package com.github.qihootest.leo.dispatch.testcase;

import java.util.ArrayList;
import java.util.List;

import com.github.qihootest.leo.toolkit.util.LogUtil;

/**
 * XML文件类型的测试用例
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a>(bwgang@163.com)<br/>
 *
 */
public class XmlFileCase extends SuperCase{

	private LogUtil log=LogUtil.getLogger(XmlFileCase.class);//日志记录
	/**
	 *默认构造函数
	 */
	public  XmlFileCase() {
		super();
		xmlPathNameList=new ArrayList<>();
	}
	/**
	 * 构造函数  根据xml文件列表信息创建
	 * @param xmlPathNameList
	 */
	public  XmlFileCase(List<String> xmlPathNameList) {
		super();
		xmlPathNameList=new ArrayList<>();
		this.xmlPathNameList=xmlPathNameList;
	}
	/**
	 * 构造函数 根据单个xml文件信息创建
	 * @param xmlPathName
	 */
	public  XmlFileCase(String xmlPathName) {
		super();
		xmlPathNameList=new ArrayList<>();
		if (null==xmlPathName)return;
		this.xmlPathNameList.add(xmlPathName);
	}

	/**
	 * 添加xml文件列表信息
	 * @param xmlPathNameList
	 */
	public void addXmlCase(List<String> xmlPathNameList) {
		this.xmlPathNameList.addAll(xmlPathNameList);
	}
	/**
	 * 添加单个xml文件信息
	 * @param xmlPathName
	 */
	public void addXmlCase(String xmlPathName) {
		if (null==xmlPathName){
			log.error("添加的Xml文件为null，添加失败");
			return;
		}
		this.xmlPathNameList.add(xmlPathName);
	}
	
}
