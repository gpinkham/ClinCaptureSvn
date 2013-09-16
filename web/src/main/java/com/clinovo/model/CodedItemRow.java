/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package com.clinovo.model;

public class CodedItemRow {

    private int itemId;
    private String dictionary = "";
    private String codedColumn = "";
    private String verbatimTerm = "";
    private String actionColumn = "";

    public final static String CODED_DIV_PREFIX = "<div id=\"";
    public final static String CODED_DIV_SUFIX = "\"></div>";
    public final static String AJAX_REQUEST_PREFIX = "<a onClick=\"codeItem(this)\" itemId=\"";
    public final static String AJAX_REQUEST_SUFIX = "\"><img style=\"float:left;\" width=\"17\" border=\"0\" title=\"Code\" alt=\"Code\" src=\"../images/code.png\" name=\"codeBtn\"/></a>";
    public final static String GOTO_CRF_DEFID = "&nbsp;&nbsp;<a onmouseup=\"javascript:setImage('Complete','../images/icon_DEcomplete.gif');\" href=\"ViewSectionDataEntry?eventDefinitionCRFId=";
    public final static String GOTO_CRF_CRFVER = "&amp;crfVersionId=";
    public final static String GOTO_CRF_SSID = "&amp;studySubjectId=";
    public final static String GOTO_CRF_TABID = "&amp;tabId=";
    public final static String GOTO_CRF_EVENTID = "&amp;eventId=";
    public final static String GOTO_CRF_SUFIX = "&amp;viewFull=yes\"><img border=\"0\" title=\"Open CRF\" alt=\"GoToCRF\" src=\"../images/icon_DEcomplete.gif\" name=\"GOTO\"/></a>";

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getVerbatimTerm() {
        return verbatimTerm;
    }

    public void setVerbatimTerm(String verbatimTerm) {
        this.verbatimTerm = verbatimTerm;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getCodedColumn() {
        return codedColumn;
    }

    public void setCodedColumn(String codedColumn) {
        this.codedColumn = codedColumn;
    }

    public String getActionColumn() {
        return actionColumn;
    }

    public void setActionColumn(String actionColumn) {
        this.actionColumn = actionColumn;
    }
}
