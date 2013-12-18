package com.github.qihootest.leo.toolkit.dbunit.data.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableIterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.OrderedTableNameMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 重写dbunit的XlsDataSet
*  
 */
public class XlsDataSet extends AbstractDataSet {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(XlsDataSet.class);

    private final OrderedTableNameMap _tables;

    /**
     * 从Excel文件中创建XlsDataSet
     */
    public XlsDataSet(String file) throws IOException, DataSetException {
        this(new FileInputStream(file));
    }
    
    /**
     * 从Excel文件中创建XlsDataSet
     */
    public XlsDataSet(File file) throws IOException, DataSetException {
        this(new FileInputStream(file));
    }
 
    
   /**
     * Creates a new XlsDataSet object that loads the specified Excel document.
     */
    public XlsDataSet(InputStream in) throws IOException, DataSetException {
        _tables = super.createTableNameMap();

        HSSFWorkbook workbook = new HSSFWorkbook(in);
        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {
            ITable table = new XlsTable(workbook, workbook.getSheetName(i),
                    workbook.getSheetAt(i));
            _tables.add(table.getTableMetaData().getTableName(), table);
        }
    }
    
    //暂不使用部分
    /**
     * 从Excel的工作簿中创建XlsDataSet.
     */
    @Deprecated
    public XlsDataSet(HSSFWorkbook workbook) throws IOException, DataSetException {
        _tables = super.createTableNameMap();

        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {
            ITable table = new XlsTable(workbook, workbook.getSheetName(i),
                    workbook.getSheetAt(i));
            _tables.add(table.getTableMetaData().getTableName(), table);
        }
    }

    /**
     * 把数据集写入到Excel文档
     */
    public static void write(IDataSet dataSet, OutputStream out)
            throws IOException, DataSetException {
        logger.debug("write(dataSet={}, out={}) - start", dataSet, out);

        new XlsDataSetWriter().write(dataSet, out);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ITableIterator createIterator(boolean reversed)
            throws DataSetException {
        if (logger.isDebugEnabled())
            logger.debug("createIterator(reversed={}) - start", String.valueOf(reversed));

        ITable[] tables = (ITable[]) _tables.orderedValues().toArray(new ITable[0]);
        return new DefaultTableIterator(tables, reversed);
    }
}
