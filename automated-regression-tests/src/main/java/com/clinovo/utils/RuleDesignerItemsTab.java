package com.clinovo.utils;

/**
 * Created by Anton on 31.05.2014.
 */
public enum RuleDesignerItemsTab {

    DTM_STROKE_ON_SET("dtmStrokeOnset", "items");

    private String labelText;
    private String tableId;

    private RuleDesignerItemsTab(String labelText, String tableId) {
        this.labelText = labelText;
        this.tableId = tableId;
    }

    public String getTableId(String labelText) {
        for (RuleDesignerItemsTab item : RuleDesignerItemsTab.values()) {
            if (item.labelText == labelText)
                return item.tableId;
        }

        return null;
    }
}
