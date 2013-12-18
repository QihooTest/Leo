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


import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

import com.github.qihootest.leo.toolkit.QtafException;

/**
* 表名称后的数据源为获取数据表名称
* 
*/
public class XlsTableWrapper extends AbstractTable {
    private ITable delegate;
    private String tableName;

    public XlsTableWrapper(String tableName, ITable table) {
        this.delegate = table;
        this.tableName = tableName;
    }
    public int getRowCount() {
        return delegate.getRowCount();
    }

    public ITableMetaData getTableMetaData() {
        ITableMetaData meta = delegate.getTableMetaData();
        try {
            return new DefaultTableMetaData(tableName, meta.getColumns(), meta.getPrimaryKeys());
        } catch (DataSetException e) {
            throw new QtafException("Don't get the meta info from  " + meta, e);
        }
    }

    public Object getValue(int row, String column) throws DataSetException {
        Object delta = delegate.getValue(row, column);
        if (delta instanceof String) {
            if (StringUtils.isEmpty((String) delta)) {
                return null;
            }
        }
        return delta;
    }

}