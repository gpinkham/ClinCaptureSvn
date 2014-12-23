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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public abstract class ExpressionNode {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected ExpressionService expressionService;
	private OpenClinicaExpressionParser expressionParser;
	boolean dateParameter;

	String value() throws OpenClinicaSystemException {
		return calculate();
	}

	/*
	 * Use this method to test the expression mainly Data types plugging test data wherever necessary. This will not
	 * only validate the syntax but also test the validity of the expression itself.
	 */
	String testValue() throws OpenClinicaSystemException {
		return testCalculate();
	}

	abstract String calculate() throws OpenClinicaSystemException;

	abstract String testCalculate() throws OpenClinicaSystemException;

	abstract void printStackCommands();

	String getNumber() {
		return null;
	}

	public HashMap<String, String> getTestValues() {
		return expressionParser.getTestValues();
	}

	public HashMap<String, String> getResponseTestValues() {
		return expressionParser.getResponseTestValues();
	}

	protected OpenClinicaExpressionParser getExpressionParser() {
		return expressionParser;
	}

	protected void setExpressionParser(OpenClinicaExpressionParser expressionParser) {
		this.expressionParser = expressionParser;
	}

	protected boolean isDateParameter() {
		return dateParameter;
	}

	protected void setDateParameter(boolean dateParameter) {
		this.dateParameter = dateParameter;
	}

	protected boolean dateShouldBeEntered(ExpressionNode left, ExpressionNode right) {
		boolean isNotImportRules = !expressionParser.isImportRulesMode();
		boolean isDateItem = expressionParser.isDateItem();
		boolean isNextToDate = (left.isDateParameter() ? (right.isDateParameter() ? false : right.value().isEmpty())
				: left.value().isEmpty());
		return isNotImportRules && isDateItem && isNextToDate;
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
}
