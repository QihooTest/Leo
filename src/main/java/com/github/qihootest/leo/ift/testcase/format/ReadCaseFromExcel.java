package com.github.qihootest.leo.ift.testcase.format;
import java.util.List;
import com.github.qihootest.leo.ift.IftConf;
import com.github.qihootest.leo.toolkit.util.ExcelUtil;

/**
 * 功能说明：从excel表中读取用例数据文件
 * @author lianghui (lianghui@360.cn)
 */
public class ReadCaseFromExcel {
	private ExcelUtil excel;
	private int rowNum;
	private String url;
	private String httpMethod;
	private String cookie;
	private int argcount;
	private String[] argKey;
	private String[] argValue;

	/**
	 * 构造函数
	 * @param pathName
	 * @param sheetName
	 */
	public ReadCaseFromExcel(String pathName,String sheetName){
		excel=new ExcelUtil(pathName, sheetName);
	}
	/**
	 * 构造函数
	 * @param pathName
	 */
	public ReadCaseFromExcel(String pathName){
		excel=new ExcelUtil(pathName, 0);
	}

	/**
	 * 功能说明：从excel表格B1中读取url并返回
	 *  @return String 
	 */
	public String readUrl(){
		this.url=excel.getCellValue(IftConf.urlRow, IftConf.urlCol).trim();
		return this.url;
	}
	
	/**
	 * 功能说明：从excel表格B2中读取readHttpMethod并返回
	 *  @return String 
	 */
	public String readHttpMethod(){
		this.httpMethod = excel.getCellValue(IftConf.methodRow, IftConf.methodCol).trim();
		return this.httpMethod;
	}
	
	/**
	 * 功能说明：从excel表格B3中读取Cookie并返回
	 * 			 
	 *  @return String 
	 * 
	 */
	public String readCookie(){
		this.cookie = excel.getCellValue(IftConf.cookieRow, IftConf.cookieCol).trim();
		return this.cookie;	
	}
	
	/**
	 * 功能说明：从excel表格B4中读取argcount并返回
	 * 			 
	 *  @return int 
	 * 
	 */
	public int readArgCount(){
		this.argcount = Integer.parseInt(excel.getCellValue(IftConf.argCountRow, IftConf.argCountCol).trim());
		return this.argcount;	
	}
	
	/**
	 * 功能说明：从excel表格第5行中读取参数标题并返回
	 * 			 
	 *  @return String[]  
	 * 
	 */
	public String[] readArgKey(){
		this.argKey=new String[excel.getColNum()];
		List<String>keylist=excel.getRowList(IftConf.typeRow);
		for (int i = 0; i < keylist.size(); i++) {
			argKey[i]=keylist.get(i);
		}
		return this.argKey;
	}	
	
	/**
	 * 功能说明：从excel表格指定行开始读取所有值并返回
	 * 			 
	 * @param argKeynum
	 *  @return String[]  
	 * 
	 */
	public String[] readArgValue(int argKeynum){	
		this.argValue=new String[excel.getColNum()];
		List<String>valuelist=excel.getRowList(argKeynum);
		for (int i = 0; i < valuelist.size(); i++) {
			argValue[i]=valuelist.get(i);
		}
		return this.argValue;
		}
	
	/**
	 * Excel的行数
	 * @return int
	 */
	public int getRowNum() {
		this.rowNum=excel.getRowNum();
		return this.rowNum;
	}
}
