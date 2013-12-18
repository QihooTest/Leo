package com.github.qihootest.leo.toolkit.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 根据模板+数据 输出文件
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 */
public class FreeMakerUtil {
	//日志记录
	private static LogUtil log=LogUtil.getLogger(FreeMakerUtil.class);

	/**
	 * 根据模板+数据 输出文件
	 * @param templateFilePath 模板的绝对路径
	 * @param data 数据
	 * @param javaFilePath 输出的文件位置
	 * @return boolean
	 */
	public boolean CreateJavaFile(String templateFilePath,Map<String, Object> data,String javaFilePath) {
		
		if (!FileUtil.isExist(templateFilePath)) {
			log.info("模板文件不存在："+templateFilePath);
			return false;
		}
		if (null == data) {
			log.info("模板数据不能为null");
			return false;
		}
//		Map<String, String> clsMap= (Map<String, String>) data.get("clsInfo");
//		for (Entry<String, String> entry : clsMap.entrySet()) {
//			String key = entry.getKey().toString();
//			String value = entry.getValue().toString();
//			log.info(key+"=="+value);
//		}
//		log.info("FreeMaker--39--"+data.size());
//		PrintModelObject.P(data.get("clsInfo"));
		if (!FileUtil.createFile(javaFilePath)) {
			return false;
		}
		try {
			String tmpPath=FileUtil.getParentPath(templateFilePath);
			String tmpName=FileUtil.getFileName(templateFilePath);
			
			Configuration cfg =new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(tmpPath));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			
			//读取模板
			Template template= cfg.getTemplate(tmpName);
			
			//输出到文件
			Writer out = new OutputStreamWriter(new FileOutputStream(javaFilePath),"UTF-8"); 
			template.process(data, out);//根据模板和数据输出java文件
			
			return true;
			
		} catch (IOException | TemplateException e) {
			//生成.java文件失败
			log.error(e.getMessage());
			return false;
		}
	}
}
