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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2011 Akaza Research
 */
package org.akaza.openclinica.control.form.spreadsheet;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.*;

/**
 * Error message for SpreadSheet uploading cooperating with existing message handling style.
 */
// ywang (Aug. 2011)
public final class SheetErrors {
	List<StringBuffer> errors;
	Map<String, String> htmlErrors;
	ResourceBundle resPageMsg;

	/**
	 * Locale is "US"
	 */
	public SheetErrors() {
		errors = new ArrayList<StringBuffer>();
		htmlErrors = new HashMap<String, String>();
		resPageMsg = ResourceBundleProvider.getPageMessagesBundle(Locale.US);
	}

	public SheetErrors(ResourceBundle resPageMsg) {
		this.resPageMsg = resPageMsg;
		errors = new ArrayList<StringBuffer>();
		htmlErrors = new HashMap<String, String>();
	}

	/**
	 * e.g., htmlErrors.put(j + "," + k + ",16", "INVALID FIELD");
	 */
	public void putHtmlError(int sheetNum, int rowNum, int colNum, String message) {
		htmlErrors.put(htmlErrorKey(sheetNum, rowNum, colNum), message);
	}

	protected String htmlErrorKey(int sheetNum, int rowNum, int colNum) {
		StringBuilder s = new StringBuilder();
		s.append(sheetNum);
		s.append(",");
		s.append(rowNum);
		s.append(",");
		s.append(colNum);
		return s.toString();
	}

	/**
	 * i18n arguments have to be done before passed in. <br/>
	 * Returned message will like: colTitle, message for forwhich. e.g., colTitle = RESPONSE_LABEL_column, message = is
	 * required (message), forWhich = check-box: RESPONSE_LABEL_column, is required for check-box <br/>
	 * 
	 * @param colTitle
	 * @param message
	 * @param forWhich
	 * @return
	 */
	protected StringBuffer errorMessage(String colTitle, String message, String forWhich) {
		StringBuffer s = new StringBuffer(colTitle);
		s.append(",");
		s.append(message);
		s.append(" ");
		s.append(resPageMsg.getString("for"));
		s.append(" ");
		s.append(forWhich);
		return s;
	}

	/**
	 * error added will like: <message> at row <rowNum> items worksheet.
	 * 
	 * @param rowNum
	 * @param message
	 */
	protected void addError(int rowNum, StringBuffer message) {
		/*
		 * errors.add(resPageMsg.getString("expression_not_start_with_func_at") + " " + k + ", " +
		 * resPageMsg.getString("items_worksheet") + ".");
		 */
		StringBuffer s = new StringBuffer(message);
		s.append(" ");
		s.append(resPageMsg.getString("at_row"));
		s.append(" ");
		s.append(rowNum);
		s.append(", ");
		s.append(resPageMsg.getString("items_worksheet"));
		s.append(".");
		errors.add(s);
	}

	public Map<String, String> putHtmlErrorsToSheet(Map<String, String> htmlErrorsFromSheet) {
		htmlErrorsFromSheet.putAll(htmlErrors);
		return htmlErrorsFromSheet == null ? new HashMap<String, String>() : htmlErrorsFromSheet;
	}

	public List<String> addErrorsToSheet(List<String> errorsFromSheet) {
		if (errors.size() > 0) {
			for (StringBuffer s : errors) {
				errorsFromSheet.add(s.toString());
			}
		}
		return errorsFromSheet == null ? new ArrayList<String>() : errorsFromSheet;
	}

	public List<StringBuffer> getErrors() {
		return errors;
	}

	public void setErrors(List<StringBuffer> errors) {
		this.errors = errors;
	}

	public Map<String, String> getHtmlErrors() {
		return htmlErrors;
	}

	public void setHtmlErrors(Map<String, String> htmlErrors) {
		this.htmlErrors = htmlErrors;
	}

	public ResourceBundle getResPageMsg() {
		return resPageMsg;
	}

	public void setResPageMsg(ResourceBundle resPageMsg) {
		this.resPageMsg = resPageMsg;
	}
}
