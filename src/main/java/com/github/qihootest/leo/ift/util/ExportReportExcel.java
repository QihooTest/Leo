package com.github.qihootest.leo.ift.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.github.qihootest.leo.toolkit.util.CommUtils;
import com.github.qihootest.leo.toolkit.util.ExcelUtil;
import com.github.qihootest.leo.toolkit.util.FileUtil;

/**
 * 说明：输出Excel格式的测试报告
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 */
public class ExportReportExcel {
	
//	private static LogUtil log = LogUtil.getLogger(ExportReportExcel.class);
	/**
	 * 根据测试执行过程中的记录，生成Excel的测试报告
	 * @param excelreportpath
	 * @param filename
	 * @param arrres
	 * @return boolean
	 */
	public boolean CreatReportExcel(String excelreportpath,String filename,ArrayList<LinkedHashMap<String,String>> arrres) {
		boolean flag=false;
		flag=CreatReportExcel(excelreportpath,filename,null,arrres);		
		return flag;
	}
	
	/**
	 * 根据测试执行过程中的记录，生成Excel的测试报告
	 * @param excelreportpath
	 * @param filename
	 * @param sheetname
	 * @param arrres
	 * @return boolean
	 */
	public boolean CreatReportExcel(String excelreportpath,String filename,String sheetname,List<LinkedHashMap<String,String>> arrres) {
		boolean flag=false;
		List<String> title =null;
		List<String[]> content=null;
		
		if (filename.indexOf("/")!=-1 || filename.indexOf("xls")!=-1 || filename.length()<1) {
			filename="未命名";
		}
		
		title=getTitle();
		content=getContentFromMap(arrres);
		writeExcel(title, content, sheetname,filename,excelreportpath);
		
		return flag;
	}
	
	//excel测试报告内容定义
	
	/**
	 * 说明：指定输出报告的标题项，按顺序存储到List中
	 * 
	 * @return List<String> title
	 */
	private List<String> getTitle() {
		List<String> title=new LinkedList<String>() ;
	
		title.add("CaseID");
		title.add("TestPoint");
		title.add("ExpRes");
		title.add("ActRes");		
		title.add("ExcResult");
		title.add("ResponseRes");
		title.add("Httpurl");
	
		return title;
	}
	
	/**
	 * 说明：解析出内容，按顺序存储到List中，每行为一字符数组
	 * @param arrres
	 * @return List<String[]>
	 */
	private List<String[]> getContentFromMap(List<LinkedHashMap<String,String>> arrres) {
		List<String[]> content=new LinkedList<String[]>();
		
		List<String> title=getTitle();
		
		
		for (int i = 0; i < arrres.size(); i++) {
			String[] str = new String[title.size()];
			for (int j = 0; j < title.size(); j++) {
				str[j]=arrres.get(i).get(title.get(j));
			}	
			content.add(str);	
		}
		
		return content;
	}

	//Excel报告格式定义
	private void writeExcel(List<String> title,List<String[]> datas,String sheetName,String excelName,String excelreportpath){
//			log.error("title:");
//			for (int i = 0; i < title.size(); i++) {
//				log.error(title.get(i));
//			}
//			log.error("datas:");
//			for (int i = 0; i < datas.size(); i++) {
//				log.error("data--"+i);
//				for (String str : datas.get(i)) {
//					log.error(str);
//				}
//				
//			}
//			log.error("sheetname--"+sheetName);
//			log.error("excelName--"+excelName);
//			log.error("excelreportpath--"+excelreportpath);
			ExcelUtil excel = new ExcelUtil();	
			String pathName=excelreportpath+"/"+excelName+".xlsx";
			
			if (!FileUtil.isExist(pathName)) {//不存在时，新建空白工作簿
				excel.createBlankExcel2007(pathName);
			}
			
			//读取Excel文件
			excel.setPathName(pathName);
			XSSFWorkbook wb=(XSSFWorkbook) excel.getWb();
			updateWbFromContent(wb,title,datas,sheetName);//测试结果写入工作簿
			excel.writeExcel2007(wb, pathName);//写入文件
		}
	
