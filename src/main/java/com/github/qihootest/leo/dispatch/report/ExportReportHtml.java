package com.github.qihootest.leo.dispatch.report;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.github.qihootest.leo.dispatch.DispatchConf;
import com.github.qihootest.leo.toolkit.util.FileUtil;
import com.github.qihootest.leo.toolkit.util.LogUtil;


/**
 * TestNG的原生报告转换为美化后的Html格式测试报告
 * @author lianghui (lianghui@360.cn)
 *
 */
public class ExportReportHtml {
	
	private static LogUtil log=LogUtil.getLogger(ExportReportHtml.class);//日志记录

	/**
	 * 根据TestNG结果输出的xml文件，优化生成html格式测试报告
	 * @param  tngOutFilePath  TestNG的结果xml文件路径
	 * @param  htmlReportPath html报告的目录
	 * @param  htmlReportTitle 测试报告标题
	 * @return boolean 创建成功返回true
	 */
	public static boolean createHtmlReport(String tngOutFilePath,String htmlReportPath,String htmlReportTitle) {
		if (!FileUtil.isExist(tngOutFilePath) ) {
			log.error("生成Html报告出错-testng输出的xml文件不存在："+tngOutFilePath);
			return false;
		}
		if (!FileUtil.createDictory(htmlReportPath)){
			log.error("生成Html报告出错-输出目录创建失败："+htmlReportPath);
			return false;
		}
		 try {
			 Source xml = new javax.xml.transform.stream.StreamSource(tngOutFilePath);
			 Source xsl = new javax.xml.transform.stream.StreamSource(DispatchConf.TestNGXsltFile);
			 Result out = new StreamResult(new BufferedOutputStream(new FileOutputStream(htmlReportPath+"/index.html")));
			  // 创建转换器工厂
			  TransformerFactory tfactory = TransformerFactory.newInstance();
			  // 创建 XSL 转换器
			  Transformer transformer = tfactory.newTransformer(xsl);
			  //参数设置
			  transformer.setParameter("testNgXslt.outputDir",htmlReportPath);
			//transformer.setParameter("testNgXslt.showRuntimeTotals", true);
			  transformer.setParameter("testNgXslt.reportTitle", htmlReportTitle);
			  transformer.transform(xml, out);
			  log.info("生成Html测试报告成功："+htmlReportPath+"/index.html");
			  return true;
		} catch (Exception e) {
			log.error("生成Html报告出错-xml转换异常："+e.getMessage());
			return false;
		}
	}
}
