package com.clinovo.model;


import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.CellEditor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class CodedItemsTableFactory extends AbstractTableFactory {

    private List<CodedItem> codedItems;

    public final static String CODED_DIV_PREFIX = "Search: <input style=\"border:1px solid #a6a6a6;margin-bottom: 2px;background-color:#d9d9d9;color:#4D4D4D\" type=\"text\" value=\"";
    public final static String CODED_DIV_MIDDLE = "\"/><div id=\"";
    public final static String CODED_DIV_SUFIX = "\"></div>";
    public final static String AJAX_REQUEST_PREFIX = "<a onClick=\"codeItem(this)\" itemId=\"";
    public final static String AJAX_REQUEST_SUFIX = "\"><img style=\"float:left;\" width=\"17\" border=\"0\" title=\"Code\" alt=\"Code\" src=\"../images/code.png\" name=\"codeBtn\"/></a>";
    public final static String GOTO_CRF_DEFID = "&nbsp;&nbsp;<a onmouseup=\"javascript:setImage('Complete','../images/icon_DEcomplete.gif');\" href=\"ViewSectionDataEntry?eventDefinitionCRFId=";
    public final static String GOTO_CRF_CRFVER = "&amp;crfVersionId=";
    public final static String GOTO_CRF_SSID = "&amp;studySubjectId=";
    public final static String GOTO_CRF_TABID = "&amp;tabId=";
    public final static String GOTO_CRF_EVENTID = "&amp;eventId=";
    public final static String GOTO_CRF_SUFIX = "&amp;viewFull=yes\"><img border=\"0\" title=\"Open CRF\" alt=\"GoToCRF\" src=\"../images/icon_DEcomplete.gif\" name=\"GOTO\"/></a>";

    @Override
    protected String getTableName() {
        return "codedItems";
    }

    @Override
    protected void configureColumns(TableFacade tableFacade, Locale locale) {
    	
        tableFacade.setColumnProperties("codedItem.verbatimTerm", "codedItem.dictionary",
                "version", "codedColumn", "actionColumn");
        Row row = tableFacade.getTable().getRow();
        configureColumn(row.getColumn("codedItem.verbatimTerm"), "Verbatium Term", null, null);
        configureColumn(row.getColumn("codedItem.dictionary"), "Dictionary", null, null);
        configureColumn(row.getColumn("version"), "Version", new VersionCellEditor(), null, true, true);
        configureColumn(row.getColumn("codedColumn"), "Coded", new CodedCellEditor(), null, false, false);
        configureColumn(row.getColumn("actionColumn"), "Actions", new ActionCellEditor(), new DefaultActionsEditor(
                locale), true, false);
    }

    @Override
    public void setDataAndLimitVariables(TableFacade tableFacade) {

        Limit limit = tableFacade.getLimit();

        if (!limit.isComplete()) {
            tableFacade.setTotalRows(codedItems.size());
        }

        Collection<HashMap<Object, Object>> codedItemsResult = new ArrayList<HashMap<Object, Object>>();
        for (CodedItem codedItem : codedItems) {
            HashMap<Object, Object> h = new HashMap<Object, Object>();
            h.put("codedItem", codedItem);
            h.put("codedItem.itemId", codedItem.getItemId());
            h.put("codedItem.verbatimTerm", codedItem.getVerbatimTerm());
            h.put("codedItem.dictionary", codedItem.getDictionary());

            codedItemsResult.add(h);
        }

        tableFacade.setItems(codedItemsResult);
    }

    private class VersionCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            String version = "";
            if (!version.isEmpty()) {
                StringBuilder url = new StringBuilder();
                url.append(version);
                value = url.toString();
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private class CodedCellEditor implements CellEditor {
        public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            CodedItem codedItem = (CodedItem) ((HashMap<Object, Object>) item).get("codedItem");
            String inputTerm = codedItem.isCoded() ? codedItem.getCodedTerm() : codedItem.getVerbatimTerm();
            if (codedItem != null) {
                StringBuilder url = new StringBuilder();
                url.append(CODED_DIV_PREFIX).append(inputTerm)
                        .append(CODED_DIV_MIDDLE).append(codedItem.getItemId())
                        .append(CODED_DIV_SUFIX);
                value = url.toString();
            }
            return value;
        }
    }
    
    @SuppressWarnings("unchecked")
    private class ActionCellEditor implements CellEditor {
		public Object getValue(Object item, String property, int rowcount) {
            String value = "";
            Integer itemId = (Integer) ((HashMap<Object, Object>) item).get("codedItem.itemId");

            if (itemId != null) {
                StringBuilder url = new StringBuilder();
                url.append(AJAX_REQUEST_PREFIX).append(itemId)
                        .append(AJAX_REQUEST_SUFIX).append(GOTO_CRF_DEFID)
                        .append(GOTO_CRF_CRFVER).append(GOTO_CRF_SSID)
                        .append(GOTO_CRF_TABID).append(GOTO_CRF_EVENTID)
                        .append(GOTO_CRF_SUFIX);
                value = url.toString();
            }
            return value;
        }
    }

    public void setCodedItems(List<CodedItem> codedItems) {
        this.codedItems = codedItems;
    }
}
