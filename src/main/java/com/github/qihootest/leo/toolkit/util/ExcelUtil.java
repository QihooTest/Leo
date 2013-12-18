package com.github.qihootest.leo.toolkit.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


//2007以下版本xsl
//2007及以上版本xslx

/**
 * Excel文件的读写
 * @author @<a href='http://weibo.com/bwgang'>bwgang</a> (bwgang@163.com)<br/>
 *
 * 2007以下版本xsl
 * 2007及以上版本xslx
 */
public class ExcelUtil {
	private static LogUtil log=LogUtil.getLogger(ExcelUtil.class);//记录日志
	
	private Workbook wb;//工作簿
	private Sheet sheet;//sheet表
	private String pathName;
	
	/**
	 * 默认构造函数
	 */
	public ExcelUtil(){
		
	}
	/**
	 * 构造函数
	 * @param pathName
	 */
	public ExcelUtil(String pathName){
		this.pathName=pathName;
		setWb();
	}
	/**
	 * 构造函数
	 * @param pathName
	 * @param sheetName
	 */
	public ExcelUtil(String pathName,String sheetName){
		this.pathName=pathName;
		setWb();
		setSheet(sheetName);
	}
	/**
	 * 构造函数
	 * @param pathName
	 * @param sheetIndex
	 */
	public ExcelUtil(String pathName,int sheetIndex){
		this.pathName=pathName;
		setWb();
		setSheet(sheetIndex);
	}
	
