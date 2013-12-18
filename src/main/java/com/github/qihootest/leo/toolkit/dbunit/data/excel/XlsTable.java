/*
* Copyright 2011 Alibaba Group Holding Limited.
* All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package com.github.qihootest.leo.toolkit.dbunit.data.excel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.excel.XlsDataSetWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 重写dbunit的XlsTable
*  
 */
public class XlsTable extends AbstractTable {
    static final Pattern pattern = Pattern.compile("[eE]");

    private static final Logger logger = LoggerFactory.getLogger(XlsTable.class);

    private final ITableMetaData _metaData;
    private final HSSFSheet _sheet;

    private final DecimalFormatSymbols symbols = new DecimalFormatSymbols();

    public XlsTable(HSSFWorkbook workbook, String sheetName, HSSFSheet sheet) throws DataSetException {
        int rowCount = sheet.getLastRowNum();
        if (rowCount >= 0 && sheet.getRow(0) != null) {
            _metaData = createMetaData(sheetName, sheet.getRow(0), workbook);
        } else {
            _metaData = new DefaultTableMetaData(sheetName, new Column[0]);
        }

        _sheet = sheet;

        // Needed for later "BigDecimal"/"Number" conversion
        symbols.setDecimalSeparator('.');
    }

    static ITableMetaData createMetaData(String tableName, HSSFRow sampleRow, HSSFWorkbook workbook) {
        logger.debug("createMetaData(tableName={}, sampleRow={}) - start", tableName, sampleRow);

        List<Column> columnList = new ArrayList<Column>();
        List<String> primaryKeyList = new ArrayList<String>();
        for (int i = 0;; i++) {
            HSSFCell cell = sampleRow.getCell(i);
            if (cell == null) {
                break;
            }

            String columnName = cell.getRichStringCellValue().getString();
            if (columnName != null) {
                columnName = columnName.trim();
            }

            // Bugfix for issue ID 2818981 - if a cell has a formatting but no
            // name also ignore it
            if (columnName.length() <= 0) {
                logger
                        .debug(
                                "The column name of column # {} is empty - will skip here assuming the last column was reached",
                                String.valueOf(i));
                break;
            }

            Column column = new Column(columnName, DataType.UNKNOWN);
            columnList.add(column);
            
            // Unique identification key
            byte underline = cell.getCellStyle().getFont(workbook).getUnderline();
            if (underline == 1) {
                primaryKeyList.add(columnName);
            } 

        }
        Column[] columns = columnList.toArray(new Column[0]);
        
        if(!primaryKeyList.isEmpty()){
        	return new DefaultTableMetaData(tableName, columns,primaryKeyList.toArray(new String[primaryKeyList.size()]));
        }else{
        	return new DefaultTableMetaData(tableName, columns);
        }
        
    }

    // //////////////////////////////////////////////////////////////////////////
    // ITable interface

    public int getRowCount() {
        logger.debug("getRowCount() - start");

        return _sheet.getLastRowNum();
    }

    public ITableMetaData getTableMetaData() {
        logger.debug("getTableMetaData() - start");

        return _metaData;
    }

    protected Object getDateValueFromJavaNumber(HSSFCell cell) {
        logger.debug("getDateValueFromJavaNumber(cell={}) - start", cell);

        double numericValue = cell.getNumericCellValue();
        BigDecimal numericValueBd = new BigDecimal(String.valueOf(numericValue));
        numericValueBd = stripTrailingZeros(numericValueBd);
        return new Long(numericValueBd.longValue());
        // return new Long(numericValueBd.unscaledValue().longValue());
    }

