/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.service;

import com.clinovo.lib.crf.builder.impl.BaseCrfBuilder;

/**
 * ImportCrfService.
 */
public interface ImportCrfService {

	int INT_3 = 3;

	String SL = "SL";
	String ONE = "1";
	String ZERO = "0";
	String CRF = "CRF";
	String POS = "pos";
	String YES = "yes";
	String FORTY = "40";
	String ROWS = "rows";
	String HIDE = "hide";
	String TRUE = "true";
	String TYPE = "type";
	String TEXT = "text";
	String NAME = "name";
	String FILE = "file";
	String VALUE = "value";
	String UTF_8 = "UTF-8";
	String PAGES = "pages";
	String TITLE = "title";
	String LAYOUT = "layout";
	String HEADER = "header";
	String INTEGER = "integer";
	String DECIMAL = "decimal";
	String COLUMNS = "columns";
	String DEFAULT = "default";
	String OPTIONS = "options";
	String VERSION = "version";
	String MAX_ROWS = "maxRows";
	String MIN_ROWS = "minRows";
	String PHI_DATA = "phiData";
	String CHILDREN = "children";
	String REQUIRED = "required";
	String TEXTAREA = "textarea";
	String SUB_TITLE = "subTitle";
	String LEFT_TEXT = "leftText";
	String ITEM_NAME = "itemName";
	String QUESTIONS = "questions";
	String NUMBERING = "numbering";
	String UNGROUPED = "Ungrouped";
	String RESIZABLE = "resizable";
	String RIGHT_TEXT = "rightText";
	String NUMBER_TYPE = "numberType";
	String CALCULATION = "calculation";
	String DESCRIPTION = "description";
	String PARTIAL_DATE = "partialDate";
	String MULTI_SELECT = "multiSelect";
	String INSTRUCTIONS = "instructions";
	String GROUP_LAYOUT = "GROUP_LAYOUT";
	String DEFAULT_VALUE = "defaultValue";
	String COLUMN_NUMBER = "columnNumber";
	String DISPLAY_STATE = "displayState";
	String WIDTH_DECIMAL = "width_decimal";
	String RESPONSE_LABEL = "responseLabel";
	String REPEATING_GROUP = "repeatingGroup";
	String FIELD_VALIDATION = "fieldValidation";
	String CALCULATION_TYPE = "calculationType";
	String GROUP_CALCULATION = "groupCalculation";
	String VALIDATION_MESSAGE = "validationMessage";
	String REPEATING_GROUP_HEADER = "repeatingGroupHeader";
	String REPEATING_ITEM_GROUP_HEADER = "repeatingItemGroupHeader";
	String AUTO_CHANGED_CELL = "<br/><span style='color: silver;'>(auto-change)</span>";

	/**
	 * Imports crf.
	 * 
	 * @param crfBuilder
	 *            BaseCrfBuilder
	 * @throws Exception
	 *             an Exception
	 */
	void importNewCrf(BaseCrfBuilder crfBuilder) throws Exception;
}