	private void updateWbFromContent(XSSFWorkbook wb,List<String> title, List<String[]> datas,
		String sheetName) {
	
		XSSFSheet sheet;
		if (null==sheetName | sheetName.length()<1) {//未指定sheet名称时，自动命名创建
			sheet=wb.createSheet();
		}else{
			if (null!=wb.getSheet(sheetName)) sheetName=sheetName+CommUtils.getRandomStr(5);//已存在同名sheet，添加随机字符串
			sheet=wb.createSheet(sheetName);//使用指定的sheetName
		}
		
		//设置列宽
		sheet.setColumnWidth(0, 15*255);    
		sheet.setColumnWidth(1, 25*255);    
		sheet.setColumnWidth(2, 40*255);   
		sheet.setColumnWidth(3, 40*255);    
		sheet.setColumnWidth(4, 10*255);    
		sheet.setColumnWidth(5, 50*255);    
		sheet.setColumnWidth(6, 15*255);    
		sheet.setColumnWidth(7, 20*255);  
		sheet.setColumnWidth(8, 10*255);    
		//创建第一行
		XSSFRow row=sheet.createRow(0);
		//表头样式
				XSSFFont fonttitle=wb.createFont();
				fonttitle.setItalic(true);
				fonttitle.setFontName("微软雅黑");
				fonttitle.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);//粗体
				
		        CellStyle styletitle = wb.createCellStyle();
		        styletitle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
		        styletitle.setFillPattern(CellStyle.SOLID_FOREGROUND); 
		        styletitle.setFont(fonttitle);
		        styletitle.setBorderLeft(CellStyle.BORDER_THIN);
		        styletitle.setBorderRight(CellStyle.BORDER_THIN);
		        styletitle.setBorderTop(CellStyle.BORDER_THIN);
		        styletitle.setBorderBottom(CellStyle.BORDER_THIN);
		        
		  //正文样式
		        //
		        XSSFFont fontRed=wb.createFont();
		        fontRed.setColor(HSSFColor.GREEN.index);
		        fontRed.setColor(IndexedColors.RED.getIndex());
		        fontRed.setFontName("微软雅黑");
		        fontRed.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);//粗体
		        fontRed.setFontHeightInPoints((short) 11);
				CellStyle styleRed = wb.createCellStyle();
				styleRed.setFont(fontRed);
				styleRed.setBorderLeft(CellStyle.BORDER_THIN);
				styleRed.setBorderRight(CellStyle.BORDER_THIN);
				styleRed.setBorderTop(CellStyle.BORDER_THIN);
				styleRed.setBorderBottom(CellStyle.BORDER_THIN);
		        
		        XSSFFont fontGreen=wb.createFont();
		        fontGreen.setColor(IndexedColors.GREEN.getIndex());
		        fontGreen.setFontName("微软雅黑");
		        fontGreen.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);//粗体
		        fontGreen.setFontHeightInPoints((short) 11);
				CellStyle styleGreen = wb.createCellStyle();
				styleGreen.setFont(fontGreen);
				styleGreen.setBorderLeft(CellStyle.BORDER_THIN);
				styleGreen.setBorderRight(CellStyle.BORDER_THIN);
				styleGreen.setBorderTop(CellStyle.BORDER_THIN);
				styleGreen.setBorderBottom(CellStyle.BORDER_THIN);
				
		        XSSFFont fontBlack=wb.createFont();
		        fontBlack.setColor(IndexedColors.BLACK.getIndex());
		        fontBlack.setFontName("Arial");
		        fontBlack.setFontHeightInPoints((short) 9);
				CellStyle styleBlack = wb.createCellStyle();
				styleBlack.setFont(fontBlack);
				styleBlack.setBorderLeft(CellStyle.BORDER_THIN);
				styleBlack.setBorderRight(CellStyle.BORDER_THIN);
				styleBlack.setBorderTop(CellStyle.BORDER_THIN);
				styleBlack.setBorderBottom(CellStyle.BORDER_THIN);
		//写入标题
		for (int i = 0; i < title.size(); i++) {
			XSSFCell cell=row.createCell(i, XSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(styletitle);
		}
		
		// 写入内容行
		for (int i = 0; i < datas.size(); i++) {
			String[] rowvalue = datas.get(i);
			XSSFRow nextrow = sheet.createRow(sheet.getLastRowNum() + 1);// 创建一行
			for (int j = 0; j < rowvalue.length; j++) {
				XSSFCell cell = nextrow
						.createCell(j, XSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(new XSSFRichTextString(rowvalue[j]));
				if (rowvalue[j].equals("Fail")) {
					cell.setCellStyle(styleRed);
				} else if (rowvalue[j].equals("Pass")) {
					cell.setCellStyle(styleGreen);
				} else {
					cell.setCellStyle(styleBlack);
				}

			}
		}
		
	}
}