    /**
     * Removes all trailing zeros from the end of the given BigDecimal value
     * up to the decimal point.
     * @param value The value to be stripped
     * @return The value without trailing zeros
     */
    private BigDecimal stripTrailingZeros(BigDecimal value) {
        if (value.scale() <= 0) {
            return value;
        }

        String valueAsString = String.valueOf(value);
        int idx = valueAsString.indexOf(".");
        if (idx == -1) {
            return value;
        }

        for (int i = valueAsString.length() - 1; i > idx; i--) {
            if (valueAsString.charAt(i) == '0') {
                valueAsString = valueAsString.substring(0, i);
            } else if (valueAsString.charAt(i) == '.') {
                valueAsString = valueAsString.substring(0, i);
                // Stop when decimal point is reached
                break;
            } else {
                break;
            }
        }
        BigDecimal result = new BigDecimal(valueAsString);
        return result;
    }

    protected BigDecimal getNumericValue(HSSFCell cell) {
        logger.debug("getNumericValue(cell={}) - start", cell);

        String formatString = cell.getCellStyle().getDataFormatString();
        String resultString = null;
        double cellValue = cell.getNumericCellValue();

        if ((formatString != null)) {
            if (!formatString.equals("General") && !formatString.equals("@")) {
                logger.debug("formatString={}", formatString);
                DecimalFormat nf = new DecimalFormat(formatString, symbols);
                resultString = nf.format(cellValue);
            }
        }

        BigDecimal result;
        if (resultString != null) {
            try {
                result = new BigDecimal(resultString);
            } catch (NumberFormatException e) {
                logger.debug("Exception occurred while trying create a BigDecimal. value={}", resultString);
                // Probably was not a BigDecimal format retrieved from the
                // excel. Some
                // date formats are not yet recognized by HSSF as DateFormats so
                // that
                // we could get here.
                result = toBigDecimal(cellValue);
            }
        } else {
            result = toBigDecimal(cellValue);
        }
        return result;
    }

    /**
     * @param cellValue
     * @return
     * @since 2.4.6
     */
    private BigDecimal toBigDecimal(double cellValue) {
        String resultString = String.valueOf(cellValue);

        // Treatment for scientific notation (for accurate to five decimal places)
        if (pattern.matcher(resultString).find()) {
            DecimalFormat format = new DecimalFormat("#####.#####");
            resultString = format.format(cellValue);
        }

        // To ensure that intergral numbers do not have decimal point and
        // trailing zero
        // (to restore backward compatibility and provide a string
        // representation consistent with Excel)
        if (resultString.endsWith(".0")) {
            resultString = resultString.substring(0, resultString.length() - 2);
        }

        BigDecimal result = new BigDecimal(resultString);
        return result;

    }

    public Object getValue(int row, String column) throws DataSetException {
        if (logger.isDebugEnabled())
            logger.debug("getValue(row={}, columnName={}) - start", Integer.toString(row), column);

        assertValidRowIndex(row);

        int columnIndex = getColumnIndex(column);
        HSSFCell cell = _sheet.getRow(row + 1).getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        int type = cell.getCellType();
        switch (type) {
        case HSSFCell.CELL_TYPE_NUMERIC:
            HSSFCellStyle style = cell.getCellStyle();
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return getDateValue(cell);
            } else if (XlsDataSetWriter.DATE_FORMAT_AS_NUMBER_DBUNIT.equals(style.getDataFormatString())) {
                // The special dbunit date format
                return getDateValueFromJavaNumber(cell);
            } else {
                return getNumericValue(cell);
                }

        case HSSFCell.CELL_TYPE_STRING:
            return cell.getRichStringCellValue().getString();

            case HSSFCell.CELL_TYPE_FORMULA:
            throw new DataTypeException("Formula not supported at row=" +
                    row + ", column=" + column);

            case HSSFCell.CELL_TYPE_BLANK:
            return null;

            case HSSFCell.CELL_TYPE_BOOLEAN:
            return cell.getBooleanCellValue() ? Boolean.TRUE : Boolean.FALSE;

            case HSSFCell.CELL_TYPE_ERROR:
            throw new DataTypeException("Error at row=" + row +
                    ", column=" + column);

            default:
            throw new DataTypeException("Unsupported type at row=" + row +
                    ", column=" + column);
        }
    }

    protected Object getDateValue(HSSFCell cell) {
        // Simplification and the use of Chinese date format
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell
                .getDateCellValue());
    }

}