	/**
	 * 写Excel文件
	 * @param wb
	 * @param pathName
	 */
	public void writeExcel2007(XSSFWorkbook wb,String pathName) {
		try {
			wb.write(FileUtil.getFileOutStream(pathName));
		} catch (IOException e) {
			log.error("写入Excel文件失败："+pathName);
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 获取工作簿
	 * @return Workbook
	 */
	public Workbook getWb() {
		return this.wb;
	}
	/**
	 * 设置Excel文件路径
	 * @param pathName
	 */
	public void setPathName(String pathName) {
		this.pathName=pathName;
		setWb();
//		setSheet();
	}

	/**
	 * 设置sheet
	 * @param sheet
	 */
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
		//删除掉空白行
		delBankRow();
	}
	/**
	 * 设置sheet
	 * @param sheetName
	 */
	public void setSheet(String sheetName) {
		if (null==wb) return;
		setSheet(getWb().getSheet(sheetName));
		if (null==getSheet()) {//按名称未获取到sheet
//			log.info("Excel："+this.pathName+"中，要获取的名称为"+sheetName+"的sheet页不存在，" +
//					"默认获取第一个sheet页");
			setSheet(getWb().getSheetAt(0));
			if (null==getSheet())log.error("Excel："+this.pathName+"中，不存在任何Sheet");
		}
	}
	/**
	 * 设置sheet
	 * @param sheetIndex
	 */
	public void setSheet(int sheetIndex) {
		if (null==wb) return;
		try {
			setSheet(getWb().getSheetAt(sheetIndex));
		} catch (Exception e) {
			log.error("Excel："+this.pathName+"中，要获取的第"+sheetIndex+"个sheet不存在,"
					+"默认获取第一个Sheet1");
			setSheet(getWb().getSheetAt(0));
			if (null==getSheet())log.error("Excel："+this.pathName+"中，不存在任何Sheet");
		}
		
	}
	/**
	 * 获取单元格内容，坐标从(0,0)开始，横为行，竖为列
	 * @param rowIndex
	 * @param colIndex
	 * @return String
	 */
	public String getCellValue(int rowIndex,int colIndex) {
		if ( colIndex<0) {
			log.error("读取Excel的列数不能为负数");
			return null;
		}
		Row row=getRow(rowIndex);
		if (null==row) return null;
 		return getStrFromCell(row.getCell(colIndex));
	}
	/**
	 * 获取一行的内容
	 * @param rowIndex
	 * @return List<String>
	 */
	public List<String> getRowList(int rowIndex) {
		List<String> reslist=new ArrayList<>();
		Row row=getRow(rowIndex);
		if (null!=row) {
			for (int i = 0; i < getColNum(); i++) {
				reslist.add(getCellValue(rowIndex, i));
			}
		}
		
		return reslist;
	}
	/**
	 * 获取总行数
	 * @return int
	 */
	public int getRowNum() {
		if (null==getSheet())return 0;
		return getSheet().getLastRowNum()+1;
	}
	/**
	 * 获取总列数
	 * @return int
	 */
	public int getColNum() {
		int max=0;
		if (null==getSheet())return 0;
		if (getSheet().getPhysicalNumberOfRows()<1)return 0;
		for (int i = 0; i < getRowNum(); i++) {
			int tmp=getRow(i).getPhysicalNumberOfCells();
//			log.error(i+"=="+tmp);
			if (tmp>=max)max=tmp;
		}
		return max;
	}

	private Row getRow(int rowIndex) {
		if ( rowIndex<0) {
			log.error("读取Excel的行数不能为负数");
			return null;
		}
		if (null==getSheet()) return null;
		Row row=getSheet().getRow(rowIndex);
		if (null==row) {
			if (rowIndex<getRowNum()) return null;
			log.info("读取Excel："+this.pathName+" 的表-"+getSheet().getSheetName()
					+" 的第"+rowIndex+"行失败");
			return null;
		}
		return row;
	}
	
	
	/**
	 * 创建空白Excel文件
	 * @param pathName
	 */
	public  void createBlankExcel2007(String pathName) {
		XSSFWorkbook wb=new XSSFWorkbook();
		writeExcel2007(wb, pathName);
	}
	
	
	private String getStrFromCell(Cell cell) {
		String res = "";
		if (null==cell) {
			return "";
		}
//		res=cell.getRichStringCellValue().toString();
		
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC: // 数字/日期
				if (DateUtil.isCellDateFormatted(cell)){
					res=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
				}else{
					BigDecimal value =  new BigDecimal(cell.getNumericCellValue());
					String str = value.toString(); 
					if(str.contains(".0"))str = str.replace(".0", "");
					res=str;
				}
				break;
			case Cell.CELL_TYPE_STRING: // 字符串
				res = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN: // 布尔
				Boolean booleanValue = cell.getBooleanCellValue();
				res = booleanValue.toString();
				break;
			case Cell.CELL_TYPE_BLANK: // 空值
				res = "";
				break;
			case Cell.CELL_TYPE_FORMULA: // 公式
				res = cell.getCellFormula();
				break;
			case Cell.CELL_TYPE_ERROR: // 故障
				res = "";
				break;
			default:
				System.out.println("未知类型");
				break;
		}
		return res;
	}
	private Sheet getSheet(){
		return this.sheet;
	}
	private void setWb() {
		try {
			if (null==this.pathName) return ;
			String exname=FileUtil.getExtensionName(this.pathName);
			if (exname.indexOf("xls")>-1 && exname.indexOf("xlsx")<0) {
				this.wb=new HSSFWorkbook(FileUtil.readToFileInputStream(this.pathName));
			}else if (exname.indexOf("xlsx")>-1) {
				this.wb=new XSSFWorkbook(FileUtil.readToFileInputStream(this.pathName));
			}else{
				log.info("无法读取，Excel文件异常："+this.pathName);
			}
		} catch ( NullPointerException | IOException e) {
			log.error("读取Excel文件出错："+this.pathName);
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	private void delBankRow() {
		for (int i = 0; i <= getRowNum(); i++) {
			Row r;
			try {
				r = sheet.getRow(i);
			} catch (Exception e) {
				r = null;
				// sheet.removeRowBreak(i);
				continue;
			}

			if (r == null && i == sheet.getLastRowNum()) {
				// 如果是空行,且到了最后一行,直接将那一行删掉
				sheet.removeRow(r);
			} else if (r == null && i < sheet.getLastRowNum()) {
				// 如果还没到最后一行，则数据往上移一行
				sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
			}

		}
	}
	
}
