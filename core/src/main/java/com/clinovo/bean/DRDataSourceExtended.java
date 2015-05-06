package com.clinovo.bean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Extended DRDataSource.
 */
public class DRDataSourceExtended implements JRRewindableDataSource, Serializable {
    private static final long serialVersionUID = 1L;

    private String[] columns;
    private List<Map<String, Object>> values;
    private Iterator<Map<String, Object>> iterator;
    private Map<String, Object> currentRecord;

    /**
     * Default constructor.
     *
     * @param columns the names of columns.
     */
    public DRDataSourceExtended(String... columns) {
        this.columns = columns;
        this.values = new ArrayList<Map<String, Object>>();
    }

    /**
     * {@inheritDoc}
     */
    public void add(Object... values) {
        Map<String, Object> row = new HashMap<String, Object>();
        for (int i = 0; i < values.length; i++) {
            row.put(columns[i], values[i]);
        }
        this.values.add(row);
    }

    /**
     * {@inheritDoc}
     */
    public void addListRow(List<List<String>> rowData) {
        for (List<String> stringRow : rowData) {
            Map<String, Object> row = new HashMap<String, Object>();
            for (int i = 0; i < columns.length; i++) {
                row.put(columns[i], stringRow.get(i));
            }
            this.values.add(row);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(JRField field) throws JRException {
        return currentRecord.get(field.getName());
    }

    /**
     * {@inheritDoc}
     */
    public boolean next() throws JRException {
        if (iterator == null) {
            this.iterator = values.iterator();
        }
        boolean hasNext = iterator.hasNext();
        if (hasNext) {
            currentRecord = iterator.next();
        }
        return hasNext;
    }

    /**
     * {@inheritDoc}
     */
    public void moveFirst() throws JRException {
        iterator = null;
    }
}
